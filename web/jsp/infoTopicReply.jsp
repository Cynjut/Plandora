<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

<html>
<html:form  action="manageResourceHome">
	
	<html:hidden name="resourceHomeForm" property="operation" value="replyInfoTeamTopic"/>
	<html:hidden name="resourceHomeForm" property="planningId"/>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	
		<tr><td width="10">&nbsp;</td>
			<td class="gridBody">&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
		
		<tr><td>&nbsp;</td>
			<td class="gridBody" valign="top">
				&nbsp;<bean:message key="label.formForum.comment.title"/>
			</td>
			<td>&nbsp;</td>
		</tr>
		
		<tr><td>&nbsp;</td>
			<td class="gridBody" valign="top">
			<center>
		      	<html:textarea name="resourceHomeForm" property="topicComment" styleClass="textBox" cols="85" rows="3" />			
			</center>
			</td>
			<td>&nbsp;</td>
		</tr>
		
		<tr><td colspan="3">&nbsp;</td></tr>
		<tr><td>&nbsp;</td>
			<td class="formBody">
				<center>
					<html:button property="yes" styleClass="button" onclick="submit();">
					  	<bean:message key="button.ok"/>
					</html:button> &nbsp;&nbsp;&nbsp;
					<html:button property="cancel" styleClass="button" onclick="closeMessage();">
					  	<bean:message key="button.cancel"/>
					</html:button>
				</center>
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
	
</html:form>
</html>