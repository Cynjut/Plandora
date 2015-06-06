package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.dao.CategoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Category entity.
 */
public class CategoryBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    CategoryDAO dao = new CategoryDAO();
    
    
    /**
     * Get a list of all Category objects from data base.
     */
    public Vector<CategoryTO> getCategoryList(boolean hideClosed) throws BusinessException {
        Vector<CategoryTO> response = new Vector<CategoryTO>();
        try {
            response = dao.getList(hideClosed);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    /**
     * Get a list of categories based on specific type and project (and subprojects).
     */
    public Vector<CategoryTO> getCategoryListByType(Integer type, ProjectTO pto, boolean includeSubProjects) throws BusinessException {
    	ProjectBUS pbus = new ProjectBUS();
        Vector<CategoryTO> response = new Vector<CategoryTO>();
        try {
 
        	if (includeSubProjects) {
                Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, false);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector<CategoryTO> childCategory = this.getCategoryListByType(type, childProj, includeSubProjects);
                    response.addAll(childCategory);
                }        		
        	}

            response.addAll(dao.getListByType(type, pto));
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a list of Category object
     */
    public CategoryTO getCategory(CategoryTO filter) throws BusinessException {
        CategoryTO response = null;
        try {
            response = (CategoryTO) dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

	
    public CategoryTO getCategoryByName(String cname, Integer type, ProjectTO pto) throws BusinessException {
        CategoryTO response = null;
        try {
            response = dao.getCategoryByName(cname, type, pto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	
    /**
     * Insert a new Category object into data base
     */
    public void insertCategory(CategoryTO cto) throws BusinessException {
        try {
            dao.insert(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }


    /**
     * Update a category object into data base
     * @param cto
     * @throws BusinessException
     */
    public void updateCategory(CategoryTO cto) throws BusinessException {
        try {
            dao.update(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }


    /**
     * Remove a category object from data base
     * @param cto
     * @throws BusinessException
     */
    public void removeCategory(CategoryTO cto) throws BusinessException {
        try {
            dao.remove(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

}
