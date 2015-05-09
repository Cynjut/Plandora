package org.apache.taglibs.display;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import com.pandora.PreferenceTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator;

public class ResourceTaskColumnTag extends ColumnTag implements Cloneable {

	private static final long serialVersionUID = 1L;

	private Vector resourceTitle = null;
	
	private String[] columnsContent = null;
	
	
    public int getColumnsNumber() {
    	this.resourceTitle = new Vector();
    	this.generateMatrix();
        return resourceTitle.size();
    }

    
    protected StringBuffer getBody(ColumnDecorator decorator, int rowcnt,
            String pageParam, Decorator dec, Properties prop)
            throws JspException {
    	StringBuffer buff = new StringBuffer("");
    	Vector content = this.getTableData();
		boolean hideCanceledTask = false;
		
        if (content!=null && content.size()>rowcnt) {
        	this.columnsContent = null;
        	AgilePanelTaskDecorator taskDec = new AgilePanelTaskDecorator();
            RequirementWithTasksTO rwto = (RequirementWithTasksTO)content.get(rowcnt);
            
            UserTO uto = (UserTO)this.pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
			PreferenceTO pto = uto.getPreference();
			try {
				hideCanceledTask = (pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_TSK)).equals("on");
			} catch (Exception e){
				hideCanceledTask = false;
			}
            
        	this.columnsContent = new String[resourceTitle.size()];
           	for (int j = 0; j<this.columnsContent.length; j++) {
           		this.columnsContent[j] = "&nbsp;";           		
           	}
			
            Vector tasks = rwto.getResourceTaskList();
            if (tasks!=null) {
            	
                Iterator i = tasks.iterator();
                while(i.hasNext()) {
                    ResourceTaskTO rtto = (ResourceTaskTO)i.next();
                    if (rtto.getResource()!=null && rtto.getResource().getId()!=null) { //separate macro tasks...
                        TaskStatusTO tsto = rtto.getTaskStatus();

                        boolean isCancel = false;
                        if (tsto!=null && tsto.getStateMachineOrder()!=null) {
                            isCancel = (tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CANCEL));
                        }
        				if (!isCancel || !hideCanceledTask) {
                        	String box = taskDec.getBox(rtto, rtto.getTaskStatus(), false, rtto.getResource().getLocale(), pto);                	
                        	int idx = this.getIndexOfResource(rtto);
                        	this.columnsContent[idx] = this.columnsContent[idx] + box + "&nbsp;";                	    					
        				}                    	                                        	
                    }
                }
            }
            
            if (this.columnsContent!=null) {
            	for (int j = 0; j<this.columnsContent.length; j++) {
                	this.setValue(this.columnsContent[j]);
                	buff.append(super.getBody(decorator, rowcnt, pageParam, dec, prop));            		
            	}
            }
        }
        return buff; 
    }
    
    
    protected StringBuffer getHeader(int sortOrder, String sortAttr, int colNumber,
            String anchorParam, String url) {
        StringBuffer buff = new StringBuffer("");
        
        if (resourceTitle!=null) {
            Iterator i = resourceTitle.iterator();
            while(i.hasNext()) {
                ResourceTO rto = (ResourceTO)i.next();
                if (rto!=null && rto.getId()!=null && rto.getUsername()!=null) {
                    if (rto.getUsername().equals("root")) {
                    	this.setTitle(" ");	
                    } else {
                    	this.setTitle(rto.getUsername());
                    }
                    buff.append(super.getHeader(sortOrder, sortAttr, colNumber, anchorParam, url));                	
                }
            }
        }
        return buff;
    }    
    
    
    private int getIndexOfResource(ResourceTaskTO rtto) {
    	int response = 0;
        if (resourceTitle!=null) {
            Iterator i = resourceTitle.iterator();
            while(i.hasNext()) {
                ResourceTO rto = (ResourceTO)i.next();
                if (rtto.getResource().getId().equals(rto.getId())) {
                	break;
                }
                response++;
            }
        }
    	return response;
    }
    
    
    private void generateMatrix(){
    	HashMap distinctList = new HashMap();
    	
    	Vector content = this.getTableData();
    	if (content!=null) {
    		Iterator i = content.iterator();
    		while(i.hasNext()) {
    			RequirementWithTasksTO rwto = (RequirementWithTasksTO)i.next();
    			if (rwto.getResourceTaskList()!=null) {
    				Iterator j = rwto.getResourceTaskList().iterator();
    				while(j.hasNext()) {
    					ResourceTaskTO rtto = (ResourceTaskTO)j.next();
    					ResourceTO rto = rtto.getResource();
    					if (distinctList.get(rto.getId())==null){
    						distinctList.put(rto.getId(), rto);
    						this.resourceTitle.addElement(rto);
    					}
    				}
    			}
    		}
    	}
    }
    
  
}
