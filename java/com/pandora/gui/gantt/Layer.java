package com.pandora.gui.gantt;

import java.awt.Color;
import java.util.StringTokenizer;

/**
 * This class represent a layer of resource (each resource can contain one or many layers)
 */
public class Layer {
	
	/** String used by PARAM applet tag */
	public static final String LAYER_NUM = "LAYERNUM";
	
	/** String used by PARAM applet tag */	
	public static final String LAYER     = "LAYER_";

	/** Default value of layer max capacity */	
	public static final int MAX_CAPACITY = 8;


	/** The id of layer */	
	private String id;
	
	/** The sequence of layer into resource */	
	private int layerNumber;

	/** The name (short description) of layer (must appear into popup menu of job) */
	private String name;

	/** The description of layer (must appear into tooltip of job) */
	private String description;

	/** The Color of layer (must be used for related jobs) */
	private Color color;
	
	/** The max capacity of current layer */
    private float maxCapacity = MAX_CAPACITY;
	
    
	/**
	 * Constructor. <br> 
	 * Initialize attributes of the new Layer object, parsing information from applet PARAM
	 * @param s
	 */
  	public Layer(String s) {
		StringTokenizer stList = new StringTokenizer(s, "|");
    	this.id = stList.nextToken();
		String number = stList.nextToken();
    	this.layerNumber = Util.getInt(number);
    	if (this.layerNumber<1) {
    		System.out.println("ERR: Layer: " + this.id + " cannot be < 1."); //debug
    		this.layerNumber = 1;
    	}
    	this.name = stList.nextToken();
    	this.description = stList.nextToken();
    	String c = stList.nextToken();
    	if (c.length()==6) {
      		this.color = new Color(
                    Integer.parseInt(c.substring(0,2), 16),
                    Integer.parseInt(c.substring(2,4), 16),
                    Integer.parseInt(c.substring(4,6), 16));    	    
    	} else {
    	    this.color = Color.LIGHT_GRAY;
    	}
		String capstr = stList.nextToken(); //get capacity (in minutes)..
    	this.maxCapacity = (Util.getInt(capstr)/60); //..and set into attribute (in hours)
	}

	
	//////////////////////////////////////////
	public void setDescription(String newValue) {
		this.description = newValue;
	}
	public String getDescription() {
		return description;
	}

	//////////////////////////////////////////
	public void setLayerNumber(int newValue) {
		this.layerNumber = newValue;
	}
	public int getLayerNumber() {
		return layerNumber;
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

	//////////////////////////////////////////	
    public Color getColor() {
        return color;
    }
    public void setColor(Color newValue) {
        this.color = newValue;
    }

	//////////////////////////////////////////
    public float getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(float newValue) {
        this.maxCapacity = newValue;
    }
    
    public String toString(){
    	return this.name;
    }
}
