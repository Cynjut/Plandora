package com.pandora.delegate;

import java.util.Vector;

import com.pandora.DiscussionTopicTO;
import com.pandora.UserTO;
import com.pandora.bus.DiscussionTopicBUS;
import com.pandora.exception.BusinessException;

public class DiscussionTopicDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private DiscussionTopicBUS bus = new DiscussionTopicBUS();


    /* (non-Javadoc)
     * @see com.pandora.bus.DiscussionTopicBUS.getListByPlanning(java.lang.String)
     */    
    public Vector<DiscussionTopicTO> getListByPlanning(String planningId) throws BusinessException{
        return bus.getListByPlanning(planningId);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.DiscussionTopicBUS.replyDiscussionTopic(java.lang.String, java.lang.String, java.lang.String, com.plandora.UserTO)
     */        
    public void replyDiscussionTopic(String planningId, String parentTopicId, String content, UserTO handler) throws BusinessException{
        bus.replyDiscussionTopic(planningId, parentTopicId, content, handler);
    }
    

    public DiscussionTopicTO getTopic(String id) throws BusinessException{
    	return bus.getTopic(id);
    }


	public void removeDiscussionTopic(String topicId, UserTO uto) throws BusinessException{
		bus.removeDiscussionTopic(topicId, uto);
	}
}
