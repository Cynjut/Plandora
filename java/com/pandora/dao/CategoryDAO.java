package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Category entity into data base. 
 */
public class CategoryDAO extends DataAccess {

    /**
     * Get a list of all Category TOs from data base.
     */
    public Vector<CategoryTO> getList(boolean hideClosed) throws DataAccessException {
        Vector<CategoryTO> response = new Vector<CategoryTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getList(hideClosed, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
    }


    /**
     * Get a list of categories based on specific type and project
     */
    public Vector<CategoryTO> getListByType(Integer type, ProjectTO pto) throws DataAccessException {
        Vector<CategoryTO> response = new Vector<CategoryTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getListByType(c, type, pto);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
    }

    
	public CategoryTO getCategoryByName(String cname, Integer type, ProjectTO pto) throws DataAccessException {
        CategoryTO response = null;
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getCategoryByName(c, cname, type, pto);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}
    
   
	private CategoryTO getCategoryByName(Connection c, String cname, Integer type, ProjectTO pto) throws DataAccessException {
		CategoryTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select c.id, c.name, c.description, c.type, c.billable, c.is_defect, c.is_testing, " +
					   "c.project_id, p.name as PROJECT_NAME, c.disable_view, c.is_developing, c.category_order " +
					   "from category c LEFT OUTER JOIN project p on c.project_id = p.id " +
					   "where (c.id='0' OR (c.type=? AND (c.project_id is NULL or c.project_id=?)) " +
					   "and (c.disable_view is null or c.disable_view=0)) and c.name=?" +
					   "order by c.category_order");
			pstmt.setInt(1, type.intValue());
			pstmt.setString(2, pto.getId());
			pstmt.setString(3, cname);
			rs = pstmt.executeQuery();
			if (rs.next()) {
			    response = this.populateBeanByResultSet(rs);
			}	
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	private Vector<CategoryTO> getListByType(Connection c, Integer type, ProjectTO pto) throws DataAccessException {
		Vector<CategoryTO> response= new Vector<CategoryTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select c.id, c.name, c.description, c.type, c.billable, c.is_defect, c.is_testing, " +
					   "c.project_id, p.name as PROJECT_NAME, c.disable_view, c.is_developing, c.category_order " +
					   "from category c LEFT OUTER JOIN project p on c.project_id = p.id " +
					   "where (c.id='0' OR (c.type=? AND (c.project_id is NULL or c.project_id=?)) " +
					   "and (c.disable_view is null or c.disable_view=0)) " +
					   "order by c.category_order");
			pstmt.setInt(1, type.intValue());
			pstmt.setString(2, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    CategoryTO cto = this.populateBeanByResultSet(rs);
			    response.addElement(cto);
			}	
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    /**
     * Get a Category object from data base.
     */
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        CategoryTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    CategoryTO cto = (CategoryTO)to;
		    
		    pstmt = c.prepareStatement("select c.id, c.name, c.description, c.type, c.billable, c.is_defect, c.is_testing, " +
					   "c.project_id, p.name as PROJECT_NAME, c.disable_view, c.is_developing, c.category_order " +
					   "from category c LEFT OUTER JOIN project p on c.project_id = p.id " +
					   "where c.id = ?");		    
			pstmt.setString(1, cto.getId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
			    response = this.populateBeanByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Insert a new Category object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    CategoryTO cto = (CategoryTO)to;
			cto.setId(this.getNewId());
					    
			pstmt = c.prepareStatement("insert into category (id, name, description, type, project_id, billable, " +
									   "is_defect, is_testing, disable_view, is_developing, category_order) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, cto.getId());
			pstmt.setString(2, cto.getName());
			pstmt.setString(3, cto.getDescription());
			if (cto.getType()!=null) {
			    pstmt.setInt(4, cto.getType().intValue());    
			} else {
			    pstmt.setInt(4, 0);
			}
			if (cto.getProject()!=null) {
			    pstmt.setString(5, cto.getProject().getId());   
			} else {
			    pstmt.setNull(5, java.sql.Types.VARCHAR);
			}
			if (cto.getIsBillable()!=null && cto.getIsBillable().booleanValue()) {
			    pstmt.setInt(6, 1);   
			} else {
			    pstmt.setInt(6, 0);
			}
			if (cto.getIsDefect()!=null && cto.getIsDefect().booleanValue()) {
			    pstmt.setInt(7, 1);   
			} else {
			    pstmt.setInt(7, 0);
			}
			if (cto.getIsTesting()!=null && cto.getIsTesting().booleanValue()) {
			    pstmt.setInt(8, 1);   
			} else {
			    pstmt.setInt(8, 0);
			}	
			if (cto.getIsHidden()!=null && cto.getIsHidden().booleanValue()) {
			    pstmt.setInt(9, 1);   
			} else {
			    pstmt.setInt(9, 0);
			}
			if (cto.getIsDevelopingTask()!=null && cto.getIsDevelopingTask().booleanValue()) {
			    pstmt.setInt(10, 1);   
			} else {
			    pstmt.setInt(10, 0);
			}
		    pstmt.setInt(11, cto.getPositionOrder());   
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    /**
     * Update data of a Category object into data base.
     */
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    CategoryTO cto = (CategoryTO)to;
			pstmt = c.prepareStatement("update category set name=?, description=?, type=?, project_id=?, " +
								       "billable=?, is_defect=?, is_testing=?, disable_view=?, is_developing=?, " +
								       "category_order=? where id=?");
			pstmt.setString(1, cto.getName());
			pstmt.setString(2, cto.getDescription());
			if (cto.getType()!=null) {
			    pstmt.setInt(3, cto.getType().intValue());    
			} else {
			    pstmt.setInt(3, 0);
			}
			if (cto.getProject()!=null) {
			    pstmt.setString(4, cto.getProject().getId());   
			} else {
			    pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
			if (cto.getIsBillable()!=null && cto.getIsBillable().booleanValue()) {
			    pstmt.setInt(5, 1);   
			} else {
			    pstmt.setInt(5, 0);
			}
			if (cto.getIsDefect()!=null && cto.getIsDefect().booleanValue()) {
			    pstmt.setInt(6, 1);   
			} else {
			    pstmt.setInt(6, 0);
			}
			if (cto.getIsTesting()!=null && cto.getIsTesting().booleanValue()) {
			    pstmt.setInt(7, 1);   
			} else {
			    pstmt.setInt(7, 0);
			}
			if (cto.getIsHidden()!=null && cto.getIsHidden().booleanValue()) {
			    pstmt.setInt(8, 1);   
			} else {
			    pstmt.setInt(8, 0);
			}
			if (cto.getIsDevelopingTask()!=null && cto.getIsDevelopingTask().booleanValue()) {
			    pstmt.setInt(9, 1);   
			} else {
			    pstmt.setInt(9, 0);
			}
			pstmt.setInt(10, cto.getPositionOrder());
			pstmt.setString(11, cto.getId());
			
			pstmt.executeUpdate();
															
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    /**
     * Remove a Category from data base
     */
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    CategoryTO cto = (CategoryTO)to;
		    
			pstmt = c.prepareStatement("delete from category where id=?");
			pstmt.setString(1, cto.getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    
    private Vector<CategoryTO> getList(boolean hideClosed, Connection c) throws DataAccessException {
		Vector<CategoryTO> response= new Vector<CategoryTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String whereHide = "";
			if (hideClosed) {
				whereHide = " and pn.final_date is null";
			}
			
			pstmt = c.prepareStatement("select c.id, c.name, c.description, c.type, c.billable, c.is_defect, c.is_testing, " +
									   "c.project_id, p.name as PROJECT_NAME, c.disable_view, is_developing, category_order  " +
									   "from category c LEFT OUTER JOIN project p on c.project_id = p.id LEFT OUTER JOIN planning pn on p.id = pn.id " +
									   "where c.id <> '0'" + whereHide);
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    CategoryTO cto = this.populateBeanByResultSet(rs);
			    response.addElement(cto);
			}			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    private CategoryTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        CategoryTO response = new CategoryTO();
        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setGenericTag(response.getName());
        response.setDescription(getString(rs, "description"));
        response.setType(getInteger(rs, "type"));
        
        Integer billable = getInteger(rs, "billable");
        if (billable!=null && billable.intValue()==1) {
        	response.setIsBillable(new Boolean(true));	
        } else {
        	response.setIsBillable(new Boolean(false));
        }

        Integer hidden = getInteger(rs, "disable_view");
        if (hidden!=null && hidden.intValue()==1) {
        	response.setIsHidden(new Boolean(true));	
        } else {
        	response.setIsHidden(new Boolean(false));
        }

        Integer defect = getInteger(rs, "is_defect");
        if (defect!=null && defect.intValue()==1) {
        	response.setIsDefect(new Boolean(true));	
        } else {
        	response.setIsDefect(new Boolean(false));
        }

        Integer testing = getInteger(rs, "is_testing");
        if (testing!=null && testing.intValue()==1) {
        	response.setIsTesting(new Boolean(true));	
        } else {
        	response.setIsTesting(new Boolean(false));
        }
        
        String projectId = getString(rs, "project_id");
        if (projectId!=null) {
            ProjectTO pto = new ProjectTO(projectId);
            pto.setName(getString(rs, "PROJECT_NAME"));
            response.setProject(pto);
        }

        Integer dev = getInteger(rs, "is_developing");
        if (dev!=null && dev.intValue()==1) {
        	response.setIsDevelopingTask(new Boolean(true));	
        } else {
        	response.setIsDevelopingTask(new Boolean(false));
        }
        
        Integer order = getInteger(rs, "category_order");
        if (order!=null) {
        	response.setPositionOrder(order.intValue());	
        } else {
        	response.setPositionOrder(0);
        }
        
        return response;
    }

}
