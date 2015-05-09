// Title: Tigra Calendar
// URL: http://www.softcomplex.com/products/tigra_calendar/
// Version: 3.2 
// Date: 10/14/2002 (mm/dd/yyyy)
// Note: Permission given to use this script in ANY kind of applications if
//    header lines are left unchanged.
// Note: Script consists of two files: calendar?.js and calendar.html

// if two digit year input dates after this year considered 20 century.
var NUM_CENTYEAR = 30;

// is time input control required by default
var BUL_TIMECOMPONENT = false;

// are year scrolling buttons required by default
var BUL_YEARSCROLL = true;

var calendars = [];
var RE_NUM = /^\-?\d+$/;
var dateMask = 'dd/MM/yyyy'; //default value

function calendar1(obj_target, dtMask) {

	this.dateMask = dtMask;
	
	// assigning methods
	this.gen_date = cal_gen_date1;
	this.prs_date = cal_prs_date1;
	this.popup    = cal_popup1;

	// validate input parameters
	if (!obj_target)
		return cal_error("Error calling the calendar: no target control specified");
	if (obj_target.value == null)
		return cal_error("Error calling the calendar: parameter specified is not valid target control");
	this.target = obj_target;
	this.time_comp = BUL_TIMECOMPONENT;
	this.year_scroll = BUL_YEARSCROLL;
	
	// register in global collections
	this.id = calendars.length;
	calendars[this.id] = this;
}

function cal_popup1 (str_date) {

	if (str_date && str_date!=null && str_date!='') {
		str_date = formatDate(new Date(str_date), calendars[0].dateMask);
	}

	this.dt_current = cal_prs_date1(str_date ? str_date : this.target.value);
	if (!this.dt_current) return;

	var obj_calwindow = window.open(
		'../jscript/calendar.html?datetime=' + this.dt_current.valueOf()+ '&id=' + this.id,
		'Calendar', 'width=210,height='+(this.time_comp ? 205 : 180)+
		',status=no,resizable=no,top=200,left=200,dependent=yes,alwaysRaised=yes'
	);
	obj_calwindow.opener = window;
	obj_calwindow.focus();
}


// date generating function
function cal_gen_date1 (dt_datetime) {
	return formatDate(dt_datetime, calendars[0].dateMask);
}


// date parsing function
function cal_prs_date1 (str_date) {
	var dt_date = new Date();

	if (str_date == '') {
		//get current date...
		str_date = formatDate(dt_date, calendars[0].dateMask);
	}	
		
	if (! isDate (str_date, calendars[0].dateMask )) {
		return cal_error("Invalid date value: '" + str_date + "'.\nAllowed format is " + calendars[0].dateMask + ".");
	} else {
		dt_date = getDateFromFormat( str_date , calendars[0].dateMask )
	}
			
	return dt_date;
}

function cal_error (str_message) {
	alert (str_message);
	return null;
}
