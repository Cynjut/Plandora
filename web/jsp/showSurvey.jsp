<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">
	function showSurvey(){
    	with(document.forms["showSurveyForm"]){
    		window.location = "../do/manageSurvey?operation=prepareForm&projectId" + projectId.value;
    	}
	}
	
	function saveSurvey(){
    	with(document.forms["showSurveyForm"]){
    		if (allowAnonymous.value == "true") {
				if (confirm("<bean:message key="message.formSurvey.confirmSave"/>")) {
		        	javascript:buttonClick('showSurveyForm', 'answer');
	        	}    	    		
    		} else {
    			javascript:buttonClick('showSurveyForm', 'answer');
    		}
		}
	}
	
</script>

<html:form  action="showSurvey">
	<html:hidden name="showSurveyForm" property="id"/>
	<html:hidden name="showSurveyForm" property="operation"/>
	<html:hidden name="showSurveyForm" property="anonymous"/>
	<html:hidden name="showSurveyForm" property="allowAnonymous"/>
	<html:hidden name="showSurveyForm" property="key"/>
	<html:hidden name="showSurveyForm" property="show"/>
	<html:hidden name="showSurveyForm" property="projectId"/>
				
	<br>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	  	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:write name="showSurveyForm" property="surveyTitle" /> &nbsp;
	</display:headerfootergrid>
		
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="3">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td class="formBody"><bean:write name="showSurveyForm" property="surveyDescription" /></td>
      <td width="10">&nbsp;</td>
    </tr>
	</table>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="3">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td class="formBody">

			<bean:write name="showSurveyForm" property="questionsBody" filter="false" />
	
      </td>
      <td>&nbsp;</td>
    </tr>

	<logic:equal name="showSurveyForm" property="showMandatoryNote" value="true">
	    <tr class="gapFormBody">
	      <td colspan="3">&nbsp;</td>
	    </tr>
	    <tr class="formNotes">
	      <td width="10">&nbsp;</td>    
	      <td colspan="2">&nbsp;&nbsp;(*)&nbsp;<bean:message key="label.formSurvey.obs"/></td>
	    </tr> 	        
	</logic:equal>
    
    <tr class="gapFormBody">
      <td colspan="3">&nbsp;</td>
    </tr>
	</table>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			<logic:equal name="showSurveyForm" property="anonymous" value="false">
				<html:button property="refresh" styleClass="button" onclick="javascript:saveSurvey();">
					<bean:message key="button.update"/>
				</html:button>    
			</logic:equal>
			<logic:equal name="showSurveyForm" property="anonymous" value="true">
				<html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('showSurveyForm', 'anonyanswer');">
					<bean:message key="button.update"/>
				</html:button>    
			</logic:equal>  		
		  </td>
		  <td width="30">&nbsp;</td>      
		  <td>&nbsp;</td>
		  <td width="50" align="right">
				<logic:present name="CURRENT_USER_SESSION" property="name">
					<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('showSurveyForm', 'backward');">
						<bean:message key="button.backward"/>
					</html:button>  			
				</logic:present>
		  </td>      
		</tr></table>  
	</display:headerfootergrid> 
		
	</td><td width="20">&nbsp;</td>
	</tr>
	<tr><td colspan="3" height="50%">&nbsp;</td></tr>
	</table>

</html:form>

<jsp:include page="footer.jsp" />