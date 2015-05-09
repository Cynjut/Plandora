package com.pandora.gui.gantt;

import java.awt.*;

public class AllocUnit {

    /** String used by PARAM applet tag */
	public static final String A_UNIT_NUM = "AUNITNUM";
	
	/** String used by PARAM applet tag */
	public static final String A_UNIT     = "AUNIT_";
	
	/** Alloc Unit type used to display a shape (label) inside slot */
	public static final int TYPE_HIDDEN = 0;

	/** Alloc Unit type used to display a shape (label) inside slot */
	public static final int TYPE_SOLID = 1;
	
	
	/** The id of job related */
	private String jobId;
	
	/** The value of current slot (allocUnit) */
  	private float value;
  	
  	/** The numer of current slot (from 1 to TimeLine.numSlots) */
  	private int slot;
  	
  	/** The AWT object used to draw some additional mark for the current slot */
  	private AllocUnitPanel shape;

  	
  	/**
  	 * Constructor. <br>
  	 * Initialize attributes of the new AllocUnit from applet PARAM
  	 * @param jobId
  	 * @param val
  	 * @param s
  	 */
  	public AllocUnit(String jobId, float val, int s, int type) {
    	this.jobId = jobId;
    	this.value = val;
    	this.slot = s;
   	
    	//label settings
    	this.shape = new AllocUnitPanel();
    	this.shape.setDecorateType(type);
	}

  	/**
  	 * The paint method called by Job object in 
  	 * order to paint the alloc unit visual mark.
  	 * @param top
  	 * @param jobLeft
  	 * @param widthSlot
  	 * @param rowHeight
  	 * @param idx
  	 */
  	public void paint(int top, int jobLeft, int widthSlot, int rowHeight, int idx) { 	    
  	    this.shape.setSize(widthSlot *2, rowHeight);
  	    this.shape.setLocation(jobLeft + (idx * widthSlot), top);
    	if (this.shape.getDecorateType()==TYPE_HIDDEN){
    	    this.shape.setVisible(false);    
    	} else {
    	    this.shape.setVisible(true);
    	}
  	}

  	
  	public void setContainer(Panel container){
  	    container.add(this.shape);
  	}

  	

	///////////////////////////////////////// 
	public void setJobId(String newValue){
		this.jobId = newValue;
	}
	public String getJobId(){
		return(this.jobId);
	}


	///////////////////////////////////////// 
	public void setValue(float newValue){
		this.value = newValue;
	}
	public float getValue(){
		return(this.value);
	}

	
	///////////////////////////////////////// 
	public void setSlot(int newValue){
		this.slot = newValue;
	}
	public int getSlot(){
		return(this.slot);
	}

}
