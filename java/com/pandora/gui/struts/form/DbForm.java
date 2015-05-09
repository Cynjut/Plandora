package com.pandora.gui.struts.form;

/**
 * 
 */
public class DbForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** SQL statement that must be executed by action */
    private String sql = "";
    
    /** Query result */
    private String result = "";
    
    /** table list */
    private String tableList;
    
    
    ////////////////////////////////////    
    public String getTableList() {
        return tableList;
    }
    public void setTableList(String newValue) {
        this.tableList = newValue;
    }
    
    ////////////////////////////////////
    public String getSql() {
        return sql;
    }
    public void setSql(String newValue) {
        this.sql = newValue;
    }

    ////////////////////////////////////    
    public String getResult() {
        return result;
    }
    public void setResult(String newValue) {
        this.result = newValue;
    }
}
