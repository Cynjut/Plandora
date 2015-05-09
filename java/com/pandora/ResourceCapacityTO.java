package com.pandora;

import java.sql.Timestamp;
import java.util.Calendar;

import com.pandora.helper.DateUtil;

public class ResourceCapacityTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String resourceId;
	
	private String projectId;
	
	private Integer year;
	
	private Integer month;
	
	private Integer day;
	
	private Integer capacity;

    /** Cost (in cent$) of resource per worked hour */
    private Integer costPerHour;
    
	
	public ResourceCapacityTO(){
	}
	
	public ResourceCapacityTO(ResourceCapacityTO rcto){
		this.setCapacity(rcto.getCapacity());
		this.setCostPerHour(rcto.getCostPerHour());
		this.setDay(rcto.getDay());
		this.setMonth(rcto.getMonth());
		this.setProjectId(rcto.getProjectId());
		this.setResourceId(rcto.getResourceId());
		this.setYear(rcto.getYear());
	}
	
	public ResourceCapacityTO(ResourceTO rto){
		initObject(rto, DateUtil.getNow());
	}

	public ResourceCapacityTO(ResourceTO rto, Timestamp cursorDate){
		initObject(rto, cursorDate);
	}
	
	private void initObject(ResourceTO rto, Timestamp cursorDate){
		this.resourceId = rto.getId();
		this.projectId = rto.getProject().getId();
		this.capacity = rto.getCapacityPerDay(null);

		//default value...
		this.setYear(new Integer(DateUtil.get(cursorDate, Calendar.YEAR)));
		this.setMonth(new Integer(DateUtil.get(cursorDate, Calendar.MONTH)+1));
		this.setDay(new Integer(DateUtil.get(cursorDate, Calendar.DATE)));
	}
	
	public boolean isToday() {
		int y = DateUtil.get(DateUtil.getNow(), Calendar.YEAR);
		int m = DateUtil.get(DateUtil.getNow(), Calendar.MONTH)+1;
		int d = DateUtil.get(DateUtil.getNow(), Calendar.DATE);
		return (this.year.intValue()==y && this.month.intValue()==m && this.day.intValue()==d);
	}

	public Timestamp getDate(){
		return DateUtil.getDateTime(this.day.toString(), (this.month.intValue()-1)+"", this.year.toString(), "0", "0", "0");
	}
	
	/*
    public String getStringCostPerHour(Locale loc) {
        String resp = "";
        if (costPerHour!=null){
        	float cost = costPerHour.floatValue();
            return StringUtil.getFloatToString(cost/100, loc);
        }
        return resp;
    }
	*/
	
	//////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}

	//////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	//////////////////////////////////////
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer newValue) {
		this.year = newValue;
	}

	//////////////////////////////////////
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer newValue) {
		this.month = newValue;
	}

	//////////////////////////////////////
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer newValue) {
		this.day = newValue;
	}

	//////////////////////////////////////
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer newValue) {
		this.capacity = newValue;
	}

	//////////////////////////////////////
	public Integer getCostPerHour() {
		return costPerHour;
	}
	public void setCostPerHour(Integer newValue) {
		this.costPerHour = newValue;
	}

}
