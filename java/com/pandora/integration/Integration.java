package com.pandora.integration;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.w3c.dom.Node;

import com.pandora.helper.XmlDomParse;
import com.pandora.integration.exception.IntegrationException;


/**
 * This class is the main transfer object used 
 * into integration prcess with PLANdora  
 */
public abstract class Integration implements Serializable {

    /** This constant is used to insert new integration objects into PLANdora*/
    public final static Integer TRANSACTION_INSERT = new Integer(1);
    
    /** This constant is used to update integration objects into PLANdora */
    public final static Integer TRANSACTION_UPDATE = new Integer(2);;
    
    /** This constant is used to remove integration objects from PLANdora */
    public final static Integer TRANSACTION_DELETE = new Integer(2);;
    
    
    /** The type of transaction (insert, update, etc) of 
     * a specific integration object */
    private Integer transaction;
    
    /** Comment related with current integration object 
     * set by thid-part application that is using the interface 
     * */
    private String comment;

    /** The user of integration action **/
    private String user;
    
    /** The password of integration's user **/
    private String password;

    /** the locale used by sender to format date and float values **/
    private String locale;

    /** if true, the current object has been validated by authentication process **/
    private boolean isValid;
    
    
    /** Should be implemented by each sub class with validation
     * rules about insertion process into PLANdora */
    public abstract void validateInsert() throws IntegrationException;

    /** Should be implemented by each sub class with validation
     * rules about updating process into PLANdora */    
    public abstract void validateUpdate() throws IntegrationException;

    /** Should be implemented by each sub class with validation
     * rules about removing process into PLANdora */        
    public abstract void validateDelete() throws IntegrationException;
    

    /**
     * This method return a XML with the current attributes of object
     * @return
     */
    public String toXML() {
        String response = " TRANSACTION=\"" + this.getTransaction().intValue() +
				 "\" USER=\"" + this.getUser() +
				 "\" PASSWORD=\"" + this.getPassword() +
				 "\" LOCALE=\"" + this.getLocale() +
				 "\" COMMENT=\"" + this.getEncodedComment() + "\"";
        return response;
    }


    /**
     * This method get a XML with objects attributes and set 
     * the appropriate values from XML content.
     * @param node
     * @throws Exception
     */
    public void fromXML(Node node) {
        this.setUser(XmlDomParse.getAttributeTextByTag(node, "USER"));
        this.setPassword(XmlDomParse.getAttributeTextByTag(node, "PASSWORD"));
        this.setEncodedComment(XmlDomParse.getAttributeTextByTag(node, "COMMENT"));
        this.setLocale(XmlDomParse.getAttributeTextByTag(node, "LOCALE"));
        String trans = XmlDomParse.getAttributeTextByTag(node, "TRANSACTION");
        this.setTransaction(new Integer(trans));
    }


    private String getEncodedComment() {
        String response = null;
        if (this.getComment()!=null) {
            try {
				response = URLEncoder.encode(this.getComment(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				response = null;
			}
        }
        return response;
    }    

    private void setEncodedComment(String encc) {
        try {
			this.setComment(URLDecoder.decode(encc, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }    
    
    ///////////////////////////////////////////    
    public Integer getTransaction() {
        return transaction;
    }
    public void setTransaction(Integer newValue) {
        this.transaction = newValue;
    }
    

    ///////////////////////////////////////////
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
    
    ///////////////////////////////////////////      
    public String getPassword() {
        return password;
    }
    public void setPassword(String newValue) {
        this.password = newValue;
    }
    
    
    ///////////////////////////////////////////      
    public String getUser() {
        return user;
    }
    public void setUser(String newValue) {
        this.user = newValue;
    }
    
    ///////////////////////////////////////////      
    public String getLocale() {
        return locale;
    }
    public void setLocale(String newValue) {
        this.locale = newValue;
    }

    
    ///////////////////////////////////////////      
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean newValue) {
		this.isValid = newValue;
	}
    
    
}
