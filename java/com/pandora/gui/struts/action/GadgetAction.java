package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.gadget.Gadget;
import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.GadgetForm;
import com.pandora.gui.struts.form.ResourceHomeForm;
import com.pandora.helper.SessionUtil;

public class GadgetAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showGadget";
		try {
			GadgetForm frm = (GadgetForm)form;
			frm.setHtmlGadgetList(this.getGadgetList(request).toString());			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}
		
	
	public ActionForward clickGadgetCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ResourceHomeForm frm = (ResourceHomeForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        StringBuffer sb = this.getGadgetBody(frm.getGenericTag(), request);
	        out.println(sb.toString());
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return null;
	}
	

	public ActionForward clickGadgetButton(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ResourceHomeForm frm = (ResourceHomeForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        this.saveGadget(frm.getPlanningId(), request);
	        
	        PrintWriter out = response.getWriter();
	        StringBuffer sb = this.getGadgetBody(frm.getGenericTag(), request);
	        out.println(sb.toString());
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return null;
	}
	
	
	private void saveGadget(String gadId, HttpServletRequest request) throws BusinessException{
		PreferenceDelegate pdel = new PreferenceDelegate();
		
		Vector gadList = this.getAll();		
		if (gadList!=null) {
			Iterator i = gadList.iterator();
			while(i.hasNext()) {				
				Gadget gad = (Gadget)i.next();
				if (gad.getId().equals(gadId)) {
			
					UserTO uto = SessionUtil.getCurrentUser(request);
					
					String value = uto.getPreference().getPreference(gad.getId() + ".enabled");
					if (value!=null && value.equals("0")){
						value = "1";
					} else {
						value = "0";
					}
					
					PreferenceTO pto = new PreferenceTO(gad.getId() + ".enabled", value, uto );
					uto.getPreference().addPreferences(pto);
					pto.addPreferences(pto);
		            pdel.insertOrUpdate(pto);
		            
		            break;
				}
			}
		}
	}
	
	
	private StringBuffer getGadgetBody(String categoryId, HttpServletRequest request) throws BusinessException, UnsupportedEncodingException{
		StringBuffer response = new StringBuffer();
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");

		Vector gadList = getAll();
		if (gadList!=null) {
			int col = 0;
			TransferObject cat = null;
			
			Iterator i = gadList.iterator();
			while(i.hasNext()) {
				Gadget gad = (Gadget)i.next();
				if (gad!=null) {
					String gcat = this.getBundleMessage(request, gad.getCategory(), true);
					if (gcat!=null) {
						
						if (!categoryId.trim().equals("")) {
							Vector categoryList = (Vector)request.getSession().getAttribute("GADGET_CATEGORY_LIST");
							if (categoryList!=null) {
								cat = (TransferObject)categoryList.get(Integer.parseInt(categoryId));
							}
						}
						
						if (cat==null || gcat.equals(cat.getId())) {
							
							if (col==0) {
								response.append("<tr class=\"gapFormBody\"><td colspan=\"2\">&nbsp;</td></tr><tr>");
							}
								
							response.append("<td width=\"50%\">");
							response.append(this.getHtmlGadget(gad, request));
							response.append("</td>");
							col++;
							
							if (col>=2) {
								response.append("</tr>\n");
								col = 0;
							}					
						}					
					}					
				}
			}
		}
		response.append("</table>\n");
		
		return response;		
	}
	
	
	private StringBuffer getHtmlGadget(Gadget gad, HttpServletRequest request) {
		StringBuffer response = new StringBuffer();

		String label = "";
		UserTO uto = SessionUtil.getCurrentUser(request);
		String value = uto.getPreference().getPreference(gad.getId() + ".enabled");
		if (value!=null && value.equals("1")){
			label = super.getBundleMessage(request, "label.manageOption.gadget.remove");
		} else {
			label = super.getBundleMessage(request, "label.manageOption.gadget.add");			
		}
		
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
		response.append("<tr valign=\"top\">");
		
		response.append("<td width=\"100\">");		
		response.append("<center><img src=\"" + gad.getImgLogo() + "\" border=\"0\" width=\"88\" height=\"50\">");
		response.append("<input type=\"button\" name=\"gadBt\" value=\"" + label + 
				"\" onClick=\"javascript:clickGadgetButton('" + gad.getId() + "');\" class=\"button\"></center>");
		response.append("</td>");

		String name = super.getBundleMessage(request, gad.getUniqueName(), true);
		String desc = "";
		if (gad.getDescription()!=null) {
			desc = super.getBundleMessage(request, gad.getDescription(), true);
			if (desc.length()>120) {
				desc = desc.substring(0, 119) + "...";
			}
		}
		
		response.append("<td valign=\"top\" align=\"left\" class=\"formNotes\">");		
		response.append("<b>" + name + "</b></br>");
		response.append(desc);
		response.append("</td>");

		response.append("</tr>");
		response.append("</table>\n");
		return response;
	}


	private StringBuffer getGadgetList(HttpServletRequest request) throws BusinessException, UnsupportedEncodingException {
		Vector categoryList = new Vector();
		
		StringBuffer response = new StringBuffer();
		response.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
				
		Vector gadList = getAll();
		if (gadList!=null) {
			HashMap hm = new HashMap();
			Iterator i = gadList.iterator();
			while(i.hasNext()) {	
				Gadget gad = (Gadget)i.next();
				if (gad!=null) {
					String label = this.getBundleMessage(request, gad.getCategory(), true);
					TransferObject to = (TransferObject)hm.get(label);
					if (to==null) {
						TransferObject newTo = new TransferObject(label, "1");
						hm.put(label, newTo);
						categoryList.addElement(newTo);
					} else {
						int n = (Integer.parseInt(to.getGenericTag()));
						n++;
						to.setGenericTag(n+"");
					}					
				}
			}
		
			//show the default link
			String allCategs = super.getBundleMessage(request,  "label.all") + " (" + gadList.size() + ")";
			response.append("<tr><td class=\"successfullyMessage\"><a href=\"javascript:clickGadgetCategory('');\">" + allCategs + "</a></td></tr>\n");
			response.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");
			
			for (int j=0; j<categoryList.size(); j++) {
				TransferObject value = (TransferObject)categoryList.elementAt(j);
				String content = value.getId() + " (" + value.getGenericTag() + ")";
				String link = "<a href=\"javascript:clickGadgetCategory('" + j + "');\">";
				response.append("<tr><td class=\"successfullyMessage\">" + link + content + "</a></td></tr>\n");
				response.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");				
			}
		}
		response.append("</table>\n");
	
		request.getSession().setAttribute("GADGET_CATEGORY_LIST", categoryList);
		
		return response;
	}
	
	
	private Vector getAll() throws BusinessException{
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		String classList = root.getPreference().getPreference(PreferenceTO.GADGET_BUS_CLASS);
		return GadgetBUS.getGadgetClassesByTokenizerString(classList);
	}
}
