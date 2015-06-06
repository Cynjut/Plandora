/**
 * $Id: ColumnDecorator.java,v 1.9 2010/11/16 23:50:05 albertopereto Exp $
 *
 * Status: Ok
 **/
package org.apache.taglibs.display;

import java.util.Locale;

import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;

public abstract class ColumnDecorator extends Decorator{
    
    public ColumnDecorator(){
        super();
    }

   	public abstract String decorate( Object columnValue );
   
   	public abstract String decorate( Object columnValue, String tag );
   
   	public abstract String contentToSearching(Object columnValue);

   	/**
   	 * This method could be override by subclass in order to append a snippet 
   	 * at the beginning of decorator content.
   	 */
   	public String getPreContent(Object columnValue, String tag) {
   		return null;
   	}

   	/**
   	 * This method could be override by subclass in order to append a snippet 
   	 * at the final of decorator content.
   	 */
   	public String getPostContent(Object columnValue, String tag) {
   		return null;
   	}

   	
   	/**
   	 * This method get a value from resource bundle and can be used for all decorators.
   	 * @param key
   	 * @return
   	 */
   	protected String getBundleMessage(String key){
   	    return getBundleMessage(key, false);
   	}
   	
   	/**
   	 * This method get a value from resource bundle and can be used for all decorators.
   	 */   	
   	protected String getBundleMessage(String key, boolean isTolerant){
   	    String value = "";
		try {   	    
	   	    UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
	   	    if (uto!=null){
	   	    	value = uto.getBundle().getMessage(uto.getLocale(), key);
				if (isTolerant) {
			        if (value.startsWith("???")) {
			            value = key;
			        }		    
				}
	   	    }
        } catch (Exception e) {
            value = "err!";
        }
        return value;
   	}   	
   	
   	
   	/**
   	 * This method gets the current user locale based on user from html session
   	 * @return
   	 */
   	protected Locale getCurrentLocale(){
   	    Locale loc = null; 
   	    UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
   	    if (uto!=null){
   	   	    String language = uto.getLanguage();
   	   	    String country = uto.getCountry();            
   	   	    loc = new Locale(language, country);   	        
   	    }
   	    return loc;
   	}
   	
}

