package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class ExpenseDAO extends PlanningDAO {

	public Vector<ExpenseTO> getListByUserId(String userId) throws DataAccessException {
        Vector<ExpenseTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByUserId(userId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
	private Vector<ExpenseTO> getListByUserId(String userId, Connection c) throws DataAccessException {
		Vector<ExpenseTO> response= new Vector<ExpenseTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select e.id, e.project_id, e.user_id, u.name, p.name as PROJECT_NAME, " +
									      "a.description, a.creation_date, a.final_date, a.iteration, a.rich_text_desc " +
									   "from expense e, tool_user u, project p, planning a " +
									   "where e.user_id=? and e.user_id = u.id and e.project_id = p.id and a.id = e.id");
			pstmt.setString(1, userId);	
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    ExpenseTO eto = this.populateObjectByResultSet(rs, c);
			    response.addElement(eto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
    	ExpenseTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			ExpenseTO filter = (ExpenseTO)to;
			pstmt = c.prepareStatement("select e.id, e.project_id, e.user_id, u.name, p.name as PROJECT_NAME, " +
									      "a.description, a.creation_date, a.final_date, a.iteration, a.rich_text_desc " +
									   "from expense e, tool_user u, project p, planning a " +
									   "where e.id=? and e.user_id = u.id and e.project_id = p.id and a.id = e.id");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs, c);
			} 						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		CostDAO cdao = new CostDAO();
		try {		    
			ExpenseTO eto = (ExpenseTO)to;
			
		    super.update(eto, c);		    
		    pstmt = c.prepareStatement("update expense set project_id=?, user_id=? where id=?");
			pstmt.setString(1, eto.getProject().getId());
			pstmt.setString(2, eto.getUser().getId());
			pstmt.setString(3, eto.getId());
			pstmt.executeUpdate();
			
			cdao.removeByExpense(eto, c);
			
			//include a list of expenses items...
			if (eto.getExpensesItems()!=null) {
				for (CostTO cto : eto.getExpensesItems()) {
					cto.setProject(eto.getProject());
					cto.setExpense(eto);
					cdao.insert(cto, c);		
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		CostDAO cdao = new CostDAO();
		try {
			ExpenseTO eto = (ExpenseTO)to;
			
		    eto.setId(this.getNewId());
		    eto.setCreationDate(DateUtil.getNow());
		    super.insert(eto, c);
		    
		    pstmt = c.prepareStatement("insert into expense (id, project_id, user_id) values (?, ?, ?)");
			pstmt.setString(1, eto.getId());		    
			pstmt.setString(2, eto.getProject().getId());
			pstmt.setString(3, eto.getUser().getId());
			pstmt.executeUpdate();

			//include a list of expenses items...
			if (eto.getExpensesItems()!=null) {
				for (CostTO cto : eto.getExpensesItems()) {
					cto.setExpense(eto);
					cdao.insert(cto, c);		
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
	
    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
    	CostDAO cdao = new CostDAO();
		PreparedStatement pstmt = null;
		try {
			ExpenseTO eto = (ExpenseTO)to;
		    		
			cdao.removeByExpense(eto, c);
			
			pstmt = c.prepareStatement("delete from expense where id=?");
			pstmt.setString(1, eto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, eto.getId());
			pstmt.setString(2, eto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, eto.getId());
			pstmt.executeUpdate();	
			
			pstmt = c.prepareStatement("delete from planning where id = ?");
			pstmt.setString(1, eto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    
    public ExpenseTO populateObjectByResultSet(ResultSet rs, Connection c) throws DataAccessException{
    	CostDAO cdao = new CostDAO();
    	AdditionalFieldDAO afdao = new AdditionalFieldDAO();
    	
    	ExpenseTO response = new ExpenseTO();       
        response.setId(getString(rs, "id"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));

        ProjectTO pto = new ProjectTO(getString(rs, "project_id"));
        pto.setName(getString(rs, "PROJECT_NAME"));
        response.setProject(pto);
        
        UserTO uto = new UserTO(getString(rs, "user_id"));
        uto.setName(getString(rs, "name"));
        response.setUser(uto);
        
        response.setExpensesItems(cdao.getListByExpenseId(response.getId(), c));
        
	    //get the additional fields
	    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));

        return response;
    }

	
}
