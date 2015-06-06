<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.NodeTemplateTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

	function addResource(){
		var ok = true;	
		
		//check if start date and estimated time was filled		
    	with(document.forms["applyTaskTemplateForm"]){
    		if (initDate.value=="") {
    			alert("<bean:message key="message.manageTask.startDateMandatory"/>");
    			ok = false;
    		}
    		if (estimatedTime.value=="") {
    			alert("<bean:message key="message.manageTask.estTimeMandatory"/>");
    			ok = false;
    		}
    		if (resourceId.value=="-1") {
    			alert("<bean:message key="message.manageTask.resourceIdMandatory"/>");
    			ok = false;
    		}
    		
			if (ok){
				buttonClick('applyTaskTemplateForm', 'addResource');
			}
    	}			
	}
	
	function removeResource(resId){
		window.location = "../do/applyTaskTemplate?operation=removeResource&removeResId=" + resId;		
	}
	
	function clickNodeTemplate(nodeId, planningId) {
		edit(nodeId,'applyTaskTemplateForm', 'editTemplateNode');
	}
	
    function changeProject(){
   		with(document.forms["applyTaskTemplateForm"]){	
			if ( confirm("<bean:message key="message.formApplyTaskTemplate.changeProject"/>")) {
		 		buttonClick("applyTaskTemplateForm", "refreshAfterProjectChange");
        	}    	
		}
    }	

    function clearAllWF(){
		with(document.forms["applyTaskTemplateForm"]){	
			displayMessage("../do/applyTaskTemplate?operation=showConfirmation", 300, 120);
		}        
    }
        
	function setCurrentTaskParent(){
		with(document.forms["applyTaskTemplateForm"]){
			showHide("resourceAllocationBox"); 
		}	
	}
	
	
	function renderDiagram(){
		with(document.forms["applyTaskTemplateForm"]){
   			operation.value = "renderImage";
   		}
		var ajaxRequestObj = ajaxSyncInit();
		ajaxSyncProcess(document.forms["applyTaskTemplateForm"], callBackRenderDiagram, "", "", ajaxRequestObj);		
	}
	
	
	function callBackRenderDiagram(dummy1, dummy2, objRequest) {			
		if(isSyncAjax(objRequest)){
			document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
			var content = objRequest.responseText;
			if (content) {
				var diagObj = document.getElementById("workFlowDiagram");
				if (diagObj) {
					diagObj.src = content;
				}
			}
		}  
	}
	
	
</script>

<html:form action="applyTaskTemplate">
	<html:hidden name="applyTaskTemplateForm" property="operation"/>
	<html:hidden name="applyTaskTemplateForm" property="id"/>
	<html:hidden name="applyTaskTemplateForm" property="reqId"/>	
	<html:hidden name="applyTaskTemplateForm" property="templateId"/>
	<html:hidden name="applyTaskTemplateForm" property="instanceId"/>
	<html:hidden name="applyTaskTemplateForm" property="nodeType"/>
	
	
	<br />		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td>
		<td class="formBody" width="400px" valign="top" rowspan="3">

			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="label.formApplyTaskTemplate.diagram"/>
				<logic:present name="applyTaskTemplateForm" property="projectName">
					- <bean:write name="applyTaskTemplateForm" property="projectName" filter="false"/>   
				</logic:present>	
			</display:headerfootergrid>
				
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="gapFormBody">
		      <td colspan="2">&nbsp;</td>
		    </tr>

		    <tr class="pagingFormBody">
		      <td width="10">&nbsp;</td>
		      <td class="formBody">
					<div id="workFlowDiagramDiv" style="width:380px; height:430px; overflow: scroll;">
						<img border="0" id="workFlowDiagram" src="../images/indicator.gif" usemap="#workFlowDiagramMap" />
					</div>
					<map name="workFlowDiagramMap">
						<bean:write name="applyTaskTemplateForm" property="htmlMap" filter="false"/>
					</map>
		      </td>
		    </tr>
			
		    <tr class="gapFormBody">
		      <td colspan="2">&nbsp;</td>
		    </tr>		
			</table>	  

			<display:headerfootergrid type="FOOTER">
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>				
				  <td width="120">
					  <html:button property="saveWorkflow" styleClass="button" onclick="javascript:buttonClick('applyTaskTemplateForm', 'saveWorkflow');">
						<bean:message key="title.formApplyTaskTemplate.create"/>
					  </html:button>    
				  </td>
				  <td>&nbsp;</td>
				</tr></table>	  
			</display:headerfootergrid> 
			
		</td>	
		<td width="10" rowspan="3">&nbsp;</td>
	    <td height="290" valign="top">

			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="title.formApplyTaskTemplate"/> 
		      	  <bean:write name="applyTaskTemplateForm" property="categoryName" filter="false" />
			</display:headerfootergrid>
		
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		      <td width="10">&nbsp;</td>
		      <td width="80" class="formTitle"><bean:message key="label.formApplyTaskTemplate.name"/>:&nbsp;</td>
		      <td class="formBody">
		        <html:text name="applyTaskTemplateForm" property="name" styleClass="textBox" size="55" maxlength="50"/>
		      </td>
		      <td width="10">&nbsp;</td>
		    </tr>
		    <tr class="pagingFormBody">
		      <td>&nbsp;</td>
		      <td class="formTitle"><bean:message key="label.formApplyTaskTemplate.desc"/>:&nbsp;</td>
		      <td class="formBody">
		   		<html:textarea name="applyTaskTemplateForm" property="description" styleClass="textBox" cols="95" rows="3" />
		      </td>
		      <td>&nbsp;</td>
		    </tr>
		    </table>

			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			    <logic:equal name="applyTaskTemplateForm" property="nodeType" value="1">	
				    <tr class="pagingFormBody">
				      <td width="10">&nbsp;</td>
				      <td width="80" class="formTitle"><bean:message key="label.formApplyTaskTemplate.project"/>:&nbsp;</td>
				      <td width="170" class="formBody">
					  		<html:select name="applyTaskTemplateForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
					             <html:options collection="projectList" property="id" labelProperty="name" filter="false" />
							</html:select>
				      </td>
				      <td width="100" class="formTitle"><bean:message key="label.formApplyTaskTemplate.category"/>:&nbsp;</td>
				      <td class="formBody">
					  		<html:select name="applyTaskTemplateForm" property="categoryId" styleClass="textBox">
					             <html:options collection="categoryList" property="id" labelProperty="name" filter="false" />
							</html:select>
				      </td>
				      <td width="10">&nbsp;</td>
				    </tr>		
				    <tr class="pagingFormBody">
				      <td>&nbsp;</td>
				      <td class="formTitle"><bean:message key="label.formApplyTaskTemplate.iteration"/>:&nbsp;</td>
				      <td class="formBody" colspan="3">
					  		<html:select name="applyTaskTemplateForm" property="iterationId" styleClass="textBox">
					             <html:options collection="iterationList" property="id" labelProperty="name" filter="false" />
							</html:select>
				      </td>
				      <td width="10">&nbsp;</td>
				    </tr>		
					
				</logic:equal>	
			    
			    <logic:equal name="applyTaskTemplateForm" property="nodeType" value="2">	
				    <tr class="pagingFormBody">
				      <td width="10">&nbsp;</td>
				      <td width="80" class="formTitle"><bean:message key="label.formApplyTaskTemplate.question"/>:&nbsp;</td>
				      <td colspan="3" class="formBody">
				      		<html:text name="applyTaskTemplateForm" property="questionContent" styleClass="textBox" size="55" maxlength="255"/>
				      </td>
				      <td width="10">&nbsp;</td>
				    </tr>		
				</logic:equal>		    
			    <tr class="gapFormBody">
			      <td colspan="6">&nbsp;</td>
			    </tr>
			</table>

		    <logic:equal name="applyTaskTemplateForm" property="nodeType" value="1">
				<table width="98%" border="0" cellspacing="0" cellpadding="0">	
				<tr class="pagingFormBody">
					<td width="10">&nbsp;</td>	
					<td width="80">&nbsp;</td>
				    <td class="formBody" colspan="5">
			    	    <html:checkbox name="applyTaskTemplateForm" property="isParentTask" styleClass="textBox" onclick="javascript:setCurrentTaskParent();"/><bean:message key="title.formApplyTaskTemplate.parentTask"/>
			      	</td>
					<td width="10">&nbsp;</td>
				</tr>
				</table>
				
				<div id="resourceAllocationBox">
				<table width="98%" border="0" cellspacing="0" cellpadding="0">			  				
				    <tr class="pagingFormBody">
				      <td width="10">&nbsp;</td>
				      <td width="80" class="formTitle"><bean:message key="label.formApplyTaskTemplate.resource"/>:&nbsp;</td>
				      <td width="170" class="formBody">
							
					  		<html:select name="applyTaskTemplateForm" property="resourceId" styleClass="textBox">
					             <html:options collection="resourceList" property="id" labelProperty="name" filter="false"/>
							</html:select>
							
				      </td>
				      <td width="80" class="formTitle"><bean:message key="label.manageTask.initDate"/>:&nbsp;</td>
				      <td width="100" class="formBody">
							<plandora-html:calendar name="applyTaskTemplateForm" property="initDate" styleClass="textBoxDisabled" />		    	    
				      </td>
				      <td width="100" class="formTitle"><bean:message key="label.manageTask.estTime"/>:&nbsp;</td>
				      <td width="80" class="formBody">
							<html:text name="applyTaskTemplateForm" property="estimatedTime" styleClass="textBox" size="6" maxlength="5"/> <bean:message key="label.hour"/>		    	    
				      </td>
				      <td>&nbsp;</td>
				    </tr>

				    <tr class="pagingFormBody">
				      <td>&nbsp;</td>
				      <td>&nbsp;</td>
				      <td class="formBody">
							<html:button property="addRes" styleClass="button" onclick="javascript:addResource();">
								<bean:message key="button.addInto"/>
							</html:button>		    	    
				      </td>
				      <td colspan="5">&nbsp;</td>
				    </tr>	
					<tr class="pagingFormBody">
						<td>&nbsp;</td>	
						<td>&nbsp;</td>
					    <td colspan="5" class="formBody">
							<plandora-html:ptable width="90%" name="resourceAllocated" frm="applyTaskTemplateForm">
								  <plandora-html:pcolumn property="label" title="label.name" />					  
								  <plandora-html:pcolumn width="25%" property="startDate" align="center" title="label.manageTask.initDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  				  <plandora-html:pcolumn width="20%" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
								  <plandora-html:pcolumn width="2%" property="resource.id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceTaskRemoveDecorator" />
							</plandora-html:ptable>			    
			      		</td>
						<td>&nbsp;</td>
					</tr>				
				</table> 
				</div>	
				
				<table width="98%" border="0" cellspacing="0" cellpadding="0">	
					<tr class="gapFormBody">
					  <td colspan="8">&nbsp;</td>
					</tr>
				</table>
				
			</logic:equal>
					
			<display:headerfootergrid type="FOOTER">					
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td width="120">
						<logic:present name="applyTaskTemplateForm" property="id">   
							<html:button property="updateNode" styleClass="button" onclick="javascript:buttonClick('applyTaskTemplateForm', 'updateNode');">
								<bean:message key="title.formApplyTaskTemplate.update"/>
							</html:button>    
						</logic:present>	
				  </td>
				  <td>&nbsp;</td>
				  <td width="120">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('applyTaskTemplateForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
				  </td>
				</tr></table>	  
			</display:headerfootergrid> 
	
		</td>
		<td width="10" rowspan="3">&nbsp;</td>
	</tr>

	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
  
	<tr height="170" valign="bottom">
		<td width="10">&nbsp;</td>
		<td>
  
			<display:headerfootergrid width="100%" type="HEADER">
				<bean:message key="title.formApplyTaskTemplate.list"/>
			</display:headerfootergrid>
  
			<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>			
					<plandora-html:ptable width="100%" name="nodeTemplateList" frm="applyTaskTemplateForm" pagesize="5">
						<plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.NodeTemplateTypeDecorator" />				
						<plandora-html:pcolumn width="35%" property="name" align="left" title="label.formApplyTaskTemplate.name" />
						<plandora-html:pcolumn property="description" maxWords="10" align="left" title="label.formApplyTaskTemplate.desc" />
						<plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'applyTaskTemplateForm', 'editTemplateNode'" />
					</plandora-html:ptable>	
				</td>
			</tr> 
			</table>

			<display:headerfootergrid type="FOOTER">					
				<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
				<td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('applyTaskTemplateForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
				</td>
				<td width="120">
					  <html:button property="clearAll" styleClass="button" onclick="javascript:clearAllWF();">
						 <bean:message key="title.formApplyTaskTemplate.clearAll"/>
					  </html:button>
				</td>				
				<td>&nbsp;</td>
				<td width="120">
					<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('applyTaskTemplateForm', 'backward');">
						<bean:message key="button.backward"/>
					</html:button>    
				</td>			
				</tr></table>  		
			</display:headerfootergrid> 			
		</td>
	</tr>
	</table>
	
</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["applyTaskTemplateForm"]){	
		name.focus();
		if (id.value != '' && nodeType.value=='1') {
			if (isParentTask.checked) {
				showHide("resourceAllocationBox");
			}				
		}
		renderDiagram();
	}
</script>