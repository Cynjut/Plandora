package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ViewBSCForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into View BSC form
 */
public class ViewBSCAction extends GeneralStrutsAction {

    /**
     * Show the View BSC form (view KPI form)
     */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewBSC";
	    
	    try {

		    //get current form
		    ViewBSCForm frm = (ViewBSCForm)form;
		    
		    //get data of curent project
		    ProjectDelegate pdel = new ProjectDelegate();
		    ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
		    frm.setProjectName(pto.getName());
		    
		    //set default values for searching fields...
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    Timestamp now = DateUtil.getNow();
		    String mask = this.getCalendarMask(request);
		    
		    frm.setFinalDate(DateUtil.getDate(now, mask, loc));
		    frm.setInitialDate(DateUtil.getDate(DateUtil.getChangedDate(now, Calendar.DATE, -7), mask, loc));

		    String defTable = "<tr><td>&nbsp</td></tr>";
		    frm.setFinantialTable(defTable);
		    frm.setCustomerTable(defTable);
		    frm.setProcessTable(defTable);
		    frm.setLearningTable(defTable);
		    
		    CategoryDelegate cdel = new CategoryDelegate();
			Vector categoryListFrmDB = cdel.getCategoryListByType(CategoryTO.TYPE_KPI, new ProjectTO(""), false);		    
			request.getSession().setAttribute("categoryList", categoryListFrmDB);

			refresh(mapping, form, request, response);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);		    
	    }

	    return mapping.findForward(forward);
	}

	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewBSC";
	    
	    try {
	        ReportDelegate rdel = new ReportDelegate();
	        
		    //get current form
		    ViewBSCForm frm = (ViewBSCForm)form;
		    
		    //preparing fields to get data of reports..
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    Timestamp initalDate = DateUtil.getDateTime(frm.getInitialDate(), super.getCalendarMask(request), loc);
		    Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), super.getCalendarMask(request), loc);
		    
		    if (finalDate.equals(initalDate) || finalDate.after(initalDate) ){
		        
			    //get data from data base
		        Timestamp initalDbDate = DateUtil.getChangedDate(initalDate, Calendar.DATE, 1);
		        Timestamp finalDbDate = DateUtil.getChangedDate(finalDate, Calendar.DATE, 1);		    		        
			    Vector listFinan = rdel.getReportListBySearch(initalDbDate, finalDbDate, ReportTO.FINANTIAL_PERSP, frm.getProjectId(), frm.getCategoryId());
			    Vector listCust = rdel.getReportListBySearch(initalDbDate, finalDbDate, ReportTO.CUSTOMER_PERSP, frm.getProjectId(), frm.getCategoryId());
			    Vector listProc = rdel.getReportListBySearch(initalDbDate, finalDbDate, ReportTO.PROCESS_PERSP, frm.getProjectId(), frm.getCategoryId());
			    Vector listLearn = rdel.getReportListBySearch(initalDbDate, finalDbDate, ReportTO.LEARNING_PERSP, frm.getProjectId(), frm.getCategoryId());
			    
			    //convert data to html table format
			    String mask = super.getCalendarMask(request);
			    String title = this.getHtmlTableTitle(loc, request, initalDate, finalDate);
			    frm.setFinantialTable(title + this.convertVectorToHtml(listFinan, mask, loc));
			    frm.setCustomerTable(title + this.convertVectorToHtml(listCust, mask, loc));
			    frm.setProcessTable(title + this.convertVectorToHtml(listProc, mask, loc));
			    frm.setLearningTable(title + this.convertVectorToHtml(listLearn, mask, loc));		        
		    }
		    		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);		    
	    }

	    return mapping.findForward(forward);
	}
	

	/**
	 * Convert list of Report/ReportResult object to html format
	 * @throws BusinessException 
	 */
	private String convertVectorToHtml(Vector list, String mask, Locale loc) throws BusinessException{
	   StringBuffer sb = new StringBuffer();
	   UserDelegate udel = new UserDelegate();
	   Locale currencyLoc = udel.getCurrencyLocale();
	   
	   //print the data line
	   Iterator i = list.iterator();	   
	   while(i.hasNext()){
	       ReportTO rto = (ReportTO)i.next();
	       sb.append(rto.convertToHtml(mask, loc, currencyLoc));
	   }
	   
	   return sb.toString();
	}
	
	
	/**
	 * Get Html content for table title.
	 */
	private String getHtmlTableTitle(Locale loc, HttpServletRequest request, Timestamp initalDate, Timestamp finalDate){
	   StringBuffer sb = new StringBuffer();

	   String mask = super.getCalendarMask(request);
	    
	   //print a title line 
	   sb.append("<tr class=\"formBody\">");
	   sb.append("<td class=\"tableCell\" align=\"center\" valign=\"top\"><b>" + this.getResources(request).getMessage(request.getLocale(), "label.viewBSC.kpiName") + "</b></td>");
	   Timestamp cursor = initalDate;
	   while (cursor.before(finalDate) || cursor.equals(finalDate)){
	       sb.append("<td class=\"tableCell\" align=\"center\" valign=\"center\" width=\"70\"><b>" + DateUtil.getDate(cursor, mask, loc) + "</b></td>");
	       cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
	   }
	   sb.append("</tr>");
   		   
	   return sb.toString();
	}
		
}
