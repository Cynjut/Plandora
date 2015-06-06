<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ page import="com.pandora.gui.struts.form.GeneralErrorForm"%>

<% if (session.getAttribute("errorForm")!=null){ %>

	<table border="0" cellspacing="0" cellpadding="0" align="center" class="errorValidationTable">
	<tr>
	<td valign="top" width="20"><img src="../images/exception.gif" width="16" height="16"></td>
	<td class="errorValidation">
		<%
		GeneralErrorForm errfrm = (GeneralErrorForm)session.getAttribute("errorForm");
		String arg0 = errfrm.getArg0();
		String arg1 = errfrm.getArg1();
		String arg2 = errfrm.getArg2();
		String arg3 = errfrm.getArg3();
		String arg4 = errfrm.getArg4();
		%>
		<bean:message name="errorForm" property="errorMessage" arg0="<%=arg0%>" arg1="<%=arg1%>" arg2="<%=arg2%>" arg3="<%=arg3%>" arg4="<%=arg4%>"/> 
	
		
		<logic:present name="errorForm" property="exceptionMessage">
			<br/><br/>
			<a class="errorValidation" href="javascript:showHide('errorDetail');">
				<bean:message key="label.more"/>
			</a>
			<br/>
			<div id="errorDetail">
				<br/>
				<bean:write name="errorForm" property="exceptionMessage" filter="false"/>
				<br/><br/>
				<bean:write name="errorForm" property="stackTrace" filter="false"/>
			</div>
		</logic:present>	
		
	</td>
	<td width="10">&nbsp</td>
	</tr>
	</table>
<% 
  session.removeAttribute("errorForm");
} %>