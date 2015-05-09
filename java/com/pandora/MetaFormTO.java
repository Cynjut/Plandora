package com.pandora;

/**
 */
public class MetaFormTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private String name;
    
    private String viewableCols;
    
    private String filterColId;
    
    private Integer gridNumber;
    
    private String jsBeforeSave;
    
    private String jsAfterSave;
    
    private String jsAfterLoad;
    
    
    
    public MetaFormTO(String i){
        super.setId(i);
    }
    
    
    /////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    /////////////////////////////////////
    public String getViewableCols() {
        return viewableCols;
    }
    public void setViewableCols(String newValue) {
        this.viewableCols = newValue;
    }


    /////////////////////////////////////
	public String getFilterColId() {
		return filterColId;
	}
	public void setFilterColId(String newValue) {
		this.filterColId = newValue;
	}


    /////////////////////////////////////
	public Integer getGridNumber() {
		return gridNumber;
	}
	public void setGridNumber(Integer newValue) {
		this.gridNumber = newValue;
	}

	
    /////////////////////////////////////
	public String getJsBeforeSave() {
		return jsBeforeSave;
	}
	public void setJsBeforeSave(String newValue) {
		this.jsBeforeSave = newValue;
	}

	
    /////////////////////////////////////
	public String getJsAfterSave() {
		return jsAfterSave;
	}
	public void setJsAfterSave(String newValue) {
		this.jsAfterSave = newValue;
	}

	
    /////////////////////////////////////
	public String getJsAfterLoad() {
		return jsAfterLoad;
	}
	public void setJsAfterLoad(String newValue) {
		this.jsAfterLoad = newValue;
	}
    
    
}
