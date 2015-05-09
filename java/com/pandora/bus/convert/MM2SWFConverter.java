package com.pandora.bus.convert;

import com.pandora.AttachmentTO;
import com.pandora.exception.BusinessException;


public class MM2SWFConverter extends Converter{

	public String[] getMimeTypeList() {
		return new String[]{"application/x-xmind"};
	}


	public String[] getExtensionList() {
		return new String[]{".mm"};
	}

	public int getOutputFormat() {
		return OUTPUT_MIND_MAP_VIEWER;
	}

	public String getContentType(){
		return "application/x-xmind";
	}
	
	public byte[] convert(AttachmentTO file) throws BusinessException {
		byte[] response = null;
		try {
			if (file!=null && file.getBinaryFile()!=null) {
				response = file.getFileInBytes();
			} else {
				String content = "<map version=\"0.8.1\">\n</map>";
				response = content.getBytes();
			}				
		} catch (Exception e){
			throw new BusinessException(e);
		}
		return response;
	}
}
