package com.pandora.bus.snip;

import com.pandora.bus.GeneralBusiness;

public class SnipArtifactBUS extends GeneralBusiness {

	public static SnipArtifact getSnipArtifactClass(String className){
		SnipArtifact response = null;
        try {
            @SuppressWarnings("rawtypes")
			Class klass = Class.forName(className);
            response = (SnipArtifact)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}

}
