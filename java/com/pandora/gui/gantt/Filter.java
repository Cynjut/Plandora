package com.pandora.gui.gantt;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;


public class Filter {

	/** String used by PARAM applet tag */    
	public static final String FILTER_NUM       = "FILTERNUM";
	
	/** String used by PARAM applet tag */	
	public static final String FILTER           = "FIL_";

	
	private String id;
	
	private String name;
	
	private Vector filterLayerList;
	
	
	/**
	 * Constructor. 
	 * Initialize attributes of the new Filter object, parsing information from applet PARAM
	 */
  	public Filter(String s) {
		StringTokenizer stList = new StringTokenizer(s, "|");
    	this.id = stList.nextToken();
    	this.name = stList.nextToken();
  	}
  	
  	
  	public void addFilterLayer(FilterLayer fla) {
  		if (this.filterLayerList==null) {
  			this.filterLayerList = new Vector();
  		}
  		this.filterLayerList.add(fla);
  	}
  	
	public Vector getLayerList() {
		Vector response = null;
		if (this.filterLayerList!=null) {
			Iterator i = this.filterLayerList.iterator();
			while(i.hasNext()) {
				FilterLayer fla = (FilterLayer)i.next();
				if (response!=null) {
					response = new Vector();
				}
				response.add(fla.getLayer());
			}
		}
		return response;
	}
  	
	//////////////////////////////////////////
	public void setId(String newValue) {
		this.id = newValue;
	}
	public String getId() {
		return id;
	}

	//////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	} 
	
	
	public String toString(){
		return this.name;
	}
}
