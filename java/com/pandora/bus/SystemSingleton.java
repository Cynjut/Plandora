package com.pandora.bus;

public class SystemSingleton {

    private static SystemSingleton myInstance;

    private static String timerStatus;
    
    private static String dataSource;
    
    private static String publicDownload;
    
    private static String EDIDownload;
    
    private static String publicArtifactDownload;
    
    private static String systemProtocol;
    
    private static String defaultEncoding;
    
    private static String jdbcDriver;
    
    private static String jdbcHost;
    
    private static String jdbcUser;
    
    private static String jdbcPass;
    
    
    private SystemSingleton() {
    }
    
    
    public static SystemSingleton getInstance(){
          if(myInstance == null) {
               myInstance = new SystemSingleton();
          }
          return myInstance;
    }

    
	
    ////////////////////////////////////////
	public String getTimerStatus() {
		return timerStatus;
	}
	public void setTimerStatus(String newValue) {
		SystemSingleton.timerStatus = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getDataSource() {
		return dataSource;
	}	
	public void setDataSource(String newValue) {
		SystemSingleton.dataSource = newValue;
	}


	////////////////////////////////////////
	public String getPublicDownload() {
		return publicDownload;
	}
	public void setPublicDownload(String newValue) {
		SystemSingleton.publicDownload = newValue;
	}

	
	////////////////////////////////////////
	public String getEDIDownload() {
		return EDIDownload;
	}
	public void setEDIDownload(String newValue) {
		SystemSingleton.EDIDownload = newValue;
	}


	////////////////////////////////////////
	public String getPublicArtifactDownload() {
		return publicArtifactDownload;
	}
	public void setPublicArtifactDownload(String newValue) {
		SystemSingleton.publicArtifactDownload = newValue;
	}


	////////////////////////////////////////
	public String getSystemProtocol() {
		return systemProtocol;
	}
	public void setSystemProtocol(String newValue) {
		SystemSingleton.systemProtocol = newValue;
	}
	
	////////////////////////////////////////
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	public void setDefaultEncoding(String newValue) {
		SystemSingleton.defaultEncoding = newValue;
	}
	
	
	////////////////////////////////////////
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public void setJdbcDriver(String newValue) {
		SystemSingleton.jdbcDriver = newValue;
	}

	
	////////////////////////////////////////
	public String getJdbcHost() {
		return jdbcHost;
	}
	public void setJdbcHost(String newValue) {
		SystemSingleton.jdbcHost = newValue;
	}

	
	////////////////////////////////////////
	public String getJdbcUser() {
		return jdbcUser;
	}
	public void setJdbcUser(String newValue) {
		SystemSingleton.jdbcUser = newValue;
	}
	
	
	////////////////////////////////////////
	public String getJdbcPass() {
		return jdbcPass;
	}
	public void setJdbcPass(String newValue) {
		SystemSingleton.jdbcPass = newValue;
	}	
}
