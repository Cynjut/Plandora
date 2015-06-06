package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AdditionalFieldTO;
import com.pandora.AdditionalTableTO;
import com.pandora.AttachmentTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.kb.KbDocumentTO;
import com.pandora.bus.kb.KbIndex;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.delegate.IndexEngineDelegate;
import com.pandora.delegate.KbDelegate;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.KbForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.gui.taglib.discussiontopic.DiscussionTopic;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


public class KbAction extends GeneralStrutsAction {

	private int pageSize = 7;
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    
	    String forward = "showKb";
	    try {
			KbForm kbf = (KbForm)form;
			kbf.clear();
			
			this.refreshList(request, form);
	        request.getSession().setAttribute("contentList", new Vector<KbDocumentTO>());
	        
	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }
	    
		return mapping.findForward(forward);		
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showKb");
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
	            
	            Vector<KbDocumentTO> tempList = kbDel.search(criteria);
	            Vector<KbDocumentTO> contentList = this.filterList(kbf.getType(), tempList);
	            request.getSession().setAttribute("contentList", contentList);
	            kbf.setCurrentPage(0);

	            this.renderContent(mapping, form, request, response);
	        }
	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }

		return mapping.findForward(forward);		
	}
	

	public ActionForward showEntity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){	    
	    String forward = "showKb";
	    PlanningDelegate pdel = new PlanningDelegate();
	    ProjectDelegate prdel = new ProjectDelegate();
	    try {			
			KbForm kbf = (KbForm)form;
			kbf.setSubject("");
			
			this.refreshList(request, form);
			Vector<KbDocumentTO> contentList = new Vector<KbDocumentTO>();
			
			if (kbf.getId()!=null && !kbf.getId().equals("") && 
					kbf.getProjectId()!=null && !kbf.getProjectId().equals("")) {
				
				ProjectTO project = prdel.getProjectObject(new ProjectTO(kbf.getProjectId()), true);

		        PlanningTO pto = pdel.getSpecializedObject(new PlanningTO(kbf.getId()));			        
		        Document doc = new Document();
		        doc.add(new Field(KbIndex.KB_ID, pto.getId(), Store.YES, Index.TOKENIZED));

		        String kbType = "";
		        if (pto.getType().equals(PlanningRelationTO.ENTITY_TASK)) {
		        	kbType = "TaskKbIndex";
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_REQ)) {
		        	kbType = "RequirementKbIndex";
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_OCCU)) {
		        	kbType = "OccurrenceKbIndex";
		        } else if (pto.getType().equals(PlanningRelationTO.ENTITY_RISK)) {
		        	kbType = "RiskKbIndex";
		        }
		        doc.add(new Field(KbIndex.KB_TYPE, kbType, Store.YES, Index.TOKENIZED));
		        
		        doc.add(new Field(KbIndex.KB_CREATION_DATE, 
		        		DateUtil.getDateTime(pto.getCreationDate(), "dd/MM/yyyy hh:mm:ss"), Store.YES, Index.TOKENIZED));
		        
		        KbDocumentTO kb = new KbDocumentTO(doc, 1);
		        kb.setProject(project);
		        
		        contentList.add(kb);
		        request.getSession().setAttribute("contentList", contentList);
			}
            kbf.setCurrentPage(0);
            this.renderContent(mapping, form, request, response);

	    }catch(Exception e){
	        this.setErrorFormSession(request, "error.formKb.showForm", e);    
	    }
	    
		return mapping.findForward(forward);		
	}

	
	private Vector<KbDocumentTO> filterList(String type, Vector<KbDocumentTO> list){
		Vector<KbDocumentTO> response = new Vector<KbDocumentTO>();
    	HashMap<String,String> hm = new HashMap<String,String>();
    	
    	if (list!=null) {
        	for (int i=0; i<list.size(); i++) {
        		KbDocumentTO kto = list.get(i);
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
		
	
	@SuppressWarnings("unchecked")
	public ActionForward renderContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showKb";
		int count = 0;
        PlanningTO pto = null;
        AttachmentTO ato = null;
        
		try {
			KbForm kbf = (KbForm)form;
			kbf.setHtmlKbGrid("");			
			PlanningDelegate pdel = new PlanningDelegate();
			ProjectDelegate prdel = new ProjectDelegate();
			KbDelegate kbdel = new KbDelegate();
			
			String newPage = request.getParameter("new_page");
			if (newPage!=null && !newPage.trim().equals("")) {
				kbf.setCurrentPage(Integer.parseInt(newPage));
			}
			
	    	StringBuffer sb = new StringBuffer("");
	    	StringBuffer body = new StringBuffer("");
	    	Vector<KbDocumentTO> contentList = (Vector<KbDocumentTO>)request.getSession().getAttribute("contentList");
	    	int iniCursor = pageSize * kbf.getCurrentPage();
	    	
	    	if (contentList!=null && contentList.size()>0) {
	    		
	    		int finalCursor = pageSize * (kbf.getCurrentPage()+1);
	    		if (finalCursor>contentList.size()) {
	    			finalCursor = contentList.size();
	    		}
	    		
	    		count = iniCursor;
	    		while(count < finalCursor && contentList.size()>count) {
		    		KbDocumentTO kto = contentList.get(count);
		            Document doc = kto.getDoc();

		            String id = doc.get(KbIndex.KB_ID);
		            String typeLabel = "";
		            KbIndex klass = kbdel.getKbByUniqueName(doc.get(KbIndex.KB_TYPE));
		            if (klass!=null) {
		            	typeLabel = super.getBundleMessage(request, klass.getContextLabel());
			            UserTO uto = SessionUtil.getCurrentUser(request);
			            LeaderTO leader = new LeaderTO(uto);
			            Vector<ProjectTO> lprj = prdel.getProjectListForManagement(leader, true);
			            
			            if (PlanningTO.class.isAssignableFrom(klass.getBusinessClass()) ||
			            		ResourceTaskTO.class.getName().equals(klass.getBusinessClass().getName())) {		            
				            pto = pdel.getSpecializedObject(new PlanningTO(id), lprj);
				            
			            } else if (AttachmentTO.class.getName().equals(klass.getBusinessClass().getName())) {
			            	AttachmentDelegate adel = new AttachmentDelegate();
			            	ato = adel.getAttachment(new AttachmentTO(id));
			            	if ((ato.getVisibility()==null || !ato.getVisibility().equals("3")) &&
			            			!prdel.containsProject(lprj, adel.getAttachmentProject(ato.getPlanning().getId()))) {
			            		ato = null;
			            	}
			            }		            	
		            }
		            	
		            
		            if (pto!=null) {
		            	body.append(getPlanningItem(request, count, kbf, contentList, kto, typeLabel, pto));
						count++;
		            } else if (ato!=null) {
		            	body.append(getAttachmentItem(request, count, kbf, contentList, kto, typeLabel, ato));
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


	private Object getAttachmentItem(HttpServletRequest request, int count,	KbForm kbf, Vector<KbDocumentTO> contentList, KbDocumentTO kto,	String typeLabel, AttachmentTO ato) throws BusinessException {
		StringBuffer body = new StringBuffer();
		Locale loc = SessionUtil.getCurrentLocale(request);
		
		body.append("<tr class=\"kbbodyHighLightRow\">");
		body.append("  <th class=\"kbtitle\" width=\"10\">&nbsp;</th>" +
				    "  <th class=\"kbtitle\" width=\"10\">&nbsp;<img src=\"" + HtmlUtil.getEntityIcon("ATTACHMENT") + "\" border=\"0\"></th>" +
				    "  <th class=\"kbbody\" width=\"100\"><center><b>" + typeLabel + "</b></center></th>" +
				    "  <th class=\"kbbody\"><a class=\"kbtitleLink\" href=\"javascript:downloadAttachment('" + ato.getId() + "');\">" + ato.getName() + "</a></th>" +
				    "  <th class=\"kbbody\" width=\"100\">" + 
				  				"<a href=\"javascript:showHide('ENTITY_" + count + "');\"><img src=\"../images/notequestion.gif\" border=\"0\"></a>" +
				  				"&nbsp;&nbsp;<a class=\"kbtitleLink\" href=\"javascript:openAttachmentHistPopup('" + ato.getId() + "');\" border=\"0\">" +
				  				"<img src=\"../images/detailed.gif\" border=\"0\"></a>" +
				    "</th>" +
				    "  <th class=\"kbtitle\" width=\"10\">&nbsp;</th>\n");
		body.append("</tr>\n");
		
		body.append("<tr class=\"kbbodyRow\">");
		body.append("<th class=\"kbbody\" colspan=\"3\">&nbsp;</th><th class=\"kbbody\" colspan=\"2\">\n");
		
		body.append("<b>ID: </b>" + ato.getId() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String prjLabel = super.getBundleMessage(request, "label.viewKb.Proj");
		body.append("<b>" + prjLabel + ": </b>" + kto.getProject().getName() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String dtLabel = super.getBundleMessage(request, "label.viewKb.grid.creation");
		body.append("<b>" + dtLabel + ": </b>" + DateUtil.getDateTime(kto.getCreationDate(), SessionUtil.getCurrentLocale(request), 2, 2) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String relevLabel = super.getBundleMessage(request, "label.viewKb.grid.relevance");
		body.append("<b>" + relevLabel + ": </b>" + this.getRelevanceBar(kto, loc));
		
		body.append("</th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
		
		body.append("<tr class=\"kbbodyRow\"><td colspan=\"6\">\n");
		body.append("<div id=\"ENTITY_" + count + "\">");
		body.append("   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		body.append("   <th class=\"kbbody\" width=\"126\">&nbsp;</th><th class=\"kbbody\">\n");			    		
		body.append(this.getBodyContentAttachment(request, ato, kbf));
		body.append("   </th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
		body.append("   </table>");
		body.append("</div>");
		
		if (contentList.size()>1) {
			body.append("<script>javascript:showHide('ENTITY_" + count + "');</script>");	
		}
		
		body.append("</td></tr>");
		body.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp;</td></tr>\n");
		
		return body;
	}


	private StringBuffer getPlanningItem(HttpServletRequest request, int count,	KbForm kbf, Vector<KbDocumentTO> contentList,
			KbDocumentTO kto, String typeLabel, PlanningTO pto) throws BusinessException {
		StringBuffer body = new StringBuffer();
		Locale loc = SessionUtil.getCurrentLocale(request);
		
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
		
		body.append("<b>ID: </b>" + pto.getId() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String prjLabel = super.getBundleMessage(request, "label.viewKb.Proj");
		body.append("<b>" + prjLabel + ": </b>" + kto.getProject().getName() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String dtLabel = super.getBundleMessage(request, "label.viewKb.grid.creation");
		body.append("<b>" + dtLabel + ": </b>" + DateUtil.getDateTime(kto.getCreationDate(), SessionUtil.getCurrentLocale(request), 2, 2) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String relevLabel = super.getBundleMessage(request, "label.viewKb.grid.relevance");
		body.append("<b>" + relevLabel + ": </b>" + this.getRelevanceBar(kto, loc));
		
		body.append("</th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
		
		body.append("<tr class=\"kbbodyRow\"><td colspan=\"6\">\n");
		body.append("<div id=\"ENTITY_" + count + "\">");
		body.append("   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		body.append("   <th class=\"kbbody\" width=\"126\">&nbsp;</th><th class=\"kbbody\">\n");			    		
		body.append(getBodyContent(request, pto, kbf));
		body.append("   </th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
		body.append("   </table>");
		body.append("</div>");
		
		if (contentList.size()>1) {
			body.append("<script>javascript:showHide('ENTITY_" + count + "');</script>");	
		}
		
		body.append("</td></tr>");

		StringBuffer topic = getTopicContent(request, pto);
		if (topic!=null && !topic.toString().trim().equals("")) {
			body.append("<tr class=\"kbbodyRow\"><td colspan=\"6\">\n");
			body.append("<div id=\"TOPIC_" + count + "\">");
			body.append("   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			body.append("   <th class=\"kbbody\" width=\"126\">&nbsp;</th><th class=\"kbbody\">\n");			    		
			body.append(topic);
			body.append("   </th><th class=\"kbbody\" width=\"10\">&nbsp;</th></tr>\n");
			body.append("   </table>");
			body.append("</div><script>javascript:showHide('TOPIC_" + count + "');</script>");
			body.append("</td></tr>");
		}						
		
		body.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp;</td></tr>\n");
		
		return body;
	}

	private String getRelevanceBar(KbDocumentTO kto, Locale loc) {
		float perc = kto.getRelevance() * (float)100;
		if (perc>100) {
			perc = 100;
		}
		return StringUtil.getFloatToString(perc, "0.#", loc) + "%";
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
	

	private StringBuffer getTopicContent(HttpServletRequest request, PlanningTO pto) {
		StringBuffer sb = new StringBuffer("");
		
		return sb;
	}
	
	private StringBuffer getBodyContentAttachment(HttpServletRequest request, AttachmentTO ato, KbForm kbf) throws BusinessException{
		StringBuffer sb = new StringBuffer("");

		String descLabel = super.getBundleMessage(request, "label.manageTask.desc");
		sb.append("<br><b>" + descLabel + ": </b>");
		sb.append(ato.getComment());
		
		if (ato.getContentType()!=null && !ato.getContentType().trim().equals("")) {
    		String label = super.getBundleMessage(request, "label.formAttachment.type");
    		sb.append("<br><br><b>" + label + ": </b>");
    		sb.append(ato.getContentType());    			
		}

		if (ato.getVisibility()!=null && !ato.getVisibility().trim().equals("")) {
    		String label = super.getBundleMessage(request, "label.formAttachment.visibility");
    		sb.append("<br><br><b>" + label + ": </b>");
    		sb.append(label = super.getBundleMessage(request, "label.formAttachment.visibility." + ato.getVisibility()));    			
		}

		if (ato.getPlanning()!=null && ato.getPlanning().getId()!=null && !ato.getPlanning().getId().trim().equals("")) {
    		String label = super.getBundleMessage(request, "label.relationTag.entitylabel");
    		sb.append("<br><br><b>" + label + ": </b>");
    		sb.append("<a class=\"gridLink\" href=\"javascript:loadMindMap('" + ato.getPlanning().getId() + "');\" border=\"0\">");
    		sb.append(ato.getPlanning().getId() + "</a>");
		}
		
		return sb;

	}
	
	private StringBuffer getBodyContent(HttpServletRequest request, PlanningTO pto, KbForm kbf) throws BusinessException{
		StringBuffer sb = new StringBuffer("");
		String entityType = pto.getType();
		ProjectTO entityProject = null;
		UserDelegate udel = new UserDelegate();
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		String descLabel = super.getBundleMessage(request, "label.manageTask.desc");
		sb.append("<br><b>" + descLabel + ": </b>");
		
    	if (entityType.equals(PlanningRelationTO.ENTITY_TASK)) {
    		sb.append(pto.getDescription());

    		TaskTO tto = (TaskTO)pto;
    		tto.setHandler(uto);
    		String label = super.getBundleMessage(request, "label.manageOption.showRelatedRes");
    		sb.append("<br><br><b>" + label + ": </b>");
    		sb.append(tto.getInvolvedResources(true));
    		entityProject = tto.getProject();
    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		sb.append(pto.getDescription());

    		RequirementTO rto = (RequirementTO)pto;
    		entityProject = rto.getProject();
    		
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
    		entityProject = (ProjectTO)pto;
    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_OCCU)) {
    		OccurrenceTO oto = (OccurrenceTO)pto;
    		entityProject = oto.getProject();
    		
    		if (!oto.getVisible()) {
    			sb.append(super.getBundleMessage(request, "label.formPlanning.visibility.hide"));
    			
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
    		entityProject = rto.getProject();
    		
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
    	    	
    	if (pto.getAdditionalFields()!=null) {
    		this.getMetaFieldContent(request, pto, sb);
    	}
    	
    	if (pto.getDiscussionTopics()!=null && entityProject!=null) {
    		String permission = udel.checkCustomerViewDiscussion(uto, entityProject);
    		if (permission!=null && permission.equalsIgnoreCase("on")){
    			this.getDiscussionContent(request, pto, kbf, sb);	
    		}
    	}

    	
		return sb;
	}


	private void getDiscussionContent(HttpServletRequest request, PlanningTO pto, KbForm kbf, StringBuffer sb) {
		UserTO uto = SessionUtil.getCurrentUser(request);
		DiscussionTopic taglib = new DiscussionTopic();
		taglib.setName("kbForm");
		kbf.setId(pto.getId());
		request.getSession().setAttribute("REPLY_FORWARD", "viewKb?operation=renderContent");
		kbf.setSaveMethod(UserForm.UPDATE_METHOD, uto);
		
		sb.append(taglib.getTitleContent(uto, request.getSession()));
		sb.append(taglib.getTopicContent(pto.getDiscussionTopics(), uto));
	}


	private void getMetaFieldContent(HttpServletRequest request, PlanningTO pto, StringBuffer sb) {
		UserTO uto = SessionUtil.getCurrentUser(request);
		for (AdditionalFieldTO field : pto.getAdditionalFields()) {
			MetaFieldTO mfto = field.getMetaField();
			if (mfto!=null) {
				sb.append("<br><br><b>" + mfto.getName() + ": </b>");

		        if (mfto.getType().intValue()==MetaFieldTO.TYPE_TABLE) {
		        	Vector<AdditionalTableTO>tableValues = field.getTableValues();
		            try {
						sb.append(mfto.getFormatedTable("textBox", tableValues, mfto, uto, "kbForm", true));
					} catch (Exception e) {
						e.printStackTrace();
					}            	
		        } else {
		        	
		        	String currValue = field.getValue();
		        	if (mfto.getType()!=null && field.getDateValue()!=null 
		        			&& mfto.getType().intValue()==MetaFieldTO.TYPE_CALENDAR) {
		        		currValue = DateUtil.getDate(field.getDateValue(), uto.getCalendarMask(), uto.getLocale());
		        	}
		            sb.append(mfto.getFormatedField("textBox", currValue, uto, true, request.getSession()));    		        	
		        }
			}
		}
	}
	
    private void refreshList(HttpServletRequest request, ActionForm form) throws Exception {
        Vector<TransferObject> typeList = new Vector<TransferObject>();
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
	
    
	@SuppressWarnings("unchecked")
	private StringBuffer getNavigationBar(HttpServletRequest request, int cursor, int querySize){
		StringBuffer sb = new StringBuffer("");

		sb.append("<tr class=\"gapFormBody\"><td colspan=\"6\">&nbsp</td></tr>\n");

    	@SuppressWarnings("rawtypes")
		Vector<KbDocumentTO> contentList = (Vector)request.getSession().getAttribute("contentList");
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
