package com.pandora.gui.taglib.decorator;

import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CategoryTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.helper.HtmlUtil;

public class TaskBillableDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		return decorate(columnValue, null);
	}

	public String decorate(Object columnValue, String tag) {
		String content = "";
		
		boolean bill = this.getBillableStatus();
		if (tag==null) {
			if (bill) {
				content = super.getBundleMessage("label.yes");
			} else {
				content = super.getBundleMessage("label.no");
			}
		} else {
			ResourceTaskTO to = (ResourceTaskTO)this.getObject();
		    Vector<TransferObject> billableStatusList = new Vector<TransferObject>();
		    billableStatusList.add(new TransferObject("1", super.getBundleMessage("label.yes")));
		    billableStatusList.add(new TransferObject("0", super.getBundleMessage("label.no")));			
			content = HtmlUtil.getComboBox("cb_" + to.getId() + "_billable", billableStatusList, "textBox", bill?"1":"0");			
		}
		
		return content;
	}

	public String contentToSearching(Object columnValue) {
		String content = super.getBundleMessage("label.yes");
		boolean bill = this.getBillableStatus();
		if (!bill) {
			content = super.getBundleMessage("label.no");
		}
		return content;
	}

	
	
	private boolean getBillableStatus() {
		boolean response = false;
		Object obj = this.getObject();
		
		if (obj instanceof ResourceTaskTO) {
			ResourceTaskTO to = (ResourceTaskTO)obj;
			if (to!=null) {
				Boolean bill = to.getBillableStatus();
				if (bill==null) {
					TaskTO tto = to.getTask();
					CategoryTO cto = tto.getCategory();
					bill = cto.getIsBillable();
					if (bill==null) {
						bill = new Boolean(false);
					}
				}
				response = bill.booleanValue();
			}
		}
		
		return response;
	}
}
