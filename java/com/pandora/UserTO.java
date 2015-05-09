package com.pandora;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Locale;

import org.apache.struts.util.MessageResources;

import com.pandora.integration.Integration;

/**
 * This object it is a bean represents of User entity. 
 */
public class UserTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    /** The username (unique) related with user */ 
    private String username;
    
    /** The full name of User */
    private String name;
    
    /** The phone number of User*/
    private String phone;
    
    /** The email address of User*/
    private String email;
        
    /** The color that represents the User on GUI*/
    private String color;
    
    /** The password of User*/
    private String password;

    /** The country of User used by locale*/
    private String country;

    /** The language of User used by locale*/
    private String language;
    
    /** The list of preferences of current user object */
    private PreferenceTO preference;
    
    /** The current department related with user. */
    private DepartmentTO lnkDepartment;

    /** The current company area related with user. */
    private AreaTO lnkArea;

    /** The current function related with user. */
    private FunctionTO lnkFunction;
    
    /** The resource bundle of system related with locale of user */
    private MessageResources bundle;
    
    /** The birth date of current user */
    private Date birth;
    
    private String authenticationMode;
    
    private String permission;
    
    private byte[] fileInBytes;
    
    private ByteArrayInputStream binaryFile;
    
    private Timestamp finalDate;
    
    
    /** Return the new Struts forward name based on specific user role <br> 
     * This method should be re-implemented by each sub-class.*/
    public String getForwardLogin() {
        return null;
    }
    
    /**
     * Constructor 
     */
    public UserTO(){
    }

    /**
     * Constructor 
     */    
    public UserTO(String id){
        this.setId(id);
    }

    /**
     * Constructor 
     */    
    public UserTO(Integration iobj){
        this.setUsername(iobj.getUser());
        this.setPassword(iobj.getPassword());
    }

    
    /**
     * Return the current locale used by user 
     * @return
     */
    public Locale getLocale(){
    	Locale response = null;
    	if (this.country==null || this.language==null){
    		response = new Locale("en", "US");
		} else {
			response = new Locale(this.language, this.country);
		}
        return response;
    }

    
    public String getCalendarMask(){
    	String response = "MM/dd/yyyy";
    	if (this.bundle!=null) {
    		response = this.bundle.getMessage(this.getLocale(), "calendar.format");
    	}
    	return response;
    }
    
    
    ////////////////////////////////////       
    public ByteArrayInputStream getBinaryFile() {
        return binaryFile;
    }
    public void setBinaryFile(ByteArrayInputStream newValue) {
        this.binaryFile = newValue;
    }

    
    ////////////////////////////////////       
    public byte[] getFileInBytes() {
        return fileInBytes;
    }
    public void setFileInBytes(byte[] newValue) {
        this.fileInBytes = newValue;
    }    
    
    ///////////////////////////////////////
    public String getUsername() {
        return username;
    }
    public void setUsername(String newValue) {
        this.username = newValue;
    }

    ///////////////////////////////////////    
    public String getColor() {
        return color;
    }
    public void setColor(String newValue) {
        this.color = newValue;
    }
    
    ///////////////////////////////////////    
    public String getEmail() {
        return email;
    }
    public void setEmail(String newValue) {
        this.email = newValue;
    }
    
    ///////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////    
    public String getPassword() {
        return password;
    }
    public void setPassword(String newValue) {
        this.password = newValue;
    }
    
    ///////////////////////////////////////
    public String getPhone() {
        return phone;
    }       
    public void setPhone(String newValue) {
        this.phone = newValue;
    }

   
    ///////////////////////////////////////        
    public AreaTO getArea() {
        return lnkArea;
    }
    public void setArea(AreaTO newValue) {
        this.lnkArea = newValue;
    }
    
    ///////////////////////////////////////        
    public DepartmentTO getDepartment() {
        return lnkDepartment;
    }
    public void setDepartment(DepartmentTO newValue) {
        this.lnkDepartment = newValue;
    }
    
    ///////////////////////////////////////        
    public FunctionTO getFunction() {
        return lnkFunction;
    }
    public void setFunction(FunctionTO newValue) {
        this.lnkFunction = newValue;
    }
    
    ///////////////////////////////////////        
    public String getCountry() {
        return country;
    }
    public void setCountry(String newValue) {
        this.country = newValue;
    }
    
    ///////////////////////////////////////    
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String newValue) {
        this.language = newValue;
    }

    ///////////////////////////////////////    
    public PreferenceTO getPreference() {
        return preference;
    }
    public void setPreference(PreferenceTO newValue) {
        this.preference = newValue;
    }
    
    ///////////////////////////////////////    
    public MessageResources getBundle() {
        return bundle;
    }
    public void setBundle(MessageResources newValue) {
        this.bundle = newValue;
    }

    ///////////////////////////////////////        
    public Date getBirth() {
        return birth;
    }
    public void setBirth(Date newValue) {
        this.birth = newValue;
    }

    
    ///////////////////////////////////////     
	public String getAuthenticationMode() {
		return authenticationMode;
	}
	public void setAuthenticationMode(String newValue) {
		this.authenticationMode = newValue;
	}
	
	
    ///////////////////////////////////////     
	public Timestamp getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(Timestamp newValue) {
		this.finalDate = newValue;
	}
	

    ///////////////////////////////////////  	
	public String getPermission() {
		return permission;
	}
	public void setPermission(String newValue) {
		this.permission = newValue;
	}

	
	
	/**
	 * This method return OK only if the user has a customer role. 
	 */
	public String getShowReportMenu() {
		String response = "OK";
		if (this instanceof ResourceTO) {
			response = null;
		}
		return response;
	}
	   
}
