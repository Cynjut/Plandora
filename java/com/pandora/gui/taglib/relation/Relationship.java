package com.pandora.gui.taglib.relation;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;
import org.apache.taglibs.display.LookupUtil;

import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;


public class Relationship extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** Related form name */
    private String name;

    /** The id of data collection from http session. This collection 
     * contain all the relation objects that should be displayed by tag lib. */
    private String collection;
  
    /** The default forward after relationship insertion */
    private String forward;

    /** Contain the type of entity that is using this tagLib. 
     * This field use the constants of PlanningRelationTO class*/
    private String entity;
    
    /** The name of attribute or method that contain the id of the source entity */
    private String property;

    /** The name of attribute or method that contain the id of the source entity project */
    private String projectProperty;
    
    /** the name of remove javascript funcion */
    private String removeFunction;
    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
    	NoteIcon note = new NoteIcon();
    	GridMindMapLinkDecorator mindMap = new GridMindMapLinkDecorator();
        
        try {
            JspWriter out = pageContext.getOut();

            //get all relation from http session
            Vector list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            UserTO handler = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
            
            buff = new StringBuffer();
            String title = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.title", null);
            String linkLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.linklabel", null);
            String searchLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.searchLabel", null);
            String entityLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.entitylabel", null);
            
            String nameLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.manageTask.name", null);
            String descLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.manageTask.desc", null);
            String respLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.requestHistoryResource", null);
            String statLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.requestStatus", null);
            
            Object entityId = LookupUtil.lookup(pageContext, this.getName(), this.getProperty(), null, false, null, null, null);
            Object projectId = LookupUtil.lookup(pageContext, this.getName(), this.getProjectProperty(), null, false, null, null, null);
            
            if (projectId!=null && !((String)projectId).trim().equals("-1")) {
            	buff.append("<tr class=\"formBody\">\n");            
               	buff.append("   <td><img src=\"../images/relation.gif\" " + HtmlUtil.getHint(title) + " border=\"0\" >&nbsp;&nbsp;<b>" + title + "</b></td>\n");
               	buff.append("</tr>\n");
                
                String combo = HtmlUtil.getComboBox("RELATION_TYPE", this.getComboList(), "textBox", "", pageContext);
                
                buff.append("<tr class=\"formBody\">\n");
                buff.append("<td>\n");
                buff.append("<table class=\"table\" width=\"100%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"2\">\n");
                buff.append("  <tr class=\"rowHighlight\">\n");
                buff.append("     <th colspan=\"7\">\n");
                buff.append("       <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"><tr class=\"rowHighlight\"><td width=\"120\" align=\"right\">");
                buff.append(linkLabel + ":\n");
                buff.append("       </td><td width=\"150\">");                
                buff.append("       " + combo + "\n");
                buff.append("       </td><td width=\"50\">");                
                buff.append(entityLabel + ":\n");
                buff.append("       </td><td width=\"30\">");
                buff.append("       <input maxlength=\"30\" name=\"linkRelation\" size=\"5\" maxlength=\"10\" value=\"\" class=\"textBox\">");
                buff.append("       </td><td width=\"10\">");
                buff.append("       <input type=\"image\" name=\"searchRelatnButton\" " + HtmlUtil.getHint(searchLabel) + " src=\"../images/search.gif\" onclick=\"javascript:window.open('../do/searchPlanning?operation=prepareForm&type=ALL&projectId=" + projectId + "', 'searchPlanning', 'width=650, height=300, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');\" class=\"button\">\n");                            
                buff.append("       </td><td>");                
                buff.append("       <input type=\"hidden\" name=\"RELATION_FORWARD\" value=\""+ this.getForward() + "\">\n");
                buff.append("       <input type=\"hidden\" name=\"SOURCE_ENTITY_ID\" value=\""+ entityId + "\">\n");
                buff.append("       <input type=\"hidden\" name=\"COLLECTION_KEY\" value=\""+ collection + "\">\n");
                buff.append("       </td><td width=\"30\">");                
                buff.append("       <input type=\"submit\" name=\"linkRelationButton\" onclick=\"javascript:buttonClick('" + this.getName() + "', 'linkRelation');\" value=\"OK\" class=\"button\">\n");
                buff.append("       </td></tr></table>");
                buff.append("     </th>\n");
                buff.append("  </tr>");
                
                if (list!=null && list.size()>0) {
                
                    Iterator i = list.iterator();
                    while(i.hasNext()) {
                    	PlanningRelationTO prto = (PlanningRelationTO)i.next();
                    	
                    	int labelIdx = Integer.parseInt(prto.getRelationType());
                    	boolean canRemove = true;
                    	String linkType = "";
                    	String linkDesc = "";
                    	String linkId = "";

                    	String linkedId = "";                    	
                    	if (prto.getPlanning().getId().equals(entityId)) {
                    		linkType = prto.getRelatedType();
                    		linkedId = prto.getRelated().getId();
                        	linkDesc = this.getLabel(prto.getRelated(), prto.getRelatedType(), nameLabel, 
                        								descLabel, respLabel, statLabel, handler);
                    	} else {
                    	    labelIdx = Integer.parseInt(prto.getRelationType()) + 50;
                    	    canRemove = false;
                    	    linkType = prto.getPlanType();
                    	    linkedId = prto.getPlanning().getId();
                        	linkDesc = this.getLabel(prto.getPlanning(), prto.getPlanType(), nameLabel, 
                        								descLabel, respLabel, statLabel, handler);
                    	}
                    	linkId = mindMap.getLink(linkedId, null);
                    	
                    	String relationType = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.relation." + labelIdx, null);
                    	String relatedType = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.entity." + linkType, null);
                    	
                    	buff.append("<tr class=\"tableRowEven\">");
                        buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\"><img src=\"" + HtmlUtil.getEntityIcon(linkType) + "\" border=\"0\"></td>\n");                       
                        buff.append("     <td class=\"tableCell\" align=\"center\" valign=\"top\">" + relationType + "</td>\n");
                        buff.append("     <td class=\"tableCell\" width=\"30%\" align=\"center\" valign=\"top\">" + relatedType + "</td>\n");
                        buff.append("     <td class=\"tableCell\" width=\"15%\" align=\"center\" valign=\"top\">" + linkId + "</td>\n");
                        buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\">" + note.getContent(linkDesc, null) + "</td>\n");

                    	buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\">");                        
                        if (canRemove) {
                            buff.append("<a href=\"javascript:" + removeFunction + "('removeLinkRelation', '" + entityId + "', '" + prto.getRelated().getId() + "', '"+ this.getForward() + "', '"+ collection + "');\" border=\"0\"><img border=\"0\" src=\"../images/linkbreak.png\" ></a>\n");    
                        } else {
                            buff.append("&nbsp;");                        		
                        }
                        buff.append("	  </td>\n");
                        
                    	buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\">");                        
                       	if (((labelIdx-50)+"").equals(PlanningRelationTO.RELATION_BLOCKS) || 
                       			(labelIdx+"").equals(PlanningRelationTO.RELATION_BLOCKS)) {
                           	String predAlt = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.predes.hist", null);                        		
                       		String alt = HtmlUtil.getHint(predAlt);
                       		buff.append("<a href=\"javascript:openTaskHistPopup('" + linkedId + "');\" border=\"0\"><img border=\"0\" " + alt + " src=\"../images/detailed.gif\" ></a>\n");
                       	} else {
                            buff.append("&nbsp;");                        		
                       	}
                       	buff.append("	  </td>\n");
                        
                        buff.append("</tr>");
                    }   
                }
                
                buff.append("</table></td></tr>");
                
                out.println(buff.toString());                     	
            }
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Relationship tag lib error", e);
        }
            
        return SKIP_BODY;
    }    
    
    
    private String getLabel(PlanningTO pto, String entityType, String nameLabel, 
    		String descLabel, String respLabel, String statLabel, UserTO handler) throws Exception{

    	TaskDelegate tdel = new TaskDelegate();
    	ProjectDelegate pdel = new ProjectDelegate();
    	OccurrenceDelegate odel = new OccurrenceDelegate();
    	RiskDelegate rdel = new RiskDelegate();
    	RequirementDelegate rqdel = new RequirementDelegate();
    	    	
    	String response = "";
    	
    	if (entityType.equals(PlanningRelationTO.ENTITY_TASK)) {
    		response = "<B>" + nameLabel + ": </B>";
    		TaskTO tto = (TaskTO)tdel.getTaskObject(new TaskTO(pto.getId()));
    		tto.setHandler(handler);
    		response = response + tto.getName();

    		if (tto.getAllocResources()!=null && tto.getAllocResources().size()>0) {
        		response = response + "<br /><B>" + respLabel + ": </B>";
    			response = response + tto.getInvolvedResources(true);        		
    		}

    	} else if (entityType.equals(PlanningRelationTO.ENTITY_PROJ)) {
    		response = "<B>" + nameLabel + ": </B>";
    		ProjectTO prto = (ProjectTO)pdel.getProjectObject(new ProjectTO(pto.getId()), true);
    		response = response + prto.getName();
    		
    		if (prto.getProjectStatus()!=null) {
        		response = response + "<br /><B>" + statLabel + ": </B>";
        		response = response + prto.getProjectStatus().getName();    			    			
    		}
    		
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_OCCU)) {
    		response = "<B>" + nameLabel + ": </B>";
    		OccurrenceTO oto = (OccurrenceTO)odel.getOccurrenceObject(new OccurrenceTO(pto.getId()));
    		response = response + oto.getName();

    		response = response + "<br /><B>" + statLabel + ": </B>";
    		response = response + oto.getStatusLabel();    		

    	} else if (entityType.equals(PlanningRelationTO.ENTITY_RISK)) {
    		response = "<B>" + nameLabel + ": </B>";
    		RiskTO rto = (RiskTO)rdel.getRisk(new RiskTO(pto.getId()));
    		response = response + rto.getName();

    		response = response + "<br /><B>" + respLabel + ": </B>";
    		response = response + rto.getResponsible();

    		if (rto.getStatus()!=null) {
        		response = response + "<br /><B>" + statLabel + ": </B>";
        		response = response + rto.getStatus().getName();    			
    		}

    	} else  if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		RequirementTO rto = (RequirementTO)rqdel.getRequirement(new RequirementTO(pto.getId()));
    		if (rto.getRequirementStatus()!=null) {
        		response = response + "<B>" + statLabel + ": </B>";
        		response = response + rto.getRequirementStatus().getName();    		    			
    		}
    	}
    	
    	if (!response.trim().equals("")) {
    		response = response + "<br />";
    	}
   		response = response + "<B>" + descLabel + ": </B>";
    	response = response + pto.getDescription();
    	response = response.replaceAll("\"", "&quot;");
    	
    	return response;
    }
    
   
    
    private String getComboList() throws Exception{       
    	StringBuffer buff = new StringBuffer();
    	
    	buff.append(PlanningRelationTO.RELATION_RELATED_WITH + "|");
    	buff.append(RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.relation.2", null));

    	if (entity.equals(PlanningRelationTO.ENTITY_TASK)) {

    		buff.append("|" + PlanningRelationTO.RELATION_COMPOSED_BY + "|");
        	buff.append(RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.relation.5", null));

        	buff.append("|" + PlanningRelationTO.RELATION_BLOCKS + "|");
        	buff.append(RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.relationTag.relation.6", null));    		

    	}

    	return buff.toString();
    }
    
    
    ///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    } 
    
    ///////////////////////////////////////////////             
    public String getForward() {
		return forward;
	}
	public void setForward(String newValue) {
		this.forward = newValue;
	}

	
    /////////////////////////////////////////////// 
	public String getEntity() {
		return entity;
	}
	public void setEntity(String newValue) {
		this.entity = newValue;
	}

	
    /////////////////////////////////////////////// 
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}

	
    /////////////////////////////////////////////// 
	public String getProjectProperty() {
		return projectProperty;
	}
	public void setProjectProperty(String newValue) {
		this.projectProperty = newValue;
	}

	
    /////////////////////////////////////////////// 	
	public String getRemoveFunction() {
		return removeFunction;
	}
	public void setRemoveFunction(String newValue) {
		this.removeFunction = newValue;
	}
	
}
