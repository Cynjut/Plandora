package com.pandora.helper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class contain util methods to handler date. 
 */
public final class DateUtil {
	
    /** The basic unit of a day slot used to calc each gantt slot */
    public static final long SLOT_TIME_MILLIS = 86400000;

    /** week slot used to calc each gantt slot */
    public static final long WEEK_SLOT_TIME_MILLIS = 604800000;

    
	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private DateUtil(){}
	
	
	/**
	 * Get Current Date/Time
	 */
	public static Timestamp getNow(){
		return new Timestamp(System.currentTimeMillis()); 
	}

	/**
	 * Get Current Date without time information 
	 */	
	public static Timestamp getNowWithoutTime(){
		Timestamp ts = DateUtil.getNow();
		int d = DateUtil.get(ts, Calendar.DATE);
		int m = DateUtil.get(ts, Calendar.MONTH) + 1;
		int y = DateUtil.get(ts, Calendar.YEAR);
		return DateUtil.getDate(d+"", m+"", y+"");
	}
	
	
	/**
	 * Return a string date based on timestamp and a pattern
	 */
	public static String getDate(Timestamp dte, String pattern, Locale loc) {
	    SimpleDateFormat formatter = new SimpleDateFormat(pattern, loc);
	    Date date = new Date(dte.getTime());
	    return formatter.format(date);
	}

	
	public static String getDateTime(Timestamp dte, String pattern) {
	    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	    Date date = new Date(dte.getTime());
	    return formatter.format(date);
	}


	public static String getDateTime(Timestamp dte, String pattern, Locale loc) {
	    SimpleDateFormat formatter = new SimpleDateFormat(pattern, loc);
	    Date date = new Date(dte.getTime());
	    return formatter.format(date);
	}

	
	public static Timestamp getDateTime(String strDate, String pattern, Locale loc) {
	    Timestamp tm = null;
	    SimpleDateFormat sdf = new SimpleDateFormat(pattern, loc); 
		try {	    
		    Date date = sdf.parse(strDate);
		    tm = new Timestamp(date.getTime());
		} catch (ParseException e) {
			tm = null;
		} 
		return tm;
	}

		
	public static Timestamp getDate(Timestamp ts, boolean isInitial){
	    Timestamp response = null;
	    if (ts!=null) {
	    	Calendar c = Calendar.getInstance();
	    	c.setTimeInMillis(ts.getTime());
	        if (isInitial){
	            response = getDateTime(get(ts, Calendar.DATE)+"", get(ts, Calendar.MONTH)+"", get(ts, Calendar.YEAR)+"", "0", "0", "0");
	        } else {
	            response = getDateTime(get(ts, Calendar.DATE)+"", get(ts, Calendar.MONTH)+"", get(ts, Calendar.YEAR)+"", "23", "59", "59");
	        }	    	
	    }
        return response;
	}

	
	/**
	 * Return a timestamp corresponding the arguments
	 */
	public static Timestamp getDate(String day, String month, String year){
	    Calendar c = Calendar.getInstance();
	    c.set(Calendar.MONTH, Integer.parseInt(month)-1);
	    c.set(Calendar.YEAR, Integer.parseInt(year));
	    c.set(Calendar.DATE, Integer.parseInt(day));
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    return new Timestamp(c.getTimeInMillis());	    
	}

	
	/**
	 * Return a timestamp corresponding the arguments
	 */
	public static Timestamp getDateTime(String day, String month, String year, 
	        						String hour, String minute, String second){
	    Calendar c = Calendar.getInstance();
	    c.set(Calendar.DATE, Integer.parseInt(day));
	    c.set(Calendar.MONTH, Integer.parseInt(month));
	    c.set(Calendar.YEAR, Integer.parseInt(year));
	    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
	    c.set(Calendar.MINUTE, Integer.parseInt(minute));
	    c.set(Calendar.SECOND, Integer.parseInt(second));
	    c.set(Calendar.MILLISECOND, 0);
	    return new Timestamp(c.getTimeInMillis());	    
	}
	
	/**
	 * Return a string date and time based on timestamp 
	 */
	public static String getDateTime(Timestamp dte, Locale userLocale, int dateFormat, int timeFormat){
	    DateFormat dtf = DateFormat.getDateTimeInstance(dateFormat, timeFormat, userLocale);
	    return dtf.format(new Date(dte.getTime()));
	}
	
	
	/**
	 * Calculate the number of slots between two dates
	 */
	public static int getSlotBetweenDates(Timestamp iniDate, Timestamp finalDate){
	    return getSlotBetweenDates(iniDate, finalDate, SLOT_TIME_MILLIS, false);
	}

	
	/**
	 * Calculate the number of slots between two dates
	 */	
	public static int getSlotBetweenDates(Timestamp iniDate, Timestamp finalDate, long slotSizeInMillis, boolean considerCeil){
	    int response = 0;
	    if (finalDate.after(iniDate)){
	        long diff = (finalDate.getTime() - iniDate.getTime());
	        double val = (diff / slotSizeInMillis);
	        if (considerCeil) {
	        	val = Math.ceil(((double)diff / (double)slotSizeInMillis));	
	        }
	        response = (int)(val);
	    }
	    return response;
	}
	
	
	/**
	 * Increment/Decrement a timestamp using a type and number.<br> 
	 * The Type argument should be used the constants of Calendar object (ex: Calendar.MONTH, Calendar.WEEK_OF_MONTH, etc). 
	 */
	public static Timestamp getChangedDate(Timestamp iniDate, int incType, int number){
	    Calendar c = Calendar.getInstance();
	    c.setTimeInMillis(iniDate.getTime());
	    c.add(incType, number);
	    return new Timestamp(c.getTimeInMillis());
	}
	
	
	public static int get(Timestamp ts, int type) {
	    int response = -1;
	    Calendar c = Calendar.getInstance();
	    if (ts!=null && type>0 && type<=17){
	    	c.setTimeInMillis(ts.getTime());
	    	response = c.get(type);		    	
	    } else {
	        response = -1;
	    }
	    return response;
	}
}
