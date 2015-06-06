package com.pandora.gui.struts.action;

import java.sql.Timestamp;
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
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DuplicatedKpiTypeException;
import com.pandora.gui.struts.form.CustReqForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.ReportForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the actions performed into Report form
 */
public class ReportAction extends GeneralStrutsAction {
    
    /**
     * Show the manage Report form (KPI form)
     */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
	    this.clearForm(form, request);	  
	    ReportForm frm = (ReportForm)form;
	    
	    UserTO uto = SessionUtil.getCurrentUser(request);	    
	    String hideClosed = uto.getPreference().getPreference(PreferenceTO.REPORT_HIDE_CLOSED);
	    frm.setHideClosedReport(hideClosed!=null && hideClosed.equals("on"));
	    
	    this.refresh(mapping, form, request, response);
	    return mapping.findForward(forward);
	}
	
	
	/**
	 * Get CategoryTO object from database and set data into struts form. 
	 */
	public ActionForward editReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
	    
	    try {
	        ReportForm rfrm = (ReportForm)form;
	        ReportDelegate rdel = new ReportDelegate();

			//clear messages of form
			this.clearMessages(request);
	        
			//set current operation status for Updating	
			rfrm.setSaveMethod(ReportForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
	        
			//create a report object used such a filter
			ReportTO filter = new ReportTO();
			filter.setId(rfrm.getId());
			
			//get a specific report from data base
			ReportTO rto = rdel.getReport(filter);
			
			//put the data (from DB) into html fields
			this.getActionFormFromTransferObject(rto, rfrm, request);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareEditReportForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.prepareEditReportForm", e);		    
	    }

	    return mapping.findForward(forward);
	}


    /**
     * Close ("remove") a report into data base
	 */
	public ActionForward closeReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
		try {
			ReportForm rfrm = (ReportForm)form;
			ReportDelegate rdel = new ReportDelegate();
				
			//create an ReportTO object based on html fields
			ReportTO rto = new ReportTO();
			rto.setId(rfrm.getId());
			
			//close a report into data base
			rdel.removeReport(rto);
			
			//clear form and messages
			this.clearMessages(request);
			this.clearForm(rfrm, request);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeReport");
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
		
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.removeReportForm", e);
		}
		return mapping.findForward(forward);		
	}

	
	/**
	 * Insert or Update data of report object into database.
	 */
	public ActionForward saveReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
		String errorMsg = "error.showReportForm";
		String succeMsg = "message.success";
		
		try {
			ReportForm rfrm = (ReportForm)form;
			ReportDelegate rdel = new ReportDelegate();

			//create an ReportTO object based on html fields
			ReportTO rto = this.getTransferObjectFromActionForm(rfrm, request);
					
			if (rfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){			    
			    errorMsg = "error.updateReportForm";
			    succeMsg = "message.insertReport";
			    rdel.insertReport(rto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.updateReportForm";
			    succeMsg = "message.updateReport";
			    rdel.updateReport(rto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
		
		} catch(DuplicatedKpiTypeException e){
			this.setErrorFormSession(request, "error.duplicatedkpi", e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		
	}

	
    /**
	 * Refresh the report list on bellow and refresh the list of projects related with current user
	 */
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
	    PreferenceDelegate pfdel = new PreferenceDelegate();
	    
		try {
		    ReportForm repFrm = (ReportForm)form;
		    
			//get all Projects from data base and put into http session (to be displayed by combo)
			ProjectDelegate pdel = new ProjectDelegate();
			Vector<ProjectTO> prjList = pdel.getProjectList();
			
			Vector<ProjectTO> allProjects = new Vector<ProjectTO>();
			if (!repFrm.getBoolIsKpiForm()) {
				ProjectTO allproject = new ProjectTO("0");
				allproject.setName(this.getBundleMessage(request, "label.all"));
				allProjects.addElement(allproject);
			} else {
				ProjectTO bellowproject = new ProjectTO("-1");
				bellowproject.setName(this.getBundleMessage(request, "label.manageReport.project.multiple"));
				allProjects.addElement(bellowproject);			    					
			}
			
			allProjects.addAll(prjList);
			
			request.getSession().setAttribute("projectList", allProjects);

		    //save the new preference
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    PreferenceTO pto = new PreferenceTO(PreferenceTO.REPORT_HIDE_CLOSED, repFrm.getHideClosedReport()?"on":"off", uto);
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
			pfdel.insertOrUpdate(pto);
			
			//get all Reports from data base and put into http session (to be displayed by combo)			
			ReportDelegate rdel = new ReportDelegate();
			Vector<ReportTO> repList = rdel.getListBySource(repFrm.getBoolIsKpiForm(), null, null, !repFrm.getHideClosedReport()); //Note: show reports of all categories 
		    request.getSession().setAttribute("reportList", repList);
		    
		    //create a list of all perspective ids of KPIs (to be displayed by combo)
		    Vector<TransferObject> perList = new Vector<TransferObject>();		    
		    if (repFrm.getBoolIsKpiForm()) {
			    for (int i= 1; i<=4; i++){
			        TransferObject to = new TransferObject();
			        to.setId(i+"");
			        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.persp." + i));
			        perList.addElement(to);
			    }		        
		    }
		    request.getSession().setAttribute("perspectiveList", perList);


		    //create a list of all tolerance units ids of KPIs (to be displayed by combo)
		    Vector<TransferObject> toleranceUnitList = new Vector<TransferObject>();		    
		    for (int i= 1; i<=2; i++){
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.tolerance.unit." + i));
		        toleranceUnitList.addElement(to);
		    }		        
		    request.getSession().setAttribute("toleranceUnitList", toleranceUnitList);
		    
		    Vector<TransferObject> toleranceScaleList = new Vector<TransferObject>();		    
		    for (int i= 1; i<=3; i++){
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.tolerance.scale." + i));
		        toleranceScaleList.addElement(to);
		    }		        
		    request.getSession().setAttribute("toleranceScaleList", toleranceScaleList);
		    
		    //create a list of all available hours (to be displayed by combo)
		    Vector<TransferObject> hourList = new Vector<TransferObject>();
		    if (repFrm.getBoolIsKpiForm()) {		    
			    for (int i= 0; i<=23; i++){
			        TransferObject to = new TransferObject();
			        to.setId(i+"");
			        to.setGenericTag(i + ":00h");
			        hourList.addElement(to);
			    }
		    }
		    request.getSession().setAttribute("hoursList", hourList);

		    //create a list of all allowed data types (to be displayed by combo)
		    Vector<TransferObject> dataTypeList = new Vector<TransferObject>();	    
		    for (int i= 0; i<=3; i++){
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.dataType." + i));
		        dataTypeList.addElement(to);
		    }
		    request.getSession().setAttribute("dataTypeList", dataTypeList);

		    Vector<TransferObject> profileList = new Vector<TransferObject>();	    
		    for (int i=0; i<=4; i++){
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.roles." + i));
		        profileList.addElement(to);
		    }
		    request.getSession().setAttribute("profileList", profileList);

		    Vector<TransferObject> kpiTypeList = new Vector<TransferObject>();	    
		    for (int i=0; i<=5; i++){
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.manageReport.kpiType." + i));
		        kpiTypeList.addElement(to);
		    }
		    request.getSession().setAttribute("kpiTypeList", kpiTypeList);		    
		    
			//get all Categories from data base and put into http session (to be displayed by combo)
		    this.refreshCategory(mapping, form, request, response);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReportForm", e);
		}
	    
	    return mapping.findForward(forward);
	}
	
	
	public ActionForward refreshCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws BusinessException {
	    String forward = "showReport";
	    
		try {		
		    CategoryDelegate cdel = new CategoryDelegate();
			Integer categoryType = null;
			ReportForm repFrm = (ReportForm)form;
			
			if (repFrm.getBoolIsKpiForm()) {
			    categoryType = CategoryTO.TYPE_KPI; 
			} else {
			    categoryType = CategoryTO.TYPE_REPORT;
			}
			Vector<CategoryTO> categoryListFrmDB = cdel.getCategoryListByType(categoryType, new ProjectTO(repFrm.getProjectId()), false);		    
			request.getSession().setAttribute("categoryList", categoryListFrmDB);
			
			if (repFrm.getProjectId()!=null && !repFrm.getProjectId().equals("-1")) {
				repFrm.setAppliedProjectList(repFrm.getProjectId());
			}
			
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReportForm", e);
		}		
		return mapping.findForward(forward);
	}
	
	
	/**
	 * Clear form after html reset button 
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showReport";
	    this.clearForm(form, request);
		this.clearMessages(request);
		return mapping.findForward(forward);		
	}
	
	
	/**
	 * Clear all values of current form.
	 * @param usrfrm
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    ReportForm repfrm = (ReportForm)form;
	    repfrm.clear();
		
		//set current operation status for Insertion
	    repfrm.setSaveMethod(CustReqForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	}

	
	/**
	 * Set values from TrasnferObject to html (struts form)
     */
    private void getActionFormFromTransferObject(ReportTO rto, ReportForm rfrm, HttpServletRequest request) {
        Locale loc = SessionUtil.getCurrentLocale(request);
        rfrm.setExecutionHour(rto.getExecutionHour()+"");
        rfrm.setId(rto.getId());
        rfrm.setDescription(rto.getDescription());
        rfrm.setProjectId(rto.getProject().getId());
        rfrm.setReportPerspectiveId(rto.getReportPerspectiveId());
        rfrm.setSqlStement(rto.getSqlStement());
        rfrm.setType(rto.getType()+"");
        
        rfrm.setKpiType(null);	
        if (rto.getKpiType()!=null) {
        	rfrm.setKpiType(rto.getKpiType()+"");	
        }
        
		rfrm.setName(rto.getName());
		rfrm.setReportFileName(rto.getReportFileName());
		rfrm.setDataType(rto.getDataType()+"");
		rfrm.setProfile(rto.getProfile());
		if (rto.getLastExecution()!=null){
		    rfrm.setLastExecution(DateUtil.getDate(rto.getLastExecution(), super.getCalendarMask(request), loc));    
		} else {
		    rfrm.setLastExecution("");
		}
		if (rto.getCategory()!=null) {
		    rfrm.setCategoryId(rto.getCategory().getId());
		} else {
		    rfrm.setCategoryId("-1");   
		}
		
		String appliedProjList = "";
		if (rto.getAppliedProjectList()!=null) {
			if (rfrm.getBoolIsKpiForm() && rto.getProject().getId().equals("0")) {
				rfrm.setProjectId("-1");	
			}
			for (ProjectTO p : rto.getAppliedProjectList()) {
				if (!appliedProjList.equals("")) {
					appliedProjList = appliedProjList + "; ";	
				}
				appliedProjList = appliedProjList + p.getId();
			}
		}
		rfrm.setAppliedProjectList(appliedProjList);
		
		rfrm.setGoal(rto.getGoal());
		rfrm.setTolerance(rto.getTolerance());
		if (rto.getToleranceType()!=null) {
			if (rto.getToleranceType().equals("1") || rto.getToleranceType().equals("2") ||	rto.getToleranceType().equals("3")) {
				rfrm.setToleranceUnit("1");
			} else {
				rfrm.setToleranceUnit("2");
			}
			
			if (rto.getToleranceType().equals("1") || rto.getToleranceType().equals("4")) { 
				rfrm.setToleranceScale("1");
			} else if (rto.getToleranceType().equals("2") || rto.getToleranceType().equals("5")) {
				rfrm.setToleranceScale("2");
			} else {
				rfrm.setToleranceScale("3");
			}
		} else {
			rfrm.setToleranceUnit("1");
			rfrm.setToleranceScale("1");
		}
    }

    
	/**
	 * Create a TransferObject based on values from struts form (html form)
     */
    private ReportTO getTransferObjectFromActionForm(ReportForm rfrm, HttpServletRequest request) {
        Locale loc = SessionUtil.getCurrentLocale(request);
        
        ReportTO response = new ReportTO(rfrm.getId());
        if (rfrm.getExecutionHour()!=null && !rfrm.getExecutionHour().equals("")) {
            response.setExecutionHour(new Integer(rfrm.getExecutionHour()));    
        } else {
            response.setExecutionHour(new Integer("0"));
        }
        if (rfrm.getLastExecution()!=null && !rfrm.getLastExecution().equals("")) {
            response.setLastExecution(DateUtil.getDateTime(rfrm.getLastExecution(), super.getCalendarMask(request), loc));    
        }
        response.setDescription(rfrm.getDescription());
        response.setName(rfrm.getName());
        response.setReportFileName(rfrm.getReportFileName());
        response.setProject(new ProjectTO(rfrm.getProjectId()));
        response.setReportPerspectiveId(rfrm.getReportPerspectiveId());
        response.setSqlStement(rfrm.getSqlStement());
        if (rfrm.getType()!=null && !rfrm.getType().equals("")) {
            response.setType(new Integer(rfrm.getType()));    
        } else {
            response.setType(new Integer("0"));
        }
        
        response.setKpiType(null);
        if (rfrm.getKpiType()!=null && !rfrm.getKpiType().equals("")) {
            response.setKpiType(new Integer(rfrm.getKpiType()));    
        }
        
        if (rfrm.getDataType()!=null && !rfrm.getDataType().equals("")) {
            response.setDataType(new Integer(rfrm.getDataType()));    
        } else {
            response.setDataType(new Integer("0"));
        }
        if (!rfrm.getCategoryId().equals("0")) {
            response.setCategory(new CategoryTO(rfrm.getCategoryId()));    
        } else {
            response.setCategory(null);
        }
        response.setHandler(SessionUtil.getCurrentUser(request));
        response.setProfile(rfrm.getProfile());


        if (response.getDataType().equals(new Integer("1"))) {
            response.setGoal(formatDateValue(request, rfrm.getGoal(), loc));
        } else { 
            //..including currency...
            response.setGoal(formatFloatValue(rfrm.getGoal(), loc));
        }
        
        response.setTolerance(formatFloatValue(rfrm.getTolerance(), loc));
        
		if (rfrm.getToleranceUnit().equals("1")) {
			response.setToleranceType(rfrm.getToleranceScale());
		} else {
			response.setToleranceType((Integer.parseInt(rfrm.getToleranceScale())+3)+"");
		}
        
		response.clearAppliedProjectList();
		if (rfrm.getBoolIsKpiForm() && rfrm.getAppliedProjectList()!=null && !rfrm.getAppliedProjectList().trim().equals("")) {
			if (rfrm.getProjectId().equals("-1")) {
				response.setProject(new ProjectTO("0"));		
			}
			String[] list = rfrm.getAppliedProjectList().split(";");
			for(int i=0; i<list.length; i++) {
				if (list[i]!=null && !list[i].trim().equals("")) {
					response.addAppliedProjectList(new ProjectTO(list[i].trim()));	
				}
			}				
		} else {
			response.addAppliedProjectList(new ProjectTO(rfrm.getProjectId()));
		}
			
        return response;
    }

    public String formatFloatValue(String val, Locale loc) {
        String response = "";
        try {
            float f = StringUtil.getStringToFloat(val, loc);
            if (f>=0) {
            	response = StringUtil.getFloatToString(f, ReportTO.KPI_DEFAULT_LOCALE);	
            }
        } catch(Exception e) {
            response = "";            
        }
        return response;
    }

    public String formatCurrencyValue(String val, Locale loc, Locale currencyLoc) {
        String response = "";
        try {
            float f = StringUtil.getStringToFloat(val, loc);
            if (f>=0) {
            	response = StringUtil.getCurrencyValue(f, currencyLoc);	
            }
        } catch(Exception e) {
            response = "";            
        }
        return response;
    }
    
    private String formatDateValue(HttpServletRequest request, String val, Locale loc) {
        String response = "";
        try {
            Timestamp ts = DateUtil.getDateTime(val, super.getCalendarMask(request), loc); 
            response = DateUtil.getDate(ts, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE);
        } catch(Exception e) {
            response = "";            
        }
        return response;
    }
    
}
