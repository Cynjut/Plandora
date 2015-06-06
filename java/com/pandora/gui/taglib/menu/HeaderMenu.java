package com.pandora.gui.taglib.menu;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;
import org.apache.taglibs.display.LookupUtil;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.LogUtil;

public class HeaderMenu extends TagSupport {

	private static final long serialVersionUID = 1L;

    /** Related form name */
    private String name;

    
    public int doStartTag() {
    	ProjectDelegate pdel = new ProjectDelegate(); 
    	UserDelegate udel = new UserDelegate();
    	
        StringBuffer buff = new StringBuffer("");
        try {
        	boolean showMenu = false;
        	
            buff.append("<td colspan=\"6\" class=\"headerShape\">");
        	
            JspWriter out = pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            if (request!=null) {
            	String servletPath = request.getServletPath();
            	if (servletPath!=null) {
            		String[] page = this.getMenuOptions(servletPath);
            		if (page!=null) {

                        Object projectId = LookupUtil.lookup(pageContext, page[1], page[2], null, false, null, null, null);
                        if (projectId!=null) {

                            ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId+""), true);
                            if (pto!=null) {
                            	
                                UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
                                LeaderTO eto = new LeaderTO(uto);
                                eto.setProject(pto);
                                eto = udel.getLeader(eto);
                                ResourceTO rto = new ResourceTO(uto.getId());
                            	
                            	String allTaskLbl = bundle("label.menuTag.task");
                            	String allReqsLbl = bundle("label.menuTag.reqs");
                            	String allRiskLbl = bundle("label.menuTag.risks");
                            	String allOccuLbl = bundle("label.menuTag.occor");
                            	String allSurvLbl = bundle("label.menuTag.surv");
                            	String allRepoLbl = bundle("label.menuTag.rep");
                            	String allKbasLbl = bundle("label.menuTag.kb");
                            	String allBscLbl = bundle("label.menuTag.bsc");
                            	String allInvoLbl = bundle("label.menuTag.invo");
                            	String allReposLbl = bundle("label.menuTag.repo");
                            	String allAgilLbl = bundle("label.menuTag.Agile");
                            	String allGanttLbl = bundle("label.menuTag.gantt");
                            	String allImpLbl = bundle("label.menuTag.imp");
                            	String allCapLbl = bundle("label.menuTag.cap");
                            	String allCostLbl = bundle("label.menuTag.cost");

                                buff.append("<div id=\"slideTopBar\" style=\"left:0px; height:4px; width:100%;\" onMouseover=\"showTopMenu();\" onMouseout=\"hideTopMenu();\">");
                	            buff.append("<div id=\"slideContent\" class=\"headerMenuContent\">");
                	            
            		            buff.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            		            buff.append("<b>" + pto.getName() + "</b>\n");

            		            if (eto!=null) {
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showAllTask?operation=prepareForm&projectRelated=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allTaskLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showAllRequirement?operation=prepareForm&projectRelated=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allReqsLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/manageSurvey?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allSurvLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/viewBSCPanel?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allBscLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showResCapacityPanel?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allCapLbl + "</a>\n");                		            
                		            rto = (ResourceTO)eto;
                                } else {
                                    rto.setProject(pto);
                                    rto = udel.getResource(rto);
                                }

            		            if (rto!=null) {
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/manageRisk?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allRiskLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/manageOccurrence?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allOccuLbl + "</a>\n");            		            	
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/ganttPanel?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allGanttLbl + "</a>\n");            		            	
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/viewReport?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allRepoLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/viewKb?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allKbasLbl + "</a>\n");
                		            buff.append("&nbsp;-&nbsp;<a href=\"../do/projectImportExport?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allImpLbl + "</a>\n");
            		            }
            		            
            		            if (rto!=null) {
                                	if (eto!=null || rto.getBoolCanSeeInvoice()) {
                    		            buff.append("&nbsp;-&nbsp;<a href=\"../do/manageInvoice?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allInvoLbl + "</a>\n");
                    		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showCostPanel?operation=prepareForm&type=PRJ&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allCostLbl + "</a>\n");
                                	}

                                	if (eto!=null || rto.getBoolCanSeeRepository()) {
                    		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showRepositoryViewer?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allReposLbl + "</a>\n");                                		
                                	}
                                	
                                	if (eto!=null || rto.getBoolCanSelfAlloc()) {
                    		            buff.append("&nbsp;-&nbsp;<a href=\"../do/showAgilePanel?operation=prepareForm&projectId=" + projectId + "\" class=\"headerMenuContent\" target=\"\">" + allAgilLbl + "</a>\n");                                		
                                	}            		            	
            		            }

                	            buff.append("</div>\n");
                	            buff.append("</div>\n");                	            

                	            buff.append("<script language=\"JavaScript\">\n");
                	            buff.append("  var ie = document.all;\n");
                	            buff.append("  var mz = document.getElementById && !document.all ? 1: 0;\n");
                	            buff.append("  if (ie || mz){ mn = (mz)? document.getElementById(\"slideTopBar\").style : document.all.slideTopBar.style; mnc = (mz)? document.getElementById(\"slideContent\").style : document.all.slideContent.style; mn.height = 4; mnc.display = 'none';}\n");
                	            buff.append("</script>\n");
                	            
                	            showMenu = true;                                	
                            }
                        }
            		}
            	}
            }
            
            if (!showMenu) {
            	buff.append("&nbsp;");
            }
            buff.append("</td>");             
            
            out.println(buff.toString());

        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Header menu tag lib error", e);
        }
            
        return SKIP_BODY;    	
    }
 
    
    private String[] getMenuOptions(String servletPath){
    	String[] page = null;
    	ArrayList<String[]> pages = new ArrayList<String[]>();
    	pages.add(new String[]{"/jsp/manageProject.jsp", "projectForm", "id"});
    	pages.add(new String[]{"/jsp/manageRisk.jsp", "riskForm", "projectId"});
    	pages.add(new String[]{"/jsp/manageOccurrence.jsp", "occurrenceForm", "projectId"});
    	pages.add(new String[]{"/jsp/manageSurvey.jsp", "surveyForm", "projectId"});
    	pages.add(new String[]{"/jsp/viewKb.jsp", "kbForm", "projectId"});
    	pages.add(new String[]{"/jsp/showAllTask.jsp", "showAllTaskForm", "projectRelated"});
    	pages.add(new String[]{"/jsp/showAllRequirement.jsp", "showAllRequirementForm", "projectRelated"});
    	pages.add(new String[]{"/jsp/viewReport.jsp", "viewReportForm", "projectId"});
    	pages.add(new String[]{"/jsp/viewBSCPanel.jsp", "viewBSCPanelForm", "projectId"});
    	pages.add(new String[]{"/jsp/manageTask.jsp", "taskForm", "projectId"});
    	pages.add(new String[]{"/jsp/repositoryViewer.jsp", "repositoryViewerForm", "projectId"});
    	pages.add(new String[]{"/jsp/viewKb.jsp", "kbForm", "projectId"});
    	pages.add(new String[]{"/jsp/agilePanel.jsp", "agilePanelForm", "projectId"});
    	pages.add(new String[]{"/jsp/manageInvoice.jsp", "invoiceForm", "projectId"});
    	pages.add(new String[]{"/jsp/costPanel.jsp", "costForm", "projectId"});
    	pages.add(new String[]{"/jsp/projectImportExport.jsp", "projectImportExportForm", "projectId"});
    	pages.add(new String[]{"/jsp/resCapacityPanel.jsp", "resCapacityPanelForm", "projectId"});
    	pages.add(new String[]{"/jsp/ganttPanel.jsp", "ganttPanelForm", "projectId"});
    	
    	for (int i=0; i<pages.size(); i++) {
    		page = (String[])pages.get(i);
    		if (page[0].equals(servletPath)) {
    			break;
    		}
    		page = null;    		
    	}
    	
    	return page;
    }
    
    
	///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
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
}
