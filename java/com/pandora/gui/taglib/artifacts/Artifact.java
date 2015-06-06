package com.pandora.gui.taglib.artifacts;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;
import org.apache.taglibs.display.LookupUtil;

import com.pandora.ArtifactTO;
import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileTO;
import com.pandora.ResourceTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.taglib.decorator.ArtifactEditDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryLogDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;

public class Artifact extends TagSupport {

	private static final long serialVersionUID = 1L;

    /** Related form name */
    private String name;
	
    /** The id of data collection from http session. This collection 
     * contain all repository item objects (artifacts) that should be displayed by tag lib. */
    private String collection;

    /** The name of attribute or method that contain the id of the source entity */
    private String property;

    /** The name of attribute or method that contain the id of the source entity project */
    private String projectProperty;

    /** The name of attribute or method that contain the id of the source entity project */
    private String multiple = "on";

    /** The name of attribute or method that contain the id of the source entity project */
    private String onlyFolders = "off";

    /** define whether the taglib must be rendered using ajax */    
    private String ajax;
    
    private String onlyBody;
    
    private String height = "107";
    
    
	public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
    	//NoteIcon note = new NoteIcon();
        
        try {
            JspWriter out = pageContext.getOut();
            buff = new StringBuffer();
            
            //get all artifacts from http session
            @SuppressWarnings("unchecked")
			Vector<RepositoryFilePlanningTO> list = (Vector<RepositoryFilePlanningTO>)pageContext.getSession().getAttribute(this.getCollection());
            Object entityId = LookupUtil.lookup(pageContext, this.getName(), this.getProperty(), null, false, null, null, null);
            Object projectId = LookupUtil.lookup(pageContext, this.getName(), this.getProjectProperty(), null, false, null, null, null);
        	UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        	
            if (ignoreAjax()) {
            	
            	String title = bundle("label.artifactTag.title");
            	String newArtifact = bundle("label.artifactTag.new");
            	String browse = bundle("label.artifactTag.browse");
            	String confirmation = bundle("label.artifactTag.removelink.confirm");
            	buff = renderArtifact(list, entityId, projectId, null, uto, title, newArtifact, browse, confirmation, pageContext.getSession());

            } else {
        		buff.append("<div name=\"ATF_DIV_" + name + "\" id=\"ATF_DIV_" + name + "\">");							
        		buff.append("<center><img src=\"../images/indicator.gif\" id=\"ATF_TABLE_" + name + "\" border=\"0\" alt=\"\" title=\"\"/></center>");					
        		buff.append("<script language=\"javascript\">requestArtifactBody('ATF_DIV_" + name + "', '" + name + "', 'projectId=" + projectId +"|entityId=" + entityId + "' );</script>");						
        		buff.append("</div>");	            	
            }

            
            out.println(buff.toString());                
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Artifacts tag lib error", e);
        }
            
        return SKIP_BODY;
    }    
    
	
    public StringBuffer renderArtifact(Vector<RepositoryFilePlanningTO> list, Object entityId, Object projectId, String artifactId, UserTO uto, String titleMsg, 
    		String newArtifactMsg, String browseMsg, String confirmationMsg, HttpSession session) throws BusinessException {
    	
    	RepositoryEntryTypeDecorator imgDec = new RepositoryEntryTypeDecorator();
    	RepositoryEntryNameDecorator nameDec = new RepositoryEntryNameDecorator();
    	ArtifactEditDecorator editDec = new ArtifactEditDecorator();
		RepositoryEntryLogDecorator logDec = new RepositoryEntryLogDecorator(); 
    	UserDelegate udel = new UserDelegate();
    	ProjectDelegate pdel = new ProjectDelegate();
    	
        StringBuffer buff = new StringBuffer();
        
        if (entityId!=null && entityId instanceof String && projectId!=null && projectId instanceof String) {

        	ProjectTO pto = pdel.getProjectObject(new ProjectTO((String)projectId), true);
        	if (pto!=null) {
            	ResourceTO rto = new ResourceTO(uto.getId());
            	rto.setProject(pto);
            	rto = udel.getResource(rto);
            	boolean allowedRep = (rto!=null && rto.getBoolCanSeeRepository() && pto.getRepositoryClass()!=null && !pto.getRepositoryClass().equals("-1"));
            	
            	if (!this.isOnlyBody()) {
                	buff.append("<tr class=\"formBody\">\n");            
                   	buff.append("   <td><img src=\"../images/artifact.png\" " + HtmlUtil.getHint(titleMsg) + " border=\"0\" >&nbsp;&nbsp;<b>" + titleMsg + "</b></td>\n");
                   	buff.append("</tr>\n");
            	}
            	
                buff.append("<tr class=\"formBody\">\n");
                buff.append("<td>\n");
                    
                if (!this.isOnlyBody()) {                    
                    buff.append("<table class=\"table\" width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"2\">\n");

                    buff.append("  <tr class=\"rowHighlight\">\n");
                    buff.append("       <td colspan=\"4\">");
                    buff.append("       <table class=\"table\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
                    buff.append("       <tr class=\"rowHighlight\"><td>");

                    if (allowedRep) {
                    	buff.append("   <input type=\"button\" name=\"newButton\" " +
                    					    "onclick=\"javascript:displayMessage('../do/showNewArtifact?operation=prepareForm&projectId=" + projectId + "&id=" + entityId + "', 600, 400);\" " +
                    					    "value=\"" + newArtifactMsg + "\" class=\"button\">\n");                	
                    } else {
                    	buff.append("&nbsp;");
                    }

                    buff.append("       </td><td>&nbsp;</td>");
                    buff.append("       <td width=\"60\">");
                    if (allowedRep) {
                    	buff.append("   <input type=\"button\" name=\"browseButton\" " +
                    					    "onclick=\"javascript:displayMessage('../do/showRepositoryViewer?operation=browse&path=&projectId=" + projectId + 
                    					    "&onlyFolders=" + this.onlyFolders + "&multiple=" + this.multiple + "', 600, 400);\" " +
                    					    "value=\"" + browseMsg + "\" class=\"button\">\n");                	
                    } else {
                    	buff.append("&nbsp;");
                    }
                    buff.append("       </td></tr>");                    
                    buff.append("       </table></td>");
                    buff.append("  </tr>");
                    buff.append("</table>");                   	
            	}
                
            	
                if (list!=null && list.size()>0) {

                	if (list.size()>5) {
                        buff.append("<div id=\"ARTIFACT_LIST_" + entityId + "\" style=\"width:100%; height:" + height + "px; overflow-y: scroll; overflow-x: hidden;\">");
                	}

                    buff.append("<table class=\"table\" width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"2\">\n");
                    Iterator<RepositoryFilePlanningTO> i = list.iterator();
                    while(i.hasNext()) {
                    	RepositoryFilePlanningTO rfpto = (RepositoryFilePlanningTO)i.next();
                    	RepositoryFileTO rfto = rfpto.getFile();
                    	rfto.setName(rfto.getPath());
                    	rfto.setPlanning(pto);

                       	imgDec.init(session, null);
                       	imgDec.initRow(rfto, 0, 0);
                       	logDec.init(session, null);
                       	logDec.initRow(rfto, 0, 0);
                       	editDec.init(session, null);
                       	editDec.initRow(rfto, 0, 0);
                       	
                       	String img = imgDec.decorate(new Boolean(false));
                       	String path = nameDec.formatPath(rfto.getPath(), pto.getRepositoryURL());
                       	String log = logDec.decorate(null);
                    	
						String url = path;
						if (allowedRep) {
							url = "<a class=\"gridLink\" href=\"../do/showRepositoryViewer?operation=getFile&projectId=" + pto.getId() + "&path=" + path + "\" border=\"0\"> \n" + path + "</a>";
						}
                       	
                    	buff.append("<tr class=\"tableRowEven\">");
                        
                    	if (this.isOnlyBody()) {
                        	String linkcolapse= "&nbsp;";
                        	 if (rfpto.getGridRowNumber()>1) {
                        		 linkcolapse = "<a href=\"javascript:requestArtifactBody('ATF_DIV_" + name + "', '" + name + "', 'projectId=" + projectId + "|entityId=" + entityId + "|artifactId=" + rfpto.getId() + "' );\"><img border=\"0\" src=\"../images/plus.gif\" /></a>";
                        	 }
                        	buff.append("     <td class=\"tableCell\" width=\"20px\" align=\"center\" valign=\"middle\">" + linkcolapse + "</td>\n");
                        }
                        
                    	buff.append("     <td class=\"tableCell\" width=\"30px\" align=\"center\" valign=\"middle\">" + img + "</td>\n");                       
                        buff.append("     <td colspan=\"2\" class=\"tableCell\" align=\"left\" valign=\"top\">" + url + "</td>\n");
                        if (!this.isOnlyBody()) {
                        	buff.append("     <td class=\"tableCell\" width=\"30px\" align=\"center\" valign=\"top\">" + log + "</td>\n");
                        }
                    	
                        if (allowedRep && !this.isOnlyBody()) {
                           	String editImage = editDec.decorate(rfpto.getEntity().getId());
                        	buff.append("     <td class=\"tableCell\" width=\"30px\" align=\"center\" valign=\"top\"><a href=\"javascript:breakRepositoryEntityLink('" + confirmationMsg + "', '" + rfpto.getId() + "', '" + rfpto.getEntity().getId() + "');\"><img src=\"../images/linkbreak.png\" border=\"0\"/></a></td>\n");
                        	buff.append("     <td class=\"tableCell\" width=\"30px\" align=\"center\" valign=\"top\">" + editImage + "</td>\n");                        	
                        }
                        buff.append("</tr>");
                        
                    	if (this.isOnlyBody() && artifactId!=null) {
                        	 if (rfpto.getId().equals(artifactId)) {
                        		 buff.append(this.getPlanningListFromArtifact(rfto.getPath(), (String)entityId, artifactId));		 
                        	 }
                        }
                        
                    }
                    buff.append("</table>");
                    
                    if (list.size()>5) {
                    	buff.append("</div>");
                    }
                }
                buff.append("</td></tr>");
        	}
        }
    	return buff;
    }
    
    
    /*
    private String getFileNote(RepositoryFilePlanningTO rfpto) {
    	StringBuffer response = new StringBuffer("");
    	if (rfpto!=null) {
    		RepositoryFileTO rfto = rfpto.getFile();
    		if (rfto!=null) {
        		response.append("<b>" + bundle("label.formRepository.author") + "</b>: " + rfto.getAuthor() + "<br>");
        		response.append("<b>" + bundle("label.formRepository.size") + "</b>: " + rfto.getFileSize() + "<br>");    			
    		}
    	}
		return response.toString();
	}
	*/


	private StringBuffer getPlanningListFromArtifact(String artifactPath, String planningId, String artifactId) throws BusinessException {
		StringBuffer buff = new StringBuffer();
		RepositoryDelegate rdel = new RepositoryDelegate();
		PlanningDelegate pdel = new PlanningDelegate();
		
		RepositoryFileTO rfto = new RepositoryFileTO();
		rfto.setPath(artifactPath);
		rfto.setArtifact(new ArtifactTO(artifactId));
		Vector<RepositoryFilePlanningTO> list = rdel.getEntitiesFromFile(rfto);
		
		if (list!=null) {
	    	for (RepositoryFilePlanningTO rfpto : list) {
    			PlanningTO pto = pdel.getSpecializedObject(rfpto.getEntity());
    			String name = "";
		        if (pto.getType().equals(PlanningRelationTO.ENTITY_TASK)) {
		        	name = ((TaskTO)pto).getName();
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_REQ)) {
		        	name = pto.getDescription();
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_OCCU)) {
		        	name = ((OccurrenceTO)pto).getName();
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_RISK)) {
		        	name = ((RiskTO)pto).getName();
		        } 
		        
		        if (name!=null && name.length()>100) {
		        	name = name.substring(0, 100) + "...";
		        }
    			
		    	buff.append("<tr class=\"tableRowEven\">\n");	    		
	    		buff.append("     <td colspan=\"2\" class=\"tableCell\">&nbsp;</td>\n");
	    		buff.append("     <td class=\"tableCell\" width=\"30\"><img src=\"" + HtmlUtil.getEntityIcon(pto.getType()) + "\" border=\"0\"></td>\n");
		    	buff.append("     <td class=\"tableCell\">[<a class=\"gridLink\" href=\"javascript:loadMindMap('" + rfpto.getEntity().getId() + "');\" border=\"0\" >" + rfpto.getEntity().getId() + "</a>] " + name + "</td>\n");
		    	buff.append("</tr>\n");
			}

		}
    	
		return buff;
	}


	///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    } 

    
    /////////////////////////////////////////////// 
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}
	
	
    /////////////////////////////////////////////// 
	public String getProjectProperty() {
		return projectProperty;
	}
	public void setProjectProperty(String newValue) {
		this.projectProperty = newValue;
	}	
	
	
	
    /////////////////////////////////////////////// 
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String newValue) {
		this.multiple = newValue;
	}


    /////////////////////////////////////////////// 
	public String getOnlyFolders() {
		return onlyFolders;
	}
	public void setOnlyFolders(String newValue) {
		this.onlyFolders = newValue;
	}


	///////////////////////////////////////////////    
    private String bundle(String key){
    	String response = "";
    	try {
    		response = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, key, null);
		} catch (JspException e) {
			response = "";
			e.printStackTrace();
		}
		return response;
    }
    
    //////////////////////////////////////////
	public String getAjax() {
		return ajax;
	}
	public void setAjax(String newValue) {
		this.ajax = newValue;
	}
	
	private boolean ignoreAjax(){
		boolean response = true;
		if (ajax!=null) {
			response = ajax.trim().equals("false");
		}
		return response;
	}

	
    //////////////////////////////////////////
	public String getOnlyBody() {
		return onlyBody;
	}
	public void setOnlyBody(String newValue) {
		this.onlyBody = newValue;
	}    
	private boolean isOnlyBody(){
		boolean response = false;
		if (onlyBody!=null) {
			response = onlyBody.trim().equals("true");
		}
		return response;
	}	

	
    public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	
}
