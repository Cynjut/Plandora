<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<% if (session.getAttribute("successForm")!=null){ %>

	<table border="0" cellspacing="0" cellpadding="0" align="center" class="errorValidationTable"><tr>
	<td width="10">&nbsp;</td>
	<td class="successfullyMessage">
		<bean:message name="successForm" property="successMessage" />
	</td>
	<td width="10">&nbsp;</td>	
	</tr></table>

<% 
  session.removeAttribute("successForm");
 } %>