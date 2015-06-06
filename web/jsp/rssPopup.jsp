<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>


<html>

<form name="ediForm">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<html:hidden name="ediForm" property="operation"/>
		<html:hidden name="ediForm" property="gotoAfterSave"/>
		
		<tr><td class="gapFormBody" colspan="3">&nbsp;</td></tr>
		
		<tr><td>&nbsp;</td>
			<td class="gridBody" valign="top" align="center">
				<center><bean:message key="label.rss.desc"/></center>
			</td>
			<td>&nbsp;</td>
		</tr>		
		
		<tr><td class="gapFormBody" colspan="3">&nbsp;</td></tr>

		<tr><td>&nbsp;</td>
			<td class="gridBody" align="left">
				<bean:write name="ediForm" property="htmlTypeList" filter="false"/>
			</td>
			<td>&nbsp;</td>
		</tr>		

		<tr><td class="gapFormBody" colspan="3">&nbsp;</td></tr>

		<tr><td>&nbsp;</td>
			<td class="gridBody" align="center">
				<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" class="rssLinkTable">
				<tr><td class="code" nowrap="nowrap">
					<div id="EDI_LINK">
						<bean:write name="ediForm" property="htmlDefaultOpt" filter="false"/>
					</div>
				</td></tr></table>
			</td>
			<td>&nbsp;</td>
		</tr>

		<tr><td class="gapFormBody" colspan="3">&nbsp;</td></tr>
				
		<tr><td>&nbsp;</td>
			<td class="formBody">
				<center>
					<html:button property="close" styleClass="button" onclick="closeMessage();">
					  <bean:message key="button.close"/>
					</html:button>
				</center>
			</td>
			<td>&nbsp;</td>
		</tr>
		
	</table>
</form>
</html>