package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * This class represents a mark used to enphasize a job selected. The SelectionMark
 * can be used to redim a job too.
 */
public class SelectionMark implements MouseListener, MouseMotionListener {

	private Panel mark;
	private int lastRedimX;
	private int lastReleasedWidth;
	
	/** Current Job selected on gantt*/
	private Job selectedJob;

	/** Current Resource selected on gantt*/
	private Resource selectedResource;
	
	/** Pointer to container (Gantt) */
	private Gantt parentGantt;	

	/**
	 * Constructor.
	 * @param g
	 * @param container
	 */
	public SelectionMark(Gantt g, Panel container){
		this.parentGantt = g;
		this.mark = new Panel();
		container.add(this.mark);
   		this.mark.addMouseMotionListener(this);		
    	this.mark.addMouseListener(this);   		
	}


	public void paint(){
    	if (this.selectedJob!=null) {
			this.mark.setBounds(this.selectedJob.getJobX() + this.selectedJob.getJobWidth()-4,
								this.selectedJob.getJobY()+(this.selectedJob.getJobHeight()/2)-2, 6, 6);
								
			this.mark.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
      		this.mark.setForeground(Color.red);
     		this.mark.setBackground(Color.red);
			this.mark.setVisible(true);      					
    	} else {
			this.mark.setVisible(false);
    	}
	}
	
	
	public void mouseEntered(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me) {}
	

	public void mouseReleased(MouseEvent me) {
		int slot = 0;
    	int newNumberSlot = 0;

    	if (this.selectedJob!=null) {
    		
			//verify if current task is out of gantt limits
			this.selectedJob.checkJobLimits();

			//get first slot selected and update Label position
      		slot = (this.selectedJob.getJobX() * this.parentGantt.getNumSlots()) /
            	 (this.parentGantt.getSlotsWidth() * this.parentGantt.getNumSlots());

      		double newNumberSlotBuff = (double)this.selectedJob.getJobWidth() / this.parentGantt.getSlotsWidth();
			newNumberSlot = (int)(newNumberSlotBuff + .5);

      		if (this.selectedJob.canChangeJob(slot, slot + newNumberSlot -1)) {

				selectedJob.setNewWidth(newNumberSlot * this.parentGantt.getSlotsWidth());
				this.parentGantt.setVisibleSelectionMark(true);
				this.selectedJob.setWasChanged(true);
				this.lastReleasedWidth = this.selectedJob.getJobWidth();
				int diff = newNumberSlot - this.selectedJob.getJobAllocSize();
	
				if (diff>0) {
					for (int i=0; i<diff; i++) {
				    	this.selectedJob.appendAlloc();
					}
				} else if (diff<0) {
				  	for (int i=diff; i<0; i++) {
				    	this.selectedJob.removeLastAlloc();
				  	}
				}
	
			} else {
				selectedJob.setNewWidth(this.lastReleasedWidth);
			}
      		
      		//Call back to parent resource of job
      		selectedJob.callBackToResource();

    	}		
	}
	

	public void mousePressed(MouseEvent me) {
		this.lastRedimX = me.getX();
		this.lastReleasedWidth = this.selectedJob.getJobWidth();
	}
	
	
	public void mouseDragged(MouseEvent me) {
		if (this.selectedJob!=null) {
			//get current position of redim cursor and calculate a new position...
	    	//this.hideAllocCursor();
	      	this.mark.setBounds(this.mark.getX() + me.getX() - this.lastRedimX,
		                        this.mark.getY(), this.mark.getWidth(),
		                        this.mark.getHeight());
		
			//calculate a new width of task...
	       	this.selectedJob.setNewWidth(this.selectedJob.getJobWidth() + me.getX() - this.lastRedimX);
	
	      	//minimum width...
	      	if (this.selectedJob.getJobWidth() < parentGantt.getSlotsWidth()){
	        	this.selectedJob.setNewWidth(parentGantt.getSlotsWidth());
	        	this.mark.setLocation(this.selectedJob.getJobX() + parentGantt.getSlotsWidth()-3,
	                                  this.mark.getY());
	      	}
		}
  	}

	
	public void setVisible(boolean isVisible){
		this.mark.setVisible(isVisible);
	}
	
	
	///////////////////////////////////////////
	public void setSelectedJob(Job newValue){
		this.selectedJob = newValue;
		paint();
	}	
	public Job getSelectedJob(){
		return this.selectedJob;
	}	


	///////////////////////////////////////////
	public void setSelectedResource(Resource newValue){
		this.selectedResource = newValue;
	}	
	public Resource getSelectedResource(){
		return this.selectedResource;
	}	
	
}
