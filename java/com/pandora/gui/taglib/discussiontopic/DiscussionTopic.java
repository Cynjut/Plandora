package com.pandora.gui.taglib.discussiontopic;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.pandora.DiscussionTopicTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;


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
    
    /** The entity id related with current Discussion Topic */
    private String entityId;

    
    
    @SuppressWarnings("unchecked")
	public int doStartTag() {
        StringBuffer buff = new StringBuffer("");       
        try {

            JspWriter out = pageContext.getOut();

           	pageContext.getSession().setAttribute("REPLY_FORWARD", forward);
        	UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        	
            //get all topics from http session           
            Vector<DiscussionTopicTO> list = (Vector)pageContext.getSession().getAttribute(this.getCollection());
            buff.append(this.getTitleContent(uto, pageContext.getSession()));

	        if (list!=null && list.size()>0) {
	            buff.append(this.getTopicContent(list, uto));
	        }
	        
            out.println(buff.toString());
           
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Attachment tag lib error", e);
        }
            
        return SKIP_BODY;
    }


	public StringBuffer getTopicContent(Vector<DiscussionTopicTO> list, UserTO uto) {
		StringBuffer buff = new StringBuffer();
		
        //group the topics based to the parent id
		HashMap<String, ArrayList<DiscussionTopicTO>> hm = this.getTopicHash(list);
		
		int level = 0;
		Iterator<DiscussionTopicTO> i = list.iterator();
		while(i.hasNext()) {
		    DiscussionTopicTO dtto = (DiscussionTopicTO)i.next();
		    if (dtto.getParentTopic()==null) {
		    	buff.append(this.getTopicTable(dtto, level, hm, uto));
		    }
		}
		
		return buff;
	}
    

	private HashMap<String, ArrayList<DiscussionTopicTO>> getTopicHash(Vector<DiscussionTopicTO> list) {
		HashMap<String, ArrayList<DiscussionTopicTO>> hm = new HashMap<String, ArrayList<DiscussionTopicTO>>();
		Iterator<DiscussionTopicTO> i = list.iterator();
		while(i.hasNext()) {
		    DiscussionTopicTO dtto = i.next();
		    if (dtto.getParentTopic()!=null) {
				ArrayList<DiscussionTopicTO> container = null;
				String key = dtto.getParentTopic().getId();
				container = (ArrayList<DiscussionTopicTO>)hm.get(key);
				if (container==null) {
					container = new ArrayList<DiscussionTopicTO>();
					hm.put(key, container);				
				}
				container.add(dtto);
		    }
		}
		return hm;
	}
	

	public StringBuffer getTitleContent(UserTO uto, HttpSession session) {
		StringBuffer buff = new StringBuffer();
        String topicTitle = uto.getBundle().getMessage(uto.getLocale(), "label.formForum.topic");           
        String initTopic = uto.getBundle().getMessage(uto.getLocale(), "label.formForum.initDiscussion");           
		
		buff.append("<table border=\"0\" width=\"100%\">\n");
		buff.append("<tr class=\"gapFormBody\"><td>&nbsp;</td></tr>\n");
		buff.append("<tr class=\"formBody\">\n");
		buff.append("   <td><b>" + topicTitle + "</b></td>\n");
		buff.append("</tr>\n");

		//start a new discussion...
		GeneralStrutsForm frm = (GeneralStrutsForm)session.getAttribute(this.getName());
		DiscussionTopicTO dtRoot = new DiscussionTopicTO("");
		
		if (entityId==null || entityId.trim().equals("")) {
			entityId = "id";
		}
		String eid = this.getIdFromForm(entityId, frm);
		dtRoot.setPlanningId(eid);
		
		buff.append("<tr class=\"formBody\">\n");
		buff.append(this.getReplySnippet(dtRoot, initTopic, uto) + "\n");
		buff.append("</tr></table>\n");
		
		return buff;
	}   
	
    
    private String getIdFromForm(String entityIdName, GeneralStrutsForm frm) {
    	String response = null;
        try {    
        	Object methodReturn = null;
            for (Method method : frm.getClass().getMethods()) {  
                String m = method.getName();   
                if (m.equals("get" + entityIdName.substring(0,1).toUpperCase() + entityIdName.substring(1))) {  
                	methodReturn = method.invoke(frm, new Object[]{});
                	response = methodReturn+"";
                	break;
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return response;
	}


	private StringBuffer getTopicTable(DiscussionTopicTO dtto, int level, 
    		HashMap<String, ArrayList<DiscussionTopicTO>> hm, UserTO uto){
    	
    	StringBuffer buff = new StringBuffer("&nbsp;");
    	
    	//create a gap before topic ("identation")
    	String identation = "" + (level*50);

    	Locale loc =  uto.getLocale();
        String postedBy = uto.getBundle().getMessage(loc, "label.formForum.posted");
        String response = uto.getBundle().getMessage(loc, "label.formForum.response");
    	
    	buff.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"1\">\n");
        buff.append("<tr class=\"tableRowOdd\">");        
        buff.append("   <td rowspan=\"4\" width=\"" + identation + "\">&nbsp;</td>");
        buff.append("   <td class=\"formLabel\" rowspan=\"4\" align=\"left\" valign=\"top\" width=\"3\" >&nbsp;</td> \n");        
        buff.append("	<td class=\"tableCell\" colspan=\"3\" align=\"left\" valign=\"top\"><i>" + 
        		postedBy + " " + dtto.getUser().getUsername() + " " + 
        		this.getPostTime(uto, dtto.getCreationDate()) + "</i></td>\n");
        buff.append("</tr>");

        String content = dtto.getContent();
        content = content.replaceAll("\n", "</br>");
        
        buff.append("<tr class=\"tableRowOdd\">");
        buff.append("	<td width=\"40\" rowspan=\"2\" valign=\"top\"><img width=\"33\" " + HtmlUtil.getHint(dtto.getUser().getUsername()) + " height=\"40\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" + dtto.getUser().getId() + "&ts=" +DateUtil.getNow().toString() + "\"></td>");
        buff.append("	<td colspan=\"2\" align=\"justify\" class=\"code\" align=\"center\" valign=\"top\">" + content + "</td>\n");
        buff.append("</tr>");
        
        buff.append("<tr class=\"tableRowOdd\">");
        buff.append(this.getReplySnippet(dtto, response, uto));        
        buff.append("	<td width=\"80\">&nbsp;</td>");
        buff.append("</tr>");
        
        buff.append("<tr class=\"gapFormBody\"><td colspan=\"3\">&nbsp;</td></tr>\n");
        
        buff.append("</table>");  
        
        if (level<4){
        	level++;
        }
        
		ArrayList<DiscussionTopicTO> container = null;
		container = (ArrayList<DiscussionTopicTO>)hm.get(dtto.getId());
		if (container!=null) {
            Iterator<DiscussionTopicTO> i = container.iterator();
            while(i.hasNext()) {
                DiscussionTopicTO child = i.next();
                buff.append(getTopicTable(child, level, hm, uto));                
            }
		}
		
        return buff;
    }
    
    public String getPostTime(UserTO uto, Timestamp postDate){
    	long DAY_MILI = 86400000;
    	long HOUR_MILI = 3600000;
    	String response = "";
    	long interval = -1;
    	
    	if (postDate!=null && uto!=null && uto.getBundle()!=null) {
    		Locale loc = uto.getLocale();
            
    		String yestLbl = uto.getBundle().getMessage(loc, "label.formForum.yesterday");
    		String atLbl = uto.getBundle().getMessage(loc, "label.formForum.postedAt");
            String agoLbl = uto.getBundle().getMessage(loc, "label.formForum.postedAgo");
            String minuteLbl = uto.getBundle().getMessage(loc, "label.formForum.postedMin");
            String hourLbl = uto.getBundle().getMessage(loc, "label.formForum.postedHour");
    		
    		interval = DateUtil.getNow().getTime() - postDate.getTime();
    		if (interval>0) {
        		if (interval >= DAY_MILI) {
        			Timestamp yesterday = DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -1);
        			yesterday = DateUtil.getDate(yesterday, true);
        			if (yesterday.before(postDate)) {
        				response = yestLbl + " " + DateUtil.getDateTime(postDate, "HH:mm:ss");
        			} else {
        				response = atLbl + " " + DateUtil.getDateTime(postDate, loc, 2, 2);
        			}

        		} else if (interval < HOUR_MILI) {
        			int minute = (int)((interval / 1000) / 60);
        			if (minute<1) {
        				minute = 1;
        			}
        			response = minute + " " + minuteLbl + " " + agoLbl;
        		} else {
        			response = (((interval / 1000) / 60) / 60) + " " + hourLbl + " " + agoLbl;
        		}    			
    		}
    	}
    	return response;
    }
    
	private String getReplySnippet(DiscussionTopicTO dtto, String hint, UserTO uto) {
        String sendPost = uto.getBundle().getMessage(uto.getLocale(), "label.formForum.send");
        String content = "<td width=\"450\" align=\"left\">";
        	
        if (dtto.getId()!=null && !dtto.getId().equals("") && dtto.getUser()!=null && 
        		uto!=null && dtto.getUser().getId().equals(uto.getId())) {
            String removePost = uto.getBundle().getMessage(uto.getLocale(), "label.grid.remove");
            String confirm = uto.getBundle().getMessage(uto.getLocale(), "message.formForum.confirmRemoveTopic");
        	content = content.concat("<a href=\"javascript:removePost('" + this.name + "', '" + 
        			dtto.getId() + "', '" + confirm + "');\"><img border=\"0\" " + HtmlUtil.getHint(removePost) +
        			" src=\"../images/delpost.png\" /></a>&nbsp;&nbsp;&nbsp;");        	
        }

        content = content.concat("<a href=\"javascript:showHide('topic#" + this.getTopicKey(dtto) + "');\">" +
				    			 "<img border=\"0\" " + HtmlUtil.getHint(hint) + " src=\"../images/replypost.png\" ></a>" +
				    			 "<div id=\"topic#" + this.getTopicKey(dtto) + "\">" +
				    			 "</br><textarea name=\"reply#" + this.getTopicKey(dtto) + "\" cols=\"60\" rows=\"2\" class=\"textBox\"></textarea>" +
				    			 "</br><a href=\"javascript:replyPost('" + this.name + "', 'reply#" + this.getTopicKey(dtto) + "');\"><i>[" + sendPost + "]</i></a>" +
				    			 "</div>" +
				    			 "<script>javascript:showHide('topic#" + this.getTopicKey(dtto) + "');</script></td>");         
        
		return  content;
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


    ///////////////////////////////////////////////
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String newValue) {
		this.entityId = newValue;
	}
	
	

}
