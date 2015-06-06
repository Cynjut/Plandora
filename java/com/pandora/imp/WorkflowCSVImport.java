package com.pandora.imp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.pandora.CategoryTO;
import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.FieldValueTO;
import com.pandora.NodeTemplateTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TaskTO;
import com.pandora.TemplateTO;
import com.pandora.TransferObject;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public class WorkflowCSVImport extends ImportBUS {
	
	private static final String TITLE_TEMPLATE_ID      = "template";
	private static final String TITLE_INSTANCE_ID      = "instance_id";
	private static final String TITLE_NODE_TEMPLATE_ID = "node_template_id";
	private static final String TITLE_TASK_NAME        = "name";
	private static final String TITLE_TASK_DESCRIPTION = "description";
	private static final String TITLE_CATEGORY         = "category";
	private static final String TITLE_IS_MACRO_TASK    = "is_macro_task";
	private static final String TITLE_ITERATION        = "iteration";
	private static final String TITLE_REQ_ID           = "req_id";
	private static final String TITLE_RESOURCE_LIST    = "resource_list";
	private static final String TITLE_QUESTION_ANS     = "question_answer";
	
	@Override
	public void validate(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
    	UserDelegate udel = new UserDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		OccurrenceDelegate odel = new OccurrenceDelegate();
		RequirementDelegate rdel = new RequirementDelegate();
		HashMap<String, String> instanceReqHm = new HashMap<String, String>();
		
    	if (!handler.isLeader(pto)) {
    		throw new BusinessException("Sorry. To perform this feature it is mandatory to be the leader of the project [ " + pto.getName() + "]");
    	}
    	
    	if (!pto.getBollCanAlloc()) {
    		throw new BusinessException("This project [" + pto.getName() + "] is not able to be allocated. Check the allocation status at 'Project Form'.");	
    	}
    	
    	try {
    		boolean first = true;
    		long row = 0;
			String templateId = null, instanceId = null, nodeTemplateId = null, name = null;
			String description = null, category = null, isMacroTask = null, iterationName = null;
			String reqId = null, resourceList = null, questionAnswer = null;

			Vector<ResourceTO> list = udel.getResourceByProject(pto.getId(), false, true);   
    		List<String[]> content = this.getCSVContent(is, fields);			
		    for (String[] nextLine : content) {
        		row++;
        		
        		try {
        			templateId = nextLine[0]; instanceId = nextLine[1]; nodeTemplateId = nextLine[2]; name = nextLine[3];
        			description = nextLine[4]; category = nextLine[5]; isMacroTask = nextLine[6]; iterationName = nextLine[7];
        			reqId = nextLine[8]; resourceList = nextLine[9]; questionAnswer = nextLine[10]; 
        		} catch(Exception e){
        			throw new BusinessException("The CSV file must contain 10 columns.");
        		}
				
    			if (first) {
    				if (templateId.equalsIgnoreCase(TITLE_TEMPLATE_ID) && instanceId.equalsIgnoreCase(TITLE_INSTANCE_ID) && nodeTemplateId.equalsIgnoreCase(TITLE_NODE_TEMPLATE_ID) 
    						&& name.equalsIgnoreCase(TITLE_TASK_NAME) && description.equalsIgnoreCase(TITLE_TASK_DESCRIPTION) && category.equalsIgnoreCase(TITLE_CATEGORY) 
    						&& isMacroTask.equalsIgnoreCase(TITLE_IS_MACRO_TASK) && iterationName.equalsIgnoreCase(TITLE_ITERATION) && reqId.equalsIgnoreCase(TITLE_REQ_ID) 
    						&& resourceList.equalsIgnoreCase(TITLE_RESOURCE_LIST) && questionAnswer.equalsIgnoreCase(TITLE_QUESTION_ANS)) {
    					first = false;
    				} else {
    					throw new BusinessException("The title of columns must be: '" + TITLE_TEMPLATE_ID + "; " + TITLE_INSTANCE_ID + "; " + TITLE_NODE_TEMPLATE_ID + "; " + 
    												TITLE_TASK_NAME + "; " + TITLE_TASK_DESCRIPTION + "; " + TITLE_CATEGORY + "; " + TITLE_IS_MACRO_TASK + "; " + 
    												TITLE_ITERATION + "; " + TITLE_REQ_ID + "; " + TITLE_RESOURCE_LIST + "; " + TITLE_QUESTION_ANS + "'");
    				}
    			} else {
    				//check if template exists...
    				TemplateTO tto = tdel.getTaskTemplate(templateId);
    				if (tto==null && !templateId.trim().equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The template [" + templateId + "] was not found.");
    				}
    				
    				//check if the instanceId exists...
    				if (instanceId==null || instanceId.trim().equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The instanceId cannot be empty.");
    				} else {
    					if (instanceReqHm.get(instanceId)==null) {
    						instanceReqHm.put(instanceId, "");
    					}
    				}
    				
    				//verify if node template_id exists...
    				NodeTemplateTO node = tdel.getNodeTemplateTree(new NodeTemplateTO(nodeTemplateId), null);
    				if (node==null && !nodeTemplateId.trim().equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The node template Id [" + nodeTemplateId + "] was not found.");
    				}
    				
    				//check if the name is not null or empty
    				if (name==null || name.trim().equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The name cannot be null or empty.");
    				}
    				if (name.length() > 50) {
    					throw new BusinessException("Error at row [" + row + "]. The column [" + TITLE_TASK_NAME + "] cannot contain more than 50 characters.");
    				}
    				
    				//check if category exists...
    				if (category!=null && !category.trim().equals("") && !category.trim().equalsIgnoreCase("NONE")) {

        				//check the length of category name...
        				if (category.length() > 50) {
        					throw new BusinessException("Error at row [" + row + "]. The column [" + TITLE_CATEGORY + "] cannot contain more than 50 characters.");
        				}
    					
    					CategoryTO cto = cdel.getCategoryByName(category, CategoryTO.TYPE_TASK, pto);
        				if (cto==null) {
        					throw new BusinessException("Error at row [" + row + "]. The category [" + category + "] was not found.");
        				}
    				}
    				
    				//if isMacroTask=1, check if the next node is a decision node..
    				if (this.getIsMacroTask(isMacroTask)) {
    					if (node!=null && node.getNextNode() instanceof DecisionNodeTemplateTO) {
        					throw new BusinessException("Error at row [" + row + "]. A macro task [node id:" + nodeTemplateId + "] cannot be a previous node from a decision.");    						
    					}
    				}

    				//if the node is a decision, the questionAnswer cannot be empty..    				
    				if (questionAnswer==null || questionAnswer.trim().equals("")) {
    					if (node!=null && node instanceof DecisionNodeTemplateTO) {
        					throw new BusinessException("Error at row [" + row + "]. A decision node must contain a question answer.");    						
    					}
    				}
    				
    				//check if iterationId exists and is valid...
    				if (iterationName!=null && !iterationName.trim().equals("-1")&& !iterationName.trim().equalsIgnoreCase("NONE")) {
        				OccurrenceTO oto = odel.getOccurrenceByName(iterationName, pto);
        				if (oto==null) {
        					throw new BusinessException("Error at row [" + row + "]. The iteration [" + iterationName + "] was not found.");
        				} else if (!oto.getProject().getId().equals(pto.getId())) {
        					throw new BusinessException("Error at row [" + row + "]. The project related to iteration [" + iterationName + "] must be [" + pto.getName() + "].");
        				} else if (!oto.getSource().equals(IterationOccurrence.class.getName())) {
        					throw new BusinessException("Error at row [" + row + "]. The [" + iterationName + "] seems not to be a valid iteration record.");        					
        				}
        					
    				}
    				
    				//check if reqId exists...
    				if (reqId!=null && !reqId.trim().equals("-1")&& !reqId.trim().equalsIgnoreCase("NONE")) {
        				RequirementTO rto = rdel.getRequirement(new RequirementTO(reqId));    					
        				if (rto==null) {
        					throw new BusinessException("Error at row [" + row + "]. The request [" + reqId + "] was not found.");       					
        				} else if (!rto.getProject().getId().equals(pto.getId())){
            				//check if the request project is the same of current project...        					
        					throw new BusinessException("Error at row [" + row + "]. The project related to the request [" + reqId + "] must be the [" + pto.getName() + "].");        					
        				} else {
        					String currentReq = instanceReqHm.get(instanceId);
        					if (currentReq!=null) {
            					if (currentReq.trim().equals("")) {
            						instanceReqHm.put(instanceId, rto.getId());
            					} else if (!currentReq.trim().equals(reqId)) {
            						throw new BusinessException("Error at row [" + row + "]. The file contains different request_id (" + currentReq + " and " + reqId + ") for the same instance_id [" + instanceId + "].");
            					}        						
        					}
        				}
    				}
    				
    				//check if the resource_list is not empty and contain an appropriate format...
    				if (resourceList!=null && !resourceList.trim().equals("")) { 					
        				String[] tokens = resourceList.trim().split(";");
        				if (tokens!=null) {
        					for(int t=0; t<tokens.length; t++) {
        						String[] resList = tokens[t].split("\\|");
        						if (resList.length!=3) {
                					throw new BusinessException("Error at row [" + row + "]. The [" + TITLE_RESOURCE_LIST + "] contain a value [" + tokens[t] + "] with a wrong format. Use the format 'username|alloc date|time in minutes'");
        						}

                				//check if the estimated time is numeric... 
        						if (!StringUtil.hasOnlyDigits(resList[2].trim())) {
                					throw new BusinessException("Error at row [" + row + "]. The value [" + resList[2].trim() + "] must be a numeric value'");
        						}
        						
                				//check the appropriated format of estimated date...
        						boolean dtError = false;
    							String mask = handler.getCalendarMask();        						
        						try {
        							Timestamp dt = DateUtil.getDateTime(resList[1].trim(), mask, handler.getLocale());
        							dtError = (dt==null);
        						} catch(Exception e){
        							dtError = false;
        						}
        						if (dtError) {
        							throw new BusinessException("Error at row [" + row + "]. The value [" + resList[1].trim() + "] must contain a valid date format [" + mask + "]'");        							
        						}
        						
                				//check if resource exists and if is related to the current project...        						
        						String resourceId = null;
        						if (list!=null) {
        							resourceId = getResource(list, resList[0].trim());
        						} else {
        							throw new BusinessException("Error at row [" + row + "]. The 'project' [" + pto.getName() + "] does not contain the resource defined at [" + tokens[t] + "].");	
        						}
        						if (resourceId==null) {
        							throw new BusinessException("Error at row [" + row + "]. The 'resource' [" + resList[0].trim() + "] was not found at project [" + pto.getName() + "].");
        						}        						
        					}
        				} else {
        					throw new BusinessException("Error at row [" + row + "]. The 'resource_list' cannot be empty or contain a wrong format.");	
        				}    					
    				} else {
    					if (node!=null && node instanceof StepNodeTemplateTO) {
        					throw new BusinessException("Error at row [" + row + "]. A common node must contain at least one resource assigned.");    						
    					}
    				}
    			}
    		}
    	} catch(BusinessException e){
    		throw e;
    	} catch(Exception e){
    		throw new BusinessException("The CSV file cannot be read and parsed.");
    	}
	}


	@Override
	public void importFile(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		OccurrenceDelegate odel = new OccurrenceDelegate();
		UserDelegate udel = new UserDelegate();
		HashMap<String, Integer> instanceHm = new HashMap<String, Integer>();
		DbQueryDelegate dbquery = new DbQueryDelegate();
		
		try {
    		boolean first = true;
			String templateId = null, instanceId = null, nodeTemplateId = null, name = null;
			String description = null, category = null, isMacroTask = null, iterationName = null;
			String reqId = null, resourceList = null, questionAnswer = null;

			FieldValueTO lnkItField = (FieldValueTO)fields.elementAt(1);
			boolean linkIteration = (lnkItField!=null && lnkItField.getCurrentValue()!=null && lnkItField.getCurrentValue().equals("1"));
			
			Vector<ResourceTO> projeResList = udel.getResourceByProject(pto.getId(), false, true);
			Vector<CustomNodeTemplateTO> cnlist  = new Vector<CustomNodeTemplateTO>();
			Vector<TaskTO> tlist  = new Vector<TaskTO>();
			List<String[]> content = this.getCSVContent(is, fields);
			
			Vector<RequirementTO> reqlist  = new Vector<RequirementTO>();
			HashMap<String, RequirementTO> reqHash  = new HashMap<String, RequirementTO>();
    		
    		if (projeResList!=null && cnlist!=null && content!=null) {
    		    for (String[] nextLine : content) {
            		
            		try {
            			templateId = nextLine[0]; instanceId = nextLine[1]; nodeTemplateId = nextLine[2]; name = nextLine[3];
            			description = nextLine[4]; category = nextLine[5]; isMacroTask = nextLine[6]; iterationName = nextLine[7];
            			reqId = nextLine[8]; resourceList = nextLine[9]; questionAnswer = nextLine[10];
            		} catch(Exception e){
            			throw new BusinessException("The CSV file must contain 10 columns.");
            		}
    				
        			if (!first) {

        				CategoryTO cto = new CategoryTO(CategoryTO.DEFAULT_CATEGORY_ID);
        				if (category!=null && !category.trim().equals("") && !category.trim().equalsIgnoreCase("NONE")) {
        					cto = cdel.getCategoryByName(category, CategoryTO.TYPE_TASK, pto);
        					if (cto==null) {
        						cto = new CategoryTO(CategoryTO.DEFAULT_CATEGORY_ID);	
        					}
        				}
        				
        				OccurrenceTO oto = null;
        				if (iterationName!=null && !iterationName.trim().equals("-1") && !iterationName.trim().equalsIgnoreCase("NONE")) {
            				oto = odel.getOccurrenceByName(iterationName, pto);
        				}
        				
        				
        				if (templateId==null || templateId.trim().equals("")){
        					TaskTO tto = new TaskTO();
        					tto.setName(name);
        					tto.setDescription(description);
        					tto.setCategory(cto);
            				if (oto!=null) {
            					tto.setIteration(oto.getId());
            				}
            				this.populateRequest(reqId, linkIteration, reqlist, reqHash, tto, null);
            				
            				tto.setCreationDate(DateUtil.getNow());
            				tto.setCreatedBy(handler);
            				tto.setIsParentTask(new Integer(this.getIsMacroTask(isMacroTask)==true?1:0));
            				tto.setProject(pto);
            				tto.setProject(pto);
            				tto.setHandler(handler);

            				String resList = this.getResourceList(resourceList, projeResList);
            				String[] tokens = resList.trim().split(";");
            				if (tokens!=null) {
            					for(int t=0; t<tokens.length; t++) {
            						String[] resListToken = tokens[t].split("\\|");
            						if (resListToken.length==3) {
                        				ResourceTaskTO rtto = new ResourceTaskTO();
                        				tto.addAllocResource(rtto);
                        				rtto.setResource(new ResourceTO(resListToken[0]));
                        				rtto.setStartDate(DateUtil.getDateTime(resListToken[1], handler.getCalendarMask(), handler.getLocale()));
                        				rtto.setEstimatedTime(new Integer(resListToken[2]));
                        				rtto.setHandler(handler);
            						}
            					}
            				}
            				tlist.add(tto);
        					
        				} else {
            				Integer instanceNumber = null;
            				
            				//check if the instanceId exists...
            				if (instanceId!=null && !instanceId.trim().equals("")) {
            					instanceNumber = instanceHm.get(instanceId);
            					if (instanceNumber==null) {
            						instanceNumber = Integer.parseInt(dbquery.getNewId());
            						instanceHm.put(instanceId, instanceNumber);
            					}
            				}
        					
            				CustomNodeTemplateTO cnto = new CustomNodeTemplateTO();
           					cnto.setInstanceId(instanceNumber);
            				cnto.setCategoryId(cto.getId());
            				if (oto!=null) {
            					cnto.setIterationId(oto.getId());
            				}
            				this.populateRequest(reqId, linkIteration, reqlist, reqHash, null, cnto);
            				
            				cnto.setDescription(description);
            				cnto.setIsParentTask(this.getIsMacroTask(isMacroTask));
            				cnto.setName(name);
            				cnto.setNodeTemplateId(nodeTemplateId);
            				cnto.setProjectId(pto.getId());
            				cnto.setRelatedTaskId(null);
            				cnto.setResource(this.getResourceList(resourceList, projeResList));
            				cnto.setTemplateId(templateId);
            				cnto.setQuestionContent(questionAnswer);
            				cnlist.add(cnto);
        				}
        				
        			} else {
        				first = false;
        			}
        		}
    		    
    		    if (cnlist.size()>0) {
    		    	tdel.saveCustomNodeTemplate(cnlist, tlist, reqlist, handler);	
    		    }
    		}
			
    	} catch(BusinessException e){
    		throw e;
    	} catch(Exception e){
    		throw new BusinessException("The CSV file cannot be read and parsed.");
    	}
	}


	private void populateRequest(String reqId,	boolean linkIteration, Vector<RequirementTO> reqlist, HashMap<String, RequirementTO> reqHash, 
			TaskTO tto, CustomNodeTemplateTO cnto) throws BusinessException {
		RequirementDelegate rdel = new RequirementDelegate();
		if (reqId!=null && !reqId.trim().equals("-1") && !reqId.trim().equalsIgnoreCase("NONE")) {
			RequirementTO rto = rdel.getRequirement(new RequirementTO(reqId));
			if (rto!=null) {
				
				if (tto!=null) {
					tto.setRequirement(rto);	
				}
				if (cnto!=null) {
					cnto.setPlanningId(reqId);	   					
				}
				
				if (linkIteration) {
					if (reqHash.get(rto.getId())==null) {
						
						if (tto!=null) {
							rto.setIteration(tto.getIteration());	
						}
						if (cnto!=null) {
							rto.setIteration(cnto.getIterationId());	
						}
						
						reqHash.put(rto.getId(), rto);
						reqlist.add(rto);
					}            							
				}
				
			} else {
				throw new BusinessException("Error generating tasks. The request [" + reqId + "] was not found.");            						
			}
		} else {
			if (tto!=null) {
				tto.setRequirement(null);	
			}
			if (cnto!=null) {
				cnto.setPlanningId("-1");
			}
		}
	}

	private Boolean getIsMacroTask(String isMacroTask) {
		Boolean response = false;
		if (isMacroTask!=null) {
			if (isMacroTask.trim().equals("1") || isMacroTask.trim().equalsIgnoreCase("S") || 
					isMacroTask.trim().equalsIgnoreCase("true") || isMacroTask.trim().equalsIgnoreCase("sim") || 
					isMacroTask.trim().equalsIgnoreCase("T")) {
				response = true;
			}
		}
		return response;
	}


	@Override
	public String getLabel() throws BusinessException {
		return "label.importExport.workflowCSVImport";
	}

	@Override
	public Vector<FieldValueTO> getFields() throws BusinessException {
    	Vector<FieldValueTO> list = new Vector<FieldValueTO>();
    	
    	String lbl0 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.workflowCSVImport.delim.1");
    	String lbl1 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.workflowCSVImport.delim.2");
    	String lbl2 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.workflowCSVImport.delim.3");

    	Vector<TransferObject> options = new Vector<TransferObject>();
    	options.add(new TransferObject("1", lbl0));
    	options.add(new TransferObject("2", lbl1));
    	options.add(new TransferObject("3", lbl2));

    	FieldValueTO delimiter = new FieldValueTO("DELIMITER", "label.importExport.workflowCSVImport.delim", options);
    	list.add(delimiter);

    	Vector<TransferObject> optionsYN = new Vector<TransferObject>();
    	String lblYes = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.workflowCSVImport.link.1");
    	String lblNo  = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.workflowCSVImport.link.2");    	
    	optionsYN.add(new TransferObject("1", lblYes));
    	optionsYN.add(new TransferObject("0", lblNo));

    	FieldValueTO linkIeration = new FieldValueTO("LNK_IT_REQ", "label.importExport.workflowCSVImport.link", optionsYN);
    	list.add(linkIeration);
    	
    	return list;
	}

	
	private List<String[]> getCSVContent(InputStream is, Vector<FieldValueTO> fields) throws BusinessException{
    	Reader reader = null;
    	DataInputStream in = null;
    	List<String[]> response = null;
    	CSVReader parser = null;
    	
    	char delimiter = ';';
        FieldValueTO fvto = (FieldValueTO)fields.elementAt(0);
        if (fvto!=null && fvto.getCurrentValue().equals("1")) {
        	delimiter = ',';
        } else if (fvto!=null && fvto.getCurrentValue().equals("2")) {
        	delimiter = ';';
        } else if (fvto!=null && fvto.getCurrentValue().equals("3")) {
        	delimiter = '\t';
        }
    	
		try {
			in = new DataInputStream(is);
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			throw new BusinessException(e);
		} finally {
			try {
				if (in!=null) {
					in.close();	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
	    	if (reader==null || !reader.ready()) {
	    		throw new BusinessException("The CSV file cannot be opened.");	
	    	}
			parser = new CSVReader(reader, delimiter);
			response = parser.readAll();
	    	
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		try {
			if (parser!=null) {
				parser.close();	
			}
		} catch (Exception e) {
			throw new BusinessException(e);
		}

		return response;
	}
	
	
	private String getResource(Vector<ResourceTO> list, String username){
		String resId = null;
		for (ResourceTO rto : list) {
			if (rto.getUsername().equals(username)) {
				resId = rto.getId();
				break;
			}
		}
		return resId;
	}

	private String getResourceList(String resourceList,	Vector<ResourceTO> projeResList) {
		String response = "";
		if (resourceList!=null && !resourceList.trim().equals("")) {
			String[] tokens = resourceList.trim().split(";");
			if (tokens!=null) {
				for(int t=0; t<tokens.length; t++) {
					if (t>0) {
						response = response + ";";
					}
					String[] resList = tokens[t].split("\\|");
					String resId = this.getResource(projeResList, resList[0].trim());
					response = response + resId + "|" + resList[1].trim() + "|" + resList[2].trim();
				}
			}			
		}

		return response;
	}
	
}
