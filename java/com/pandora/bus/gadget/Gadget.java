package com.pandora.bus.gadget;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.SessionUtil;


public class Gadget {

	protected UserTO handler;
	
	private int width = 250;
	
	
	public String getUniqueName(){
		return null;
	}

	public String getId(){
		return null;
	}

    public Vector<TransferObject> getFieldsId(){
        return null;
    }

    public Vector getFields(){
        return null;
    }
    
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 200;
    }
    
	public int getWidth(){
		return width;
	}
	public void setWidth(int newValue){
		width = newValue;
	}
	
	public int getHeight(){
		return 200;
	}    

	public boolean showMaximizedOption(){
		return true;
	}    
	
	public String getCategory(){
		return "label.manageOption.gadget.other";
	}

	public String getDescription(){
		return null;
	}

	public String getImgLogo(){
		return "../images/gdglogo.png";
	}

	public boolean canReloadFields(){
		return false;
	}

    public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues){
        return null;
    }

	
	public final void perform(HttpServletRequest request, HttpServletResponse response, Vector<String> overrideParam) throws BusinessException{
		try {
			
			this.handler = SessionUtil.getCurrentUser(request);
			//this.handler.setLanguage(SessionUtil.getCurrentLocale(request).getLanguage());
			//this.handler.setCountry(SessionUtil.getCurrentLocale(request).getCountry());
			
			//get the current selected paramters...
			if (overrideParam==null) {
				UserTO uto = SessionUtil.getCurrentUser(request);
				Vector ids = this.getSelectedIds(uto);
				
				//process the gadget body...
				process(request, response, ids);
				
			} else {
				
				//process the gadget body...
				process(request, response, overrideParam);

			}
						
		}catch(Exception e){
			throw new  BusinessException(e);
		}
	}

	protected Vector getSelectedIds(UserTO uto) {
		Vector ids = this.getFieldsId();
		if (ids!=null) {
			Iterator i = ids.iterator();
			while(i.hasNext()) {
				TransferObject idField = (TransferObject)i.next();
				String selected = uto.getPreference().getPreference(this.getId() + "." + idField.getId());
				if (selected!=null || selected.trim().length()>0) {
					idField.setGenericTag(selected);
				}
			}
		}
		return ids;
	}
	
	
	public void process(HttpServletRequest request, HttpServletResponse response, Vector selectedFields) throws BusinessException{
		throw new  BusinessException("This method must be implemented by sub-class.");		
	}


	public StringBuffer gadgetToHtml(HttpServletRequest request, int w, int h, String loadingLabel) throws BusinessException{
		throw new  BusinessException("This method must be implemented by sub-class.");		
	}
	
	
	protected String getProjectIn(String id) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		return pdel.getProjectIn(id);
	}
	
	
	protected Vector<TransferObject> getProjectFromUser(boolean isAlloc) throws BusinessException{
    	ProjectDelegate pdel = new ProjectDelegate();
    	Vector<ProjectTO> buff = pdel.getProjectListForWork(this.handler, isAlloc);
    	
    	Vector<TransferObject> projList = new Vector<TransferObject>();
    	TransferObject defaultOpt = new TransferObject("-1", "label.combo.select");
    	projList.addElement(defaultOpt);
    	
    	if (buff!=null) {
    		projList.addAll(buff);   		
    	}
    	
		return projList;
	}	

	protected String getI18nMsg(String keyMsg){
		return getI18nMsg(keyMsg, null);
	}
	
	protected String getI18nMsg(String keyMsg, Locale loc){
		String response = null;
		try {
			Locale lc = loc;
			if (lc==null) {
				lc = this.handler.getLocale(); 
			}
			
			if (this.handler!=null && this.handler.getBundle()!=null && lc!=null) {
				response = this.handler.getBundle().getMessage(lc, keyMsg);
		        if (response.startsWith("???")) {
		        	response = keyMsg;
		        }		    
			}			
		} catch (Exception e) {
			response = keyMsg;
		}
		return response;
	}
	
	
	public static final String getSelected(String id, Vector<TransferObject> selectedFields) {
		String response = "";
		if (selectedFields!=null) {
			Iterator<TransferObject> i = selectedFields.iterator();
			while(i.hasNext()) {
				TransferObject idField = (TransferObject)i.next();
				if (idField.getId().equals(id)){
					response = idField.getGenericTag();	
				}
			}
		}
		return response;
	}
	
}
