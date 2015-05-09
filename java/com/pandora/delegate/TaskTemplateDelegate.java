package com.pandora.delegate;

import java.awt.image.BufferedImage;
import java.util.Vector;

import com.pandora.CustomNodeTemplateTO;
import com.pandora.NodeTemplateTO;
import com.pandora.TemplateTO;
import com.pandora.UserTO;
import com.pandora.bus.TaskNodeTemplateBUS;
import com.pandora.bus.TaskTemplateBUS;
import com.pandora.exception.BusinessException;

public class TaskTemplateDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private TaskTemplateBUS bus = new TaskTemplateBUS();
    private TaskNodeTemplateBUS tnbus = new TaskNodeTemplateBUS();

    
    public Vector getTemplateListByProject(String projectId, boolean onCascade) throws BusinessException{
        return bus.getTemplateListByProject(projectId, onCascade);
    }

    public TemplateTO getTaskTemplate(String templateId) throws BusinessException{
        return bus.getTaskTemplate(templateId);
    }

    public TemplateTO getTaskTemplateByInstance(String instanceId) throws BusinessException{
        return bus.getTaskTemplateByInstance(instanceId);
    }
    
	public void saveWorkflow(String nodeTemplateId, Integer instanceId, String planningId, UserTO createBy) throws Exception{
		tnbus.saveWorkflow(nodeTemplateId, instanceId, planningId, createBy);
	}

    public NodeTemplateTO getNodeTemplateTree(NodeTemplateTO node, String planningId) throws BusinessException {
    	return tnbus.getNodeTemplateTree(node, planningId);
    }
    
    public Vector getNodeListByTemplate(String templateId, String instanceId, String planningId) throws BusinessException{
        return tnbus.getNodeListByTemplate(templateId, instanceId, planningId);
    }
    
	public BufferedImage drawWorkFlow(NodeTemplateTO root, UserTO uto, String bgcolor, boolean showNodeStatus) throws BusinessException{
		return tnbus.drawWorkFlow(root, uto, bgcolor, showNodeStatus);
	}

	public void saveCustomNodeTemplate(CustomNodeTemplateTO cntto) throws BusinessException{
		tnbus.saveCustomNodeTemplate(cntto);
	}

	public String getResourceListFromVector(Vector resList) throws Exception {
		return tnbus.getResourceListFromVector(resList);
	}

	public Vector getResourceListFromString(String resource, UserTO handler) throws Exception {
		return tnbus.getResourceListFromString(resource, handler);
	}

	public NodeTemplateTO getNodeTemplate(NodeTemplateTO filter, String instanceId, String planningId) throws BusinessException {
		return tnbus.getNodeTemplate(filter, instanceId, planningId);
	}

	public Integer getInstance(String templateId, String planningId) throws BusinessException {
		return tnbus.getInstance(templateId, planningId);
	}
}
