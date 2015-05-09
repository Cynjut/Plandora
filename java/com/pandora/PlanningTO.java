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
	
	
    /** Description of Requirement */
    private String description;

    /** The creation date of Requirement */
    private Timestamp creationDate;

    /** The date when the Requirement was finalized (aborting, normal closing, etc)*/
    private Timestamp finalDate;

    /** List of additional fields of current Planning object. Each field is a meta field with a value set. */
    private Vector<AdditionalFieldTO> additionalFields;

    /** List of attachments of current Planning object. */
    private Vector attachments;

    /** List of Discussion Topics of current Planning object. */
    private Vector discussionTopics;

    /** Iteration ID */
    private String iteration;
    
    /** List of planning objects whose current task depends on */
    private Vector<PlanningRelationTO> relationList;
    
    /** Define if the current planning object is a task, requirement, project, etc */
    private String type;
    
    
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
     * Add a Addtional Field into planning object based on MetaFieldTO and value
     */
    public void addAdditionalField(MetaFieldTO mfto, String value){
    	addAdditionalField(mfto, value, null);
    }

    /**
     * Add a Addtional Field into planning object based on MetaFieldTO and value
     */
    public void addAdditionalField(MetaFieldTO mfto, String value, Timestamp dateValue){
        if (this.getAdditionalFields()==null) {
            this.setAdditionalFields(new Vector<AdditionalFieldTO>());
        }
    
        //create a new Additional Field
        AdditionalFieldTO afto = new AdditionalFieldTO();
        afto.setMetaField(mfto);
        afto.setPlanning(this);
        afto.setValue(value);
        afto.setDateValue(dateValue);
        
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
    public Vector getAttachments() {
        return attachments;
    }
    public void setAttachments(Vector newValue) {
        this.attachments = newValue;
    }

    ///////////////////////////////////////////// 
	public Vector getDiscussionTopics() {
		return discussionTopics;
	}
	public void setDiscussionTopics(Vector newValue) {
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
	public void setRelationList(Vector newValue) {
		this.relationList = newValue;
	}


    ///////////////////////////////////////////// 	
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

}
