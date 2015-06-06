package com.pandora.gui.taglib.decorator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RepositoryFileTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.helper.HtmlUtil;

public class RepositoryEntityRadioBoxDecorator extends ColumnDecorator {

	public static final String REPOSITORY_SELECTED_PATH = "REPOSITORY_SELECTED_PATH";

	private HttpSession session;
	
	public String decorate(Object columnValue) {
		String response = "&nbsp;";
		RepositoryFileTO item = (RepositoryFileTO)getObject();

		if (item.getIsDirectory().booleanValue() && !item.getName().equals("..")) {
			
			if (session==null) {
				session = super.getSession();	
			}
			
			if (session!=null) {
				String entityId = (String)session.getAttribute(RepositoryEntityCheckBoxDecorator.REPOSITORY_ENTITY_ID);
				String path = (String)session.getAttribute(REPOSITORY_SELECTED_PATH);
				
				if (entityId!=null && !entityId.trim().equals("")) {
					try {
						
						boolean related = path!=null && path.equals(item.getPath());
						
						String encoding = SystemSingleton.getInstance().getDefaultEncoding();									
						response = HtmlUtil.getRadioBox(related, item.getPath(), false, 
								"javascript:clickRadioBoxRepository('" + URLEncoder.encode(item.getPath(), encoding) + "', '" + entityId + "');");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					response = "&nbsp;";
				}				
			} else {
				response = "&nbsp;";
			}
		}
		return response;
	}
	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}
	
	public void setSession(HttpSession newValue){
		this.session = newValue;
	}
}


