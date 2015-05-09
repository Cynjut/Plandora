package com.pandora.delegate;

import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.bus.DiscussionBUS;
import com.pandora.exception.BusinessException;


public class DiscussionDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private DiscussionBUS bus = new DiscussionBUS();

    
	public Vector getDiscussionList(ProjectTO pto, CategoryTO category) throws BusinessException {
		return bus.getDiscussionList(pto, category);
	}

}
