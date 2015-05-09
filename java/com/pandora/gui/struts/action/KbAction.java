package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.kb.KbDocumentTO;
import com.pandora.bus.kb.KbIndex;
import com.pandora.delegate.IndexEngineDelegate;
import com.pandora.delegate.KbDelegate;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.KbForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * 
 */
public class KbAction extends GeneralStrutsAction {

	private int pageSize = 7;
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    
	    String forward = "showKb";
	    try {
			KbForm kbf = (KbForm)form;
			kbf.clear();
						
			this.refreshList(request, form);
			
			//set empy to content grid
	        request.getSession().setAttribute("contentList", new Vector());
			
	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }
	    
		return mapping.findForward(forward);		
	}

	
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    
	    String forward = "showKb";
	    KbDelegate kbDel = new KbDelegate();
	    
	    try {
		    KbForm kbf = (KbForm)form;
		    
	        if (kbf.getSubject()!=null && !kbf.getSubject().trim().equals("") ) {

	            String projectSearch = kbDel.getProjectSearchSintax(kbf.getProjectId());
	            String similarity = "~0.7";
	            
	            String criteria = projectSearch ;
	            criteria = criteria + " AND " + kbf.getSubject() + similarity;
	            
	            Vector tempList = kbDel.search(criteria);
	            Vector contentList = this.filterList(kbf.getType(), tempList);
	            
	            request.getSession().setAttribute("contentList", contentList);
	            kbf.setCurrentPage(0);
	            
	            this.renderContent(mapping, form, request, response);
	        }
        
	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }

		return mapping.findForward(forward);		
	}
	
	private Vector filterList(String type, Vector list){
		Vector response = new Vector();
    	HashMap hm = new HashMap();
    	
    	if (list!=null) {
        	for (int i=0; i<list.size(); i++) {
        		KbDocumentTO kto = (KbDocumentTO)list.get(i);
                Document doc = kto.getDoc();

                String ktoId = doc.get(KbIndex.KB_ID);
                String ktoType = doc.get(KbIndex.KB_TYPE);
                
        		if (hm.get(ktoId)==null) {
        	        if (type!=null && !type.equalsIgnoreCase("ALL")) {
        	        	if (ktoType.equals(type)) {
        	        		response.addElement(kto);	
        	        	}
        			} else {
        				response.addElement(kto);
        			}
        		}
        		
                hm.put(ktoId, ktoId);        		
        	}    		
    	}
    	
        return response;
	}
		
	
	public ActionForward renderContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showKb";
		int count = 0;	
		try {
			KbForm kbf = (KbForm)form;
			kbf.setHtmlKbGrid("");			
			PlanningDelegate pdel = new PlanningDelegate();
			KbDelegate kbdel = new KbDelegate();
			
			String newPage = request.getParameter("new_page");
			if (newPage!=null && !newPage.trim().equals("")) {
				kbf.setCurrentPage(Integer.parseInt(newPage));
			}
			
	    	StringBuffer sb = new StringBuffer("");
	    	StringBuffer body = new StringBuffer("");
	    	Vector contentList = (Vector)request.getSession().getAttribute("contentList");
	    	int iniCursor = pageSize * kbf.getCurrentPage();
	    	
	    	if (contentList!=null && contentList.size()>0) {
	    		
	    		int finalCursor = pageSize * (kbf.getCurrentPage()+1);
	    		if (finalCursor>contentList.size()) {
	    			finalCursor = contentList.size();
	    		}
	    		
	    		count = iniCursor;
	    		while(count < finalCursor && contentList.size()>count) {
		    		KbDocumentTO kto = (KbDocumentTO)contentList.get(count);
		            Document doc = kto.getDoc();

		            String id = doc.get(KbIndex.KB_ID);
		            String typeLabel = "";
		            KbIndex klass = kbdel.getKbByUniqueName(doc.get(KbIndex.KB_TYPE));
		            if (klass!=null) {
		            	typeLabel = super.getBundleMessage(request, klass.getContextLabel());
		            }
		            //String content = doc.get(KbIndex.KB_CONTENT);
		            
		            PlanningTO pto = pdel.getSpecializedObject(new PlanningTO(id));
		            if (pto!=null) {

		            	body.append("<tr class=\"kbbodyHighLightRow\">");
			    		body.append("  <th class=\"kbtitle\" width=\"10\">&nbsp;</th>" +
			    				  "  <th class=\"kbtitle\" width=\"10\">&nbsp;<img src=\"" + HtmlUtil.getEntityIcon(pto.getType()) + "\" border=\"0\"></th>" +
			    				  "  <th class=\"kbbody\" width=\"100\"><center><b>" + typeLabel + "</b></center></th>" +
			    				  "  <th class=\"kbbody\">" + getTitleContent(request, pto) + "</th>" +
			    				  "  <th class=\"kbbody\" width=\"100\">" + 
			    				  				"<a href=\"javascript:showHide('ENTITY_" + count + "');\"><img src=\"../images/notequestion.gif\" border=\"0\"></a>" +
			    				  				this.getHistoryContent(request, pto) +
			    				  "</th>" +
			    				  "  <th class=\"kbtitle\" width=\"10\">&nbsp;</th>\n");
			    		body.append("</tr>\n");
			    		
			    		body.append("<tr class=\"kbbodyRow\">");
			    		body.append("<th class=\"kbbody\" colspan=\"3\">&nbsp;</th><th class=\"kbbody\" colspan=\"2\">\n");
			    		
			    		body.append("<b>ID: </b>" + id + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						String prjLabel = super.getBundleMessage(request, "label.viewKb.Proj");
						body.append("<b>" + prjLabel + ": </b>" + kto.getProject().getName() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						String dtLabel = super.getBundleMessage(request, "label.viewKb.grid.creation");
						body.append("<b>" + dtLabel + ": </b>" + DateUtil.getDateTime(kto.getCreationDate(), SessionUtil.getCurrentLocale(request), 2, 2));
						
			    		body.append("</th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
			    		
			    		body.append("<tr class=\"kbbodyRow\"><td colspan=\"6\">\n");
			    		body.append("<div id=\"ENTITY_" + count + "\">");
			    		body.append("   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			    		body.append("   <th class=\"kbbody\" width=\"126\">&nbsp;</th><th class=\"kbbody\">\n");			    		
						body.append(getBodyContent(request, pto));
			    		body.append("   </th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
						body.append("   </table>");
						body.append("</div><script>javascript:showHide('ENTITY_" + count + "');</script>");
						body.append("</td></tr>");

						body.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp;</td></tr>\n");
						
						count++;
						
		            } else {
			    	    contentList.remove(kto);
			    	    request.getSession().setAttribute("contentList", contentList);
		            }
		    	}
		    	
		    	kbf.setQuerySize(count-iniCursor);
		    	if (!body.toString().equals("")) {
			    	sb.append(this.getNavigationBar(request, kbf.getCurrentPage(), kbf.getQuerySize()));
			    	sb.append(body);
			    	sb.append(this.getNavigationBar(request, kbf.getCurrentPage(), kbf.getQuerySize()));		    		
		    	}
		    	
		    	kbf.setHtmlKbGrid(sb.toString());
		    	
	    	}
	    	
	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }
	    
		return mapping.findForward(forward);
    }
	
	private StringBuffer getTitleContent(HttpServletRequest request, PlanningTO pto){
		StringBuffer sb = new StringBuffer("");
		String entityType = pto.getType();
						
		sb.append("<a class=\"kbtitleLink\" href=\"javascript:loadMindMap('" + pto.getId() + "');\" border=\"0\">"); 		
    	if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		sb.append(StringUtil.cropWords(pto.getDescription(), 18, true));
    	} else {
    		sb.append(pto.getName());
    	}					
	  	sb.append("</a>");
	  	
		return sb;		
	}

	private StringBuffer getHistoryContent(HttpServletRequest request, PlanningTO pto){
		StringBuffer sb = new StringBuffer("");
		String entityType = pto.getType();
						 		
    	if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		sb.append("&nbsp;&nbsp;<a class=\"kbtitleLink\" href=\"javascript:showReqHistory('" + pto.getId() + "');\" border=\"0\">");    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_TASK)) {
        	sb.append("&nbsp;&nbsp;<a class=\"kbtitleLink\" href=\"javascript:showTaskHistory('" + pto.getId() + "');\" border=\"0\">");
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_OCCU)) {
        	sb.append("&nbsp;&nbsp;<a class=\"kbtitleLink\" href=\"javascript:showOccHistory('" + pto.getId() + "');\" border=\"0\">");
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_RISK)) {
        	sb.append("&nbsp;&nbsp;<a class=\"kbtitleLink\" href=\"javascript:showRiskHistory('" + pto.getId() + "');\" border=\"0\">");
    	}			
    	
    	if (!sb.toString().trim().equals("")) {
    		sb.append("<img src=\"../images/detailed.gif\" border=\"0\"></a>");
    	}
	  	
		return sb;		
	}
	
	
	private StringBuffer getBodyContent(HttpServletRequest request, PlanningTO pto){
		StringBuffer sb = new StringBuffer("");
		String entityType = pto.getType();
		
		String descLabel = super.getBundleMessage(request, "label.manageTask.desc");
		sb.append("<br><b>" + descLabel + ": </b>");
		
    	if (entityType.equals(PlanningRelationTO.ENTITY_TASK)) {
    		sb.append(pto.getDescription());

    		TaskTO tto = (TaskTO)pto;
    		tto.setHandler(SessionUtil.getCurrentUser(request));
    		String label = super.getBundleMessage(request, "label.manageOption.showRelatedRes");
    		sb.append("<br><br><b>" + label + ": </b>");
    		sb.append(tto.getInvolvedResources(true));
    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		sb.append(pto.getDescription());

    		RequirementTO rto = (RequirementTO)pto;

    		if (rto.getRequester()!=null) {
        		String label = super.getBundleMessage(request, "label.requester");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(rto.getRequester().getName());    			
    		}

    		if (rto.getPriority()!=null) {
        		String label = super.getBundleMessage(request, "label.requestPriority");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(super.getBundleMessage(request, "label.requestPriority." + rto.getPriority().toString()));    			
    		}

    	} else if (entityType.equals(PlanningRelationTO.ENTITY_PROJ)) {
    		sb.append(pto.getDescription());
    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_OCCU)) {
    		OccurrenceTO oto = (OccurrenceTO)pto;
    		if (!oto.isVisible()) {
    			sb.append(super.getBundleMessage(request, "label.formOccurrence.visibility.hide"));
    			
    		} else {
        		sb.append(pto.getDescription());

        		if (oto.getStatusLabel()!=null && !oto.getStatusLabel().trim().equals("")) {
            		String label = super.getBundleMessage(request, "label.formOccurrence.status");
            		sb.append("<br><br><b>" + label + ": </b>");
            		sb.append(oto.getStatusLabel());    			
        		}

                if (oto.getFields()!=null) {
                    for (int i = 0; i< oto.getFields().size(); i++) {
                        OccurrenceFieldTO field = (OccurrenceFieldTO)oto.getFields().elementAt(i);
                        if (field.getField()!=null && !field.getField().equals("")) {
                    		sb.append("<br><br><b>" + field.getField() + ": </b>");
                    		sb.append(field.getValue());    			
                        }
                    }
                }
        		
    		}

    	} else if (entityType.equals(PlanningRelationTO.ENTITY_RISK)) {
    		sb.append(pto.getDescription());
    		
    		RiskTO rto = (RiskTO)pto;

    		if (rto.getContingency()!=null && !rto.getContingency().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.contingency");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(rto.getContingency());    			
    		}

    		if (rto.getStrategy()!=null && !rto.getStrategy().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.strategy");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(rto.getStrategy());    			
    		}

    		if (rto.getResponsible()!=null && !rto.getResponsible().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.responsible");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(rto.getResponsible());    			
    		}

    		if (rto.getProbability()!=null && !rto.getProbability().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.probability");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(super.getBundleMessage(request, "label.formRisk.probability." + rto.getProbability()) );
    		}

    		if (rto.getImpact()!=null && !rto.getImpact().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.impact");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(super.getBundleMessage(request, "label.formRisk.impact." + rto.getImpact()) );
    		}

    		if (rto.getTendency()!=null && !rto.getTendency().trim().equals("")) {
        		String label = super.getBundleMessage(request, "label.formRisk.tendency");
        		sb.append("<br><br><b>" + label + ": </b>");
        		sb.append(super.getBundleMessage(request, "label.formRisk.tendency." + rto.getTendency()) );
    		}

    	}
						
		return sb;
	}
	
    private void refreshList(HttpServletRequest request, ActionForm form) throws Exception {
        Vector typeList = new Vector();
        UserDelegate udel = new UserDelegate();
        IndexEngineDelegate indexDel = new IndexEngineDelegate();
        KbForm kbf = (KbForm)form;

        TransferObject allTypes = new TransferObject("ALL", this.getBundleMessage(request, "label.all"));
        typeList.add(allTypes);
        
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();
        String classes = pref.getPreference(PreferenceTO.KB_BUS_CLASS);
        if (classes!=null) {
            
           String[] classList = classes.split(";");
           if (classList!=null && classList.length>0) {
                for (int i = 0; i<classList.length; i++) {
                    KbIndex kbus = indexDel.getKbClass(classList[i].trim());
                	if (kbus!=null) {
                        TransferObject to = new TransferObject(kbus.getUniqueName(), this.getBundleMessage(request, kbus.getContextLabel(), true));
                        typeList.add(to);                		
                	}
                }
            }
        }
        request.getSession().setAttribute("typeList", typeList);
        
        //get the name of project
		ProjectDelegate pdel = new ProjectDelegate();
		ProjectTO pto = pdel.getProjectObject(new ProjectTO(kbf.getProjectId()), true);
		kbf.setProjectName(pto.getName());
    }
	
    
	private StringBuffer getNavigationBar(HttpServletRequest request, int cursor, int querySize){
		StringBuffer sb = new StringBuffer("");

		sb.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp</td></tr>\n");

    	Vector contentList = (Vector)request.getSession().getAttribute("contentList");
    	if (contentList!=null && contentList.size()>0) {
    		sb.append("<tr  class=\"kbbodyRow\"><td colspan=\"6\">\n");
    		
    		String prevLabel = super.getBundleMessage(request, "paging.banner.prev_label");
    		String nextLabel = super.getBundleMessage(request, "paging.banner.next_label");
    		
    		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");	
    		sb.append("<tr>");
    		sb.append("<td class=\"kbbodyRow\">&nbsp;</td>\n");
    		if (cursor>0) {
    			sb.append("<td class=\"kbbodyRow\" width=\"70\"><a href=\"javascript:goToPage('" + (cursor-1) + "')\" class=\"gridLink\">[" + prevLabel + "]</a></td>");	
    		} else {
    			sb.append("<td class=\"kbNodyNavigationNoLink\" width=\"70\">[" + prevLabel + "]</td>");
    		}   
    		sb.append("<td class=\"kbNodyNavigationNoLink\" width=\"70\">&nbsp;</td>");
    		if ( (cursor+1) * pageSize < contentList.size()) {
        		sb.append("<td class=\"kbbodyRow\" width=\"70\"><a href=\"javascript:goToPage('" + (cursor+1) + "')\" class=\"gridLink\">[" + nextLabel + "]</a></td>");    			
    		} else {
    			sb.append("<td class=\"kbNodyNavigationNoLink\" width=\"70\">[" + nextLabel + "]</td>");    			
    		}
    		sb.append("</tr>");
    		sb.append("</table>\n");

    		sb.append("</td></tr>\n");
    		sb.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp</td></tr>\n");

    	} else {
    		sb.append("&nbsp;");
    	}
		
		return sb;
	}    
}
