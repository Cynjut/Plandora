<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<html>
	<title><bean:message key="title.riskHistory"/></title>
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
	
		function viewContent(index){
			with(document.forms["histRiskForm"]){
		    	selectedIndex.value = index;
	    		operation.value = "viewContent";
    			submit();
			 }         
	    }
	    
	</script>

	<html:form  action="manageHistRisk">
		<html:hidden name="histRiskForm" property="operation"/>
		<html:hidden name="histRiskForm" property="selectedIndex"/>		
	
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		   <td width="10">&nbsp;</td>
		   <td width="350"><bean:message key="title.riskHistory"/></td>
		   <td>&nbsp;</td>
		   <td width="10">&nbsp;</td>
		</tr>
		</table>			
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="riskHistoryList" scope="session" pagesize="5" completeempty="true">
					  <display:column width="15%" property="user.name" title="label.riskHistoryUser" />
					  <display:column align="center" property="riskStatusLabel" title="label.riskHistoryStatus" />
					  <display:column width="5%" align="center" property="probability" title="label.formRisk.probability" tag="PROB" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="impact" title="label.formRisk.impact" tag="IMPA" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="tendency" title="label.formRisk.tendency" tag="TEND" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="costImpact" title="label.formRisk.impact.cost" tag="I_COST" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="qualityImpact" title="label.formRisk.impact.qual" tag="I_QUAL" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="scopeImpact" title="label.formRisk.impact.scop" tag="I_SCOP" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="5%" align="center" property="timeImpact" title="label.formRisk.impact.time" tag="I_TIME" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="10%" align="center" property="riskType" title="label.formRisk.type" tag="TYPE" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
					  <display:column width="35%" property="creationDate" align="center" title="label.riskHistoryDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
					  <display:column width="3%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridContentDecorator" />
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