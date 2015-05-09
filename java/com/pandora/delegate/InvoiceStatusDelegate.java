package com.pandora.delegate;

import java.util.Vector;

import com.pandora.InvoiceStatusTO;
import com.pandora.bus.InvoiceStatusBUS;
import com.pandora.exception.BusinessException;

public class InvoiceStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */      
	InvoiceStatusBUS bus = new InvoiceStatusBUS();


    /* (non-Javadoc)
     * @see com.pandora.bus.InvoiceStatusBUS.getList()
     */    
    public Vector getList() throws BusinessException{
        return bus.getList();
    }


	public InvoiceStatusTO getInitialStatus() throws BusinessException{
        return bus.getInitialStatus();
	}

}

