<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="JavaScript">

	function refreshGrid(src){
    	with(document.forms["resourceHomeForm"]){
    		operation.value = "refreshList";
    		source.value = src;
    		submit();
        }       
	}
	
	function hideClosedCheck(){
    	with(document.forms["resourceHomeForm"]){
        	if (hideClosedRequests.value=="on"){
         		hideClosedRequests.value = "";
         	} else {
         		hideClosedRequests.value = "on";
         	}
         }       
    }

    function hideClosedTaskCheck(){
         with(document.forms["resourceHomeForm"]){
         	if (hideClosedTasks.value=="on"){
         		hideClosedTasks.value = "";
         	} else {
         		hideClosedTasks.value = "on";
         	}
         }             
    }	
    
    function openPopup(id) {
	    var pathWindow ="../do/manageHistRequest?operation=prepareForm&reqId=" + id;
		window.open(pathWindow, 'reqHist', 'width=540, height=330, location=no, menubar=no, status=yes, toolbar=no, scrollbars=no, resizable=no');
    }

    function openResTaskHistPopup(taskid, resourceid) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=" + taskid + "&resourceId=" + resourceid;
		window.open(pathWindow, 'reqTask', 'width=740, height=250, location=no, menubar=no, status=yes, toolbar=no, scrollbars=no, resizable=no');
    }

    function edit(id, src){
    	if (src =="REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;
    	} else if (src =="ADJUST_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;    	    		
    	} else if (src =="REF_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=on&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;    		    	
    	} else if (src =="ADJUST_REQ_WOUT_PRJ") {    		
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=on&readOnlyMode=off&id=" + id;    		
    	} else if (src=="TSK") {
    		window.location = "";
    	} else if (src =="PRJ") {
	   		window.location = "../do/manageProject?operation=editProject&id=" + id;
    	} else if (src =="RO_MODE") {
			window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=on&id=" + id;		    		
    	}
    }    

    function remove(id, src){
    	if (src=="REQ") {
        	if ( confirm("<bean:message key="message.confirmGiveUpRequest"/>")) {
         		window.location = "../do/manageCustRequest?operation=giveUpRequest&showBackward=on&nextFwd=home&id=" + id;
         		src = "";
         	}
    	} else if (src=="TSK") {
        	if ( confirm("<bean:message key="message.confirmDeleteTask"/>")) {
         		window.location = "../do/manageResTask?operation=removeTask&nextFwd=home&task_id=" + id;
         		src = "";
         	}
    	}
    }
    
	function goToForm(src){
    	if (src=="REQ") {
       		window.location = "../do/manageCustRequest?operation=prepareForm&showBackward=on";
    	} else {
    		window.location = src;
    	}	
	}
	
	function showAllRequirements(projId){
		window.location = "../do/showAllRequirement?operation=prepareForm&projectRelated=" + projId;
	}

	function showAllTasks(projId){
		window.location = "../do/showAllTask?operation=prepareForm&projectRelated=" + projId;
	}
	
	function showWorkFlow(instId, planId){
		window.location = "../do/showAllTask?operation=showWorkFlow&fwd=worflowMainForm&instanceId=" + instId + "&planningId=" + planId + "&showWorkflowDiagram=on&bgcolor=F0F0F0";	
	}
	
	function clickNodeTemplate(instId, planId){
		with(document.forms["resourceHomeForm"]){
    		closeFloatPanel();		
   			operation.value = "clickNodeTemplate";
    		planningId.value = planId;
   		}
		ajaxProcess(document.forms["resourceHomeForm"], callBackNodeTemplateClick, instId, planId);		
	}
	
				
	function callBackNodeTemplateClick(instId, planId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			openFloatPanel(content);   	
	    }  
	}
			
	function clickGadgetCategory(catId){
		with(document.forms["resourceHomeForm"]){		
   			operation.value = "clickGadgetCategory";
    		genericTag.value = catId;
   		}
		ajaxProcess(document.forms["resourceHomeForm"], callBackClickGadgetButton, catId);		
	}
	
	function clickGadgetButton(planId){
		with(document.forms["resourceHomeForm"]){		
   			operation.value = "clickGadgetButton";
    		planningId.value = planId;
   		}
		ajaxProcess(document.forms["resourceHomeForm"], callBackClickGadgetButton, planId);		
	}
				
	function callBackClickGadgetButton(objId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
	       	document.getElementById("GADGET_BODY").innerHTML = content;  
	    }  
	}
			
		
	function newTask(projId){
		window.location = "../do/manageTask?operation=prepareForm&projectId=" + projId + "&requirementId=-1";	
	}
	
	function newTaskByReq(projId, requirementId){
		window.location = "../do/manageTask?operation=prepareForm&projectId=" + projId + "&requirementId=" + requirementId;	
	}

	function pin(taskId, type){
		with(document.forms["resourceHomeForm"]){
   			operation.value = "pin";
   			id.value = taskId;
   		}
		ajaxProcess(document.forms["resourceHomeForm"], callBackPinClick, taskId);		
	}
	
				
	function callBackPinClick(taskId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			var i = content.indexOf("|");
			var newImg = "../images/" + content.substr(0,i);
			var newLabel = content.substr(i+1);			
		   	(document.getElementById("PIN_"+taskId)).src = newImg;  
			(document.getElementById("PIN_"+taskId)).setAttribute("title", newLabel);
			(document.getElementById("PIN_"+taskId)).setAttribute("alt", newLabel);		   	
	    }  
	}
	  	
	function showHidePanel(pId){
		with(document.forms["resourceHomeForm"]){
   			operation.value = "showHidePanel";
   			panelId.value = pId;
			submit();
   		}	
	}

	function executeTemplate(reqId){
		with(document.forms["resourceHomeForm"]){
			var templId = document.getElementById("TEMPLATE_" + reqId).value;
			if (templId!=null && templId!='-1') {
				window.location = "../do/applyTaskTemplate?operation=prepareForm&reqId=" + reqId + "&templateId=" + templId;
			}
   		}
	}

	function executeTemplateByProject(projectId){
		with(document.forms["resourceHomeForm"]){
			var templId = document.getElementById("TEMPLATE_" + projectId).value;
			if (templId!=null && templId!='-1') {
				window.location = "../do/applyTaskTemplate?operation=prepareForm&reqId=-1&projId=" + projectId + "&templateId=" + templId;
			}
   		}
	}

	function showMaximizedGadget(gagId){
		with(document.forms["resourceHomeForm"]){
   			operation.value = "showMaximizedGadget";
   			maximizedGadgetId.value = gagId;
			submit();
   		}	
	}

		
	function showGantt(id, type){
		var pathWindow = "";
		if (type=='PRJ'){
			pathWindow = "../do/ganttViewer?operation=prepareForm&projectId=" + id + "&type=1";
		} else if (type=='RES'){
			with(document.forms["resourceHomeForm"]) {
				pathWindow = "../do/ganttViewer?operation=prepareForm&resourceId=" + resourceId.value + "&type=3";
			}
		}
		window.open(pathWindow, 'gantt', 'width=1010, height=705, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	}
	
	function callResourceTask(taskId, resId, projId){
		window.location = "../do/manageResTask?operation=prepareForm&projectId=" + projId + "&resourceId=" + resId + "&taskId=" + taskId;	
	}

	function callExpenseForm(){
		window.location = "../do/manageExpense?operation=prepareForm";	
	}

	function grabTask(taskId, projId){
		window.location = "../do/manageResTask?operation=grabTask&fwd=home&projectId=" + projId + "&taskId=" + taskId;	
	}

	function showBSC(projId, src){
		window.location = "../do/viewBSCPanel?operation=prepareForm&projectId=" + projId;	
	}

	function showReport(projId, src){
		window.location = "../do/viewReport?operation=prepareForm&projectId=" + projId;	
	}

	function showKB(projId){
		window.location = "../do/viewKb?operation=prepareForm&projectId=" + projId;	
	}

	function showRisk(projId){
		window.location = "../do/manageRisk?operation=prepareForm&projectId=" + projId;	
	}

	function showAgilBoard(projId){
		window.location = "../do/showAgilePanel?operation=prepareForm&projectId=" + projId;	
	}

	function showInvoice(projId){
		window.location = "../do/manageInvoice?operation=prepareForm&projectId=" + projId;	
	}

	function showCosts(projId){	
		window.location = "../do/showCostPanel?operation=prepareForm&type=PRJ&projectId=" + projId;	
	}

	function showSurvey(projId){
		window.location = "../do/manageSurvey?operation=prepareForm&projectId=" + projId;	
	}

	function showRepository(projId){
		window.location = "../do/showRepositoryViewer?operation=prepareForm&projectId=" + projId;	
	}

	function showResourceCapacity(projId){
		window.location = "../do/showResCapacityPanel?operation=prepareForm&type=PRJ&projectId=" + projId;
	}

	function refuseCost(cstId){
    	if ( confirm("<bean:message key="message.expense.confirmRefuseExpense"/>")) {
     		window.location = "../do/showCostPanel?operation=refuseCost&id=" + cstId;
     	}
	}

	function approveCost(cstId){
		window.location = "../do/showCostPanel?operation=approveCost&id=" + cstId;
	}
	
	function showExpenseReport(eid){
		with(document.forms["resourceHomeForm"]){
   			operation.value = "showExpenseReport";
   			id.value = eid;
			submit();
   		}	
	}
		
	function reloadFields(gadId, fieldListIds) {	
		var concatValues = "";
		var idList = fieldListIds.split(';');
		if (idList) {
			for(var i=0; i<=idList.length; i++) {
				if (idList[i] && idList[i]!="") {
					var fieldVal = document.getElementById(idList[i]).value;					
					concatValues = concatValues + idList[i] + "|" + fieldVal + "|";				
				}
			}		
		}
		with(document.forms["resourceHomeForm"]){
   			operation.value = "refreshGadgetFields";
   			genericTag.value = gadId + "|" + concatValues;
   		}
		ajaxProcess(document.forms["resourceHomeForm"], callBackReloadFields, gadId + "|" + concatValues);		
	}

	function callBackReloadFields(gadgetFieldId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			document.getElementById("GADGET_PROPERTY_ID").innerHTML = content; 
	    }  
	}

	
</script>

<html:form  action="manageResourceHome">
	<html:hidden name="resourceHomeForm" property="id"/>
	<html:hidden name="resourceHomeForm" property="operation"/>
	<html:hidden name="resourceHomeForm" property="hideClosedRequests"/>
	<html:hidden name="resourceHomeForm" property="hideClosedTasks"/>	
	<html:hidden name="resourceHomeForm" property="source"/>
	<html:hidden name="resourceHomeForm" property="resourceId"/>
	<html:hidden name="resourceHomeForm" property="panelId"/>
	<html:hidden name="resourceHomeForm" property="gagclass"/>
	<html:hidden name="resourceHomeForm" property="maximizedGadgetId"/>
	<html:hidden name="resourceHomeForm" property="showWorkflowDiagram"/>
	<html:hidden name="resourceHomeForm" property="planningId"/>
	<html:hidden name="resourceHomeForm" property="genericTag"/>
	<html:hidden name="resourceHomeForm" property="expenseReportURL"/>
	

	<table border="0" width="100%" cellspacing="0" cellpadding="0">	
		<tr>
			<td>&nbsp;</td>
			<bean:write name="resourceHomeForm" property="shorcutsHtmlBody" filter="false" />
			<td valign="middle" align="right" width="20">
				<a href="javascript:displayMessage('../do/manageGadget?operation=prepareForm', 700, 440);" border="0"> 
					<img border="0" title="<bean:write name="resourceHomeForm" property="showHideGadgetLabel" />" alt="<bean:write name="resourceHomeForm" property="showHideGadgetLabel" />" align="center" src="../images/showhidechart.png" ></a>
				</td>
				<td width="10">&nbsp;</td>
			</td>
		</tr>
	</table>	   	


	<table border="0" width="100%" cellspacing="0" cellpadding="0">	
	<tr valign="top">
		<td>
		
			<!-- MY TASK LIST -->
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.resHome.myTasks"/>
			</display:headerfootergrid>
		
			<table border="0" width="98%" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
				<display:table border="1" width="100%" name="taskList" scope="session" pagesize="<%=PreferenceTO.HOME_TASKLIST_NUMLINE%>" requestURI="../do/manageResourceHome?operation=navigate">
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" />
					  <display:column width="2%" property="task.id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			      
					  <display:column sort="true" property="task.name" likeSearching="true" title="label.manageTask.name" decorator="com.pandora.gui.taglib.decorator.TaskDelayHiLightDecorator" tag="<%=PreferenceTO.LIST_NUMWORDS%>" />
					  <display:column width="2%" sort="true" property="classification" align="center" title="label.grid.requestform.pin.title" description="label.grid.requestform.pin.description" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_CLASSIF%>" decorator="com.pandora.gui.taglib.decorator.TaskGridPinDecorator" />
					  <display:column sort="true" width="16%" align="center" comboFilter="true" property="task.project.name" title="label.manageTask.project" description="label.manageTask.project" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_PROJECT%>" />
					  <display:column width="6%" property="id" align="center" title="label.resTaskForm.billable" description="label.showAllTaskForm.billable.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BILLABLE%>" decorator="com.pandora.gui.taglib.decorator.TaskBillableDecorator"/>						  
					  <display:column width="8%" property="actualDate" align="center" title="label.manageTask.grid.acualDate" description="label.manageTask.grid.acualDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="5%" property="actualTimeInHours" align="center" title="label.manageTask.grid.actualTime" description="label.manageTask.grid.actualTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>				  
					  <display:column width="8%" property="startDate" align="center" title="label.manageTask.grid.initDate" description="label.manageTask.grid.initDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="5%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" description="label.manageTask.grid.estTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
					  <display:column width="5%" sort="true" align="center" property="id" title="label.gridParent" description="label.gridParent.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_PARENT%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="PARENT_ID"/>
					  <display:column width="10%" sort="true" property="taskStatus.name" align="center" title="label.manageTask.taskStatus" description="label.manageTask.taskStatus" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_STATUS%>" />
					  <display:column width="10%" sort="true" property="task.category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_CATEG%>" />
					  <display:column width="10%" sort="true" property="id" align="center" title="label.manageTask.grid.iteration" description="label.manageTask.grid.iteration.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_ITERAT%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" />
					  <display:column width="5%" sort="true" likeSearching="true" align="center" property="task.requirement.id" title="label.gridReqNum" description="label.gridReqNum.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_REL_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="REQ_ID"/>
					  <display:column width="5%" align="center" property="id" title="label.blockers" description="label.blockers.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BLOCKERS%>" decorator="com.pandora.gui.taglib.decorator.TaskBlockedByDecorator" />						  
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridDeleteDecorator" />
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskLinkDecorator" />
					  <display:column width="2%" property="task.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'RES_TSK'" />
				</display:table>			
				</td>
			</tr> 
			</table>
				
			<display:headerfootergrid type="FOOTER">
				<table border="0" width="98%" cellspacing="0" cellpadding="0"><tr>
				  <td width="150">      					
					  <html:button property="createAdHocTask" styleClass="button" onclick="javascript:callResourceTask('', '', '');">
						<bean:message key="button.resHome.adHocTask"/>
					  </html:button>    
				  </td>					
				  <td width="80">      
					  <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('TSK');">
						<bean:message key="button.refresh"/>
					  </html:button>    
				  </td>      
				  <td width="20">&nbsp;</td>
				  <td class="textBoxOverTitleArea">
					<input type="checkbox" name="hideClosedTaskCheckbox" onclick="javascript:hideClosedTaskCheck();"/><bean:message key="label.resTaskForm.taskHideClosed"/>
				  </td>
				  <td width="80">      
					  <html:button property="addexpense" styleClass="button" onclick="javascript:callExpenseForm();">
						<bean:message key="button.resHome.expense"/>
					  </html:button>    
				  </td>      				  
				</tr></table>  			
			</display:headerfootergrid>
			
			<div>&nbsp;</div>
			
			
			<logic:equal name="resourceHomeForm" property="showPendingCosts" value="on">
				
				<!-- PENDING COSTS LIST -->		
				<display:headerfootergrid width="100%" type="HEADER">
					<bean:message key="label.resHome.myHoldCost"/>
				</display:headerfootergrid>
							
				<table width="98%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formBody">
					<td>
						<display:table border="1" width="100%" name="pendingCostList" scope="session" pagesize="<%=PreferenceTO.HOME_PENDLIST_NUMLINE%>" requestURI="../do/manageResourceHome?operation=navigate">
						  <display:column sort="true" width="30" align="center" property="expense.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseReportDecorator" />
						  <display:column sort="true" align="center" property="name" title="label.grid.requestform.name" description="label.grid.requestform.name" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_NAME%>"/>
						  <display:column sort="true" width="120" align="center" property="project.name" title="label.grid.requestform.prj" description="label.grid.requestform.prj" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_PROJ%>"/>
						  <display:column width="130" property="expense.creationDate" align="center" title="label.grid.requestform.creat" description="label.grid.requestform.creat" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
						  <display:column sort="true" width="110" likeSearching="true" property="expense.user.username" align="center" title="label.grid.requestform.user" description="label.grid.requestform.user" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_USER%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="resourceHomeForm" />
  						  <display:column width="70" property="firstInstallmentDate" align="center" title="label.costedit.duedate"  decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
						  <display:column width="70" align="center" property="totalValue" title="label.costedit.value" decorator="com.pandora.gui.taglib.decorator.ExpenseTotalDecorator"/>
					  	  <display:column width="16" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseRefuseDecorator" />
					      <display:column width="16" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseApproveDecorator" />
						</display:table>		
					</td>
				</tr> 
				</table>

				<display:headerfootergrid type="FOOTER">
					&nbsp;
				</display:headerfootergrid>
				
				<div>&nbsp;</div>
				
			</logic:equal>
			
			
			<logic:equal name="resourceHomeForm" property="showPendingReq" value="on">
			
				<!-- PENDING REQUIREMENT LIST -->
				<a name="pendRequirementList"></a>			
				<display:headerfootergrid width="100%" type="HEADER">
					<bean:message key="label.resHome.myHoldReq"/>
				</display:headerfootergrid>
							
				<table width="98%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formBody">
					<td>
					<display:table border="1" width="100%" name="pendRequirementList" scope="session" pagesize="<%=PreferenceTO.HOME_PENDLIST_NUMLINE%>" requestURI="../do/manageResourceHome?operation=navigate">
					  <display:column sort="true" align="left" likeSearching="true" width="8%" property="id" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />
					  <display:column sort="true" property="description" likeSearching="true" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.requestDesc" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
					  <display:column sort="true" width="13%" align="center" property="project.name" title="label.requestProject" description="label.requestProject" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_PROJ%>"/>
					  <display:column sort="true" width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_CATEG%>"/>
					  <display:column width="10%" property="suggestedDate" align="center" title="label.requestSuggestedDate" description="label.requestSuggestedDate" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_SUGG%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="12%" property="creationDate" align="center" title="grid.title.creationDate" description="grid.title.creationDate" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <display:column sort="true" width="7%" likeSearching="true" property="requester.username" align="center" title="label.requester" description="label.requester" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_REQUE%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="resourceHomeForm" />
					  <display:column sort="true" width="7%" align="center" property="priority" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridRefuseDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridByTemplate" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" />
					</display:table>		
					</td>
				</tr> 
				</table>

				<display:headerfootergrid type="FOOTER">
					&nbsp;
				</display:headerfootergrid>
			
			<div>&nbsp;</div>
			</logic:equal>
			

			<a name="myRequirementList"></a>			

			<!-- MY REQUEST LIST -->
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.resHome.myRequests"/>
			</display:headerfootergrid>
			
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
				<display:table border="1" width="100%" name="myRequirementList" scope="session" pagesize="<%=PreferenceTO.HOME_REQULIST_NUMLINE%>" requestURI="../do/manageResourceHome?operation=navigate">
					  <display:column sort="true" align="left" likeSearching="true" width="8%" property="id" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />			
					  <display:column sort="true" property="description" likeSearching="true" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.requestDesc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
					  <display:column sort="true" width="12%" align="center" property="project.name" title="label.requestProject" description="label.requestProject" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PROJ%>" />
					  <display:column sort="true" width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_CATEG%>" />
					  <display:column sort="true" width="7%" align="center" property="priority" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />				  
					  <display:column sort="true" width="16%" align="center" property="requirementStatus.name" title="label.requestStatus" description="label.requestStatus" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_STAT%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridStatus" />
					  <display:column sort="true" width="10%" property="iteration" align="center" title="label.occurrence.iteration" description="label.manageTask.grid.iteration.req.desc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_ITERA%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="req_only"/>
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" tag="'REQ'" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridDeleteDecorator" tag="'REQ'" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'REQ'" />
				</display:table>		
				</td>
			</tr> 
			</table>
			
			<display:headerfootergrid type="FOOTER">
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td width="150">
					<html:button property="gotoRequestForm" styleClass="button" onclick="javascript:goToForm('REQ');">
						<bean:message key="button.resHome.newReq"/>
					</html:button>    
				  </td>
				  <td width="80">      
					<html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('REQ');">
						<bean:message key="button.refresh"/>
					</html:button>    
				  </td>
				  <td width="20">&nbsp;</td>
				  <td class="textBoxOverTitleArea">
					<input type="checkbox" name="hideClosedCheckbox" onclick="javascript:hideClosedCheck();"/><bean:message key="label.requestHideClosed"/>
				  </td>
				</tr></table>
			</display:headerfootergrid>

			<div>&nbsp;</div>
			
			<a name="projectList"></a>
			
			<!-- MY PROJECT LIST -->    	
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.resHome.myProjects"/>
			</display:headerfootergrid>
			
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
				<display:table border="1" width="100%" name="projectList" scope="session" pagesize="<%=PreferenceTO.HOME_PROJLIST_NUMLINE%>" requestURI="../do/manageResourceHome?operation=navigate">
					  <display:column property="name" title="label.projName" decorator="com.pandora.gui.taglib.decorator.ProjectTreeDecorator" />
					  <display:column width="10%" property="creationDate" align="center" title="label.resHome.projectCreatDate" description="label.resHome.projectCreatDate" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="10%" property="id" align="center" title="label.resHome.project.grid.lifeTipe" description="label.resHome.project.grid.lifeTipe" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_LIFE%>" decorator="com.pandora.gui.taglib.decorator.ProjectGridLifeTimeDecorator" />				  
					  <display:column width="10%" property="projectStatus.name" align="center" title="label.projStatus" description="label.projStatus" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_STAT%>" />
					  <display:column width="10%" property="roleIntoProject" title="label.role" align="center" description="label.role" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_ROLE%>" decorator="com.pandora.gui.taglib.decorator.UserRoleDecorator" />
					  <display:column width="4%" property="id" align="center" title="label.resHome.project.grid.id" description="label.resHome.project.grid.id.desc" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_ID%>"/>
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridRiskDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridOccurrenceBookDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridSurveyDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridInvoiceDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridCostDecorator" />	
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryViewerDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridKBDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridReportDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridBSCDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridAgileBoardDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridShowTaskDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridShowReqDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridImpExpDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridGanttDecorator" tag="'PRJ'" />
  					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceCapacityDecorator" />					  
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridEditDecorator" tag="'PRJ'" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridNewTaskDecorator" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridByTemplate" />
				</display:table>		
				</td>
			</tr> 
			</table>

			<display:headerfootergrid type="FOOTER">
				&nbsp;
			</display:headerfootergrid>

			<div>&nbsp;</div>

			<!-- MY FORUM LIST -->
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.resHome.myForuns"/>
			</display:headerfootergrid>
			
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
				<display:table border="1" width="100%" name="discussionTopicList" scope="session" pagesize="10" requestURI="../do/manageResourceHome?operation=navigate">
					  <display:column width="7%" property="planningId" title="label.formForum.grid.planningId" />
					  <display:column width="10%" property="user.username" align="center" title="label.formForum.posted" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="resourceHomeForm"/>
					  <display:column width="7%" property="creationDate" align="center" title="label.formForum.grid.creation" description="label.formForum.grid.creation.desc" visibleProperty="<%=PreferenceTO.HOME_FORUMLIST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="7%" property="lastUpd" align="center" title="label.formForum.grid.lastUpd" description="label.formForum.grid.lastUpd.desc" visibleProperty="<%=PreferenceTO.HOME_FORUMLIST_SW_LSTUPD%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="5%" property="replyNumber" align="center" title="label.formForum.grid.replyNum" description="label.formForum.grid.replyNum.desc" visibleProperty="<%=PreferenceTO.HOME_FORUMLIST_SW_REPLYN%>" />
					  <display:column property="content" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.formForum.grid.content" />
				</display:table>		
				</td>
			</tr> 
			</table>

			<display:headerfootergrid type="FOOTER">
				&nbsp;
			</display:headerfootergrid>
							
		</td>
		<td width="3">&nbsp;</td>
		<logic:equal name="resourceHomeForm" property="showHideGadgetColumn" value="on">		
			<bean:write name="resourceHomeForm" property="gadgetHtmlBody" filter="false" />
		</logic:equal>
	</tr>
	</table>	   	
	
</html:form>

<jsp:include page="footer.jsp" />

<script> 

   	with(document.forms["resourceHomeForm"]){
   		<logic:notEqual name="resourceHomeForm" property="expenseReportURL" value="">	
			window.open('<bean:write name="resourceHomeForm" property="expenseReportURL" filter="false"/>', 'ExpenseReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
		</logic:notEqual>
   		expenseReportURL.value = '';
	   	<logic:equal name="resourceHomeForm" property="taskPanelStyle" value="on">
			hideClosedTaskCheckbox.checked=(hideClosedTasks.value=="on");
		</logic:equal>
		<logic:equal name="resourceHomeForm" property="requestPanelStyle" value="on">
			hideClosedCheckbox.checked=(hideClosedRequests.value=="on");
		</logic:equal>
		
		if (maximizedGadgetId.value!=null && maximizedGadgetId.value != "" ) {
			displayMessage('../do/showGadgetProperty?operation=maximize&forwardAfterSave=..%2Fdo%2FmanageResourceHome%3Foperation%3DrefreshList&gagid=' + maximizedGadgetId.value, 700, 500);
			maximizedGadgetId.value = ""; 
		} else if (showWorkflowDiagram.value=='on') {
			displayMessage("../do/showAllTask?operation=prepareWorkflow", 450, 450);
			setTimeout('centralizeDiagram()', 500);
			showWorkflowDiagram.value = 'off';
		}
	}
<%
	String anchor = request.getParameter("anchor");
	if (anchor!=null) {
		out.println("goToAnchor('" + anchor + "');");
	}
%>
</script> 