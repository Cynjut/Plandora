package com.pandora.gui.struts.action;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.ReportFieldTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.EmptyReportBusinessException;
import com.pandora.gui.struts.form.ViewReportForm;
import com.pandora.gui.taglib.calendar.Calendar;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

/**
 */
public class ViewReportAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewReport";
		ProjectDelegate pdel = new ProjectDelegate();
	    
	    try {
		    ViewReportForm frm = (ViewReportForm)form;
		    ProjectTO pto = null;

		    UserTO uto = SessionUtil.getCurrentUser(request);
		    Vector prjList = pdel.getProjectListByUser(uto);
			if (prjList!=null && prjList.size()>0) {
				pto = (ProjectTO)prjList.get(0);
				request.getSession().setAttribute("projectList", prjList);					
			} else {
				prjList = new Vector(); 
			}

			if (pto!=null && frm.getProjectId().trim().equals("ALL")) {
				frm.setProjectId(pto.getId());
			}
		    
			request.getSession().setAttribute("projectList", prjList);			
		    	
			if (pto!=null) {
				this.refreshProject(mapping, form, request, response);
				frm.setCategoryId("0");				

				this.refresh(mapping, form, request, response);
			} else {
				request.getSession().setAttribute("exportReportList", new Vector());		
			}
		    
	    } catch (Exception e) {
		    this.setErrorFormSession(request, "error.prepareViewReportForm", e);		    
	    }

	    return mapping.findForward(forward);
	}
    
	
	public ActionForward refreshProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws BusinessException{
	    String forward = "showViewReport";
	    ViewReportForm frm = (ViewReportForm)form;	    
	    ProjectDelegate pdel = new ProjectDelegate();
	    
	    ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
	    frm.setProjectName(pto.getName());
	    
	    CategoryDelegate cdel = new CategoryDelegate();
		Vector categoryListFrmDB = cdel.getCategoryListByType(CategoryTO.TYPE_REPORT, new ProjectTO(frm.getProjectId()), false);		    
		request.getSession().setAttribute("categoryList", categoryListFrmDB);

	    Vector expList = new Vector();
		expList.add(new TransferObject("PDF", "PDF"));
		expList.add(new TransferObject("ODT", "OpenOffice Writer"));
		expList.add(new TransferObject("RTF", "Rich Text Format"));
		request.getSession().setAttribute("exportReportList", expList);
		
		this.refresh(mapping, form, request, response);
		
	    return mapping.findForward(forward);
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewReport";
	    StringBuffer sb = new StringBuffer();
	    
	    try {
		    ViewReportForm frm = (ViewReportForm)form;	        
	        ReportDelegate rdel = new ReportDelegate();
		    ProjectDelegate pdel = new ProjectDelegate();
		    
	        UserTO uto = SessionUtil.getCurrentUser(request);
	        ProjectTO pto = pdel.getProjectByUser(uto, new ProjectTO(frm.getProjectId()));	        
            Vector reportList = rdel.getListBySource(false, frm.getCategoryId(), pto, false);
	        
            //generate html table for each report...
            if (reportList!=null && reportList.size()>0) {
            	
                Iterator i = reportList.iterator();
                while(i.hasNext()) {
                    ReportTO rto = (ReportTO)i.next();
                    rto.setProject(pto);
                    String repPrj = rto.getProject().getId();
                    if (repPrj.equals(frm.getProjectId()) || repPrj.equals(ProjectTO.PROJECT_ROOT_ID)){
                        sb.append(this.formatReportHtml(rto, request));    
                    }
                }
            }
            
            frm.setReportTable(sb.toString());            

        } catch (Exception e) {
		    this.setErrorFormSession(request, "error.prepareViewReportForm", e);		    
	    }

	    return mapping.findForward(forward);
	}

	
	public ActionForward generate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    ActionForward forward = mapping.findForward("showEmptyReport");
	    
	    try {
	        ViewReportForm frm = (ViewReportForm)form;
	        this.clearMessages(request);
	        boolean showReport = true;
	        frm.setExportReportFormat(request.getParameter("reportOutput"));
	        
	        //get the project related to the form
	        ProjectDelegate pdel = new ProjectDelegate();
	        ProjectTO formProject = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
	        
	        ReportDelegate rdel = new ReportDelegate();
	        ReportTO rto = rdel.getReport(new ReportTO(frm.getReportId()));
	        this.verifyFilePath(rto);

	        //link the form project with the report (eventually the report could related with project root)	        
	        rto.setProject(formProject);
			rto.setExportReportFormat(frm.getExportReportFormat());
	        
	        //create a list of fields from form with selected values and set into ReportTO
	        Vector formFields = new Vector();
	        Vector fields = rdel.getReportFields(rto.getSqlStement());
	        if (fields!=null) {
	        	HashMap hm = new HashMap();
	            Iterator i = fields.iterator();
	            while(i.hasNext()) {
	                ReportFieldTO fieldTO = (ReportFieldTO)i.next();
	                
	                if (hm.get(fieldTO.getId())==null) {
	                	hm.put(fieldTO.getId(), fieldTO);
	                	
		                String value = request.getParameter(fieldTO.getFieldToHtml(rto));
		                if (value!=null) {
		                    if (!value.equals("") || fieldTO.getReportFieldType().equals(ReportFieldTO.TYPE_OBJECT)) {
				                fieldTO.setValue(value);
				                formFields.addElement(fieldTO);	                    
			                } else {
			                	this.setErrorFormSession(request, "error.viewReport.mandatoryFields", null);
			                	showReport = false;
			                }
		                }	                	
	                }
	            }
	            rto.setFormFieldsValues(formFields);
	        }
	        rto.setLocale(SessionUtil.getCurrentLocale(request));
	        rto.setHandler(SessionUtil.getCurrentUser(request));      

	        if (showReport) {
		        byte[] reportStream = rdel.performReport(rto);
		        if (reportStream!=null) {
		        	if (frm.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_PDF)) {
		        		response.setContentType("application/pdf");
		        		response.setHeader("content-disposition", "inline;filename=report.pdf");
		        	} else if (frm.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_RTF)) {
		        		response.setContentType("application/rtf");
		        		response.setHeader("content-disposition", "inline;filename=report.rtf");
		        	} else if (frm.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_ODT)) {	
		        		response.setContentType("application/vnd.oasis.opendocument.text");
		        		response.setHeader("content-disposition", "inline;filename=report.odt");
		        	}
		    		
		    		response.setContentLength(reportStream.length); 
		    		OutputStream ouputStream = response.getOutputStream(); 
		    		ouputStream.write(reportStream);
		    		ouputStream.flush(); 
		    		ouputStream.close();
		    		forward = null;
		        }	        	
	        }
	    
	    } catch (EmptyReportBusinessException e) {
	        this.setSuccessFormSession(request, "error.emptyReport");
        } catch (Exception e) {
		    this.setErrorFormSession(request, "error.prepareViewReportForm", e);		    
	    }

	    return forward;	    
	}

	/**
	 * Check if the current path into report object is a relative or absolute path.
	 * if is relative, find the absolute path. Otherwise, do nothing.
	 */
	private void verifyFilePath(ReportTO rto){
	    String fileName = rto.getReportFileName();
	    
	    //find the absolute path using the relative reference... 
	    if (fileName.indexOf("#CLASS_PATH#")>=0) {
	        fileName = fileName.replaceAll("#CLASS_PATH#", "");
	        rto.setReportFileName(this.getServlet().getServletContext().getRealPath(fileName));
	    }
	}
	
	/**
	 * Generate a html of a specific report name, fields and button to call the report
	 */
	private StringBuffer formatReportHtml(ReportTO rto, HttpServletRequest request) throws BusinessException{
	    StringBuffer sb = new StringBuffer();
	    ReportDelegate rdel = new ReportDelegate();
	    String parameters = "";
	    
	    String buttonLabel = this.getResources(request).getMessage(request.getLocale(), "label.button.generate");
	    
	    //get the filter(s) field(s) of current report 
	    Vector filterList = rdel.getReportFields(rto.getSqlStement());
	    
	    sb.append("<input type=\"hidden\" name=\"PROJECT_ID_" + rto.getId() + "\" value=\"" + rto.getId() + "\"> \n");
	    sb.append("<table width=\"70%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> \n");
	    sb.append("<tr class=\"formBody\"> \n");
	    sb.append("<td class=\"formLabel\" width=\"3\" align=\"left\" valign=\"top\">&nbsp;</td> \n");
	    sb.append("<td colspan=\"3\" class=\"tableCell\" align=\"left\" valign=\"top\"><b>&nbsp;&nbsp;&nbsp;" + rto.getName() + "</b></td> \n");
	    sb.append("</tr>\n");
	    sb.append("</table>\n");	    
	    
	    //for each filter field, create a row in html format
	    if (filterList.size()>0 && this.someFieldIsVisible(filterList) ) {
	    	HashMap hm = new HashMap();
		    sb.append("<table width=\"70%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    Iterator i = filterList.iterator();
		    boolean firstRow = true;
		    while(i.hasNext()) {
		        ReportFieldTO filter = (ReportFieldTO)i.next();
		        if (hm.get(filter.getId())==null) {
		        	hm.put(filter.getId(), filter);
		        	
			        filter.setProject(rto.getProject());
			         
			        StringBuffer htmlContent = this.getFilterInHtmlFormat(rto, filter, request);
			        if (htmlContent!=null) {
				        sb.append("<tr class=\"formBody\">");
				        if (firstRow) {
				            sb.append("<td rowspan=\"" + (filterList.size()+1) + "\" class=\"formLabel\" width=\"3\" align=\"left\" valign=\"top\">&nbsp;</td>");
				            firstRow = false;
				        }
				        sb.append(htmlContent);
				        sb.append("</tr>");		            
			        }
			        
			        if (!parameters.trim().equals("")) {
			        	parameters = parameters + ","; 
			        }
			        parameters = parameters + "'" + filter.getFieldToHtml(rto) + "'";	
		        }
		    }
	        sb.append("<tr class=\"gapFormBody\"><td colspan=\"2\">&nbsp;</td></tr>");		    
		    sb.append("</table>");		    
	    }
	    	    
	    sb.append("<table width=\"70%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");	    
	    sb.append("<tr class=\"formBody\">");
	    sb.append("<td class=\"formLabel\" width=\"3\" align=\"left\" valign=\"top\">&nbsp;</td>");
	    sb.append("<td colspan=\"3\" class=\"tableCell\" align=\"left\" valign=\"top\">&nbsp;&nbsp;&nbsp;" +
	    		"<input type=\"button\" name=\"genReport\" value=\"" + buttonLabel + 
	    		"\" onclick=\"javascript:generate('" + rto.getId() + "', new Array(" + parameters + "));\" class=\"button\"></td>\n");
	    sb.append("</tr>");
	    sb.append("</table>");
	    sb.append("</br>");

	    return sb;
	}
	
	
	private boolean someFieldIsVisible(Vector filterList){
	    boolean response = false ;
	    if (filterList!=null) {
		    Iterator i = filterList.iterator();
		    while(i.hasNext()) {
		        ReportFieldTO filter = (ReportFieldTO)i.next();
		        if (filter.isVisible()) {
		            response = true;
		            break;
		        }
		    }	        
	    }
	    return response;
	}
	

	/**
	 * Return a filter field in html format with appopriate look'n'feel 
	 * based on the type of each filter.
	 */
	private StringBuffer getFilterInHtmlFormat(ReportTO rto, ReportFieldTO filter, HttpServletRequest request) throws BusinessException{
	    StringBuffer response = null;
	    
	    if (filter!=null) {
	        String value = filter.getLabel();
	        response = new StringBuffer();
	        if (filter.isVisible()){
	            
	            String key = filter.getFieldToHtml(rto);
	    	    response.append("<td align=\"right\" width=\"200\" class=\"tableCell\" valign=\"top\">" + filter.getId() + ":&nbsp;</td>");

	    	    StringBuffer htmlObject = new StringBuffer();
	    	    if (filter.getReportFieldType()!=null && filter.getReportFieldType().equals(ReportFieldTO.TYPE_TIMESTAMP)) {
	    	        htmlObject.append(this.getCalendarField(key, value, request));

	    	    } else if (value!=null && value.startsWith("!") && value.endsWith("!")) {
		    	    htmlObject.append(this.getSQLComboField(key, value, filter, request));

	    	    } else if (value!=null && value.indexOf("|")>0) {
	    	        htmlObject.append(HtmlUtil.getComboBox(key, value, null));
	    	        
	    	    } else {	    	        
	    	        htmlObject.append(HtmlUtil.getTextBox(key, value, 30, 30));
	    	    }
	    	    
	    	    response.append("<td>");
	    	    response.append(htmlObject);
	    	    response.append("</td>");
	        }
	    }
	    return response;
	}
	
	    
	/**
	 * Build a calendar object in html format
	 */
	private StringBuffer getCalendarField(String key, String value, HttpServletRequest request){
        Calendar cal = new Calendar();
        cal.setProperty(key);
        cal.setName("viewReportForm");
        cal.setStyleClass("textBox");
        String alt = this.getBundleMessage(request, "label.calendar.button");
        String calFormat = this.getBundleMessage(request, "calendar.format");    	        
        return new StringBuffer(cal.getCalendarHtml(alt, value, calFormat));	    
	}

	
	/**
	 * Build a SQL combo box in html format 
	 */
	private StringBuffer getSQLComboField(String key, String value, ReportFieldTO filter, HttpServletRequest request){
	    StringBuffer combo = new StringBuffer();
	    ReportDelegate rdel = new ReportDelegate();
	    
        try {

	        combo.append("<select id=\"" + key + "\" name=\"" + key + "\" class=\"textBox\">");
	        
	        UserTO uto = SessionUtil.getCurrentUser(request);
	        filter.setLabel(HtmlUtil.checkSQLKeyWord(filter.getLabel(), filter.getProject().getId(), uto.getId() ));

	        Vector list = rdel.performSQLByReportField(filter, uto);
	        if (list!=null) {
	            Iterator i = list.iterator();
	            while(i.hasNext()) {
	                ReportResultTO rrto = (ReportResultTO)i.next();
	                combo.append("<option value=\"" + rrto.getId() + "\">" + rrto.getValue() +"</option>");
	            }
	        }
	        
	        combo.append("</select>");
        } catch(Exception e){
            combo = new StringBuffer("<b>---- SQL Err! ----[<i>" + e.getMessage() + "</i>]</b>");
        }
        
        //TODO lembrar que para o formato SQLCombo, so e permitido inner filter de valores estaticos (PROJECT_ID, etc)...ajustar JUnits...
        
        return combo;
	}
	
}
