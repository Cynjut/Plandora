<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	<script language="JavaScript">
	
		function submitSnip(){
			var snp;
			with(document.forms["snipArtifactForm"]){
	   			operation.value = "submitSnip";
				snp = snip.value;
	   		}
			ajaxProcess(document.forms["snipArtifactForm"], callBackSubmitSnip, snp);
		}
		
					
		function callBackSubmitSnip(snipId) {
		    if(isAjax()){
		       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
				var content = objRequest.responseText;

				//call the method from manageArtifact.jsp that shows the content into mceTiny object
				showEditor(content); 0
		    }  
		}		
	</script>
	
<html:form  action="showSnipArtifact">
	<html:hidden name="snipArtifactForm" property="operation"/>
	<html:hidden name="snipArtifactForm" property="snip"/>
	
	<br>
		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp</td><td>
		
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
	
	</td><td width="20">&nbsp</td>
	</tr>
	</table>

</html:form>