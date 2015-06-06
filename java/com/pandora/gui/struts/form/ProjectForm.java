package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;
import com.pandora.RepositoryPolicyTO;
import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the data of Manage Project Form 
 */
public class ProjectForm extends GeneralStrutsForm{
    
	private static final long serialVersionUID = 1L;
	
    /** Name field of project form */
    private String name;
    
    /** Description field of project form */
    private String description;
    
    /** Parent Project id related */
    private String parentProject;
    
    /** Current Project Status id. */
    private String projectStatus;
    
    /** Text typed into user searcher field */
    private String userSearch;
    
    /** User selected into user combo (project resource area) */
    private String selectedUserId;
    
    /** User selected to be removed from the allocation users grid */
    private String removedUserId;
        
    /** Name of Open State stored into data base to be used into combo on 'insert' save-mode */
    private String openStatusName;
    
    /** current locale of user connected */
    private Locale userLocale;

    /** The Creation date of project */
    private Timestamp creationDate;
    
    private String budget;
    
    private String budgetCurrencySymbol;
    
    private String canAlloc;
    
    private String repositoryUser;
    
    private String repositoryPass;
    
    private String repositoryURL;
    
    private String repositoryClass;
    
    /** The estimated date of project closure */
    private String estimatedClosureDate;
    
    private boolean allowBillable = false;
    
    private HashMap<String,RepositoryPolicyTO> repositoryPolicies;
    
    private String showRepositoryUserPwd = "on";
    
    private String htmlQualifications = "";
    
    
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.allowBillable = false;
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_OPEN_PROJ, "off");
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_PROJ_RESOURCE, "off");
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_REPOS_SAME_PROJ, "off");
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_ENTITY_REF, "off");
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_COMMENT_MANDATORY, "off");
	}
    
	
    public void clear(){
        id="";
        name= null;
        description= null;
        parentProject= null;
        projectStatus= null;
        userSearch= null;
        selectedUserId= null;
        canAlloc = null;
        repositoryUser = null;
        repositoryPass = null;
        repositoryURL = null;
        repositoryClass = null;
        estimatedClosureDate = null;
        showRepositoryUserPwd = "on";
        budget=null;
        this.setSaveMethod(null, null);
        this.setAdditionalFields(null);
    } 
    
    
	public String getRepositoryPolicy(String policyType) {
		String response = null;
		RepositoryPolicyTO rpto = null;
		if (this.repositoryPolicies!=null) {
			rpto = (RepositoryPolicyTO)this.repositoryPolicies.get(policyType);
			if (rpto!=null) {
				response = rpto.getValue();	
			} else {
				response = "";
			}
		}
		return response;
	}
	
	public void setRepositoryPolicy(String type, String value) {
		RepositoryPolicyTO rpto = null;
		if (this.repositoryPolicies!=null) {
			rpto = (RepositoryPolicyTO)this.repositoryPolicies.get(type);
			if (rpto==null) {
				rpto = new RepositoryPolicyTO();
				rpto.setPolicyType(type);
				rpto.setProject(new ProjectTO(id));
			}
			rpto.setValue(value);
		}
	}
	
	
    ///////////////////////////////////////////    
	public void setRepositoryPolicies(HashMap<String,RepositoryPolicyTO> policies) {
		this.repositoryPolicies = policies;
	}
	public HashMap<String,RepositoryPolicyTO> getRepositoryPolicies() {
		return this.repositoryPolicies;
	}

    
    ///////////////////////////////////////////    
    public String getCanAlloc() {
		return canAlloc;
	}
    public void setCanAlloc(String newValue) {
		this.canAlloc = newValue;
	}
    
    ///////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    ///////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////////////    
    public String getParentProject() {
        return parentProject;
    }
    public void setParentProject(String newValue) {
        this.parentProject = newValue;
    }
    
    ///////////////////////////////////////////////    
    public String getProjectStatus() {
        return projectStatus;
    }
    public void setProjectStatus(String newValue) {
        this.projectStatus = newValue;
    }

    ///////////////////////////////////////////////    
    public String getUserSearch() {
        return userSearch;
    }
    public void setUserSearch(String newValue) {
        this.userSearch = newValue;
    }    
    
    ///////////////////////////////////////////////    
    public String getSelectedUserId() {
        return selectedUserId;
    }
    public void setSelectedUserId(String newValue) {
        this.selectedUserId = newValue;
    }    

    ///////////////////////////////////////////////    
    public String getRemovedUserId() {
        return removedUserId;
    }
    public void setRemovedUserId(String newValue) {
        this.removedUserId = newValue;
    }    
    
	////////////////////////////////////////////////    
    public Locale getUserLocale() {
        return userLocale;
    }
    public void setUserLocale(Locale newValue) {
        this.userLocale = newValue;
    }   
    
    ///////////////////////////////////////////////    
    public String getOpenStatusName() {
        return openStatusName;
    }
    public void setOpenStatusName(String newValue) {
        this.openStatusName = newValue;
    }

    ///////////////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    

    ///////////////////////////////////////////////
    public String getEstimatedClosureDate() {
		return estimatedClosureDate;
	}
	public void setEstimatedClosureDate(String newValue) {
		this.estimatedClosureDate = newValue;
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
	public boolean getAllowBillable() {
		return allowBillable;
	}
	public void setAllowBillable(boolean newValue) {
		this.allowBillable = newValue;
	}

	///////////////////////////////////////////////
	public String getShowRepositoryUserPwd() {
		return showRepositoryUserPwd;
	}
	public void setShowRepositoryUserPwd(String newValue) {
		this.showRepositoryUserPwd = newValue;
	}
	
    //////////////////////////////////////////////		
	public boolean getPolicyMandatoryComment() {
		String value = getRepositoryPolicy(RepositoryPolicyTO.POLICY_COMMENT_MANDATORY);
		return (value!=null && value.equals("on"));
	}
	public void setPolicyMandatoryComment(boolean newValue) {
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_COMMENT_MANDATORY, newValue?"on":"off");
	}


    //////////////////////////////////////////////	
	public boolean getPolicyAllowOnlyResources() {
		String value = getRepositoryPolicy(RepositoryPolicyTO.POLICY_PROJ_RESOURCE);
		return (value!=null && value.equals("on"));		
	}
	public void setPolicyAllowOnlyResources(boolean newValue) {
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_PROJ_RESOURCE, newValue?"on":"off");
	}

	
    //////////////////////////////////////////////	
	public boolean getPolicyAllowOnlyOpenProj() {
		String value = getRepositoryPolicy(RepositoryPolicyTO.POLICY_OPEN_PROJ);
		return (value!=null && value.equals("on"));
	}
	public void setPolicyAllowOnlyOpenProj(boolean newValue) {
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_OPEN_PROJ, newValue?"on":"off");

	}


    //////////////////////////////////////////////	
	public boolean getPolicyCheckRepositorySource() {
		String value = getRepositoryPolicy(RepositoryPolicyTO.POLICY_REPOS_SAME_PROJ);
		return (value!=null && value.equals("on"));
	}
	public void setPolicyCheckRepositorySource(boolean newValue) {
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_REPOS_SAME_PROJ, newValue?"on":"off");
	}

	
    //////////////////////////////////////////////	
	public boolean getPolicyCheckEntityReference() {
		String value = getRepositoryPolicy(RepositoryPolicyTO.POLICY_ENTITY_REF);
		return (value!=null && value.equals("on"));
	}
	public void setPolicyCheckEntityReference(boolean newValue) {
		setRepositoryPolicy(RepositoryPolicyTO.POLICY_ENTITY_REF, newValue?"on":"off");
	}
	
	
    //////////////////////////////////////////////	
	public void setBudget(String newValue) {
		budget = newValue;
	}
	public String getBudget() {
		return budget;
	}

	
    //////////////////////////////////////////////
	public void setBudgetCurrencySymbol(String newValue) {
		budgetCurrencySymbol = newValue;
	}
	public String getBudgetCurrencySymbol() {
		return budgetCurrencySymbol;
	}
	
	
    //////////////////////////////////////////////
	public String getHtmlQualifications() {
		return htmlQualifications;
	}
	public void setHtmlQualifications(String newValue) {
		this.htmlQualifications = newValue;
	}

	
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		boolean thereIsLeader = false;
		boolean connectedUserIsLeader = false;
		UserTO currUser = SessionUtil.getCurrentUser(request);			

		if (this.operation.equals("searchUser")) {
		    
			if (this.userSearch==null || this.userSearch.trim().equals("")){             	
			    errors.add("Pesquisa", new ActionError("validate.project.blankUserSearch") );
			}

		} else if (this.operation.equals("saveProject")) {

			if (this.name==null || this.name.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.project.blankName") );
			}
			if (this.description==null || this.description.trim().equals("")){
			    errors.add("Descricao", new ActionError("validate.project.blankDescription") );
			}

		    Vector allocList = (Vector)request.getSession().getAttribute("allocList");
		    if (allocList!=null){
		        Iterator i = allocList.iterator();
		        while(i.hasNext()){
		            UserTO uto = (UserTO)i.next();
		            
					//check if there is at least one leader among the allocated users 
					//AND check if the user on http session is a leader into current project		            
		            String leaderValue = request.getParameter("cb_" + uto.getId() + "_" + ProjectGridCheckBoxDecorator.LEADER_COL);
		            if (leaderValue!=null) {
		                thereIsLeader = true;
			            if (uto.getId().equals(currUser.getId())){
			                connectedUserIsLeader = true;
			            }		                
                    }
		        }
		    }
			if (!thereIsLeader){
			    errors.add("Leader", new ActionError("validate.project.blankLeaderList") );
			}
			if (!connectedUserIsLeader && !currUser.getUsername().toLowerCase().equals(RootTO.ROOT_USER)){
			    errors.add("Leader", new ActionError("validate.project.userNotLeader") );
			}
			
			if (repositoryURL!=null && !repositoryURL.trim().equals("")) {
				repositoryURL = repositoryURL.trim();
				
				if (repositoryURL.charAt(repositoryURL.length()-1)=='/' &&  repositoryURL.length()>1) {
					repositoryURL = repositoryURL.substring(0, repositoryURL.length()-1);
				}
				
				if (repositoryURL.indexOf("\\")>-1) {
					errors.add("Leader", new ActionError("validate.project.slashRepositoryURL") );	
				}
				
			}

		} else if (this.operation.equals("addUser")) {
		    
		    String userIdSelect = request.getParameter("selectedUserId");
		    if (userIdSelect==null) {
		        errors.add("Usuario", new ActionError("message.noUserSelected") );
		    } else {
			    Vector allocList = (Vector)request.getSession().getAttribute("allocList");
			    if (allocList!=null){
			        Iterator i = allocList.iterator();
			        while(i.hasNext()){
			            UserTO uto = (UserTO)i.next();
			            if (uto.getId().equals(userIdSelect)){
			                errors.add("Usuario", new ActionError("message.userIntoListExists") );  
			                break;
			            }
			        }
			    }		        
		    }
		    
		}

		return errors;
	}


}