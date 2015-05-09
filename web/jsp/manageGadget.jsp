<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<html:form action="manageGadget" enctype="multipart/form-data"> 
	<html:hidden name="gadgetForm" property="operation"/>
	<html:hidden name="gadgetForm" property="id"/>	
	
	<br>
	
	<table width="100%" height="420" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td width="10">&nbsp</td>
	<td width="150" valign="top">
	
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
      			<bean:write name="gadgetForm" property="htmlGadgetList" filter="false"/>
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
	      <td><bean:message key="label.manageOption.gadget"/>&nbsp;</td>
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
				<div id="gadget_body_scroll_div" style="width:490px; height:310px; overflow: scroll;">
					<div ID="GADGET_BODY" />
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
			<html:button property="cancel" styleClass="button" onclick="window.location='../do/manageResourceHome?operation=refreshList'; closeMessage();">
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