<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title><bean:message key="title.occHistory"/></title>
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
				<plandora-html:ptable width="100%" name="occHistoryList" pagesize="10" scope="session" ajax="true" frm="histOccForm">				
					  <plandora-html:pcolumn property="user.name" title="label.occHistoryUser" />
					  <plandora-html:pcolumn width="30%" align="center" property="occurrenceStatusLabel" title="label.occHistoryStatus" />
					  <plandora-html:pcolumn width="35%" property="creationDate" align="center" title="label.occHistoryDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <plandora-html:pcolumn width="3%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.OccHistoryGridContentDecorator" />
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
