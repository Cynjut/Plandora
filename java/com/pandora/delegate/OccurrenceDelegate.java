package com.pandora.delegate;

import java.util.Locale;
import java.util.Vector;

import com.pandora.OccurrenceHistoryTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.bus.occurrence.OccurrenceBUS;
import com.pandora.exception.BusinessException;

/**
 */
public class OccurrenceDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private OccurrenceBUS bus = new OccurrenceBUS();
    
    
	public Occurrence getKbClass(String className) throws Exception{
	    return bus.getOccurrenceClass(className);
	}


    public Vector<OccurrenceTO> getOccurenceList(String projectId, boolean hideClosed) throws BusinessException  {
        return bus.getOccurrenceList(projectId, null, hideClosed, true);
    }
    
    public Vector<OccurrenceTO> getOccurenceList(String projectId, String userId, boolean hideClosed) throws BusinessException  {
        return bus.getOccurrenceList(projectId, userId, hideClosed, true);
    }

    public Vector<OccurrenceTO> getOccurenceListByType(String projectId, String occClass) throws BusinessException  {
        return bus.getOccurrenceListByType(projectId, occClass, true);
    }

    public Vector<OccurrenceTO> getOccurenceListByType(String projectId, String occClass, boolean includeSubProjects) throws BusinessException  {
        return bus.getOccurrenceListByType(projectId, occClass, includeSubProjects);
    }

    
    public OccurrenceTO getOccurrenceObject(OccurrenceTO oto) throws BusinessException  {
        return bus.getOccurrenceObject(oto);        
    }


    public void insertOccurrence(OccurrenceTO oto) throws BusinessException  {
        bus.insertOccurrence(oto);        
    }


    public void updateOccurrence(OccurrenceTO oto) throws BusinessException  {
        bus.updateOccurrence(oto);
    }
    
    
    public Vector<OccurrenceHistoryTO> getHistory(String occId) throws BusinessException{
        return bus.getHistory(occId);        
    }


    public void removeOccurrence(OccurrenceTO oto) throws BusinessException{
        bus.removeOccurrence(oto);
    }


	public void saveKpiLink(String selectedKpi, String weight, String id, Locale loc) throws BusinessException{
		bus.saveKpiLink(selectedKpi, weight, id, loc);
	}


	public void removeKpiLink(String selectedKpi, String id) throws BusinessException{
		bus.removeKpiLink(selectedKpi, id);
	}


	public Vector<OccurrenceTO> getIterationListByProject(String projectId, boolean includeChildren) throws BusinessException{
		return bus.getIterationListByProject(projectId, includeChildren);
	}


	public OccurrenceTO getOccurrenceByName(String occName, ProjectTO pto) throws BusinessException {
		return bus.getOccurrenceByName(occName, pto);
	}    
}
