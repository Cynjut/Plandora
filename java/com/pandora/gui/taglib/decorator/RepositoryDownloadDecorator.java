package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.helper.HtmlUtil;

public class RepositoryDownloadDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		try {
			String enable = SystemSingleton.getInstance().getPublicArtifactDownload(); 
			if (enable!=null && enable.trim().equalsIgnoreCase("on")) {
				RepositoryFileTO item = (RepositoryFileTO)getObject();
				if (item!=null && item.getIsDirectory()!=null && !item.getIsDirectory().booleanValue()) {

					String icon = "downloff.gif";
					String possibleAction = "ON";
					RepositoryDelegate del = new RepositoryDelegate();				
					RepositoryFileProjectTO rfp = del.getFileFromDB((ProjectTO)item.getPlanning(), item.getPath());
					if (rfp!=null) {
						if (rfp.getIsDownloadable()!=null && rfp.getIsDownloadable().booleanValue()) {
							icon = "downlon.gif";
							possibleAction = "OFF";
						}
						
						String altValue = this.getBundleMessage("label.formRepository.download.tooltip");
					    image =  "<a class=\"gridLink\" href=\"javascript:void(0);\" onclick=\"displayMessage('../do/showRepositoryViewer?operation=showDownloadPopup&projectId=" + 
					    rfp.getProject().getId() + "&id=" + rfp.getId() + "&genericTag=" + possibleAction + "', 450, 180);\" border=\"0\"> \n"; 
					    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/" + icon+ "\" />";
					    image += "</a>";								
					}				
				}							
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			image = "&nbsp;";
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
