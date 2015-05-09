<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

    function openPopup(id) {
	    var pathWindow ="../do/manageHistRequest?operation=prepareForm&reqId=" + id;
		window.open(pathWindow, 'reqHist', 'width=540, height=320, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
    
	function newTaskByReq(projecId, requirementId){
		window.location = "../do/manageTask?operation=prepareForm&projectId=" + projecId + "&requirementId=" + requirementId;	
	}    
    
    function openTaskHistPopupByReq(reqId) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=&reqIdRelated=" + reqId + "&resourceId=";
		window.open(pathWindow, 'taskHist', 'width=710, height=240, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
    
	function refuse(id){
	    var pathWindow ="../do/refuseRequirement?operation=prepareForm&requirementId=" + id;
		window.open(pathWindow, 'refuseRequirement', 'width=510, height=200, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');	
	}
    
    //Important Note: This method is used too by refuseRequirement.jsp    
    function refreshGrid(source){
    	with(document.forms["showAllRequirementForm"]){
	    	if (source=="REFUSE_REQ"){
	    		alert("<bean:message key="message.refuseReq.RefusedSuccessfully"/>");
	    	}
    		operation.value = "prepareForm";
    		submit();
        }
    }

	function showGantt(reqId, type){
		var pathWindow = "../do/ganttViewer?operation=prepareForm&requirementId=" + reqId + "&type=2";
		window.open(pathWindow, 'gantt', 'width=1010, height=705, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	}
    
    function edit(id, src){
    	if (src =="REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;
    	} else if (src =="ADJUST_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;    		
    	} else if (src =="ADJUST_REQ_WOUT_PRJ") {    		
    		window.location = "../do/manageCustRequest?operation=editRequest&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=on&readOnlyMode=off&id=" + id;    		
    	} else if (src =="RO_MODE") {
			window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=on&id=" + id;		    		
    	}
    }
    
	function showWorkFlow(instId, planId){
		window.location = "../do/showAllRequirement?operation=showWorkFlow&instanceId=" + instId + "&planningId=" + planId + "&showWorkflowDiagram=on&bgcolor=F0F0F0";	
	}
    
    function clickNodeTemplate(instId, planId){
		with(document.forms["showAllRequirementForm"]){
			closeFloatPanel();	
   			operation.value = "clickNodeTemplate";
    		instanceId.value = instId;
    		planningId.value = planId;
   		}
		ajaxProcess(document.forms["showAllRequirementForm"], callBackNodeTemplateClick, instId, planId);		
	}
	
				
	function callBackNodeTemplateClick(instId, planId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			openFloatPanel(content);	   	
	    }  
	}

	function executeTemplate(reqId){
		with(document.forms["showAllRequirementForm"]){
			var templId = document.getElementById("TEMPLATE_" + reqId).value;
			if (templId!=null && templId!='-1') {
				window.location = "../do/applyTaskTemplate?operation=prepareForm&reqId=" + reqId + "&templateId=" + templId;
			}
   		}
	}    
</script>

<html:form  action="showAllRequirement">
	<html:hidden name="showAllRequirementForm" property="id"/>
	<html:hidden name="showAllRequirementForm" property="operation"/>
	<html:hidden name="showAllRequirementForm" property="projectRelated"/>	
	<html:hidden name="showAllRequirementForm" property="showWorkflowDiagram"/>
	<html:hidden name="showAllRequirementForm" property="instanceId"/>	
	<html:hidden name="showAllRequirementForm" property="planningId"/>
				
	<br>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	  	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.showAllReqForm"/>
	</display:headerfootergrid>

  	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="100">&nbsp;</td>
      <td width="230">&nbsp;</td>
      <td width="80">&nbsp;</td>      
      <td width="140">&nbsp;</td>
      <td width="10">&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>	  	
    <tr class="formBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requestStatus"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="showAllRequirementForm" property="statusSelected" styleClass="textBox">
			<html:options collection="statusList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td class="formTitle"><bean:message key="label.requester"/>:&nbsp;</td>      
      <td class="formBody">
	  	<html:select name="showAllRequirementForm" property="requesterSelected" styleClass="textBox">
			<html:options collection="requesterList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requestPriority"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="showAllRequirementForm" property="prioritySelected" styleClass="textBox">
			<html:options collection="priorityList" property="id" labelProperty="genericTag"/>
		</html:select>      
      </td>
    </tr>
    <tr class="formBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.manageTask.category"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="showAllRequirementForm" property="categorySelected" styleClass="textBox">
			<html:options collection="categoryList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td class="formTitle"><bean:message key="label.showAllReqForm.viewMode"/>:&nbsp;</td>      
      <td class="formBody" colspan="3">
	  	<html:select name="showAllRequirementForm" property="viewModeSelected" styleClass="textBox">
			<html:options collection="viewModeList" property="id" labelProperty="genericTag"/>
		</html:select>      	
      </td>
      <td class="formBody">&nbsp;</td>
    </tr>    
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
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
			  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('REQ');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td width="30">&nbsp;</td>      
		  <td>&nbsp;</td>
		  <td width="100">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('REQ');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>      
		  <td width="100" align="right">
			<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('showAllRequirementForm', 'backward');">
				<bean:message key="button.backward"/>
			</html:button>    
		  </td>      
		</tr></table>  	
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="label.requestList"/>
	</display:headerfootergrid>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
		
		   	<logic:notEqual name="showAllRequirementForm" property="viewModeSelected" value="0" >
				<display:table border="1" width="100%" name="allRequirementList" scope="session" pagesize="<%=PreferenceTO.LIST_ALL_SHOW_PAGING%>" requestURI="../do/showAllRequirement?operation=navigate">
			  		<display:column sort="true" width="8%" property="id" align="center" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />			    	      
			  		<display:column sort="true" property="description" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.manageOption.showDescription" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
   		  	  		<display:column sort="true" width="12%" property="creationDate" align="center" description="label.manageOption.showCreationDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_CR_DATE%>" title="label.showAllReqForm.grid.creationDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
			  		<display:column sort="true" width="10%" align="center" property="project.name" title="label.requestProject" description="label.manageOption.showProjName" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_NAME%>" />	  	      
			  		<display:column sort="true" width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageOption.showCategory" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_CATEGORY%>" />			  	  	      
			  		<display:column sort="true" width="8%" align="center" property="requirementStatus.name" title="label.requestStatus" description="label.manageOption.showStatus" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_STATUS%>" />			  
			  		<display:column sort="true" width="5%" property="requester.username" align="center" title="label.showAllReqForm.grid.requester" description="label.manageOption.showRequester" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_REQUESTER%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="showAllRequirementForm"/>
		  	  		<display:column sort="true" width="10%" property="suggestedDate" align="center" title="label.showAllReqForm.grid.suggDate" description="label.manageOption.showSuggDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_SUG_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />	  	      
			  		<display:column sort="true" width="10%" property="deadlineDate" align="center" title="label.showAllReqForm.grid.estiDate" description="label.manageOption.showDeadlineDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_DEADL_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />			  
			  		<display:column sort="true" width="4%" align="center" property="parentRequirementId" title="label.showAllReqForm.grid.parentReq" description="label.manageOption.showParentReq" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PARENT_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridTextBoxDecorator" tag="50;10"/>			  
			  		<display:column sort="true" width="9%" align="center" property="strPriority" title="label.requestPriority" description="label.manageOption.showPriority" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PRIORITY%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="priority;0|label.requestPriority.0|1|label.requestPriority.1|2|label.requestPriority.2|3|label.requestPriority.3|4|label.requestPriority.4|5|label.requestPriority.5|"/>			  
			  		<display:column sort="true" width="9%" align="center" property="iteration" title="label.occurrence.iteration" description="label.manageOption.showIteration" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_ITERATION%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="iteration;!select id, name from occurrence where source like '%IterationOccurrence' and (project_id='?#PROJECT_ID#' or project_id in (select parent_id from project where id='?#PROJECT_ID#')) union select '-1', ' ' order by 2!"/>			  
			  		<display:column width="8%" property="id" align="center" title="label.showAllReqForm.grid.showResources" description="label.manageOption.showRelatedRes" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_REL_RES%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridShowResourcesDecorator" />
			  		<display:column width="4%" property="id" align="center" title="label.showAllReqForm.grid.progTask" description="label.manageOption.showTskProgs" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PRG_TSK%>" decorator="com.pandora.gui.taglib.decorator.RequirementTasksProgressDecorator" />			  		
			  		<display:metafieldcolumn width="10%" property="id" align="center" description="label.manageOption.showMetaField" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_META_FIELD%>" />				
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridGanttDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridWorkflowDecorator" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridRefuseDecorator" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" tag="ONLY_HISTORY"/>
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" tag="ONLY_NEW"/>
				    <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridByTemplate" />
				</display:table>
			</logic:notEqual>
		

		   	<logic:equal name="showAllRequirementForm" property="viewModeSelected" value="0">
				<display:table border="1" width="100%" name="allRequirementList" scope="session" pagesize="0" requestURI="../do/showAllRequirement?operation=navigate">
			  		<display:column width="8%" property="id" align="center" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />			    	      
			  		<display:column property="description" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.manageOption.showDescription" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" tag="show_hiearquy" />
		  	  		<display:column width="12%" property="creationDate" align="center" description="label.manageOption.showCreationDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_CR_DATE%>" title="label.showAllReqForm.grid.creationDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />			  			  	      
			  		<display:column width="10%" align="center" property="project.name" title="label.requestProject" description="label.manageOption.showProjName" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_NAME%>" />	  	      
			  		<display:column width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageOption.showCategory" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_CATEGORY%>" />			  	  	      
			  		<display:column width="8%" align="center" property="requirementStatus.name" title="label.requestStatus" description="label.manageOption.showStatus" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_STATUS%>" />			  
			  		<display:column width="5%" property="requester.username" align="center" title="label.showAllReqForm.grid.requester" description="label.manageOption.showRequester" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_REQUESTER%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="showAllRequirementForm"/>
		  	  		<display:column width="10%" property="suggestedDate" align="center" title="label.showAllReqForm.grid.suggDate" description="label.manageOption.showSuggDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_SUG_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />	  	      
			  		<display:column width="10%" property="deadlineDate" align="center" title="label.showAllReqForm.grid.estiDate" description="label.manageOption.showDeadlineDate" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_DEADL_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />			  
			  		<display:column width="4%" align="center" property="parentRequirementId" title="label.showAllReqForm.grid.parentReq" description="label.manageOption.showParentReq" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PARENT_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridTextBoxDecorator" tag="50;10"/>			  
			  		<display:column width="9%" align="center" property="strPriority" title="label.requestPriority" description="label.manageOption.showPriority" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PRIORITY%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="priority;0|label.requestPriority.0|1|label.requestPriority.1|2|label.requestPriority.2|3|label.requestPriority.3|4|label.requestPriority.4|5|label.requestPriority.5|"/>			  
			  		<display:column width="9%" align="center" property="iteration" title="label.occurrence.iteration" description="label.manageOption.showIteration" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_ITERATION%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="iteration;!select id, name from occurrence where source like '%IterationOccurrence' and (project_id='?#PROJECT_ID#' or project_id in (select parent_id from project where id='?#PROJECT_ID#')) union select '-1', ' ' order by 2!"/>			  
			  		<display:column width="8%" property="id" align="center" title="label.showAllReqForm.grid.showResources" description="label.manageOption.showRelatedRes" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_REL_RES%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridShowResourcesDecorator" />
			  		<display:column width="4%" property="id" align="center" title="label.showAllReqForm.grid.progTask" description="label.manageOption.showTskProgs" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_PRG_TSK%>" decorator="com.pandora.gui.taglib.decorator.RequirementTasksProgressDecorator" />			  					  
			  		<display:metafieldcolumn width="10%" property="id" align="center" description="label.manageOption.showMetaField" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_META_FIELD%>" />				
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridGanttDecorator" tag="'REQ'" />
			  		<display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridWorkflowDecorator" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridRefuseDecorator" />
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" tag="ONLY_HISTORY"/>
			  		<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" tag="ONLY_NEW"/>
				    <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridByTemplate" />			  		
				</display:table>
			</logic:equal>

		</td>
	</tr> 
	</table>
      		
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('REQ');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td align="center">
			  <logic:equal name="showAllRequirementForm" property="showUpdateInBatch" value="on">
				  <html:button property="updateInBatch" styleClass="button" onclick="javascript:buttonClick('showAllRequirementForm', 'updateInBatch');">
					   <bean:message key="label.showAllReqForm.updateInBatch"/>
				  </html:button>
			  </logic:equal>
			  <logic:equal name="showAllRequirementForm" property="showUpdateInBatch" value="off">
					&nbsp;
			  </logic:equal>   	  
		  </td>      
		  <td width="50" align="right">
			<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('showAllRequirementForm', 'backward');">
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
	with(document.forms["showAllRequirementForm"]){	
		if (showWorkflowDiagram.value=="on" ) {
			displayMessage("../do/showAllRequirement?operation=prepareWorkflow", 450, 450);
			setTimeout('centralizeDiagram()', 500);			
		}		
	}
</script>