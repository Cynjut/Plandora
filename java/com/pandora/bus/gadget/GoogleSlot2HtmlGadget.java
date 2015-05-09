package com.pandora.bus.gadget;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;
import com.pandora.exception.BusinessException;


public final class GoogleSlot2HtmlGadget extends HtmlScriptGadget {
	
	private static final String GOOGLE_GADGET_BODY = "GOOGLE_GADGET_BODY";
    

	protected String generate(Vector selectedFields) throws BusinessException {
		String script = super.getSelected(GOOGLE_GADGET_BODY, selectedFields);
		
		int w = super.getWidth()-20;
		int indxIni = script.indexOf("w=");
		if (indxIni>-1) {
			int indxFinal = script.indexOf("&", indxIni);
			if (indxFinal>-1){
				script = script.substring(0, indxIni) + "w=" + w + script.substring(indxFinal); 		
			}
		}
		
		
		return script;
	}


	public String getCategory() {
		return "Google";
	}

	
	public String getDescription() {
		return "label.manageOption.gadget.google.desc";
	}

	
	public Vector getFields() {
    	Vector response = new Vector();
    	try {
    		FieldValueTO text = new FieldValueTO(GOOGLE_GADGET_BODY, "label.manageOption.gadget.google.script", "", 55, 4);
    		text.setType(FieldValueTO.FIELD_TYPE_AREA);
        	response.add(text);        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
	}

	@Override	
	public Vector<TransferObject> getFieldsId() {
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(GOOGLE_GADGET_BODY, ""));
        return response;
	}

	
	public String getId() {
		return "GOOGLE_GADGET_SLOT2";
	}
	

	public String getImgLogo() {
		return "../images/gdglogo-14.png";
	}

	
	public int getPropertyPanelHeight() {
		return 170;
	}

	
	public boolean showMaximizedOption(){
		return false;
	}    
	

	
	public int getPropertyPanelWidth() {
		return 530;
	}

	
	public String getUniqueName() {
		return "label.manageOption.gadget.google.slot2";
	}

}
