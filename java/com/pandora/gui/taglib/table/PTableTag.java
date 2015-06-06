package com.pandora.gui.taglib.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class PTableTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	public static String FILTERED_GRID_PREFIX = "FILTERED_";
	public static String DEF_TABLE_PREFIX     = "DEF_TABLE_";
	public static String SORT_COL             = "sCol";
	public static String SORT_ORDER           = "sOrder";
	
    public static final int SORT_ORDER_DECENDING = 1;
    public static final int SORT_ORDER_ASCEENDING = 2;
	
	
    private int columnsNumber = 0;

    private ArrayList<PColumnTag> columns = new ArrayList<PColumnTag>();    

    private HashMap<String,String> parameters = new HashMap<String,String>();    

    private String name;
    
    private String scope;
    
    private String length;
    
    private String pagesize;
        
    private String width;
    
    private String height;
    
    private String frm;
    
    private String dataexport;
    
    private String parenttag;
    
    private String ajax;
    
	@Override
	public int doStartTag() throws JspException {
		this.columns = new ArrayList<PColumnTag>();
		this.parameters = new HashMap<String, String>();
		return super.doStartTag();
	}
	

	@Override
	public int doEndTag() throws JspException {
        StringBuffer str = new StringBuffer("");		
        HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
        
        if (ignoreAjax()) {
            req.getSession().setAttribute(DEF_TABLE_PREFIX + name, this.getClone());
        	PTableHelper tHelper = new PTableHelper();
		    String body = tHelper.getTableBody(name, req, "");					
		    String header = tHelper.getTableHeader(name, req, "");
		    
		    str.append(header);
		    str.append(body);
        	
        } else {
    		if (height==null || height.trim().equals("")){
    			height = "40";
    		}

    		if (width==null || width.trim().equals("")){
    			width = "100%";
    		}

    		str.append("<div id=\"DIV_" + name + "\" style=\"width:" + width + ";height:" + height + ";overflow:none;\">");							
    		str.append("<center><img src=\"../images/indicator.gif\" id=\"TABLE_" + name + "\" border=\"0\" alt=\"\" title=\"\"/></center>");					
    		str.append("<script language=\"javascript\">requestPTableBody('" + name + "', '" + frm + "', '');</script>");						
    		str.append("</div>");
        }
		
        write(str);
        
        req.getSession().setAttribute(DEF_TABLE_PREFIX + name, this.getClone());
        
		return EVAL_PAGE;
	}


	public void addColumn(PColumnTag colObj) {
		HttpServletRequest req = null;
		if (this.pageContext!=null) {
			req = (HttpServletRequest) this.pageContext.getRequest();	
		}
        
        int colNum  = colObj.getColumnsNumber(req, name);
        for (int i = 0; i<colNum; i++) {
            columnsNumber++;
        }
        columns.add(colObj);        
    }

    public int getColumnsNumber() {
    	int num = columnsNumber;
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (!column.isVisible()) {
            	num--;	
            }
        }
        return num;
    }

    
	public int getRelativeCol(String colId) {
    	int response = -1;
        for (int c = 0; c < this.columns.size(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (column.getProperty().equals(colId)) {
            	response = c;
            	break;
            }
        }
        return response;
	}

    
    public int getRealColumnsNumber(){
        return this.columns.size();
    }

	public PColumnTag getColumn(int i) {
		PColumnTag response = null;
		if (columns!=null) {
			response = this.columns.get(i);		
		}
		return response;
	}

	public boolean containVisibleProperty() {
		boolean response = false;
        for (int c = 0; c < this.columns.size(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (column.getVisibleProperty()!=null && !column.getVisibleProperty().trim().equals("")) {
            	response = true;
            	break;
            }
        }
        return response;
	}


	public boolean isExportable(){
		boolean response = true;
		if (this.dataexport!=null && this.dataexport.equalsIgnoreCase("false")){
			response = false;
		}
		return response;
	}
	
	public boolean containLikeSearching() {
		boolean response = false;
        for (int c = 0; c < this.columns.size(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (column.containLikeSearching()) {
            	response = true;
            	break;
            }
        }
        return response;
	}


	public boolean containComboFilter() {
		boolean response = false;
        for (int c = 0; c < this.columns.size(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (column.containComboFilter()) {
            	response = true;
            	break;
            }
        }
        return response;
	}
	
	
	public PColumnTag getColumnByProperty(String colProperty) {
		PColumnTag response = null;
        for (int c = 0; c < this.columns.size(); c++) {
            PColumnTag column = (PColumnTag)columns.get(c);
            if (column.getProperty().equals(colProperty)) {
            	response = column;
            	break;
            }
        }
        return response;
	}

	
    
	public void addParameter(String params) {
		if (params!=null && !params.trim().equals("")) {
			String[] plist = params.split("\\|");
			if (plist!=null) {
				for (String item : plist) {
					item = item + " ";
					String[] param = item.split("=");
					if (param!=null && param.length==2) {
						this.parameters.remove(param[0].trim());
						this.parameters.put(param[0].trim(), param[1].trim());
					}
				}
			}
		}
	}

	public String getParameter(String id) {
		if (this.parameters!=null) {
			return this.parameters.get(id);
		} else {
			return null;
		}
	}

	
	public boolean isHierarchical() {
		boolean response = false;
		if (getParenttag()!=null && !getParenttag().trim().equals("")) {
			response = true;
		}
		return response;
	}

	
    private void write(StringBuffer val) throws JspTagException {
       try {
          JspWriter out = pageContext.getOut();
          out.write(val.toString());
       } catch( IOException e ) {
          throw new JspTagException( "Writer Exception: " + e );
       }
    }    
    
    
    //////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
    //////////////////////////////////////////
	public String getScope() {
		return scope;
	}
	public void setScope(String newValue) {
		this.scope = newValue;
	}

	
    //////////////////////////////////////////
	public String getLength() {
		return length;
	}
	public void setLength(String newValue) {
		this.length = newValue;
	}

	
    //////////////////////////////////////////
	public String getPagesize() {
		return pagesize;
	}
	public void setPagesize(String newValue) {
		this.pagesize = newValue;
	}

		
    //////////////////////////////////////////
	public String getWidth() {
		return width;
	}
	public void setWidth(String newValue) {
		this.width = newValue;
	}

	
    //////////////////////////////////////////	
	public String getHeight() {
		return height;
	}
	public void setHeight(String newValue) {
		this.height = newValue;
	}

	
	
	
    //////////////////////////////////////////
	public String getFrm() {
		return frm;
	}
	public void setFrm(String newValue) {
		this.frm = newValue;
	}

	
    private PTableTag getClone() {
    	PTableTag response = new PTableTag();
    	response.setFrm(this.getFrm());
    	response.setHeight(this.getHeight());
    	response.setWidth(this.getWidth());
    	response.setPagesize(this.getPagesize());
    	response.setLength(this.getLength());
    	response.setScope(this.getScope());
    	response.setName(this.getName());
    	response.setDataexport(this.getDataexport());
    	response.setParenttag(this.getParenttag());
    	for (PColumnTag col : columns) {
   			response.addColumn(col.getClone());	
		}
		return response;
	}


    //////////////////////////////////////////	
	public String getDataexport() {
		return dataexport;
	}
	public void setDataexport(String newValue) {
		this.dataexport = newValue;
	}


    //////////////////////////////////////////
	public String getParenttag() {
		return parenttag;
	}
	public void setParenttag(String newValue) {
		this.parenttag = newValue;
	}

	
    //////////////////////////////////////////
	public String getAjax() {
		return ajax;
	}
	public void setAjax(String newValue) {
		this.ajax = newValue;
	}
	
	private boolean ignoreAjax(){
		boolean response = false;
		if (ajax!=null) {
			response = ajax.trim().equals("false");
		}
		return response;
	}
}
