<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td>
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.userManagement"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="160">&nbsp; </td>
      <td>&nbsp; </td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><a href="../do/manageUser?operation=prepareUser"><bean:message key="title.user"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><a href="../do/manageArea?operation=screenArea"><bean:message key="title.area"/></a></td>      
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><a href="../do/manageDepartment?operation=screenDepartment"><bean:message key="title.department"/></a></td>      
      <td><bean:message key="title.underConstruction"/></td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
       <td class="formTitle"><a href="../do/manageFunction?operation=screenFunction"><bean:message key="title.function"/></a></td>     
      <td><bean:message key="title.underConstruction"/></td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><a href="../do/manageCompany?operation=prepareForm"><bean:message key="label.userCompany"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageDB?operation=prepareForm">Manage DB</a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
  	</table>

	<display:headerfootergrid type="FOOTER">
		&nbsp;
  	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.projectManagement"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="300">&nbsp; </td>
      <td>&nbsp; </td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageProject?operation=prepareProject">Project</a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageReport?operation=prepareForm&isKpiForm=on"><bean:message key="title.manageReport"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageReport?operation=prepareForm&isKpiForm=off"><bean:message key="title.manageReport.Report"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>        
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageMetaForm?operation=prepareForm"><bean:message key="title.metaForm"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageMetaField?operation=prepareForm"><bean:message key="title.formMetaField"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageNotification?operation=prepareForm"><bean:message key="title.formNotification"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>       
<!--     
    <tr class="pagingFormBody">
      <td>&nbsp;</td>      
      <td class="formTitle"><a href="../do/manageTemplate?operation=prepareForm"><bean:message key="title.manageTemplate"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>        
-->    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">Project Status</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">Request Status</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">Task Status</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><a href="../do/manageCategory?operation=prepareForm"><bean:message key="title.category"/></a></td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
    </tr>    
  	</table>
	
	<display:headerfootergrid type="FOOTER">
		&nbsp;
  	</display:headerfootergrid> 

	<plandora-html:metaform collection="metaFormList" styleTitle="formTitle" styleBody="formBody" />     

	<div>&nbsp;</div>

	<logic:present name="newversion.url" property="genericTag">
		<table border="0" cellspacing="0" cellpadding="0" align="center" class="errorValidationTable">
		<tr> 
			<td class="errorValidation">
				<bean:message key="title.newVersion"/><br/>
				<bean:write name="newversion.url" property="genericTag" filter="false" />
			</td>
		</tr>
		</table>
	</logic:present>
	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

<jsp:include page="footer.jsp" />
