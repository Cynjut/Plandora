package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pandora.MetaFieldTO;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.XmlDomParse;

/**
 * This class handle the data of Meta Field Form 
 */
public class MetaFieldForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** The meta Field's name */
    private String name;
    
    /** The meta Field's type */
    private String type;

    /** The project related with meta field */
    private String projectId;

    /** The category related with meta field */
    private String categoryId;
    
    /** The form destination of metaField */
    private String applyTo;
    
    /** The list of meta Field's option or default value query used to get data from DB **/
    private String domain;

    /** If 'on' the current metaField should't be displayed in any GUI */
    private String isDisabledRequest = "off";
    
    /** If 'on' the category Combo List is disabled */
    private String isDisableCategory = "off";

    /** If 'on' the project Combo List is disabled */    
    private String isDisableProject = "off";
    
    /** The Meta Form related with current meta field */
    private String metaFormId;

    private String helpContent;
    
    
    /**
     * Clear values of Meta Field Form
     */
    public void clear(){
        this.name = null;        
        this.type = null;
        this.projectId = null;
        this.domain = null;
        this.isDisabledRequest = "off";
        this.isDisableCategory = "off";
        this.isDisableProject = "off";
        this.setSaveMethod(null, null);
        this.metaFormId = null;
        this.helpContent = null;
    }    
    
    ///////////////////////////////////////////
    public String getDomain() {
        return domain;
    }
    public void setDomain(String newValue) {
        this.domain = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }

    ///////////////////////////////////////////    
    public String getApplyTo() {
        return applyTo;
    }
    public void setApplyTo(String newValue) {
        this.applyTo = newValue;
    }       
    
    ///////////////////////////////////////////    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }
    
    ///////////////////////////////////////////        
    public String getIsDisabledRequest() {
        return isDisabledRequest;
    }
    public void setIsDisabledRequest(String newValue) {
        this.isDisabledRequest = newValue;
    }
    
    ///////////////////////////////////////////   
    public String getIsDisableCategory() {
        return isDisableCategory;
    }
    public void setIsDisableCategory(String newValue) {
        this.isDisableCategory = newValue;
    }
    
    
    ///////////////////////////////////////////   
    public String getIsDisableProject() {
        return isDisableProject;
    }
    public void setIsDisableProject(String newValue) {
        this.isDisableProject = newValue;
    }    
    
    
    ///////////////////////////////////////////           
    public String getMetaFormId() {
        return metaFormId;
    }
    public void setMetaFormId(String newValue) {
        this.metaFormId = newValue;
    }
    
    
    ///////////////////////////////////////////     
	public String getHelpContent() {
		return helpContent;
	}
	public void setHelpContent(String newValue) {
		this.helpContent = newValue;
	}    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String[] options = null;
	
		if (this.operation.equals("saveMetaField")) {
			int typeNum = Integer.parseInt(this.type);		    
		    
		    if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formMetaField.blankName") );
		    }
		    
		    if (this.domain==null || this.domain.trim().equals("")){
		        if (typeNum==MetaFieldTO.TYPE_COMBO_BOX) {
		            errors.add("Domain", new ActionError("validate.formMetaField.cBoxDomainInvalid") );
		        } else {
		            errors.add("Domain", new ActionError("validate.formMetaField.blankDomain") );    
		        }
		        
		    } else {
		        options = this.domain.split("\\|");
		        int numValues = options.length;
		        
			    if (typeNum==MetaFieldTO.TYPE_TEXT_BOX) {
			        
			        int maxLenght = 0;
			        boolean isValisEnableStatus = false;
			        String defValue = "";
			        try {
			            maxLenght = Integer.parseInt(options[0]);
			            isValisEnableStatus = (options[1].equalsIgnoreCase("TRUE") || options[1].equalsIgnoreCase("FALSE"));
			            defValue = options[2];
			        }catch(Exception e){
			            maxLenght = 0;
			            isValisEnableStatus = false;
			        }
			        
			        if (options.length!=3 || maxLenght<=0 || !isValisEnableStatus) {
			            errors.add("Domain", new ActionError("validate.formMetaField.tBoxDomainInvalid") );        
			        } 
			        
			        if (maxLenght>50 || defValue.length()>50) {
			            errors.add("Domain", new ActionError("validate.formMetaField.tBoxMaxLenght") );
			        }			            
			        
			    } else if (typeNum==MetaFieldTO.TYPE_CALENDAR) {
			    	boolean isValisEnableStatus = false;
			    	int maxLenght = 0;
			    	try {
			    		maxLenght = Integer.parseInt(options[0]);
			    		DateUtil.getDateTime(DateUtil.getNow(), options[3]); // check if date format is valid
			    		isValisEnableStatus = (options[1].equalsIgnoreCase("TRUE") || options[1].equalsIgnoreCase("FALSE"));	
			    	}catch(Exception e){
			    		maxLenght = 0;
			    		isValisEnableStatus = false;
			    	}
			        if (options.length!=4 || maxLenght<=0 || !isValisEnableStatus) {
			            errors.add("Domain", new ActionError("validate.formMetaField.calendarDomainInvalid") );        
			        } 
			        
			    } else if (typeNum==MetaFieldTO.TYPE_COMBO_BOX) {
			        if (numValues % 2 != 0) {
			            errors.add("Domain", new ActionError("validate.formMetaField.cBoxDomainInvalid") );    
			        }
			        
			    } else if (typeNum==MetaFieldTO.TYPE_SQL_COMBO_BOX) {
			        long initTime = 0, finalTime = 0;
			        
			        //check if SQL is valid
			        try {
			            DbQueryDelegate dbExec = new DbQueryDelegate();
			            initTime = System.currentTimeMillis();
			            dbExec.performQuery(this.domain);
			            finalTime = System.currentTimeMillis();
			        } catch (BusinessException e) {
			            errors.add("Domain", new ActionError("validate.formMetaField.scBoxDomainInvalid") );
			        }
			        
			        //check if SQL has a reasonable performance			        
			        if (finalTime>0) {
			            if ((finalTime-initTime)>1000) {
			                errors.add("Domain", new ActionError("validate.formMetaField.scBoxDomainBadSQL") );    
			            }
			        }
			        
			    } else if (typeNum==MetaFieldTO.TYPE_TEXT_AREA) {
				        
			        int colNum = 0, rowNum = 0;
			        boolean isValisEnableStatus = false;
			        try {
			            colNum = Integer.parseInt(options[0]);
			            rowNum = Integer.parseInt(options[1]);
			            isValisEnableStatus = (options[2].equalsIgnoreCase("TRUE") || options[2].equalsIgnoreCase("FALSE"));
			        }catch(Exception e){
			            colNum = 0; rowNum = 0;
			            isValisEnableStatus = false;
			        }
			        
			        if (options.length!=4 || colNum<=0 || rowNum<=0 || !isValisEnableStatus) {
			            errors.add("Domain", new ActionError("validate.formMetaField.tAreaDomainInvalid") );        
			        }
			        
			    } else if (typeNum==MetaFieldTO.TYPE_TABLE) {
			    	boolean isOk = true;
			    	try {
			    		Document doc = XmlDomParse.getXmlDom(this.domain);
			    		Node grid = doc.getFirstChild();
			    		NodeList childs = grid.getChildNodes();
			    		for (int i=0; i<childs.getLength(); i++) {
			    			Node node = childs.item(i);
			    			if (node!=null && node.getNodeName()!=null && node.getNodeName().equalsIgnoreCase("row")) {
			    				NodeList cols = node.getChildNodes();
			            		for (int j=0; j<cols.getLength(); j++) {
			            			Node colNode = cols.item(j);
					    			if (colNode!=null && colNode.getNodeName()!=null && colNode.getNodeName().equalsIgnoreCase("col")) {
			                			String colType = XmlDomParse.getAttributeTextByTag(colNode, "type");
			                			if ("[1] [2] [3] [4] [5]".indexOf(colType.trim())==-1) {
			                				errors.add("Domain", new ActionError("validate.formMetaField.gridDomainInvalidType") );
			                				break;
			                			}					    				
					    			}
			            		}
			    			}
			    		}
			    	}catch(Exception e) {
			    		isOk = false;
			    	}
			    	if (!isOk) {
			    		errors.add("Domain", new ActionError("validate.formMetaField.gridDomainInvalid") );
			    	}
			    } 
		    }
		}
		
		return errors;
	}
    
}
