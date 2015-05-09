package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PreferenceTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.StringUtil;

public class AgilePanelTaskDecorator extends ColumnDecorator {

	public String decorate(Object columnValue, String tag) {
		String response = "";
		RequirementWithTasksTO rwto = (RequirementWithTasksTO)this.getObject();		
		Vector<ResourceTaskTO> list = rwto.getResourceTaskList();
		if (list!=null) {
			
			UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
			PreferenceTO pto = uto.getPreference();
			boolean hideCanceledTask = false;
			try {
				hideCanceledTask = (pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_TSK)).equals("on");
			} catch (Exception e){
				hideCanceledTask = false;
			}
			
			Iterator<ResourceTaskTO> i = list.iterator();
			while(i.hasNext()) {
				ResourceTaskTO rtto = i.next();
				int estimatedTime = 0;
				if (rtto.getEstimatedTime()!=null) {
					estimatedTime = rtto.getEstimatedTime().intValue();
				}
				TaskStatusTO tsto = rtto.getTaskStatus();			
				boolean isCancel = (tsto.getStateMachineOrder()!=null && tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CANCEL));
				
				if (isCancel && hideCanceledTask) {
					response = response + "";
				} else {
					boolean isUnassigned = (rtto.getResource()!=null && rtto.getResource().getUsername()!=null && rtto.getResource().getUsername().equals(RootTO.ROOT_USER));
					Locale loc = super.getCurrentLocale();

					if (tag.equals("TODO") && tsto.isOpen()) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("DONE") && tsto.isFinish()) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("PROGRESS") && !tsto.isFinish() && !tsto.isOpen()) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("EST_RANGE_1") && estimatedTime<=120) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("EST_RANGE_2") && estimatedTime>120 && estimatedTime<=480) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("EST_RANGE_3") && estimatedTime>480) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));

					} else if (tag.equals("ASSIG_1") && !isUnassigned) {
						response = response.concat(getBox(rtto, tsto, true, loc, pto));
						
					} else if (tag.equals("ASSIG_2") && isUnassigned) {
						response = response.concat(getBox(rtto, tsto, false, loc, pto));
					}					
				}
			}
		}
		return response;
	}

	public String getBox(ResourceTaskTO rtto, TaskStatusTO tsto, boolean showUser, Locale loc, PreferenceTO pto){
		String response = "";
		TaskTO tto = rtto.getTask();
		TaskDelegate tdel = new TaskDelegate();
		boolean isDone = tsto.isFinish();
		
		String username = "";
		if (showUser) {
			username = rtto.getResource().getUsername();
			if (username!=null && username.equals(RootTO.ROOT_USER)) {
				username = super.getBundleMessage("title.agilePanelForm.unasign");
			}
			username = " [" + username + "]";			
		}
		
		String cardImg = "postit.png";
		if (tto.getIsUnpredictable()!= null && tto.getIsUnpredictable().booleanValue()) {
			cardImg = "postit2.png";
		}

		boolean isCancel = false;
		if (tsto!=null && tsto.getStateMachineOrder()!=null && tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
			isCancel = true;	
		}

		response = response.concat("<div id=\""+ rtto.getId() + "\" class=\"agileTaskBox\" " +
				"onmouseout=\"hideIconTask('EDIT_"+ rtto.getId() + "');hideIconTask('REMOVE_"+ rtto.getId() + "');\" " +
				"onmouseover=\"showIconTask('EDIT_"+ rtto.getId() + "');showIconTask('REMOVE_"+ rtto.getId() + "');\">");
		response = response.concat("<table width=\"69\" height=\"75\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed; background: url(../images/" + cardImg + ") no-repeat top left; border:none\">");
		
		response = response.concat("<tr height=\"14\" class=\"agilePanelPost\">" +
				"<td width=\"14\"><div id=\"EDIT_"+ rtto.getId() + "\" class=\"agileIconPost\"><a href=\"javascript:editPostTask('"+ rtto.getId() + "');\">" +
						"<img src=\"../images/edit.gif\" border=\"0\"></a></div></td>\n");
		
		response = response.concat("<td width=\"41\">");
		if (tdel.isBlocked(tto)) {
			response = response.concat("<img src=\"../images/small_lock.gif\" border=\"0\">");
		}
		response = response.concat("&nbsp;</td>\n");
		
		response = response.concat("<td width=\"14\"><div id=\"REMOVE_"+ rtto.getId() + "\" class=\"agileIconPost\">");
		if (isDone) {
			response = response.concat("&nbsp;");
		} else {
			response = response.concat("<a href=\"javascript:removePostTask('"+ rtto.getId() + "');\"><img src=\"../images/remove.gif\" border=\"0\"></a>");			
		}
		response = response.concat("</div></td></tr>");
		response = response.concat("<tr class=\"agilePanelPost\"><td colspan=\"3\">");
		if (isCancel){
			response = response.concat("<strike>");	
		}
		response = response.concat(tto.getName() + username) ;
		
		response = response.concat(this.getTimeContent(rtto, loc, pto)) ;
		
		if (isCancel){
			response = response.concat("</strike>");
		}		
		response = response.concat("</td></tr>") ;
		response = response.concat("</table>");
		response = response.concat("<script>hideIconTask('EDIT_"+ rtto.getId() + "');hideIconTask('REMOVE_"+ rtto.getId() + "');</script>");
		response = response.concat("</div>");
				
		return response;
	}
	
	
	private String getTimeContent(ResourceTaskTO rtto, Locale loc, PreferenceTO pto) {
		float estTime = 0;
		float actualTime = 0;
		String estTimeStr = "", actTimeStr = "";
		
		if (rtto.getEstimatedTime()!=null) {
			estTime = rtto.getEstimatedTimeInHours();
			if (pto.getPreference(PreferenceTO.INPUT_TASK_FORMAT).equals("1")) {
				estTimeStr = StringUtil.getFloatToString(estTime , loc);
			} else {
				estTimeStr = StringUtil.getIntegerToHHMM(rtto.getEstimatedTime(), loc);	
			}			
		}
		
		if (rtto.getActualTime()!=null) {
			actualTime = rtto.getActualTimeInHours();
			if (pto.getPreference(PreferenceTO.INPUT_TASK_FORMAT).equals("1")) {
				actTimeStr = StringUtil.getFloatToString(actualTime, loc);
			} else {
				actTimeStr = StringUtil.getIntegerToHHMM(rtto.getActualTime(), loc);	
			}			
		}
		
		if (estTime>0 || actualTime>0) {
			return " [" + estTimeStr + " / " + actTimeStr + "]";	
		} else {
			return "";
		}
		
	}

	public String decorate(Object columnValue) {
		return decorate(columnValue, "TODO");
	}

	
	public String contentToSearching(Object columnValue) {
		return null;
	}
	
}
