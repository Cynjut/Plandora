package com.pandora.gui.gantt;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class TimeLine extends Panel {

	private static final long serialVersionUID = 1L;

 	public static final int TIME_LINE_HEIGHT = 30;

	public static final String SLOTS            = "SLOTS";
	public static final String INITIAL_DATE     = "INITIALDATE";
	public static final String TIME_UNIT        = "TIMEUNIT";
	public static final String SLOT_SIZE        = "SLOTSIZE";
	public static final String PARENT_SLOT_SIZE = "PARENTSLOTSIZE";
		
	public static final String SLOT_DATE_FORMAT   = "SLOTDATEFORMAT";			
	public static final String PARENT_DATE_FORMAT = "PARENTDATEFORMAT";		
 	
	private int numSlots;
	private int slotWidth;	
	private TimeGantt startDate;
	private Vector noWorkSlots;
	private String slotDateFormat;
	private String parentDateFormat;
	private String language = "pt";
	private String country  = "BR";	

	
	/**
	 * Constructor
	 * @param slots
	 * @param iniDate
	 * @param sWidth
	 * @param timeUnit
	 * @param slotSize
	 * @param parentSize
	 * @param lang
	 * @param cntry
	 */
	public TimeLine (String slots, String iniDate, int sWidth, String timeUnit, String slotSize, String parentSize, String lang, String cntry){
	    this.slotWidth = sWidth;
	    
		//get the number of units (slots) of gantt
	    int numSlots = Util.getInt(slots);
	    if (numSlots<1) {
			System.out.println("ERR: invalid value for SLOTS"); //debug
			numSlots = 30;
		}
	    this.numSlots = numSlots;
	    
		//get the start date for gantt
	    if (iniDate==null || iniDate.trim().equals("")) {
			System.out.println("ERR: invalid value for INITIAL DATE"); //debug
			TimeGantt defaultStartDate = new TimeGantt();
			iniDate = defaultStartDate.toString();
		}

		//get time settings
	    Long tUnitObj = null;
	    if (timeUnit!=null) tUnitObj = new Long(timeUnit);
				
	    Long sSizeObj = null;
	    if (slotSize!=null) sSizeObj = new Long(slotSize);
	    
	    Long pSizeObj = null;
	    if (parentSize!=null) pSizeObj = new Long(parentSize);
	    
  		//set Locale settings, if necessary
  		if (lang!=null)	this.language = lang;
  		if (cntry!=null) this.country = cntry;   

		//create a new TimeGantt object with gantt initial date value
	    DateFormat rf = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(this.language, this.country));
		this.startDate = new TimeGantt(iniDate, rf, tUnitObj, sSizeObj, pSizeObj);
  		
	}


  	public void paint(Graphics g) {
  		int cont=0;
    	String cellStr = "";
    	TimeGantt cursor = null;
    	FontMetrics fm = getFontMetrics(g.getFont());
    	
  		int w = this.getWidth();
  		int h = this.getHeight();
  		int step = (int)(this.getParentSize() / this.getSlotSize());
  		
  		cursor = new TimeGantt(this.startDate);
	    DateFormat sFormat = this.setFormat(this.slotDateFormat);
	    DateFormat pFormat = this.setFormat(this.parentDateFormat);
		
		g.setColor(Color.darkGray);
		
  		for (int i=1; i <= this.numSlots; i++) {
    		cont++;
    		    		
    		//draw the slot vertical lines
	  		int xPoint = (i-1) * this.slotWidth;		
    		g.drawLine(xPoint, h/2, xPoint, h);

    		//if format of slot is weekday and current slot is the first, adjust 'cont' 
    		if (i==1 && this.slotDateFormat.equals("E")) {
		       Calendar c = Calendar.getInstance();
		       c.setTimeInMillis(this.startDate.getTimeInMillis());      
		       cont = c.get(Calendar.DAY_OF_WEEK);
    		}

    		//format and draw the slot label
	   		cellStr = cursor.toString(sFormat);
	   		if (cellStr.length()==3) cellStr = cellStr.substring(0, 1);    		
    		g.drawString(cellStr, xPoint + 4 , h-3);
    		
    		if (cont==step) {
    		    
	    		//format and draw the slot label
				cursor.addSlot(-(step-1));
	    		cellStr = cursor.toString(pFormat);
				cursor.addSlot(+(step-1));
	    		int xpos = ((this.slotWidth * step)/2) - (fm.stringWidth(cellStr)/2);
	    		g.drawString(cellStr, ((i-step) * this.slotWidth) + xpos , (h/2)-3);
    			
    			g.drawLine(i * this.slotWidth, 0, i * this.slotWidth, h/2);
    			cont=0;
    		}

    		cursor.addSlot(1);
  		}
  		
  		//draw border lines
    	g.drawLine(1, h/2, w, h/2);
    	g.drawRect(0, 0, w-1, h-1);    	
  	}
  	
  	/**
  	 * Create a DateFormat object based on date time mask of applet param. 
  	 * @param dform
  	 * @return
  	 */
	private DateFormat setFormat(String dform){
		DateFormat df = null;
		if (dform!=null){
		    df = new SimpleDateFormat(dform, new Locale(this.language, this.country));
		}		
		return(df);
	}
	
	
	///////////////////////////////////////// 	
	public int getSlotsWidth(){
		return(this.slotWidth);
	}
	public void setSlotsWidth(int newValue){
		this.slotWidth = newValue;
	}
	
	///////////////////////////////////////// 	
	public int getNumSlots(){
		return(this.numSlots);
	}
  	

	///////////////////////////////////////// 
	public long getSlotSize(){
		return(this.startDate.getSlotSize());
	}


	///////////////////////////////////////// 
	public long getParentSize(){
		return(this.startDate.getParentSlotSize());
	}


	///////////////////////////////////////// 
	public TimeGantt getStartDate(){
		return(this.startDate);
	}


	///////////////////////////////////////// 
	public void setNoWorkSlots(Vector newValue){
		this.noWorkSlots = newValue;
	}	
	public Vector getNoWorkSlots(){
		return(this.noWorkSlots);
	}


	///////////////////////////////////////// 
	public void setSlotDateFormat(String newValue){
	    if (newValue!=null)
	        this.slotDateFormat = newValue;
	    else 
	        this.slotDateFormat = "E"; //default value
	}
	public String getSlotDateFormat(){
		return(this.slotDateFormat);
	}


	///////////////////////////////////////// 
	public void setParentDateFormat(String newValue){
		if (newValue!=null) 
			this.parentDateFormat = newValue;		
		else
			this.parentDateFormat = "dd/MMMM/yyyy";//default value
	}		
	public String getParentDateFormat(){
		return(this.parentDateFormat);
	}


	///////////////////////////////////////// 
	public String getLanguage(){
		return(this.language);
	}


	///////////////////////////////////////// 
	public String getCountry(){
		return(this.country);
	}

}
