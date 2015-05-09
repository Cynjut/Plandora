package com.pandora.gui.taglib.calendar;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;

/**
 * The class of Calendar TagLib
 */
public class Calendar extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** Set the name of form where calendar is placed (used by struts) */
    private String name;
    
    /** Set the property of calendar text box (used by struts) */
    private String property;
    
    /** Set the style sheet class to format the calendar textbox */
    private String styleClass;

    /** Set the disabled status of calendar. Any arbitrary value is disabled */
    private String disabled;
   
    
    public int doStartTag() {
        try {
            JspWriter out = pageContext.getOut();
            String alt = this.getBundleMessage(pageContext, "label.calendar.button");
            String calFormat = this.getBundleMessage(pageContext, "calendar.format");
            Object value = RequestUtils.lookup(pageContext, name, property, null);
            if (value==null){
                value = "";
            }

            out.println(getCalendarHtml(alt, value, calFormat));
            
        } catch (IOException e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Calendar tag lib error", e);
        } catch (JspException e1) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Calendar tag lib error", e1);
        }
 
        return SKIP_BODY;
    }
    
    
    public String getCalendarHtml(String alt, Object value, String calMask){
        String textBox = "";
        String link = "";
        String js = "";
        
        if (disabled!=null && !disabled.trim().equals("")){
            textBox = "<input type=\"text\" id=\"" + property + "\" name=\"" + property + "\" size=\"10\" disabled=\"disabled\" value=\"" + value + "\" class=\"" + styleClass + "\">\n";
            link = "<img src=\"../images/calendaroff.gif\" " + HtmlUtil.getHint(alt) + " border=\"0\" >";
        } else {
            textBox = "<input type=\"text\" id=\"" + property + "\" name=\"" + property + "\" size=\"10\" value=\"" + value + "\" class=\"" + styleClass + "\">\n";
            String button = "<img src=\"../images/calendar.gif\" " + HtmlUtil.getHint(alt) + " border=\"0\" >";
            link = "<a href=\"javascript:" + this.getJSFunc(property) + ".popup();\" border=\"0\">" + button + "</a>";
            js = "<script language=\"JavaScript\">\n" + 
			          "var " + this.getJSFunc(property) + " = new calendar1(document.forms['" + name + "'].elements['" + property + "'], '" + calMask + "');\n " +
			          this.getJSFunc(property) + ".year_scroll = true;\n " +
			          this.getJSFunc(property) + ".time_comp = false;\n " +
			         "</script>\n";
       }
       String table = "<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\"><tr><td>\n" + textBox + "</td><td>" + link + "</td></tr></table>\n";
        
       return table + "\n" + js;
    }
    
    
    /**
     * Format the name of calendar JavaScript object 
     * @param prop
     * @return
     */
    private String getJSFunc(String prop){
        return "calc_" + prop;
    }
    
    /**
     * Get the key used by image html 'alt'
     * @param pageContext
     * @return
     */
   	private String getBundleMessage(PageContext pageContext, String key){
   	    String value = "";
		try {   	    			
		    value = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, key, null);
        } catch (JspException e) {
            value = "err!";
        }
        return value;
   	}
    

    ///////////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getProperty() {
        return property;
    }
    public void setProperty(String newValue) {
        this.property = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getStyleClass() {
        return styleClass;
    }
    public void setStyleClass(String newValue) {
        this.styleClass = newValue;
    }
    
    ///////////////////////////////////////////        
    public String getDisabled() {
        return disabled;
    }
    public void setDisabled(String newValue) {
        this.disabled = newValue;
    }
}
