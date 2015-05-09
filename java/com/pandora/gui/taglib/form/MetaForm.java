package com.pandora.gui.taglib.form;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;
import com.pandora.delegate.UserDelegate;

import com.pandora.helper.LogUtil;
import com.pandora.MetaFormTO;
import com.pandora.UserTO;
import com.pandora.RootTO;

public class MetaForm extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** The id of data collection from http session. This collection 
     * contain all objects that should be displayed by tag lib. */
    private String collection;
	
    /** The html 'class' (style sheet) that should be used by table data */
    private String styleTitle = null;
    
    /** The html 'class' (style sheet) that should be used by table data */
    private String styleBody = null;

    
    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
        
        try {
            JspWriter out = pageContext.getOut();

            //get all relation from http session
            Vector list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            UserTO user = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);		
            boolean permission = false;
            
            if (list!=null && list.size()>0) {
                String title = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.customForm.title", null);
            	buff.append(this.getHeader(title));
            	
            	Iterator i = list.iterator();
            	while(i.hasNext()) {
            		MetaFormTO mto = (MetaFormTO)i.next();

            		if (this.checkPermission(user, mto)) {
            			permission = true;
                		String text = "<tr class=\"" + styleBody + "\">" +
  				  			"<td width=\"30\">&nbsp;</td>" +
  				  			"<td>" +
  				  			"<a href=\"../do/manageCustomForm?operation=prepareForm&metaFormId=" + mto.getId() + "\">" + mto.getName() + "</a>" +
  				  			"</td></tr>";
                		buff.append(text);
            		}	
            	}
            	buff.append(this.getFooter());
            } else {
                buff = new StringBuffer();
            }
            
            if (!permission) {
            	buff = new StringBuffer();
            }
            
            out.println(buff.toString());         
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Relationship tag lib error", e);
        }
            
        return SKIP_BODY;
    }  
    
    
    private String getHeader(String title) {
    	return HeaderFooterGrid.printHeaderBeforeBody("100%", true).toString() + "&nbsp;" +
   				title + HeaderFooterGrid.printHeaderAfterBody(true).toString() + 
   				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"95%\">";
    }

    private String getFooter(){
    	return "</table>" + HeaderFooterGrid.printFooterBeforeBody().toString() + "&nbsp;" + 
    		   HeaderFooterGrid.printFooterAfterBody().toString();
    }
    
    private boolean checkPermission(UserTO uto, MetaFormTO mfto){
    	boolean response = false;
    	
    	if (uto.getUsername().equalsIgnoreCase(RootTO.ROOT_USER)) {
    		response = true;
    	} else {
        	String permission = uto.getPermission();
        	if (permission!=null) {
        		response = (permission.indexOf("[" + mfto.getId() + "]")>-1);
        	}
    		
    	}
    	
    	return response;
    }
    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    } 
    
    ///////////////////////////////////////////////    
    public String getStyleTitle() {
        return styleTitle;
    }
    public void setStyleTitle(String newValue) {
        this.styleTitle = newValue;
    }
    
    ///////////////////////////////////////////////      
    public String getStyleBody() {
        return styleBody;
    }
    public void setStyleBody(String newValue) {
        this.styleBody = newValue;
    }    
    
}
