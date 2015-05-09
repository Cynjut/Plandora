package com.pandora.imp;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.XmlDomParse;

/**
 * This class contain the business rules to get information 
 * about tasks and resource_task from XML file comming from
 * MSProject application.
 */
public class MsProjectXmlImportBUS extends ImportBUS {

    private HashMap taskNodesHash = new HashMap();
    private HashMap calendarHash = new HashMap();
    private boolean ignoreAssignmentProblems = false;
    private boolean ignoreResourceProblems = false;
    private int dependencePolicy = 0;
    

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFields()
     */
    public Vector getFields() throws BusinessException {    	
    	Vector list = new Vector();
    	
    	String lbl0 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.assignment.0");
    	String lbl1 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.assignment.1");
    	
    	Vector options = new Vector();
    	options.add(new TransferObject("0", lbl0));
    	options.add(new TransferObject("1", lbl1));
    	
    	FieldValueTO assigmentProblem = new FieldValueTO("ASGN_PROB", "label.importExport.msProjectXmlImport.assignment", options);
    	list.add(assigmentProblem);

    	
    	String lbl2 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.resource.0");
    	String lbl3 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.resource.1");
    	
    	Vector options2 = new Vector();
    	options2.add(new TransferObject("0", lbl2));
    	options2.add(new TransferObject("1", lbl3));
    	
    	FieldValueTO resourceProblem = new FieldValueTO("RES_PROB", "label.importExport.msProjectXmlImport.resource", options2);
    	list.add(resourceProblem);

    	
    	String lbl4 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.dependence.0");
    	String lbl5 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.dependence.1");
    	String lbl6 = super.handler.getBundle().getMessage("label.importExport.msProjectXmlImport.dependence.2");
    	
    	Vector options3 = new Vector();
    	options3.add(new TransferObject("0", lbl4));
    	options3.add(new TransferObject("1", lbl5));
    	options3.add(new TransferObject("2", lbl6));
    	
    	FieldValueTO dependences = new FieldValueTO("DEPEND", "label.importExport.msProjectXmlImport.dependence", options3);
    	list.add(dependences);

    	return list;

    }


	public String getLabel() throws BusinessException {
		return "label.importExport.msProjectXmlImport";
	}
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ImportBUS#validate(java.io.InputStream, com.pandora.ProjectTO, java.util.Vector)
     */
    public void validate(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
    	UserDelegate udel = new UserDelegate();
    	HashMap resourceList = new HashMap();
    	HashMap taskList = new HashMap();           
    	
        try {
            
            Document doc = XmlDomParse.getXmlDom(is);
            Element root = doc.getDocumentElement();

            FieldValueTO field1 = (FieldValueTO)fields.elementAt(0);
            if (field1!=null && field1.getCurrentValue().equals("1")) {
                this.ignoreAssignmentProblems = true;   	
            }

            FieldValueTO field2 = (FieldValueTO)fields.elementAt(1);
            if (field2!=null && field2.getCurrentValue().equals("1")) {
                this.ignoreResourceProblems = true;   	
            }

            FieldValueTO field3 = (FieldValueTO)fields.elementAt(2);
            if (field3!=null) {
            	this.dependencePolicy = Integer.parseInt(field3.getCurrentValue());
            }

            //checks if the first elemet is project tag
            if (!root.getNodeName().toLowerCase().equals("project")){
            	throw new Exception("The XML file from MSProject is invalid.");
            	
            	
            } else {
            
            	if (!pto.getBollCanAlloc()) {
            		throw new Exception("This project [" + pto.getName() + "] is not able to be allocated. Check the allocation status inti the Project Form.");	
            	}
            	
            	NodeList taskNodes = doc.getElementsByTagName("Task");
                for (int i = 0; i< taskNodes.getLength(); i++){
                    Node taskNode = taskNodes.item(i);
                    String taskUid = XmlDomParse.getFirstTextByTag(taskNode, "UID");
                    if (taskUid!=null) {
                    	taskList.put(taskUid, "NEW_TASK");
                    } else {
                    	throw new Exception("The information related to the TASKS was not found.");   	
                    }
                }

                //check if resources exists...
                NodeList resNodes = doc.getElementsByTagName("Resource");
                if (resNodes==null || resNodes.getLength()==0) {
                	throw new Exception("The information related to the RESOURCES was not found.");
                	
                } else {
                    for (int j = 0; j< resNodes.getLength(); j++){ 
                        Node resNode = resNodes.item(j);
                        String resname = XmlDomParse.getFirstTextByTag(resNode, "Name");
                        String resId = XmlDomParse.getFirstTextByTag(resNode, "UID");
                        String calendarId = XmlDomParse.getFirstTextByTag(resNode, "CalendarUID");        
                        
                        //'calendar UID = null' means that the current resource must be skipped 
                        if (calendarId!=null && !calendarId.trim().equals("") && !calendarId.trim().equals("-1")) {
                            if (resname!=null && resId!=null) {
                                UserTO uto = new UserTO();
                                uto.setUsername(resname);
                                uto = udel.getObjectByUsername(uto);
                                if (uto==null) {
                                	if (!ignoreResourceProblems) {
                                		throw new Exception("The RESOURCE [" + resname + "] was not found into data base.");	                                	
                                	}
                                } else {
                            		ResourceTO rto = new ResourceTO(new ResourceTO(uto.getId()));
                                    rto.setProject(pto);
                                    rto = udel.getResource(rto);
                                    if (rto==null) {
                                    	throw new Exception("The resource id [" + resname + "] is not a resource of project [" + pto.getName() + "]. Check the resource allocation into the Project Form.");                	
                                	} else {
                                    	resourceList.put(resId, resname);                            		
                                	}
                                }
                            }                        	
                        }
                    }                                	
                }
                
                if (!this.ignoreAssignmentProblems) {
                    NodeList resTaskNodes = doc.getElementsByTagName("Assignment");
                    if (resNodes==null || resNodes.getLength()==0) {
                    	throw new Exception("The information related to the ASSIGMENTS (resources X task) was not found.");
                    } else {
                        for (int i = 0; i< resTaskNodes.getLength(); i++){
                            Node resTaskNode = resTaskNodes.item(i);
                            String taskUid = XmlDomParse.getFirstTextByTag(resTaskNode, "TaskUID");
                            String resUid = XmlDomParse.getFirstTextByTag(resTaskNode, "ResourceUID");
                            String taskRef = (String)taskList.get(taskUid);
                            String resRef = (String)resourceList.get(resUid);
                            if (taskRef==null || resRef==null) {
                            	throw new Exception("There is something wrong with the assigment that references task [" + taskUid + "] and  resource [" + resUid + "].");    	
                            }
                        }
                    }             	                	
                }
                
            }
            
		} catch (Exception e) {
			throw new BusinessException(e);
		}
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ImportBUS#importFile(java.io.InputStream, com.pandora.ProjectTO, java.util.Vector)
     */
    public void importFile(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
        TaskDelegate tdel = new TaskDelegate();
        
        try {
            Document doc = XmlDomParse.getXmlDom(is);
            
            //get a list of task objects
            NodeList taskNodes = doc.getElementsByTagName("Task");
            HashMap taskList = this.getTaskList(taskNodes, pto);

            //get a list of resource objects
            NodeList resNodes = doc.getElementsByTagName("Resource");
            HashMap resList = this.getResourceList(doc, resNodes, pto);

            //get a list of resourceTask objects
            NodeList resTaskNodes = doc.getElementsByTagName("Assignment");
            this.getResTaskList(resTaskNodes, resList ,taskList);
            
            Vector tasks = this.getVectorFromHash(taskList, taskNodes);
            tdel.insertTask(tasks);
            
		} catch (Exception e) {
			throw new BusinessException(e);
		}			
    }


    private Vector getVectorFromHash(HashMap taskList, NodeList taskNodes) {
        Vector response = new Vector();
        for (int i = 0; i< taskNodes.getLength(); i++){
            Node taskNode = taskNodes.item(i);
            String taskUid = XmlDomParse.getFirstTextByTag(taskNode, "UID");
            response.addElement((TaskTO)taskList.get(taskUid));
        }
        return response;
    }


    private void getResTaskList(NodeList resTaskNodes, HashMap resHash, HashMap taskHash) throws BusinessException {
        TaskStatusDelegate tsdel = new TaskStatusDelegate();
        TaskStatusTO iniStatus = tsdel.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_OPEN);
        
        for (int i = 0; i< resTaskNodes.getLength(); i++){
            Node resTaskNode = resTaskNodes.item(i);
            String taskUid = XmlDomParse.getFirstTextByTag(resTaskNode, "TaskUID");
            String resUid = XmlDomParse.getFirstTextByTag(resTaskNode, "ResourceUID");
            
            TaskTO tto = (TaskTO)taskHash.get(taskUid);
            if (tto!=null && !tto.isParentTask()){
                ResourceTO rto = (ResourceTO)resHash.get(resUid);
                
                if (rto!=null) {
                    ResourceTaskTO rtto = new ResourceTaskTO();
                    rtto.setActualDate(null);
                    rtto.setActualTime(null);
                    rtto.setHandler(tto.getHandler());
                    rtto.setResource(rto);
                    rtto.setTask(tto);
                    rtto.setTaskStatus(iniStatus);
                    Vector allocList = new Vector();
                    
                    Node taskNode = (Node)this.taskNodesHash.get(taskUid);
                    Timestamp start = this.getDateFromXml(XmlDomParse.getFirstTextByTag(taskNode, "Start"));
                    Timestamp finish = this.getDateFromXml(XmlDomParse.getFirstTextByTag(taskNode, "Finish"));
                    
                    int estimatedTimeAcc = 0, seq = 1;
                    Timestamp cursor = start;
                    do{
                    	int weekDay = DateUtil.get(cursor, Calendar.DAY_OF_WEEK);
                    	String capacity = (String)this.calendarHash.get(rto.getUsername() + "|" + weekDay);
                    	int capacityInt = 0;
                    	if (capacity!=null && !capacity.equals("OFF")) {
                    		capacityInt = Integer.parseInt(capacity);
                    		estimatedTimeAcc += capacityInt;
                    	}
                    	ResourceTaskAllocTO alloc = new ResourceTaskAllocTO(rtto, seq, capacityInt);
                    	allocList.add(alloc);
                    	
                    	seq++;
                    	cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
                    } while(cursor.before(finish));

                    rtto.setAllocList(allocList);
                    rtto.setEstimatedTime(new Integer(estimatedTimeAcc));
                    rtto.setStartDate(start);
                    
                    tto.addAllocResource(rtto);                	
                }
            }
        }
    }


    private HashMap getResourceList(Document doc, NodeList resNodes, ProjectTO pto) throws BusinessException {
        UserDelegate udel = new UserDelegate();
        HashMap response = new HashMap();
        NodeList calNodes = doc.getElementsByTagName("Calendar");
        
        for (int i = 0; i< resNodes.getLength(); i++){ 
            Node resNode = resNodes.item(i);
            
            String resKey = XmlDomParse.getFirstTextByTag(resNode, "UID");
            String calendarId = XmlDomParse.getFirstTextByTag(resNode, "CalendarUID");        

            //'calendar UID = null' means that the current resource must be skipped 
            if (calendarId!=null && !calendarId.trim().equals("") && !calendarId.trim().equals("-1")) {
                String userName = XmlDomParse.getFirstTextByTag(resNode, "Name");
                if (userName!=null) {
                    UserTO uto = new UserTO();
                    uto.setUsername(userName);
                    uto = udel.getObjectByUsername(uto);
                    if (uto!=null) {
                        ResourceTO rto = new ResourceTO(new ResourceTO(uto.getId()));
                        rto.setProject(pto);
                        rto = udel.getResource(rto);
                        
                        response.put(resKey, rto);
                        
                        //read the Calendar info
                        this.loadCalendarListByResource(calNodes, calendarId, rto);           
                    }
                }            	
            }
        }
        
        return response;
    }


    private void loadCalendarListByResource(NodeList calNodes, String calendarId, ResourceTO rto) throws BusinessException {
        for (int i = 0; i< calNodes.getLength(); i++){ 
            Node calNode = calNodes.item(i);
            String uid = XmlDomParse.getFirstTextByTag(calNode, "UID");
            if (uid!=null && uid.trim().equals(calendarId.trim())){
            	Node weekDays = XmlDomParse.getFirstNodeByTag(calNode, "WeekDays");
            	ArrayList daysList = XmlDomParse.getNodesByTag(weekDays, "WeekDay");
            	
            	for (int j=0; j < daysList.size(); j++) {
            		Node day = (Node)daysList.get(j);
            		String dayType = XmlDomParse.getFirstTextByTag(day, "DayType");
            		String dayWorking = XmlDomParse.getFirstTextByTag(day, "DayWorking");
            		String workTime = "OFF";
            		if (dayWorking.equals("1")) {
            			Node wtimeNode = XmlDomParse.getFirstNodeByTag(day, "WorkingTimes");
            			ArrayList wtimeList = XmlDomParse.getNodesByTag(wtimeNode, "WorkingTime");
            			int minutesDiff = 0;
                    	for (int k=0; k < wtimeList.size(); k++) {
                    		Node wt = (Node)wtimeList.get(k);
                    		Timestamp fromTime = this.getDateTime(XmlDomParse.getFirstTextByTag(wt, "FromTime"));
                    		Timestamp toTime = this.getDateTime(XmlDomParse.getFirstTextByTag(wt, "ToTime"));
                    		if (fromTime.before(toTime)){
                    	        long diff = (toTime.getTime() - fromTime.getTime());
                    	        minutesDiff = minutesDiff + ((int)(diff / 60000));
                    		}
                    	}
                    	workTime = minutesDiff+"";
            		}
            		
            		this.calendarHash.put(rto.getUsername() + "|" + dayType, workTime);
            	}
            }
        }
    }
    
    
    private HashMap getTaskList(NodeList taskNodes, ProjectTO pto) throws BusinessException {
        HashMap response = new HashMap();
        HashMap lastLoadTaskByLevelHash = new HashMap();
                
        //create a hash of all plandora task objects
        for (int i = 0; i< taskNodes.getLength(); i++){
            Node taskNode = taskNodes.item(i);
            TaskTO tto = new TaskTO();
            String outlineLevel = XmlDomParse.getFirstTextByTag(taskNode, "OutlineLevel");
            String taskUid = XmlDomParse.getFirstTextByTag(taskNode, "UID");
            Node predecessorLink = XmlDomParse.getFirstNodeByTag(taskNode, "PredecessorLink");
            
            tto.setCategory(new CategoryTO(CategoryTO.DEFAULT_CATEGORY_ID));
            tto.setCreationDate(DateUtil.getNow());
            String notes = XmlDomParse.getFirstTextByTag(taskNode, "Notes");
            if (notes!=null) {
                tto.setDescription(notes);    
            } else {
                tto.setDescription(XmlDomParse.getFirstTextByTag(taskNode, "Name"));
            }
            tto.setFinalDate(null);
            tto.setHandler(super.handler);
            tto.setCreatedBy(super.handler);
            tto.setName(XmlDomParse.getFirstTextByTag(taskNode, "Name"));
            tto.setProject(pto); 
            tto.setRequirement(null);
            
            lastLoadTaskByLevelHash.put(outlineLevel, tto);
            tto.setIsParentTask(new Integer(0));
            
            int level = Integer.parseInt(outlineLevel); 
            if (level>1) {
            	TaskTO parentTask = (TaskTO)lastLoadTaskByLevelHash.get("" + (level-1));
            	if (parentTask!=null) {
            		parentTask.setIsParentTask(new Integer(1));
            		tto.setParentTask(parentTask);
            	}            	
            }
            
            if (predecessorLink!=null && this.dependencePolicy!=2) {
            	String linkId = XmlDomParse.getFirstTextByTag(predecessorLink, "PredecessorUID");
            	if (linkId!=null && !linkId.trim().equals("")) {
            		TaskTO predecTaskTO = (TaskTO)response.get(linkId.trim());
            		if (predecTaskTO!=null) {
            			PlanningRelationTO relation = new PlanningRelationTO();
            			relation.setPlanning(predecTaskTO);
            			relation.setRelated(tto);
            			relation.setPlanType(PlanningRelationTO.ENTITY_TASK);
            			relation.setRelatedType(PlanningRelationTO.ENTITY_TASK);
            			if (this.dependencePolicy==0) {
            				relation.setRelationType(PlanningRelationTO.RELATION_BLOCKS);	
            			} else {
            				relation.setRelationType(PlanningRelationTO.RELATION_RELATED_WITH);
            			}
            			predecTaskTO.addRelation(relation);
            		}
            	}
            }
                        
            this.taskNodesHash.put(taskUid, taskNode);
            response.put(taskUid, tto);
        }
        
        return response;
    }

    
    private Timestamp getDateFromXml(String content){
        String[] sep  = content.split("T");
        String datePart = sep[0];
        String timePart = sep[1];
        
        String[] dateList = datePart.split("-");
        String year = dateList[0];
        String month = dateList[1];
        String day = dateList[2];
        
        String[] timeList = timePart.split(":");
        String hour = timeList[0];
        String min = timeList[1];
        String sec = timeList[2];
        
        return DateUtil.getDateTime(day, (Integer.parseInt(month)-1)+"", year, hour, min, sec);
    }
        
    
	public Timestamp getDateTime(String timePart){
        String[] timeList = timePart.split(":");
        String hour = timeList[0];
        String min = timeList[1];
        String sec = timeList[2];
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, DateUtil.get(DateUtil.getNow(), Calendar.DATE));
        c.set(Calendar.MONTH, DateUtil.get(DateUtil.getNow(), Calendar.MONTH));
        c.set(Calendar.YEAR, DateUtil.get(DateUtil.getNow(), Calendar.YEAR));
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        c.set(Calendar.MINUTE, Integer.parseInt(min));
        c.set(Calendar.SECOND, Integer.parseInt(sec));
        c.set(Calendar.MILLISECOND, 0);
        return new Timestamp(c.getTimeInMillis());
        
	}
    
}
