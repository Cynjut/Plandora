package com.pandora.gui.taglib.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.pandora.AdditionalFieldTO;
import com.pandora.MetaFieldTO;
import com.pandora.PlanningTO;
import com.pandora.ResourceTaskTO;
import com.pandora.UserTO;

/**
 * This class is used in order to show the meta fields values dynamically. <br>
 */
public class MetaFieldPColumnTag extends PColumnTag implements Cloneable {

private static final long serialVersionUID = 1L;
	
    /** The elements of the vector is the title of each sub-column of Meta Field column */
    private Vector<MetaFieldTO> metaFieldsTitle = new Vector<MetaFieldTO>();
    
    @Override
    public int getColumnsNumber(HttpServletRequest req, String tableName) {
        int col = 0;
        
        this.checkMatrix(req, tableName);        
        //get the number of sub-columns of Meta Field column
        if (metaFieldsTitle.size()>0) {
            col = metaFieldsTitle.size();
        }
        
        return col;
    }


    @SuppressWarnings("unchecked")
	public ArrayList<String> getBody(PColumnTag column, Object line, PTableTag table, UserTO uto, boolean toBeSearch, Object additionalFields) {
    	ArrayList<String> buff = new ArrayList<String>();

    	if (metaFieldsTitle!=null) {
        	for (MetaFieldTO mftoTitle : metaFieldsTitle) {
        		String text = "";
                if (additionalFields!=null && additionalFields instanceof Vector) {
                	Vector<AdditionalFieldTO> addFields = (Vector<AdditionalFieldTO>)additionalFields;
                	for (AdditionalFieldTO afto : addFields) {
                        if (afto!=null) {
                            MetaFieldTO mfto = afto.getMetaField();
                            if (mfto.getId().equals(mftoTitle.getId())) {
                            	text = mfto.getValueByKey(afto, uto.getLocale());
                            	break;
                            }
                        }
        			}
                }
                
        		buff.add(text);            
    		}    	    		
    	}
                
        return buff; 
    }


    @Override
    public StringBuffer getHeader(HttpServletRequest req, UserTO uto, String tableName, String frmName) {
        StringBuffer buff = new StringBuffer("");

        this.checkMatrix(req, tableName);
        if (metaFieldsTitle.size()>0) {
            Iterator<MetaFieldTO> i = metaFieldsTitle.iterator();
            while(i.hasNext()) {
                MetaFieldTO mfto = i.next();
                this.setTitle(mfto.getName());
             
                buff.append(super.getHeader(req, uto, tableName, frmName));
            }            
        }
        return buff;
    }
        
    @Override    
    public ArrayList<String> getHeaderList(HttpServletRequest req, UserTO uto, String tableName){
		ArrayList<String> response = new ArrayList<String>();
		
        this.checkMatrix(req, tableName);
        if (metaFieldsTitle.size()>0) {
            Iterator<MetaFieldTO> i = metaFieldsTitle.iterator();
            while(i.hasNext()) {
                MetaFieldTO mfto = i.next();
                response.add(mfto.getName());
            }
        }
        
    	return response;
    }

	public ArrayList<MetaFieldTO> getComboFields() {
		ArrayList<MetaFieldTO> response = new ArrayList<MetaFieldTO>();
        if (metaFieldsTitle.size()>0) {
        	for (MetaFieldTO mfto : metaFieldsTitle) {
        		if (mfto.getType().equals(MetaFieldTO.TYPE_COMBO_BOX) || mfto.getType().equals(MetaFieldTO.TYPE_SQL_COMBO_BOX)) {
        			response.add(mfto);
        		}
			}
        }
    	return response;
	}

	
	private void checkMatrix(HttpServletRequest req, String tableName) {
		if (metaFieldsTitle.size()==0) {
            try {
    			this.generateMatrix(req, tableName);
    		} catch (JspException e) {
    			e.printStackTrace();
    		}        	
        }
	}

	/**
     * Prepare data of meta field to be displayed into column  
     * @throws JspException 
     */
    @SuppressWarnings({ "rawtypes" })
	private void generateMatrix(HttpServletRequest req, String tableName) throws JspException{

    	if (req!=null && tableName!=null) {
        	Collection datagrid = (Collection)req.getSession().getAttribute(tableName);
            if (datagrid!=null) {
            	
                //populate the metaFieldsTitle vector
            	for (Object line : datagrid) {
                    PlanningTO planning = this.getPlanning(line);

                    //check if obj is a PlanningTO object
                    if (planning!=null) {
                        Vector<AdditionalFieldTO> addFields = planning.getAdditionalFields();
                        this.checkTitleNotExists(addFields);
                    }				
    			}
            }
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
    

    /**
     * Verify if a specific meta field object is already into the index (meta field titles) 
     * @return
     */
    private void checkTitleNotExists(Vector<AdditionalFieldTO> addFields){
        
        if (addFields!=null && addFields.size()>0) {
            Iterator<AdditionalFieldTO> i = addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO afto =(AdditionalFieldTO)i.next();
                MetaFieldTO mfto = afto.getMetaField();
                boolean alreadyExists = false;
                
                Iterator<MetaFieldTO> j = this.metaFieldsTitle.iterator();
                while (j.hasNext()) {
                    MetaFieldTO mfTitle = j.next();
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
    
    @Override
	public PColumnTag getClone() {
    	MetaFieldPColumnTag response = new MetaFieldPColumnTag();
		response.setProperty(this.getProperty());
		response.setAlign(this.getAlign());
		response.setValue(this.getValue());
		response.setTitle(this.getTitle());
		response.setSort(this.getSort());
		response.setMaxWords(this.getMaxWords());
		response.setWidth(this.getWidth());
		response.setDecorator(this.getDecorator());
		response.setDescription(this.getDescription());
		response.setTag(this.getTag());
		response.setVisible(this.isVisible());
		response.setVisibleProperty(this.getVisibleProperty());
		response.setComboFilter(this.getComboFilter());
		response.setLikeSearching(this.getLikeSearching());
		response.metaFieldsTitle = this.metaFieldsTitle;
		return response;
	}

    public void resetMetaFieldData() {
    	this.metaFieldsTitle = new Vector<MetaFieldTO>();
    }
}
