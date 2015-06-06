package com.pandora.delegate;

import java.util.ArrayList;
import java.util.Vector;

import com.pandora.DBQueryParam;
import com.pandora.DBQueryResult;
import com.pandora.bus.DbQueryBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for DbQuery entity.
 */
public class DbQueryDelegate extends GeneralDelegate {


    /** The Business object related with current delegate */
    DbQueryBUS bus = new DbQueryBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.DbQueryBUS.performQuery(java.lang.String)
     */
    public Vector<Vector<Object>> performQuery(String sql) throws BusinessException{
        return this.performQuery(sql, null, null);
    }


    public Vector<Vector<Object>> performQuery(String sql, int[] types, Vector params) throws BusinessException{
    	DBQueryResult r = bus.performQuery(sql, types ,params);
    	Vector<Vector<Object>> v = new Vector<Vector<Object>>();
    	v.addElement(r.getColumns());
    	v.addAll(r.getData());
        return v;
    }


    public String getNewId() throws BusinessException{
        return bus.getNewId();
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.DbQueryBUS.executeQuery(java.lang.String)
     */
    public Vector executeQuery(String sql) throws BusinessException{
        return bus.executeQuery(sql);
    }

    public Vector executeQuery(ArrayList<String> sql, ArrayList<DBQueryParam> params) throws BusinessException{
        return bus.executeQuery(sql, params);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.DbQueryBUS.getMetaData(java.lang.String)
     */
    public Vector<Vector<Object>> getMetaData(String tableName) throws BusinessException{
        return bus.getMetaData(tableName);
    }    
    
    
    public String getDBProductName() throws BusinessException{
    	return bus.getDBProductName();
    }
}

