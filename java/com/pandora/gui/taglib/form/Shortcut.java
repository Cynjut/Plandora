package com.pandora.gui.taglib.form;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;

public class Shortcut extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String property;
    
    private String fieldList; 
    

    public int doStartTag() {
        try {
            JspWriter out = pageContext.getOut();
        	UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        	
        	String content = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
        			"<tr><td width=\"10\">&nbsp;</td><td>&nbsp;</td>" + getHtmlShortcuts(uto, "10") + "<td width=\"20\">&nbsp;</td>" +
        					"<td class=\"shortcutText\" width=\"20\">" + this.getEDIIcon(uto, property) + "</td>" +
        					"<td class=\"shortcutText\" width=\"20\">" + this.getShortCutIcon(uto, property, fieldList) + "</td><td width=\"10\">&nbsp;</td></tr></table>";
            out.println(content);
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Shortcut tag lib error", e);
        }
        return SKIP_BODY;
    }

    
    public String getEDIIcon(UserTO uto, String gotoAfterSave) throws BusinessException {
    	String response = "&nbsp;";
    	UserDelegate udel = new UserDelegate();

		String enable = SystemSingleton.getInstance().getEDIDownload(); 
		if (enable!=null && enable.trim().equalsIgnoreCase("on")) {
	    	UserTO topRole = udel.getUserTopRole(uto);
	    	if (topRole instanceof LeaderTO || topRole instanceof ResourceTO) {
	        	String label = uto.getBundle().getMessage(uto.getLocale(), "title.rss");
	        	response = "<a href=\"javascript:displayMessage('../do/showRssPopup?operation=prepareForm&gotoAfterSave=" + gotoAfterSave + "', 580, 260);\">" +
	        					"<img border=\"0\" title=\"" + label + "\" alt=\"" + label + "\" style=\"vertical-align:middle\"src=\"../images/integ.png\" />"+
	        			"</a>";    		
	    	}
		}    	
    	
    	return response;
	}


	public String getShortCutIcon(UserTO uto, String gotoAfterSave, String fieldList){
    	String label = uto.getBundle().getMessage(uto.getLocale(), "label.shortcut.link");
    	return  "<a href=\"javascript:displayMessage('../do/showShortCutPopup?operation=prepareForm&gotoAfterSave=" + gotoAfterSave + "&shortcutURI=' + getShortcutURL('" + fieldList + "'), 450, 180);\">" +
    					"<img border=\"0\" title=\"" + label + "\" alt=\"" + label + "\" style=\"vertical-align:middle\"src=\"../images/shortcut.png\" />"+
    			"</a>";
    }


    public static String getHtmlShortcuts(UserTO uto, String additionalWidth){
		String response = "";
		String[] url = {"", "", "", "", "", "", "", "", "", ""};
		
		for (int i=0; i<10 ;i++) {
			url[i] = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_URL + (i+1));
			
			if (url[i]!=null && !url[i].equals("")) {
				
				String altValue = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_NAME + (i+1));
				
				String opening = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_OPEN + (i+1));
				if (opening == null || opening.trim().equals("")) {
					opening = "1";
				}
				
				String image = "table.png";
				String img = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_ICON + (i+1));
				if (img!=null && !img.trim().equals("")) {
					if (img.equals("1")) {
						image = "report.gif";
					} else if (img.equals("2")) {
						image = "bsc.gif";
					} else if (img.equals("3")) {
						image = "form.png";
					} else if (img.equals("4")) {
						image = "table.png";
					} else if (img.equals("5")) {
						image = "alert-task.gif";
					} else if (img.equals("6")) {
						image = "fav-task.gif";
					} else if (img.equals("7")) {
						image = "bomb.png";
					} else if (img.equals("8")) {
						image = "clock.png";
					}
				}
					
				response += "<td valign=\"middle\" align=\"right\" width=\"20\">";
				response += 	"<a href=\"javascript:goToForm('" + url[i] + "', " + opening + ");\" border=\"0\"> \n";
				response += 		"<img border=\"0\" " + HtmlUtil.getHint(altValue) + " align=\"center\" src=\"../images/" + image + "\" >";
				response += 	"</a>"; 				
				response += "</td>";
			}
		}
		
		if (!response.trim().equals("")){
			response += "<td width=\"" + additionalWidth + "\">&nbsp;</td>";				
		}
		
		return response;    	
    }

    
    
    /////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}


	/////////////////////////////////////
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}


	/////////////////////////////////////
	public String getFieldList() {
		return fieldList;
	}
	public void setFieldList(String newValue) {
		this.fieldList = newValue;
	}    
	
	
}
