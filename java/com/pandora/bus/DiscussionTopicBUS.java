package com.pandora.bus;

import java.util.Vector;

import com.pandora.DiscussionTopicTO;
import com.pandora.UserTO;
import com.pandora.dao.DiscussionTopicDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class DiscussionTopicBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    private DiscussionTopicDAO dao = new DiscussionTopicDAO();
	
    
    public Vector<DiscussionTopicTO> getListByPlanning(String planningId) throws BusinessException{
    	Vector<DiscussionTopicTO> response = new Vector<DiscussionTopicTO>();
        try {
            response = dao.getListByPlanning(planningId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    public void replyDiscussionTopic(String planningId, String parentTopicId, String content, UserTO handler) throws BusinessException{
        try {
        	DiscussionTopicTO dtto = new DiscussionTopicTO();
        	dtto.setParentTopic(new DiscussionTopicTO(parentTopicId));
        	dtto.setUser(handler);
        	dtto.setContent(content);
        	dtto.setCreationDate(DateUtil.getNow());
        	dtto.setPlanningId(planningId);
            dao.insert(dtto);
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }           
    }


	public DiscussionTopicTO getTopic(String id) throws BusinessException {
		DiscussionTopicTO response = null;
        try {
        	DiscussionTopicTO dtto = new DiscussionTopicTO(id);
            response = (DiscussionTopicTO) dao.getObject(dtto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
		return response;
	}


	public void removeDiscussionTopic(String topicId, UserTO uto) throws BusinessException {
        try {
        	DiscussionTopicTO dtto = new DiscussionTopicTO();
        	dtto.setId(topicId);
        	dtto.setUser(uto);
            dao.remove(dtto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }           
	}        
}
