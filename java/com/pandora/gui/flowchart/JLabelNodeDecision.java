package com.pandora.gui.flowchart;

import java.awt.Graphics;

import com.pandora.DecisionNodeTemplateTO;
import com.pandora.helper.WorkflowUtil;

public class JLabelNodeDecision extends JLabelNode {

	private static final long serialVersionUID = 1L;
	
  	public void paint(Graphics g) {
  		DecisionNodeTemplateTO node = new DecisionNodeTemplateTO("");
  		node.setQuestionContent("Pergunta ??");
  		
		int width = this.getWidth();

		nodeHeight = WorkflowUtil.paintDecision(g, (width/2), 0, node, "Yes", "No");
  	}
  	

}
