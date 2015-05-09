package com.pandora.gui.struts.form;


import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.pandora.AdditionalFieldTO;
import com.pandora.UserTO;

/**
 * Generic struts form. All forms of project must extends this class 
 * in order to contain commons attributes. 
 */
public class GeneralStrutsForm extends ActionForm {
    
	private static final long serialVersionUID = 1L;
	
    /** User by save button to set current action for user updating */
    public static final String INSERT_METHOD = "0";
    
    /** User by save button to set current action for user insertion */
    public static final String UPDATE_METHOD = "1";

    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	Object u = request.getAttribute("uid");
    	if (u!=null) {
    		this.uid = (String)u;	
    	}
	}
    
    
    /** Action UID */
    private String uid;
    
    /** Action method that must be executed */
    protected String operation;

    /** id of object into data base */
    protected String id;
    
    /** The status of current save process. Used for "Save" method to decide which
     * action must be performed (update or insert) */ 
    private String saveMethod;

    /** The current user connected */     
    private UserTO uto;
    
    /** List of additional fields to be displayed on Requirement, Task and Project Forms 
     * This list is populated by struts action of each form and should contain the additional fields
     * for a specific planning id.*/
    private Vector additionalFields;
    
    /** A generic tag that can be used for multiples purposes */
    private String genericTag;
    
    
    /**
     * Found the additional field that corresponding to specific Meta Field id
     * @param metaFieldId
     * @return
     */
    public AdditionalFieldTO getAdditionalField(String metaFieldId) {
        AdditionalFieldTO response = null;
        if (additionalFields!=null) {
            Iterator i = additionalFields.iterator();
            while(i.hasNext()){
                AdditionalFieldTO afto = (AdditionalFieldTO)i.next();
                if (afto.getMetaFieldId().equals(metaFieldId)) {
                    response = afto;
                    break;
                }
            }            
        }
        return response;
    }

    
    public String getSaveLabel() {
        String response = "Err!";
        
        if (uto!=null){
            MessageResources mr = uto.getBundle();
            if (saveMethod==null || saveMethod.equals(INSERT_METHOD)){
                response = mr.getMessage(uto.getLocale(), "button.saveNew");
            } else if (saveMethod.equals(UPDATE_METHOD)){
                response = mr.getMessage(uto.getLocale(), "button.update");
            }
        }
        
        return response;
    }

    
    ///////////////////////////////////////    
    public String getId() {
        return id;
    }
    public void setId(String newValue) {
        this.id = newValue;
    }
    
    ///////////////////////////////////////    
    public String getOperation() {
        return operation;
    }
    public void setOperation(String newValue) {
        this.operation = newValue;
    }
    
    ///////////////////////////////////////    
    public void setSaveMethod(String newValue1, UserTO newValue2) {
        this.saveMethod = newValue1;
        this.uto = newValue2;
    }
    public String getSaveMethod() {
        return saveMethod;
    }
    public UserTO getCurrentUser() {
        return uto;
    }

    ///////////////////////////////////////        
    public void setAdditionalFields(Vector newValue) {
        this.additionalFields = newValue;
    }
    
    ////////////////////////////////////////    
    public String getGenericTag() {
        return genericTag;
    }
    public void setGenericTag(String newValue) {
        this.genericTag = newValue;
    }

    
    ////////////////////////////////////////    
	public String getUid() {
		return uid;
	}    
    
}
