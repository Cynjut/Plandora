package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.NodeTemplateTO;
import com.pandora.helper.HtmlUtil;

public class NodeTemplateTypeDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String img = "&nbsp;";
		NodeTemplateTO node = (NodeTemplateTO)this.getObject();
		if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
			String altValue = this.getBundleMessage("title.formApplyTaskTemplate.type.step");
			img = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/flow-step.gif\" />";
		} else if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
			String altValue = this.getBundleMessage("title.formApplyTaskTemplate.type.decision");
			img = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/flow-decision.gif\" />";	
		}
		return img;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
    	return columnValue+"";
	}
}
