package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.DbForm;

/**
 * 
 */
public class DbAction extends GeneralStrutsAction {

    /**
     * Show form.
     */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showDB";
	    return mapping.findForward(forward);
	}



	/**
	 * Perform the sql into data base, and show a list of data into form.
	 */
	public ActionForward performQuery(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    
	    String forward = "showDB";

	    try {
	        DbForm frm = (DbForm)form;
	        this.clearMessages(request);
	        String sql = frm.getSql();
	        
	        if (sql.toUpperCase().indexOf("UPDATE")>-1 || 
	        		sql.toUpperCase().indexOf("INSERT")>-1 || 
	        			sql.toUpperCase().indexOf("DELETE")>-1){
	        	executeQuery(sql, frm);
	        } else {
	        	performQuery(sql, frm);
	        }

        } catch (BusinessException e) {
            this.setErrorFormSession(request, "error.performDBForm", e);            
        }

	    return mapping.findForward(forward);
	}
	
	
	private void performQuery(String sql, DbForm frm) throws BusinessException{
	    DbQueryDelegate del = new DbQueryDelegate();
	    Vector list = null;
	    list = del.performQuery(sql);
        StringBuffer result = formatOutput(list);
        frm.setResult(result.toString());	    
	}
	

	private void executeQuery(String sql, DbForm frm) throws BusinessException{
	    String[] queryList = sql.split(";");
	    DbQueryDelegate del = new DbQueryDelegate();
	    for (int i=0; i< queryList.length; i++) {
	        del.executeQuery(queryList[i]);    
	    }
	    frm.setResult("<table width=\"95%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><center>EXECUTED.</center><td></tr></table>");
	}
	
	/**
	 * write data in html table format. 
	 * @param list
	 */
	private StringBuffer formatOutput(Vector list){
	    StringBuffer response = new StringBuffer();
	    
	    response.append("<table width=\"95%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
	    Iterator i = list.iterator();
	    while(i.hasNext()){
	        response.append("\n<tr>");
	        Vector line = (Vector)i.next();
	        Iterator j = line.iterator();
	        while(j.hasNext()){
	        	String cell = "";
	        	Object c = (Object)j.next();
	        	if (c!=null) {
	        		cell = c.toString();	
	        	} else {
	        		cell = "NULL";
	        	}
	            response.append("<td class=\"tableCell\">" + cell + "</td>");
	        }
	        response.append("</tr>");
	    }
	    response.append("\n</table>");
	    
	    return response;
	}
	
}
