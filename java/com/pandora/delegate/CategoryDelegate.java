package com.pandora.delegate;

import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.bus.CategoryBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Category entity information access.
 */
public class CategoryDelegate  extends GeneralDelegate{

    /** The Business object related with current delegate */
    private CategoryBUS bus = new CategoryBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.getCategoryList(boolean)
     */
    public Vector<CategoryTO> getCategoryList(boolean hideClosed) throws BusinessException{
        return bus.getCategoryList(hideClosed);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.getCategoryListByType(java.lang.Integer, com.pandora.ProjectTO, boolean)
     */
    public Vector<CategoryTO> getCategoryListByType(Integer type, ProjectTO pto, boolean includeSubProjects) throws BusinessException{
        return bus.getCategoryListByType(type, pto, includeSubProjects);
    }    

    
    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.getCategory(com.pandora.CategoryTO)
     */
    public CategoryTO getCategory(CategoryTO filter) throws BusinessException {
        return bus.getCategory(filter);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.insertCategory(com.pandora.CategoryTO)
     */
    public void insertCategory(CategoryTO cto) throws BusinessException {
        bus.insertCategory(cto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.updateCategory(com.pandora.CategoryTO)
     */
    public void updateCategory(CategoryTO cto) throws BusinessException {
        bus.updateCategory(cto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.removeCategory(com.pandora.CategoryTO)
     */
    public void removeCategory(CategoryTO cto) throws BusinessException {
        bus.removeCategory(cto);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.CategoryBUS.getCategoryByName(java.lang.String)
     */
	public CategoryTO getCategoryByName(String cname, Integer type, ProjectTO pto) throws BusinessException {
		return bus.getCategoryByName(cname, type, pto);
	}



}
