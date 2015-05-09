package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.bus.GeneralTimer;
import com.pandora.bus.SystemSingleton;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;

/**
 * This class is responsible for starting-up the system timer  
 */
public class SystemStartup extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * kick-off.
	 */	
	public void init() {
		String timer = "";
		String ds = "";
		try {
			timer = getInitParameter("timer");
			ds = getInitParameter("data_source");
			
			
			if (timer!=null && !timer.trim().toUpperCase().equals("OFF")){
				SystemSingleton.getInstance().setTimerStatus("on");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "Starting Timer...");
				System.out.println("Starting Timer...");
				GeneralTimer.getInstance();
			} else {
				SystemSingleton.getInstance().setTimerStatus("off");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "Timer off...");
				System.out.println("Timer off...");
			}

			
			if (ds!=null){
				SystemSingleton.getInstance().setDataSource(ds);
				LogUtil.log(this, LogUtil.LOG_DEBUG, "Data source defined..." + ds);
				System.out.println("Data source defined..." + ds);
			} else {
				SystemSingleton.getInstance().setDataSource(null);
				LogUtil.log(this, LogUtil.LOG_DEBUG, "ERROR!! data source not defined. Check web.xml file.");
				System.out.println("ERROR!! data source not defined. Check web.xml file.");
			}
			
		} catch (BusinessException e) {
			LogUtil.log(this, LogUtil.LOG_ERROR, "error at startup parameters time=" + timer + "; ds=" + ds + " - ", e);
		}
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
	}
	
}