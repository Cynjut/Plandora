package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.dao.DiscussionDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class DiscussionBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    private DiscussionDAO dao = new DiscussionDAO();
	
	
	public Vector getDiscussionList(ProjectTO pto, CategoryTO category) throws BusinessException {
        Vector response = new Vector();
		ProjectBUS pbus = new ProjectBUS();
        
        try {
        	
            Vector childs = pbus.getProjectListByParent(pto, true);
            Iterator i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = (ProjectTO)i.next();
                Vector dssOfChild = this.getDiscussionList(childProj, category);
                response.addAll(dssOfChild);
            }
                    
            response.addAll(dao.getDiscussionList(pto, category));

        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

}
