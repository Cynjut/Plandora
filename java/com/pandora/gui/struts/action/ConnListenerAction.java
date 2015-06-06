package com.pandora.gui.struts.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.ConnectorDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.XmlDomParse;
import com.pandora.integration.IntegrationResponse;
import com.pandora.integration.RepositoryMessageIntegration;
import com.pandora.integration.ResourceTaskAllocIntegration;
import com.pandora.integration.ResourceTaskIntegration;

/**
 * 
 */
public class ConnListenerAction extends GeneralStrutsAction {

    /**
     * Perform the listener of Plandora connector. <br> 
     * Get the content from http payload, de-serialize the incoming object, 
     * persist data into database (if necessary) and format the response 
     * to the requester.
     */
	public ActionForward execute (ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
	    
	    String respMessage = "OK";
	    IntegrationResponse ir = new IntegrationResponse();
	    ConnectorDelegate cdel = new ConnectorDelegate();
	    RepositoryDelegate rdel = new RepositoryDelegate();
	    
	    try {
	        //get content sent by client and perform the xml unmarshalling
	        String content = this.getQueryString(request);
	        if (content!=null && !content.equals("")) {
	        	ArrayList<Object> list = this.getObjectsFromXML(content);
		        if (list!=null && list.size()>0) {
		        	
		        	//Process a list of Integration objects into PLANdora data base	        	
		        	cdel.process(request, list);

		        } else {
		        	
		        	RepositoryMessageIntegration rep = getObjectsFromRepositoryXML(content);
		        	if (rep!=null) {
		        		ProjectDelegate pdel = new ProjectDelegate();
		        		respMessage = pdel.applyRepositoryPolicies(rep);
		        		if (!respMessage.trim().equals("0")) {
			        		String msg = super.getBundleMessage(request, "label.formRepository.msg." + respMessage, true);
			        		ir.setOptionalMsg(msg);	        			
		        		} else {
		        			ir.setOptionalMsg("OK");
		        			
		        			ArrayList<TransferObject> parsedFiles = rep.getParsedFiles();
		        			if (rep.getProjectId()!=null && parsedFiles!=null) {
		        				for (TransferObject aFile : parsedFiles) {
				        			rdel.insertHistory(rep.getAuthorUser(), new ProjectTO(rep.getProjectId()), 
				        					aFile.getGenericTag(), RepositoryDelegate.ACTION_COMMIT, 
				        					rep.getComment());		        													
								}
		        			}
		        		}
		        		
		        	} else {
		        		respMessage = "The object sent by the serialization protocol was not reconized";	
		        	}
		        }	        	
	        } else {
	        	respMessage = "ERR";
	        	ir.setOptionalMsg("The Query String was empty.");
	        }
	        
		}catch(Exception e){
			LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on Connector Listener process", e);
			respMessage = e.getMessage();
			
		} finally{
			//put response content into Standard output
			ServletOutputStream sos;
			try {		    
				sos = response.getOutputStream();
				ir.setStatus(respMessage);
				String content = ir.toXML();
				
				String chartset = SystemSingleton.getInstance().getDefaultEncoding();
				response.setContentType("text/xml; charset=" + chartset);
				response.setContentLength(content.length());
				sos.write(content.getBytes());
				sos.close();

			} catch (IOException e2) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on Connector Listener process", e2);
			}
		}
			
		return null;
	}
	

	/**
	 * Convert XML data into integration objects list
	 */
	private ArrayList<Object> getObjectsFromXML(String xml) throws Exception{
	    ArrayList<Object> response = null;
	    Document doc = XmlDomParse.getXmlDom(xml);

	    //retrieve Resource Task Alloc information
	    NodeList nodes1 = doc.getElementsByTagName(ResourceTaskAllocIntegration.RESOURCE_TASK_ALLOC);
	    if (nodes1!=null){
	        response = new ArrayList<Object>();
		    for(int i=0; i< nodes1.getLength(); i++){
		        Node node = nodes1.item(i);
		        ResourceTaskAllocIntegration alloc = new ResourceTaskAllocIntegration();
		        alloc.fromXML(node);
		        response.add(alloc);
		    }	        
	    }

	    //retrieve Resource Task Alloc information	    
	    NodeList nodes2 = doc.getElementsByTagName(ResourceTaskIntegration.RESOURCE_TASK);
	    if (nodes2!=null){
	        if (response==null) {
	            response = new ArrayList<Object>();    
	        }
		    for(int i=0; i< nodes2.getLength(); i++){
		        Node node = nodes2.item(i);
		        ResourceTaskIntegration task = new ResourceTaskIntegration();
		        task.fromXML(node);
		        response.add(task);
		    }	        
	    }
	    
	    return response;
	}

	
	private RepositoryMessageIntegration getObjectsFromRepositoryXML(String xml) throws Exception{
		RepositoryMessageIntegration response = null;
	    Document doc = XmlDomParse.getXmlDom(xml);

	    Node root = doc.getDocumentElement();
	    String projId = XmlDomParse.getAttributeTextByTag(root, RepositoryMessageIntegration.REPOSITORY_PROJ_ID);
        if (projId != null && !projId.trim().equals("")) {
        	response = new RepositoryMessageIntegration();
        	response.setProjectId(projId);
        }
	    
	    NodeList nodes1 = doc.getElementsByTagName(RepositoryMessageIntegration.REPOSITORY_FILES);
	    if (nodes1!=null && nodes1.getLength()>0){
	        if (response == null) {
	        	response = new RepositoryMessageIntegration();
	        }
	        Node node = nodes1.item(0);
	        response.setFiles(node.getTextContent());
	    }

	    NodeList nodes2 = doc.getElementsByTagName(RepositoryMessageIntegration.REPOSITORY_LOG);
	    if (nodes2!=null && nodes2.getLength()>0){
	        if (response == null) {
	        	response = new RepositoryMessageIntegration();
	        }
	        Node node = nodes2.item(0);
	        response.setComment(node.getTextContent());
	    }
	    
	    NodeList nodes3 = doc.getElementsByTagName(RepositoryMessageIntegration.REPOSITORY_REPOS);
	    if (nodes3!=null && nodes3.getLength()>0){
	        if (response == null) {
	        	response = new RepositoryMessageIntegration();
	        }
	        Node node = nodes3.item(0);	        
	        response.setRepositoryPath(node.getTextContent());
	    }

	    NodeList nodes4 = doc.getElementsByTagName(RepositoryMessageIntegration.REPOSITORY_AUTHOR);
	    if (nodes4!=null && nodes4.getLength()>0){
	        if (response == null) {
	        	response = new RepositoryMessageIntegration();
	        }
	        Node node = nodes4.item(0);	        
	        response.setAuthor(node.getTextContent());
	        
	        if (response.getAuthor()!=null && !response.getAuthor().trim().equals("")){
	        	this.extractAuthorUser(response);	
	        }
	    }
	    
	    return response;
	}


	private void extractAuthorUser(RepositoryMessageIntegration response) throws BusinessException {
		UserDelegate udel = new UserDelegate();
		UserTO filter = new UserTO();
		filter.setUsername(response.getAuthor());
		UserTO uto = udel.getObjectByUsername(filter);
		response.setAuthorUser(uto);
	}
	
		
	/**
	 * Get the content from http message
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String getQueryString(HttpServletRequest request) throws IOException{
		String queryStringBuf = "";
				
		if (request.getMethod().equals("GET")) {
			queryStringBuf = request.getQueryString();
			String chartset = SystemSingleton.getInstance().getDefaultEncoding();
			queryStringBuf = URLDecoder.decode(queryStringBuf, chartset);
			
			//get only the content from 'data' get-http field
			queryStringBuf = getFieldFromHttpGet(queryStringBuf, "data");
			
		} else {
			int contentLength = request.getContentLength();
			if (contentLength>-1) {
				BufferedReader in = new BufferedReader(request.getReader());
				char[] cbuf = new char[contentLength];
				in.read(cbuf);    
				queryStringBuf = new String(cbuf);				
			}
		}
		return(queryStringBuf);
	}
	

	private String getFieldFromHttpGet(String payload, String fieldName) {
	    String response = "";
	    String tokens[] = payload.split("&");
	    for(int i=0; i<tokens.length; i++) {
	        String params = tokens[i];
	        int j = params.indexOf("=");
	        if (j>-1 && params.substring(0, j).equals(fieldName)) {
	            response = params.substring(j+1);
	            break;
	        }
	    }
	    return response;
	}
}

