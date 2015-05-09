package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.BSCReportTO;
import com.pandora.CategoryTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.gadget.Gadget;
import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ViewBSCPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ViewBSCPanelAction extends GeneralStrutsAction {

	private HashMap statusHm = new HashMap();
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewBSCPanel";
	    try {
	    	ViewBSCPanelForm frm = (ViewBSCPanelForm)form;

		    //set default values for searching fields...
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    Timestamp now = DateUtil.getNow();
		    frm.setRefDate(DateUtil.getDate(now, super.getCalendarMask(request), loc));
	    	
	    	CategoryDelegate cdel = new CategoryDelegate();
	    	Vector categoryListFrmDB = cdel.getCategoryListByType(CategoryTO.TYPE_KPI, new ProjectTO(""), false);		    
	    	request.getSession().setAttribute("categoryList", categoryListFrmDB);
	    	
	    	CategoryTO cto = (CategoryTO)categoryListFrmDB.get(0);
	    	frm.setCategoryId(cto.getId());
	    	
	    	//read the option status...
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    PreferenceTO pref = uto.getPreference();
		    String onlyProj = pref.getPreference(PreferenceTO.KPI_SHOW_ONLY_CURPROJ);
		    if (onlyProj!=null) {
		    	frm.setShowOnlyCurrentProject(onlyProj.equals("on"));
		    } 
		    String onlyOpened = pref.getPreference(PreferenceTO.KPI_SHOW_ONLY_OPENED);
		    if (onlyOpened!=null) {
		    	frm.setShowOnlyOpenedKpi(onlyOpened.equals("on"));
		    } 
		    	    	
	    	this.refresh(frm, request);
	    	
	    } catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);		    
	    }
	    return mapping.findForward(forward);
	}

	
	public ActionForward showChart(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewBSCPanel";
	    try {
		    PreferenceDelegate pdel = new PreferenceDelegate();
		    ViewBSCPanelForm frm = (ViewBSCPanelForm)form;
		    String newChartId = frm.getKpiId();
		    
		    if (newChartId!=null && !newChartId.equals("")) {
			    Gadget gad = GadgetBUS.getGadgetClass("com.pandora.bus.gadget.KpiChartGadget");
			    
			    if (gad!=null) {
				    UserTO uto = SessionUtil.getCurrentUser(request);
				    PreferenceTO pref = uto.getPreference();
					PreferenceTO newPto = new PreferenceTO(gad.getId() + ".KPI_ID", newChartId, uto);
					pref.addPreferences(newPto);
					pdel.insertOrUpdate(pref);
			    }
		    }
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);		    
	    }
	
	    return mapping.findForward(forward);
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showViewBSCPanel";
	    
	    try {
		    ViewBSCPanelForm frm = (ViewBSCPanelForm)form;
		    this.refresh(frm, request);
		    
	    	//save the new option configuration...		    
		    this.saveOptions(request, frm);
		    
	    } catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareViewBSCForm", e);		    
	    }
	    return mapping.findForward(forward);
	}


	private void refresh(ViewBSCPanelForm frm, HttpServletRequest request) throws BusinessException {
		ReportDelegate rdel = new ReportDelegate();
	    StringBuffer sb = new StringBuffer("");
	    
	    Locale loc = SessionUtil.getCurrentLocale(request);
	    Timestamp refDate = DateUtil.getDateTime(frm.getRefDate(), super.getCalendarMask(request), loc);	    	
    	Vector list = rdel.getBSC(refDate, new ProjectTO(frm.getProjectId()), 
    			new CategoryTO(frm.getCategoryId()), !frm.getShowOnlyCurrentProject() );
    	String sessionOld = null;
    	boolean onlyOpened = frm.getShowOnlyOpenedKpi();
    	
    	if (list!=null) {
    		
    		//create a hash indexed by strategy id and grouping the status of each KPI
    		statusHm = this.getStatusByStrategy(list, loc, onlyOpened);
    		
	    	Iterator i = list.iterator();
	    	while(i.hasNext()) {
	    		BSCReportTO bsc = (BSCReportTO)i.next();

	    		if (!onlyOpened || (onlyOpened && bsc.getKpi().getFinalDate()==null) ) {
		    		String sessionId = bsc.getStrategyId() + "|" + bsc.getProjectId();
		    		if (sessionId==null) {
		    			sessionId = "";
		    		}
		    		
		    		if (sessionOld==null || !sessionOld.equals(sessionId)){
		    		    if (sessionOld!=null) {  
		    		        sb.append("\n</table>");    
		    		    }
		    		    sb.append(this.getTitle(bsc, request));
		    		    sessionOld = sessionId;
		    		}
		    		
		    		sb.append(getBody(bsc, request, loc));		    			
	    		}
	    	}
    	}
    	
    	if (!sb.toString().equals("")) {
	        sb.append("</table><br/>");	    	    
    	}
    	frm.setBscTable(sb.toString());

    	this.refreshGadgets(request, frm);	    		    	    
	}
	
	
	private void saveOptions(HttpServletRequest request, ViewBSCPanelForm frm) throws BusinessException {
	    PreferenceDelegate pdel = new PreferenceDelegate();
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    PreferenceTO pref = uto.getPreference();
		pref.addPreferences(new PreferenceTO(PreferenceTO.KPI_SHOW_ONLY_CURPROJ, frm.getShowOnlyCurrentProject()?"on":"off", uto));
		pref.addPreferences(new PreferenceTO(PreferenceTO.KPI_SHOW_ONLY_OPENED, frm.getShowOnlyOpenedKpi()?"on":"off", uto));
		pdel.insertOrUpdate(pref);		
	}


	private StringBuffer getBody(BSCReportTO bsc, HttpServletRequest request, Locale loc) throws BusinessException {
	    StringBuffer sb = new StringBuffer("");
	    UserDelegate udel = new UserDelegate();
	    
	    Integer dataType = bsc.getKpiDataType();
		if (dataType!=null) {
			
			Locale currencyLoc = udel.getCurrencyLocale();
			
			String mask = super.getCalendarMask(request);
			sb.append("<tr class=\"formBody\">");

			if (dataType.equals(ReportTO.FLOAT_DATA_TYPE) || dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
				String lineLbl = super.getBundleMessage(request, "label.viewBSCPanel.chart");
				String hint = HtmlUtil.getHint(lineLbl);
				sb.append("   <td class=\"tableCell\" align=\"center\" valign=\"center\">");
				sb.append("     <a href=\"javascript:showChart(" + bsc.getKpi().getId() + ")\"><img border=\"0\" " + hint + " src=\"../images/linechart.gif\" ></a>");
				sb.append("   </td>\n");			    
			} else {
				sb.append("   <td class=\"tableCell\" align=\"center\" valign=\"center\">");
				sb.append("     &nbsp;");
				sb.append("   </td>\n");			    
			}

			sb.append("   <td class=\"tableCell\" align=\"center\" valign=\"center\">");
			String perspLbl = super.getBundleMessage(request, "title.viewBSCPanel.persp." + bsc.getKpi().getReportPerspectiveId());
			sb.append(perspLbl);
			sb.append("   </td>\n");

			sb.append("   <td class=\"tableCell\" align=\"right\" valign=\"center\">");
			sb.append(bsc.getKpi().getName());
			sb.append("   &nbsp;</td>\n");

			sb.append("   <td class=\"tableCell\" align=\"center\" valign=\"center\">");
			int status = this.getStatus(bsc, loc);
			sb.append(this.getStatusIcon(status, request));
			sb.append("   </td>\n");

			sb.append("   <td class=\"tableCell\" align=\"center\" valign=\"center\">");
			sb.append(DateUtil.getDate(bsc.getResult().getLastExecution(), mask, loc));
			sb.append("   </td>\n");

			sb.append(bsc.getResult().convertToHtml(loc, mask, dataType, currencyLoc));

			sb.append("   <td class=\"tableCell\"  align=\"center\" valign=\"center\">");
	       	sb.append(this.getGoal(bsc, request, loc, currencyLoc));
			sb.append("   </td>\n");

			sb.append("   <td class=\"tableCell\"  align=\"center\" valign=\"center\">");
			sb.append(getTolerance(bsc, loc, request));
			sb.append("   </td>\n");

			sb.append("   <td class=\"tableCell\"  align=\"center\" valign=\"center\">");
			if (bsc.getKpiWeight()!=null) {
				sb.append(bsc.getKpiWeight());	
			} else {
				sb.append("&nbsp;");
			}
			sb.append("   </td>\n");

			String rowspan= "";
			Vector statusList = (Vector)statusHm.get(bsc.getStrategyId());
			if (statusList!=null) {
				rowspan = " rowspan=\""+ statusList.size() + "\" ";
			}
			sb.append("   <td class=\"tableCell\" align=\"center\"" + rowspan + " valign=\"center\">");

			if (statusList!=null) {
				int strategyStatus = this.getStrategyStatus(statusList);
				sb.append(this.getStatusIcon(strategyStatus, request));			
				statusHm.remove(bsc.getStrategyId());
			}
			sb.append("   </td>\n");

			sb.append("</tr>\n");			
		}
	    
	    return sb;
	}
	
	
	private StringBuffer getTitle(BSCReportTO bsc, HttpServletRequest request) {
	    StringBuffer sb = new StringBuffer("");

	    sb.append("\n&nbsp;<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
	    sb.append("<tr class=\"tableRowEven\" height=\"20\">");

		sb.append("   <td class=\"tableCell\" align=\"left\" valign=\"center\"><b>&nbsp;&nbsp;");
		if (bsc.getStrategyName()==null) {
		    sb.append(super.getBundleMessage(request, "label.viewBSCPanel.grid.other"));		    
		} else {
		    sb.append(bsc.getStrategyName());    
		}
		
		sb.append("   </b></td>\n");

		sb.append("   <td class=\"tableCell\" align=\"right\" valign=\"center\" width=\"300\"><b>");
		sb.append(bsc.getProjectName());
		sb.append("   &nbsp;&nbsp;</b></td>\n");

		sb.append("</tr>");
		sb.append("</table>\n");


		String kpiLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.name");
		String lastExecLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.lastExec");
		String valueLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.value");
		String statLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.stat");
		String goalLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.goal");
		String toleranceLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.tolerance");
		String perspecLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.persp");
		String weigLbl = "-";
		String totLbl = "-";
		if (bsc.getStrategyName()!=null) {
			weigLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.weig");
			totLbl = super.getBundleMessage(request, "label.viewBSCPanel.grid.total");
		}

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");		
		sb.append("<tr class=\"tableRowHeader\">");
		sb.append("   <th class=\"tableCellHeader\" width=\"16\" align=\"center\">&nbsp;</th>\n");		
		sb.append("   <th class=\"tableCellHeader\" width=\"120\" align=\"center\">" + perspecLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" align=\"center\">" + kpiLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"25\" align=\"center\">" + statLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"80\" align=\"center\">" + lastExecLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"90\" align=\"center\">" + valueLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"100\" align=\"center\">" + goalLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"110\" align=\"center\">" + toleranceLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"50\" align=\"center\">" + weigLbl + "</th>\n");
		sb.append("   <th class=\"tableCellHeader\" width=\"50\" align=\"center\">" + totLbl + "</th>\n");
		sb.append("</tr>\n");
		
	    return sb;
	}
	
	
	private String getTolerance(BSCReportTO bsc, Locale loc, HttpServletRequest request){
	    String response = "&nbsp;";
	    ReportAction action = new ReportAction();
	    
	    if (bsc.getKpiTolerance()!=null) {

		    String type = bsc.getKpiToleranceType();
		    Integer dataType = bsc.getKpiDataType();

			if (type.equals("1") || type.equals("4")) {
			    response = "+/-";
			}

			String val = action.formatFloatValue(bsc.getKpiTolerance(), ReportTO.KPI_DEFAULT_LOCALE);
		    response = response + "&nbsp;" + val + "&nbsp;";
		    
			if (type.equals("1") || type.equals("2") || type.equals("3")) {
			    String unitLbl = "";
			    if (dataType.equals(ReportTO.FLOAT_DATA_TYPE) || dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
			    	unitLbl = super.getBundleMessage(request, "label.manageReport.tolerance.unit.1");	
			    } else if (dataType.equals(ReportTO.DATE_DATA_TYPE)) {
			    	unitLbl = super.getBundleMessage(request, "label.manageReport.tolerance.days");	
			    }
			    response = response + unitLbl;
			} else {
			    response = response + "%";
			}
			
			if (type.equals("2") ||	type.equals("5")) {
				String moreLbl = super.getBundleMessage(request, "label.manageReport.tolerance.more");
				response = response + " " + moreLbl;
			} else if (type.equals("3") ||	type.equals("6")) {
				String lessLbl = super.getBundleMessage(request, "label.manageReport.tolerance.less");
				response = response + " " + lessLbl;
			}
			
	    }

	    return response;
	}

	private int getStatus(BSCReportTO bsc, Locale loc){
	    int response = 0;
	    ReportDelegate rdel = new ReportDelegate();
	    Integer dataType = bsc.getKpiDataType();
	    
	    float floatValue = 0;
	    Timestamp dtValue = null;
	    float floatGoal = 0;
	    Timestamp dtGoal = null;
	    float tolerance = 0;
            
        String type = bsc.getKpiToleranceType();
        
        if (bsc.getResult()!=null && bsc.getResult().getValue()!=null){
            String val = bsc.getResult().getValue();
            if (dataType.equals(ReportTO.FLOAT_DATA_TYPE) || dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
            	floatValue = StringUtil.getStringToFloat(val, ReportTO.KPI_DEFAULT_LOCALE);	
            } else if (dataType.equals(ReportTO.DATE_DATA_TYPE)) {
            	dtValue = DateUtil.getDateTime(val, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE);
            }
        }
        
        if (bsc.getKpiGoal()!=null){
        	String val = bsc.getKpiGoal();
            if (dataType.equals(ReportTO.FLOAT_DATA_TYPE)|| dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
            	floatGoal = StringUtil.getStringToFloat(val, ReportTO.KPI_DEFAULT_LOCALE);
            } else if (dataType.equals(ReportTO.DATE_DATA_TYPE)) {
            	dtGoal = DateUtil.getDateTime(val, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE);
            }
        }

        if (bsc.getKpiTolerance()!=null){
            tolerance = StringUtil.getStringToFloat(bsc.getKpiTolerance(), ReportTO.KPI_DEFAULT_LOCALE);    
        }          
        
        if (dataType.equals(ReportTO.FLOAT_DATA_TYPE)|| dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
        	response = rdel.getKpiStatus(floatValue, floatGoal, tolerance, type);	
        } else if (dataType.equals(ReportTO.DATE_DATA_TYPE)) {
        	response = rdel.getKpiStatus(dtValue, dtGoal, tolerance, type);
        }
                        
	    
	    return response;
	}
	
	
	private HashMap getStatusByStrategy(Vector list, Locale loc, boolean showOnlyOpenedKpi){
		HashMap response = new HashMap();
		
    	Iterator j = list.iterator();
    	while(j.hasNext()) {
    		BSCReportTO bsc = (BSCReportTO)j.next();
    		
    		if (!showOnlyOpenedKpi || (showOnlyOpenedKpi && bsc.getKpi().getFinalDate()==null) ) {
    			
        		int status = this.getStatus(bsc, loc);

        		Vector statusList = (Vector)response.get(bsc.getStrategyId());
        		if (statusList==null) {
        			statusList = new Vector();
        			response.put(bsc.getStrategyId(), statusList);
        		}
        		
        		String strWeight = "0"; 
        		if (bsc.getKpiWeight()!=null) {
        			strWeight = bsc.getKpiWeight().toString();
        		}
        		
        		TransferObject to = new TransferObject(status+"", strWeight);
        		statusList.addElement(to);    			
    		}
    	}
		
    	return response;
	}
	
	
	private int getStrategyStatus(Vector statusList){
		int response = 0;
		
		if (statusList!=null) {
			
			int sumWeight = 0;
			int total = 0;
			
			for (int i=0; i<statusList.size(); i++) {
				TransferObject to = (TransferObject)statusList.get(i);
				
				int status = Integer.parseInt(to.getId());
				int weight = Integer.parseInt(to.getGenericTag());
				
				if (status>0 && weight>0) {
					sumWeight += weight;
					total += (status * weight);
				}

			}
				
			if (sumWeight>0) {
				response = total / sumWeight;	
			}
		}
		
		return response;
	}
	
	
	private String getStatusIcon(int status, HttpServletRequest request){
		String response = "&nbsp;";
		if (status>0) {
			String statLbl = super.getBundleMessage(request, "label.viewBSCPanel.status." + status);
			String hint = HtmlUtil.getHint(statLbl);
			response = "<img border=\"0\" " + hint + " src=\"../images/smile_" + status + ".gif\" >";	
		}
		return response;
	}


	private String getGoal(BSCReportTO bsc, HttpServletRequest request, Locale loc, Locale currencyLoc){
		ReportAction action = new ReportAction();		
		String response = "";
		Integer dataType = bsc.getKpiDataType();
		
		if (bsc.getKpiGoal()!=null) {
		
			String type = bsc.getKpiToleranceType();
		    boolean isMax = type.equals("2") ||	type.equals("5");
		    boolean isMin = type.equals("3") ||	type.equals("6");	    
			
		    if (isMin) {
		    	response = ">= ";
		    } else if (isMax) {
		    	response = "<= ";
		    }
			 			
	    	String val = bsc.getKpiGoal();
	        if (val!=null && dataType.equals(ReportTO.DATE_DATA_TYPE)) {
	            Timestamp ts = DateUtil.getDateTime(val, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE); 
	            response = response + DateUtil.getDate(ts, super.getCalendarMask(request), loc);
	            
	        } else if (val!=null && dataType.equals(ReportTO.FLOAT_DATA_TYPE)) {
	        	response = response + action.formatFloatValue(val, ReportTO.KPI_DEFAULT_LOCALE);
	        
	        } else if (val!=null && dataType.equals(ReportTO.CURRENCY_DATA_TYPE)) {
	            response = response + action.formatCurrencyValue(val, ReportTO.KPI_DEFAULT_LOCALE, currencyLoc);
	            
	        } else {
	        	response = "&nbsp;";
	        }
		}
		
		return response;
	}
	
	private void refreshGadgets(HttpServletRequest request, ViewBSCPanelForm frm) throws BusinessException {
	    StringBuffer content =  new StringBuffer("");
	    ResourceHomeAction resHome = new ResourceHomeAction();
	    
		String loadingLabel = super.getBundleMessage(request, "label.manageOption.gadget.loading");
		Gadget gad = GadgetBUS.getGadgetClass("com.pandora.bus.gadget.KpiChartGadget");

		int gadWidth = 485;
		content.append(gad.gadgetToHtml(request, gadWidth, 115, loadingLabel));
		frm.setGadgetHtmlBody("<td width=\"" + gadWidth + "\" valign=\"top\" align=\"center\">" + content.toString() + "</td>");
		
		if (gad.getFieldsId()!=null && gad.getFieldsId().size()>0) {
			String prop = resHome.getGadgetPropertyHtml(gad, request, "goToBscForm");	
			frm.setGadgetPropertyBody(prop);			
		} else {
		    frm.setGadgetPropertyBody("");    
		}
	}

}
