package com.pandora.gui.gantt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;

public class Resource implements ActionListener,  MouseListener {
	
    /** String used by PARAM applet tag */
	public static final String RES_NUM = "RESNUM";
	
	/** String used by PARAM applet tag */
	public static final String RES     = "RES_";

	private static final int MAX_FIRST_SLOT = 100000; //magic initial number for first slot
	    
	/** The id of current resource (from applet param) */
	private String id;
	
	/** Name of resource (from applet param) */
  	private String name;
  	
  	/** Description of resource (from applet param) */
  	private String description;
  	
  	/** Total number of layer of resource (from applet param) */  	
  	private int numLayer;
  	
  	/** the if of parent resource related (from applet param) */  	
  	private String parentResourceId;

  	/** The parent Resource object related, generated from parenteResourceId attribute */
  	private Resource parentResource;

  	/** List of jobs indexed by job id */
  	private Hashtable htJobs;
  	
  	/** A AWT visual object used to show the label of current resource */
  	private JLabel label;
  	
  	/** The current color of resource label */
  	private Color background;
  	
  	/** For resources that don't have any jobs, a joinner object is used to display a horizontal line through all child resources */
  	private JoinnerJob visualJoinner;
  	
  	/** Pointer to ResourceManager object that control the current resource */
	private ResourceManager parentManager;
	
	/** The slot of job localized on the most left position of gantt for the current resource */
	private int firstSlot;

	/** The slot of job localized on the most right position of gantt for the current resource */	
	private int lastSlot;

	/** The id of layer that is visible in gantt chart. If null, all layers are visible. */
	private String visibleLayerId;
	
	
	/**
	 * Constructor. <br> 
	 * Initialize attributes of the new Resource object, 
	 * parsing information from applet PARAM
	 * @param s
	 */
  	public Resource(String s, ResourceManager container) {
  	    
  	    this.parentManager = container;
		StringTokenizer stList = new StringTokenizer(s, "|");

    	this.id = stList.nextToken();
    	this.name = stList.nextToken();
    	this.description = stList.nextToken();
    	this.parentResourceId = stList.nextToken();    	
    	String strNumLayers = stList.nextToken();
    	this.numLayer = Util.getInt(strNumLayers);
    	if (numLayer<1) {
    		System.out.println("ERR: Invalid Layer Number for Resource: " + id); //debug
    		this.numLayer=1;
    	}
    	
    	this.htJobs = null;
    	this.firstSlot = MAX_FIRST_SLOT;
    	this.lastSlot = 1;
		
		//label settings
    	this.label = new JLabel("");
    	this.label.setHorizontalAlignment(JLabel.LEFT);
    	this.label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
    	this.label.setForeground(Color.black);
    	this.label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    	this.label.setOpaque(true);
    	this.label.addMouseListener(this);
    	
    	this.visibleLayerId = null;
	}

 
	/**
	 * Add a Job object into current Resource
	 * @param j
	 */
	public void addJob(Job j){
		if (htJobs==null) htJobs = new Hashtable();
		htJobs.put(j.getId(), j);
	}


	/**
	 * Return a specific job object from hash of current Resource.
	 * @param jobId
	 * @return
	 */
	public Job getJob(String jobId){
		Job response = null;
		try {
			if (htJobs!=null) {
				response = (Job)this.htJobs.get(jobId);
			} 
		} catch(Exception e) {
			response = null;
		}
		return response;
	}


	/**
	 * add an AllocUnit object into a specific Job  
	 * @param jobId
	 * @param au
	 */
	public void addAllocUnit(String jobId, AllocUnit au){
		Job j = (Job)htJobs.get(jobId);
		j.addAllocUnit(au);
		
		//calc first and last slots of resource domain
		this.calcSlotDomain(au.getSlot());	
	}
	
	
	/**
	 * Calculate slot domain of resource and call parent resource calculation method recursivilly 
	 * @param slot
	 */
	private void calcSlotDomain(int slot){
  	    if (this.getParentResource()!=null){
  	        this.getParentResource().calcSlotDomain(slot);
  	    }
		if (this.firstSlot>slot) this.firstSlot = slot;
		if (this.lastSlot<slot) this.lastSlot = slot;
	}

	/*
	private void clearSlotDomain(){
  	    if (this.getParentResource()!=null){
  	        this.getParentResource().clearSlotDomain();
  	    }
    	this.firstSlot = MAX_FIRST_SLOT;
    	this.lastSlot = 1;  	    
	}
	*/

	/**
	 * Change the decoration of Resource to enphasize a selection event.
	 * @param status
	 */
	public void setSelectionDecorate(boolean status){
		if (status) {
			label.setFont(new Font("Arial", Font.BOLD, 11));
		} else {
			label.setFont(new Font("Arial", Font.PLAIN, 11));	
		}	        
	}


	/**
	 * Return a number of visible layers for a current Resource.
	 * @return
	 */
	public int getVisibleLayers(){
		int jobQty = 1;
		HashSet hs = new HashSet();
	
		//verify the layer of each job, in order to find out the max visible layers
    	if (htJobs!=null) {
	      	Iterator ij = this.htJobs.values().iterator();
		   	while(ij.hasNext()) {
		   	    Job j = (Job)ij.next();
		   	    Layer layer = j.getLayer();
		   	    if (visibleLayerId==null || layer.getId().equals(visibleLayerId)) {
			        int jobLayer = layer.getLayerNumber();
			        hs.add(new Integer(jobLayer));		   	    	
		   	    }
	    	}
		   	jobQty = hs.size();
	    }
	  	return (jobQty);
	}

	
	/**
	 * Paint the current resource object on applet. 
	 */
  	public void paint(int x, int y, int width, int height, int widthSlot, int rowHeight) {
  	    int order = 0;
		label.setLocation(x, y);
		label.setSize(width, height);
      	label.setBackground(this.background);		
		label.setText(this.getIdentation() + this.name);
		label.setFont(new Font("Arial", Font.PLAIN, 11));
		label.setVisible(true);
		
		//repaint jobs
    	if (htJobs!=null) {
	      	Iterator ij = this.htJobs.values().iterator();
		   	while(ij.hasNext()) {
		        Job j = (Job)ij.next();
		        Layer layer = j.getLayer();
		        if (this.visibleLayerId==null || this.visibleLayerId.equals(layer.getId())) {
		        	j.getJLabel().setVisible(true);
		        	j.paint(y, widthSlot, rowHeight, order);
			   	    order++;
		        } else {
		        	j.getJLabel().setVisible(false);
		        }
		        
	        	j.paintDependence(this.visibleLayerId==null);
	    	}
	    } else {
	        this.calcJoinnerPosition(widthSlot, y + (rowHeight/2), (rowHeight/2));
	    }    	
  	}

  	
  	/**
  	 * This method is called by a inner Job when some movement ocurrs.
  	 */
  	public void jobRepaintCallBack(Vector jobAllocUnits){ 	    
  	    if (this.parentResource!=null && jobAllocUnits!=null) {
  	        /*
  	         TODO: resolver como fazer para a joinner acompanhar as movimentacoes dos jobs..
  	        this.clearSlotDomain();
  	        Iterator i = jobAllocUnits.iterator();
  	        while(i.hasNext()) {
  	            AllocUnit au = (AllocUnit)i.next();
  	            this.calcSlotDomain(au.getSlot());
  	        }
  	        this.parentResource.repaintJoinner();
  	        */
  	    }
  	}
  	
  	
  	public void repaintJoinner(){
  	    int widthSlot = this.parentManager.getSlotWidth(); 	    
        int y = this.visualJoinner.getY();
        int rowHeight = this.visualJoinner.getHeight();
        this.calcJoinnerPosition(widthSlot, y, rowHeight);
  	}
  	
  	
  	private void calcJoinnerPosition(int widthSlot, int y, int rowHeight){
        int xj = (this.firstSlot-1) * widthSlot;
        int wj = (this.lastSlot - this.firstSlot + 1) * widthSlot;
        this.visualJoinner.setBounds(xj, y , wj, rowHeight);
        this.visualJoinner.setVisible(true);
  	}
  	
  	/**
  	 * Return the identation of parent resource more spaces.
     * @return
     */
    public String getIdentation() {
        String response = " ";
  	    if (this.getParentResource()!=null){
  	        response = this.getParentResource().getIdentation();
  	        response = response.concat("   ");
  	    }
        return response;
    }

    
    /**
     * Verify if current Resource contain at least one job in it. 
     * @return
     */
    public boolean hasJobs(){
        return (this.htJobs != null);
    }

    
    /**
     * Return a list of Job objects that was changed. 
     * @return
     */
    public Vector getChangedJobs(){
        Vector response = new Vector();
        if (this.htJobs!=null) {
          	Iterator i = this.htJobs.values().iterator();
    	   	while(i.hasNext()) {
    	        Job j = (Job)i.next();
    	        if (j.wasChanged()){
    	            response.addElement(j);
    	        }
        	}          
        }
	   	return response;
    }

    
    /**
     * Clear the information of 'changed objects' of jobs
     */
    public void clearChangedJobs() {
        if (this.htJobs!=null) {
          	Iterator i = this.htJobs.values().iterator();
    	   	while(i.hasNext()) {
    	        Job j = (Job)i.next();
    	        j.setWasChanged(false);
        	}
        }
    }
        
    /**
  	 * This event is capture by system to process a menu item popup 
  	 * of Resource selected by user. 
  	 */
    public void actionPerformed(ActionEvent ev) {
        this.parentManager.setSelectedResource(this);
        
        MenuItemGantt mig = (MenuItemGantt)ev.getSource();
        if (mig.getTarget().equals(MenuItemGantt.INSERT_RES_TARGET)){
            String newId = "<new_ID_" + this.parentManager.getResourceCount() + ">";
            String tokens = newId + "|New Name|New Description|1";
            this.parentManager.addResource(tokens);
            
        } else if (mig.getTarget().equals(MenuItemGantt.UP_RES_TARGET)){
            this.parentManager.changeResourcePosition(this, true);
            
        } else if (mig.getTarget().equals(MenuItemGantt.DOWN_RES_TARGET)){
            this.parentManager.changeResourcePosition(this, false);
        }
    }


    /** Not implemented */    
    public void mouseClicked(MouseEvent ev) {}

    /** Not implemented */
    public void mouseEntered(MouseEvent ev) {}

    /** Not implemented */
    public void mouseExited(MouseEvent ev) {}
    
    /** Not implemented */
    public void mousePressed(MouseEvent ev) {}

    /**
     * This event is captured by system to show the menu popup over the resource label. 
     */
    public void mouseReleased(MouseEvent ev) {
        this.parentManager.setSelectedResource(this);
        
        if (this.parentManager.isEditable()) {
			if (ev.getModifiers()<=MouseEvent.BUTTON2_MASK) {		    
		    	this.parentManager.showMenuGantt(MenuGantt.RESOURCE_AREA, this.label, this, ev.getX(), ev.getY());
			}
        }
    }
  	

	///////////////////////////////////////// 
	public void setId(String newValue){
		id = newValue;
	}
	public String getId(){
		return(id);
	}


	///////////////////////////////////////// 
	public void setName(String newValue){
		name = newValue;
	}
	public String getName(){
		return(name);
	}


	///////////////////////////////////////// 
	public void setDescription(String newValue){
		description = newValue;
	}
	public String getDescription(){
		return(description);
	}


	///////////////////////////////////////// 
	public void setNumLayer(int newValue){
		numLayer = newValue;
	}
	public int getNumLayer(){
		return(numLayer);
	}
	

	///////////////////////////////////////// 
	public JLabel getJLabel(){
		return(label);
	}
	
	
	///////////////////////////////////////// 
	public Color getBackground() {
		return background;
	}
	public void setBackground(Color newValue) {
		this.background = newValue;
	}

	
	///////////////////////////////////////// 
    public Resource getParentResource() {
        return parentResource;
    }
    public void setParentResource(Resource newValue) {
        this.parentResource = newValue;
    }
    
    
	/////////////////////////////////////////     
    public String getParentResourceId() {
        return parentResourceId;
    }
    public void setParentResourceId(String newValue) {
        this.parentResourceId = newValue;
    }
    
    
	/////////////////////////////////////////    
    public JoinnerJob getVisualJoinner() {
        return visualJoinner;
    }
    public void setVisualJoinner(JoinnerJob newValue) {
        this.visualJoinner = newValue;
    }


	/////////////////////////////////////////       
	public String getVisibleLayerId() {
		return visibleLayerId;
	}
	public void setVisibleLayerId(String newValue) {
		this.visibleLayerId = newValue;
	}

    
}
