package com.pandora.gui.taglib.table;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.taglibs.display.ColumnDecorator;
import org.apache.taglibs.display.Decorator;

import com.pandora.AdditionalFieldTO;
import com.pandora.CustomFormTO;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PTableSorter extends Object implements Comparator {

    private String property;
    private Decorator dec;
    private int relativeCol;
    private String orderBy;
    

    protected PTableSorter(String property, Decorator dec, int relative, String order) {
        this.property = property;
        this.dec = dec;
        this.relativeCol = relative;
        this.orderBy = order;
    }

    public int compare(Object ob1, Object ob2) throws RuntimeException {
        Object p1 = null;
        Object p2 = null;
    	
        try {
            if (property != null) {
                if (this.dec != null) {
                    try {
                        dec.initRow(ob1, -1, -1);
                        Object value1 = PropertyUtils.getProperty(ob1, property);
                        p1 = ((ColumnDecorator)dec).contentToSearching(value1);
                        if (p1==null) {
                        	p1 = value1;	
                        }
                        
                        dec.initRow(ob2, -1, -1);
                        Object value2 = PropertyUtils.getProperty(ob2, property);
                        p2 = ((ColumnDecorator)dec).contentToSearching(value2);
                        if (p2==null) {
                        	p2 = value2;
                        }

                    } catch (Exception e) {
                    	p1 = PropertyUtils.getProperty(ob1, property);
                        p2 = PropertyUtils.getProperty(ob2, property);
                    }

                } else {                
                	p1 = this.getCustomField(ob1);
                	if (p1==null) {
                		if (property.indexOf(".")>-1) {
                    		p1 = (String)PTableHelper.getDataByReflexion(property, ob1);                			
                		} else {
                			p1 = PropertyUtils.getProperty(ob1, property);	
                		}
                	}

                	p2 = this.getCustomField(ob2);
                	if (p2==null) {
                		if (property.indexOf(".")>-1) {
                    		p2 = (String)PTableHelper.getDataByReflexion(property, ob2);                			
                		} else {
                			p2 = PropertyUtils.getProperty(ob2, property);	
                		}
                	}
                }

                if (p1 instanceof Comparable && p2 instanceof Comparable) {
                    Comparable c1 = (Comparable) p1;
                    Comparable c2 = (Comparable) p2;
                    if (orderBy.equals(PTableTag.SORT_ORDER_ASCEENDING+"")) {
                    	return c1.compareTo(c2);	
                    } else {
                    	return c2.compareTo(c1);	
                    }
                    
                } else if (p1 == null) {
                	if (orderBy.equals(PTableTag.SORT_ORDER_ASCEENDING+"")) {
                		return -1;	
                	} else {
                		return 1;
                	}
                    
                } else if (p2 == null) {
                	if (orderBy.equals(PTableTag.SORT_ORDER_ASCEENDING+"")) {
                		return 1;	
                	} else {
                		return -1;
                	}
                    
                } else if (p1 instanceof Boolean) {
                	if (orderBy.equals(PTableTag.SORT_ORDER_ASCEENDING+"")) {
                		return p1.toString().compareTo(p2.toString());	
                	} else {
                		return p2.toString().compareTo(p1.toString());
                	}
                } else {
                    throw new RuntimeException("Object returned by property \"" + property + "\" is not a Comparable object");
                }            	
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException thrown while trying to fetch property \"" + property  + "\" during sort");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("InvocationTargetException thrown while trying to fetch property \"" + property + "\" during sort");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("NoSuchMethodException thrown while trying to fetch property \"" + property + "\" during sort");
        }
        return 0;
    }

    public boolean equals(Object obj) {
        boolean resp = false;
        
        if (obj == this) {
            resp = true;
            
        } else {
        
            if (obj instanceof PTableSorter) {
                if (this.property != null) {
                    resp = this.property.equals(((PTableSorter) obj).property);
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
    			Vector<AdditionalFieldTO> list = cfto.getAdditionalFields();
    			if (list!=null && list.size()>0) {
    				AdditionalFieldTO afto = list.get(this.relativeCol);
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
