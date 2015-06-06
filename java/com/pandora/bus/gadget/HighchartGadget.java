package com.pandora.bus.gadget;

import java.awt.Font;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.delegate.GadgetDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.StringUtil;

public class HighchartGadget extends Gadget {

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
	
	public StringBuffer gadgetToHtml(HttpServletRequest request, HttpServletResponse response, int width, int height, String loadingLabel) throws BusinessException{
	    StringBuffer content = new StringBuffer();
	    GadgetDelegate del = new GadgetDelegate();

	    String name = this.getClass().getName();
	    if (response!=null) {
	    	del.renderContent(request, response, name);	
	    }

		content.append("<div id=\""+ this.getId() + "\" ></div>\n");
		
		content.append("<script type=\"text/javascript\">\n");

		content.append("  function callGadget_" + this.getId() + "(c, w, h){\n");	
		content.append("      document.getElementById('"+ this.getId() + "').innerHTML = \"<center><img src='../images/indicator.gif' border='0'></center>\";\n");	
		content.append("      with(document.forms['resourceHomeForm']){\n");		
		content.append("         operation.value = 'renderGadget';\n");	
		content.append("         gagclass.value = '" + name + "';\n");
		content.append("      }\n");	
		content.append("      var ajaxRequestObj = ajaxSyncInit();");
		content.append("      ajaxSyncProcess(document.forms[\"resourceHomeForm\"], callGadget_" + this.getId() + "_callBack, c, '', ajaxRequestObj);\n"); 		
		content.append("  }\n");
		content.append("  function callGadget_" + this.getId() + "_callBack(c, argTp, objRequest){\n");	
		content.append("      if(c && isSyncAjax(objRequest)){ \n");
		content.append("         document.getElementById('ajaxResponse').innerHTML = '';\n");
		content.append("         document.getElementById(c).innerHTML = '';\n");
		content.append("         var content = objRequest.responseText;\n");		
		content.append("         var mp = c.indexOf('maximized_gadget');\n");
		content.append("         if (mp>-1) {\n");
		content.append("            content = content.replace('container_"+ this.getId() + "','maximized_gadget');\n");
		content.append("            document.getElementById('"+ this.getId() + "').innerHTML = '';\n");
		content.append("         }\n");
		content.append("         var g = document.createElement('script');");
		content.append("         var s = document.getElementsByTagName('script')[0];");
		content.append("         g.text = content; ");
		content.append("         s.parentNode.insertBefore(g, s);");
		content.append("      }\n");
		content.append("  }\n");
		content.append("  window.setTimeout(\"callGadget_" + this.getId() + "('" + this.getId() + "','" + width + "','" + height + "')\", 1000);");
		content.append("</script>\n");
		content.append("<div id=\"container_"+ this.getId() + "\" style=\"min-width:" + width + "px; height:" + height + "px; overflow-x:hidden; overflow-y:hidden; margin: 0 auto\"></div>\n");		

		return content;
	}

	
	public int getWidth(){
		return 250;
	}
	
	public int getHeight(){
		return 200;
	}
	
	
	protected String generate(Vector selectedFields) throws BusinessException {
		throw new  BusinessException("This method must be implemented by sub-class.");
	}
	
	protected String getHeader(String id, String type, String title, String subtitle, boolean isPolar) {
		String response = "$(function () { " +
		        "$('#container_"+ id + "').highcharts({ " +
		            "chart: { " +
		            	(isPolar?"polar: true, ":"") +
		                "type: '" + type + "' " +
		            "}, " +
		            "title: { " +
		                "text: '" + title + "', " +
		                "style: {color: '#000000', fontSize: '10px', fontWeight: 'bold'} " +
		            "}, ";
        if (subtitle!=null) {
        	response = response + 
        		"subtitle: { " +
	                "text: '" + subtitle + "', " +
	                "style: {color: '#000000', fontSize: '9px'} " +
	            "}, ";
        }
        return response;
	}

	protected String getLegend(boolean enable) {
		return
			"legend: { " +
			   "enabled: " + (enable?"true":"false") + ", " +
               "itemStyle: { " +
         	   	  "cursor: 'pointer', " +
         	      "color: '#274b6d', " +
         	      "fontSize: '9px', " +
         	      "itemDistance: '2' " +
         	   "}}, ";
	}

	protected String getXAxis(String[] xaxis, boolean isPolar) {
		String response = 
        		"xAxis: { " +
                "categories: [ ";
		if (xaxis!=null) {
			for(int i=0; i<xaxis.length; i++) {
				if (i>0) {
					response = response + ",";	
				}
				response = response + "'" + xaxis[i] + "'";
			}			
		} else {
			response = response + "''";
		}
		response = response + " ], " + (isPolar?"tickmarkPlacement: 'on', lineWidth: 0":"labels: {rotation: -90, style: {fontSize: '8px'}}") +  " }, ";

		return response;
	}

	protected String getYAxis(int min, String content) {
		return 
		"yAxis: { " +
    		(content!=null?"title: { text: '" + content + "'}":"gridLineInterpolation: 'polygon', lineWidth: 0") +				
        	", min: " + min + 
        "}, ";
	}

	
	protected String getTooltip() {
		return 
	            "tooltip: { " +
            	"style: {color: '#000000', fontSize: '9px', padding: '8px'} " +
            "}, ";
	}

	protected String getPlot(String[] labels, float[][] valMatrix, boolean isStack, String barColors[]) {
		String response = "";
		
		if (barColors==null) {
			barColors = new String[]{"000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF", "000040", "8080FF", "0080FF", "8000FF", "80FF80", "800040", "800000",  "FF0000", "FF8C37", "FFDC39", "FFFF00", "BEF01E", "5AFF28", "008000", "00FF00", "808000", "808040", "808080", "C0C0C0", "00FFFF"};			
		}
				
		String colorList = "";
		String serieList = "";
		
		if (labels!=null && valMatrix!=null && valMatrix.length>0 && labels.length>0 && labels.length==valMatrix[0].length) {
			for(int i=0; i<labels.length; i++) {
				if (i>0) {
					colorList = colorList + ", ";
				}
				colorList = colorList + "'#" + barColors[i] + "'";
			}
			
			serieList = getSeries(labels, valMatrix, barColors, false);
		}
		
		String plotOptStr =
				"plotOptions: { " +
	                "column: { " +
	                	(isStack?"stacking: 'normal', ":"") +
	                	"pointPadding: 0, " +
	                	"borderWidth: 0 " +
	                "} " +
	                ",series: { " +
	            	    "color: [" + colorList + "]" +
	            	"} " +
	             "}, ";
		
		
		response = plotOptStr + serieList;
		
		return response;
	}

	protected String getSeries(String[] labels, float[][] valMatrix, String barColors[], boolean isPolar){
		String response = "series: [{";
		if (labels!=null && valMatrix!=null && valMatrix.length>0 && labels.length>0) {
			for(int i=0; i<labels.length; i++) {
				if (i>0) {
					response = response + "}, { ";
				}
				response =  response + (barColors!=null?"color: '#" + barColors[i]+"', ":"") + "name: '" + labels[i] + "', data: [";
				for(int j=0; j<valMatrix.length; j++) {
					if (j>0) {
						response = response + ", ";
					}
					response = response + StringUtil.getFloatToString(valMatrix[j][i], "0.#", new Locale("en")) ;
				}
				response = response + "] ";

				if (isPolar) {
					response = response + ",pointPlacement: 'on' ";
				}
			}
		}
		return response + "}] ";
	}
	
	protected String getFooter() {
		return "}); });\n";
	}

	
	protected void cropMatrix(float[][] sourceArray, int size){
		if (sourceArray!=null) {
			for (int j=0; j<sourceArray.length; j++) {
				float[] item = sourceArray[j];
				float[] newItem = new float[size];
				for (int i=0; i<newItem.length; i++) {
					newItem[i] = item[i];
				}
				sourceArray[j] = newItem;
			}
		}	
	}

		
	protected String[] cropArray(String[] sourceArray){
		String[] response = new String[0];
		
		if (sourceArray!=null) {
			ArrayList<String> cropList = new ArrayList<String>();
			for (String item : sourceArray) {
				if (item!=null) {
					cropList.add(item);
				}
			}
			
			response = new String[cropList.size()];
			for (int i=0; i<cropList.size(); i++) {
				response[i] = cropList.get(i);
			}
		}	
		
		return response;
	}
	
	
}
