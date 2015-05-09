package com.pandora.gui.gantt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;
import com.pandora.integration.Connector;
import com.pandora.integration.Integration;

import com.pandora.integration.ResourceTaskAllocIntegration;
import com.pandora.integration.ResourceTaskIntegration;
import com.pandora.integration.exception.IntegrationException;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

public class ToolBox extends Panel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** String used by PARAM applet tag */    
	public static final String BUTTON_NUM   = "BUTTONNUM";
	
	/** String used by PARAM applet tag */	
	public static final String BUTTON       = "BUTTON_";
	
	/** The default height of Tool Box panel */
 	public static final int TOOL_BOX_HEIGHT = 27;
	
 	/** The list of itens of Gantt ToolBox */
  	private Vector vItens;
  	
  	/** The applet Code Base */
  	private URL codeBase;
  	
  	/** Pointer to Gantt object */
  	private Gantt parentGantt;  	
  	
  	
	/**
	 * Constructor. <br> 
	 * @param g
	 */  	
  	public ToolBox(Gantt g) {
  		this.parentGantt = g;
	}	

  	
  	/** 
  	 * Create a new visual object (button, combo, etc) into tool box.
  	 */
	public void addToolItem(String c){
		if (vItens==null) {
			vItens = new Vector();
		}
		StringTokenizer stList = new StringTokenizer(c, "|");
		
    	String name = stList.nextToken();
    	String role = stList.nextToken();
    	String url = stList.nextToken();
    	boolean enable = Util.getBoolean(stList.nextToken());		
		String imagePath = stList.nextToken();
		
		if (role.equals(ToolItem.LAYER_COMBO)) {
			ToolCombo item = new ToolCombo();
			addToolItem(item, name, role, enable);
			((JComboBox)item.getVisualObject()).addActionListener(this);
			
			String allLabel = this.parentGantt.getMessage("label.all");
			Layer allLayers = new Layer("-1|1|" + allLabel + "|" + allLabel + "|empty|480");
			item.addOption(allLayers);
			
			HashMap layers = this.parentGantt.getLayerHash();
			if (layers!=null) {
		  	    Iterator i = layers.values().iterator();
		  	    while(i.hasNext()){
		  	        Layer layer = (Layer)i.next();
		  	        item.addOption(layer);
		  	    }				
			}
		} else if (role.equals(ToolItem.FILTER_COMBO)) {
			ToolCombo item = new ToolCombo();
			addToolItem(item, name, role, enable);
			((JComboBox)item.getVisualObject()).addActionListener(this);

			String allLabel = this.parentGantt.getMessage("label.all");
			Filter allFilters = new Filter("-1|" + allLabel);
			item.addOption(allFilters);

			Vector filters = this.parentGantt.getFilterVector();
			if (filters!=null) {
		  	    Iterator i = filters.iterator();
		  	    while(i.hasNext()){
		  	        Filter f = (Filter)i.next();
		  	        item.addOption(f);
		  	    }				
			}
		} else {
			ToolButton item = new ToolButton();	
			item.setUrlTarget(url);		
			addToolItem(item, name, role, enable);
			if (!imagePath.equals(ToolButton.SEPARATOR)){
				item.setIcon(new ImageIcon(this.getURL(imagePath)));    
			}
			((JButton)item.getVisualObject()).addActionListener(this);
		}
	}
	

	private void addToolItem(ToolItem item, String name, String role, boolean enable) {
		item.setName(name);
		item.setRole(role);
		this.add(item.getVisualObject());
		item.getVisualObject().setEnabled(enable);
		vItens.addElement(item);
	}
	
	
  	public void paint(Graphics g) {
  	    int cursorX = 0;
  	    
		int x2 = this.getWidth();
		int y2 = this.getHeight();
			     	  		
		g.setColor(this.getBackground());
		g.fillRect(0, 0, x2, y2);
		
		g.setColor(Color.darkGray);
		g.drawLine(1, 1, x2, 1);
		g.drawLine(1, y2-1, x2, y2-1);
		
		g.setColor(Color.white);
		g.drawLine(1, 2, x2, 2);
		g.drawLine(1, y2-2, x2, y2-2);
		
		if (vItens!=null) {
			Iterator i = vItens.iterator();
			while(i.hasNext()){
				ToolItem obj = (ToolItem)i.next();
				
				if (obj instanceof ToolButton) {
					ToolButton tb = (ToolButton) obj;
					if (tb.getRole().equals(ToolItem.SEPARATOR)){
					    cursorX = cursorX + 10;					
					    obj.getVisualObject().setVisible(false);
					    
					} else {
					    int size = TOOL_BOX_HEIGHT-5;
					    tb.getVisualObject().setBounds(cursorX+3, 3, size, size);
					    cursorX = cursorX + size;
					    obj.getVisualObject().setVisible(true);
					}
					
				} else if (obj instanceof ToolCombo) {
					ToolCombo tc = (ToolCombo) obj;
					tc.getVisualObject().setLocation(cursorX +3, 3);
					obj.getVisualObject().setVisible(true);
					cursorX = cursorX + tc.getVisualObject().getWidth();
				}
				
				obj.getVisualObject().repaint();
			}
		}		
  	}

	public void setCodeBase(URL newValue) {
		this.codeBase = newValue;
	}
	
	private URL getURL(String filename) {
		URL url = null;
		try {
	    	url = new URL(this.codeBase, filename);
	    } catch (java.net.MalformedURLException e) {
	    	return null;
	    }
	    return url;
	}
	
	
	/**
	 * Verify which button was clicked and process the appropriate procedure.
	 */
	public void actionPerformed(ActionEvent ev) {
	    Connector c = new Connector();
	    
		Iterator i = vItens.iterator();
		while(i.hasNext()){
			ToolItem ti = (ToolItem)i.next();
			
			if (ti.getVisualObject().equals(ev.getSource())){

				if (ti.getRole().equals(ToolButton.ZOOM_IN_ROLE)){
					this.parentGantt.setZoom(true);
					
				} else if (ti.getRole().equals(ToolButton.ZOOM_OUT_ROLE)){
					this.parentGantt.setZoom(false);
					
				} else if (ti.getRole().equals(ToolButton.SAVE_ROLE)){
					Vector v = this.parentGantt.getChangedJobs();
					
					if (v.size()>0) {
					    String comment = this.getComment();
					    if ((comment != null) && (comment.length() > 0)) {
					        try {
					            boolean resp = c.executeWithoutResponse(this.getIntegrationDataFromJobs(v, comment), this.parentGantt.getUri());
					            if (resp) {
					                this.showMsgBox("gantt.saveTask.updateeok", null);
					                this.parentGantt.clearChangedJobs();
					            } else {
					                this.showMsgBox("gantt.saveTask.saveerror", null);    
					            }
                            } catch (IntegrationException e) {
                                this.showMsgBox("gantt.saveTask.saveerror", e.getErrorMessage());
                                e.printStackTrace();
                            }    
					    } else {
					        this.showMsgBox("gantt.saveTask.commentmandatory", null);
					    }
					} else {
					    this.showMsgBox("gantt.saveTask.nottosave", null);
					}
					
				} else if (ti.getRole().equals(ToolButton.UP_RES_ROLE)){
				    this.parentGantt.changeSelectedResourcePosition(true);
				    
				} else if (ti.getRole().equals(ToolButton.DOWN_RES_ROLE)){
				    this.parentGantt.changeSelectedResourcePosition(false);

				//} else if (ti.getRole().equals(ToolButton.FILTER_COMBO)){
					//JComboBox cb = ((JComboBox)ti.getVisualObject());
					//Filter selected = (Filter)cb.getItemAt(cb.getSelectedIndex());				
					//if (selected.getId().equals("-1")) {
					//	this.parentGantt.setVisibleLayerId(null);
					//} else {
					//	this.parentGantt.setVisibleLayerId(selected.getLayerList());	
					//}
					//this.parentGantt.setVisibleSelectionMark(false);
					
				} else if (ti.getRole().equals(ToolButton.LAYER_COMBO)){
					JComboBox cb = ((JComboBox)ti.getVisualObject()); 
					Layer selected = (Layer)cb.getItemAt(cb.getSelectedIndex());
					if (selected.getId().equals("-1")) {
						this.parentGantt.setVisibleLayerId(null);
					} else {
						this.parentGantt.setVisibleLayerId(selected.getId());	
					}
					this.parentGantt.setVisibleSelectionMark(false);
				}
			
				break;
			}
		}
	}
	
	private String getComment(){
	    return (String)JOptionPane.showInputDialog(this.parentGantt,
	            this.parentGantt.getMessage("gantt.saveTask.dialog.desc"),
                this.parentGantt.getMessage("gantt.saveTask.dialog.title"), 
                JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
	
	
	private void showMsgBox(String keyBundle, String additionalInfo) {
	    if (additionalInfo==null) {
	        JOptionPane.showMessageDialog(this.parentGantt, this.parentGantt.getMessage(keyBundle));    
	    } else {
	        JOptionPane.showMessageDialog(this.parentGantt, this.parentGantt.getMessage(keyBundle) + 
	                "\n" + additionalInfo);	        
	    }
	}
	
	
	private ArrayList getIntegrationDataFromJobs(Vector v, String comment){
	    ArrayList response = new ArrayList();
	    String user = this.parentGantt.getUsername();
	    String pwd = this.parentGantt.getPassword();
	    
	    Iterator i = v.iterator();
	    while(i.hasNext()){
	        ResourceTaskIntegration rti = new ResourceTaskIntegration(user, pwd, this.parentGantt.getLocale()+""); 
	        Job job = (Job)i.next();
  			AllocUnit firstSlot = job.getJobAlloc(0);
  			Timestamp startDate = parentGantt.getStartDate().getTimestamp();
  			Timestamp slotDate = DateUtil.getChangedDate(startDate, Calendar.DATE, (firstSlot.getSlot()-1));
  
	 	    rti.setEstimatedDay(DateUtil.get(slotDate, Calendar.DAY_OF_MONTH)+"");
	 	    rti.setEstimatedMonth((DateUtil.get(slotDate, Calendar.MONTH)+1)+"");
			rti.setEstimatedYear(DateUtil.get(slotDate, Calendar.YEAR)+"");
		    rti.setResourceId(job.getLayer().getId());
		    rti.setTaskId(job.getResourceId());
		    rti.setComment(comment);
		    rti.setTransaction(Integration.TRANSACTION_UPDATE);

		    int totalTime = 0;
	        for (int k=0; k<job.getJobAllocSize(); k++) {
	            AllocUnit slot = job.getJobAlloc(k);
	            totalTime = totalTime + (int)(slot.getValue() * 60);
	        }
	        rti.setEstimatedTime(StringUtil.getFloatToString(totalTime, this.parentGantt.getLocale()));
	        
		    System.out.println(rti);
		    response.add(rti);
	    }
	    
	    Iterator j = v.iterator();
	    while(j.hasNext()){
	        Job job = (Job)j.next();
	        for (int k=0; k<job.getJobAllocSize(); k++) {
	            AllocUnit slot = job.getJobAlloc(k);

	 	 	    ResourceTaskAllocIntegration rtai = new ResourceTaskAllocIntegration(user, pwd, this.parentGantt.getLocale()+"");
	 	 	    rtai.setResourceId(job.getLayer().getId());
	 	 	    rtai.setSequence((k+1)+"");
	 	 	    rtai.setTaskId(job.getResourceId());
	 	 	    rtai.setValue(StringUtil.getFloatToString(slot.getValue(), this.parentGantt.getLocale()));
	 	 	    rtai.setTransaction(Integration.TRANSACTION_UPDATE);
	 	 	    System.out.println(rtai);
			    response.add(rtai);
	        }
	    }

	    return response;
	}
}
