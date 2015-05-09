package org.apache.taglibs.display;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;

/**
 */
public final class LookupUtil {

	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private LookupUtil(){}
	
    /**
     * This functionality is borrowed from struts, but I've removed some struts
     * specific features so that this tag can be used both in a struts
     * application, and outside of one.
     * 
     * Locate and return the specified bean, from an optionally specified scope,
     * in the specified page context. If no such bean is found, return
     * <code>null</code> instead.
     * 
     * @param pageContext
     *            Page context to be searched
     * @param name
     *            Name of the bean to be retrieved
     * @param scope
     *            Scope to be searched (page, request, session, application) or
     *            <code>null</code> to use <code>findAttribute()</code>
     *            instead
     * 
     * @exception JspException
     *                if an invalid scope name is requested
     */
    static private Object lookup(PageContext pageContext, String name, 
            String scope, String pageParam, Properties prop)  throws JspException {

        Object bean = null;
        if (scope == null)
            bean = pageContext.findAttribute(name);
        else if (scope.equalsIgnoreCase(pageParam))
            bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
        else if (scope.equalsIgnoreCase("request"))
            bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
        else if (scope.equalsIgnoreCase("session"))
            bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
        else if (scope.equalsIgnoreCase("application"))
            bean = pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
        else {
            Object[] objs = { name, scope };
            String msg = MessageFormat.format(prop.getProperty("error.msg.cant_find_bean"), objs);
            throw new JspException(msg);
        }

        return (bean);
    }

    /**
     * This functionality is borrowed from struts, but I've removed some struts
     * specific features so that this tag can be used both in a struts
     * application, and outside of one.
     * 
     * Locate and return the specified property of the specified bean, from an
     * optionally specified scope, in the specified page context.
     * 
     * @param pageContext
     *            Page context to be searched
     * @param name
     *            Name of the bean to be retrieved
     * @param property
     *            Name of the property to be retrieved, or <code>null</code>
     *            to retrieve the bean itself
     * @param scope
     *            Scope to be searched (page, request, session, application) or
     *            <code>null</code> to use <code>findAttribute()</code>
     *            instead
     * 
     * @exception JspException
     *                if an invalid scope name is requested
     * @exception JspException
     *                if the specified bean is not found
     * @exception JspException
     *                if accessing this property causes an
     *                IllegalAccessException, IllegalArgumentException,
     *                InvocationTargetException, or NoSuchMethodException
     */
    static public Object lookup(PageContext pageContext, String name, String property,
            String scope, boolean useDecorator, String pageParam, Decorator dec, Properties prop) throws JspException {
        if (useDecorator && dec != null) {
            
            // First check the decorator, and if it doesn't return a value
            // then check the inner object...
            try {
                if (property == null)
                    return dec;
                return (PropertyUtils.getProperty(dec, property));
            } catch (IllegalAccessException e) {
                Object[] objs = { property, dec };
                throw new JspException(MessageFormat.format(prop.getProperty("error.msg.illegal_access_exception"), objs));
            } catch (InvocationTargetException e) {
                Object[] objs = { property, dec };
                throw new JspException(MessageFormat.format(prop.getProperty("error.msg.invocation_target_exception"),objs));
            } catch (NoSuchMethodException e) {
                // We ignore this puppy and just fall down to the bean lookup below.
            }
        }

        // Look up the requested bean, and return if requested
        Object bean = lookup(pageContext, name, scope, pageParam, prop);
        if (property == null) {
            return (bean);
        }
        if (bean == null) {
            Object[] objs = { name, scope };
            throw new JspException(MessageFormat.format(prop.getProperty("error.msg.cant_find_bean"), objs));
        }

        // Locate and return the specified property
        try {
       		return (PropertyUtils.getProperty(bean, property));	
        } catch (IllegalAccessException e) {
            Object[] objs = { property, name };
            throw new JspException(MessageFormat.format(prop.getProperty("error.msg.illegal_access_exception"), objs));
        } catch (InvocationTargetException e) {
            Object[] objs = { property, name };
            throw new JspException(MessageFormat.format(prop.getProperty("error.msg.invocation_target_exception"), objs));
        } catch (NoSuchMethodException e) {
            Object[] objs = { property, name };
            throw new JspException(MessageFormat.format(prop.getProperty("error.msg.nosuchmethod_exception"), objs));
        }
    }
	
}

