package com.pandora.gui.taglib.decorator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.bus.artifact.HtmlArtifactExport;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.HtmlUtil;

public class ArtifactEditDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		RepositoryFileTO item = (RepositoryFileTO)getObject();
		if (item!=null && item.getPlanning()!=null && item.getPath()!=null && !item.getPath().equals("..")) {
			
			//get from db the information about the artifact template...
			if (item.getArtifactTemplateType()==null && item.getPath()!=null) {
				RepositoryDelegate rdel = new RepositoryDelegate();
				try {
					RepositoryFileProjectTO pf = rdel.getFileFromDB(((ProjectTO)item.getPlanning()), item.getPath());
					if (pf!=null) {
						item.setArtifactTemplateType(pf.getFile().getArtifactTemplateType());	
					}
				} catch (BusinessException e) {
					e.printStackTrace();
				}
			}
			
			if (item.getArtifactTemplateType()!=null && item.getArtifactTemplateType().equals(HtmlArtifactExport.class.getName())) {
				String altValue = this.getBundleMessage("label.artifactTag.edit");
				String path = item.getPath();
				try {
					if (item.getPlanning()!=null) {
						ProjectTO pto = (ProjectTO)item.getPlanning();
						if (pto!=null && pto.getRepositoryURL()!=null) {
							path = path.replaceAll(pto.getRepositoryURL(), "");		
						}
					}
					String encoding = SystemSingleton.getInstance().getDefaultEncoding();					
					path = URLEncoder.encode(path, encoding);
					
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
