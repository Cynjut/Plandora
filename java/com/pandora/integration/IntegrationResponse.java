package com.pandora.integration;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.pandora.helper.XmlDomParse;
import com.pandora.integration.exception.IntegrationException;

/**
 * This class contain the data responsed by PLAndora
 * and the final status of transaction.
 */
public class IntegrationResponse {

    /** The final status of transaction set by 'information supplier'.
     * If 'OK' the current transaction was successfully performed, any
     * other value, the transaction failed. */
    private String status;
    
    /** List of integration objects optionally returned by 'information supplyer' */
    private ArrayList result;

    
    private String optionalMsg;    
    
    
    /** 
     * Return the current status of transaction.
     * @return
     */
    public boolean wasSuccess(){
        return status.equalsIgnoreCase("OK");
    }
    

    public String toXML() {
        String response = "<RESPONSE STATUS=\"" + status + "\"";

        if (optionalMsg!=null) {
            response = response + " MSG=\"" + optionalMsg + "\"";	
        }
        response = response + ">";
        
        if (result!=null) {
            for(int i =0 ; i<result.size(); i++) {
                Integration integ = (Integration)result.get(i);
                response = response + integ.toXML();
            }            
        }            
        response = response + "</RESPONSE>";
        
        return response;
    }
    
    
    public void fromXML(String xml) throws IntegrationException {
        try {
            Document doc = XmlDomParse.getXmlDom(xml);
            Node root = doc.getFirstChild();
            this.setStatus(XmlDomParse.getAttributeTextByTag(root, "STATUS"));

            ArrayList nodes = XmlDomParse.getNodesByTag(root);
            if (nodes!=null && nodes.size()>0) {
                this.result = new ArrayList();
                
                for (int i = 0 ; i<nodes.size(); i++) {
                    Node node = (Node) nodes.get(i);
                    if (node.getNodeName().equals(ResourceTaskAllocIntegration.RESOURCE_TASK_ALLOC)){
                        ResourceTaskAllocIntegration obj = new ResourceTaskAllocIntegration();
                        obj.fromXML(node);
                        this.result.add(obj);
                    } else if (node.getNodeName().equals(ResourceTaskIntegration.RESOURCE_TASK)){
                        ResourceTaskIntegration obj = new ResourceTaskIntegration();
                        obj.fromXML(node);
                        this.result.add(obj);
                    } else {
                        throw new IntegrationException("The object is not known by IntegrationResponse");
                    }
                }            
            }
        } catch (Exception e){
            throw new IntegrationException(e);
        }
    }


    ///////////////////////////////////////////    
    public ArrayList getResult() {
        return result;
    }
    public void setResult(ArrayList newValue) {
        this.result = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }

    
    ///////////////////////////////////////////
	public String getOptionalMsg() {
		return optionalMsg;
	}
	public void setOptionalMsg(String newValue) {
		this.optionalMsg = newValue;
	}
    
    
}
