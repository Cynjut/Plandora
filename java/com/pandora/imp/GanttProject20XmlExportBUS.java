package com.pandora.imp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.FieldValueTO;
import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.ResourceTaskBUS;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 * 
 */
public class GanttProject20XmlExportBUS extends ExportBUS {
    
    private long objId = 0;
    
    private StringBuffer alloBuff = new StringBuffer();
    
    private HashMap<String, String> hashRoles = new HashMap<String, String>();
    
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFields()
     */
    public Vector<FieldValueTO> getFields() throws BusinessException {
    	return null;
    }

       
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getUniqueName()
     */
    public String getUniqueName() {
        return "GANTT_PROJECT_20_XML_EXPORT";
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getLabel()
     */
    public String getLabel() throws BusinessException {
        return "label.importExport.ganttProject20Export";
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFileName(com.pandora.ProjectTO)
     */
    public String getFileName(ProjectTO pto) throws BusinessException {
        //format the file name suggested for ganttProject file -> ProjectNameYYYYMMDD.gan
	    Timestamp ts = DateUtil.getNow();
	    return pto.getName().replaceAll(" ", "") + DateUtil.get(ts, Calendar.YEAR) + "_" + 
	    	DateUtil.get(ts, Calendar.MONTH) + "_" +  DateUtil.get(ts, Calendar.DATE) + ".gan";  
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getContentType()
     */
    public String getContentType() throws BusinessException {
        return "application/gan; charset=" + SystemSingleton.getInstance().getDefaultEncoding();
    }    
    
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getHeader(com.pandora.ProjectTO, java.util.Vector)
     */
    public StringBuffer getHeader(ProjectTO pto, Vector fields) throws BusinessException {
	    StringBuffer response = new StringBuffer();

	    String viewDate = getStrDate(pto.getCreationDate(),"-");
	    String encoding = SystemSingleton.getInstance().getDefaultEncoding();
	    
	    response.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
	    response.append("<project name=\"" + pto.getName() + "\" company=\"\" webLink=\"\" view-date=\"" + viewDate + "\" version=\"1.11\">\n");
	    response.append("	<view zooming-state=\"default:3\"/>\n\n");
	    
	    response.append("	<!-- -->\n\n");

	    response.append("	<calendars>\n");
	    
	    response.append("		<day-types>\n");
	    response.append("			<day-type id=\"0\"/>\n");
	    response.append("			<day-type id=\"1\"/>\n");
	    
	    response.append("			<calendar id=\"1\" name=\"default\">\n");
	    response.append("				<default-week sun=\"1\" mon=\"0\" tue=\"0\" wed=\"0\" thu=\"0\" fri=\"0\" sat=\"1\"/>\n");
	    response.append("				<overriden-day-types/>\n");
	    response.append("				<days/>\n");
	    response.append("			</calendar>\n");
	    
	    response.append("		</day-types>\n");

	    response.append("	</calendars>\n\n");            

	    response.append("	<description>    </description>\n\n");
	    
	    return response;
    }

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getBody(com.pandora.ProjectTO, com.pandora.UserTO, java.util.Vector)
     */
    public StringBuffer getBody(ProjectTO pto, UserTO handler, Vector fields) throws BusinessException {
	    TaskDelegate tdel = new TaskDelegate();
	    StringBuffer response = new StringBuffer();
	    StringBuffer taskBuff = new StringBuffer();
	    this.objId = 0;
	    this.alloBuff = new StringBuffer();

	    //populate a hash of roles...
	    this.populateHashRoles(pto);
	    	    
	    //get task list related with project in tree structure
	    Vector<TaskTO> treeTskList = tdel.getTaskListByProjectInTree(pto);	    
	    Iterator<TaskTO> j = treeTskList.iterator();
	    while(j.hasNext()){	    
	        TaskTO tto = (TaskTO)j.next();
	        taskBuff.append("	" + this.getTaskExportFormat(tto));
	    }
	    
	    //put all together...
	    response.append("	<tasks color=\"#8cb6ce\">\n");
	    response.append("	<taskproperties>\n");
	    response.append("		<taskproperty id=\"tpd0\" name=\"type\" type=\"default\" valuetype=\"icon\"/>\n");
	    response.append("		<taskproperty id=\"tpd1\" name=\"priority\" type=\"default\" valuetype=\"icon\"/>\n");
	    response.append("		<taskproperty id=\"tpd2\" name=\"info\" type=\"default\" valuetype=\"icon\"/>\n");
	    response.append("		<taskproperty id=\"tpd3\" name=\"name\" type=\"default\" valuetype=\"text\"/>\n");
	    response.append("		<taskproperty id=\"tpd4\" name=\"begindate\" type=\"default\" valuetype=\"date\"/>\n");
	    response.append("		<taskproperty id=\"tpd5\" name=\"enddate\" type=\"default\" valuetype=\"date\"/>\n");
	    response.append("		<taskproperty id=\"tpd6\" name=\"duration\" type=\"default\" valuetype=\"int\"/>\n");
	    response.append("		<taskproperty id=\"tpd7\" name=\"completion\" type=\"default\" valuetype=\"int\"/>\n");
	    response.append("		<taskproperty id=\"tpd8\" name=\"coordinator\" type=\"default\" valuetype=\"text\"/>\n");
	    response.append("		<taskproperty id=\"tpd9\" name=\"predecessorsr\" type=\"default\" valuetype=\"text\"/>\n");
	    response.append("	</taskproperties>");	
	    
	    response.append(taskBuff);
	    response.append("	</tasks>\n\n");
	    
	    response.append("	<resources>\n");	    
	    response.append(this.getResources(pto));
	    response.append("	</resources>\n\n");
	    
	    response.append("	<allocations>\n");	    
	    response.append(this.alloBuff);
	    response.append("	</allocations>\n");

	    response.append("	<taskdisplaycolumns>\n");
	    response.append("		<displaycolumn property-id=\"tpd3\" order=\"0\" width=\"197\"/>\n");
	    response.append("		<displaycolumn property-id=\"tpd4\" order=\"1\" width=\"15\"/>\n");
	    response.append("		<displaycolumn property-id=\"tpd5\" order=\"2\" width=\"15\"/>\n");
	    response.append("	</taskdisplaycolumns>\n");
    	    
	    response.append(this.getRoles(handler));	    
	    
	    return response;
    }

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFooter(com.pandora.ProjectTO, java.util.Vector)
     */
    public StringBuffer getFooter(ProjectTO pto, Vector fields) throws BusinessException {
	    StringBuffer response = new StringBuffer();
	    response.append("</project>\n");	    
	    return response;
    }

    
    private StringBuffer getRoles(UserTO handler){
        StringBuffer response = new StringBuffer();
        
	    response.append("	<roles roleset-name=\"Default\"/>\n");	    
	    response.append("   <roles>\n");
	    response.append("	    <role id=\"0\" name=\"" + 
	            handler.getBundle().getMessage(handler.getLocale(), 
	                    "label.importExport.role_0") + "\"/>\n");
	    response.append("	    <role id=\"1\" name=\"" + 
	            handler.getBundle().getMessage(handler.getLocale(), 
	                    "label.importExport.role_1") + "\"/>\n");
	    response.append("	    <role id=\"2\" name=\"" + 
	            handler.getBundle().getMessage(handler.getLocale(), 
	                    "label.importExport.role_2") + "\"/>\n");
	    response.append("   </roles>\n");
	    
	    return response;
    }
    
    
	/**
	 * Get all resources allocated into specific project and 
	 * create the xml structure according of GanttProject file format.
	 * @param pto
	 * @return
	 * @throws BusinessException
	 */
	private StringBuffer getResources(ProjectTO pto) throws BusinessException{
	    StringBuffer response = new StringBuffer();
	    
	    //get list of resources allocated into project
	    UserDelegate udel = new UserDelegate();
	    Vector<ResourceTO> resList = udel.getResourceByProject(pto.getId(), true, false);
	    
	    Iterator<ResourceTO> i = resList.iterator();
	    while(i.hasNext()){
	    	ResourceTO uto = i.next();
	        response.append("		<resource id=\"" + uto.getId() + "\" name=\"" + 
	                uto.getName() + "\" function=\"" + hashRoles.get(uto.getId()) + "\" contacts=\"" + 
	                uto.getEmail() + "\" phone=\"" + uto.getPhone() + "\" />\n");
	    }
	    
	    return response;
	}
	
	
	private void populateHashRoles(ProjectTO pto){
	    Iterator<UserTO> r = pto.getAllocUsers().iterator();
	    String keyRole = "";
	    
	    while(r.hasNext()){
	        UserTO uto = r.next();
		    if (uto instanceof LeaderTO) {
		        keyRole = "2";
		    } else if (uto instanceof ResourceTO) {
		        keyRole = "1";
		    } else if (uto instanceof CustomerTO) {
		        keyRole = "0";
		    }
	        
	        hashRoles.put(uto.getId(), keyRole);
	    }
	}
			
	
    /**
     * Return the current object in GanttProject file format.
     */
    private String getAllocExportFormat(String taskId, ResourceTaskTO rtto) {
        Locale loc = new Locale("en", "us");
        String load = StringUtil.getFloatToString(rtto.getActualTimeInHours(), loc);
        return "	<allocation task-id=\"" + taskId + 
        		"\" resource-id=\"" + rtto.getResource().getId() +
        		"\" function=\"" + hashRoles.get(rtto.getResource().getId()) +
        		"\" load=\"" + load + "\"/>\n"; 
    }	
    
    /**
     * Return the current object in GanttProject file format.
     * @param loc
     * @return
     */
    public String getTaskExportFormat(TaskTO tto) {
        String response = "";
	    boolean isParentNode = (tto.getAllocResources().size()>1 || tto.getChildTasks()!=null);
	    
	    if (isParentNode) {
	        Timestamp stDate = this.getGPDate(true, tto);
	        String start = getStrDate(stDate,"-");
	        
	        Timestamp finalDate = this.getGPDate(false, tto);
	        int duration = DateUtil.getSlotBetweenDates(stDate, finalDate);

	        //get first resourceTask of task.. and create a fake task to represent the parent node        
	        response = "<task id=\"" + tto.getId() + "\" name=\"" + StringUtil.formatWordForGP(tto.getName()) + 
					"\" meeting=\"false\" start=\"" + start + "\" duration=\"" + duration + 
					"\" complete=\"0\" fixed-start=\"false\" priority=\"1\" expand=\"true\">\n";	        
	    }
        
        //format the current task...
        Iterator<ResourceTaskTO> j = tto.getAllocResources().iterator();
        while(j.hasNext()) {
            objId++;
            
            ResourceTaskTO rtto = j.next();
            String taskId = objId + "";
            
            response = response + this.getSubTaskExportFormat(taskId, tto.getName(), rtto);
            
       	 	//get all allocations for a specific task and   
            // create the xml structure according of GanttProject file format. 
            this.alloBuff.append("	" + this.getAllocExportFormat(taskId, rtto));           
        }
        
        //format the childs tasks...
        if (tto.getChildTasks()!=null){
            Iterator<TaskTO> i = tto.getChildTasks().values().iterator();
            while(i.hasNext()){
                TaskTO child = i.next();
                response = response.concat(this.getTaskExportFormat(child));
            }
        }            
        
	    if (isParentNode) {        
	        response = response.concat("</task> \n");
	    }

        return response; 
    }
    

    
    private String getSubTaskExportFormat(String taskId, String taskName, ResourceTaskTO rtto) {
        String response = "";
        String start = this.getStartDateFormated(rtto);
        int duration = this.getDurationFormated(rtto);
        String shape = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", complete = "0"; 

        TaskStatusTO tsto = rtto.getTaskStatus();
        Integer state = tsto.getStateMachineOrder();
        if (state.equals(TaskStatusTO.STATE_MACHINE_HOLD) || state.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)){
            complete = "50";
        }else if (state.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
            complete = "100";
        }

        if (tsto.isFinish()){
            shape = "1,1,0,0,0,1,1,0,0,0,1,1,1,0,0,1";
        }

        response = "<task id=\"" + taskId + "\" name=\"" + StringUtil.formatWordForGP(taskName) +
        	"\" shape=\"" + shape + "\" color=\"#" + rtto.getResource().getColor() +
			"\" meeting=\"false\" start=\"" + start + "\" duration=\"" + duration + 
			"\" complete=\"" + complete + "\" fixed-start=\"false\" priority=\"1\" expand=\"true\" />\n";

        return response;
    }    
	
    
    /**
     * Return the start date used for GanttProject export process.
     */
    private String getStartDateFormated(ResourceTaskTO rtto){
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        String response = "";
        Timestamp start = rtbus.getPreferedDate(rtto);
        response = getStrDate(start,"-");
        return response;
    }    
    
    /**
     * Return the duration used for GanttProject export process.
     */
    private int getDurationFormated(ResourceTaskTO rtto) {
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        int response = 0;
        Integer refTime = rtbus.getPreferedTime(rtto);
        
        Timestamp cursor = rtto.getActualDate();
        if (cursor==null) {
        	cursor = rtto.getStartDate();
        }
        int capacity = rtto.getResource().getCapacityPerDay(cursor).intValue();
        if (capacity==0) {
        	capacity = ResourceTO.DEFAULT_FULLDAY_CAPACITY;
        }
        
        response = (refTime.intValue() / capacity);
        float r = (refTime.intValue() % capacity);
        if (r>0){
            response++;
        }
        return response;
    } 
    
    /**
     * For parent tasks or tasks with more than one resource Task, 
     * calculate the start date based on all child task and child resource task objects  
     * @return
     */
    private Timestamp getGPDate(boolean isEarliest, TaskTO tto){
        Timestamp start = null;
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        
        //search among her child tasks the earliest or latest startDate...
        if (tto.getChildTasks()!=null){
            Iterator<TaskTO> i = tto.getChildTasks().values().iterator();
            while(i.hasNext()) {
                TaskTO childTask = i.next();
                Timestamp temp = this.getGPDate(isEarliest, childTask);
                start = this.compareDates(start, temp, isEarliest);
            }
        }

        //search among her ResourceTask the earliest startDate...        
        if (tto.getAllocResources()!=null){
            Iterator<ResourceTaskTO> i = tto.getAllocResources().iterator();
            while(i.hasNext()) {
                ResourceTaskTO childRTask = i.next();
                Timestamp temp = rtbus.getPreferedDate(childRTask);
                if (!isEarliest) {
                    int slots = childRTask.getAllocList().size();
                    temp = DateUtil.getChangedDate(temp, Calendar.DATE, slots);                    
                }
                start = this.compareDates(start, temp, isEarliest);
            }            
        }

        return start;
    }
    
    /**
     * Compare two dates based on criteria: latest or earliest
     * @param start
     * @param temp
     * @param isEarliest
     * @return
     */
    private Timestamp compareDates(Timestamp start, Timestamp temp, boolean isEarliest){
        Timestamp response = start;
        if (response==null){
            response = temp;
        } else {
            if (isEarliest) {
                if (temp.before(response)) response = temp;    
            } else {
                if (temp.after(response)) response = temp;                        
            }
        }
        return response;
    } 
    
    
    private String getStrDate(Timestamp ts, String separator){
        return DateUtil.get(ts, Calendar.YEAR) + separator + 
        	   DateUtil.get(ts, Calendar.MONTH) + separator +
        	   DateUtil.get(ts, Calendar.DATE);
    }
}
