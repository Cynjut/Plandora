<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<html>
	<title><bean:message key="title.occHistory"/></title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >
	<jsp:include page="validator.jsp" />
	
	<script language="JavaScript">
	
		function viewContent(index){
			with(document.forms["histOccForm"]){
		    	selectedIndex.value = index;
	    		operation.value = "viewContent";
    			submit();
			 }         
	    }
	    
	</script>

	<html:form  action="manageHistOccurrence">
		<html:hidden name="histOccForm" property="operation"/>
		<html:hidden name="histOccForm" property="selectedIndex"/>		
	
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="title.occHistory"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>			
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="occHistoryList" scope="session" pagesize="4" completeempty="true">
					  <display:column property="user.name" title="label.occHistoryUser" />
					  <display:column width="30%" align="center" property="occurrenceStatusLabel" title="label.occHistoryStatus" />
					  <display:column width="35%" property="creationDate" align="center" title="label.occHistoryDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <display:column width="3%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.OccHistoryGridContentDecorator" />
				</display:table>		
			</td>
		</tr> 
		</table>
		      		
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
	</html:form>
	
	</body>
</html>
