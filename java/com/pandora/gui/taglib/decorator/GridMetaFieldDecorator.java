package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.MetaFieldTO;

/**
 * This decorator formats a grid cell of Meta Field form
 */
public class GridMetaFieldDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "1");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String cell = "";
		String index = "";
		
		if (columnValue!=null) {
		    index = ((Integer)columnValue).toString();
    	}
    
		if (tag.equals("1")) {
		    cell = this.getBundleMessage("label.formMetaField.type" + index);
		    
		} else if (tag.equals("2")) {
		    cell = getMetaFieldApplyTo(index, (MetaFieldTO)this.getObject());
		    
		} else {
		    cell = "&nbsp;";
		}
		
		return cell;
    }
    
        
    private String getMetaFieldApplyTo(String index, MetaFieldTO mf){
        String response = "";
        
        if (index.equals(MetaFieldTO.APPLY_TO_REQUIREMENT.toString())) {
            response = this.getBundleMessage("label.formMetaField.applyTo.requirement");
        } else if (index.equals(MetaFieldTO.APPLY_TO_TASK.toString())) {
            response = this.getBundleMessage("label.formMetaField.applyTo.task");
        } else if (index.equals(MetaFieldTO.APPLY_TO_PROJECT.toString())) {
            response = this.getBundleMessage("label.formMetaField.applyTo.project");
        } else if (index.equals(MetaFieldTO.APPLY_TO_RISK.toString())) {
            response = this.getBundleMessage("label.formMetaField.applyTo.risk");
        } else if (index.equals(MetaFieldTO.APPLY_TO_INVOICE.toString())) {
            response = this.getBundleMessage("label.formMetaField.applyTo.risk");
        } else if (index.equals(MetaFieldTO.APPLY_TO_EXPENSE.toString())) {    
        	response = this.getBundleMessage("label.formMetaField.applyTo.expense");
        } else if (index.equals(MetaFieldTO.APPLY_TO_COST.toString())) {
        	response = this.getBundleMessage("label.formMetaField.applyTo.cost");
        } else if (index.equals(MetaFieldTO.APPLY_TO_CUSTOM_FORM.toString())) {
            if (mf.getMetaform()!=null) {
                response = mf.getMetaform().getName();
            } else {
                response = "???";
            }
        }
        return response;
    }
  
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
