package com.pandora.delegate;

import java.util.Locale;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.bus.UserBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for User entity information access.
 */
public class UserDelegate extends GeneralDelegate{

    /** Label used like a key by system to store current user connected into http session */
    public static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";
    
    public static final String USER_SURVEY_LIST     = "USER_SURVEY_LIST";
    
    /** The Business object related with current delegate */
    UserBUS bus = new UserBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getUserList()
     */
    public Vector<UserTO> getUserList(boolean hideDisable) throws BusinessException{
        return bus.getUserList(hideDisable);
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getUser(com.pandora.UserTO)
     */            
    public UserTO getUser(UserTO filter) throws BusinessException{
        return bus.getUser(filter);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getRoot()
     */                
    public UserTO getRoot()  throws BusinessException{
        return bus.getRoot();
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.updateUser(com.pandora.UserTO)
     */            
    public void updateUser(UserTO uto) throws BusinessException{
        bus.updateUser(uto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.updatePassword(com.pandora.UserTO)
     */            
    public void updatePassword(UserTO uto) throws BusinessException{
        bus.updatePassword(uto);
    }    
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.checkUserName(com.pandora.UserTO)
     */            
    public void checkUserName(UserTO uto) throws BusinessException{
        bus.checkUserName(uto);        
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getObjectByUsername(com.pandora.UserTO)
     */                
    public UserTO getObjectByUsername(UserTO uto) throws BusinessException{
        return bus.getObjectByUsername(uto);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.insertUser(com.pandora.UserTO)
     */            
    public void insertUser(UserTO uto) throws BusinessException{
        bus.insertUser(uto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.removeUser(com.pandora.UserTO)
     */            
    public void removeUser(UserTO uto) throws BusinessException{
        bus.removeUser(uto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.authenticateUser(com.pandora.UserTO, boolean)
     */            
    public UserTO authenticateUser(UserTO uto, boolean isPassEncrypted) throws BusinessException{
        return bus.authenticateUser(uto, isPassEncrypted);
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getListByKeyword(com.pandora.ProjectTO)
     */            
    public Vector<UserTO> getListByKeyword(Vector<String> kwList) throws BusinessException {
        return bus.getListByKeyword(kwList);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getResourceByProject(com.pandora.ProjectTO, boolean, boolean)
     */            
    public Vector<ResourceTO> getResourceByProject(String projectId, boolean considerChild, boolean hideDisabled) throws BusinessException {
        return bus.getResourceByProject(projectId, considerChild, hideDisabled);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.ggetResourceByUser(com.pandora.UserTO)
     */            
    public Vector<ResourceTO> getResourceByUser(UserTO uto) throws BusinessException {        
        return bus.getResourceByUser(uto);        
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getCustomerByProject(com.pandora.UserTO)
     */            
    public Vector<CustomerTO> getCustomerByUser(UserTO uto) throws BusinessException {
        return bus.getCustomerByUser(uto);        
    }    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getLeaderByProject(java.lang.String)
     */                
    public Vector<LeaderTO> getLeaderByProject(String projectIdList) throws BusinessException {
        return bus.getLeaderByProject(projectIdList);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getUserByLeaderInAllProjects(com.pandora.LeaderTO, int)
     */                
    public Vector<UserTO> getUserByLeaderInAllProjects(LeaderTO eto, int role) throws BusinessException {
        return bus.getUserByLeaderInAllProjects(eto, role);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getCustomerByProject(com.pandora.ProjectTO)
     */      
    public ResourceTO getResource(ResourceTO rto) throws BusinessException {
        return bus.getResource(rto);        
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getCustomerByProject(com.pandora.ProjectTO, boolean)
     */        
    public Vector<CustomerTO> getCustomerByProject(ProjectTO pto, boolean considerChild) throws BusinessException {
        return bus.getCustomerByProject(pto, considerChild);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getUserTopRole(com.pandora.UserTO)
     */    
    public UserTO getUserTopRole(UserTO uto) throws BusinessException {
        return bus.getUserTopRole(uto);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.getCustomer(com.pandora.CustomerTO)
     */            
    public CustomerTO getCustomer(CustomerTO cto) throws BusinessException {
        return bus.getCustomer(cto);        
    }    
    

	public LeaderTO getLeader(LeaderTO eto) throws BusinessException {
		return bus.getLeader(eto);
	}
	
    
    /* (non-Javadoc)
     * @see com.pandora.bus.UserBUS.attribRootIntoProjectResource(com.pandora.UserTO, com.pandora.ProjectTO)
     */                
    public void attribRootIntoProjectResource(UserTO root, ProjectTO pto) throws BusinessException {
    	bus.attribRootIntoProjectResource(root, pto);
    }

    public void updatePicture(UserTO uto) throws BusinessException {
		bus.updatePicture(uto);
	}

    
	public Locale getCurrencyLocale() throws BusinessException {
		return bus.getCurrencyLocale();
	}


	public String checkCustomerViewDiscussion(UserTO uto, ProjectTO pto) throws BusinessException{
		String response = "off";
		if (uto!=null && pto!=null && uto.getId()!=null) {
		    CustomerTO cto = new CustomerTO(uto.getId());
		    cto.setProject(pto);
		    cto = bus.getCustomer(cto);
		    if (cto!=null && cto.getBoolCanSeeDiscussion()) {
		    	response = "on";	
		    }					
		}
		return response;
	}

}
