package com.pandora.bus;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.bus.auth.Authentication;
import com.pandora.bus.auth.SystemAuthentication;
import com.pandora.dao.CustomerDAO;
import com.pandora.dao.LeaderDAO;
import com.pandora.dao.ResourceDAO;
import com.pandora.dao.RootDAO;
import com.pandora.dao.UserDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.InvalidUserRoleException;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.exception.UserNameAlreadyExistsException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 * This class contain the business rules related with User entity.
 */
public class UserBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */    
    UserDAO dao = new UserDAO();

    /** The Data Acess Object related with current business entity */    
    CustomerDAO cdao = new CustomerDAO();    
    
    /** The Data Acess Object related with current business entity */    
    ResourceDAO rdao = new ResourceDAO();
    
    /** The Data Acess Object related with current business entity */    
    LeaderDAO edao = new LeaderDAO();
    
    /** The Data Acess Object related with current business entity */    
    RootDAO rootdao = new RootDAO();
    
    private int MAX_USER_PIC_BYTES = 50000;

    
    /**
     * Get a list of all User TOs from data base.
     */
    public Vector<UserTO> getUserList(boolean hideDisable) throws BusinessException{
        Vector<UserTO> response = new Vector<UserTO>();
        try {
            response = dao.getList(hideDisable);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a specific User object from data base.
     */
    public UserTO getUser(UserTO filter) throws BusinessException{
        UserTO response = null;
        try {
            response = (UserTO)dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    

    /**
     * Update information of current user into data base. <br>
     * Note: The password is not saved here, see updatePassword method.
     */
    public void updateUser(UserTO uto) throws BusinessException{
        try {
            if (uto.getFileInBytes()==null || uto.getFileInBytes().length < MAX_USER_PIC_BYTES) {
                
                if (uto.getFileInBytes()!=null && uto.getFileInBytes().length > 0) {
                    ByteArrayInputStream inStream = new ByteArrayInputStream(uto.getFileInBytes());
                    uto.setBinaryFile(inStream);
                }
                
                dao.update(uto);
                
            } else {
                throw new MaxSizeAttachmentException();    
            }
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }

    
    /**
     * Update the password of user into database
     */
    public void updatePassword(UserTO uto) throws BusinessException{
        try {
            //incrypt the password before updating process
    		String encPass = PasswordEncrypt.getInstance().encrypt(uto.getPassword());
    		uto.setPassword(encPass);
    		
            dao.updatePassword(uto);
            
            //log event...
            LogUtil.log(LogUtil.SUMMARY_CHANGE_PASS, this, uto.getUsername(), 
                    LogUtil.LOG_INFO, "[" + uto.getUsername() + "] password changed.");
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }
    
    
    /**
     * Check if username already exists into the data base and if is "in use".
     */
    public void checkUserName(UserTO uto) throws BusinessException{
        try {
            UserTO response = dao.getObjectByUsername(uto);
            if (response!=null){
                if (!response.getId().equals(uto.getId())){
                    throw new UserNameAlreadyExistsException();
                }
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        } 
    }
    
    
    /**
     * This method get a User TO from BD based on username.
     */
    public UserTO getObjectByUsername(UserTO uto) throws BusinessException{
        UserTO response = null;
        try {
            response = dao.getObjectByUsername(uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        } 
        return response;
    }

    
    /**
     * Insert a new user into data base. <br>
     * Note: The password is not saved here, see updatePassword method.
     */
    public void insertUser(UserTO uto) throws BusinessException{
        try {
            if (uto.getFileInBytes()==null || uto.getFileInBytes().length < MAX_USER_PIC_BYTES) {
                
                if (uto.getFileInBytes()!=null && uto.getFileInBytes().length > 0) {
                    ByteArrayInputStream inStream = new ByteArrayInputStream(uto.getFileInBytes());
                    uto.setBinaryFile(inStream);
                }
                
                dao.insert(uto);                    
            } else {
                throw new MaxSizeAttachmentException();    
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }         
    }
    
    
    /**
     * Remove an user of data base
     */
    public void removeUser(UserTO uto) throws BusinessException{
        try {
            
            if (uto.getUsername()==null || uto.getUsername().equalsIgnoreCase(RootTO.ROOT_USER)) {
                throw new BusinessException("This user can not be removed.");    
            }
            
            dao.remove(uto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }         
    }

    
       
    /**
     * Perform the user authentication. Verify if user exists and password is correct.<br>
     * If user exists, get the user object from data base, if user is invalid return null.
     * @param uto
     * @param isPassEncrypted if true, the password of uto is encrypted already
     */
    public UserTO authenticateUser(UserTO uto, boolean isPassEncrypted) throws BusinessException{
        UserTO fullUTo = null;
        UserTO root = null;
        try {
            
            //get data of userTO from database based on username
            fullUTo = dao.getObjectByUsername(uto);
            
            if (fullUTo!=null && fullUTo.getFinalDate()==null) {
                root = this.getRoot();
                
                String authType = SystemAuthentication.class.getName();
                if (fullUTo.getAuthenticationMode()!=null) {
                	authType = fullUTo.getAuthenticationMode();
                }
                
                //the user root must be ALWAYS authenticated by DataBase!
                if (uto.getUsername().equals(root.getUsername())) {
                    authType = SystemAuthentication.class.getName();
                }
                
                Authentication auth = Authentication.getClass(authType);
				if (auth!=null) {
	                boolean response = auth.authenticate(fullUTo, uto.getPassword(), isPassEncrypted);
	                if (!response) {
	                    fullUTo = null;
	                }
                } else {
                    throw new BusinessException("Authentication Type Unknown!");
                }

                //log event...
                LogUtil.log(LogUtil.SUMMARY_LOGIN, this, uto.getUsername(), LogUtil.LOG_INFO, 
                        "[" + uto.getUsername() + "] " + (fullUTo!=null?"authentication successfully.":"authentication failed."));
                
            } else {
                //log event...
                LogUtil.log(LogUtil.SUMMARY_LOGIN, this, uto.getUsername(), LogUtil.LOG_INFO, 
                        "[" + uto.getUsername() + "] authentication failed - user unknown.");
                throw new BusinessException("User name not found into data base.");
            }
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);  
        }
        return fullUTo;
    }    
    
        
    
    /**
     * Search into data base a list of user objects based on a filter 
     * related with username and name fields.
     */
    public Vector getListByKeyword(Vector kwList) throws BusinessException {
        try {
            return dao.getListByKeyword(kwList);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }         
    }

    
    /**
     * Get a list of Resources objects based on a project id.
     */
    public Vector<ResourceTO> getResourceByProject(String projectId, boolean considerChild, boolean hideDisabled) throws BusinessException {
        Vector<ResourceTO> response = new Vector<ResourceTO>();
        HashMap<String, ResourceTO> hmList = new HashMap<String, ResourceTO>();
        try {
        	Vector<ResourceTO> resources = rdao.getResourceByProject(projectId, hideDisabled, considerChild);
        	if (resources!=null) {
            	Iterator<ResourceTO> i = resources.iterator();
            	while(i.hasNext()) {
            		ResourceTO rto = i.next();
            		if (hmList.get(rto.getId())==null) {
            			hmList.put(rto.getId(), rto);
            			response.add(rto);
            		}
            	}        		
        	}
            return response;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }

    
    /**
     * Get a list of Resources objects based on a user id.
     * @param projectId
     * @return
     * @throws BusinessException
     */
    public Vector getResourceByUser(UserTO uto) throws BusinessException {
        try {
            return rdao.getResourceByUser(uto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }

    
    /**
     * Get a list of Customer objects based on a user id.
     * @param uto
     * @return
     * @throws BusinessException
     */
    public Vector getCustomerByUser(UserTO uto) throws BusinessException {
        try {
            return cdao.getCustomerByUser(uto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }


    /**
     * Get a list of Leader objects based on a user id.
     * @param uto
     * @return
     * @throws BusinessException
     */
    public Vector getLeaderByProject(ProjectTO pto) throws BusinessException {
        try {
            return edao.getLeaderListByProjectId(pto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }    
    
    
    public Vector getUserByLeaderInAllProjects(LeaderTO eto, int role) throws BusinessException {
        try {
            return dao.getUserByLeaderInAllProjects(eto, role);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }            	
    }
    
    
    public boolean userIsLeader(UserTO uto, ProjectTO pto) {
    	boolean response = false;
    	try {
            Vector leaderList = this.getLeaderByProject(pto);
            if (leaderList!=null && leaderList.size()>0) {
            	Iterator i = leaderList.iterator();
            	while(i.hasNext()) {
            		LeaderTO lto = (LeaderTO)i.next();
        	    	if (lto.getId().equals(uto.getId())) {
        	    		response = true;
        	    		break;
        	    	}        		
            	}
            }    		
    	} catch(Exception e) {
    		response = false;
    		e.printStackTrace();
    	}
        return response;
    }


    
    /**
     * Get a Resource object based on a resource id and project id.
     * @param rto
     * @return
     * @throws BusinessException
     */
    public ResourceTO getResource(ResourceTO rto) throws BusinessException {
        try {
            return (ResourceTO)rdao.getObject(rto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }

    
    /**
     * Get a Customer object based on a customer id and project id
     */
    public CustomerTO getCustomer(CustomerTO cto) throws BusinessException {
        try {
            return (CustomerTO)cdao.getObject(cto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
    }

    
	public LeaderTO getLeader(LeaderTO eto) throws BusinessException {
        try {
            return (LeaderTO)edao.getObject(eto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
	}
	
	
    public void attribRootIntoProjectResource(UserTO root, ProjectTO pto) throws BusinessException {
        try {
            rdao.attribRootIntoProjectResource(root, pto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }        
	}
    
    /**
     * Get a list of Customers objects based on a project object.
     */
    public Vector<CustomerTO> getCustomerByProject(ProjectTO pto, boolean considerChild) throws BusinessException {
        ProjectBUS pbus = new ProjectBUS();
        Vector<CustomerTO> response = new Vector<CustomerTO>();
        
        try {            
        	if (considerChild) {
                //get the customers of child projects 
                Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(pto.getId()), false);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector<CustomerTO> custOfChild = this.getCustomerByProject(childProj, considerChild);
                    
                    //check if list of customers just getted from database was loaded already
                    Vector custOfChildDistinct = StringUtil.minus(custOfChild, response);
                    response.addAll(custOfChildDistinct);
                }

                //check if list of resource just getted from database was loaded already            
                response.addAll(StringUtil.minus(cdao.getCustomerListByProjectId(pto), response));
                
        	} else {
        		
        		response = cdao.getCustomerListByProjectId(pto);
        	}
        	
            return response;
                        
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }
    

    /**
     * Get child user object (ResourceTO, CustomerTO, LeaderTO or RootTO) based on
     * current 'top' role of user into database
     * 
     * A role is a attribute related with Project X User, then a same user
     * could has a several roles, depending project. So this business method
     * return a strongest role possible. 
     *  
     * @param uto
     * @return
     * @throws BusinessException
     */
    public UserTO getUserTopRole(UserTO uto) throws BusinessException {
        UserTO roleUser = null;

        try {
            
           roleUser = (RootTO)rootdao.getObject(uto);
           if (roleUser==null){
                roleUser = edao.getFirstLeaderByUserId(uto);
                if (roleUser==null){
                    roleUser = rdao.getFirstResourceByUserId(uto);
                    if (roleUser==null) {
                        roleUser = cdao.getFirstCustomerByUserId(uto);
                        if (roleUser==null) {
                            throw new InvalidUserRoleException();
                        }
                    }
                }
            }
           
           	roleUser.setFileInBytes(uto.getFileInBytes());
           	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }         
        
        return roleUser;     
    }

    
    public UserTO getRoot() throws BusinessException {
	    UserTO uto = new UserTO();
	    uto.setUsername(RootTO.ROOT_USER);
	    uto = this.getObjectByUsername(uto);
        return uto;
    }


	public Locale getCurrencyLocale() throws BusinessException {
		UserTO uto = this.getRoot();
		Locale loc = Locale.ENGLISH;
		
		String currencyLocale = uto.getPreference().getPreference(PreferenceTO.GENERAL_CURRENCY);
		if (currencyLocale!=null && !currencyLocale.equals("") && currencyLocale.indexOf("_")>-1) {
			String[] tokens = currencyLocale.split("_");
			loc = new Locale(tokens[0], tokens[1]); 
		}
		return loc;
	}




}
