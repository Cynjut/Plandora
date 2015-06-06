package com.pandora.bus.convert;

import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

public class ConverterBUS extends GeneralBusiness {

	public static Converter getClass(AttachmentTO attach) throws BusinessException{
		Converter response = null;
		
		if (attach!=null && (attach.getContentType()!=null || attach.getName()!=null)) {
			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String classes = root.getPreference().getPreference(PreferenceTO.CONVERTER_BUS_CLASS);
			if (classes!=null) {
				String[] conv = classes.split(";");
				if (conv!=null && conv.length>0) {
					for (int i=0; i<conv.length; i++) {
						Converter convClass = getClass(conv[i].trim());
						if (convClass!=null && response==null) {
							
							String[] ext = convClass.getExtensionList();
							if (ext!=null) {
								for (int j=0; j<ext.length; j++) {
									String fname = attach.getName().trim();
									String buff = fname.substring(fname.length() - ext[j].length());
									if  (buff.equalsIgnoreCase(ext[j])) {
										response = convClass;
										break;
									}
								}
							}
							
							if (response==null && attach.getContentType()!=null && convClass.getMimeTypeList()!=null){
								String[] types = convClass.getMimeTypeList();
								if (types!=null) {
									for (int j=0; j<types.length; j++) {
										if  (attach.getContentType().equalsIgnoreCase(types[j])) {
											response = convClass;
											break;
										}
									}
								}								
							}

						} else {
							break;
						}
					}					
				}
			}			
		}
		return response;
	}
	
	
	private static Converter getClass(String convClass){
		Converter auth = null;
		try {
			@SuppressWarnings("rawtypes")
			Class busClass = Class.forName(convClass);
            auth = (Converter)busClass.newInstance();					
		} catch (InstantiationException e) {
			e.printStackTrace();
			auth = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			auth = null;					
		} catch (ClassNotFoundException e) {
			auth = null;
		}
		return auth;
	}
	
}
