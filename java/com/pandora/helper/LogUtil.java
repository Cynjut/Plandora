package com.pandora.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

/**
 * This class contain util methods to handler file logs.
 */
public class LogUtil {

    /** priority of log */
    public static final int LOG_FATAL = 6;
    /** priority of log */
    public static final int LOG_ERROR = 5;
    /** priority of log */
    public static final int LOG_WARN  = 4;
    /** priority of log */
    public static final int LOG_INFO  = 3;
    /** priority of log */
    public static final int LOG_DEBUG = 2;
    /** priority of log */
    public static final int LOG_TRACE = 1;
    
    public static final String SUMMARY_LOGIN         = "USER_LOGIN";
    public static final String SUMMARY_CHANGE_PASS   = "USR_CG_PAS";
    public static final String SUMMARY_NOTIFIC       = "NOTIFIC";
    public static final String SUMMARY_KPI_GENERATE  = "KPI_GENERA";
    public static final String SUMMARY_KB_RESET      = "KB_RESET";
    public static final String SUMMARY_KB_GENERATE   = "KB_GENERAT";
    public static final String SUMMARY_USER_ACTION   = "USR_ACTION";
    public static final String SUMMARY_CHECK_FILE    = "CHECK_FILE";
    
    private static EventBUS ebus = new EventBUS();
    
    private static RootTO rootUser = null;
    
    
	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private LogUtil(){}
    

	/**
	 * Log content into log file.
	 */
	public static void log(String summary, Object c, String username, int type, String content, Throwable e){
	    Log log = LogFactory.getLog(c.getClass());
	    switch(type) {
	    	case LOG_FATAL:
	    	    log.fatal(content); break;
	    	case LOG_ERROR:
	    	    log.error(content); break;
	    	case LOG_WARN:
	    	    log.warn(content); break;	    	    
	    	case LOG_INFO:
	    	    log.info(content); break;	    	    
	    	case LOG_DEBUG:
	    	    log.debug(content); break;	    	    
	    	case LOG_TRACE:
	    	    log.trace(content); break;
	    }
	    
	    //save log record into data base
	    insertLogIntoDataBase(type, summary, content, username, e);	    
	}

	public static void log(String summary, Object c, String username, int type, String content){
	    log(summary, c, username, type, content, null);
	}
	
	public static void log(Object c, int type, String content){
	    log(null, c, null, type, content);
	}

	
	/**
	 * Log content into log file.
	 */	
	public static void log(Object c, int type, String content, String username, Throwable e){
	    Log log = LogFactory.getLog(c.getClass());
	    switch(type) {
	    	case LOG_FATAL:
	    	    log.fatal(content, e); break;
	    	case LOG_ERROR:
	    	    log.error(content, e); break;
	    	case LOG_WARN:
	    	    log.warn(content, e); break;	    	    
	    	case LOG_INFO:
	    	    log.info(content, e); break;	    	    
	    	case LOG_DEBUG:
	    	    log.debug(content, e); break;	    	    
	    	case LOG_TRACE:
	    	    log.trace(content, e); break;
	    }
	    
	    //save log record into data base
	    insertLogIntoDataBase(type, null, content, username, e);
	}

	
	public static void log(Object c, int type, String content, Throwable e){
	    log(c, type, content, null, e);
	}
	
	public static void resetRootRef(){
	    rootUser = null;
	}

	private static void insertLogIntoDataBase(int type, String summary, String content, String username, Throwable e){
	    if (type==LOG_ERROR || type==LOG_FATAL || type==LOG_INFO) {
	        
	        try {
	            if (rootUser==null) {
	                UserDelegate udel = new UserDelegate();
	                UserTO uto = udel.getRoot();
	                if (uto!=null) {
	                    rootUser = new RootTO(uto);
	                }
	            }
            } catch (Exception e2) {
                System.out.println("error getting the root properties from data base:" + e2);
            }
	        
            
            //TODO pegar propriedades do root para definir se precisa ou nao salvar log em banco.
	        
            try {
                ebus.insertEvent(type, StringUtil.trunc(summary, 10), 
                        StringUtil.trunc(content, 1024), username, e);
            } catch (BusinessException e1) {
                System.out.println("error saving log into data base:" + e1.getErrorMessage());
            }
	    }	    
	}
	
	
	public static String getTypeInString(int type){
	    String response = "";
	    switch(type) {
	    	case LOG_FATAL:
	    	    response = "FATAL ERROR"; break;
	    	case LOG_ERROR:
	    	    response = "ERROR"; break;
	    	case LOG_WARN:
	    	    response = "WARNING"; break;	    	    
	    	case LOG_INFO:
	    	    response = "INFO"; break;	    	    
	    	case LOG_DEBUG:
	    	    response = "DEBUG"; break;	    	    
	    	case LOG_TRACE:
	    	    response = "TRACE"; break;
	    	default:
	    	    response = "UNKNOWN"; break;
	    }
	    return response;
	}
	
}
