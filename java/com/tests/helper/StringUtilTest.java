package com.tests.helper;

import junit.framework.TestCase;

import com.pandora.helper.StringUtil;

public class StringUtilTest extends TestCase {

 	public void setUp() {
 	}
 	
 	public void tearDown() {}

 	public void testGetBase36(){
 		String content = StringUtil.getBase36(10);
 		assertTrue(StringUtil.checkChars(content, "0123456789ABCDEFGHIJKLMNOPQRSTUVXWYZ"));
 		
 		content = StringUtil.getBase36(1212321);
 		assertTrue(StringUtil.checkChars(content, "0123456789ABCDEFGHIJKLMNOPQRSTUVXWYZ"));
 		
 		content = StringUtil.getBase36(20110210);
 		assertTrue(StringUtil.checkChars(content, "0123456789ABCDEFGHIJKLMNOPQRSTUVXWYZ"));

 		content = StringUtil.getBase36(23203239);
 		assertTrue(StringUtil.checkChars(content, "0123456789ABCDEFGHIJKLMNOPQRSTUVXWYZ"));

 	}

}
