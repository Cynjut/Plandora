package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RepositoryFileTO;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;

public class RepositoryEntryLockDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		RepositoryFileTO item = (RepositoryFileTO)getObject();
		if (item!=null && item.getIsLocked()!=null && item.getIsLocked().booleanValue()) {	
			String altValue = this.getBundleMessage("label.formRepository.locked.desc");
		    image =  "<a class=\"gridLink\" href=\"javascript:void(0);\" onclick=\"openFloatPanel(''); showLockInfo('"; 
		    image +=  item.getLockOwner() + "', '"; 
		    if (item.getLockDate()!=null) {
		    	image += DateUtil.getDateTime(item.getLockDate(), this.getCurrentLocale(), 2, 2); 	
		    }
		    image += "', '" + item.getLockComment() + "');\" border=\"0\"> \n";			
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/lock.png\" >";
		    image += "</a>";			
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
