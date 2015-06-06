package com.pandora.gui.taglib.form;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

import com.pandora.ResourceDateAllocTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.ResTaskForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;

public class TaskGridCell extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String slot;
    
    private String property;    
    
    private String line;
    
    
    
    public int doStartTag() {
        String buff = "&nbsp;";
        
        try {
            JspWriter out = pageContext.getOut();
            ResTaskForm frm = (ResTaskForm)pageContext.getSession().getAttribute(this.getName());

            if (frm==null) { 
                buff = "";
            } else {
                String hourLabel = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, "label.hour", null);
                UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
                
            	boolean isDisable = line.equalsIgnoreCase("1") || line.equalsIgnoreCase("3");
            	int slotIndex = Integer.parseInt(slot) - 1;
        		String value = this.getValueForSlot(frm, slotIndex, uto);
        		
        		if (this.isTodaySlot(frm, slotIndex) && !isDisable) {
        			buff = "<td class=\"tableCellHeaderHighlight\">";	
        		} else if (this.isWeekEndSlot(frm, slotIndex, isDisable)) {
        			buff = "<td class=\"tableCellHeader\">";	
        		} else {
        			buff = "<td class=\"formBody\">";
        		}
        		
        		String styleClass = "";
        		boolean alloc = this.isAllocatedSlot(frm, slotIndex, isDisable, value);
        		if (alloc && isDisable) {
        			styleClass = "textBoxDisabledAlloc";
        		} else if (!alloc && isDisable) {
        			styleClass = "textBoxDisabled";
        		} else if (alloc && !isDisable) {
        			styleClass = "textBoxAlloc";
        		} else {
        			styleClass = "textBox";
        		}
        		
                buff = buff + "\n" + HtmlUtil.getTextBox(this.property, value, isDisable, null, 5, 2, "text", styleClass);
                buff = buff + "&nbsp;" + hourLabel + "\n";
                
                if (isDisable) {
                	buff = buff + "<input type=\"hidden\" name=\"" + this.property + "\" value=\"" + value + "\">\n";	
                }
                buff = buff + "</td>\n";
            }
            
            out.println(buff);
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "TaskGridCell tag lib error", e);
        }
            
        return SKIP_BODY;
    }    

    
	private boolean isAllocatedSlot(ResTaskForm frm, int slotIndex, boolean isTop, String value){
	    boolean isAllocated = false;
		if (value!=null && !value.trim().equals("0") && 
				!value.trim().equals("-") && !value.trim().equals("") && !line.equals("3")) {
			isAllocated = true;
		}
	    return isAllocated;	    
	}	
    

	private boolean isWeekEndSlot(ResTaskForm frm, int slotIndex, boolean isTop){
	    boolean isWeekEnd = false;
	    
	    Timestamp currDate = null;
	    Locale loc =  frm.getCurrentUser().getLocale();
	    if (frm.getActualDate()!=null) {
	        currDate = frm.getDate(false, loc);    
	    } else {
	        currDate = frm.getDate(true, loc);
	    }
	    
		currDate = DateUtil.getChangedDate(currDate, Calendar.DATE, slotIndex + (frm.getAllocCursor()-1));
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currDate.getTime());
		int currentWeekDay = c.get(Calendar.DAY_OF_WEEK);
		if (currentWeekDay==Calendar.SATURDAY || currentWeekDay==Calendar.SUNDAY){
			isWeekEnd = true;
		}
		
		return isWeekEnd;
	}	
	
	private boolean isTodaySlot(ResTaskForm frm, int slotIndex){
	    boolean isToday = false;
	    String comparative_format = "yyyy-MM-dd";

	    Locale loc =  frm.getCurrentUser().getLocale();
	    Timestamp currDate = null;
	    if (frm.getActualDate()!=null) {
	        currDate = frm.getDate(false, loc);    
	    } else {
	        currDate = frm.getDate(true, loc);
	    }
		
		currDate = DateUtil.getChangedDate(currDate, Calendar.DATE, slotIndex + (frm.getAllocCursor()-1));
		String strCurrDate = DateUtil.getDate(currDate, comparative_format, loc);
		String strNow = DateUtil.getDate(DateUtil.getNow(), comparative_format, loc);
		if (strCurrDate.equals(strNow)) {
			isToday = true;
		}
		
		return isToday;
	}	
	
    /**
     * Return the correct value to be displayed into Allocation time grid
     */
	private String getValueForSlot(ResTaskForm frm, int slotIndex, UserTO uto){
	    String response = "";
	    Locale loc =  uto.getLocale();
	    String mask = uto.getCalendarMask();
	    Timestamp currDate = null;
	    if (frm.getActualDate()!=null) {
	        currDate = DateUtil.getDateTime(frm.getActualDate(), mask, loc);    
	    } else {
	        currDate = DateUtil.getDateTime(frm.getEstimDate(), mask, loc);
	    }
	    
        currDate = DateUtil.getChangedDate(currDate, Calendar.DATE, slotIndex + (frm.getAllocCursor()-1));
	    
	    if(line.equals("3")){
	    	Iterator<ResourceDateAllocTO> itDateAllocList = frm.getDateAllocTimeList().iterator();
	    	while(itDateAllocList.hasNext()){
	    		ResourceDateAllocTO rdato = itDateAllocList.next();
	    		if(rdato.getDate().equals(currDate)){
	    			if (!frm.isDecimalInput()) {
	            		response = rdato.getAllocTimeInTimeFormat(loc);
	            	} else {
	            		response = rdato.getAllocTimeInHours(loc);
	            	}
	    			break;
	    		}
	    	}
	    }else{
		    HashMap<String, ResourceTaskAllocTO> allocation = new HashMap<String, ResourceTaskAllocTO>();
		    if (line.equals("1")) {
		    	allocation = frm.getEstimAllocList();
		    } else  if (line.equals("2")){
		    	allocation = frm.getAllocationList();
		    }
		    
		    if (allocation!=null) {
	            String key = DateUtil.getDate(currDate, mask, loc);
	            ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)allocation.get(key);
	            if (rtato!=null && rtato.getAllocTime()!=null) {
	            	if (!frm.isDecimalInput()) {
	            		response = rtato.getAllocTimeInTimeFormat(loc);
	            	} else {
	            		response = rtato.getAllocTimeInHours(loc);
	            	}
	                    
	            } else {
	                response = "0";
	            }
		    }
	    }
	    return response;
	}

	
    ///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ///////////////////////////////////////////////    
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}    
    
	
    ///////////////////////////////////////////////    
	public String getSlot() {
		return slot;
	}
	public void setSlot(String newValue) {
		this.slot = newValue;
	}


	///////////////////////////////////////////////   	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}


	
}
