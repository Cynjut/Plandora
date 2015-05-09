package com.pandora.bus.alert;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

public class NewTaskNotification extends Notification {

	private static final String NEWTASK_PROJECT_ID  = "NEWTASK_PROJ_ID";
    private static final String NEWTASK_CATEGORY_ID = "NEWTASK_CAT_ID";
    private static final String NEWTASK_NAME        = "NEWTASK_NAME";
    private static final String NEWTASK_DESC        = "NEWTASK_DESC";
    private static final String NEWTASK_EST_TIME    = "NEWTASK_EST_TIME";
    private static final String NEWTASK_EST_DATE    = "NEWTASK_EST_DATE";
    private static final String NEWTASK_RES_ID      = "NEWTASK_RES_ID";

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
    	boolean response = false;
    	EventBUS bus = new EventBUS();
		ProjectDelegate pdel = new ProjectDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		TaskDelegate tdel = new TaskDelegate();
		UserDelegate udel = new UserDelegate();
		
		for (int i=1; i<sqlData.size(); i++) {
			Vector sqlDataItem = (Vector)sqlData.elementAt(i);
			
			String projectId = this.getParamByKey(NEWTASK_PROJECT_ID, fields);
			String categoryId = this.getParamByKey(NEWTASK_CATEGORY_ID, fields);
			String name = this.getParamByKey(NEWTASK_NAME, fields);
			String desc = this.getParamByKey(NEWTASK_DESC, fields);
			String estTime = this.getParamByKey(NEWTASK_EST_TIME, fields);
			String estDate = this.getParamByKey(NEWTASK_EST_DATE, fields);
			String resId = this.getParamByKey(NEWTASK_RES_ID, fields);
			Locale loc = new Locale("pt", "BR");

			//replace the wildcards with the fields...
			projectId = super.replaceByToken(sqlDataItem, projectId);
			categoryId = super.replaceByToken(sqlDataItem, categoryId);
			name = super.replaceByToken(sqlDataItem, name);
			desc = super.replaceByToken(sqlDataItem, desc);
			estTime = super.replaceByToken(sqlDataItem, estTime);
			estDate = super.replaceByToken(sqlDataItem, estDate);
			resId = super.replaceByToken(sqlDataItem, resId);
			
			if (projectId!=null && categoryId!=null && name!=null && estTime!=null && estDate!=null
					&& !projectId.trim().equals("") && !categoryId.trim().equals("") && !name.trim().equals("")
					&& !estTime.trim().equals("") && !estDate.trim().equals("")) {

				float time = StringUtil.getStringToFloat(estTime, loc);
				if (time>0) {
					Timestamp startDate = DateUtil.getDateTime(estDate, "yyyyMMdd", new Locale("pt", "BR"));
					if (startDate!=null) {
						ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId.trim()), false);
						if (pto!=null) {
							CategoryTO cto = cdel.getCategory(new CategoryTO(categoryId.trim()));
							if (cto!=null) {
								UserTO uto = udel.getRoot();
						    	TaskTO tto = createTaskObject(name, desc, pto, cto, uto);
						    	
						    	if (resId==null || resId.trim().equals("")) {
						    		resId = uto.getId();
						    	}

						    	ResourceTaskTO rtto = createResourceTask(resId, startDate, time, pto, uto, tto);					    	
						    	Vector restaskList = new Vector();
						    	restaskList.add(rtto);
						    	tto.setAllocResources(restaskList);
						    	
						    	tdel.insertTask(tto);
						    	response = true;

						        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The Task [" + 
						        		categoryId + "] was created successfully.", RootTO.ROOT_USER, null);								

							} else {
						        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The category id [" + 
						        		categoryId + "] was not found.", RootTO.ROOT_USER, null);								
							}
						} else {
					        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The project id [" + 
					        		projectId + "] was not found.", RootTO.ROOT_USER, null);			
						}				
					} else {
				        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The estimated date field [" + 
				        		estDate + "] is invalid.", RootTO.ROOT_USER, null);							
					}				
				} else {
			        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The estimated time field [" + 
			        		estTime + "] is invalid.", RootTO.ROOT_USER, null);											
				}
			} else {
		        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "There are empty fields [" + 
		        		projectId + "] [" + categoryId + "] [" + name + "] [" + estTime + "] [" + 
		        		estDate + "]", RootTO.ROOT_USER, null);			
			}			
		}
		
        return response;
    }


    private ResourceTaskTO createResourceTask(String resId, Timestamp startDate, float time, 
    		ProjectTO pto, UserTO uto, TaskTO tto) throws BusinessException{
		UserDelegate udel = new UserDelegate();
		TaskStatusDelegate tsdel = new TaskStatusDelegate();
		
    	ResourceTaskTO rtto = new ResourceTaskTO();

    	ResourceTO rto = new ResourceTO(resId);
    	rto.setProject(pto);
    	rto = udel.getResource(rto);
    	
    	TaskStatusTO status = tsdel.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_OPEN);
    	rtto.setBillableStatus(new Boolean(false));
    	rtto.setHandler(uto);
    	rtto.setResource(rto);
    	rtto.setStartDate(startDate);
    	rtto.setEstimatedTime(new Integer((new Float(time)).intValue()));			    	
    	rtto.setTask(tto);
    	rtto.setTaskStatus(status);
		return rtto;
    }
    
	private TaskTO createTaskObject(String name, String desc, ProjectTO pto,
			CategoryTO cto, UserTO uto) {
		TaskTO tto = new TaskTO();
		
		tto.setAdditionalFields(null);
		tto.setAttachments(null);
		tto.setComment(null);
		tto.setDecisionNode(null);
		tto.setDiscussionTopics(null);
		tto.setIteration(null);
		tto.setRelationList(null);
		tto.setTemplateInstanceId(null);
		
		tto.setCategory(cto);
		tto.setCreatedBy(uto);
		tto.setCreationDate(DateUtil.getNow());
		tto.setDescription(desc);
		tto.setFinalDate(null);
		tto.setHandler(uto);
		tto.setIsParentTask(new Integer(0));
		tto.setIsUnpredictable(new Boolean(true));
		tto.setName(name);
		tto.setParentTask(null);
		tto.setProject(pto);
		tto.setRequirement(null);
		tto.setType(PlanningRelationTO.ENTITY_TASK);
		return tto;
	}
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFields()
     */        
    public Vector getFields(){
        Vector response = new Vector();
        response.add(new FieldValueTO(NEWTASK_PROJECT_ID, "notification.newTask.project", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        response.add(new FieldValueTO(NEWTASK_CATEGORY_ID, "notification.newTask.category", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        response.add(new FieldValueTO(NEWTASK_NAME, "notification.newTask.name", FieldValueTO.FIELD_TYPE_TEXT, 50, 50));
        response.add(new FieldValueTO(NEWTASK_DESC, "notification.newTask.desc", FieldValueTO.FIELD_TYPE_AREA, 85, 4));
        response.add(new FieldValueTO(NEWTASK_EST_DATE, "notification.newTask.estdate", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        response.add(new FieldValueTO(NEWTASK_EST_TIME, "notification.newTask.esttime", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        response.add(new FieldValueTO(NEWTASK_RES_ID, "notification.newTask.resid", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        
        return response;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "New Task";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.newTask.help";
    }

    
    
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector getFieldTypes() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector getFieldKeys() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector getFieldLabels() {
        return null;
    }
    
}
