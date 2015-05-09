package com.pandora;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

/**
 * This object it is a bean that represents the Project entity.
 */
public class ProjectTO extends PlanningTO{

	private static final long serialVersionUID = 1L;

    /** The id used by system to specify the System Project Root */
    public static final String PROJECT_ROOT_ID = "0";
    
    
    /** Basic name of project*/
    private String name;
       
    /** Reference to parent project */
    private ProjectTO parentProject;
    
    /** Current status of project. */
    private ProjectStatusTO projectStatus;

    private String canAlloc;
    
    /** List of allocated users into project. */
    private Vector allocUsers;

    /** List of occurrences related to the project. */
    private Vector occurrenceList;
   
    private String repositoryUser;
    
    private String repositoryPass;
    
    private String repositoryURL;
    
    private String repositoryClass;
    
    private Timestamp estimatedClosureDate;

    private Boolean allowBillable;
    
    private HashMap repositoryPolicies;
    
    
    /** List of LeaderTO objects related with project. This attribute is not persistent.*/
    private Vector projectLeaders;
    
    /** List of customers that must be used by DAO into insertion process. This attribute is not persistent. */
    private Vector insertCustomers;

    /** List of resources that must be used by DAO into insertion process. This attribute is not persistent.  */
    private Vector insertResources;

    /** List of leaders that must be used by DAO into insertion process. This attribute is not persistent.  */
    private Vector insertLeaders;

    /** List of customers that must be used by DAO into removing process. This attribute is not persistent. */
    private Vector removeCustomers;

    /** List of resources that must be used by DAO intto removing process. This attribute is not persistent.  */
    private Vector removeResources;

    /** List of leaders that must be used by DAO intto removing process. This attribute is not persistent.  */
    private Vector removeLeaders;

    /** List of resources that must be used by DAO into updating process. This attribute is not persistent.  */
    private Vector updateResources;

    /** List of resources that must be used by DAO into updating process. This attribute is not persistent.  */
    private Vector updateCustomers;
       
    /** This attribute is not persistent. */
    private int gridLevel;
    
    
    
    /** This field is used by view layer to show a current role of user related with project. This attribute is not persistent. */
    private String roleIntoProject;

    /** This field is used by define if current user of project can see the agile board. This attribute is not persistent. */
    private boolean canSelfAlloc = false;

    private boolean canSeeRepository = true;
    
    private boolean canSeeInvoice = false;
    
       
    
    /**
     * Constructor 
     */
    public ProjectTO(){
    }

    /**
     * Constructor 
     */    
    public ProjectTO(String id){
        this.setId(id);
    }      
    
    
    public String getEntityType(){
        return PLANNING_PROJECT;
    }
    
    
    /**
     * This method is overriding because is necessary to update the user objects
     * related with project (users allocated) when the id of project is changed.  
     */
    public void setId(String newValue) {
        super.setId(newValue);
        this.linkProjectWithAlloc();
    }
    
    
    /**
     * Set id of current project into all objects related with
     * allocated users, i.e., vector of customers, resources and leaders
     */
    private void linkProjectWithAlloc(){
        if (this.allocUsers!=null){
            Iterator i = this.allocUsers.iterator();
            while(i.hasNext()){
                CustomerTO cto = (CustomerTO)i.next();
                cto.setProject(this);
            }
        }
        
        if (this.insertCustomers!=null){
            Iterator i = this.insertCustomers.iterator();
            while(i.hasNext()){
                CustomerTO cto = (CustomerTO)i.next();
                cto.setProject(this);
            }            
        }

        if (this.insertResources!=null){
            Iterator i = this.insertResources.iterator();
            while(i.hasNext()){
                ResourceTO rto = (ResourceTO)i.next();
                rto.setProject(this);
            }                        
        }

        if (this.insertLeaders!=null){
            Iterator i = this.insertLeaders.iterator();
            while(i.hasNext()){
                LeaderTO eto = (LeaderTO)i.next();
                eto.setProject(this);
            }            
        }
        
    }

	public boolean isLeader(String id) {
		boolean response = false;
        if (this.allocUsers!=null){
            Iterator i = this.allocUsers.iterator();
            while(i.hasNext()){
                UserTO to = (UserTO)i.next();
                if (to instanceof LeaderTO && id.equals(to.getId())) {
					response = true;
					break;
				}
            }            
        }
		return response;
	}

	public RepositoryPolicyTO getRepositoryPolicy(String policyType) {
		RepositoryPolicyTO response = null;
		if (this.repositoryPolicies!=null) {
			response= (RepositoryPolicyTO)this.repositoryPolicies.get(policyType);
		}
		return response;
	}

	
	public void addRepositoryPolicy(RepositoryPolicyTO policy) {
		if (this.repositoryPolicies==null) {
			this.repositoryPolicies = new HashMap();
		}
		this.repositoryPolicies.put(policy.getPolicyType(), policy);
	}
	
	
    ///////////////////////////////////////////    
	public void setRepositoryPolicies(HashMap policies) {
		this.repositoryPolicies = policies;
	}
	public HashMap getRepositoryPolicies() {
		return this.repositoryPolicies;
	}
	
	

    ///////////////////////////////////////////    
    public ProjectTO getParentProject() {
        return parentProject;
    }
    public void setParentProject(ProjectTO newValue) {
        this.parentProject = newValue;
    }
    
    ///////////////////////////////////////////    
    public ProjectStatusTO getProjectStatus() {
        return projectStatus;
    }
    public void setProjectStatus(ProjectStatusTO newValue) {
        this.projectStatus = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////////    
    public boolean getBollCanAlloc() {
    	return (canAlloc==null || canAlloc.equals("1"));
	}    
    public String getCanAlloc() {
		return canAlloc;
	}
    public void setCanAlloc(String newValue) {
		this.canAlloc = newValue;
	}

	///////////////////////////////////////////    
    public Vector getAllocUsers() {
        return allocUsers;
    }
    public void setAllocUsers(Vector newValue) {
        this.allocUsers = newValue;
    }
        
    ///////////////////////////////////////////    
    public Vector getInsertCustomers() {
        return insertCustomers;
    }
    public void setInsertCustomers(Vector newValue) {
        this.insertCustomers = newValue;
    }
    
    ///////////////////////////////////////////    
    public Vector getInsertLeaders() {
        return insertLeaders;
    }
    public void setInsertLeaders(Vector newValue) {
        this.insertLeaders = newValue;
    }
    
    ///////////////////////////////////////////    
    public Vector getInsertResources() {
        return insertResources;
    }
    public void setInsertResources(Vector newValue) {
        this.insertResources = newValue;
    }
    
    ///////////////////////////////////////////    
    public Vector getRemoveCustomers() {
        return removeCustomers;
    }
    public void setRemoveCustomers(Vector newValue) {
        this.removeCustomers = newValue;
    }
    
    ///////////////////////////////////////////    
    public Vector getRemoveLeaders() {
        return removeLeaders;
    }
    public void setRemoveLeaders(Vector newValue) {
        this.removeLeaders = newValue;
    }
    
    ///////////////////////////////////////////    
    public Vector getRemoveResources() {
        return removeResources;
    }
    public void setRemoveResources(Vector newValue) {
        this.removeResources = newValue;
    }
        
    ///////////////////////////////////////////        
    public Vector getUpdateResources() {
        return updateResources;
    }
    public void setUpdateResources(Vector newValue) {
        this.updateResources = newValue;
    }

    ///////////////////////////////////////////        
    public Vector getUpdateCustomers() {
        return updateCustomers;
    }
    public void setUpdateCustomers(Vector newValue) {
        this.updateCustomers = newValue;
    }
        
    ///////////////////////////////////////////    
    public String getRoleIntoProject() {
        return roleIntoProject;
    }
    public void setRoleIntoProject(String newValue) {
        this.roleIntoProject = newValue;
    }

    
    ///////////////////////////////////////////     
    public boolean getCanSelfAlloc() {
		return canSelfAlloc;
	}
	public void setCanSelfAlloc(boolean newValue) {
		this.canSelfAlloc = newValue;
	}


	///////////////////////////////////////////    
    public boolean getCanSeeRepository() {
		return this.canSeeRepository;
	}	
	public void setCanSeeRepository(boolean newValue) {
		this.canSeeRepository = newValue;
	}
	

	///////////////////////////////////////////    
    public boolean getCanSeeInvoice() {
		return this.canSeeInvoice;
	}	
	public void setCanSeeInvoice(boolean newValue) {
		this.canSeeInvoice = newValue;
	}


	
	///////////////////////////////////////////    
    public Vector getProjectLeaders() {
    	
    	if (projectLeaders==null || projectLeaders.size()==0) {
        	//lazzy initialization
        	UserDelegate udel = new UserDelegate();
        	try {
				projectLeaders = udel.getLeaderByProject(this);
			} catch (BusinessException e) {
				projectLeaders = null;
			}
    	}
    	
        return projectLeaders;
    }
    public void setProjectLeaders(Vector newValue) {
        this.projectLeaders = newValue;
    }
    
    //////////////////////////////////////////////   
    public Vector getOccurrenceList() {
        return occurrenceList;
    }
    public void setOccurrenceList(Vector newValue) {
        this.occurrenceList = newValue;
    }
    
    //////////////////////////////////////////////    
    public int getGridLevel() {
        return gridLevel;
    }
    public void setGridLevel(int newValue) {
        this.gridLevel = newValue;
    }

    //////////////////////////////////////////////    
    public String getRepositoryUser() {
		return repositoryUser;
	}
	public void setRepositoryUser(String newValue) {
		this.repositoryUser = newValue;
	}

    //////////////////////////////////////////////	
	public String getRepositoryPass() {
		return repositoryPass;
	}
	public void setRepositoryPass(String newValue) {
		this.repositoryPass = newValue;
	}

    //////////////////////////////////////////////	
	public String getRepositoryURL() {
		return repositoryURL;
	}
	public void setRepositoryURL(String newValue) {
		this.repositoryURL = newValue;
	}

    //////////////////////////////////////////////
	public String getRepositoryClass() {
		return repositoryClass;
	}
	public void setRepositoryClass(String newValue) {
		this.repositoryClass = newValue;
	}
	
	
    //////////////////////////////////////////////	
    public Boolean getAllowBillable() {
		return allowBillable;
	}
	public void setAllowBillable(Boolean newValue) {
		this.allowBillable = newValue;
	}

	///////////////////////////////////////////////
    public Timestamp getEstimatedClosureDate() {
		return estimatedClosureDate;
	}
	public void setEstimatedClosureDate(Timestamp newValue) {
		this.estimatedClosureDate = newValue;
	}	

	/**
     * Generate a ordered list based on ruled of parent project. 
     * This recursive logic must be used by grid in order to 
     * display a tree structure of projects.
     */
    public int order(Vector unorderedList, Vector orderedList, int level){
        int newLevel = level+1;
        
        this.setGridLevel(newLevel);
        orderedList.addElement(this);
        
        //get a list of child projects...
        Iterator i = unorderedList.iterator();
        while(i.hasNext()){
            //...for each child task...            
            ProjectTO childPrj = (ProjectTO)i.next();
            if (childPrj.getParentProject()!=null && childPrj.getParentProject().getId()!=null && 
                    childPrj.getParentProject().getId().equals(this.getId())){
                newLevel = childPrj.order(unorderedList, orderedList, newLevel);
            }
        }

        newLevel--;

        return newLevel;
    }


}
