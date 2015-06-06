package com.pandora.imp;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class CostCsvExportBUS extends ExportBUS {

	@Override
	public String getFileName(ProjectTO pto) throws BusinessException {
        return "project_costs.csv";
	}

	@Override
	public String getContentType() throws BusinessException {
        return "text/csv; charset=ISO-8859-1";
	}
	
	@Override	
    public String getEncoding(){
    	return "ISO-8859-1";
    }	

	@Override
	public StringBuffer getHeader(ProjectTO pto, Vector fields) throws BusinessException {
    	return new StringBuffer("\"expense_id\";\"category\";\"name\";\"project_name\";\"username\";\"status\";\"due_date\";\"value\"");
	}

	@Override
	public StringBuffer getFooter(ProjectTO pto, Vector fields)	throws BusinessException {
        return new StringBuffer("");
	}

	@Override
    public String getLabel() throws BusinessException {
        return "label.importExport.costCsvExport";
    }

	@Override
	public Vector<FieldValueTO> getFields() throws BusinessException {
    	Vector<FieldValueTO> list = new Vector<FieldValueTO>();
	    CategoryDelegate cdel = new CategoryDelegate();
	    
    	FieldValueTO iniDate = new FieldValueTO("INI_DATE", "label.importExport.inidate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(iniDate);
    	
    	FieldValueTO finalDate = new FieldValueTO("FINAL_DATE", "label.importExport.enddate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(finalDate);

    	Vector categoryList = new Vector(); 
		String allLbl = handler.getBundle().getMessage("label.all2", handler.getLocale());
		CategoryTO all = new CategoryTO("-1");
		all.setGenericTag(allLbl);
		categoryList.addElement(all);			
    	
		Vector categoryListFrmDB = cdel.getCategoryListByType(CategoryTO.TYPE_COST, new ProjectTO(""), false);
		if (categoryListFrmDB!=null) {
			Iterator i = categoryListFrmDB.iterator();
			while(i.hasNext()) {
				CategoryTO cto = (CategoryTO)i.next();
				cto.setGenericTag(cto.getName());
				categoryList.add(cto);
			}
		}
		
		FieldValueTO catList = new FieldValueTO("CATEGORIES", "label.costedit.category", categoryList);
    	list.add(catList);
    	
    	return list;
	}
	

	@Override
    public String getUniqueName() {
        return "COST_CSV_EXPORT";
    }
    
    
	@Override
	public StringBuffer getBody(ProjectTO pto, UserTO handler, Vector fields) throws BusinessException {
        StringBuffer response = new StringBuffer("");
        DbQueryDelegate dbq = new DbQueryDelegate();

    	if (!handler.isLeader(pto)) {
    		throw new BusinessException("Sorry. To perform this feature it is mandatory to be the leader of the project [ " + pto.getName() + "]");
    	}
    	
        Locale loc = handler.getLocale();
    	
		FieldValueTO field1 = (FieldValueTO)fields.elementAt(0);
    	Timestamp iniDate = DateUtil.getDateTime(field1.getCurrentValue(), handler.getCalendarMask(), loc);
    	
		FieldValueTO field2 = (FieldValueTO)fields.elementAt(1);    	
    	Timestamp finalDate = DateUtil.getDateTime(field2.getCurrentValue(),  handler.getCalendarMask(), loc);	    
        
    	FieldValueTO field3 = (FieldValueTO)fields.elementAt(2);
    	String categoryId = field3.getCurrentValue();
    	
    	
    	
	    if (finalDate.equals(iniDate) || finalDate.after(iniDate) ){
	        
	    	String catwhere = "and c.category_id=?";
			int[] types = new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP};
	    	Vector<Object> param = new Vector<Object>();
	    	param.add(categoryId);
	    	param.add(iniDate);
	    	param.add(finalDate);
	    	if (categoryId!=null && categoryId.equals("-1")) {
		    	catwhere = "";	    		
	    		types = new int[]{Types.TIMESTAMP, Types.TIMESTAMP};
		    	param = new Vector<Object>();
		    	param.add(iniDate);
		    	param.add(finalDate);		    	
	    	}
	    	
	    	String sql =  "select c.expense_id, cat.name as category, c.name, p.name as project_name, u.username, cs.name as status, i.due_date, i.value/100 " +
	    			      "from project p, category cat,  " +
			    	      "cost c left outer join expense e on (e.id = c.expense_id) " + 
			    	                 "left outer join tool_user u on (u.id = e.user_id), cost_installment i, cost_status cs " +
					      "where c.project_id  = p.id and i.cost_id = c.id and i.cost_status_id = cs.id" +
					    	    " and c.category_id = cat.id " + catwhere +
					    	    " and i.due_date >= ? and i.due_date <= ?";
	    	Vector<Vector<Object>> data = dbq.performQuery(sql, types, param);
	    	
		    response = this.getCsv(data);
		    
	    } else {
	    	throw new BusinessException("The final date cannot be earlier than initial date.");
	    }
        
    	return response;
    }

	    
    private StringBuffer getCsv(Vector<Vector<Object>> data) {
    	StringBuffer response = new StringBuffer("");
    	boolean skipTitle = true;
    	if (data!=null) {
    		for (Vector<Object> line : data) {

    			if (skipTitle) {
    				skipTitle = false;
    			} else {
        			String content = "";
    				for (Object cell : line) {
    					if (!content.equals("")) {
    						content = content + ";";	
    					} else {
    						content = content + "\n";
    					}
    					if (cell==null || (cell!=null && (cell+"").trim().equalsIgnoreCase("null") )) {
    						cell = "";
    					}
    					if (cell!=null && (cell+"").equals("&nbsp;")) {
    						cell = "";
    					}
    					content = content + "\"" + cell + "\"";
    				}
    				response.append(content);
    			}
			}
    	}
    	return response;
    }

}
