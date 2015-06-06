package com.pandora.gui.struts.action;

import java.io.IOException;
import java.io.Writer;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import com.pandora.CalendarSyncTO;
import com.pandora.EdiTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RiskTO;
import com.pandora.RootTO;
import com.pandora.TeamInfoTO;
import com.pandora.UserTO;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.bus.GeneralTimer;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.DiscussionDelegate;
import com.pandora.delegate.EdiDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class EdiDownload extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public void init() {
		String enable = "";
		try {
			enable = getInitParameter("enable");
			if (enable!=null && !enable.trim().equalsIgnoreCase("off")){
				SystemSingleton.getInstance().setEDIDownload("on");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "EDI download listener is ON...");
				System.out.println("EDI download listener is ON...");
				GeneralTimer.getInstance();
			} else {
				SystemSingleton.getInstance().setEDIDownload("off");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "EDI download listener is OFF...");
			}
		} catch (BusinessException e) {
			LogUtil.log(this, LogUtil.LOG_ERROR, "error at startup parameters enable_EDI_download=" + enable, e);
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);

	}


	private void process(HttpServletRequest req, HttpServletResponse resp) {
		try {			
			String enable = SystemSingleton.getInstance().getEDIDownload(); 
			if (enable!=null && enable.trim().equalsIgnoreCase("on")) {
				String key = req.getParameter("key");
				if (key!=null) {
					this.performDownload(req, resp, key);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.log(this, LogUtil.LOG_ERROR, "the EDI donwload failed.", e);
		}
	}

	
	private void performDownload(HttpServletRequest req, HttpServletResponse resp, String key) throws Exception {
		Writer writer = null;
		EdiDelegate del = new EdiDelegate();
		UserDelegate udel = new UserDelegate();
		
		try {
			EdiTO rto = del.getEdiFromUUID(key);
			if (rto!=null) {
				UserTO uto = udel.getUser(new UserTO(rto.getUserId()));
			    writer = resp.getWriter();

				if (rto.isRss()) {
					performRssDownload(rto, writer, uto, req, resp);
				} else{
			    	net.fortuna.ical4j.model.Calendar icsCalendar = this.performICalDownload(req, rto, uto);
					CalendarOutputter outputter = new CalendarOutputter();
					outputter.output(icsCalendar, writer);
				}
			}
		} catch(Exception e) {
			throw e;			
		} finally {
		    try {
		    	if (writer!=null) {
		    		writer.close();
		    	}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}


	private void performRssDownload(EdiTO rto, Writer writer, UserTO uto, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Vector<SyndEntry> entities = this.getInfoToRss(req, rto, uto);
		SyndFeed feed = new SyndFeedImpl();
		
		feed.setFeedType("rss_2.0");
    	feed.setTitle(this.getTitle(req, uto, rto.getEdiId()));

    	feed.setLink("http://www.plandora.com/" );
		feed.setDescription("Project Information");
		feed.setEncoding(SystemSingleton.getInstance().getDefaultEncoding());			
	    feed.setEntries(entities);
	    
	    SyndFeedOutput output = new SyndFeedOutput();		    
	    output.output(feed, writer);
	}


	private Vector<SyndEntry> getInfoToRss(HttpServletRequest req, EdiTO rto, UserTO uto) throws BusinessException {
		Vector<SyndEntry> response = new Vector<SyndEntry>(); 

		if (rto.getEdiId().equals(EdiTO.RSS_TYPE_ALL_OCC)) {
			response = getPublicOcc(req, uto);
		} else if (rto.getEdiId().equals(EdiTO.RSS_TYPE_UNCL_REQ)) {
			response = getUnclosedReqs(req, uto);
		} else if (rto.getEdiId().equals(EdiTO.RSS_TYPE_UNCL_TASK)) {
			response = getUnclosedTasks(req, uto);
		} else if (rto.getEdiId().equals(EdiTO.RSS_TYPE_TEAM_INFO)) {
			response = getMyTeamInfo(req, uto);
		} else if (rto.getEdiId().equals(EdiTO.RSS_TYPE_ALL_RISK)) {
			response = getPublicRisks(req, uto);
		}
		
		return response;
	}

	private net.fortuna.ical4j.model.Calendar performICalDownload(HttpServletRequest req, EdiTO rto, UserTO uto) throws Exception {
		net.fortuna.ical4j.model.Calendar response = new net.fortuna.ical4j.model.Calendar();
		    	
		response.getProperties().add(new ProdId("-//Plandora.org//Plandora//EN"));
		response.getProperties().add(Version.VERSION_2_0);
		response.getProperties().add(CalScale.GREGORIAN);		
		
		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
	    Calendar c = new GregorianCalendar(uto.getLocale());
		TimeZone timezone = registry.getTimeZone(c.getTimeZone().getID());
		VTimeZone tz = ((net.fortuna.ical4j.model.TimeZone) timezone).getVTimeZone();
		
		if (rto.getEdiId().equals(EdiTO.ICAL_TYPE_ALL_OCC)) {
			this.getCalendarFromOccurrences(uto, response, timezone, tz);
			
		} else if (rto.getEdiId().equals(EdiTO.ICAL_TYPE_ALL_TSK)) {
		    this.getCalendarFromTasks(uto, response, timezone, tz);
		}
		
		return response;
	}


	private void getCalendarFromTasks(UserTO uto, net.fortuna.ical4j.model.Calendar response, TimeZone timezone, VTimeZone tz) throws BusinessException {
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		Vector<ResourceTaskTO> list = rtdel.getTaskListByResource(new ResourceTO(uto.getId()), true);
		if (list!=null) {
			for (ResourceTaskTO rtto : list) {
				if (rtto.getTask()!=null && rtto.getTask().getProject()!=null && rtto.getTask().getCategory()!=null && rtto.getTaskStatus()!=null){
					VToDo todo = rtto.toVToDo(timezone, tz.getTimeZoneId());
					response.getComponents().add(todo);							
				}
			}
		}
	}


	private void getCalendarFromOccurrences(UserTO uto,	net.fortuna.ical4j.model.Calendar response, TimeZone timezone, VTimeZone tz)
			throws BusinessException, Exception, SocketException {
		UserDelegate udel = new  UserDelegate();
    	Locale loc = null;
    	String dtMask = "dd/MM/yyyy";
		OccurrenceDelegate odel =  new OccurrenceDelegate();
		UserTO root = udel.getRoot();
		String allClasses = root.getPreference().getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS);
		
		Vector<OccurrenceTO> list = odel.getOccurenceList(ProjectTO.PROJECT_ROOT_ID, uto.getId(), true);
		if (list!=null) {
			for (OccurrenceTO to : list) {
				if (to.getProject()!=null && to.getProject().getId()!=null && allClasses!=null && !allClasses.trim().equals("")) {
					OccurrenceTO oto = (OccurrenceTO)to;
					loc = oto.getLocale();
					
		        	Object bus = OccurrenceTO.getClass(allClasses, oto.getSource());
					if (bus!=null && oto.getVisible()) { //only public events and milestones
						oto = odel.getOccurrenceObject(oto);
						CalendarSyncTO csto = ((CalendarSyncInterface)bus).populateCalendarFields(oto, dtMask, loc);
						csto.setOccurrence(oto);
						csto.setName(oto.getName());

						if (csto.getEventDate()!=null && !csto.getEventDate().trim().equals("") && csto.getEventTime()!=null ) {
							VEvent meeting = csto.toVEvent(timezone, tz.getTimeZoneId());
							response.getComponents().add(meeting);							
						}
					}
				}
			}
		}
	}
	
	
	
	private Vector<SyndEntry> getMyTeamInfo(HttpServletRequest request, UserTO uto) throws BusinessException {
		Vector<SyndEntry> response = new Vector<SyndEntry>();
		DiscussionDelegate dtdel = new DiscussionDelegate();
		
		int maxDays = 7;
		String max = uto.getPreference().getPreference(PreferenceTO.HOME_TOPICLIST_NUMLINE);
		if (max!=null) {
			maxDays = Integer.parseInt(max);
		}
		
		ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", uto.getLocale());
		String path = this.getPath(request);
		
		Vector<TeamInfoTO> list = dtdel.getTeamInfo(uto, DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -maxDays));
		if (list!=null) {
			for (TeamInfoTO to : list) {
				if (to.getProject()!=null && to.getUser()!=null){
					SyndContent description = new SyndContentImpl();
				    description.setType("text/html");
			    	description.setValue(to.getComment());
			    	
			    	String catName = "";
			    	if(rb!=null) {
			    		catName =  rb.getString("label." + to.getType());
			    	}
			    	String name = "[" +catName + "@" + to.getProject().getName() + "] " + to.getName(); 
			    	
					SyndEntry entry = new SyndEntryImpl();
					entry.setTitle(name);	
					entry.setPublishedDate(to.getCreationDate());
					if (to.getId()!=null && to.getProject().getId()!=null) {
						entry.setLink(path + "&id=" + to.getId() + "&projectId=" + to.getProject().getId());	
					}

					String author = "";
					if (to.getUser()!=null && to.getUser().getUsername()!=null) {
						author = to.getUser().getUsername();
						if (author.equals(RootTO.ROOT_USER)) {
							author = "";
						}
					}
					
					entry.setAuthor(author);
					entry.setDescription( description );
			    
					if(rb!=null) {
						ArrayList<SyndCategory> categories = new ArrayList<SyndCategory>();
						SyndCategoryImpl cat = new SyndCategoryImpl();
						cat.setName(catName);
						cat.setTaxonomyUri("");
						categories.add(cat);
					    entry.setCategories(categories);						
					}
				    
					response.add(entry);				    
				}				
			}
		}
		
		return response;
	}


	private Vector<SyndEntry> getUnclosedTasks(HttpServletRequest request, UserTO uto) throws BusinessException {
		Vector<SyndEntry> response = new Vector<SyndEntry>();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate(); 

		ResourceTO rto = new ResourceTO(uto.getId());
		rto.setPreference(uto.getPreference());
		
		String path = this.getPath(request);

		Vector<ResourceTaskTO> list = rtdel.getTaskListByResource(rto, true);
		if (list!=null) {
			for (ResourceTaskTO rtto : list) {
				if (rtto.getTask()!=null && rtto.getTask().getProject()!=null && rtto.getTask().getCategory()!=null && rtto.getTaskStatus()!=null){
					SyndContent description = new SyndContentImpl();
				    description.setType("text/html");
			    	description.setValue(rtto.getTask().getDescription());
			    	
			    	String name = "[" + rtto.getTask().getId() + "@" + rtto.getTask().getProject().getName() + "] " + rtto.getTask().getName() + " [" + rtto.getTaskStatus().getName() + "]"; 
			    	
					SyndEntry entry = new SyndEntryImpl();
					entry.setTitle(name);	
					entry.setPublishedDate( rtto.getLatestDate() );
					entry.setLink(path + "&id=" + rtto.getTask().getId() + "&projectId=" + rtto.getTask().getProject().getId());
					
					String author = rtto.getResource().getUsername();
					if (author.equals(RootTO.ROOT_USER)) {
						author = "";
					}
					
					entry.setAuthor(author);
					entry.setDescription( description );
			    
					ArrayList<SyndCategory> categories = new ArrayList<SyndCategory>();
					
					SyndCategoryImpl cat = new SyndCategoryImpl();
					cat.setName(rtto.getTask().getCategory().getName());
					cat.setTaxonomyUri("");
					categories.add(cat);
				    
					SyndCategoryImpl catstat = new SyndCategoryImpl();
					catstat.setName(rtto.getTaskStatus().getName());
					catstat.setTaxonomyUri("");
					categories.add(catstat);

					entry.setCategories(categories);
				    
					response.add(entry);				    
				}
			}
		}
		return response;
	}
	
	
	private Vector<SyndEntry> getUnclosedReqs(HttpServletRequest request,UserTO uto) throws BusinessException {
		Vector<SyndEntry> response = new Vector<SyndEntry>();		
		RequirementDelegate del = new RequirementDelegate();
		
		String path = this.getPath(request);
		
		Vector<RequirementTO> list = del.getListByUser(uto, true, true);
		if (list!=null) {
			for (RequirementTO to : list) {
				if (to.getProject()!=null && to.getRequester()!=null && to.getProject().getId()!=null && to.getCategory()!=null) {
					
					String name = "REQ:[" + to.getId()+ "] [" + to.getRequester().getUsername() + "@" + to.getProject().getName() + "]";
					
					SyndContent description = new SyndContentImpl();
				    description.setType("text/html");
				    description.setValue( to.getDescription());
				    
					SyndEntry entry = new SyndEntryImpl();
					entry.setTitle(name);
					entry.setPublishedDate( to.getCreationDate() );
					entry.setLink(path + "&id=" + to.getId() + "&projectId=" + to.getProject().getId());
					entry.setAuthor(to.getRequester().getUsername());
					entry.setDescription( description );
					
					ArrayList<SyndCategory> categories = new ArrayList<SyndCategory>();
					SyndCategoryImpl cat = new SyndCategoryImpl();
					cat.setName(to.getCategory().getName());
					cat.setTaxonomyUri("");
					categories.add(cat);
				    entry.setCategories(categories);
				    
					response.add(entry);					
				}
			}
		}
		return response;
	}
	

	private Vector<SyndEntry> getPublicRisks(HttpServletRequest request,UserTO uto) throws BusinessException {
		Vector<SyndEntry> response = new Vector<SyndEntry>();		
		RiskDelegate rdel =  new RiskDelegate();

		String path = this.getPath(request);

		Vector<RiskTO> list = rdel.getRiskList(ProjectTO.PROJECT_ROOT_ID, uto.getId());
		if (list!=null) {
			for (RiskTO to : list) {
				if (to.getProject()!=null && to.getProject().getId()!=null && to.getCategory()!=null) {

					SyndContent description = new SyndContentImpl();
				    description.setType("text/html");
				    description.setValue( to.getDescription());
				    
					SyndEntry entry = new SyndEntryImpl();
					entry.setTitle(to.getName());
					entry.setPublishedDate( to.getCreationDate() );
					entry.setLink(path + "&id=" + to.getId() + "&projectId=" + to.getProject().getId());
					entry.setDescription( description );
					
					ArrayList<SyndCategory> categories = new ArrayList<SyndCategory>();
					SyndCategoryImpl cat = new SyndCategoryImpl();
					cat.setName(to.getCategory().getName());
					cat.setTaxonomyUri("");
					categories.add(cat);
				    entry.setCategories(categories);
				    
					response.add(entry);					
				}
			}
		}	
		return response;
	}
	
	
	private Vector<SyndEntry> getPublicOcc(HttpServletRequest request, UserTO uto) throws BusinessException { 
		Vector<SyndEntry> response = new Vector<SyndEntry>();
		OccurrenceDelegate odel =  new OccurrenceDelegate();
		
		String path = this.getPath(request);
		
		Vector<OccurrenceTO> list = odel.getOccurenceList(ProjectTO.PROJECT_ROOT_ID, uto.getId(), true);
		if (list!=null) {
			for (OccurrenceTO to : list) {
				if (to.getProject()!=null && to.getProject().getId()!=null) {
					SyndContent description = new SyndContentImpl();
				    description.setType("text/html");
				    description.setValue( to.getDescription() );
				    
					SyndEntry entry = new SyndEntryImpl();
					entry.setTitle(to.getName());
					entry.setPublishedDate( to.getCreationDate() );
					entry.setLink(path + "&id=" + to.getId() + "&projectId=" + to.getProject().getId());
					entry.setDescription( description );
				    
					response.add(entry);					
				}
			}
		}	
		return response;
	}
	
	
	
	private String getTitle(HttpServletRequest req, UserTO uto, String rssId) {
		String response = "";
		
		ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", uto.getLocale());
		if (rb!=null) {
			if (rssId.equals(EdiTO.RSS_TYPE_ALL_OCC)) {
				response = rb.getString("label.rss.type.6");
			} else if (rssId.equals(EdiTO.RSS_TYPE_ALL_RISK)) {
				response = rb.getString("label.rss.type.5");
			} else if (rssId.equals(EdiTO.RSS_TYPE_ALL_TASK)) {			
				response = rb.getString("label.rss.type.2");
			} else if (rssId.equals(EdiTO.RSS_TYPE_TEAM_INFO)) {
				response = rb.getString("label.rss.type.3");
			} else if (rssId.equals(EdiTO.RSS_TYPE_UNCL_REQ)) {
				response = rb.getString("label.rss.type.4");
			} else if (rssId.equals(EdiTO.RSS_TYPE_UNCL_TASK)) {
				response = rb.getString("label.rss.type.1");
			}			
		}
		
		return response;
	}	
	
	
	private String getPath(HttpServletRequest request) {
		String reqUri = request.getRequestURI();	
		reqUri = reqUri.substring(0, reqUri.indexOf("/edi"));
		return SessionUtil.getUri(request) + reqUri + "/do/viewKb?operation=showEntity";
	}
}
