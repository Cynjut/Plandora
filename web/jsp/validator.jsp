<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="error.jsp" />
<jsp:include page="success.jsp" />

<logic:messagesPresent> 
	<table border="0" cellspacing="0" cellpadding="0" align="center" class="errorValidationTable"><tr>
	<td valign="top" width="20"><img src="../images/error.gif" width="16" height="16"></td>
	<td class="errorValidation"><html:errors/></td>
	</tr></table>
</logic:messagesPresent>