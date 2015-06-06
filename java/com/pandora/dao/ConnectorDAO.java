package com.pandora.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;
import com.pandora.integration.Integration;
import com.pandora.integration.ResourceTaskAllocIntegration;
import com.pandora.integration.ResourceTaskIntegration;

/**
 * This class contain the methods to access information about 
 * many classes into integration process (Connector object)
 */
public class ConnectorDAO extends DataAccess {

    /**
     * Process a list of Integration objects
     */
    public boolean process(ArrayList<Integration> list) throws DataAccessException {
		Connection c = null;
		UserDelegate udel = new UserDelegate();
		UserTO uto = null;
		
		try {
			c = getConnection(false);
			
	        Iterator<Integration> i = list.iterator();
	        while(i.hasNext()){
	            Integration iobj = i.next();
	            if (iobj.isValid()) {
		            uto = udel.getObjectByUsername(new UserTO(iobj));
		            if (uto!=null) {
			            Integer trans = iobj.getTransaction();
			            
			            //create the native TO and the DAO object based on Integration object info
			            TransferObject to = this.getNativeTObject(iobj, uto);
			            DataAccess dao = this.getDBClass(iobj);
			            
			            //pre-processing for Updating and Deleting...
			        	if (trans.equals(Integration.TRANSACTION_UPDATE) || trans.equals(Integration.TRANSACTION_DELETE)) {
			        	    to = dao.getObject(to, c);    
			        	}

			        	if (to.getId()!=null) {
				            //populate data based on Integration object info and persisted info from database 		        	    
			        	    to.populate(iobj, uto);    
			        	} else {
			        	    if (trans.equals(Integration.TRANSACTION_UPDATE) || trans.equals(Integration.TRANSACTION_DELETE)) {
				        	    //if the object not exists into database, recovery the object data again and set
				        	    //the current transaction to INSERT
				        	    to = this.getNativeTObject(iobj, uto);
				        	    trans = Integration.TRANSACTION_INSERT;		        	        
			        	    } else {
			        	        throw new BusinessException("The object is invalid and cannot be inserted into database.");    	        
			        	    }
			        	}
			            
			            //post-processing...
			            if (trans.equals(Integration.TRANSACTION_INSERT)) {
			                dao.insert(to, c);
			            } else if (trans.equals(Integration.TRANSACTION_UPDATE)) {
			                dao.update(to, c);
			            } else if (trans.equals(Integration.TRANSACTION_DELETE)) {
			                dao.remove(to, c);
			            } else {
			                throw new BusinessException("The Transaction field of one of those Integration objects is invalid.");                
			            }
			            
		            } else {
		                throw new BusinessException("The user used to save data is invalid.");
		            }	            	
	            } else {
	                throw new BusinessException("The session authentication failed. Try to open a new session or (if necessary) check the password.");
	            }
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
        
        return true;
    }
 
    
    /**
     * Return the native transfer object based on Integration object info
     * @param iobj
     * @param uto
     * @return
     * @throws BusinessException
     */
    private TransferObject getNativeTObject(Integration iobj, UserTO uto) throws BusinessException{
        TransferObject response = null;
        
        if (iobj instanceof ResourceTaskAllocIntegration) {
            response = new ResourceTaskAllocTO();
                        
        } else if (iobj instanceof ResourceTaskIntegration){
            response = new ResourceTaskTO();
            
        } else {
            throw new BusinessException("The Integration object didn't match with any native transfer object of system");
        }

        response.populate(iobj, uto);
        
        return response;
    }
    
    
    /**
     * Return the DAO object based on Integration object info
     * @param iobj
     * @return
     * @throws BusinessException
     */
    private DataAccess getDBClass(Integration iobj) throws BusinessException{
        DataAccess response = null;
        
        if (iobj instanceof ResourceTaskAllocIntegration) {
            response = new ResourceTaskAllocDAO();
            
        } else if (iobj instanceof ResourceTaskIntegration){
            response = new ResourceTaskDAO();
            
        } else {
            throw new BusinessException("The Integration object didn't match with any DataAccess object of system");
        }

        return response;
    }    
}
