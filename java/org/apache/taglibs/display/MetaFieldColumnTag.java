package org.apache.taglibs.display;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import com.pandora.AdditionalFieldTO;
import com.pandora.MetaFieldTO;
import com.pandora.PlanningTO;
import com.pandora.ResourceTaskTO;

/**
 * This class is used in order to show the meta fields values dinamically. <br>
 * If the current project, 
 */
public class MetaFieldColumnTag extends ColumnTag implements Cloneable {
    
	private static final long serialVersionUID = 1L;
	
    /** The elements of the vector is the title of each sub-column of Meta Field column */
    private Vector metaFieldsTitle = null;
    
    /** Vector of AdditionalFieldTO array.
     * The itens of vector stand for the rows of table.
     * The elements into array stand for the columns of table */
    private Vector additionalFields = null;
    
    private String comboFilterId = null;
    
    
    public int getColumnsNumber() {
        int col = 1;
        
        //first all, process data from parent table...
        this.metaFieldsTitle = new Vector();
        this.additionalFields = null;
        this.generateMatrix();
        
        //get the number of sub-collums of Meta Field column
        if (metaFieldsTitle.size()>0) {
            col = metaFieldsTitle.size();
        }
        
        return col;
    }

    
    protected StringBuffer getBody(ColumnDecorator decorator, int rowcnt,
            String pageParam, Decorator dec, Properties prop)
            throws JspException {
        StringBuffer buff = new StringBuffer("");
                
        if (this.additionalFields!=null) {
            AdditionalFieldTO[] array = (AdditionalFieldTO[])this.additionalFields.elementAt(rowcnt);
            for (int j=0; j < array.length; j++){
                AdditionalFieldTO afto = array[j];
                if (afto!=null) {
                    MetaFieldTO mfto = afto.getMetaField();
                    String text = mfto.getValueByKey(afto.getValue());
                    this.setValue(text);
                } else {
                    this.setValue("");
                }
                buff.append(super.getBody(decorator, rowcnt, pageParam, dec, prop));                    
            }            
        }
        return buff; 
    }
    
    
    protected StringBuffer getHeader(int sortOrder, String sortAttr, int colNumber,
            String anchorParam, String url) {
        StringBuffer buff = new StringBuffer("");

        if (additionalFields!=null) {
        	int col = 0;
            Iterator i = metaFieldsTitle.iterator();
            while(i.hasNext()) {
                MetaFieldTO mfto = (MetaFieldTO)i.next();
                this.setTitle(mfto.getName());
             
                buff.append(super.getHeader(sortOrder, sortAttr, (colNumber+col), anchorParam, url));
                
                col++;
            }            
        }
        return buff;
    }
        
    
    /**
     * Prepare data of metafield to be displayed into column  
     */
    private void generateMatrix(){
        Vector tableData = null;        
        boolean thereIsBadElement = false;
        HashMap hm = new HashMap();
        
        tableData = this.getTableData();
        if (tableData!=null) {
            
            //populate the metaFieldsTitle vector
            Iterator i = tableData.iterator();
            while(i.hasNext()) {
                Object obj = i.next();                
                PlanningTO planning = this.getPlanning(obj);
                
                //check if obj is a PlanningTO object
                if (planning!=null) {
                    Vector addFields = planning.getAdditionalFields();
                    this.checkTitleNotExists(addFields);
                    if (planning.getGridRowNumber()>=0) {
                    	hm.put("" + planning.getGridRowNumber(), planning);	
                    }
                } else { 
                    thereIsBadElement = true;                    
                }
            }

            //populate the metaFields vector of array
            if (!thereIsBadElement) {
            	if (hm.values().size()>0) {
                	for (int j=0; j<tableData.size(); j++) {
                		PlanningTO curr = null;
               			curr = (PlanningTO)hm.get(j+"");
               			if (curr!=null) {
                            Vector addFields = curr.getAdditionalFields();
                            this.allocDataIntoArray(addFields);               				
               			}
                    }            		
            	} else {
                    Iterator j = tableData.iterator();
                    while(j.hasNext()) {
                    	PlanningTO curr = this.getPlanning(j.next());                    	
                        Vector addFields = curr.getAdditionalFields();
                        this.allocDataIntoArray(addFields);
                    }            	            		
            	}
            }
            
        }
        
        //if there is an element into list that is not a child of PlanningTO, 
        //it is better give up...
        if (thereIsBadElement) {
            this.additionalFields = null;    
        }        
    }

    
    private PlanningTO getPlanning(Object obj){
        PlanningTO response = null;
        if (obj instanceof PlanningTO) {
        	response = (PlanningTO)obj;
        } else if (obj instanceof ResourceTaskTO) {
        	response = ((ResourceTaskTO)obj).getTask();
        }
    	return response;
    }
    
    
    private void allocDataIntoArray(Vector addFields){
        
        //create a new entry into metaFields vector because 
        //each method calling (this method!) is a row of table processed 
        AdditionalFieldTO[] array = new AdditionalFieldTO[this.metaFieldsTitle.size()]; 
        if (this.additionalFields==null) {
            this.additionalFields = new Vector();
        }
        this.additionalFields.addElement(array);
        

        if (addFields!=null && addFields.size()>0) {
            Iterator i = addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO afto =(AdditionalFieldTO)i.next();
                MetaFieldTO mfto = afto.getMetaField();
                
                for (int index=0; index<this.metaFieldsTitle.size(); index++){
                    MetaFieldTO mfTitle = (MetaFieldTO)this.metaFieldsTitle.elementAt(index);
                    if (mfTitle.getId().equals(mfto.getId()) || mfTitle.getName().equals(mfto.getName())){
                        array[index] = afto;
                        break;
                    }
                }

                if (comboFilterId!=null) {
                	String valueFilterId = (String)pageContext.findAttribute(comboFilterId);
                	if (valueFilterId==null) {
                		valueFilterId = (String)pageContext.getRequest().getParameter(comboFilterId);	
                	}
                	if (valueFilterId!=null && valueFilterId.equals(mfto.getId())){
                		super.setComboFilter(true);
                	}
                }                
            }
        }        
    }
    
    
    /**
     * Verify if a specific meta field object is already into the index (meta field titles) 
     * @return
     */
    private void checkTitleNotExists(Vector addFields){
        
        if (addFields!=null && addFields.size()>0) {
            Iterator i = addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO afto =(AdditionalFieldTO)i.next();
                MetaFieldTO mfto = afto.getMetaField();
                boolean alreadyExists = false;
                
                Iterator j = this.metaFieldsTitle.iterator();
                while (j.hasNext()) {
                    MetaFieldTO mfTitle = (MetaFieldTO)j.next();
                    if (mfTitle.getId().equals(mfto.getId()) || mfTitle.getName().equals(mfto.getName())){                    	
                        alreadyExists = true;
                        break;
                    }
                }
                
                if (!alreadyExists){
                    this.metaFieldsTitle.addElement(mfto);
                }
            }
        }
    }
    
    
    //////////////////////////////////////////////
    public void setComboFilterId(String newValue ) {
        this.comboFilterId = newValue; 
    }
    public String getComboFilterId() { 
        return this.comboFilterId; 
    }    
    
}
