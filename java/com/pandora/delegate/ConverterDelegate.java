package com.pandora.delegate;

import com.pandora.AttachmentTO;
import com.pandora.bus.convert.Converter;
import com.pandora.bus.convert.ConverterBUS;
import com.pandora.exception.BusinessException;

public class ConverterDelegate extends GeneralDelegate {

    
    public Converter getClass(AttachmentTO attach) throws BusinessException{
    	return ConverterBUS.getClass(attach);
    }
}
