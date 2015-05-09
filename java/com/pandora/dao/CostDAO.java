package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class CostDAO extends PlanningDAO {

	
	public Vector<TransferObject> getAccountCodesByLeader(UserTO uto) throws DataAccessException {
        Vector<TransferObject> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getAccountCodesByLeader(uto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}
	

	public Vector<CostTO> getListByProject(ProjectTO pto, Timestamp inidate, Timestamp finalDate) throws DataAccessException {
        Vector<CostTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(pto, inidate, finalDate, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
	public Vector<CostTO> getListByCategory(CategoryTO catTO, Timestamp iniDate, Timestamp finalDate) throws DataAccessException {
        Vector<CostTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByCategory(catTO, iniDate, finalDate, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}


	public Vector<CostTO> getListByAccountCode(String accountCode, Timestamp iniDate, Timestamp finalDate) throws DataAccessException {
        Vector<CostTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByAccountCode(accountCode, iniDate, finalDate, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			CostTO cto = (CostTO)to;
		    super.update(cto, c);
		    pstmt = c.prepareStatement("update cost set name=?, project_id=?, " +
		    						   "category_id=?, account_code=?, expense_id=? where id=?");
			pstmt.setString(1, cto.getName());
			pstmt.setString(2, cto.getProject().getId());
			pstmt.setString(3, cto.getCategory().getId());
			pstmt.setString(4, cto.getAccountCode());
			if (cto.getExpense()!=null) {
				pstmt.setString(5, cto.getExpense().getId());	
			} else {
				pstmt.setNull(5, Types.VARCHAR);
			}
			pstmt.setString(6, cto.getId());
			pstmt.executeUpdate();
			
			//remove all installments
			this.removeInstallments(cto, c);

			//include a list of installments
			this.insertInstallments(cto, c);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    

	public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			CostTO cto = (CostTO)to;
		    cto.setId(this.getNewId());
		    cto.setCreationDate(DateUtil.getNow());

		    //insert data into parent entity (PlanningDAO)
		    super.insert(cto, c);
		    
		    pstmt = c.prepareStatement("insert into cost (id, name, project_id, category_id, " +
		    						   "account_code, expense_id) values (?,?,?,?,?,?)");
			pstmt.setString(1, cto.getId());
			pstmt.setString(2, cto.getName());			
			pstmt.setString(3, cto.getProject().getId());
			pstmt.setString(4, cto.getCategory().getId());
			pstmt.setString(5, cto.getAccountCode());
			if (cto.getExpense()!=null) {
				pstmt.setString(6, cto.getExpense().getId());	
			} else {
				pstmt.setNull(6, Types.VARCHAR);
			}
			pstmt.executeUpdate();
			
			//include a list of installments
			this.insertInstallments(cto, c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
	
	public void refuseCost(CostTO cto, UserTO refuser) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection(true);
			this.refuseOrapproveCost(cto, refuser, CostStatusTO.STATE_MACHINE_CANCELED, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}		
	}


	public void approveCost(CostTO cto, UserTO approver) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection(true);
			this.refuseOrapproveCost(cto, approver, CostStatusTO.STATE_MACHINE_BUDGETED, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}		
	}

	
	private void refuseOrapproveCost(CostTO cto, UserTO approverOrRefuser, Integer state, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		CostStatusDAO csdao = new CostStatusDAO();
		try {
			CostStatusTO csto = csdao.getCostStatusByState(state);
		    pstmt = c.prepareStatement("update cost_installment set due_date=?, cost_status_id=?, " +
		    						   "approver=? where cost_id=?");
		    pstmt.setTimestamp(1, cto.getFirstInstallmentDate());
		    pstmt.setString(2, csto.getId());
			pstmt.setString(3, approverOrRefuser.getId());
		    pstmt.setString(4, cto.getId());
			pstmt.executeUpdate();
			
			//insert a new history for each installment changed...
			cto = (CostTO)this.getObject(cto, c);
			ExpenseTO eto = cto.getExpense();
			if (eto!=null) {
				eto.setRefuserAprroverId(approverOrRefuser.getId());	
			}
			
			for (CostInstallmentTO cito : cto.getInstallments()) {
				cito.setDueDate(cto.getFirstInstallmentDate());
				cito.setCostStatus(csto);
				this.insertCostHistory(cto, cito, c);	
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
	}
	
	
	
	public Vector<CostTO> getPendindCosts(UserTO uto) throws DataAccessException {
        Vector<CostTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getPendindCosts(uto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}
	
	
    private Vector<CostTO> getPendindCosts(UserTO uto, Connection c) throws DataAccessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ExpenseDAO edao = new ExpenseDAO();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select e.id, c.project_id, e.user_id, u.name, u.username, p.name as PROJECT_NAME, " +
									      "a.description, a.creation_date, a.final_date, a.iteration, a.rich_text_desc," +
									      "c.id as COST_ID, c.name as COST_NAME, c.project_id, c.category_id, c.account_code, c.expense_id " +
									   "from expense e, tool_user u, project p, planning a, cost c, cost_installment ci, cost_status cs " +
									   "where e.user_id = u.id and c.project_id = p.id and a.id = e.id " +
									      "and c.expense_id = e.id and ci.cost_id = c.id and cs.id = ci.cost_status_id " +
									      "and cs.state_machine_order = ? " +
									      "and c.project_id in (select project_id from leader where id=?)");
			pstmt.setInt(1, (CostStatusTO.STATE_MACHINE_WAITING).intValue());
			pstmt.setString(2, uto.getId());	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ExpenseTO eto = edao.populateObjectByResultSet(rs, c);
				UserTO user = eto.getUser();
				user.setUsername(getString(rs, "username"));
				
				CostTO cto = this.populateObjectByResultSet(rs, c);
				cto.setExpense(eto);
				cto.setProject(eto.getProject());
				cto.setName(getString(rs, "COST_NAME"));
				cto.setId(getString(rs, "COST_ID"));
			    cto.setInstallments(this.getInstallmentList(cto, c));
				
			    response.addElement(cto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
	
    private void insertInstallments(CostTO cto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			for (CostInstallmentTO cito : cto.getInstallments()) {

			    pstmt = c.prepareStatement("insert into cost_installment (cost_id, installment_num, " +
			    							   "due_date, cost_status_id, value, approver) " +
			    							   "values (?,?,?,?,?,?)");
				pstmt.setString(1, cto.getId());
				pstmt.setInt(2, cito.getInstallmentNum().intValue());
				pstmt.setTimestamp(3, cito.getDueDate());
				pstmt.setString(4, cito.getCostStatus().getId());
				pstmt.setInt(5, cito.getValue().intValue());
				
				if (cito.getCostStatus()!=null && cito.getCostStatus().getStateMachineOrder()!=null) {
					if (!cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
						if (cito.getApprover()!=null) {
							pstmt.setString(6, cito.getApprover().getId());	
						} else if (cto.getExpense()!=null && cto.getExpense().getRefuserAprroverId()!=null){
							pstmt.setString(6, cto.getExpense().getRefuserAprroverId());
						} else {
							pstmt.setNull(6, Types.VARCHAR);	
						}
						
					} else if (cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
						pstmt.setString(6, cto.getExpense().getRefuserAprroverId());
					} else {
						pstmt.setNull(6, Types.VARCHAR);
					}
					
				} else {
					if (cto.getExpense()!=null && cto.getExpense().getRefuserAprroverId()!=null && 
							!cto.getExpense().getRefuserAprroverId().equals("")) {
						pstmt.setString(6, cto.getExpense().getRefuserAprroverId());	
					} else {
						pstmt.setNull(6, Types.VARCHAR);
					}
				}
				
				pstmt.executeUpdate();
				
				this.insertCostHistory(cto, cito, c);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
	}

   

	private void insertCostHistory(CostTO cto, CostInstallmentTO cito,	Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			
		    pstmt = c.prepareStatement("insert into cost_history (cost_id, installment_num, " +
		    							   "creation_date, name, project_id, category_id, account_code, " +
		    							   "expense_id, due_date, cost_status_id, value, user_id) " +
		    							   "values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, cto.getId());
			pstmt.setInt(2, cito.getInstallmentNum().intValue());
			pstmt.setTimestamp(3, DateUtil.getNow());
			pstmt.setString(4, cto.getName());
			pstmt.setString(5, cto.getProject().getId());
			pstmt.setString(6, cto.getCategory().getId());
			pstmt.setString(7, cto.getAccountCode());
			if (cto.getExpense()!=null) {
				pstmt.setString(8, cto.getExpense().getId());	
			} else {
				pstmt.setNull(8, Types.VARCHAR);
			}
			pstmt.setTimestamp(9, cito.getDueDate());
			pstmt.setString(10, cito.getCostStatus().getId());
			pstmt.setInt(11, cito.getValue().intValue());
			if (cto.getExpense()!=null && cto.getExpense().getRefuserAprroverId()!=null 
					&& !cto.getExpense().getRefuserAprroverId().trim().equals("")) {
				pstmt.setString(12, cto.getExpense().getRefuserAprroverId());
			} else {
				pstmt.setNull(12, Types.VARCHAR);
			}
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
	}


	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        CostTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			CostTO filter = (CostTO)to;
		    pstmt = c.prepareStatement("select c.id, c.name, c.project_id, c.category_id, c.account_code, " +
					   					"p.description, p.creation_date, p.final_date, p.iteration, c.expense_id " +
					   					"from cost c, planning p " +
	                   					"where c.id = p.id and c.id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = populateObjectByResultSet(rs, c);
				response.setInstallments(this.getInstallmentList(response, c));
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		try {	    
		    CostTO cto = (CostTO)to;

			pstmt = c.prepareStatement("delete from cost_installment where cost_id=?");
			pstmt.setString(1, cto.getId());
			pstmt.executeUpdate();
		    			
			pstmt = c.prepareStatement("delete from cost where id=?");
			pstmt.setString(1, cto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, cto.getId());
			pstmt.setString(2, cto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, cto.getId());
			pstmt.executeUpdate();	
			
			pstmt = c.prepareStatement("delete from planning where id = ?");
			pstmt.setString(1, cto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    
	public void removeByExpense(ExpenseTO eto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			
			pstmt = c.prepareStatement("delete from cost_installment " +
					                   "where cost_id in (select id from cost where expense_id=?)");
			pstmt.setString(1, eto.getId());
			pstmt.executeUpdate();
			
			pstmt = c.prepareStatement("delete from cost where expense_id=?");
			pstmt.setString(1, eto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from plan_relation " +
										"where plan_related_id in (select id from cost where expense_id=?) " +
										"or planning_id in (select id from cost where expense_id=?)");
			pstmt.setString(1, eto.getId());
			pstmt.setString(2, eto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field " +
					                   "where planning_id in (select id from cost where expense_id=?)");
			pstmt.setString(1, eto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from planning " +
					                   "where id in (select id from cost where expense_id=?)");
			pstmt.setString(1, eto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
	}
    
	private void removeInstallments(CostTO cto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("delete from cost_installment where cost_id=?");
		    pstmt.setString(1, cto.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
	}

	public Vector<CostTO> getListByExpenseId(String expenseId, Connection c) throws DataAccessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select c.id, c.name, c.project_id, c.category_id, c.account_code, " +
		    						   "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id, " +
		    						   "cg.name as CATEGORY_NAME " +
		    						   "from cost c, planning p, category cg " +
		    						   "where c.category_id = cg.id and c.id = p.id and c.expense_id = ? order by c.id");
			pstmt.setString(1, expenseId);	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CostTO cto = populateObjectByResultSet(rs, c);
				CategoryTO cat = cto.getCategory();
				cat.setName(getString(rs, "CATEGORY_NAME"));
				cto.setInstallments(this.getInstallmentList(cto, c));
				response.add(cto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}

	
	private Vector<CostTO> getListByAccountCode(String accountCode, Timestamp iniDate, Timestamp finalDate, Connection c) throws DataAccessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select sub.id, sub.name, sub.project_id, sub.category_id, sub.account_code, " +
								         "sub.description, sub.creation_date, sub.final_date, sub.iteration, sub.expense_id " + 
								       "from (select c.id, c.name, c.project_id, c.category_id, c.account_code, " + 
									                "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id," +
									                "min(i.due_date) as first, max(i.due_date) as last " +
									          "from cost c, planning p, cost_installment i " +
									          "where c.id = p.id and i.cost_id = c.id and c.account_code = ? " +
									   "group by c.id, c.name, c.project_id, c.category_id, c.account_code, " +
									         "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id" +
									   ") as sub " + (iniDate!=null?"where sub.first >= ? and sub.last <= ?":"") +
									   " order by sub.expense_id, sub.id");
			pstmt.setString(1, accountCode);
			if (iniDate!=null) {
				pstmt.setTimestamp(2, iniDate);
				pstmt.setTimestamp(3, finalDate);							
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CostTO cto = populateObjectByResultSet(rs, c);
				cto.setInstallments(this.getInstallmentList(cto, c));
				response.add(cto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
	
	private Vector<CostTO> getListByProject(ProjectTO pto, Timestamp iniDate, Timestamp finalDate, Connection c) throws DataAccessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select sub.id, sub.name, sub.project_id, sub.category_id, sub.account_code, " +
		    					         "sub.description, sub.creation_date, sub.final_date, sub.iteration, sub.expense_id " + 
		    					         "from (select c.id, c.name, c.project_id, c.category_id, c.account_code, " + 
		    						     "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id," +
		    						     "min(i.due_date) as first, max(i.due_date) as last " +
					                   "from cost c, planning p, cost_installment i " +
					                   "where c.id = p.id and i.cost_id = c.id and c.project_id = ? " +
					                   "group by c.id, c.name, c.project_id, c.category_id, c.account_code, " +
		    						         "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id" +
		    						   ") as sub " + (iniDate!=null?"where sub.first >= ? and sub.last <= ?":"") + 
		    						   " order by sub.expense_id, sub.id");
			pstmt.setString(1, pto.getId());
			if (iniDate!=null) {
				pstmt.setTimestamp(2, iniDate);
				pstmt.setTimestamp(3, finalDate);							
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CostTO cto = populateObjectByResultSet(rs, c);
				cto.setInstallments(this.getInstallmentList(cto, c));
				response.add(cto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}

	
	public Vector<CostTO> getListByCategory(CategoryTO catTO, Timestamp iniDate, Timestamp finalDate, Connection c) throws DataAccessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select sub.id, sub.name, sub.project_id, sub.category_id, sub.account_code, " +
								         "sub.description, sub.creation_date, sub.final_date, sub.iteration, sub.expense_id " + 
								         "from (select c.id, c.name, c.project_id, c.category_id, c.account_code, " + 
									     "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id," +
									     "min(i.due_date) as first, max(i.due_date) as last " +
					                  "from cost c, planning p, cost_installment i " +
					                  "where c.id = p.id and i.cost_id = c.id and c.category_id = ? " +
					                  "group by c.id, c.name, c.project_id, c.category_id, c.account_code, " +
									         "p.description, p.creation_date, p.final_date, p.iteration, c.expense_id" +
									  ") as sub " + (iniDate!=null?"where sub.first >= ? and sub.last <= ?":"") +
									  " order by sub.expense_id, sub.id");
			pstmt.setString(1, catTO.getId());
			if (iniDate!=null) {
				pstmt.setTimestamp(2, iniDate);
				pstmt.setTimestamp(3, finalDate);							
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CostTO cto = populateObjectByResultSet(rs, c);
				cto.setInstallments(this.getInstallmentList(cto, c));
				response.add(cto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	

	
	private Vector<TransferObject> getAccountCodesByLeader(UserTO uto, Connection c) throws DataAccessException {
		Vector<TransferObject> response = new Vector<TransferObject>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select distinct account_code " +
		    						   "from cost where account_code is not null and project_id in " +
		    						   "(select project_id from resource where id=?)");
			pstmt.setString(1, uto.getId());	
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    String accountCode = super.getString(rs, "account_code");
			    response.addElement(new TransferObject(accountCode, accountCode));
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
/*
    private CostInstallmentTO getInstallmentObject(CostInstallmentTO cito, Connection c) throws DataAccessException {
		CostInstallmentTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select i.cost_id, i.installment_num, i.due_date, i.cost_status_id, i.value, " +
		    							   "cs.name as STATUS_NAME, cs.state_machine_order, i.approver " +
		    						   "from cost_installment i, cost_status cs " +
		    						   "where i.cost_status_id = cs.id " +
		    						   "and i.cost_id = ? and i.installment_num=?");
			pstmt.setString(1, cito.getCost().getId());
			pstmt.setInt(2, cito.getInstallmentNum().intValue());	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				response = populateInstallmentByResultSet(rs, c);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
*/
    
	private Vector<CostInstallmentTO> getInstallmentList(CostTO cto, Connection c) throws DataAccessException {
		Vector<CostInstallmentTO> response = new Vector<CostInstallmentTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select i.cost_id, i.installment_num, i.due_date, i.cost_status_id, i.value, " +
		    							   "cs.name as STATUS_NAME, cs.state_machine_order, i.approver " +
		    						   "from cost_installment i, cost_status cs " +
		    						   "where i.cost_status_id = cs.id " +
		    						   "and i.cost_id = ? order by i.due_date");
			pstmt.setString(1, cto.getId());	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CostInstallmentTO inst = populateInstallmentByResultSet(rs, c);
				inst.setCost(cto);
				response.add(inst);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}

	
    private CostInstallmentTO populateInstallmentByResultSet(ResultSet rs, Connection c) throws DataAccessException{
    	CostInstallmentTO response = new CostInstallmentTO();

        response.setDueDate(getTimestamp(rs, "due_date"));
        response.setInstallmentNum(getInteger(rs, "installment_num"));
        response.setValue(getInteger(rs, "value"));
        
        String approver = getString(rs, "approver");
        if (approver!=null && !approver.trim().equals("")) {
        	response.setApprover(new UserTO(approver));
        }

        CostStatusTO csto = new CostStatusTO(getString(rs, "cost_status_id"));
        csto.setName(getString(rs, "STATUS_NAME"));
        csto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
        response.setCostStatus(csto);

        return response;
    }
	
    private CostTO populateObjectByResultSet(ResultSet rs, Connection c) throws DataAccessException{
    	AdditionalFieldDAO afdao = new AdditionalFieldDAO();

    	CostTO response = new CostTO();       
        response.setId(getString(rs, "id"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setIteration(getString(rs, "iteration"));
        
        response.setCategory(new CategoryTO(getString(rs, "category_id")) );
        response.setName(getString(rs, "name"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setAccountCode(getString(rs, "account_code"));
        
        String expid = getString(rs, "expense_id");
        if (expid!=null && !expid.trim().equals("")) {
        	response.setExpense(new ExpenseTO(expid));	
        } else {
        	response.setExpense(null);
        }
                
	    //get the additional fields
	    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));

        return response;
    }

}
