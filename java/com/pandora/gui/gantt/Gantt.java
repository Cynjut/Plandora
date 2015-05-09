package com.pandora.gui.gantt;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.UIManager;


public class Gantt extends Applet implements AdjustmentListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;

	/** String used by PARAM applet tag */
	private static final String LANGUAGE         = "LANGUAGE";

	/** String used by PARAM applet tag */	
	private static final String COUNTRY          = "COUNTRY";

	/** String used by PARAM applet tag */	
	private static final String EDITABLE         = "EDITABLE";

	/** String used by applet tag */	
  	private static final String APPLET_WIDTH     = "WIDTH";
  
	/** String used by applet tag */	  	
  	private static final String APPLET_HEIGHT    = "HEIGHT";

  	/** String used by applet tag */
	public static final String NW_MARK_COLOR     = "NWMARKCOLOR";		

	/** String used by PARAM applet tag */
	private static final String USERNAME         = "USERNAME";

	/** String used by PARAM applet tag */	
	private static final String PASSWORD         = "PASSWORD";

	/** String used by PARAM applet tag */	
	private static final String URI              = "URI";

  	/** Default thickness (width or height) of scroll bars */
	private final int SCROLL_BAR_THICK = 15;
	
	private boolean isEditable;
	private int width;
	private int height;

	private ResourceManager resmg;
	private Panel jobAreaPanel;
	private ToolBox tbox;
	private TimeLine timeln;
  	private ToolTipText tiptxt;
  	private SelectionMark redimCursor;
	private HashMap htMenu;
	private HashMap htLayers;
	
	private Vector vFilter;
	
  	private Scrollbar hScroll;
  	private Scrollbar vScroll;
	private DecorativePanel decoUpperCorner;
	private DecorativePanel decoBottonCornerRight;
	private DecorativePanel decoBottonCornerLeft;	
	
	private Vector noWorkingDaysPanels;
	private Panel resResizeBar;
	
	/** The current username of Gantt's user */
	private String username;
	
	/** The password of Gantt's user */
	private String password;

	/** The URI used to send data to external application */
	private String uri;

	/** The id of layer that is visible in gantt chart. If null, all layers are visible. */
	private String visibleLayerId;
	
	/** The resource bundle used to internacionalization */
	private ResourceBundle bundle;
	
	/**
	 * Constructor
	 */
  	public Gantt() {
		tbox = null;
		timeln = null;
		visibleLayerId = null;
	}	


  	/**
  	 * Initialization method called by browser (default applet method). Overload by Gantt
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
		this.username = getParameter(USERNAME);
		this.password = getParameter(PASSWORD);
		this.uri = getParameter(URI);
		
		//init tool tip text box
		this.tiptxt = new ToolTipText();
  		add(this.tiptxt);
  		tiptxt.setVisible(false);
  		    	
  		//init Background panels  		
  		decoUpperCorner = new DecorativePanel(false);
  		decoBottonCornerRight = new DecorativePanel(true);
  		decoBottonCornerLeft = new DecorativePanel(false);  		
  		add(decoUpperCorner);
  		add(decoBottonCornerRight);
  		add(decoBottonCornerLeft);
  		
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

  		//init Tool Box
		tbox = new ToolBox(this);
  		add(tbox);
  		
  		//create the resource manager resize bar
    	resResizeBar = new Panel();
    	add(resResizeBar);
    	resResizeBar.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    	resResizeBar.addMouseMotionListener(this);
    	resResizeBar.setBackground(this.hScroll.getBackground());
    	
  		//init ResourceManager
  		resmg = new ResourceManager(this);
  		add(resmg);
  		if (getParameter(ResourceManager.ROW_HEIGHT)!=null) {
  			resmg.setRowHeight(Util.getInt(getParameter(ResourceManager.ROW_HEIGHT)));
  		}
    	if (getParameter(ResourceManager.SLOT_WIDTH)!=null) {
    		resmg.setSlotWidth(Util.getInt(getParameter(ResourceManager.SLOT_WIDTH)));
    	}
  		    	
  		//get information from <param> values about timeline
		this.loadTimeLineValues();
  	  	  		
		
  		//init Job Area Panel
  		jobAreaPanel = new Panel();
  		add(jobAreaPanel);
		  		
  		//get information from <param> values and generate Gantt data structure
  		this.loadResourcesValues();
  		this.loadLayerValues(); 		
  		this.loadJobsValues();
  		this.loadDependenceValues();  		   		
  		this.loadAllocUnitValues(); 		
  		this.loadNoWorkingDaysValues();  		
  		this.loadFilterValues();
  		this.loadToolBarValues();
  		this.loadMenuValues();
  		
  		//THE TEST BELLOW SEND CALL A JAVASCRIPT METHOD (but MAYSCRIPT must be set into APPLET tag) 
        //JSObject win = JSObject.getWindow(this);
        //System.out.println(win);
        //JSObject doc = (JSObject) win.getMember("document");
        //System.out.println(doc);
        //win.call("testeApplet", new String[0]);
        //JSObject loc = (JSObject) doc.getMember("location");
        //System.out.println(loc);
        //String s = (String) loc.getMember("href");  // document.location.href
        //win.call("f", null);                      // Call f() in HTML page
  		
  	}

  	/**
  	 * Painting method called by browser (default applet method). Overload by Gantt
  	 * to painting/positioning the visual objects. 
  	 */
  	public void paint(Graphics g) {
 		int tickness = 3;
		
  		//paint Resources
  		int resmgHeight = this.resmg.getResourcesHeight();
  		if (resmgHeight<this.vScroll.getHeight()) {
			this.vScroll.setEnabled(false);
  			this.resmg.setSize(this.resmg.getCurrentWidth()-tickness, this.vScroll.getHeight());
  		}else {
			this.vScroll.setEnabled(true);  			
  			this.resmg.setSize(this.resmg.getCurrentWidth()-tickness, this.resmg.getResourcesHeight());  		
  		}
      	
      	hScroll.setEnabled((this.getGanttWidth() > this.hScroll.getWidth()));
      	
      	//positioning scroll bars
		this.hScroll.setBounds(this.resmg.getWidth() + this.resmg.getX(),
					           this.height - SCROLL_BAR_THICK,
                               this.width - SCROLL_BAR_THICK - this.resmg.getWidth() - this.resmg.getX(),
                               SCROLL_BAR_THICK);
		this.vScroll.setBounds(this.width - SCROLL_BAR_THICK,
		                       this.timeln.getY(),
                               SCROLL_BAR_THICK, 
                               this.height - SCROLL_BAR_THICK - this.timeln.getY());
		
      	//positioning Time Line Panel area
      	this.timeln.setBackground(Color.lightGray);
  		this.timeln.setFont(new Font("Arial", Font.PLAIN, 11));      	
      	this.timeln.setVisible(true);
      	this.timeln.setSize(this.timeln.getNumSlots() * this.timeln.getSlotsWidth(), TimeLine.TIME_LINE_HEIGHT);
      	
      	//positioning Tool Box
      	this.tbox.setSize(this.width, ToolBox.TOOL_BOX_HEIGHT);      	
      	this.tbox.setLocation(0,0);
  		this.tbox.setBackground(this.hScroll.getBackground());      	
      	this.tbox.setVisible(true);
      	      	
      	//positioning Job Panel area
      	this.jobAreaPanel.setSize(this.timeln.getWidth(), resmg.getHeight());
      	this.jobAreaPanel.setBackground(new Color(245, 245, 245));
      	this.jobAreaPanel.setVisible(true);
      	
      	//recalculate location of job area, timeline, resource area etc, based on 
      	//scroll bars coordenates      	
      	recalcFromScrollBar();

      	//positioning decorate panels
  		decoUpperCorner.setBounds(0, this.timeln.getY(), this.resmg.getWidth(), this.timeln.getHeight());
  		decoUpperCorner.setBackground(this.hScroll.getBackground());
  		decoBottonCornerRight.setBounds(this.width - SCROLL_BAR_THICK, this.height - SCROLL_BAR_THICK, this.vScroll.getHeight(), SCROLL_BAR_THICK);
  		decoBottonCornerRight.setBackground(this.hScroll.getBackground());
  		decoBottonCornerLeft.setBounds(0, this.height - SCROLL_BAR_THICK, this.resmg.getWidth(), SCROLL_BAR_THICK);
  		decoBottonCornerLeft.setBackground(this.hScroll.getBackground());
  		
  		//calculate the position of resource manager resize bar 
    	resResizeBar.setBounds(resmg.getWidth()-tickness, this.timeln.getY()+this.timeln.getHeight()+1, tickness, resmg.getHeight());
  		
  		//positioning no working days panels
  		this.refreshNoWorkingDaysPanels();
  	}

  	/**
  	 * Refresh the noWorkingDays Panels
  	 */
  	private void refreshNoWorkingDaysPanels(){
	    Calendar c = Calendar.getInstance();
	    TimeGantt tg = new TimeGantt(this.timeln.getStartDate());
	    int idx = 0;
	    
	    if (this.noWorkingDaysPanels!=null && this.timeln.getSlotDateFormat().equals("E")){
	  	    for(int i=1; i<this.timeln.getNumSlots();i++){
  	           tg.addSlot(1);
		       c.setTimeInMillis(tg.getTimeInMillis());
		       if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
		           JLabel p = (JLabel)this.noWorkingDaysPanels.elementAt(idx);	           
		           int w = this.timeln.getSlotsWidth();
		           p.setSize(w * 2, this.jobAreaPanel.getHeight());
		           p.setLocation((i * w), 0);
		           p.setBackground(new Color(230, 230, 230));
		           p.setOpaque(true);
		           p.setVisible(true);
		           idx++;		           
		       }
	  	    }	        
	    }
  	}

  	/**
  	 * Get all values of resources from PARAM applet tag.
  	 */ 	
  	private void loadResourcesValues(){

	    //get total number of resources
	    int resNum = Util.getInt(getParameter(Resource.RES_NUM));
	    if (resNum<1) {
			System.out.println("ERR: invalid value for Resource Number"); //debug
			resNum = 1;
		}
	    
	    //if there are more than 100 resources, adjust the max of vertical scroll
	    if (resNum>100){
	        vScroll.setMaximum(resNum);    
	    }
	    
	    //get data of each resource 
	    for (int i=1; i <= resNum; i++) {
	    	String c = getParameter(Resource.RES + i);
	    	if (c==null) {
	      		System.out.println("ERR: error parsing Resource item: " + i); //debug	    	    
	    	} else {
	      	  	resmg.addResource(c);
	      	}
	      		
	    }	    
  	}

  	
  	/**
  	 * Get all values of jobs from PARAM applet tag.
  	 */ 	  	
	private void loadJobsValues(){
		
	    //get total number of jobs
	    int jobNum = Util.getInt(getParameter(Job.JOB_NUM));
	    if (jobNum<1) {
			System.out.println("ERR: invalid value for Job Number"); //debug
			jobNum = 1;
		}

		//create a new Selection Mark
		redimCursor = new SelectionMark(this, this.jobAreaPanel);
		redimCursor.setVisible(false);
				
	    //get data of each job
	    for (int i=1; i <= jobNum; i++) {
	    	String c = getParameter(Job.JOB + i);
	    	if (c==null)
	    		System.out.println("ERR: error parsing Job item: " + i); //debug
	    	else {
	    		Job j = new Job(getParameter(Job.JOB + i), this);
	    		j.setLayer((Layer)this.htLayers.get(j.getLayerId()));
	    		Resource r = resmg.getResource(j.getResourceId());
	    		if (r==null) 
	    			System.out.println("ERR: Resource unknown for Job: " + j.getId()); //debug
	    		else {
	    			r.addJob(j);
					j.setContainer(this.jobAreaPanel);
	    		}
	    	}
	    }
	    
	    //special treatment for resources without jobs in it...
	    this.resmg.checkResourcesWithoutJobs(this.jobAreaPanel);
  	}


  	/**
  	 * Get all values of alloc units from PARAM applet tag.
  	 */ 	
  	private void loadAllocUnitValues(){
  		
	    //get total number of Alloc Units
	    int allocUnitNum = Util.getInt(getParameter(AllocUnit.A_UNIT_NUM));
	    if (allocUnitNum<1) {
			System.out.println("ERR: invalid value for AllocUnit Number"); //debug
			allocUnitNum = 1;
		}
	
	    //get data of each Alloc Unit
	    for (int i=1; i <= allocUnitNum; i++) {
	    	String c = getParameter(AllocUnit.A_UNIT + i);
	    	if (c==null)
	    		System.out.println("ERR: error parsing AllocUnit: " + i); //debug
	    	else {
				StringTokenizer stList = new StringTokenizer(c, "|");
		    	String jobId = stList.nextToken();
		    	String resId = stList.nextToken();
		    	String strValue = stList.nextToken();
		    	String strIniSlot = stList.nextToken();
				String strFinalSlot = stList.nextToken();
				String sType = stList.nextToken();
				int type = 0;
				if (!sType.trim().equals("")){
				    type = Integer.parseInt(sType);
				}
				int initialSlot = Util.getInt(strIniSlot);
				int finalSlot = Util.getInt(strFinalSlot);
				if (initialSlot<1 || finalSlot<1 || initialSlot>finalSlot) {
					System.out.println("ERR: Invalid values of Initial or Final slots of AllocUnit of Job: " + jobId); //debug
				} else {
					for(int s=initialSlot; s<=finalSlot; s++){
						AllocUnit au = new AllocUnit(jobId, Float.parseFloat(strValue), s, type);
						resmg.addAllocUnit(resId, jobId, au);
					}
				}
			}	    	
	    }
  	}


  	/**
  	 * Get all values of timeline from PARAM applet tag.
  	 */ 	
  	private void loadTimeLineValues(){

		//get the number of units (slots) of gantt
	    String sSlots = getParameter(TimeLine.SLOTS);

		//get the start date for gantt
	    String sDate = getParameter(TimeLine.INITIAL_DATE);

		//get time settings
	    String tUnit = getParameter(TimeLine.TIME_UNIT);
	    String sSize = getParameter(TimeLine.SLOT_SIZE);
	    String pSize = getParameter(TimeLine.PARENT_SLOT_SIZE);

		//get the current country (opional) and Language (optional)
	    String ctr = getParameter(COUNTRY);
	    String lang = getParameter(LANGUAGE);
	    
	    this.loadBundle(lang, ctr);
		
	    //create a Time Line object with all necessary parameters
		this.timeln = new TimeLine(sSlots, sDate, this.resmg.getSlotWidth(), 
		        					tUnit, sSize, pSize, lang, ctr);
  		add(this.timeln);

		//get format of gantt time (optional) 
	    this.timeln.setSlotDateFormat(getParameter(TimeLine.SLOT_DATE_FORMAT));
	    this.timeln.setParentDateFormat(getParameter(TimeLine.PARENT_DATE_FORMAT));
  		
	    //if there are more than 100 slots, adjust the max of horizontal scroll
	    if (this.timeln.getNumSlots()>100){
	        hScroll.setMaximum(this.timeln.getNumSlots());    
	    }
  	}

  	
  	/**
  	 * Load the bundle based on locale
  	 */
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

  	/**
  	 * Get all values of dependeces from PARAM applet tag.
  	 */ 	  	
  	private void loadDependenceValues(){
  		
		//get the number of dependeces of gantt (optional)
	    int numDeps = Util.getInt(getParameter(DependenceArrow.DEP_NUM));
	    if (numDeps>0) {

		    //get data of each Dependece occurrence
		    for (int i=1; i <= numDeps; i++) {
		    	String c = getParameter(DependenceArrow.DEP + i);
		    	if (c==null)
		    		System.out.println("ERR: error parsing Dependence: " + i); //debug
		    	else
		    		this.resmg.parseDependence(c);
		    }
		}
  	}  	
  	
  	
  	/**
  	 * Get all values of layer from PARAM applet tag.
  	 */
  	private void loadLayerValues(){
  		
		//get the number of layers 
	    int numLayers = Util.getInt(getParameter(Layer.LAYER_NUM));
	    if (numLayers>0) {

		    //get data of each Dependece occurrence
		    for (int i=1; i<=numLayers; i++) {
		    	String c = getParameter(Layer.LAYER + i);
		    	if (c==null)
		    		System.out.println("ERR: error parsing Layers: " + i); //debug
		    	else {
		    		Layer lay = new Layer(c);
		    		if (this.htLayers==null) {
		    			htLayers = new HashMap();
		    		}
		    		this.htLayers.put(lay.getId(), lay);
		    	}
		    }
		}
  	}  	


  	/**
  	 * Get all values of toolbar from PARAM applet tag.
  	 */  	
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
  	
  	private void loadFilterValues(){
  		HashMap htFilter = null;
  		
	    int numfilters = Util.getInt(getParameter(Filter.FILTER_NUM));
	    if (numfilters>0) {
		    for (int i=1; i <= numfilters; i++) {
		    	String c = getParameter(Filter.FILTER + i);
		    	if (c==null) {
		    		System.out.println("ERR: error parsing Filter: " + i); //debug
		    	} else {
		    		Filter f = new Filter(c);
		    		if (this.vFilter==null) {
		    			this.vFilter = new Vector();
		    			htFilter = new HashMap();
		    		}
		    		htFilter.put(f.getId(), f);
		    		this.vFilter.add(f);		    		
		    	}
		    }
		    
		    int numfilLayer = Util.getInt(getParameter(FilterLayer.FILTER_LAYER_NUM));
		    if (numfilLayer>0) {
			    for (int i=1; i <= numfilLayer; i++) {
			    	String c = getParameter(FilterLayer.FILAYER + i);
			    	if (c==null) {
			    		System.out.println("ERR: error parsing Filter Layer: " + i); //debug
			    	} else {
			    		FilterLayer fla = new FilterLayer(c);
			    		Filter f = (Filter)htFilter.get(fla.getFilterId());
			    		Layer lay = (Layer)this.htLayers.get(fla.getLayerId());
			    		fla.setLayer(lay);
			    		f.addFilterLayer(fla);
			    	}
			    }
			}		    
		}	    	    
  	}
  	
  	/**
  	 * Assembly the panels of noWorkingDays information
  	 */  	
  	private void loadNoWorkingDaysValues(){
	    Calendar c = Calendar.getInstance();
	    TimeGantt tg = new TimeGantt(this.timeln.getStartDate());

	    if (this.timeln.getSlotDateFormat().equals("E")){	    
	        for(int i=1; i<this.timeln.getNumSlots();i++){ 	        
  	           tg.addSlot(1);  	           
		       c.setTimeInMillis(tg.getTimeInMillis());
		       if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {	           
		           JLabel p = new JLabel();
		           this.jobAreaPanel.add(p);
		           if(this.noWorkingDaysPanels==null){
		               this.noWorkingDaysPanels = new Vector();
		           }
		           this.noWorkingDaysPanels.add(p);
		       }
  	        }
  	    }    
  	}

  	/**
  	 * Get all values of Menu from PARAM applet tag.
  	 */  	
  	private void loadMenuValues(){
		MenuGantt mg = null;
		
		//get the number of menus of gantt (optional)
	    int numMenus = Util.getInt(getParameter(MenuGantt.MENU_NUM));
	    
	    if (numMenus>0) {

		    //get data of each gantt Menu
		    for (int i=1; i <= numMenus; i++) {
		    	String c = getParameter(MenuGantt.MENU + i);
		    	if (c==null)
		    		System.out.println("ERR: error parsing Menu: " + i); //debug
		    	else{   		
		    		
		    		//create a new menu item gantt
		    		if (this.htMenu==null) this.htMenu = new HashMap();		    		
		    		MenuItemGantt mig = new MenuItemGantt(c);

		    		//verify if exists some 'menu gantt' for the specific context
		    		try {
		    			mg = null;
			    		mg = (MenuGantt)this.htMenu.get(mig.getContext());
		    		}catch(Exception e){
		    			//do nothing!
		    			System.out.println("");
		    		}
		    		if (mg==null) {
		    			//don't exists a Menu of a context into the hash...Create it!
			    		mg = new MenuGantt();
			    		add(mg);
			    		this.htMenu.put(mig.getContext(), mg);
		    		}
		    		
		    		//add a menu item gantt into a menu gantt
		    		if (mig.getTarget().equals(MenuItemGantt.SEPARATOR)){
		    			mg.addSeparator();
		    		} else if (mig.getTarget().equals(MenuItemGantt.ALLOC_LAYER_TARGET)){
			    	    createAllocLayerMenus(mg);
		    		} else {
			    		mg.add(mig);			    		
		    		}
		    	}
		    }
		}
  	}

  	
  	/**
  	 * In case of Layer Allocation target, create new SUB Menus Item 
  	 * (second level cascade menu) and insert into current menu item.
  	 * @param parent
  	 */
  	private void createAllocLayerMenus(MenuGantt parent){
  	    Iterator i = this.htLayers.values().iterator();
  	    while(i.hasNext()){
  	        Layer ay = (Layer)i.next();
  	        MenuCheckItemGantt subCheck = new MenuCheckItemGantt(ay.getName());
  	        parent.add(subCheck);
  	    }
  	}


  	/**
  	 * Recalc main panels of gantt based on new scroll bars positions
  	 */
	private void recalcFromScrollBar() {
				
	    //recalc scroll values
	    float widthFator = (-(float)(this.jobAreaPanel.getWidth() - this.width +
	                  this.resmg.getWidth() + SCROLL_BAR_THICK) / (hScroll.getMaximum()/2))
	                  * hScroll.getValue();
	    float heightFactor = (-(float)(this.jobAreaPanel.getHeight() - this.height +
	                  this.timeln.getY() + this.timeln.getHeight() + SCROLL_BAR_THICK) /
	                  (this.vScroll.getMaximum() -this.vScroll.getVisibleAmount())) * vScroll.getValue();

	    //reset Panels position
	    this.jobAreaPanel.setLocation((int)widthFator + this.resmg.getWidth(),
	                                (int)heightFactor + this.timeln.getY() + this.timeln.getHeight());
	    this.timeln.setLocation(this.jobAreaPanel.getX(), ToolBox.TOOL_BOX_HEIGHT);
	    this.resmg.setLocation(0, (int)heightFactor + TimeLine.TIME_LINE_HEIGHT + this.timeln.getY() + 1);
	}
	
	
	public void adjustmentValueChanged(AdjustmentEvent event) {
		if (event.getSource().equals(hScroll) || event.getSource().equals(vScroll)) {
			recalcFromScrollBar();
		}
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent event) {
        int barX = resmg.getCurrentWidth() + event.getX();
        if (barX>200 && barX<500) {
            resmg.setCurrentWidth(resmg.getCurrentWidth() + event.getX());
        	this.repaint();            
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent event) {}
    
    
	/**
	 * Return the width of Gantt applet.
	 * @return
	 */
  	public int getGanttWidth() {
  		return this.timeln.getWidth();
  	}

  	/**
  	 * Return the width (in pixels) of a gantt slot.
  	 * @return
  	 */
	public int getSlotsWidth() {
  		return this.timeln.getSlotsWidth();
	}

	/**
	 * Return the total number of slots of Gantt.
	 * @return
	 */
	public int getNumSlots() {
  		return this.timeln.getNumSlots();
	}

	/**
	 * Return the start date of Gantt.
	 * @return
	 */
	public TimeGantt getStartDate() {
  		return this.timeln.getStartDate();
	}	
	
	/**
	 * Set current resource selected into gantt.
	 * @param newValue
	 */
	public void setSelectedResource(Resource newValue) {

	    //hide current decoration
		Resource sr = redimCursor.getSelectedResource();
		if (sr!=null && newValue!=sr) {
			sr.setSelectionDecorate(false);
		}

		//set new selected resource
		redimCursor.setSelectedResource(newValue);
		newValue.setSelectionDecorate(true);
	}
	
	/**
	 * Set current job selected into gantt.
	 * @param newValue
	 */
	public void setSelectedJob(Job newValue) {

	    if (this.isEditable){
		    //hide current decoration
			Job sj = redimCursor.getSelectedJob();
			if (sj!=null && newValue!=sj) {
				sj.setSelectionDecorate(false);
			}
		
			//set new selected job
			redimCursor.setSelectedJob(newValue);
			newValue.setSelectionDecorate(true);
			
			//select resource related with selected job
			Resource res = this.resmg.getResource(newValue.getResourceId());
			this.setSelectedResource(res);	        
	    } else {
	        redimCursor.setSelectedJob(null);
	    }
	}
	
	/**
	 * Set current redim mark visible/unvisible
	 */
	public void setVisibleSelectionMark(boolean isVisible){
		if (isVisible){
			this.redimCursor.paint();
		}
		this.redimCursor.setVisible(isVisible);
	}
  	
  	/**
  	 * Ask to Gantt increase or reduce the zoom settings
  	 */
  	public void setZoom(boolean isIncrease){
  		int currentSlotWidth = this.resmg.getSlotWidth();
  		int currentRowHeight = this.resmg.getRowHeight();
  		if (isIncrease){
  			currentSlotWidth+=5;
  			currentRowHeight+=5;
  		} else {
  			currentSlotWidth-=5;
  			currentRowHeight-=5;  			
  		}
  		
  		//set limits (min and max) for the job size
  		if (currentSlotWidth>5 && currentSlotWidth<50){
  			this.setVisibleSelectionMark(false);
	  		this.resmg.setSlotWidth(currentSlotWidth);
  			this.timeln.setSlotsWidth(currentSlotWidth);
  			this.resmg.setRowHeight(currentRowHeight);
  			this.resmg.repaint();
  			this.timeln.repaint();
  			this.refreshNoWorkingDaysPanels();
  			
	  		this.vScroll.setEnabled((this.resmg.getResourcesHeight()>this.vScroll.getHeight()));
      		this.hScroll.setEnabled((this.getGanttWidth()>this.hScroll.getWidth()));
  		}
  	}

	/**
	 * Ask to Gantt to show the tool tip text box on a new location
	 */
  	public void setToolTipText(int cursorX, int cursorY, Component source, ToolTipText tt){
  		
  		if (source!=null && tt!=null) {
	  		this.tiptxt.setContent(tt.getContent());
	  			  		
	  		int x = source.getLocationOnScreen().x + cursorX - this.getLocationOnScreen().x;
	  		int y = source.getLocationOnScreen().y + cursorY - this.getLocationOnScreen().y + 20;

	  		if (this.getSize().width < ( x + this.tiptxt.getSize().width )) {
	  			x = this.getSize().width - this.tiptxt.getSize().width;
	  		}

	  		if (this.getSize().height < ( y + this.tiptxt.getSize().height )) {
	  			y = this.getSize().height - this.tiptxt.getSize().height;
	  		}

	  		this.tiptxt.setLocation(x, y); 	  				  			  		 
	  		this.tiptxt.setSize(230, 10);//Note: the height value (10) don't matter because the tooltip panel recalculate the height of panel
  			this.tiptxt.setVisible(true);
  			
  		} else {
  			//hide Tool Tip Text !!
  			this.tiptxt.setVisible(false);
  		}
  	}
	
  	/**
  	 * Ask to Gantt to change the position of selected Resource. 
  	 * @param isUp
  	 */
  	public void changeSelectedResourcePosition(boolean isUp){
  	    this.resmg.changeResourcePosition(redimCursor.getSelectedResource(), isUp);
  	}
  	
	/**
	 * Ask to Gantt to show a specific Menu Popup on a new location
	 */
	public void showMenuGantt(String context, Component source, 
							  Object handler, int x, int y){
		MenuGantt popup = (MenuGantt)this.htMenu.get(context);
		if (popup != null){
			
			//get each Menu Item of current Popup Menu...
			for (int i=0; i<popup.getItemCount(); i++){
				MenuItem mi = popup.getItem(i);
				
				//clear listener of Mouse Item
				for (int j=0; j<mi.getActionListeners().length; j++){
					mi.removeActionListener(mi.getActionListeners()[j]);
				}

				//create a listener into a specific component (job, resource, etc)
				//to link the event (click) dispatched by Menu Item 
				if (context.equals(MenuGantt.JOB_AREA)){
				    //TODO Quando exibe menu no job precisa setar check box de layer relacionado!
					mi.addActionListener((Job)handler);
				} else if (context.equals(MenuGantt.GANTT_AREA)){
					//TODO: must be implemented!	
					//mi.addActionListener((xxx)handler);
				} else if (context.equals(MenuGantt.RESOURCE_AREA)){
				    mi.addActionListener((Resource)handler);
				}
			}
			
			popup.show(source, x, y);
		}
	}

	
	/** 
	 * Get a Resource object from id
	 */
	public Resource getResource(String id){
		return this.resmg.getResource(id);
	}

	public Panel getJobAreaPanel(){
		return this.jobAreaPanel;
	}
	
    /**
     * Return a list of Job objects that was changed into resources. 
     * @return
     */
    public Vector getChangedJobs(){
        return this.resmg.getChangedJobs();
    }

    /**
     * Clear the information of 'changed objects' into resources  
     */
    public void clearChangedJobs() {
        this.resmg.clearChangedJobs();
    }    
    
    /**
     * Get the content into bundle based on key
     * @param key
     * @return
     */
    public String getMessage(String key) {
        return this.bundle.getString(key);
    }
	
    
	//////////////////////////////////////////
	public boolean isEditable() {
		return isEditable;
	}

	//////////////////////////////////////////	
    public String getPassword() {
        return password;
    }
    
	//////////////////////////////////////////    
    public String getUsername() {
        return username;
    }

	//////////////////////////////////////////    
    public String getUri() {
        return uri;
    }

	/////////////////////////////////////////       
	public String getVisibleLayerId() {
		return visibleLayerId;
	}
	public void setVisibleLayerId(String newValue) {
		this.visibleLayerId = newValue;
	}
	
	/////////////////////////////////////////	
	public HashMap getLayerHash(){
		return htLayers;
	}
	
	/////////////////////////////////////////	
	public Vector getFilterVector() {
		return this.vFilter;
	}
    
}