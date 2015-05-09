package com.pandora.integration;

import java.util.ArrayList;
import java.util.Iterator;

import com.pandora.helper.PosterUtil;
import com.pandora.integration.exception.IntegrationException;

/**
 * This class contain the way to share 
 * information from PLANdora with a third part application.
 */
public class Connector {

    /**
     * Send a list of objects to PLANdora for a specific purpose.<br> 
     * When this method is called, the connector validates all records,
     * build a transaction with system and perform the action (inserting, 
     * updating or removing data). If some error ocurrs all the bunch 
     * of changes will be rollbacked.
     */
    public IntegrationResponse execute(ArrayList data, String uri) throws IntegrationException{
        IntegrationResponse resp = null;
        
        if (data!=null) {
            
            //validate content
            this.validate(data);
                   
            //submit elements of arraylist into PLANdora
            resp = this.send(data, uri, false);
        }

        return resp;
    }


    public boolean executeWithoutResponse(ArrayList data, String uri) throws IntegrationException{
        IntegrationResponse resp = null;
        
        if (data!=null) {
            
            //validate content
            this.validate(data);
                   
            //submit elements of arraylist into PLANdora
            resp = this.send(data, uri, true);
        }
        
        return resp.wasSuccess();
    }

    /**
     * TODO modelar e implementar
     * @return
     * @throws IntegrationException
     */
    public IntegrationResponse query() throws IntegrationException{       
        return null;
    }
    

    private IntegrationResponse send(ArrayList data, String uri, boolean noParseResponse) throws IntegrationException {
        IntegrationResponse resp = null;
        PosterUtil poster = null;
        
        //serialize Integration object into XML format
        String seriliazed = this.getXmlFromObjects(data);

        //send XML content to PLANdora
        try {
            poster = new PosterUtil();
            poster.setURL(uri);
            poster.addParameter("data", seriliazed);
            poster.openGet();

            //retrieve the http response, deserialize and populate the result object
            String httpResponse = "";        
            while (poster.readLine()) {
                httpResponse+= poster.getLine();
            }

            resp = new IntegrationResponse();
            if (noParseResponse) {
            	System.out.println(httpResponse);
                resp.setStatus(httpResponse.indexOf("STATUS=\"OK\"")>-1?"OK":"ERR");
            } else {
                resp.fromXML(httpResponse);            	
            }
            
        } catch(Exception e){
            resp = null;
        } finally {
            if (poster!=null) {
                poster.closePost();    
            }
        }
        
        return resp;
    }


    /**
     * Convert a list of integration objects into XML string.
     * @param data
     * @return
     * @throws IntegrationException
     */
    private String getXmlFromObjects(ArrayList data) throws IntegrationException{
        String response = "";
        if (data !=null && data.size()>0) {
            for(int i=0; i<data.size(); i++) {
                Integration integ = (Integration)data.get(i);
                response = response + integ.toXML();
            }
        } else {
            throw new IntegrationException("The objects was not known by Connector");
        }
        
        return "<REQUEST>" + response + "</REQUEST>";
    }
    
    
    /**
     * Call the specific validation method of each entity. 
     * @param data
     * @throws IntegrationException
     */
    private void validate(ArrayList data) throws IntegrationException{
        Iterator i = data.iterator();
        while(i.hasNext()){
            Integration entity = (Integration)i.next();

            if (entity.getTransaction()!=null) {
                if (entity.getTransaction().equals(Integration.TRANSACTION_INSERT)){
                    entity.validateInsert();

                } else if (entity.getTransaction().equals(Integration.TRANSACTION_UPDATE)){
                    entity.validateUpdate();

                } else if (entity.getTransaction().equals(Integration.TRANSACTION_DELETE)){
                    entity.validateDelete();

                } else {
                    throw new IntegrationException("The transaction field of some record contain an invalid value.");  
                }
            } else {
                throw new IntegrationException("The transaction field of some record is empty. The transaction field is mandatory.");
            }
        }
    }

}
