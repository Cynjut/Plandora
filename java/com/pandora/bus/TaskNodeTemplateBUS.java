package com.pandora.bus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.NodeTemplateTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.dao.TaskNodeTemplateDAO;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.WorkflowUtil;

public class TaskNodeTemplateBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    TaskNodeTemplateDAO dao = new TaskNodeTemplateDAO();

    private int seq = 1;
    private int top = 10;
    private HashMap paintedNodes = new HashMap();
    
    
	public NodeTemplateTO getNodeTemplateTree(NodeTemplateTO node, String planningId) throws BusinessException {
		NodeTemplateTO response = null;
		HashMap cache = new HashMap();
        try {
            response = (NodeTemplateTO) dao.getObjectInTree(node);

            //if necessary, override some attribute from CustomNodeTemplate values...
            if (planningId!=null && !planningId.trim().equals("")) {
            	response = this.overrideNodeTemplate(response, planningId, true, cache);
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}
	
	
	public Vector getNodeListByTemplate(String templateId, String instanceId, String planningId) throws BusinessException {
        Vector response = new Vector();
        HashMap cache = new HashMap();
        try {
            response = dao.getNodeListByPlanning(templateId);
            
            if (response!=null && planningId!=null) {
                Iterator i = response.iterator();
                while(i.hasNext()) {
                	NodeTemplateTO ntto = (NodeTemplateTO)i.next();
        			if (instanceId!=null && !instanceId.equals("")) {
                    	ntto.setInstanceId(new Integer(instanceId));
        			}
                	ntto = this.overrideNodeTemplate(ntto, planningId, false, cache);
                }            	
            }
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}	

	
	public NodeTemplateTO getNodeTemplate(NodeTemplateTO filter, String instanceId, String planningId) throws BusinessException {
		NodeTemplateTO response = null;
		HashMap cache = new HashMap();
        try {
            response = (NodeTemplateTO) dao.getObject(filter);
            
            //if necessary, override some attribute from CustomNodeTemplate values...
            if (planningId!=null && !planningId.trim().equals("")) {
            	if (instanceId!=null && !instanceId.trim().equals("")){
            		response.setInstanceId(new Integer(instanceId));
            	}
            	response = this.overrideNodeTemplate(response, planningId, false, cache);
            }
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	
	/**
	 * If necessary, override some attribute from CustomNodeTemplate values...
	 */
	private NodeTemplateTO overrideNodeTemplate(NodeTemplateTO ntto, String planningId, boolean includeSubNodes, HashMap cache) throws DataAccessException{
		
		//the cache is used to know witch nodes was already loaded considering circle references    
		if (cache.get(ntto.getId())==null) {
			
			//fetch Custom nodeTemplate from data base
	    	CustomNodeTemplateTO cntto = dao.getCustomNodeTemplate(ntto, planningId);
	    	cache.put(ntto.getId(), ntto.getId());
	    	
	    	if (includeSubNodes) {
	    		if (ntto instanceof DecisionNodeTemplateTO) {
	    			DecisionNodeTemplateTO speclass = (DecisionNodeTemplateTO) ntto;
	    			if (speclass.getNextNodeIfFalse()!=null) {
	    				speclass.setNextNodeIfFalse(this.overrideNodeTemplate(speclass.getNextNodeIfFalse(), planningId, true, cache));
	    			}
	    		}
	    		if (ntto.getNextNode()!=null) {
	    			ntto.setNextNode(this.overrideNodeTemplate(ntto.getNextNode(), planningId, true, cache));
	    		}    		    		
	    	}
	    	this.overrideNodeTemplate(ntto, cntto);			
		}

        return ntto;
	}


	public void overrideNodeTemplate(NodeTemplateTO ntto, CustomNodeTemplateTO cntto) {
		if (cntto!=null && ntto!=null) {
			if (ntto instanceof StepNodeTemplateTO) {
				StepNodeTemplateTO speclass = (StepNodeTemplateTO) ntto;
				speclass.setCategoryId(cntto.getCategoryId());
				speclass.setProject(new ProjectTO(cntto.getProjectId()));
				speclass.setResourceId(cntto.getResource());
			} else if (ntto instanceof DecisionNodeTemplateTO) {
				DecisionNodeTemplateTO speclass = (DecisionNodeTemplateTO) ntto;
				speclass.setQuestionContent(cntto.getQuestionContent());
			}
			ntto.setDescription(cntto.getDescription());
			ntto.setName(cntto.getName());
			ntto.setRelationPlanningId(cntto.getRelatedTaskId());
			ntto.setInstanceId(cntto.getInstanceId());
			if (cntto.getIsParentTask()!=null) {
				ntto.setIsParentTask(cntto.getIsParentTask().booleanValue());	
			} else {
				ntto.setIsParentTask(false);
			}
		}
	}
	
	
	public void saveCustomNodeTemplate(CustomNodeTemplateTO cntto) throws BusinessException {
        try {
            dao.saveCustomNodeTemplate(cntto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}

	
	public Integer getInstance(String templateId, String planningId) throws BusinessException {
		Integer response = null;
        try {
			response = dao.getInstance(templateId, planningId);
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}
		return response;        
	}
	
	
	public void saveWorkflow(String nodeTemplateId, Integer instanceId, String planningId, UserTO createBy) throws Exception {
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();

		NodeTemplateTO filter = new NodeTemplateTO(nodeTemplateId);
		filter.setInstanceId(instanceId);
		
		NodeTemplateTO rootNode = tdel.getNodeTemplateTree(filter, planningId);
        dao.saveWorkflowByRequirement(rootNode, planningId, createBy);
	}
	
	
	public BufferedImage drawWorkFlow(NodeTemplateTO root, UserTO uto, String bgcolor, boolean showNodeStatus) throws BusinessException{
		BufferedImage buffer = new BufferedImage( 1200, 800, BufferedImage.TYPE_INT_RGB);		
		try {
			Graphics g = buffer.createGraphics();
			int x = 600;

			//clear screen
			Color bColor = Color.decode("0x" + bgcolor);
			g.setColor(bColor);
			g.fillRect(0, 0, 1200, 800);

			g.setColor( Color.BLACK ); 
			g.setFont(WorkflowUtil.getDefaultFount());

			this.top = WorkflowUtil.paintEgde(g, x, this.top, true, null);
			this.drawNode(root, uto, x, g, bColor, showNodeStatus); //draw the first node (root)
			this.paintedNodes = new HashMap(); //empty cache
			
		} catch (Exception e) {
			throw new BusinessException(e);
		}	
		
		return buffer;  
	}

	
	private void drawNode(NodeTemplateTO node, UserTO uto, int x, Graphics g, Color bColor, boolean showNodeStatus) throws BusinessException{
		PlanningDelegate pdel = new PlanningDelegate();
		TaskDelegate tdel = new TaskDelegate();
		Color nodeColor = bColor;
		
		//check if node was already painted...
		NodeTemplateTO cacheNode = null;
		if (node.getId()!=null && !node.getId().equals("")) {
			cacheNode = (NodeTemplateTO)paintedNodes.get(node.getId());	
		}
		
		if (cacheNode==null) {
			String tag = null;
			node.setAdditionalInfo("");
			
	    	if (showNodeStatus && node.getRelationPlanningId()!=null) {
	    		PlanningTO pto = null;
	    		try {
					pto = pdel.getSpecializedObject(new PlanningTO(node.getRelationPlanningId()));
				} catch (BusinessException e) {
					pto = null;
				}
				if (pto!=null && pto instanceof TaskTO) {
					TaskTO planningTask = (TaskTO) pto;
					nodeColor = this.getNodeColor(planningTask, bColor);
					node.setName(planningTask.getName());
					String tip = this.getTaskTooltTip(uto, planningTask);
					node.setAdditionalInfo(tip); 
					
					int times = tdel.getReopenTimes(planningTask);
					if (times>0) {
						tag = times+"";	
					}
					
				} else {
					node.setAdditionalInfo("");
				}
	    	}
			
			//fetch nodes not loaded... 
			if (node.getNodeType()==null) {
				String instanceId = "";
				if (node.getInstanceId()!=null) {
					instanceId = node.getInstanceId().toString();
				}
				NodeTemplateTO buff = this.getNodeTemplate(node, instanceId, null);
				node.copyFromClone(buff);
			}
	    	
			node.setGenericTag(""+seq);
			this.paintedNodes.put(node.getId(), node);
			this.seq++;
						
			if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
				this.top = WorkflowUtil.paintSquare(g, x, this.top, node, nodeColor, tag);
				
				if (node.getNextNode()!=null) {
					this.drawNode(node.getNextNode(), uto, x, g, bColor, showNodeStatus);
				} else {
					WorkflowUtil.paintEgde(g, x, this.top, false, null);
				}			
				
			} else if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
				DecisionNodeTemplateTO decision = (DecisionNodeTemplateTO) node;
				String yesLabel = uto.getBundle().getMessage(uto.getLocale(), "label.yes");
				String noLabel = uto.getBundle().getMessage(uto.getLocale(), "label.no");				
				this.top = WorkflowUtil.paintDecision(g, x, this.top, decision, yesLabel, noLabel);
				int oldTop = this.top; //save the current top position
				
				if (decision.getNextNode()!=null) {
					this.drawNode(decision.getNextNode(), uto, x-80, g, bColor, showNodeStatus);
				} else {
					WorkflowUtil.paintEgde(g, x-80, this.top, false, null);
				}			
				
				this.top = oldTop; //restore the top position
				if (decision.getNextNodeIfFalse()!=null) {
					this.drawNode(decision.getNextNodeIfFalse(), uto, x+80, g, bColor, showNodeStatus);
				} else {
					WorkflowUtil.paintEgde(g, x+80, this.top, false, null);
				}			
			}
			
		} else {
			
			try {
				URL url = null;
				url = NodeTemplateTO.class.getResource("../../../../images/tag-workflow.png");				
				BufferedImage image = ImageIO.read(url);
				g.drawImage(image, x-9, this.top+1, null);				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			g.setColor( Color.BLACK );
			g.setFont(new Font("sans serif", Font.BOLD, 12));
			g.drawString(cacheNode.getGenericTag(), x-3, this.top+15);
			g.setFont(WorkflowUtil.getDefaultFount());
		}
		
	}

	
	private String getTaskTooltTip(UserTO uto, TaskTO planningTask) {
		String response = "";
		
		if (planningTask.isParentTask()) {
			response = uto.getBundle().getMessage(uto.getLocale(), "label.manageTask.taskParent");	
		} else {
			String envolved = planningTask.getInvolvedResources();
			if (envolved!=null && !envolved.trim().equals("")) {
				String resourceLabel = uto.getBundle().getMessage(uto.getLocale(), "label.showAllReqForm.grid.showResources");
				response = response + resourceLabel + ":" + envolved;			
			}			
		}
		
		return response;
	}


	private Color getNodeColor(TaskTO planningTask, Color defaultColor) {
		Color response = defaultColor;
		if (planningTask!=null) {

			Vector allocResources = this.getResourceTaskList(planningTask);
			if (allocResources!=null) {
				Iterator i = allocResources.iterator();
				while(i.hasNext()) {
					ResourceTaskTO rtto = (ResourceTaskTO)i.next();
					if (rtto.getTaskStatus()!=null && rtto.getTaskStatus().getStateMachineOrder()!=null) {
						Integer s = rtto.getTaskStatus().getStateMachineOrder();
						if (s.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
								s.equals(TaskStatusTO.STATE_MACHINE_HOLD)) {
							response = Color.YELLOW;
							break;
						} else if ((s.equals(TaskStatusTO.STATE_MACHINE_OPEN) || 
								s.equals(TaskStatusTO.STATE_MACHINE_REOPEN))
								&& response.equals(defaultColor)) {
							response = Color.RED;

						} else if ((s.equals(TaskStatusTO.STATE_MACHINE_CLOSE) 
								|| s.equals(TaskStatusTO.STATE_MACHINE_CANCEL)) 
								&& response.equals(defaultColor)) {
							response = Color.GREEN;
						}
					}
				}
			}			
		}
		return response;
	}

	
	private Vector getResourceTaskList(TaskTO planningTask){
		TaskDelegate tdel = new TaskDelegate();
		Vector response = null;
		
		//if task is a parent task, get the resource tasks from children tasks...			
		if (planningTask.isParentTask()) {

			Vector subTasksList = null;
			try {
				subTasksList = tdel.getSubTasksList(planningTask);
			} catch (BusinessException e) {
				subTasksList = null;
			}
			if (subTasksList!=null) {
				Iterator i = subTasksList.iterator();
				while(i.hasNext()) {
					TaskTO child = (TaskTO)i.next();
					Vector resTaskList = child.getAllocResources();
					if (resTaskList!=null && resTaskList.size()>0) {
						if (response==null) {
							response = new Vector();	
						}
						response.addAll(resTaskList);
					}
				}
			}
			
		} else {
			response = planningTask.getAllocResources();	
		}
		
		return response;
	}

	
	
	

	
	

	public String getResourceListFromVector(Vector resList) throws Exception {
		String response = "";
		if (resList!=null) {		
			Iterator i = resList.iterator();
			while(i.hasNext()) {
				ResourceTaskTO rtto = (ResourceTaskTO)i.next();
				response = response + rtto.getResource().getId() + "|" + 
							getDateOfResourceList(rtto.getStartDate()) + "|" +
							rtto.getEstimatedTime().toString() + "; ";
			}
		}
		return response;
	}


	public Vector getResourceListFromString(String resource, UserTO handler) throws BusinessException {
		Vector response = new Vector();
		if (resource!=null && !resource.trim().equals("")) {
			String[] resources = resource.split(";");
			for (int i=0; i<resources.length; i++) {
				String[] tokens = resources[i].split("\\|");
				if (tokens.length==3) {
					ResourceTaskTO rtto = new ResourceTaskTO();
					
					UserDelegate udel = new UserDelegate();
					
					UserTO uto = udel.getUser(new UserTO(tokens[0].trim()));
					ResourceTO rto = new ResourceTO(uto.getId());
					rto.setName(uto.getName());
					rto.setUsername(uto.getUsername());
					
					rtto.setResource(rto);
					rtto.setLabel(uto.getName());
					
					Timestamp dt = getPreDefinedDate(tokens[1]);
					if (dt!=null) {
						rtto.setStartDate(dt);
					} else {
						rtto.setStartDate(getDateOfResourceList(tokens[1].trim()));	
					}					
					rtto.setEstimatedTime(new Integer(tokens[2].trim()));
					rtto.setHandler(handler);
					response.add(rtto);
				}
			}
		}
		return response;
	}
	
	public static Timestamp getDateOfResourceList(String stringDate){
		Locale dummyLoc = new Locale("pt", "BR"); //default locale used only to serialize date
		return DateUtil.getDateTime(stringDate, "dd/MM/yyyy", dummyLoc);
	}

	public static String getDateOfResourceList(Timestamp date){
		Locale dummyLoc = new Locale("pt", "BR"); //default locale used only to serialize date
		return DateUtil.getDate(date, "dd/MM/yyyy", dummyLoc);
	}
	
	public static Timestamp getPreDefinedDate(String token){
		Timestamp response = null;
		if (token!=null && token.trim().equals("DEFAULT")) {
			response = DateUtil.getNow();
		}
		return response;
	}
}
