package org.apache.taglibs.display;

import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;

import org.apache.struts.util.RequestUtils;

/**
 * One line description of what this class does.
 * 
 * More detailed class description, including examples of usage if applicable.
 */
public final class StringUtil {
    
	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private StringUtil(){}
	
    /**
     * Replace character at given index with the same character to upper case
     * 
     * @param oldString
     *            old string
     * @param index
     *            of replacement
     * @return String new string
     * @exception StringIndexOutOfBoundsException
     *                &nbsp;
     *  
     */
    public static String toUpperCaseAt(String oldString, int index)
            throws NullPointerException, StringIndexOutOfBoundsException {
        int length = oldString.length();
        String newString = "";

        if (index >= length || index < 0) {
            throw new StringIndexOutOfBoundsException("Index " + index
                    + " is out of bounds for string length " + length);
        }

        //get upper case replacement
        String upper = String.valueOf(oldString.charAt(index)).toUpperCase();

        //avoid index out of bounds
        String paddedString = oldString + " ";

        //get reusable parts
        String beforeIndex = paddedString.substring(0, index);
        String afterIndex = paddedString.substring(index + 1);

        //generate new String - remove padding spaces
        newString = (beforeIndex + upper + afterIndex).substring(0, length);

        return newString;
    }

    
    /**
     * This takes the string that is passed in, and "auto-links" it, it turns
     * email addresses into hyperlinks, and also turns things that looks like
     * URLs into hyperlinks as well. The rules are currently very basic, In Perl
     * regex lingo...
     * 
     * Email: \b\S+\@[^\@\s]+\b URL: (http|https|ftp)://\S+\b
     * 
     * I'm doing this via brute-force since I don't want to be dependent on a
     * third party regex package.
     */    
    public static String autoLink(String data) {
        String work = new String(data);
        int index = -1;
        String results = "";

        if (data == null || data.length() == 0)
            return data;

        // First check for email addresses.
        while ((index = work.indexOf("@")) != -1) {
            int start = 0;
            int end = work.length() - 1;

            // scan backwards...
            for (int i = index; i >= 0; i--) {
                if (Character.isWhitespace(work.charAt(i))) {
                    start = i + 1;
                    break;
                }
            }

            // scan forwards...
            for (int i = index; i <= end; i++) {
                if (Character.isWhitespace(work.charAt(i))) {
                    end = i - 1;
                    break;
                }
            }

            String email = work.substring(start, (end - start + 1));
            results = results + work.substring(0, start) + "<a href=\"mailto:" + email + "\">" + email + "</a>";
            if (end == work.length()) {
                work = "";
            } else {
                work = work.substring(end + 1);
            }
        }

        work = results + work;
        results = "";

        // Now check for urls...
        while ((index = work.indexOf("http://")) != -1) {
            int end = work.length() - 1;

            // scan forwards...
            for (int i = index; i <= end; i++) {
                if (Character.isWhitespace(work.charAt(i))) {
                    end = i - 1;
                    break;
                }
            }
            String url = work.substring(index, (end - index + 1));
            results = results + work.substring(0, index) + "<a href=\"" + url + "\">" + url + "</a>";
            if (end == work.length()) {
                work = "";
            } else {
                work = work.substring(end + 1);
            }
        }

        results += work;
        return results;
    }
 
    
    /**
     * Try to get the current preference of connected user (albertopereto may, 25 2005 ; november, 12 2008)
     */
    public static int getUserPref(PageContext pageContext, String key){
        int p = 0 ;
        
        UserTO uto = (UserTO)pageContext.getAttribute(UserDelegate.CURRENT_USER_SESSION, PageContext.SESSION_SCOPE);
        if (uto!=null){
            PreferenceTO pto = uto.getPreference();
            String pref = pto.getPreference(key);
            
            if (pref.equalsIgnoreCase("true")) {
            	p = 1;	
            } else if (pref.equalsIgnoreCase("false")) {
            	p = 0;
            } else {
            	p = Integer.parseInt(pref);	
            }
        }
        
        return p;
    }
    
    /**
     * This sets up all the default properties for the table tag.
     * @throws JspException
     */
    public static Properties loadDefaultProperties(PageContext ctx) throws JspException {
        Properties prop = new Properties();
                
        prop.setProperty("basic.show.header", RequestUtils.message(ctx, null, null,  "basic.show.header"));
        prop.setProperty("basic.msg.empty_list", RequestUtils.message(ctx, null, null,  "basic.msg.empty_list"));
        prop.setProperty("sort.behavior", RequestUtils.message(ctx, null, null,  "sort.behavior"));
        prop.setProperty("export.banner", RequestUtils.message(ctx, null, null,  "export.banner"));
        
        prop.setProperty("export.banner.sepchar", " | ");
        prop.setProperty("export.csv", "true");
        prop.setProperty("export.csv.label", "CSV");
        prop.setProperty("export.csv.mimetype", "text/csv");
        prop.setProperty("export.csv.include_header", "false");
        prop.setProperty("export.excel", "true");
        prop.setProperty("export.excel.label", "Excel");
        prop.setProperty("export.excel.mimetype", "application/vnd.ms-excel");
        prop.setProperty("export.excel.include_header", "false");
        prop.setProperty("export.xml", "true");
        prop.setProperty("export.xml.label", "XML");
        prop.setProperty("export.xml.mimetype", "text/xml");
        
        prop.setProperty("export.amount", RequestUtils.message(ctx, null, null,  "export.amount"));
        
        prop.setProperty("export.decorated", "true");
        prop.setProperty("paging.banner.include_first_last", "false");
        
        prop.setProperty("paging.banner.placement", RequestUtils.message(ctx, null, null,  "paging.banner.placement"));
        prop.setProperty("paging.banner.item_name", RequestUtils.message(ctx, null, null,  "paging.banner.item_name"));
        prop.setProperty("paging.banner.items_name", RequestUtils.message(ctx, null, null,  "paging.banner.items_name"));
        prop.setProperty("paging.banner.no_items_found", RequestUtils.message(ctx, null, null,  "paging.banner.no_items_found"));
        prop.setProperty("paging.banner.one_item_found", RequestUtils.message(ctx, null, null,  "paging.banner.one_item_found"));
        prop.setProperty("paging.banner.all_items_found", RequestUtils.message(ctx, null, null,  "paging.banner.all_items_found"));
        prop.setProperty("paging.banner.some_items_found", RequestUtils.message(ctx, null, null,  "paging.banner.some_items_found"));
        
        prop.setProperty("paging.banner.first_label", RequestUtils.message(ctx, null, null,  "paging.banner.first_label"));
        prop.setProperty("paging.banner.last_label", RequestUtils.message(ctx, null, null,  "paging.banner.last_label"));
        prop.setProperty("paging.banner.prev_label", RequestUtils.message(ctx, null, null,  "paging.banner.prev_label"));
        prop.setProperty("paging.banner.next_label", RequestUtils.message(ctx, null, null,  "paging.banner.next_label"));
        
        prop.setProperty("paging.banner.group_size", "8");

        prop.setProperty("error.msg.cant_find_bean", RequestUtils.message(ctx, null, null,  "error.msg.cant_find_bean"));
        prop.setProperty("error.msg.invalid_bean", RequestUtils.message(ctx, null, null,  "error.msg.invalid_bean"));
        prop.setProperty("error.msg.no_column_tags", RequestUtils.message(ctx, null, null,  "error.msg.no_column_tags"));
        prop.setProperty("error.msg.illegal_access_exception", RequestUtils.message(ctx, null, null,  "error.msg.illegal_access_exception"));
        prop.setProperty("error.msg.invocation_target_exception", RequestUtils.message(ctx, null, null,  "error.msg.invocation_target_exception"));
        prop.setProperty("error.msg.nosuchmethod_exception", RequestUtils.message(ctx, null, null,  "error.msg.nosuchmethod_exception"));
        prop.setProperty("error.msg.invalid_decorator", RequestUtils.message(ctx, null, null,  "error.msg.invalid_decorator"));
        prop.setProperty("error.msg.invalid_page", RequestUtils.message(ctx, null, null, "error.msg.invalid_page"));

        prop.setProperty("search.title", RequestUtils.message(ctx, null, null, "search.title"));
        prop.setProperty("search.button", RequestUtils.message(ctx, null, null, "search.button"));
        prop.setProperty("showHide.title", RequestUtils.message(ctx, null, null, "showHide.title"));
        prop.setProperty("exportCSV.title", RequestUtils.message(ctx, null, null, "exportCSV.title"));
        
        return prop;
    }    
}