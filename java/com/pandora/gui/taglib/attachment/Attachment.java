package com.pandora.gui.taglib.attachment;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.AttachmentTO;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.taglib.decorator.AttachmentGridDownloadDecorator;
import com.pandora.gui.taglib.decorator.AttachmentGridTypeDecorator;
import com.pandora.helper.LogUtil;

/**
 * 
 */
public class Attachment extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** The form name related with current Attachment */
    private String name;

    private String removedForward;

    private String title;
    
    /** The id of data collection from http session. This collection 
     * contain all the attachments objects that should be displayed by tag lib. */
    private String collection;
    
    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
        
        try {
            JspWriter out = pageContext.getOut();

            //get all attachments from http session
            Vector list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            GeneralStrutsForm frm = (GeneralStrutsForm)pageContext.getSession().getAttribute(this.getName());
            
            if (frm==null) { 
                buff.append("");
            } else {
                

                buff = new StringBuffer();
                
                if (list!=null && list.size()>0) {
                    
                    String tagTitle = "";
                    if (title==null || title.trim().equals("")) {
                        tagTitle = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "title.attachTagLib.List", null);
                    } else {
                        tagTitle = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, title, null);
                    }

                    String remove = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.grid.remove", null);
                    String gridFileName = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formAttachment.name", null);
                    String gridContType = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formAttachment.type", null);
                    String gridCommemt = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formAttachment.comment", null);

                    buff.append("<tr class=\"formBody\">\n");
                    buff.append("   <td><b>" + tagTitle + "</b></td>\n");
                    buff.append("</tr>\n");
                    
                    
                    buff.append("<tr class=\"formBody\">\n");
                    buff.append("<td>\n");
                    buff.append("<table class=\"table\" width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"2\">\n");
                    buff.append("  <tr class=\"rowHighlight\">\n");
                    buff.append("     <th width=\"2%\" class=\"tableCellHeader\">&nbsp;</th>\n");
                    buff.append("     <th width=\"20%\" class=\"tableCellHeader\">" + gridFileName + "</th>\n");
                    buff.append("     <th width=\"15%\" class=\"tableCellHeader\">" + gridContType + "</th>\n");
                    buff.append("     <th class=\"tableCellHeader\">" + gridCommemt + "</th>\n");
                    buff.append("     <th width=\"2%\" class=\"tableCellHeader\">&nbsp;</th>\n");
                    buff.append("  </tr>");

                    Iterator i = list.iterator();
                    while(i.hasNext()) {
                        AttachmentTO ato = (AttachmentTO)i.next();
                        if (!ato.getStatus().equals(AttachmentTO.ATTACH_STATUS_REMOVED)) {

                            String removeConfirm = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "message.confirmRemoveAttachment", null);
                        	AttachmentGridDownloadDecorator decDownload = new AttachmentGridDownloadDecorator();
                        	AttachmentGridTypeDecorator decPicture = new AttachmentGridTypeDecorator();
                        	decDownload.initRow(ato, -1, -1);
                        	decPicture.initRow(ato, -1, -1);
                        	
                            buff.append("  <tr class=\"tableRowEven\">");
                            buff.append("     <td class=\"tableCell\" align=\"center\" valign=\"top\">" + decPicture.decorate(ato.getId()) + "</td>\n");
                            buff.append("     <td class=\"tableCell\" align=\"center\" valign=\"top\">" + decDownload.decorate(ato.getId(), name) + "</td>\n");
                            buff.append("     <td class=\"tableCell\" align=\"center\" valign=\"top\">" + ato.getType() + "</td>\n");
                            buff.append("     <td class=\"tableCell\" align=\"left\" valign=\"top\">" + ato.getComment() + "</td>\n");
                            buff.append("     <td class=\"tableCell\" align=\"left\" valign=\"top\">\n");
                            if (removedForward!=null && !removedForward.trim().equals("")) {
                                buff.append("         <a href=\"javascript:removeAttachment('" + ato.getId() + "','" + name + "', '" + removedForward + "', '" + removeConfirm + "');\" border=\"0\">\n");
                                buff.append("         <img border=\"0\" title=\"" + remove + "\" alt=\"" + remove + "\" src=\"../images/remove.gif\" ></a>\n");                                
                            } else {
                                buff.append("     	  &nbsp;\n");    
                            }
                            buff.append("     </td>");
                            buff.append("  </tr>");
                        }
                    }
                    buff.append("</table></td></tr>");                    
                }
            }
            
            out.println(buff.toString());         
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Attachment tag lib error", e);
        }
            
        return SKIP_BODY;
    }    
    
    
    ///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    ///////////////////////////////////////////////       
    public String getRemovedForward() {
        return removedForward;
    }
    public void setRemovedForward(String newValue) {
        this.removedForward = newValue;
    }
    
    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    }    
    
    
    ///////////////////////////////////////////////
    public String getTitle() {
        return title;
    }
    public void setTitle(String newValue) {
        this.title = newValue;
    }
}
