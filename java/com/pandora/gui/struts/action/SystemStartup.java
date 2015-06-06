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
		String timer = "", ds = "", prot = "", defEncoding = "";
		String jdbcDriver = "", jdbcHost = "", jdbcUser = "", jdbcPass = "";
		try {
			timer = getInitParameter("timer");
			ds = getInitParameter("data_source");
			prot = getInitParameter("sys_protocol");
			defEncoding = getInitParameter("default_encoding");
			
			jdbcDriver= getInitParameter("jdbc_driver");
			jdbcHost= getInitParameter("jdbc_host");
			jdbcUser= getInitParameter("jdbc_user");
			jdbcPass= getInitParameter("jdbc_pass");
			
			if (timer!=null && !timer.trim().equalsIgnoreCase("off")){
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


			if (prot!=null){
				SystemSingleton.getInstance().setSystemProtocol(prot);
				LogUtil.log(this, LogUtil.LOG_DEBUG, "Protocol defined..." + prot);
				System.out.println("Protocol defined..." + prot);
			} else {
				SystemSingleton.getInstance().setSystemProtocol("http");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "The system will use a default value for sys_protocol: [http]");
				System.out.println("The system will be use a default value for sys_protocol: [http]");
			}
			
			if (defEncoding!=null){
				SystemSingleton.getInstance().setDefaultEncoding(defEncoding);
				LogUtil.log(this, LogUtil.LOG_DEBUG, "Default Encoding..." + defEncoding);
				System.out.println("Default Encoding defined..." + defEncoding);
			} else {
				SystemSingleton.getInstance().setDefaultEncoding("UTF-8");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "The system will use a default encoding: [UTF-8]");
				System.out.println("The system will use a default encoding: [UTF-8]");
			}
			
			if (ds==null || ds.trim().equals("")) {
				if (jdbcDriver!=null && !jdbcDriver.trim().equals("")){
					SystemSingleton.getInstance().setJdbcDriver(jdbcDriver);
					System.out.println("Defining JDBC Driver to [" + jdbcDriver + "]");
				}

				if (jdbcHost!=null && !jdbcHost.trim().equals("")){
					SystemSingleton.getInstance().setJdbcHost(jdbcHost);
					System.out.println("Defining JDBC Host to [" + jdbcHost + "]");
				}
				
				if (jdbcUser!=null && !jdbcUser.trim().equals("")){
					SystemSingleton.getInstance().setJdbcUser(jdbcUser);
					System.out.println("Defining JDBC User to [" + jdbcUser + "]");
				}

				if (jdbcPass!=null && !jdbcPass.trim().equals("")){
					SystemSingleton.getInstance().setJdbcPass(jdbcPass);
					System.out.println("Defining JDBC Password...");
				}				
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