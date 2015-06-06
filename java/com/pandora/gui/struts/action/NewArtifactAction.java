package com.pandora.gui.struts.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ArtifactTemplateTO;
import com.pandora.CategoryTO;
import com.pandora.delegate.ArtifactTemplateDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.NewArtifactForm;

public class NewArtifactAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showNewArtifact";
		try {
			NewArtifactForm frm = (NewArtifactForm)form;
			frm.setHtmlArtifactList(this.getArtifactList(frm, request).toString());			
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}

	
	
	public StringBuffer getArtifactBody(GeneralStrutsForm frm, HttpServletRequest request) throws BusinessException, UnsupportedEncodingException{
		StringBuffer response = new StringBuffer();
		String categoryId = frm.getGenericTag();
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");

		@SuppressWarnings("unchecked")
		Vector<ArtifactTemplateTO> list = (Vector<ArtifactTemplateTO>)request.getSession().getAttribute("ARTIFACT_LIST");
		if (list!=null) {
			Iterator<ArtifactTemplateTO> i = list.iterator();
			while(i.hasNext()) {
				ArtifactTemplateTO atto = i.next();
				if (categoryId.equals("") || atto.getCategory().getId().equals(categoryId)) {
					response.append("<tr><td>");
					response.append(this.getHtmlArtifact(atto, request));
					response.append("</td></tr>\n");
				}
			}
		}
		response.append("</table>\n");
		
		return response;		
	}
	

	private StringBuffer getHtmlArtifact(ArtifactTemplateTO atto, HttpServletRequest request) {
		StringBuffer response = new StringBuffer();
		
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
		response.append("<tr valign=\"top\">");
		
		response.append("<td width=\"10\" valign=\"top\">");		
		response.append("<input type=\"radio\" name=\"templateId\" id=\"templateId\" onclick=\"document.forms[1].selectedTemplate.value=this.value;\" value=\"" + atto.getId() + "\" />");
		response.append("</td>");

		String name = atto.getName();
		String desc = "";
		if (atto.getDescription()!=null) {
			desc = super.getBundleMessage(request, atto.getDescription(), true);
			if (desc.length()>255) {
				desc = desc.substring(0, 254) + "...";
			}
		}
		
		response.append("<td valign=\"top\" align=\"left\" class=\"formNotes\">");		
		response.append("<b>" + name + "</b><br/>");
		response.append(desc);
		response.append("<br></br></td>");

		response.append("</tr>");
		response.append("</table>\n");
		return response;
	}


	private StringBuffer getArtifactList(NewArtifactForm frm, HttpServletRequest request) throws BusinessException, UnsupportedEncodingException {
		Vector<CategoryTO> categoryList = new Vector<CategoryTO>();
		HashMap<String,CategoryTO> hm = new HashMap<String,CategoryTO>();		
		ArtifactTemplateDelegate atdel = new ArtifactTemplateDelegate();
		
		StringBuffer response = new StringBuffer();
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");

		Vector<ArtifactTemplateTO> list = atdel.getListByProject(frm.getProjectId());
		if (list!=null) {
			Iterator<ArtifactTemplateTO> i = list.iterator();
			while(i.hasNext()) {	
				ArtifactTemplateTO atto = i.next();
				String catlabel = atto.getCategory().getName();
				CategoryTO cto = (CategoryTO)hm.get(catlabel);
				if (cto==null) {
					atto.getCategory().setGenericTag("1");
					hm.put(catlabel, atto.getCategory());
					categoryList.addElement(atto.getCategory());
				} else {
					int n = (Integer.parseInt(cto.getGenericTag()));
					n++;
					cto.setGenericTag(n+"");
				}
			}
		
			//show the default link
			String allCategs = super.getBundleMessage(request,  "label.all") + " (" + list.size() + ")";
			response.append("<tr><td class=\"successfullyMessage\"><a href=\"javascript:clickArtifactCategory('');\">" + allCategs + "</a></td></tr>\n");
			response.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");
			
			for (int j=0; j<categoryList.size(); j++) {
				CategoryTO cto = (CategoryTO)categoryList.elementAt(j);
				String content = cto.getName() + " (" + cto.getGenericTag() + ")";
				String link = "<a href=\"javascript:clickArtifactCategory('" + cto.getId() + "');\">";
				response.append("<tr><td class=\"successfullyMessage\">" + link + content + "</a></td></tr>\n");
				response.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");				
			}
		}
		response.append("</table>\n");
	
		request.getSession().setAttribute("ARTIFACT_LIST", list);
		
		return response;
	}
}
