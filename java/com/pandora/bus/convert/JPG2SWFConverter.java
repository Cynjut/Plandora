package com.pandora.bus.convert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class JPG2SWFConverter extends Converter{

	public String[] getMimeTypeList() {
		return new String[]{"image/jpeg" };
	}
	
	public String[] getExtensionList() {
		return new String[]{".jpe", ".jpg", ".jpeg" };
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
		FileOutputStream out = null;
		
		try {
			//get the temporary folder and generate a file name
			UserTO root = udel.getRoot();
			String folder = root.getPreference().getPreference(PreferenceTO.KB_INDEX_FOLDER);
	    	String key = file.getHandler().getId() + "_" + DateUtil.getDateTime(DateUtil.getNow(), "yyyyMMddhhmmss");
			
	    	File jpgFile = new File(folder + "/" + key + ".jpg");
	    	out = new FileOutputStream(jpgFile);
	    	out.write(file.getFileInBytes());
	    	
			//convert the jpg files to swf movie...
	        super.convertJpgToSwf(baos, folder, key);
	        
	        if (baos!=null) {
	        	response = baos.toByteArray();	
	        }
			
		} catch (Exception e){
			throw new BusinessException(e);
		} finally {
			try {
				if (out!=null) {
					out.close();					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}



}
