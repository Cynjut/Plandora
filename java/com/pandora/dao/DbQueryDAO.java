package com.pandora.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.pandora.DBQueryParam;
import com.pandora.DBQueryResult;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * 
 */
public class DbQueryDAO extends DataAccess {

    
    /**
     * Perform statement into data base
     */
    public  DBQueryResult performQuery(String sql, int[] types, Vector<Object> params) throws DataAccessException{
    	DBQueryResult response = null;
        Connection c = null;
		try {
			c = this.getConnectionBasedToSql(sql);
			sql = removeConnStringFromSql(sql);
			
			response = this.performQuery(sql, types, params, c);
			
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }


    /**
     * Return the meta data of a table into a vector of vector, where:<br>
     * - 1st element is the field name <br>
     * - 2nd element is the field type <br>
     * - 3th element is the field size in characters <br>
     *  
     * @param table
     * @return
     * @throws DataAccessException
     */
    public Vector<Vector<Object>> getMetaData(String table) throws DataAccessException {
    	Vector<Vector<Object>> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getMetaData(table, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    public String getNewId() throws DataAccessException{
        String response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = super.getNewId();
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    /**
     * Execute statement into data base
     */
    public Vector executeQuery(String sql) throws DataAccessException{
        Vector response = null;
        Connection c = null;
		try {
			c = this.getConnectionBasedToSql(sql);
			sql = removeConnStringFromSql(sql);

			response = this.executeQuery(sql, null, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
        
	public Vector executeQuery(ArrayList<String> sql, ArrayList<DBQueryParam> params) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = this.getConnectionBasedToSql((String)sql.get(0), false);			
			for (int i=0; i<sql.size(); i++) {
				String theSql = removeConnStringFromSql((String)sql.get(i));
				Vector resp = this.executeQuery(theSql, (DBQueryParam)params.get(i), c);
				if (response==null) {
					response = new Vector();	
				}
				response.add(resp.get(0));
			}
			
			c.commit();

		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
	}
    
    private Vector<Vector<Object>> getMetaData(String table, Connection c) throws DataAccessException{
		Vector<Vector<Object>> response= new Vector<Vector<Object>>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
		    pstmt = c.prepareStatement("SELECT * FROM " + table);
			rs = pstmt.executeQuery();

			// Get the metadata
	        ResultSetMetaData md = rs.getMetaData();
	        
	        for(int i = 1; i<=md.getColumnCount(); i++) {
	           Vector<Object> item = new Vector<Object>();
	           item.addElement(new Integer(i));
	           item.addElement(md.getColumnLabel(i));
	           item.addElement(new Integer(md.getColumnType(i)));
	           item.addElement(new Integer(md.getColumnDisplaySize(i)));
	           response.addElement(item);
	        }

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    /**
     * Perform statement into data base
     */
    private DBQueryResult performQuery(String sql, int[] types, Vector<Object> params, Connection c) throws DataAccessException{
    	DBQueryResult response = new DBQueryResult();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {

			pstmt = c.prepareStatement(sql);			

			DBQueryParam qp = new DBQueryParam(types, params);
			this.setupPrepareStatement(qp, pstmt);
			
			rs = pstmt.executeQuery();

			while (rs.next()){
			    Vector<Object> line = new Vector<Object>(); 
			    ResultSetMetaData meta = rs.getMetaData();
			    int size = meta.getColumnCount();
			    for (int i=1; i<=size ;i++){
			        String colName = meta.getColumnLabel(i);
				    Object colValue = "NULL";
				    try {
				    	if (meta.getColumnType(i)==Types.TIMESTAMP) {
				    		colValue = getTimestamp(rs, colName);
				    		
				    	} else if (meta.getColumnType(i)==Types.LONGVARBINARY) {
			    			colValue = (ByteArrayInputStream)rs.getBinaryStream(i);
			    			
				    	} else {
					        colValue = getString(rs, colName);
					        if (colValue!=null && colValue.equals("")) {
					            colValue = "&nbsp;";
					        }				    		
				    	}
				    }catch(Exception e) {
				        colValue = "Error!";
				    }
				    line.addElement(colValue);
				    
				    //for the first line, get the columns names...
				    if (response.size()==0){
				        response.addColumn(colName);				        
				    }
			    }
			    
			    response.add(line);
			} 
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


	private void setupPrepareStatement(DBQueryParam qp,	PreparedStatement pstmt) throws SQLException {
		
		if (qp!=null && qp.getTypes()!=null && qp.getParams()!=null && qp.getTypes().length==qp.getParams().size()) {
			
			int[] types = qp.getTypes();
			Vector<Object> params = qp.getParams();
			
			for (int i=1; i<=types.length; i++) {
				int t = types[i-1]; 
				Object p = params.elementAt(i-1);
				if (t==Types.TIMESTAMP) {
					pstmt.setTimestamp(i, (Timestamp)p);
				} else if (t==Types.INTEGER) {
					pstmt.setInt(i, ((Integer)p).intValue());
				} else if (t==Types.FLOAT) {
					pstmt.setFloat(i, ((Float)p).floatValue());
				} else if (t==Types.BLOB) {
					ByteArrayInputStream bai = new ByteArrayInputStream((byte[])p);
					pstmt.setBinaryStream(i, bai, ((byte[])p).length);
					
				} else {
					pstmt.setString(i, (String)p);
				}
			}
		}
	}
	

    /**
     * Execute statement into data base
     */
    private Vector executeQuery(String sql, DBQueryParam qp, Connection c) throws DataAccessException{
		Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			
			pstmt = c.prepareStatement(sql);
			if (qp!=null) {
				this.setupPrepareStatement(qp, pstmt);	
			}
			int rows = pstmt.executeUpdate();
			response.add(new Integer(rows));
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    

	public String getDBProductName() throws DataAccessException {
		String response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = c.getMetaData().getDatabaseProductName();
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
	}

	
	/**
	 *	If the sql contain a block with connection information, the system
	 *	must bypass the native connection and try to perform an external DB connection
	 */
	private Connection getConnectionBasedToSql(String sql) throws Exception{
		return getConnectionBasedToSql(sql, true);
	}
	
	public Connection getConnectionBasedToSql(String sql, boolean isAutoCommit) throws Exception{
		Connection response = null;
		
		if (sql.startsWith("[") && sql.indexOf("]", 2)>-1) {
			int end = sql.indexOf("]");
			String connectionString = sql.substring(1, end);
			String[] tokens = connectionString.split("\\|");
			if (tokens.length==4) {
				response = super.getJDBCConnection(tokens[0].trim(), tokens[1].trim(), 
										tokens[2].trim(), tokens[3].trim(), isAutoCommit);
			} else {
				throw new DataAccessException("An external connection could not be performed '" + connectionString + "'");
			}
		} else {
			response = getConnection(isAutoCommit);
		}
		
		return response;
	}
	
	
	public String removeConnStringFromSql(String sql) {
		String response = sql;
		if (sql.startsWith("[") && sql.indexOf("]", 2)>-1) {
			int end = sql.indexOf("]");
			response = sql.substring(end+1).trim();
		}
		return response;
	}
	
}
