<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>
<form name="shortCutForm">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<html:hidden name="shortCutForm" property="operation"/>
		<html:hidden name="shortCutForm" property="gotoAfterSave"/>
		<html:hidden name="shortCutForm" property="shortcutURI"/>
		
		<tr><td width="10">&nbsp;</td>
			<td class="gridBody" colspan="2">&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
		
		<tr><td>&nbsp;</td>
			<td class="gridBody" colspan="2" valign="top" align="center">
				<center><bean:message key="label.shortcut.desc"/></center>
			</td>
			<td>&nbsp;</td>
		</tr>		
		<tr><td colspan="4">&nbsp;</td></tr>

		<tr><td>&nbsp;</td>
			<td widht="60" class="formTitle"><bean:message key="label.shortcut.title"/>:&nbsp;</td>
			<td><html:text name="shortCutForm" property="shorcutTitle" styleClass="textBox" size="60" maxlength="90"/></td>
			<td>&nbsp;</td>
		</tr>		

		<tr><td>&nbsp;</td>
			<td class="formTitle"><bean:message key="label.shortcut.type"/>:&nbsp;</td>
			<td><bean:write name="shortCutForm" property="htmlTypeList" filter="false"/></td>
			<td>&nbsp;</td>
		</tr>		

		<tr><td>&nbsp;</td>
			<td class="formTitle"><bean:message key="label.shortcut.opening"/>:&nbsp;</td>
			<td><bean:write name="shortCutForm" property="htmlOpenList" filter="false"/></td>
			<td>&nbsp;</td>
		</tr>		
		
		<tr><td colspan="4">&nbsp;</td></tr>
				
		<tr><td>&nbsp;</td>
			<td colspan="2" class="formBody">
				<center>
					<html:button property="yes" styleClass="button" onclick="javascript:window.location='../do/showShortCutPopup?operation=saveShortCut&shorcutTitle=' + shorcutTitle.value + '&iconType=' + iconType.value + '&opening=' + opening.value">
					  <bean:message key="button.saveNew"/>
					</html:button> &nbsp;&nbsp;&nbsp;
					<html:button property="cancel" styleClass="button" onclick="closeMessage();">
					  <bean:message key="button.cancel"/>
					</html:button>
				</center>
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
</form>
</html>