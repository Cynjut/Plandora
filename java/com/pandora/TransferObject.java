package com.pandora;

import java.io.Serializable;

import com.pandora.exception.BusinessException;
import com.pandora.integration.Integration;

public class TransferObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /** The id of an entity into data base*/
    private String id;

    /** A generic tag that can be used for multiples purposes */
    private String genericTag;
    
    /** This attribute is used when the object is into a grid.
     * This sequence ID is used to identify a row independently of grid sorting */
    private int gridRowNumber;

    
    public TransferObject(){
    	this.gridRowNumber = -1;
    }
    
    
    public TransferObject(String toId, String tag){
        this.id = toId;
        this.genericTag = tag;
        this.gridRowNumber = -1;
    }
    
    
    /**
     * This method should be overloaded by subclasses in order to
     * pupulate the attributes using the Integration object information.
     * 
     * @param iobj
     * @param handler The third-part client application's user
     */
    public void populate(Integration iobj, UserTO handler) throws BusinessException {
       //must be overloaded by sub-classes if necessary (for integration process)!!! 
    }
    
    
    
    ////////////////////////////////////////
    public String getId() {
        return id;
    }
    public void setId(String newValue) {
        this.id = newValue;
    }
    
    ////////////////////////////////////////    
    public String getGenericTag() {
        return genericTag;
    }
    public void setGenericTag(String newValue) {
        this.genericTag = newValue;
    }

    ////////////////////////////////////////   
	public int getGridRowNumber() {
		return gridRowNumber;
	}
	public void setGridRowNumber(int newValue) {
		this.gridRowNumber = newValue;
	}


	public String toString() {
		return id;
	}
}
