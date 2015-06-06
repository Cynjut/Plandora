package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.EdiTO;
import com.pandora.UserTO;
import com.pandora.delegate.EdiDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.EdiForm;
import com.pandora.helper.SessionUtil;

public class EdiAction extends GeneralStrutsAction {

		
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRssPopup";
		try {
			EdiForm frm = (EdiForm)form;
			EdiDelegate del = new EdiDelegate();

			UserTO uto = SessionUtil.getCurrentUser(request);

			//get available uuid from database or create new...
			Vector<String> uuidList = new Vector<String>();
			HashMap<String, EdiTO> list = del.getUserEdiUUID(uto);		
			
			//IMPORTANT!! the order of TYPEs below, must be the same of it appears at popup
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_UNCL_TASK, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_ALL_TASK, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_TEAM_INFO, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_UNCL_REQ, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_ALL_RISK, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.RSS_TYPE_ALL_OCC, uto));	
			uuidList.add(this.checkEdiItem(list, EdiTO.ICAL_TYPE_ALL_OCC, uto));
			uuidList.add(this.checkEdiItem(list, EdiTO.ICAL_TYPE_ALL_TSK, uto));

			String reqUri = request.getRequestURI();	
			reqUri = reqUri.substring(0, reqUri.indexOf("/do"));

			String path = SessionUtil.getUri(request) + reqUri + "/edi?key=";

			//generate the UUID for each option and concatenate it at URI
			String content = this.getHtmlCombo(uuidList, request, "ediType", path, frm);
			frm.setHtmlTypeList(content);
			
		} catch(Exception e){
		   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return mapping.findForward(forward);		
	}

		
	private String checkEdiItem(HashMap<String, EdiTO> list, String ediType, UserTO uto) throws BusinessException {
		EdiDelegate del = new EdiDelegate();
		EdiTO rto = null;
		String uuid = null;

		if (list!=null) {
			rto = list.get(ediType);
			if (rto!=null) {
				uuid = rto.getEdiUUID();
			}
		}
		
		if (uuid==null) {
			uuid = this.getUUID();
			del.createUUID(ediType, uto,  uuid);
		}
		
		return uuid;
	}

	
	private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();			
	}
	

	private String getHtmlCombo(Vector<String> uuidList, HttpServletRequest request, String comboId, String path, EdiForm frm) {
		String list = "";
		for (int i=1; i<=8; i++) {
			if (i!=2) { //the implementation of RSS_TYPE_ALL_TASK was postponed... 
				
				String imgpath = "../images/sm-rss.png";
				if (i>=7){
					imgpath = "../images/sm-ics.png";
				}
				
				String uuid = uuidList.get(i-1);
				String optSelected = ""; 
				if (i==1) {
					optSelected = " checked";
					frm.setHtmlDefaultOpt(path + uuid);
				}
				list = list + "&nbsp;&nbsp;<input" + optSelected + " class=\"checkbox\" type=\"radio\" name=\"ediOptions\" onclick=\"changeEdiLink('" + 
						path + "', '" + uuid + "');\" value=\"" + uuid + "\"/><img valign=\"bottom\" src='" + imgpath  + "' border=\"0\" />&nbsp;" + super.getBundleMessage(request, "label.rss.type." + i) + "</br>";					
			}
		}			
		return list;
	}	
}
