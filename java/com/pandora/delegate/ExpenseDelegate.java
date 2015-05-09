package com.pandora.delegate;

import java.util.Vector;

import com.pandora.ExpenseTO;
import com.pandora.bus.ExpenseBUS;
import com.pandora.exception.BusinessException;


public class ExpenseDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private ExpenseBUS bus = new ExpenseBUS();

    
    public Vector<ExpenseTO> getExpenseList(String userId) throws BusinessException {
        return bus.getExpenseList(userId);
    }

    
    public ExpenseTO getExpense(ExpenseTO eto) throws BusinessException {
        return bus.getExpense(eto);
    }

    
    public void insertExpense(ExpenseTO eto) throws BusinessException  {
        bus.insertExpense(eto);        
    }


    public void updateExpense(ExpenseTO eto) throws BusinessException  {
        bus.updateExpense(eto);
    }


    public void removeExpense(ExpenseTO eto) throws BusinessException  {
        bus.removeExpense(eto);        
    }

}
