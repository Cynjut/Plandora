package com.pandora.gui.taglib.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.pandora.helper.LogUtil;

public class HeaderFooterGrid extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private static final String TYPE_HEADER = "HEADER";
	
	private static final String TYPE_FOOTER = "FOOTER";
	
	
    private String type;
    
    private String width;
    
    
    public int doStartTag() {
        try {
            JspWriter out = pageContext.getOut();
            
        	String gridWidth = "100%";
        	if (width!=null && !width.equals("")){
        		gridWidth = width;
        	}
        	
            StringBuffer sb = new StringBuffer();
            
            if (type.equals(TYPE_HEADER)) {
            	sb.append(printHeaderBeforeBody(gridWidth, true));
            } else if (type.equals(TYPE_FOOTER)) {
            	sb.append(printFooterBeforeBody());
            }
            
            out.println(sb.toString());
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "HeaderFooterGrid tag lib error", e);
        }
            
        return EVAL_BODY_INCLUDE;
    }

   

	public int doAfterBody() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            
            StringBuffer sb = new StringBuffer();            
            if (type.equals(TYPE_HEADER)) {
            	sb.append(printHeaderAfterBody(true));
            } else if (type.equals(TYPE_FOOTER)) {
            	sb.append(printFooterAfterBody());
            }
            
            out.println(sb.toString());
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "HeaderFooterGrid tag lib error", e);
        }
    	return (SKIP_BODY);
    }
    

	public static StringBuffer printHeaderBeforeBody(String gridWidth, boolean includeIcon) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("<div class=\"rndborder\" style=\"width:" + gridWidth + "\">");
    	sb.append("<b class=\"b1\"></b><b class=\"b2\"></b><b class=\"b3\"></b><b class=\"b4\"></b>");
    	sb.append("<div class=\"paneltitle\">");
    	if (includeIcon) {
        	sb.append("<img src=\"../images/gear.png\" border=\"0\" alt=\"\" title=\"\"/>");
        	sb.append("<span class=\"titlelabel\">");    		
    	}
    	return sb;
	}

	public static StringBuffer printHeaderAfterBody(boolean includeIcon, String contentClass) {
    	StringBuffer sb = new StringBuffer();
    	if (includeIcon) {
    		sb.append("</span><br>");	
    	}
    	sb.append("</div>");
    	sb.append("<div class=\"" + contentClass + "\">"); //open div of grid content
    	return sb;					
	}
	
	public static StringBuffer printHeaderAfterBody(boolean includeIcon) {
		return printHeaderAfterBody(includeIcon, "gridcontent");
	}


	public static StringBuffer printFooterBeforeBody() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("</div>"); //close div of grid content
    	sb.append("<div class=\"panelfooter\">");		
    	return sb;
	}

    public static StringBuffer printFooterAfterBody() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("</div>");
    	sb.append("<b class=\"b4\"></b><b class=\"b3\"></b><b class=\"b2\"></b><b class=\"b1\"></b>");
    	sb.append("</div>");
    	return sb;	
	}
	
    
    /////////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}
	
	
    /////////////////////////////////////
	public String getWidth() {
		return width;
	}
	public void setWidth(String newValue) {
		this.width = newValue;
	}
	
	
}
