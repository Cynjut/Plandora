package com.pandora.gui.struts.action.projectpanel;

import javax.servlet.http.HttpServletRequest;

import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.action.ProjectPanelAction;
import com.pandora.gui.struts.form.ProjectPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

public class ProjectInfoProjectPanel  extends ProjectPanelAction {

	@Override
	public String getPanelId() {
		return "PROJ_INFO";
	}

	@Override
	public String getPanelTitle() {
		return "title.projectPanelForm.prjinf";
	}
	
	@Override
	public String renderPanel(HttpServletRequest request,
			ProjectPanelForm frm) throws BusinessException {
		StringBuffer sb = new StringBuffer("");
		ProjectDelegate pdel = new ProjectDelegate();

		if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
			ProjectTO pto = new ProjectTO(frm.getProjectId());
			pto = pdel.getProjectObject(pto, true);
			UserTO uto = SessionUtil.getCurrentUser(request);

			ProjectTO parent = pto.getParentProject();
			if (parent!=null && !parent.getId().equals(ProjectTO.PROJECT_ROOT_ID)) {
				parent = pdel.getProjectObject(parent, true);	
			} else {
				parent = null;
			}
			
			String estimatedEnd = "";
			if (pto.getEstimatedClosureDate()!=null) {
				estimatedEnd = DateUtil.getDate(pto.getEstimatedClosureDate(), uto.getCalendarMask(), uto.getLocale());
			}
			
			sb.append("<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			sb.append("<tr class=\"pagingFormBody\"><td class=\"formTitle\" width=\"140\"><b>" + super.getBundleMessage(request, "label.projName") + ":&nbsp;</b></td><td class=\"formBody\">" + pto.getName() + "</td></tr>\n");
			sb.append("<tr class=\"formNotes\"><td>&nbsp;</td><td>" + pto.getDescription() + "</td></tr>\n");
			if (parent!=null) {
				sb.append("<tr class=\"pagingFormBody\"><td class=\"formTitle\"><b>" + super.getBundleMessage(request, "label.projParent") + ":&nbsp;</b></td><td class=\"formBody\">" + parent.getName() + "</td></tr>\n");	
			}
			sb.append("<tr class=\"pagingFormBody\"><td class=\"formTitle\" width=\"100\"><b>" + super.getBundleMessage(request, "label.projStatus") + ":&nbsp;</b></td><td class=\"formBody\">" + pto.getProjectStatus().getName() + "</td></tr>\n");
			sb.append("<tr class=\"pagingFormBody\"><td class=\"formTitle\" width=\"100\"><b>" + super.getBundleMessage(request, "label.projEstimClosure") + ":&nbsp;</b></td><td class=\"formBody\">" + estimatedEnd + "</td></tr>\n");
						
			sb.append("</table>\n");  	
		}
		
		return sb.toString();

	}

}
