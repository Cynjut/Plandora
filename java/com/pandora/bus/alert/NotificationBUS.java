package com.pandora.bus.alert;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.DBQueryResult;
import com.pandora.NotificationTO;
import com.pandora.RootTO;
import com.pandora.bus.DbQueryBUS;
import com.pandora.bus.GeneralBusiness;
import com.pandora.dao.NotificationDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 */
public class NotificationBUS  extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    NotificationDAO dao = new NotificationDAO();

    
    @SuppressWarnings("unchecked")
	public void performNotification() throws BusinessException{ 
        Vector<NotificationTO> response = new Vector<NotificationTO>();
        long elapsedTime = 0;
        try {
            response = dao.getList(false);
            Timestamp current = DateUtil.getNow();
            
            Iterator<NotificationTO> i = response.iterator();
            while(i.hasNext()) {
                NotificationTO nto = (NotificationTO) i.next();
                
                //define how much times the agents should retry if some error occurs..
                int totalRetry = 1;
                if (nto.getRetryNumber()!=null && nto.getRetryNumber().intValue()>0) {
                    totalRetry = nto.getRetryNumber().intValue();
                }
                    
                //check if the agent is enable and it's time to send
                if (nto.getFinalDate()==null && (nto.getNextNotification()==null || current.after(nto.getNextNotification()))) {                    
                    
                    int retry = 1;
                    elapsedTime = DateUtil.getNow().getTime();
                    boolean dispatchOk = false;
                    boolean shouldDispatch = false;
                    
                    while (retry<=totalRetry && !dispatchOk) {
                        
                        try {
                            //get the specific business object
                            String busClassName = nto.getNotificationClass();
                            @SuppressWarnings("rawtypes")
							Class busClass = Class.forName(busClassName);
                            Notification nbus = (Notification)busClass.newInstance();
                            
                            String sql = nto.getSqlStement();
                            sql = sql.replaceAll("#CURRENT_NOTIFICATION_ID#", nto.getId());
                            
                            //run the SQL statement related to the notification
                            DbQueryBUS dbBus = new DbQueryBUS();
                            Vector<Vector<Object>> notifResponse = null;
                            if (nbus.isQuery()) {
                            	DBQueryResult r = dbBus.performQuery(sql, null, null);
                            	nbus.columnNames = r.getColumns();
                            	
                            	notifResponse = new Vector<Vector<Object>>();
                            	notifResponse.add(r.getColumns());
                            	notifResponse.addAll(r.getData());
                            	
                            } else {
                            	notifResponse = dbBus.executeQuery(sql);
                            }
                            
                            //send the alarm if found something to notify!!                            
                            if (notifResponse!=null && !notifResponse.isEmpty()) {
                                nbus.sendNotification(nto.getFields(), notifResponse);
                                shouldDispatch = true;
                            }
                            
                            dispatchOk = true;
                            
                        } catch (Exception e) {
                            //the message dispatching failed... :-(
                            LogUtil.log(LogUtil.SUMMARY_NOTIFIC, this, RootTO.ROOT_USER, 
                                    LogUtil.LOG_INFO, "The message dispatching failed. Retry number:[" + retry + "]", e);
                            retry++;                            
                        }
                    }
                    
                    //update notification into data base
                    elapsedTime = DateUtil.getNow().getTime() - elapsedTime;                     
                    this.saveNotificationInfo(nto, current, retry, dispatchOk, elapsedTime, shouldDispatch);
                }   
            }
            
        } catch (Exception e) {
            throw new  BusinessException(e);
        }
    }

    
    
    public Vector<NotificationTO> getNotificationList(boolean hideClosedAgents) throws BusinessException {
        Vector<NotificationTO> response = new Vector<NotificationTO>();
        try {
            response = dao.getList(hideClosedAgents);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

   
    
    /**
     * Calculate the next notification time based to the information
     * about periodicity selected by user
     * 
     * @param nto
     * @return
     */
    public Timestamp getNextNotification(NotificationTO nto, Timestamp baseline) {
        Timestamp response = null;

        if (nto.getPeriodicity()==null || nto.getPeriodicity().equals(NotificationTO.PERIODICITY_EVENTUALLY)) {
            //periodicity = eventually
            if (nto.getPeriodicityMinute()!=null && nto.getPeriodicityMinute().intValue()>=10) {
                response = DateUtil.getChangedDate(baseline, Calendar.MINUTE , nto.getPeriodicityMinute().intValue());    
            }
            
        } else if (nto.getPeriodicity().equals(NotificationTO.PERIODICITY_YEARLY)) {
            //periodicity = yearly
            response = this.addPeriodicity(Calendar.YEAR, 1, nto.getPeriodicityMinute(), nto.getPeriodicityHour(), baseline);
            
        } else if (nto.getPeriodicity().equals(NotificationTO.PERIODICITY_WEEKLY)) {
            //periodicity = weekly
            response = this.addPeriodicity(Calendar.DATE, 7, nto.getPeriodicityMinute(), nto.getPeriodicityHour(), baseline);
        
        } else if (nto.getPeriodicity().equals(NotificationTO.PERIODICITY_MONTHLY)) {
            //periodicity = monthly
            response = this.addPeriodicity(Calendar.MONTH, 1, nto.getPeriodicityMinute(), nto.getPeriodicityHour(), baseline);
        
        } else if (nto.getPeriodicity().equals(NotificationTO.PERIODICITY_DAILY)) {
            //periodicity = daily
            response = this.addPeriodicity(Calendar.DATE, 1, nto.getPeriodicityMinute(), nto.getPeriodicityHour(), baseline);
            
        } else {
            response = null;
        }
        
        return response;
    }

    
    
    
    private void saveNotificationInfo(NotificationTO nto, Timestamp current, int numberOfRetries, 
            boolean notifSentOk, long elapsedTime, boolean hasDispatched) throws BusinessException{
        try {
            nto.setLastCheck(current);
            Timestamp nextNotif = this.getNextNotification(nto, current);
            if (nextNotif!=null) {
                nto.setNextNotification(nextNotif);    
            } else {
                LogUtil.log(LogUtil.SUMMARY_NOTIFIC, this, RootTO.ROOT_USER, 
                        LogUtil.LOG_INFO, "The notification [" + nto.getName() + "]contain a invalid periodicity configuration. The system turn the notification off.");
                nto.setFinalDate(current);
            }
            dao.update(nto);

            //define the message of audit log
            String logMessage = "wasn't sent. There is NOTHING to send!";
            if (hasDispatched) {
                if (notifSentOk) {
                    logMessage = "was performed successfully";
                } else {
                    logMessage = "failed";
                }
            }
            
            LogUtil.log(LogUtil.SUMMARY_NOTIFIC, this, RootTO.ROOT_USER, LogUtil.LOG_INFO, 
                    "The notification [" + nto.getName() + "] " + logMessage + 
                    ". Retry:[" + numberOfRetries + 
                    "] Next Notification:[" + (nextNotif!=null?DateUtil.getDateTime(nextNotif, new Locale("en", "US"),2,2):"-") +
                    "] Elapsed time (ms):[" + elapsedTime + "]");
                        
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }    
    
    private Timestamp addPeriodicity(int incType, int incNumber, Integer minute, Integer hour, Timestamp baseline){
        Timestamp response = null;
        
        if (minute!=null && hour!=null) {
            response = DateUtil.getChangedDate(baseline, incType, incNumber);
            Calendar c = Calendar.getInstance();
    	    c.setTimeInMillis(response.getTime());
            response = DateUtil.getDateTime(c.get(Calendar.DATE)+"", c.get(Calendar.MONTH)+"", c.get(Calendar.YEAR)+"", 
                    hour+"", minute+"", c.get(Calendar.SECOND)+"");
        }
        
        return response;
        
    }

    public NotificationTO getNotificationObject(NotificationTO nto) throws BusinessException {
        NotificationTO response = new NotificationTO();
        try {        
            response = (NotificationTO) dao.getObject(nto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    


    public void insertNotification(NotificationTO nto) throws BusinessException {
        try {        
            dao.insert(nto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }
    
    public void updateNotification(NotificationTO nto) throws BusinessException {
        try {        
            dao.update(nto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }


    public void removeNotification(NotificationTO nto) throws BusinessException {
        try {        
            dao.remove(nto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }                
    }    
}
