package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.helper.HtmlUtil;

public class RepositoryDeleteItemDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String response = "";
		String altValue = this.getBundleMessage("label.formRepository.del.tooltip");
		try {
			RepositoryDelegate rdel = new RepositoryDelegate();
			RepositoryFileTO item = (RepositoryFileTO)getObject();		
			
			if (item!=null && item.getPath()!=null && !item.getPath().equals("")) {
				
				ProjectTO pto = (ProjectTO)item.getPlanning();
				String rootUrl = pto.getRepositoryURL();
				String newPath = formatPath(item.getPath(), rootUrl);
				
				boolean rem = rdel.canRemoveFile(pto);
				
				if (rem) {
					String image = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/remove.gif\" >";
					response = "<a href=\"javascript:removeRepItem('" + pto.getId() + "', '" + newPath + "');\">" + image + "</a>";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;		
	}
	

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}
	

	public String contentToSearching(Object columnValue) {
		return "";
	}
	
	public String formatPath(String path, String rootUrl){
		String newPath = "";
		if (path!=null && !path.trim().equals("")) {
			if (rootUrl!=null) {
				newPath = path.replaceAll(rootUrl, "");	
			} else {
				newPath = path;
			}
						
			if (newPath.startsWith("/")) {
				newPath = newPath.substring(1);
			}
			
		}
		return newPath;
	}
	

}
