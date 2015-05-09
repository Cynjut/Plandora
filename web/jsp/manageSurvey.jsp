<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveSurvey"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation);
         }
     }

     function saveWithConfirmation(){
         if ( confirm("<bean:message key="message.confirmSaveSurvey"/>")) {
	         buttonClick('surveyForm', 'saveSurvey');
         }
     }
     
     function removeQuestion(qid) {
    	with(document.forms["surveyForm"]){
			removedQuestionId.value = qid;
			callAction(id.value, 'surveyForm', 'removeQuestion');
		}     
     }
    
	function editQuestion(){
		buttonClick('surveyForm', 'showEditQuestion');
	}


	function changeQuestionId(qid){
    	with(document.forms["surveyForm"]){
			editQuestionId.value = qid;
			callAction(id.value, 'surveyForm', 'showEditQuestion');
		}     
	}

    
	function showReport(sid){
    	with(document.forms["surveyForm"]){
			id.value = sid;
			callAction(id.value, 'surveyForm', 'showReport');
		}     
	}

	function showReplicationPopup(){
		displayMessage("../do/manageSurvey?operation=showReplicationPopup", 450, 130);
	}

	function replicate(surveyId){
		with(document.forms["surveyForm"]){
			window.location="../do/manageSurvey?operation=replicate&id=" + surveyId;
		}
	}
    
</script>


<html:form action="manageSurvey">
	<html:hidden name="surveyForm" property="operation"/>
	<html:hidden name="surveyForm" property="id"/>	
	<html:hidden name="surveyForm" property="projectId"/>
	<html:hidden name="surveyForm" property="removedQuestionId"/>
	<html:hidden name="surveyForm" property="showSaveConfirmation"/>
	<html:hidden name="surveyForm" property="showEditQuestion"/>
	<html:hidden name="surveyForm" property="editQuestionId"/>
		
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formSurvey"/>
	</display:headerfootergrid>
	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formSurvey.name"/>:&nbsp;</td>
      <td width="460" class="formBody">
        <html:text name="surveyForm" property="name" styleClass="textBox" size="50" maxlength="50"/>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formSurvey.desc"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="surveyForm" property="description" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">&nbsp;</td>
      <td class="formBody">      
	   		<html:checkbox property="isAnonymous" name="surveyForm" >
	   			<bean:message key="label.formSurvey.anonymous"/>
	   		</html:checkbox>
      </td>
      <td>&nbsp;</td>
    </tr>

	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formSurvey.publishing"/>:&nbsp;</td>
      <td class="formBody">
		  <plandora-html:calendar name="surveyForm" property="publishingDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
    </tr>

	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formSurvey.finaldate"/>:&nbsp;</td>
      <td class="formBody">
		  <plandora-html:calendar name="surveyForm" property="finalDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
	
	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">&nbsp;</td>
      <td class="formBody">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formLabel">
					<td width="10">&nbsp;</td>
					<td><bean:message key="title.formSurvey.questionList"/></td>
					<td width="50">
						<html:button property="addQuestion" styleClass="button" onclick="javascript:editQuestion();">
							<bean:message key="button.addInto"/>
						</html:button>			
					</td>
				</tr>
				<tr class="pagingFormBody">
					<td colspan="3" class="formBody">
						<display:table border="1" width="100%" name="questionList" pagesize="0" scope="session">
							  <display:column align="center" property="content" title="label.formSurvey.question.content" />
							  <display:column width="25%"align="center" property="position" title="label.formSurvey.question.position"/>
  					  		  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.SurveyQuestionEditDecorator" />
					  		  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.SurveyQuestionRemoveDecorator" />							  
						</display:table>
					</td>
				</tr>
				<tr class="gapFormBody">
					<td colspan="3">&nbsp;</td>
				</tr> 				
			</table>				

      </td>
      <td>&nbsp;</td>
    </tr>
		
  	</table>


	<logic:equal name="surveyForm" property="anonymousStatus" value="on">
	    <table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
			<td colspan="4">&nbsp;</td>
		</tr>
	    <tr class="pagingFormBody">
	      <td width="20">&nbsp;</td>
	      <td width="300" class="formTitle"><bean:message key="label.formSurvey.anonymousUrl"/></td>
	      <td>&nbsp;</td>
	      <td width="20">&nbsp;</td>
	    </tr>
	    <tr class="tableRowOdd">
	      <td>&nbsp;</td>
	      <td class="code" colspan="2">
				<bean:write name="surveyForm" property="anonymousURI" />
	      </td>
	      <td>&nbsp;</td>
	    </tr>
		<tr class="pagingFormBody">
			<td colspan="4">&nbsp;</td>
		</tr>
		</table>	    
	</logic:equal>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			<logic:equal name="surveyForm" property="showSaveConfirmation" value="on">
				<html:button property="save" styleClass="button" onclick="javascript:saveWithConfirmation();">
					<bean:write name="surveyForm" property="saveLabel" />
				</html:button>    		
			</logic:equal>
			<logic:equal name="surveyForm" property="showSaveConfirmation" value="off">
				<html:button property="save" styleClass="button" onclick="javascript:buttonClick('surveyForm', 'saveSurvey');">
					<bean:write name="surveyForm" property="saveLabel" />
				</html:button>    		
			</logic:equal>      
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('surveyForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="150">
			  <html:button property="replicate" styleClass="button" onclick="javascript:showReplicationPopup();">
				<bean:message key="label.formSurvey.replicate"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('surveyForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
   	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.surveyList"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="surveyList" scope="session" pagesize="6">
					  <display:column width="2%" align="center" property="id" title="grid.title.empty" />
					  <display:column property="name" title="label.formSurvey.name" />
					  <display:column width="15%" align="center" property="publishingDate" title="label.formSurvey.publishing" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="15%" align="center" property="finalDate" title="label.formSurvey.finaldate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.SurveyAnswerViewerDecorator" />					  
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'surveyForm', 'editSurvey'" />
					  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'surveyForm', 'removeSurvey'" />
				</display:table>		
			</td>
		</tr> 
	</table>
			
	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('surveyForm', 'refresh');">
			<bean:message key="button.refresh"/>
		  </html:button>    
		</td>
		<td>&nbsp;</td>
		</tr></table>  	
	</display:headerfootergrid>
	
	</td><td width="10">&nbsp;</td>
	</tr></table>
	
</html:form>
	
<jsp:include page="footer.jsp" />
	
<!-- End of source-code -->
<script> 

	<logic:notEqual name="surveyForm" property="reportURL" value="">	
		window.open('<bean:write name="surveyForm" property="reportURL" filter="false"/>', 'SurveyReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
	</logic:notEqual>

	with(document.forms["surveyForm"]){	
		name.focus();
		if (showEditQuestion.value=='on') {
			displayMessage("../do/showSurveyQuestion?operation=showEditPopup&surveyId=" + id.value + "&editQuestionId=" + editQuestionId.value, 450, 220);
		}		 
	}
</script> 