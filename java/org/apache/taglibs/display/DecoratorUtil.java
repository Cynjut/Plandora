package org.apache.taglibs.display;

import javax.servlet.jsp.JspException;

/**
 */
public final class DecoratorUtil {

	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private DecoratorUtil(){}
	
    /**
     * If the user has specified a decorator, then this method takes care of
     * creating the decorator (and checking to make sure it is a subclass of the
     * TableDecorator object). If there are any problems loading the decorator
     * then this will throw a JspException which will get propogated up the
     * page.
     */
	public static Decorator loadDecorator(String decorator) throws JspException {
	    
        if (decorator == null || decorator.length() == 0){
            return null;
        }
 
        try {
            Class c = Class.forName(decorator);
            if (!Class.forName("org.apache.taglibs.display.Decorator").isAssignableFrom(c)) {
                throw new JspException("error.msg.invalid_decorator");
            }

            return (Decorator) c.newInstance();
        } catch (Exception e) {
            throw new JspException(e.toString());
        }
    }
    
    /**
     * paulsenj:columndecorator If the user has specified a column decorator,
     * then this method takes care of creating the decorator (and checking to
     * make sure it is a subclass of the ColumnDecorator object). If there are
     * any problems loading the decorator then this will throw a JspException
     * which will get propogated up the page.
     */
    public static ColumnDecorator loadColumnDecorator(String columnDecorator)  throws JspException {
        if (columnDecorator == null || columnDecorator.length() == 0) {
            return null;
        }
            
        try {
            Class c = Class.forName(columnDecorator);

            // paulsenj - removed 'jakarta' from class name
            if (!Class.forName("org.apache.taglibs.display.ColumnDecorator").isAssignableFrom(c)){
                throw new JspException("column decorator class is not a subclass of ColumnDecorator.");
            }
            return (ColumnDecorator) c.newInstance();
        } catch (Exception e) {
            throw new JspException(e.toString());
        }
    }
	
}
