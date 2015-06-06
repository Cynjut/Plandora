package com.pandora.gui.struts.action.projectpanel;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.CustomerFunctionTO;
import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.delegate.CustomerFunctionDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.action.ProjectPanelAction;
import com.pandora.gui.struts.form.ProjectPanelForm;
import com.pandora.helper.DateUtil;

public class StakeholdersProjectPanel extends ProjectPanelAction {

	@Override
	public String getPanelId() {
		return "PROJ_STHLD";
	}

	@Override
	public String getPanelTitle() {
		return "title.projectPanelForm.sthld";
	}
	
	@Override
	public String renderPanel(HttpServletRequest request,
			ProjectPanelForm frm) throws BusinessException {
		StringBuffer sb = new StringBuffer("");
		UserDelegate udel = new UserDelegate();

		if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
			ProjectTO pto = new ProjectTO(frm.getProjectId());

			HashMap<String, String> hmSh = new HashMap<String, String>();
			Vector<UserTO> allMembers = new Vector<UserTO>();

			Vector<LeaderTO> leaderList = udel.getLeaderByProject("'" + pto.getId() + "'");
			Vector<ResourceTO> resourceList = udel.getResourceByProject(pto.getId(), false, true);
			Vector<CustomerTO> customerList = udel.getCustomerByProject(pto, false);

			for (LeaderTO to : leaderList) {
				if (!to.getBoolIsDisabled()) {
					hmSh.put(to.getId(), to.getId());
					allMembers.add(to);
				}
			}
			for (ResourceTO to : resourceList) {
				if (hmSh.get(to.getId())==null && !to.getBoolIsDisabled()) {
					hmSh.put(to.getId(), to.getId());
					allMembers.add(to);
				}
			}
			for (CustomerTO to : customerList) {
				if (hmSh.get(to.getId())==null && !to.getBoolIsDisabled()) {
					hmSh.put(to.getId(), to.getId());
					allMembers.add(to);
				}
			}

			sb.append("<table width=\"70%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			sb.append("<tr><td width=\"150\">&nbsp;</td><td>\n");
			
			sb.append("<table border=\"0\" width=\"70%\" cellspacing=\"0\" cellpadding=\"10\">\n");		
			boolean firstCol = true;
			for (UserTO to : allMembers) {
				if (!to.getUsername().equals(RootTO.ROOT_USER))  {
					if (firstCol) {
						sb.append("<tr class=\"pagingFormBody\">\n");					
					}

					sb.append("<td class=\"formTitle\">" + this.getUserBox(to, pto.getId()) + "</td><td width=\"40\">&nbsp;</td>\n");
					
					if (!firstCol) {
						sb.append("</tr>\n");					
					}
					firstCol =  !firstCol;					
				}
			}
			
			if (!firstCol) {
				sb.append("<td>&nbsp;</td><td>&nbsp;</td></tr>\n");
			}
			sb.append("</table></td></tr></table>\n"); 
		}
		
		return sb.toString();
	}

	
	private String getUserBox(UserTO uto, String projectId) throws BusinessException{
		StringBuffer sb = new StringBuffer("");
		CustomerFunctionDelegate cfbus = new CustomerFunctionDelegate();
		
    	String phone = "&nbsp;";
    	if (uto.getPhone()!=null) {
    		phone = uto.getPhone();
    	}

    	String email = "&nbsp;";
    	if (uto.getEmail()!=null) {
    		email = uto.getEmail();
    	}
    	
    	String roles = "";
    	Vector<CustomerFunctionTO> cflist = cfbus.getListByCustomerProject(uto.getId(), projectId);
    	if (roles!=null) {
        	for (CustomerFunctionTO cfto : cflist) {
        		if (cfto.getCustomer()!=null && cfto.getFunct()!=null ) {
            		if (!roles.equals("")) {
            			roles = roles + ", ";
            		}        		
            		roles = roles + cfto.getFunct().getName();        			
        		}
    		}    		
    	}
    	
		sb.append("<table cellpadding=\"0\" border=\"0\" cellspacing=\"0\" width=\"100%\">" +
      		  "<tr class=\"tableRowOdd\">" +
      		     "<td width=\"50\" rowspan=\"4\"><img width=\"50\" height=\"60\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" + uto.getId() + "&ts=" +DateUtil.getNow().toString() + "\"></td>" +
      		     "<td width=\"10\">&nbsp;</td>" +
      		     "<td class=\"successfullyMessage\">" + uto.getName() + "</td>" +
      		  "</tr>" +
      		  "<tr class=\"tableRowOdd\"><td>&nbsp;</td><td class=\"formNotes\">" + email + "</td></tr>" +
      		  "<tr class=\"tableRowOdd\"><td>&nbsp;</td><td class=\"formNotes\">" + phone + "</td></tr>" +
      		  "<tr class=\"tableRowOdd\"><td>&nbsp;</td><td class=\"formNotes\">" + roles + "</td></tr>" +
      		  "</table>");
				
		return sb.toString();
	}
	
}
