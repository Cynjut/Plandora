<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp;</td>
			<td class="gridBody">&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
		<tr><td>&nbsp;</td>
			<td class="gridBody" valign="top">
				<center><bean:message key="message.formApplyTaskTemplate.clearConfirm"/></center>
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr><td colspan="3">&nbsp;</td></tr>
		<tr><td>&nbsp;</td>
			<td class="formBody">
				<center>
					<html:button property="yes" styleClass="button" onclick="javascript:window.location='../do/applyTaskTemplate?operation=clearAll'">
						&nbsp;&nbsp;&nbsp;<bean:message key="label.yes"/>&nbsp;&nbsp;&nbsp;
					</html:button> &nbsp;&nbsp;&nbsp;
					<html:button property="no" styleClass="button" onclick="closeMessage();">
						&nbsp;&nbsp;&nbsp;<bean:message key="label.no"/>&nbsp;&nbsp;&nbsp;
					</html:button>
				</center>
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
</html>