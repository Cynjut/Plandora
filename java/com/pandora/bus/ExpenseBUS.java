package com.pandora.bus;

import java.util.Vector;

import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.LeaderTO;
import com.pandora.dao.ExpenseDAO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.PaidExpenseCannotBeExcludedException;

public class ExpenseBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    ExpenseDAO dao = new ExpenseDAO();

    
	public Vector<ExpenseTO> getExpenseList(String userId) throws BusinessException {
        Vector<ExpenseTO> response = new Vector<ExpenseTO>();
        try {
            response = dao.getListByUserId(userId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}
	

	public void insertExpense(ExpenseTO eto) throws BusinessException {
		UserDelegate udel = new UserDelegate();
        try {
        	
        	LeaderTO led = new LeaderTO(eto.getUser());
        	led.setProject(eto.getProject());
        	led = udel.getLeader(led);
        	
        	if (eto.getExpensesItems()!=null) {
        		for (CostTO item : eto.getExpensesItems()) {
        			if (item.getInstallments()!=null) {
        				for (CostInstallmentTO cito : item.getInstallments()) {
        					if (cito.getCostStatus()!=null){
            					if (led==null && !cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
            						throw new BusinessException("The expense must be set to 'Wait Approve' status.");
            					}        						
        					} else {
        						throw new BusinessException("The expense must contain an appropriate status.");        						
        					}
        				}
        			} else {
        				throw new BusinessException("The expense must contain a list of items.");		
        			}
				}
        	} else {
        		throw new BusinessException("The expense must contain a list of items.");	
        	}
        	
            dao.insert(eto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}

	
	public ExpenseTO getExpense(ExpenseTO eto) throws BusinessException {
		ExpenseTO response;
        try {
            response = (ExpenseTO) dao.getObject(eto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public void updateExpense(ExpenseTO eto) throws BusinessException {
        try {        
            dao.update(eto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
	}
	

	public void removeExpense(ExpenseTO eto) throws BusinessException {
        try {
        	ExpenseTO dbExp = this.getExpense(eto);
        	if (dbExp.getExpensesItems()!=null) {
            	for (CostTO exp : dbExp.getExpensesItems()) {
            		if (exp.getInstallments()!=null) {
            			for (CostInstallmentTO cost : exp.getInstallments()) {
            				if (cost.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_PAID)) {
            					throw new PaidExpenseCannotBeExcludedException();
            				}
            			}
            		}
    			}
        	}
        	
            dao.remove(eto);
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
	}




}
