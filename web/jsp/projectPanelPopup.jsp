<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
   		<bean:message key="title.projectPanelForm"/>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<script language="javascript">
	
		function changeRole(roleId, panelId, pjId){
			with(document.forms["projectPanelPopupForm"]){	
				operation.value = "changeViewPanel";
				panelBoxId.value = panelId;
				projectId.value = pjId;
				genericTag.value = roleId;
			}
			ajaxProcess(document.forms["projectPanelPopupForm"], callBackChangeRole, panelId, pjId, "");
		}	
		
		function callBackChangeRole(panelId, projectId, dummy) {  
			if(isAjax()){  
				document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			}  
		}	
	</script>
	
	<html:form action="/showProjectPanelPopup">
		<html:hidden name="projectPanelPopupForm" property="projectId"/>
		<html:hidden name="projectPanelPopupForm" property="operation"/>
		<html:hidden name="projectPanelPopupForm" property="panelBoxId"/>
		<html:hidden name="projectPanelPopupForm" property="genericTag"/>		
			
		<br>
		
		<table width="490px" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="15">&nbsp;</td><td colspan="3">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="320">
		    		<bean:message key="title.projectPanelForm"/>
		      </td>
		      <td>&nbsp;</td>
		      <td width="10">&nbsp;</td>
		    </tr>
		  	</table>
		</td><td width="15">&nbsp;</td></tr>
		
		<tr><td>&nbsp;</td><td class="formBody">
		
			<bean:write name="projectPanelPopupForm" property="panelBoxHtml" filter="false"/>
		
		</td><td width="10">&nbsp;</td>
		<td>&nbsp;</td>
		</tr>
		
		<tr><td width="15">&nbsp;</td><td colspan="3">
		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">			
		    <tr class="formLabel">
		      <td>&nbsp;</td>
		      <td width="100">      
			      <html:button property="cancel" styleClass="button" onclick="closeMessage();">
		    	    &nbsp;&nbsp;<bean:message key="button.close"/>&nbsp;&nbsp;
			      </html:button>    
		      </td>      
		      <td>&nbsp;</td>      
		      <td width="10">&nbsp;</td>      
		    </tr>
			</table>  	
		</td><td>&nbsp;</td>
		</tr></table>

	</html:form>
</html>