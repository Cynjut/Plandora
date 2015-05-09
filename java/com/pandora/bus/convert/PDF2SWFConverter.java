package com.pandora.bus.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;

import com.anotherbigidea.flash.movie.ImageUtil;
import com.anotherbigidea.flash.movie.Movie;
import com.anotherbigidea.flash.movie.Shape;
import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;


public class PDF2SWFConverter extends Converter{

	public String[] getMimeTypeList() {
		return new String[]{"application/pdf" };
	}
	
	public String[] getExtensionList() {
		return new String[]{".pdf" };
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
			this.convertPdfToJpg(file, folder, key);
			
			//convert the jpg files to swf movie...
	        super.convertJpgToSwf(baos, folder, key);
	        
	        if (baos!=null) {
	        	response = baos.toByteArray();	
	        }
			
		} catch (Exception e){
			throw new BusinessException(e);
		}
		
		
		return response;
	}


	private void convertPdfToJpg(AttachmentTO file, String folder, String key)
			throws IOException {
		PDFImageWriter writer = new PDFImageWriter();
		PDDocument document = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(file.getFileInBytes()); 
		    document = PDDocument.load(in);
		    writer.writeImage(document, "jpg", "", 1, Integer.MAX_VALUE, folder + key);
		} catch(Exception e){
			e.printStackTrace();
			document = null;
		} finally {
		    document.close();
		}
	}
}
