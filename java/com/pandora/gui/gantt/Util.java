package com.pandora.gui.gantt;

public class Util {
	
	private Util(){
		//can not be instanced!
	}
	
	public static int getInt(String s){
		int response = 0;
		try {
			response = Integer.parseInt(s);
		} catch(Exception e){
			//no messages.
		}
		return response;
	}

	public static boolean getBoolean(String s){
		boolean response = false;
		try {
			response = (s.toUpperCase().equals("TRUE"));
		} catch(Exception e){
			//no messages.
		}
		return response;
	}
	

}
