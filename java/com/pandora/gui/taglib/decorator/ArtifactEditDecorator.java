package com.pandora.gui.taglib.decorator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RepositoryFileTO;
import com.pandora.bus.artifact.HtmlArtifactExport;
import com.pandora.helper.HtmlUtil;

public class ArtifactEditDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		RepositoryFileTO item = (RepositoryFileTO)getObject();
		if (item!=null && item.getArtifactTemplateType()!=null && item.getPlanning()!=null) {
			if (item.getArtifactTemplateType().equals(HtmlArtifactExport.class.getName())) {
				String altValue = this.getBundleMessage("label.artifactTag.edit");
				String path = item.getPath();
				try {
					path = URLEncoder.encode(path, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				String planningId = "";
				if (columnValue!=null) {
					planningId = columnValue+ ""; 
				}
				
				image =  "<a class=\"gridLink\" href=\"javascript:void(0);\" onclick=\"window.location='../do/manageArtifact?" +
						"operation=editArtifact&planningId=" + planningId + "&projectId="+ item.getPlanning().getId() + "&editPath=" + path + "&editPathRev=" + item.getRevision() + "'\"; border=\"0\"> \n"; 
			    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/edit.gif\" >";
			    image += "</a>";
			}			
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
