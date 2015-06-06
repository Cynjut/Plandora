package com.pandora.delegate;

import java.util.Vector;

import com.pandora.CompanyTO;
import com.pandora.bus.CompanyBUS;
import com.pandora.exception.BusinessException;

public class CompanyDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
	CompanyBUS bus = new CompanyBUS();
	
	
	public CompanyTO getCompany(CompanyTO cto) throws BusinessException {
		return bus.getCompany(cto);
	}

	
	public void insertCompany(CompanyTO cto) throws BusinessException {
		bus.insertCompany(cto);
	}
	

	public void updateCompany(CompanyTO cto) throws BusinessException {
		bus.updateCompany(cto);
	}

	public void removeCompany(CompanyTO cto) throws BusinessException {
		bus.removeCompany(cto);
	}

	public Vector<CompanyTO> getCompanyList() throws BusinessException {
        return bus.getCompanyList();
	}

}
