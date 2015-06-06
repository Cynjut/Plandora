package com.pandora.helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.pandora.bus.SystemSingleton;

/**
 * This class contains the objects needed to send an HTTP post
 * using the classic 'java.net' http client classes.  
 */
public class PosterUtil {
    
    /** Destination of http connection **/
    private String uri;

    /** List of parameters/value used by get connection */
    private String parameters;

    /** BufferReader object used to get the content returned by URI */
    private BufferedReader in;

    /** Part of response (some line) of content returned by URI */
    private String line;

    
    /**
     * Construtor
     */
    public PosterUtil() {
        uri = "";
        parameters = "";
        line = "";
    }
    
    
    /**
     * Add http parameter to get connection 
     */
    public void addParameter(String param, String value) {
    	try {
			String encoding = SystemSingleton.getInstance().getDefaultEncoding();    		
            if (parameters.equals("")) {
				parameters = param + "=" + URLEncoder.encode(value, encoding);
            } else {
                parameters += "&" + param + "=" + URLEncoder.encode(value, encoding);
            }    		
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    }
    

    /**
     * Define the URL used by GET connection
     * @param theUrl
     */
    public void setURL(String theUrl) {
        uri = theUrl;
    }

    
    /**
     * Open the get connection
     */
    public void openGet() {
        try {
            String urlNew = this.uri;
            if (!parameters.equals("")) {
                urlNew = urlNew + "?" + parameters;                
            }
            URL urlSend = new URL(urlNew);
            URLConnection loConnection = urlSend.openConnection();
            loConnection.setDoOutput(true);
            loConnection.setUseCaches(false);
            this.in = new BufferedReader(new InputStreamReader(loConnection.getInputStream()));
        } catch (FileNotFoundException e) {
            System.out.println("error:" + e);            
        } catch (Exception e2) {
            System.out.println("error:" + e2);
        }
    }

    
    /**
     * Read the response
     * @return
     */
    public boolean readLine() {
        try {
            line = this.in.readLine();
            return (line != null);
        } catch (Exception e) {
            System.out.println("error:" + e);
            return false;
        }
    }

    
    /**
     * Close the post/get connection
     */
    public void closePost() {
        try {
            if (in != null) {
                in.close();                
            }
        } catch (Exception e) {
            System.out.println("error:" + e);
        }
    }

    /**
     * Recupera o retorno
     */
    public String getLine() {
        return (line);
    }


}