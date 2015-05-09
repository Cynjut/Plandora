<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td>
			<td class="gridBody">&nbsp</td>
			<td width="10">&nbsp</td>
		</tr>
		<tr><td>&nbsp</td>
			<td class="gridBody" valign="top">
				<div id="maximized_gadget" style="width:680px; height:440px; overflow-x:scroll; overflow-y:hidden; border:1px" />
			</td>
			<td>&nbsp</td>
		</tr>
		<tr><td colspan="3">&nbsp</td></tr>
		<tr><td>&nbsp</td>
			<td class="formBody">
				<center>
					<input type="button" name="cancel" onclick="window.location='<bean:write name="showGadgetPropertyForm" property="forwardAfterSave" filter="false" />'; closeMessage();" value="<bean:message key="button.close"/>" class="button" />
				</center>
			</td>
			<td>&nbsp</td>
		</tr>
	</table>
</html>

<script> 
	window.setTimeout(" <bean:write name="showGadgetPropertyForm" property="showGadgetCall" filter="false" /> ", 1000);
</script>