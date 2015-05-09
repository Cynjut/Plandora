package com.pandora.gui.gantt;

import java.awt.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Iterator;

public class ResourceManager extends Panel {

	private static final long serialVersionUID = 1L;

    /** String used by PARAM applet tag */
	public static final String SLOT_WIDTH = "SLOTWIDTH";
	
	/** String used by PARAM applet tag */
	public static final String ROW_HEIGHT = "ROWHEIGHT";

	
	/** The height in pixels of a Gantt row (where the jobs is placed) */
	private int rowHeight;
	
	/** The widht in pixels of each slot of Gantt */
	private int slotWidth;
	
	
	/** Current Width of Resource Manager */
	private int currentWidth = 200;
  
	/** The list of resource objects indexed by resource id */
  	private Hashtable htResources;
  	
  	/** The list of ordered resource objects */
  	private Vector vOrderedResources;
  	
  	/** The color of resource painel background */
  	private Color resourceBGColor;  
  	
  	/** Pointer to Gantt object */
	private Gantt parentGantt;  	

	/** List of DecorativeLines. The number of elements = number of resources */
	private Vector horizontalGanttLines;
	
	/**
	 * Constructor. <br> 
	 */
  	public ResourceManager(Gantt container) {
  		this.rowHeight = 25;
  		this.slotWidth = 15;
  		this.resourceBGColor = Color.WHITE;
  		this.parentGantt = container;
  		this.horizontalGanttLines = new Vector();
  	}
	
	
	public void addResource(String p){
		Resource r = new Resource(p, this);
		if (this.htResources==null) {
			this.htResources = new Hashtable();
			this.vOrderedResources = new Vector();
		}
		this.htResources.put(r.getId(), r);
		this.vOrderedResources.addElement(r);
		this.add(r.getJLabel());
		
		DecorativeLine line = new DecorativeLine();
		this.parentGantt.getJobAreaPanel().add(line);
		this.horizontalGanttLines.addElement(line);
		
		//link current parsed resource with her super-resource (parent)
		if (!r.getParentResourceId().trim().equals("")){
		    Resource parentRes = (Resource)this.htResources.get(r.getParentResourceId());
		    r.setParentResource(parentRes);
		}
	}

	/**
	 * Select all resources that don't have any jobs in it, and set a visual JoinnerJob object. 
	 * @param jobAreaPanel
	 */
	public void checkResourcesWithoutJobs(Panel jobAreaPanel){
	    if (this.vOrderedResources!=null) {
		    Iterator ri = this.vOrderedResources.iterator();
	    	while(ri.hasNext()) {
	    		Resource r = (Resource)ri.next();
	    		if (!r.hasJobs()){
	    		    JoinnerJob jj = new JoinnerJob(); 
	    		    jobAreaPanel.add(jj);
	    		    r.setVisualJoinner(jj);
	    		}
	    	}	        
	    }
	}

	/**
	 * Add an AllocUnit object into a specific Resource 
	 * @param resourceId
	 * @param jobId
	 * @param au
	 */
	public void addAllocUnit(String resourceId, String jobId, AllocUnit au){
		Resource r = (Resource)htResources.get(resourceId);
		r.addAllocUnit(jobId, au);
	}


    /**
     * Return a list of Job objects that was changed into any resource. 
     * @return
     */
    public Vector getChangedJobs(){
        Vector response = new Vector();
      	Iterator i = this.vOrderedResources.iterator();
	   	while(i.hasNext()) {
	        Resource r = (Resource)i.next();
	        response.addAll(r.getChangedJobs());
    	}
	   	return response;
    }	
    
    
    /**
     * Clear the information of 'changed objects' into all resources
     */
    public void clearChangedJobs() {
     	Iterator i = this.vOrderedResources.iterator();
	   	while(i.hasNext()) {
	        Resource r = (Resource)i.next();
	        r.clearChangedJobs();
    	}
    }
    
    
	public void parseDependence(String c){
		StringTokenizer stList = new StringTokenizer(c, "|");

    	String childJob = stList.nextToken();
    	String childRes = stList.nextToken();
    	String parentJob = stList.nextToken();
    	String parentRes = stList.nextToken();
    	
    	Resource childResObj = this.getResource(childRes);
    	if (childResObj!=null) {
    		Job childJobObj = childResObj.getJob(childJob);
    		if (childJobObj!=null) {
    			
		    	Resource parentResObj = this.getResource(parentRes);
	    		if (parentResObj!=null) {
		    		Job parentJobObj = parentResObj.getJob(parentJob);
		    		if (parentJobObj!=null){

		    			//create the dependence!!
		    			childJobObj.setPreviousJob(parentJobObj);
		    			
		    		} else {
	    				System.out.println("Unknown job: " + parentJob); //debug
		    		}
	    		} else {
    				System.out.println("Unknown resource: " + parentRes); //debug
	    		}
	    		
    		} else {
    			System.out.println("Unknown job: " + childJob); //debug
    		}
    	} else {
    		System.out.println("Unknown resource: " + childRes); //debug
    	}
	}

	/**
	 * Return the number of resources into manager.
	 * @return
	 */
	public int getResourceCount(){
	    int response = 0;
	    if (htResources!=null) {
	        response = htResources.size();
	    }
	    return response;
	}
	
	
	/**
	 * Return a resource specific by id.
	 * @param id
	 * @return
	 */
	public Resource getResource(String id){
		Resource response = null;
		try {
			if (htResources!=null) {
				response = (Resource)this.htResources.get(id);
			} 
		} catch(Exception e) {
			response = null;
		}
		return response;
	}
	

  	public void paint(Graphics g) {
  		int top = 0;
	    if (this.vOrderedResources!=null) {
	        for (int ri=0; ri<this.vOrderedResources.size(); ri++) {
	    		Resource r = (Resource)this.vOrderedResources.elementAt(ri);
	    		r.setVisibleLayerId(parentGantt.getVisibleLayerId());
				r.setBackground(this.resourceBGColor);    		
	    		int vl = r.getVisibleLayers();
	    		r.paint(0, top, this.getWidth(), this.rowHeight * vl, this.slotWidth, this.rowHeight);
	    		top += this.rowHeight * vl;
	    		
	  	   		//refresh horizontal line into gantt
	  	   		DecorativeLine line = (DecorativeLine)this.horizontalGanttLines.elementAt(ri);
	  	   		line.setLocation(0, top+1);
	  	   		line.setSize(this.parentGantt.getJobAreaPanel().getWidth(), 2);
	  	   		line.setVisible(true);
	        }
	    }
  	   	setVisible(true);
  	   	setBackground(Color.lightGray);
  	}
  	
	
	public int getResourcesHeight(){
  		int response = 0;
	    if (this.vOrderedResources!=null) {  		
		    Iterator ri = this.vOrderedResources.iterator();
	    	while(ri.hasNext()) {
	    		Resource r = (Resource)ri.next();
	  	   		response += this.rowHeight * r.getVisibleLayers();
	  	   	}
	    }
  	   	return response;
		
	}

	/**
	 * This method is used in order to create a communication between resources and gantt 
	 * @return
	 */
	public boolean isEditable() {
	   return this.parentGantt.isEditable(); 
	}
	
	/**
	 * This method is used in order to create a communication between resources and gantt
	 * @param context
	 * @param source
	 * @param handler
	 * @param x
	 * @param y
	 */
	public void showMenuGantt(String context, Component source, 
			  Object handler, int x, int y){
	    this.parentGantt.showMenuGantt(context, source, handler, x, y);
	}
	
	/**
	 * Ask to Gantt to set the current resource into gantt.
	 * @param newValue
	 */
	public void setSelectedResource(Resource newValue) {
	    this.parentGantt.setSelectedResource(newValue);
	}

	/**
	 * Change visual resource position
	 * @param res
	 * @param isUp
	 */
    public void changeResourcePosition(Resource res, boolean isUp){
        String newOrderIds[] = new String[this.vOrderedResources.size()];
        String lastResourceId = null;
        int order = 0;
        boolean jumpTwo = false;
        
        if (checkExtremeOrderPosition(res, isUp)){
            
        	//hide selection mark of job
        	this.parentGantt.setVisibleSelectionMark(false);        	
            
    	    Iterator ri = this.vOrderedResources.iterator();
        	while(ri.hasNext()) {
        		Resource r = (Resource)ri.next();
        		if (r.equals(res)){
        		    //change position!!!
        		    if (isUp){
        		        newOrderIds[order] = lastResourceId;
        		        newOrderIds[order-1] = r.getId();
        		        order++;
        		    } else {
        		        newOrderIds[order+1] = r.getId();
        		        jumpTwo = true;
        		    }
        		} else{
        		    newOrderIds[order] = r.getId();
        		    if (jumpTwo){
        		        order+=2;
        		        jumpTwo = false;
        		    } else{
        		        order++;    
        		    }
        		}
        		lastResourceId = r.getId();
      	   	}
        	
        	//Re-order resource list. Get element from unorder hashtable and create a new
        	//vector or orderes elements based on ids array. 
        	this.vOrderedResources = new Vector();
        	for (int i=0; i<newOrderIds.length;i++){
        	    Resource r = (Resource)this.htResources.get(newOrderIds[i]);
        	    this.vOrderedResources.addElement(r);
        	}
        	this.repaint();        	
        }
    }
    
    /**
     * Check if current resource is on first or last position already.
     * @param res
     * @param isUp
     * @return
     */
    private boolean checkExtremeOrderPosition(Resource res, boolean isUp){
        boolean isExtremePosition = true;
        if (isUp){
            if (this.vOrderedResources.elementAt(0).equals(res)){
                isExtremePosition = false;
            }
        } else {
            if (this.vOrderedResources.elementAt(this.vOrderedResources.size()-1).equals(res)){
                isExtremePosition = false;
            }            
        }
        return isExtremePosition;
    }
    
    
	///////////////////////////////////////// 
	public void setRowHeight(int newValue){
		this.rowHeight = newValue;
	}
	public int getRowHeight(){
		return(this.rowHeight);
	}


	///////////////////////////////////////// 
	public void setSlotWidth(int newValue){
		this.slotWidth = newValue;
	}
	public int getSlotWidth(){
		return(this.slotWidth);
	}

	
	///////////////////////////////////////// 
	public Color getResourceBgColor() {
		return resourceBGColor;
	}
	public void setResourceBgColor(Color newValue) {
		this.resourceBGColor = newValue;
	}

	
	/////////////////////////////////////////	
    public int getCurrentWidth() {
        return currentWidth;
    }
    public void setCurrentWidth(int newValue) {
        this.currentWidth = newValue;
    }

}
