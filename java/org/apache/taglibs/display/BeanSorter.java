/**
 * $Id: BeanSorter.java,v 1.8 2010/06/21 17:30:21 albertopereto Exp $
 *
 * Status: Under Development
 *
 * Todo
 *   - implementation
 *   - documentation (javadoc, examples, etc...)
 *   - junit test cases
 **/

package org.apache.taglibs.display;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import com.pandora.AdditionalFieldTO;
import com.pandora.CustomFormTO;

/**
 * This utility class is used to sort the list that the table is viewing based
 * on arbitrary properties of objects contained in that list. The only
 * assumption made is that the object returned by the property is either a
 * native type, or implements the Comparable interface.
 * 
 * If the property does not implement Comparable, then this little sorter object
 * will just assume that all objects are the same, and quietly do nothing (so
 * you should check for Comparable objecttypes elsewhere as this object won't
 * complain).
 */
class BeanSorter extends Object implements Comparator {
    private String property;
    private Decorator dec;
    
    private int relativeCol;    

    /**
     * BeanSorter is a decorator of sorts, you need to initialize it with the
     * property name of the object that is to be sorted (getXXX method name).
     * This property should return a Comparable object.
     */
    protected BeanSorter(String property, Decorator dec, int rc, int relative) {
        this.property = property;
        this.dec = dec;
        this.relativeCol = relative;
    }

    /**
     * Compares two objects by first fetching a property from each object and
     * then comparing that value. If there are any errors produced while trying
     * to compare these objects then a RunTimeException will be thrown as any
     * error found here will most likely be a programming error that needs to be
     * quickly addressed (like trying to compare objects that are not
     * comparable, or trying to read a property from a bean that is invalid,
     * etc...)
     * 
     * @throws RuntimeException
     *             if there are any problems making a comparison of the object
     *             properties.
     */
    public int compare(Object o1, Object o2) throws RuntimeException {
        if (property == null) {
            throw new NullPointerException("Null property provided which "
                    + "prevents BeanSorter sort");
        }

        try {
            Object p1 = null;
            Object p2 = null;

            // If they have supplied a decorator, then make sure and use it for
            // the sorting as well... 
            if (this.dec != null) {
                try {
                    dec.initRow(o1, -1, -1);
                    Object value1 = PropertyUtils.getProperty(o1, property);
                    p1 = ((ColumnDecorator)dec).contentToSearching(value1);
                    if (p1==null) {
                    	p1 = value1;	
                    }
                    
                    dec.initRow(o2, -1, -1);
                    Object value2 = PropertyUtils.getProperty(o2, property);
                    p2 = ((ColumnDecorator)dec).contentToSearching(value2);
                    if (p2==null) {
                    	p2 = value2;
                    }

                } catch (Exception e) {
                	p1 = PropertyUtils.getProperty(o1, property);
                    p2 = PropertyUtils.getProperty(o2, property);
                }

            } else {                
            	p1 = this.getCustomField(o1);
            	if (p1==null) {
            		p1 = PropertyUtils.getProperty(o1, property);
            	}

            	p2 = this.getCustomField(o2);
            	if (p2==null) {
            		p2 = PropertyUtils.getProperty(o2, property);               		
            	}
            }

            if (p1 instanceof Comparable && p2 instanceof Comparable) {
                Comparable c1 = (Comparable) p1;
                Comparable c2 = (Comparable) p2;
                return c1.compareTo(c2);
            } else if (p1 == null) {
                return -1;
            } else if (p2 == null) {
                return 1;
            } else if (p1 instanceof Boolean) {
                return p1.toString().compareTo(p2.toString());

            } else {

                throw new RuntimeException("Object returned by property \""
                        + property + "\" is not a Comparable object");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException thrown while "
                    + "trying to fetch property \"" + property
                    + "\" during sort");
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "InvocationTargetException thrown while "
                            + "trying to fetch property \"" + property
                            + "\" during sort");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("NoSuchMethodException thrown while "
                    + "trying to fetch property \"" + property
                    + "\" during sort");
        }
    }

    /**
     * Is this Comparator the same as another one...
     */
    public boolean equals(Object obj) {
        boolean resp = false;
        
        if (obj == this) {
            resp = true;
            
        } else {
        
            if (obj instanceof BeanSorter) {
                if (this.property != null) {
                    resp = this.property.equals(((BeanSorter) obj).property);
                } else {
                    resp = false;
                }
            } else {
                resp = false;
            }
        }

        return resp;
    }
    
    
    private String getCustomField(Object obj){
    	String response = null;
    	
    	if (obj instanceof CustomFormTO) {
    		CustomFormTO cfto = (CustomFormTO)obj;
    		if (cfto!=null) {
    			Vector list = cfto.getAdditionalFields();
    			if (list!=null && list.size()>0) {
    				AdditionalFieldTO afto = (AdditionalFieldTO)list.get(this.relativeCol);
    				if (afto!=null) {
    					if (afto.getMetaField()!=null) {
    						response = afto.getMetaField().getValueByKey(afto.getValue());	
    					} else {
    						response = afto.getValue();	
    					}
    				}
    			}
    		}
		}
    	return response;
    }
}