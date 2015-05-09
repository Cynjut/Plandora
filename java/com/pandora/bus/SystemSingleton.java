package com.pandora.bus;

public class SystemSingleton {

    private static SystemSingleton myInstance;

    private static String timerStatus;
    
    private static String dataSource;
    
    
    
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
}
