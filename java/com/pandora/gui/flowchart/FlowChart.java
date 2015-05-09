package com.pandora.gui.flowchart;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ResourceBundle;

import javax.swing.UIManager;

public class FlowChart extends Applet implements AdjustmentListener {

	private static final long serialVersionUID = 1L;

	/** String used by PARAM applet tag */
	//private static final String LANGUAGE         = "LANGUAGE";

	/** String used by PARAM applet tag */	
	//private static final String COUNTRY          = "COUNTRY";

	/** String used by PARAM applet tag */	
	private static final String EDITABLE         = "EDITABLE";

	/** String used by applet tag */	
  	private static final String APPLET_WIDTH     = "WIDTH";
  
	/** String used by applet tag */	  	
  	private static final String APPLET_HEIGHT    = "HEIGHT";

	/** String used by PARAM applet tag */	
	private static final String URI              = "URI";

  	/** Default thickness (width or height) of scroll bars */
	//private final int SCROLL_BAR_THICK = 15;
	
	private boolean isEditable;
	private int width;
	private int height;
	private ChartNodeManager chartNodeMgr;
	
	//private ToolBox tbox;
	
  	private Scrollbar hScroll;
  	private Scrollbar vScroll;
	//private DecorativePanel decoUpperCorner;
	//private DecorativePanel decoBottonCornerRight;
	//private DecorativePanel decoBottonCornerLeft;	
	
	/** The URI used to send data to external application */
	private String uri;

	/** The resource bundle used to internacionalization */
	private ResourceBundle bundle;
	
	/**
	 * Constructor
	 */
  	public FlowChart() {
	}	


  	/**
  	 * Initialization method called by browser (default applet method). Overload by FlowChart
  	 * to initialize the visual objects and get all informations from PARAM applet tag. 
  	 */
  	public void init() {
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
  		
    	this.width = Integer.parseInt(getParameter(APPLET_WIDTH));
    	this.height = Integer.parseInt(getParameter(APPLET_HEIGHT));
		this.uri = getParameter(URI);
		 		    	
  		//init Background panels  		
  		//decoUpperCorner = new DecorativePanel(false);
  		//decoBottonCornerRight = new DecorativePanel(true);
  		//decoBottonCornerLeft = new DecorativePanel(false);  		
  		//add(decoUpperCorner);
  		//add(decoBottonCornerRight);
  		//add(decoBottonCornerLeft);
  		
  		//verify if Gantt can be editable
    	if (getParameter(EDITABLE)!=null) {
    		this.isEditable = Boolean.valueOf(getParameter(EDITABLE)).booleanValue();
    	}

  		//init Scroll Bars
		hScroll = new Scrollbar(Scrollbar.HORIZONTAL, 0, 50, 0, 100);
		vScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 50, 0, 100);
  		add(hScroll);
  		add(vScroll);
    	hScroll.addAdjustmentListener(this);
	    vScroll.addAdjustmentListener(this);
	    
	    hScroll.setVisible(false);
	    vScroll.setVisible(false);

  		//init Tool Box
		//tbox = new ToolBox(this);
  		//add(tbox);
  	  	  		
		
  		//init Chart Panel
	    this.chartNodeMgr = new ChartNodeManager(this);
  		add(this.chartNodeMgr);
  		
  		//get information from <param> values and generate Gantt data structure
  		this.loadChartNodeValues();
  		
  	}

  	/**
  	 * Painting method called by browser (default applet method). Overload by FlowChart
  	 * to painting/positioning the visual objects. 
  	 */
  	public void paint(Graphics g) {
 		//int tickness = 3;
		
  		//paint Resources
  		//int resmgHeight = this.resmg.getResourcesHeight();
  		//if (resmgHeight<this.vScroll.getHeight()) {
		//	this.vScroll.setEnabled(false);
  		//	this.resmg.setSize(this.resmg.getCurrentWidth()-tickness, this.vScroll.getHeight());
  		//}else {
		//	this.vScroll.setEnabled(true);  			
  		//	this.resmg.setSize(this.resmg.getCurrentWidth()-tickness, this.resmg.getResourcesHeight());  		
  		//}
      	
      	//hScroll.setEnabled((this.getGanttWidth() > this.hScroll.getWidth()));
      	
      	//positioning scroll bars
		//this.hScroll.setBounds(this.resmg.getWidth() + this.resmg.getX(),
		//			           this.height - SCROLL_BAR_THICK,
        //                      this.width - SCROLL_BAR_THICK - this.resmg.getWidth() - this.resmg.getX(),
        //                       SCROLL_BAR_THICK);
		//this.vScroll.setBounds(this.width - SCROLL_BAR_THICK,
		//                       this.timeln.getY(),
        //                       SCROLL_BAR_THICK, 
        //                       this.height - SCROLL_BAR_THICK - this.timeln.getY());
		
      	
      	//positioning Tool Box
      	//this.tbox.setSize(this.width, ToolBox.TOOL_BOX_HEIGHT);      	
      	//this.tbox.setLocation(0,0);
  		//this.tbox.setBackground(this.hScroll.getBackground());      	
      	//this.tbox.setVisible(true);
      	      	
      	//positioning Panel area
 		//this.chartNodeMgr.setSize(width, height);
  		this.chartNodeMgr.setSize(width, height);
  		this.chartNodeMgr.setLocation(0, 0);

 		this.chartNodeMgr.setBackground(new Color(245, 245, 245));
 		this.chartNodeMgr.setVisible(true);
      	
 		this.chartNodeMgr.paint(g);
 		
      	//recalculate location of job area, timeline, resource area etc, based on 
      	//scroll bars coordenates      	
      	//recalcFromScrollBar();

      	//positioning decorate panels
  		//decoUpperCorner.setBounds(0, this.timeln.getY(), this.resmg.getWidth(), this.timeln.getHeight());
  		//decoUpperCorner.setBackground(this.hScroll.getBackground());
  		//decoBottonCornerRight.setBounds(this.width - SCROLL_BAR_THICK, this.height - SCROLL_BAR_THICK, this.vScroll.getHeight(), SCROLL_BAR_THICK);
  		//decoBottonCornerRight.setBackground(this.hScroll.getBackground());
  		//decoBottonCornerLeft.setBounds(0, this.height - SCROLL_BAR_THICK, this.resmg.getWidth(), SCROLL_BAR_THICK);
  		//decoBottonCornerLeft.setBackground(this.hScroll.getBackground());  		
  	}

  	/**
  	 * Get all values of resources from PARAM applet tag.
  	 */ 	
  	private void loadChartNodeValues(){

	    //get total number of nodes
	    int nodeNum = Integer.parseInt(getParameter(ChartNode.NODE_NUM));
	    if (nodeNum<1) {
			System.out.println("ERR: invalid value for Node Number"); //debug
			nodeNum = 1;
		}
	    
	    //if there are more than 100 resources, adjust the max of vertical scroll
	    //if (resNum>100){
	    //    vScroll.setMaximum(resNum);    
	    //}
	    
	    //get data of each node
	    for (int i=1; i <= nodeNum; i++) {
	    	String c = getParameter(ChartNode.NODE + i);
	    	if (c==null) {
	      		System.out.println("ERR: error parsing Node item: " + i); //debug	    	    
	    	} else {
	    		chartNodeMgr.addNode(c, getCodeBase());
	      	}
	      		
	    }	    
  	}

  	
  	/**
  	 * Load the bundle based on locale
  	 */
  	/*
  	private void loadBundle(String lang, String country) {
  	    String prefix = "ApplicationResources";
  	    boolean isOk = false;
		this.bundle = ResourceBundle.getBundle(prefix + "_" + lang);
		try {
		    this.getMessage("footer.copyright");
		    isOk = true;
		} catch(Exception e) {
		    isOk = false;
	    }

		if (!isOk) {
		    this.bundle = ResourceBundle.getBundle(prefix + "_" + lang + "_" + country);		    
			try {
			    this.getMessage("footer.copyright");
			    isOk = true;			    
			} catch(Exception e) {
			    isOk = false;
		    }		    
		}

		if (!isOk) {
		    this.bundle = ResourceBundle.getBundle(prefix + "_en_US");		    
		} 
  	}
  	 *
  	 */
  	/*
  	private void loadToolBarValues(){

		//get the number of buttons of gantt (optional)
	    int numButtoms = Util.getInt(getParameter(ToolBox.BUTTON_NUM));

		//set applet Code Base int Tool Box
		this.tbox.setCodeBase(this.getCodeBase());
	    
	    if (numButtoms>0) {

		    //get data of each gantt Button
		    for (int i=1; i <= numButtoms; i++) {
		    	String c = getParameter(ToolBox.BUTTON + i);
		    	if (c==null)
		    		System.out.println("ERR: error parsing Button: " + i); //debug
		    	else
		    		this.tbox.addToolItem(c);
		    }
		}	
  	}

	private void recalcFromScrollBar() {
				
	    //recalc scroll values
	    float widthFator = (-(float)(this.nodeAreaPanel.getWidth() - this.width +
	                  this.resmg.getWidth() + SCROLL_BAR_THICK) / (hScroll.getMaximum()/2))
	                  * hScroll.getValue();
	    float heightFactor = (-(float)(this.nodeAreaPanel.getHeight() - this.height +
	                  this.timeln.getY() + this.timeln.getHeight() + SCROLL_BAR_THICK) /
	                  (this.vScroll.getMaximum() -this.vScroll.getVisibleAmount())) * vScroll.getValue();

	    //reset Panels position
	    this.nodeAreaPanel.setLocation((int)widthFator + this.resmg.getWidth(),
	                                (int)heightFactor + this.timeln.getY() + this.timeln.getHeight());
	    this.timeln.setLocation(this.nodeAreaPanel.getX(), ToolBox.TOOL_BOX_HEIGHT);
	    this.resmg.setLocation(0, (int)heightFactor + TimeLine.TIME_LINE_HEIGHT + this.timeln.getY() + 1);
	}
  	*/
	
	public void adjustmentValueChanged(AdjustmentEvent event) {
		if (event.getSource().equals(hScroll) || event.getSource().equals(vScroll)) {
			//recalcFromScrollBar();
		}
	}
	
    /**
     * Get the content into bundle based on key
     */
    public String getMessage(String key) {
        return this.bundle.getString(key);
    }
	
    
	//////////////////////////////////////////
	public boolean isEditable() {
		return isEditable;
	}


	//////////////////////////////////////////    
    public String getUri() {
        return uri;
    }
  
}