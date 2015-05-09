package com.pandora.bus.occurrence;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.OccurrenceHistoryTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.bus.ProjectBUS;
import com.pandora.dao.OccurrenceDAO;
import com.pandora.dao.OccurrenceHistoryDAO;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

/**
 */
public class OccurrenceBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    OccurrenceDAO dao = new OccurrenceDAO();
    
    
	public Occurrence getOccurrenceClass(String className){
	    Occurrence response = null;
        try {
            Class klass = Class.forName(className);
            response = (Occurrence)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}

    public Vector<OccurrenceTO> getOccurrenceListByType(String projectId, String occurrenceClass) throws BusinessException {
    	Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
    	
    	Vector<OccurrenceTO> list = this.getOccurrenceList(projectId, false);
    	if (list!=null) {
        	Iterator<OccurrenceTO> i = list.iterator();
        	while(i.hasNext()) {
        		OccurrenceTO oto = (OccurrenceTO)i.next();
        		if (oto.getSource().equals(occurrenceClass)) {
        			response.addElement(oto);
        		}
        	}    		
    	}
    	return response;
    }
    
    
    public Vector<OccurrenceTO> getOccurrenceList(String projectId, boolean hideClosed) throws BusinessException {
        Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
        ProjectBUS pbus = new ProjectBUS();

        try {
        	
            //get occurrences of child projects 
            Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<OccurrenceTO> rskOfChild = this.getOccurrenceList(childProj.getId(), hideClosed);
                response.addAll(rskOfChild);
            }
                    
            response.addAll(dao.getListByProjectId(projectId, hideClosed));
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;

    }


    public OccurrenceTO getOccurrenceObject(OccurrenceTO oto) throws BusinessException {
        OccurrenceTO response;
        try {
            response = (OccurrenceTO) dao.getObject(oto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }


    public void insertOccurrence(OccurrenceTO oto) throws BusinessException {
        try {        
            
            oto.setDescription(oto.getName());
            oto.setCreationDate(DateUtil.getNow());
            oto.setFinalDate(null);
            
            dao.insert(oto);
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }


    public void updateOccurrence(OccurrenceTO oto) throws BusinessException {
        try {        
            dao.update(oto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


    public Vector<OccurrenceHistoryTO> getHistory(String occId) throws BusinessException{
        Vector<OccurrenceHistoryTO> response = new Vector<OccurrenceHistoryTO>();
        try {
            OccurrenceHistoryDAO dao = new OccurrenceHistoryDAO();
            response = dao.getListByOccurrence(occId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    public Vector getListUntilID(String initialId, String finalId) throws BusinessException{
        Vector response = new Vector();
        try {
            response = dao.getListUntilID(initialId, finalId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }


    public long getMaxID() throws BusinessException {
        long response = -1;
        try {
            response = dao.getMaxId("occurrence");
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
        return response;
    }



    public void removeOccurrence(OccurrenceTO oto) throws BusinessException {
        try {        
            dao.remove(oto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


	public void saveKpiLink(String kpiId, String weight, String id, Locale loc) throws BusinessException {
		ReportDelegate rdel = new ReportDelegate();
        try {
        	StrategicObjectivesOccurrence bus = new StrategicObjectivesOccurrence();
        	ReportTO kpi = rdel.getReport(new ReportTO(kpiId));
        	OccurrenceTO oto = this.getOccurrenceObject(new OccurrenceTO(id));
        	if (kpi!=null && oto!=null && oto.getSource().equals(bus.getClass().getName())) {
       			dao.saveKpiLink(kpiId, weight, id);	
        	}
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
	}


	public void removeKpiLink(String kpiId, String id) throws BusinessException {
        try {        
            dao.removeKpiLink(kpiId, id);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   		
	}

	public Vector<OccurrenceTO> getIterationListByProject(String projectId, boolean includechildren)  throws BusinessException {
        Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
        ProjectBUS pbus = new ProjectBUS();

        try {

            //get occurrences of child projects 
        	if (includechildren) {
                Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector occOfChild = this.getOccurrenceList(childProj.getId(), false);
                    response.addAll(occOfChild);
                }        		
        	}
                    
            response.addAll(dao.getIterationListByProject(projectId));
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	} 
        
}
