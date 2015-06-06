/**
 * $Id: SmartListHelper.java,v 1.13 2009/10/22 16:09:36 albertopereto Exp $
 *
 * Status: Under Development
 **/

package org.apache.taglibs.display;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.jsp.PageContext;

import org.apache.struts.util.RequestUtils;

import com.pandora.TransferObject;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.HtmlUtil;

/**
 * This is a little utility class that the SmartListTag uses to chop up a List
 * of objects into small bite size pieces that are more suitable for display.
 * 
 * This class is a stripped down version of the WebListHelper that we got from
 * Tim Dawson (tdawson@is.com)
 */

class SmartListHelper extends Object {
    private List masterList;

    private int pageSize;

    private int pageCount;

    private int currentPage;

    private Properties prop = null;

    /**
     * Creates a SmarListHelper instance that will help you chop up a list into
     * bite size pieces that are suitable for display.
     */

    protected SmartListHelper(List list, int pageSize, Properties prop) {
        super();

        if (list == null || pageSize < 1) {
            throw new IllegalArgumentException("Bad arguments passed into "
                    + "SmartListHelper() constructor");
        }

        this.prop = prop;
        this.pageSize = pageSize;
        this.masterList = list;
        this.pageCount = this.computedPageCount();
        this.currentPage = 1;
    }

    /**
     * Returns the computed number of pages it would take to show all the
     * elements in the list given the pageSize we are working with.
     */

    protected int computedPageCount() {

        int result = 0;

        if ((this.masterList != null) && (this.pageSize > 0)) {
            int size = this.masterList.size();
            int div = size / this.pageSize;
            int mod = size % this.pageSize;
            result = (mod == 0) ? div : div + 1;
        }

        return result;

    }

    /**
     * Returns the index into the master list of the first object that should
     * appear on the current page that the user is viewing.
     */

    protected int getFirstIndexForCurrentPage() {
        return this.getFirstIndexForPage(this.currentPage);
    }

    /**
     * Returns the index into the master list of the last object that should
     * appear on the current page that the user is viewing.
     */

    protected int getLastIndexForCurrentPage() {
        return this.getLastIndexForPage(this.currentPage);
    }

    /**
     * Returns the index into the master list of the first object that should
     * appear on the given page.
     */

    protected int getFirstIndexForPage(int page) {
        return ((page - 1) * this.pageSize);
    }

    /**
     * Returns the index into the master list of the last object that should
     * appear on the given page.
     */

    protected int getLastIndexForPage(int page) {
        int firstIndex = this.getFirstIndexForPage(page);
        int pageIndex = this.pageSize - 1;
        int lastIndex = this.masterList.size() - 1;

        return Math.min(firstIndex + pageIndex, lastIndex);
    }

    /**
     * Returns a subsection of the list that contains just the elements that are
     * supposed to be shown on the current page the user is viewing.
     */

    protected List getListForCurrentPage() {
        return this.getListForPage(this.currentPage);
    }

    /**
     * Returns a subsection of the list that contains just the elements that are
     * supposed to be shown on the given page.
     */

    protected List getListForPage(int page) {
        List list = new ArrayList(this.pageSize + 1);

        int firstIndex = this.getFirstIndexForPage(page);
        int lastIndex = this.getLastIndexForPage(page);

        for (int i = firstIndex; i <= lastIndex; i++) {
            list.add(this.masterList.get(i));
        }

        return list;
    }

    /**
     * Set's the page number that the user is viewing.
     * 
     * @throws IllegalArgumentException
     *             if the page provided is invalid.
     */

    protected void setCurrentPage(int page) {
        if (page < 1 || page > this.pageCount) {
            Object[] objs = { new Integer(page), new Integer(pageCount) };
            throw new IllegalArgumentException(MessageFormat.format(prop
                    .getProperty("error.msg.invalid_page"), objs));
        }

        this.currentPage = page;
    }

    /**
     * Return the little summary message that lets the user know how many
     * objects are in the list they are viewing, and where in the list they are
     * currently positioned. The message looks like:
     * 
     * nnn <item(s)>found, displaying nnn to nnn.
     * 
     * <item(s)>is replaced by either itemName or itemNames depending on if it
     * should be signular or plurel.
     */

    protected String getSearchResultsSummary() {

        if (this.masterList.size() == 0) {
            Object[] objs = { prop.getProperty("paging.banner.items_name") };
            return MessageFormat.format(prop.getProperty("paging.banner.no_items_found"), objs);
            
        } else if (this.masterList.size() == 1) {
            Object[] objs = { prop.getProperty("paging.banner.item_name") };
            return MessageFormat.format(prop.getProperty("paging.banner.one_item_found"), objs);
            
        } else if (this.getFirstIndexForCurrentPage() == this.getLastIndexForCurrentPage()) {
            Object[] objs = { new Integer(this.masterList.size()), prop.getProperty("paging.banner.items_name"), prop.getProperty("paging.banner.items_name") };
            return MessageFormat.format(prop.getProperty("paging.banner.all_items_found"), objs);
            
        } else {
            Object[] objs = { new Integer(this.masterList.size()), prop.getProperty("paging.banner.items_name"), 
                    new Integer(this.getFirstIndexForCurrentPage() + 1),new Integer(this.getLastIndexForCurrentPage() + 1) };
            return MessageFormat.format(prop.getProperty("paging.banner.some_items_found"), objs);
        }
    }
 
    protected String getSearchOptions(String url, String pageParam, String anchorParam, String filterParam, 
    		ArrayList<TransferObject> filterCrit, PageContext ctx) {
    	
    	NoteIcon note = new NoteIcon();
        String fieldList = "";
        try {
            if (filterCrit!=null) {
                Iterator<TransferObject> i = filterCrit.iterator();
                while(i.hasNext()) {
                    TransferObject filter = i.next();
                    String label = RequestUtils.message(ctx, null, null, filter.getGenericTag()); //get title label
                    if (label==null) {
                        label = filter.getGenericTag();
                    }
                    if (!fieldList.equals("")){
                        fieldList = fieldList + ", ";
                    }
                    fieldList = fieldList + "[" + label + "]";                    
                }
                
                fieldList = RequestUtils.message(ctx, null, null, "search.fields") + " " + fieldList;
                fieldList = note.getContent(fieldList, null);
            }            
        } catch (Exception e){
            fieldList = "";
        }
        
        String searchValue = ctx.getRequest().getParameter(filterParam);
        if (searchValue==null) {
            searchValue = "";
        }
        
        return "<td>" +
					"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr class=\"tableRowAction\">" +
						"<td width=\"40\" align=\"right\" class=\"tableCellAction\">" + prop.getProperty("search.title") + ": </td>" +
						"<td width=\"105\" class=\"tableCellAction\">&nbsp;&nbsp;" +
							"<input type=\"hidden\" name=\"searchText\" value=\"OK\">" +
							"<input maxlength=\"30\" name=\"" + filterParam + "\" size=\"10\" value=\"" + searchValue + "\" class=\"textBoxSmall\">&nbsp;" +
							  "<input type=\"submit\" name=\"filter\" onclick=\"javascript:gridTextFilterRefresh('" + url + "', '" + pageParam + "', '" + filterParam + "');\" value=\"" + prop.getProperty("search.button") + "\" class=\"button\">" +
						"</td>" +
						"<td><div class=\"tableCellAction\">" + fieldList + "</div>" +
			        "</tr></table>" +
		       "</td>";
    }
    
    protected String getSearchCombo(String url, String pgParam, String anchorParam, List orderedList, String pageParam, String selected) {
    	String list = " | |";
    	
        if (orderedList!=null && orderedList.size()>0) {     
        	Iterator<String> i = orderedList.iterator();
        	while(i.hasNext()) {
        		String item = i.next();
        		list = list + item + "|" + item + "|"; 
        	}    		        	
        }
    	    	
    	String comboHtml = HtmlUtil.getComboBox(pageParam, list, "textBoxSmall", selected, null);
        return "<td>" +
					"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr class=\"tableRowAction\">" +
						"<td align=\"left\" class=\"tableCellAction\">&nbsp;&nbsp;" + comboHtml + "&nbsp;" +
							"<input type=\"submit\" name=\"filterCombo\" onclick=\"javascript:gridComboFilterRefresh('" + url + "', '" + pgParam + "', '" + pageParam + "');\"" + 						
							" value=\"" + prop.getProperty("search.button") + "\" class=\"button\">" +
						"</td>" +
			        "</tr></table>" +
		       "</td>";
    }
    
    
    /**
     * Returns a string containing the nagivation bar that allows the user to
     * move between pages within the list.
     * 
     * The urlFormatString should be a URL that looks like the following:
     * 
     * http://.../somepage.page?page={0}
     */
    protected String getPageNavigationBar(String urlFormatString) {
        MessageFormat form = new MessageFormat(urlFormatString);

        int maxPages = 8;

        try {
            maxPages = Integer.parseInt(prop.getProperty("paging.banner.group_size"));
        } catch (NumberFormatException e) {
            // Don't care, we will just default to 8.
        }

        int currentPage = this.currentPage;
        int pageCount = this.pageCount;
        int startPage = 1;
        int endPage = maxPages;

        if (pageCount == 1 || pageCount == 0) {
            return "<b>1</b>";
        }

        if (currentPage < maxPages) {
            startPage = 1;
            endPage = maxPages;
            if (pageCount < endPage) {
                endPage = pageCount;
            }
        } else {
            startPage = currentPage;
            while (startPage + maxPages > (pageCount + 1)) {
                startPage--;
            }

            endPage = startPage + (maxPages - 1);
        }

        boolean includeFirstLast = prop.getProperty("paging.banner.include_first_last").equals("true");

        String msg = "";
        if (currentPage == 1) {
            if (includeFirstLast) {
                msg += "[" + prop.getProperty("paging.banner.first_label")
                        + "/" + prop.getProperty("paging.banner.prev_label") + "] ";
            } else {
                msg += "[" + prop.getProperty("paging.banner.prev_label") + "] ";
            }
        } else {
            Object[] objs = { new Integer(currentPage - 1) };
            Object[] v1 = { new Integer(1) };
            if (includeFirstLast) {
                msg += "[<a href=\"" + form.format(v1) + "\">"
                        + prop.getProperty("paging.banner.first_label")
                        + "</a>/<a href=\"" + form.format(objs) + "\">"
                        + prop.getProperty("paging.banner.prev_label")
                        + "</a>] ";
            } else {
                msg += "[<a href=\"" + form.format(objs) + "\">"
                        + prop.getProperty("paging.banner.prev_label")
                        + "</a>] ";
            }
        }

        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                msg += "<b>" + i + "</b>";
            } else {
                Object[] v = { new Integer(i) };
                msg += "<a href=\"" + form.format(v) + "\">" + i + "</a>";
            }

            if (i != endPage) {
                msg += ", ";
            } else {
                msg += " ";
            }
        }

        if (currentPage == pageCount) {
            if (includeFirstLast) {
                msg += "[" + prop.getProperty("paging.banner.next_label") + "/"
                        + prop.getProperty("paging.banner.last_label") + "] ";
            } else {
                msg += "[" + prop.getProperty("paging.banner.next_label")
                        + "] ";
            }
        } else {
            Object[] objs = { new Integer(currentPage + 1) };
            Object[] v1 = { new Integer(pageCount) };
            if (includeFirstLast) {
                msg += "[<a href=\"" + form.format(objs) + "\">"
                        + prop.getProperty("paging.banner.next_label")
                        + "</a>/<a href=\"" + form.format(v1) + "\">"
                        + prop.getProperty("paging.banner.last_label")
                        + "</a>] ";
            } else {
                msg += "[<a href=\"" + form.format(objs) + "\">"
                        + prop.getProperty("paging.banner.next_label")
                        + "</a>] ";
            }
        }

        return msg;
    }
}