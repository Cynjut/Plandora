package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;
import com.pandora.UserTO;
import com.pandora.dao.SurveyDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.SurveyContainAnswerException;

public class SurveyBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    SurveyDAO dao = new SurveyDAO();

    
	public SurveyTO getSurvey(SurveyTO filter) throws BusinessException {
		SurveyTO response = null;
        try {
            response = (SurveyTO) dao.getObject(filter) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}


	public SurveyTO getSurveyByKey(String key) throws BusinessException {
		SurveyTO response = null;
        try {
            response = (SurveyTO) dao.getSurveyByKey(key) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector getSurveyListByUser(UserTO uto, boolean ignoreClosed) throws BusinessException {
		Vector response = null;
        try {
            response = dao.getSurveyListByUser(uto, ignoreClosed) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public boolean checkIfThereAreAnswers(SurveyQuestionTO qto) throws BusinessException {
		boolean response = false;
        try {
            response = dao.checkIfThereAreAnswers(qto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}	
	
	
	public Vector getSurveyList(String projectId) throws BusinessException {
		Vector response = new Vector();
		ProjectBUS pbus = new ProjectBUS();
        try {
        	ProjectTO pto = new ProjectTO(projectId);
        	
            Vector childs = pbus.getProjectListByParent(pto, true);
            Iterator i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = (ProjectTO)i.next();
                Vector rskOfChild = this.getSurveyList(childProj.getId());
                response.addAll(rskOfChild);
            }
                    
            response.addAll(dao.getSurveyListByProject(pto));
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}


	public void saveAnswer(Vector answerList) throws BusinessException {
        try {
        	dao.saveAnswer(answerList) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}


	public void insertSurvey(SurveyTO sto) throws BusinessException {
        try {
        	
        	//generate a key for anonymous access...
        	String key = PasswordEncrypt.getInstance().encrypt(sto.getCreationDate().getTime() 
        			+ "_" + sto.getOwner().getId());
        	key = key.replaceAll("\\+", "x");
        	sto.setAnonymousKey(key);
        	
        	dao.insert(sto) ;
        	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}


	public void updateSurvey(SurveyTO sto) throws BusinessException {
        try {
        	dao.update(sto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}

	
	public void removeSurvey(SurveyTO sto) throws BusinessException {
		boolean removeIt = true;
		
        try {
        	
        	if (sto.getQuestionList()!=null) {
        		Iterator i = sto.getQuestionList().iterator();
        		while(i.hasNext()) {
        			SurveyQuestionTO sq = (SurveyQuestionTO)i.next();
        			Vector answers = dao.getAnswerByQuestion(sq);
        			if (answers!=null) {
        				removeIt = false;
        				break;
        			}
        		}
        	}
        	
        	if (removeIt) {
        		dao.remove(sto) ;	 
        	} else {
				throw new SurveyContainAnswerException();
        	}
        	
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}

}
