package com.pandora.imp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class KPICsvExportBUS extends ExportBUS {


	/* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFields()
     */
    public Vector<FieldValueTO> getFields() throws BusinessException {    	
    	Vector<FieldValueTO> list = new Vector<FieldValueTO>();
	    CategoryDelegate cdel = new CategoryDelegate();
	    
	    
    	FieldValueTO iniDate = new FieldValueTO("INI_DATE", "label.importExport.inidate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(iniDate);
    	
    	
    	FieldValueTO finalDate = new FieldValueTO("FINAL_DATE", "label.importExport.enddate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(finalDate);

    	
		Vector categoryListFrmDB = cdel.getCategoryListByType(CategoryTO.TYPE_KPI, new ProjectTO(""), false);
		if (categoryListFrmDB!=null) {
			Iterator i = categoryListFrmDB.iterator();
			while(i.hasNext()) {
				CategoryTO cto = (CategoryTO)i.next();
				cto.setGenericTag(cto.getName());
			}			
		} else {
			String allLbl = handler.getBundle().getMessage("label.all2", handler.getLocale());
			CategoryTO all = new CategoryTO("");
			all.setGenericTag(allLbl);
			categoryListFrmDB = new Vector();
			categoryListFrmDB.addElement(all);			
		}
		FieldValueTO catList = new FieldValueTO("CATEGORIES", "label.viewBSC.category", categoryListFrmDB);
    	list.add(catList);
    	
    	return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getUniqueName()
     */
    public String getUniqueName() {
        return "KPI_CSV_EXPORT";
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getLabel()
     */
    public String getLabel() throws BusinessException {
        return "label.importExport.kpiCsvExport";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFileName(com.pandora.ProjectTO)
     */    
    public String getFileName(ProjectTO pto) throws BusinessException {
        return "kpi.csv";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getContentType()
     */    
    public String getContentType() throws BusinessException {
        return "text/csv; charset=ISO-8859-1";
    }
    
    
    public String getEncoding(){
    	return "ISO-8859-1";
    }
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getHeader(com.pandora.ProjectTO, java.util.Vector)
     */    
    public StringBuffer getHeader(ProjectTO pto, Vector fields) throws BusinessException {
    	return new StringBuffer("");
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getBody(com.pandora.ProjectTO, com.pandora.UserTO, java.util.Vector)
     */    
    public StringBuffer getBody(ProjectTO pto, UserTO handler, Vector fields) throws BusinessException {
        StringBuffer response = new StringBuffer("");
        ReportDelegate rdel = new ReportDelegate();
        UserDelegate udel = new UserDelegate();
	   
        Locale currencyLoc = udel.getCurrencyLocale();
        
		Locale loc = handler.getLocale();
		String mask = handler.getCalendarMask();
		
		FieldValueTO field1 = (FieldValueTO)fields.elementAt(0);
    	Timestamp iniDate = DateUtil.getDateTime(field1.getCurrentValue(), mask, loc);
    	
		FieldValueTO field2 = (FieldValueTO)fields.elementAt(1);    	
    	Timestamp finalDate = DateUtil.getDateTime(field2.getCurrentValue(),  mask, loc);	    
        
    	FieldValueTO field3 = (FieldValueTO)fields.elementAt(2);
    	String categoryId = field3.getCurrentValue();
    	
	    if (finalDate.equals(iniDate) || finalDate.after(iniDate) ){
	        
	    	finalDate = DateUtil.getChangedDate(finalDate, Calendar.DATE, +1);
	    	
	    	Vector listFinan = rdel.getReportListBySearch(iniDate, finalDate, ReportTO.FINANCIAL_PERSP, pto.getId(), categoryId);
		    Vector listCust = rdel.getReportListBySearch(iniDate, finalDate, ReportTO.CUSTOMER_PERSP, pto.getId(), categoryId);
		    Vector listProc = rdel.getReportListBySearch(iniDate, finalDate, ReportTO.PROCESS_PERSP, pto.getId(), categoryId);
		    Vector listLearn = rdel.getReportListBySearch(iniDate, finalDate, ReportTO.LEARNING_PERSP, pto.getId(), categoryId);
		    
		    //generate the title line
		    String line = "\" \";\" \";";
		    Timestamp cursor = iniDate;
		    do{		    	
		    	line = line + "\"" + DateUtil.getDate(cursor, mask, loc) + "\";";
		    	cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);		    	
		    } while((cursor.before(finalDate)));
		    response.append(line + "\n");
		    
		    response.append(getCsv("Financeira", listFinan, currencyLoc) );
		    response.append(getCsv("Cliente", listCust, currencyLoc) );
		    response.append(getCsv("Processo", listProc, currencyLoc) );
		    response.append(getCsv("Aprendizado", listLearn, currencyLoc) );
		    
	    } else {
	    	throw new BusinessException("The final date cannot be earlier than initial date.");
	    }
        
    	return response;
    }

    
    private StringBuffer getCsv(String perspective, Vector kpiList, Locale currencyLoc) {
    	StringBuffer response = new StringBuffer("");
    	Locale loc = handler.getLocale();
    	
    	response.append("\"" + perspective + "\"; \n");
    	
    	if (kpiList!=null) {
    		Iterator i = kpiList.iterator();
    		while(i.hasNext()){
    			ReportTO rto = (ReportTO)i.next();
    			String line = "\" \";\"" + rto.getName() + "\";";
    			
        		if (rto.getResultList()!=null) {
        			Iterator j = rto.getResultList().iterator();
        			j.next();
        			while(j.hasNext()){
        				ReportResultTO rrto = (ReportResultTO)j.next();
        				if (rrto.getLastExecution()==null) {
        					line = line + "\" \";";
        				} else {
        					line = line + "\"" + ReportResultTO.format(loc, handler.getCalendarMask(), rto.getDataType(), rrto.getValue(), currencyLoc) + "\";";
        				}
        			}
        		}
    			response.append(line + "\n");        		
    		}
    	}
    	
    	
    	return response;
    }
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFooter(com.pandora.ProjectTO, java.util.Vector)
     */
    public StringBuffer getFooter(ProjectTO pto, Vector fields) throws BusinessException {
        return new StringBuffer("");
    }
      
}
