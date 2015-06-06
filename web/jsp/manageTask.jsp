<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<%@ page import="com.pandora.PlanningRelationTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

	function refreshGrid(){
    	with(document.forms["taskForm"]){
    		operation.value = "refresh";
    		submit();
        }       
	}

	function searchReq(){
    	with(document.forms["taskForm"]){
   		    saveMethod.value = "0";
		    var pathWindow ="../do/searchPlanning?operation=prepareForm&type=REQ&projectId=" + projectId.value;
			window.open(pathWindow, 'searchReq', 'width=650, height=320, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');	
		}
	}

	function selectedPlanning(planId){
    	with(document.forms["taskForm"]){
			requirementId.value = planId;
			callAction(id.value, "taskForm", "refreshRequirementAfterSearch");
		}
	}

	function addResource(){
		var ok = true;
		//check if start date and estimated time was filled		
    	with(document.forms["taskForm"]){
    		if (initDate.value=="") {
    			alert("<bean:message key="message.manageTask.startDateMandatory"/>");
    			ok = false;
    		}
    		if (estimatedTime.value=="") {
    			alert("<bean:message key="message.manageTask.estTimeMandatory"/>");
    			ok = false;
    		}
			if (ok){
				buttonClick('taskForm', 'addResource');
			}
    	}			
	}
	
	function saveTask(){
		buttonClick('taskForm', 'saveTask');
	}	

	function removeResource(resId){
		with(document.forms["taskForm"]){	
			resourceId.value = resId;
			buttonClick('taskForm', 'removeResource');
		}
	}

	function setCurrentTaskParent(){
		with(document.forms["taskForm"]){
    		if (isParentTask.value == "on") {
    			isParentTask.value = "";
    			enableDisableFields(false);
    		} else {
    			isParentTask.value = "on";
    			enableDisableFields(true);
    		}
		}	
	}

	function enableDisableFields(isParent){
    	<logic:equal name="taskForm" property="showAllocField" value="on">	
			with(document.forms["taskForm"]){	
	    		if (isParent) {
	    			initDate.className='textBoxDisabled';
		   			initDate.disabled = "true";
		   			initDate.value = "";
	    			estimatedTime.className='textBoxDisabled';
	    			estimatedTime.disabled = "true";
	    			estimatedTime.value = "";
	    			resourceId.className='textBoxDisabled';
	    			resourceId.disabled = "true";
	    			addRes.disabled = "true";
	    		} else {
	    			initDate.className='textBox';
	    			initDate.disabled = "";
	    			estimatedTime.className='textBox';
	    			estimatedTime.disabled = "";
	    			resourceId.className='textBox';
	    			resourceId.disabled = "";
	    			addRes.disabled = "";
	    		}		
			}
		</logic:equal>			
	}
	
    function hideClosedkCheck(){
         with(document.forms["taskForm"]){
         	if (hideClosedTasks.value=="on"){
         		hideClosedTasks.value = "";
         	} else {
         		hideClosedTasks.value = "on";
         	}
         }             
    }
	
	function copyContent(){
		var copyIt = false;
        with(document.forms["taskForm"]){
        	if (name.value!="" || description.value!=""){
				if ( confirm("<bean:message key="message.confirmCapyContent"/>")) {
		        	copyIt = true;
	         	}    	
         	} else {
         		copyIt = true;
         	}
         	
         	if (copyIt){
         		name.value = requestDesc.value.substr(0,50);
         		description.value = requestDesc.value;
         	}
         }
	}
				
    function removeRelation(operat, planningId, relatedId, fwd, listKey) {
		window.location = "../do/manageTask?operation=" + operat + "&linkRelation=" + relatedId + "&SOURCE_ENTITY_ID=" + planningId + "&RELATION_FORWARD=" + fwd + "&COLLECTION_KEY=" + listKey;	
    }
    
    function showAllocationBox() {	
		with(document.forms["taskForm"]){	
			showAllocField.value = "on";
			buttonClick('taskForm', 'refresh');
		}
    }
	
    function openTaskHistPopup(id) {
	    var pathWindow = "../do/manageHistTask?operation=prepareForm&taskId=" + id + "&resourceId=";
		window.open(pathWindow, 'reqHist', 'width=740, height=250, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
	
	function showAllTasks(){
		var projId = '';
		with(document.forms["taskForm"]){	
			projId = projectId.value;
		}
		window.location = "../do/showAllTask?operation=prepareForm&projectRelated=" + projId;
	}
	
	function showAllRequirements(){
		var projId = '';
		with(document.forms["taskForm"]){	
			projId = projectId.value;
		}	
		window.location = "../do/showAllRequirement?operation=prepareForm&projectRelated=" + projId;
	}
	
	
</script>

<html:form  action="manageTask">

	<html:hidden name="taskForm" property="operation"/>
	<html:hidden name="taskForm" property="id"/>
	<html:hidden name="taskForm" property="projectId"/>
	<html:hidden name="taskForm" property="requirementId"/>
	<html:hidden name="taskForm" property="isParentTask"/>		
	<html:hidden name="taskForm" property="taskStatus"/>
	<html:hidden name="taskForm" property="saveMethod"/>
	<html:hidden name="taskForm" property="hideClosedTasks"/>	
	<html:hidden name="taskForm" property="showWarnPopup"/>	
	<html:hidden name="taskForm" property="showAllocField"/>
	<html:hidden name="taskForm" property="showCloseReqConfirmation"/>
   	<logic:equal name="taskForm" property="showAllocField" value="off">
		<html:hidden name="taskForm" property="resourceId"/>
	</logic:equal>
	
	<plandora-html:shortcut name="taskForm" property="goToTaskForm" fieldList="projectId"/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>

		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.task"/>
		</display:headerfootergrid>
	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150">&nbsp; </td>
	      <td width="280">&nbsp;</td>
		  <td width="85">&nbsp; </td>
		  <td>&nbsp; </td>		  
	      <td width="10">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageTask.name"/>:&nbsp;</td>
	      <td class="formBody">
	        <html:text name="taskForm" property="name" styleClass="textBox" size="50" maxlength="50"/>
	      </td>
	      <td class="formTitle"><bean:message key="label.manageTask.category"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="taskForm" property="categoryId" styleClass="textBox">
				<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>
	      </td>	      
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">	
    	   <td>&nbsp;</td>
       	   <td class="formTitle"><bean:message key="label.manageTask.desc"/>:&nbsp;</td>    
	       <td class="formBody" colspan="3">
    	     <html:textarea name="taskForm" property="description" styleClass="textBox" cols="88" rows="3" />
       	   </td>
           <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">	
    	   <td>&nbsp;</td>
       	   <td class="formTitle"><bean:message key="label.manageTask.taskParent"/>:&nbsp;</td>    
	       <td class="formBody" colspan="3">
		  		<html:select name="taskForm" property="parentTaskId" styleClass="textBox">
		             <option value="-1">
		                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		             </option>		  		
					<html:options collection="taskAvailable" property="id" labelProperty="name" filter="false"/>
				</html:select>
       	   </td>
           <td>&nbsp;</td>
	    </tr>	    
	    </table>

		<table width="98%" border="0" cellspacing="0" cellpadding="0">	    
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150" class="formTitle"><bean:message key="label.manageTask.relatReq"/>:&nbsp;</td>
	      <td class="formBody">
	        <html:text name="taskForm" property="requestNum" styleClass="textBox" size="5" disabled="true" />
			<html:text name="taskForm" property="requestSuggDate" styleClass="textBox" size="10" disabled="true" />
	        <html:text name="taskForm" property="requestCustomer" styleClass="textBox" size="37" disabled="true" />
			<html:button property="search" styleClass="button" onclick="javascript:searchReq();">
				<bean:message key="button.search"/>
			</html:button>
			<html:button property="clearSearch" styleClass="button" onclick="javascript:buttonClick('taskForm', 'clearSearch');">
				<bean:message key="button.clearSearch"/>
			</html:button>			
			<html:button property="copy" styleClass="button" onclick="javascript:copyContent();">
				<bean:message key="button.manageTask.copyContent"/>
			</html:button>									
	      </td>
	      <td width="10">&nbsp;</td>
	    </tr> 	    
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	      <td class="formBody">
	      	<html:textarea name="taskForm" property="requestDesc" styleClass="textBox" cols="88" rows="3" readonly="true" />
	      </td>
	      <td>&nbsp;</td>
	    </tr> 	    	    
	    <tr class="gapFormBody">
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	    </tr> 
	    
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	      <td>
	      
			<table width="80%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		    	<td width="10">&nbsp;</td>
		      	<td><bean:message key="title.taskAlloc"/></td>
		      	<td width="10">&nbsp;</td>
		    </tr>
		    <tr class="gapFormBody">
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		    </tr> 
		    
		    </table>
		    
			<table width="80%" border="0" cellspacing="0" cellpadding="0">
			<tr class="pagingFormBody">
				<td width="10">&nbsp;</td>	
				<td width="120">&nbsp;</td>
			    <td class="formBody" colspan="4">
		    	    <html:checkbox name="taskForm" property="isParentTaskCheckbox" styleClass="textBox" onclick="javascript:setCurrentTaskParent();"/><bean:message key="label.manageTask.IsParentTask"/>
		      	</td>
				<td width="10">&nbsp;</td>
			</tr>
			</table>

		   	<logic:equal name="taskForm" property="showAllocField" value="on">
				<table width="80%" border="0" cellspacing="0" cellpadding="0">
				<tr class="pagingFormBody">
					<td width="10">&nbsp;</td>	
					<td width="120" class="formTitle"><bean:message key="label.manageTask.initDate"/>:&nbsp;</td>
				    <td width="120" class="formBody">
				        <plandora-html:calendar name="taskForm" property="initDate" styleClass="textBoxDisabled" />		    	    
			      	</td>
			      	<td width="10">&nbsp;</td>
			      	<td width="120" class="formTitle"><bean:message key="label.manageTask.estTime"/>:&nbsp;</td>
				    <td class="formBody">
			    	    <html:text name="taskForm" property="estimatedTime" styleClass="textBox" size="5" maxlength="5"/> <bean:message key="label.hour"/>
			      	</td>	      	
					<td width="10">&nbsp;</td>
				</tr>	
				</table>
							
				<table width="80%" border="0" cellspacing="0" cellpadding="0">			
				<tr class="pagingFormBody">
					<td width="10">&nbsp;</td>	
					<td width="120" class="formTitle"><bean:message key="label.manageTask.projResource"/>:&nbsp;</td>
				    <td class="formBody">
				  		<html:select name="taskForm" property="resourceId" styleClass="textBox">
							<html:options collection="resourceAvailable" property="id" labelProperty="name" filter="false"/>
						</html:select>
		      		</td>
					<td width="10">&nbsp;</td>
				</tr>
				<tr class="pagingFormBody">
					<td>&nbsp;</td>	
					<td>&nbsp;</td>
				    <td class="formBody">
						<html:button property="addRes" styleClass="button" onclick="javascript:addResource();">
							<bean:message key="button.addInto"/>
						</html:button>
		      		</td>
					<td>&nbsp;</td>
				</tr>
				</table>
		   	</logic:equal>
		   	       		
			<logic:notEmpty name="resourceAllocated">
			<table width="80%" border="0" cellspacing="0" cellpadding="0">						
				<tr class="pagingFormBody">
					<td width="10">&nbsp;</td>	
					<td width="120">&nbsp;</td>
					<td class="formBody">
						<plandora-html:ptable width="80%" name="resourceAllocated" scope="session" frm="taskForm">
							  <plandora-html:pcolumn property="label" title="label.name" />					  
							  <plandora-html:pcolumn width="30%" property="startDate" align="center" title="label.manageTask.initDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
							  <plandora-html:pcolumn width="10%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
							  <plandora-html:pcolumn width="2%" property="resource.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskRemoveDecorator" />
						</plandora-html:ptable>			    
					</td>
					<td width="10">&nbsp;</td>
				</tr>
			</table>				
			</logic:notEmpty>
			
		   	<logic:equal name="taskForm" property="showAllocField" value="off">       				
			<table width="80%" border="0" cellspacing="0" cellpadding="0">		   	
			<tr class="pagingFormBody">
				<td width="10">&nbsp;</td>	
				<td width="120">&nbsp;</td>
			    <td class="formBody">
					<html:button property="showBox" styleClass="button" onclick="javascript:showAllocationBox();">
						<bean:message key="label.more"/>
					</html:button>
	      		</td>
				<td width="10">&nbsp;</td>
			</tr>
			</table>
       		</logic:equal>

		   	<logic:equal name="taskForm" property="showWarnPopup" value="on">       				
			<table width="70%" border="0" cellspacing="0" cellpadding="0">   	    
			<tr class="gapFormBody">
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr class="formNotes">
				<td width="130">&nbsp;</td>
			    <td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="message.warnUpdateTaskStatus"/></div></td>
			</tr> 	    
			</table>
       		</logic:equal>
			
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="gapFormBody">
	      <td>&nbsp;</td>
	      <td>&nbsp; </td>
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	    </tr>    
		</table>
		
		<logic:notEqual name="taskForm" property="id" value="">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
	  	    <td width="10">&nbsp;</td>
	      	<td width="150" class="formTitle">&nbsp;</td>
			<td>
				<table width="55%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:relationship name="taskForm" entity="<%=PlanningRelationTO.ENTITY_TASK%>" property="id" projectProperty="projectId" collection="taskRelationshipList" forward="showTask" removeFunction="removeRelation"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>
	    <tr class="gapFormBody">
	      <td colspan="4">&nbsp;</td>
	    </tr>    		
		</table>
		</logic:notEqual>
		

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:saveTask();">
					<bean:write name="taskForm" property="saveLabel" filter="false"/>
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('taskForm', 'clear');">
					<bean:message key="button.new"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="showAllReq" styleClass="button" onclick="javascript:showAllRequirements();">
					<bean:message key="label.showAllTaskForm.projreqs"/>
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="showAllTsk" styleClass="button" onclick="javascript:showAllTasks();">
					<bean:message key="label.showAllTaskForm.projtasks"/>
				  </html:button>    
			  </td>	      	      	      
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('taskForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
		</display:headerfootergrid>
		  	
		<div>&nbsp;</div>

		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.taskList"/>
		</display:headerfootergrid>
				    
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<plandora-html:ptable width="100%" name="manageTaskList" scope="session" pagesize="7" frm="taskForm" >
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" />
					  <plandora-html:pcolumn width="5%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />
					  <plandora-html:pcolumn property="name" title="label.manageTask.name" decorator="com.pandora.gui.taglib.decorator.TaskTreeDecorator" />
					  <plandora-html:pcolumn width="25%" property="id" align="center" title="label.manageTask.taskStatus" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" tag="LABEL" />
					  <plandora-html:pcolumn width="15%" property="category.name" align="center" title="label.manageTask.category" />
					  <plandora-html:pcolumn width="10%" property="id" align="center" title="label.manageTask.grid.iteration" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" />						  
					  <plandora-html:pcolumn width="5%" align="center" property="requirementId" title="label.gridReqNum" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />						  
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'taskForm', 'editTask'" />
					  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.CancelTaskDecorator" />						  
				</plandora-html:ptable>		
			</td>
		</tr> 
		</table>
		      	
		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		      <td width="120">      
			      <html:button property="refresh" styleClass="button" onclick="javascript:refreshGrid();">
		    	    <bean:message key="button.refresh"/>
			      </html:button>    
		      </td>
		      <td>&nbsp;</td>
		      <td class="textBoxOverTitleArea">
		        <input type="checkbox" name="hideClosedCheckbox" onclick="javascript:hideClosedkCheck();"/><bean:message key="label.manageTask.taskHideClosed"/>
		      </td>		      
		    </tr></table>  	
		</display:headerfootergrid>
		
	</td><td width="20">&nbsp</td>
	
	<tr><td width="10">&nbsp</td>
	<td>
		<!-- set empty spaces -->
		<br><br><br>
	</td>
	<td width="20">&nbsp</td>
			
  	</tr></table>
</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
   	with(document.forms["taskForm"]){	
   		name.focus();	
        enableDisableFields(isParentTask.value=="on");
       	hideClosedCheckbox.checked=(hideClosedTasks.value=="on");

		if (showCloseReqConfirmation.value=='on') {
			displayMessage("../do/manageTask?operation=showCloseReqConfirmation", 580, 130);
		}
	}
</script>