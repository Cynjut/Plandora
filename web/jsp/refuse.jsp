<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<title>
		<logic:equal name="refuseForm" property="refuseType" value="REQ">
	    	<bean:message key="label.refuse.refReq"/>		  
		</logic:equal>
	  	<logic:equal name="refuseForm" property="refuseType" value="TSK">
	      	<bean:message key="label.refuse.cancTask"/>
	  	</logic:equal>
		<bean:write name="refuseForm" property="refusedId" filter="false"/>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	<script language="JavaScript">

		function refuseIt(){
	    	with(document.forms["refuseForm"]){
	    		if (comment.value != "") {
	    			if (refuseType.value=="REQ") {
			        	operation.value = "refuseRequirement";
	    			} else if (refuseType.value=="TSK") {
			        	operation.value = "cancelTask";
					} else {
						operation.value="";
					}
					if (operation.value!= "") {
			        	closeMessage();
			        	submit();			
					}
	         	} else {
	         		alert("<bean:message key="message.refuse.mandatoryComment"/>");
	         	}
	        }       	
		}

	</script>

	<html:form  action="refuse">
	<html:hidden name="refuseForm" property="operation"/>
	<html:hidden name="refuseForm" property="refusedId"/>
	<html:hidden name="refuseForm" property="refuseType"/>
	<html:hidden name="refuseForm" property="forwardAfterRefuse"/>
	<html:hidden name="refuseForm" property="relatedRequirementId"/>
		
	<br>
	
	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp</td><td>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="10">&nbsp;</td>
	      <td width="350">
			  <logic:equal name="refuseForm" property="refuseType" value="REQ">
			      <bean:message key="label.refuse.refReq"/>		  
		      </logic:equal>
			  <logic:equal name="refuseForm" property="refuseType" value="TSK">
			      <bean:message key="label.refuse.cancTask"/>		  
		      </logic:equal>	      
	      </td>
	      <td>&nbsp;</td>
	      <td width="10">&nbsp;</td>
	    </tr>
	  	</table>
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="80" class="formTitle"><bean:message key="label.refuse.comment"/></td>
	      <td>&nbsp;</td>
	      <td width="10">&nbsp;</td>
	    </tr>	
	    </table>
	    
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td class="formBody">
			<logic:present name="refuseForm" property="relatedRequirementId">	      
	        	<html:textarea name="refuseForm" property="comment" styleClass="textBox" cols="85" rows="3" />
	    	</logic:present>    	
			<logic:notPresent name="refuseForm" property="relatedRequirementId">	      
	        	<html:textarea name="refuseForm" property="comment" styleClass="textBox" cols="85" rows="5" />
	    	</logic:notPresent>    	
		  </td>
	      <td width="10">&nbsp;</td>
		</tr>
		</table>

		<logic:present name="refuseForm" property="relatedRequirementId">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.refuse.cancTaskreq"/></td>
		    </tr>	
		    </table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		      <td width="10">&nbsp;</td>
		      <td class="formBody">
		      	<html:radio name="refuseForm" property="reopenReqAfterTaskCancelation" value="true"/><bean:message key="label.refuse.cancTask.reqyes"/><br/>
		      	<html:radio name="refuseForm" property="reopenReqAfterTaskCancelation" value="false"/><bean:message key="label.refuse.cancTask.reqno"/>
			  </td>
		      <td width="10">&nbsp;</td>
			</tr>
			</table>
		</logic:present>
		
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
	      		<html:button property="refuse" styleClass="button" onclick="javascript:refuseIt();">
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

<script>
document.forms["refuseForm"].comment.value = "";
document.forms["refuseForm"].comment.focus();
</script>  