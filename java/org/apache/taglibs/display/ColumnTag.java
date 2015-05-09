package org.apache.taglibs.display;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.helper.LogUtil;

/**
 * This tag works hand in hand with the SmartListTag to display a list of 
 * objects.  This describes a column of data in the SmartListTag.  There can
 * be any (reasonable) number of columns that make up the list.
 *
 * <p>This tag does no work itself, it is simply a container of information.  The
 * TableTag does all the work based on the information provided in the
 * attributes of this tag.
 * <p>
 * Usage:</p><p>
 *   &lt;display:column property="title"
 *	 	           title="College Title" width="33%"
 *               href="/osiris/pubs/college/edit.page"
 *		           paramId="OID"
 *               paramProperty="OID" /&gt;
 * </p><p>
 * Attributes:<pre>
 *
 *   property       - the property method that is called to retrieve the 
 *                    information to be displayed in this column.  This method
 *                    is called on the current object in the iteration for
 *                    the given row.  The property format is in typical struts
 *                    format for properties (required)
 *
 *   title          - the title displayed for this column.  if this is omitted
 *                    then the property name is used for the title of the column
 *                    (optional)
 *
 *   width          - the width of the column (gets passed along to the html
 *                    td tag). (optional)
 *
 *   decorator      - a class that should be used to "decorate" the underlying
 *                    object being displayed. If a decorator is specified for
 *                    the entire table, then this decorator will decorate that
 *                    decorator. (optional)
 *
 *   autolink       - if set to true, then any email addresses and URLs found
 *                    in the content of the column are automatically converted
 *                    into a hypertext link.
 *
 *   href           - if this attribute is provided, then the data that is 
 *                    shown for this column is wrapped inside a &lt;a href&gt;
 *                    tag with the url provided through this attribute.  
 *                    Typically you would use this attribute along with one
 *                    of the struts-like param attributes below to create
 *                    a dynamic link so that each row creates a different
 *                    URL based on the data that is being viewed. (optional)
 *
 *   paramId        - The name of the request parameter that will be dynamically
 *                    added to the generated href URL. The corresponding value 
 *                    is defined by the paramProperty and (optional) paramName 
 *                    attributes, optionally scoped by the paramScope attribute.
 *                    (optional)
 *
 *   paramName      - The name of a JSP bean that is a String containing the 
 *                    value for the request parameter named by paramId (if 
 *                    paramProperty is not specified), or a JSP bean whose 
 *                    property getter is called to return a String (if 
 *                    paramProperty is specified). The JSP bean is constrained 
 *                    to the bean scope specified by the paramScope property, 
 *                    if it is specified.  If paramName is omitted, then it is
 *                    assumed that the current object being iterated on is the
 *                    target bean. (optional)
 *
 *   paramProperty  - The name of a property of the bean specified by the 
 *                    paramName attribute (or the current object being iterated
 *                    on if paramName is not provided), whose return value must 
 *                    be a String containing the value of the request parameter 
 *                    (named by the paramId attribute) that will be dynamically 
 *                    added to this href URL. (optional)
 *
 *   paramScope     - The scope within which to search for the bean specified by
 *                    the paramName attribute. If not specified, all scopes are 
 *                    searched.  If paramName is not provided, then the current
 *                    object being iterated on is assumed to be the target bean.
 *                    (optional)
 *
 *
 *   maxLength      - If this attribute is provided, then the column's displayed
 *                    is limited to this number of characters.  An elipse (...)
 *                    is appended to the end if this column is linked, and the
 *                    user can mouseover the elipse to get the full text.
 *                    (optional)
 *
 *   maxWords       - If this attribute is provided, then the column's displayed
 *                    is limited to this number of words.  An elipse (...) is
 *                    appended to the end if this column is linked, and the user
 *                    can mouseover the elipse to get the full text. (optional)
 * </pre></p>
 */
public class ColumnTag extends BodyTagSupport implements Cloneable {
   
	private static final long serialVersionUID = 1L;
	
   /** */
   private String property;
   /** */
   private String title;
   /** */
   private String sort;
   /** */
   private String autolink;

   private String description;
   
   /** */
   private String href;
   /** */
   private String paramId;
   /** */
   private String paramName;
   /** */
   private String paramProperty;
   /** */
   private String paramScope;
   
   /** This tag can be used by decorator for multiples purposes (alberto/may2004) */
   private String tag;
   
   /** */
   private int maxLength;
   /** */
   private int maxWords;
   /** */
   private String maxWordsKey;   
   /** */
   private String width;
   /** */
   private String align;
   /** */
   private String background;
   /** */
   private String bgcolor;
   /** */
   private String height;
   /** */
   private String nowrap;
   /** */
   private String valign;
   /** */
   private String clazz;
   /** */
   private String headerClazz;
   /** */
   private String value;
   /** */
   //private String doubleQuote ;
   /** */
   private String decorator;

   private boolean likeSearching = false;
   
   private boolean visible = true;
   
   private String visibleProperty = "";
  
   private boolean comboFilter = false;
   
   /**
    * Return the pointer to TableTag related with Column Tag
    * @return
 * @throws JspException
    */
   public Object getTableTag() throws JspException{
       Object parent = this.getParent();       
       boolean foundTableTag = false;
       
       while (!foundTableTag) {
           if( parent == null ) {
               throw new JspException( "Can not use column tag outside of a TableTag." );
           }

           if( !( parent instanceof TableTag ) ) {
               if (parent instanceof TagSupport) {
                   parent = ((TagSupport) parent).getParent ();
               } else {
                   throw new JspException( "Can not use column tag outside of a TableTag." );
               }
           } else {
               foundTableTag = true;    
           }
       }
       
       return parent;
   }
   
   /**
    * Passes attribute information up to the parent TableTag.
    *
    * <p>When we hit the end of the tag, we simply let our parent (which better
    * be a TableTag) know what the user wants to do with this column.
    * We do that by simple registering this tag with the parent.  This tag's
    * only job is to hold the configuration information to describe this 
    * particular column.  The TableTag does all the work.</p>
    *
    * @throws JspException if this tag is being used outside of a 
    *    &lt;display:list...&gt; tag.
    **/
   public int doEndTag() throws JspException {
      Object parent = this.getTableTag();

      // Need to clone the ColumnTag before passing it to the TableTag as
      // the ColumnTags can be reused by some containers, and since we are
      // using the ColumnTags as basically containers of data, we need to
      // save the original values, and not the values that are being changed
      // as the tag is being reused...

      ColumnTag copy = this;
      try {
         copy = (ColumnTag)this.clone();
      } catch( CloneNotSupportedException e ) {
      	LogUtil.log(this, LogUtil.LOG_ERROR, "shouldn't happen", e);
      } // shouldn't happen...

      ((TableTag)parent).addColumn( copy );

      return super.doEndTag();
   }

 
   
   /**
    * Get the content of header (content between the <th>'s)
    */
   protected StringBuffer getHeader(int sortOrder, String sortAttr, int colNumber, String anchorParam,
           							String url){
       StringBuffer buf = new StringBuffer();       

       if (this.visible) {
           //get the attributes of header
           buf.append(this.getHeaderAttributes());
           
           String header = this.getTitle();
           if (header == null) {
               header = StringUtil.toUpperCaseAt(this.getProperty(), 0);
           }
           header = this.getTitleFromBundle(header);

           //if is sortable...
           buf.append(this.getSortFormating(sortOrder, sortAttr + "=" + colNumber, anchorParam, url, header));

           buf.append("</th>\n");    	       	   
       }
       
       return buf;       
   }

   /**
    * Try to get the title from the struts default resource bundle.
    * Retrieve the message string we are looking for.
    * @return
    */
   public String getTitleFromBundle(String header){
       String response = header;
       try {
           String message = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, header, null);
           if (message != null) {
               response = message;
           }
           if (message.indexOf("???")>-1) {
        	   response = header;   
           }
       } catch (JspException e) {
           response = header;
       }
       return response;
   }
          

   /**
    * Get the content of body column (content between the <td>'s)
    */   
   protected StringBuffer getBody(ColumnDecorator decorator, int rowcnt, 
				String pageParam, Decorator dec, Properties prop) throws JspException {
       StringBuffer buf = new StringBuffer();

       if (this.visible) {
           //get the attributes of body
           buf.append(this.getBodyAttributes());
                       
           // Get the value to be displayed
           String val = this.getFormatedValue(decorator, rowcnt, pageParam, dec, prop);
           buf.append(val);
                      
           buf.append("</td>\n");    	   
       }
       
       return buf;
   }

      
   
   /**
    * Create the header format using the current column attributes
    * @return
    */
   protected StringBuffer getHeaderAttributes(){
       StringBuffer buf = new StringBuffer();
              
       buf.append("<th");
       if (this.getWidth() != null) {
           buf.append(" width=\"" + this.getWidth() + "\"");
       }

       if (this.getAlign() != null) {
           buf.append(" align=\"" + this.getAlign() + "\"");
       }

       if (this.getHeaderStyleClass() != null) {
           buf.append(" class=\"" + this.getHeaderStyleClass() + "\">");
       } else {
           buf.append(" class=\"tableCellHeader\">");
       }
       
       return buf;
   }

   
   /**
    * Takes all the column pass-through arguments and bundles them up as a
    * string that gets tacked on to the end of the td tag declaration.<p>
    **/
   protected StringBuffer getBodyAttributes(){
      StringBuffer results = new StringBuffer();
      
      results.append("<td ");
      
      if( this.clazz != null ) {
          results.append( " class=\"" );
          results.append( this.clazz );
          results.append( "\"" );
      } else {
          results.append( " class=\"tableCell\"" );
      }

      if( this.width != null ) {
         results.append( " width=\"" );
         results.append( this.width );
         results.append( "\"" );
      }

      if( this.align != null ) {
         results.append( " align=\"" );
         results.append( this.align );
         results.append( "\"" );
      } else {
          results.append( " align=\"left\"" );
      }

      if( this.background != null ) {
         results.append( " background=\"" );
         results.append( this.background );
         results.append( "\"" );
      }

      if( this.bgcolor != null ) {
         results.append( " bgcolor=\"" );
         results.append( this.bgcolor );
         results.append( "\"" );
      }

      if( this.height != null ) {
         results.append( " height=\"" );
         results.append( this.height );
         results.append( "\"" );
      }

      if( this.nowrap != null ) {
         results.append( " nowrap" );
      }

      if( this.valign != null ) {
         results.append( " valign=\"" );
         results.append( this.valign );
         results.append( "\"" );
      } else {
          results.append( " valign=\"top\"" );
      }
      
      results.append(">");    	  

      return results;
   }

     
   /**
    * Return the number of columns of ColumnTag
    */
   protected int getColumnsNumber() {
       return 1;
   }

   
   /**
    * Returns the formated html in order to show the ordering feature of header
    */
   private StringBuffer getSortFormating(int sortOrder, String sortAttr, String anchorParam, String url, String header) {
       StringBuffer buf = new StringBuffer();
       String orderStr = null;
       
       if (this.getSort() != null) {
          if (sortOrder == TableTag.SORT_ORDER_ASCEENDING) {
              orderStr = "dec";
          } else if (sortOrder == TableTag.SORT_ORDER_DECENDING) {
              orderStr = "asc";
          }
          
          if (orderStr!=null) {
              buf.append("<a href=\"" + url + 
                      "order=" + orderStr + "&" + sortAttr + "&" + anchorParam +
                      "\"" + anchorParam + " class=\"tableCellHeader\">");
              buf.append(header);
              buf.append("</a>");
          }
       } else {
           buf.append(header);
       }
       
       return buf;
   }
   
   
   /**
    * Returns a String representation of this Tag that is suitable for
    * printing while debugging.  The format of the string is subject to change
    * but it currently:
    * <p>Where the placeholders in brackets are replaced with their appropriate
    * instance variables.</p>
    **/   
   public String toString() {
      return "ColumnTag(" + title + "," + property + "," + href + ")";
   }

   public String getSearchValue(ColumnDecorator decorator, int rowcnt, 
				String pageParam, Properties prop, Object obj) throws JspException {
       String value = "";
       Decorator dec = null;
       
	   if (this.getValue() != null) {
	        value = this.getValue();
	   } else {
	        if (this.getProperty().equals("ff")) {
	            value = String.valueOf(rowcnt);
	        } else {
	            if (decorator!=null) {
		            dec = DecoratorUtil.loadDecorator(decorator.getClass().getName());
	            }
		        Object columnValue = LookupUtil.lookup(pageContext, "smartRow", this.getProperty(), null, true, pageParam, dec, prop);
		        if (decorator!=null) {
		        	decorator.initRow(obj, decorator.getViewIndex(), decorator.getListIndex());
	                value = decorator.contentToSearching(columnValue);                
	            } else {
	                value = columnValue + "";
	            }
	        }
	   }
		
	   if (value==null) {
		   value = "";
	   }
	   
	   return value;
	}
   
   /**
    * Get the value to be displayed
    */
   private String getFormatedValue(ColumnDecorator decorator, int rowcnt, 
           						String pageParam, Decorator dec, Properties prop) throws JspException {
       Object value = null;
       
       if (this.getValue() != null) {
           value = this.getValue();
       } else {
           if (this.getProperty()!=null && this.getProperty().equals("ff")) {
               value = String.valueOf(rowcnt);
           } else {
               value = LookupUtil.lookup(pageContext, "smartRow", this.getProperty(), null, true, pageParam, dec, prop);
               if (decorator != null) {
                   if (this.getTag() == null) {
                       value = decorator.decorate(value);
                   } else {
                       value = decorator.decorate(value, this.getTag());
                   }
               }
           }    		   
       }

       // By default, we show null values as empty strings
       if (value == null || value.equals("null")) {
           value = "&nbsp;";
       } else {
    	   if (value instanceof String){
    		   if (((String)value).trim().equals("")) {
    			   value = "&nbsp;";
    		   }
    	   }
       }


       // String to hold what's left over after value is chopped
       String leftover = "";
       boolean chopped = false;
       String tempValue = ""; //, tdEnding = "";
       if (value != null) {
           tempValue = value.toString();
       }
       
       // trim the string if a maxLength or maxWords is defined
       if (this.getMaxLength() > 0 && tempValue.length() > this.getMaxLength()) {
           leftover = "..." + tempValue.substring(this.getMaxLength(), tempValue.length());
           value = tempValue.substring(0, this.getMaxLength());
           if (this.getHref()!=null) {
        	   value = value + "...";   
           }
           chopped = true;
       } else if (this.getMaxWords() > 0) {
           StringBuffer tmpBuffer = new StringBuffer();
           StringTokenizer st = new StringTokenizer(tempValue);
           int numTokens = st.countTokens();
           if (numTokens > this.getMaxWords()) {
               int x = 0;
               while (st.hasMoreTokens() && (x < this.getMaxWords())) {
                   tmpBuffer.append(st.nextToken() + " ");
                   x++;
               }
               leftover = "..." + tempValue.substring(tmpBuffer.length(), tempValue.length());
               leftover = leftover.replaceAll("\"", "''");
               value = tmpBuffer;
               chopped = true;
           }
       }

       // Are we supposed to set up a link to the data being displayed
       // in this column...
       if (this.getAutolink() != null && this.getAutolink().equals("true")) {
           value = StringUtil.autoLink(value.toString());
       }

       if (chopped && this.getHref()==null) {
    	   value = value + "<a style=\"cursor: help;\" title=\"" + leftover + "\">...</a>";
       }

       //check if there are some 'pre-content' and 'post-content' from decorator 
       if (decorator != null) {
    	   String precontent = decorator.getPreContent(value, this.getTag()); 
    	   if (precontent!=null) {
    		   value = precontent + value;
    	   }
    	   String postcontent = decorator.getPostContent(value, this.getTag()); 
    	   if (postcontent!=null) {
    		   value = value + postcontent;
    	   }    	   
       }
              
       return value+"";
   }

   
   /**
    * Get the data of table
    */
   protected Vector getTableData() {
       String tableProperty = "";
       Vector response = null;
       
       //get the tableTag object in order to get the id of table
       try {
           Object parent = this.getTableTag();
           tableProperty = ((TableTag)parent).getName();
       } catch (JspException e) {
           response = null;
       }
       
       //get the data of table
       try {
           response = (Vector)super.pageContext.getSession().getAttribute(tableProperty);
       } catch(Exception e) {
           response = null;
       }
       
       return response;
   }      
   

   
   /**
    * If the current maxWords was sent with string value, store the
    * key into maxWordsKey attribute
    * @param key
    */
   public void setMaxWords(String key) { 
       this.maxWordsKey = key; 
   } 
   public String getMaxWordsKey() { 
       return this.maxWordsKey; 
   }

   
   //////////////////////////////////////////////
   public void setProperty(String newValue ) {
       this.property = newValue; 
   }
   public String getProperty() { 
       return this.property; 
   }

   //////////////////////////////////////////////   
   public void setTitle( String newValue ) { 
       this.title = newValue; 
   }
   public String getTitle() { 
       return this.title; 
   }
   
   //////////////////////////////////////////////   
   public void setSort( String v ) { this.sort = v; }
   public String getSort() { return this.sort; }
   
   public void setAutolink( String v ) { this.autolink = v; }
   public String getAutolink() { return this.autolink; }

   public void setHref( String v ) { this.href = v; }
   public String getHref() { return this.href; }
   
   public void setParamId( String v ) { this.paramId = v; }
   public String getParamId() { return this.paramId; }
   
   public void setParamName( String v ) { this.paramName = v; }
   public String getParamName() { return this.paramName; }
   
   public void setParamProperty( String v ) { this.paramProperty = v; }
   public String getParamProperty() { return this.paramProperty; }
   
   public void setParamScope( String v ) { this.paramScope = v; }
   public String getParamScope() { return this.paramScope; }
   
   public void setTag( String newValue ) { this.tag = newValue; }
   public String getTag() { return this.tag; }
   
   public void setMaxLength( int v ) { this.maxLength = v; }
   public int getMaxLength() { return this.maxLength; }
   
   public void setMaxWords( int v ) { this.maxWords = v; }
   public int getMaxWords() { return this.maxWords; }
   
   public void setWidth( String v ) { this.width = v; }
   public String getWidth() { return this.width; }
   
   public void setAlign( String v ) { this.align = v; }
   public String getAlign() { return this.align; }
   
   public void setBackground( String v ) { this.background = v; }
   public String getBackground() { return this.background; }
   
   public void setBgcolor( String v ) { this.bgcolor = v; }
   public String getBgcolor() { return this.bgcolor; }
   
   public void setHeight( String v ) { this.height = v; }
   public String getHeight() { return this.height; }
   
   public void setNowrap( String v ) { this.nowrap = v; }
   public String getNowrap() { return this.nowrap; }
   
   public void setValign( String v ) { this.valign = v; }
   public String getValign() { return this.valign; }
   
   public void setStyleClass( String v ) { this.clazz = v; }
   public String getStyleClass() { return this.clazz; }
   
   public void setHeaderStyleClass( String v ) { this.headerClazz = v; }
   public String getHeaderStyleClass() { return this.headerClazz; }
   
   public void setValue( String v ) { this.value = v; }
   public String getValue() { return this.value; }
   
   //public void setDoubleQuote ( String v ) { this.doubleQuote = v ; }
   //public String getDoubleQuote () { return this.doubleQuote ; }
   
   public void setDecorator(String v) {this.decorator = v; }
   public String getDecorator() { return this.decorator ;}

   public void setComboFilter(boolean newValue) {this.comboFilter = newValue; }
   public boolean getComboFilter() { return this.comboFilter ;}
   
   public void setLikeSearching(boolean newValue) {this.likeSearching = newValue; }
   public boolean getLikeSearching() { return this.likeSearching ;}

   public boolean isVisible() { return this.visible; }
   public void setVisible(boolean newValue) { this.visible = newValue; }
   
   public void setVisibleProperty(String newValue) {this.visibleProperty = newValue; }
   public String getVisibleProperty() { return this.visibleProperty ;}

   public void setDescription(String newValue) {this.description = newValue; }
   public String getDescription() { return this.description ;}
    
}