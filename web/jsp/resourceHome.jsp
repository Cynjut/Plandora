<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

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
	
	function gridComboFilterRefresh(url, param, cbName){
		refreshGrid('TSK');
	}

	function gridTextFilterRefresh(url, param, cbName){
		refreshGrid('TSK');
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
	    var pathWindow ="../do/manageHistRequest?operation=prepareForm&reqId=" + id + "&ts=" + getTs2Str();	
		window.open(pathWindow, 'reqHist', 'width=540, height=330, location=no, menubar=no, status=yes, toolbar=no, scrollbars=no, resizable=no');
    }

    function openResTaskHistPopup(taskid, resourceid) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=" + taskid + "&resourceId=" + resourceid + "&ts=" + getTs2Str();	
		window.open(pathWindow, 'reqTask', 'width=740, height=250, location=no, menubar=no, status=yes, toolbar=no, scrollbars=no, resizable=no');
    }

    function edit(id, src){
    	if (src =="REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id + "&ts=" + getTs2Str();	
    	} else if (src =="ADJUST_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id + "&ts=" + getTs2Str();	    		
    	} else if (src =="REF_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=on&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id + "&ts=" + getTs2Str();	
    	} else if (src =="ADJUST_REQ_WOUT_PRJ") {    		
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=on&readOnlyMode=off&id=" + id + "&ts=" + getTs2Str();	
    	} else if (src=="TSK") {
    		window.location = "";
    	} else if (src =="PRJ") {
	   		window.location = "../do/manageProject?operation=editProject&id=" + id + "&ts=" + getTs2Str();	
    	} else if (src =="RO_MODE") {
			window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=on&id=" + id + "&ts=" + getTs2Str();	
    	}
    }    

    function remove(id, src){
    	if (src=="REQ") {
        	if ( confirm("<bean:message key="message.confirmGiveUpRequest"/>")) {
         		window.location = "../do/manageCustRequest?operation=giveUpRequest&showBackward=on&nextFwd=home&id=" + id + "&ts=" + getTs2Str();	
         		src = "";
         	}
    	} else if (src=="TSK") {
        	if ( confirm("<bean:message key="message.confirmDeleteTask"/>")) {
         		window.location = "../do/manageResTask?operation=removeTask&nextFwd=home&task_id=" + id + "&ts=" + getTs2Str();	
         		src = "";
         	}
    	}
    }
    	
	function showAllRequirements(projId){
		window.location = "../do/showAllRequirement?operation=prepareForm&projectRelated=" + projId + "&ts=" + getTs2Str();	
	}

	function showAllTasks(projId){
		window.location = "../do/showAllTask?operation=prepareForm&projectRelated=" + projId + "&ts=" + getTs2Str();	
	}
	
	function showWorkFlow(instId, planId){
		window.location = "../do/showAllTask?operation=showWorkFlow&fwd=worflowMainForm&instanceId=" + instId + "&planningId=" + planId + "&showWorkflowDiagram=on&bgcolor=F0F0F0" + "&ts=" + getTs2Str();	
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
		window.location = "../do/manageTask?operation=prepareForm&projectId=" + projId + "&requirementId=-1" + "&ts=" + getTs2Str();
	}
	
	function newTaskByReq(projId, requirementId){
		window.location = "../do/manageTask?operation=prepareForm&projectId=" + projId + "&requirementId=" + requirementId + "&ts=" + getTs2Str();	
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
				window.location = "../do/applyTaskTemplate?operation=prepareForm&reqId=" + reqId + "&templateId=" + templId + "&ts=" + getTs2Str();	
			}
   		}
	}

	function executeTemplateByProject(projectId){
		with(document.forms["resourceHomeForm"]){
			var templId = document.getElementById("TEMPLATE_" + projectId).value;
			if (templId!=null && templId!='-1') {
				window.location = "../do/applyTaskTemplate?operation=prepareForm&reqId=-1&projId=" + projectId + "&templateId=" + templId + "&ts=" + getTs2Str();	
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
		window.location = "../do/ganttPanel?operation=prepareForm&requirementId=&projectId=" + id + "&ts=" + getTs2Str();	
	}
	
	function callResourceTask(taskId, resId, projId){
		window.location = "../do/manageResTask?operation=prepareForm&projectId=" + projId + "&resourceId=" + resId + "&taskId=" + taskId + "&ts=" + getTs2Str();	
	}

	function callExpenseForm(){
		window.location = "../do/manageExpense?operation=prepareForm" + "&ts=" + getTs2Str();	
	}

	function grabTask(taskId, projId){
		window.location = "../do/manageResTask?operation=grabTask&fwd=home&projectId=" + projId + "&taskId=" + taskId + "&ts=" + getTs2Str();	
	}

	function showBSC(projId, src){
		window.location = "../do/viewBSCPanel?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showReport(projId, src){
		window.location = "../do/viewReport?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showKB(projId){
		window.location = "../do/viewKb?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showRisk(projId){
		window.location = "../do/manageRisk?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showAgilBoard(projId){
		window.location = "../do/showAgilePanel?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showInvoice(projId){
		window.location = "../do/manageInvoice?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showCosts(projId){	
		window.location = "../do/showCostPanel?operation=prepareForm&type=PRJ&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showSurvey(projId){
		window.location = "../do/manageSurvey?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showRepository(projId){
		window.location = "../do/showRepositoryViewer?operation=prepareForm&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function showResourceCapacity(projId){
		window.location = "../do/showResCapacityPanel?operation=prepareForm&type=PRJ&projectId=" + projId + "&ts=" + getTs2Str();	
	}

	function refuseCost(cstId){
    	if ( confirm("<bean:message key="message.expense.confirmRefuseExpense"/>")) {
     		window.location = "../do/showCostPanel?operation=refuseCost&id=" + cstId + "&ts=" + getTs2Str();	
     	}
	}

	function approveCost(cstId){
		window.location = "../do/showCostPanel?operation=approveCost&id=" + cstId + "&ts=" + getTs2Str();	
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

	function showInfoMenu(infoId) {  
		var removeLink = document.getElementById("REMOVE_POST_" + infoId);
		if (removeLink!=null) {
			removeLink.style.display =  'block'; 
		}	
		
		var replyLink = document.getElementById("REPLY_POST_" + infoId);		
		if (replyLink!=null) {
			replyLink.style.display =  'block'; 
		}	
		
		var moreLink = document.getElementById("MORE_POST_" + infoId);		
		if (moreLink!=null) {
			moreLink.style.display =  'block'; 
		}	
	}

	function hideInfoMenu(infoId) {  
		var removeLink = document.getElementById("REMOVE_POST_" + infoId);
		if (removeLink!=null) {
			removeLink.style.display =  'none' ; 
		}	
		
		var replyLink = document.getElementById("REPLY_POST_" + infoId);		
		if (replyLink!=null) {
			replyLink.style.display =  'none' ; 
		}	
		
		var moreLink = document.getElementById("MORE_POST_" + infoId);		
		if (moreLink!=null) {
			moreLink.style.display =  'none' ; 
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
				<bean:write name="resourceHomeForm" property="htmlEDIIcon" filter="false"/>
			</td>
			<td valign="middle" align="right" width="20">
				<a href="javascript:displayMessage('../do/manageGadget?operation=prepareForm', 700, 440);" border="0"> 
					<img border="0" title="<bean:write name="resourceHomeForm" property="showHideGadgetLabel" filter="false"/>" alt="<bean:write name="resourceHomeForm" property="showHideGadgetLabel" filter="false"/>" align="center" src="../images/showhidechart.png" ></a>
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
				<plandora-html:ptable width="100%" name="taskList" scope="session" pagesize="<%=PreferenceTO.HOME_TASKLIST_NUMLINE%>" frm="resourceHomeForm">
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" />
					  <plandora-html:pcolumn width="2%" likeSearching="true" property="task.id" align="center" title="label.resHome.project.grid.id" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			      
					  <plandora-html:pcolumn sort="true" likeSearching="true" property="task.name" title="label.manageTask.name" decorator="com.pandora.gui.taglib.decorator.TaskDelayHiLightDecorator" tag="<%=PreferenceTO.LIST_NUMWORDS%>" />
					  <plandora-html:pcolumn width="2%" sort="true" property="classification" align="center" title="label.grid.requestform.pin.title" description="label.grid.requestform.pin.description" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_CLASSIF%>" decorator="com.pandora.gui.taglib.decorator.TaskGridPinDecorator" />
					  <plandora-html:pcolumn sort="true" width="16%" align="center" comboFilter="true" property="task.project.name" title="label.manageTask.project" description="label.manageTask.project" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_PROJECT%>" decorator="com.pandora.gui.taglib.decorator.ProjectPanelDecorator"/>
					  <plandora-html:pcolumn width="6%" property="id" align="center" title="label.resTaskForm.billable" description="label.showAllTaskForm.billable.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BILLABLE%>" decorator="com.pandora.gui.taglib.decorator.TaskBillableDecorator"/>						  
					  <plandora-html:pcolumn width="8%" property="actualDate" align="center" title="label.manageTask.grid.acualDate" description="label.manageTask.grid.acualDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <plandora-html:pcolumn width="5%" property="actualTimeInHours" align="center" title="label.manageTask.grid.actualTime" description="label.manageTask.grid.actualTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>				  
					  <plandora-html:pcolumn width="8%" property="startDate" align="center" title="label.manageTask.grid.initDate" description="label.manageTask.grid.initDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <plandora-html:pcolumn width="5%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" description="label.manageTask.grid.estTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
					  <plandora-html:pcolumn width="5%" sort="true" align="center" property="task.parentTask.id" title="label.gridParent" description="label.gridParent.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_PARENT%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="PARENT_ID"/>
					  <plandora-html:pcolumn width="10%" sort="true" comboFilter="true" property="taskStatus.name" align="center" title="label.manageTask.taskStatus" description="label.manageTask.taskStatus" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_STATUS%>" />
					  <plandora-html:pcolumn width="10%" sort="true" property="task.category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_CATEG%>" />
					  <plandora-html:pcolumn width="10%" sort="true" property="id" align="center" title="label.manageTask.grid.iteration" description="label.manageTask.grid.iteration.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_ITERAT%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" />
					  <plandora-html:pcolumn width="5%" likeSearching="true" sort="true" align="center" property="task.requirement.id" title="label.gridReqNum" description="label.gridReqNum.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_REL_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="REQ_ID"/>
					  <plandora-html:pcolumn width="5%" align="center" property="id" title="label.blockers" description="label.blockers.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BLOCKERS%>" decorator="com.pandora.gui.taglib.decorator.TaskBlockedByDecorator" />
					  <plandora-html:metafieldPcolumn property="task.additionalFields" align="center" title="title.expense.metafield" description="label.manageOption.showTaskMetaField" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_META%>" />					  						  
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridDeleteDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskLinkDecorator" />
					  <plandora-html:pcolumn width="2%" property="task.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'RES_TSK'" />
				</plandora-html:ptable>			
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
						<plandora-html:ptable width="100%" name="pendingCostList" scope="session" pagesize="<%=PreferenceTO.HOME_PENDLIST_NUMLINE%>" frm="resourceHomeForm">
						  <plandora-html:pcolumn sort="true" width="30" align="center" property="expense.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseReportDecorator" />
						  <plandora-html:pcolumn sort="true" align="center" property="name" title="label.grid.requestform.name" description="label.grid.requestform.name" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_NAME%>"/>
						  <plandora-html:pcolumn sort="true" width="120" comboFilter="true" align="center" property="project.name" title="label.grid.requestform.prj" description="label.grid.requestform.prj" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_PROJ%>" decorator="com.pandora.gui.taglib.decorator.ProjectPanelDecorator"/>
						  <plandora-html:pcolumn width="130" property="expense.creationDate" align="center" title="label.grid.requestform.creat" description="label.grid.requestform.creat" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
						  <plandora-html:pcolumn sort="true" width="110" comboFilter="true" property="expense.user.username" align="center" title="label.grid.requestform.user" description="label.grid.requestform.user" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_USER%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="resourceHomeForm" />
  						  <plandora-html:pcolumn width="70" property="firstInstallmentDate" align="center" title="label.costedit.duedate"  decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					      <plandora-html:metafieldPcolumn property="additionalFields" align="center" title="title.expense.metafield" description="title.expense.metafield.desc" visibleProperty="<%=PreferenceTO.HOME_PENDCOST_SW_META%>" />  						  
						  <plandora-html:pcolumn width="70" align="center" property="totalValue" title="label.costedit.value" decorator="com.pandora.gui.taglib.decorator.ExpenseTotalDecorator"/>
					  	  <plandora-html:pcolumn width="16" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseRefuseDecorator" />
					      <plandora-html:pcolumn width="16" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseApproveDecorator" />
						</plandora-html:ptable>		
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
					<plandora-html:ptable width="100%" name="pendRequirementList" scope="session" pagesize="<%=PreferenceTO.HOME_PENDLIST_NUMLINE%>" frm="resourceHomeForm">
					  <plandora-html:pcolumn sort="true" align="left" width="8%" property="id" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />
					  <plandora-html:pcolumn sort="true" property="description" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.requestDesc" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
					  <plandora-html:pcolumn sort="true" width="13%" align="center" comboFilter="true" property="project.name" title="label.requestProject" description="label.requestProject" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_PROJ%>" decorator="com.pandora.gui.taglib.decorator.ProjectPanelDecorator" />
					  <plandora-html:pcolumn sort="true" width="10%" property="category.name" comboFilter="true" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_CATEG%>"/>
					  <plandora-html:pcolumn width="10%" property="suggestedDate" align="center" title="label.requestSuggestedDate" description="label.requestSuggestedDate" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_SUGG%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <plandora-html:pcolumn width="12%" property="creationDate" align="center" title="grid.title.creationDate" description="grid.title.creationDate" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <plandora-html:pcolumn sort="true" width="7%" property="requester.username" align="center" title="label.requester" description="label.requester" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_REQUE%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="resourceHomeForm" />
					  <plandora-html:pcolumn sort="true" width="7%" align="center" property="priority" comboFilter="true" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />
	   				  <plandora-html:metafieldPcolumn width="16%" property="additionalFields" align="center" description="label.manageOption.showMetaField" visibleProperty="<%=PreferenceTO.HOME_PENDLIST_SW_META%>" />					  
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridRefuseDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridNewTaskDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridByTemplate" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" />
					</plandora-html:ptable>		
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
				<plandora-html:ptable width="100%" name="myRequirementList" scope="session" pagesize="<%=PreferenceTO.HOME_REQULIST_NUMLINE%>" frm="resourceHomeForm">
					  <plandora-html:pcolumn sort="true" align="left" width="8%" property="id" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.RequirementGridIdent" />			
					  <plandora-html:pcolumn sort="true" property="description" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" description="label.requestDesc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_DESC%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
					  <plandora-html:pcolumn sort="true" width="12%" comboFilter="true" align="center" property="project.name" title="label.requestProject" description="label.requestProject" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PROJ%>" decorator="com.pandora.gui.taglib.decorator.ProjectPanelDecorator" />
					  <plandora-html:pcolumn sort="true" width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_CATEG%>" />
					  <plandora-html:pcolumn sort="true" width="7%" align="center" property="priority" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />				  
					  <plandora-html:pcolumn sort="true" width="16%" comboFilter="true" align="center" property="requirementStatus.name" title="label.requestStatus" description="label.requestStatus" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_STAT%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridStatus" />
					  <plandora-html:pcolumn sort="true" width="10%" property="iteration" align="center" title="label.occurrence.iteration" description="label.manageTask.grid.iteration.req.desc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_ITERA%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="req_only"/>
	   				  <plandora-html:metafieldPcolumn width="16%" property="additionalFields" align="center" description="label.manageOption.showMetaField" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_META_FIELD%>" />					  
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" tag="'REQ'" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridDeleteDecorator" tag="'REQ'" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'REQ'" />
				</plandora-html:ptable>		
				</td>
			</tr> 
			</table>
			
			<display:headerfootergrid type="FOOTER">
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td width="150">
					<html:button property="gotoRequestForm" styleClass="button" onclick="javascript:goToForm('REQ', 1);">
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
				<plandora-html:ptable width="100%" name="myProjectList" scope="session" pagesize="<%=PreferenceTO.HOME_PROJLIST_NUMLINE%>" frm="resourceHomeForm">
					  <plandora-html:pcolumn property="name" title="label.projName" decorator="com.pandora.gui.taglib.decorator.ProjectTreeDecorator" />
					  <plandora-html:pcolumn width="10%" property="creationDate" align="center" title="label.resHome.projectCreatDate" description="label.resHome.projectCreatDate" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_CREAT%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <plandora-html:pcolumn width="10%" property="id" align="center" title="label.resHome.project.grid.lifeTipe" description="label.resHome.project.grid.lifeTipe" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_LIFE%>" decorator="com.pandora.gui.taglib.decorator.ProjectGridLifeTimeDecorator" />				  
					  <plandora-html:pcolumn width="10%" property="projectStatus.name" align="center" title="label.projStatus" description="label.projStatus" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_STAT%>" />
					  <plandora-html:pcolumn width="10%" property="roleIntoProject" title="label.role" align="center" description="label.role" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_ROLE%>" decorator="com.pandora.gui.taglib.decorator.UserRoleDecorator" />
					  <plandora-html:pcolumn width="4%" property="id" align="center" title="label.resHome.project.grid.id" description="label.resHome.project.grid.id.desc" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_ID%>"/>
				  	  <plandora-html:metafieldPcolumn property="additionalFields" align="center" title="title.expense.metafield" description="title.expense.metafield.desc" visibleProperty="<%=PreferenceTO.HOME_PROJLIST_SW_META%>" />					  
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridRiskDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridOccurrenceBookDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridSurveyDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridInvoiceDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridCostDecorator" />	
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryViewerDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridKBDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridReportDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridBSCDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridAgileBoardDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridShowTaskDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridShowReqDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridImpExpDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridGanttDecorator" />
  					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceCapacityDecorator" />					  
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridEditDecorator" tag="'PRJ'" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridNewTaskDecorator" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridByTemplate" />
				</plandora-html:ptable>		
				</td>
			</tr> 
			</table>

			<display:headerfootergrid type="FOOTER">
				&nbsp;
			</display:headerfootergrid>

			<div>&nbsp;</div>

			<!-- MY TEAM LIST -->
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.resHome.myForuns"/>
			</display:headerfootergrid>
			
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
				<plandora-html:ptable width="100%" name="teamInfoList" scope="session" pagesize="<%=PreferenceTO.HOME_TEAMLIST_NUMLINE%>" frm="resourceHomeForm" dataexport="false">
					  <plandora-html:pcolumn width="75" property="user.id" comboFilter="true" align="center" title="label.formForum.posted" decorator="com.pandora.gui.taglib.decorator.InfoTeamUserDecorator" />
					  <plandora-html:pcolumn property="comment" comboFilter="true" title="label.formForum.grid.content" decorator="com.pandora.gui.taglib.decorator.InfoTeamContentDecorator" tag="<%=PreferenceTO.LIST_NUMWORDS%>"/>
				</plandora-html:ptable>		
				</td>
			</tr> 
			</table>

			<display:headerfootergrid type="FOOTER">
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td width="150">
					<html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid('REQ');">
						<bean:message key="button.refresh"/>
					</html:button>    
				  </td>
				  <td>&nbsp;</td>
				</tr></table>
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
			displayMessage("../do/showAllTask?operation=prepareWorkflow", 800, 450);
			showWorkflowDiagram.value = 'off';
		}
	}
</script> 