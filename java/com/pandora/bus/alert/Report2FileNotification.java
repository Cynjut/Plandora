package com.pandora.bus.alert;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.ProjectTO;
import com.pandora.ReportFieldTO;
import com.pandora.ReportTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.action.ViewReportAction;
import com.pandora.helper.LogUtil;

public class Report2FileNotification extends Notification {

	private static final String FILE_REP_ID          = "FILE_REP_ID";
	private static final String FILE_REP_PRJ         = "FILE_REP_PRJ";
	private static final String FILE_REP_OUT         = "FILE_REP_OUT";
	
    private static final String FILE_REP_PATH        = "FILE_REP_PATH";
	
	
	@Override
	public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
		String filePath = this.getParamByKey(FILE_REP_PATH, fields);
		String repId = this.getParamByKey(FILE_REP_ID, fields);
		String projId = this.getParamByKey(FILE_REP_PRJ, fields);
		String output = this.getParamByKey(FILE_REP_OUT, fields);

		if (sqlData!=null && sqlData.size()>0) {
			Vector<Object> sqlDataItem = (Vector<Object>)sqlData.elementAt(1);
		    this.writeReportFile(filePath, repId, projId, output, sqlDataItem);			
		}
                
		return true;
	}

	
    private void writeReportFile(String filePath, String reportId, String projectId, String output, Vector<Object> sqlDataItem) throws Exception {	    
    	EventBUS bus = new EventBUS();
		try {
			
			//replace the wildcards with the fields...
			filePath = super.replaceByToken(sqlDataItem, filePath);

			String logMsg = "filePath:[" + filePath + "] reportId:[" + reportId + "] projectId:[" + projectId + "] output:[" + output + "]"; 
            bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg, RootTO.ROOT_USER, null);

		    byte[] reportStream = this.getReport(reportId, projectId, output, sqlDataItem);
		    if (reportStream!=null) {
		    	File file = new File(filePath);
		    	writeFile(file, reportStream);
		    }

		} catch (Exception ex) {
			throw new Exception("Error writing report to: " + filePath, ex);
		} 
    }
    
	public void writeFile(File file, byte[] content) {
		FileOutputStream fos  = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content);			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if (fos!=null) {
					fos.close();	
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
       
    private byte[] getReport(String reportId, String projectId, String output, Vector<Object> firstRow) {
    	byte[] response = null;
    	ReportDelegate rdel = new ReportDelegate();
    	ViewReportAction vract = new ViewReportAction();
    	ProjectDelegate pdel = new ProjectDelegate();
    	EventBUS bus = new EventBUS();
    	
		try {
	    	ReportTO rto = vract.getReport(reportId, projectId, output, null);
	    	if (rto!=null) {
	    		//&Initial_Date_7=01/10/2011&Final_Date_7=31/10/2011
		        Vector<ReportFieldTO> formFields = new Vector<ReportFieldTO>();
		        Vector<ReportFieldTO> fields = rdel.getReportFields(rto.getSqlStement());
		        if (fields!=null) {
		        	
		        	HashMap<String, ReportFieldTO> hm = new HashMap<String, ReportFieldTO>();
		            Iterator<ReportFieldTO> i = fields.iterator();
		            while(i.hasNext()) {
		                ReportFieldTO fieldTO = (ReportFieldTO)i.next(); 
		                if (hm.get(fieldTO.getId())==null) {
		                	
			                bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "looking for value to... [" + fieldTO.getId() + "]", RootTO.ROOT_USER, null);		                	
		                	hm.put(fieldTO.getId(), fieldTO);
		                	
		                	if (fieldTO.getId().equalsIgnoreCase("PROJECT_ID")) {
		                		fieldTO.setValue(projectId);
		                		formFields.addElement(fieldTO);
		                		bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "[" + fieldTO.getId() + "] matched! [" + projectId + "]", RootTO.ROOT_USER, null);
		                		
		                	} else if (fieldTO.getId().equalsIgnoreCase("PROJECT_DESCENDANT")) {
		            	    	String inList = pdel.getProjectIn(projectId);
		            	    	String sql = rto.getSqlStement();
		            	    	int pd = sql.indexOf(ReportTO.PROJECT_DESCENDANT);
		            	    	sql = sql.substring(0, pd-1) + sql.substring(pd);
		            	    	sql = sql.replaceAll(ReportTO.PROJECT_DESCENDANT, inList);
		                		rto.setSqlStement(sql);
		                		bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "[" + fieldTO.getId() + "] matched! [" + inList + "]", RootTO.ROOT_USER, null);		                		
		                	} else {
		                		for (int j=0; j<super.columnNames.size(); j++) {
		                			String col = (String)super.columnNames.get(j);
		                			if (fieldTO.getId().equalsIgnoreCase(col)){
		                				fieldTO.setValue(firstRow.get(j).toString());
		                				formFields.addElement(fieldTO);
		                				
		                				if (col.equalsIgnoreCase("USER_ID")) {
		                					rto.setHandler(new UserTO(fieldTO.getValue()));
		                				}
		                				bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "[" + fieldTO.getId() + "] matched! [" + firstRow.get(j).toString() + "]", RootTO.ROOT_USER, null);
		                				
		                				break;
		                			}
								}
		                	}
		                }
		            }
		            rto.setFormFieldsValues(formFields);
		        }
	    		
	    		response = rdel.performReport(rto);

	    	}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return response;
	}


	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Report To File";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.reportfile.help";
    }

    
	@Override
	public Vector<FieldValueTO> getFields() {
        Vector<FieldValueTO> response = new Vector<FieldValueTO>();

		Vector<TransferObject> list = new Vector<TransferObject>();
        try {
            ReportDelegate del = new ReportDelegate();
    		Vector<ReportTO> repList = del.getListBySource(false, null, null, false);
    		for (ReportTO r : repList) {
    			list.add(new TransferObject(r.getId(), r.getName()));
    		}        	
        } catch (Exception e){
        	e.printStackTrace();
        }
        response.add(new FieldValueTO(FILE_REP_ID, "notification.reportfile.rep", list));

        Vector<TransferObject> plist = new Vector<TransferObject>();
        try {
            ProjectDelegate del = new ProjectDelegate();
    		Vector<ProjectTO> projList = del.getProjectList();
    		for (ProjectTO p : projList) {
    			plist.add(new TransferObject(p.getId(), p.getName()));
    		}        	
        } catch (Exception e){
        	e.printStackTrace();
        }
        response.add(new FieldValueTO(FILE_REP_PRJ, "notification.reportfile.proj", plist));
        
        Vector<TransferObject> olist = new Vector<TransferObject>();
        olist.add(new TransferObject("PDF", "PDF"));
        olist.add(new TransferObject("ODT", "OpenOffice Writer"));
        olist.add(new TransferObject("RTF", "Rich Text Format"));
        olist.add(new TransferObject("JPG", "JPG Image"));
        response.add(new FieldValueTO(FILE_REP_OUT, "label.viewReport.format", olist));
        
        response.add(new FieldValueTO(FILE_REP_PATH, "notification.reportfile.path", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        
		return response;
	}

}
