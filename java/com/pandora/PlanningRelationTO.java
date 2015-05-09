package com.pandora;

import java.util.Iterator;
import java.util.Vector;

public class PlanningRelationTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	public static final String ENTITY_TASK = "1";  
	public static final String ENTITY_REQ  = "2"; 
	public static final String ENTITY_PROJ = "3";
	public static final String ENTITY_OCCU = "4"; 
	public static final String ENTITY_RISK = "5"; 

	public static final String RELATION_RELATED_WITH     = "2";
	public static final String RELATION_COMPOSED_BY      = "5";
	public static final String RELATION_BLOCKS           = "6";
	
	public static final String RELATION_PART_OF          = "7";
	
	public static final String RELATION_IMPLEMENTED_BY   = "3";

	public static final String CONSEQUENCE_OF            = "8";

	
	
	private PlanningTO planning;
	
	private PlanningTO related;
	
	private String planType;
	
	private String relatedType;
	
	private String relationType;

	
	////////////////////////////////////////
	public PlanningTO getPlanning() {
		return planning;
	}
	public void setPlanning(PlanningTO newValue) {
		this.planning = newValue;
	}

	
	////////////////////////////////////////	
	public PlanningTO getRelated() {
		return related;
	}
	public void setRelated(PlanningTO newValue) {
		this.related = newValue;
	}

	
	////////////////////////////////////////	
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String newValue) {
		this.planType = newValue;
	}
	

	////////////////////////////////////////	
	public String getRelatedType() {
		return relatedType;
	}
	public void setRelatedType(String newValue) {
		this.relatedType = newValue;
	}

	
	////////////////////////////////////////	
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String newValue) {
		this.relationType = newValue;
	}
	
	/**
	 * Return a sub set of PlanningRelationTO objects according to a specific relationType and a planningId. 
	 */
	public static Vector<PlanningRelationTO> getRelation(Vector<PlanningRelationTO> relationList, String relationType, String planningId, boolean isPlanningField) {
		Vector<PlanningRelationTO> response = new Vector<PlanningRelationTO>();
		if (relationList!=null) {
			Iterator<PlanningRelationTO> i = relationList.iterator();
			while(i.hasNext()) {
				PlanningRelationTO relation = i.next();
				if (relation.getRelationType()!=null && relation.getRelationType().equals(relationType)) {
					if (planningId==null) {
						response.addElement(relation);
					} else if (isPlanningField && relation.getPlanning().getId().equals(planningId)){
						response.addElement(relation);
					} else if (!isPlanningField && relation.getRelated().getId().equals(planningId)){
						response.addElement(relation);
					}
				}
			}
		}
		return response;
	}
}

