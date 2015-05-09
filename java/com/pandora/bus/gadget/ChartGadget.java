package com.pandora.bus.gadget;

import java.awt.Font;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.StringUtil;


public class ChartGadget extends Gadget {
	
	public static final Font CHART_GADGET_FONT = new Font("sans serif", Font.BOLD, 10);
	
	public final void process(HttpServletRequest request, HttpServletResponse response, Vector selectedFields) throws BusinessException{
		try {
			
            response.setContentType("text/json");  
            response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);            
            PrintWriter out = response.getWriter();
            String jsonContent = "";
			try {
				jsonContent = generate(selectedFields);	
			} catch(Exception e) {
				e.printStackTrace();
				jsonContent = e.getMessage();
			}
		    out.println(jsonContent);
		    
		} catch(Exception e) {
			throw new  BusinessException(e);
		}
	}
	
	public StringBuffer gadgetToHtml(HttpServletRequest request, int width, int height, String loadingLabel) throws BusinessException{
	    StringBuffer content = new StringBuffer();
	    
	    String id = getId();
	    String name = this.getClass().getName();
	    
		content.append("<div id=\""+ id + "\" style=\"z-index: -1; position: absolute; width:" + width + "px; height:" + height + "px; overflow-x:scroll; overflow-y:hidden; border:1px\">\n");
		content.append("</div><br>\n");
		content.append("<script type=\"text/javascript\">\n");
		content.append("function callGadget_" + id + "(container, w, h){\n");				
		content.append("   swfobject.embedSWF(\"open-flash-chart\", container, w, h, " + 
				"\"9.0.0\", \"expressInstall\",{\"data-file\" : \"../do/manageResourceHome?gagclass=" + 
				name + "%26operation=renderGadget\", \"loading\" : \"" + loadingLabel + "\"}, {\"wmode\":\"opaque\"}, {\"wmode\":\"opaque\"});");				
		content.append("}");
		content.append("callGadget_" + id + "('" + id + "','" + width + "','" + height + "');");			
		content.append("</script>");
	    
		return content;
	}

	
	public int getWidth(){
		return 250;
	}
	
	public int getHeight(){
		return 150;
	}
	
	
	protected String generate(Vector selectedFields) throws BusinessException {
		throw new  BusinessException("This method must be implemented by sub-class.");
	}
	
	
	/*
	 * The float value must be formated using the pattern "0.X"
	 */
	private String formatFloat(float value){
		return StringUtil.getFloatToString(value, new Locale("en", "us"));
	}
	
	protected String getJSonTitle(){
		String title = this.getI18nMsg(this.getUniqueName());	
		return getJSonTitle(title);
	}
	
	protected String getJSonTitle(String title){
		return "\"title\":{\n" +
        	"\"text\":  \"" + title + "\",\n" +
        	//"\"tooltip\": { \"shadow\": true, \"title\": \"{font-size: 9px; color: #808080;}\", \"body\": \"{font-size: 9px; color: #808080;}\" }, \n" +
        	"\"style\": \"{font-size: 9px; color:#000080; font-family: Verdana; text-align: center;}\"\n" +
        	"}, " +
        	"\"bg_colour\": \"#FFFFFF\" \n";		
	}
	
	protected String getJSonYLegend(String content){
		return "\"y_legend\":{\n" +
	    	"\"text\": \"" + content + "\",\n" +
	    	"\"style\": \"{color: #000080; font-size: 10px;}\"\n" +
	    	"}\n";

	}
	
	protected String getBarStackValues(float[][] vals, String labels[]){
	    return getBarStackValues(vals, labels, null, null, null, null, true);    
	}
	
	protected String getBarStackValues(float[][] vals, String labels[], float[][] lineVals, String lineNames[], String lineColors[], boolean isVerticalLabels){
		return getBarStackValues(vals, labels, null, lineVals, lineNames, lineColors, true);
	}
	
	protected String getBarStackValues(float[][] vals, String labels[], String[] barColors, float[][] lineVals, String lineNames[], String lineColors[], boolean isVerticalLabels){
		
		if (barColors==null) {
			barColors = new String[]{"000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF"};			
		}
		
		String response = "	  \"elements\":[\n";				
		response = response + " {\n" +
							  "\"type\":   \"bar_stack\",\n" +
        					  "\"alpha\":  0.5,\n" +
        					  "\"outline-colour\": \"#000000\",\n" +
                              "\"font-size\": 9,\n";

		//write the chart values 
		response = response + "\"values\" :   [";
		for (int i=0; i<vals.length;i++) {
			boolean isFirst = true;
			
			String line = "";
			for (int j=0; j<vals[i].length;j++) {
				if (vals[i][j]>0) {
					if (!isFirst) {
						line = line + ",";
					}
					
					line = line + "{\"val\": " + formatFloat(vals[i][j]) + ", \"tip\": \"#val#";
					int idx = (isVerticalLabels?j:i);
					if (labels!=null && labels[idx]!=null && !labels[idx].trim().equals("")) {
						line = line + " [" + labels[idx] + "] #x_label#\", ";						
					} else {
						line = line + "\", " ;						
					}						

					line = line + "\"colour\": \"#" + barColors[j] + "\"} " ;
					
					isFirst = false;
				}
			}
			
			if (line.equals("")) {
				line = line + "{\"val\": 0 , \"tip\": \"#val#\"} " ;
			}
			response = response + "[" + line + "]\n";				
			if (i+1<vals.length) {
				response = response + ",";	
			}
		}
		response = response + "]\n";
		
		if (lineVals!=null && lineVals[0]!=null) {
			response = response + "},";

			for (int i=0; i<lineVals[0].length;i++) {
				
				response = response + "{\"type\": \"line\",\n";
			    response = response + "\"values\": [";

			    boolean isFirst = true;
			    for (int j=0; j<lineVals.length;j++) {
			        if (lineVals[j][i]>-1) {
			            if (!isFirst) {
			            	response = response + ", ";
			            }
			            response = response + "{ \"type\": \"dot\", \"value\": " + formatFloat(lineVals[j][i]) + " } " ;
			            isFirst = false;
			        }
			    }

				response = response + "], ";
				response = response + "\"dot-style\": { \"type\": \"dot\", \"dot-size\": 3, \"halo-size\": 1}, ";
			    if (lineNames!=null && lineNames[i]!=null) {
			    	response = response + "\"text\": \"" + lineNames[i] + "\", \"font-size\": 10, ";    
			    }
			    
			    if (lineColors!=null && lineColors[i]!=null) {
			    	response = response + "\"colour\": \"#" + lineColors[i] + "\" ";	
			    } else {
			    	response = response + "\"colour\": \"#0000CC\" ";
			    }
			    
			    
			    if (i+1<lineVals[0].length) {				
					response = response + "},\n";	
				} else {
					response = response + "} \n";
				}
			}
		} else {
			response = response + "}\n";	
		}

		response = response + "]\n";
		
		return response;
  	}

	
	protected String getAreaChartValues(float[][] vals, String labels[], float[][] lineVals, String lineNames[], String colorList[], boolean isVerticalLabels){
		if (colorList==null) {
			colorList = new String[]{"000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF"}; 
		}

		String response = " \"elements\":[\n";
		
		for (int i=vals.length-1; i>=0; i--) {
			response = response + "{" +
					  "\"type\": \"area\",\n" +
					  "\"width\": 1, " +
					  "\"dot-style\": { \"type\": \"dot\", \"colour\": \"#" + colorList[i] + "\", \"dot-size\": 2 }, " +
					  "\"colour\": \"#" + colorList[i] + "\"," +
					  "\"fill\": \"#" + colorList[i] + "\", \"fill-alpha\": 1, "; //+
					  //"\"on-show\": { \"type\": \"pop-up\", \"cascade\": 2, \"delay\": 0.5 },";

			response = response + "\"values\" : [";
			boolean isFirst = true;
			String area = "";
			for (int j=0; j<vals[i].length;j++) {
				if (!isFirst) {
					area = area + ",";
				}
				area = area + formatFloat(vals[i][j]);
				isFirst = false;
			}
			
			if (area.equals("")) {
				area = area + "0";
			}
			response = response + area + "]}\n";				
			if (i>0) {
				response = response + ",";	
			}
		}
		response = response + "] ";
		return response;
	}

	
	protected String getPieValues(float[] vals, String[] labels){
		String response = "	  \"elements\":[\n {\n " +
							 "\"type\":   \"pie\",\n" +
							 " \"alpha\":  0.5,\n" +
							 " \"start-angle\": 35,\n" +
							 " \"animate\": [ { \"type\": \"fade\" } ],\n" +
							 " \"tip\": \"#val#h [#percent# : #label#]\",\n" +
							 " \"colours\": [ \"#000080\", \"#5E5EFF\", \"#8080FF\", \"#8080FF\" ],\n";

		response = response + "\"values\" :   [";
		boolean isFirst = true;
		for (int j=0; j<vals.length;j++) {
			if (labels[j]!=null) {
				if (!isFirst) {
					response = response + ",";
				}
				response = response + "{\"value\": " + formatFloat(vals[j]) + ", \"label\": \"" + labels[j] + "\"} " ;
				isFirst = false;
			}
		}
		response = response + "]\n";
		
		response = response + " }\n ]\n";

		return response;
	}

	protected String getBarValues(float[][] vals){
		String[] colorList = new String[]{"000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000", "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000", "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF"};
		String response = "	  \"elements\":[\n";
				
		for (int i=0; i<vals[0].length;i++) {
			response = response + " {\n" +
								  "\"type\":   \"bar_glass\",\n" +
	        					  "\"alpha\":  0.8,\n" +
	                              "\"colour\": \"#"+ colorList[i] + "\",\n" +
	                              "\"font-size\": 8,\n";
			
			response = response + "\"values\" :   [";
			boolean isFirst = true;
			for (int j=0; j<vals.length;j++) {
				if (vals[j][i]>0) {
					if (!isFirst) {
						response = response + ", ";
					}
					response = response + formatFloat(vals[j][i]);
					isFirst = false;
				}
			}
			response = response + "]\n";
			
			if (i+1<vals[0].length) {
				response = response + "},\n";	
			}
		}
		response = response + " }\n" +
        					  " ]\n";

		return response;
  	}

	protected String getRadarValues(float[][] vals, String[][] labels, String name[]){
		String response = "	  \"elements\":[\n";
		String[] colorList = new String[]{"000040", "800000", "0080FF", "8000FF"};
		String[] colorListBg = new String[]{"000080", "AA0000", "0090FF", "AA00FF"};
		
		for (int i=0; i<vals.length; i++) {
			response = response + "{" +
			   "\"type\": \"area\",\n" +
			   "\"dot-style\": { \"type\": \"hollow-dot\", \"colour\": \"#" + colorListBg[i] + "\", \"dot-size\": 3 },\n" +
               "\"colour\": \"#" + colorList[i] + "\", \"font-size\": 9,\n" +
               "\"fill\": \"#" + colorListBg[i] + "\", \"fill-alpha\": 0.3, \n" +	                              
               "\"width\": 1, \"text\": \"" + name[i] + "\", \"loop\": true,\n";

			response = response + "\"values\": [";
			boolean isFirst = true;
			for (int j=0; j<vals[i].length;j++) {
				if (vals[i][j]>-1) {
					if (!isFirst) {
						response = response + ", ";
					}
					
					//response = response + formatFloat(vals[j]);	
					response = response + "{\"value\": " + formatFloat(vals[i][j]) + ", \"tip\": \"" + labels[i][j] + "\"} " ;
					isFirst = false;
				}
			}
			response = response + "] ";
			
			if (i+1<vals.length) {
				response = response + "},\n";	
			} else {
				response = response + "}";
			}
		}
		
		response = response + "]\n";

		return response;
  	}

	protected String getLineValues(float[] vals, String name){
		String response = "	  \"elements\":[\n";
								
		response = response + "{" +
							  "\"type\": \"line\",\n";                             
		
		response = response + "\"values\": [";
		boolean isFirst = true;
		for (int j=0; j<vals.length;j++) {
			if (vals[j]>-1) {
				if (!isFirst) {
					response = response + ", ";
				}
				response = response + "{ \"type\": \"hollow-dot\", \"value\": " + formatFloat(vals[j]) + ", \"colour\": \"#D02020\", \"tip\": \"#x_label# #val#min\" } " ;
				isFirst = false;
			}
		}
		
		response = response + "], ";
		response = response + "\"text\": \"" + name + "\", \"font-size\": 10 } ";
		
		response = response + "] } ]\n";

		return response;
  	}

	
	protected String getMultiLineValues(float[][] vals, String[] labels){
		String[] colorList = new String[]{"000040", "800000", "0080FF", "8000FF"};
		int size = vals[0].length;

		String response = "	  \"elements\":[\n";
		boolean isFirstLine = true;
		
		for (int i=0; i<size; i++) {
			if (!isFirstLine) {
				response = response + ", ";
			} else {
				isFirstLine = false;
			}
			
			response = response + "{ \"type\": \"line\",\n";                             

			response = response + "\"values\": [";
			boolean isFirst = true;
			for (int j=0; j<vals.length;j++) {
				if (vals[j][i]>-1) {
					if (!isFirst) {
						response = response + ", ";
					}
					response = response + formatFloat(vals[j][i]);
					isFirst = false;
				}
			}
			
			response = response + "], ";
			if (labels!=null) {
				response = response + "\"text\": \"" + labels[i] + "\", \"font-size\": 10, ";	
			}
			response = response + "\"dot-style\": { \"type\": \"dot\", \"dot-size\": 3, \"halo-size\": 1, \"colour\": \"#" + colorList[i] + "\" }, ";
			response = response + "\"width\": 2, \"colour\": \"#" + colorList[i] + "\"} ";
		}		
		response = response + "] \n";

		return response;
  	}
	
	
	private float getUBound(float[][] vals, boolean isStackBar) {
		float maxTotal = 0;
		if (vals!=null) {
			for (int i=0; i<vals.length;i++) {
				float buff = 0;
				for (int j=0; j<vals[i].length;j++) {
					if (isStackBar) {
						buff = buff + vals[i][j];	
					} else {
						buff = vals[i][j];
						if (buff>maxTotal) {
							maxTotal = buff;
						}											
					}
				}
				if (isStackBar) {
					if (buff>maxTotal) {
						maxTotal = buff;
					}					
				}
			}			
		}
		return maxTotal;
	}

	protected String getJSonAxis(String[] labels, float[][] vals, String axisType){
		return getJSonAxis(labels, vals, null, axisType);
	}
	protected String getJSonAxis(String[] labels, float[][] vals, float[][] valLine, String axisType){
		return getJSonAxis(labels, vals, valLine, axisType, true);
	}
	
	protected String getJSonAxis(String[] labels, float[][] valBar, float[][] valLine, String axisType, boolean isStackBar){
		String response = "";
		float maxTotal = 0;
		
		response = response + "\"" + axisType + "\": {\n";		  
		response = response +  "\"colour\":\"#C0C0C0\", \"font-size\": 8, \n" +
	      					   "\"grid_colour\":\"#C0C0C0\",\n";		
		if (labels!=null) {
			
			if (axisType.equals("radar_axis")) {
				response = response + "\"max\": " + (labels.length-1) + ", ";
			}
			
			response = response + "\"labels\": {\n ";
			response = response + "\"rotate\": 270, \"style\": \"{font-size: 8}\", \"colour\": \"#808080\", \"labels\": [";

			boolean isFirst = true;
			for (int i=0;i<labels.length; i++) {
				if (labels[i]!=null) {
					if (!isFirst) {
						response = response + ",";
					}
					response = response +  "\"" + labels[i] + "\"";
					isFirst = false;
				}
			}
			response = response + "]\n }\n";
		} else {
			maxTotal = this.getUBound(valBar, isStackBar);
			if (valLine!=null){
				float maxLine = this.getUBound(valLine, false);
				if (maxLine>maxTotal){
					maxTotal = maxLine;
				}
			}
			response = response + " \"min\":   0,\n" +
								  " \"steps\": " + ((int)(maxTotal/4)) + ",\n" +
                                  "  \"max\":  " + ((int)(maxTotal+1)) + " \n";
		}
		
		response = response + "}\n";
		
		return response;
	}

	
	protected Vector<TransferObject> getAtiveUsersByProject(String projectId) throws BusinessException {
		return getAtiveUsers(null, projectId);
	}
	
	
	protected Vector<TransferObject> getAtiveUsers(String userId) throws BusinessException{
		return getAtiveUsers(userId, null);
	}
	
	
	private Vector<TransferObject> getAtiveUsers(String userId, String projectId) throws BusinessException{
    	Vector userlist = new Vector();
    	DbQueryDelegate qdel = new DbQueryDelegate();
    	HashMap hm = new HashMap();
    	
    	TransferObject defaultOpt = new TransferObject("-1", "label.combo.select");
    	TransferObject separator = new TransferObject("-1", "");
    	userlist.addElement(defaultOpt);

    	//get the current active users related to the project where 'userId' is allocated
    	String sqlData = "select distinct t.id, t.name from resource r, customer c, tool_user t " +
    			"where t.id = r.id and t.id = c.id and c.project_id = r.project_id and " +
    			"(c.is_disable = 0 or c.is_disable is null) and t.final_date is null and t.username <> 'root' and ";
    	if (userId!=null) {
    		sqlData = sqlData + "r.project_id in (select project_id from resource where id= '" + userId + "' ) ";
    	} else {
    		sqlData = sqlData + "r.project_id ='" + projectId + "' ";
    	}
    	sqlData = sqlData + "order by name";
    			
    	Vector dbAllocList = qdel.performQuery(sqlData, null, null);
    	if (dbAllocList!=null) {
    		for (int i=1; i<dbAllocList.size(); i++) {
    			Vector item = (Vector)dbAllocList.elementAt(i);
    			TransferObject to = new TransferObject((String)item.elementAt(0), (String)item.elementAt(1));
    			hm.put(to.getId(), to);
    			userlist.addElement(to);
    		}
    	}

    	//get the inactive users related to the project where 'userId' is allocated    	
    	String sqlData2 = "select distinct t.id, t.name from resource r, customer c, tool_user t " +
			"where t.id = r.id and t.id = c.id and c.project_id = r.project_id and " +
			"c.is_disable = 1 and t.username <> 'root' and t.final_date is null and ";
    	if (userId!=null) {
    		sqlData2 = sqlData2 + "r.project_id in (select project_id from resource where id= '" + userId + "' ) ";
    	} else {
    		sqlData2 = sqlData2 + "r.project_id ='" + projectId + "' ";
    	}
    	sqlData2 = sqlData2 + "order by name";
    	
    	dbAllocList = qdel.performQuery(sqlData2, null, null);
    	if (dbAllocList!=null && dbAllocList.size()>0) {
    		boolean isSep = false;

    		for (int i=1; i<dbAllocList.size(); i++) {
    			Vector item = (Vector)dbAllocList.elementAt(i);
    			TransferObject to = new TransferObject((String)item.elementAt(0), (String)item.elementAt(1));
    			if (hm.get(to.getId())==null) {
    				
    				if (!isSep) {
    		    		userlist.addElement(separator);
    		    		isSep = true;
    				}
    				
    				userlist.addElement(to);	
    			}
    		}
    	}
    	
		return userlist;
	}	
		
}
