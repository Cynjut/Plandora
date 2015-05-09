package com.pandora.gui.gantt;

import java.util.*;
import java.sql.Timestamp;
import java.text.*;

public class TimeGantt {

	private static final long DEFAULT_TIME_UNIT        = 60000;
	private static final long DEFAULT_SLOT_SIZE        = 1440;
	private static final long DEFAULT_PARENT_SLOT_SIZE = 10080;

	private long timeUnit       = DEFAULT_TIME_UNIT;
	private long slotSize       = DEFAULT_SLOT_SIZE;
	private long parentSlotSize = DEFAULT_PARENT_SLOT_SIZE;
	private long timeGantt = 0; //in minutes

	/**
	 * Constructor. <br> 
	 */
	public TimeGantt() {
		timeGantt = System.currentTimeMillis()/timeUnit;
	}

	public TimeGantt(TimeGantt tg) {
		this.timeGantt = tg.timeGantt;
		this.timeUnit = tg.timeUnit;
		this.slotSize = tg.slotSize;
		this.parentSlotSize = tg.parentSlotSize;
	}

	public TimeGantt(String date, DateFormat rf, Long tUnit, Long sSize, Long pSize){
		try {
			timeGantt = rf.parse(date).getTime()/timeUnit;
			
			//verify if is necessary to update the time settings
			if (tUnit!=null) this.timeUnit = tUnit.longValue();
			if (sSize!=null) this.slotSize = sSize.longValue();
			if (pSize!=null) this.parentSlotSize = pSize.longValue();
					
		} catch (Exception e) {
    		System.out.println(e.getMessage());
		}
	}
	

	public long getTimeGantt() {
		return timeGantt;
	}

	public long getSlotSize() {
		return this.slotSize;
	}

	public long getParentSlotSize() {
		return this.parentSlotSize;
	}

	/**
	 * Convert the current date/time of TimeGantt to Date representation using
	 *  the Brasillian locale "dd/mm/yyyy" 
	 */
	public String toString() {
	    DateFormat wf = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pt", "BR"));
    	return this.toString(wf);
	}

	public String toString(DateFormat wf) {
    	return wf.format(new Date(timeGantt*timeUnit));
	}

	public int getSlot() {
  		int result = -1;
     	long cont = timeGantt - (timeGantt/parentSlotSize) * parentSlotSize;
     	result = (int) (cont/slotSize) + 1;
	  	return result;
	}

	public int getSlotId() {
		return (int) (timeGantt/slotSize);
	}

	public void addSlot(int slot) {
		timeGantt += (slot * this.slotSize) ;
	}

	/**
	 * Return the current time gantt in millisec 
	 * @return
	 */
	public long getTimeInMillis() {
    	return timeGantt * timeUnit;
	}

	/**
	 * Return the current time gantt in Timestamp
	 * @return
	 */
	public Timestamp getTimestamp() {
    	return new Timestamp(this.getTimeInMillis());
	}	
}
