package com.pandora.bus.snip;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;
import com.pandora.gui.struts.form.SnipArtifactForm;

public class SnipArtifact {

	protected UserTO handler;
	

	public String getUniqueName(){
		return null;
	}

	public String getId(){
		return null;
	}

	public int getWidth(){
		return 250;
	}
	
	public int getHeight(){
		return 200;
	}    

	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		return "";
	}
	
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		return "";
	}

	public String refresh(HttpServletRequest request, SnipArtifactForm frm, String command) {
		return "";
	}

	protected String getI18nMsg(String keyMsg, Locale loc){
		String response = null;
		try {
			Locale lc = loc;
			if (lc==null) {
				lc = this.handler.getLocale(); 
			}
			
			if (this.handler!=null && this.handler.getBundle()!=null && lc!=null) {
				response = this.handler.getBundle().getMessage(lc, keyMsg);
		        if (response.startsWith("???")) {
		        	response = keyMsg;
		        }		    
			}			
		} catch (Exception e) {
			response = keyMsg;
		}
		return response;
	}

	
	public void setHandler(UserTO uto){
		this.handler = uto;
	}

	
	protected int getIntValue(String lineStr, int defaultvalue, int minValue, int maxValue) {
		int response = defaultvalue;
		try {
			if (lineStr!=null && !lineStr.trim().equals("")) {
				response = Integer.parseInt(lineStr);
			}		
			if (response < minValue || response > maxValue) {
				response = defaultvalue;
			}			
		}catch(Exception e) {
			e.printStackTrace();
			response = defaultvalue;
		}
		return response;
	}    

}

