package com.pandora.delegate;

import com.pandora.CustomerFunctionTO;
import com.pandora.bus.CustomerFunctionBUS;
import com.pandora.exception.BusinessException;

public class CustomerFunctionDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private CustomerFunctionBUS bus = new CustomerFunctionBUS();

    
    public CustomerFunctionTO getCustomerFunctionObject(CustomerFunctionTO filter) throws BusinessException {
    	return bus.getCustomerFunctionObject(filter) ;
    }


	public void removeCustomerFunction(CustomerFunctionTO cfto) throws BusinessException {
		bus.removeCustomerFunction(cfto) ;
	}
	
	
	public void insertCustomerFunction(CustomerFunctionTO cfto) throws BusinessException {
		bus.insertCustomerFunction(cfto) ;
	}
	
}
