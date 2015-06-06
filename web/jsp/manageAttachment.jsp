<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html:form action="manageAttachment" enctype="multipart/form-data"> 
	<html:hidden name="attachmentForm" property="operation"/>
	<html:hidden name="attachmentForm" property="fileId"/>	
	<html:hidden name="attachmentForm" property="planningId"/>
	<html:hidden name="attachmentForm" property="source"/>
	<html:hidden name="attachmentForm" property="fwd"/>

	<br>
	<jsp:include page="validator.jsp" />	
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="formLabel">
      <td width="8">&nbsp;</td>
      <td width="400">
			<logic:equal name="attachmentForm" property="source" value="REQ">
				<bean:message key="title.formAttachment"/>&nbsp;<bean:write name="attachmentForm" property="planningId" filter="false"/>
			</logic:equal>
			<logic:notEqual name="attachmentForm" property="source" value="REQ">
				<bean:message key="title.attachTagLib.show"/>
			</logic:notEqual>			
      </td>
      <td>&nbsp;</td>
      <td width="8">&nbsp;</td>
    </tr>
  	</table>
  	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formAttachment.name"/>:&nbsp;</td>
      <td width="400" colspan="3" class="formBody">
		  	<logic:equal name="attachmentForm" property="upload" value="true">
				<html:file name="attachmentForm" property="theFile" styleClass="textBox"/>
				<bean:message key="title.formAttachment.maxSizeWarning"/>&nbsp;(<bean:write name="attachmentForm" property="maxSizeFile" filter="false"/> KB)
		  	</logic:equal>
		  	<logic:equal name="attachmentForm" property="upload" value="false">
		        <html:text name="attachmentForm" readonly="true" property="name" styleClass="textBox" size="40" maxlength="200" />
				<html:hidden name="attachmentForm" property="name"/>		        
		  	</logic:equal>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formAttachment.type"/>:&nbsp;</td>
      <td class="formBody">
	  		<html:select name="attachmentForm" property="type" styleClass="textBox">
	             <html:options collection="typeList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>
      </td>
      <td width="130" class="formTitle"><bean:message key="label.formAttachment.visibility"/>:&nbsp;</td>
      <td width="50" class="formBody">
			<html:hidden name="attachmentForm" property="visibility" value="3"/>      
	  		<html:select name="attachmentForm" property="visibility" styleClass="textBox" disabled="true">
	             <html:options collection="visibilityList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>

      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formAttachment.comment"/>:&nbsp;</td>
      <td class="formBody" colspan="3">
   		<html:textarea name="attachmentForm" property="comment" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr> 
    </table>
  	
  	
  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="formLabel">
      <td width="8">&nbsp;</td>
      <td width="120">
		<logic:present name="attachmentForm" property="fileId">      
			<html:button property="uploadButton" styleClass="button" onclick="javascript:saveAttachment();">
				<bean:message key="label.formAttachment.upload"/>
			</html:button>
	    </logic:present>
	  	<logic:equal name="attachmentForm" property="upload" value="true">
			<html:button property="uploadButton" styleClass="button" onclick="javascript:saveAttachment();">
				<bean:message key="label.formAttachment.upload"/>
			</html:button>
	  	</logic:equal>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
	  <td>&nbsp;</td>
	  <td width="8">&nbsp;</td>
    </tr>
  </table>

  </p>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formLabel">
		<td width="8">&nbsp;</td>
		<td><bean:message key="title.attachmentList"/></td>
		<td width="8">&nbsp;</td>
	</tr>
  </table>

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="580px" height="100px"  name="attachmentList" scope="session" pagesize="3" frm="attachmentForm">
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridTypeDecorator" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" />
				  <plandora-html:pcolumn property="name" title="label.formAttachment.name" />
				  <plandora-html:pcolumn property="type" title="label.formAttachment.type" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridEditDecorator" tag="'attachmentForm'" />				  
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridDeleteDecorator" tag="'attachmentForm'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridAttachmentDetailDecorator" />				  
			</plandora-html:ptable>	
		</td>
	</tr> 
  </table>
		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formLabel">
		<td width="10">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>      
	</tr>
	<tr class="formBody">
		<td width="10">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>	
	<tr class="formBody">
		<td width="8">&nbsp;</td>
		<td><center>
		      <html:button property="close" styleClass="button" onclick="closeMessage();">
	    	    <bean:message key="button.close"/>
		      </html:button>    		
		</center></td>
		<td width="8">&nbsp;</td>
	</tr>
	</table>  	

  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<!-- End of source-code -->
<script> 
	with(document.forms["attachmentForm"]){	
		comment.focus(); 
	}
</script>