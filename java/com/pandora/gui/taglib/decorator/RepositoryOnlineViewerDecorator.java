package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.convert.Converter;
import com.pandora.delegate.ConverterDelegate;
import com.pandora.helper.HtmlUtil;

public class RepositoryOnlineViewerDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		try {
			RepositoryFileTO item = (RepositoryFileTO)getObject();
			if (item!=null && item.getIsDirectory()!=null && !item.getIsDirectory().booleanValue()) {
				
				ProjectTO pto = (ProjectTO)item.getPlanning();
				
				ConverterDelegate cdel = new ConverterDelegate();
				Converter converter = cdel.getClass(item);
				
				if (converter!=null) {
					String newPath = "";
					String rootUrl = pto.getRepositoryURL();
					if (item.getPath()!=null && !item.getPath().trim().equals("")) {
						int idx = item.getPath().indexOf(rootUrl) + 1;
						if (idx>0 && !rootUrl.equals("")) {
							newPath = item.getPath().substring(idx + rootUrl.length());	
						} else {
							newPath = item.getPath();
						}
					}

					String altValue = this.getBundleMessage("label.formRepository.viewer.desc");
				    image =  "<a class=\"gridLink\" href=\"javascript:void(0);\" onclick=\"showOnlineViewer('"; 
				    image += pto.getId() + "', '" + converter.getOutputFormat() + "', '" + newPath; 
				    image += "');\" border=\"0\"> \n";			
				    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/search.gif\" >";
				    image += "</a>";			
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
