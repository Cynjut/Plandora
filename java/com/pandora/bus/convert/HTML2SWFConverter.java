package com.pandora.bus.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class HTML2SWFConverter extends Converter {

	public String[] getMimeTypeList() {
		return new String[]{"image/jpeg" };
	}
	
	public String[] getExtensionList() {
		return new String[]{".html", ".htm" };
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
				    	
	    	byte[] pdfFile = this.convertHTML2PDF(new String(file.getFileInBytes()));
	    	this.convertPdfToJpg(pdfFile, folder, key);
	    	
			//convert the jpg files to swf movie...
	        super.convertJpgToSwf(baos, folder, key);
	        
	        if (baos!=null) {
	        	response = baos.toByteArray();	
	        }
				        
		} catch (Exception e){
			throw new BusinessException(e);
		} finally {
			try {
				if (baos!=null) {
					baos.close();					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	
	public byte[] convertHTML2PDF(String content) throws Exception {
		byte[] response = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());  
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {			
			Tidy tidy = new Tidy();             
			Document doc = tidy.parseDOM(bis, null);
			ITextRenderer renderer = new ITextRenderer();  
			renderer.setDocument(doc, null);  
			renderer.layout();
			renderer.createPDF(out);
			
			response = out.toByteArray();
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if (out!=null) {
				out.close();
			}
			if (bis!=null) {
				bis.close();
			}
		}
		return response;	
	}
	
	private void convertPdfToJpg(byte[] payload, String folder, String key) throws IOException {
		PDFImageWriter writer = new PDFImageWriter();
		PDDocument document = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(payload); 
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
