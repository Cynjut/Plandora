package com.pandora.bus.alert;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.axis2.util.Base64;

import com.atlassian.jira.rpc.soap.beans.RemoteAttachment;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapService;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapServiceServiceLocator;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JirasoapserviceV2SoapBindingStub;
import com.pandora.AttachmentTO;
import com.pandora.ExternalDataTO;
import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.ExternalDataDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class Jira5IssueToRequestNotification extends Notification {

    private JiraSoapServiceServiceLocator fJiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
    private JiraSoapService fJiraSoapService = null;
    private String fToken = null;
	
	private static final String DEST_PROJECT     = "DEST_PROJ";
	private static final String SOURCE_USER      = "SOURCE_USER";
	private static final String JIRA_HOST        = "JIRA_HOST";
	private static final String JIRA_TIMEOUT     = "TIMEOUT";
	private static final String JIRA_USER        = "JIRA_USER";
	private static final String JIRA_PASS        = "JIRA_PASS";
	private static final String JIRA_JQL         = "JIRA_JQL";

	
	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */    
	public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
    	EventBUS bus = new EventBUS();
    	ExternalDataDelegate del = new ExternalDataDelegate();
    	String logMsg = "";
    	try {
    		String jirahost = this.getParamByKey(JIRA_HOST, fields);
    		String jiraUser = this.getParamByKey(JIRA_USER, fields);
    		String jiraPass = this.getParamByKey(JIRA_PASS, fields);
    		String projectId = this.getParamByKey(DEST_PROJECT, fields);
    		String requesterId = this.getParamByKey(SOURCE_USER, fields);
    		String jqlContent = this.getParamByKey(JIRA_JQL, fields);
    		String timeout = this.getParamByKey(JIRA_TIMEOUT, fields);
    		if (timeout==null) {
    			timeout = "30000";
    		}
    		
    		while (jirahost.endsWith("/")) {
    			jirahost = jirahost.substring(0, jirahost.length()-1);
    		}
    		
    		Vector<Object> sqlDataItem = (Vector<Object>)sqlData.elementAt(1);
    		jqlContent = super.replaceByToken(sqlDataItem, jqlContent);
    		
    		logMsg = "projId:[" + projectId + "] host:[" + jirahost + "] jiraUser:[" + jiraUser + "] jql:[" + jqlContent + "]";
    		bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg, RootTO.ROOT_USER, null);
    		logMsg = "";
    				
    		long ini = DateUtil.getNow().getTime();
    		this.connectToJira(jirahost.trim(), jiraUser.trim(), jiraPass.trim(), timeout.trim());
    		logMsg = "Jira Connected in " + (DateUtil.getNow().getTime() - ini) + "ms; ";

    		ini = DateUtil.getNow().getTime();
    		RemoteIssue[] ris = fJiraSoapService.getIssuesFromJqlSearch(fToken, jqlContent, 1000);
    		logMsg = logMsg + "Loading data in " + (DateUtil.getNow().getTime() - ini) + "ms; ";
    		
    		ini = DateUtil.getNow().getTime();
            for (RemoteIssue ri : ris) {
            	ExternalDataTO dbto = del.getExternalData(ri.getKey());
            	
            	boolean isUpdate = (dbto!=null);
            	ExternalDataTO edto = this.getTransferObjectFromJira(ri, jirahost, jiraUser, requesterId);            	
        		RemoteAttachment[] atts = fJiraSoapService.getAttachmentsFromIssue(fToken, ri.getKey());
        		if (atts!=null) {
        			for (RemoteAttachment ra : atts) {
                		AttachmentTO ato = this.getAttachment(jirahost,	jiraUser, jiraPass, ra);
						ato.setComment(edto.getExternalSys() + ": " + ra.getAuthor());                		
                		edto.addAttachment(ato);
					}
        		}
        		
        		if (isUpdate) {
        			edto.setId(dbto.getId());
        			edto.setExternalId(dbto.getExternalId());
        			edto.setPlanningId(dbto.getPlanningId());
        			del.updateRequirement(edto, projectId);
        		} else {
            		del.createRequirement(edto, projectId);
            	}
    		}
            logMsg = logMsg + "parsing data in " + (DateUtil.getNow().getTime() - ini) + "ms";
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		logMsg = e.getMessage();
    		throw e;
    	} finally {
    		if (logMsg!=null && !logMsg.trim().equals("")) {
    			bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg, RootTO.ROOT_USER, null);	
    		}
    	}

		return true;
	}


	private AttachmentTO getAttachment(String jirahost, String jiraUser, String jiraPass, RemoteAttachment ra) throws MalformedURLException, IOException {
		String id = ra.getId();
		String name = ra.getFilename();
		name = name.replaceAll(" ", "+");
		
		URL u = new URL(jirahost + "/secure/attachment/" + id + "/" + name + "?os_username=" + jiraUser + "&os_password=" + jiraPass);
		URLConnection urlc = u.openConnection();
		String userData = jiraUser + ":" + jiraPass;
		String encodedUserPass = Base64.encode(userData.getBytes());
		urlc.addRequestProperty("Authorization", encodedUserPass);
		InputStream in = urlc.getInputStream();
		
		// convert the input stream to a byte array
	    int len;
	    int size = 1024;
	    byte[] buf;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		buf = new byte[size];
		while ((len = in.read(buf, 0, size)) != -1){
			bos.write(buf, 0, len);
		}
		buf = bos.toByteArray();		
				
		AttachmentTO ato = new AttachmentTO(id);
		ato.setFileInBytes(buf, buf.length);
		ato.setBinaryFile(new ByteArrayInputStream(buf), buf.length);
		ato.setContentType(ra.getMimetype());
		ato.setName(ra.getFilename());
		ato.setCreationDate(new Timestamp(ra.getCreated().getTimeInMillis()));
		
		return ato;
	}
	
	
	private ExternalDataTO getTransferObjectFromJira(RemoteIssue ri, String jirahost, String jiraUser, String requesterId) {
		ExternalDataTO response = new ExternalDataTO();
		
		response.setExternalAccount(jirahost);
		response.setExternalHost(jiraUser);
		
		response.setBody(ri.getDescription() + " [" + ri.getReporter() + "]");
		response.setDestination(ri.getAssignee());
		response.setExternalId(ri.getKey());
		response.setExternalSys("JIRA");
		
		if (requesterId==null || requesterId.equals("-1") || requesterId.trim().equals("")) {
			response.setSource(ri.getReporter());
		} else {
			response.setSource(requesterId);
		}
		
		response.setSummary(ri.getSummary());
		
		if (ri.getCreated()!=null) {
			response.setMessageDate(new Timestamp(ri.getCreated().getTimeInMillis()));	
		}
		if (ri.getDuedate()!=null) {
			response.setDueDate(new Timestamp(ri.getDuedate().getTimeInMillis()));	
		}
		
		response.setEventDate(DateUtil.getNow());
		response.setId(null);
		response.setPlanningId(null);

		Integer priority = new Integer("0");
		if (ri.getPriority()!=null && !ri.getPriority().trim().equals("")) {
			String prStr = ri.getPriority().trim();
			if (prStr.equals("1")) {
				priority = new Integer("5");
				
			} else if (prStr.equals("2")) {
				priority = new Integer("4");
				
			} else if (prStr.equals("3")) {
				priority = new Integer(prStr);
				
			} else if (prStr.equals("4")) {
				priority = new Integer("2");
				
			} else if (prStr.equals("5")) {
				priority = new Integer("1");
			}
		}
		response.setPriority(priority);

		return response;
	}


	private void connectToJira(String server, String user, String pass, String timeout) throws Exception { 
        String endPoint = "/rpc/soap/jirasoapservice-v2";
        fJiraSoapServiceGetter.setJirasoapserviceV2EndpointAddress(server + endPoint);
        ((JirasoapserviceV2SoapBindingStub)fJiraSoapServiceGetter.getJirasoapserviceV2()).setTimeout(Integer.parseInt(timeout));
        fJiraSoapService = fJiraSoapServiceGetter.getJirasoapserviceV2();
        fToken = fJiraSoapService.login(user, pass);
	}
    
	
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFields()
     */    
	public Vector<FieldValueTO> getFields() {
		Vector<FieldValueTO> response = new Vector<FieldValueTO>();
        UserDelegate udel = new UserDelegate();
        ProjectDelegate del = new ProjectDelegate();
        
        Vector<TransferObject> plist = new Vector<TransferObject>();
        try {
    		Vector<ProjectTO> projList = del.getProjectList();
    		for (ProjectTO p : projList) {
    			plist.add(new TransferObject(p.getId(), p.getName()));
    		}        	
    		response.add(new FieldValueTO(DEST_PROJECT, "notification.jira.proj", plist));

    		Vector<TransferObject> ulist = new Vector<TransferObject>();
    		ulist.add(new TransferObject("-1", "Auto"));
    		
    		Vector<UserTO> userList = udel.getUserList(true);
    		for (UserTO u : userList) {
    			if (!u.getUsername().equals(RootTO.ROOT_USER)) {
    				ulist.add(new TransferObject(u.getUsername(), u.getName()));	
    			}
    		}
    		response.add(new FieldValueTO(SOURCE_USER, "notification.jira.requ", ulist));
    		
        } catch (Exception e){
        	e.printStackTrace();
        }
        
        response.add(new FieldValueTO(JIRA_HOST, "notification.jira.host", FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
        response.add(new FieldValueTO(JIRA_USER, "notification.jira.user", FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
        response.add(new FieldValueTO(JIRA_PASS, "notification.jira.pass", FieldValueTO.FIELD_TYPE_PASS, 50, 20));
        response.add(new FieldValueTO(JIRA_TIMEOUT, "notification.jira.timeout", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));
        response.add(new FieldValueTO(JIRA_JQL, "notification.jira.body", FieldValueTO.FIELD_TYPE_AREA, 85, 4));
        
        return response;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Jira5ToRequest";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
    	return "notification.jira.help";
    }

}
