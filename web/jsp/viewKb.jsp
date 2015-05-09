<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="JavaScript">
	function goToPage(newPage) {
    	with(document.forms["kbForm"]){
    		window.location = "../do/viewKb?operation=renderContent&new_page=" + newPage;
    	}
	}

    function showReqHistory(id) {
	    var pathWindow ="../do/manageHistRequest?operation=prepareForm&reqId=" + id;
		window.open(pathWindow, 'reqHist', 'width=540, height=330, location=no, menubar=no, status=yes, toolbar=no, scrollbars=no, resizable=no');
    }
    
    
    function showTaskHistory(id) {
	    var pathWindow ="../do/manageHistTask?operation=prepareForm&taskId=" + id + "&resourceId=";
		window.open(pathWindow, 'tskHist', 'width=740, height=250, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

    
    function showOccHistory(id) {
	    var pathWindow ="../do/manageHistOccurrence?operation=prepareForm&occId=" + id;
		window.open(pathWindow, 'occHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

    
    function showRiskHistory(id) {
	    var pathWindow ="../do/manageHistRisk?operation=prepareForm&riskId=" + id;
		window.open(pathWindow, 'riskHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
    
    
	
</script>

<html:form action="viewKb">
<html:hidden name="kbForm" property="operation"/>
<html:hidden name="kbForm" property="projectId"/>
<html:hidden name="kbForm" property="currentPage"/>

<br>

<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewKb"/> <bean:write name="kbForm" property="projectName" />
	</display:headerfootergrid>
  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="30">&nbsp;</td>
		<td width="50">&nbsp;</td>
		<td width="400">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td class="formTitle"><bean:message key="label.viewKb.subject"/>:&nbsp;</td>
		<td><html:text name="kbForm" property="subject" styleClass="textBox" size="100" maxlength="200"/></td>
        <td>&nbsp;</td>		
		<td>&nbsp;</td>			
	</tr> 
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td class="formTitle"><bean:message key="label.viewKb.type"/>:&nbsp;</td>
		<td>
	  		<html:select name="kbForm" property="type" styleClass="textBox">
	             <html:options collection="typeList" property="id" labelProperty="genericTag"/>
			</html:select>
		</td>
		<td>&nbsp;</td>	
		<td>&nbsp;</td>			
	</tr> 	
	<tr class="gapFormBody">
		<td colspan="5">&nbsp;</td>
	</tr>
	</table>
  	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
				<html:button property="search" styleClass="button" onclick="javascript:buttonClick('kbForm', 'search');">
					<bean:message key="label.viewKb.search"/>
				</html:button>    
		  </td>
		  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('kbForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
		</tr></table>  	
   	</display:headerfootergrid> 

	<logic:notEqual name="kbForm" property="htmlKbGrid" value="">
  	
		<div>&nbsp;</div>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="label.viewKb.content"/>
		</display:headerfootergrid>
	    
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<bean:write name="kbForm" property="htmlKbGrid" filter="false"/>
		</table>
	  	
		<display:headerfootergrid type="FOOTER">			
			&nbsp;
		</display:headerfootergrid>
		
	</logic:notEqual>
		
	<br>
	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<script> 
   	with(document.forms["kbForm"]){	
	   subject.focus()
	}
</script>