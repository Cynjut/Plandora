package com.pandora.bus;

import java.util.ArrayList;
import java.util.Vector;

import com.pandora.DBQueryParam;
import com.pandora.DBQueryResult;
import com.pandora.dao.DbQueryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the method to browse data into db. 
 */
public class DbQueryBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    DbQueryDAO dao = new DbQueryDAO();
    
    
    /**
     * Perform statement into data base
     */    
    public DBQueryResult performQuery(String sql, int[] types, Vector params) throws BusinessException{
    	DBQueryResult response = new DBQueryResult();
        try {
            response = dao.performQuery(sql, types, params);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
    }

    
    public String getNewId() throws BusinessException{
    	String response = null;
        try {
            response = dao.getNewId();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    	return response;
    }
    
    /**
     * Execute statement into data base
     * @param sql
     * @return
     * @throws BusinessException
     */
    public Vector executeQuery(String sql) throws BusinessException{
        Vector response = new Vector();
        try {
            DbQueryDAO dao = new DbQueryDAO();
            response = dao.executeQuery(sql);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
    }    

    
	public Vector executeQuery(ArrayList<String> sql, ArrayList<DBQueryParam> params) throws BusinessException {
        Vector response = new Vector();
        try {
            DbQueryDAO dao = new DbQueryDAO();
            response = dao.executeQuery(sql, params);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
	}
    
    
    /**
     * For a tableName get all fields from it.
     */
    public Vector<Vector<Object>> getMetaData(String tableName) throws BusinessException{
    	Vector<Vector<Object>> response = new Vector<Vector<Object>>();
        try {
            DbQueryDAO dao = new DbQueryDAO();
            response = dao.getMetaData(tableName);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;                
    }


	public String getDBProductName() throws BusinessException{
        String response = null;
        try {
            DbQueryDAO dao = new DbQueryDAO();
            response = dao.getDBProductName();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
	}


}
