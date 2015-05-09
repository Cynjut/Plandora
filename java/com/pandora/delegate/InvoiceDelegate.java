package com.pandora.delegate;

import java.util.Vector;

import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceTO;
import com.pandora.bus.InvoiceBUS;
import com.pandora.exception.BusinessException;

public class InvoiceDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
	private InvoiceBUS bus = new InvoiceBUS();


	
	public void insertInvoice(InvoiceTO to) throws BusinessException {
		bus.insertInvoice(to);
	}


	public void updateInvoice(InvoiceTO to) throws BusinessException {
		bus.updateInvoice(to);
	}


	public InvoiceTO getInvoice(InvoiceTO to) throws BusinessException {
		return bus.getInvoice(to);
	}


	public Vector<InvoiceTO> getInvoiceList(String projectId, boolean includeSubProjects)  throws BusinessException {
		return bus.getInvoiceList(projectId, includeSubProjects);
	}


	public void removeInvoice(InvoiceTO ito) throws BusinessException {
		bus.removeInvoice(ito);
	}


	public Vector<InvoiceHistoryTO> getHistory(String invId) throws BusinessException {
		return bus.getHistory(invId);
	}

}
