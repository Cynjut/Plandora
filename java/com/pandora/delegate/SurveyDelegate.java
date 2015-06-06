package com.pandora.delegate;

import java.util.Vector;

import com.pandora.QuestionAnswerTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;
import com.pandora.UserTO;
import com.pandora.bus.SurveyBUS;
import com.pandora.exception.BusinessException;

public class SurveyDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private SurveyBUS bus = new SurveyBUS();
	
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.getSurvey(com.pandora.SurveyTO)
     */
    public SurveyTO getSurvey(SurveyTO filter) throws BusinessException {
        return bus.getSurvey(filter);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.getSurveyByKey(java.lang.String)
     */
	public SurveyTO getSurveyByKey(String key) throws BusinessException {
        return bus.getSurveyByKey(key);
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.getSurveyList(java.lang.String)
     */	
	public Vector<SurveyTO> getSurveyList(String projectId, boolean ignoreClosed) throws BusinessException {
        return bus.getSurveyList(projectId, ignoreClosed);
	}


    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.saveAnswer(java.util.Vector)
     */	
	public void saveAnswer(Vector<QuestionAnswerTO> answerList) throws BusinessException {
		bus.saveAnswer(answerList);
	}


    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.insertSurvey(com.pandora.SurveyTO)
     */	
	public void insertSurvey(SurveyTO sto) throws BusinessException {
		bus.insertSurvey(sto);		
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.insertSurvey(com.pandora.SurveyTO)
     */	
	public void updateSurvey(SurveyTO sto) throws BusinessException {
		bus.updateSurvey(sto);
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.removeSurvey(com.pandora.SurveyTO)
     */	
	public void removeSurvey(SurveyTO sto) throws BusinessException {
		bus.removeSurvey(sto);
	}

	
	public boolean checkIfThereAreAnswers(SurveyQuestionTO qto) throws BusinessException {
		return bus.checkIfThereAreAnswers(qto);
	}
	
	
    /* (non-Javadoc)
     * @see com.pandora.bus.SurveyBUS.getSurveyListByUser(com.pandora.UserTO, boolean)
     */	
	public Vector<SurveyTO> getSurveyListByUser(UserTO uto, boolean ignoreClosed) throws BusinessException {
		return bus.getSurveyListByUser(uto, ignoreClosed);
	}

	
}
