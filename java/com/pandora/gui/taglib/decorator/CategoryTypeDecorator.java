package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CategoryTO;

/**
 * This decorator formats a grid cell with the type information of Category object
 */
public class CategoryTypeDecorator extends ColumnDecorator {
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String text = "";
		CategoryTO cto = (CategoryTO)this.getObject();
	    
		if (cto!=null && cto.getType()!=null) {
		    if (cto.getType().equals(CategoryTO.TYPE_TASK)) {
		        text = this.getBundleMessage("label.category.type.0");
		    } else if (cto.getType().equals(CategoryTO.TYPE_REQUIREMENT)) {
		        text = this.getBundleMessage("label.category.type.1");
		    } else if (cto.getType().equals(CategoryTO.TYPE_REPORT)) {
		        text = this.getBundleMessage("label.category.type.2");        
		    } else if (cto.getType().equals(CategoryTO.TYPE_KPI)) {
		        text = this.getBundleMessage("label.category.type.3");
		    } else if (cto.getType().equals(CategoryTO.TYPE_RISK)) {
		        text = this.getBundleMessage("label.category.type.4");
		    } else if (cto.getType().equals(CategoryTO.TYPE_WORKFLOW)) {
		        text = this.getBundleMessage("label.category.type.5");
		    } else if (cto.getType().equals(CategoryTO.TYPE_DISCUSSION)) {
		        text = this.getBundleMessage("label.category.type.6");        		        		        
		    } else if (cto.getType().equals(CategoryTO.TYPE_INVOICE)) {
		        text = this.getBundleMessage("label.category.type.7");        		        
		    } else if (cto.getType().equals(CategoryTO.TYPE_ARTIFACT)) {
		    	text = this.getBundleMessage("label.category.type.8");        		        
		    } else if (cto.getType().equals(CategoryTO.TYPE_COST)) {
		    	text = this.getBundleMessage("label.category.type.9");        		        
		    }
		    
		} else {
		    text = this.getBundleMessage("label.category.type.0");
		}
		return text;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }

    

}
