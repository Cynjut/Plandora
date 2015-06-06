<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<title>
    	<bean:message key="title.agilePanelForm.newReq.title"/>		  
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
    <script language="JavaScript" src="../jscript/ajax.js" type="text/JavaScript"></script>	
    	        	
	<script language="JavaScript">
		function saveReq(){
	    	with(document.forms["agilePanelReqForm"]){
	    		if (reqDescription.value != "") {
		        	operation.value = "saveReq";
		        	closeMessage();
		        	submit();			
	         	} else {
	         		alert("<bean:message key="message.agilePanelForm.mandatoryField"/>");
	         	}
	        }
		}	
		
		function refreshProject(){
			var prjId;
			with(document.forms["agilePanelReqForm"]){
	   			operation.value = "refreshCategory";
	   			prjId = reqProjectId.value;
	   		}
		    var ajaxRequestObj = ajaxSyncInit();         
			ajaxSyncProcess(document.forms["agilePanelReqForm"], callBackRefreshProject, prjId, true, ajaxRequestObj);		
		}
		
					
		function callBackRefreshProject(prjId, dummy, objRequest) {			
			if(isSyncAjax(objRequest)){
		       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
				var content = objRequest.responseText;
			   	(document.getElementById("project_category")).innerHTML = content;  
		    }  
		}
		
	</script>

	<html:form  action="showAgilePanelReq">
		<html:hidden name="agilePanelReqForm" property="operation"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
			      <bean:message key="title.agilePanelForm.newReq.title"/>		  
		      </td>
		      <td>&nbsp;</td>
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
		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		      <td width="100" class="formTitle"><bean:message key="label.requestProject"/>:&nbsp;</td>		      
		      <td>
		  		<html:select name="agilePanelReqForm" property="reqProjectId" styleClass="textBox" onkeypress="javascript:refreshProject();" onchange="javascript:refreshProject();">
					<html:options collection="projectList" property="id" labelProperty="name" filter="false"/>
				</html:select>
		      </td>
		      <td>&nbsp;</td>
		    </tr>		    	
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.requestDesc"/>:&nbsp;</td>
		      <td><html:textarea name="agilePanelReqForm" property="reqDescription" styleClass="textBox" cols="60" rows="5" /></td>
		      <td>&nbsp;</td>
		    </tr>	
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.requestPriority"/>:&nbsp;</td>		      
		      <td>
		  		<html:select name="agilePanelReqForm" property="priority" styleClass="textBox">
					<html:options collection="priorityList" property="id" labelProperty="genericTag" filter="false"/>
				</html:select>
		      </td>
		      <td>&nbsp;</td>
		    </tr>	
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.requestCategory"/>:&nbsp;</td>		      
		      <td>
		      		<div id="project_category" name="project_category"></div>
		      </td>
		      <td>&nbsp;</td>
		    </tr>			    	    	
		    			    	    	
		    </table>
		    			
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
		      		<html:button property="save" styleClass="button" onclick="javascript:saveReq();">
	    	    		<bean:message key="button.ok"/>
		      		</html:button>    
		      </td>
		      <td width="100">      
			      <html:button property="cancel" styleClass="button" onclick="closeMessage();">
		    	    <bean:message key="button.close"/>
			      </html:button>    
		      </td>      
		      <td>&nbsp;</td>      
		      <td width="10">&nbsp;</td>      
		    </tr>
			</table>  	
		
		</td><td width="20">&nbsp</td>
		</tr>
		<tr><td colspan="3" height="50%">&nbsp</td></tr>
		</table>

	</html:form>
</html>

<script> 
   	refreshProject();	
</script>