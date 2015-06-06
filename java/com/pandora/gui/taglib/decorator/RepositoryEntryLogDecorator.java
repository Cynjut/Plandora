package com.pandora.gui.taglib.decorator;

import java.net.URLEncoder;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.helper.HtmlUtil;

public class RepositoryEntryLogDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		try {
			RepositoryFileTO item = (RepositoryFileTO)getObject();
			if(!item.getName().equals("..")) {
				String path = item.getPath();
				ProjectTO pto = (ProjectTO)item.getPlanning();
				
				if (pto!=null && pto.getRepositoryURL()!=null) {
					path = path.replaceAll(pto.getRepositoryURL(), "");	
				}
				
				if (path.startsWith("/")){
					path = path.substring(1);
				}
				String encoding = SystemSingleton.getInstance().getDefaultEncoding();					
				path = URLEncoder.encode(path, encoding);
				
				String projId = pto.getId();
				String rev = item.getRevision()+"";
				String altValue = super.getBundleMessage("label.formRepository.log.desc");
				
				image = "<a href=\"javascript:displayMessage('../do/showRepositoryViewer?operation=showLogFile&path=" + 
									path + "&projectId=" + projId + "&logrev=" + rev + "', 620, 460);\">" +
						"<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/repositorylog.png\" ></a>";				
			}

		} catch (Exception e){
			e.printStackTrace();
			image = "";
		}
		return image;
	}

	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return "";
	}

}
