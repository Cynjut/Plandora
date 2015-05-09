package com.pandora.delegate;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.exception.BusinessException;

public class GadgetDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private GadgetBUS bus = new GadgetBUS();

	public void renderContent(HttpServletRequest request, HttpServletResponse response, String gclass) throws BusinessException {
		bus.renderContent(request, response, gclass, null);
	}

	public void renderContent(HttpServletRequest request, HttpServletResponse response, String gclass, Vector<String> overrideParam) throws BusinessException {
		bus.renderContent(request, response, gclass, overrideParam);
	}
	
}
