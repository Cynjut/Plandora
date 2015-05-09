package com.pandora.gui.gantt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import java.lang.Math;

import com.pandora.helper.StringUtil;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.StringTokenizer;


public class Job implements MouseListener, MouseMotionListener, ActionListener{
	
	/** String used by PARAM applet tag */
	public static final String JOB_NUM = "JOBNUM";
	
	/** String used by PARAM applet tag */
	public static final String JOB     = "JOB_";

	/** The unique identification of Job intot Gantt */
	private String id;
	
	/** The pointer of resource related with current Job*/
	private String resourceId;
	
	/** The name of current Job used into visual label */
  	private String name;
  	
  	/** The description of current Job used into toolTip text*/
  	private String description;
  	
  	/** The id of layer where Job is placed */
	private String layerId;  	
	
	/** The layer object related (where Job is placed)*/
  	private Layer layer;
  	
  	/** Is 'true' when occurs a current job behaviour changing (default value=false)*/
  	private boolean wasChanged;
  	
  	/** Is 'true' if the current Job is locked for moving and resizing */
  	private boolean isLocked;

  	
  	/** The pointer of parent job (which current job is dependend on)*/
  	private Job previousJob;
  	
  	/** The list of Job 'childs' (which depend on current job)*/
  	private Vector childJobs;
  	  	
  	private DependenceArrow depArrow;
  	private Vector vAllocUnits;
	private JobLabel label;
	private Gantt parentGantt;
	private Panel container;
	
	private int lastRedimX, lastReleasedX;
	private int currentSlotMouseOver;
	private int lastSlotSelected;
	
	/**
	 * Constructor. <br>
	 * Initialize attributes of the new Job, parsing data from applet PARAM
	 * @param s
	 * @param pg
	 */
  	public Job(String s, Gantt pg) {
		StringTokenizer stList = new StringTokenizer(s, "|");
		
		this.wasChanged = false;
    	this.id = stList.nextToken();
    	this.resourceId = stList.nextToken();
    	this.name = stList.nextToken();
    	this.description = stList.nextToken();
    	this.layerId = stList.nextToken();
    	String strType = stList.nextToken();
    	if (strType==null || strType.trim().equals("")){
    	    strType = "0";
    	}
    	int type = Integer.parseInt(strType);

		this.depArrow = new DependenceArrow();
    	
    	//label settings
    	this.label = new JobLabel();		
      	this.label.setBorder(BorderFactory.createLineBorder(Color.black));
		this.label.setHorizontalAlignment(JLabel.CENTER);
		this.label.setOpaque(true);
		this.label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      	this.label.setForeground(Color.black);
      	this.label.setBackground(Color.lightGray);			
		this.label.setVisible(false);
		this.label.setDecorativeType(type);
    	this.isLocked = (type==JobLabel.TYPE_DASH_BLOCK);
    	
	    this.label.addMouseMotionListener(this);
    	this.label.addMouseListener(this);
	    
	    //set source Gantt into Job
	    this.parentGantt = pg;	    
	}

  	/**
  	 * Add into current job a new AllocUnit
  	 * @param au
  	 */
	public void addAllocUnit(AllocUnit au){
		if (vAllocUnits==null) vAllocUnits = new Vector();
		vAllocUnits.addElement(au);
		au.setContainer(this.container);
		
		this.label.addVisibleSlots(true);
	}

	
	/**
	 * Set the hoster panel related with current job
	 * @param container
	 */
  	public void setContainer(Panel c){
  	    c.add(this.label);
  	    c.add(this.depArrow);
  	    this.container = c;
  	}	
  	

	/**
	 * Paint the current Job on gantt.
	 */
  	public void paint(int top, int widthSlot, int rowHeight, int order) {
  		int gap = rowHeight/4;
		if (vAllocUnits==null) {
			//debug
			System.out.println("ERR: Job without AllocUnit. Job: " + this.id);
			this.label.setVisible(false);
		} else {
			//set width and height size of current job
		    int h = rowHeight - (rowHeight/4);
			this.label.setSize(widthSlot * vAllocUnits.size(), h);
			this.label.setSlotWidth(widthSlot);

  			//set x,y position of current job
  			AllocUnit firstSlot = this.getJobAlloc(0);
  			int x = widthSlot * (firstSlot.getSlot()-1);
  			//TODO o calculo da variavel y nao esta levando em conta a ordem do atributo layerNum do objeto layer
  			int y = top + (rowHeight * order) + gap;
			this.label.setLocation(x, y);

			//refresh label's job
			this.label.setFont(new Font("Arial", Font.BOLD, 11));
			this.label.setText(" " + this.name);		
			this.label.setVisible(true);  		
			
			//re-paint tha allocUnits
			this.label.initVisibleSlots();
			for (int i=0; i<vAllocUnits.size(); i++){
			    AllocUnit au = this.getJobAlloc(i);
			    au.paint(y, x, widthSlot, h, i);
			
			    this.label.addVisibleSlots((au.getValue()>0));
			}
			
			//paint the dependence lines if necessary
			this.paintDependence(true);
		}
		
		//update color from related layer object
      	if (this.layer!=null){
      		this.label.setBackground(this.layer.getColor());
      	}				  			
  	}
 	
  	/** Not implemented */
	public void mouseEntered(MouseEvent me) {}
	
	/** Not implemented */
	public void mouseClicked(MouseEvent me) {}

	public void mouseExited(MouseEvent me) {
		this.currentSlotMouseOver = -1; //reset
		this.hideToolTip();
	}

	public void mouseReleased(MouseEvent me) {
  		int slot = 0;

		if (this.parentGantt.isEditable()) {
		    
			if (me.getModifiers()<=MouseEvent.BUTTON2_MASK) {
				this.parentGantt.setToolTipText(0,0, null, null);
		    	this.parentGantt.showMenuGantt(MenuGantt.JOB_AREA, this.label, this, me.getX(), me.getY());
		      
		    } else if (me.getModifiers()<=MouseEvent.BUTTON1_MASK && me.getClickCount()==1) {
		        
				//verify if a job label is out of gantt bounds
				this.checkJobLimits();
	
				//get first slot selected and update Label position (into slot place)
	    	  	double slotBuff = (double)(this.label.getX() * this.parentGantt.getNumSlots()) /
	        	    (this.parentGantt.getSlotsWidth() * this.parentGantt.getNumSlots());
		      	slot = (int)(slotBuff +.5);
				
				//paint job (if possible) on new position
	      		if (this.canChangeJob(slot, slot + this.vAllocUnits.size()-1)) {
	        		this.label.setLocation(slot * this.parentGantt.getSlotsWidth(), this.label.getY());
			        this.lastReleasedX = this.label.getX();
			        this.updateAllocsAttributes(slot+1);
				} else {
	        		this.label.setLocation(this.lastReleasedX, label.getY());
				}
		      	this.label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		      	this.parentGantt.setVisibleSelectionMark(true);
			}		    
    	}

	    this.lastSlotSelected = this.getSelectedSlot(me.getX());
	    
		//paint the dependence lines if necessary
		this.paintDependence(true); 
		
        this.callBackToResource();
	}
	

	public void mousePressed(MouseEvent me) {
	    this.lastRedimX = me.getX();
		this.lastReleasedX = this.label.getX();
	    if (!this.isLocked()){			    
		    this.parentGantt.setSelectedJob(this);
		    this.label.grabFocus();
        }		    
	}
	
	
	/**
	 * Event performed when the mouse is dragged over the job visual square.
	 */
	public void mouseDragged(MouseEvent me) {
    	if (this.parentGantt.isEditable() && !this.isLocked()) {
    		
    		//hide dependence arrows
    		this.paintDependence(false);
    	    
    	    this.parentGantt.setToolTipText(0,0, null, null);
      		this.parentGantt.setVisibleSelectionMark(false);
      		this.label.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

			//get current position of job and calculate a new position...
      		this.label.setBounds(label.getX()  + me.getX() - this.lastRedimX,
                           label.getY(), label.getWidth(), label.getHeight());
      		this.setWasChanged(true);  
    	}
  	}

	/**
	 * Event fired when a mouse is over a job. In this case is necessary to
	 * display the tool tip text.
	 */
	public void mouseMoved(MouseEvent me) {
		this.displayToolTip(me.getX(), me.getY());
	}
	
	
	/**
	 * Capture the event of Menu Item clicked over a Job label
	 */
	public void actionPerformed(ActionEvent me){
	    MenuItemGantt mig = (MenuItemGantt)me.getSource();
	    String target = mig.getTarget();
	    if (this.parentGantt.isEditable() && !this.isLocked()) {
		    if (target.equals(MenuItemGantt.ALLOC_EDIT_TARGET)) {
		        this.editJobAllocation();
		    }	        
	    } else {
	        JOptionPane.showMessageDialog(this.parentGantt, this.parentGantt.getMessage("gantt.label.error.locked"), "", JOptionPane.ERROR_MESSAGE);
	    }
	}

	
	/**
	 * Open the Input box and ask user the new allocation value 
	 */
	private void editJobAllocation(){
	    Locale loc = parentGantt.getLocale();
	    AllocUnit au = (AllocUnit)this.vAllocUnits.get(this.lastSlotSelected-1);
	    String currInfoSlot = getCurrentInfoSlot(this.lastSlotSelected);
	    
		Object source = JOptionPane.showInputDialog(this.parentGantt, 
		        this.parentGantt.getMessage("gantt.label.newAllocValue") + currInfoSlot,
		        this.parentGantt.getMessage("gantt.label.editAlloc"),
		        JOptionPane.QUESTION_MESSAGE, null, null, StringUtil.getFloatToString(au.getValue(), loc));
				
		if (source!=null) {
		    String s = (String)source;
		    if (s.trim().length()>0) {
				if (StringUtil.checkIsFloat(s, loc)) {
				    float f = StringUtil.getStringToFloat(s, loc);
				    if (f>=0) {
					    au.setValue(f);
					    this.setWasChanged(true);		        
				    } else {
					    JOptionPane.showMessageDialog(this.parentGantt, this.parentGantt.getMessage("gantt.label.error.invalidNumber"));		        
				    }
				} else {
				    JOptionPane.showMessageDialog(this.parentGantt, this.parentGantt.getMessage("gantt.label.error.invalidNumber"));
				}		    		        
		    }
		}
	}
	
	/**
	 * Verify if current slot has changed (to avoid undesired refresh of toot tip box)
	 * and display a tool tip with fresh information.
	 */
	private void displayToolTip(int mouseXPos, int mouseYPos){
      	int slot = this.getSelectedSlot(mouseXPos);
      	if (slot!=this.currentSlotMouseOver){
      		this.currentSlotMouseOver = slot;

			ToolTipText tt = new ToolTipText();
			
			//get information from resource related
			Resource res = this.parentGantt.getResource(this.getResourceId());
			if (res.getDescription()!=null && !res.getDescription().trim().equals("")) {
				tt.addContent(res.getDescription());				
			}
			
			//get information from layer related
			tt.addContent(this.getDescription() + " - " + this.layer.getDescription());
			
			//get information from alloc unit related
  			String start = getCurrentInfoSlot(slot);
  			AllocUnit currAloc = this.getJobAlloc(slot-1);
			tt.addContent(start + " - " + currAloc.getValue() + "h");
			
			parentGantt.setToolTipText(mouseXPos, mouseYPos, this.label, tt);		
      	}
	}
	
	
	/**
	 * Return the current slot based on mouse cursor
	 * @param mouseXPos
	 * @return
	 */
	private int getSelectedSlot(int mouseXPos){
	    return (int)((double)(mouseXPos / this.parentGantt.getSlotsWidth()) +1);
	}
	
	
	/**
	 * Return the information of current slot in date format
	 * @param slot
	 * @return
	 */
	private String getCurrentInfoSlot(int slot){
		AllocUnit firstSlot = this.getJobAlloc(0);
		TimeGantt start = new TimeGantt(this.parentGantt.getStartDate());
		start.addSlot(firstSlot.getSlot()+slot-2);
		return start.toString();	    
	}
	
	/**
	 * Hide the Tool Tip box
	 */
	private void hideToolTip(){
		parentGantt.setToolTipText(0, 0, null, null);		
	}
		
	
	/**
	 * Verify if a job label is out of gantt bounds
	 */
	public void checkJobLimits(){
	 	if (this.label.getX()<0) {
	 	   this.label.setLocation(0, label.getY());
	 	}
	  	if (this.label.getX() + this.label.getWidth() > this.parentGantt.getGanttWidth()) {
	    	this.label.setLocation(this.parentGantt.getGanttWidth() -
                    this.label.getWidth(), this.label.getY());
	  	}
	}

	
	/**
	 * Verify if current Job can be moved or redimensioned.
	 */  	
	public boolean canChangeJob(int slot, int slotNumber){
		return !this.isLocked();
	}


	/**
	 * Draw the dependence arrow, if necessary
	 */
	public void paintDependence(boolean isVisible){	
		
		this.depArrow.setVisible(isVisible);
		
		if (this.previousJob!=null && isVisible && this.previousJob.getJLabel().isVisible()
				&& this.label.isVisible()){
				
			boolean leftToRight = true;
			int x = this.previousJob.getJLabel().getX();
			if (this.label.getX() < x) {
				x = this.label.getX();
				leftToRight = false;
			}

			int w = this.label.getX() - this.previousJob.getJLabel().getX();
			w = Math.abs(w);
			
			
			boolean uptoDown = true;
			int y = this.previousJob.getJLabel().getY();
			int h = this.label.getY() - y;
			if (this.label.getY() < y) {
				uptoDown = false;
				y = this.label.getY();
				h = this.previousJob.getJLabel().getY() - y;
			}			
			this.depArrow.setLocation(x-10,y);
			this.depArrow.setSize(w+20, h+10);
			this.depArrow.setLeftToRight(leftToRight);
			this.depArrow.setUpToDown(uptoDown);		

		} else {
			this.depArrow.setVisible(false);
		}
		
		//verify if current job has childs and request repaint
		paintDependenceInChild(isVisible);
		
	}
	
	
	/**
	 * Call back to resource in order to inform that some moment was done...
	 */
	public void callBackToResource(){
		Resource parentRes = parentGantt.getResource(this.resourceId);
		parentRes.jobRepaintCallBack(vAllocUnits);			    
	}

	
	/**
	 * Verify if current job has childs and request repaint in each one
	 */
	private void paintDependenceInChild(boolean isVisible){
		if (this.childJobs!=null){
			Iterator i = this.childJobs.iterator();
			while (i.hasNext()){
				Job child = (Job)i.next();
				child.paintDependence(isVisible);
			}
		}	    
	}
	
	
	/**
	 * Add a new child Job into list of dependence
	 */
	private void addChildDependence(Job child){
		if (this.childJobs==null) this.childJobs = new Vector();
		this.childJobs.addElement(child);
	}
	

	public void setNewWidth(int newWidth){
      	this.label.setBounds(this.label.getX(), this.label.getY(), newWidth,
      						 this.label.getHeight());
      	this.setWasChanged(true);
	}

	
	/**
	 * Create a new AllocUnit into current Job
	 */	
	public void appendAlloc(){
		AllocUnit oAlloc = null, oLastAlloc = null;
		
		//get last Allocation
		oLastAlloc = this.getJobAlloc(this.vAllocUnits.size()-1);
		
		//append a new allocation 
		oAlloc = new AllocUnit(this.id, this.layer.getMaxCapacity(), 
		        oLastAlloc.getSlot()+1, AllocUnit.TYPE_HIDDEN);
		this.addAllocUnit(oAlloc);		
  	}
	
	
	/**
	 * Removes the last allocUnit of current Job
	 */
	public void removeLastAlloc() {
    	AllocUnit alloc = null;

	    if (this.vAllocUnits!=null) {

	    	//hide shape of last alloc
	    	//TO DO
    	  	alloc = this.getJobAlloc(this.vAllocUnits.size()-1);

		  	//remove last element
		  	this.vAllocUnits.remove(alloc);
		
			alloc = null;
		}
	}
	
	
	/**
	 * Corrects the slots reference into allocs of current job, after a "move-job" event.
	 */
	private void updateAllocsAttributes(int newSlot) {
    	AllocUnit alloc = null;
	    if (this.vAllocUnits!=null) {
	    	Iterator i = this.vAllocUnits.iterator();
	    	while(i.hasNext()){
    	  		alloc = (AllocUnit)i.next();
    	  		alloc.setSlot(newSlot++);
	    	}
		}
	}	

	
	/**
	 * Changes the border of current Job to enphasize a selection event.
	 */
	public void setSelectionDecorate(boolean status){
		if (label.isVisible()) {
			if (status) {
			    this.label.setBorder(BorderFactory.createLineBorder(Color.black, 3));
			} else {
			    this.label.setBorder(BorderFactory.createLineBorder(Color.black));
			}
		    this.label.setVisible(true);
		}
	}
	
	
	///////////////////////////////////////// 
	public void setId(String newValue){
		this.id = newValue;
	}
	public String getId(){
		return(this.id);
	}
	

	///////////////////////////////////////// 
	public void setResourceId(String newValue){
		this.resourceId = newValue;
	}
	public String getResourceId(){
		return(this.resourceId);
	}


	///////////////////////////////////////// 
	public void setName(String newValue){
		this.name = newValue;
	}
	public String getName(){
		return(this.name);
	}
	

	///////////////////////////////////////// 
	public void setDescription(String newValue){
		this.description = newValue;
	}
	public String getDescription(){
		return(this.description);
	}

	
	///////////////////////////////////////// 
	public void setLayerId(String newValue){
		this.layerId = newValue;
	}
	public String getLayerId(){
		return(this.layerId);
	}

	
	///////////////////////////////////////// 
	public void setLayer(Layer newValue){
		this.layer = newValue;
	}
	public Layer getLayer(){
		return(this.layer);
	}
  	
	
	///////////////////////////////////////// 
	public JLabel getJLabel(){
		return(label);
	}


	///////////////////////////////////////// 
	public void setPreviousJob(Job newValue){
		this.previousJob = newValue;
		this.previousJob.addChildDependence(this);
	}
	public Job getPreviousJob(){
		return(this.previousJob);
	}

	

	///////////////////////////////////////// 
	public int getJobX() {
		return this.label.getX();
	}


	///////////////////////////////////////// 
	public int getJobY() {
		return this.label.getY();
	}


	///////////////////////////////////////// 
	public int getJobWidth() {
		return this.label.getWidth();
	}


	///////////////////////////////////////// 
	public int getJobHeight() {
		return this.label.getHeight();
	}

	
	///////////////////////////////////////// 
	public int getJobAllocSize() {
		return this.vAllocUnits.size();
	}

	
	///////////////////////////////////////// 
	public AllocUnit getJobAlloc(int index) {
	    AllocUnit response = null;
	    if (this.vAllocUnits!=null && this.vAllocUnits.size()>index){
	        response = (AllocUnit)this.vAllocUnits.elementAt(index);
	    }
		return response;
	}
	
	
	///////////////////////////////////////// 	
    public boolean wasChanged() {
        return wasChanged;
    }
    public void setWasChanged(boolean newValue) {
        this.wasChanged = newValue;
    }
    
    
	/////////////////////////////////////////    
    public boolean isLocked() {
        return isLocked;
    }
}
