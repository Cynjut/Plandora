<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<html>
	<title><bean:message key="title.requestHistoryWindow"/> <bean:write name="histReqForm" property="reqId" /></title>
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
	
	<script language="JavaScript">
	
		function viewComment(index){
			with(document.forms["histReqForm"]){
		    	selectedIndex.value = index;
	    		operation.value = "viewComment";
    			submit();
			 }         
	    }
	    
	</script>

	<html:form  action="manageHistRequest">
		<html:hidden name="histReqForm" property="operation"/>
		<html:hidden name="histReqForm" property="selectedIndex"/>		
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="label.requestHistory.requirement"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>
	
		<table width="100%" border="1" bordercolor="#10389C" cellspacing="1" cellpadding="2">
		<tr class="formBody">
		   <td align="right"><bean:message key="label.requestHistory.estimantedDeadline"/></td>
		   <td align="left"><bean:write name="histReqForm" property="deadlineDateTime" /></td>
		</tr> 		
		<tr class="formBody">
		   <td align="right"><bean:message key="label.requestHistory.suggestedDeadline"/></td>
		   <td align="left"><bean:write name="histReqForm" property="deadlineSuggested" /></td>
		</tr> 	
		<tr class="formBody">
		   <td colspan="2">
		   		<html:textarea name="histReqForm" property="descRequirement" styleClass="textBox" cols="102" rows="4" readonly="true" />
		   </td>
		</tr>	
		</table>
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="title.requestHistory"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>			

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="reqHistoryList" scope="session" pagesize="4" completeempty="true">
					  <display:column property="resource.name" title="label.requestHistoryResource" />
					  <display:column width="25%" align="center" property="status.name" title="label.requestStatus" />
					  <display:column width="27%" property="date" align="center" title="label.requestHistoryDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
		  		      <display:column width="18%" property="iteration" align="center" title="label.occurrence.iteration" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="req_hist"/>					  					  
					  <display:column width="20" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ReqHistoryGridCommentDecorator" />
				</display:table>		
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