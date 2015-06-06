package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.DiscussionTO;
import com.pandora.ProjectTO;
import com.pandora.TeamInfoTO;
import com.pandora.UserTO;
import com.pandora.bus.DiscussionBUS;
import com.pandora.exception.BusinessException;


public class DiscussionDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private DiscussionBUS bus = new DiscussionBUS();

    
	public Vector<DiscussionTO> getDiscussionList(ProjectTO pto, CategoryTO category) throws BusinessException {
		return bus.getDiscussionList(pto, category);
	}

	public Vector<TeamInfoTO> getTeamInfo(UserTO uto, Timestamp iniDate) throws BusinessException {
		return bus.getTeamInfo(uto, iniDate);
	}
}
