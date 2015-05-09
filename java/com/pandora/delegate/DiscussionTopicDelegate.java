package com.pandora.delegate;

import java.util.Vector;

import com.pandora.bus.DiscussionTopicBUS;
import com.pandora.exception.BusinessException;
import com.pandora.DiscussionTopicTO;
import com.pandora.UserTO;

public class DiscussionTopicDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private DiscussionTopicBUS bus = new DiscussionTopicBUS();


    /* (non-Javadoc)
     * @see com.pandora.bus.DiscussionTopicBUS.getListByPlanning(java.lang.String)
     */    
    public Vector getListByPlanning(String planningId) throws BusinessException{
        return bus.getListByPlanning(planningId);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.DiscussionTopicBUS.replyDiscussionTopic(java.lang.String, java.lang.String, java.lang.String, com.plandora.UserTO)
     */        
    public void replyDiscussionTopic(String planningId, String parentTopicId, String content, UserTO handler) throws BusinessException{
        bus.replyDiscussionTopic(planningId, parentTopicId, content, handler);
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.DiscussionTopicBUS.getListByUser(com.plandora.UserTO)
     */            
    public Vector<DiscussionTopicTO> getListByUser(UserTO uto) throws BusinessException{
    	return bus.getListByUser(uto);
    }
}
