package com.pandora.gui.struts.form;

import java.util.Vector;

/**
 * This class handle the data of Gantt viewer 
 */
public class GanttViewerForm extends GeneralStrutsForm{
    
	private static final long serialVersionUID = 1L;
	
    /** constant used by viewer type to show Gantt for project */
    public static final String PROJECT_GANTT = "1";

    /** constant used by viewer type to show Gantt for requirement */    
    public static final String SHOW_REQ_GANTT = "2";
    
    /** constant used by viewer type to show Gantt for resource */    
    public static final String SHOW_RES_GANTT = "3";

    
    
    /** Type of gantt viewer (mandatory fields for all requests) */
    private String type;
    
    /** Id of project related with gantt (by project) */
    private String projectId;

    /** Id of requirement related with gantt (by requirement) */    
    private String requirementId;
    
    /** Id of resource related with gantt (by resource) */
    private String resourceId;
    
    /** The language of user that is viewing the gantt */  
    private String language;
   
    /** The country of user that is viewing the gantt */
    private String country;

    /** The username of user that is viewing the gantt */  
    private String username;
   
    /** The password of user that is viewing the gantt */
    private String password;

    
    /** The number os slots of gantt chart */
    private String slots;
    
    /** The date of gantt chart first slot */
    private String initialDate;
    
    /** If current user is a leader of project, this field = "true"*/
    private String editable;
    
    /** The server URI used by applet to send data to server */
    private String serverURI;
 
    /** The number of gantt resources (left collumn of gantt) <br> 
     * 	&lt;param name="RESNUM" value="6" /&gt;
     */    
    private String resNum;
    
    /** The information of gantt resources in PARAM applet format <br> 
     *  &lt;param name="RES_1" value="id|name|description|number_of_resources_envolved" /&gt; 
     */
    private String resBody;
    
    /** The number of gantt layer <br> 
     * 	&lt;param name="LAYERNUM" value="4" /&gt;
     */    	
    private String layerNum;

    /** The information of gantt layer in PARAM applet format <br> 
     *  &lt; param name="LAYER_1" value="id|sequence_number|user_name|name|color in RGB format" /&gt; 
     */	
    private String layerBody;

    /** The number of gantt jobs <br> 
     * 	&lt;param name="JOBNUM" value="16" /&gt;
     */    	
    private String jobNum;

    /** The information of gantt job in PARAM applet format <br> 
     *  &lt; param name="JOB_1" value="id|resource_id|job_name|job_Description|layer_id" /&gt; 
     */	
    private String jobBody;
    
    /** The number of gantt alloc units <br> 
     * 	&lt;param name="AUNITNUM" value="19" /&gt;
     */    
    private String allocUnitNum;

    /** The information of gantt alloc unit in PARAM applet format <br> 
     *  &lt; param name="AUNIT_1" value="job_id|res_id|capacity_value|ini_slot|final_slot" /&gt; 
     */		
    private String allocUnitBody;
        
    /** The number of job dependences <br> 
     * 	&lt;param name="DEPNUM" value="3" /&gt;
     */    
    private String depedenceNum;

    /** The information of dependence in PARAM applet format <br> 
     *  &lt; param name="DEP_1" value="job_id_dependent|res_id_dependent|job_id_master|res_id_master|arrow_type /&gt; 
     */		
    private Vector depedenceBody;

    
    /**
     * Clear values of Tranfer Object
     */
    public void clear(){
        language = "pt";
        country = "BR";
        slots = "";
        initialDate = "";
        resNum = "";
        resBody = "";
        layerNum = "";
        layerBody = "";
        jobNum = "";
        jobBody = "";
        allocUnitNum = "";
        allocUnitBody = "";
        editable = "FALSE";
    } 
    
    
    /////////////////////////////////////////////
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getAllocUnitBody() {
        return allocUnitBody;
    }
    public void setAllocUnitBody(String newValue) {
        this.allocUnitBody = newValue;
    }

    /////////////////////////////////////////////    
    public String getRequirementId() {
        return requirementId;
    }
    public void setRequirementId(String newValue) {
        this.requirementId = newValue;
    }

    /////////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getAllocUnitNum() {
        return "<param name=\"AUNITNUM\" value=\""+ this.allocUnitNum + "\" />";
    }
    public void setAllocUnitNum(String newValue) {
        this.allocUnitNum = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getCountry() {
        return "<param name=\"COUNTRY\" value=\"" + country + "\" />";
    }
    public void setCountry(String newValue) {
        this.country = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getInitialDate() {
        return "<param name=\"INITIALDATE\" value=\""+ initialDate + "\" />";
    }
    public void setInitialDate(String newValue) {
        this.initialDate = newValue;
    }

    /////////////////////////////////////////////
    public String getJobBody() {
        return jobBody;
    }
    public void setJobBody(String newValue) {
        this.jobBody = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getJobNum() {
        return "<param name=\"JOBNUM\" value=\""+ jobNum + "\" />";
    }
    public void setJobNum(String newValue) {
        this.jobNum = newValue;
    }
    
    /////////////////////////////////////////////
    public String getLanguage() {
        return "<param name=\"LANGUAGE\" value=\""+ language + "\" />";
    }
    public void setLanguage(String newValue) {
        this.language = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getLayerBody() {
        return layerBody;
    }
    public void setLayerBody(String newValue) {
        this.layerBody = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getLayerNum() {
        return "<param name=\"LAYERNUM\" value=\""+ layerNum + "\" />";
    }
    public void setLayerNum(String newValue) {
        this.layerNum = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getResBody() {
        return resBody;
    }
    public void setResBody(String newValue) {
        this.resBody = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getResNum() {
        return "<param name=\"RESNUM\" value=\""+ resNum + "\" />";
    }
    public void setResNum(String newValue) {
        this.resNum = newValue;
    }
    
    /////////////////////////////////////////////
    public String getSlots() {
        return "<param name=\"SLOTS\" value=\""+ slots + "\" />";
    }
    public void setSlots(String newValue) {
        this.slots = newValue;
    }

    /////////////////////////////////////////////    
    public String getEditable() {
        return "<param name=\"EDITABLE\" value=\"" + editable + "\" />";        
    }
    public void setEditable(String newValue) {
        this.editable = newValue;
    }
    
    /////////////////////////////////////////////       
    public String getPassword() {
        return "<param name=\"PASSWORD\" value=\"" + password + "\" />";
    }
    public void setPassword(String newValue) {
        this.password = newValue;
    }
    
    /////////////////////////////////////////////          
    public String getUsername() {
        return "<param name=\"USERNAME\" value=\"" + username + "\" />";        
    }
    public void setUsername(String newValue) {
        this.username = newValue;
    }
    
    
    /////////////////////////////////////////////          
    public String getServerURI() {
        return  "<param name=\"URI\" value=\"" + serverURI + "\" />";
    }
    public void setServerURI(String newValue) {
        this.serverURI = newValue;
    }


    /////////////////////////////////////////////      
	public String getDepedenceNum() {
		return "<param name=\"DEPNUM\" value=\""+ depedenceNum + "\" />";
	}
	public void setDepedenceNum(String depedenceNum) {
		this.depedenceNum = depedenceNum;
	}

	
    /////////////////////////////////////////////   
	public String getDepedenceBody() {
		String buff = "";
		if (this.depedenceBody !=null) {
			for (int i=0; i<this.depedenceBody.size(); i++) {
				buff = buff + (String)this.depedenceBody.elementAt(i);
			}
		}
		return buff;
	}
	public void setDepedenceBody(Vector newValue) {
		this.depedenceBody = newValue;
	}
    
    
}
