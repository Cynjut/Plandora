package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.TransferObject;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public final class IterationBurndownChartGadget extends ChartGadget {
	
	public static final String ITERATION_BURNDOWN_PROJECT  = "PROJECT";
	public static final String ITERATION_BURNDOWN_CATEGORY = "CATEGORY";

    private float[][] valBar = null;
    private float[][] valLine = null;

	
	public String getUniqueName(){
		return "label.manageOption.gadget.iterationBurndown";
	}
	
	public String getId(){
		return "ITERATION_BURNDOWN_CHART";
	}
	
	public String getImgLogo() {
		return "../images/gdglogo-6.png";
	}	

	public String getCategory() {
		return "label.manageOption.gadget.agile";
	}
	
	public String getDescription() {
		return "label.manageOption.gadget.burndown.desc";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(ITERATION_BURNDOWN_PROJECT, "-1"));
       	response.add(new TransferObject(ITERATION_BURNDOWN_CATEGORY, "-1"));
        return response;
    }
	
	@Override
    public Vector getFields(){
		return getFields(null);
    }
	
	@Override
	public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues) {
		Vector<FieldValueTO> response = new Vector<FieldValueTO>();
    	ProjectDelegate pdel = new ProjectDelegate();
    	CategoryDelegate cdel = new CategoryDelegate();

    	try {
        	Vector<ProjectTO> buff = pdel.getProjectListByUser(super.handler);
        	
        	Vector<TransferObject> projList = new Vector<TransferObject>();
        	TransferObject defaultOpt = new TransferObject("-1", "label.combo.select");
        	projList.addElement(defaultOpt);
        	if (buff!=null) {
        		projList.addAll(buff);
        	}
        	response.add(new FieldValueTO(ITERATION_BURNDOWN_PROJECT, "label.manageOption.gadget.burndown.project", projList));

        	
        	Vector<TransferObject> projCategory = new Vector<TransferObject>();
        	TransferObject defcat = new TransferObject("-1", "label.all2");
        	projCategory.addElement(defcat);
        	
        	ProjectTO pto = new ProjectTO(super.getSelected(ITERATION_BURNDOWN_PROJECT, currentValues));
        	Vector<CategoryTO> catlist = cdel.getCategoryListByType(CategoryTO.TYPE_REQUIREMENT, pto, false);
        	projCategory.addAll(catlist);
        	
        	response.add(new FieldValueTO(ITERATION_BURNDOWN_CATEGORY, "label.manageOption.gadget.burndown.category", projCategory));

    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }
    
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 160;
    }
    
	@Override
	public boolean canReloadFields() {
		return true;
	}	    
   
	public String generate(Vector selectedFields)  throws BusinessException {		
	    String response = null;
	    ProjectDelegate pdel = new ProjectDelegate();
        RequirementDelegate rdel = new RequirementDelegate();
        String xaxis[] = null;
        String barLabels[] = null;
        
        try {
            ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(ITERATION_BURNDOWN_PROJECT, selectedFields)), true);
            if (pto!=null) {
            	String categoryId = super.getSelected(ITERATION_BURNDOWN_CATEGORY, selectedFields);            	
                
                Vector<RequirementTO> allReqs = rdel.getThinListByProject(pto, categoryId);
                if (allReqs!=null) {
                    int remainingReqs = allReqs.size();
                    int createdReqs = 0;
                    int closedReqs = remainingReqs;
                    int barsUntilProjectEnd = 0;
                    String endLabel = super.getI18nMsg("label.manageOption.gadget.burndown.end");
                    String burnLabel = super.getI18nMsg("label.manageOption.gadget.burndown.label") + " " + pto.getName();
                    
                    Vector list = this.getIterations(pto);
                    this.initArray(list.size());
                    xaxis = new String[list.size()];
                    barLabels = new String[list.size()];
                    
                    int c = 0, itc = 1;
                    Iterator i = list.iterator();
                    while(i.hasNext()) {
                        Object[] item = (Object[])i.next();
                        if (item[0]!=null) {
                            Timestamp deadline = (Timestamp)item[0];
                            int[] reqsNum = this.getReqsByDeadLine(allReqs, deadline);
                            closedReqs = reqsNum[0];
                            createdReqs = reqsNum[1];
                            boolean isIteration = ((Boolean)item[2]).booleanValue();
                            xaxis[c] = (isIteration ?  "I" + (itc)  : endLabel); 
                            barLabels[c] = (String)item[1] + " : " + DateUtil.getDate(deadline, super.handler.getCalendarMask(), super.handler.getLocale());
                            
                            if (isIteration) {
                            	this.valLine[c][1]= remainingReqs - closedReqs;	
                            	itc ++;
                            } else {
                            	barsUntilProjectEnd = c + 1;
                            }
                            this.valBar[c][0] = createdReqs;
                            
                        } else {
                        	if (c>0) {
                        		boolean isIerationBeforeNow = ((Boolean)item[2]).booleanValue();
                        		if (isIerationBeforeNow) {
                        			this.valLine[c][1] = this.valLine[c-1][1];	
                        		}
                        	}
                            xaxis[c] = "";
                            barLabels[c] = "";
                        }
                        
                        c++;
                    }
                    
                    if (pto.getEstimatedClosureDate()!=null) {
                    	this.generateRefLine(remainingReqs, barsUntilProjectEnd);	
                    }

                    
            		//draw the bars
                    response = "{ \n" + 
                    	getJSonTitle(burnLabel) + "," +
                    	getJSonYLegend(" ") + "," +
                    	getBarStackValues(this.valBar, barLabels, this.valLine, null, new String[]{"c0c0c0", "800000"}, false) + "," +            	
                    	getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, this.valBar, this.valLine, "y_axis") + "}";            
                }
            }
            
        	//empty chart...
            if (response==null) {
                response = "{ \n" + 
            		getJSonTitle() + "," +
            		getJSonAxis(new String[1], null, "x_axis") + "," +
            		getJSonYLegend(" ") + "}";        	                
            }
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response;
	}

	
	private void generateRefLine(int remainingReqs, int size) {
		if (remainingReqs>0 && size>1) { // && remainingReqs>=size) {
			float reqs = (float)remainingReqs;
			float diff = (float)((float)remainingReqs / (float)(size-1)); 
			for (int i=0; i<size; i++) {
				this.valLine[i][0] = reqs;
				reqs = reqs - diff;				
			}
		}
	}

	
	private Vector getIterations(ProjectTO pto) throws BusinessException{
	    OccurrenceDelegate odel = new OccurrenceDelegate();
	    Vector response = new Vector();
	    Vector buff = new Vector();
	    
        Timestamp cursor = null;
        int gap = 1000000; //contain the small difference (in days) between two iterations
        
        Vector<OccurrenceTO> list = odel.getIterationListByProject(pto.getId(), true);
        if (list!=null) {
            Iterator<OccurrenceTO> i = list.iterator();
            while(i.hasNext()) {
                OccurrenceTO oto = i.next();
                
        		//check if iteration deadline exists and make sure that must be considered only iterations non-aborted and already started                
    			if (oto.getStatus()!=null && !oto.getStatus().equals(IterationOccurrence.STATE_FINAL_1) && !oto.getStatus().equals(IterationOccurrence.STATE_START)) {
                    Timestamp deadline = oto.getFieldDateByKey(IterationOccurrence.ITERATION_FINAL_DATE, new IterationOccurrence());
                    if (deadline!=null) {
                        Object[] item = new Object[4];
                        item[0] = deadline;
                        item[1] = oto.getName();
                        item[2] = new Boolean(true);
                        buff.addElement(item);
                        
                        if (cursor!=null) {
                            int between = DateUtil.getSlotBetweenDates(cursor, deadline);
                            if (between>0 && between<=gap) {
                                gap = between;
                            }
                        }
                        
                        cursor = deadline;                        
                    }
    			}
            }
            
            //if necessary, include the estimated project closure date into the list (at ordered position)
            if (pto.getEstimatedClosureDate()!=null) {
            	int z = 0;
            	String endLabel = super.getI18nMsg("label.projEstimClosure");
            	boolean lastOne = true;
                Iterator e = buff.iterator();
                while(e.hasNext()) {
                    Object[] item = (Object[])e.next();
                    Timestamp deadline = (Timestamp)item[0];
                    if (!pto.getEstimatedClosureDate().after(deadline)) {
                    	buff.add(z, new Object[] {pto.getEstimatedClosureDate(), endLabel, new Boolean(false)});
                    	lastOne = false;
                    	break;
                    }
                    z++;
                }
                
                if (lastOne) {
                	buff.addElement(new Object[] {pto.getEstimatedClosureDate(), endLabel, new Boolean(false)});
                }
            }

            
            cursor = null;
            Iterator j = buff.iterator();
            while(j.hasNext()) {
                Object[] item = (Object[])j.next();
                Timestamp deadline = (Timestamp)item[0];
                if (cursor!=null) {
                    int between = DateUtil.getSlotBetweenDates(cursor, deadline);
            	    for (int k=1; k<(int)(between/gap); k++) {
            	    	Object[] gapItem = new Object[3];
            	    	gapItem[2] = new Boolean(deadline.before(DateUtil.getNow()));
            	        response.addElement(gapItem); 
            	    }
            	    response.addElement(item);
            	    
                } else {
                    response.addElement(item);
                }
                cursor = deadline;
            }                    
        }
        
        return response;
	}
	

	
	private int[] getReqsByDeadLine(Vector allReqs, Timestamp deadline) {
	    int[] response = new int[2];
	    
	    Iterator i = allReqs.iterator();
	    while(i.hasNext()) {
	        RequirementTO rto = (RequirementTO)i.next();
	        if (rto.getFinalDate()!=null && rto.getFinalDate().before(deadline)) {
	            response[0]++;
	        }
	        
	        if (rto.getCreationDate().before(deadline)) {
	            response[1]++;
	        }
	    }
	    return response;
	}

	
	private void initArray(int size){
		
		if (size<=0) {
			size = 1;
		}
	
		this.valBar = new float[size][2];
		this.valLine = new float[size][2];
	
        for (int i=0; i<size; i++) {
        	this.valBar[i][0] = -1;
        	this.valBar[i][1] = -1;
        	this.valLine[i][0] = -1;
        	this.valLine[i][1] = -1;
        }
	}
	
}
