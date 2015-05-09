package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CategoryTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;

public class TaskCategoryDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		return getCategoryName();
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return getCategoryName();
	}

	
	
	private String getCategoryName() {
		String text = "";
		CategoryTO cto = null;		
		Object obj = this.getObject();
		
		if (obj instanceof TaskTO) {
			TaskTO to = (TaskTO)obj;
			if (to!=null) {
				cto = to.getCategory();    		    
			}
			
		} else if (obj instanceof ResourceTaskTO) {
			ResourceTaskTO to = (ResourceTaskTO)obj;
			if (to!=null) {
				TaskTO tto = to.getTask();
				if (tto!=null){
					cto = tto.getCategory();	 
				}
			}
		}
		
		if (cto!=null) {
		    text = cto.getName();
		}
		
		return text;
	}
}