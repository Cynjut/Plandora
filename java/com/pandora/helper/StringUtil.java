package com.pandora.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.pandora.TransferObject;

/**
 * This class contain utilities methods to handler strings.
 */
public final class StringUtil {

	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private StringUtil(){}


    public static String getBase36(long val) {
        StringBuffer ret = new StringBuffer();
        while (val > 0) {
            if ((val % 36) < 10) ret.append((char)(((int)'0') + (int)(val % 36)));
            else ret.append((char)(((int)'A') + (int)((val % 36) - 10)));
            val /= 36;
        }
        return ret.toString();
    }
    

	/**
	 * Parse a list of keywords separated by comma and 
	 * return a Vector of words (string).
	 */	 
	public static Vector<String> getKeyworks(String list) {
		Vector<String> response = null;
		StringTokenizer st = null;
		
		if ((list!=null) && (!list.trim().equals(""))) {
			st = new StringTokenizer(list, ",");
			while  (st.hasMoreTokens()){
				String keyw = (st.nextToken()).trim();
				if (!keyw.trim().equals("")) {
					if (response==null) response = new Vector<String>();
					response.addElement(keyw);					
				}
			}	 		
		}

		return response;
	 }
	
	
	/**
	 * Verify if there are keywords and create a appropriate SQL using a
	 * specific table field for searching. 
	 */
	public static String getSQLKeywordsByField(Vector list, String field) {
		String response = "";
		boolean isFirst = true;
		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				if (!isFirst)
					response = response.concat(" OR ");
				String kw = formatWordToSql((String)i.next());
				kw = kw.toUpperCase();
				response = response.concat("(UPPER(" + field + ") LIKE '%" + kw + "%')");
				isFirst = false;
			}
		}
		return response;
	}
	
	
	/**
	 * Verify if there are keywords and create a appropriate SQL using a
	 * specific table fields for searching. 
	 */
	public static String getSQLKeywordsByFields(Vector<String> keywordlist, Vector<String> fieldsList) {
		String response = "";
		Iterator<String> iFieldsList = fieldsList.iterator();
		while (iFieldsList.hasNext()){
		    String field = (String)iFieldsList.next();
			if (!response.equals("")){
				response += " OR ";
			}
			response += getSQLKeywordsByField(keywordlist,field);
		}
		return response;
	}
	
	
	/**
	 * Format a string using the insertion constraints for SQL.
	 */
	public static String formatWordToSql(String wd){
		String response = wd;
		if (wd!=null){
			response = response.replaceAll("'", "''");	    
		}
		return response;
	}
	

	/**
	 * Format the string to be displayed into messageBox
	 */
	public static String formatWordToJScript(String comment){
	    String response = "";
	    //response = comment.replaceAll("\r\n", "\\n"); //bug fix: reqHistory didn't show the line break 
	    response = comment.replaceAll("'", "");
	    return response;
	}


	/**
	 * Format the string to be displayed into messageBox
	 */
	public static String formatWordToHtml(String comment){
	    return StringEscapeUtils.escapeHtml(comment);
	}	

	/**
	 * Format the string to be displayed into messageBox using the FreeMind format
	 */	
	public static String formatWordToFreeMind(String comment){
	    String response = comment;
	    response = StringEscapeUtils.escapeXml(response);
	    return response;
	}
	
    /**
     * Change the string (ex.: name, description, etc) in order to 
     * write in applet param format.
     */
	public static String formatWordForParam(String field){
        String response = " ";
        if (field!=null && !field.trim().equals("")){
        	response = field.replaceAll("\\r\\n", " ");
            response = field.replaceAll("\\n", " ");
            response = response.replaceAll("\"", "'");            
        }
        return response;
    }	

    /**
     * Change the string in order to write in GanttProject file format.
     */
	public static String formatWordForGP(String comment){
        String response = " ";
        if (comment!=null && !comment.trim().equals("")) {
            response = comment.replaceAll("\"", "'");
            response = response.replaceAll("\\r\\n", " ");
            response = response.replaceAll("\\n", " ");
            response = StringEscapeUtils.escapeHtml(response);
        }
        return response;
    }
	
	/**
     * Return a vector of elements based on string divided by separator char.  
     */
    public static Vector<String> getTokens(String source, String separator){
        Vector<String> response = new Vector<String>();
        try {
	        StringTokenizer st = new StringTokenizer(source, separator);
	        while(st.hasMoreTokens()){
	            String token = st.nextToken();
	            response.addElement(token);
	        }
        } catch(Exception e){
            response = new Vector<String>();
        }
        return response;
    }
    
    /**
     * Check if the string is a float numeric representation.
     */
    public static boolean checkIsFloat(String value, Locale loc){
        boolean response = false;
        try {
	        DecimalFormatSymbols decSymbols = new DecimalFormatSymbols(loc);
	        String sep = ""+ decSymbols.getDecimalSeparator();
        	
            Pattern patternForFloat = Pattern.compile( "([ ]*)((-?+)|(\\+{0,1}+))([ ]*)([0-9]+)([ 0-9 ]*)([ ]*)((\\" + 
            		sep + ")([ ]*[0-9]+))?+([ ]*)([ 0-9 ]*)([ ]*)" );
            Matcher m = patternForFloat.matcher(value);
            response = m.matches();
            
        } catch (Exception e) {
            response = false;
        }
        return response;
    }

	
    /**
     * Return a float from a numeric string.
     */
    public static float getStringToFloat(String value, Locale loc){
        float response = 0;
        try {        
	        NumberFormat nf = NumberFormat.getNumberInstance(loc);
	        Number n = nf.parse(value);
	        response = n.floatValue();
        } catch (ParseException e) {
            response = 0;
        }
        return response;
    }

    /**
     * Return a float from a numeric string.
     */
    public static double getStringToDouble(String value, Locale loc){
    	double response = 0;
        try {        
	        NumberFormat nf = NumberFormat.getNumberInstance(loc);
	        Number n = nf.parse(value);
	        response = n.doubleValue();
        } catch (ParseException e) {
            response = 0;
        }
        return response;
    }
    
    
    /**
     * Return a float from a HH:MM string.
     */
    public static int getHHMMToInteger(String value, boolean validate){
        int response = 0;
        try {
        	
        	String[] token = value.split(":");
        	if (token.length==2 || token.length==1) {
        		int h = Integer.parseInt(token[0]);
        		int m = 0;
        		if (token.length==2) {
        			m = Integer.parseInt(token[1]);        			
        		}
        		
        		if (validate){
            		if (h >= 0 && h <=24 && m>=0 && m<=59) {
            			response = (h * 60) + m;	
            		}        			
        		} else {
           			response = (h * 60) + m;	
        		}
        	}
	        
        } catch (Exception e) {
            response = 0;
        }
        return response;
    }    

    public static int getHHMMToInteger(String value){
    	return getHHMMToInteger(value, true);
    }
    
    public static String getIntegerToHHMM(Integer alloc, Locale loc){
    	String response = "";
        if (alloc!=null){
            int h = (int)(alloc.floatValue() / 60);
            int m = alloc.intValue() - (h * 60);
            response = StringUtil.getIntToString(h, loc) + ":" + StringUtil.getIntToString(m, loc);
        }    	
        return response;
    }
    
    
    /**
     * Return a string from a float number.
     */
    public static String getFloatToString(float value, Locale loc){
        String response = "";
        Float f = new Float(value);
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc);
        df.applyPattern("0.###");
        response = df.format(f.doubleValue());
        return response;
    }

    /**
     * Return a string from a float number.
     */
    public static String getFloatToString(float value, String pattern, Locale loc){
        String response = "";
        Float f = new Float(value);
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc);
        df.applyPattern(pattern);
        response = df.format(f.doubleValue());
        return response;
    }

    
    /**
     * Return a string from a currency number.
     */
    public static String getCurrencyValue(float value, Locale loc){
        String response = "";
        NumberFormat nf = NumberFormat.getCurrencyInstance(loc);
        response = nf.format(value);
        
        return response;
    }
    
    
    /**
     * Return a formated string from a int number.
     */
    public static String getIntToString(int value, Locale loc){
        String response = "";
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc);
        df.applyPattern("00");
        response = df.format((new Integer(value)).longValue());
        return response;
    }

    
   	/**
   	 * Return a string truncated at specified index. 
   	 */
   	public static String trunc(String content, int i, boolean addPoints){
   	    String response = "";
   	    if (content!=null && content.length()>i){
   	        response = content.substring(0, i);
   	        if (addPoints) {
   	         response = response + "...";
   	        }
   	    } else {
   	        response = content;
   	    }
   	    return response;
   	}
   	
   	
   	public static String trunc(String content, int i){
   	    return trunc(content, i, false);
   	}

   	
   	public static String cropWords(String content, int maxWords, boolean addPoints){
   	    String response = "";
   	    if (content!=null){
   	    	String[] words = content.split(" ");
   	    	if (maxWords<words.length) {
   	    		for (int i=0; i<maxWords; i++) {
   	    			response = response + words[i] + " "; 	
   	    		}
   	   	        if (addPoints) {
   	   	        	response = response + "<a style=\"cursor: help;\" title=\"" + content.substring(response.length()) + "\">...</a>";
   	   	        }   	    		
   	    	} else {
   	    		response = content;	
   	    	}
   	    } else {
   	        response = content;
   	    }
   	    return response;
   	}
  	
	/**
	 * Verify if current string contain only the caractes sent into argument 
	 * TODO melhorar este metodo...usar regex talvez seja uma boa ideia
	 */
	public static boolean checkChars(String wd, String scope){
	    boolean response = true;
	    if (scope!=null && wd!=null && scope.length()>0 && wd.length()>0){
	        
		    for (int i=0; i<wd.length();i++){
		        boolean find = false;
		        for (int j=0; j<scope.length();j++){
		            String cw = wd.substring(i, i+1);
		            String cs = scope.substring(j, j+1);
		            if (cw.equals(cs)){
		                find = true;
		                break;
		            }
		        }
		        if (!find){
		            response = false;
		            break;
		        }
		    }	        
	    }
	    return response;
	}
   
    
    /**
     * TODO procurar um lugar melhor para colocar este metodo
     * Return a difference between two groups of transferObjects, A-List minus B-List.
     */
    public static Vector minus(Vector aList, Vector bList){
        Vector response = new Vector();
        
        Iterator ia = aList.iterator();
        while(ia.hasNext()){
            TransferObject to1 = (TransferObject)ia.next();
            boolean userFound = false;
            
            Iterator ib = bList.iterator();
            while(ib.hasNext()){
                TransferObject to2 = (TransferObject)ib.next();                
                if (to1.getId().equals(to2.getId())){
                    userFound = true;
                    break;
                }                
            }
            
            if(!userFound){
                response.addElement(to1);
            }
        }
        
        return response;
    }

    /**
     * Checks if current string contain only numeric chars.
     */
    public static boolean hasOnlyDigits(String content) {
        boolean response = true;
        for (int i=0;i<content.length();i++) {
            if (!Character.isDigit(content.charAt(i))) {
                response = false;
                break;
            }
        }
        return response;
    }    
    
    
	/**
	 * Get stack trace of exception, and convert to String.
	 */
	public static String getStackTraceToString(Throwable e){
		String response = "";

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		if (e.getCause()!=null) {
		    e.getCause().printStackTrace(pw);    
		} else{
		    e.printStackTrace(pw);
		}
		
		response = sw.getBuffer().toString();
		
		return response;	
	} 

	
    /**
     * Creates a string with the size given, filled with a specific content and 
     * aligned on right or left.
     */
    public static String fill(String content, String fillChar, int size, boolean isRightAlign) {
    	String results = "";
    	String addContent = "";
   		
    	for (int k=0; k<size - content.length(); k++) {
   			addContent += String.valueOf(fillChar.charAt(0));
   		} 
   		
    	if (isRightAlign) {
    		results = addContent + content;    		
    	} else {
    		results = content + addContent;
    	}
    	
		results = results.substring(0, size);

		return results;
    }

    
    public static String getGoogleDateFormat(Timestamp ts, Locale loc, boolean showTime){
    	String response = DateUtil.getDate(ts, "yyyyMMdd", loc);
    	if (showTime) {
    		response = response + "T" + StringUtil.fill(DateUtil.get(ts, Calendar.HOUR_OF_DAY)+"", "0", 2, true) + 
			  							StringUtil.fill(DateUtil.get(ts, Calendar.MINUTE)+"", "0", 2, true) + 
			  							StringUtil.fill(DateUtil.get(ts, Calendar.SECOND)+"", "0", 2, true);
    	}
        return response; 
    }
    
    
    public static String getIso2Utf8(String value) throws UnsupportedEncodingException{
		byte[] bytes = value.getBytes("UTF-8");
		return new String(bytes, "ISO-8859-1");
    }


	public static Long getStringToCents(String strValue, Locale locale) {
		Long response = null;
		try {
			double d = StringUtil.getStringToDouble(strValue, locale) * 100;
			BigDecimal bd = new BigDecimal(d);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			response = new Long(bd.longValue()); 
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return response;
	}


	public static String getDoubleToString(double value, String pattern, Locale loc) {
        String response = "";
        Double d = new Double(value);
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc);
        df.applyPattern("0.###");
        response = df.format(d);
        return response;
	}

}
