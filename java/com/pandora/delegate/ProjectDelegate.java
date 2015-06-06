package com.pandora.delegate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.ProjectBUS;
import com.pandora.exception.BusinessException;
import com.pandora.integration.RepositoryMessageIntegration;

/**
 * This class has the interface for the Project entity.
 */
public class ProjectDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    ProjectBUS bus = new ProjectBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectList()
     */
    public Vector<ProjectTO> getProjectList() throws BusinessException{
        return bus.getProjectList();
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListToHash(boolean)
     */    
    public HashMap<String,ProjectTO> getProjectListToHash(boolean leanSelect) throws BusinessException{
        return bus.getProjectListToHash(leanSelect);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListByParent(com.pandora.ProjectTO, boolean)
     */    
    public Vector<ProjectTO> getProjectListByParent(ProjectTO parent, boolean considerOnlyNonClosed) throws BusinessException {
    	return bus.getProjectListByParent(parent, considerOnlyNonClosed);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListByUser(com.pandora.UserTO)
     */
    public Vector<ProjectTO> getProjectListByUser(UserTO uto) throws BusinessException{
        return bus.getProjectListByUser(uto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectByUser(com.pandora.UserTO, com.pandora.ProjectTO)
     */    
    public ProjectTO getProjectByUser(UserTO uto, ProjectTO pto)  throws BusinessException{
    	return bus.getProjectByUser(uto, pto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectRoot()
     */
    public Vector<ProjectTO> getProjectRoot() throws BusinessException{
        return bus.getProjectRoot();
    } 
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectObject(com.pandora.ProjectTO, boolean)
     */
    public ProjectTO getProjectObject(ProjectTO pto, boolean isLazyLoad) throws BusinessException{
        return bus.getProjectObject(pto, isLazyLoad);
    }    

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectIn(java.lang.String)
     */        
    public String getProjectIn(String id, boolean considerOnlyNonClosed) throws BusinessException {
    	return bus.getProjectIn(id, considerOnlyNonClosed);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectIn(java.lang.String)
     */        
    public String getProjectIn(String id) throws BusinessException {
    	return bus.getProjectIn(id, true);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.insertProject(com.pandora.ProjectTO)
     */
    public void insertProject(ProjectTO pto) throws BusinessException{
        bus.insertProject(pto);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.updateProject(com.pandora.ProjectTO)
     */
    public void updateProject(ProjectTO pto) throws BusinessException{
        bus.updateProject(pto);                
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListForManagement(com.pandora.LeaderTO, boolean)
     */
    public Vector<ProjectTO> getProjectListForManagement(LeaderTO uto, boolean hideClosed) throws BusinessException{
        return bus.getProjectListForManagement(uto, hideClosed);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListForWork(com.pandora.UserTO, boolean, boolean)
     */
    public Vector<ProjectTO> getProjectListForWork(UserTO uto, boolean isAlloc, boolean nonClosed) throws BusinessException{
        return bus.getProjectListForWork(uto, isAlloc, nonClosed);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.getProjectListForWork(com.pandora.UserTO, boolean)
     */
    public Vector<ProjectTO> getProjectListForWork(UserTO uto, boolean isAlloc) throws BusinessException{
        return bus.getProjectListForWork(uto, isAlloc, true);
    }    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectBUS.applyRepositoryPolicies(com.pandora.integration.RepositoryMessageIntegration)
     */
	public String applyRepositoryPolicies(RepositoryMessageIntegration rep) throws BusinessException {
		return bus.applyRepositoryPolicies(rep, false);
	}    
    
    
	public ProjectTO getProjectByName(String projectName) throws BusinessException{
		return bus.getProjectByName(projectName);
	}

	public Vector<ProjectTO> getAllProjectsByParent(ProjectTO pto, boolean considerOnlyNonClosed) throws BusinessException{
		return bus.getAllProjectsByParent(pto, considerOnlyNonClosed);
	}

	
    public boolean containsProject(Vector<ProjectTO> projects, ProjectTO pto){
    	boolean contains = false;
    	Iterator<ProjectTO> it = projects.iterator();
    	while(it.hasNext()){
    		if(pto.getId().equals(it.next().getId())){
    			contains = true;
    			break;
    		}
    	}
    	return contains;
    }
	
}
