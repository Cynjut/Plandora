package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.InvoiceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

public class InvoiceHistoryDAO extends DataAccess {

	public Vector<InvoiceHistoryTO> getListByInvoice(String invId) throws DataAccessException {
        Vector<InvoiceHistoryTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByInvoice(invId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
	}

	
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    InvoiceHistoryTO ihto = (InvoiceHistoryTO)to;

			pstmt = c.prepareStatement("insert into invoice_history (invoice_id, creation_date, name, " +
												"category_id, invoice_status_id, due_date, " +
												"invoice_date, invoice_number, purchase_order, contact, " +
												"description, user_id, total_price) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, ihto.getInvoice().getId());
			pstmt.setTimestamp(2, ihto.getCreationDate());
			pstmt.setString(3, ihto.getName());
			pstmt.setString(4, ihto.getCategory().getId());
			pstmt.setString(5, ihto.getInvoiceStatus().getId());
			pstmt.setTimestamp(6, ihto.getDueDate());
			if (ihto.getInvoiceDate()!=null){
			    pstmt.setTimestamp(7, ihto.getInvoiceDate());    
			} else {
			    pstmt.setNull(7, java.sql.Types.TIMESTAMP);
			}
		    pstmt.setString(8, ihto.getInvoiceNumber());
		    pstmt.setString(9, ihto.getPurchaseOrder());
		    pstmt.setString(10, ihto.getContact());
		    pstmt.setString(11, ihto.getDescription());
		    pstmt.setString(12, ihto.getHandler().getId());
			if (ihto.getTotalPrice()!=null){
			    pstmt.setLong(13, ihto.getTotalPrice().longValue());    
			} else {
			    pstmt.setNull(13, java.sql.Types.BIGINT);
			}
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}       
    }
    
    
    private Vector<InvoiceHistoryTO> getListByInvoice(String invId, Connection c) throws DataAccessException{
        Vector<InvoiceHistoryTO> response= new Vector<InvoiceHistoryTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    String sql = "select h.invoice_id, h.creation_date, h.name, h.category_id, h.invoice_status_id, h.due_date, " +
		    		     "h.invoice_date, h.invoice_number, h.purchase_order, h.contact, h.description, " +
		    		     "h.total_price, h.user_id, ivs.name as STATUS_NAME, c.name as CATEGORY_NAME, u.username as USERNAME " +
		    			 "from invoice_history h, invoice_status ivs, tool_user u, category c " +
		    			 "where h.invoice_status_id = ivs.id " +
		    			   "and h.user_id = u.id and h.category_id = c.id " +
		    			   "and h.invoice_id = ? " +
		    			 "order by h.creation_date";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, invId);
			rs = pstmt.executeQuery();						
			while (rs.next()){
			    InvoiceHistoryTO ihto = this.populateByResultSet(rs);
				response.addElement(ihto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private InvoiceHistoryTO populateByResultSet(ResultSet rs) throws DataAccessException{
    	InvoiceHistoryTO response = new InvoiceHistoryTO();
    	
        InvoiceStatusTO isto = new InvoiceStatusTO(getString(rs, "invoice_status_id"));
    	isto.setName(getString(rs, "STATUS_NAME"));
        
    	CategoryTO cto = new CategoryTO(getString(rs, "category_id"));
        cto.setName(getString(rs, "CATEGORY_NAME"));
        
        InvoiceTO ito = new InvoiceTO(getString(rs, "invoice_id"));
        UserTO uto = new UserTO(getString(rs, "user_id"));
        uto.setUsername(getString(rs, "USERNAME"));

        response.setCategory(cto);
        response.setContact(getString(rs, "contact"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setDescription(getString(rs, "description"));
        response.setDueDate(getTimestamp(rs, "due_date"));
        response.setHandler(uto);
        response.setInvoice(ito);
        response.setInvoiceDate(getTimestamp(rs, "invoice_date"));
        response.setInvoiceNumber(getString(rs, "invoice_number"));
        response.setInvoiceStatus(isto);
        response.setName(getString(rs, "name"));
        response.setPurchaseOrder(getString(rs, "purchase_order"));
        response.setTotalPrice(getLong(rs, "total_price"));

        return response;
    }    
    
	
}
