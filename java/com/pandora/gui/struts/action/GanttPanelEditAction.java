package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.ResourceTaskAllocDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.gui.struts.form.GanttPanelEditForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


public class GanttPanelEditAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		ResourceTaskAllocDelegate del = new ResourceTaskAllocDelegate();
		ResourceTaskDelegate trdel = new ResourceTaskDelegate();
		StringBuffer content = new StringBuffer();
		
		try {
			GanttPanelEditForm frm = (GanttPanelEditForm)form;
			
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
		    if (frmtInput!=null) {
		    	frm.setDecimalInput(!frmtInput.equals("2"));
		    }

			ResourceTaskTO rtto = trdel.getResourceTaskObject(frm.getTaskId(), frm.getResourceId());
			
			Vector<ResourceTaskAllocTO> list = del.getListByResourceTask(rtto);
			if (list!=null) {
				String cursor = null;
				String lastCursor = null;
			    Integer value = 0;
			    Timestamp currDay = null;
			    Timestamp labelDate = null;
			    
				for (ResourceTaskAllocTO rtato : list) {

					currDay = DateUtil.getChangedDate(rtto.getInitialDate(), Calendar.DATE, rtato.getSequence()-1);
					cursor = DateUtil.getDate(currDay, "yyyy-MM-dd", uto.getLocale());
					if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)) {
						cursor = "W_" + DateUtil.get(currDay, Calendar.WEEK_OF_YEAR);
					}
					
					if (labelDate==null) {
						labelDate = currDay; 
					}
					
					if (lastCursor==null){
						if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)) {
							lastCursor = cursor;	
						} else{
							lastCursor = "";
						}
					}

					if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_DAY)) {
						value += rtato.getAllocTime();	
					}
					
					if (!lastCursor.equals(cursor)) {
						content.append(this.createRow(labelDate, uto, (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)?lastCursor:cursor), frm, value, request));
						lastCursor = cursor;
						value = 0;
						if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_DAY)) {
							labelDate = null;	
						} else {
							labelDate = currDay;
						}
					}

					if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)) {
						value += rtato.getAllocTime();	
					}
				}
				
				if (currDay!=null && value>0) {
					content.append(createRow(labelDate, uto, (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)?lastCursor:cursor), frm, value, request));
				}				
			}
			
			frm.setAllocHtml(content.toString());
			
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}
		
		return mapping.findForward("showGanttEdit");
	}
	

	private StringBuffer createRow(Timestamp currDay, UserTO uto, String cursor, GanttPanelEditForm frm, Integer value,  HttpServletRequest request){
		StringBuffer response = new StringBuffer();
		
		String column1 = DateUtil.getDate(currDay, uto.getCalendarMask(), uto.getLocale());
	    String column2 = "", column3 = "";
		if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)) {
			column2 = super.getBundleMessage(request, "gantt.edit.label.week")+ " " + DateUtil.get(currDay, Calendar.WEEK_OF_YEAR);;
		} else {
			column2 = DateUtil.getDate(currDay, "EEEE", uto.getLocale());
		}
	    
	    if (frm.isDecimalInput()) {
            float f = (float)((float)value / 60);
            column3 = StringUtil.getFloatToString(f, uto.getLocale());
	    } else {
	    	column3 = StringUtil.getIntegerToHHMM(value, uto.getLocale());		    	
	    }
		
	    response.append("<tr class=\"tableRowOdd\"><td class=\"tableCell\">&nbsp;&nbsp;" + column1 + "</td><td class=\"tableCell\">" + column2 + 
				"</td><td class=\"tableCell\"><center>" + HtmlUtil.getTextBox(cursor, column3, 7, 5) + "&nbsp;h</center></td></tr>");
		
		return response;
	}

	
	public ActionForward saveAlloc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		ResourceTaskAllocDelegate del = new ResourceTaskAllocDelegate();
		ResourceTaskDelegate trdel = new ResourceTaskDelegate();
		HashMap<String,String> hmList = new HashMap<String,String>();
		ArrayList<ResourceTaskAllocTO> arrayList = new ArrayList<ResourceTaskAllocTO>();
		
		try {
		    UserTO uto = SessionUtil.getCurrentUser(request);
			GanttPanelEditForm frm = (GanttPanelEditForm)form;
			ResourceTaskTO rtto = trdel.getResourceTaskObject(frm.getTaskId(), frm.getResourceId());
			Vector<ResourceTaskAllocTO> list = del.getListByResourceTask(rtto);
			if (list!=null) {
				for (ResourceTaskAllocTO rtato : list) {
					Timestamp currDay = DateUtil.getChangedDate(rtto.getInitialDate(), Calendar.DATE, rtato.getSequence()-1);
					String cursor = DateUtil.getDate(currDay, "yyyy-MM-dd", uto.getLocale());
					if (frm.getVisibility().equals(GanttPanelAction.VISIBILITY_WEEK)) {
						cursor = "W_" + DateUtil.get(currDay, Calendar.WEEK_OF_YEAR);
					}
					if (hmList.get(cursor)==null) {
						hmList.put(cursor, cursor);
						rtato.setGenericTag(cursor);
						arrayList.add(rtato);
					}
				}
				
				int totalTime = 0;
				for (ResourceTaskAllocTO rtato : arrayList) {
					String val = request.getParameter(rtato.getGenericTag());
					if (val!=null) {
						
						float f = 0;
					    if (frm.isDecimalInput()) {
				            f = StringUtil.getStringToFloat(val, uto.getLocale());
				            f = f * 60;
					    } else {
					    	f = StringUtil.getHHMMToInteger(val);		    	
					    }
					    Float ff = new Float(f);
					    totalTime+=f;
					    
						rtato.setAllocTime(new Integer(ff.intValue()));
						del.updateResourceTask(rtato);
					}
				}
				
				rtto.setThirdPartComment(frm.getComment());
				rtto.setEstimatedTime(new Integer(totalTime));
				rtto.setAllocList(null); //avoid alloc updating...
				rtto.setHandler(uto);
				trdel.updateResourceTask(rtto);
			}
		}catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}
		
		return mapping.findForward("goToGantt");
	}

}
