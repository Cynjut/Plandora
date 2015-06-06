<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
   		<bean:message key="title.agilePanelForm.edit"/>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	        	
	<script language="JavaScript">
		function saveTask(){
	    	with(document.forms["agilePanelTaskForm"]){
	    		if (name.value != "") {
		        	operation.value = "saveTask";
		        	closeMessage();
		        	submit();			
	         	} else {
	         		alert("<bean:message key="message.agilePanelForm.mandatoryField"/>");
	         	}
	        }
		}
	</script>

	<html:form  action="/showAgilePanelTask">
		<html:hidden name="agilePanelTaskForm" property="taskProjectId"/>
		<html:hidden name="agilePanelTaskForm" property="operation"/>
		<html:hidden name="agilePanelTaskForm" property="taskId"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
			      <bean:message key="title.agilePanelForm.edit"/>		  
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
		      <td width="100" class="formTitle"><bean:message key="label.manageTask.name"/>:&nbsp;</td>
		      <td><html:text name="agilePanelTaskForm" property="name" styleClass="textBox" size="50" maxlength="50"/></td>
		      <td width="10">&nbsp;</td>
		    </tr>
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.manageTask.desc"/>:&nbsp;</td>
		      <td><html:textarea name="agilePanelTaskForm" property="description" styleClass="textBox" cols="60" rows="5" /></td>
		      <td>&nbsp;</td>
		    </tr>	
		    
			<tr class="pagingFormBody">	
		       	<td class="formTitle"><bean:message key="label.manageTask.taskParent"/>:&nbsp;</td>    
			    <td class="formBody">
				  	<html:select name="agilePanelTaskForm" property="parentTaskId" styleClass="textBox">
			        <option value="-1">
			           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			        </option>		  		
				    <html:options collection="taskAvailable" property="id" labelProperty="name" filter="false"/>
					</html:select>
		   	    </td>
	    	    <td>&nbsp;</td>
		    </tr>
		    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="title.agilePanelForm.edit.story"/>:&nbsp;</td>
		      <td>
		      	<logic:equal name="agilePanelTaskForm" property="taskId" value="">		      
			  		<html:select name="agilePanelTaskForm" property="requirementId" styleClass="textBox" disabled="true">
						<html:options collection="requirementList" property="id" labelProperty="comboName" filter="false"/>
					</html:select>		      		      	
		      	</logic:equal>
		      	<logic:notEqual name="agilePanelTaskForm" property="taskId" value="">		      
			  		<html:select name="agilePanelTaskForm" property="requirementId" styleClass="textBox">
						<html:options collection="requirementList" property="id" labelProperty="comboName" filter="false"/>
					</html:select>		      		      	
		      	</logic:notEqual>		      	
		      </td>
		      <td>&nbsp;</td>
		    </tr>		    	
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="title.agilePanelForm.edit.category"/>:&nbsp;</td>
		      <td>
		  			<html:select name="agilePanelTaskForm" property="categoryId" styleClass="textBox">
						<html:options collection="taskCategoryList" property="id" labelProperty="name" filter="false"/>
					</html:select>		      	
		      </td>
		      <td>&nbsp;</td>
		    </tr>		    	
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="title.agilePanelForm.edit.assigment"/>:&nbsp;</td>
		      <td>
		      	<logic:equal name="agilePanelTaskForm" property="taskId" value="">
			  		<html:select name="agilePanelTaskForm" property="resourceId" styleClass="textBox">
						<html:options collection="projectResourceList" property="id" labelProperty="name" filter="false"/>
					</html:select>		      	
		      	</logic:equal>
		      	<logic:notEqual name="agilePanelTaskForm" property="taskId" value="">
			  		<html:select name="agilePanelTaskForm" property="resourceId" styleClass="textBox" disabled="true">
						<html:options collection="projectResourceList" property="id" labelProperty="name" filter="false"/>
					</html:select>		      	
		      	</logic:notEqual>		      	
		      </td>
		      <td>&nbsp;</td>
		    </tr>		    	
			</table>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		        <td width="100" class="formTitle"><bean:message key="title.agilePanelForm.edit.estDate"/>:&nbsp;</td>
		    	<logic:equal name="agilePanelTaskForm" property="isOpen" value="on">
				      <td width="110" class="formBody"><plandora-html:calendar name="agilePanelTaskForm" property="estimatedDate" styleClass="textBox" /></td>
				      <td width="110" class="formTitle"><bean:message key="title.agilePanelForm.edit.estTime"/>:&nbsp;</td>
		              <td class="formBody"><html:text name="agilePanelTaskForm" property="estimatedTime" styleClass="textBox" size="4" maxlength="6"/>&nbsp;<bean:message key="label.hour"/></td>
				</logic:equal>
		    	<logic:equal name="agilePanelTaskForm" property="isOpen" value="off">
				      <td width="110" class="formBody"><html:text name="agilePanelTaskForm" property="estimatedDate" styleClass="textBoxDisabled" size="10" maxlength="10" disabled="true"/></td>
				      <td width="110" class="formTitle"><bean:message key="title.agilePanelForm.edit.estTime"/>:&nbsp;</td>
				      <td class="formBody"><html:text name="agilePanelTaskForm" property="estimatedTime" styleClass="textBoxDisabled" size="4" maxlength="6" disabled="true"/>&nbsp;<bean:message key="label.hour"/></td>
				</logic:equal>
				<td width="10">&nbsp;</td>
		    </tr>
		    <tr class="pagingFormBody">
		      <td class="formTitle">&nbsp;</td>		    
		      <td colspan="3" class="formBody">
			   		<html:checkbox property="isUnPredictable" name="agilePanelTaskForm" >
			   			<bean:message key="title.agilePanelForm.edit.predictable"/>
			   		</html:checkbox>		      		    		
		      </td>
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
		      		<html:button property="save" styleClass="button" onclick="javascript:saveTask();">
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