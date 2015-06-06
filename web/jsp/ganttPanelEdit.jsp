<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
   		<bean:message key="gantt.edit.title"/>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
    <script language="JavaScript" src="../jscript/calendar1.js" type="text/JavaScript"></script>
	        	
	<script language="JavaScript">
	
		function saveTaskAlloc(){
	    	with(document.forms["ganttPanelEditForm"]){
	    		if (validate()) {
		        	operation.value = "saveAlloc";
					closeMessage();
					submit();	    		
	    		}
	        }
		}
	
		function validate(){
	    	with(document.forms["ganttPanelEditForm"]){	    	
				//if (name.value == ''){
				//    alert("<bean:message key="validate.cost.blankName"/>");
				//   return false;
				//} 	    	
				return true;
	        }
		}		
	</script>

	<html:form action="showGanttEdit">
		<html:hidden name="ganttPanelEditForm" property="id"/>	
		<html:hidden name="ganttPanelEditForm" property="operation"/>
			
		<br>
		
		<table width="490px" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="15">&nbsp;</td><td colspan="3">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="320">
		    		<bean:message key="gantt.edit.title"/>
		      </td>
		      <td>&nbsp;</td>
		      <td width="10">&nbsp;</td>
		    </tr>
		  	</table>
		</td><td width="15">&nbsp;</td></tr>
		
		<tr><td>&nbsp;</td><td>
			
			<table class="table" width="250" border="1" cellspacing="1" cellpadding="2">
			    <tr class="tableRowHeader">
			      <th class="tableCellHeader"><bean:message key="gantt.edit.label.slot"/></th>
			    </tr>
			</table>
			<div id="repository_log_scroll_div" style="width:250px; height:75px; overflow-y: scroll; overflow-x: hidden;">				
				<table class="table" width="230" border="0" cellspacing="0" cellpadding="0">
					<bean:write name="ganttPanelEditForm" property="allocHtml" filter="false"/>
				</table>
			</div>
			<table class="table" width="250" border="1" cellspacing="1" cellpadding="2">
			    <tr class="tableRowHeader">
			      <th class="tableCellHeader">&nbsp;</th>
			    </tr>
			</table>
		
		</td><td width="10">&nbsp;</td>
		<td class="formBody"><bean:message key="label.resTaskForm.comment"/>:<br>
			<textarea name="comment" cols="33" rows="7" class="textBox"></textarea>
		</td>
		<td>&nbsp;</td>
		</tr>
		
		<tr><td width="15">&nbsp;</td><td colspan="3">
		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">			
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="100">      
		      		<html:button property="save" styleClass="button" onclick="javascript:saveTaskAlloc();">
	    	    		&nbsp;&nbsp;&nbsp;<bean:message key="button.ok"/>&nbsp;&nbsp;&nbsp;
		      		</html:button>    
		      </td>
		      <td width="100">      
			      <html:button property="cancel" styleClass="button" onclick="closeMessage();">
		    	    &nbsp;&nbsp;<bean:message key="button.close"/>&nbsp;&nbsp;
			      </html:button>    
		      </td>      
		      <td>&nbsp;</td>      
		      <td width="10">&nbsp;</td>      
		    </tr>
			</table>  	
		</td><td>&nbsp;</td>
		</tr></table>

	</html:form>
</html>