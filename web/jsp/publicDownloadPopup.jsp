<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<html:form action="showRepositoryViewer">
		<html:hidden name="repositoryViewerForm" property="operation"/>
		<html:hidden name="repositoryViewerForm" property="id"/>
		<html:hidden name="repositoryViewerForm" property="genericTag"/>
		<html:hidden name="repositoryViewerForm" property="projectId"/>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="10">&nbsp</td>
				<td class="gridBody">&nbsp</td>
				<td width="10">&nbsp</td>
			</tr>
			
			<tr>
				<td>&nbsp</td>
				<td class="gridBody" valign="top">
					<center>
						<logic:equal name="repositoryViewerForm" property="genericTag" value="ON">
							<bean:message key="label.formRepository.download.popup.allow"/>					
						</logic:equal>
						<logic:equal name="repositoryViewerForm" property="genericTag" value="OFF">
							<bean:message key="label.formRepository.download.popup.block"/>					
						</logic:equal>																
					</center>
				</td>
				<td>&nbsp</td>
			</tr>

			<tr>
				<td colspan="3">&nbsp</td>
			</tr>

			<tr class="tableRowOdd">
				<td>&nbsp</td>
				<td class="gridbox" align="center">
					<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" class="rssLinkTable">
					<tr><td class="code" nowrap="nowrap">
						<bean:write name="repositoryViewerForm" property="anonymousURI" filter="false"/>
					</td></tr></table>				
				</td>
				<td>&nbsp</td>
			</tr>
			
			<tr>
				<td colspan="3">&nbsp</td>
			</tr>
			
			<tr>
				<td>&nbsp</td>
				<td class="formBody">
					<center>
						<html:button property="toggle" styleClass="button" onclick="javascript:window.location='../do/showRepositoryViewer?operation=toggleDownloadStatus'">
							<logic:equal name="repositoryViewerForm" property="genericTag" value="ON">
								<bean:message key="label.formRepository.download.btallow"/>
							</logic:equal>
							<logic:equal name="repositoryViewerForm" property="genericTag" value="OFF">
							    <bean:message key="label.formRepository.download.btblock"/>
							</logic:equal>
						</html:button> &nbsp;&nbsp;&nbsp;					
					
						<html:button property="cancel" styleClass="button" onclick="closeMessage();">
						  <bean:message key="button.cancel"/>
						</html:button>
					</center>
				</td>
				<td>&nbsp</td>
			</tr>
		</table>
		
	</html:form>
</html>