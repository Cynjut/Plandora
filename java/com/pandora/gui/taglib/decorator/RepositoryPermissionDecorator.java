package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.HtmlUtil;

public class RepositoryPermissionDecorator extends ColumnDecorator {


	public String decorate(Object columnValue) {
		String response = "";
		String altValue = this.getBundleMessage("label.formRepository.perm.off");
		String src = "off.gif";
		try {			
			RepositoryFileTO item = (RepositoryFileTO)getObject();
			if (item!=null && item.getPath()!=null && !item.getPath().equals("") && 
					item.getIsDirectory()!=null && !item.getIsDirectory().booleanValue()) {
				
				RepositoryDelegate del = new RepositoryDelegate();				
				if (item!=null) {
					RepositoryFileProjectTO rfp = del.getFileFromDB((ProjectTO)item.getPlanning(), item.getPath());
					if (rfp!=null && rfp.getIsDisabled()!=null) {
						if (!rfp.getIsDisabled().booleanValue()) {
							altValue = this.getBundleMessage("label.formRepository.perm.on");
							src = "on.gif";
						}
					}				
				}				
				
				int idx = super.getListIndex();				
				String image = "<img border=\"0\" id=\"GRANT_" + idx + "\" " + 
								HtmlUtil.getHint(altValue) + " src=\"../images/" + src + "\" >";
				response = "<a href=\"javascript:changeGrant('" + idx + "');\">" + image + "</a>";			
				
			}
		} catch (BusinessException e) {
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


}
