package com.pandora.bus.convert;

import java.io.ByteArrayOutputStream;

import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class PPT2SWFConverter  extends Converter{

	public String[] getMimeTypeList() {
		return new String[]{"application/vnd.ms-powerpoint" };
	}
	
	public String[] getExtensionList() {
		return new String[]{".ppt", ".pot" };
	}

	public int getOutputFormat() {
		return OUTPUT_FLASH_VIEWER;
	}

	public String getContentType(){
		return "application/x-shockwave-flash";
	}
	
	public byte[] convert(AttachmentTO file) throws BusinessException {
		byte[] response = null;
		UserDelegate udel = new UserDelegate();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			//get the temporary folder and generate a file name
			UserTO root = udel.getRoot();
			String folder = root.getPreference().getPreference(PreferenceTO.KB_INDEX_FOLDER);
	    	String key = file.getHandler().getId() + "_" + DateUtil.getDateTime(DateUtil.getNow(), "yyyyMMddhhmmss");

	    	//convert the pdf file to jpg files (one picture by pdf page) 
			//this.convertPptToJpg(file, folder, key);
			
	    	//convert the ppt file to swf movie...
	        //this.convertPptToSwf(baos, folder, key);
	        
	        if (baos!=null) {
	        	response = baos.toByteArray();	
	        }
			
		} catch (Exception e){
			throw new BusinessException(e);
		}
		
		
		return response;
	}

}
