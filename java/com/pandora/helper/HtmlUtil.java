package com.pandora.helper;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.FieldValueTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ReportTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.taglib.calendar.Calendar;

/**
 * This class contain util methods to handler html.
 */
public class HtmlUtil {

	/**
	 * The constructor was set to private to avoid instancing  
	 */
	private HtmlUtil(){}

	
	/**
	 * Return a html formated string that containt a hint text:<br>
	 * "alt" for IExplorer and "title" for FireFox  
	 */
	public static String getHint(String content){
		
		content = content.replaceAll("\"", "''");
		
	    return "title=\"" + content + "\" alt=\"" + content + "\"";
	}
	

	/**
	 * Formats a generic html text box 
	 */
	public static String getTextBox(String name, String value, boolean isDisable, String eventJs){
	    return getTextBox(name, value, isDisable, eventJs, -1, 8, "text");
	}
	

	/**
	 * Formats a generic html text box 
	 */
	public static String getTextBox(String name, String value, int maxLength, int size){
	    return getTextBox(name, value, false, null, maxLength, size, "text");
	}
	

	/**
	 * Formats a generic html text box 
	 */	
	public static String getTextBox(String name, String value, boolean isDisable, String eventJs, int maxLength, int size){
	    return getTextBox(name, value, isDisable, eventJs, maxLength, size, "text");
	}
	

	/**
	 * Formats a generic html text box 
	 */	
	public static String getTextBox(String name, String value, boolean isDisable, String eventJs, int maxLength, int size, String type){
		return getTextBox(name, value, isDisable, eventJs, maxLength, size, type, "textBox");
	}
	
	
	/**
	 * Formats a generic html text box
	 */
	public static String getTextBox(String name, String value, boolean isDisable, String eventJs, int maxLength, int size, String type, String styleClass){
        String content = "<input type=\"" + type + "\" id=\"" + name + "\" name=\"" + name + "\" value=\"" + value + "\"";
        if (isDisable) {
            content = content + " disabled"; 
        }
        if (eventJs!=null){
            content = content + eventJs;    
        }
        if (maxLength>0) {
            content = content + " maxlength=\"" + maxLength + "\"";
        }
        if (size>0) {
            content = content + " size=\"" + size + "\"";
        }
        content = content + " class=\"" + styleClass + "\" />";
	    
	    return content;
	}

	
    /**
     * Return a CheckBox html.
     */
	public static String getChkBox(boolean status, String id, String tag, boolean disable){
		return getChkBox(status, id, tag, disable, "javaScript:check_" + tag + "('" + id + "');");
    }


	public static String getChkBox(boolean status, String id, String tag, boolean disable, String jscript){
        String name = "cb_" + id + "_" + tag;
        String content="<INPUT type=\"checkbox\" name=\"" + name + "\" value=\"" + id + "\"";
        
        if (status) content = content + " checked"; 
        if (disable) content = content + " disabled"; 

        if (jscript!=null) {
        	content = content + " onClick=\"" + jscript + "\"";	
        }
        
        content = content + " />";

        if (disable){
            content = content + " <input type=\"hidden\" name=\"" + name + "\" />"; 
        }

        return content;
    }

	
	public static String getComboBox(String key, String values, String selectedValue){
	    return getComboBox(key, values, "textBox", selectedValue, null);
	}
	
	public static String getComboBox(String key, String values, String cssName, String selectedValue, PageContext pageContext){
		return getComboBox(key, values, cssName, selectedValue, pageContext, 0, null);
	}
	
	public static String getComboBox(String key, String values, String cssName, String selectedValue, 
			PageContext pageContext, int size, String jScript){
	    StringBuffer response = new StringBuffer();
	    
	    response.append("<select id=\"" + key + "\" name=\"" + key + "\" " + (size>0?" size=\"" + size + "\"":""));
	    if (jScript !=null) {
	    	response.append(" onkeypress=\"" + jScript + "\" onchange=\"" + jScript + "\"");
	    }
	    response.append(" class=\"" + cssName + "\">");
        String[] options = values.split("\\|");
        if (options.length % 2 == 0) {
            for (int p=0; p<options.length; p+=2) {
                response.append("<option " + getSelectedComboValue(selectedValue, options[p]) + 
                        " value=\"" + options[p] + "\">" + 
                        getBundleString(options[p+1], pageContext) +"</option>");    
            }
        }
        response.append("</select>");
        
        return response.toString();
	}

	
	public static String getComboBox(String key, Vector values, String cssName, String selectedValue){
		return getComboBox(key, values, cssName, selectedValue, 0, null, false);
	}
	
	public static String getComboBox(String key, Vector values, String cssName, String selectedValue, int size, String jsScript, boolean isDisabled){
	    StringBuffer response = new StringBuffer();
	    
	    response.append("<select id=\"" + key + "\" name=\"" + key + "\" " + (size>0?" size=\"" + size + "\"":""));
	    if (jsScript !=null) {
	    	response.append(" onkeypress=\"" + jsScript + "\" onchange=\"" + jsScript + "\"");
	    }
	    response.append(" class=\"" + cssName + "\"" + (isDisabled?" disabled=\"disabled\"":"") + ">");
	    response.append(getComboOptions(key, values, cssName, selectedValue));
        response.append("</select>");
        
        return response.toString();
	}

	
	public static String getComboOptions(String key, Vector values, String cssName, String selectedValue){
	    StringBuffer response = new StringBuffer();
	    
	    if (values!=null) {
	    	Iterator<TransferObject> s = values.iterator();
	    	while(s.hasNext()) {
	    	    TransferObject to = s.next();
	            response.append("<option " + getSelectedComboValue(selectedValue, to.getId()) + 
	                    " value=\"" + to.getId() + "\">" + to.getGenericTag() + "</option>");        	    
	    	}	    	
	    }
        
        return response.toString();
	}
	
    private static String getSelectedComboValue(String currValue, String optionValue){
        String response = "";
        if (currValue!=null) {
            if (currValue.equals(optionValue)) {
                response = "selected ";
            }
        }
        return response;                
    }	
	
	

	public static String getProgressBar(long cursorValue, long maxValue, boolean showCursor){
	    StringBuffer sb = new StringBuffer();
	    if (cursorValue<=maxValue) {
	        sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
	        
	        	sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
	        
	        		float cursorBar = ((float)cursorValue / (float)maxValue) * 100;
	        		if (cursorBar<1) cursorBar = 1; 
	        		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"2\" cellpadding=\"0\"><tr class=\"tableRowOdd\">");	        
	        		sb.append("<td class=\"tableCellHeader\" width=\"" + ((int)cursorBar) + "%\">&nbsp;</td>");
	        		sb.append("<td class=\"tableRowOdd\" >&nbsp;</td>");
	        		sb.append("</tr></table>");
	        
	        	sb.append("</td></tr></table>");
	        
	        sb.append("</td>");
	        
	        if (showCursor) {
	        	sb.append("<td class=\"tableCell\" width=\"30\"><center>" + ((int)cursorBar) + "%</center></td></table>");	
	        } else {
	        	sb.append("<td class=\"tableCell\" width=\"5\">&nbsp;</td></table>");
	        }
	        
	    }
	    return sb.toString();
	}
	
	
    public static String getHtmlField(FieldValueTO field, String fieldValue, String formName, 
            String[] i18nBoolLabels, String[] i18nDateLabels){
    	return getHtmlField(field, fieldValue, formName, i18nBoolLabels, i18nDateLabels, null);
    }
    
	
    public static String getHtmlField(FieldValueTO field, String fieldValue, String formName, 
            String[] i18nBoolLabels, String[] i18nDateLabels, String jsScript){
        
        String response = "";
        if (field.getType().equals(FieldValueTO.FIELD_TYPE_BOOL)) {
            String labels = "";
            if (i18nBoolLabels!=null && i18nBoolLabels.length>=2) {
                labels = "OK|" + i18nBoolLabels[0] + "|NOK|" + i18nBoolLabels[1];                
            } else {
                labels = "<Err>|<Err>";
            }
            response = getComboBox(field.getId(), labels, "textBox", fieldValue, null, 0, jsScript);
            
        } else if (field.getType().equals(FieldValueTO.FIELD_TYPE_PASS)) {
            response = getTextBox(field.getId(), fieldValue, false, null, field.getMaxLen(), field.getSize(), "password");
        
        } else if (field.getType().equals(FieldValueTO.FIELD_TYPE_COMBO)) {
            response = getComboBox(field.getId(), field.getDomain(), "textBox", fieldValue, 0, jsScript, false);

        } else if (field.getType().equals(FieldValueTO.FIELD_TYPE_AREA)) {
            response = "<textarea name=\"" + field.getId() + "\" rows=\"" + field.getSize() + "\" cols=\"80\" class=\"textBox\">" + fieldValue + "</textarea>";

        } else if (field.getType().equals(FieldValueTO.FIELD_TYPE_DATE)) {
            Calendar cal = new Calendar();
            cal.setProperty(field.getId());
            cal.setName(formName);
            cal.setStyleClass("textBox");
            String alt = i18nDateLabels[0];
            String calFormat = i18nDateLabels[1];    	        
            response = cal.getCalendarHtml(alt, fieldValue, calFormat);
            
        } else if (field.getType().equals(FieldValueTO.FIELD_TYPE_RELATION_MANY)) {
        	response = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"40%\">" +
        			   "<tr><td width=\"40%\">" +
        			   			  getComboBox(field.getId(), field.getDomain(), (field.isReadOnly()?"textBoxDisabled":"textBox"), "", field.getSize(), null, false) + 
        			        "</td>" +
        			        "<td width=\"20%\">" +
        		            "<input type=\"button\" " + (field.isReadOnly()?"disabled=\"true\"":"") + " class=\"button\" value=\"   >>  \">" +
			                "<br/>" +
			                "<input type=\"button\" " + (field.isReadOnly()?"disabled=\"true\"":"") + " class=\"button\" value=\"   <<  \">" +
		        	        "</td><td width=\"40%\">" +
		        		      	  getComboBox(field.getId(), fieldValue, (field.isReadOnly()?"textBoxDisabled":"textBox"), "", 
		        		      			  		null, field.getSize(), jsScript) +
		        		    "</td>" +
		        		 "</tr></table>";
            
        } else {
            response = getTextBox(field.getId(), fieldValue, field.getMaxLen(), field.getSize());
        }
        
        return response;
    }

    
    public static Vector<TransferObject> getQueryData(String sql, PageContext pageContext, String optionalProjectId, String optionalUserId){
        DbQueryDelegate dbdel = new DbQueryDelegate();
        Vector<TransferObject> response = new Vector<TransferObject>();

        try {
        	
        	sql = checkSQLKeyWord(sql, optionalProjectId, optionalUserId);
        	
        	Vector<Vector<Object>> queryList = dbdel.performQuery(sql);
            if (queryList!=null && queryList.size()>0) {
            	
            	//remove the first row (title row)
            	queryList.remove(0);
            	
            	Iterator<Vector<Object>> s = queryList.iterator();
            	while(s.hasNext()) {
            		Vector<Object> item = s.next();
            	    TransferObject to = new TransferObject();
            	    to.setId((String)item.elementAt(0));
            	    
            	    String key = (item.elementAt(1)).toString();
               	    to.setGenericTag(getBundleString(key, pageContext));            	    	
            	    
                    response.add(to);        	    
            	}            	
            }
        } catch (BusinessException e) {
            response = new Vector<TransferObject>();
        }
                
        return response;
    }
    
    public static String checkSQLKeyWord(String sql, String optionalProjectId, String optionalUserId){
    	if (optionalProjectId!=null) {
    		sql = sql.replaceAll("\\?" + ReportTO.PROJECT_ID, optionalProjectId);
    	}
    	if (optionalUserId!=null) {
    		sql = sql.replaceAll("\\?" + ReportTO.USER_ID, optionalUserId);
    	}    	    	
    	return sql;
    }
    
    public static String getTextByPriority(UserTO uto, Integer priority){
    	String response = null;
    	
    	try {
    		PreferenceTO pto = uto.getPreference();
    		String show = pto.getPreference(PreferenceTO.HOME_REQULIST_PRIORITY_COLOR);
    		boolean showColor = (show!=null && show.trim().equalsIgnoreCase("true"));
    			
    		if (showColor) {
    			
    			if (priority.equals(new Integer("1"))) {
    				response = "#DDDDDD";
    			} else if (priority.equals(new Integer("2"))) {
    				response = "#8080C0";    				
    			} else if (priority.equals(new Integer("3"))) {
    				response = "#ffff80";
    			} else if (priority.equals(new Integer("4"))) {
    				response = "#ff8040";
    			} else if (priority.equals(new Integer("5"))) {
    				response = "#ff2020";
    			}
    		}

    	} catch(Exception e) {
    		response = null;
    	}
    	
		return response;
    }
    
    public static String getEntityIcon(String entityType) {
    	String response = "../images/empty.gif";
    	
    	if (entityType.equals(PlanningRelationTO.ENTITY_TASK)) {
    		response = "../images/newTask.gif";
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_REQ)) {
    		response = "../images/requirem.gif";
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_PROJ)) {
    		response = "../images/project.gif";
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_OCCU)) {
    		response = "../images/occurrence.gif";
    	} else if (entityType.equals(PlanningRelationTO.ENTITY_RISK)) {
    		response = "../images/risk.gif";
    	} else {
    		response = "../images/empty.gif";
    	}
    	return response;
    }
    
    private static String getBundleString(String key, PageContext pageContext ){
	    String value = "";
	    if (pageContext!=null) {
			try {   	    
			    value = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, key, null);
		        if (value.startsWith("???")) {
		            value = key;
				}
	        } catch (JspException e) {
	            value = "err!";
	        }	    	
	    } else {
	    	value = key;
	    }
        return value;
    }


	public static String getRadioBox(boolean status, String id, boolean disable, String jscript) {
        String name = "radio";
        String content="<INPUT type=\"radio\" name=\"" + name + "\" value=\"" + id + "\"";
        
        if (status) content = content + " checked"; 
        if (disable) content = content + " disabled"; 

        if (jscript!=null) {
        	content = content + " onClick=\"" + jscript + "\"";	
        }
        
        content = content + " />";

        if (disable){
            content = content + " <input type=\"hidden\" name=\"" + name + "\" />"; 
        }

        return content;
	}
	
	
	 public static Color decodeColor(String colorString){
		 Color color = null;
		 int red, green, blue;
		 if (colorString!=null) {
		     if (colorString.startsWith("#")) {
		    	 colorString = colorString.substring(1);
		     }
		     
		     if (colorString.endsWith(";")) {
		    	 colorString = colorString.substring(0, colorString.length() - 1);
		     }
		    
		     switch (colorString.length()) {
		     	case 6:
		            red = Integer.parseInt(colorString.substring(0, 2), 16);
		            green = Integer.parseInt(colorString.substring(2, 4), 16);
		            blue = Integer.parseInt(colorString.substring(4, 6), 16);
		            color = new Color(red, green, blue);
		            break;
		        case 3:
		            red = Integer.parseInt(colorString.substring(0, 1), 16);
		            green = Integer.parseInt(colorString.substring(1, 2), 16);
		            blue = Integer.parseInt(colorString.substring(2, 3), 16);
		            color = new Color(red, green, blue);
		            break;
		        case 1:
		            red = green = blue = Integer.parseInt(colorString.substring(0, 1), 16);
		            color = new Color(red, green, blue);
		            break;
		        default:
		            throw new IllegalArgumentException("Invalid color: " + colorString);
		    }			 
		 }
	    return color;
	 }
}
