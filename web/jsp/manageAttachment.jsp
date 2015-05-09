<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<script language="javascript">
        
	function editAttachment(argId, argForm){
		displayMessage('../do/manageAttachment?operation=editAttachment&id=' + argId, 600, 385);
	}            

	function removeAttach(argId, argForm, msg){
		with(document.forms["attachmentForm"]){
			removeAttachment(argId, argForm, fwd.value, msg);			
		}
	}            
	
	function openAttachmentHistPopup(id){
		var pathWindow ="../do/manageHistAttach?operation=prepareForm&attachmentId=" + id;
		window.open(pathWindow, 'attachHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	}

</script>

<html:form action="manageAttachment" enctype="multipart/form-data"> 
	<html:hidden name="attachmentForm" property="operation"/>
	<html:hidden name="attachmentForm" property="id"/>	
	<html:hidden name="attachmentForm" property="planningId"/>
	<html:hidden name="attachmentForm" property="source"/>
	<html:hidden name="attachmentForm" property="fwd"/>
	
	<br>
	<jsp:include page="validator.jsp" />	
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp</td><td>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="formLabel">
      <td width="8">&nbsp;</td>
      <td width="400"><bean:message key="title.formAttachment"/>&nbsp;<bean:write name="attachmentForm" property="planningId" /></td>
      <td>&nbsp;</td>
      <td width="8">&nbsp;</td>
    </tr>
  	</table>
  	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formAttachment.name"/>:&nbsp;</td>
      <td width="400" class="formBody">
		  	<logic:equal name="attachmentForm" property="upload" value="true">
				<html:file name="attachmentForm" property="theFile" styleClass="textBox"/>
				<bean:message key="title.formAttachment.maxSizeWarning"/>&nbsp;(<bean:write name="attachmentForm" property="maxSizeFile" /> KB)
		  	</logic:equal>
		  	<logic:equal name="attachmentForm" property="upload" value="false">
		        <html:text name="attachmentForm" readonly="true" property="name" styleClass="textBox" />
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
	             <html:options collection="typeList" property="id" labelProperty="genericTag"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formAttachment.visibility"/>:&nbsp;</td>
      <td class="formBody">
			<html:hidden name="attachmentForm" property="visibility" value="3"/>      
	  		<html:select name="attachmentForm" property="visibility" styleClass="textBox" disabled="true">
	             <html:options collection="visibilityList" property="id" labelProperty="genericTag"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formAttachment.comment"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="attachmentForm" property="comment" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr> 
    </table>
  	
  	
  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="formLabel">
      <td width="8">&nbsp;</td>
      <td width="120">
		<logic:present name="attachmentForm" property="id">      
			<html:button property="uploadButton" styleClass="button" onclick="javascript:buttonClick('attachmentForm', 'save');">
				<bean:message key="label.formAttachment.upload"/>
			</html:button>
	    </logic:present>
	  	<logic:equal name="attachmentForm" property="upload" value="true">
			<html:button property="uploadButton" styleClass="button" onclick="javascript:buttonClick('attachmentForm', 'save');">
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
			<display:table border="1" width="100%" name="attachmentList" scope="session" pagesize="2">
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridTypeDecorator" />
				  <display:column width="2%" property="id" title="grid.title.empty" />
				  <display:column property="name" title="label.formAttachment.name" />
				  <display:column property="type" title="label.formAttachment.type" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridEditDecorator" tag="'attachmentForm'" />				  
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridDeleteDecorator" tag="'attachmentForm'" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridAttachmentDetailDecorator" />				  
			</display:table>		
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

  </td><td width="10">&nbsp</td>
  </tr></table>

</html:form>

<!-- End of source-code -->
<script> 
	with(document.forms["attachmentForm"]){	
		comment.focus(); 
	}
</script>    	
