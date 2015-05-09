package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceTO;
import com.pandora.ProjectTO;
import com.pandora.dao.InvoiceDAO;
import com.pandora.dao.InvoiceHistoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class InvoiceBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    InvoiceDAO dao = new InvoiceDAO();
    
	
	public void insertInvoice(InvoiceTO to) throws BusinessException {
        try {
        	dao.insert(to) ;        	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}


	public void updateInvoice(InvoiceTO to) throws BusinessException {
        try {
        	dao.update(to) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}

	
	public InvoiceTO getInvoice(InvoiceTO to) throws BusinessException {
		InvoiceTO ito = null;
        try {
        	ito = (InvoiceTO) dao.getObject(to) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
		return ito;
	}

		
		
	public Vector<InvoiceTO> getInvoiceList(String pid, boolean includeSubProjects) throws BusinessException {
		Vector<InvoiceTO> response = new Vector<InvoiceTO>();
		ProjectBUS pbus = new ProjectBUS();
        try {
        	
            //get invoices of child projects 
        	if (includeSubProjects) {
                Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(pid), true);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector<InvoiceTO> invOfChild = this.getInvoiceList(childProj.getId(), true);
                    response.addAll(invOfChild);
                }        		
        	}
        	
        	response.addAll(dao.getInvoiceList(pid));
        	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}


	public void removeInvoice(InvoiceTO to) throws BusinessException {
        try {
        	dao.remove(to) ;        	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}


    public Vector<InvoiceHistoryTO> getHistory(String invId) throws BusinessException{
        Vector<InvoiceHistoryTO> response = new Vector<InvoiceHistoryTO>();
        try {
            InvoiceHistoryDAO dao = new InvoiceHistoryDAO();
            response = dao.getListByInvoice(invId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    } 
}
