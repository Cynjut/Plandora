<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td>
			<td class="gridBody">&nbsp</td>
			<td width="10">&nbsp</td>
		</tr>
		<tr><td>&nbsp</td>
			<td class="gridBody" valign="top">
				<center><bean:message key="label.formSurvey.currentanswer"/></center>
			</td>
			<td>&nbsp</td>
		</tr>
		<tr><td>&nbsp</td>
			<td class="formBody" valign="top">
				<center><bean:write name="showSurveyForm" property="questionContent" filter="false"/></center>
			</td>
			<td>&nbsp</td>
		</tr>
		
		<logic:notEqual name="showSurveyForm" property="questionSummary" value="">
			<td>&nbsp</td>
			<td class="formBody" valign="top">
				<center><bean:write name="showSurveyForm" property="questionSummary" filter="false" /></center>
			</td>			
		</logic:notEqual>
		
		
		<tr><td colspan="3">&nbsp</td></tr>
		<tr><td>&nbsp</td>
			<td class="formBody">
				<center>
					<html:button property="cancel" styleClass="button" onclick="closeMessage();">
					  <bean:message key="button.cancel"/>
					</html:button>
				</center>
			</td>
			<td>&nbsp</td>
		</tr>
	</table>
</html>