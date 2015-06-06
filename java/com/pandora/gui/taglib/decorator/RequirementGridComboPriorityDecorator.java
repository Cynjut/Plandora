package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.helper.HtmlUtil;

public class RequirementGridComboPriorityDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

	@Override
    public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";

		RequirementTO rto = (RequirementTO)this.getObject();		
		if (tag!=null) {
			String name = "cb_" + rto.getId();
			String[] tokens = tag.split(";");
			if (tokens!=null && tokens.length==2) {
				name = name + "_" + tokens[0];
				String text = tokens[1];
				response = HtmlUtil.getComboBox(name, text, "textBox", ""+columnValue, this.getSession());		
			} else {
				response = ""+columnValue;
			}
		} else {
			response = ""+columnValue;
		}
		
		return response;
    }


	@Override
	public String contentToSearching(Object columnValue) {
		return this.getBundleMessage("label.requestPriority." + columnValue);
	}

}
