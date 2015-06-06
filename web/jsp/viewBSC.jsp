<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="JavaScript">

	function showGoals(){
		var prjId;
		with(document.forms["viewBSCForm"]){
			prjId = projectId.value
		}
		window.location = "../do/viewBSCPanel?operation=prepareForm&projectId=" + prjId;	
	}
</script>

<html:form  action="viewBSC">
<html:hidden name="viewBSCForm" property="operation"/>
<html:hidden name="viewBSCForm" property="projectId"/>

<plandora-html:shortcut name="viewBSCForm" property="goToBscForm" fieldList="projectId"/>

<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSC"/> <bean:write name="viewBSCForm" property="projectName" filter="false"/>
	</display:headerfootergrid>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="50">&nbsp;</td>
		<td width="100">&nbsp;</td>
		<td width="100">&nbsp;</td>
		<td width="100">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td class="formTitle"><bean:message key="label.viewBSC.initialDate"/>:&nbsp;</td>
		<td><plandora-html:calendar name="viewBSCForm" property="initialDate" styleClass="textBoxDisabled" /></td>
        <td class="formTitle"><bean:message key="label.viewBSC.finalDate"/>:&nbsp;</td>		
		<td><plandora-html:calendar name="viewBSCForm" property="finalDate" styleClass="textBoxDisabled" /></td>
		<td>&nbsp;</td>			
	</tr> 
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td class="formTitle"><bean:message key="label.viewBSC.category"/>:&nbsp;</td>
		<td>
	  		<html:select name="viewBSCForm" property="categoryId" styleClass="textBox">
	             <html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>		
		</td>
		<td>&nbsp;</td>	
		<td>&nbsp;</td>	
		<td>&nbsp;</td>			
	</tr> 	
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td colspan=4 class="formNotes"><bean:message key="label.message.kpi"/></td>
		<td>&nbsp;</td>			
	</tr> 	
	<tr class="gapFormBody">
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
	
	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('viewBSCForm', 'refresh');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="100">
				  <html:button property="showValues" styleClass="button" onclick="javascript:showGoals();">
					<bean:message key="label.viewBSCPanel.indicgoals"/>
				  </html:button>    
		  </td>            
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('viewBSCForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>  	
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>

	<!-- FINANCIAL DATA -->	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSC.title1"/>
	</display:headerfootergrid>
	    
	<table width="100%" border="1" cellspacing="0" cellpadding="0">
		<bean:write name="viewBSCForm" property="finantialTable" filter="false" />	
	</table>

	<display:headerfootergrid type="FOOTER">	
      &nbsp;
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	

	<!-- CUSTOMER DATA -->
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSC.title2"/>
	</display:headerfootergrid>
	    
	<table width="100%" border="1" cellspacing="0" cellpadding="0">
		<bean:write name="viewBSCForm" property="customerTable" filter="false"  />
	</table>
	
	<display:headerfootergrid type="FOOTER">	
      &nbsp;
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>

	
	<!-- PROCESS DATA -->
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSC.title3"/>
	</display:headerfootergrid>
	    
	<table width="100%" border="1" cellspacing="0" cellpadding="0">
		<bean:write name="viewBSCForm" property="processTable" filter="false"  />
	</table>
	
	<display:headerfootergrid type="FOOTER">	
      &nbsp;
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>


	<!-- LEARNING DATA -->
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSC.title4"/>
	</display:headerfootergrid>
	    
	<table width="100%" border="1" cellspacing="0" cellpadding="0">
		<bean:write name="viewBSCForm" property="learningTable" filter="false" />	
	</table>
	
	<display:headerfootergrid type="FOOTER">	
      &nbsp;
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<script> 
   	with(document.forms["viewBSCForm"]){	
	}
</script>  	