package com.pandora.gui.taglib.form;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

import org.apache.taglibs.display.LookupUtil;

public class NoteIcon extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String type;
    
    private String property;    
    

    public int doStartTag() {
        try {
            JspWriter out = pageContext.getOut();

            Object description = null;
    		try {
    			description = LookupUtil.lookup(pageContext, this.name, this.property, null, false, null, null, null);            	
    		} catch(Exception e){
    			description = null;
    		}

    		if (description==null) {
    			description = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, this.property, null);
    		}

            out.println(this.getContent(description+"", this.type));
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "NoteIcon tag lib error", e);
        }
            
        return SKIP_BODY;
    }


	public String getContent(String description, String t) throws Exception{
		
		description = description.replaceAll("\r\n", "");
		description = description.replaceAll("\n", "");
		description = StringUtil.formatWordToJScript(description);
		
		String image = "<img valign=\"center\" src=\"../images/notequestion.gif\" border=\"0\"/>";
		if (t!=null && t.trim().equalsIgnoreCase("info")) {
			image = "<img valign=\"center\" src=\"../images/noteinfo.gif\" border=\"0\"/>";	
		}
		return "<a href=\"javascript:void(0);\" onclick=\"openFloatPanel('" + description + "');\">" + image + "</a>";
		//return "<a style=\"cursor: help;\" title=\"" + description + "\">" + image + "</a>";
	}

    
    /////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

    /////////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

    /////////////////////////////////////
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}    
    
}
