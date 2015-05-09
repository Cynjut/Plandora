package com.pandora.gui.client;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class ThinClient {

	private static JFrame frame;
	
	private static ResourceBundle rb;

	
	public static void main(String[] args) {
		try {
			
	    	try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			rb = ResourceBundle.getBundle("ApplicationResources", new Locale("pt", "BR"));
			
			frame = new JFrame(rb.getString("thin.client.title"));
			
			frame.setSize(400,100);
			frame.setVisible(true);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
		
		
}
