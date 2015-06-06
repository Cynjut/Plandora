/**
 * Todo:
 *   - Investigate using styles rather then color specific attributes for
 *     displaying the table.
 *
 *
 * $Id: TableTag.java,v 1.55 2011/01/25 20:12:20 albertopereto Exp $
 **/
package org.apache.taglibs.display;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;

/**
 * This tag takes a list of objects and creates a table to display those
 * objects. With the help of column tags, you simply provide the name of
 * properties (get Methods) that are called against the objects in your list
 * that gets displayed [[reword that...]]
 * 
 * This tag works very much like the struts iterator tag, most of the attributes
 * have the same name and functionality as the struts tag.
 * 
 * Simple Usage:
 * <p>
 * 
 * <osiris:list name="list" > <osiris:column property="title" /> <osiris:column
 * property="code" /> <osiris:column property="dean" /> </osiris:list>
 * 
 * More Complete Usage:
 * <p>
 * 
 * <osiris:list name="list" pagesize="100"> <osiris:column property="title"
 * title="College Title" width="60%" sort="true"
 * href="/osiris/pubs/college/edit.page" paramId="OID" paramProperty="OID" />
 * <osiris:column property="code" width="10%" sort="true"/> <osiris:column
 * property="primaryOfficer.name" title="Dean" width="30%" /> <osiris:column
 * property="active" sort="true" /> </osiris:list>
 * 
 * 
 * Attributes:
 * <p>
 * 
 * collection name property scope length offset pagesize decorator
 * 
 * 
 * HTML Pass-through Attributes
 * 
 * There are a number of additional attributes that just get passed through to
 * the underlying HTML table declaration. With the exception of the following
 * few default values, if these attributes are not provided, they will not be
 * displayed as part of the <table ...> tag.
 * 
 * width - defaults to "100%" if not provided border - defaults to "0" if not
 * provided cellspacing - defaults to "0" if not provided cellpadding - defaults
 * to "2" if not provided align background bgcolor frame height hspace rules
 * summary vspace
 * 
 * Notes: - We rely on struts goodies to help us deal with a variety of things
 * we can iterator over (Collections, Arrays, etc...), and we use their tools to
 * allow us to fetch properties from beans.
 */
public class TableTag extends TemplateTag {

	private static final long serialVersionUID = 1L;
	
    public static final int SORT_ORDER_DECENDING = 1;

    public static final int SORT_ORDER_ASCEENDING = 2;

    private List<ColumnTag> columns = new ArrayList<ColumnTag>(10);

    private Object list = null;

    private Decorator dec = null;

    private String name = null;

    private String property = null;

    private String length = null;

    private String offset = null;

    private String scope = null;

    private String pagesize = null;

    private String decorator = null;

    private String width = null;

    private String border = null;

    private String cellspacing = null;

    private String cellpadding = null;

    private String align = null;

    private String background = null;

    private String bgcolor = null;

    private String frame = null;

    private String height = null;

    private String hspace = null;

    private String vspace = null;

    private String clazz = null;

    private String requestURI = null;

    private Properties prop = null;
   
    private String completeEmpty = null;
    
    /** This attribute contain the number of columns of table */
    private int columnsNumber = 0;
    
    /** This variable hold the value of the data has to be displayed */
    private SmartListHelper helper = null;

    private int sortColumn = -1;

    private int sortOrder = SORT_ORDER_ASCEENDING;

    private int pageNumber = 1;
    
    private String filterContent = null; 

    private String filterComboSelected = null;
    
    
    
    
    /**
     * Returns the offset that the person provided as an int. If the user does
     * not provide an offset, or we can't figure out what they are trying to
     * tell us, then we default to 0.
     * 
     * @return the number of objects that should be skipped while looping
     *         through the list to display the table
     */
    private int getOffsetValue() {
        int offsetValue = 0;

        if (this.offset != null) {
            try {
                offsetValue = Integer.parseInt(this.offset);
            } catch (NumberFormatException e) {
                Integer offsetObject = (Integer) pageContext.findAttribute(offset);
                if (offsetObject == null) {
                    offsetValue = 0;
                } else {
                    offsetValue = offsetObject.intValue();
                }
            }
        }
        if (offsetValue < 0) {
            offsetValue = 0;
        }

        return offsetValue;
    }

    /**
     * Returns the length that the person provided as an int. If the user does
     * not provide a length, or we can't figure out what they are trying to tell
     * us, then we default to 0.
     * 
     * @return the maximum number of objects that should be shown while looping
     *         through the list to display the values. 0 means show all objects.
     */

    private int getLengthValue() {
        int lengthValue = 0;

        if (this.length != null) {
            try {
                lengthValue = Integer.parseInt(this.length);
            } catch (NumberFormatException e) {
                Integer lengthObject = (Integer) pageContext.findAttribute(length);
                if (lengthObject == null) {
                    lengthValue = 0;
                } else {
                    lengthValue = lengthObject.intValue();
                }
            }
        }
        if (lengthValue < 0) {
            lengthValue = 0;
        }

        return lengthValue;
    }

    /**
     * Returns the pagesize that the person provided as an int. If the user does
     * not provide a pagesize, or we can't figure out what they are trying to
     * tell us, then we default to 0.
     * 
     * @return the maximum number of objects that should be shown on any one
     *         page when displaying the list. Setting this value also indicates
     *         that the person wants us to manage the paging of the list.
     */
    private int getPagesizeValue() {
        int pagesizeValue = 0;

        if (this.pagesize != null) {
            try {
                pagesizeValue = Integer.parseInt(this.pagesize);
            } catch (NumberFormatException e) {
                Integer pagesizeObject = (Integer) pageContext.findAttribute(this.pagesize);
                if (pagesizeObject == null) {
                    
                    //try to get the current preference of connected user (albertopereto may, 25 2005)
                    pagesizeValue = StringUtil.getUserPref(pageContext, this.pagesize);
                    
                } else {
                    pagesizeValue = pagesizeObject.intValue();
                }
            }
        }

        if (pagesizeValue < 0) {
            pagesizeValue = 0;
        }

        return pagesizeValue;
    }

    // ---------------------------------------- Communication with interior tags

    /**
     * Called by interior column tags to help this tag figure out how it is
     * supposed to display the information in the List it is supposed to display
     * 
     * @param obj
     *            an internal tag describing a column in this tableview
     */
    public void addColumn(ColumnTag obj) {
        int colNum  = obj.getColumnsNumber();
        for (int i = 0; i<colNum; i++) {
            columnsNumber++;
        }
        columns.add(obj);        
    }

    // --------------------------------------------------------- Tag API methods

    /**
     * When the tag starts, we just initialize some of our variables, and do a
     * little bit of error checking to make sure that the user is not trying to
     * give us parameters that we don't expect.
     */
    public int doStartTag() throws JspException {
        columns = new ArrayList<ColumnTag>(10);
        columnsNumber = 0;

        
        HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
        
        this.prop = StringUtil.loadDefaultProperties(this.pageContext);

        this.pageNumber = 1;
        if (req.getParameter(this.getPageParam()) != null) {
            try {
                this.pageNumber = Integer.parseInt(req.getParameter(this.getPageParam()));
            } catch (NumberFormatException e) {
                this.pageNumber = 1;
            }
        }

        if (req.getParameter(this.getSortParam()) != null) {
            this.sortColumn = 0;
            try {
                this.sortColumn = Integer.parseInt(req.getParameter(this.getSortParam()));
            } catch (NumberFormatException e) {
                this.sortColumn = 0;
            }

            this.sortOrder = SORT_ORDER_ASCEENDING;
            if (req.getParameter("order") != null) {
                if (req.getParameter("order").equals("dec")) {
                    this.sortOrder = SORT_ORDER_DECENDING;
                }
            }
        }

        if (req.getParameter(this.getFilterParam()) != null) {
            this.filterContent = req.getParameter(this.getFilterParam());                       
        }

        this.filterComboSelected = "";
        if (req.getParameter(this.getFilterComboParam()) != null) {
            this.filterComboSelected = req.getParameter(this.getFilterComboParam());                       
        }
        
        
        return super.doStartTag();
    }

    
    /**
     * Draw the table. This is where everything happens, we figure out what
     * values we are supposed to be showing, we figure out how we are supposed
     * to be showing them, then we draw them.
     */
    public int doEndTag() throws JspException {
    	Timestamp ini = DateUtil.getNow();

    	HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
        if (req.getParameter(this.getShowHideParam()) != null) {
        	this.saveColumnStatus(req);
        }
        
        HashMap<String,ColumnDecorator> decoratorList = new HashMap<String,ColumnDecorator>();
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            String decoratorName = ((ColumnTag) columns.get(c)).getDecorator();
            ColumnDecorator dec = DecoratorUtil.loadColumnDecorator(decoratorName);
            if (dec != null) {
                decoratorList.put(decoratorName, dec);
                dec.init(req.getSession(), this.list);
            }
        }
        
        List viewableData = this.getViewableData(decoratorList);

        // Figure out how we should sort this data, typically we just sort
        // the data being shown, but the programmer can override this behavior
        if (prop.getProperty("sort.behavior").equals(this.getPageParam())) {
            this.sortDataIfNeeded(viewableData);
        }

    	// Get the HTML data
        StringBuffer buf = new StringBuffer(8000);        
        buf.append(this.getHTMLData(viewableData, decoratorList, req));
        write(buf);

   		System.out.println("table [" + this.name + "] time: " + (DateUtil.getNow().getTime() - ini.getTime()));
        
        return EVAL_PAGE;
    }
    
    /**
     * Given an Object, let's do our best to iterate over it
     */
    public static Iterator getIterator(Object o) throws JspException {

        Iterator iterator = null;

        if (o instanceof Collection) {
            iterator = ((Collection) o).iterator();
        } else if (o instanceof Iterator) {
            iterator = (Iterator) o;
        } else if (o instanceof Map) {
            iterator = ((Map) o).entrySet().iterator();
            /*
             * This depends on importing struts.util.* -- see remarks in the
             * import section of the file
             *  } else if( o instanceof Enumeration ) { iterator = new
             * IteratorAdapter( (Enumeration)o ); }
             */
        } else {
            throw new JspException("I do not know how to iterate over '" + o.getClass() + "'.");
        }
        return iterator;
    }

    /**
     * This returns a list of all of the data that will be displayed on the page
     * via the table tag. This might include just a subset of the total data in
     * the list due to to paging being active, or the user asking us to just
     * show a subset, etc...
     * <p>
     * 
     * The list that is returned from here is not the original list, but it does
     * contain references to the same objects in the original list, so that
     * means that we can sort and reorder the list, but we can't mess with the
     * data objects in the list.
     */

    public List getViewableData(HashMap decoratorList) throws JspException {
        List viewableData = new ArrayList();

        //get the current filter criteria (column properties that will be used for filtering)..
        ArrayList<TransferObject> filterCriteria = this.getFilterCriteria();
        
        // Acquire the collection that we are going to iterator over...
        Object collection = this.list;

        if (collection == null) {
            collection = LookupUtil.lookup(this.pageContext, this.name, this.property, this.scope, 
                    false, this.getPageParam(), this.dec, this.prop);
        }

        if (collection == null) {
            collection = new ArrayList();
        }

        if (collection.getClass().isArray()) {
            collection = Arrays.asList((Object[]) collection);
        }

        //set a "num_row" to all elements of list. 
        //This sequence ID is used to identify a row independently of sorting
        Iterator nr = getIterator(collection);
        int numRow = 0;
        while (nr.hasNext()) {
        	TransferObject obj = (TransferObject)nr.next();
        	if (obj.getGridRowNumber()<0) {
            	obj.setGridRowNumber(numRow);
                numRow++;        		
        	} else {
        		break;
        	}
        }
        
        // this test solves the problem of deleting the only element on the
        // last page and reload.        
        if (collection instanceof Collection) {
            Collection c = (Collection) collection;
            if ((c.size() <= ((this.pageNumber - 1) * this.getPagesizeValue()))
                    && (pageNumber > 1)) {
                pageNumber--;
            }
        }

        // Load our table decorator if it is requested
        this.dec = DecoratorUtil.loadDecorator(this.getDecorator());
        if (this.dec != null) {
            this.dec.init(this.pageContext.getSession(), collection);
        }
        if (!prop.getProperty("sort.behavior").equals(this.getPageParam())) {
            // Sort the total list...
            this.sortDataIfNeeded(collection);

            // If they have changed the default sorting behavior of the table
            // tag, and just clicked on a column, then reset their page number

            HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
            if (req.getParameter(this.getSortParam()) != null) {
                if (!prop.getProperty("sort.behavior").equals(this.getPageParam())) {
                    this.pageNumber = 1;
                }
            }
        }

        Iterator iterator = getIterator(collection);
        
        // If they have asked for an subset of the list via the offset or length
        // attributes, then only fetch those items out of the master list.
        int offsetValue = this.getOffsetValue();
        int lengthValue = this.getLengthValue();
        for (int i = 0; i < offsetValue; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }

        int cnt = 0;
        while (iterator.hasNext()) {          
            if (lengthValue > 0 && ++cnt > lengthValue) {
                break;
            }
            Object obj = iterator.next();
            viewableData.add(obj);

            //generate the filter combo if necessary            
            if (this.filterContent==null || this.filterContent.trim().equals("")) {
                for (int j = 0; j < this.getRealColumnsNumber(); j++) {
                    ColumnTag tag = (ColumnTag)this.columns.get(j);                    
                    if (tag.getComboFilter()) {
                        pageContext.setAttribute("smartRow", obj);
                        ColumnDecorator dec = (ColumnDecorator)decoratorList.get(tag.getDecorator());
                        String val = tag.getSearchValue(dec, cnt, this.getPageParam(), this.prop, obj);
                        String buff = this.getFilterList(val);
                        if (buff==null && val!=null && !val.trim().equals("null")) {
                        	this.addFilterList(val);
                        }
                    }
                }                
            }
            
        }
        
        
        // If they have asked for just a page of the data, then use the
        // SmartListHelper to figure out what page they are after, etc...       
        int pagesizeValue = this.getPagesizeValue();
        if (pagesizeValue > 0) {
            if (!(collection instanceof List)) {
                throw new JspException("Paging is not available for collections of type '" + collection.getClass() + "'.");
            }
        }

        //check if the current master list should be filtered...
        ArrayList subCollection = new ArrayList();        
        boolean filter1 = filterCriteria!=null && this.filterContent!=null && !this.filterContent.trim().equals("");
        boolean filter2 = this.getFilterListSize()>0 && this.filterComboSelected!=null && !this.filterComboSelected.trim().equals("");
        if (filter1 || filter2) {
            Iterator it = ((List) collection).iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                pageContext.setAttribute("smartRow", obj);
                
                boolean filter1Add = true;
                if (filter1) {
                	filter1Add = this.canShowLine(filterCriteria, decoratorList, cnt, obj);                        	                    	
                }
                
                boolean filter2Add = true;
                if (filter2) {
                	filter2Add = this.canShowLineByComboFilter(decoratorList, cnt, obj);                        	                    	
                }
                
                if (filter1Add && filter2Add) {
                    subCollection.add(obj);
                }                        	
            }
        } else {
        	subCollection.addAll(((List)collection));	
        }
        
        if (pagesizeValue > 0) {
            helper = new SmartListHelper(subCollection, pagesizeValue, this.prop);
            if (subCollection.size() > 0) {
                helper.setCurrentPage(this.pageNumber);
            }
            iterator = helper.getListForCurrentPage().listIterator();

            viewableData.clear();
            while (iterator.hasNext()) {
                if (lengthValue > 0 && ++cnt > lengthValue) {
                    break;
                }             
                viewableData.add(iterator.next());               
            }        	
        }
        
        return viewableData;
    }

    
    private int getFilterListSize() {
    	int response = 0;
    	HashMap cb = (HashMap)pageContext.getSession().getAttribute(getFilterComboParam()+ "_HASH");
    	if (cb!=null) {
    		response = cb.size();
    	}
    	return response;
	}

	private void addFilterList(String val) {
    	HashMap cb = (HashMap)pageContext.getSession().getAttribute(getFilterComboParam()+ "_HASH");
    	if (cb==null) {
    		cb = new HashMap();
    		pageContext.getSession().setAttribute(getFilterComboParam()+ "_HASH", cb);
    	}
    	cb.put(val, val);
	}

	private String getFilterList(String val) {
    	String response = null;
    	HashMap cb = (HashMap)pageContext.getSession().getAttribute(getFilterComboParam()+ "_HASH");
    	if (cb!=null) {
    		response = (String)cb.get(val);
    	}
		return response;
	}

	private List getFilterListOrdered() {
		List response = null;
    	HashMap cb = (HashMap)pageContext.getSession().getAttribute(getFilterComboParam()+ "_HASH");
    	if (cb!=null) {
    		try {
    	        Object[] comboFilterArray = cb.values().toArray();  
    	        response = Arrays.asList(comboFilterArray);  
    	        Collections.sort(response);                  		
    		} catch(Exception e) {
    			//do nothing...
    		}
    	}
		return response;
	}
	
	/**
     * This method will sort the data in either ascending or decending order
     * based on the user clicking on the column headers.
     */
    private void sortDataIfNeeded(Object viewableData) {
        if (!(viewableData instanceof List)) {
            throw new RuntimeException("This function is only supported if the given collection is a java.util.List.");
        }

        // At this point we have all the objects that are supposed to be shown
        // sitting in our internal list ready to be shown, so if they have
        // clicked on one of the titles, then sort the list in either ascending or
        // decending order...
        HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
        if (req.getParameter(this.getSortParam()) != null) {
	        if (this.sortColumn != -1) {
	
	            if (this.sortColumn >= this.columnsNumber) {
	                this.sortColumn = 0;
	            }
	            

	            ColumnTag tag = null;
	            int incCol = -1 ; int iniCol = 0;
	            for (int c = 0; c < this.getRealColumnsNumber(); c++) {
	            	tag = (ColumnTag) this.columns.get(c);
	            	int tagCols = tag.getColumnsNumber();	            		
            		incCol += tagCols;
	            	if (incCol>=this.sortColumn) {
	            		iniCol = this.sortColumn - (incCol - tagCols) -1;
	            		break;
	            	}
	            }
            

	            // If it is an explicit value, then sort by that, otherwise sort by the property...
	            ColumnDecorator dec = null;
	            try {
	            	dec = DecoratorUtil.loadColumnDecorator(tag.getDecorator());
	            } catch (Exception e) {
	            	dec = null;
	            }

                Collections.sort((List) viewableData, new BeanSorter(tag.getProperty(), dec, 
                		this.sortColumn, iniCol));
	
	            if (this.sortOrder == SORT_ORDER_DECENDING) {
	                Collections.reverse((List) viewableData);
	            }
	        }
        }
    }

	/**
	 * Get fake html to represents the empty rows.
	 * @param size
	 * @return
	 */   
    private StringBuffer getEmptyRows(int size, int colsize){
        StringBuffer response = new StringBuffer("");
        if (this.getCompleteempty()!=null) {
            int ps = this.getPagesizeValue();
            if (size < ps) {
                response.append("<tr class=\"tableRowEven\"><td class=\"tableCell\" align=\"center\" colspan=\"" + (this.getColumnsNumber()) + "\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
                for (int i=size; i<ps; i++) {
                    response.append("<tr><td colspan=\"" + (ps-size) + "\">&nbsp</td></tr>");    
                }
                response.append("</table></td></tr>");
            }            
        }
        return response;
    }

    
    private StringBuffer getHTMLData(List viewableData, HashMap decoratorList, HttpServletRequest req) throws JspException {
        StringBuffer bufRaw = new StringBuffer(8000);
        StringBuffer exportContent = new StringBuffer("");
                
        req.getSession().removeAttribute("EXPORT_CSV_" + this.getName());
        
        int rowcnt = 0;
        bufRaw.append(this.getTableHeader());
        Iterator iterator = viewableData.iterator();

        //generate the title of csv content
		for (int j = 0; j < this.getRealColumnsNumber(); j++) {
            ColumnTag titleTag = (ColumnTag)this.columns.get(j);
            String csvTitle = titleTag.getTitleFromBundle(titleTag.getTitle());
            if (csvTitle==null) {
            	csvTitle = "";
            } else {
            	csvTitle = csvTitle.replaceAll("&nbsp;", "");
            }
            exportContent.append("\"" + csvTitle + "\";");
        }
		exportContent.append("\n");

		
        while (iterator.hasNext()) {
            StringBuffer bufLine = new StringBuffer(2048);
            StringBuffer exportLine = new StringBuffer("");
            Object obj = iterator.next();

            if (this.dec != null) {
                String rt = this.dec.initRow(obj, rowcnt, rowcnt + this.getOffsetValue());
                if (rt != null)
                    bufLine.append(rt);
            }

            // init all collumn decorators..
            Iterator c = decoratorList.values().iterator();
            while(c.hasNext()) {
                ColumnDecorator cd = (ColumnDecorator)c.next();
                cd.initRow(obj, rowcnt, rowcnt + (this.getPagesizeValue() * (this.pageNumber - 1)));
            }

            pageContext.setAttribute("smartRow", obj);

            if (this.dec != null) {
                String rt = this.dec.startRow();
                if (rt != null)
                    bufLine.append(rt);
            }

            // Start building the row to be displayed...
            bufLine.append("<tr");

            if (rowcnt % 2 == 0) {
                bufLine.append(" class=\"tableRowOdd\"");
            } else {
                bufLine.append(" class=\"tableRowEven\"");
            }

            bufLine.append(">\n");

            //draw the body of table
            for (int i = 0; i < this.getRealColumnsNumber(); i++) {
                ColumnTag tag = (ColumnTag)this.columns.get(i);

                //check if maxWord attribute was set with a key that must to be find into user preferences. Alberto (27/09/2005)
                if (tag.getMaxWordsKey()!=null){
                    tag.setMaxWords(StringUtil.getUserPref(pageContext, tag.getMaxWordsKey()));    
                }
                
                // Get the value to be displayed               
                ColumnDecorator dec = (ColumnDecorator)decoratorList.get(tag.getDecorator());
                StringBuffer body = null;
                String exportBody = null;
            	
                try {
                	if (tag instanceof MetaFieldColumnTag) {
                		body = tag.getBody(dec,  ((TransferObject)obj).getGridRowNumber(), this.getPageParam(), this.dec, this.prop);
                		exportBody = tag.getSearchValue(dec,  ((TransferObject)obj).getGridRowNumber(), this.getPageParam(), this.prop, obj);					
					} else {
						body = tag.getBody(dec,  rowcnt + (this.getPagesizeValue() * (this.pageNumber - 1)), this.getPageParam(), this.dec, this.prop);
						exportBody = tag.getSearchValue(dec,  rowcnt + (this.getPagesizeValue() * (this.pageNumber - 1)), this.getPageParam(), this.prop, obj) ;	
					}
                } catch(Exception e) {
                	e.printStackTrace();
                	body = null;
                }

                if (body!=null) {
                    bufLine.append(body);
                	exportLine.append("\"" + com.pandora.helper.StringUtil.formatWordForParam(exportBody) + "\";");
                } else {
                    //this line should be `
                    bufLine = new StringBuffer("");
                    exportLine = null;                    
                    break;
                }
            }
        	

            // Special case, if they didn't provide any columns, then just spit
            // out the object's string representation to the table.
            if (this.getColumnsNumber() == 0) {
                bufLine.append("<td class=\"tableCell\">");
                bufLine.append(obj.toString());
                bufLine.append("</td>");
            }
            
            bufLine.append("</tr>\n");
            
            if (this.dec != null) {
                String rt = this.dec.finishRow();
                if (rt != null)
                    bufLine.append(rt);
            }

            Iterator e = decoratorList.values().iterator();
            while(e.hasNext()) {
                ColumnDecorator cd = (ColumnDecorator)e.next();
                cd.finishRow();
            }                
            
            rowcnt++;                     
            bufRaw.append(bufLine);
            exportContent.append(exportLine + "\n");
        }
        
        if (rowcnt == 0) {
            bufRaw.append("<tr class=\"tableRowOdd\">");
            bufRaw.append("<td class=\"tableCell\" align=\"center\" colspan=\""
                    + (this.getColumnsNumber() + 1) + "\">"
                    + prop.getProperty("basic.msg.empty_list") + "</td></tr>");
        } else {
            //if necessary, complete the blank space with fake lines
            bufRaw.append(this.getEmptyRows(viewableData.size(), this.getRealColumnsNumber()));            
        }

        bufRaw.append("</table>\n");
        
        req.getSession().setAttribute("EXPORT_CSV_" + this.getName(), exportContent);
        
        if (this.dec != null) {
            this.dec.finish();            
        }
        this.dec = null;

        Iterator e = decoratorList.values().iterator();
        while(e.hasNext()) {
            ColumnDecorator cd = (ColumnDecorator)e.next();
            cd.finish();
        }

        return bufRaw;
    }

    
    // --------------------------------------------------------- Utility Methods
    
    /**
     * Generates the table header, including the first row of the table which
     * displays the titles of the various columns
     */
    private String getTableHeader() {
        StringBuffer buf = new StringBuffer(1000);
        int pagesizeValue = this.getPagesizeValue();

        HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
        String url = this.requestURI;
        if (url == null) {
            url = req.getRequestURI();
        } else {
			if (this.filterComboSelected!=null && !this.filterComboSelected.trim().equals("")) {
				url = url + "&" + this.getFilterComboParam() + "=" + this.filterComboSelected;
			}
			if (this.filterContent!=null && !this.filterContent.trim().equals("")) {
				url = url + "&" + this.getFilterParam() + "=" + this.filterContent;
			}
        }

        // determine if the url must be concatenated with a ? or &
        int index = url.indexOf('?');
        if (index == -1) {
        	url = url + "?";
        } else {
        	url = url + "&";
        }

        // If they don't want the header shown for some reason, then stop here.
        if (!this.prop.getProperty("basic.show.header").equals("true")) {
            return buf.toString();
        }

        buf.append("<table ");
        buf.append(this.getTableAttributes());
        buf.append(">\n");
        
    	buf.append("<tr><td colspan=\"" + this.getColumnsNumber() + "\">");

        String floatPanelContent = this.getColumnListToShowHide(req);    	
    	if (helper != null || floatPanelContent!=null) {
        	buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
            buf.append("<tr class=\"tableRowAction\">");    		
    	}

        if (floatPanelContent!=null && !floatPanelContent.equals("")) {
        	buf.append("<td width=\"20\" valign=\"center\" align=\"left\" class=\"tableCellAction\">" +
            				"<a href=\"javascript:void(0);\" onclick=\"openFloatPanel(getContent_" + this.getPageParam() + "());\">" +
            				"<img src=\"../images/tablecols.png\" title=\"" + prop.getProperty("showHide.title") + "\" alt=\"" + prop.getProperty("showHide.title") + "\" border=\"0\"></a>" +
            			"</td>\n" +
                    	"<script language=\"JavaScript\">\n" +
                    	"   function getContent_" + this.getPageParam() + "() { \n" +
                    	"      return \"" + floatPanelContent + "\";\n" +
                    	"	}\n" +
                    	"   function checkvalue_" + this.getPageParam() + "(opt) { \n" +
                    	"      var checkBox = document.getElementById(opt); \n" + 
                    	"      var param = (checkBox.checked?'on':'off');\n" +          		
                    	"      window.location='" + url + this.getShowHideParam() + "=1&" + this.getAnchorParam() + "&' + opt + '=' + param;\n" +            		
                    	"	}\n" + 
            			"</script>\n");
        }

        //create a url to export to csv
    	String exportUrl = "";
    	int operIni = url.indexOf("operation");
    	if (operIni>-1) {
    		int operEnd = url.indexOf("&", operIni);
    		if (operEnd>-1) {
    			exportUrl = url.substring(0, operIni) + "operation=exportGrid" + url.substring(operEnd);
            	buf.append("<td width=\"20\" valign=\"center\" align=\"left\" class=\"tableCellAction\">");

            	buf.append("<a href=\"" + exportUrl + "exportkey=EXPORT_CSV_" + this.getName() + "\" class=\"tableCellHeader\">");
            	buf.append("<img src=\"../images/tableexp.png\" title=\"" + prop.getProperty("exportCSV.title") + "\" alt=\"" + prop.getProperty("exportCSV.title") + "\" border=\"0\"></a>" );
            	buf.append("</td><td>&nbsp;</td>\n");        			
    		}
    	}
        
        if (helper != null) {

        	buf.append("<td width=\"230\" align=\"left\" valign=\"bottom\" class=\"");
        	buf.append("tableCellAction\">");
        	buf.append(helper.getSearchResultsSummary());
        	buf.append("</td>\n");
        	
            ArrayList<TransferObject> filterCrit = this.getFilterCriteria();
            if (filterCrit!=null){
                buf.append(helper.getSearchOptions(url, this.getPageParam(), 
                         this.getAnchorParam(), this.getFilterParam(), filterCrit, this.pageContext));    
            }

            if (this.getFilterListSize()>0 && this.showComboFilter()){
            	List list = this.getFilterListOrdered();
                buf.append(helper.getSearchCombo(url, this.getPageParam(), this.getAnchorParam(), list, 
                        this.getFilterComboParam(), this.filterComboSelected));    
            }
            
            buf.append("<td valign=\"bottom\" align=\"right\" class=\"tableCellAction\">\n");
            
            if (pagesizeValue != 0) {
            	buf.append(helper.getPageNavigationBar(url + 
                    this.getPageParam() + "={0}&tagLibPageChanging=OK&" + this.getAnchorParam()));
            	buf.append("</td>\n");
            }
        }
        
    	if (helper != null || floatPanelContent!=null) {
    		buf.append("</tr></table>\n");    		
    	}
        
        buf.append("</td></tr>\n");    	
        
        buf.append("<tr class=\"tableRowHeader\">");
        int col = 0;
        for (int i = 0; i < this.getRealColumnsNumber(); i++) {
            ColumnTag tag = (ColumnTag)this.columns.get(i); 
            buf.append(tag.getHeader(this.sortOrder, this.getSortParam(), col, this.getAnchorParam(), url));
            col = col + tag.getColumnsNumber();
        }

        // Special case, if they don't provide any columns, then just set
        // the title to a message, telling them to provide some...
        if (this.getColumnsNumber() == 0) {
            buf.append("<td><b>" + prop.getProperty("error.msg.no_column_tags")  + "</b></td>");
        }

        buf.append("</tr>\n");

        return buf.toString();
    }

    /**
     * Takes all the table pass-through arguments and bundles them up as a
     * string that gets took on to the end of the table tag declaration.
     * <p>
     * 
     * Note that we override some default behavior, specifically:
     * <p>
     * 
     * width defaults to 100% if not provided border defaults to 0 if not
     * provided cellspacing defaults to 1 if not provided cellpadding defaults
     * to 2 if not provided
     */
    private String getTableAttributes() {
        StringBuffer results = new StringBuffer();

        if (this.clazz != null) {
            results.append(" class=\"");
            results.append(this.clazz);
            results.append("\"");
        } else {
            results.append(" class=\"table\"");
        }

        if (this.width != null) {
            results.append(" width=\"");
            results.append(this.width);
            results.append("\"");
        } else {
            results.append(" width=\"100%\"");
        }

        if (this.border != null) {
        	results.append(" border=\"0\"");
            //results.append(" border=\"");
            //results.append(this.border);
            //results.append("\" bordercolor=\"#D0D0D0\"");
        } else {
            results.append(" border=\"0\"");
        }

        if (this.cellspacing != null) {
            results.append(" cellspacing=\"");
            results.append(this.cellspacing);
            results.append("\"");
        } else {
            results.append(" cellspacing=\"1\"");
        }

        if (this.cellpadding != null) {
            results.append(" cellpadding=\"");
            results.append(this.cellpadding);
            results.append("\"");
        } else {
            results.append(" cellpadding=\"2\"");
        }

        if (this.align != null) {
            results.append(" align=\"");
            results.append(this.align);
            results.append("\"");
        }

        if (this.background != null) {
            results.append(" background=\"");
            results.append(this.background);
            results.append("\"");
        }

        if (this.bgcolor != null) {
            results.append(" bgcolor=\"");
            results.append(this.bgcolor);
            results.append("\"");
        }

        if (this.frame != null) {
            results.append(" frame=\"");
            results.append(this.frame);
            results.append("\"");
        }

        if (this.height != null) {
            results.append(" height=\"");
            results.append(this.height);
            results.append("\"");
        }

        if (this.hspace != null) {
            results.append(" hspace=\"");
            results.append(this.hspace);
            results.append("\"");
        }

        if (this.getCompleteempty() != null) {
            results.append(" completeempty=\"");
            results.append(this.getCompleteempty());
            results.append("\"");
        }
        
        if (this.vspace != null) {
            results.append(" vspace=\"");
            results.append(this.vspace);
            results.append("\"");
        }

        return results.toString();
    }





    /**
     * Called by the setProperty tag to override some default behavior or text
     * string.
     */
    public void setProperty(String n, String value) {
        this.prop.setProperty(n, value);
    }


    /**
     * Format the http parameter used to store the current page of grid
     * @return
     */
    private String getPageParam(){
        return "page_" + this.getName();
    }

    /**
     * Format the http parameter used to store the current sort status of grid
     * @return
     */
    protected String getSortParam(){
        return "sort_" + this.getName();
    }


    protected String getExportParam(){
        return "export_" + this.getName();
    }
    
    
    /**
     * Format the http parameter used to store the filter criteria by user
     * @return
     */
    private String getFilterParam(){
        return "filter_" + this.getName();
    }

    /**
     * Format the http parameter used to store the filter criteria by user
     * @return
     */
    private String getFilterComboParam(){
        return "filter_combo_" + this.getName();
    }    
    
    /**
     * Format the http parameter used by show/hide columns
     * @return
     */
    private String getShowHideParam(){
        return "showHide_" + this.getName();
    }

    /**
     * Format the http parameter used to store the current anchor
     * @return
     */
    private String getAnchorParam(){
        return "anchor=" + this.getName();
    }    

    
    
    private int getColumnsNumber() {
    	int num = columnsNumber;
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            ColumnTag column = (ColumnTag)columns.get(c);
            if (!column.isVisible()) {
            	num--;	
            }
        }
        return num;
    }
    

    private boolean showComboFilter() {
    	boolean response = false;
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            ColumnTag column = (ColumnTag)columns.get(c);
            if (column.getComboFilter()) {
            	response = true;
            	break;	
            }
        }
        return response;
    }
    
    
    private int getRealColumnsNumber(){
        return this.columns.size();
    }
    
    
    private ArrayList<TransferObject> getFilterCriteria(){
        ArrayList<TransferObject> response = null;
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            ColumnTag column = ((ColumnTag) columns.get(c));
            if (column.getLikeSearching()) {
                TransferObject filter = new TransferObject();
                if (response==null) {
                    response = new ArrayList<TransferObject>();
                }
                filter.setId(column.getProperty());
                filter.setGenericTag(column.getTitle());
                response.add(filter);
            }
        }            
        return response;
    }
    
    private boolean canShowLine(ArrayList filterCriteria, HashMap decoratorList, int rowcnt, Object obj) throws JspException{
        boolean response = false;
        
        //check if current value should be filtered...
        if (filterCriteria!=null && this.filterContent!=null && !this.filterContent.trim().equals("")) {
            Iterator i = filterCriteria.iterator();
            while(i.hasNext()) {
                TransferObject filter = (TransferObject)i.next();
                for (int j = 0; j < this.getRealColumnsNumber(); j++) {
                    ColumnTag tag = (ColumnTag)this.columns.get(j);                    
                    if (filter.getId().equalsIgnoreCase(tag.getProperty())) {
                        ColumnDecorator dec = (ColumnDecorator)decoratorList.get(tag.getDecorator());
                        String content = tag.getSearchValue(dec, rowcnt, this.getPageParam(), this.prop, obj);
                        if (content!=null && content.toLowerCase().indexOf(filterContent.toLowerCase())>=0) {
                            response = true;
                        }
                    }
                    
                }                
            }     
        } else {
            response = true;
        }

      	return response;
    }
    
    private boolean canShowLineByComboFilter(HashMap decoratorList, int rowcnt, Object obj) throws JspException{
        boolean response = false;
        
        //check if current value should be filtered...
        for (int j = 0; j < this.getRealColumnsNumber(); j++) {
            ColumnTag tag = (ColumnTag)this.columns.get(j);                    
            if (tag.getComboFilter()) {
                ColumnDecorator dec = (ColumnDecorator)decoratorList.get(tag.getDecorator());
                String content = tag.getSearchValue(dec, rowcnt, this.getPageParam(), this.prop, obj);
                if (content!=null && this.filterComboSelected.equalsIgnoreCase(content)) {
                    response = true;
                }
            }
        }                

      	return response;
    }    
    
    private String getColumnListToShowHide(HttpServletRequest req){
    	String response = "";
    	
    	this.loadColumnStatus(req);
    	
        for (int c = 0; c < this.getRealColumnsNumber(); c++) {
        	ColumnTag col = (ColumnTag)columns.get(c);
        	if (col.getVisibleProperty()!=null && col.getDescription()!=null) {
        		String title = col.getTitleFromBundle(col.getDescription());
        		response = response + "<input type='checkbox' onclick='checkvalue_" + this.getPageParam() + "(&quot;" + col.getDescription() + "&quot;);' id='" + col.getDescription() + "' name='" + col.getDescription() + "'";
        		if (col.isVisible()) {
        			response = response + " checked";
        		}
        		response = response + " value='on'>" + title + "<br/>";
        	}
        }
                
        return response;
    }
    
    
    private void saveColumnStatus(HttpServletRequest req){
    	try {
        	UserTO uto = (UserTO)this.pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
            PreferenceDelegate pdel = new PreferenceDelegate(); 
            
            for (int c = 0; c < this.getRealColumnsNumber(); c++) {
            	ColumnTag col = (ColumnTag)columns.get(c);
            	if (col.getVisibleProperty()!=null && col.getDescription()!=null && !col.getVisibleProperty().equals("")) {
                	String chk = req.getParameter(col.getDescription());
                	if (chk!=null) {
                    	PreferenceTO pto = new PreferenceTO(col.getVisibleProperty(), chk, uto);
            			uto.getPreference().addPreferences(pto);
            			pto.addPreferences(pto);
            			pdel.insertOrUpdate(pto);
            			col.setVisible(chk.equals("on"));
            			break;
                	}
            	}
            }
            
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    private void loadColumnStatus(HttpServletRequest req){
    	UserTO uto = (UserTO)this.pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
    	
    	if (uto!=null) {
        	PreferenceTO pto = uto.getPreference();
        	if (pto!=null) {
                for (int c = 0; c < this.getRealColumnsNumber(); c++) {
                	ColumnTag col = (ColumnTag)columns.get(c);
                	if (col.getVisibleProperty()!=null && !col.getVisibleProperty().equals("")) {
                		String value = pto.getPreference(col.getVisibleProperty());
                		if (value==null || value.trim().equals("")) {
                			col.setVisible(true);
                		} else {
                			col.setVisible(value.equals("off")?false:true);
                		}
                	}
                }    		        		
        	}
    	}
    }
    
    
    public void reset() {
        this.pageNumber = 1;
        this.sortColumn = 1;
    }

    
    ////////////////////////////////////////////
    public void setName(String newValue) {
        this.name = newValue;
    }
    public String getName() {
        return this.name;
    }

    ////////////////////////////////////////////
    public void setLength(String newValue) {
        this.length = newValue;
    }
    public String getLength() {
        return this.length;
    }

    ////////////////////////////////////////////
    public void setScope(String newValue) {
        this.scope = newValue;
    }
    public String getScope() {
        return this.scope;
    }

    ////////////////////////////////////////////
    public void setDecorator(String newValue) {
        this.decorator = newValue;
    }
    public String getDecorator() {
        return this.decorator;
    }

    ////////////////////////////////////////////
    public void setWidth(String newValue) {
        this.width = newValue;
    }
    public String getWidth() {
        return this.width;
    }

    ////////////////////////////////////////////
    public void setCellspacing(String newValue) {
        this.cellspacing = newValue;
    }
    public String getCellspacing() {
        return this.cellspacing;
    }

    ////////////////////////////////////////////
    public void setAlign(String newValue) {
        this.align = newValue;
    }
    public String getAlign() {
        return this.align;
    }

    ////////////////////////////////////////////
    public void setBgcolor(String newValue) {
        this.bgcolor = newValue;
    }
    public String getBgcolor() {
        return this.bgcolor;
    }

    ////////////////////////////////////////////
    public void setHeight(String newValue) {
        this.height = newValue;
    }
    public String getHeight() {
        return this.height;
    }

    ////////////////////////////////////////////
    public void setVspace(String newValue) {
        this.vspace = newValue;
    }
    public String getVspace() {
        return this.vspace;
    }


    ////////////////////////////////////////////    
    public Object getList() {
        return this.list;
    }
    public void setList(Object newValue) {
        this.list = newValue;
    }

    ////////////////////////////////////////////
    public String getProperty() {
        return this.property;
    }
    public void setProperty(String newValue) {
        this.property = newValue;
    }

    ////////////////////////////////////////////
    public String getOffset() {
        return this.offset;
    }
    public void setOffset(String newValue) {
        this.offset = newValue;
    }

    ////////////////////////////////////////////
    public String getPagesize() {
        return this.pagesize;
    }
    public void setPagesize(String newValue) {
        this.pagesize = newValue;
    }


    ////////////////////////////////////////////
    public String getBorder() {
        return this.border;
    }
    public void setBorder(String newValue) {
        this.border = newValue;
    }

    ////////////////////////////////////////////
    public String getCellpadding() {
        return this.cellpadding;
    }
    public void setCellpadding(String newValue) {
        this.cellpadding = newValue;
    }

    ////////////////////////////////////////////
    public String getBackground() {
        return this.background;
    }
    public void setBackground(String newValue) {
        this.background = newValue;
    }

    ////////////////////////////////////////////
    public String getFrame() {
        return this.frame;
    }
    public void setFrame(String newValue) {
        this.frame = newValue;
    }

    ////////////////////////////////////////////
    public String getHspace() {
        return this.hspace;
    }
    public void setHspace(String newValue) {
        this.hspace = newValue;
    }

    ////////////////////////////////////////////
    public String getStyleClass() {
        return this.clazz;
    }
    public void setStyleClass(String newValue) {
        this.clazz = newValue;
    }

    ////////////////////////////////////////////
    public String getRequestURI() {
        return requestURI;
    }
    public void setRequestURI(String newValue) {
        this.requestURI = newValue;
    }

    ////////////////////////////////////////////    
    public String getCompleteempty() {
        return completeEmpty;
    }
    public void setCompleteempty(String newValue) {
        this.completeEmpty = newValue;
    }
}