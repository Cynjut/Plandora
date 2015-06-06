package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CalendarSyncTO;
import com.pandora.LeaderTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.bus.PreferenceBUS;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskAllocDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.GanttPanelForm;
import com.pandora.gui.taglib.decorator.OccurrenceColorGridDecorator;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class GanttPanelAction extends GeneralStrutsAction {

	private static final int SLOT_WIDTH = 20;
	
	private static final String LOCKED_STATUS   = "1";
	private static final String UNLOCKED_STATUS = "0";
	
	public static final String VISIBILITY_DAY  = "1";
	public static final String VISIBILITY_WEEK = "2";

	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
	    GanttPanelForm frm = (GanttPanelForm)form;
	    this.defineDefaultValues(frm, request);
	    frm.setResourceId(null);	    
	    return this.refresh(mapping, form, request, response);
	}


	public ActionForward changeGanttTask(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		ResourceTaskAllocDelegate rtadel = new ResourceTaskAllocDelegate();
		String content = "";
		try {
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
			
			GanttPanelForm frm = (GanttPanelForm)form;
			String jobId = frm.getResourceTaskId();
			String[] tokens = jobId.split("-");
			int refSlot = Integer.parseInt(frm.getRefSlot());
			if (frm.getVisibility()!=null && frm.getVisibility().equals(VISIBILITY_WEEK)) {
				refSlot*=7;
			}
						
			if (tokens !=null && tokens.length==3) {
				ResourceTaskTO rtto = getResourceBySlotId(tokens, request);
				
				if (rtto!=null && rtto.getTaskStatus()!=null && rtto.getTaskStatus().isOpenNotReopened()) {
					
					rtto.setActualTime(0);					
					Vector<ResourceTaskAllocTO> currAlloc = rtadel.getListByResourceTask(rtto); 

					if (frm.getChangeTaskCommand()!=null && frm.getChangeTaskCommand().equals("0")) {

						//if the command is to REDIM the task....
						if (refSlot>=1) {
							Vector<ResourceTaskAllocTO> newalloc = new Vector<ResourceTaskAllocTO>();
							int newEstimTime = 0;
							for(int i=0; i< refSlot; i++) {
								ResourceTaskAllocTO rsato = null;
								
								if (currAlloc!=null && currAlloc.size()>i) {
									rsato = currAlloc.get(i);
									if (rsato!=null) {
										newalloc.add(rsato);								
									}
								} else {
									Timestamp currDate = DateUtil.getChangedDate(rtto.getInitialDate(), Calendar.DATE, i);
									int dw = DateUtil.get(currDate, Calendar.DAY_OF_WEEK);
									int capacity = 480;
									if (dw==Calendar.SATURDAY || dw==Calendar.SUNDAY) {
										capacity = 0;
									}
									rsato = new ResourceTaskAllocTO(rtto, i+1, capacity);
									newalloc.add(rsato);
								}
								if (rsato!=null) {
									newEstimTime+=rsato.getAllocTime();	
								}
							}
							if (newalloc.size() > 0) {
								rtto.setAllocList(newalloc);
								rtto.setEstimatedTime(new Integer(newEstimTime));
							    rtdel.changeTaskStatus(rtto, rtto.getTaskStatus().getStateMachineOrder(), rtto.getThirdPartComment(), rtto.getTask().getAdditionalFields(), false);								
							}
						}
						
					} else {
						//if the command is to MOVE the task....
						UserTO uto = SessionUtil.getCurrentUser(request);
						rtto.setAllocList(currAlloc);
						Timestamp iniDt = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
						iniDt = DateUtil.getChangedDate(iniDt, Calendar.DATE, refSlot);

						if (!iniDt.equals(rtto.getStartDate())) {
							rtto.setStartDate(iniDt);
						    rtdel.changeTaskStatus(rtto, rtto.getTaskStatus().getStateMachineOrder(), rtto.getThirdPartComment(), rtto.getTask().getAdditionalFields(), false);							
						}
					}

					content = "OK";
				} else {
					content = super.getBundleMessage(request, "gantt.validate.save");	
				}
			} else {
				content = super.getBundleMessage(request, "gantt.error.save");
			}

		}catch(Exception e) {
			e.printStackTrace();
			content = (e.getMessage()!=null?e.getMessage():e.toString());
		}
		
        PrintWriter out;
		try {
			out = response.getWriter();
	        out.println(content);  
	        out.flush();			
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
	    return null;
	}
		
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		String forward = "showGanttPanel";
		try {		
		    GanttPanelForm frm = (GanttPanelForm)form;
		    Vector<TaskTO> list = null;
		    
		    if (frm.getRequirementId()!=null && !frm.getRequirementId().trim().equals("")) {
		    	list = this.getGanttByRequirement(frm, request);
		    } else {
		    	list = this.getGanttByProject(frm, request);	
		    }
	        setTaskName(list, frm, request);		    
	        setTimeLine(frm, request);
	        
	        this.savePreferences(form, request);
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showGanttForm", e);
		}
		return mapping.findForward(forward);
	}



	public ActionForward changeLockStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		try {		
		    GanttPanelForm frm = (GanttPanelForm)form;
		    if (frm.getLockedGantt().equals(LOCKED_STATUS)) {
		    	frm.setLockedGantt(UNLOCKED_STATUS);
		    } else {
		    	frm.setLockedGantt(LOCKED_STATUS);
		    }
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showGanttForm", e);
		}
	    return this.refresh(mapping, form, request, response);
	}
	
	
	
	private void setTaskName(Vector<TaskTO> taskList, GanttPanelForm frm, HttpServletRequest request) throws Exception{
		StringBuffer sbLeftBar = new StringBuffer();
		StringBuffer sbBody = new StringBuffer();
		long slotSize = DateUtil.SLOT_TIME_MILLIS;
		
		frm.setHtmlGanttLeftBar("");
		frm.setHtmlGanttBody("");			

		if (!frm.isWeekDayVisibility()) {
			slotSize = DateUtil.WEEK_SLOT_TIME_MILLIS;
		}
		
		HashMap<String,String> leaderProjectList = this.getLeaderProjectList(frm, request);
		HashMap<String,TaskTO> taskHash = this.getTaskListInHash(taskList);
		HashMap<String,Vector<ResourceTaskTO>> macroTaskHash = this.getMacroTaskInHash(taskList);
		
		//TODO create a hash (based to taskList) with a macro task id (hash_key) that contains child task. 
		//This hash must be used to check if the macro task will be shown (to avoid macro tasks with no task into it)  

		Vector<ResourceTO> resourceList = new Vector<ResourceTO>();
		HashMap<String, ResourceTO> resourceHash = new HashMap<String, ResourceTO>();
		ResourceTO all = new ResourceTO("-1");
		all.setUsername(super.getBundleMessage(request, "label.all"));
		resourceHash.put("-1", all);
		resourceList.add(all);

		Timestamp iniDt = this.getDate(true, frm, request);
		Timestamp finDt = this.getDate(false, frm, request);
		int totalslots = 0;
		if (iniDt!=null && finDt!=null && iniDt.before(finDt)) {
			Timestamp occurrenceCursor = iniDt;
			HashMap<String, Vector<CalendarSyncTO>> occurrences = this.loadOccurrences(frm.getProjectId(), frm, request, iniDt, finDt);
			totalslots = DateUtil.getSlotBetweenDates(iniDt, finDt, slotSize, false) + 1;
			sbBody.append("<table border=\"0\" width=\"" + (totalslots * SLOT_WIDTH) + "px\" id=\"jobsAreaTable\" cellspacing=\"0\" cellpadding=\"0\">");

			HashMap<String, String> nonEditableList = new HashMap<String, String>(); 
			for (TaskTO tto : taskList) {
				Vector<ResourceTaskTO> list = tto.getAllocResources();
				
				//if it is a parent tasks, create a fake resource task to force a creation of visual object (by default, a macro task has not a resource task...)
				if (tto.isParentTask()) {
					list.add(this.getFakeResourceTaskForMacroTask(tto, taskHash));
				} 

				for (ResourceTaskTO rtto : list) {
					if (rtto!=null) {
						Timestamp firstDay = null;
						int width = 0;
						boolean considerResource = true;

						if (frm.getResourceId()!=null && !frm.getResourceId().trim().equals("-1") && !frm.getResourceId().equals(rtto.getResource().getId())) {
							considerResource = false;
						}
						if (resourceHash.get(rtto.getResource().getId())==null) {
							resourceHash.put(rtto.getResource().getId(), rtto.getResource());
							if (!rtto.getResource().getUsername().equals(RootTO.ROOT_USER)) {
								resourceList.add(rtto.getResource());	
							} else {
								ResourceTO rto = rtto.getResource();
								rto.setUsername(super.getBundleMessage(request, "gantt.label.anyrec"));
								resourceList.add(rtto.getResource());
							}
						}
						
						//create gantt body
						if (iniDt!=null && finDt!=null && iniDt.before(finDt) && (considerResource || tto.isParentTask()) ) {
												
							String resProjectId = rtto.getResource().getProject().getId();
							
							boolean isOpen = true;
							if (rtto.getTaskStatus()!=null && rtto.getTaskStatus().getStateMachineOrder()!=null) {
								isOpen = rtto.getTaskStatus().isOpenNotReopened();
							}
							
							boolean editable = (leaderProjectList.get(resProjectId)!=null) && frm.getLockedGantt().equals(UNLOCKED_STATUS) && isOpen;
							
							String cssClass = "ganttBar";
							String jobId = "ganttJob_" + rtto.getId();
							String macroTask = "";
							int left = 0;
							
							firstDay = this.getFirstTaskDay(rtto);
							if (firstDay!=null) {
								left = DateUtil.getSlotBetweenDates(iniDt, firstDay, slotSize, false) * SLOT_WIDTH;

								int sequence = 1;
								if (rtto.getAllocList()!=null && rtto.getAllocList().size()>0) {
									ResourceTaskAllocTO lastSlot = rtto.getAllocList().get(rtto.getAllocList().size() - 1);
									if (lastSlot!=null) {
										sequence = lastSlot.getSequence();
									}
								}
								
								Timestamp finalDate = DateUtil.getChangedDate(firstDay, Calendar.DATE, sequence);
								if (!finalDate.before(iniDt) && !firstDay.after(finDt)) {
									
									if (finalDate.after(DateUtil.getChangedDate(finDt, Calendar.DATE, 1))) {
										finalDate = finDt;
									}
																		
									int innerslots = DateUtil.getSlotBetweenDates(iniDt, finalDate, slotSize, true);
									if (totalslots < innerslots) {
										width = totalslots * SLOT_WIDTH;
										editable = false;
									} else {
										if (!iniDt.after(firstDay)) {
											innerslots = DateUtil.getSlotBetweenDates(firstDay, finalDate, slotSize, true);
											if (innerslots==0 && !frm.isWeekDayVisibility()) {
												innerslots = 1;
											}
										}									
										width = innerslots * SLOT_WIDTH;	
									}
								}
								
								if (firstDay.before(iniDt) || finalDate.after(finDt)) {
									editable = false;
								}
							}
							String cssStyle = "width:" + width + "px; left:" + left + "px";

							String childList = ""; 
							if (tto.isParentTask()) {
								cssClass = "ganttMacroBar";
								jobId = "macroTask_" + rtto.getTask().getId();
								
								boolean showMacroTask = true;
								if (tto.isParentTask() && !considerResource) {
									showMacroTask = this.checkMacroTaskVisibility(tto, frm.getResourceId(), macroTaskHash);
								}
								
								if (showMacroTask) {
									int[] pos = new int[2];
									childList = this.processChildTasks(rtto.getTask(), taskHash, iniDt, finDt, slotSize, pos);
									cssStyle = "width:" + pos[0] + "px; left:" + pos[1] + "px";
									if (pos[0]>0) {
										width = pos[0];
									}									
								}
								
							} else {
								if (tto.getParentTask()!=null) {
									macroTask = " macroTask=\"" + tto.getParentTask().getId() + "\"";	
								}
								cssStyle = cssStyle + this.getJobColor(rtto);
							}

							if (!editable && !tto.isParentTask()) {
								jobId = "notEditableJob_" + rtto.getId();
								nonEditableList.put(rtto.getId(), rtto.getId());
								cssClass = "ganttBarNonEditable";
							}

							if (width > 0) {
								this.writeGanttBodyHtml(frm, request, sbBody, iniDt, tto, rtto, cssClass, jobId, macroTask, cssStyle, childList);
							}
						}

						if (width > 0) {
							this.writeGanttLeftBarHtml(frm, request, sbLeftBar, taskHash, nonEditableList, tto, rtto);
						}

						if (occurrences.size()>0 && firstDay!=null && firstDay.after(iniDt) && firstDay.after(occurrenceCursor)) {
							this.writeOccurrence(frm, request, firstDay, occurrences, sbLeftBar, sbBody, iniDt, slotSize, occurrenceCursor);
							occurrenceCursor = firstDay;
						}
					}
				}
			}
			
			//if necessary...display into gantt chart the remaining occurrences...
			if (occurrences.size()>0) {
				this.writeOccurrence(frm, request, finDt, occurrences, sbLeftBar, sbBody, iniDt, slotSize, iniDt);
			}
			

			sbBody.append("</table>");
			
			if (!sbLeftBar.toString().trim().equals("") && !sbBody.toString().trim().equals("")) {
				frm.setHtmlGanttLeftBar(sbLeftBar.toString());
				frm.setHtmlGanttBody(sbBody.toString());			
			}
			request.getSession().setAttribute("ganttResourceList", resourceList);			
		}
	}


	private boolean checkMacroTaskVisibility(TaskTO tto, String resourceId, HashMap<String, Vector<ResourceTaskTO>> macroTaskHash) {
		boolean response = false;
		Vector<ResourceTaskTO> childList = macroTaskHash.get(tto.getId());
		if (childList!=null) {
			for (ResourceTaskTO rtto : childList) {
				if (rtto.getResource()!=null && rtto.getResource().getId().equals(resourceId)) {
					response = true;
					break;
				}
			}
		}
		return response;
	}


	private HashMap<String, Vector<ResourceTaskTO>> getMacroTaskInHash(Vector<TaskTO> taskList) {
		HashMap<String, Vector<ResourceTaskTO>> response = new HashMap<String, Vector<ResourceTaskTO>>();
		for (TaskTO tto : taskList) {
			if (tto.getParentTask()!=null) {
				Vector<ResourceTaskTO> childList = response.get(tto.getParentTask().getId());
				if (childList==null) {
					childList = new Vector<ResourceTaskTO>();
					response.put(tto.getParentTask().getId(), childList);
				}
				
				Vector<ResourceTaskTO> list = tto.getAllocResources();
				for (ResourceTaskTO rtto : list) {
					childList.add(rtto);
				}				
			}
		}
		return response;
	}


	private void writeGanttBodyHtml(GanttPanelForm frm, HttpServletRequest request, StringBuffer sbBody, Timestamp iniDt, TaskTO tto, 
			ResourceTaskTO rtto, String cssClass, String jobId, String macroTask, String cssStyle, String childList) {

		//define the no-working days background...
		String rowBarStyle = "";
		if (frm.isWeekDayVisibility()) {
			int dw = DateUtil.get(iniDt, Calendar.DAY_OF_WEEK);
			rowBarStyle = "style=\"background: url(../images/bgdailygantt.png) " + ((dw-2) * -20) + "px 0px;\"";
		}  else {
			rowBarStyle = "style=\"background: url(../images/bgmonthlygantt.png) 0px 0px;\"";
		}
		
		sbBody.append("<tr id=\"b_" + rtto.getId() + "\" class=\"ganttRowBar\" " + rowBarStyle + "><td class=\"ganttCellBar\">\n");

		String toolTip = "";
		if (tto!=null && !tto.isParentTask()) {
			UserTO uto = SessionUtil.getCurrentUser(request);
			toolTip = " title=\"" + this.getTaskTip(rtto, uto.getLocale(), request) + "\" ";	
		}
		sbBody.append("<div class=\"" + cssClass + "\" id=\"" + jobId + "\" " + childList.toString() + macroTask + toolTip + "style=\"" + cssStyle + "\" />\n");

		sbBody.append("</td></tr>\n");
	}


	private void writeOccurrence(GanttPanelForm frm, HttpServletRequest request, Timestamp firstJobDay, HashMap<String, Vector<CalendarSyncTO>> occurrences, 
			StringBuffer sbLeftBar, StringBuffer sbBody, Timestamp iniDt, long slotSize, Timestamp occurrenceCursor) {
		OccurrenceColorGridDecorator dec = new OccurrenceColorGridDecorator();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		Timestamp cursor = occurrenceCursor;
		Vector<CalendarSyncTO> list = new Vector<CalendarSyncTO>();
		while(cursor.before(firstJobDay)){
			Vector<CalendarSyncTO> cslist = occurrences.get(DateUtil.getDateTime(cursor, uto.getCalendarMask()));
			if (cslist!=null) {
				list.addAll(cslist);	
			}
			cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
		}
		
		if (list.size()>0) {
			for (CalendarSyncTO csto : list) {
				if (csto.getGenericTag()==null) {
					csto.setGenericTag("SHOW");
					
					String title = StringUtil.formatWordToHtml(csto.getName());
					String status = "-";
					if (csto.getOccurrence()!=null && csto.getOccurrence().getStatusLabel()!=null) {
						status = csto.getOccurrence().getStatusLabel();
					}							
					
					String occId = "&nbsp;";
					if (csto.getOccurrence()!=null && csto.getOccurrence().getId()!=null) {
						occId= csto.getOccurrence().getId();
					}
					sbLeftBar.append("<tr><th width=\"100px\" class=\"ganttlabel\">" + occId + "&nbsp;&nbsp;&nbsp;");

					String img = dec.getOccurrenceBallon(csto.getOccurrence());
					sbLeftBar.append("&nbsp;<img border=\"0\" " + HtmlUtil.getHint(status) + " border=\"0\" src=\"../images/" + img + "\" >");
					
					sbLeftBar.append("&nbsp;&nbsp;&nbsp;</th><th class=\"ganttlabel\" style=\"text-align: left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + crop(title, 60));
					
					sbLeftBar.append("</th><th width=\"70px\" class=\"ganttlabel\">" + csto.getEventDate());  
					sbLeftBar.append("</th><th width=\"70px\" class=\"ganttlabel\">" + csto.getEventDate());
					sbLeftBar.append("</th><th width=\"130px\" class=\"ganttlabel\">-</th>");

					String projectName = "&nbsp;";
					if (csto.getOccurrence()!=null && csto.getOccurrence().getProject()!=null && csto.getOccurrence().getProject().getName()!=null) {
						projectName = csto.getOccurrence().getProject().getName();
					}
					sbLeftBar.append("<th width=\"300px\" class=\"ganttlabel\">" + projectName + "</th>\n");

					sbLeftBar.append("<th width=\"140px\" class=\"ganttlabel\">-</th>");
					
					sbLeftBar.append("</th><th width=\"130px\" class=\"ganttlabel\">" + crop(status, 23) + "</th></tr>");
					
					String eventTsStr = csto.getEventDate() + " " + csto.getEventTime();
					Timestamp event = DateUtil.getDateTime(eventTsStr, uto.getCalendarMask() + " hh:mm:ss", uto.getLocale());					
					int left = DateUtil.getSlotBetweenDates(iniDt, event, slotSize, false) * SLOT_WIDTH ;
					String cssStyle = "left:" + left + "px";
					String toolTip = " title=\"" + StringUtil.formatWordToHtml(csto.getDescription()) +"\" ";
					writeGanttBodyHtml(frm, request, sbBody, iniDt, null, null, "diamond", occId, toolTip, cssStyle, "");					
				}
			}
		}
	}


	private void writeGanttLeftBarHtml(GanttPanelForm frm, HttpServletRequest request,StringBuffer sbLeftBar, 
			HashMap<String, TaskTO> taskHash, HashMap<String, String> nonEditableList, TaskTO tto, ResourceTaskTO rtto) {
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		String nonEditableLbl  = super.getBundleMessage(request, "gantt.message.nonEdit");
		String bilableTaskLbl  = super.getBundleMessage(request, "gantt.message.billable");
		String editTaskLbl  = super.getBundleMessage(request, "gantt.message.editAlloc");
		
		//create gantt left bar
		sbLeftBar.append("<tr id=\"t_" + rtto.getId() + "\"><th width=\"100px\" class=\"ganttlabel\">" + tto.getId());
		if (nonEditableList.get(rtto.getId())!=null && frm.getLockedGantt().equals(UNLOCKED_STATUS)) {
			sbLeftBar.append(" <img " + HtmlUtil.getHint(nonEditableLbl) + " src=\"../images/small_lock.gif\" border=\"0\">");
		} else {
			sbLeftBar.append("&nbsp;&nbsp;&nbsp;");
		}
		
		if (rtto.getBillableStatus()!=null && rtto.getBillableStatus().booleanValue()) {
			sbLeftBar.append(" <img " + HtmlUtil.getHint(bilableTaskLbl) + " src=\"../images/small_money.png\" border=\"0\">");
		} else {
			sbLeftBar.append("&nbsp;&nbsp;&nbsp;");
		}

		if (rtto.getTaskStatus()!=null && rtto.getTaskStatus().isOpenNotReopened()) {
			sbLeftBar.append(" <a href=\"javascript:editTaskAlloc('" + tto.getId() + "', '" + rtto.getResource().getId() + "', '" + rtto.getResource().getProject().getId() + "', '" + frm.getVisibility() + "')\"><img " + HtmlUtil.getHint(editTaskLbl) + " src=\"../images/edit.gif\" border=\"0\"></a>");
		} else {
			sbLeftBar.append("&nbsp;&nbsp;&nbsp;");
		}
		
		sbLeftBar.append("</th><th class=\"ganttlabel\" style=\"text-align: left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

		if(tto.isParentTask()) {
			sbLeftBar.append("<a href=\"javascript:colapseExpandMacroTask('" + tto.getId() + "');\"><img id=\"img_" + tto.getId() + "\" src=\"../images/minus.gif\" border=\"0\"></a>&nbsp;");
		}
		
		String identat = getIdentation(rtto.getTask(), taskHash);
		String taskname = identat + crop(tto.getName(), 60 - (identat.length()/6) ); 
		sbLeftBar.append(taskname);
		
		String resourceName = "-";
		if (!tto.isParentTask()) {
			resourceName = rtto.getResource().getUsername();
			if (resourceName.equals(RootTO.ROOT_USER)) {
				resourceName = super.getBundleMessage(request, "gantt.label.anyrec");
			}							
		}

		sbLeftBar.append("</th><th width=\"70px\" class=\"ganttlabel\">");
		Timestamp firstDay = this.getFirstTaskDay(rtto);
		if (firstDay!=null) {
			sbLeftBar.append(DateUtil.getDate(this.getFirstTaskDay(rtto), uto.getCalendarMask(), uto.getLocale()));	
		} else {
			sbLeftBar.append("&nbsp;");
		}
		sbLeftBar.append("</th><th width=\"70px\" class=\"ganttlabel\">");
		if (firstDay!=null && rtto.getAllocList()!=null && rtto.getAllocList().size()>0) {
			ResourceTaskAllocTO lastSlot = rtto.getAllocList().get(rtto.getAllocList().size() - 1);
			Timestamp finalDate = DateUtil.getChangedDate(firstDay, Calendar.DATE, lastSlot.getSequence() -1 );
			if (finalDate!=null) {
				sbLeftBar.append(DateUtil.getDate(finalDate, uto.getCalendarMask(), uto.getLocale()));	
			} else {
				sbLeftBar.append("&nbsp;");	
			}
		} else {
			sbLeftBar.append("&nbsp;");
		}

		String categoryName = "&nbsp;";
		if (tto.getCategory()!=null && tto.getCategory().getName() !=null && !tto.getCategory().getName().equals("")) {
			categoryName = tto.getCategory().getName();
		}							
		sbLeftBar.append("</th><th width=\"130px\" class=\"ganttlabel\">" + crop(categoryName, 23) + "</th>");

		String projectName = "&nbsp;";
		if (rtto.getTask()!=null && rtto.getTask().getProject()!=null && rtto.getTask().getProject().getName()!=null) {
			projectName = rtto.getTask().getProject().getName();
		}
		sbLeftBar.append("<th width=\"300px\" class=\"ganttlabel\">" + projectName + "</th>\n");
		
		sbLeftBar.append("<th width=\"140px\" class=\"ganttlabel\">" + crop(resourceName, 23) + "</th>");
		
		String status = "-";
		if (rtto.getTaskStatus()!=null) {
			status = rtto.getTaskStatus().getName();
		}							
		sbLeftBar.append("</th><th width=\"130px\" class=\"ganttlabel\">" + crop(status, 23) + "</th></tr>");
	}


	private String crop(String label, int maxSize) {
		String response = label;
		if (label!=null && label.length() > maxSize) {
			response = label.substring(0, maxSize-1) + "...";
		}
		return response;
	}

	
	private String getIdentation(TaskTO tto, HashMap<String,TaskTO> taskHash) {
		String response = "";
		
		TaskTO copyTask = taskHash.get(tto.getId());
		if (copyTask.getParentTask()!=null) {
			response = getIdentation(copyTask.getParentTask(), taskHash);
			response = response + "&nbsp;&nbsp;&nbsp;&nbsp;";			
		}		
		
		return response;
	}
	

	private void setTimeLine(GanttPanelForm frm, HttpServletRequest request) throws BusinessException{
		StringBuffer sbDown = new StringBuffer();
		StringBuffer sbUp = new StringBuffer();
		UserTO uto = SessionUtil.getCurrentUser(request);
		frm.setHtmlGanttTimeLine("");
		
		Timestamp iniDt = this.getDate(true, frm, request);
		Timestamp finDt = this.getDate(false, frm, request);
		
		if (iniDt!=null && finDt!=null) {
			
			//give one more slot to (visually) overcome the vertical scroll bar.. 
			finDt = DateUtil.getChangedDate(finDt, this.getGranularity(frm, false), 1);
			
			int counter = 0;
			int totalslots = 0;
			Timestamp cursor = iniDt;
			int oldSuperType = DateUtil.get(cursor, this.getGranularity(frm, true));

			while(!cursor.after(finDt)) {
				int superType = DateUtil.get(cursor, this.getGranularity(frm, true));
				if (superType!=oldSuperType) {
					String label = "";
					if (counter > this.getMinSlotToShowLabel(frm)) {
						Timestamp refDt = DateUtil.getChangedDate(cursor, this.getGranularity(frm, false), -counter);
						label = DateUtil.getDate(refDt, this.getGranularityMask(frm, true, uto), uto.getLocale());	
					} else {
						label = "&nbsp;";
					}
					sbUp.append("<th class=\"gantttimelineUp\" colspan=\"" + counter + "\">" + label + "</th>\n");
					counter = 0;
				}
				
				Timestamp refCursor = cursor; 
				if (!frm.isWeekDayVisibility()) {
					refCursor = DateUtil.getChangedDate(cursor, this.getGranularity(frm, false), -1);
				}
				String label = DateUtil.getDate(refCursor, this.getGranularityMask(frm, false, uto), uto.getLocale());
				if (frm.isWeekDayVisibility()) {
					label = label.substring(0, 1);
				}
				sbDown.append("<th class=\"gantttimelineDown\">" + label + "</th>\n");
				
				counter++; totalslots++;
				oldSuperType = superType;
				cursor = DateUtil.getChangedDate(cursor, this.getGranularity(frm, false), 1);
			}
			if (counter>0) {
				String label = "";
				if (counter > this.getMinSlotToShowLabel(frm)) {
					Timestamp refDt = DateUtil.getChangedDate(cursor, this.getGranularity(frm, false), -counter);
					label = DateUtil.getDate(refDt, this.getGranularityMask(frm, true, uto), uto.getLocale());						
				} else {
					label = "&nbsp;";
				}
				sbUp.append("<th class=\"gantttimelineUp\" colspan=\"" + counter + "\">" + label + "</th>\n");
			}

			if (!sbUp.toString().trim().equals("") && !sbDown.toString().trim().equals("")) {
				StringBuffer content = new StringBuffer();
				content.append("<table id=\"ganttBarTable\" border=\"0\" height=\"28px\" width=\"" + (totalslots * SLOT_WIDTH) + "px\" cellspacing=\"0\" cellpadding=\"0\">\n");
				content.append("<tr class=\"ganttBarRow\">\n");
				content.append(sbUp);
				content.append("</tr>\n");
				content.append("<tr class=\"ganttBarRow\">\n");
				content.append(sbDown);
				content.append("</tr></table>\n");
				frm.setHtmlGanttTimeLine(content.toString());
			}			
		}
	}
	
	
	private int getMinSlotToShowLabel(GanttPanelForm frm) {
		int response = 4; 
		if (!frm.isWeekDayVisibility()) {
			response = 3;
		}
		return response;
	}


	private String getGranularityMask(GanttPanelForm frm, boolean isSuperType, UserTO uto) {
		String response = ""; 
		if (isSuperType) {
			if (frm.isWeekDayVisibility()) {
				response = uto.getCalendarMask();
			} else {
				response = "MMM/yyyy";
			}
		} else {
			if (frm.isWeekDayVisibility()) {
				response = "E";
			} else {
				response = "w";
			}			
		}
		return response;
	}


	private int getGranularity(GanttPanelForm frm, boolean isSuperType) {
		int response = -1; 
		if (isSuperType) {
			if (frm.isWeekDayVisibility()) {
				response = Calendar.WEEK_OF_YEAR;
			} else {
				response = Calendar.MONTH;
			}
		} else {
			if (frm.isWeekDayVisibility()) {
				response = Calendar.DATE;
			} else {
				response = Calendar.WEEK_OF_YEAR;
			}
		}
		return response;
	}


	private Vector<TaskTO> getGanttByProject(GanttPanelForm frm, HttpServletRequest request) throws BusinessException {	    
	    TaskDelegate tdel = new TaskDelegate();
	    ProjectTO pto = new ProjectTO(frm.getProjectId());

	    Timestamp iniDate = this.getDate(true, frm, request);
	    Timestamp finalDate = this.getDate(true, frm, request);
	    
	    return tdel.getTaskListByProject(pto, iniDate, finalDate, true, true);
	}	
	
	
	private Vector<TaskTO> getGanttByRequirement(GanttPanelForm frm, HttpServletRequest request) throws BusinessException {
		Vector<TaskTO> response = null;
		TaskDelegate tdel = new TaskDelegate();
		RequirementDelegate rdel = new RequirementDelegate();
		
		RequirementTO rto = rdel.getRequirement(new RequirementTO(frm.getRequirementId()));
		if (rto!=null) {
			response = tdel.getTaskListByRequirement(rto, rto.getProject(), true, true);	
		}
		
		return response;
	}	
	
	private Timestamp getDate(boolean isInitialDate, GanttPanelForm frm, HttpServletRequest request) {
		Timestamp refDt = null;
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		//define default date
		if(isInitialDate) {
			refDt = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.MONTH, -3);	
		} else {
			refDt =  DateUtil.getNowWithoutTime();
		}
		
		if(isInitialDate && frm.getInitialDate()!=null) {		
			refDt = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		} else if(!isInitialDate && frm.getFinalDate()!=null) { 
			refDt = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
		}
		
		return refDt;
	}
	
	private Timestamp getFirstTaskDay(ResourceTaskTO rtto) {
		Timestamp reference = rtto.getActualDate(); 
		if (reference==null) {
			reference = rtto.getStartDate();
		}

		//skip 1 hour to avoid problems with daylight saving.
		if (reference!=null) {
			reference = DateUtil.getChangedDate(reference, Calendar.HOUR, 1);	
		}
		
		return reference;
	}

	
	private String getTaskTip(ResourceTaskTO rtto, Locale loc, HttpServletRequest request) {
		StringBuffer response = new StringBuffer();

		boolean decimalInput = true;
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
	    if (frmtInput!=null) {
	    	decimalInput = !frmtInput.equals("2");
	    }
		
		String resourceName = rtto.getResource().getUsername();
		if (resourceName.equals(RootTO.ROOT_USER)) {
			resourceName = super.getBundleMessage(request, "gantt.label.anyrec");
		}

		response.append("[" + StringUtil.formatWordToHtml(resourceName) + "] ");
		response.append(super.getBundleMessage(request, "gantt.label.estTime") + "=");
		
		String est = StringUtil.getIntegerToHHMM(rtto.getEstimatedTime(), loc);
		if (decimalInput) {
			est = StringUtil.getFloatToString((float)((float)rtto.getEstimatedTime()/60), loc);
		}
		
		if (est!=null && !est.trim().equals("")) {
			response.append(est + "h ");	
		} else {
			response.append("?");
		}

		response.append(super.getBundleMessage(request, "gantt.label.actTime") + "=");

		String act = StringUtil.getIntegerToHHMM(rtto.getActualTime(), loc);
		if (decimalInput && rtto.getActualTime()!=null) {
			act = StringUtil.getFloatToString((float)((float)rtto.getActualTime()/60), loc);
		}
		
		if (act!=null && !act.trim().equals("")) {
			response.append(act + "h");	
		} else { 
			response.append("?");
		}
		
		String status = "&nbsp;";
		if (rtto.getTaskStatus()!=null) {
			status = rtto.getTaskStatus().getName();
		}
		response.append(" [" + status + "]");
		
		return response.toString();
	}

	private String getJobColor(ResourceTaskTO rtto) {
		String color = "C0C0C0"; //default bar color
		if (rtto.getResource().getColor()!=null) {
			color = rtto.getResource().getColor();
		}						
		return " ;border-color:#000000; background-color: #" + color + ";";
	}

	
	private String processChildTasks(TaskTO tto, HashMap<String,TaskTO> taskHash, Timestamp iniDt, Timestamp endDt, long slotSize, int[] pos) {
		String childList = "";
		if (tto.getChildTasks()!=null){

			Timestamp latestDay = iniDt;
			Timestamp earliestDay = endDt;

			for (TaskTO child : tto.getChildTasks().values()) {
				child = taskHash.get(child.getId());
				if (child!=null) {
					for (ResourceTaskTO rtto : child.getAllocResources()) {					
						if (rtto.getAllocList()!=null && rtto.getAllocList().size()>0) {
							Timestamp firstDay = this.getFirstTaskDay(rtto);
							ResourceTaskAllocTO lastSlot = rtto.getAllocList().get(rtto.getAllocList().size() - 1);
							Timestamp finalDay = DateUtil.getChangedDate(firstDay, Calendar.DATE, lastSlot.getSequence());
							if (!iniDt.after(finalDay) && !endDt.before(firstDay)) {
								if (latestDay.before(finalDay)) {
									latestDay = finalDay;
								}
								if (earliestDay.after(firstDay)) {
									earliestDay = firstDay;
								}
								
								if (childList.toString().equals("")){
									childList = "childTask=\"";
								} else {
									childList = childList + ";";	
								}
								childList = childList + rtto.getId();						
							}						
						}
					}					
				}
			}
			if (!childList.equals("")){
				childList = childList + "\" ";
				if (earliestDay.before(iniDt)) {
					earliestDay = iniDt;
				}
				if (latestDay.after(endDt)) {
					latestDay = endDt;
				}
				int innerslots = DateUtil.getSlotBetweenDates(earliestDay, latestDay, slotSize, false);
				pos[0] = (SLOT_WIDTH * innerslots) + SLOT_WIDTH;					
				int diffslots = DateUtil.getSlotBetweenDates(iniDt, earliestDay, slotSize, false);
				pos[1] = SLOT_WIDTH * diffslots;
			}
		}
		
		return childList;		
	}
	

	private HashMap<String, String> getLeaderProjectList(GanttPanelForm frm, HttpServletRequest request) throws BusinessException {
		HashMap<String, String> response = new HashMap<String, String>();
		UserDelegate udel = new UserDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		String projectIdList = pdel.getProjectIn(frm.getProjectId());
		Vector<LeaderTO> leaders = udel.getLeaderByProject(projectIdList);
		if (leaders!=null) {
			for (LeaderTO to : leaders) {
				if (uto.getId().equals(to.getId())) {
					response.put(to.getProject().getId(), to.getProject().getId());		
				}
			}
		}
		
		return response;
	}

	
	private void defineDefaultValues(GanttPanelForm frm, HttpServletRequest request) {
		try {
			UserTO uto = SessionUtil.getCurrentUser(request);
			PreferenceTO pto = uto.getPreference();
			request.getSession().setAttribute("ganttResourceList", new Vector<ResourceTO>());
			
			Timestamp iniDt = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.MONTH, -3);
			String iniDateStr = pto.getPreference(PreferenceTO.GANTT_PANEL_INI_DT);
			if (iniDateStr!=null && !iniDateStr.trim().equals("")) {
				iniDt = DateUtil.getDateTime(iniDateStr, "yyyy-MM-dd", uto.getLocale());
			}
			
			Timestamp finDt = DateUtil.getNowWithoutTime();
			String finDateStr = pto.getPreference(PreferenceTO.GANTT_PANEL_FIN_DT);
			if (finDateStr!=null && !finDateStr.trim().equals("")) {
				finDt = DateUtil.getDateTime(finDateStr, "yyyy-MM-dd", uto.getLocale());
			}
									
			frm.setInitialDate(DateUtil.getDate(iniDt, uto.getCalendarMask(), uto.getLocale()));
			frm.setFinalDate(DateUtil.getDate(finDt, uto.getCalendarMask(), uto.getLocale()));
			
			Vector<TransferObject> visibilityList = new Vector<TransferObject>();
			visibilityList.add(new TransferObject(VISIBILITY_DAY, super.getBundleMessage(request, "gantt.label.visible.1")));
			visibilityList.add(new TransferObject(VISIBILITY_WEEK, super.getBundleMessage(request, "gantt.label.visible.2")));
			request.getSession().setAttribute("ganttVisibilityList", visibilityList);

			String visib = pto.getPreference(PreferenceTO.GANTT_PANEL_VISIB);
			if (visib!=null && !visib.trim().equals("")) {
				frm.setVisibility(visib);
			}

			String cures = pto.getPreference(PreferenceTO.GANTT_PANEL_CURRES);
			if (cures!=null && !cures.trim().equals("")) {
				frm.setResourceId(cures);
			} else {
				frm.setResourceId("-1");	
			}
			
			frm.setLockedGantt(LOCKED_STATUS);
			
			String hideOcc = uto.getPreference().getPreference(PreferenceTO.GANTT_HIDE_OCC);
			frm.setHideOccurrences(hideOcc!=null && hideOcc.equals("on"));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	private ResourceTaskTO getFakeResourceTaskForMacroTask(TaskTO tto, HashMap<String,TaskTO> taskHash) throws BusinessException{
		ResourceTaskTO fakert = null;
		
		TaskDelegate tdel = new TaskDelegate();
		tto = tdel.getTaskObject(tto);
		
		//the macro task must be considered only if contain at least on child task selected on gantt chart... 
		boolean considerThisMacroTask = false;
		if (tto.getChildTasks()!=null) {
			for (TaskTO child : tto.getChildTasks().values()) {
				if (taskHash.get(child.getId())!=null) {
					considerThisMacroTask = true;
					break;
				}
			}			
		}
		
		if (considerThisMacroTask) {
			fakert = new ResourceTaskTO();
			fakert.setTask(tto);
			ResourceTO rfake = new ResourceTO("-1");
			rfake.setUsername(RootTO.ROOT_USER);
			rfake.setProject(tto.getProject());
			fakert.setResource(rfake);			
		}
		
		return fakert;
	}
	
	
	private HashMap<String, TaskTO> getTaskListInHash(Vector<TaskTO> taskList) {
		HashMap<String, TaskTO> response = new HashMap<String, TaskTO>();
		for (TaskTO tto : taskList) {
			response.put(tto.getId(), tto);
		}
		return response;
	}

	
	private ResourceTaskTO getResourceBySlotId(String[] tokens, HttpServletRequest request) throws BusinessException {
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		TaskDelegate tdel = new TaskDelegate();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		ResourceTaskTO rtto = new ResourceTaskTO();
		TaskTO tto = tdel.getTaskObject(new TaskTO(tokens[0]));			
		ResourceTO rto = new ResourceTO(tokens[1]);
		ProjectTO pto = new ProjectTO(tokens[2]);
		rto.setProject(pto);
		rtto.setTask(tto);
		rtto.setResource(rto);
		
		ResourceTaskTO response = rtdel.getResourceTaskObject(rtto);
		response.setTask(tto);
		response.setHandler(uto);
				
		return response;
	}
	
	private void savePreferences(ActionForm form, HttpServletRequest request) throws BusinessException{
		GanttPanelForm frm = (GanttPanelForm)form;
		
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		
		Timestamp iniDt = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		pto.addPreferences(new PreferenceTO(PreferenceTO.GANTT_PANEL_INI_DT, DateUtil.getDate(iniDt, "yyyy-MM-dd", uto.getLocale()), uto));

		Timestamp finDt = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
		pto.addPreferences(new PreferenceTO(PreferenceTO.GANTT_PANEL_FIN_DT, DateUtil.getDate(finDt, "yyyy-MM-dd", uto.getLocale()), uto));
		
		pto.addPreferences(new PreferenceTO(PreferenceTO.GANTT_PANEL_VISIB, frm.getVisibility(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.GANTT_PANEL_CURRES, frm.getResourceId(), uto));
		
		pto.addPreferences(new PreferenceTO(PreferenceTO.GANTT_HIDE_OCC, frm.getHideOccurrences()?"on":"off", uto));
		
		pbus.insertOrUpdate(pto);			
	}
	
	
	private HashMap<String, Vector<CalendarSyncTO>> loadOccurrences(String projectId,  GanttPanelForm frm, HttpServletRequest request, Timestamp startDt, Timestamp finalDt) throws Exception{
        OccurrenceDelegate odel = new OccurrenceDelegate();
        HashMap<String, Vector<CalendarSyncTO>> response = new HashMap<String, Vector<CalendarSyncTO>>();
        
        if (!frm.getHideOccurrences()) {
            UserTO uto = SessionUtil.getCurrentUser(request);
    		String mask = uto.getCalendarMask();

    		UserDelegate udel = new UserDelegate();
    		UserTO root = udel.getRoot();
    		String allClasses = root.getPreference().getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS);

    		Vector<OccurrenceTO> dbList = odel.getOccurenceList(projectId, false);
            if (dbList!=null) {
                Iterator<OccurrenceTO> i = dbList.iterator();
                while(i.hasNext()) {
                	OccurrenceTO oto = (OccurrenceTO)i.next();
                	Locale loc = oto.getLocale();
                	
                	Object bus = null;
                	try {
                		bus = OccurrenceTO.getClass(allClasses, oto.getSource());	
                	}catch(Exception e){
                		e.printStackTrace();
                		bus = null;
                	}
                	
    				if (bus!=null && oto.getVisible()) { //only public events and milestones
    					oto = odel.getOccurrenceObject(oto);

    					CalendarSyncTO csto = ((CalendarSyncInterface)bus).populateCalendarFields(oto, mask, loc);
    					csto.setName(oto.getName());
    					csto.setOccurrence(oto);
    					
    					if (csto!=null && csto.getEventDate()!=null) {
    						String eventTsStr = csto.getEventDate() + " " + csto.getEventTime();
    						Timestamp event = DateUtil.getDateTime(eventTsStr, mask + " hh:mm:ss", loc);
    						if (!event.before(startDt) && !event.after(finalDt)) {
    							Vector<CalendarSyncTO> alist = response.get(csto.getEventDate());
    							if (alist == null) {
    								alist = new Vector<CalendarSyncTO>();
    								response.put(csto.getEventDate(), alist);
    							}
    							alist.add(csto);
    						}
    					}
    				}
            	}
            }                        	
        }
		
        return response;
	}
	
}
