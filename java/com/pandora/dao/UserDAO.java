package com.pandora.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.AreaTO;
import com.pandora.CustomerTO;
import com.pandora.DepartmentTO;
import com.pandora.FunctionTO;
import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 * This class contain all methods to handle data related with User entity into data base.
 */
public class UserDAO extends DataAccess {


    /**
     * Get a list of all User TOs from data base.
     */
	public Vector<UserTO> getList(boolean hideDisable) throws DataAccessException{
		Vector<UserTO> response = null;
		Connection c = null;
		try {
			c = getConnection();
			response = this.getList(hideDisable, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}		
		return response;
	}	
	
    private Vector<UserTO> getList(boolean hideDisable, Connection c) throws DataAccessException {
		Vector<UserTO> response = new Vector<UserTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String hideWhere = "";
			if (hideDisable) {
				hideWhere = "where final_date is null ";
			}
			
			pstmt = c.prepareStatement("select id, username, color, email, name, " +
					   "phone, password, department_id, area_id, function_id, country, language, " +
					   "birth, auth_mode, permission, pic_file, final_date " +
					   "from tool_user " + hideWhere + "order by username asc");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    UserTO uto = this.populateUserByResultSet(rs);
			    uto.setPreference(this.getPreferences(uto, c));
			    response.addElement(uto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }    

    
    /**
     * Get a specific User TO from data base, based on id.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
		UserTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    //select a user from database
		    UserTO filter = (UserTO)to;
			pstmt = c.prepareStatement("select id, username, color, email, name, " +
									   "phone, password, department_id, area_id, function_id, country, " +
									   "language, birth, auth_mode, permission, pic_file, final_date " +
									   "from tool_user where id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
						
			//Prepare data to return
			if (rs.next()){
				response = this.populateUserByResultSet(rs);
				response.setPreference(this.getPreferences(response, c));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Insert a new user into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		CustomerDAO cdao = new CustomerDAO();
		try {
		    
		    UserTO uto = (UserTO)to;
			uto.setId(this.getNewId());
					    
			pstmt = c.prepareStatement("insert into tool_user (id, username, color, email, name, " +
									   "phone, department_id, area_id, function_id, " +
									   "country, language, birth, auth_mode, permission, pic_file, final_date) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, uto.getId());
			pstmt.setString(2, uto.getUsername());
			pstmt.setString(3, uto.getColor());
			pstmt.setString(4, uto.getEmail());
			pstmt.setString(5, uto.getName());
			pstmt.setString(6, uto.getPhone());
			pstmt.setString(7, uto.getDepartment().getId());
			pstmt.setString(8, uto.getArea().getId());
			pstmt.setString(9, uto.getFunction().getId());
			pstmt.setString(10, uto.getCountry());
			pstmt.setString(11, uto.getLanguage());
			pstmt.setDate(12, uto.getBirth());
			if (uto.getAuthenticationMode()!=null) {
				pstmt.setString(13, uto.getAuthenticationMode());			    
			} else {
			    pstmt.setNull(13, java.sql.Types.VARCHAR);
			}
			pstmt.setString(14, uto.getPermission());
			if (uto.getFileInBytes()!=null && uto.getFileInBytes().length > 0) {
			    pstmt.setBinaryStream(15, uto.getBinaryFile(), uto.getFileInBytes().length);    
			} else {
			    pstmt.setNull(15, java.sql.Types.BINARY);
			}
			if (uto.getFinalDate()!=null) {
				pstmt.setTimestamp(16, uto.getFinalDate());			    
			} else {
			    pstmt.setNull(16, java.sql.Types.TIMESTAMP);
			}
			
			pstmt.executeUpdate();

			//create a new customer for current user, and related with System Project Root
			CustomerTO cto = new CustomerTO(uto);
			cto.setProject(new ProjectTO(ProjectTO.PROJECT_ROOT_ID));
			cdao.insert(cto, c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    
    /**
     * Update information of user into data base.
     */    
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    UserTO uto = (UserTO)to;
			pstmt = c.prepareStatement("update tool_user set username=?, color=?, email=?, " +
									   "name=?, phone=?, department_id=?, " +
									   "area_id=?, function_id=?, country=?, language=?, birth=?, " +
									   "auth_mode=?, permission=?, pic_file=?, final_date=? " +
									   "where id=?");
			pstmt.setString(1, uto.getUsername());
			pstmt.setString(2, uto.getColor());
			pstmt.setString(3, uto.getEmail());
			pstmt.setString(4, uto.getName());
			pstmt.setString(5, uto.getPhone());
			pstmt.setString(6, uto.getDepartment().getId());
			pstmt.setString(7, uto.getArea().getId());
			pstmt.setString(8, uto.getFunction().getId());
			pstmt.setString(9, uto.getCountry());
			pstmt.setString(10, uto.getLanguage());
			pstmt.setDate(11, uto.getBirth());
			if (uto.getAuthenticationMode()!=null) {
				pstmt.setString(12, uto.getAuthenticationMode());			    
			} else {
			    pstmt.setNull(12, java.sql.Types.VARCHAR);
			}
			pstmt.setString(13, uto.getPermission());
			if (uto.getFileInBytes()!=null && uto.getFileInBytes().length > 0) {
			    pstmt.setBinaryStream(14, uto.getBinaryFile(), uto.getFileInBytes().length);    
			} else {
			    pstmt.setNull(14, java.sql.Types.BINARY);
			}
			if (uto.getFinalDate()!=null) {
				pstmt.setTimestamp(15, uto.getFinalDate());			    
			} else {
			    pstmt.setNull(15, java.sql.Types.TIMESTAMP);
			}
			pstmt.setString(16, uto.getId());
			
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }

    
    /**
     * Update the password of user into database
     */
    public void updatePassword(UserTO uto) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.updatePassword(uto, c);				
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
    }
    
    
    /**
     * Update the password of user into database
     */
    private void updatePassword(UserTO uto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("update tool_user set password=? where id=?");
			pstmt.setString(1, uto.getPassword());
			pstmt.setString(2, uto.getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }    
    
    
    /**
     * Remove an user of data base.
     */    
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    UserTO uto = (UserTO)to;
		    
		    //if user is not linked with any project (except root project), remove it from customer table
		    ProjectDAO pdao = new ProjectDAO(); 
		    Vector v = pdao.getProjectAllocation(uto, null, c);
		    if (v.size()==0){
		        CustomerDAO cdao = new CustomerDAO();
		        CustomerTO cto = new CustomerTO(uto.getId());
		        cto.setProject(new ProjectTO(ProjectTO.PROJECT_ROOT_ID));
		        cdao.remove(cto, c);
		    } else {
		        throw new DataAccessException("The user cannot be removed because it is allocated into one or more projects.");    
		    }
		    
			pstmt = c.prepareStatement("DELETE from tool_user WHERE ID=?");
			pstmt.setString(1, uto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }
    
    
    /**
     * This method get a User TO from BD based on username.
     */
    public UserTO getObjectByUsername(UserTO uto) throws DataAccessException{
        UserTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByUsername(uto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    

    /**
     * This method get a User TO from BD based on username.
     */
    private UserTO getObjectByUsername(UserTO uto, Connection c) throws DataAccessException{
		UserTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
			pstmt = c.prepareStatement("select id, username, color, email, name, " +
									   "phone, password, department_id, area_id, function_id, country, language, " +
									   "birth, auth_mode, permission, pic_file, final_date " +
									   "from tool_user where username = ?");
			pstmt.setString(1, uto.getUsername());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateUserByResultSet(rs);
				response.setPreference(this.getPreferences(response, c));
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Search into data base a list of user objects based on a filter 
     * related with username and name fields.
     */
    public Vector getListByKeyword(Vector kwList) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByKeyword(kwList, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Search into data base a list of user objects based on a filter 
     * related with username and name fields.
     */
    private Vector getListByKeyword(Vector kwList, Connection c) throws DataAccessException{
        Vector response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    //concat keywords...
		    Vector vfields = new Vector();
		    vfields.addElement("USERNAME");
		    vfields.addElement("NAME");
		    String wc = StringUtil.getSQLKeywordsByFields(kwList, vfields);

		    //select a user from database
			pstmt = c.prepareStatement("select id, username, color, email, name, " +
									   "phone, password, department_id, area_id, function_id, country, language, " +
									   "birth, auth_mode, permission, pic_file, final_date " +
									   "from tool_user " +
									   "where username <> 'root' and final_date is null " +
									   "and (" + wc + ") order by name");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    UserTO uto = this.populateUserByResultSet(rs);
			    uto.setPreference(this.getPreferences(uto, c));
			    if (response==null) response = new Vector();
				response.addElement(uto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public Vector getUserByLeaderInAllProjects(LeaderTO eto, int role) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getUserByLeaderInAllProjects(eto, role, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }    
    

    private Vector getUserByLeaderInAllProjects(LeaderTO eto, int role, Connection c) throws DataAccessException{
        Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {

			String whereClause = "";
			String roleTable = "";			
			if (role==RootTO.ROLE_RESOURCE) {
				roleTable = "resource";
			} else if (role==RootTO.ROLE_CUSTOMER) {
				roleTable = "customer";
			} else if (role==RootTO.ROLE_LEADER) {
				roleTable = "leader";
			}
			whereClause = "and id in (select distinct id from " + roleTable + " where project_id in (select project_id from leader where id = ?))";

			pstmt = c.prepareStatement("select id, username, color, email, name, " +
					   "phone, password, department_id, area_id, function_id, country, language, " +
					   "birth, auth_mode, permission, pic_file, final_date " +
					   "from tool_user " +
					   "where username <> 'root' and final_date is null " + whereClause + 
					   "order by name");
			
			pstmt.setString(1, eto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateUserByResultSet(rs));
			}

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
		    super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }
    
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected UserTO populateUserByResultSet(ResultSet rs) throws DataAccessException{        
        UserTO response = new UserTO();
        DepartmentTO dto = new DepartmentTO();
        AreaTO ato = new AreaTO();
        FunctionTO fto = new FunctionTO();
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
        
        response.setId(getString(rs, "id"));
        response.setUsername(getString(rs, "username"));
        response.setGenericTag(response.getUsername());
        response.setColor(getString(rs, "color"));
        response.setEmail(getString(rs, "email"));
        response.setName(getString(rs, "name"));
        response.setPhone(getString(rs, "phone"));
        response.setPassword(getString(rs, "password"));
        response.setCountry(getString(rs, "country"));
        response.setLanguage(getString(rs, "language"));
        response.setPermission(getString(rs, "permission"));
        Date birth = getDate(rs, "birth");
        if (birth!=null) {
            response.setBirth(birth);    
        }
        response.setAuthenticationMode(getString(rs, "auth_mode"));
        
        dto.setId(getString(rs, "department_id"));
        response.setDepartment(dto);
        
        ato.setId(getString(rs, "area_id"));
        response.setArea(ato);
        
        fto.setId(getString(rs, "function_id"));
        response.setFunction(fto);

        Timestamp finalDate = getTimestamp(rs, "final_date");
        response.setFinalDate(finalDate);
        
        try {
            bis = (ByteArrayInputStream)rs.getBinaryStream("pic_file");
            response.setBinaryFile(bis);
            if (bis!=null) {
        	    int bytesRead = 0;
        	    byte[] buffer = new byte[bis.available()];  
        	    bos = new ByteArrayOutputStream();
        	    while ((bytesRead = bis.read(buffer)) != -1) {
        	        bos.write(buffer, 0, bytesRead);
        	    }
        	    response.setFileInBytes(buffer);            	
            }

		} catch (Exception e) {
		    response.setFileInBytes(null);
		    response.setBinaryFile(null);

        }finally{
            try {
                if (bis!=null) bis.close();
                if (bos!=null) bos.close();
            }catch (Exception e) {
            	e.printStackTrace();
            }
		}

        
        return response;
    }

    
    /**
     * Get from data base all preferences occurrences based on user object.
     */
    private PreferenceTO getPreferences(UserTO uto, Connection c) throws DataAccessException{
        PreferenceDAO pdao = new PreferenceDAO();
        return pdao.getObjectByUser(uto, c);
    }

}
