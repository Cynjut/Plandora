package com.pandora.gui.taglib.table;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.UserTO;

public class PColumnTag extends BodyTagSupport implements Cloneable {

	private static final long serialVersionUID = 1L;
	
	private String property;
	
	private String value;
	
	private String title;
	
	private String sort;
	
	private String maxWords;
	
	private String width;
	
	private String align;
	
	private String decorator;

	private ColumnDecorator decoratorInstance;

	private String tag;
	
	private boolean visible = true;
	
	private String visibleProperty;
	
	private String description;
	
	private String likeSearching;
	
	private String comboFilter;
	
	
	
	public int doEndTag() throws JspException {
		Object parent = this.getTableTag();
		PColumnTag clone = this;
	    try {
	    	if (this instanceof MetaFieldPColumnTag) {
	    		clone = (MetaFieldPColumnTag)this.clone();
	    		((MetaFieldPColumnTag)this).resetMetaFieldData();
	    	} else {
	    		clone = (PColumnTag)this.clone();	
	    	}
	    } catch( CloneNotSupportedException e ) {
	    	e.printStackTrace();
	    }
	    ((PTableTag)parent).addColumn(clone);
	    return super.doEndTag();
	}
	
	
	public Object getTableTag() throws JspException{
		Object parent = this.getParent();
		
		boolean foundTableTag = false;
		String err = "Can not use pColumn tag outside of a PTableTag.";
       
		while (!foundTableTag) {
			if( parent == null ) {
				throw new JspException(err);
			}
			if( !( parent instanceof PTableTag ) ) {
				if (parent instanceof TagSupport) {
					parent = ((TagSupport) parent).getParent();
				} else {
					throw new JspException(err);
				}
			} else {
				foundTableTag = true;    
			}
		}		
		return parent;
	}

	
	public ArrayList<String> getHeaderList(HttpServletRequest req, UserTO uto, String tableName){
		ArrayList<String> response = new ArrayList<String>();       

		String headerTitle = this.getTitle();
		if (headerTitle == null) {
			headerTitle = this.getProperty().toUpperCase();
		}
        response.add(headerTitle);

		return response;       
	}
	
	public StringBuffer getHeader(HttpServletRequest req, UserTO uto, String tableName, String frmName){
		StringBuffer buf = new StringBuffer();       

		if (this.visible) {

			buf.append(this.getHeaderAttributes());

			String headerTitle = this.getTitle();
			if (headerTitle == null) {
				headerTitle = this.getProperty().toUpperCase();
			}
			String header = uto.getBundle().getMessage(req.getLocale(), headerTitle);
	        if (header.startsWith("???")) {
	        	header = headerTitle;
	        }		    
			
			buf.append(this.getSortFormating(tableName, frmName, this.getProperty(), header));

			buf.append("</th>\n");    	       	   
		}

		return buf;       
	}

	
	protected int getColumnsNumber(HttpServletRequest req, String tableName) {
		return 1;
	}


	private StringBuffer getHeaderAttributes(){
		StringBuffer buf = new StringBuffer();
              
		buf.append("<th");
		if (this.getWidth() != null) {
			buf.append(" width=\"" + this.getWidth() + "\"");
		}

		if (this.getAlign() != null) {
			buf.append(" align=\"" + this.getAlign() + "\"");
		}

		buf.append(" class=\"tableCellHeader\">");
       
		return buf;
	}
	

	private StringBuffer getSortFormating(String tableName, String frmname, String colProperty, String label) {
		StringBuffer buf = new StringBuffer();
	   
		if (this.getSort() != null) {
			String param = PTableTag.SORT_ORDER + "=" + this.getSort() + "|" + 
			               PTableTag.SORT_COL   + "=" + colProperty;  
			
			buf.append("<a href=\"#\" onclick=\"javascript:requestPTableBody('" + tableName + "', '" + frmname + "', '" + param + "');return false;\" " +  
					" class=\"tableCellHeader\">");
			buf.append(label);
			buf.append("</a>");			
		} else {
			buf.append(label);
		}
		
		return buf;
	}


	public boolean containComboFilter() {
		return (this.getComboFilter()!=null && this.getComboFilter().equalsIgnoreCase("true"));
	}

	
	public boolean containLikeSearching() {
		return (this.getLikeSearching()!=null && this.getLikeSearching().equalsIgnoreCase("true"));
	}

	
	
	//////////////////////////////////////////
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}


	//////////////////////////////////////////
	public String getValue() {
		return value;
	}
	public void setValue(String newValue) {
		this.value = newValue;
	}

	
	//////////////////////////////////////////
	public String getTitle() {
		return title;
	}
	public void setTitle(String newValue) {
		this.title = newValue;
	}


	//////////////////////////////////////////
	public String getSort() {
		return sort;
	}
	public void setSort(String newValue) {
		this.sort = newValue;
	}

	
	//////////////////////////////////////////
	public String getMaxWords() {
		return maxWords;
	}
	public void setMaxWords(String newValue) {
		this.maxWords = newValue;
	}

	
	//////////////////////////////////////////
	public String getWidth() {
		return width;
	}
	public void setWidth(String newValue) {
		this.width = newValue;
	}

	
	//////////////////////////////////////////
	public String getAlign() {
		return align;
	}
	public void setAlign(String newValue) {
		this.align = newValue;
	}

	
	//////////////////////////////////////////
	public String getDecorator() {
		return decorator;
	}
	public void setDecorator(String newValue) {
		this.decorator = newValue;
	}


	//////////////////////////////////////////
	public String getTag() {
		return tag;
	}
	public void setTag(String newValue) {
		this.tag = newValue;
	}

	
	//////////////////////////////////////////
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean newValue) {
		this.visible = newValue;
	}
	
	
	//////////////////////////////////////////
	public String getVisibleProperty() {
		return visibleProperty;
	}
	public void setVisibleProperty(String newValue) {
		this.visibleProperty = newValue;
	}


	//////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}


	//////////////////////////////////////////
	public ColumnDecorator getDecoratorInstance() {
		return decoratorInstance;
	}
	public void setDecoratorInstance(ColumnDecorator newValue) {
		this.decoratorInstance  = newValue;
	}

	
	
	@Override
	public String toString() {
		return property;
	}


	//////////////////////////////////////////
	public String getLikeSearching() {
		return likeSearching;
	}
	public void setLikeSearching(String newValue) {
		this.likeSearching = newValue;
	}

	
	
	//////////////////////////////////////////
	public String getComboFilter() {
		return comboFilter;
	}
	public void setComboFilter(String newValue) {
		this.comboFilter = newValue;
	}


	
	public PColumnTag getClone() {
		PColumnTag response = new PColumnTag();
		response.setProperty(this.getProperty());
		response.setAlign(this.getAlign());
		response.setValue(this.getValue());
		response.setTitle(this.getTitle());
		response.setSort(this.getSort());
		response.setMaxWords(this.getMaxWords());
		response.setWidth(this.getWidth());
		response.setDecorator(this.getDecorator());
		response.setDescription(this.getDescription());
		response.setTag(this.getTag());
		response.setVisible(this.isVisible());
		response.setVisibleProperty(this.getVisibleProperty());
		response.setComboFilter(this.getComboFilter());
		response.setLikeSearching(this.getLikeSearching());
		return response;
	}


}