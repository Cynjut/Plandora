package com.pandora.bus.gadget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.DbQueryDelegate;
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
				Vector<TransferObject> ids = this.getSelectedIds(uto);
				
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

	protected Vector<TransferObject> getSelectedIds(UserTO uto) {
		Vector<TransferObject> ids = this.getFieldsId();
		if (ids!=null) {
			Iterator<TransferObject> i = ids.iterator();
			while(i.hasNext()) {
				TransferObject idField = i.next();
				String selected = uto.getPreference().getPreference(this.getId() + "." + idField.getId());
				if (selected!=null && selected.trim().length()>0) {
					idField.setGenericTag(selected);
				}
			}
		}
		return ids;
	}
	
	
	public void process(HttpServletRequest request, HttpServletResponse response, Vector<?> selectedFields) throws BusinessException{
		throw new  BusinessException("This method must be implemented by sub-class.");		
	}


	public StringBuffer gadgetToHtml(HttpServletRequest request, HttpServletResponse response, int w, int h, String loadingLabel) throws BusinessException{
		throw new  BusinessException("This method must be implemented by sub-class.");		
	}
	
	
	protected String getProjectIn(String id) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		return pdel.getProjectIn(id);
	}
	
	
	protected Vector<TransferObject> getProjectFromUser(boolean isAlloc) throws BusinessException{
    	ProjectDelegate pdel = new ProjectDelegate();
    	Vector<ProjectTO> buff = pdel.getProjectListForWork(this.handler, isAlloc, true);
    	
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
	
	protected Vector<TransferObject> getAtiveUsersByProject(String projectId) throws BusinessException {
		return getAtiveUsers(null, projectId);
	}
	
	
	protected Vector<TransferObject> getAtiveUsers(String userId) throws BusinessException{
		return getAtiveUsers(userId, null);
	}
	
	
	private Vector<TransferObject> getAtiveUsers(String userId, String projectId) throws BusinessException{
    	Vector<TransferObject> userlist = new Vector<TransferObject>();
    	DbQueryDelegate qdel = new DbQueryDelegate();
    	HashMap<String, TransferObject> hm = new HashMap<String, TransferObject>();
    	
    	TransferObject defaultOpt = new TransferObject("-1", "label.combo.select");
    	TransferObject separator = new TransferObject("-1", "");
    	userlist.addElement(defaultOpt);

    	//get the current active users related to the project where 'userId' is allocated
    	String sqlData = "select distinct t.id, t.name from resource r, customer c, tool_user t " +
    			"where t.id = r.id and t.id = c.id and c.project_id = r.project_id and " +
    			"(c.is_disable = 0 or c.is_disable is null) and t.final_date is null and t.username <> 'root' and ";
    	if (userId!=null) {
    		sqlData = sqlData + "r.project_id in (select project_id from resource where id= '" + userId + "' ) ";
    	} else {
    		sqlData = sqlData + "r.project_id ='" + projectId + "' ";
    	}
    	sqlData = sqlData + "order by name";
    			
    	Vector dbAllocList = qdel.performQuery(sqlData, null, null);
    	if (dbAllocList!=null) {
    		for (int i=1; i<dbAllocList.size(); i++) {
    			Vector item = (Vector)dbAllocList.elementAt(i);
    			TransferObject to = new TransferObject((String)item.elementAt(0), (String)item.elementAt(1));
    			hm.put(to.getId(), to);
    			userlist.addElement(to);
    		}
    	}

    	//get the inactive users related to the project where 'userId' is allocated    	
    	String sqlData2 = "select distinct t.id, t.name from resource r, customer c, tool_user t " +
			"where t.id = r.id and t.id = c.id and c.project_id = r.project_id and " +
			"c.is_disable = 1 and t.username <> 'root' and t.final_date is null and ";
    	if (userId!=null) {
    		sqlData2 = sqlData2 + "r.project_id in (select project_id from resource where id= '" + userId + "' ) ";
    	} else {
    		sqlData2 = sqlData2 + "r.project_id ='" + projectId + "' ";
    	}
    	sqlData2 = sqlData2 + "order by name";
    	
    	dbAllocList = qdel.performQuery(sqlData2, null, null);
    	if (dbAllocList!=null && dbAllocList.size()>0) {
    		boolean isSep = false;

    		for (int i=1; i<dbAllocList.size(); i++) {
    			Vector item = (Vector)dbAllocList.elementAt(i);
    			TransferObject to = new TransferObject((String)item.elementAt(0), (String)item.elementAt(1));
    			if (hm.get(to.getId())==null) {
    				
    				if (!isSep) {
    		    		userlist.addElement(separator);
    		    		isSep = true;
    				}
    				
    				userlist.addElement(to);	
    			}
    		}
    	}
    	
		return userlist;
	}	
	
}
