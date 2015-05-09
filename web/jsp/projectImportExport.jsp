<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>


<jsp:include page="header.jsp" />

<script language="JavaScript">
    function selectOption() {
	  	buttonClick("projectImportExportForm", "select");    	    
    }
</script>

<html:form  action="projectImportExport" enctype="multipart/form-data">
<html:hidden name="projectImportExportForm" property="operation"/>

<br>

<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td valign="top">

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.importExport"/> - <bean:write name="projectImportExportForm" property="projectName" />
	</display:headerfootergrid>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="10">&nbsp;</td>
		<td width="150">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
	<tr class="formBody">
		<td>&nbsp;</td>	
        <td class="formTitle"><bean:message key="label.importExport.option"/>:&nbsp;</td>
		<td>
			<html:select name="projectImportExportForm" property="importExportOption" styleClass="textBox" onkeypress="javascript:selectOption();" onchange="javascript:selectOption();">
				<html:options collection="optionList" property="id" labelProperty="genericTag"/>
			</html:select>		
		</td>
		<td>&nbsp;</td>			
	</tr>
	<tr class="gapFormBody">
		<td colspan="4">&nbsp;</td>
	</tr>	
	</table>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">   
		<bean:write name="projectImportExportForm" property="fieldsHtml" filter="false"/>
	</table>      	

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="10">&nbsp;</td>
		<td width="150">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
	</table>      	

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('projectImportExportForm', 'generate');">
				<bean:message key="button.importExport.generate"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('projectImportExportForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
		</tr></table>  	
	</display:headerfootergrid> 	
			
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<script> 
   	with(document.forms["projectImportExportForm"]){	
	}
</script>