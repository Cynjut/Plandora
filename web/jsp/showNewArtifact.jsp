<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<html:form action="showNewArtifact" enctype="multipart/form-data" onsubmit="return false"> 
	<html:hidden name="newArtifactForm" property="operation"/>
	<html:hidden name="newArtifactForm" property="id"/>
	<html:hidden name="newArtifactForm" property="projectId"/>
	<html:hidden name="newArtifactForm" property="selectedTemplate"/>	

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td width="10">&nbsp;</td>
	<td valign="top">
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="8">&nbsp;</td>
		  <td><bean:message key="label.artifactTag.new"/>&nbsp;</td>
		  <td width="8">&nbsp;</td>
		</tr>
	  	</table>
		
		<br>
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="70" class="formTitle"><bean:message key="label.artifactTag.name"/>:&nbsp;</td>
	      <td class="formBody">
	        	<html:text name="newArtifactForm" property="name" styleClass="textBox" size="70" maxlength="70"/>
	      </td>
	      <td width="10">&nbsp;</td>
	    </tr>
		</table>
		
	</td><td width="10">&nbsp;</td>
  	</tr>
  	<table>
  	
	
	<br>
	
	
	<table width="100%" height="225" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td width="10">&nbsp;</td>
	<td width="170" valign="top">
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel" height="20">
	      <td width="8">&nbsp;</td>
	      <td width="150">&nbsp;</td>
	      <td width="8">&nbsp;</td>
	    </tr>
	  	</table>
  	
		<table width="100%" height="85%" border="0" cellspacing="0" cellpadding="0">
    	<tr class="gapFormBody">
      		<td colspan="3">&nbsp;</td>
    	</tr>
    
    	<tr class="pagingFormBody" valign="top" height="90%">
      		<td width="10">&nbsp;</td>
      		<td class="formBody">
      			<bean:write name="newArtifactForm" property="htmlArtifactList" filter="false"/>
      		</td>
      		<td width="10">&nbsp;</td>
    	</tr>
    	
	    <tr class="gapFormBody">
	      <td colspan="3">&nbsp;</td>
	    </tr> 
	    </table>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="8">&nbsp;</td>
	      <td width="150">&nbsp;</td>
	      <td width="8">&nbsp;</td>
	    </tr>
	  	</table>
		
  	</td>
  	<td width="10">&nbsp;</td>
  	<td valign="top">
  	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="8">&nbsp;</td>
	      <td><bean:message key="label.artifactTag.template.list"/>&nbsp;</td>
	      <td width="8">&nbsp;</td>
	    </tr>
	  	</table>
  	
		<table width="100%" height="85%" border="0" cellspacing="0" cellpadding="0">
    	<tr class="gapFormBody">
      		<td colspan="3">&nbsp;</td>
    	</tr>
    
    	<tr class="pagingFormBody">
      		<td width="10">&nbsp;</td>
      		<td class="formBody">
				<div id="artifact_body_scroll_div" style="width:380px; height:220px; overflow: scroll;">
					<div ID="ARTIFACT_BODY" />
				</div>
      		</td>
      		<td width="10">&nbsp;</td>
    	</tr>
    	
	    <tr class="gapFormBody">
	      <td colspan="3">&nbsp;</td>
	    </tr> 
	    </table>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="8">&nbsp;</td>
	      <td><center>
			<html:button property="select" styleClass="button" onclick="selectTemplate();">
				  <bean:message key="button.ok"/>
			</html:button> 	
		  </center></td>
	      <td><center>
			<html:button property="cancel" styleClass="button" onclick="closeMessage();">
				  <bean:message key="button.close"/>
			</html:button> 	
		  </center></td>		  
	      <td width="8">&nbsp;</td>
	    </tr>
	  	</table>

	</td><td width="10">&nbsp;</td>
  	</tr>
  	
  	</table>

</html:form>