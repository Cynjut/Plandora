package com.pandora;

import java.util.Date;
import java.util.Locale;

import com.pandora.helper.StringUtil;

public class ResourceDateAllocTO extends TransferObject {
	private static final long serialVersionUID = 1L;
	
	/** The resource task related */
    private ResourceTO resource;
    
    /** The value (in minutes) of a slot */
    private Integer allocTime;
    
    private Date date;
    
    public ResourceDateAllocTO(String resourceId){
    	resource = new ResourceTO(resourceId);
    }

	public Integer getAllocTime() {
		return allocTime;
	}

	public void setAllocTime(Integer allocTime) {
		this.allocTime = allocTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ResourceTO getResource() {
		return resource;
	}

	public void setResource(ResourceTO resource) {
		this.resource = resource;
	}
	
	public String getAllocTimeInTimeFormat(Locale loc) {
        return StringUtil.getIntegerToHHMM(this.getAllocTime(), loc);
    }
	
	public String getAllocTimeInHours(Locale loc) {
        String response = "";
        Integer alloc = this.getAllocTime();
        
        if (alloc!=null){
            float f = alloc.floatValue() / 60;
            response = StringUtil.getFloatToString(f, loc);
        }
        
        return response;
    }
    
    
}
