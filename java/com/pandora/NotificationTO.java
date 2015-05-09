package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Vector;

import com.pandora.bus.alert.Notification;
import com.pandora.helper.DateUtil;

/**
 */
public class NotificationTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    public static Integer PERIODICITY_YEARLY     = new Integer(1);
    
    public static Integer PERIODICITY_MONTHLY    = new Integer(2);
    
    public static Integer PERIODICITY_WEEKLY     = new Integer(3);
    
    public static Integer PERIODICITY_DAILY      = new Integer(4);
    
    public static Integer PERIODICITY_EVENTUALLY = new Integer(5); //or column periodicy = NULL into DB
    
    
    /** The Name of Notification */
    private String name;
    
    /** The Description of Notification */
    private String description;
    
    /** The specific fields of notification */
    private Vector fields;
    
    /** The number of retry of engine should be perform 
     * if the notification process fail.*/
    private Integer retryNumber;
    
    /** The date/time of next notification, (if a notification is necessary) */
    private Timestamp nextNotification;
    
    /** SQL Statement to be executed */
    private String sqlStement;
    
    /** if is null, the notification is enable, 
     * otherwise, no notification will be perform.*/
    private Timestamp finalDate;
    
    /** This attribute contain the date/time of last notification 
     * checking done by system. */
    private Timestamp lastCheck;
    
    /** The business class that contain the business rule for the notification */
    private String notificationClass;
    
    /** Define the periodicity of notification in minutes*/
    private Integer periodicityMinute;
    
    /** Define the periodicity of notification in hours*/
    private Integer periodicityHour;
        
    /** Define the periodicity of notification.*/
    private Integer periodicity;
       
    
    /**
     * Constructor 
     */
    public NotificationTO(){
    }

    
    /**
     * Constructor 
     */    
    public NotificationTO(String id){
        this.setId(id);
    }
    
    
    /**
     * Return a field value based to the fieldKey
     */
    public String getFieldValueByKey(String fieldKey) {
        String response = "";
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                NotificationFieldTO field = (NotificationFieldTO)fields.elementAt(i);
                if (field.getName().equals(fieldKey)) {
                    response = field.getValue();
                    break;
                }
            }
        }
        return response;
    }


    public String getFieldValueByKey(String fieldKey, Notification bus, String mask, Locale loc) {
        String response = "";
        
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                NotificationFieldTO field = (NotificationFieldTO)fields.elementAt(i);
                if (field.getName().equals(fieldKey)) {
                	
                	String type = bus.getType(field.getName());
                	if (type!=null && type.equals(FieldValueTO.FIELD_TYPE_DATE)) {
                	    Timestamp ts = DateUtil.getDateTime(field.getValue(), "MM/dd/yyyy", new Locale("en", "US"));
                		response = DateUtil.getDate(ts, mask, loc);
                	} else {
                		response = field.getValue();	
                	}
                    
                    break;
                }
            }
        }
        return response;
    }
    
    
    /////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    /////////////////////////////////////////    
    public Vector getFields() {
        return fields;
    }
    public void setFields(Vector newValue) {
        this.fields = newValue;
    }
    
    
    /////////////////////////////////////////    
    public Timestamp getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(Timestamp newValue) {
        this.finalDate = newValue;
    }

    
    /////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    /////////////////////////////////////////    
    public Timestamp getNextNotification() {
        return nextNotification;
    }
    public void setNextNotification(Timestamp newValue) {
        this.nextNotification = newValue;
    }
    
    /////////////////////////////////////////    
    public Integer getRetryNumber() {
        return retryNumber;
    }
    public void setRetryNumber(Integer newValue) {
        this.retryNumber = newValue;
    }
    
    
    /////////////////////////////////////////    
    public String getSqlStement() {
        return sqlStement;
    }
    public void setSqlStement(String newValue) {
        this.sqlStement = newValue;
    }

    
    /////////////////////////////////////////        
    public String getNotificationClass() {
        return notificationClass;
    }
    public void setNotificationClass(String newValue) {
        this.notificationClass = newValue;
    }
    
    
    /////////////////////////////////////////    
    public Timestamp getLastCheck() {
        return lastCheck;
    }
    public void setLastCheck(Timestamp newValue) {
        this.lastCheck = newValue;
    }
    
    /////////////////////////////////////////        
    public Integer getPeriodicityHour() {
        return periodicityHour;
    }
    public void setPeriodicityHour(Integer newValue) {
        this.periodicityHour = newValue;
    }
    
    /////////////////////////////////////////        
    public Integer getPeriodicityMinute() {
        return periodicityMinute;
    }
    public void setPeriodicityMinute(Integer newValue) {
        this.periodicityMinute = newValue;
    }
        
    /////////////////////////////////////////     
    public Integer getPeriodicity() {
        return periodicity;
    }
    public void setPeriodicity(Integer newValue) {
        this.periodicity = newValue;
    }


    public void addField(NotificationFieldTO nfto) {
        if (this.fields==null) {
            this.fields = new Vector();
        }
        this.fields.add(nfto);        
    }
}
