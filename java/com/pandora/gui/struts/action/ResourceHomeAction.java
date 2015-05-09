package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CostTO;
import com.pandora.DiscussionTopicTO;
import com.pandora.ExpenseTO;
import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.gadget.Gadget;
import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.delegate.CostDelegate;
import com.pandora.delegate.DiscussionTopicDelegate;
import com.pandora.delegate.GadgetDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ExpenseForm;
import com.pandora.gui.struts.form.ResourceHomeForm;
import com.pandora.gui.taglib.decorator.TaskGridPinDecorator;
import com.pandora.gui.taglib.form.HeaderFooterGrid;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into Resource Home form
 */
public class ResourceHomeAction extends GeneralStrutsAction {

	/**
	 * Shows the Resource/Leader Home page
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

		String forward = "showResourceHome";
		DiscussionTopicDelegate dtdel = new DiscussionTopicDelegate();
		try {
		    //get current user connected
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    
		    //clear current form
		    ResourceHomeForm rhfrm =(ResourceHomeForm)form; 
		    this.clearForm(form, request);
		    rhfrm.clear();

		    //this.clearMessages(request); //this was removed in order to receive the messages (error/success) from others fowards
		    rhfrm.setResourceId(uto.getId()+"");

		    rhfrm.setTaskPanelStyle("on"); rhfrm.setPendingPanelStyle("on"); rhfrm.setRequestPanelStyle("on");
		    rhfrm.setProjectPanelStyle("on"); rhfrm.setForumPanelStyle("on");		        
		    
		    String visibleStatus = uto.getPreference().getPreference(PreferenceTO.PANEL_HIDDEN);
		    if (visibleStatus!=null) {
		    	String[] hiddenPanels = visibleStatus.split(",");
		    	for (int i=0; i<hiddenPanels.length; i++) {
		    		String panel = hiddenPanels[i].trim();
		    		if (!panel.equals("")){
		    			if (panel.trim().equals("TASK_PANEL")) {
		    				rhfrm.setTaskPanelStyle("off");			
		    			} else if (panel.trim().equals("PENDING_PANEL")) {
		    				rhfrm.setPendingPanelStyle("off");
		    			} else if (panel.trim().equals("REQUIREMENT_PANEL")) {
		    				rhfrm.setRequestPanelStyle("off");
		    			} else if (panel.trim().equals("PROJECT_PANEL")) {
		    				rhfrm.setProjectPanelStyle("off");
		    			} else if (panel.trim().equals("FORUM_PANEL")) {
		    				rhfrm.setForumPanelStyle("off");
		    			}
		    		}
		    	}
		    }
		    
			//get all Projects from data base and put into http session (to be displayed by combo)
			ProjectDelegate prjdel = new ProjectDelegate();
			Vector<ProjectTO> prjList = prjdel.getProjectListByUser(uto);
			prjList = this.filterProjects(prjList, uto);
			request.getSession().setAttribute("projectList", prjList);

			//get all Requirements from data base and put into http session (to be displayed by combo)
			this.refreshRequirements(request, form, uto);
			
			//get all Tasks from data base and put into http session (to be displayed by combo)
			this.refreshTasks(request, form, uto);

			//get all Forums from data base and put into http session (to be displayed by combo)
			Vector<DiscussionTopicTO> discList = dtdel.getListByUser(uto);
			request.getSession().setAttribute("discussionTopicList", discList);

			//get all pending costs...
			this.refreshCosts(request);

			this.refreshGadgets(request, rhfrm, uto);
			rhfrm.setShowHideGadgetLabel(this.getBundleMessage(request, "label.manageOption.gadget.showhide"));
		    rhfrm.setShorcutsHtmlBody(this.getHtmlShortcuts(request, rhfrm, uto));
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}

		return mapping.findForward(forward);
	}

	
	/**
	 * Get all pending costs...
	 */
	public void refreshCosts(HttpServletRequest request) throws BusinessException {
		CostDelegate cdel = new CostDelegate();
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    ResourceHomeForm rhfrm = (ResourceHomeForm)request.getSession().getAttribute("resourceHomeForm");
	    
	    if (rhfrm!=null) {
			Vector<CostTO> costList = cdel.getPendindCosts(uto);
			if (costList!=null && costList.size()>0) {
				rhfrm.setShowPendingCosts("on");
				request.getSession().setAttribute("pendingCostList", costList);	
			} else {
				rhfrm.setShowPendingCosts("off");
				request.getSession().setAttribute("pendingCostList", new Vector<ExpenseTO>());
			}	    	
	    }
	}


	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		this.clearForm(form, request);
		return mapping.findForward("showResourceHome");
	}

	public ActionForward showWorkflow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    ResourceHomeForm rhfrm = (ResourceHomeForm)form;
	    rhfrm.setMaximizedGadgetId("");
		return mapping.findForward("showResourceHome");
	}

	
	public ActionForward showExpenseReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		UserDelegate udel = new UserDelegate();		
		try {
		    ResourceHomeForm rhfrm = (ResourceHomeForm)form;
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.EXPENSE_REPORT_URL);
			url = url.replaceAll("#EXPENSE_ID#", rhfrm.getId());
			rhfrm.setExpenseReportURL(url);			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}	    
		return mapping.findForward("showResourceHome");
	}	
	
	public ActionForward showMaximizedGadget(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    ResourceHomeForm rhfrm = (ResourceHomeForm)form;
	    rhfrm.setShowWorkflowDiagram("off");		
		return mapping.findForward("showResourceHome");
	}

	public ActionForward clickGadgetCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		GadgetAction gaction = new GadgetAction();
		return gaction.clickGadgetCategory(mapping, form, request, response);
	}

	public ActionForward clickGadgetButton(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		GadgetAction gaction = new GadgetAction();
		return gaction.clickGadgetButton(mapping, form, request, response);
	}

	
	public ActionForward refreshGadgetFields(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String content = "";
		PrintWriter out = null;
		UserDelegate udel = new UserDelegate();
		
		try {
			ResourceHomeForm rhfrm =(ResourceHomeForm)form;
			String params = rhfrm.getGenericTag();
			if (params!=null) {
				String[] tokens = params.split("\\|");
				if (tokens!=null) {

					//call the gadget and request new fields...
					UserTO root = udel.getRoot(); 
					UserTO uto = SessionUtil.getCurrentUser(request);						
					String classList = root.getPreference().getPreference(PreferenceTO.GADGET_BUS_CLASS);
					Gadget gad = GadgetBUS.getGadgetClassByTokenizerString(classList, tokens[0], uto);
					if (gad!=null) {
						Vector<TransferObject> currentValues = new Vector<TransferObject>();
						if (tokens.length>1) {
							for (int i=1; i<tokens.length; i+=2) {
								currentValues.add(new TransferObject(tokens[i], tokens[i+1]));
							}						
						}
						
						//get the new content in html format
						ShowGadgetPropertyAction showPropAction = new ShowGadgetPropertyAction();
						content = showPropAction.getHtmlPropertiesFields(gad, request, currentValues);						
					}

					//prepare the ajax response..
		            response.setContentType("text/xml");  
		            response.setHeader("Cache-Control", "no-cache");
					out = response.getWriter();	            
		            out.println(content); 
		            out.flush();
				}
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.showResHomeForm", e);
		} finally {
			if (out!=null) {
				out.close();
			}
		}
	
		return null;
	}
	

	public ActionForward hideGadget(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResourceHome";		
		try {		
			PreferenceDelegate pdel = new PreferenceDelegate();
			ResourceHomeForm rhfrm =(ResourceHomeForm)form;
			
			this.clearForm(form, request);

			String id = request.getParameter("gagid");
			UserTO uto = SessionUtil.getCurrentUser(request);
			PreferenceTO pto = new PreferenceTO(id + ".enabled", "0", uto );

		    //save the new preference
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
            pdel.insertOrUpdate(pto);	            
		
            this.refreshGadgets(request, rhfrm, uto);
            
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
		
		return mapping.findForward(forward);
	}
	
	
	
	public ActionForward renderGadget(ActionMapping mapping, ActionForm form,
				 HttpServletRequest request, HttpServletResponse response){
		try {
			GadgetDelegate del = new GadgetDelegate();						
			String gclass = request.getParameter("gagclass");
			del.renderContent(request, response, gclass);
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
		
	    return null;
	}

	
	public ActionForward refreshList(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResourceHome";
		
		try {		    
		    //clear current messages
		    ResourceHomeForm rhfrm =(ResourceHomeForm)form;
		    this.clearForm(form, request);
		    this.clearMessages(request);

		    //get current user connected
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    
		    if (rhfrm.getSource()!=null) {
			    if (rhfrm.getSource().equals(ResourceHomeForm.REQ_SOURCE) || rhfrm.getSource().equals(ResourceHomeForm.REFUSE_REQ_SOURCE)){
					this.refreshRequirements(request, form, uto);		        				

			    } else if (rhfrm.getSource().equals(ResourceHomeForm.TSK_SOURCE)){	        
			        this.refreshTasks(request, form, uto);
			    }		    	
		    }
			
		    this.refreshGadgets(request, rhfrm, uto);
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
	    return mapping.findForward(forward);
	}

	
	public ActionForward pin(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    TaskGridPinDecorator decorator = new TaskGridPinDecorator();
	    
		try {
		    ResourceHomeForm frm =(ResourceHomeForm)form;
		    this.clearForm(form, request);
		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
			PreferenceTO pto = uto.getPreference();
			String pin = pto.getPreference(PreferenceTO.PIN_TASK_LIST);

			//set the current value of Pin
			int classif = 0;
			int curPinPos = pin.indexOf(frm.getId());
			if (curPinPos>-1) {
				int nextPos = pin.indexOf("|", curPinPos)+1;
				String buff = pin.substring(curPinPos, nextPos-1);
				if (buff!=null) {
					String[] token = buff.split(";");
					if (token!=null && token.length==2) {
						classif = Integer.parseInt(token[1]);
						if (classif==3) {
							classif = 0;
						} else {
							classif++;
						}				
					}				    
				}
			} else {
			    classif = 0;
			}
			//replace the current value from preferences to the new pin value
			int pos = pin.indexOf(frm.getId());
			if (pin!=null && pos>-1) {
				String before = pin.substring(0, pos);
				int nextPos = pin.indexOf("|", pos)+1;
				String after = pin.substring(nextPos);
				pin = before + after;
			}
			
			pin = pin + frm.getId() + ";" + classif + "|";
			pto = new PreferenceTO(PreferenceTO.PIN_TASK_LIST, pin, uto);
			
		    //save the current preferences	
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
	        PreferenceDelegate pdel = new PreferenceDelegate(); 
            pdel.insertOrUpdate(pto);	            
					
            response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();  
            
            String key = decorator.getLabelKey(classif+"");
            out.println(decorator.getImg(classif+"") + "|" + this.getBundleMessage(request, key));  
            out.flush();

		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
	    return null;
	}

	
	public ActionForward showHidePanel(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResourceHome";		
		try {
		    ResourceHomeForm frm =(ResourceHomeForm)form;
		    this.clearForm(form, request);
		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
			PreferenceTO pto = uto.getPreference();
			String hidden = pto.getPreference(PreferenceTO.PANEL_HIDDEN);

			boolean isCurrentlyHidden = false;
			String newValue = "";
			if (hidden!=null) {
				String[] hiddenPannels = hidden.split(",");
				for (int i=0; i< hiddenPannels.length; i++) {
					String token = hiddenPannels[i];
					if (token!=null && !token.trim().equals("")) {
						if (token.trim().equals(frm.getPanelId())) {
							isCurrentlyHidden = true;
							
							if (frm.getPanelId().trim().equals("TASK_PANEL")) {
			    				frm.setTaskPanelStyle("on");			
			    			} else if (frm.getPanelId().trim().equals("PENDING_PANEL")) {
			    				frm.setPendingPanelStyle("on");
			    			} else if (frm.getPanelId().trim().equals("REQUIREMENT_PANEL")) {
			    				frm.setRequestPanelStyle("on");
			    			} else if (frm.getPanelId().trim().equals("PROJECT_PANEL")) {
			    				frm.setProjectPanelStyle("on");
			    			} else if (frm.getPanelId().trim().equals("FORUM_PANEL")) {
			    				frm.setForumPanelStyle("on");
			    			}							
							
						} else {
							newValue = newValue + token + ",";
						}
					}
				}
			}
			
			if (!isCurrentlyHidden) {
				newValue = newValue + frm.getPanelId() + ",";

				if (frm.getPanelId().trim().equals("TASK_PANEL")) {
    				frm.setTaskPanelStyle("off");			
    			} else if (frm.getPanelId().trim().equals("PENDING_PANEL")) {
    				frm.setPendingPanelStyle("off");
    			} else if (frm.getPanelId().trim().equals("REQUIREMENT_PANEL")) {
    				frm.setRequestPanelStyle("off");
    			} else if (frm.getPanelId().trim().equals("PROJECT_PANEL")) {
    				frm.setProjectPanelStyle("off");
    			} else if (frm.getPanelId().trim().equals("FORUM_PANEL")) {
    				frm.setForumPanelStyle("off");
    			}				
			}
			
		    //save the current preferences
			pto = new PreferenceTO(PreferenceTO.PANEL_HIDDEN, newValue, uto);
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
	        PreferenceDelegate pdel = new PreferenceDelegate(); 
	        pdel.insertOrUpdate(pto);	            
					
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
		
		return mapping.findForward(forward);
	}

	
	private void refreshGadgets(HttpServletRequest request, ResourceHomeForm frm, UserTO uto) throws BusinessException {
		StringBuffer content =  new StringBuffer("");
		
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		String classList = root.getPreference().getPreference(PreferenceTO.GADGET_BUS_CLASS);
		Vector<Gadget> list = GadgetBUS.getGadgetClassesByTokenizerString(classList);
		String gadWidth = uto.getPreference().getPreference(PreferenceTO.GADGET_WIDTH);
		
		if (list!=null) {
			
			String lblRefresh = this.getBundleMessage(request, "button.refresh");
			String lblClose = this.getBundleMessage(request, "button.close");
			String loadingLabel = super.getBundleMessage(request, "label.manageOption.gadget.loading");
			
			Iterator<Gadget> i = list.iterator();
			while(i.hasNext()) {
				Gadget gad = (Gadget)i.next();
				if (gad!=null) {
					String prefGad = uto.getPreference().getPreference(gad.getId() + ".enabled");
					if (prefGad!=null && prefGad.equals("1")) {
						content.append(HeaderFooterGrid.printHeaderBeforeBody("100%", false));
						content.append("<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
						content.append("  <tr><td>&nbsp;</td>");
						content.append("  <td width=\"20\"><a href=\"#\" onclick=\"callGadget_" + gad.getId() + "('" + gad.getId() + "','" + gadWidth + "','" + (gad.getHeight()+15) + "');\");\"><img align=\"center\"" + HtmlUtil.getHint(lblRefresh) + " border=\"0\" src=\"../images/rgadget.gif\" ></a></td>");
						
						//maximized form icon
						content.append(this.getMaximizedGadgetHtml(gad, request));	

						//properties form icon					
						if (gad.getFieldsId()!=null && gad.getFieldsId().size()>0) {
							content.append(this.getGadgetPropertyHtml(gad, request, "home_show"));	
						}
						
						content.append("  <td width=\"20\"><a href=\"../do/manageResourceHome?operation=hideGadget&gagid=" + gad.getId() + "\"><img align=\"center\" border=\"0\" " + HtmlUtil.getHint(lblClose) + " src=\"../images/hgadget.gif\" ></a></td>");
						content.append("  </tr></table>\n");
						content.append(HeaderFooterGrid.printHeaderAfterBody(false, "gridcontentgadget"));

						content.append("<table width=\"95%\" height=\"" + (gad.getHeight()-15) + "\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
						content.append("  <tr><td>");
						content.append(gad.gadgetToHtml(request, Integer.parseInt(gadWidth)-10, (gad.getHeight()), loadingLabel)); 
						content.append("  </br></td></tr></table>\n");
						
						content.append("</div><div class=\"panelfooter\"></div>");
						content.append("<div>&nbsp;</div>");						
					}					
				}
			}
		}
		
		if (content.toString().trim().equals("")) {
			frm.setShowHideGadgetColumn("off");
			frm.setContainGagdet("off");
		} else {
			frm.setShowHideGadgetColumn("on");
			frm.setContainGagdet("on");
		}
		
		frm.setGadgetHtmlBody("<td width=\"" + gadWidth + "\" valign=\"top\">" + content.toString() + "</td>");		
	}
	
	public String getGadgetPropertyHtml(Gadget gad, HttpServletRequest request, String forwad){
		String lblPropety = this.getBundleMessage(request, "label.manageOption.gadget.property");		
		return "  <td width=\"20\"><a href=\"#\" onclick=\"displayMessage('../do/showGadgetProperty?operation=prepareForm&forwardAfterSave=" + forwad + "&gagid=" + gad.getId() + "', " + 
			gad.getPropertyPanelWidth() + ", " + gad.getPropertyPanelHeight() + ");return false;\" border=\"0\">" +
			"<img align=\"center\" " + HtmlUtil.getHint(lblPropety) + " border=\"0\" src=\"../images/pgadget.gif\" ></a></td>";	
	}
	
	
	public String getMaximizedGadgetHtml(Gadget gad, HttpServletRequest request){
		String response = "";
		if (gad.showMaximizedOption()) {
			String lblMaximize = this.getBundleMessage(request, "label.manageOption.gadget.maximize");		
			response = "  <td width=\"20\"><a href=\"#\" onclick=\"showMaximizedGadget('" + gad.getId() + "');\" border=\"0\">" +
						"<img align=\"center\" " + HtmlUtil.getHint(lblMaximize) + " border=\"0\" src=\"../images/mgadget.gif\" ></a></td>";		
		}
		return response;
	}
	
	/**
	 * Refresh requirement grid with list of all requests from data base.
	 */
	private void refreshRequirements(HttpServletRequest request, ActionForm form, UserTO uto) throws BusinessException{
	    ResourceHomeForm rhfrm =(ResourceHomeForm)form;
		RequirementDelegate rdel = new RequirementDelegate();
		
		Vector<RequirementTO> reqList = rdel.getListByUser(uto, rhfrm.getBoolCloseRequest(), false);
		request.getSession().setAttribute("myRequirementList", reqList);
		
		//if current user is a leader into any project show a list of pending requirements...
		if (uto instanceof LeaderTO) {
		    rhfrm.setShowPendingReq("on");
		    Vector<RequirementTO> pendReqList = rdel.getPendingListByUser(uto, false);
			request.getSession().setAttribute("pendRequirementList", pendReqList);		                
        } else {
		    rhfrm.setShowPendingReq("off");
		}
	}	
	
	/**
	 * Refresh requirement grid with list of all requests from data base.
	 */
	private void refreshTasks(HttpServletRequest request, ActionForm form, UserTO uto) throws BusinessException{
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate(); 
	    ResourceHomeForm rhfrm =(ResourceHomeForm)form;
		ResourceTO rto = new ResourceTO(uto.getId());
		rto.setPreference(uto.getPreference());
		Vector<ResourceTaskTO> taskList = rtdel.getTaskListByResource(rto, rhfrm.getBoolCloseTask());
		request.getSession().setAttribute("taskList", taskList);		
	}
	
	
	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    ResourceHomeForm rhfrm = (ResourceHomeForm)form;
	    rhfrm.setMaximizedGadgetId("");
	    rhfrm.setShowWorkflowDiagram("off");
	    rhfrm.setExpenseReportURL("");
	}
	
	
	private Vector<ProjectTO> filterProjects(Vector<ProjectTO> prjList, UserTO uto) {
		Vector<ProjectTO> response = new Vector<ProjectTO>();
		if (prjList!=null) {
			String hidePrj = uto.getPreference().getPreference(PreferenceTO.HIDE_PROJECT);
			String[] hidePrjList = null;
			if (hidePrj!=null && hidePrj.trim().length()>0) {
				hidePrjList = hidePrj.split("\\|");	
			}
			Iterator<ProjectTO> i = prjList.iterator();
			while(i.hasNext()) {
				ProjectTO pto = i.next();
				if (hidePrjList!=null) {
					
					boolean hideIt = false;
					
					for (int j=0; j<hidePrjList.length; j++) {
						if (hidePrjList[j].equals(pto.getId())) {
							hideIt = true;
							break;
						}
					}
					
					if (!hideIt) {
						response.add(pto);
					}
				} else {
					response.add(pto);	
				}
				
			}
		}
		return response;
	}
	

	public ActionForward clickNodeTemplate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ShowAllTaskAction showAllAction = new ShowAllTaskAction();
			ResourceHomeForm rhfrm = (ResourceHomeForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        String content = showAllAction.createDiagramNodeTip(request, rhfrm.getPlanningId()).toString();
	        out.println(content);
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return null;
	}
	
	
	private String getHtmlShortcuts(HttpServletRequest request, ResourceHomeForm rhfrm, UserTO uto){
		String response = "";
		String[] url = {"", "", ""};
		
		for (int i=0; i<3 ;i++) {
			url[i] = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_URL + (i+1));
			
			if (url[i]!=null && !url[i].equals("")) {
				
				String altValue = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_NAME + (i+1));
				
				String image = "table.png";
				String img = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_ICON + (i+1));
				if (img!=null && !img.trim().equals("")) {
					if (img.equals("1")) {
						image = "report.gif";
					} else if (img.equals("2")) {
						image = "bsc.gif";
					} else if (img.equals("3")) {
						image = "form.png";
					} else if (img.equals("4")) {
						image = "table.png";
					} else if (img.equals("5")) {
						image = "alert-task.gif";
					} else if (img.equals("6")) {
						image = "fav-task.gif";
					}
				}
					
				response += "<td valign=\"middle\" align=\"right\" width=\"20\">";
				response += 	"<a href=\"javascript:goToForm('" + url[i] + "');\" border=\"0\"> \n";
				response += 		"<img border=\"0\" " + HtmlUtil.getHint(altValue) + " align=\"center\" src=\"../images/" + image + "\" >";
				response += 	"</a>"; 				
				response += "</td>";
			}
		}
		
		if (!response.trim().equals("")){
			String width = "10";
			if (rhfrm.getShowHideGadgetColumn().equals("on") && !rhfrm.getGadgetHtmlBody().trim().equals("")) {
				width = uto.getPreference().getPreference(PreferenceTO.GADGET_WIDTH);
			}
			response += "<td width=\"" + width + "\">&nbsp;</td>";				
		}
		
		return response;
	}
	
}
