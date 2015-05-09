<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
    	<logic:equal name="costEditForm" property="editCostId" value="">
    		<bean:message key="title.costedit.new"/>    	
    	</logic:equal>
    	<logic:notEqual name="costEditForm" property="editCostId" value="">
    		<bean:message key="title.costedit.edit"/>
    	</logic:notEqual>
	</title>
	
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
    <script language="JavaScript" src="../jscript/calendar1.js" type="text/JavaScript"></script>
	        	
	<script language="JavaScript">
	
		function changeInstallment(fieldId, installment, visualObjType){
			with(document.forms["costEditForm"]){
	   			operation.value = "changeInstallment";
				var obj = document.getElementById(fieldId);
	   			changeInstValue.value = obj.value;
	   			changeInstId.value = installment;
	   			changeInstType.value = visualObjType;
				ajaxProcess(document.forms["costEditForm"], callBackChangeInstallment, fieldId);			
	   		}			
		}

		function callBackChangeInstallment(fieldId) {  
		    if(isAjax()){
		       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
				var content = objRequest.responseText;
				if (content) {
					alert(content);
					var obj = document.getElementById(fieldId);				
					if (obj) {
						obj.focus();
					}
				}  
		    }  
		}

		
		function addInstallment(){
			with(document.forms["costEditForm"]){
	   			operation.value = "addInstallment";
				ajaxProcess(document.forms["costEditForm"], callBackRefreshInstallment, '');			
	   		}
		}		

		function removeInstallment(rmdId){
			with(document.forms["costEditForm"]){
	   			operation.value = "removeInstallment";
	   			removeInstId.value = rmdId;
				ajaxProcess(document.forms["costEditForm"], callBackRefreshInstallment, rmdId);			
	   		}
		}		
					
		function callBackRefreshInstallment(dummy) {  
		    if(isAjax()){
		       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
				var content = objRequest.responseText;
			   	(document.getElementById("cost_installment_body")).innerHTML = content;  
		    }  
		}


		function saveCost(){
	    	with(document.forms["costEditForm"]){
	    		if (validate()) {
		        	operation.value = "saveCost";
					closeMessage();
					submit();	    		
	    		}
	        }
		}
	
		function validate(){
	    	with(document.forms["costEditForm"]){
	    	
				if (name.value == ''){
				    alert("<bean:message key="validate.cost.blankName"/>");
				    return false;
				} 	    	
	    	
	    		<bean:write name="costEditForm" property="installmentHtmlValidation" filter="false" />
				return true;
	        }
		}
		
		function changeProject(){
			with(document.forms["costEditForm"]){
	   			operation.value = "changeProject";
				ajaxProcess(document.forms["costEditForm"], callBackChangeProject, projectId.value);			
	   		}
		}		
					
		function callBackChangeProject(prjId) {  
		    if(isAjax()){
		       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
				var content = objRequest.responseText;
			   	(document.getElementById("cost_category")).innerHTML = content;  
		    }  
		}

		function changeAccount(){
			with(document.forms["costEditForm"]){
				accountCode.value = accountCodeSelected.value;
	   		}
		}		

		
	</script>

	<html:form action="showCostEdit">
		<html:hidden name="costEditForm" property="id"/>	
		<html:hidden name="costEditForm" property="operation"/>
		<html:hidden name="costEditForm" property="editCostId"/>
		<html:hidden name="costEditForm" property="removeInstId"/>
		<html:hidden name="costEditForm" property="changeInstValue"/>
		<html:hidden name="costEditForm" property="changeInstId"/>
		<html:hidden name="costEditForm" property="changeInstType"/>
		<html:hidden name="costEditForm" property="usedByExpenseForm"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp;</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
			    	<logic:equal name="costEditForm" property="editCostId" value="">
			    		<bean:message key="title.costedit.new"/>    	
			    	</logic:equal>
			    	<logic:notEqual name="costEditForm" property="editCostId" value="">
			    		<bean:message key="title.costedit.edit"/>
			    	</logic:notEqual>
		      </td>
		      <td>&nbsp;</td>
		      <td width="10">&nbsp;</td>
		    </tr>
			<tr class="gapFormBody">
				<td colspan="4">&nbsp;</td>
			</tr>
		  	</table>

			<logic:empty name="metaFieldCostList">
				<div id="costFormDiv">				
			</logic:empty>
			<logic:notEmpty name="metaFieldCostList">
				<div id="costFormDiv" style="width:500px; height:240px; overflow-y: auto; overflow-x: hidden;">				
			</logic:notEmpty>
							
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
			    <tr class="pagingFormBody">
			      <td width="100" class="formTitle"><bean:message key="label.costedit.name"/>:&nbsp;</td>
			      <td colspan="2"><html:text name="costEditForm" property="name" styleClass="textBox" size="30" maxlength="30"/></td>
			      <td width="10">&nbsp;</td>
			    </tr>   
	
			    <tr class="pagingFormBody">
			      <td class="formTitle"><bean:message key="label.costedit.desc"/>:&nbsp;</td>
			      <td colspan="2"><html:textarea name="costEditForm" property="description" styleClass="textBox" cols="70" rows="3" /></td>
			      <td>&nbsp;</td>
			    </tr>   
				
				<tr class="pagingFormBody">	
			       	<td class="formTitle"><bean:message key="label.costedit.prj"/>:&nbsp;</td>
				    <td class="formBody" colspan="2">
				    	<logic:equal name="costEditForm" property="usedByExpenseForm" value="on">
						  	<html:select name="costEditForm" property="projectId" styleClass="textBox" disabled="true">
						   		<html:options collection="costProjectList" property="id" labelProperty="name"/>
							</html:select>
				    	</logic:equal>
				    	<logic:equal name="costEditForm" property="usedByExpenseForm" value="off">
						  	<html:select name="costEditForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
						   		<html:options collection="costProjectList" property="id" labelProperty="name"/>
							</html:select>
				    	</logic:equal>
			   	    </td>
		    	    <td width="10">&nbsp;</td>
			    </tr>
	
				<tr class="pagingFormBody">	
			       	<td class="formTitle"><bean:message key="label.costedit.category"/>:&nbsp;</td>
				    <td class="formBody" colspan="2">			    
				    	<div id="cost_category">
						  	<html:select name="costEditForm" property="categoryId" styleClass="textBox">
						   		<html:options collection="costCategoryList" property="id" labelProperty="name"/>
							</html:select>
						</div>
			   	    </td>
		    	    <td width="10">&nbsp;</td>
			    </tr>
	
			    <tr class="pagingFormBody">
			      <td class="formTitle"><bean:message key="label.costedit.acccode"/>:&nbsp;</td>
			      <td class="formBody" colspan="2"><html:text name="costEditForm" property="accountCode" styleClass="textBox" size="17" maxlength="30"/>
						&nbsp;&nbsp;&nbsp;
					  	<html:select name="costEditForm" property="accountCodeSelected" styleClass="textBox" onkeypress="javascript:changeAccount();" onchange="javascript:changeAccount();">
					   		<html:options collection="accountCodeList" property="id" labelProperty="genericTag"/>
						</html:select>
			      </td>
			      <td>&nbsp;</td>
			    </tr>   
			    </table>

				<plandora-html:metafield name="costEditForm" collection="metaFieldCostList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="100" /> 
				
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="gapFormBody">	
					<td width="100">&nbsp;</td>
					<td width="20">&nbsp;</td>
					<td width="100">&nbsp;</td>
					<td>&nbsp;</td>
					<td width="70">&nbsp;</td>
					<td width="20">&nbsp;</td>
					<td width="60">&nbsp;</td>
				</tr>		    
				<tr class="pagingFormBody">	
					<td>&nbsp;</td>
					<td class="formBody" colspan="5">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr class="formLabel">	
							<logic:equal name="costEditForm" property="usedByExpenseForm" value="on">
								<td><b>&nbsp;<bean:message key="label.expense.installment"/></b></td>
								<td>&nbsp;</td>						
							</logic:equal>
							<logic:equal name="costEditForm" property="usedByExpenseForm" value="off">
								<td><b>&nbsp;<bean:message key="label.costedit.installment"/></b></td>
								<td>
									<html:button property="add" styleClass="button" onclick="javascript:addInstallment();">
										<bean:message key="button.addInto"/>
									</html:button>    												
								</td>										    		    	
							</logic:equal>
						</tr>		    
						</table>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr class="pagingFormBody" height="20">
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="formTitle"><b><center>
						<logic:equal name="costEditForm" property="usedByExpenseForm" value="on">
							<bean:message key="label.expense.duedate"/>
						</logic:equal>
						<logic:equal name="costEditForm" property="usedByExpenseForm" value="off">
							<bean:message key="label.costedit.duedate"/>
						</logic:equal>
					</center></b></td>
					<td class="formTitle"><b><center><bean:message key="label.costedit.status"/></center></b></td>
					<td class="formTitle"><b><center><bean:message key="label.costedit.value"/></center></b></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>			
				</table>
				
				<div id="cost_installment_body" style="width:430px; height:65px; overflow-y: auto; overflow-x: hidden;">
					<bean:write name="costEditForm" property="installmentHtmlBody" filter="false" />
				</div>      

				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td width="10">&nbsp;</td>
						<td>&nbsp;</td>
						<td width="10">&nbsp;</td>
					</tr>			
				</table>
				
			</div>				

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
		      		<html:button property="save" styleClass="button" onclick="javascript:saveCost();">
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
		
		</td><td width="20">&nbsp;</td>
		</tr>
		<tr><td colspan="3" height="50%">&nbsp;</td></tr>
		</table>

	</html:form>
</html>