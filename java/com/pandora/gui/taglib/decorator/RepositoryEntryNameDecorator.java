package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;

public class RepositoryEntryNameDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		return decorate(columnValue, null);
	}

	
	public String decorate(Object columnValue, String tag) {
		String url = "&nbsp;";
		String forward = "navigate";
		RepositoryFileTO item = (RepositoryFileTO)getObject();

		if (item!=null) {
			ProjectTO pto = (ProjectTO)item.getPlanning();
			if (pto!=null) {
				String rootUrl = pto.getRepositoryURL();
				String content = columnValue+"";
				if (tag!=null) {
					if (!tag.startsWith("browse")) {
						if (item.getIsDirectory()!=null && item.getIsDirectory().booleanValue()) {	
							content = rootUrl + "/" + item.getPath() + "/";	
						} else {
							content = rootUrl + "/" + item.getPath();
						}						
					}
					forward = tag;
				}

				if (content!=null || !content.equalsIgnoreCase("null")) {

					String newPath = formatPath(item.getPath(), rootUrl);

					if (item.getIsDirectory()!=null && item.getIsDirectory().booleanValue()) {	
						if (forward!=null && forward.startsWith("browse")) {
							url =  "<a class=\"gridLink\" href=\"javascript:clickBrowseRepository('"+ newPath + "', '" + 
														pto.getId() + "');\" border=\"0\"> \n" + content + "</a>";																	
						} else {
							 url =  "<a class=\"gridLink\" href=\"../do/showRepositoryViewer?operation=" + forward + "&projectId=" + pto.getId() + "&path=" + newPath + "\" border=\"0\"> \n" + content + "</a>";										
						}						
					} else {
						if (forward!=null && forward.equals("browse")) {
							url =  content;																	
						} else {
							url =  "<a class=\"gridLink\" href=\"../do/showRepositoryViewer?operation=getFile&projectId=" + pto.getId() + "&path=" + newPath + "\" border=\"0\"> \n" + content + "</a>";										
						}						
					}
				}
			}
		}
		return url;
		
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}
	
	public String formatPath(String path, String rootUrl){
		String newPath = "";
		if (path!=null && !path.trim().equals("")) {
			
			//int idx = 0;
			//if (item.getPath().startsWith("/")) {
			//	idx = item.getPath().indexOf(rootUrl) + 1;	
			//} else {
			//	idx = item.getPath().indexOf(rootUrl);
			//}
			
			//if (idx>0) {
			//	newPath = item.getPath().substring(idx + rootUrl.length());	
			//} else {
			//	newPath = item.getPath();
			//}
			
			newPath = path.replaceAll(rootUrl, "");			
			if (newPath.startsWith("/")) {
				newPath = newPath.substring(1);
			}
			
		}
		return newPath;
	}
}
