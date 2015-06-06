<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<title>
   		<bean:message key="title.formSurvey.questionList.edit"/>
	</title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
	        	
	<script language="JavaScript">
		function saveQuestion(){
	    	with(document.forms["surveyQuestionForm"]){
	    		if (name.value != "") {
		        	operation.value = "saveQuestion";
		        	closeMessage();
		        	submit();			
	         	} else {
	         		alert("<bean:message key="message.formSurvey.mandatoryField"/>");
	         	}
	        }
		}
	</script>

	<html:form  action="/showSurveyQuestion">
		<html:hidden name="surveyQuestionForm" property="surveyId"/>
		<html:hidden name="surveyQuestionForm" property="operation"/>
		<html:hidden name="surveyQuestionForm" property="editQuestionId"/>
			
		<br>
		
		<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp</td><td>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="formLabel">
		      <td width="10">&nbsp;</td>
		      <td width="350">
			      <bean:message key="title.formSurvey.questionList.edit"/>		  
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
		       	<td width="120" class="formTitle"><bean:message key="label.formSurvey.question.type"/>:&nbsp;</td>    
			    <td class="formBody">
			    	<logic:equal name="surveyQuestionForm" property="editQuestionId" value="">
					  	<html:select name="surveyQuestionForm" property="type" styleClass="textBox">
					   		<html:options collection="surveyQuestionTypes" property="id" labelProperty="genericTag" filter="false"/>
						</html:select>
			    	</logic:equal>
			    	<logic:notEqual name="surveyQuestionForm" property="editQuestionId" value="">
					  	<html:select name="surveyQuestionForm" property="type" styleClass="textBox" disabled="true">
					   		<html:options collection="surveyQuestionTypes" property="id" labelProperty="genericTag" filter="false"/>
						</html:select>
			    	</logic:notEqual>
		   	    </td>
	    	    <td width="10">&nbsp;</td>
		    </tr>
		    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.formSurvey.question.content"/>:&nbsp;</td>
		      <td><html:text name="surveyQuestionForm" property="content" styleClass="textBox" size="50" maxlength="1024"/></td>
		      <td>&nbsp;</td>
		    </tr>    		    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.formSurvey.question.subtitle"/>:&nbsp;</td>
		      <td><html:text name="surveyQuestionForm" property="subtitle" styleClass="textBox" size="30" maxlength="50"/></td>
		      <td>&nbsp;</td>
		    </tr>    
		    <tr class="pagingFormBody">
		      <td class="formTitle"><bean:message key="label.formSurvey.question.domain"/>:&nbsp;</td>
					<logic:equal name="surveyQuestionForm" property="editDomain" value="on">
					      <td><html:text name="surveyQuestionForm" property="domain" styleClass="textBox" size="50" maxlength="1024"/></td>				
					</logic:equal>
					<logic:notEqual name="surveyQuestionForm" property="editDomain" value="on">
					      <td><html:text name="surveyQuestionForm" property="domain" styleClass="textBoxDisabled" disabled="true"/></td>				
					</logic:notEqual>
		      <td>&nbsp;</td>
		    </tr> 
		    </table>

			<table width="100%" border="0" cellspacing="0" cellpadding="0">   	    
    			<tr class="formNotes">
    				<td width="120" class="formTitle">&nbsp;</td>
      				<td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="label.formSurvey.question.domainnote"/></div></td>
      				<td width="10">&nbsp;</td>
    			</tr> 	    
  			</table>
		    
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="pagingFormBody">
		      <td width="120" class="formTitle"><bean:message key="label.formSurvey.question.position"/>:&nbsp;</td>
		      <td>     
			  		<html:select name="surveyQuestionForm" property="position" styleClass="textBox">
						<html:options collection="positionList" property="id" labelProperty="genericTag" filter="false"/>
					</html:select>		      		      	
		      </td>
		      <td width="200" class="formTitle"><bean:message key="label.formSurvey.mandatory"/>:&nbsp;</td>
		      <td>     
			  		<html:select name="surveyQuestionForm" property="mandatory" styleClass="textBox">
						<html:options collection="mandatoryList" property="id" labelProperty="genericTag" filter="false"/>
					</html:select>		      		      	
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
		      		<html:button property="save" styleClass="button" onclick="javascript:saveQuestion();">
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