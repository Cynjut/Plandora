package com.pandora.gui.taglib.metafield;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.pandora.AdditionalFieldTO;
import com.pandora.AdditionalTableTO;
import com.pandora.MetaFieldTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 * This tag lib is used to show the specific GUI fields (meta fields)
 * for the requirement, task or project forms.
 */
public class MetaField extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** The form name related with current MetaField */
    private String name;
    
    /** The id of data collection from http session. This collection 
     * contain all the meta fields that should be displayed by tag lib. */
    private String collection;
        
    /** The html 'class' (style sheet) that should be used by table data */
    private String styleTitle = null;
    
    /** The html 'class' (style sheet) that should be used by table data */
    private String styleBody = null;

    /** The html 'class' (style sheet) that should be used by GUI objects (textBox, combo, etc) */
    private String styleForms = "textBox";

    /** The with (in pixels) for title column */
    private String titleWidth = "130";

    private String forward;
    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
        
        try {
            JspWriter out = pageContext.getOut();
            
            //get all meta fields from http session
            Vector list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
           	pageContext.getSession().setAttribute("META_FIELD_FORWARD", forward);
            
            //format each meta field in html output
            if (list!=null) {
                buff = new StringBuffer();
                buff.append("<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

                Iterator i = list.iterator();
                while(i.hasNext()) {
                    MetaFieldTO token = (MetaFieldTO)i.next();
                    if (token.isEnable()) {
                        buff.append("<tr>");
                        buff.append(this.getFormatedField(token, uto, pageContext.getSession()));
                        buff.append("</tr>");                        
                    }
                }
                
                buff.append("</table>");
            }
            
            out.println(buff.toString());
             
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "MetaField tag lib error", e);
        }
            
        return SKIP_BODY;
    }
    
    
    private StringBuffer getFormatedField(MetaFieldTO mfto, UserTO uto, HttpSession session) throws Exception{
        StringBuffer buff = new StringBuffer();
        
        //format the field's title 
        buff.append("<td width=\"" + this.getTitleWidth() + "\"");
        if (this.getStyleTitle()!=null) {
            buff.append("class=\"" + this.getStyleTitle() + "\"");    
        }
        buff.append(">");

        //format the body's content according meta field type
        if (mfto.getType().intValue()==MetaFieldTO.TYPE_TABLE) {

        	buff.append("&nbsp;</td><td>");
    		
        	Vector<AdditionalTableTO> tableValues = this.getMetaFieldCurrentTableValues(mfto);
            buff.append(mfto.getFormatedTable(this.getStyleForms(), tableValues, mfto, uto, name, false));            	
            
            //end of field's body
            buff.append("</td>");

        } else {
        	
            //format the field's title 
        	String title = HtmlUtil.getBundleString(mfto.getName(), session);
            buff.append(title + ":&nbsp;</td>");

            //format the field's body
            buff.append("<td ");
            if (this.getStyleBody()!=null) {
                buff.append("class=\"" + this.getStyleBody() + "\"");    
            }
            buff.append(">");

            String currValue = this.getMetaFieldCurrentValue(mfto, uto);        	
            buff.append(mfto.getFormatedField(this.getStyleForms(), currValue, uto, session));
            
            //end of field's body
            buff.append("</td>");
        }
        
        
        
        return buff;
    }

    
    /**
     * Return the value of meta field for a specific form.
     */
    private String getMetaFieldCurrentValue(MetaFieldTO mfto, UserTO uto){
        String value = null;
        AdditionalFieldTO afto = getRelatedAdditionalField(mfto);
        if (afto!=null){
        	if (mfto.getType()!=null && afto.getDateValue()!=null 
        			&& mfto.getType().intValue()==MetaFieldTO.TYPE_CALENDAR) {
        		
        		value = DateUtil.getDate(afto.getDateValue(), uto.getCalendarMask(), uto.getLocale());
        		
        	} else if (mfto.getType()!=null && afto.getNumericValue()!=null 
        			&& mfto.getType().intValue()==MetaFieldTO.TYPE_TEXT_BOX_NUMERIC) {
        		
        		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(uto.getLocale());
        		String[] options = mfto.getDomain().split("\\|");	
        		df.applyPattern(options[3]);
        		value =  (afto.getNumericValue() != null ? df.format(afto.getNumericValue()) : "" );
        	} else {
        		value = afto.getValue();	
        	}
        }
        
        return value;
    }


	private AdditionalFieldTO getRelatedAdditionalField(MetaFieldTO mfto) {
		
		//get the current form
        GeneralStrutsForm frm = (GeneralStrutsForm)pageContext.getSession().getAttribute(this.getName());
        
        //get the additional Field object that corresponding to specific meta field
        AdditionalFieldTO afto = frm.getAdditionalField(mfto.getId());
		return afto;
	}

    
    /**
     * Return the value of meta field for a specific form.
     */
    private Vector<AdditionalTableTO> getMetaFieldCurrentTableValues(MetaFieldTO mfto){
    	Vector<AdditionalTableTO> value = null;
        AdditionalFieldTO afto = getRelatedAdditionalField(mfto);
        if (afto!=null && mfto.getType().intValue()==MetaFieldTO.TYPE_TABLE){
            value = afto.getTableValues();
        }
        return value;
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
    
    ///////////////////////////////////////////////          
    public String getTitleWidth() {
        return titleWidth;
    }
    public void setTitleWidth(String newValue) {
        this.titleWidth = newValue;
    }
    
    ///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////////////             
    public String getStyleForms() {
        return styleForms;
    }
    public void setStyleForms(String newValue) {
        this.styleForms = newValue;
    }

    
    ///////////////////////////////////////////////  
	public String getForward() {
		return forward;
	}
	public void setForward(String newValue) {
		this.forward = newValue;
	}
    
    
}