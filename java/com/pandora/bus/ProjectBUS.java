package com.pandora.bus;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.InvoiceTO;
import com.pandora.LeaderTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryPolicyTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.UserTO;
import com.pandora.dao.CustomerDAO;
import com.pandora.dao.ProjectDAO;
import com.pandora.dao.ProjectStatusDAO;
import com.pandora.dao.ResourceDAO;
import com.pandora.delegate.InvoiceDelegate;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.IncompatibleUsrsBetweenPrjException;
import com.pandora.exception.InvalidProjectUserReplaceException;
import com.pandora.exception.ProjectCannotBeClosedException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;
import com.pandora.integration.RepositoryMessageIntegration;

/**
 * This class contain the business rules related with Project entity.
 */
public class ProjectBUS extends GeneralBusiness {
    
    /** The Data Access Object related with current business entity */
    ProjectDAO dao = new ProjectDAO();

    /**
     * Get a list of all Project TOs from data base except the root project.
     */
    public Vector<ProjectTO> getProjectList() throws BusinessException{
        Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    public HashMap<String,ProjectTO> getProjectListToHash(boolean leanSelect) throws BusinessException{
        HashMap<String,ProjectTO> response = new HashMap<String,ProjectTO>();
        try {
            Vector<ProjectTO> list = dao.getProjectRoot(leanSelect);
            
            if (list!=null) {
                Iterator<ProjectTO> i = list.iterator();
                while(i.hasNext()) {
                    ProjectTO pto = i.next();
                    response.put(pto.getId(), pto);
                }
            }
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    /**
     * Get a list of all Project TOs from data base based on user id.
     */
    public Vector<ProjectTO> getProjectListByUser(UserTO uto) throws BusinessException{
        Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = this.sortProjects(dao.getProjectAllocation(uto, null));
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a Project object from data base based on user id.
     */
    public ProjectTO getProjectByUser(UserTO uto, ProjectTO pto) throws BusinessException{
    	ProjectTO response = null;
        try {
        	Vector<ProjectTO> v = dao.getProjectAllocation(uto, pto);
        	if (v!=null && v.size()>0) {
        		response = (ProjectTO)v.get(0);
        	}
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    /**
     * Get all projects from data base to be managed by root.
     */
    public Vector<ProjectTO> getProjectRoot() throws BusinessException{
        Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = dao.getProjectRoot(false);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
    }
    
    
    /**
     * Get a list of all Project TOs that is child of a specific projet 
     */
    public Vector<ProjectTO> getProjectListByParent(ProjectTO pto, boolean considerOnlyNonClosed) throws BusinessException{
        Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = dao.getProjectListByParent(pto, considerOnlyNonClosed);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    public String getProjectIn(String id, boolean considerOnlyNonClosed) throws BusinessException {
		String response = "'" + id + "'";
		Vector<ProjectTO> childList = this.getProjectListByParent(new ProjectTO(id), considerOnlyNonClosed);
		if (childList!=null) {
			Iterator<ProjectTO> i = childList.iterator();
			while(i.hasNext()) {
				ProjectTO child = i.next();
				response = response + ", " + getProjectIn(child.getId());
			}
		}
		return response;    	
    }
    
    
    public String getProjectIn(String id) throws BusinessException {
    	return getProjectIn(id, true);
	}
    
    /**
     * Get a specific Project object from data base.
     */
     public ProjectTO getProjectObject(ProjectTO pto, boolean isLazyLoad) throws BusinessException{
         ProjectTO response = null;
         try {
             response = (ProjectTO)dao.getProjectById(pto, isLazyLoad);
         } catch (DataAccessException e) {
             throw new  BusinessException(e);
         }
         return response;
     }    
    
    /**
     * Insert a new project object into data base.
     */
    public void insertProject(ProjectTO pto) throws BusinessException{
        try {
            
            //Check if current Customer alloc into project exists into the scope of parent project            
            if (pto.getAllocUsers()!=null) {
                CustomerDAO cdao = new CustomerDAO();
                if (!cdao.checkCustomerExists(pto.getAllocUsers(), pto.getParentProject())){
                    throw new IncompatibleUsrsBetweenPrjException();                        
                }
            }
            
            if (pto.getParentProject()==null || pto.getParentProject().getId()==null){
                throw new BusinessException("Parent Project cannot be null.");
            }
                    
            if (pto.getProjectStatus()==null || pto.getProjectStatus().getId()==null){
                throw new BusinessException("Project Status cannot be null.");
            }
                
            //insert a mew project!           
            dao.insert(pto);
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }

    /**
     * Update data of project object into data base.
     */
    public void updateProject(ProjectTO pto) throws BusinessException{        
        try {
            ProjectTO currProject = this.getProjectObject(pto, false);
            
            //get current and new state of project
            ProjectStatusDAO psdao = new ProjectStatusDAO();
            pto.setProjectStatus((ProjectStatusTO)psdao.getObject(pto.getProjectStatus()));
            currProject.setProjectStatus((ProjectStatusTO)psdao.getObject(currProject.getProjectStatus()));
            Integer newStatus = pto.getProjectStatus().getStateMachineOrder();
            Integer currStatus = currProject.getProjectStatus().getStateMachineOrder();
            
            //if current project is the root project throws an exception...
            if (pto.getId().equals(ProjectTO.PROJECT_ROOT_ID)){
                throw new BusinessException("The SYSTEM ROOT PROJECT cannot be updated at all.");    
            }
                        
            //setting project status to Close or Abort state => set a final date for project
            if (newStatus.equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT)){
                pto.setFinalDate(DateUtil.getNow());
            }
            
            //if the updating process is changing the current state and the current state is 
            //Close or Abort throws an exception
            if (!currStatus.equals(newStatus)){
                if (currStatus.equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT)){
                    throw new BusinessException("The current status of project (Close or Abort) cannot be changed.");    
                }
            }

            //if the updating process is changing the current state to Close or Abort and the 
            //current project contain open tasks, throws an exception          
            ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
            Vector<ResourceTaskTO> openTasks = rtdel.getListByProject(pto, "-1" , "-1", true); //get tasks of any resource, with status different of "Finished"
            if (newStatus.equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT) && (openTasks!=null && openTasks.size()>0)){
            	String list = ""; 
            	int item = 0;
            	Iterator<ResourceTaskTO> i = openTasks.iterator();
            	while(i.hasNext()) {
            		ResourceTaskTO rtto = i.next();
            		list = list + "{" + rtto.getId() + ", " + rtto.getResource().getUsername() + ", " + rtto.getTask().getName() + "} ";
            		if (item>4) {
            			break;
            		}
            		item++;            		
            	}
                throw new ProjectCannotBeClosedException("The project cannot be Closed or Aborted because there are related tasks with OPEN state. For example:" + list);    
            }

            InvoiceDelegate idel = new InvoiceDelegate();
            Vector<InvoiceTO> invlist = idel.getInvoiceList(pto.getId(), false);
            if (newStatus.equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT) && (invlist!=null && invlist.size()>0)){
            	boolean open = false;
            	Iterator<InvoiceTO> i = invlist.iterator();
            	while(i.hasNext()) {
            		InvoiceTO ito = i.next();
            		if (ito.getInvoiceStatus()!=null) {
            			Integer stat = ito.getInvoiceStatus().getStateMachineOrder();
                		if (stat!=null && !stat.equals(InvoiceStatusTO.STATE_MACHINE_CANCEL) && 
                				!stat.equals(InvoiceStatusTO.STATE_MACHINE_PAID)) {
                			open=true;
                		}
            		}
            	}
            	if (open) {
                    throw new ProjectCannotBeClosedException("The project cannot be Closed or Aborted because this project contain non-closed invoices.");            		
            	}
            }
            
            RequirementDelegate rdel = new RequirementDelegate();
            Vector<RequirementTO> reqlist = rdel.getListByProject(pto, "-1", "-1", "-2", "-1", "-1");
            if (newStatus.equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT) && (reqlist!=null && reqlist.size()>0)){
            	boolean open = false;
            	Iterator<RequirementTO> i = reqlist.iterator();
            	while(i.hasNext()) {
            		RequirementTO rto = i.next();
            		if (rto.getFinalDate()==null) {
            			open=true;
            		}
            	}
            	if (open) {
                    throw new ProjectCannotBeClosedException("The project cannot be Closed or Aborted because this project contain non-closed requirements.");            		
            	}
            }

            
            //verify which customer users must be inserted or removed from data base
            pto = this.validateProjectByUsers(currProject, pto);
                        
            //TODO checar as inconsistencias que hoje estao sendo validadas apenas pelo Jscripts:
            //por exemplo: eh lider mas nao eh recurso; eh is_req_accep mas nao eh recurso, etc. 
            //Este tipo de checagem deve ser colocado no caso de INSERT tmb. 

            dao.update(pto);
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }                
    }

    /**
     * Define the users of new project and verify if is valid. 
     */
    private ProjectTO validateProjectByUsers(ProjectTO currProject, ProjectTO newProject) throws InvalidProjectUserReplaceException, DataAccessException {
        RequirementBUS rbus = new RequirementBUS();
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        ProjectTO pto = newProject;

        //verify which customer users must be inserted or removed from data base
        pto = this.checkProjectUsers(currProject, newProject);
        
        //check if a customer that must be removed, has Requirements into project
        Vector removeCustList = pto.getRemoveCustomers();
        if (removeCustList!=null && removeCustList.size()>0){
            Iterator<CustomerTO> i = removeCustList.iterator();
            while(i.hasNext()){
                CustomerTO cto = i.next();
                Vector list;
                try {
                    list = rbus.getListByUserProject(cto, pto, false);
                } catch (BusinessException e) {
                    list = null;
                }
                if (list!=null && list.size()>0){
                    throw new InvalidProjectUserReplaceException("The customer [" + cto.getUsername() + "] cannot be removed from project, because it related with requirement into project.");        
                }
            }
        }

        //check if a resource that must be removed, has tasks into project
        Vector removeResList = pto.getRemoveResources();
        if (removeResList!=null && removeResList.size()>0){
            Iterator i = removeResList.iterator();
            while(i.hasNext()){
                ResourceTO rto = (ResourceTO)i.next();
                Vector list;
                try {
                    list = rtbus.getTaskListByResourceProject(rto, pto);
                } catch (BusinessException e) {
                    list = null;
                }
                if (list!=null && list.size()>0){
                    throw new InvalidProjectUserReplaceException("The resource [" + rto.getUsername() + "] cannot be removed from project, because it related with tasks into project.");        
                }
            }            
        }
        
        return pto;
    }
    
    /**
     * Get a list of all Project TOs from data base based on leader id.
     */
    public Vector<ProjectTO> getProjectListForManagement(LeaderTO uto, boolean hideClosed) throws BusinessException{
    	Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = dao.getProjectListForManagement(uto, hideClosed);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    } 
    
    
    public Vector<ProjectTO> getProjectListForWork(UserTO uto, boolean isAlloc) throws BusinessException{
        Vector<ProjectTO> response = new Vector<ProjectTO>();
        try {
            response = dao.getProjectListForWork(uto, isAlloc);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    

    
	public ProjectTO getProjectByName(String projectName) throws BusinessException{
        ProjectTO response = null;
        try {
            response = (ProjectTO)dao.getProjectByName(projectName);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	
	public String applyRepositoryPolicies(RepositoryMessageIntegration rep, boolean internalCommit) throws BusinessException {
		try {			
			LogUtil.log(this, LogUtil.LOG_INFO, "request commit: [" + rep.getComment() + 
					"] [" + rep.getFiles() + "] [" + rep.getProjectId() + "] [" + 
					rep.getRepositoryPath() + "] [" + rep.getAuthor() + "]");
			
			ProjectTO pto = this.getProjectObject(new ProjectTO(rep.getProjectId()), false);
			if (pto==null) {
				//verify if project exists...
				return "1000";
				
			} else {
				String comment = rep.getComment();
	
				//verify if project is opened			
				RepositoryPolicyTO ppto = pto.getRepositoryPolicy(RepositoryPolicyTO.POLICY_OPEN_PROJ);
				if (ppto!=null && ppto.getValue().equals("on")) {
					if (pto.getProjectStatus()!=null) {
						ProjectStatusTO st = pto.getProjectStatus();
						if (!st.getStateMachineOrder().equals(ProjectStatusTO.STATE_MACHINE_OPEN)) {
							return "1005";		
						}
					}
				}
	
				//verify if 'comitter' is resource of related project
				ppto = pto.getRepositoryPolicy(RepositoryPolicyTO.POLICY_PROJ_RESOURCE);
				if (ppto!=null && ppto.getValue().equals("on")) {
					if (pto.getAllocUsers()!=null) {
						boolean isOk = false;
						Iterator i = pto.getAllocUsers().iterator();
						while(i.hasNext()) {
							UserTO uto = (UserTO)i.next();
							if (rep.getAuthor()!=null && uto.getUsername().equals(rep.getAuthor().trim())) {
								if (uto instanceof ResourceTO) {
									isOk = true;
									break;
								}
							}
						}
						
						if (!isOk) {
							return "1007";
						}
					}
				}
	
				
				//TODO verify if the user that is commiting, is the owner of entity (if is a task..)
				
				
				//verify if message log is not empty
				ppto = pto.getRepositoryPolicy(RepositoryPolicyTO.POLICY_COMMENT_MANDATORY);	
				if (ppto!=null && ppto.getValue().equals("on")) {
					if (comment!=null && !comment.trim().equals("")){
						comment = comment.trim();
					} else {
						return "1010";	
					}				
				} else {
					comment = comment.trim();
				}
				
	
				//verify if current repository path is related to the project...
				ppto = pto.getRepositoryPolicy(RepositoryPolicyTO.POLICY_REPOS_SAME_PROJ);	
				if (ppto!=null && ppto.getValue().equals("on")) {
					if (!pto.getRepositoryURL().trim().endsWith(rep.getLastTokenPath())) {
						return "1020";	
					}				
				}
	
				String entityId = null;
				ppto = pto.getRepositoryPolicy(RepositoryPolicyTO.POLICY_ENTITY_REF);	
				if (ppto!=null && ppto.getValue().equals("on")) {
					
					//verify if log message content contain the expected pattern [#123]
					entityId = PlanningBUS.extractPlanningIdFromComment(comment);
					if (entityId==null) {
						return "1030";
					}
					
					//verify if entity exists...
					PlanningDelegate pdel = new PlanningDelegate();
					PlanningTO entity = pdel.getSpecializedObject(new PlanningTO(entityId));
					if (entity==null && (entity.getType().equals(PlanningRelationTO.ENTITY_TASK) 
							|| entity.getType().equals(PlanningRelationTO.ENTITY_REQ))) {
						return "1040";
					}
					
					//get the path files to be commited...
					String files = rep.getFiles();
					if (files!=null && !files.trim().equals("")) {
						if (!internalCommit) {
							RepositoryDelegate rdel = new RepositoryDelegate();
							BufferedReader br = new BufferedReader(new StringReader(files));
							if (br!=null) {
								String strLine = "";
								while( (strLine = br.readLine()) != null) {
									String[] filesToken = strLine.split("  ");
									if (filesToken!=null && filesToken.length>1) {
										String stat = filesToken[0];
										String path = filesToken[filesToken.length-1];
										if (stat!=null && path!=null && !path.trim().equals("")) {
											rdel.updateRepositoryFilePlan(path, entityId, null, null, (stat.trim().equalsIgnoreCase("D")));	
										}
									} else {
										return "1050";		
									}
								}							
							} else {
								return "1050";
							}							
						}
					} else {
						return "1050";
					}
				}
			}

		} catch(Exception e) {
			throw new  BusinessException(e);			
		}

		return "0";
	}            


	public Vector<ProjectTO> getAllProjectsByParent(ProjectTO pto, boolean considerOnlyNonClosed) throws BusinessException {
		Vector<ProjectTO> response = new Vector<ProjectTO>();
		
		Vector<ProjectTO> children = this.getProjectListByParent(pto, considerOnlyNonClosed);
		
        Iterator<ProjectTO> i = children.iterator();
        while(i.hasNext()){
            ProjectTO childProj = i.next();
    		response.add(childProj);
    		
            Vector<ProjectTO> childrenOfChild = getAllProjectsByParent(childProj, considerOnlyNonClosed);
            response.addAll(childrenOfChild);
        }
		
		return response;
	}

	
    /**
     * Verify which user (customer, resource and leader) must be inserted and which one 
     * must be removed from database. This method creates six vectors containing those 
     * information and set into Project object.
     * 
     * @param currProject
     * @param newProject
     * @return
     * @throws DataAccessException
     */
    private ProjectTO checkProjectUsers(ProjectTO currProject, ProjectTO newProject) throws DataAccessException{
        
        //verify which customers must be inserted or removed from data base
        Vector customerInsertList = new Vector();
        Vector customerRemoveList = new Vector();
        Vector customerUpdateList = new Vector();
        customerRemoveList = this.updateProjectUser(currProject.getInsertCustomers(), newProject.getInsertCustomers(), 2);
        customerInsertList = this.updateProjectUser(newProject.getInsertCustomers(), currProject.getInsertCustomers(), 1);
        customerUpdateList = this.updateProjectUser(newProject.getInsertCustomers(), currProject.getInsertCustomers(), 3);

        //verify which resources must be inserted, removed or updated from data base
        Vector resourceInsertList = new Vector();
        Vector resourceRemoveList = new Vector();
        Vector resourceUpdateList = new Vector();
        resourceRemoveList = this.updateProjectUser(currProject.getInsertResources(), newProject.getInsertResources(), 2);
        resourceInsertList = this.updateProjectUser(newProject.getInsertResources(), currProject.getInsertResources(), 1);
        resourceUpdateList = this.updateProjectUser(newProject.getInsertResources(), currProject.getInsertResources(), 3);

        //verify which leaders must be inserted, removed or updated from data base
        Vector leaderInsertList = new Vector();
        Vector leaderRemoveList = new Vector();
        leaderRemoveList = this.updateProjectUser(currProject.getInsertLeaders(), newProject.getInsertLeaders(), 2);
        leaderInsertList = this.updateProjectUser(newProject.getInsertLeaders(), currProject.getInsertLeaders(), 1);
        
        //set insertion and removing vectors into transfer object
        newProject.setInsertCustomers(customerInsertList);
        newProject.setInsertResources(resourceInsertList);
        newProject.setInsertLeaders(leaderInsertList);
        newProject.setRemoveCustomers(customerRemoveList);
        newProject.setRemoveResources(resourceRemoveList);
        newProject.setRemoveLeaders(leaderRemoveList);
        newProject.setUpdateResources(resourceUpdateList);
        newProject.setUpdateCustomers(customerUpdateList);
        
        return newProject;
    }
    
    /**
     * Perform the business rule related with update of project users lists.  
     * @param userList1 list of users (customer, resource or leaders) of project that must be compared 
     * @param userList2 list of users of project used such as base comparison
     * @param type defines if current repository is a list of insertion (type=1), removing (type=2) or updating (type=3)
     * @return list of User objects (CustomerTO, ResourceTO or LeaderTO) that must be inserted or removed from database 
     * @throws DataAccessException
     */
    private Vector updateProjectUser(Vector userList1, Vector userList2, int type) throws DataAccessException{
        Vector repository = new Vector();
        
        if (userList1.size()==0){
            if (type==2){
                repository = userList2;
            }            
        } else if (userList2.size()==0){
            if (type==2){
                repository = userList1;
            }
        } else {
	        Iterator i = userList1.iterator();
	        while (i.hasNext()){
	            UserTO utoSource = (UserTO)i.next();
	            boolean matchIt = false;
	            UserTO utoBase =null;
	            
                Iterator j = userList2.iterator();
                while (j.hasNext()){
                    utoBase = (UserTO)j.next();
                    
                    if (utoSource.getId().equals(utoBase.getId())){
                        
                        //Insertion and Removing comparison...                        
                        if (type==1 || type==2){
                            matchIt = true;                       
                        } else {
                            
            	            //Updating comparison: verify if some resource/customer attribute was changed.                            
                            if (!this.checkAttribChanges(utoSource, utoBase)){
                                repository.addElement(utoSource);
                                break;                                           
                            }                                                        
                        }
                    }
                                        
                }

                if (type==1 || type==2){                
                    if (!matchIt){
                        repository.addElement(utoSource);
                    }
                }
                
	        }
        }
        
        return repository;
    }
    
    
    /**
     * Verify if some resource attribute was changed ("CanSeeCustomer", "CapacityPerDay" and "CostPerHour")
     * @param utoSource
     * @param utoBase
     * @return true if both are equals; false if there is some difference
     * @throws DataAccessException
     */
    private boolean checkAttribChanges(UserTO utoSource, UserTO utoBase) throws DataAccessException {
        ResourceDAO rdao = new ResourceDAO();
        CustomerDAO cdao = new CustomerDAO();
        boolean response = false;
                
        if (utoSource instanceof ResourceTO && utoBase instanceof ResourceTO) {
            ResourceTO source = (ResourceTO)utoSource;
            ResourceTO base = (ResourceTO)(rdao.getObject(utoBase));
           	response =  (source.getCanSeeCustomer().equals(base.getCanSeeCustomer()) &&
           				 source.getBoolCanSelfAlloc() == base.getBoolCanSelfAlloc() &&
           				 source.getBoolCanSeeInvoice() == base.getBoolCanSeeInvoice() &&
           				 source.getBoolCanSeeRepository() == base.getBoolCanSeeRepository());
           	
        } else if (utoSource instanceof CustomerTO && utoBase instanceof CustomerTO) {
            CustomerTO source = (CustomerTO)utoSource;
            CustomerTO base = (CustomerTO) cdao.getObject(utoBase);

            response = (source.getBoolPreApproveReq() == base.getBoolPreApproveReq() &&
                    	source.getBoolIsDisabled() == base.getBoolIsDisabled() &&
                    	source.getBoolIsReqAcceptable() == base.getBoolIsReqAcceptable() &&
                    	source.getBoolCanSeeDiscussion() == base.getBoolCanSeeDiscussion() &&
                    	source.getBoolCanSeeOtherReqs() == base.getBoolCanSeeOtherReqs() &&
                    	source.getBoolCanOpenOtherOwnerReq() == base.getBoolCanOpenOtherOwnerReq() &&
                    	source.getBoolCanSeeTechComments() == base.getBoolCanSeeTechComments() &&
                    	source.getProjectFunctionId() == base.getProjectFunctionId() );    
        }
        
        return response;
    }
    

    /**
     * Get a list of projects ordered in tree format
     */
    private Vector<ProjectTO> sortProjects(Vector<ProjectTO> unorderedList){
        Vector<ProjectTO> response = new Vector<ProjectTO>();
    	Vector<ProjectTO> childProjWithoutParent = new Vector<ProjectTO>();

        if (unorderedList!=null && unorderedList.size()>0){

            //get the root projects from list...
            Iterator<ProjectTO> i = unorderedList.iterator();
            while(i.hasNext()){
                ProjectTO pto = i.next();
                if (pto.getParentProject().getId()==null || pto.getParentProject().getId().equals("0")){
                    childProjWithoutParent.addElement(pto);
                }
            }

            Iterator<ProjectTO> j = childProjWithoutParent.iterator();
            while(j.hasNext()){
                ProjectTO pto = j.next();
                Vector<ProjectTO> subList = new Vector<ProjectTO>();                        
                pto.order(unorderedList, subList, -1);
                response.addAll(subList);                
            }
            
            if (response.size()==0){
                response = unorderedList;
            } else {
                //if there are remaining child projects not considered...('orfa')
                if (response.size()<unorderedList.size()){
                    Vector<ProjectTO> diff = StringUtil.minus(unorderedList, response);
                    response.addAll(diff);
                }
            }
        }

        return response;
    }

}
