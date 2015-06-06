package com.pandora.gui.taglib.table;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.MetaFieldTO;
import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class PTableHelper {

	private static int MAX_PAGES_NAVIGATION = 8;
	 
	@SuppressWarnings("rawtypes")
	public String getTableHeader(String name, HttpServletRequest req, String parameters) {
		StringBuffer buf = new StringBuffer();
 
        buf.append("<table class=\"table\" width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">\n");
        UserTO uto = SessionUtil.getCurrentUser(req);

		PTableTag table = (PTableTag)req.getSession().getAttribute(PTableTag.DEF_TABLE_PREFIX + name);
		Collection datagrid = (Collection)req.getSession().getAttribute(name);
        Collection filteredDataGrid = (Collection)req.getSession().getAttribute(PTableTag.FILTERED_GRID_PREFIX + name);
		if (table!=null && uto!=null && datagrid!=null){			
			     
			this.getHeaderIcons(buf, table, datagrid, filteredDataGrid, uto, req);	
			
	        buf.append("<tr class=\"tableRowHeader\">");
	        //int col = 0;
	        if (table.getColumnsNumber() > 0) {
	            for (int i = 0; i < table.getRealColumnsNumber(); i++) {
	                PColumnTag column = table.getColumn(i);	                
	                buf.append(column.getHeader(req, uto, name, table.getFrm()));
	            }        	
	        } else {
	            if (uto.getBundle()!=null) {
	            	buf.append("<td><b>" + uto.getBundle().getMessage(req.getLocale(), "error.msg.no_column_tags")  + "</b></td>");            	
	            }
	        }
	        buf.append("</tr>\n");
	        
		} else {
			buf.append("<tr><td>Err</td></tr>\n");    	
		}
		
        return buf.toString();
	}

	
	@SuppressWarnings({ "rawtypes" })
	public String getTableBody(String name, HttpServletRequest req, String parameters) {
		StringBuffer buf = new StringBuffer("");		
		ArrayList<Object> filteredDataGrid = new ArrayList<Object>();
        UserTO uto = SessionUtil.getCurrentUser(req);
        Timestamp globalStat = DateUtil.getNow();
        
        if (req!=null && req.getSession()!=null) {
            Collection datagrid = (Collection)req.getSession().getAttribute(name);        
    		PTableTag table = (PTableTag)req.getSession().getAttribute(PTableTag.DEF_TABLE_PREFIX + name);
    		if (table!=null && uto!=null && datagrid!=null){
    		
    			//input the post parameters into grid structure...
    			table.addParameter(parameters);
    			
    			//load the columns visible status...
    	    	this.loadColumnStatus(req, uto, table, parameters);
    	        ArrayList<PColumnTag> columns = this.getColumns(table);
    	        
    	        if (datagrid.size()>0) {
    		        
    				//check if the grid must be sorted...
    	        	this.sortData(datagrid, table);
    	        	
    	        	//check if the grid must be filtered or put into hierarchical order..
    	    		if (table.isHierarchical()) {
    	    			filteredDataGrid = this.hierarchicalData(datagrid, columns, table, uto);
    	    		} else {
    		        	filteredDataGrid = this.filterData(datagrid, columns, table, uto);	    			
    	    		}
    	    		req.getSession().setAttribute(PTableTag.FILTERED_GRID_PREFIX + name, filteredDataGrid);	        	
    	        	
    		        this.setupDecorator(req, datagrid, columns);
    		        
    		        //define the range of pages and records to display...
    		        int currentPage = this.getPageCursor(table);
    		        int pageSize = this.getPageSize(req, table, uto);

    		        int ini = currentPage * pageSize;
    		        if (ini > filteredDataGrid.size()) {
    		        	currentPage = 0;
    		        	ini = currentPage * pageSize;
    		        	table.addParameter("page=" + currentPage);
    		        }

    		        int end = ini + pageSize;
    		        if (end > filteredDataGrid.size()) {
    		        	end = filteredDataGrid.size();
    		        }
    		        
    		        if (ini >= end) {
    		        	currentPage = 0;
    		        	ini = currentPage * pageSize;
    		        	end = ini + pageSize;
    		        	table.addParameter("page=" + currentPage);
    			        if (end > filteredDataGrid.size()) {
    			        	end = filteredDataGrid.size();
    			        }		        	
    		        }		        
    		        
    		        if (pageSize==0) {
    		        	currentPage = 0;
    		        	ini = 0;
    		        	end = filteredDataGrid.size();
    		        }
    		
    		        int rowcnt = ini + 1;		        
    		        for(int c=ini; c<end; c++) {
    		        	Object line = filteredDataGrid.toArray()[c];
    		        	buf.append(this.renderLine(columns, table, line, rowcnt, uto));
    		        	rowcnt++;		        	
    		        }
    		        
    	        } else {
    	            if (uto.getBundle()!=null) {
    	            	buf.append("<tr class=\"tableRowOdd\"><td valign=\"center\" colspan=\"" + columns.size() + "\" align=\"center\" class=\"tableCellAction\">" + uto.getBundle().getMessage(req.getLocale(), "basic.msg.empty_list")  + "</td></tr>");            	
    	            }	
    	        }
    		} else {
    			buf.append("<tr><td>Err</td></tr>\n");    		        
    		}
    		
    		buf.append("</table>");
    		
    		if (uto!=null) {
    			System.out.println("Grid: [" + name + "] by [" + uto.getUsername() + "] " + (DateUtil.getNow().getTime() - globalStat.getTime()) + "ms");			
    		}        	
        }
		
		return buf.toString();
	}

	
	
	@SuppressWarnings("rawtypes")
	private ArrayList<Object> hierarchicalData(Collection datagrid, ArrayList<PColumnTag> columns, PTableTag table, UserTO uto) {
		ArrayList<Object> response = new ArrayList<Object>();
		HashMap<String, Vector<Object>> parentHash = new HashMap<String, Vector<Object>>(); 
		
		//collect a hash of parent IDs
		String parentId = table.getParenttag();
		for (Object line : datagrid) {
			Object parentVal = getDataByReflexion(parentId, line);
			if (parentVal==null) {
				parentVal = "ROOT";
			}
			
			Vector<Object> list = parentHash.get(parentVal);
			if (list==null) {
				list = new Vector<Object>();	
			}
			 
			list.add(line);
			parentHash.put((String)parentVal, list);
			
			//TODO: tirar a linha abaixo			
			response.add(line);
		}

		
		
		return response;
	}
	
	
	private boolean cellIsEmpty(ArrayList<String> values) {
		return (values==null || values.size()==0 || (values.size()==1 && values.get(0).trim().equals("")));
	}
	
	
	@SuppressWarnings("rawtypes")
	private ArrayList<Object> filterData(Collection datagrid, ArrayList<PColumnTag> columns, PTableTag table, UserTO uto) {
		int rowNumber = 0;
		ArrayList<Object> response = new ArrayList<Object>();
		for (Object line : datagrid) {
			boolean skipRow = false;
			
	        for(int i=0; i<columns.size(); i++) {
	        	PColumnTag column = columns.get(i);
	        	
	        	if (column.containComboFilter()) {
	        		
	        		ArrayList<String> values = this.renderCell(column, line, table, uto, true, true, rowNumber);
	        		if (this.cellIsEmpty(values)){
	        			values = this.renderCell(column, line, table, uto, false, true, rowNumber);
	        		}

	        		if (column instanceof MetaFieldPColumnTag) {
	        			ArrayList<MetaFieldTO> metaFields = ((MetaFieldPColumnTag)column).getComboFields();
	        			for (MetaFieldTO mfto :  metaFields) {
			        		String filterValue = table.getParameter("filter_" + table.getName() + "_" + (i+1)+ "_" + mfto.getId());
			        		skipRow = this.filterData(uto, skipRow, values, filterValue);	        										
						}
	        		} else {
	        			String filterValue = table.getParameter("filter_" + table.getName() + "_" + (i+1));
		        		skipRow = this.filterData(uto, skipRow, values, filterValue);	        			
	        		}
	        	}
	        }

	        if (!skipRow) {
	        	boolean findContent = false;
        		String filterValue = table.getParameter("search_" + table.getName());
				if (filterValue!=null && !filterValue.trim().equals("")) {
			        for(int i=0; i<columns.size(); i++) {
			        	PColumnTag column = columns.get(i);
			        	if (column.containLikeSearching()) {
				        	ArrayList<String> values = this.renderCell(column, line, table, uto, true, true, rowNumber);
				        	for (String content : values) {
					        	content = content.toLowerCase();
			    				if (content.indexOf(filterValue.toLowerCase())>-1) {
			    					findContent = true;
			    					break;
			    				}	        										
							}
			        	}
			        }					
				} else {
					findContent = true;
				}
		        skipRow = !findContent;
	        }
	        
	        if (!skipRow) {
	        	response.add(line);
	        }
	        
			rowNumber ++;
		}
		return response;
	}


	private boolean filterData(UserTO uto, boolean skipRow,	ArrayList<String> values, String filterValue) {
		if (!skipRow && filterValue!=null && !filterValue.trim().equals("")) {
			for (String content : values) {
				try {
					filterValue = new String(filterValue.getBytes(), uto.getEncoding());
					skipRow = (!filterValue.equalsIgnoreCase(content));
					if (!skipRow) {
						break;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return skipRow;
	}


	public String reloadPanelList(String name, HttpServletRequest req, String parameters) {
		String response = "";
        UserTO uto = SessionUtil.getCurrentUser(req);
		PTableTag table = (PTableTag)req.getSession().getAttribute(PTableTag.DEF_TABLE_PREFIX + name);
		if (table!=null && uto!=null){
			if (parameters.startsWith("show_panel")) {
				response = this.getColumnListToShowHide(req, uto, table);
			} else if (parameters.startsWith("search_like")) {
				response = this.getSearchLikeContent(req, uto, table);
			}
		}
		return response;
	}

	
	@SuppressWarnings("rawtypes")
	public String getCsvContent(String name, HttpServletRequest req, String gridParam) {
		StringBuffer title = new StringBuffer();
		StringBuffer body = new StringBuffer();
		int rowNumber =0;
		
        UserTO uto = SessionUtil.getCurrentUser(req);
        Collection datagrid = (Collection)req.getSession().getAttribute(name);        
		PTableTag table = (PTableTag)req.getSession().getAttribute(PTableTag.DEF_TABLE_PREFIX + name);
		if (table!=null && uto!=null && datagrid!=null){
			
			for (Object line : datagrid) {
				ArrayList<PColumnTag> columns = this.getColumns(table);
				boolean firstcol = true;
		        for(int i=0; i<columns.size(); i++) {
		        	PColumnTag column = columns.get(i);
		        	if (column.getTitle()!=null && !column.getTitle().equals("") && !column.getTitle().equals("grid.title.empty")) {

		        		//write the header of csv file
		        		if (rowNumber==0) {
		        			ArrayList<String> headerList = column.getHeaderList(req, uto, name);
		        			for (String headerContent : headerList) {
			        			if (!firstcol) {
			        				title.append(";");
			        			}
			        			String locatedTitle = uto.getBundle().getMessage(uto.getLocale(), headerContent);
			        			if (locatedTitle.startsWith("???")) {
			        				locatedTitle = headerContent;
			        			}
			        			title.append("\"" + locatedTitle + "\"");
		        			}
		        		}

		        		//write the body of csv file
		        		ArrayList<String> values = this.renderCell(column, line, table, uto, true, true, rowNumber);
		        		for (String cellContent : values) {
				        	if (!firstcol) {
				        		body.append(";");
				        	}
				        	body.append("\"" + StringUtil.formatWordForParam(cellContent) + "\"");
				        	firstcol = false;
						}
		        	}
		        }
		        body.append("\n");		        
		        rowNumber ++;
			}
		}

		return title.toString() + "\n" + body.toString();
	}	

	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sortData(Collection datagrid, PTableTag table) {
		String sortcol = table.getParameter(PTableTag.SORT_COL);
		String sortorder = table.getParameter(PTableTag.SORT_ORDER);
		if (sortcol!=null && !sortcol.trim().equals("") && sortorder!=null && !sortorder.trim().equals("")) {

			int rc = table.getRelativeCol(sortcol);
		    Collections.sort((List)datagrid, new PTableSorter(sortcol, null, rc, sortorder));
		    if (sortorder.equals(PTableTag.SORT_ORDER_ASCEENDING+"")) {
		    	sortorder = PTableTag.SORT_ORDER_DECENDING+"";
		    } else {
		    	sortorder = PTableTag.SORT_ORDER_ASCEENDING+"";
		    }
		    
		    PColumnTag column = table.getColumnByProperty(sortcol);
		    column.setSort(sortorder);
		}
	}

	
	private StringBuffer renderLine(ArrayList<PColumnTag> columns, PTableTag table, Object line, int rowcnt, UserTO uto) {
		StringBuffer response = new StringBuffer("<tr"); 		
        if (rowcnt % 2 == 0) {
        	response.append(" class=\"tableRowOdd\"");
        } else {
        	response.append(" class=\"tableRowEven\"");
        }
        response.append(">\n");
		
        for(int i=0; i<columns.size(); i++) {
        	PColumnTag column = columns.get(i);
        	if (column.isVisible()) {

        		ArrayList<String> values = this.renderCell(column, line, table, uto, false, false, rowcnt-1);        		
        		String sortcol = table.getParameter(PTableTag.SORT_COL);
        		
        		for (String content : values) {
            		if (sortcol!=null && sortcol.equals(column.getProperty()) && 
            				column.getTitle()!=null && !column.getTitle().equals("") && 
            				!column.getTitle().equals("grid.title.empty")) {
            			
                        if (rowcnt % 2 == 0) {
                        	response.append("<td class=\"tableCellSortOdd\">");
                        } else {
                        	response.append("<td class=\"tableCellSortEven\">");
                        }
                        
            		} else{
            			response.append("<td class=\"tableCell\">");
            		}
            		
                	response.append((content!=null&&!content.equals("")?content:"&nbsp;"));
                	response.append("</td>\n");        		        								
				}        		
        	}
        }        
       	response.append("</tr>\n");	
        
		return response;
	}


	private ArrayList<String> renderCell(PColumnTag column, Object line, PTableTag table, UserTO uto, boolean toBeSearch, boolean onlyBody, int rowNumber) {
		ArrayList<String> response = new ArrayList<String>();
		Object data = null;
		
		if (column instanceof MetaFieldPColumnTag) {
			Object addFields = getDataByReflexion(column.getProperty(), line);
			response = ((MetaFieldPColumnTag)column).getBody(column, line, table, uto, toBeSearch, addFields);
			
		} else {
			data = getDataByReflexion(column.getProperty(), line);

			if (column.getDecoratorInstance()!=null) {
				column.getDecoratorInstance().initRow(line, -1, rowNumber);			
				if (toBeSearch) {
					data = column.getDecoratorInstance().contentToSearching(data);	
				} else {
					String decContent = "";
					
					if (!onlyBody) {
						String preContent = column.getDecoratorInstance().getPreContent(data, column.getTag());
						if (preContent!=null) {
							decContent = decContent + preContent; 
						}						
					}
					 
					String contentBody = "";
					if (column.getTag()!=null && !column.getTag().trim().equals("")) {
						contentBody = column.getDecoratorInstance().decorate(data, column.getTag());
					} else {
						contentBody = column.getDecoratorInstance().decorate(data);
					}
					
					if (contentBody!=null) {
						contentBody = this.checkMaxWord(column, contentBody, uto);	
						decContent = decContent + contentBody; 
					}
					
					if (!onlyBody) {
						String postContent = column.getDecoratorInstance().getPostContent(data, column.getTag());
						if (postContent!=null) {
							decContent = decContent + postContent; 
						}						
					}
					
					data = decContent; 
				}
			} else {
				data = this.checkMaxWord(column, data+"", uto);	
			}
			
			
			if (data!=null) {
				response.add(data+"");	
			} else {
				response.add("&nbsp;");
			}			
		}
				
		return response;
	}


	private String checkMaxWord(PColumnTag column, String value, UserTO uto) {
		String response = value;

		if (column.getMaxWords()!=null && !column.getMaxWords().trim().equals("")) {
			
			
			
			String content = StringEscapeUtils.escapeHtml(value);
			int maxWords = -1;
			try {
				maxWords = Integer.parseInt(column.getMaxWords());
			} catch (Exception e) {
				maxWords = -1;
			}
			
			if (maxWords==-1) {
				String str = uto.getPreference().getPreference(column.getMaxWords());
				if (str!=null) {
					try {
						maxWords = Integer.parseInt(str);
					} catch (Exception e) {
						maxWords = -1;
					}				
				}				
			}
			
			if (maxWords>0) {
				String[] words = content.split(" ");
				String stment = "";				
				if (words.length>0) {
					for(int i=0; i<words.length; i++) {
						if (i > maxWords) {
							break;
						} else {
							stment = stment + words[i] + " ";	
						}
					}
					
					if (stment.length() < content.length()) {
						stment = stment + "<a style=\"cursor: help;\" title=\"" + content.substring(stment.length()) + "\">...</a>";
					}
					response = stment;
				}				
			}
		}
		return response;
	}


	@SuppressWarnings("rawtypes")
	private void getHeaderIcons(StringBuffer buf, PTableTag table, Collection datagrid, Collection filteredDatagrid, UserTO uto, HttpServletRequest req) {
		buf.append("<tr><td colspan=\"" + table.getColumnsNumber() + "\">\n");
		
		buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
		buf.append("<tr class=\"tableRowAction\">\n");

		//display the icon to show/hide columns
		if (table.containVisibleProperty()) { 
			String title = uto.getBundle().getMessage(uto.getLocale(), "showHide.title");
	    	buf.append("<td width=\"20\" valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n" +
    				"<a href=\"#\" onclick=\"javascript:openFloatPanel('<div id=&quot;DIV_PANEL_COL_" + table.getName() + "&quot; />'); requestPTablePanel('" + table.getName() + "', '" + table.getFrm() + "', 'show_panel');return false;\">\n" +
    				"<img src=\"../images/tablecols.png\" title=\"" + title + "\" alt=\"" + title + "\" border=\"0\" /></a>\n" +
    			"</td>\n");		    	
		}
		
		//display the icon to export to CSV
		if (table.isExportable()) {
			String expcsv = uto.getBundle().getMessage(uto.getLocale(), "exportCSV.title");		
	    	buf.append("<td width=\"20\" valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n");
	    	String uri = req.getRequestURI();
	    	buf.append("<a href=\"" + uri + "?operation=requestPTableExport&ajaxGridId=" + table.getName() + "\" class=\"tableCellHeader\">\n");
	    	buf.append("<img src=\"../images/tableexp.png\" title=\"" + expcsv + "\" alt=\"" + expcsv + "\" border=\"0\" /></a>\n" );
	    	buf.append("</td>\n");        						
		}

		//display the icon of searching by content...
		if (table.containLikeSearching()) {
			String title = uto.getBundle().getMessage(uto.getLocale(), "search.fields");
	    	buf.append("<td width=\"20\" valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n" +
    				"<a href=\"#\" onclick=\"javascript:openFloatPanel('<div id=&quot;DIV_PANEL_SCH_" + table.getName() + "&quot; />'); requestPTablePanel('" + table.getName() + "', '" + table.getFrm() + "', 'search_like');return false;\">\n" +
    				"<img src=\"../images/tablesearch.png\" title=\"" + title + "\" alt=\"" + title + "\" border=\"0\" /></a>\n" +
    			"</td>\n");		    	
		}

		//display the number of records
    	buf.append("<td width=\"130\" valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n");    	
    	buf.append(this.getResultsSummary(filteredDatagrid, uto));
    	buf.append("</td>\n");        	

		//display the filter comboBox        	
		if (table.containComboFilter() && datagrid!=null) {
	    	buf.append("<td valign=\"bottom\" align=\"left\" class=\"tableCellAction\">\n");			
	    	buf.append(this.getFilterComboBox(datagrid, table, uto));
	    	buf.append("</td>\n");
		}
    	
    	
		//display the paging control
		buf.append("<td valign=\"bottom\" align=\"right\" class=\"tableCellAction\">\n");
    	int pageSize = this.getPageSize(req, table, uto);
    	if (filteredDatagrid!=null && filteredDatagrid.size() > pageSize && pageSize>0) {
        	buf.append(this.getNavigationControl(uto, table, filteredDatagrid.size(), pageSize));
    	} else {
    		buf.append("1");	
    	}
    	buf.append("</td>\n");

		buf.append("</tr></table></td></tr>\n");
	}




	private String getColumnListToShowHide(HttpServletRequest req, UserTO uto, PTableTag table){
    	String response = "";
    	
    	this.loadColumnStatus(req, uto, table, null);
    	
        for (int c = 0; c < table.getRealColumnsNumber(); c++) {
        	PColumnTag col = table.getColumn(c);
        	if (col.getVisibleProperty()!=null && col.getDescription()!=null) {
        		String title = uto.getBundle().getMessage(uto.getLocale(), col.getDescription());
        		response = response + "<input type='checkbox' onclick='javascript:requestPTableBody(&quot;" + table.getName() + "&quot;, &quot;" + table.getFrm() + "&quot;, &quot;check=" + col.getVisibleProperty() + "&quot;)'" +
        							  " id='" + col.getDescription() + "' name='" + col.getDescription() + "'";
        		if (col.isVisible()) {
        			response = response + " checked";
        		}
        		response = response + " value='on'>" + title + "<br/>\n";
        	}
        }
                
        return response;
    }

    private void loadColumnStatus(HttpServletRequest req, UserTO uto, PTableTag table, String parameters){
        PreferenceDelegate pdel = new PreferenceDelegate();
        
    	if (uto!=null) {
    		PreferenceTO pto = uto.getPreference();
        	if (pto!=null) {

	    		//check if the checkbox was pressed...
	    		if (parameters!=null && !parameters.trim().equals("")) {
	    			String[] param = parameters.split("=");
	    			if (param!=null && param.length==2 && param[0].equals("check")) {

	    				String currentValue = pto.getPreference(param[1]);
	    				if (currentValue!=null && currentValue.trim().equals("on")) {
	    					currentValue = "off";
	    				} else {
	    					currentValue = "on";
	    				}
	    				
	    				try {
		                	PreferenceTO newPref = new PreferenceTO(param[1], currentValue, uto);
		        			uto.getPreference().addPreferences(newPref);
		        			pto.addPreferences(newPref);
		        			pdel.insertOrUpdate(newPref);	    					
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		}

	    		//return a lists of checkbox to be refreshed...
                for (int c = 0; c < table.getRealColumnsNumber(); c++) {
                	PColumnTag col = table.getColumn(c);
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
    
    
	private ArrayList<PColumnTag> getColumns(PTableTag table) {
		ArrayList<PColumnTag> response = new ArrayList<PColumnTag>(); 
		if (table.getColumnsNumber() > 0) {
		    for (int i = 0; i < table.getRealColumnsNumber(); i++) {
		        PColumnTag column = table.getColumn(i);
		        response.add(column);
		    }        	
		}
		return response;
	}

	
	private ColumnDecorator loadColumnDecorator(String decorator) {
    	ColumnDecorator response = null;
        try {
            if (decorator!=null && decorator.length() > 0) {
                @SuppressWarnings("rawtypes")
				Class c = Class.forName(decorator);
                response = (ColumnDecorator) c.newInstance();
            }        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;    	
    }
	
    
	@SuppressWarnings("rawtypes")
	private String getResultsSummary(Collection datagrid, UserTO uto) {
    	String response = "";
    	
    	if (datagrid!=null) {
            if (datagrid.size() == 0) {
            	response = uto.getBundle().getMessage(uto.getLocale(), "paging.banner.no_items_found");
                
            } else if (datagrid.size() == 1) {
                Object[] objs = { uto.getBundle().getMessage(uto.getLocale(), "paging.banner.item_name") };
                response = MessageFormat.format(uto.getBundle().getMessage(uto.getLocale(), "paging.banner.one_item_found") , objs);

            } else {
            	Object[] objs = { datagrid.size(), uto.getBundle().getMessage(uto.getLocale(), "paging.banner.items_name") }; 
                response = MessageFormat.format(uto.getBundle().getMessage(uto.getLocale(), "paging.banner.page_items_found") , objs);            
            }    		
    	}
        
        return response;
    }
     

	@SuppressWarnings("rawtypes")
	private StringBuffer getFilterComboBox(Collection datagrid, PTableTag table, UserTO uto) {
		
		HashMap<String, HashMap<String, String>>hm = new HashMap<String, HashMap<String, String>>();
		ArrayList<TransferObject> filterItems = new ArrayList<TransferObject>();
		StringBuffer response = new StringBuffer("");
		int rowNumber = 0;
    	
		ArrayList<PColumnTag> columns = this.getColumns(table);
		for (Object line : datagrid) {
			int colNum = 0;
			for (PColumnTag column : columns) {
				colNum++;
				if (column.containComboFilter()) {
					if (!(column instanceof MetaFieldPColumnTag)) {						
						ArrayList<String> values = this.renderCell(column, line, table, uto, column.getDecorator()!=null, true, rowNumber);
						getFilterComboBoxValues(hm, filterItems, colNum+"", column.getTitle(), values);						
					}
				}
			}
			rowNumber++;
		}
		
		int colNum = 0;		
		for (PColumnTag column : columns) {
			colNum++;
			if (column.containComboFilter()) {
				if (column instanceof MetaFieldPColumnTag) {
					ArrayList<MetaFieldTO> metaFields = ((MetaFieldPColumnTag)column).getComboFields();
					for (MetaFieldTO mfCol : metaFields) {
						Vector<TransferObject> vals = mfCol.getDomainList();
						ArrayList<String> values = new ArrayList<String>();
						for (TransferObject to : vals) {
							values.add(to.getGenericTag());
						}
						getFilterComboBoxValues(hm, filterItems, colNum + "_" + mfCol.getId(), mfCol.getName(), values);
					}
				}
			}
		}
		        
		for (int i=0; i<filterItems.size(); i++) {
			TransferObject ftrItem = filterItems.get(i);
			String cmbId = "filter_" + table.getName() + "_" + ftrItem.getId();
			HashMap<String, String> list = hm.get(ftrItem.getId());
			response.append("<select id=\"" + cmbId + "\" name=\"" + ftrItem.getGenericTag() + "\"  class=\"textBoxSmall\" " +
					"onchange=\"javascript:requestPTableBody('" + table.getName() + "', '" + table.getFrm() + "', '" + cmbId + "=' + this.value);return false;\">");

			String label = uto.getBundle().getMessage(uto.getLocale(), ftrItem.getGenericTag());
	        if (label.startsWith("???")) {
	        	label = ftrItem.getGenericTag();
	        }
			response.append("<option value=\"\">" + label + "...</option>");

			ArrayList<TransferObject> olist = this.getOrderedList(list);

			String currentVal = table.getParameter(cmbId);
			for (TransferObject item : olist) {
				
				String itemLabel = uto.getBundle().getMessage(uto.getLocale(), item.getGenericTag());
		        if (itemLabel.startsWith("???")) {
		        	itemLabel = item.getGenericTag();
		        }
				
				String selectedLabel = "";
				if (currentVal!=null && currentVal.equals(itemLabel)) {
					selectedLabel = "selected";
				} else {
					if (currentVal!=null) {
						try {
    						String encoding = SystemSingleton.getInstance().getDefaultEncoding();					
							String currentValWithEncoding = new String(currentVal.getBytes(), encoding);
							if (currentValWithEncoding!=null && currentValWithEncoding.equals(itemLabel)) {
								selectedLabel = "selected";		
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				response.append("<option " + selectedLabel + " value=\"" + itemLabel + "\">" + itemLabel + "</option>");	
			}
			response.append("</select>");			
		}

        return response;
    }


	private ArrayList<TransferObject> getOrderedList(HashMap<String, String> list) {
		ArrayList<TransferObject> response = new ArrayList<TransferObject>();
		
		for (String val : list.values()) {
			TransferObject to = new TransferObject(val, val);
			response.add(to);
		}
		Collections.sort(response);
		
		return response;
	}


	private void getFilterComboBoxValues(HashMap<String, HashMap<String, String>> hm, ArrayList<TransferObject> filterItems,
			String colNum, String columnTitle, ArrayList<String> values) {
		
		HashMap<String, String> list = hm.get("" + colNum);
		if (list==null) {
			list = new HashMap<String, String>();
			hm.put(colNum, list);
			
			TransferObject to = new TransferObject(colNum, columnTitle);
			filterItems.add(to);
		}
		
		for (String cellContent : values) {
			if (list.get(cellContent)==null) {
				list.put(cellContent, cellContent);
			}						
		}
	}

	
	private String getSearchLikeContent(HttpServletRequest req, UserTO uto, PTableTag table){
		String response = "";
		
		String fieldList = "";
		for (PColumnTag column : this.getColumns(table)) {
			if (column.containLikeSearching()) {
				if (!fieldList.equals("")) {
					fieldList = fieldList + ", ";
				}
				String title = "";
				if (column.getTitle()!=null && !column.getTitle().equals("")) {
					title = uto.getBundle().getMessage(uto.getLocale(), column.getTitle());
				}
				fieldList = fieldList + "[" + title + "]";
			}
		}
		
		if (!fieldList.trim().equals("")) {
			response = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
						"<tr class=\"tableRowAction\">" +
							"<td width=\"40\" align=\"right\" class=\"tableCellAction\">" + uto.getBundle().getMessage(uto.getLocale(), "search.title") + ": </td>" +
							"<td width=\"105\" class=\"tableCellAction\">&nbsp;&nbsp;" +
								"<input maxlength=\"30\" id=\"" + table.getName() + "_search_text\" name=\"" + table.getName() + "_search_text\" size=\"10\" value=\"\" class=\"textBoxSmall\">&nbsp;" +
								"<input type=\"button\" name=\"filter\" onclick=\"javascript:requestPTableBody('" + table.getName() + "', '" + table.getFrm() + "', 'search_" + table.getName() + "=' + " + table.getName() + "_search_text.value);\" value=\"" + 
									uto.getBundle().getMessage(uto.getLocale(), "search.button") + "\" class=\"button\">" +
							"</td>" +
						"</tr><tr class=\"tableRowAction\">" +							
							"<td colspan=\"2\"><div class=\"tableCellAction\"><br>" + uto.getBundle().getMessage(uto.getLocale(), "search.fields") + ": " +
								fieldList + "</div></td>" +
						"</tr></table>";			
		}
		
		return response;
	}

    
    private String getNavigationControl(UserTO uto, PTableTag table, int gridSize, int pageSize) {
    	StringBuffer buff = new StringBuffer();
    	
    	int currentPage = this.getPageCursor(table);
    	int numberPages = gridSize / pageSize;
    	if((gridSize % pageSize) > 0) {
    		numberPages++;
    	}

    	if (numberPages>0) {
        	int start = ((currentPage / MAX_PAGES_NAVIGATION) * MAX_PAGES_NAVIGATION) + 1;
        	int end = start + MAX_PAGES_NAVIGATION;
        	if (end > numberPages) {
        		end = numberPages;
        	}
        	
        	String privLbl = uto.getBundle().getMessage(uto.getLocale(), "paging.banner.prev_label");
        	String hrefStart = "<a href=\"#\" onclick=\"javascript:requestPTableBody('" + table.getName() + "', '" + table.getFrm() + "', 'page=";
        	String hrefEnd = "');return false;\">";
        	
			if (currentPage==0) {
				buff.append("[" + privLbl + "]");
			} else {
				buff.append("[" + hrefStart + (currentPage-1) + hrefEnd + privLbl + "</a>]");
			}
        	
    		for(int c=start; c<=end; c++) {
    			buff.append(c>start?", ":" ");
    			if (c==(currentPage+1)) {
    				buff.append("<b>" + c + "</b>");
    			} else {
    				buff.append(hrefStart + (c-1) + hrefEnd + c + "</a>");
    			}
    		}
    		
        	String nextLbl = uto.getBundle().getMessage(uto.getLocale(), "paging.banner.next_label");
			if (currentPage==(numberPages-1)) {
				buff.append(" [" + nextLbl + "]");
			} else {
				buff.append(" [" + hrefStart + (currentPage+1) + hrefEnd + nextLbl + "</a>]");
			}
    	}
    	    	
		return buff.toString();
	}


	public static Object getDataByReflexion(String fullName, Object obj) {
		Object val = obj;
		try {
			String[] methods = fullName.split("\\.");			
			for (String token : methods) {
				if (val!=null) {
					String methodName = "get" + token.substring(0, 1).toUpperCase() + token.substring(1);
					Method m = val.getClass().getMethod(methodName);
					val = m.invoke(val);					
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

    
	private int getPageCursor(PTableTag table) {
		int currentPage = 0;
		String currPageStr = table.getParameter("page");
    	if (currPageStr!=null && !currPageStr.trim().equals("")) {
    		currentPage = Integer.parseInt(currPageStr);
    	}
		return currentPage;
	}

	
	private int getPageSize(HttpServletRequest req, PTableTag table, UserTO uto) {
		int response = 0;
		
		if (table.getParenttag()==null) {
			
			try {
				response = Integer.parseInt(table.getPagesize());
			}catch(Exception e) {
				response = -1;
			}
			
			if(response == -1 && table.getPagesize()!=null && !table.getPagesize().trim().equals("")) {
				String prop = uto.getPreference().getPreference(table.getPagesize());
				try {
					response = Integer.parseInt(prop);
				}catch(Exception e) {
					response = -1;
				}
				
				if(response == -1) {
					try {
						prop = ""+req.getSession().getAttribute(table.getPagesize());
						response = Integer.parseInt(prop);
					}catch(Exception e) {
						response = 0;
					}					
				}
			}
			
			if (response==-1) {
				response = 0;
			}			
			
		}
		
		return response;
	}
	
	
	@SuppressWarnings("rawtypes")
	private void setupDecorator(HttpServletRequest req, Collection datagrid, ArrayList<PColumnTag> columns) {
		for (PColumnTag column : columns) {
		    String decoratorName = column.getDecorator();
		    if (decoratorName!=null) {
		    	ColumnDecorator dec = this.loadColumnDecorator(decoratorName);
		        if (dec != null) {
		            dec.init(req.getSession(), datagrid);			            	
		        	column.setDecoratorInstance(dec);
		        }		            	
		    }					
		}
	}

}