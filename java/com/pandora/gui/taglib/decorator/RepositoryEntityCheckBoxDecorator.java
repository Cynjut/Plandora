package com.pandora.gui.taglib.decorator;

import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.helper.HtmlUtil;

public class RepositoryEntityCheckBoxDecorator extends ColumnDecorator {

	public static final String REPOSITORY_ENTITY_ID = "REPOSITORY_ENTITY_ID";
	
	private HttpSession session;
	
	public String decorate(Object columnValue) {
		RepositoryDelegate rdel = new RepositoryDelegate();
		String response = "&nbsp;";
		RepositoryFileTO item = (RepositoryFileTO)getObject();
		
		if (!item.getIsDirectory().booleanValue()) {

			if (session==null) {
				session = super.getSession();	
			}
			
			String entityId = (String)session.getAttribute(REPOSITORY_ENTITY_ID);
			
			if (entityId!=null && !entityId.trim().equals("")) {
				
				boolean isRelated = item.checkEntity(entityId);
				
				String path = item.getPath();				
				try {
					if (item.getPlanning()!=null) {
						ProjectTO pto = (ProjectTO)item.getPlanning();
						path = rdel.getExtractPath(pto, path);
					}					
					
					String encoding = SystemSingleton.getInstance().getDefaultEncoding();					
					response = HtmlUtil.getChkBox(isRelated, item.getPath(), "RPS", false, 
							"javascript:clickCheckBoxRepository('" + URLEncoder.encode(path, encoding) + "', '" + entityId + "');");
				} catch (Exception e) {
					e.printStackTrace();
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


