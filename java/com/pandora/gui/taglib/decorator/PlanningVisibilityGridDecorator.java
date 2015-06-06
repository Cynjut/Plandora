package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;

public class PlanningVisibilityGridDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		return decorate(columnValue, null);    
	}
	
	private boolean getVisibility(TransferObject to){
		boolean response = false;
		if (to instanceof RiskTO) {
     		response = ((RiskTO)to).getVisible();
     	} else if (to instanceof OccurrenceTO ){
     		response = ((OccurrenceTO)to).getVisible();
     	}
		return response;
	}

	@Override
	public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";
		
		TransferObject to = (TransferObject)this.getObject();
		
		if (to!=null){
			response = this.getBundleMessage("label.formPlanning.visibility." + this.getVisibility(to));
		}
		
		return response;
	}

	@Override
	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

}
