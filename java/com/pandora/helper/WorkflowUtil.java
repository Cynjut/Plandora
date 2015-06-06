package com.pandora.helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.pandora.DecisionNodeTemplateTO;
import com.pandora.NodeTemplateTO;

public class WorkflowUtil {

	private static final int MAX_WIDTH_NODE = 100;
	
	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private WorkflowUtil(){}

	
	public static Font getDefaultFount(){
	    return new Font("sans serif", Font.PLAIN, 10);		
	}
	
	
	public static int paintSquare(Graphics g, int x, int y, NodeTemplateTO node, Color bgColor, String tag){
		int response = y;
		ArrayList<String> content = new ArrayList<String>();
		
		int maxWidth = crop(g, node.getName(), MAX_WIDTH_NODE, content) + 10;
		int maxHeight = content.size() * 15;
	
		int left = (int)(x - 20 - (maxWidth/2));
		int arrowTop = y + maxHeight + 10;
		
		//draw the shadow
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(left-8, y+2, (int)(maxWidth+57), (maxHeight+12), 20, 20 );		
		g.setColor(bgColor);
		g.fillRoundRect(left-10, y, (int)(maxWidth+55), (maxHeight+10), 20, 20 );
		g.setColor(Color.BLACK);
		
		g.drawRoundRect(left-10, y, (int)(maxWidth+55), (maxHeight+10), 20, 20 );
		
		//set the current coordinates of block
		node.setCoords((left-10) + "," + y + "," + (int)(maxWidth+45+left) + "," +(maxHeight+20+y));

		//paint multiline content
		paintMultilineText(g, content, x, y);
		
		//paint lateral tag
		g.drawLine(left+5, y, left+5, y + (maxHeight+10));			
		g.setFont(new Font("sans serif", Font.BOLD, 12));
		if (node.getGenericTag()!=null) {
			g.drawString(node.getGenericTag(), left-5, y + (maxHeight/2) + 12);			
		}
		g.setFont(getDefaultFount());

		if (tag!=null) {
			paintLateralTag(g, left + maxWidth +50 , y-4, tag);	
		}
				
		paintArrow(g, x, arrowTop);
		
		response = arrowTop + 30;
		
		return response;
	}
	

	public static int paintDecision(Graphics g, int x, int y, DecisionNodeTemplateTO node, String yesLabel, String noLabel){
		int response = y;
		ArrayList<String> content = new ArrayList<String>();
		String answer = node.getDecisionAnswer();
		
		g.drawLine(x-40, y+20, x, y);
		g.drawLine(x, y, x+40, y+20);
		g.drawLine(x+40, y+20, x, y+40);
		g.drawLine(x, y+40, x-40, y+20);
				
		//set the current coordinates of block
		node.setCoords((x-40) + "," + y + "," + (x+40) + "," +(y+40));

		crop(g, node.getQuestionContent(), 160, content);
		paintMultilineText(g, content, x+100, y-15);
		
		//draw 'Yes' label
		g.drawString(yesLabel, x+10, y+54);
		if (answer!=null && answer.equals(DecisionNodeTemplateTO.LABEL_YES)){
			Rectangle2D dimen = g.getFontMetrics().getStringBounds(yesLabel, g);
			g.drawRect(x+10, y+55, (int)dimen.getWidth(), 1);
		}

		//draw 'No' label
		g.drawString(noLabel, x+45, y+35);		
		if (answer!=null && answer.equals(DecisionNodeTemplateTO.LABEL_NO)){
			Rectangle2D dimen = g.getFontMetrics().getStringBounds(noLabel, g);
			g.drawRect(x+45, y+36, (int)dimen.getWidth(), 1);
		}
		
		//'yes' branch..
		//g.drawLine(x-40, y+20, x-70, y+20);
		//g.drawLine(x-80, y+30, x-80, y+40);
		//g.drawArc(x-80, y+20, 20, 20, 90, 90);
		//g.drawLine(x, y+40, x, y+40);
		paintArrow(g, x, y+40);
		
		//'no' branch..
		//g.drawLine(x+40, y+20, x+70, y+20);
		//g.drawLine(x+80, y+30, x+80, y+40);
		//g.drawArc(x+60, y+20, 20, 20, 0, 90);
		//paintArrow(g, x+80, y+40);
		g.drawLine(x+40, y+20, x+150, y+20);
		g.drawLine(x+160, y+30, x+160, y+40);
		g.drawArc(x+140, y+20, 20, 20, 0, 90);
		paintArrow(g, x+160, y+40);
		
		return response + 70;
	}

	
	public static void paintArrow(Graphics g, int x, int y){
		g.drawLine(x, y, x, y+28);
		g.drawLine(x, y+28, x-5, y+20);
		g.drawLine(x, y+28, x+5, y+20);
	}

	
	public static int paintEgde(Graphics g, int x, int y, boolean isStart, ImageIcon img){
		URL url = null;
		try {
			if(isStart) {
				if (img==null) {
					url = NodeTemplateTO.class.getResource("../../../../images/start-workflow.png");	
				}
				WorkflowUtil.paintArrow(g, x, y+20);				
			} else {
				if (img==null) {
					url = NodeTemplateTO.class.getResource("../../../../images/end-workflow.png");	
				}
			}
			
			if (img==null) {
				BufferedImage image = ImageIO.read(url);
				g.drawImage(image, x-9, y, null);				
			} else {
				g.drawImage(img.getImage(), x-9, y, null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return y+50;
	}

	
	private static void paintMultilineText(Graphics g, ArrayList<String> content, int x, int y){
		for (int i=0; i<content.size(); i++) {
			Rectangle2D dimen = g.getFontMetrics().getStringBounds((String)content.get(i), g);
			g.drawString((String)content.get(i), (int)(x - (dimen.getWidth()/2)), y + 17 + (i * 15));	
		}		
	}
	
	
	private static int crop(Graphics g, String rawContent, int maxWidth, ArrayList<String> content) {
		int response = maxWidth;
		
		if (rawContent!=null) {
			String[] words = rawContent.split(" ");
			
			int lineAcc = 0;
			String currentLine = "";

			for (int i =0; i<words.length; i++) {
				Rectangle2D dimen = g.getFontMetrics().getStringBounds(words[i], g);
				lineAcc += dimen.getWidth();
				
				if (lineAcc>maxWidth) {
					if (currentLine.trim().equals("")) {
						content.add(words[i]);
						response = (int)dimen.getWidth();
						lineAcc = 0;
					} else {
						content.add(currentLine);							
						currentLine = words[i] + " ";
						lineAcc = (int)dimen.getWidth();
					}
					
				} else {
					currentLine = currentLine.concat(words[i] + " ");
				}
			}
			
			//put the remaining (residual) string at last line...
			if (!currentLine.trim().equals("")) {
				content.add(currentLine);	
			}
		}
		return response;
	}
	
		
	private static void paintLateralTag(Graphics g, int x, int y, String tag){		
		g.setColor( Color.LIGHT_GRAY );
		g.fillRect(x-13, y, 15, 15);
		g.fillRect(x-2, y+4, 8, 15);
		
		g.setColor( Color.BLACK );
		g.drawRect(x-13, y, 15, 15);
		g.setFont(new Font("sans serif", Font.BOLD, 10));
		g.drawString(tag, x-8, y+8);		
		g.setFont(getDefaultFount());
	}

}

