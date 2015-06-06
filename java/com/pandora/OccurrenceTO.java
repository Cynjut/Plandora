package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Vector;

import com.pandora.bus.occurrence.Occurrence;
import com.pandora.helper.DateUtil;

/**
 */
public class OccurrenceTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

    private String source;
    
    private String name;
    
    private String status;
    
    private String statusLabel;
    
    private ProjectTO project;
    
    private Vector<OccurrenceFieldTO> fields;
    
    private UserTO handler;
    
    private Locale locale;
    
   
    /**
     * Constructor 
     */
    public OccurrenceTO(){
    }

    
    /**
     * Constructor 
     */    
    public OccurrenceTO(String id){
        this.setId(id);
    }
    
    
    public String getEntityType(){
        return PLANNING_OCCURENCE;
    }

    
    /**
     * Return a field value based to the fieldKey
     */
    public String getFieldValueByKey(String fieldKey, Occurrence bus, String mask, Locale loc) {
        String response = "";
        
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                OccurrenceFieldTO field = (OccurrenceFieldTO)fields.elementAt(i);
                if (field.getField().equals(fieldKey)) {
                	
                	String type = bus.getType(field.getField());
                	if (type!=null && field.getDateValue()!=null && type.equals(FieldValueTO.FIELD_TYPE_DATE)) {
                		if (loc==null || mask==null){
                			loc = new Locale("en", "US");
                			mask = "MM/dd/yyyy";
                		}
                		response = DateUtil.getDate(field.getDateValue(), mask, loc);
                		
                	} else {
                		response = field.getValue();	
                	}
                    
                    break;
                }
            }
        }
        return response;
    }
    
    
	public Vector<Vector<Object>> getFieldTableValueByKey(String fieldKey, Occurrence bus) {
		Vector<Vector<Object>> response =null;
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                OccurrenceFieldTO field = (OccurrenceFieldTO)fields.elementAt(i);
                if (field!=null && field.getField()!=null && field.getField().equals(fieldKey)) {
               		response = field.getTableValues();	
                    break;
                }
            }
        }
        return response;
	}
    
    

    public Timestamp getFieldDateByKey(String fieldKey, Occurrence bus) {
        Timestamp response = null;
        
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                OccurrenceFieldTO field = (OccurrenceFieldTO)fields.elementAt(i);
                if (field!=null && field.getField()!=null && field.getField().equals(fieldKey)) {
                	String type = bus.getType(field.getField());
                	if (type!=null && field.getDateValue()!=null && 
                			type.equals(FieldValueTO.FIELD_TYPE_DATE)) {
                		response = field.getDateValue();
                	}                    
                    break;
                }
            }
        }
        return response;
    }
    
    public OccurrenceFieldTO getField(String fieldKey) {
    	OccurrenceFieldTO response = null;
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                OccurrenceFieldTO field = (OccurrenceFieldTO)fields.elementAt(i);
                if (field!=null && field.getField()!=null && field.getField().equals(fieldKey)) {                	
               		response = field;
                    break;
                }
            }
        }
        return response;
    }
    
    
    public String getFieldToString(){
        String response = "";
        if (fields!=null) {
            for (int i = 0; i< fields.size(); i++) {
                OccurrenceFieldTO field = (OccurrenceFieldTO)fields.elementAt(i);
                if (field.getField()!=null && !field.getField().equals("")) {
                    response = response + field.getField() + "=[" + field.getValue() + "]\n";                    
                }
            }
        }
        return response;        
    }
    
    
	public static Object getClass(String classesList, String source) throws Exception{
		Object response = null;
		
	    if (classesList!=null && !classesList.trim().equals("")) {
	        String[] list = classesList.split(";");
	        if (list!=null && list.length>0) {
	        	
	            for (int i = 0; i<list.length; i++) {
	                String classStr = list[i].trim();
	                Object bus = null;
	                try {
	                    @SuppressWarnings("rawtypes")
						Class klass = Class.forName(classStr);
	                    bus = klass.newInstance();
	                } catch (java.lang.NoClassDefFoundError e) {
	                	bus = null;
	                	throw e;
	                    
	                } catch (Exception e) {
	                	bus = null;
	                	throw e;
	                }
	                
                    if (bus!=null && source.equals(bus.getClass().getName())) {
                        response = bus;
                    }
	            }
	        }
	    }
	    
	    return response;
	}

	
    /////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    /////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    
    /////////////////////////////////    
    public String getSource() {
        return source;
    }
    public void setSource(String newValue) {
        this.source = newValue;
    }
    
    
    /////////////////////////////////        
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }

    
	/////////////////////////////////         
    public String getStatusLabel() {
        return statusLabel;
    }
    public void setStatusLabel(String newValue) {
        this.statusLabel = newValue;
    }
    
    
    /////////////////////////////////             
    public Vector<OccurrenceFieldTO> getFields() {
        return fields;
    }
    public void setFields(Vector<OccurrenceFieldTO> newValue) {
        this.fields = newValue;
    }


    public void addField(OccurrenceFieldTO ofto) {
        if (this.fields==null) {
            this.fields = new Vector<OccurrenceFieldTO>();
        }
        this.fields.add(ofto);        
    }
    
    
    /////////////////////////////////        
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    

    /////////////////////////////////     
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale newValue) {
        this.locale = newValue;
    }


}
