package com.pandora.gui.gantt;

import java.util.StringTokenizer;

public class FilterLayer {

	/** String used by PARAM applet tag */    
	public static final String FILTER_LAYER_NUM = "FILTER_LAYER_NUM";
	
	/** String used by PARAM applet tag */	
	public static final String FILAYER          = "FILAYER_";

	private String layerId;
	
	private String filterId;
	
	private Layer layer;
	
	/**
	 * Constructor. 
	 * Initialize attributes of the new Filter object, parsing information from applet PARAM
	 */
  	public FilterLayer(String s) {
		StringTokenizer stList = new StringTokenizer(s, "|");
    	this.filterId = stList.nextToken();
    	this.layerId = stList.nextToken();
  	}

  	
  	/////////////////////////////////////
	public String getLayerId() {
		return layerId;
	}


  	/////////////////////////////////////	
	public String getFilterId(){
		return this.filterId;
	}

	
  	/////////////////////////////////////
	public Layer getLayer() {
		return layer;
	}
	public void setLayer(Layer newValue) {
		this.layer = newValue;
	}
	
}
