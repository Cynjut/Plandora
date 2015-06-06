<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title><bean:message key="title.invHistory"/></title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
		<style type="text/css">
		#popupfooter {
			position:absolute;
			bottom:0 !important;
			height:20px;
			width:100%;
		}
		</style>		
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >
	<jsp:include page="validator.jsp" />
	
    <script language="JavaScript" src="../jscript/ajaxsync.js" type="text/JavaScript"></script>	
    <script language="JavaScript" src="../jscript/default.js" type="text/JavaScript"></script>		
	<script language="JavaScript">
	
		function viewComment(index){
			with(document.forms["histInvoiceForm"]){
		    	selectedIndex.value = index;
	    		operation.value = "viewComment";
    			submit();
			 }         
	    }
	    
	</script>

	<html:form  action="manageHistInvoice">
		<html:hidden name="histInvoiceForm" property="operation"/>
		<html:hidden name="histInvoiceForm" property="selectedIndex"/>		
	
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="title.invHistory"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<plandora-html:ptable width="100%" name="invHistoryList" pagesize="10" scope="session" ajax="true" frm="histInvoiceForm">					
				  <plandora-html:pcolumn width="120" property="creationDate" title="label.invHistoryDate"/>
				  <plandora-html:pcolumn property="handler.username" title="label.invHistoryUser"/>				
				  <plandora-html:pcolumn property="name" title="label.invoiceForm.name"/>
				  <plandora-html:pcolumn align="center" property="invoiceNumber" title="title.invoiceForm.grid.number" />
				  <plandora-html:pcolumn align="center" property="purchaseOrder" title="title.invoiceForm.grid.purchaseorder" />
				  <plandora-html:pcolumn align="center" property="id" title="label.invoiceForm.status" decorator="com.pandora.gui.taglib.decorator.InvoiceStatusDecorator" />
				  <plandora-html:pcolumn align="center" property="id" title="label.invoiceForm.category" description="label.invoiceForm.category" decorator="com.pandora.gui.taglib.decorator.InvoiceCategoryDecorator" />
				  <plandora-html:pcolumn align="center" property="dueDate" title="title.invoiceForm.grid.duedate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <plandora-html:pcolumn align="center" property="invoiceDate" title="title.invoiceForm.grid.invoicedate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				  <plandora-html:pcolumn align="center" property="contact" title="label.invoiceForm.contact" />
				  <plandora-html:pcolumn align="center" property="id" title="title.invoiceForm.total" decorator="com.pandora.gui.taglib.decorator.InvoiceTotalDecorator" />
				  <plandora-html:pcolumn width="18" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.InvHistoryGridCommentDecorator" />					  
				</plandora-html:ptable>		
			</td>
		</tr> 
		</table>
		      		
		<div id="popupfooter">							
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formLabel">
			   <td width="10">&nbsp;</td>
			   <td><center>
				  <html:button property="close" styleClass="button" onclick="javascript:window.close();">
					<bean:message key="button.close"/>
				  </html:button>    
			   </center></td>
			   <td width="10">&nbsp;</td>      
			</tr>
			</table> 
		</div>
		
	</html:form>
	
	</body>
</html>	