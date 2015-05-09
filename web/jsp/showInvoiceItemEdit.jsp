<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
    	<logic:equal name="invoiceItemForm" property="editInvoiceItemId" value="">
    		<bean:message key="title.invoiceForm.itemList.new"/>    	
    	</logic:equal>
    	<logic:notEqual name="invoiceItemForm" property="editInvoiceItemId" value="">
    		<bean:message key="title.invoiceForm.itemList.edit"/>
    	</logic:notEqual>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	        	
	<script language="JavaScript">
		function saveInvoiceItem(){
	    	with(document.forms["invoiceItemForm"]){
	    		if (validate()) {
		        	operation.value = "saveInvoiceItem";
		        	closeMessage();
		        	submit();			
	         	}
	        }
		}

		function validate(){
	    	with(document.forms["invoiceItemForm"]){
				if (amount.value == ""){
					alert("<bean:message key="validate.invoiceForm.blankAmount"/>");
					return false;
				} else if (price.value == ""){
					alert("<bean:message key="validate.invoiceForm.blankPrice"/>");
					return false;
	         	} else if (!isAllDigits(amount.value)){
					alert("<bean:message key="validate.invoiceForm.invalidAmount"/>");
					return false;
	         	} else if (!isCurrency(price.value)){
					alert("<bean:message key="validate.invoiceForm.invalidPrice"/>");
					return false;
				}
				return true;
	        }
		}
	         		

	</script>

	<html:form  action="/showInvoiceItem">
		<html:hidden name="invoiceItemForm" property="invoiceId"/>
		<html:hidden name="invoiceItemForm" property="operation"/>
		<html:hidden name="invoiceItemForm" property="editInvoiceItemId"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
			    	<logic:equal name="invoiceItemForm" property="editInvoiceItemId" value="">
			    		<bean:message key="title.invoiceForm.itemList.new"/>    	
			    	</logic:equal>
			    	<logic:notEqual name="invoiceItemForm" property="editInvoiceItemId" value="">
			    		<bean:message key="title.invoiceForm.itemList.edit"/>
			    	</logic:notEqual>
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
		       	<td width="100" class="formTitle"><bean:message key="label.invoiceForm.itemList.type"/>:&nbsp;</td>
			    <td class="formBody"  colspan="2">
				  	<html:select name="invoiceItemForm" property="type" styleClass="textBox">
				   		<html:options collection="invoiceTypes" property="id" labelProperty="genericTag"/>
					</html:select>
		   	    </td>
	    	    <td width="10">&nbsp;</td>
		    </tr>

		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.invoiceForm.itemList.name"/>:&nbsp;</td>
		      <td  colspan="2"><html:text name="invoiceItemForm" property="itemName" styleClass="textBox" size="30" maxlength="50"/></td>
		      <td>&nbsp;</td>
		    </tr>   
		    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.invoiceForm.form.qty"/>:&nbsp;</td>
		      <td  colspan="2"><html:text name="invoiceItemForm" property="amount" styleClass="textBox" size="8" maxlength="3"/></td>
		      <td>&nbsp;</td>
		    </tr>   
		    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.invoiceForm.itemList.prc"/>:&nbsp;</td>
		      <td class="formTitle" width="20"><bean:write name="invoiceItemForm" property="currencySymbol" />&nbsp;</td>
		      <td><html:text name="invoiceItemForm" property="price" styleClass="textBox" size="10" maxlength="20"/></td>
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
		      		<html:button property="save" styleClass="button" onclick="javascript:saveInvoiceItem();">
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