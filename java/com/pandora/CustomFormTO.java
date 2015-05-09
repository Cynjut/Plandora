package com.pandora;


/**
 * 
 */
public class CustomFormTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

    private MetaFormTO metaForm;
        
    /**
     * Constructor 
     */
    public CustomFormTO(){
    }

    
    /**
     * Constructor 
     */
    public CustomFormTO(String id){
        this.setId(id);
    }    

   
    ////////////////////////////////////////
    public MetaFormTO getMetaForm() {
        return metaForm;
    }
    public void setMetaForm(MetaFormTO newValue) {
        this.metaForm = newValue;
    }
}
