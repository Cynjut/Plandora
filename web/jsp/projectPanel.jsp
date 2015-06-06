<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<%@ page import="com.pandora.PreferenceTO"%>

<script language="javascript">
 
	function renderPanelBox(panelId){
		with(document.forms["projectPanelForm"]){	
   			operation.value = "showPanelDiv";
    		panelBoxId.value = panelId;
   		}
		ajaxProcess(document.forms["projectPanelForm"], callBackRenderPanelBox, panelId, "");
	}
	
				
	function callBackRenderPanelBox(panelId, dummy) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			document.getElementById(panelId).innerHTML = content;
	    }  
	}
	
	function changeRole(roleId, panelId, pjId){
		with(document.forms["projectPanelForm"]){	
   			operation.value = "changeViewPanel";
    		panelBoxId.value = panelId;
			projectId.value = pjId;
			genericTag.value = roleId;
   		}
		ajaxProcess(document.forms["projectPanelForm"], callBackChangeRole, panelId, pjId, "");
	}	
	
	function callBackChangeRole(panelId, projectId, dummy) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
	    }  
	}
	
	function comboProjectPanelField(panelId, fieldId){
		with(document.forms["projectPanelForm"]){	
   			operation.value = "showPanelDiv";
    		panelBoxId.value = panelId;
   		}
		ajaxProcess(document.forms["projectPanelForm"], callBackRenderPanelBox, panelId, "");
	}
	
    function editViewer(projId, pBoxId) {
		with(document.forms["projectPanelForm"]){
			displayMessage("../do/showProjectPanelPopup?operation=prepareForm&projectId=" + projId + "&panelBoxId=" + pBoxId, 530, 220);    	    
		}	    	    
    }	
	
</script>
 
<html:form  action="/showProjectPanel">
	<html:hidden name="projectPanelForm" property="projectId"/>
	<html:hidden name="projectPanelForm" property="operation"/>
	<html:hidden name="projectPanelForm" property="panelBoxId"/>
	<html:hidden name="projectPanelForm" property="genericTag"/>
			
	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	  	
		<br>

		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.projectPanelForm"/>
		</display:headerfootergrid>

		<bean:write name="projectPanelForm" property="portletHtml" filter="false" />
		
		<br>
	  	
		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('projectPanelForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			</tr></table>  	
		</display:headerfootergrid>
		
	</td><td width="20">&nbsp;</td>
	</tr>
	</table>

</html:form>

<jsp:include page="footer.jsp" />