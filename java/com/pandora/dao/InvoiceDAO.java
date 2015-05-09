package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceItemTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.InvoiceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class InvoiceDAO extends PlanningDAO {

	
	public Vector<InvoiceTO> getInvoiceList(String pid) throws DataAccessException {
        Vector<InvoiceTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getInvoiceList(pid, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	} 

	
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		InvoiceHistoryDAO ihdao = new InvoiceHistoryDAO();
		try {
		    InvoiceTO ito = (InvoiceTO)to;
		    ito.setId(this.getNewId());
		    ito.setCreationDate(DateUtil.getNow());

		    //insert data into parent entity (PlanningDAO)
		    super.insert(ito, c);
		    
		    pstmt = c.prepareStatement("insert into invoice (id, name, project_id, " +
		    							"category_id, due_date, invoice_status_id, " +
		    							"invoice_date, invoice_number, purchase_order, contact) " +
										"values (?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, ito.getId());
			pstmt.setString(2, ito.getName());
			pstmt.setString(3, ito.getProject().getId());
			pstmt.setString(4, ito.getCategory().getId());
			pstmt.setTimestamp(5, ito.getDueDate());
			pstmt.setString(6, ito.getInvoiceStatus().getId());
			pstmt.setTimestamp(7, ito.getInvoiceDate());
			pstmt.setString(8, ito.getInvoiceNumber());
			pstmt.setString(9, ito.getPurchaseOrder());
			pstmt.setString(10, ito.getContact());
			pstmt.executeUpdate();
			
			Iterator i = ito.getItemsList().iterator();
			while(i.hasNext()) {
				InvoiceItemTO iito = (InvoiceItemTO)i.next();
				iito.setInvoice(ito);				
				this.insertInvoiceItem(iito, c);
			}
			
			InvoiceHistoryTO ihto = new InvoiceHistoryTO(ito);
			ihdao.insert(ihto, c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    

    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		InvoiceHistoryDAO ihdao = new InvoiceHistoryDAO();		
		try {
			
		    InvoiceTO ito = (InvoiceTO)to;
		    
			super.update(to, c);
		    
		    pstmt = c.prepareStatement("update invoice set name=?, project_id=?, " +
									"category_id=?, due_date=?, invoice_status_id=?, " +
									"invoice_date=?, invoice_number=?, purchase_order=?, contact=? " +
		    						"where id=?");
			pstmt.setString(1, ito.getName());
			pstmt.setString(2, ito.getProject().getId());
			pstmt.setString(3, ito.getCategory().getId());
			pstmt.setTimestamp(4, ito.getDueDate());
			pstmt.setString(5, ito.getInvoiceStatus().getId());
			pstmt.setTimestamp(6, ito.getInvoiceDate());
			pstmt.setString(7, ito.getInvoiceNumber());
			pstmt.setString(8, ito.getPurchaseOrder());
			pstmt.setString(9, ito.getContact());
		    pstmt.setString(10, ito.getId());
			pstmt.executeUpdate(); 
			
			InvoiceHistoryTO ihto = new InvoiceHistoryTO(ito);
			ihdao.insert(ihto, c);
			
			
			if (ito.getItemsToBeUpdated()!=null && ito.getItemsList()!=null) {
				Object[] list = ito.getItemsToBeUpdated().toArray();
				for (int i=0 ; i<list.length; i++) {
					Iterator j = ito.getItemsList().iterator();
					while(j.hasNext()) {
						InvoiceItemTO it = (InvoiceItemTO)j.next();
						if (list[i].equals(it.getId())) {
							this.updateInvoiceItem(it, c);
						}						
					}
				}							
			}

			//remove items if necessary
			if (ito.getItemsToBeRemoved()!=null && ito.getItemsList()!=null) {
				Object[] list = ito.getItemsToBeRemoved().toArray();
				for (int i=0 ; i<list.length; i++) {
					this.removeInvoiceItem(ito.getId(), (String)list[i], c);
				}							
			}

			//insert new items if necessary
			Iterator i = ito.getItemsList().iterator();
			while(i.hasNext()) {
				InvoiceItemTO it = (InvoiceItemTO)i.next();
				if (it.getId().startsWith("NEW_")) {
					it.setInvoice(ito);				
					this.insertInvoiceItem(it, c);					
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        InvoiceTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			InvoiceTO ito = (InvoiceTO)to;
			pstmt = c.prepareStatement("select i.id, i.name, i.project_id, i.category_id, i.due_date, i.invoice_status_id, " +
											"i.invoice_date, i.invoice_number, i.purchase_order, i.contact, p.description, " +
											"p.creation_date, p.final_date, pr.name as PROJECT_NAME, isn.name as INVOICE_STATUS, " +
											"c.name as CATEGORY_NAME " +
									   "from invoice i, planning p, project pr, invoice_status isn, category c " +
									   "where i.id = p.id and i.project_id = pr.id " +
									     "and i.invoice_status_id = isn.id and i.category_id = c.id " +
									     "and i.id = ?");			
			pstmt.setString(1, ito.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			    response.setItemsList(this.getItemList(response, c));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    InvoiceTO ito = (InvoiceTO)to;
		    
			pstmt = c.prepareStatement("delete from invoice_history where invoice_id=?");
			pstmt.setString(1, ito.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from invoice where id=?");
			pstmt.setString(1, ito.getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    
    private void insertInvoiceItem(InvoiceItemTO iito, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (iito.getId().startsWith("NEW_")) {
				iito.setId(this.getNewId());
			}
			
			pstmt = c.prepareStatement("insert into invoice_item (invoice_id, invoice_item_id, " +
									        "type, price, amount, type_index, name) values (?,?,?,?,?,?,?)");
			pstmt.setString(1, iito.getInvoice().getId());
			pstmt.setString(2, iito.getId());
			pstmt.setInt(3, iito.getType().intValue());
			pstmt.setInt(4, iito.getPrice().intValue());
			pstmt.setInt(5, iito.getAmount().intValue());
			pstmt.setInt(6, iito.getTypeIndex().intValue());
			pstmt.setString(7, iito.getItemName());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}               
    }
    
    private void updateInvoiceItem(InvoiceItemTO iito, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("update invoice_item set type=?, price=?, amount=?, " +
									   "type_index=?, name=? where invoice_id=? and invoice_item_id=?");
			pstmt.setInt(1, iito.getType().intValue());
			pstmt.setInt(2, iito.getPrice().intValue());
			pstmt.setInt(3, iito.getAmount().intValue());
			pstmt.setInt(4, iito.getTypeIndex().intValue());
			pstmt.setString(5, iito.getItemName());
			pstmt.setString(6, iito.getInvoice().getId());
			pstmt.setString(7, iito.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}               
    }    
    
    private void removeInvoiceItem(String invoiceId, String invoiceItemId, Connection c) throws DataAccessException{
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("delete from invoice_item where invoice_id=? and invoice_item_id=?");
			pstmt.setString(1, invoiceId);
			pstmt.setString(2, invoiceItemId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}                   	
    }
    
    
    private Vector getItemList(InvoiceTO ito, Connection c) throws DataAccessException {
    	Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 		
		try {
			pstmt = c.prepareStatement("select invoice_id, invoice_item_id, type, price, amount, type_index, name " +
									   "from invoice_item where invoice_id = ?");			
			pstmt.setString(1, ito.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response.add(this.populateItemByResultSet(rs, ito));
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}


    private Vector<InvoiceTO> getInvoiceList(String projectId, Connection c) throws DataAccessException {
    	Vector<InvoiceTO> response = new Vector<InvoiceTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 		
		try {
			pstmt = c.prepareStatement("select i.id, i.name, i.project_id, i.category_id, i.due_date, i.invoice_status_id, " +
									"i.invoice_date, i.invoice_number, i.purchase_order, i.contact, p.description, " +
									"p.creation_date, p.final_date, pr.name as PROJECT_NAME, isn.name as INVOICE_STATUS, " +
									"c.name as CATEGORY_NAME, isn.state_machine_order " +
									"from invoice i, planning p, project pr, invoice_status isn, category c " +
									"where i.id = p.id and i.project_id = pr.id " +
									"and i.invoice_status_id = isn.id and i.category_id = c.id " +
			     					"and i.project_id = ?");			
			pstmt.setString(1, projectId);
			rs = pstmt.executeQuery();
			while (rs.next()){
				InvoiceTO ito = this.populateBeanByResultSet(rs);
				ito.setItemsList(this.getItemList(ito, c));
				
				InvoiceStatusTO isto = ito.getInvoiceStatus();
				isto.setStateMachineOrder(getInteger(rs, "isn.state_machine_order"));
				
				response.add(ito);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}


    private InvoiceItemTO populateItemByResultSet(ResultSet rs, InvoiceTO ito) throws DataAccessException{
    	InvoiceItemTO response = new InvoiceItemTO();
        response.setInvoice(ito);
        response.setId(getString(rs, "invoice_item_id"));        
        response.setAmount(getInteger(rs, "amount"));
        response.setPrice(getInteger(rs, "price"));
        response.setType(getInteger(rs, "type"));
        response.setTypeIndex(getInteger(rs, "type_index"));
        response.setItemName(getString(rs, "name"));
        return response;
    }
    
    
	private InvoiceTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
    	InvoiceTO response = new InvoiceTO();
        response.setId(getString(rs, "id"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        
        response.setCategory(new CategoryTO(getString(rs, "category_id")));
        response.setContact(getString(rs, "contact"));
        response.setDueDate(getTimestamp(rs, "due_date"));
        response.setInvoiceDate(getTimestamp(rs, "invoice_date"));
        response.setInvoiceNumber(getString(rs, "invoice_number"));
        response.setInvoiceStatus(new InvoiceStatusTO(getString(rs, "invoice_status_id")));
        response.setName(getString(rs, "name"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setPurchaseOrder(getString(rs, "purchase_order"));
        
        CategoryTO cto = response.getCategory();
        cto.setName(getString(rs, "CATEGORY_NAME"));
        
        InvoiceStatusTO isto = response.getInvoiceStatus();
        isto.setName(getString(rs, "INVOICE_STATUS"));
        
        String projName = getString(rs, "PROJECT_NAME");
        ProjectTO pto = response.getProject();
        pto.setName(projName);
        
        return response;
    }

	
}
