<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>

    <table width="60%" border="0" cellspacing="1" cellpadding="1">	
	<tr>
		<td width="10">
			<table width="100%" cellspacing="0" cellpadding="0"><tr><td class="legendClosed">&nbsp;</td></tr></table>
		</td>
		<td class="formNotes" align="left"><bean:message key="title.formApplyTaskTemplate.legend.closed"/></td>
		<td width="10">
			<table width="100%" cellspacing="0" cellpadding="0"><tr><td class="legendInProgress">&nbsp;</td></tr></table>
		</td>
		<td class="formNotes" align="left"><bean:message key="title.formApplyTaskTemplate.legend.inprogress"/></td>
		<td width="10">
			<table width="100%" cellspacing="0" cellpadding="0"><tr><td class="legendOpen">&nbsp;</td></tr></table>
		</td>
		<td class="formNotes" align="left"><bean:message key="title.formApplyTaskTemplate.legend.open"/></td>		
		<td width="10">
			<table width="100%" cellspacing="0" cellpadding="0"><tr height="10"><td class="legendEmpty">&nbsp;</td></tr></table>
		</td>
		<td class="formNotes" align="left"><bean:message key="title.formApplyTaskTemplate.legend.notyet"/></td>
	</tr>
	</table>


	<table border="0" width="100%" cellspacing="0" cellpadding="0">	
	<tr>
	<td width="360">
	
		<div id="workFlowDiagramDiv" style="width:360px; height:390px; overflow: scroll;">
			<img border="0" id="workFlowDiagramImg" src="../do/showAllTask?operation=renderImage" usemap="#workFlowDiagramMap" />
		</div>
		<map name="workFlowDiagramMap">
			<bean:write name="showAllTaskForm" property="htmlMap" filter="false" />
		<map>
	</td>
	<td width="20">&nbsp;</td>
	<td> 
		
		<table border="0" width="100%" cellspacing="0" cellpadding="0">		
		<tr class="pagingFormBody">
			<td class="formBody">&nbsp;<bean:message key="label.requestHistory.requirement"/></td>
		</tr>
		<tr>
			<td class="formBody">
				<html:textarea name="showAllTaskForm" property="requirementContent" styleClass="textBox" cols="75" rows="8" />
			</td>
		</tr>
		
		<tr class="gapFormBody"><td>&nbsp;</td></tr>
		
		<tr class="pagingFormBody">
			<td class="formBody">&nbsp;<bean:message key="label.taskHistory.FUP"/></td>
		</tr>
		<tr>
			<td class="formBody">
				<html:textarea name="showAllTaskForm" property="followupContent" styleClass="textBox" cols="75" rows="16" />
			</td>
		</tr>
		</table>
		
	</td>
	</tr>
	</table>

	<table border="0" width="100%" cellspacing="0" cellpadding="0">	
	<tr><td>
	<center>
		<html:button property="close" styleClass="button" onclick="closeMessage();closeFloatPanel();">
		  <bean:message key="button.close"/>
		</html:button>
	</center>
	</td></tr></table>
</html>