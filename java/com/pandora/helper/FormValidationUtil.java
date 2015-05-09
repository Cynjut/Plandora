package com.pandora.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 */
public final class FormValidationUtil {

	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private FormValidationUtil(){}
	
	
	/**
	 * Check if a field of form is a valid date.
	 */
	public static boolean checkDate(ActionErrors errors, String fieldName, String value, Locale loc, String mask){
	    boolean response = true;
	    if (value!=null && value.trim().length()>0) {
	        if (DateUtil.getDateTime(value, mask, loc)==null){
                errors.add(fieldName, new ActionError("validate.invalidDate", fieldName) );
                response = false;
	        }	        
	    }
	    return response;
	}
	
	
	/**
	 * check if a field of form is a valid integer
	 */
	public static boolean checkInt(ActionErrors errors, String fieldName, String value){
	    boolean response = true;
	    if (value!=null && value.trim().length()>0) {
	        if (!StringUtil.checkChars(value, "0123456789")) {
                errors.add(fieldName, new ActionError("validate.invalidInt", fieldName) );
                response = false;
	        }	        
	    }
	    return response;
	}	
	
	/**
	 * Check if a field of form is a valid float.
	 */
	public static boolean checkFloat(ActionErrors errors, String fieldName, String value, Locale loc){	    
	    boolean response = true;
	    
	    if (value!=null && value.trim().length()>0) {
	        
		    if (StringUtil.checkChars(value, "0123456789.,")) {
		        
		        DecimalFormatSymbols decSymbols = new DecimalFormatSymbols(loc);
		        String sep = ""+ decSymbols.getDecimalSeparator();
		        String nonSeparator = ".";
		        if (sep.equals(".")) {
		            nonSeparator = ",";
		        }
		        
	            int findNonSeparator = value.indexOf(nonSeparator);
	            if (findNonSeparator>0) {
	                errors.add(fieldName, new ActionError("validate.invalidDecSeparator", fieldName) );
	                response = false;
		        }
		        
		    } else {
		        errors.add(fieldName, new ActionError("validate.invalidFloat", fieldName) );
		        response = false;
		    }	        
	    }
	    
	    return response;
	}

	
	public static boolean checkMaxValue(ActionErrors errors, String fieldName, String value, float maxValue, Locale loc){	    
	    boolean response = true;
	    
	    if (value!=null && value.trim().length()>0) {
	        
	        float num = 0; 
	        try {
	            num = StringUtil.getStringToFloat(value, loc);
	        }catch(Exception e){
	            num = -1;
	        }
	        
		    if (num>=0) {
	            if (num>maxValue) {
	                DecimalFormat myformat = new DecimalFormat("#,###.##");
	                errors.add(fieldName, new ActionError("validate.tooHigthFloat", fieldName, myformat.format(maxValue)) );
	                response = false;
		        }
		    } else {
		        errors.add(fieldName, new ActionError("validate.invalidFloat", fieldName) );
		        response = false;
		    }	        
	    }
	    
	    return response;
	}


    public static boolean checkMinValue(ActionErrors errors, String fieldName, String value, float minValue, Locale loc) {
	    boolean response = true;
	    
	    if (value!=null && value.trim().length()>0) {
	        
	        float num = 0; 
	        try {
	            num = StringUtil.getStringToFloat(value, loc);
	        }catch(Exception e){
	            num = -1;
	        }
	        
		    if (num>=0) {
	            if (num<minValue) {
	                DecimalFormat myformat = new DecimalFormat("#,###.##");
	                errors.add(fieldName, new ActionError("validate.tooLowFloat", fieldName, myformat.format(minValue)) );
	                response = false;
		        }
		    } else {
		        errors.add(fieldName, new ActionError("validate.invalidFloat", fieldName) );
		        response = false;
		    }	        
	    }
	    
	    return response;
    }

    
	public static boolean checkHHMM(ActionErrors errors, String fieldName, String value, Locale loc, boolean checkBounds){	    
	    boolean response = true;
	    
	    try {
		    if (value!=null && value.trim().length()>0) {

		    	String[] tokens = value.split(":");
			    if (StringUtil.checkChars(value, "0123456789:")	&& (tokens.length==2 || tokens.length==1)) {

			    	int h = Integer.parseInt(tokens[0]);
			    	int m = 0;
			    	if (tokens.length==2) {
			    		m = Integer.parseInt(tokens[1]);	
			    	}

			    	if (checkBounds) {
			            if (h<0 || h>24) {
			                errors.add(fieldName, new ActionError("validate.invalidHour.max") );
			                response = false;
				        }

			            if (m<0 || h>59) {
			                errors.add(fieldName, new ActionError("validate.invalidMinute.max") );
			                response = false;
				        }			    		
			    	}
			        

			    } else {
			    	if (!value.equals("0")) {
				        errors.add(fieldName, new ActionError("validate.invalidTime", fieldName) );
				        response = false;			    		
			    	}
			    }	        
		    }	    	
	    } catch (Exception e) {
	    	response = false;
	    }
	    
	    return response;
	}    
}
