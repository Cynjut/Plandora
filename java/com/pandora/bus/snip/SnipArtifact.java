package com.pandora.bus.snip;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;

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

	public String getHtmlBody(HttpServletRequest request) {
		return "";
	}
	
	public String submit(HttpServletRequest request) {
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

}

