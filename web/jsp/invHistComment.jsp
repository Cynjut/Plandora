<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<html>
	<title><bean:message key="title.invHistory"/></title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >
	<jsp:include page="validator.jsp" />
	

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formLabel">
	   <td width="10">&nbsp;</td>
	   <td width="350"><bean:message key="title.invHistory"/></td>
	   <td>&nbsp;</td>
	   <td width="10">&nbsp;</td>
	</tr>
	</table>
	
	<table class="table" width="100%" border="1" bordercolor="#10389C" cellspacing="1" cellpadding="2">
		<tr class="tableRowEven">
		   <td colspan="2">
				<html:textarea name="histInvoiceForm" property="historyContent" styleClass="textBox" cols="160" rows="16" readonly="true" />
		   </td>
		</tr>	
	</table>
		      		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td><center>
		      <html:button property="back" styleClass="button" onclick="javascript:window.history.back();">
	    	    <bean:message key="button.back"/>
		      </html:button>    
		   </center></td>
		   <td width="10">&nbsp;</td>      
		</tr>
	</table> 
		
	</body>
</html>
	