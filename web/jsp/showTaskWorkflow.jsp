<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>

    <table width="100%" border="0">
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


	<div id="workFlowDiagramDiv" style="width:450px; height:410px; overflow: scroll;">
		<img border="0" id="workFlowDiagramImg" src="../do/showAllTask?operation=renderImage" usemap="#workFlowDiagramMap" />
	</div>
	<map name="workFlowDiagramMap">
		<bean:write name="showAllTaskForm" property="htmlMap" filter="false" />
	<map>
	<center>
		<html:button property="close" styleClass="button" onclick="closeMessage();closeFloatPanel();">
		  <bean:message key="button.close"/>
		</html:button>
		<html:button property="reset" styleClass="button" onclick="centralizeDiagram();">
		  <bean:message key="title.formApplyTaskTemplate.centralize"/>
		</html:button>    	         
	</center>

</html>