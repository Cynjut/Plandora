<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<html:form action="showRepositoryViewer" enctype="multipart/form-data"> 
	<html:hidden name="repositoryViewerForm" property="operation"/>
	<html:hidden name="repositoryViewerForm" property="id"/>	
	
	<br>
	
	<table width="100%" height="420" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td width="10">&nbsp</td>
  	<td valign="top">
  	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="8">&nbsp;</td>
	      <td><bean:message key="label.formRepository.log.title"/>&nbsp;</td>
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
				<div id="repository_log_scroll_div" style="width:600px; height:360px; overflow-y: scroll; overflow-x: hidden;">				
					<bean:write name="repositoryViewerForm" property="logFileListHtml" filter="false"/>
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
			<html:button property="cancel" styleClass="button" onclick="closeMessage();goToForwardPage();">
				  <bean:message key="button.close"/>
			</html:button> 	
		  </center></td>
	      <td width="8">&nbsp;</td>
	    </tr>
	  	</table>


	</td><td width="10">&nbsp</td>
  	</tr>  	
  	</table>

</html:form>