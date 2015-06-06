package com.pandora.bus;

import java.sql.Timestamp;

import com.pandora.bus.kb.IndexEngineBUS;
//import com.pandora.delegate.EmailImapDelegate;
import com.pandora.delegate.NotificationDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 * This class is a singleton and can be used to simulate a timer.
 */
public class GeneralTimer implements Runnable {

	/** Static instance of current singleton */	
	private static GeneralTimer instance = null;
	
	//private static long SLEEP_TIME = 840000; //default time interval (14min)
	private static long SLEEP_TIME = 60000; //debug time interval (1min)
		
	/** The timer thread */
	private static Thread timer;
	
	/** boolean used by run method to stop the timer process */
	private static boolean stopTimer;
		
	private static long lastIndexExecution;
	
	
	/**
	 * Constructor
	 */
	private GeneralTimer() {	
	}

	
	/**
	 * This method starts the singleton.
	 */
	public static GeneralTimer getInstance() throws BusinessException {
		if (instance == null) {			
			instance = new GeneralTimer();
			
			//Create the thread supplying it with the runnable object
			timer = new Thread(GeneralTimer.getInstance());
			timer.setName("PLANdora_timer");
			timer.setPriority(Thread.MIN_PRIORITY);
			stopTimer = false;
			timer.start();
		}
		return instance;
	}

	
	/** 
	 * This method stops the current timer 
	 * */
	public static void stopTimer(){
		if (timer!=null) {
			stopTimer = false;
			//timer.stop(); //TODO warning: [deprecation] stop() in java.lang.Thread has been deprecated
		}
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
    public void run() {        
		while(!stopTimer) {
		    LogUtil.log(this, LogUtil.LOG_DEBUG, "timer call on " + System.currentTimeMillis());
			try {				
			    onTimer();
			} catch (Exception e) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "error on timer", e);
			}
			try {
			    //sleep for the given interval
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException es) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "error sleeping thread", es);
			}
		}
	
    }
    
	/**
	 * Function executed when the timer expires
	 * @throws BusinessException
	 */
	protected void onTimer() throws BusinessException{
		
	    ReportDelegate rdel = new ReportDelegate();
	    rdel.performKPI();
	    
	    NotificationDelegate ndel = new NotificationDelegate();
	    ndel.performNotification();

	    //EmailImapDelegate edel = new EmailImapDelegate();
	    //edel.updateEmailImap();
	    
	    //perform the engine indexes from 10 to 10 minutes...
	    Timestamp ts = DateUtil.getNow();
	    if (lastIndexExecution==0 || ((ts.getTime() - lastIndexExecution) >= 600000) ) {
		    IndexEngineBUS ind;
	        try {
	            ind = new IndexEngineBUS();
	            ind.call();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	lastIndexExecution = DateUtil.getNow().getTime();
	        }
	    }

	}    
}
