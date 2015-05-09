package com.pandora.gui.taglib.discussiontopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Locale;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.DiscussionTopicTO;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.HtmlUtil;


public class DiscussionTopic extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /** The id of data collection from http session. This collection 
     * contain all the attachments objects that should be displayed by tag lib. */
    private String collection;

    private String action;

    /** The default forward after discussion topic insertion */
    private String forward;
    
    /** The form name related with current Discussion Topic */
    private String name;
    
    
    private HashMap hm = new HashMap();
    String postedBy = "";
    String postedAt = "";
    String response = "";
    String sendPost = "";
    
    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("");       
        try {
        	
            JspWriter out = pageContext.getOut();

            String topicTitle = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.topic", null);           
            String initTopic = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.initDiscussion", null);
            sendPost = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.send", null);            
           	pageContext.getSession().setAttribute("REPLY_FORWARD", forward);
            
            //get all topics from http session
        	hm = new HashMap();            
            Vector list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            	
            buff.append("<table border=\"0\" width=\"100%\">\n");
            buff.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");
            buff.append("<tr class=\"formBody\">\n");
            buff.append("   <td><b>" + topicTitle + "</b></td>\n");
            buff.append("</tr>\n");

            //start a new discussion...
        	GeneralStrutsForm frm = (GeneralStrutsForm)pageContext.getSession().getAttribute(this.getName());
            DiscussionTopicTO dtRoot = new DiscussionTopicTO("");
            dtRoot.setPlanningId(frm.getId());
            buff.append("<tr class=\"formBody\">\n");
            buff.append(this.getReplySnippet(dtRoot, initTopic) + "\n");
            buff.append("</tr></table>\n");

	        if (list!=null && list.size()>0) {
	            
	            postedBy = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.posted", null);
	            postedAt = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.postedAt", null);
	            response = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.formForum.response", null);
	            	            
	            //agroup the topics based to the parent id
	            Iterator i = list.iterator();
	            while(i.hasNext()) {
	                DiscussionTopicTO dtto = (DiscussionTopicTO)i.next();
	                if (dtto.getParentTopic()!=null) {
		    			ArrayList container = null;
		    			String key = dtto.getParentTopic().getId();
		    			container = (ArrayList)hm.get(key);
		    			if (container==null) {
		    				container = new ArrayList();
		    				hm.put(key, container);				
		    			}
		    			container.add(dtto);
	                }
	            }
	            
	            int level = 0;
	            i = list.iterator();
	            while(i.hasNext()) {
	                DiscussionTopicTO dtto = (DiscussionTopicTO)i.next();
	                if (dtto.getParentTopic()==null) {
	                	buff.append(this.getTopicTable(dtto, level));
	                }
	            }
	        }
	        
            out.println(buff.toString());
           
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Attachment tag lib error", e);
        }
            
        return SKIP_BODY;
    }    
    
    private StringBuffer getTopicTable(DiscussionTopicTO dtto, int level){
    	StringBuffer buff = new StringBuffer("&nbsp;");
    	UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
    	Locale loc =  uto.getLocale();
    	
    	//create the identation before topic
    	String identation = "" + (level*50);
    	
    	buff.append("<table border=\"0\">\n");	                	
        buff.append("<tr class=\"tableRowOdd\">");        
        buff.append("   <td rowspan=\"4\" width=\"" + identation + "\">&nbsp;</td>");
        buff.append("   <td class=\"formLabel\" rowspan=\"4\" align=\"left\" valign=\"top\" width=\"3\" >&nbsp;</td> \n");        
        buff.append("	<td class=\"tableCell\" colspan=\"3\" align=\"left\" valign=\"top\"><i>" + 
        		this.postedBy + " " + dtto.getUser().getUsername() + " " + this.postedAt + " " + 
        		DateUtil.getDateTime(dtto.getCreationDate(), loc, 2, 2) + "</i></td>\n");
        buff.append("</tr>");

        String content = dtto.getContent();
        content = content.replaceAll("\n", "</br>");
        
        buff.append("<tr class=\"tableRowOdd\">");
        buff.append("	<td width=\"40\" rowspan=\"2\" valign=\"top\"><img width=\"33\" " + HtmlUtil.getHint(dtto.getUser().getUsername()) + " height=\"40\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" + dtto.getUser().getId() + "\"></td>");
        buff.append("	<td colspan=\"2\" align=\"justify\" class=\"code\" align=\"center\" valign=\"top\">" + content + "</td>\n");
        buff.append("</tr>");
        
        buff.append("<tr class=\"tableRowOdd\">");
        //buff.append("	<td>&nbsp;</td>");
        buff.append(this.getReplySnippet(dtto, this.response));        
        buff.append("	<td width=\"80\">&nbsp;</td>");
        buff.append("</tr>");
        
        buff.append("<tr class=\"gapFormBody\"><td colspan=\"3\">&nbsp;</td></tr>\n");
        
        buff.append("</table>");  
        
        level++;
        
		ArrayList container = null;
		container = (ArrayList)hm.get(dtto.getId());
		if (container!=null) {
            Iterator i = container.iterator();
            while(i.hasNext()) {
                DiscussionTopicTO child = (DiscussionTopicTO)i.next();
                buff.append(getTopicTable(child, level));                
            }
		}
		
        return buff;
    }

	private String getReplySnippet(DiscussionTopicTO dtto, String hint) {
			return "<td width=\"450\" align=\"left\"><a href=\"javascript:showHide('topic#" + this.getTopicKey(dtto) + "');\">" +
					        "<img border=\"0\" " + HtmlUtil.getHint(hint) + " src=\"../images/replypost.png\" ></a>" +
        					"<div id=\"topic#" + this.getTopicKey(dtto) + "\">" +
        						"</br><textarea name=\"reply#" + this.getTopicKey(dtto) + "\" cols=\"60\" rows=\"2\" class=\"textBox\"></textarea>" +
        						"</br><a href=\"javascript:replyPost('" + this.name + "', 'reply#" + this.getTopicKey(dtto) + "');\"><i>[" + sendPost + "]</i></a>" +
        					"</div>" +
    						"<script>javascript:showHide('topic#" + this.getTopicKey(dtto) + "');</script>" +
        			"</td>";
	}
    
    
    private String getTopicKey(DiscussionTopicTO dtto){
    	return dtto.getId() + "#" + dtto.getPlanningId();
    }
    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    }

    
    ///////////////////////////////////////////////    
	public String getAction() {
		return action;
	}
	public void setAction(String newValue) {
		this.action = newValue;
	}        
	
	
    ///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }    

    
    ///////////////////////////////////////////////             
    public String getForward() {
		return forward;
	}
	public void setForward(String newValue) {
		this.forward = newValue;
	}

}
