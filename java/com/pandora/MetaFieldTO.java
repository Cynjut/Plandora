package com.pandora;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pandora.delegate.DbQueryDelegate;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.XmlDomParse;

/**
 * This object it is a java bean that represents an MetaField entity.
 */
public class MetaFieldTO extends TransferObject {
    	
	private static final long serialVersionUID = 1L;

    /** Constant used by apply_to attribute */
    public static final Integer APPLY_TO_REQUIREMENT = new Integer(1);
    public static final Integer APPLY_TO_TASK        = new Integer(2);
    public static final Integer APPLY_TO_PROJECT     = new Integer(3);
    public static final Integer APPLY_TO_RISK        = new Integer(4);
    public static final Integer APPLY_TO_INVOICE     = new Integer(5);
    public static final Integer APPLY_TO_EXPENSE     = new Integer(6);
    public static final Integer APPLY_TO_COST        = new Integer(7);
    
    public static final Integer APPLY_TO_CUSTOM_FORM = new Integer(9);

    
    /** Constant used by type attribute */
    public static final int TYPE_TEXT_BOX      = 1;
    public static final int TYPE_COMBO_BOX     = 2;
    public static final int TYPE_SQL_COMBO_BOX = 3;
    public static final int TYPE_TEXT_AREA     = 4;
    public static final int TYPE_CALENDAR      = 5;
    public static final int TYPE_TABLE         = 9;
    
    
    /** The name of meta field used by GUI to display the label content */
    private String name;
    
    /** The type of meta field used by GUI to format the visual component (text, combo, etc)*/
    private Integer type;

    /** Where the meta field should be used. (Requirement form, Task Form or Project Form)*/
    private Integer applyTo = APPLY_TO_REQUIREMENT;
    
    /** The project related with meta field */
    private ProjectTO project;

    /** The category related with meta field */
    private CategoryTO category;
    
    /** The list of data of meta field. <br>
     * <li>If meta field is a text box or text area the domain is used to store the default value </li>
     * <li>If meta field is a combo box the domain is used to list the options of combo separated by pipe '|' </li> 
     * <li>If meta field is a combo box from DB, the domain is used to store the query used to get data from DB </li>
     **/
    private String domain;
    private Vector domainCache;
    
    /** If NOT NULL the meta Field should be hidden into other GUIs*/
    private Timestamp disableDate = null;
    
    /** The Meta Form related with current meta field */
    private MetaFormTO metaform;
    
    
    private String helpContent;
    
    /** This attribute is transient */
    private int numCols = 0;
    
    
    /**
     * Constructor 
     */
    public MetaFieldTO(){
    }

    /**
     * Constructor 
     */    
    public MetaFieldTO(String id){
        this.setId(id);
    }

    
    public StringBuffer getFormatedTable(String style, Vector values, MetaFieldTO mfto, 
    		UserTO uto, String formName) throws Exception{
    	StringBuffer buff = new StringBuffer();
    	int valuesIndex = 0;
    	
    	Document doc = XmlDomParse.getXmlDom(mfto.getDomain());
    	Node gridNode = doc.getFirstChild();
    	if (gridNode!=null) {
    		
    		this.numCols = 0;
    		int numRows = 0;
    		NodeList colsOfFirstRow = null;
    		
    		buff.append("<table cellspacing=\"1\" cellpadding=\"0\"");
    		String gridWidth = XmlDomParse.getAttributeTextByTag(gridNode, "width");
    		if (gridWidth!=null && !gridWidth.equals("")) {
    			buff.append(" width=\"" + gridWidth + "\"");	
    		}
    		String gridborder = XmlDomParse.getAttributeTextByTag(gridNode, "border");
    		String newrowStr = XmlDomParse.getAttributeTextByTag(gridNode, "newrow");
    		boolean newrow = (newrowStr!=null && newrowStr.equalsIgnoreCase("true"));    		
    		buff.append(">\n");   		

    		StringBuffer payload = new StringBuffer();
    		NodeList childs = gridNode.getChildNodes();
    		for (int i=0; i<childs.getLength(); i++) {
    			Node node = childs.item(i);
    			if (node!=null && node.getNodeName()!=null) {
	        		NodeList cols = node.getChildNodes();
	        		if (cols!=null && cols.getLength()>0) {

	        			if (node.getNodeName().equalsIgnoreCase("header")){
		        			payload.append("<tr class=\"tableRowHeader\">");	        				
	        				for (int j=0; j<cols.getLength(); j++) {
		            			Node titleNode = cols.item(j);
		            			if (titleNode!=null && titleNode.getNodeName().equalsIgnoreCase("title")) {
               						payload.append("<th class=\"tableCellHeader\">" + titleNode.getTextContent() + "</th>");
		            			}
	        				}
           					if (newrow) {
           						payload.append("<th width=\"15\" class=\"tableCellHeader\">&nbsp</th>");
           					}	        				
	            			payload.append("</tr>");
	            			
	        			} else if (node.getNodeName().equalsIgnoreCase("row")) {

		        			numRows++;
	        				if (colsOfFirstRow==null) {
    							colsOfFirstRow = cols;
    						}
	        				
        					if (!newrow || numRows==1) {
    		        			payload.append("<tr>");
           						this.numCols = 0;
       		        			valuesIndex = this.getFormatedColByDomain(cols, payload, numRows, 
       		        					(gridborder!=null && gridborder.equalsIgnoreCase("true")), values, 
       		        					style, uto, valuesIndex);           						
    		            		payload.append("</tr>\n");   
        					}
	    				}
	        		}
    			}
    		}

    		if (newrow && values!=null) {
				int totalRows = values.size() / valuesIndex;
				for (int r=2; r<=totalRows; r++) {
					numRows++;
        			payload.append("<tr>");					
					this.numCols = 0;
	       			valuesIndex = this.getFormatedColByValues(colsOfFirstRow, payload, r, 
	       					(gridborder!=null && gridborder.equalsIgnoreCase("true")), values, 
	       					style, uto, valuesIndex);           						           						
	    			
					if (gridborder!=null && gridborder.equalsIgnoreCase("true")) {
						payload.append("<td class=\"metaFieldTableCel\">");
					} else {
						payload.append("<td>");               						
					}
					if (r>1) {
						payload.append("<a href=\"javascript:metaTableRemoveRow('" + formName + "', '" + this.getHtmlName() + "|" + r + "');\" border=\"0\"><center><img border=\"0\" src=\"../images/remove.gif\" ></center></a></td>");	
					} else {
						payload.append("&nbsp;</td>");               						
					}
					payload.append("</tr>\n");
				}
			}
    		
    		buff.append("<tr class=\"gapFormBody\"><td colspan=\"" + numCols + "\">&nbsp;</td></tr>");    		
    		buff.append("<tr class=\"formBody\"><td colspan=\"" + (numCols-1) + "\"><img src=\"../images/brick.png\" " +
    				        "title=\"Artefatos\" alt=\"Artefatos\" border=\"0\">&nbsp;&nbsp;<b>" + mfto.getName() + "</b>" + getHelpNote() + "</td></tr>");
    		buff.append(payload);
			if (newrow) {
				buff.append("<tr class=\"gapFormBody\"><td colspan=\"" + numCols + "\" align=\"left\">");
				buff.append("<input type=\"button\" name=\"rowAddButton\" value=\"  +  \" onclick=\"javascript:metaTableAddRow('" + formName + "', '" + this.getHtmlName() + "');\" class=\"button\"></td></tr>");
			} else {
				buff.append("<tr class=\"gapFormBody\"><td colspan=\"" + numCols + "\">&nbsp;</td></tr>");				
			}
    		buff.append("</table>\n");
    		
        	buff.append("<input type=\"hidden\" name=\"" + this.getHtmlName() + "_NUM_ROWS\" value=\"" + numRows + "\">");
        	buff.append("<input type=\"hidden\" name=\"" + this.getHtmlName() + "_NUM_COLS\" value=\"" + numCols + "\">");
    	}
   	
    	return buff;
    }
    
    
 
    /**
     * Format a html field with title and body.
     */
    public StringBuffer getFormatedField(String style, String currValue, UserTO uto){    	
        StringBuffer buff = new StringBuffer();
                
        //format the body's content according meta field type
        if (this.getType().equals(new Integer(TYPE_TEXT_BOX))) {
            buff.append("<input type=\"text\" name=\"" + 
                    this.getHtmlName() + "\" id=\"" + this.getHtmlName() + "\" maxlength=\"" +
                    this.getTextBoxMaxLenght() + "\" size=\"" + 
                    this.getTextBoxMaxLenght() + "\" value=\"" +
                    this.getDefaultValue(this.getDomain(), currValue, MetaFieldTO.TYPE_TEXT_BOX) + "\"" +
                    this.getEnabledStatus(true) +
                    this.getStyleSheetClass(style) + ">");
           	buff.append(getHelpNote());	
            buff.append(this.getHiddenFieldByEnabledStatus(MetaFieldTO.TYPE_TEXT_BOX, currValue));
            
        } else if (this.getType().equals(new Integer(TYPE_COMBO_BOX))) {
            buff.append(HtmlUtil.getComboBox(this.getHtmlName(), this.getDomain(), style, currValue, null) );
           	buff.append(getHelpNote());	

        } else if (this.getType().equals(new Integer(TYPE_SQL_COMBO_BOX))) {
            Vector<TransferObject> options = HtmlUtil.getQueryData(this.getDomain(), null, (project!=null?project.getId():null), null);
            buff.append(HtmlUtil.getComboBox(this.getHtmlName(), options, style, currValue) );
           	buff.append(getHelpNote());	
        
        } else if (this.getType().equals(new Integer(TYPE_TEXT_AREA))) {
            buff.append("<textarea name=\"" + 
                    this.getHtmlName() + "\" id=\"" + this.getHtmlName() + "\" cols=\"" +
                    this.getTextAreaCols() + "\" rows=\"" +
                    this.getTextAreaRows() + "\" " +
                    this.getEnabledStatus(false) +
                    this.getStyleSheetClass(style) + ">" + 
                    this.getDefaultValue(this.getDomain(), currValue, MetaFieldTO.TYPE_TEXT_AREA) + "</textarea>");
           	buff.append(getHelpNote());	
            buff.append(this.getHiddenFieldByEnabledStatus(MetaFieldTO.TYPE_TEXT_AREA, currValue));
            
        } else if (this.getType().equals(new Integer(TYPE_CALENDAR))) {
        	String calLabel = uto.getBundle().getMessage(uto.getLocale(), "label.calendar.button");
        	String calFormat = uto.getBundle().getMessage(uto.getLocale(), "calendar.format");
        	
        	String defVal = this.getDefaultValue(this.getDomain(), currValue, MetaFieldTO.TYPE_CALENDAR);
        	if (defVal!=null && defVal.equals("NOW")) {
        		defVal = DateUtil.getDate(DateUtil.getNow(), calFormat, uto.getLocale());
        	}
 			buff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\"><tr><td>\n");
 			buff.append("<input type=\"text\" name=\"" + 
 					this.getHtmlName() + "\" id=\"" + this.getHtmlName() + "\" size=\"" + 
 						this.getTextBoxMaxLenght() + "\" value=\"" + defVal + "\"" +
 						this.getEnabledStatus(true) +
 						this.getStyleSheetClass(style) + ">\n");
 			buff.append("</td><td><a href=\"javascript:calc_" + this.getHtmlName() + ".popup();\" border=\"0\">");
 			buff.append("<img src=\"../images/calendar.gif\" title=\"" + calLabel + "\" alt=\"" + calLabel + "\" border=\"0\" ></a>\n");
           	buff.append(getHelpNote());	
 			buff.append("</td></tr></table>\n");
            
 			buff.append("<script language=\"JavaScript\">\n");
 			buff.append("  var calc_" + this.getHtmlName() + " = new calendar1(document.forms[0].elements['" + this.getHtmlName() + "'], '" + calFormat+ "');\n");
 			buff.append("  calc_" + this.getHtmlName() + ".year_scroll = true;\n");
 			buff.append("  calc_" + this.getHtmlName() + ".time_comp = false;\n");
 			buff.append("</script>\n");
        }
                        
        return buff;
    }


    
    /*
     * if necessary, put a help context icon... 
     */
    private String getHelpNote(){
    	String response = "";
    	try {    	
    		if (this.getHelpContent()!=null && !this.getHelpContent().trim().equals("")) {
        		NoteIcon note = new NoteIcon();    			
				response = "&nbsp;" + note.getContent(this.getHelpContent(), null) + "\n";
    		}
		} catch (Exception e) {
			response = "";
		}	
        return response;
    }
    
    
    /**
     * Get a content from domain based on id
     */
    public String getValueByKey(String id){
        String response = "";
        
        try {
            if (this.getType().equals(new Integer(TYPE_TEXT_BOX))) {
                response = id;
            
            } else if (this.getType().equals(new Integer(TYPE_TEXT_AREA))) {
                response = id;
                
            } else if (this.getType().equals(new Integer(TYPE_COMBO_BOX))) {
                String domain = this.getDomain();
                int ini = domain.indexOf(id+"|");
                int sizeIni = (id+"|").length();
                int fin = domain.indexOf("|", ini + 1 + sizeIni);
                if (fin<0 && ini>0){
                    fin = domain.length();
                }
                response = domain.substring(ini+sizeIni,fin);
                
            } else if (this.getType().equals(new Integer(TYPE_SQL_COMBO_BOX))) {
            	if (this.domainCache==null) {
                    DbQueryDelegate query = new DbQueryDelegate();
                    this.domainCache = query.performQuery(this.getDomain());           		
            	}
                Iterator i = this.domainCache.iterator();
                while(i.hasNext()){
                	Vector item = (Vector)i.next();
                    if (((item.get(0)).toString()).equals(id)) {
                        response = (String)item.get(1);
                        break;
                    }
                }
                
            } else if (this.getType().equals(new Integer(TYPE_CALENDAR))) {
                response = id;
            }
            
        } catch (Exception e) {
            response = "";
        }
        
        return response;
    }
    
    
    /**
     * Return if the current meta field is enable or disable 
     * (should be displayed or hidden into form)
     * @return
     */
    public boolean isEnable(){
        return (this.getDisableDate()==null);
    }
    
    
    /**
     * Return the name of Meta Field formated to HTML form name standards.
     */
    public String getHtmlName(){
        return "META_DATA_" + this.getId();
    }

   
    private String getStyleSheetClass(String style){
        String response = "";
        if (style!=null) {
            response = " class=\"" + style + "\"";
        }
        return response;
    }
    
    private String getTextBoxMaxLenght(){
        String[] options = this.getDomain().split("\\|");
        return options[0];
    }

    private String getTextAreaCols(){
        String[] options = this.getDomain().split("\\|");
        return options[0];
    }

    private String getTextAreaRows(){
        String[] options = this.getDomain().split("\\|");
        return options[1];
    }
   
    private String getEnabledStatus(boolean isTextBox){
        String response = "";
        String[] options = this.getDomain().split("\\|");
        String option = "";
        if (isTextBox) {
            option = options[1]; 
        } else {
            option = options[2];
        }
        if (option.equalsIgnoreCase("FALSE")) {
            response = " disabled=\"true\" ";
        }
        return response;        
    }

    
    private String getHiddenFieldByEnabledStatus(int guiType, String currValue){
        String response = "";
        String[] options = this.getDomain().split("\\|");
        String option = "";
        if (guiType==MetaFieldTO.TYPE_TEXT_BOX) {
            option = options[1]; 
        } else {
            option = options[2];
        }
        if (option.equalsIgnoreCase("FALSE")) {
            response = "<input type=\"hidden\" name=\"" + this.getHtmlName() + 
                       "\" value=\"" + this.getDefaultValue(this.getDomain(), currValue, guiType) + "\" >";
        }
        return response;        
    }
    
    
    private String getDefaultValue(String domain, String currValue, int guiType){
        String response = "";
        if (currValue==null) {
            String[] options = domain.split("\\|");
            if (guiType==MetaFieldTO.TYPE_TEXT_BOX) {
                if (options.length>2) {
                    response = options[2];
                }                            
            } else if (guiType==MetaFieldTO.TYPE_TEXT_AREA) {
                if (options.length>3) {
                    response = options[3];
                }
            } else if (guiType==MetaFieldTO.TYPE_COMBO_BOX) {
            	response = options[0];
            } else if (guiType==MetaFieldTO.TYPE_CALENDAR) {
                if (options.length>2) {
                    response = options[2];
                }            	
            }
            response = response.trim();            
        } else {
            response = currValue;
        }
        
        return response;
    }


	private int getFormatedColByDomain(NodeList cols, StringBuffer payload,	int rowNumber, boolean isBorder, 
			Vector values, String style, UserTO uto, int counter) {
		
		for (int j = 0; j < cols.getLength(); j++) {
			Node colNode = cols.item(j);
			if (colNode != null) {
				String colType = XmlDomParse.getAttributeTextByTag(colNode, "type");
				if (colType != null) {

					this.numCols++;
					if (isBorder) {
						payload.append("<td class=\"metaFieldTableCel\">");
					} else {
						payload.append("<td>");
					}

					String currentValueOfCell = null;
					if (values != null && counter < values.size()) {
						AdditionalTableTO atto = (AdditionalTableTO) values.get(counter);
						if (atto.getDateValue() != null) {
							currentValueOfCell = DateUtil.getDate(atto.getDateValue(), uto.getCalendarMask(), uto.getLocale());
						} else {
							currentValueOfCell = atto.getValue();
						}
					}

					MetaFieldTO innerField = new MetaFieldTO();
					innerField.setType(new Integer(colType));
					innerField.setDomain(colNode.getTextContent());
					innerField.setId(this.getHtmlName() + "_" + rowNumber + "_" + this.numCols);
					innerField.setHelpContent(null);
					payload.append(innerField.getFormatedField(style, currentValueOfCell, uto));
					counter++;

					payload.append("</td>");
					payload.append("<input type=\"hidden\" name=\"" + this.getHtmlName() + "_" + rowNumber + "_"
							+ this.numCols + "_CEL_TYPE\" value=\"" + colType + "\">");
				}
			}
		}
		return counter;
	}
    
	
	private int getFormatedColByValues(NodeList colsOfFirstRow, StringBuffer payload, int rowNumber, boolean isBorder, 
			Vector values, String style, UserTO uto, int counter) {
		
		for (int j = 0; j < colsOfFirstRow.getLength(); j++) {
			Node colNode = colsOfFirstRow.item(j);
			if (colNode != null) {
				String colType = XmlDomParse.getAttributeTextByTag(colNode, "type");
				if (colType != null) {

					this.numCols++;
					if (isBorder) {
						payload.append("<td class=\"metaFieldTableCel\">");
					} else {
						payload.append("<td>");
					}

					String currentValueOfCell = null;
					if (values != null) {
						AdditionalTableTO atto = this.getTableCellValue(rowNumber, this.numCols, values);
						if (atto!=null) {
							if (atto.getDateValue() != null) {
								currentValueOfCell = DateUtil.getDate(atto.getDateValue(), uto.getCalendarMask(), uto.getLocale());
							} else {
								currentValueOfCell = atto.getValue();
							}							
						} else {
							currentValueOfCell = this.getDefaultValue(colNode.getTextContent(), null, Integer.parseInt(colType));
						}
					}

					MetaFieldTO innerField = new MetaFieldTO();
					innerField.setType(new Integer(colType));
					innerField.setDomain(colNode.getTextContent());
					innerField.setId(this.getHtmlName() + "_" + rowNumber + "_" + this.numCols);
					innerField.setHelpContent(null);
					payload.append(innerField.getFormatedField(style, currentValueOfCell, uto));
					counter++;

					payload.append("</td>");
					payload.append("<input type=\"hidden\" name=\"" + this.getHtmlName() + "_" + rowNumber + "_"
							+ this.numCols + "_CEL_TYPE\" value=\"" + colType + "\">");
				}
			}
		}
		return counter;
	}
	
	
    private AdditionalTableTO getTableCellValue(int row, int col, Vector values) {
    	AdditionalTableTO response = null;
    	Iterator i = values.iterator();
    	while(i.hasNext()) {
    		AdditionalTableTO atto = (AdditionalTableTO)i.next();
    		if (atto.getCol().intValue()==col &&
    				atto.getLine().intValue()==row) {
    			response = atto;
    			break;
    		}
    	}
		return response;
	}
    

	////////////////////////////////////////////////
    public String getDomain() {
        return domain;
    }
    public void setDomain(String newValue) {
        this.domain = newValue;
    }
    
    
    ////////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    ////////////////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    
    ////////////////////////////////////////////////    
    public Integer getType() {
        return type;
    }
    public void setType(Integer newValue) {
        this.type = newValue;
    }

    
    ////////////////////////////////////////////////    
    public Integer getApplyTo() {
        return applyTo;
    }
    public void setApplyTo(Integer newValue) {
        this.applyTo = newValue;
    }
    
    
    ////////////////////////////////////////////////      
    public Timestamp getDisableDate() {
        return disableDate;
    }
    public void setDisableDate(Timestamp newValue) {
        this.disableDate = newValue;
    }
    
    
    ////////////////////////////////////////////////    
    public CategoryTO getCategory() {
        return category;
    }
    public void setCategory(CategoryTO newValue) {
        this.category = newValue;
    }
    
    
    ///////////////////////////////////////////   
    public MetaFormTO getMetaform() {
        return metaform;
    }
    public void setMetaform(MetaFormTO newValue) {
        this.metaform = newValue;
    }

    
    ///////////////////////////////////////////     
	public String getHelpContent() {
		return helpContent;
	}
	public void setHelpContent(String newValue) {
		this.helpContent = newValue;
	}
    
}

