<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<html:form action="repositoryUpload" enctype="multipart/form-data"> 
	<html:hidden name="repositoryUploadForm" property="operation"/>
	<html:hidden name="repositoryUploadForm" property="path"/>
	<html:hidden name="repositoryUploadForm" property="projectId"/>
	<html:hidden name="repositoryUploadForm" property="folderCreation"/>

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="10">&nbsp;</td>
      <td>

	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="10">&nbsp;</td>
		  <td>
				<logic:equal name="repositoryUploadForm" property="folderCreation" value="off">		      
					<bean:message key="label.formRepository.uploadbutton"/>
				</logic:equal>
				<logic:equal name="repositoryUploadForm" property="folderCreation" value="on">		      
					<bean:message key="label.formRepository.createFolder"/>
				</logic:equal>
				
		  </td>
		  <td width="10">&nbsp;</td>
		</tr>
		</table>
			
					
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
		  <td colspan="4">&nbsp;</td>
		</tr>

		<logic:equal name="repositoryUploadForm" property="folderCreation" value="off">
			<tr class="pagingFormBody">
			  <td width="10">&nbsp;</td>
			  <td width="80" class="formTitle"><bean:message key="label.formRepository.file"/>:&nbsp;</td>
			  <td class="formBody">
					<html:file name="repositoryUploadForm" property="theFile" styleClass="textBox"/>
					<bean:message key="title.formAttachment.maxSizeWarning"/>&nbsp;(<bean:write name="repositoryUploadForm" property="maxFileSize" filter="false"/> KB)
			  </td>
			  <td width="10">&nbsp;</td>
			</tr>			
		</logic:equal>
		

		<logic:equal name="repositoryUploadForm" property="folderCreation" value="on">		      
			<tr class="pagingFormBody">
			  <td width="10">&nbsp;</td>
			  <td width="80" class="formTitle"><bean:message key="label.formRepository.foldername"/>:&nbsp;</td>
			  <td class="formBody">
			  		<html:text name="repositoryUploadForm" property="newFolder" styleClass="textBox" size="70" maxlength="200"/>
			  </td>
			  <td width="10">&nbsp;</td>
			</tr>			
		</logic:equal>

		<tr class="pagingFormBody">
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.formRepository.comment"/>:&nbsp;</td>
		  <td class="formBody">
			<html:textarea name="repositoryUploadForm" property="comment" styleClass="textBox" cols="70" rows="4" />
		  </td>
		  <td>&nbsp;</td>
		</tr>
		
		<tr class="gapFormBody">
		  <td colspan="4">&nbsp;</td>
		</tr> 
		</table>
		
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="10">&nbsp;</td>
		  <td width="130">
				<html:button property="uploadButton" styleClass="button" onclick="javascript:uploadFile();">
					<logic:equal name="repositoryUploadForm" property="folderCreation" value="off">		      
						<bean:message key="label.formRepository.uploadbutton"/>
					</logic:equal>
					<logic:equal name="repositoryUploadForm" property="folderCreation" value="on">		      
						<bean:message key="label.formRepository.createFolder"/>
					</logic:equal>
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

<!-- End of source-code -->
<script> 
	with(document.forms["repositoryUploadForm"]){	
		comment.focus(); 
	}
</script>