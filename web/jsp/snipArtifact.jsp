<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

	
<html:form action="showSnipArtifact">
	<html:hidden name="snipArtifactForm" property="operation"/>
	<html:hidden name="snipArtifactForm" property="snip"/>
	<html:hidden name="snipArtifactForm" property="refreshCommand"/>
	
	<br>
		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="10">&nbsp;</td>
	      <td>
		      <bean:write name="snipArtifactForm" property="popupTitle" filter="false" />		  
	      </td>
	      <td width="10">&nbsp;</td>
	    </tr>
	  	</table>

	    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
			<td width="10">&nbsp;</td>
			<td>&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
	    </table>
	
	    <bean:write name="snipArtifactForm" property="htmlFormBody" filter="false" />	

	    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
			<td width="10">&nbsp;</td>
			<td>&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
	    </table>
		
	  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="10">&nbsp;</td>
	      <td width="100">      
	      		<html:button property="subSnip" styleClass="button" onclick="javascript:closeMessage();submitSnip();">
    	    		<bean:message key="button.ok"/>
	      		</html:button>    
	      </td>
	      <td width="30">&nbsp;</td>	      
	      <td width="100">      
		      <html:button property="cancel" styleClass="button" onclick="closeMessage();">
	    	    <bean:message key="button.cancel"/>
		      </html:button>    
	      </td>      
	      <td>&nbsp;</td>      
	      <td width="10">&nbsp;</td>      
	    </tr>
		</table>  	
	
	</td><td width="20">&nbsp;</td>
	</tr>
	</table>
</html:form>