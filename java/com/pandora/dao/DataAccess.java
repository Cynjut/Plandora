package com.pandora.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pandora.TransferObject;
import com.pandora.bus.SystemSingleton;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.ZeroCapacityDBException;
import com.pandora.helper.LogUtil;

/**
 * This class contain the basic procedures of data access. <br>
 * All DAO classes extends his features.
 */
public class DataAccess {
	
	/** used to perform insert */
	private final int INSERT_ACTION = 1;
	
	/** used to perform update */	
	private final int UPDATE_ACTION = 2;	
	
	/** used to perform delete */	
	private final int DELETE_ACTION = 3;	

				
	/**
	 * Return a connection with data base. <br>
	 * The auto commit status is true.
	 */
	public Connection getConnection() throws DataAccessException{
		return this.getConnection(true);
	}


	/**
	 * Return a connection with data base. <br>
	 */
	public Connection getConnection(boolean isAutoCommit) throws DataAccessException{
		Connection con = null;
		String datasource = SystemSingleton.getInstance().getDataSource();  
		
        try {
            
            //get the context
     	    Context initCtx = new InitialContext();
     	    Context envCtx = (Context) initCtx.lookup("java:comp/env");

     	    //retrieve the data source object
     	    DataSource ds = (DataSource) envCtx.lookup(datasource);

     	    //get a clean connection from pool
     	    con = ds.getConnection();
			con.setAutoCommit(isAutoCommit);

        } catch (SQLException e) {
			throw new DataAccessException(e);
        } catch (NamingException e) {
         	throw new DataAccessException(e);
        }
		return con;
	}

	
	/**
	 * This method open a new connection with database and
	 * request a query with all data of specific entity.
	 */
	public Vector getList() throws DataAccessException{
		Vector response = null;
		Connection c = null;
		try {
			c = getConnection();
			response = this.getList(c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}		
		return response;
	}


	/**
	 * This method must be overload by DAO class.
	 */
	public Vector getList(Connection c) throws DataAccessException{
		return null;
	}


	/**
	 * This method open a new connection with database and
	 * request a query of a specific object from a specific entity.
	 */
	public TransferObject getObject(TransferObject to) throws DataAccessException{
		TransferObject response = null;
		Connection c = null;
		try {
			c = getConnection();		
			response = this.getObject(to, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
		return response;		
	}


	/**
	 * This method must be overload by DAO class.
	 */
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException{
		return null;
	}

	
	/**
	 * This method open a connection with database and 
	 * request the persistence of the current object (TO).
	 */
	public void insert(TransferObject to) throws DataAccessException{
		this.performExecute(to, this.INSERT_ACTION);
	}


	/**
	 * This method must be overload by DAO class.
	 */
	public void insert(TransferObject to, Connection c) throws DataAccessException{
		//This method must be implemented externally
	}

	
	/**
	 * This method open a connection with database and 
	 * request the information update of the current object (TO).
	 */	
	public void update(TransferObject to) throws DataAccessException{
		this.performExecute(to, this.UPDATE_ACTION);
	}


	/**
	 * This method must be overload by DAO class.
	 */
	public void update(TransferObject to, Connection c) throws DataAccessException{
		//This method must be implemented externally
	}


	/**
	 * This method open a connection with database and
	 * request the delete of current object (TO).
	 */
	public void remove(TransferObject to) throws DataAccessException{
		this.performExecute(to, this.DELETE_ACTION);
	}

	
	/**
	 * This method must be overload by DAO class.
	 */
	public void remove(TransferObject to, Connection c) throws DataAccessException{
		//This method must be implemented externally
	}
	
	
	/**
	 * Auxiliary method to perform a specific action into data base.
	 */
	private void performExecute(TransferObject to, int action) throws DataAccessException{
		Connection c = null;
		try {
			c = getConnection(false);
			
			if (action==this.INSERT_ACTION){
				this.insert(to, c);
			} else if (action==this.UPDATE_ACTION){
				this.update(to, c);				
			} else if (action==this.DELETE_ACTION){
				this.remove(to, c);				
			}

			c.commit();
		} catch(Exception e){
			try {
				if (c!=null) {
					c.rollback();	
				}
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			if (e instanceof ZeroCapacityDBException) {
				throw (ZeroCapacityDBException)e;
			} else {
				throw new DataAccessException(e);	
			}
			
		} finally{
			this.closeConnection(c);
		}		
	}
	
	
	/**
	 * Close a connection
	 */
	public void closeConnection(Connection c){
		try {
			if (c!=null) c.close();
		} catch (SQLException ec) {
		    LogUtil.log(this, LogUtil.LOG_ERROR, "", ec);
		}		
	}
	
	
	/**
	 * Get next value for new id.
	 * @return
	 * @throws DataAccessException
	 */
	protected String getNewId() throws DataAccessException{
		String response = "";
		Connection c = null;
		try {
			c = getConnection();		
			response = "" + this.getNewId(c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally {
		    closeConnection(c);
		}
		return response;	   
	}
	
	
	protected Connection getJDBCConnection(String driver, String uri, String user, String pass, boolean isAutoCommit) throws Exception{
		Connection con = null;
		
		/*
		//MICROSOFT ACCESS THRU ODBC 
		try {
		    Class.forName(driver);
		    con = DriverManager.getConnection(uri, user, pass);
		    con.setAutoCommit(isAutoCommit);
		}catch(Exception e)	{
		    throw new DataAccessException(e);
		}
		*/

	    Class.forName(driver);
	    con = DriverManager.getConnection(uri, user, pass);
	    con.setAutoCommit(isAutoCommit);
	    
	    if(!con.isClosed()) {
	    	LogUtil.log(this, LogUtil.LOG_INFO, "Successfully connected to DB [" + uri + "]");
	    }
		    
		return con;
	}
	
	
	/**
	 * Get next value for new id. 
	 */
	private synchronized long getNewId(Connection c) throws DataAccessException {
		long currentValue = 0;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {

		    //get the current value of sequence table
			pstmt = c.prepareStatement("select id from p_sequence");
			rs = pstmt.executeQuery();
			if (rs.next()){
			    currentValue = getLong(rs, "id").longValue();
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} catch (NullPointerException e) {
		    LogUtil.log(this, LogUtil.LOG_ERROR, "current sequence is null, assuming value=0", e);
		    currentValue = 0;			
		}finally{
			closeStatement(rs, pstmt);
		}
		
		try {
		    pstmt = null;
		    currentValue++;
			pstmt = c.prepareStatement("update p_sequence set id=" + currentValue);
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			closeStatement(null, pstmt);
		}
		return currentValue;	   
	}

	
	/**
	 * Gets a Long object from a ResultSet given the field name
	 */
	public static Long getLong(ResultSet rs, String fieldName) throws DataAccessException{
		Long result = null;
		String str = null;
		try{
			str = rs.getString(fieldName);	
			if (str != null){
				result = Long.valueOf(str);	
			}
		}catch(Exception e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ " as Long");
			throw ec;
		}
		return result;
	}

	/**
	 * Gets a Integer object from a ResultSet given the field name
	 */
	public static Integer getInteger(ResultSet rs, String fieldName) throws DataAccessException{
		Integer result = null;
		try{
			BigDecimal value = rs.getBigDecimal(fieldName);	
			if (value != null){
				result =  new Integer(value.intValue());	
			}
		}catch(Exception e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as Integer");
			throw ec;
		}
		return result;
	}

	/**
	 * Gets a Boolean object from a ResultSet given the field name
	 */
	public static Boolean getBoolean(ResultSet rs, String fieldName) throws DataAccessException{
		Boolean result = null;
		String str = null;
		try{
			str = rs.getString(fieldName);	
			if (str != null){
				result = new Boolean (rs.getBoolean(fieldName));	
			}
		}catch(Exception e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as Boolean");
			throw ec;
		}
		return result;
	}
	
	/**
	 * Gets a Float object from a ResultSet given the field name
	 */
	public static Float getFloat(ResultSet rs, String fieldName) throws DataAccessException{
		Float result = null;
		try{
			float value = rs.getFloat(fieldName);	
			result = new Float(value);	

		}catch(Exception e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as Float");
			throw ec;
		}
		return result;
	}
	
	/**
	 * Gets a Timestamp object from a ResultSet given the field name
	 */
	public static Timestamp getTimestamp(ResultSet rs, String fieldName) throws DataAccessException{
		Timestamp result = null;
		String str = null;
		try{
			str = rs.getString(fieldName);	
			if (str != null){
				result = Timestamp.valueOf(str);	
			}
		}catch(SQLException e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as Timestamp");
			throw ec;
		}
		return result;
	}

	/**
	 * Gets a Timestamp object from a ResultSet given the field name
	 */
	public static String getString(ResultSet rs, String fieldName) throws DataAccessException{
		
		String result = null;
		try{
			result = rs.getString(fieldName);	
		}catch(SQLException e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as String");
			throw ec;
		}
		return result;
	}

	/**
	 * Gets a Date object from a ResultSet given the field name
	 */
	public static Date getDate(ResultSet rs, String fieldName) throws DataAccessException{
		
	    Date result = null;
		try{
			result = rs.getDate(fieldName);	
		}catch(SQLException e){
			DataAccessException ec = new DataAccessException(e);
			ec.setErrorMessage("Error getting field "+fieldName+ "as Date");
			throw ec;
		}
		return result;
	}	
	
	
	protected void closeStatement(ResultSet rs, PreparedStatement pstmt){
		try {
			if(rs != null) {
				rs.close();
			}
			if(pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException ec) {
		    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
		} 				
	}
}
