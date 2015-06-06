package com.pandora.bus;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.DiscussionTO;
import com.pandora.ProjectTO;
import com.pandora.TeamInfoTO;
import com.pandora.UserTO;
import com.pandora.dao.DiscussionDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class DiscussionBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    private DiscussionDAO dao = new DiscussionDAO();
	
	
	public Vector<DiscussionTO> getDiscussionList(ProjectTO pto, CategoryTO category) throws BusinessException {
		Vector<DiscussionTO> response = new Vector<DiscussionTO>();
		ProjectBUS pbus = new ProjectBUS();
        
        try {
        	
            Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<DiscussionTO> dssOfChild = this.getDiscussionList(childProj, category);
                response.addAll(dssOfChild);
            }
                    
            response.addAll(dao.getDiscussionList(pto, category));

        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector<TeamInfoTO> getTeamInfo(UserTO uto, Timestamp iniDate) throws BusinessException {
		Vector<TeamInfoTO> response = new Vector<TeamInfoTO>();       
        try {
            response = dao.getTeamInfo(uto, iniDate);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;		
	}
	
}
