package com.tests.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestCase;

import com.pandora.helper.DateUtil;

/**
 * 
 */
public class DateHelperTest extends TestCase {

    private Locale brasil;
    private Locale usa;


 	public void setUp() {
	    this.brasil = new Locale("pt", "BR");
	    this.usa = new Locale("en", "US");
 	}
 	
 	public void tearDown() {}

 
 	public void testGetDate0(){
 		Calendar c = Calendar.getInstance();
 		Timestamp d1 = DateUtil.getDate( DateUtil.getDateTime("01", "2", "2005", "10", "30", "58"), true);
 		c.setTimeInMillis(d1.getTime());
 		assertTrue(c.get(Calendar.HOUR)==0);
 		assertTrue(c.get(Calendar.MINUTE)==0);
 		assertTrue(c.get(Calendar.SECOND)==0);
 		assertTrue(c.get(Calendar.MILLISECOND)==0);
 		assertTrue(c.get(Calendar.DATE)==1);
 		
 		Timestamp d2 = DateUtil.getDate( DateUtil.getDateTime("01", "2", "2005", "10", "30", "58"), false);
 		c.setTimeInMillis(d2.getTime());
 		assertTrue(c.get(Calendar.HOUR_OF_DAY)==23);
 		assertTrue(c.get(Calendar.MINUTE)==59);
 		assertTrue(c.get(Calendar.SECOND)==59);
 		assertTrue(c.get(Calendar.MILLISECOND)==999);
 		assertTrue(c.get(Calendar.DATE)==1); 		
 	}
 	
 	public void testGet() {
 		int i0 = DateUtil.get( DateUtil.getDateTime("5", "2", "2005", "21", "30", "58"), Calendar.HOUR_OF_DAY);
 		assertTrue(i0==21);
 		
 		int i1 = DateUtil.get( DateUtil.getDateTime("5", "2", "1999", "21", "30", "58"), Calendar.YEAR);
 		assertTrue(i1==1999);

 		int i2 = DateUtil.get( DateUtil.getDateTime("5", "2", "1999", "21", "30", "58"), Calendar.SECOND);
 		assertTrue(i2==58);

 		int i3 = DateUtil.get( null, Calendar.SECOND);
 		assertTrue(i3==-1);

 		int i4 = DateUtil.get(  DateUtil.getDateTime("5", "2", "1999", "21", "30", "58"), -1);
 		assertTrue(i4==-1);
 		
 		int i5 = DateUtil.get(  DateUtil.getDateTime("5", "2", "1999", "21", "30", "58"), 1000000);
 		assertTrue(i5==-1); 		
 		
 	}
 	
 	public void testGetNow(){
	    Object now = DateUtil.getNow();
	    assertTrue("is timestamp: ", (now instanceof Timestamp));
	    assertTrue("is year 2008: ", (DateUtil.get((Timestamp)now, Calendar.YEAR)==2008));
	}
	
	
	public void testGetDate2(){
	    Timestamp date1 = DateUtil.getDateTime("23/05/76", "dd/MM/yy", brasil);
	    assertTrue("1- is year 1976: ", (DateUtil.get(date1, Calendar.YEAR)==1976));
	    
	    Timestamp date2 = DateUtil.getDateTime("23/05/1976", "dd/MM/yyyy", brasil);
	    assertTrue("2- is year 1976: ", (DateUtil.get(date2, Calendar.YEAR)==1976));
	    
	    Timestamp date3 = DateUtil.getDateTime("23/05/1976", "dd/MM/yyyy", brasil);
	    assertTrue("3- is year 1976: ", (DateUtil.get(date3, Calendar.YEAR)==1976));
	    
	    Timestamp date4 = DateUtil.getDateTime("05/23/1976", "MM/dd/yyyy", usa);
	    assertTrue("4- is year 1976: ", (DateUtil.get(date4, Calendar.YEAR)==1976));
	    
	    Timestamp date5 = DateUtil.getDateTime("05/23/1976", "MM/dd/yyyy", usa);
	    assertTrue("5- is year 1976: ", (DateUtil.get(date5, Calendar.YEAR)==1976));
	    
	    Timestamp date6 = DateUtil.getDateTime("05/23/1976", "MM/dd/yyyy", usa);
	    assertTrue("6- is year 1976: ", (DateUtil.get(date6, Calendar.YEAR)==1976));
    }

	
	public void testGetDate3(){
	    Timestamp date1 = DateUtil.getDate("1", "1", "2001");
	    Calendar c = Calendar.getInstance();
	    c.setTimeInMillis(date1.getTime());
	    assertTrue("test 1- is day 1: ", (c.get(Calendar.DAY_OF_MONTH)==1));
	    assertTrue("test 1- is month 1: ", ((c.get(Calendar.MONTH)+1)==1));
	    assertTrue("test 1- is year 2001: ", (c.get(Calendar.YEAR)==2001));
	    
	    Timestamp date2 = DateUtil.getDate("11", "10", "1999");
	    c.setTimeInMillis(date2.getTime());
	    assertTrue("test 2- is day 11: ", (c.get(Calendar.DAY_OF_MONTH)==11));
	    assertTrue("test 2- is month 10: ", ((c.get(Calendar.MONTH)+1)==10));
	    assertTrue("test 2- is year 1999: ", (c.get(Calendar.YEAR)==1999));	    
	}
	
	
	public void testGetDateTime(){
	    Timestamp date1 = DateUtil.getDateTime("01", "2", "2005", "10", "30", "58");
	    Calendar c = Calendar.getInstance();
	    c.setTimeInMillis(date1.getTime());
	    assertTrue("test 1- is day 1: ", (c.get(Calendar.DAY_OF_MONTH)==1));
	    assertTrue("test 1- is hour 10: ", (c.get(Calendar.HOUR)==10));
	    assertTrue("test 1- is sec 58: ", (c.get(Calendar.SECOND)==58));	    
	    
	    Timestamp date2 = DateUtil.getDateTime("02", "12", "2005", "23", "59", "00");
	    c.setTimeInMillis(date2.getTime());	    
	    assertTrue("test 2- is month 12: ", ((c.get(Calendar.MONTH)+1)==12));
	    assertTrue("test 2- is minute 59: ", (c.get(Calendar.MINUTE)==59));
	    assertTrue("test 2- is hour 23: ", (c.get(Calendar.HOUR_OF_DAY)==23));	    

	}


	public void testGetSlotBetweenDates(){
	    Timestamp date1 = DateUtil.getDateTime("01/02/2005", "dd/MM/yyyy", brasil);
	    Timestamp date2 = DateUtil.getDateTime("03/01/2005", "dd/MM/yyyy", usa);
	    
	    int i = DateUtil.getSlotBetweenDates(date2, date1);
	    assertTrue("number slots:" + i, (i==0));
	    
	    int j = DateUtil.getSlotBetweenDates(date1, date2);
	    assertTrue("slots:" + j, (j==28));
	}

	
	public void testGetChangedDate(){
	}
    
	}
