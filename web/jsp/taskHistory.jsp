<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<html>
	<title><bean:message key="title.taskHistoryWindow"/><bean:write name="histTaskForm" property="taskId" filter="false"/></title>
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
    <script>
		function viewComment(index){
			with(document.forms["histTaskForm"]){
		    	selectedIndex.value = index;
	    		operation.value = "viewComment";
    			submit();
			 }         
	    }

     	function showFUP(){
			buttonClick("histTaskForm", "showFollowUp");
     	}	    
		
    </script>	

	<html:form  action="manageHistTask">
		<html:hidden name="histTaskForm" property="operation"/>
		<html:hidden name="histTaskForm" property="selectedIndex"/>		

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="title.taskHistory"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
			    <plandora-html:ptable width="100%" ajax="true" name="taskHistoryList" pagesize="10" scope="session" frm="histTaskForm">
					  <plandora-html:pcolumn width="40" align="center" property="resourceTask.task.id" title="grid.title.empty" />			
					  <plandora-html:pcolumn property="handler.username" align="center" title="label.taskHistoryResource" />
					  <plandora-html:pcolumn width="70" property="status.name" align="center" title="label.manageTask.taskStatus" />
					  <plandora-html:pcolumn width="120" property="date" align="center" title="label.taskHistoryDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <plandora-html:pcolumn width="70" property="actualDate" align="center" title="label.manageTask.grid.acualDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
	  				  <plandora-html:pcolumn width="60" property="actualTimeInHours" align="center" title="label.manageTask.grid.actualTime" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
	  				  <plandora-html:pcolumn width="70" property="startDate" align="center" title="label.manageTask.grid.initDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
	  				  <plandora-html:pcolumn width="60" property="estimatedTimeInHours" align="center" title="label.manageTask.grid.estTime" decorator="com.pandora.gui.taglib.decorator.GridFloatDecorator" tag="h"/>
	  				  <plandora-html:pcolumn width="80" property="iteration.name" align="center" title="label.manageTask.grid.iteration"/>
					  <plandora-html:pcolumn width="13" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.TaskHistoryGridCommentDecorator" />
				</plandora-html:ptable>		
			</td>
		</tr> 
		</table>

		<div id="popupfooter">			      		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formLabel">
			   <td width="10">&nbsp;</td>
			   <td width="50">&nbsp;</td>
			   <td><center>
			      <html:button property="close" styleClass="button" onclick="javascript:window.close();">
		    	    <bean:message key="button.close"/>
			      </html:button>    
			   </center></td>
			   <td width="50" align="right">
			      <html:button property="followUp" styleClass="button" onclick="javascript:showFUP();">
		    	    <bean:message key="label.taskHistoryFUP"/>
			      </html:button>    		   
			   </td>
			   <td width="10">&nbsp;</td>      
			</tr>
			</table>
		</div>

	</html:form>

	</body>
</html>	