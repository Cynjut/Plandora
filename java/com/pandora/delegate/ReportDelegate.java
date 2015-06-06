package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.BSCReportTO;
import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.ReportFieldTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.UserTO;
import com.pandora.bus.ReportBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Report entity information access.
 */
public class ReportDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    ReportBUS bus = new ReportBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.getListBySource(boolean, java.lang.String, com.pandora.ProjectTO)
     */    
    public Vector<ReportTO> getListBySource(boolean isKpi, String categoryId, ProjectTO pto, boolean includeClosed) throws BusinessException{
        return bus.getListBySource(isKpi, categoryId, pto, includeClosed);
    }    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.getReportListBySearch(java.util.Timestamp, java.util.Timestamp, java.lang.String, java.lang.String, java.lang.String)
     */    
    public Vector<ReportTO> getReportListBySearch(Timestamp initialDate, Timestamp finalDate, String  reportPerspectiveId, String projectId, String categoryId) throws BusinessException{
        return bus.getReportListBySearch(initialDate, finalDate, reportPerspectiveId, projectId, categoryId);
    }

    
    public void getKpiValues(Timestamp initialDate, Timestamp finalDate, ReportTO kpiObj, String projectId) throws BusinessException {
        bus.getKpiValues(initialDate, finalDate, kpiObj, projectId);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.getReport(com.pandora.ReportTO)
     */    
    public ReportTO getReport(ReportTO filter) throws BusinessException {
        return bus.getReport(filter);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.insertReport(com.pandora.ReportTO)
     */    
    public void insertReport(ReportTO rto) throws BusinessException {
        bus.insertReport(rto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.updateReport(com.pandora.ReportTO)
     */    
    public void updateReport(ReportTO rto) throws BusinessException {
        bus.updateReport(rto);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.removeReport(com.pandora.ReportTO)
     */    
    public void removeReport(ReportTO rto) throws BusinessException {
        bus.removeReport(rto);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.performKPI()
     */    
    public void performKPI() throws BusinessException {
        bus.performKPI();        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.performReport(com.pandora.ReportTO)
     */    
    public byte[] performReport(ReportTO rto) throws BusinessException {
        return bus.performReport(rto);        
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.performSQLByReportField(com.pandora.ReportFieldTO)
     */        
    public Vector<ReportResultTO> performSQLByReportField(ReportFieldTO field, UserTO uto) throws BusinessException {
        return bus.performSQLByReportField(field, uto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.getReportFields(java.lang.String)
     */    
    public Vector<ReportFieldTO> getReportFields(String content) throws BusinessException {
        return bus.getReportFields(content);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ReportBUS.getKpiByOccurrence(java.lang.String)
     */        
	public Vector<ReportTO> getKpiByOccurrence(String occurrenceId) throws BusinessException {
		return bus.getKpiByOccurrence(occurrenceId);
	}

	
	public Vector<BSCReportTO> getBSC(Timestamp refDate, ProjectTO pto, CategoryTO cto, boolean onCascade) throws BusinessException {
		return bus.getBSC(refDate, pto, cto, onCascade);
	}
	
	
	public int getKpiStatus(float value, float goal, float tolerance, String type){
		return bus.getKpiStatus(value, goal, tolerance, type);
	}

	
	public int getKpiStatus(Timestamp value, Timestamp goal, float tolerance, String type){
		return bus.getKpiStatus(value, goal, tolerance, type);
	}
	
}
