package com.pandora.bus.alert;

import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.pandora.RootTO;
import com.pandora.bus.EventBUS;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.sun.mail.smtp.SMTPTransport;

/**
 * This class contain the business rule to send a notification
 * by email.
 * 
 * In sendNotification the fields vector must contain the following values:
 * <li>host name or ip</li>
 * <li>SMTP user account</li>
 * <li>SMTP user password</li>
 * <li>Destination email</li>
 * <li>Sender email</li>
 * <li>Subject text</li>
 * <li>Body text</li>
 * <li>the group paramenter that define if the email should be sent once, or each line of sqlData</li>
 * <li>the format of email body: text-plain or html
 */
public class EmailNotification extends Notification {

	/** Content type of the mail message */
	private static final String CONTENT_TYPE = "text/html";
	
	private static final String SMTP_MAILER = "smtpsend";	
	
	private static final String EMAIL_HOST        = "EMAIL_HOST";
	private static final String EMAIL_USER        = "EMAIL_USER";
	private static final String EMAIL_PASSWORD    = "EMAIL_PASSWORD";
	private static final String EMAIL_DESTINATION = "EMAIL_DESTINATION";
	private static final String EMAIL_SENDER      = "EMAIL_SENDER";
	private static final String EMAIL_SUBJECT     = "EMAIL_SUBJECT";
	private static final String EMAIL_TEXT        = "EMAIL_TEXT";
	private static final String EMAIL_GROUP       = "EMAIL_GROUP";
	private static final String EMAIL_TEXT_FORMAT = "EMAIL_TEXT_FORMAT";
	
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
		String host = this.getParamByKey(EMAIL_HOST, fields);
		String smtpUser = this.getParamByKey(EMAIL_USER, fields);
		String smtpPass = this.getParamByKey(EMAIL_PASSWORD, fields);
		String destination = this.getParamByKey(EMAIL_DESTINATION, fields);
		String sender = this.getParamByKey(EMAIL_SENDER, fields);
		String subject = this.getParamByKey(EMAIL_SUBJECT, fields);
		String text = this.getParamByKey(EMAIL_TEXT, fields);
		String group = this.getParamByKey(EMAIL_GROUP, fields);
		String format = this.getParamByKey(EMAIL_TEXT_FORMAT, fields);

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, null);

		if (group.equals("OK")) {
		    
		    String content = "";
		    if (format.equals("OK")) {
			    content = super.getHtmlContent(sqlData);
		    } else {
		        content = super.getContent(sqlData, false, false);
		    }
		    text = text + "\n" + content;
		    
		    Vector sqlDataItem = (Vector)sqlData.elementAt(1);
		    this.sendMessage(session, destination, text, sender, subject, host, smtpUser, smtpPass, sqlDataItem);
		    
		} else {
			boolean isFirst = true;
			Iterator i = sqlData.iterator();
			while(i.hasNext()) {
			    Vector sqlDataItem = (Vector)i.next();
			    if (!isFirst) {
			    	this.sendMessage(session, destination, text, sender, subject, host, smtpUser, smtpPass, sqlDataItem);	
			    } else {
			    	isFirst = false;
			    }
			}
		}
        
        
        return true;
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector getFieldLabels() {
        Vector list = new Vector();
        list.add("notification.email.from");
        list.add("notification.email.to");
        list.add("notification.email.title");
        list.add("notification.email.body");
        list.add("notification.email.host");
        list.add("notification.email.user");
        list.add("notification.email.pass");
        list.add("notification.email.group");
        list.add("notification.email.format");
        return list;
    }

 
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */
    public Vector getFieldTypes() {
        Vector list = new Vector();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("3");
        list.add("2");
        list.add("2");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */
    public Vector getFieldKeys() {
        Vector list = new Vector();
        list.add(EMAIL_SENDER);
        list.add(EMAIL_DESTINATION);
        list.add(EMAIL_SUBJECT);
        list.add(EMAIL_TEXT);
        list.add(EMAIL_HOST);
        list.add(EMAIL_USER);
        list.add(EMAIL_PASSWORD);
        list.add(EMAIL_GROUP);
        list.add(EMAIL_TEXT_FORMAT);
        return list;        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "E-Mail";
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.email.help";
    }
    
    
    private void sendMessage(Session session, String destination, String text, 
            String sender, String subject, String host, String smtpUser, String smtpPass, Vector sqlDataItem) throws Exception {
    
		//replace the wildcards with the fields...
		destination = super.replaceByToken(sqlDataItem, destination);
		sender = super.replaceByToken(sqlDataItem, sender);
		subject = super.replaceByToken(sqlDataItem, subject);		
		text = super.replaceByToken(sqlDataItem, text);
	    
	    this.sendMessage(session, destination, text, sender, subject, host, smtpUser, smtpPass);        
    }
    
    
    private void sendMessage(Session session, String destination, String text, 
            String sender, String subject, String host, String smtpUser, String smtpPass) 
    throws Exception {
    	EventBUS bus = new EventBUS();
		try {
			String logMsg = "sender:[" + sender + "] destination:[" + destination + "] subject:[" + subject + "] host:[" + host + "] smtpUser:[" + smtpUser + "]"; 
            bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg, RootTO.ROOT_USER, null);
			
			// Create the message to be sent
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			
			if (destination!=null) {
			    String[] multRecip = destination.split(",");
			    for (int i=0; i<multRecip.length; i++) {
			    	message.addRecipient(Message.RecipientType.TO, 
			    			new InternetAddress(multRecip[i]));
			    }			    
			}

			message.setSubject(subject);
			message.setHeader("X-Mailer", SMTP_MAILER);
			message.setSentDate(DateUtil.getNow());

			message.setContent(text, CONTENT_TYPE);
			
			// Create the SMTP Transport
		    SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
		    try {
			    transport.connect(host, smtpUser, smtpPass);		    	
			    // Send the message
			    transport.sendMessage(message, message.getAllRecipients());
		    } finally {
		    	transport.close();
		    }

		} catch (MessagingException ex) {
			throw new Exception("Error sending email to: " + destination, ex);
		} 
	}
    

}
