package com.pandora;

/**
 * This object it is a bean that represents a Category entity. 
 */
public class CategoryTO extends TransferObject{

	private static final long serialVersionUID = 1L;
	
    /** The category with id=0 is a default category for any type */
    public static final String DEFAULT_CATEGORY_ID = "0";

    
    /** The type value related with task category */
    public static final Integer TYPE_TASK        = new Integer(0);

    /** The type value related with requirement category */    
    public static final Integer TYPE_REQUIREMENT = new Integer(1);

    /** The type value related with report category */    
    public static final Integer TYPE_REPORT      = new Integer(2);

    /** The type value related with KPI category */    
    public static final Integer TYPE_KPI         = new Integer(3);

    /** The type value related with Risk category */    
    public static final Integer TYPE_RISK        = new Integer(4);
   
    /** The type value related with Workflow category */    
    public static final Integer TYPE_WORKFLOW    = new Integer(5);

    /** The type value related with Discussion category */    
    public static final Integer TYPE_DISCUSSION  = new Integer(6);

    /** The type value related with Invoice category */    
    public static final Integer TYPE_INVOICE     = new Integer(7);

    /** The type value related with artifact category */    
    public static final Integer TYPE_ARTIFACT    = new Integer(8);

    /** The type value related with cost category */    
    public static final Integer TYPE_COST        = new Integer(9);
    
    /** The type value related with occurrence category */    
    public static final Integer TYPE_OCCURRENCE  = new Integer(10);

    
    /** The Name of Category */
    private String name;
    
    /** The description of Category */
    private String description;

    /** The type of Category, e.g., where the category is applicable (Task, Requirement, etc) */
    private Integer type;
    
    /** The project related with category */
    private ProjectTO project = null;
    
    private int positionOrder;
    
    /** (only for task category) Define whether the tasks of this category is billable tasks */
    private Boolean isBillable = null;

    /** (only for task category) Define whether the tasks of this category is related with defect */
    private Boolean isDefect = null;

    /** (only for task category) Define whether the tasks of this category is related with testing */
    private Boolean isTesting = null;

    /** (only for task category) Define whether the tasks of this category is related with developing */
    private Boolean isDevelopingTask = null;
    
    /** Define whether this category is hidden in task form (cannot be used to allocation purposes) */
    private Boolean isHidden = null;
    

    
    /**
     * Constructor 
     */
    public CategoryTO(){
    }

    /**
     * Constructor 
     */    
    public CategoryTO(String id){
        this.setId(id);
    }
    
    
    /////////////////////////////////////////////
    public String getDescription(){
    	return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    //////////////////////////////////////////       
    public Integer getType() {
        return type;
    }
    public void setType(Integer newValue) {
        this.type = newValue;
    }

    //////////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }

    //////////////////////////////////////////      
	public Boolean getIsBillable() {
		return isBillable;
	}
	public void setIsBillable(Boolean newValue) {
		this.isBillable = newValue;
	}

    //////////////////////////////////////////	
	public Boolean getIsHidden() {
		return isHidden;
	}
	public void setIsHidden(Boolean newValue) {
		this.isHidden = newValue;
	}

    //////////////////////////////////////////		
	public Boolean getIsDefect() {
		return isDefect;
	}
	public void setIsDefect(Boolean newValue) {
		this.isDefect = newValue;
	}

    //////////////////////////////////////////		
	public Boolean getIsTesting() {
		return isTesting;
	}

	public void setIsTesting(Boolean newValue) {
		this.isTesting = newValue;
	}
	
	////////////////////////////////////////
	public Boolean getIsDevelopingTask() {
		return isDevelopingTask;
	}
	public void setIsDevelopingTask(Boolean newValue) {
		this.isDevelopingTask = newValue;
	}

	
    //////////////////////////////////////////    
	public int getPositionOrder() {
		return positionOrder;
	}
	public void setPositionOrder(int newValue) {
		this.positionOrder = newValue;
	}	
}
