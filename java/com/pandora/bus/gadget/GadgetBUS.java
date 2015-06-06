package com.pandora.bus.gadget;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.UserTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.exception.BusinessException;

public class GadgetBUS extends GeneralBusiness {

	public void renderContent(HttpServletRequest request, HttpServletResponse response, 
			String gclass, Vector<String> overrideParam) throws BusinessException{
		Gadget gag = getGadgetClass(gclass);
		if (gag!=null) {
			gag.perform(request, response, overrideParam);	
		}
	}

	public static Gadget getGadgetClassByTokenizerString(String classList, String gedgetId, UserTO handler) {
		Gadget response = null;
		if (classList!=null && !classList.trim().equals("")) {
			String[] cList = classList.split(";");
			if (cList!=null && cList.length>0) {
				for (int i=0; i<cList.length;i++) {
					String gadgetClass = cList[i].trim();
					Gadget gad = getGadgetClass(gadgetClass);
					gad.handler = handler;
					if (gad.getId().equals(gedgetId)) {
						response = gad;
						break;
					}
				}
			}
		}
		return response;
	}
	
	
	public static Vector<Gadget> getGadgetClassesByTokenizerString(String classList) {
		Vector<Gadget> response = new Vector<Gadget>();
		if (classList!=null && !classList.trim().equals("")) {
			String[] cList = classList.split(";");
			if (cList!=null && cList.length>0) {
				for (int i=0; i<cList.length;i++) {
					String gadgetClass = cList[i].trim();
					Gadget gad = getGadgetClass(gadgetClass);
					response.addElement(gad);	
				}
			}
		}
		return response;
	}

	
	public static Gadget getGadgetClass(String className){
		Gadget response = null;
        try {
            @SuppressWarnings("rawtypes")
			Class klass = Class.forName(className);
            response = (Gadget)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}
	
}
