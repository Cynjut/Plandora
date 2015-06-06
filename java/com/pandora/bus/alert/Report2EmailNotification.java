package com.pandora.bus.alert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

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
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.sun.mail.smtp.SMTPTransport;

public class Report2EmailNotification extends Notification {

	private static final String SMTP_MAILER = "smtpsend";	

	private static final String EMAIL_REP_ID          = "EMAIL_REP_ID";
	private static final String EMAIL_REP_PRJ         = "EMAIL_REP_PRJ";
	private static final String EMAIL_REP_OUT         = "EMAIL_REP_OUT";
	
	private static final String EMAIL_REP_HOST        = "EMAIL_REP_HOST";
	private static final String EMAIL_REP_PORT        = "EMAIL_REP_PORT";
	private static final String EMAIL_REP_USER        = "EMAIL_REP_USER";
	private static final String EMAIL_REP_PASSWORD    = "EMAIL_REP_PASSWORD";
	private static final String EMAIL_REP_DESTINATION = "EMAIL_REP_DESTINATION";
	private static final String EMAIL_REP_SENDER      = "EMAIL_REP_SENDER";
	private static final String EMAIL_REP_SUBJECT     = "EMAIL_REP_SUBJECT";
	private static final String EMAIL_REP_TEXT        = "EMAIL_REP_TEXT";
	
	
	@Override
	public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
		String host = this.getParamByKey(EMAIL_REP_HOST, fields);
		String port = this.getParamByKey(EMAIL_REP_PORT, fields);
		String smtpUser = this.getParamByKey(EMAIL_REP_USER, fields);
		String smtpPass = this.getParamByKey(EMAIL_REP_PASSWORD, fields);
		String destination = this.getParamByKey(EMAIL_REP_DESTINATION, fields);
		String sender = this.getParamByKey(EMAIL_REP_SENDER, fields);
		String subject = this.getParamByKey(EMAIL_REP_SUBJECT, fields);
		String text = this.getParamByKey(EMAIL_REP_TEXT, fields);
		String repId = this.getParamByKey(EMAIL_REP_ID, fields);
		String projId = this.getParamByKey(EMAIL_REP_PRJ, fields);
		String output = this.getParamByKey(EMAIL_REP_OUT, fields);

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, null);
		
		if (sqlData!=null && sqlData.size()>0) {
			Vector<Object> sqlDataItem = (Vector<Object>)sqlData.elementAt(1);
		    this.sendMessage(session, destination, text, sender, subject, host, port, smtpUser, smtpPass, repId, projId, output, sqlDataItem);			
		}
                
		return true;
	}

	
    private void sendMessage(Session session, String destination, String text, 
            String sender, String subject, String host, String port, String smtpUser, String smtpPass, 
            String reportId, String projectId, String output, Vector<Object> sqlDataItem) throws Exception {	    
    	EventBUS bus = new EventBUS();
		try {
			
			//replace the wildcards with the fields...
			destination = super.replaceByToken(sqlDataItem, destination);
			sender = super.replaceByToken(sqlDataItem, sender);
			subject = super.replaceByToken(sqlDataItem, subject);		
			text = super.replaceByToken(sqlDataItem, text);

			String logMsg = "sender:[" + sender + "] destination:[" + destination + "] subject:[" + subject + "] host:[" + host + "] port:[" + port + "] smtpUser:[" + smtpUser + "]"; 
            bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg, RootTO.ROOT_USER, null);

		    Multipart multipart = new javax.mail.internet.MimeMultipart();
		    
		    // create the message part
            MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setSubject(subject);
			message.setHeader("X-Mailer", SMTP_MAILER);
			message.setSentDate(DateUtil.getNow());
			if (destination!=null) {
			    String[] multRecip = destination.split(",");
			    for (int i=0; i<multRecip.length; i++) {
			    	message.addRecipient(Message.RecipientType.TO, new InternetAddress(multRecip[i]));
			    }    
			}

		    MimeBodyPart messageBodyPart = new MimeBodyPart();
		    messageBodyPart.setText(text);
		    multipart.addBodyPart(messageBodyPart);		    
		    
		    byte[] reportStream = this.getReport(reportId, projectId, output, sqlDataItem);
		    if (reportStream!=null) {
		    	
			    // Part two is the attachment
			    messageBodyPart = new MimeBodyPart();
			    DataSource source = new ByteArrayDataSource(reportStream, "application/pdf");
			    messageBodyPart.setDataHandler(new DataHandler(source));
			    messageBodyPart.setFileName("Report.pdf");
			    multipart.addBodyPart(messageBodyPart);

			    // Put parts in message
			    message.setContent(multipart);
			    
			    SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
			    try {
				    transport.connect(host, Integer.parseInt(port), smtpUser, smtpPass);		    	
				    transport.sendMessage(message, message.getAllRecipients());
			    } finally {
			    	transport.close();
			    }		    	
		    }

		} catch (MessagingException ex) {
			throw new Exception("Error sending email to: " + destination, ex);
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
        return "Report By Email";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.reportemail.help";
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
        response.add(new FieldValueTO(EMAIL_REP_ID, "notification.reportemail.rep", list));

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
        response.add(new FieldValueTO(EMAIL_REP_PRJ, "notification.reportemail.proj", plist));
        
        Vector<TransferObject> olist = new Vector<TransferObject>();
        olist.add(new TransferObject("PDF", "PDF"));
        olist.add(new TransferObject("ODT", "OpenOffice Writer"));
        olist.add(new TransferObject("RTF", "Rich Text Format"));
        response.add(new FieldValueTO(EMAIL_REP_OUT, "label.viewReport.format", olist));
        
        response.add(new FieldValueTO(EMAIL_REP_SENDER, "notification.email.from", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(EMAIL_REP_DESTINATION, "notification.email.to", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(EMAIL_REP_SUBJECT, "notification.email.title", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(EMAIL_REP_TEXT, "notification.email.body", FieldValueTO.FIELD_TYPE_AREA, 85, 4));
        response.add(new FieldValueTO(EMAIL_REP_HOST, "notification.email.host", FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
        response.add(new FieldValueTO(EMAIL_REP_PORT, "notification.email.port", FieldValueTO.FIELD_TYPE_TEXT, 10, 5));
        response.add(new FieldValueTO(EMAIL_REP_USER, "notification.email.user", FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
        response.add(new FieldValueTO(EMAIL_REP_PASSWORD, "notification.email.pass", FieldValueTO.FIELD_TYPE_PASS, 50, 20));
        
		return response;
	}
	
}
