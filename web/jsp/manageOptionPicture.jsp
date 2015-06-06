<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html:form action="manageOptionPicture" enctype="multipart/form-data"> 
	<html:hidden name="optionPictureForm" property="operation"/>
	<html:hidden name="optionPictureForm" property="confirmationMsg"/>

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="10">&nbsp;</td>
      <td>

	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="10">&nbsp;</td>
		  <td><bean:message key="label.manageOption.uploadbutton"/></td>
		  <td width="10">&nbsp;</td>
		</tr>
		</table>
			
					
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
		  <td colspan="4">&nbsp;</td>
		</tr>

		<tr class="pagingFormBody">
		  <td width="10">&nbsp;</td>
		  <td width="120" class="formTitle"><bean:message key="label.userpic"/>:&nbsp;</td>
		  <td class="formBody">
				<html:file name="optionPictureForm" property="theFile" styleClass="textBox"/>
				<bean:message key="label.userpic.maxSizeWarning"/>
		  </td>
		  <td width="10">&nbsp;</td>
		</tr>			

		<tr class="gapFormBody">
		  <td colspan="4">&nbsp;</td>
		</tr> 
		</table>
		

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="10">&nbsp;</td>
		  <td width="130">
				<html:button property="uplButton" styleClass="button" onclick="javascript:uploadPicture(theFile.value, confirmationMsg.value );">
					<bean:message key="label.manageOption.uploadbutton"/>
				</html:button>
		  </td>
		  <td width="120">
			  <html:button property="close" styleClass="button" onclick="closeMessage();">
				<bean:message key="button.close"/>
			  </html:button>    		
		  </td>
		  <td>&nbsp;</td>
		  <td width="10">&nbsp;</td>
		</tr>
	  </table>

	</td>
	<td width="10">&nbsp;</td>
	</tr></table>
	
</html:form>