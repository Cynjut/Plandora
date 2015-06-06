package com.pandora;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.delegate.PlanningRelationDelegate;
import com.pandora.exception.BusinessException;

/**
 * This object it is a bean that represents the Planning entity. This entity
 * is the parent class of Requirement, Task and Project objects.
 */
public class PlanningTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	public static final String PLANNING_TASK        = "TSK";
	public static final String PLANNING_REQUIREMENT = "REQ";
	public static final String PLANNING_PROJECT     = "PRJ";	
	public static final String PLANNING_OCCURENCE   = "OCC";
	public static final String PLANNING_RISK	    = "RSK";
	public static final String PLANNING_COST        = "CST";
	public static final String PLANNING_EXPENSE     = "EXP";
	public static final String PLANNING_ARTIFACT    = "ART";
	public static final String PLANNING_INVOICE     = "INV";
	
	
    /** Description of Requirement */
    private String description;

    /** The creation date of Requirement */
    private Timestamp creationDate;

    /** The date when the Requirement was finalized (aborting, normal closing, etc)*/
    private Timestamp finalDate;

    /** List of additional fields of current Planning object. Each field is a meta field with a value set. */
    private Vector<AdditionalFieldTO> additionalFields;

    /** List of attachments of current Planning object. */
    private Vector<AttachmentTO> attachments;

    /** List of Discussion Topics of current Planning object. */
    private Vector<DiscussionTopicTO> discussionTopics;

    /** Iteration ID */
    private String iteration;
    
    /** List of planning objects whose current task depends on */
    private Vector<PlanningRelationTO> relationList;
    
    /** Define if the current planning object is a task, requirement, project, etc */
    private String type;
    
    /**Define if the current planning object is visible */
    private boolean visible = true;
    
    /**
     * Constructor 
     */
    public PlanningTO(){
    }

    /**
     * Constructor 
     */    
    public PlanningTO(String id){
        this.setId(id);
    }

    
    /**
     * This method must be implemented by sub-class
     */
    public String getName(){
        return null;
    }

    /**
     * This method must be implemented by sub-class
     */    
    public String getEntityType(){
        return null;
    }
    
    
    /**
     * Add a Additional Field into planning object based on MetaFieldTO and value
     */
    public void addAdditionalField(MetaFieldTO mfto, String value){
    	addAdditionalField(mfto, value, null, null);
    }
    
    public void addAdditionalField(MetaFieldTO mfto, String value, Float numericValue){
    	addAdditionalField(mfto, value, null, numericValue);
    }

    /**
     * Add a Additional Field into planning object based on MetaFieldTO and value
     */
    public void addAdditionalField(MetaFieldTO mfto, String value, Timestamp dateValue, Float numericValue){
        if (this.getAdditionalFields()==null) {
            this.setAdditionalFields(new Vector<AdditionalFieldTO>());
        }
    
        //create a new Additional Field
        AdditionalFieldTO afto = new AdditionalFieldTO();
        afto.setMetaField(mfto);
        afto.setPlanning(this);
        afto.setValue(value);
        afto.setDateValue(dateValue);
        afto.setNumericValue(numericValue);
        
        this.getAdditionalFields().addElement(afto);
    }

    public void addAdditionalTable(MetaFieldTO mfto, String value, Vector<AdditionalTableTO> values){
        if (this.getAdditionalFields()==null) {
            this.setAdditionalFields(new Vector<AdditionalFieldTO>());
        }
    
        //create a new Additional Field
        AdditionalFieldTO afto = new AdditionalFieldTO();
        afto.setMetaField(mfto);
        afto.setPlanning(this);
        afto.setValue(value);
        afto.setTableValues(values);
        
        this.getAdditionalFields().addElement(afto);
    }
    
    
    /////////////////////////////////////////////
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }

    /////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    /////////////////////////////////////////////    
    public Timestamp getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(Timestamp newValue) {
        this.finalDate = newValue;
    }

    /////////////////////////////////////////////    
    public Vector<AdditionalFieldTO> getAdditionalFields() {
        return additionalFields;
    }
    public void setAdditionalFields(Vector<AdditionalFieldTO> newValue) {
        this.additionalFields = newValue;
    }
    
    /////////////////////////////////////////////      
    public Vector<AttachmentTO> getAttachments() {
        return attachments;
    }
    public void setAttachments(Vector<AttachmentTO> newValue) {
        this.attachments = newValue;
    }

    ///////////////////////////////////////////// 
	public Vector<DiscussionTopicTO> getDiscussionTopics() {
		return discussionTopics;
	}
	public void setDiscussionTopics(Vector<DiscussionTopicTO> newValue) {
		this.discussionTopics = newValue;
	}

	
    ///////////////////////////////////////////// 
	public String getIteration() {
		return iteration;
	}
	public void setIteration(String newValue) {
		this.iteration = newValue;
	}


    ///////////////////////////////////////////// 	
	public void addRelation(PlanningRelationTO newValue){
		if (relationList==null) {
			relationList = new Vector<PlanningRelationTO>();
		}
		relationList.addElement(newValue);
	}
	public Vector<PlanningRelationTO> getRelationList() {
		
		//lazy initialization
		if (relationList==null) {
			PlanningRelationDelegate prdel = new PlanningRelationDelegate();
			try {
				relationList = prdel.getRelationList(this);
			} catch (BusinessException e) {
				relationList = null;
				e.printStackTrace();
			}
		}
		
		return relationList;
	}
	public void setRelationList(Vector<PlanningRelationTO> newValue) {
		this.relationList = newValue;
	}


    ///////////////////////////////////////////// 	
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

	
	public static PlanningTO getEntity(String entity){
		PlanningTO response = null;
	    if (entity.equals(PLANNING_REQUIREMENT)) {
	    	response = new RequirementTO();	        
	    } else if (entity.equals(PLANNING_TASK)) {
	    	response = new TaskTO();
	    } else if (entity.equals(PLANNING_PROJECT)) {
	    	response = new ProjectTO();
	    } else if (entity.equals(PLANNING_OCCURENCE)) {
	    	response = new OccurrenceTO();
	    } else if (entity.equals(PLANNING_RISK)) {
	    	response = new RiskTO();
	    } else if (entity.equals(PLANNING_ARTIFACT)) {	    	
	    	response = new ArtifactTO();
	    } else if (entity.equals(PLANNING_COST)) {
	    	response = new CostTO();
	    } else if (entity.equals(PLANNING_EXPENSE)) {
	    	response = new ExpenseTO();
	    } else if (entity.equals(PLANNING_INVOICE)) {
	    	response = new InvoiceTO();
	    }		
	    return response;
	}

	public ProjectTO getPlanningProject() {
		ProjectTO response = null;
		
		if (this instanceof TaskTO) {
			TaskTO obj = (TaskTO)this;
			response = obj.getProject();
			
		} else if (this instanceof RequirementTO) {
			RequirementTO obj = (RequirementTO)this;
			response = obj.getProject();
			
		} else if (this instanceof ProjectTO) {
			ProjectTO obj = (ProjectTO)this;
			response = obj;
			
		} else if (this instanceof OccurrenceTO) {
			OccurrenceTO obj = (OccurrenceTO)this;
			response = obj.getProject();
			
		} else if (this instanceof RiskTO) {
			RiskTO obj = (RiskTO)this;
			response = obj.getProject();
			
		} else if (this instanceof ArtifactTO) {
			ArtifactTO obj = (ArtifactTO)this;
			response = new ProjectTO(obj.getProjectId());
			
		} else if (this instanceof CostTO) {
			CostTO obj = (CostTO)this;
			response = obj.getProject();
			
		} else if (this instanceof ExpenseTO) {
			ExpenseTO obj = (ExpenseTO)this;
			response = obj.getProject();
			
		} else if (this instanceof InvoiceTO) {
			InvoiceTO obj = (InvoiceTO)this;
			response = obj.getProject();
		}
		
		return response;
	}
	
    /////////////////////////////////        
    public boolean getVisible() {
		return visible;
	}
	public void setVisible(boolean newValue) {
		this.visible = newValue;
	}

	
}
