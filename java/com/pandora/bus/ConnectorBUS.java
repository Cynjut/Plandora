package com.pandora.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;
import com.pandora.dao.ConnectorDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.integration.Integration;
import com.pandora.bus.PasswordEncrypt;

/**
 * This class contain the business rules related with Connector Integration entity.
 */
public class ConnectorBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    ConnectorDAO dao = new ConnectorDAO();
    
    /**
     * Process a list of Integration objects
     */
    public boolean process(HttpServletRequest request, ArrayList list) throws BusinessException {
        boolean response = false;
        try {
        	Object obj = request.getSession().getServletContext().getAttribute("com.plandora.connector");
        	if (obj!=null) {
        		HashMap hmContext = (HashMap)obj;
        		
    	        Iterator i = list.iterator();
    	        while(i.hasNext()){
    	            Integration iobj = (Integration)i.next();
    	            
    	            String publicKey = iobj.getPassword();
    	            publicKey = publicKey.replaceAll("\\+", " ");
    	            
		            //checks authentication...    	            
    	            Object pass = hmContext.get(publicKey);
    	            iobj.setValid(pass!=null);
    	        }        	
    	        
    	        response = dao.process(list);
        	} else {
        		throw new BusinessException("The session authentication failed. Try to open a new session or (if necessary) check the password.");
        	}
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    } 
    
    
    public String generatePublicKey(HttpServletRequest request, UserTO uto) throws BusinessException {
		String content = request.getSession().getId() + ":" + uto.getId();
		String encrypted = PasswordEncrypt.getInstance().encrypt(content);
		encrypted = encrypted.replaceAll("\\+", " ");
			
		//insert the new encrypted content into application context
		HashMap hmContext = new HashMap(); 
		Object obj = request.getSession().getServletContext().getAttribute("com.plandora.connector");
		if (obj!=null) {
			hmContext = (HashMap)obj;
		}
		hmContext.put(encrypted, "OK");
		request.getSession().getServletContext().setAttribute("com.plandora.connector", hmContext);
		
		return encrypted;
    }
    
}
