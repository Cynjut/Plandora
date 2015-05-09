package com.pandora.gui.taglib.form;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;

/**
 * 
 */
public class Forms extends TagSupport {
    
	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String table;
    
    private String tablePkList;
    
    private String lableList;
    
    private String collection;
    
    private String styleTitle;

    private String styleBody;
    
    private String titleWidth = "200";
    
    private String styleForms;

    
    private Vector formFields;
    
    /**
     * This event is performed when the tagLib starts
     */
    public int doStartTag() {      
        try {

            HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
            
            //if the meta data was not loaded yet... 
            if (formFields==null) {
                this.getFormFields(this.getTable());
            }
            
            //TODO obter a lista com todos os registros e armazenar cursor...
            
            if (req.getParameter(this.getCommandParam()) != null) {
            
            }
                    
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Forms tag lib error", e);            
        }
            
        return SKIP_BODY;
    }
    
    
    /**
     * This event is performed when the tagLib ends. Here the taglib get all
     * the information collected and display using the html format.
     */
    public int doEndTag() throws JspException {
        StringBuffer buf = new StringBuffer();
        try {        
            JspWriter out = pageContext.getOut();
            buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
            
            //draw the fields....
            if (this.getFormFields()!=null) {
                Iterator i = this.getFormFields().iterator();
                while(i.hasNext()) {
                    buf.append("<tr>\n");                
                    FormField ff = (FormField)i.next();
                    
                    //TODO com os dados ref. ao cursor, injeto o valor do campo dentro de cada form field
                    
                    //display title...
                    buf.append("<td width=\"" + this.getTitleWidth() + "\" class=\"" + 
                            this.getStyleTitle() + "\">" + getLabelFromList(ff) + ":&nbsp;</td>\n"); 
                    buf.append("<td class=\"" + this.getStyleBody() + "\">");
                    
                    //display field content...                    
                    buf.append(this.getHtmlObject(ff));
                    
                    buf.append("</td>\n");
                    buf.append("</tr>\n");
                }                
            } else {
                buf.append("The form cannot display the fields using table: " + this.getTable() + "\n");
            }
            buf.append("</table>\n");
            
            out.write(buf.toString());
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Forms tag lib error", e);
        }
        
        return EVAL_PAGE;
    }
    
    /**
     * Display the html objects based on the type of the form field.
     * @param ff
     * @return
     */
    private StringBuffer getHtmlObject(FormField ff){
        StringBuffer response = new StringBuffer();
        
        String name = "field_" + ff.getLabel();
        if (ff.getType().equals(FormField.TYPE_STRING)){
            
            if (ff.getMaxlength() > 50) {
                response.append("<textarea name=\"" + name + "\" rows=\"3\" cols=\"80\" class=\"" +
                        this.getStyleForms() + "\">" + ff.getContent() + "</textarea>");
                
            } else {
                response.append("<input type=\"text\" name=\"" + name + "\" maxlength=\"" + ff.getMaxlength() + 
                        "\" value=\"" + ff.getContent() + "\" size=\"" + (ff.getMaxlength()/2) + "\" class=\"" + this.getStyleForms() + "\">");    
            }
               
        } else {
            //TODO se for do tipo numerico tem que fazer um campo que faca validacao de entrada...
            response.append("<input type=\"text\" name=\"" + name + "\" maxlength=\"" + ff.getMaxlength() + 
                    "\" value=\"" + ff.getContent() + "\" size=\"" + (ff.getMaxlength()/2) + "\" class=\"" + this.getStyleForms() + "\">");                
        }
        return response;
    }
    
    
    private String getLabelFromList(FormField ff) throws JspException{
        String response = ff.getLabel();
        int index = 0;
        String lableList = this.getLableList();
        
        if (lableList!=null && lableList.length()>0){
            
            //check if the content is into the resource bundle...
            int i = lableList.indexOf("@bundle");
            if (i>-1){
                String key = lableList.substring(0, i); 
                lableList = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, key, null);
            }
            
            StringTokenizer st = new StringTokenizer(lableList, "|");
            while(st.hasMoreTokens()) {
                index++;
                String token = st.nextToken();
                if (index==ff.getSequence()) {
                    response = token;
                    break;
                }                
            }
        }
        
        return response;
    }
    
    /**
     * Get into data base the meta data about a table
     * and create a list of FormFields objects
     * @throws BusinessException
     */
    private void getFormFields(String table) throws BusinessException {
        DbQueryDelegate query = new DbQueryDelegate();
        Vector metaFields = query.getMetaData(table);
        
        Iterator i = metaFields.iterator();
        while(i.hasNext()) {
            
            if (this.formFields==null) {
                this.formFields = new Vector();
            }
            
            FormField ff = new FormField();
            Vector item = (Vector)i.next();
            ff.setSequence(((Integer)item.get(0)).intValue());
            ff.setLabel((String)item.get(1));
            ff.setType((Integer)item.get(2));
            ff.setMaxlength(((Integer)item.get(3)).intValue());
            this.formFields.addElement(ff);
        }
    }
    
    
    /**
     * Return the http GET key in order to point the command 
     * selected into form
     * @return
     */
    private String getCommandParam(){
        return "command_" + this.getName();
    }
    
    
    //////////////////////////////////////////////
    public String getLableList() {
        return lableList;
    }
    public void setLableList(String newValue) {
        this.lableList = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getTable() {
        return table;
    }
    public void setTable(String newValue) {
        this.table = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getTablePkList() {
        return tablePkList;
    }
    public void setTablePkList(String newValue) {
        this.tablePkList = newValue;
    }
    
    //////////////////////////////////////////////        
    public Vector getFormFields() {
        return formFields;
    }
    public void setFormFields(Vector newValue) {
        this.formFields = newValue;
    }
    
    //////////////////////////////////////////////          
    public String getStyleBody() {
        return styleBody;
    }
    public void setStyleBody(String newValue) {
        this.styleBody = newValue;
    }
    
    //////////////////////////////////////////////          
    public String getStyleForms() {
        return styleForms;
    }
    public void setStyleForms(String newValue) {
        this.styleForms = newValue;
    }
    
    //////////////////////////////////////////////          
    public String getStyleTitle() {
        return styleTitle;
    }
    public void setStyleTitle(String newValue) {
        this.styleTitle = newValue;
    }
    
    //////////////////////////////////////////////          
    public String getTitleWidth() {
        return titleWidth;
    }
    public void setTitleWidth(String newValue) {
        this.titleWidth = newValue;
    }
}
