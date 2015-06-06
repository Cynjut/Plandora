package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.FieldValueTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ProjectPanelForm;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class ProjectPanelAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		//TODO precisa verificar se usuario que entrou nesse metodo esta alocado no projeto para evitar acessos de usuarios nao alocados
		return refresh(mapping,form,request,response);
	}

	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showProjectPanel";
		try {		
			ProjectPanelForm frm = (ProjectPanelForm)form;
			frm.setPortletHtml(this.getPortletHtml(request, frm));
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);	    
	}
	

	private String getPortletHtml(HttpServletRequest request, ProjectPanelForm frm) throws BusinessException {
		StringBuffer response = new StringBuffer("");
		Vector<ProjectPanelAction> panels = this.getPanels();
		//UserDelegate udel = new UserDelegate();
		
		if (panels!=null) {
						
			UserTO uto = SessionUtil.getCurrentUser(request);
			boolean isLeader = uto.isLeader(new ProjectTO(frm.getProjectId()));
			boolean isResource = uto.isResource(new ProjectTO(frm.getProjectId()));
			
			//String leaderLbl = super.getBundleMessage(request, "label.importExport.role_2");
			//String resourceLbl = super.getBundleMessage(request, "label.importExport.role_1");
			//String customerLbl = super.getBundleMessage(request, "label.importExport.role_0");
			String iconHint = super.getBundleMessage(request, "title.projectPanelForm.watcher");
			

			for (ProjectPanelAction ppa : panels) {
				if (ppa!=null) {
					
					boolean showPanel = this.getPermission(ppa, frm.getProjectId(), isLeader, isResource);
					if (showPanel) {
						String iconViewerHtml = "&nbsp;";
						String title = super.getBundleMessage(request, ppa.getPanelTitle(), true);
						response.append("<br><br>");
						response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						response.append("<tr><td width=\"10\">&nbsp;</td><td>");

						if (isLeader) {
							//response.append("<a href=\"javascript:void(0);\" onclick=\"openFloatPanel(getContent_" + ppa.getPanelId()+ "());\"><img border=\"0\" " + HtmlUtil.getHint(iconHint) + " src=\"../images/search.gif\" ></a>");
							//response.append("<script language=\"JavaScript\">\n");
							//response.append("function getContent_" + ppa.getPanelId()+ "(){\n");
							
							//String disableStr = "";
							//String checkResourceStr = "", checkCustomerStr = "";
							//if (ppa instanceof ProjectInfoProjectPanel) {
							//	disableStr = "disabled='1' checked ";
							//} else {
							//	disableStr = "";
							//	String keyResource = PreferenceTO.OVERVIEW_PROJ_PREFIX + frm.getProjectId() + "." + ppa.getPanelId() + ".1";
							//	String keyCustomer = PreferenceTO.OVERVIEW_PROJ_PREFIX + frm.getProjectId() + "." + ppa.getPanelId() + ".0";
							//	UserTO root = udel.getRoot();
							//	String prefRes = root.getPreference().getPreference(keyResource);
							//	if (prefRes!=null && prefRes.equalsIgnoreCase("on")) {
							//		checkResourceStr = "checked ";
							//	}
							//	String prefCus = root.getPreference().getPreference(keyCustomer);								
							//	if (prefCus!=null && prefCus.equalsIgnoreCase("on")) {
							//		checkCustomerStr = "checked ";
							//	}
							//}
							
							//response.append("return \"<input type='checkbox' onclick='checkvalue_" + ppa.getPanelId()+ "(&quot;2&quot;);' id='chk_2_" + ppa.getPanelId()+ "' disabled='1' name='chk_2_" + ppa.getPanelId()+ "' checked value='on'>" + leaderLbl + "</br>");
							//response.append("<input type='checkbox' onclick='checkvalue_" + ppa.getPanelId()+ "(&quot;1&quot;);' id='chk_1_" + ppa.getPanelId()+ "' " + disableStr + checkResourceStr + "name='chk_1_" + ppa.getPanelId()+ "' value='on'>" + resourceLbl + "</br>");
							//response.append("<input type='checkbox' onclick='checkvalue_" + ppa.getPanelId()+ "(&quot;0&quot;);' id='chk_0_" + ppa.getPanelId()+ "' " + disableStr + checkCustomerStr + "name='chk_0_" + ppa.getPanelId()+ "' value='on'>" + customerLbl + "</br>\";\n");
							//response.append("}\n");
							//response.append("function checkvalue_" + ppa.getPanelId()+ "(opt) {\n");
							//response.append("changeRole(opt+'', '" + ppa.getPanelId()+ "', '" + frm.getProjectId() + "');\n");
							//response.append("}\n"); 
							//response.append("</script>\n");			  
							
							iconViewerHtml = "<a href=\"javascript:editViewer('" +  frm.getProjectId() + "', '" +  ppa.getPanelId() + "');\"><img border=\"0\" " + HtmlUtil.getHint(iconHint) + " src=\"../images/search.gif\" ></a>";
						}


						response.append("<a class=\"successfullyMessage\" href=\"javascript:renderPanelBox('" + ppa.getPanelId()+ "');\">" + title + "</a>");
						response.append("<hr>\n");

						response.append("</td><td width=\"20\">" + iconViewerHtml + "</td></tr></table>");
						response.append("<div id=\"" + ppa.getPanelId()+ "\"></div>");					
					}
				}
			}
		}
		return response.toString();
	}
	
	
	public String getFilterComboBox(HttpServletRequest request, ProjectPanelAction ppa, UserTO uto, int colspan) {
		StringBuffer response = new StringBuffer();
		if (ppa.getPanelFields(request)!=null) {
			response.append("<tr><td colspan=\"" + colspan +" \" valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n");
			for (FieldValueTO fvto : ppa.getPanelFields(request)) {
				if (fvto.getType().equals(FieldValueTO.FIELD_TYPE_COMBO)) {
					Vector<TransferObject> domain = fvto.getDomain();
					domain.add(0, new TransferObject("", uto.getBundle().getMessage("label.combo.select", uto.getLocale())));
					String jsScript = "javascript:comboProjectPanelField('" + ppa.getPanelId() + "', '" + fvto.getId() + "');";
					response.append(HtmlUtil.getComboBox(ppa.getPanelId()+"_"+fvto.getId(), domain, "textBox", fvto.getCurrentValue(), 0, jsScript, false));
				}
			}
			response.append("</td>\n");
		}
		return response.toString();
	}

	
	public ActionForward showPanelDiv(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			ProjectPanelForm frm = (ProjectPanelForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  

	        PrintWriter out = response.getWriter();
	        String content = "";
	        if (frm.getPanelBoxId()!=null) {
        		Vector<ProjectPanelAction> panels = this.getPanels();
        		if (panels!=null) {
        			for (ProjectPanelAction ppa : panels) {
        				if (ppa.getPanelId().equals(frm.getPanelBoxId())) {
        					content = ppa.renderPanel(request, frm);
        					break;
        				}
        			}
        		}
	        }
	        out.println(content);
	        
	        out.flush();
	        
		} catch(Exception e){
			e.printStackTrace();
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return null;
	}

	public Vector<ProjectPanelAction> getPanels(){
		Vector<ProjectPanelAction> response = new Vector<ProjectPanelAction>();
		UserDelegate udel = new UserDelegate();
		try {
			UserTO root = udel.getRoot();
			String classlist = root.getPreference().getPreference(PreferenceTO.OVERVIEW_PROJ_CLASS);
			if (classlist!=null) {
				String[] clst = classlist.split(";");
				if (clst!=null) {
					for (String className: clst) {
						response.add(getPanelClass(className.trim()));
					}
				}
			}
			
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
	private ProjectPanelAction getPanelClass(String className){
		ProjectPanelAction response = null;
        try {
            Class<?> klass = Class.forName(className);
            response = (ProjectPanelAction)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}

	
	private boolean getPermission(ProjectPanelAction ppa, String projectId, boolean isLeader, boolean isResource) throws BusinessException {
		boolean response = false;
		UserDelegate udel = new UserDelegate();
			
		if (!isLeader && !ppa.getPanelId().equals("PROJ_INFO")) {
			
			String role = "0";
			if (isResource) {
				role = "1";
			}
			
			String key = PreferenceTO.OVERVIEW_PROJ_PREFIX + projectId + "." + ppa.getPanelId() + "." + role;
			UserTO root = udel.getRoot();
			String pref = root.getPreference().getPreference(key);
			response = (pref!=null && pref.equalsIgnoreCase("on"));
		}  else {
			response = true;
		}
			
		return response;
	}
		
	
	public String getPanelId(){
		return null;
	}

	public String getPanelTitle(){
		return null;
	}
	
	public String renderPanel(HttpServletRequest request, ProjectPanelForm frm)throws BusinessException {
		return null;
	}

	public Vector<FieldValueTO> getPanelFields(HttpServletRequest request){
		return null;
	}
		
}


