<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<title>
		<bean:write name="showGadgetPropertyForm" property="propertyTitle" />
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	<script language="JavaScript">
	
		function saveForm(){
	    	with(document.forms["showGadgetPropertyForm"]){
				if (operation.value!= "") {
		        	closeMessage();
		        	submit();			
				}
	        }       	
		}
	
	</script>
	
<html:form  action="showGadgetProperty">
	<html:hidden name="showGadgetPropertyForm" property="operation"/>
	<html:hidden name="showGadgetPropertyForm" property="gagid"/>
	<html:hidden name="showGadgetPropertyForm" property="forwardAfterSave"/>
	
	<br>
		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp</td><td>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formLabel">
	      <td width="10">&nbsp;</td>
	      <td width="350">
		      <bean:message key="label.manageOption.gadget.property"/>		  
	      </td>
	      <td>&nbsp;</td>
	      <td width="10">&nbsp;</td>
	    </tr>
	  	</table>
	
		<div id="GADGET_PROPERTY_ID" name="GADGET_PROPERTY_ID">
		    <bean:write name="showGadgetPropertyForm" property="fieldsHtml" filter="false" />	
	    </div>
	    
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
	      		<html:button property="saveForm" styleClass="button" onclick="javascript:closeMessage();buttonClick('showGadgetPropertyForm', 'saveForm');">
    	    		<bean:message key="button.update"/>
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