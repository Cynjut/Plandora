<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

    function refreshGrid(source){
    	with(document.forms["showAllTaskForm"]){
    		operation.value = "prepareForm";
    		submit();
        }
    }   

    function openTaskHistPopup(id) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=" + id + "&resourceId=";
		window.open(pathWindow, 'reqHist', 'width=740, height=250, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

	function callResourceTask(taskId, resId, projId){
		window.location = "../do/manageTask?operation=editTask&taskIdFromExternalForm=" + taskId + "&projectId=" + projId;	
	}
		
	function showWorkFlow(instId, planId){
		window.location = "../do/showAllTask?operation=showWorkFlow&instanceId=" + instId + "&planningId=" + planId + "&showWorkflowDiagram=on&bgcolor=F0F0F0";	
	}

	function clickNodeTemplate(instId, planId){
		with(document.forms["showAllTaskForm"]){
    		closeFloatPanel();		
   			operation.value = "clickNodeTemplate";
    		instanceId.value = instId;
    		planningId.value = planId;
   		}
		ajaxProcess(document.forms["showAllTaskForm"], callBackNodeTemplateClick, instId, planId);		
	}
	
				
	function callBackNodeTemplateClick(instId, planId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			openFloatPanel(content);   	
	    }  
	}
</script>

<html:form  action="showAllTask">
	<html:hidden name="showAllTaskForm" property="id"/>
	<html:hidden name="showAllTaskForm" property="operation"/>
	<html:hidden name="showAllTaskForm" property="projectRelated"/>		
	<html:hidden name="showAllTaskForm" property="showWorkflowDiagram"/>
	<html:hidden name="showAllTaskForm" property="instanceId"/>	
	<html:hidden name="showAllTaskForm" property="planningId"/>
		
	<br>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	  	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.showAllTaskForm"/>
	</display:headerfootergrid>

  	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="100">&nbsp;</td>
      <td width="250">&nbsp;</td>
      <td width="100">&nbsp;</td>      
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>		  	
    <tr class="formBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.manageTask.taskStatus"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="showAllTaskForm" property="statusSelected" styleClass="textBox">
			<html:options collection="statusList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td class="formTitle"><bean:message key="label.manageTask.projResource"/>:&nbsp;</td>      
      <td class="formBody">
	  	<html:select name="showAllTaskForm" property="resourceSelected" styleClass="textBox">
			<html:options collection="resourceList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>      
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>	    
	</table>  	
	
	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('TSK');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td width="30">&nbsp;</td>      
		  <td>&nbsp;</td>
		  <td width="100">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('TSK');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>      
		  <td width="100" align="right">
			<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('showAllTaskForm', 'backward');">
				<bean:message key="button.backward"/>
			</html:button>    
		  </td>      
		</tr></table>  	
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="label.showAllTaskForm.taskList"/>
	</display:headerfootergrid>
	    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="allResTaskList" scope="session" pagesize="<%=PreferenceTO.LIST_ALL_SHOW_PAGING%>" requestURI="../do/showAllTask?operation=navigate">
			      <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" />
			      <display:column width="2%" property="task.id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			      
				  <display:column sort="true" property="task.name" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.manageTask.name" />
				  <display:column sort="true" width="12%" align="center" property="task.project.name" description="label.requestProject" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_PROJECT%>" title="label.requestProject" />
				  <display:column sort="true" width="9%" property="resource.username" align="center" title="label.showAllTaskForm.resource" description="label.showAllTaskForm.resource.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_RESOURCE%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="showAllTaskForm"/>
				  <display:column sort="true" width="6%" property="id" align="center" title="label.resTaskForm.billable" description="label.showAllTaskForm.billable.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_BILLABLE%>" decorator="com.pandora.gui.taglib.decorator.TaskBillableDecorator" tag="EDIT"/>
				  <display:column sort="true" width="8%" property="startDate" align="center" title="label.manageTask.grid.initDate" description="label.showAllTaskForm.startDate.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_EST_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <display:column sort="true" width="5%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" description="label.showAllTaskForm.estimatedTimeInHours.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_EST_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>	
				  <display:column sort="true" width="8%" property="actualDate" align="center" title="label.manageTask.grid.acualDate" description="label.showAllTaskForm.actualDate.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_ACT_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <display:column sort="true" width="5%" property="actualTimeInHours" align="center" title="label.manageTask.grid.actualTime" description="label.showAllTaskForm.actualTimeInHours.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_ACT_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
				  <display:column sort="true" width="5%" align="center" property="id" title="label.gridParent" description="label.gridParent.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_PARENT%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="PARENT_ID"/>
				  <display:column sort="true" width="10%" property="taskStatus.name" align="center" description="label.showAllTaskForm.taskStatus.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_STATUS%>" title="label.manageTask.taskStatus" />
				  <display:column sort="true" width="10%" align="center" property="id" title="label.manageTask.category" description="label.showAllTaskForm.category.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_CATEG%>" decorator="com.pandora.gui.taglib.decorator.TaskCategoryDecorator" />
				  <display:column sort="true" width="10%" property="id" align="center" title="label.manageTask.grid.iteration" description="label.manageTask.grid.iteration.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_ITERAT%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="edit" />
		  		  <display:column sort="true" width="9%"  property="id" align="center" title="label.manageTask.grid.iteration.req" description="label.manageTask.grid.iteration.req.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_REQ_ITERAT%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="req"/>				  		  
				  <display:column sort="true" width="5%" likeSearching="true" align="center" property="task.requirement.id" title="label.gridReqNum" description="label.gridReqNum.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_REL_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="REQ_ID"/>
			      <display:metafieldcolumn width="10%" property="id" align="center" description="label.manageOption.showTaskMetaField" visibleProperty="<%=PreferenceTO.LIST_ALL_TSK_SW_META_FIELD%>" />				  
  				  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskLinkDecorator" tag="SHOW_ALL"/>
  				  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridWorkflowDecorator" />
				  <display:column width="2%" property="task.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'TSK'" />
			</display:table>
		</td>
	</tr> 
	</table>
      		
	<display:headerfootergrid type="FOOTER">
  	<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
      <td width="120">      
	      <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('TSK');">
    	    <bean:message key="button.refresh"/>
	      </html:button>    
      </td>      
      <td width="30">&nbsp;</td>
   	  <td align="center">
		  <logic:equal name="showAllTaskForm" property="showUpdateInBatch" value="on">
			  <html:button property="updateInBatch" styleClass="button" onclick="javascript:buttonClick('showAllTaskForm', 'updateInBatch');">
	  			   <bean:message key="label.showAllTaskForm.updateInBatch"/>
	   		  </html:button>
  	      </logic:equal>
		  <logic:equal name="showAllTaskForm" property="showUpdateInBatch" value="off">
		  		&nbsp;
  	      </logic:equal>   	  
  	  </td>      
      <td width="50" align="right">
      	<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('showAllTaskForm', 'backward');">
	    	<bean:message key="button.backward"/>
      	</html:button>    
	  </td>      
    </tr></table>  	
  	</display:headerfootergrid> 
	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["showAllTaskForm"]){	
		if (showWorkflowDiagram.value=='on') {
			displayMessage("../do/showAllTask?operation=prepareWorkflow", 450, 450);
			setTimeout('centralizeDiagram()', 500);
			showWorkflowDiagram.value = 'off';
		}		
	}
</script> 