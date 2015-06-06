<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<%@ page import="com.pandora.PreferenceTO"%>
<%@ page import="com.pandora.PlanningRelationTO"%>
<%@ page import="com.pandora.TaskStatusTO"%>
<jsp:include page="header.jsp" />

<script language="javascript">

    function hideClosedCheck(){
         with(document.forms["resTaskForm"]){
         	if (hideClosedTasks.value=="on"){
         		hideClosedTasks.value = "";
         	} else {
         		hideClosedTasks.value = "on";
         	}
         }
    }
        
	function callResourceTask(tskId, resId, projId){
		window.location = "../do/manageResTask?operation=prepareForm&projectId=" + projId + "&resourceId=" + resId + "&taskId=" + tskId;
	}


    function openTaskHistPopup(id) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=" + id + "&resourceId=";
		window.open(pathWindow, 'reqHist', 'width=740, height=250, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

    function changeProject() {
	  	buttonClick("resTaskForm", "refreshProject");    	    
    }
    
    function changeProjectResTask(selectedProjectId) {
    	with(document.forms["resTaskForm"]){
			window.location="../do/manageResTask?operation=changeProject&id=" + selectedProjectId;
		}
    }

	function changeStatus() {
	    <logic:equal name="resTaskForm" property="showDecisionQuestion" value="on">
			with(document.forms["resTaskForm"]){
				var qt = document.getElementById( 'question_text' );			
				if(taskStatus.value=='<%=TaskStatusTO.STATE_MACHINE_CLOSE.toString()%>') {
					qt.style.display =  'block'; 
					behidden = false;  
				} else {
					qt.style.display =  'none' ; 
					behidden = true ;
				}
			}
	    </logic:equal>
	}

    function changeCategory() {
	  	buttonClick("resTaskForm", "refreshCategory");    	    
    }
    
	function selectedPlanning(planId){
    	with(document.forms["resTaskForm"]){
			linkRelation.value = planId;
		}
	}
    
    function removeRelation(operat, planningId, relatedId, fwd, listKey) {
		window.location = "../do/manageResTask?operation=" + operat + "&linkRelation=" + relatedId + "&SOURCE_ENTITY_ID=" + planningId + "&RELATION_FORWARD=" + fwd + "&COLLECTION_KEY=" + listKey;	
    }

    function remove(id, src){
    	if (src=="TSK") {
        	if ( confirm("<bean:message key="message.confirmDeleteTask"/>")) {
         		window.location = "../do/manageResTask?operation=removeTask&nextFwd=showResTask&task_id=" + id;
         		src = "";
         	}
    	}
    }
    
	function showWorkFlow(instId, planId){
		window.location = "../do/showAllTask?operation=showWorkFlow&fwd=worflowResTaskForm&instanceId=" + instId + "&planningId=" + planId + "&showWorkflowDiagram=on&bgcolor=F0F0F0";
	}
	
	function clickNodeTemplate(instId, planId){
		with(document.forms["resTaskForm"]){
    		closeFloatPanel();		
   			operation.value = "clickNodeTemplate";
    		planningId.value = planId;
   		}
		ajaxProcess(document.forms["resTaskForm"], callBackNodeTemplateClick, instId, planId);		
	}
	
				
	function callBackNodeTemplateClick(instId, planId) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			openFloatPanel(content);   	
	    }  
	}
	
	    
    function jumpToSlot(slot) {
    	window.location = "../do/manageResTask?operation=jumpToSlot&newslot=" + slot;
    }

    function saveResTask() {
        with(document.forms["resTaskForm"]){
		    <logic:equal name="resTaskForm" property="isAdHocTask" value="on">	  
				if (categoryId.value=='0' && categoryId.length>1) {
					if (confirm("<bean:message key="message.confirmChangeCategory"/>")) {
						buttonClick("resTaskForm", "saveResTaskPreProcess");
					}			
				} else {
					buttonClick("resTaskForm", "saveResTaskPreProcess");
				}
			</logic:equal>
		    <logic:equal name="resTaskForm" property="isAdHocTask" value="off">	  
				buttonClick("resTaskForm", "saveResTaskPreProcess");
			</logic:equal>
        }
    }
    
	function grabTask(taskId, projId){
		window.location = "../do/manageResTask?operation=grabTask&fwd=&projectId=" + projId + "&taskId=" + taskId;	
	}

	function showTaskReport(){
		window.location = "../do/manageResTask?operation=showTaskReport";	
	}


	function goToForwardPage(){
	  	buttonClick("resTaskForm", "refreshArtifacts");  
	}

	function gridComboFilterRefresh(url, param, cbName){
		javascript:buttonClick('resTaskForm', 'refresh');
	}

	function gridTextFilterRefresh(url, param, cbName){
		javascript:buttonClick('resTaskForm', 'refresh');
	}

	function showProjectPopup(){
		displayMessage("../do/manageResTask?operation=showProjectPopup", 350, 150);
	}

</script>

<html:form  action="manageResTask">
	<html:hidden name="resTaskForm" property="uid"/>
	<html:hidden name="resTaskForm" property="operation"/>
	<html:hidden name="resTaskForm" property="id"/>	
	<html:hidden name="resTaskForm" property="saveMethod"/>
	<html:hidden name="resTaskForm" property="hideClosedTasks"/>
	<html:hidden name="resTaskForm" property="resourceId"/>			
	<html:hidden name="resTaskForm" property="taskId"/>	
	<html:hidden name="resTaskForm" property="allocCursor"/>
	<html:hidden name="resTaskForm" property="firstAllocSlot"/>
	<html:hidden name="resTaskForm" property="strDecimalInput"/>
	<html:hidden name="resTaskForm" property="reportTaskURL"/>
	<html:hidden name="resTaskForm" property="showCloseMacroTaskConfirm"/>
	<html:hidden name="resTaskForm" property="showWorkflowDiagram"/>
	<html:hidden name="resTaskForm" property="planningId"/>
   	<logic:equal name="resTaskForm" property="isAdHocTask" value="off">
		<html:hidden name="resTaskForm" property="projectId"/>		
	</logic:equal>	
	
	<!-- from HosterRepositoryForm... -->
	<html:hidden name="resTaskForm" property="genericTag"/>
	<html:hidden name="resTaskForm" property="onlyFolders"/>
	<html:hidden name="resTaskForm" property="multiple"/>
	
	<plandora-html:shortcut name="resTaskForm" property="goToResTaskForm" fieldList="projectId,categoryId"/>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.resTask"/> 
		<logic:present name="resTaskForm" property="reqCustomerName">
			&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; <bean:message key="label.resTaskForm.customername" />: <bean:write name="resTaskForm" property="reqCustomerName" filter="false"/>
		</logic:present>
	</display:headerfootergrid>

	<logic:equal name="resTaskForm" property="showDecisionQuestion" value="on">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">	
		<tr class="pagingFormBody">
			<td width="10">&nbsp;</td>
			<td width="195">&nbsp;</td>
			<td width="400" class="formBody">
				<div id="question_text">
						<br /><b><bean:write name="resTaskForm" property="questionText" filter="false"/></b><br />
						<input type="radio" name="questionAnswer" value="1"> <bean:message key="label.yes"/> 
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" name="questionAnswer" value="0"> <bean:message key="label.no"/>
						<br /><br />
				</div>
			</td>
			<td>&nbsp;</td>
		</tr>	
		</table>
	</logic:equal>
		
	  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="195">&nbsp;</td>
	      <td width="240">&nbsp;</td>
	      <td width="70">&nbsp;</td>
	      <td>&nbsp;</td>
	      <td width="10">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageTask.project"/>:&nbsp;</td>
	      <td class="formBody">
    			<logic:equal name="resTaskForm" property="isAdHocTask" value="on">	      
					<html:select name="resTaskForm" property="projectId" styleClass="textBox" onkeypress="javascript:refreshProject();" onchange="javascript:changeProject();">
						<html:options collection="projectList" property="id" labelProperty="name" filter="false"/>
					</html:select>
				</logic:equal>	
    			<logic:equal name="resTaskForm" property="isAdHocTask" value="off">
    				<table border="0" cellspacing="0" cellpadding="0">
    					<tr>
    						<td>
								<html:select name="resTaskForm" property="projectId" styleClass="textBoxDisabled" disabled="true">
									<html:options collection="projectList" property="id" labelProperty="name" filter="false"/>
								</html:select>
							</td>
							<logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="on">
								<td width="10">&nbsp;</td>
								<td width="20" valign="bottom" align="left" class="tableCellAction">
									<a href="javascript:showProjectPopup();">
										<img src="../images/prefresh.png" border="0" title="<bean:message key="label.changeProject"/>" alt="<bean:message key="label.changeProject"/>"/>
									</a>
								</td>
							</logic:equal>
						</tr>  
					</table>
				</logic:equal>				
	  	  </td>
	      <td class="formTitle"><bean:message key="label.manageTask.category"/>:&nbsp;</td>
	      <td class="formBody">
    			<logic:equal name="resTaskForm" property="isAdHocTask" value="on">	      
		  			<html:select name="resTaskForm" property="categoryId" styleClass="textBox" onkeypress="javascript:changeCategory();" onchange="javascript:changeCategory();">
						<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
					</html:select>
				</logic:equal>
    			<logic:equal name="resTaskForm" property="isAdHocTask" value="off">	      
					<logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="on">
			  			<html:select name="resTaskForm" property="categoryId" styleClass="textBox" onkeypress="javascript:changeCategory();" onchange="javascript:changeCategory();">
							<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
						</html:select>					
					</logic:equal>
					<logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="off">
			  			<html:select name="resTaskForm" property="categoryId" styleClass="textBoxDisabled" disabled="true">
							<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
						</html:select>					
					</logic:equal>					
				</logic:equal>									
	      </td>	      
	      <td>&nbsp;</td>      	      
		</tr>	
	</table>		
	
  	    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">	
	<tr class="gapFormBody">
	  <td width="10">&nbsp;</td>
	  <td width="195">&nbsp;</td>
	  <td>&nbsp;</td>
	  <td width="10">&nbsp;</td>
	</tr>
  	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.manageTask.name"/>:&nbsp;</td>
      <td class="formBody">
      	<logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="off">
        	<html:text name="resTaskForm" property="taskName" styleClass="textBoxDisabled" size="50" maxlength="50" disabled="true"/>
		</logic:equal>        	
        <logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="on">
        	<html:text name="resTaskForm" property="taskName" styleClass="textBox" size="50" maxlength="50" />
        </logic:equal>
	  </td>
      <td>&nbsp;</td>
	</tr>
    <tr class="pagingFormBody">	
	   <td>&nbsp;</td>
   	   <td class="formTitle"><bean:message key="label.manageTask.desc"/>:&nbsp;</td>    
       <td class="formBody">
       	 <logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="off">
	     	<html:textarea name="resTaskForm" property="taskDesc" styleClass="textBoxDisabled" cols="80" rows="5" readonly="true"/>
		</logic:equal>        	
        <logic:equal name="resTaskForm" property="isCurrentTaskCreator" value="on">
        	<html:textarea name="resTaskForm" property="taskDesc" styleClass="textBox" cols="80" rows="5"/>
        </logic:equal>	     	
   	   </td>
       <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">	
    	<td>&nbsp;</td>
       	<td class="formTitle"><bean:message key="label.manageTask.taskParent"/>:&nbsp;</td>    
	    <td class="formBody">
		  	<html:select name="resTaskForm" property="parentTaskId" styleClass="textBox">
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
			<td width="195" class="formTitle"><bean:message key="label.manageTask.grid.iteration"/>:&nbsp;</td>
			<td width="240" class="formBody">
				<html:select name="resTaskForm" property="iterationId" styleClass="textBox">
					<option value="-1">
					   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</option>		  				  	
					<html:options collection="iterationList" property="id" labelProperty="name" filter="false"/>
				</html:select>
			</td>
			<logic:equal name="resTaskForm" property="allowBillable" value="on">
				<td width="70" class="formTitle"><bean:message key="label.resTaskForm.billable"/>:&nbsp;</td>
				<td width="150" class="formBody">
					<html:select name="resTaskForm" property="billableStatus" styleClass="textBox">
						<html:options collection="billableStatusList" property="id" labelProperty="genericTag" filter="false"/>
					</html:select>
				</td>
			</logic:equal>
			<logic:equal name="resTaskForm" property="allowBillable" value="off">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</logic:equal>
			<td>&nbsp;</td>
		</tr>	
	</table>

	
	<plandora-html:metafield name="resTaskForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="205" forward="manageResTask?operation=navigate"/>    
	      
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<logic:equal name="resTaskForm" property="isAdHocTask" value="off">
	    	<tr class="pagingFormBody">
	      		<td width="10">&nbsp;</td>
	     	 	<td width="195" class="formTitle"><bean:message key="label.resTaskForm.initDate"/>:&nbsp;</td>
		      	<td width="100" class="formBody">
		      		<bean:write name="resTaskForm" property="estimDate" filter="false"/>
			  	</td>
		        <td width="30">&nbsp;</td>
          		<td width="80" class="formTitle"><bean:message key="label.manageTask.taskStatus"/>:&nbsp;</td>
      	  		<td width="150" class="formBody">
					<html:select name="resTaskForm" property="taskStatus" styleClass="textBox" onkeypress="javascript:changeStatus();" onchange="javascript:changeStatus();">
						<html:options collection="taskStatusList" property="stateMachineOrder" labelProperty="name" filter="false"/>
					</html:select>
		  		</td>
	      		<td>&nbsp;</td>
			</tr>	
 		</logic:equal>		

	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>	      
		  <logic:equal name="resTaskForm" property="isAdHocTask" value="off">
	      	  		<td width="195" class="formTitle"><bean:message key="label.resTaskForm.actualDate"/>:&nbsp;</td>		  
		      		<td width="200" class="formBody">
		      			<bean:write name="resTaskForm" property="actualDateFormat" filter="false"/>
			  		</td>
			  		<td colspan="4">&nbsp;</td>
 		  </logic:equal>
		  <logic:equal name="resTaskForm" property="isAdHocTask" value="on">
          	  	<td width="195" class="formTitle"><bean:message key="label.manageTask.taskStatus"/>:&nbsp;</td>
      	  		<td width="200" class="formBody">
					<html:select name="resTaskForm" property="taskStatus" styleClass="textBox">
						<html:options collection="taskStatusList" property="stateMachineOrder" labelProperty="name" filter="false"/>
					</html:select>
		  		</td>
			  	<td conspan="4">&nbsp;</td>
 		  </logic:equal>
		</tr>	
	</table>


	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td colspan="4">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="195" height="33">&nbsp;</td>
		  <logic:equal name="resTaskForm" property="isAdHocTask" value="off">
			<logic:equal name="resTaskForm" property="containEstimatedValues" value="on">
				<td class="formBody" rowspan="4" width="580">
			 </logic:equal>
		  </logic:equal>
		  
	      <logic:equal name="resTaskForm" property="containEstimatedValues" value="off">
			<logic:equal name="resTaskForm" property="isAdHocTask" value="off">
				<td class="formBody" rowspan="3" width="580">
			</logic:equal>
		  </logic:equal>
	      
	      <logic:equal name="resTaskForm" property="containEstimatedValues" value="off">
			<logic:equal name="resTaskForm" property="isAdHocTask" value="on">
				<td class="formBody" rowspan="3" width="580">
			</logic:equal>
		  </logic:equal>
	      	
	      	<table  class="table" width="100%" border="0" cellspacing="1" cellpadding="2">
	      		<tr class="rowHighlight">
	      			  <th width="70" align="center" class="tableCellHeader">&nbsp;Total&nbsp;</th>
	      			  <th width="10" class="pagingFormBody">&nbsp;</th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle1" filter="false"/></th>	      			  
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle2" filter="false"/></th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle3" filter="false"/></th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle4" filter="false"/></th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle5" filter="false"/></th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle6" filter="false"/></th>
	      		      <th width="75" align="center" class="tableCellHeader"><bean:write name="resTaskForm" property="slotTitle7" filter="false"/></th>
				</tr>
				
      			<logic:equal name="resTaskForm" property="isAdHocTask" value="off">
      				<logic:equal name="resTaskForm" property="containEstimatedValues" value="on">
	      		   		<tr class="pagingFormBody">
	      		      		<td class="formBody">
					        	<html:text name="resTaskForm" property="estimTime" styleClass="textBoxDisabled" size="2" disabled="true"/> <bean:message key="label.hour"/>
		  			  		</td>
	      		      		<td class="pagingFormBody">&nbsp;</td>
		  			  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop1" slot="1" line="1"/>		  			  
		  			  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop2" slot="2" line="1"/>
		  			  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop3" slot="3" line="1"/>
		  			  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop4" slot="4" line="1"/>
					  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop5" slot="5" line="1"/>
					  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop6" slot="6" line="1"/>
		  			  		<plandora-html:taskgridcell name="resTaskForm" property="slotTop7" slot="7" line="1"/>
				   		</tr>
				   	</logic:equal>
				</logic:equal>
				
				<tr class="pagingFormBody">	  			  
				      <td class="formBody">
	        			<html:text name="resTaskForm" property="actualTime" styleClass="textBoxDisabled" size="2" disabled="true"/> <bean:message key="label.hour"/>
		  			  </td>
		  			  <td class="pagingFormBody">&nbsp;</td>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton1" slot="1" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton2" slot="2" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton3" slot="3" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton4" slot="4" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton5" slot="5" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton6" slot="6" line="2"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton7" slot="7" line="2"/>		  			  		  			  
	      		</tr>	
	      		
	      		<tr class="pagingFormBody">	  			  
				      <td class="pagingFormBody">&nbsp;</td>
		  			  <td class="pagingFormBody">&nbsp;</td>

		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton1" slot="1" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton2" slot="2" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton3" slot="3" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton4" slot="4" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton5" slot="5" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton6" slot="6" line="3"/>
		  			  <plandora-html:taskgridcell name="resTaskForm" property="slotBotton7" slot="7" line="3"/>		  			  		  			  
	      		</tr>				
	      	</table>
	      </td>
	      <td>&nbsp;</td>
		</tr>
		<logic:equal name="resTaskForm" property="containEstimatedValues" value="on">
			<tr class="pagingFormBody" height="25">
				<td>&nbsp;</td>		
				<td class="formTitle">
					<bean:message key="label.resTaskForm.estTime"/>:&nbsp;
				</td>					
				<td>&nbsp;</td>
			</tr>
		</logic:equal>
		
		<logic:equal name="resTaskForm" property="containEstimatedValues" value="off">
			<logic:equal name="resTaskForm" property="isAdHocTask" value="on">
				<tr class="pagingFormBody" height="25">
					<td>&nbsp;</td>		
					<td class="formTitle">
						<bean:message key="label.resTaskForm.estTime"/>:&nbsp;
					</td>					
					<td>&nbsp;</td>
				</tr>
			</logic:equal>
		</logic:equal>
		
	    <logic:equal name="resTaskForm" property="isAdHocTask" value="off">			
		    <tr class="pagingFormBody" height="25">
		    	<td>&nbsp;</td>
		    	<td class="formTitle"><bean:message key="label.resTaskForm.actualTime"/>:&nbsp;</td>
		      	<td>&nbsp;</td>
		    </tr>  
		</logic:equal>
		<tr class="pagingFormBody" height="25">
	    	<td>&nbsp;</td>
	    	<td class="formTitle"><bean:message key="label.resTaskForm.allocTime"/>:&nbsp;</td>
	      	<td>&nbsp;</td>
	    </tr>  	    
	    <tr class="pagingFormBody">
	    	<td>&nbsp;</td>
	    	<td>&nbsp;</td>
	    	<td class="tableCellHeader">
	      	    <table width="100%">
	      		<tr>
	      			<td align="center" width="40">
						<b><html:button property="previous" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'previous');">
						  &nbsp;&nbsp;<bean:message key="label.resTaskForm.prevWeek"/>&nbsp;&nbsp;
						</html:button></b>
		      	 	</td>
	      			<td align="center" width="40">
						<b><html:button property="previousDay" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'previousDay');">
						  &nbsp;&nbsp;&nbsp;<bean:message key="label.resTaskForm.prevDay"/>&nbsp;&nbsp;&nbsp;
						</html:button></b>
		      	 	</td>
		      	 	
		      		<td align="center">
					  	<html:button property="showHileAlloc" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'showHideAllocatedDays');">
					  		<logic:equal name="resTaskForm" property="showAllocatedDays" value="off">
					  			<bean:message key="label.resTaskForm.grid.ShowAlloc"/>
					  		</logic:equal>
					  		<logic:equal name="resTaskForm" property="showAllocatedDays" value="on">
					  			<bean:message key="label.resTaskForm.grid.HideAlloc"/>
					  		</logic:equal>						  		
      					</html:button>
		      		</td>
		      		<td align="center" width="40">
						  <b><html:button property="nextDay" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'nextDay');">
				    	    &nbsp;&nbsp;&nbsp;<bean:message key="label.resTaskForm.nextDay"/>&nbsp;&nbsp;&nbsp;
					      </html:button></b>    
		      		</td>
		      		<td align="center" width="40">
						  <b><html:button property="next" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'next');">
				    	    &nbsp;&nbsp;<bean:message key="label.resTaskForm.nextWeek"/>&nbsp;&nbsp;
					      </html:button></b>    
		      		</td>
		      		
		      	</tr>
	      	    </table>
	         </td>
	         <td>&nbsp;</td>
		</tr>
	    <tr class="pagingFormBody">
	    	<td>&nbsp;</td>
	    	<td>&nbsp;</td>
	      	<td>
	      	    <table width="100%" border="0">
				<tr>
					<td width="70%">&nbsp;</td>
					<td width="10">
						<table width="100%" cellspacing="0" cellpadding="0"><td><td class="tableCellHeader">&nbsp;</td></tr></table>
					</td>
					<td class="formNotes" align="left"><bean:message key="label.resTaskForm.grid.legend.1"/></td>
					<td width="10">
						<table width="100%" cellspacing="0" cellpadding="0"><td><td class="tableCellHeaderHighlight">&nbsp;</td></tr></table>
					</td>
					<td class="formNotes" align="left"><bean:message key="label.resTaskForm.grid.legend.2"/></td>
				</tr>
				</table>
			</td>
			<td>&nbsp;</td>
	    </tr>  		
	</table>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
		  <logic:equal name="resTaskForm" property="isAdHocTask" value="off">	      
		      <td width="195"class="formTitle"><bean:write name="resTaskForm" property="commentLabel" filter="false"/>:&nbsp;</td>
		      <td class="formBody">
			  	<html:textarea name="resTaskForm" property="comment" styleClass="textBox" cols="80" rows="4" />
			  </td>
		  </logic:equal>
		  <logic:equal name="resTaskForm" property="isAdHocTask" value="on">
		      <td width="195">&nbsp;</td>
		      <td><html:hidden name="resTaskForm" property="comment" value=""/></td>
		  </logic:equal>		  			  
	      <td width="10">&nbsp;</td>
		</tr>	
    </table>
    
    <logic:notEqual name="resTaskForm" property="taskId" value="">
    	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
			<tr class="pagingFormBody">
				<td width="10">&nbsp;</td>
				<td width="195" class="formTitle">&nbsp;</td>
				<td>
					<table width="55%" border="0" cellspacing="0" cellpadding="0">
						<tr class="gapFormBody">
							<td colspan="4">&nbsp;</td>
						</tr>		
			  			<plandora-html:attachment name="resTaskForm" collection="taskAttachmentList" removedForward="refreshTaskAfterAttach"/> 	
					</table> 
				</td>
				<td width="10">&nbsp;</td>
			</tr>
		</table>    
    </logic:notEqual>

    <logic:notEqual name="resTaskForm" property="taskId" value="">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
    	    <td width="10">&nbsp;</td>
        	<td width="195" class="formTitle">&nbsp;</td>
			<td>
				<table width="55%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:relationship name="resTaskForm" entity="<%=PlanningRelationTO.ENTITY_TASK%>" property="taskId" projectProperty="projectId" collection="relationshipList" forward="showResTask" removeFunction="removeRelation"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>		
		<tr class="pagingFormBody">
    	    <td width="10">&nbsp;</td>
        	<td width="195" class="formTitle">&nbsp;</td>
			<td>
				<table width="55%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:artifact name="resTaskForm" property="taskId" projectProperty="projectId" collection="repositoryList"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>		
		</table>
	</logic:notEqual>
	    	
	<logic:equal name="resTaskForm" property="canSeeDiscussion" value="on">
	<table width="98%" border="0" cellspacing="0" cellpadding="0">	    
		<tr class="pagingFormBody">
			<td width="20">&nbsp;</td>
			<td>
				<logic:notEqual name="resTaskForm" property="taskId" value="">
					<plandora-html:discussiontopic name="resTaskForm" entityId="taskId" collection="discussionTopicList" action="manageResTask" forward="manageResTask?operation=refreshAfterTopicDiscussion" />
				</logic:notEqual>
			</td>
			<td width="10">&nbsp;</td>
		</tr>
	</table>		
	</logic:equal>   
 	
	
    <logic:equal name="resTaskForm" property="showAllocatedDays" value="on">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="gapFormBody">
		      <td colspan="4">&nbsp;</td>
		    </tr>		
		    <tr class="pagingFormBody">
		      <td width="10">&nbsp;</td>
		      <td width="195"class="formTitle"><bean:message key="label.resTaskForm.allocDays"/>:&nbsp;</td>
		      <td class="formBody">
		      
		      		<table width="190" border="1" cellspacing="0" cellpadding="0">
		      		<tr class="rowHighlight">
						<th align="center" class="tableCellHeader" width="100"><bean:message key="label.resTaskForm.grid.AllocDate"/></th>
						<th align="center" class="tableCellHeader"><bean:message key="label.resTaskForm.grid.AllocTime"/></th>
						<th align="center" class="tableCellHeader" width="10">&nbsp;</th>
		      		</tr>
					<bean:write name="resTaskForm" property="slotInHtml" filter="false" />		      		
		      		</table>
		      		
			  </td>
		      <td width="10">&nbsp;</td>
			</tr>	
	    </table>
	</logic:equal>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
	<tr class="pagingFormBody">
		<td width="20">&nbsp;</td>
		<td>

			<table width="80%" border="0" cellspacing="0" cellpadding="0">

			<tr class="gapFormBody">
			<td colspan="2">&nbsp;</td>
			</tr>		
						
		  	<plandora-html:attachment name="resTaskForm" collection="reqAttachList" title="title.requirement.attach" removedForward=""/> 
		  			
			<tr class="gapFormBody">
			<td colspan="2">&nbsp;</td>
			</tr>
						
			</table> 
		</td>
		<td width="10">&nbsp;</td>
	</tr>
	</table> 
        
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="150">
			  <html:button property="save" styleClass="button" onclick="javascript:saveResTask();">
				<bean:write name="resTaskForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>					
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="resTaskForm" styleClass="button" onclick="javascript:showTaskReport();">
				<bean:message key="button.resHome.showTaskRep"/>
			  </html:button>     
		  </td>
		  <td width="150">
			  <html:button property="createAdHocTask" styleClass="button" onclick="javascript:callResourceTask('', '', '');">
				<bean:message key="button.resHome.adHocTask"/>
			  </html:button>     
		  </td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>  	
	</display:headerfootergrid>

	
	<div>&nbsp;</div>

	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.resTaskList"/>
	</display:headerfootergrid>
 	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>		
			<plandora-html:ptable width="100%" name="taskList" scope="session" pagesize="<%=PreferenceTO.HOME_TASKLIST_NUMLINE%>" frm="resTaskForm">
			      <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridStatusDecorator" />
			      <plandora-html:pcolumn width="2%" likeSearching="true" property="task.id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			      
				  <plandora-html:pcolumn property="task.name" likeSearching="true" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.manageTask.name" />
				  <plandora-html:pcolumn sort="true" width="12%" align="center" comboFilter="true" property="task.project.name" title="label.manageTask.project" description="label.manageTask.project" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_PROJECT%>" />				  
				  <plandora-html:pcolumn width="6%" property="id" align="center" title="label.resTaskForm.billable" description="label.showAllTaskForm.billable.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BILLABLE%>" decorator="com.pandora.gui.taglib.decorator.TaskBillableDecorator"/>
				  <plandora-html:pcolumn width="8%" property="actualDate" align="center" title="label.manageTask.grid.acualDate" description="label.manageTask.grid.acualDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <plandora-html:pcolumn width="5%" property="actualTimeInHours" align="center" title="label.manageTask.grid.actualTime" description="label.manageTask.grid.actualTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_ACT_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>				  
				  <plandora-html:pcolumn width="8%" property="startDate" align="center" title="label.manageTask.grid.initDate" description="label.manageTask.grid.initDate.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <plandora-html:pcolumn width="5%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" description="label.manageTask.grid.estTime.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_EST_TIME%>" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
				  <plandora-html:pcolumn width="5%" align="center" property="id" title="label.gridParent" description="label.gridParent.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_PARENT%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="PARENT_ID"/>				  
				  <plandora-html:pcolumn width="10%" sort="true" property="taskStatus.name" comboFilter="true" align="center" title="label.manageTask.taskStatus" description="label.manageTask.taskStatus" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_STATUS%>" />
				  <plandora-html:pcolumn width="10%" sort="true" property="task.category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_CATEG%>" />
				  <plandora-html:pcolumn width="10%" sort="true" property="id" align="center" title="label.manageTask.grid.iteration" description="label.manageTask.grid.iteration.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_TSK_ITERAT%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" />
				  <plandora-html:pcolumn width="5%" sort="true" likeSearching="true" align="center" property="task.requirement.id" title="label.gridReqNum" description="label.gridReqNum.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_REL_REQ%>" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" tag="REQ_ID"/>
				  <plandora-html:pcolumn width="5%" align="center" property="id" title="label.blockers" description="label.blockers.desc" visibleProperty="<%=PreferenceTO.HOME_TASKLIST_SW_BLOCKERS%>" decorator="com.pandora.gui.taglib.decorator.TaskBlockedByDecorator" />				  				  
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskGridDeleteDecorator" />
  				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskLinkDecorator" />
				  <plandora-html:pcolumn width="2%" property="task.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'TSK'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridDecorator" tag="TSK" />
			</plandora-html:ptable>
		</td>
	</tr> 
	</table>
	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('resTaskForm', 'refresh');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td width="10">&nbsp;</td>      
		  <td width="400" class="textBoxOverTitleArea">
			<input type="checkbox" name="hideClosedCheckbox" onclick="javascript:hideClosedCheck();"/><bean:message key="label.resTaskForm.taskHideClosed"/>
		  </td>
		  <td>&nbsp;</td>
		</tr></table>  	
	</display:headerfootergrid>
		
</td><td width="10">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
   	with(document.forms["resTaskForm"]){
   		<logic:equal name="resTaskForm" property="isAdHocTask" value="off">	
			comment.focus();
		</logic:equal>
		hideClosedCheckbox.checked=(hideClosedTasks.value=="on");		
		
		<logic:equal name="resTaskForm" property="showDecisionQuestion" value="on">
			if(taskStatus.value!='<%=TaskStatusTO.STATE_MACHINE_CLOSE.toString()%>') {
				var qt = document.getElementById( 'question_text' );			
				qt.style.display =  'none' ; 
				behidden = true ;
			}
		</logic:equal>
		
   		<logic:notEqual name="resTaskForm" property="reportTaskURL" value="">	
			window.open('<bean:write name="resTaskForm" property="reportTaskURL" filter="false"/>', 'TaskReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
		</logic:notEqual>
		
		if (showCloseMacroTaskConfirm.value=='on') {
			displayMessage("../do/manageResTask?operation=showMTConfirmation", 480, 130);

		} else if (showWorkflowDiagram.value=='on') {
			displayMessage("../do/showAllTask?operation=prepareWorkflow", 800, 450);
			showWorkflowDiagram.value = 'off';
		}				
				
	}	
</script>