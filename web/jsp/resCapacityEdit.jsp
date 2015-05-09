<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
   		<bean:message key="label.resCapacity.editpanel.title"/>    	
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	        	
	<script language="JavaScript">
		function saveCapacity(){
	    	with(document.forms["resCapacityEditForm"]){
	    		if (validate()) {
		        	operation.value = "saveCapacity";
		        	closeMessage();
		        	submit();
	         	}
	        }
		}

		function validate(){
	    	with(document.forms["resCapacityEditForm"]){
				if (cost.value == ""){
					alert("<bean:message key="validate.resCapacity.blankCost"/>");
					return false;
				} else if (capacity.value == ""){
					alert("<bean:message key="validate.resCapacity.blankCapacity"/>");
					return false;
	         	} else if (!isAllDigits(capacity.value)){
					alert("<bean:message key="validate.resCapacity.invalidCapacity"/>");
					return false;
	         	} else if (!isCurrency(cost.value)){
					alert("<bean:message key="validate.resCapacity.invalidCost"/>");
					return false;
				}
				return true;
	        }
		}
	</script>

	<html:form  action="showResCapacityEdit">
		<html:hidden name="resCapacityEditForm" property="operation"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp;</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
		    		<bean:message key="label.resCapacity.editpanel.title"/>    	
		      </td>
		      <td>&nbsp;</td>
		      <td width="10">&nbsp;</td>
		    </tr>
		  	</table>

		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="pagingFormBody">	
		       	<td width="100">&nbsp;</td>
			    <td width="20">&nbsp;</td>
				<td>&nbsp;</td>
		    </tr>			

			<tr class="pagingFormBody">	
		       	<td width="100" class="formTitle"><bean:message key="label.resCapacity.project"/>:&nbsp;</td>
			    <td class="formBody" colspan="2">
				  	<html:select name="resCapacityEditForm" property="editProjectId" styleClass="textBox">
				   		<html:options collection="ResCapEditProjectList" property="id" labelProperty="name"/>
					</html:select>
		   	    </td>
		    </tr>			


			<tr class="pagingFormBody">	
		       	<td width="100" class="formTitle"><bean:message key="label.resCapacity.resource"/>:&nbsp;</td>
			    <td class="formBody" colspan="2">
				  	<html:select name="resCapacityEditForm" property="editResourceId" styleClass="textBox">
				   		<html:options collection="ResCapEditResourceList" property="id" labelProperty="name"/>
					</html:select>
		   	    </td>
		    </tr>			
			
			<tr class="pagingFormBody">	
		       	<td width="100" class="formTitle"><bean:message key="title.resCapacity.since"/>:&nbsp;</td>
			    <td class="formBody"  colspan="2">
			    	<plandora-html:calendar name="resCapacityEditForm" property="sinceDate" styleClass="textBoxDisabled" />
		   	    </td>
		    </tr>

		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="title.resCapacity.value"/>:&nbsp;</td>
		      <td colspan="2"><html:text name="resCapacityEditForm" property="capacity" styleClass="textBox" size="8" maxlength="10"/></td>
		    </tr>   
		    
		    <tr class="pagingFormBody">
		      <td width="100" class="formTitle"><bean:message key="title.resCapacity.cost"/>:&nbsp;</td>
			  <td colspan="2" class="formBody" valign="left">
				<bean:write name="resCapacityEditForm" property="currencySymbol" filter="false" />&nbsp;
				<html:text name="resCapacityEditForm" property="cost" styleClass="textBox" size="5" maxlength="10"/>
			  </td>
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
		      <td width="120">      
		      		<html:button property="save" styleClass="button" onclick="javascript:saveCapacity();">
	    	    		<bean:message key="button.update"/>
		      		</html:button>    
		      </td>
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
		<tr><td colspan="3" height="50%">&nbsp;</td></tr>
		</table>

	</html:form>
</html>