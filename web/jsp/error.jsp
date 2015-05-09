<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<% if (session.getAttribute("errorForm")!=null){ %>

	<table border="0" cellspacing="0" cellpadding="0" align="center" class="errorValidationTable">
	<tr>
	<td valign="top" width="20"><img src="../images/exception.gif" width="16" height="16"></td>
	<td class="errorValidation">
		<bean:message name="errorForm" property="errorMessage" /> 
		
		<logic:present name="errorForm" property="exceptionMessage">
			<br/><br/>
			<a class="errorValidation" href="javascript:showHide('errorDetail');">
				<bean:message key="label.more"/>
			</a>
			<br/>
			<div id="errorDetail">
				<br/>
				<bean:write name="errorForm" property="exceptionMessage" />
				<br/><br/>
				<bean:write name="errorForm" property="stackTrace" />
			</div>
		</logic:present>	
		
	</td>
	<td width="10">&nbsp</td>
	</tr>
	</table>
<% 
  session.removeAttribute("errorForm");
} %>