<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>
<jsp:include page="header.jsp" />

<script language="javascript">

	function showOnlineViewer(projectId, outFormat, newpath){
	    var pathWindow ="../do/repositoryFileViewer?operation=prepareForm&projectId=" + projectId + "&outputType=" + outFormat + "&newpath=" + newpath;
		window.open(pathWindow, 'fileViewer', 'width=600, height=400, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	}
	
</script>

<html:form action="showRepositoryViewerCustomer">
	<html:hidden name="repositoryViewerCustomerForm" property="operation"/>
	<html:hidden name="repositoryViewerCustomerForm" property="id"/>

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.formCustRepository"/>
		</display:headerfootergrid>
		
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="gapFormBody">
		      <td width="10">&nbsp;</td>
		      <td width="80">&nbsp;</td>
		      <td width="200">&nbsp;</td>
		      <td>&nbsp;</td>
		    </tr>	    	
			<tr class="formBody">
				<td valign="top">&nbsp;</td>	
				<td class="formTitle"><bean:message key="label.viewReport.project"/>:&nbsp;</td>		
				<td>
					<html:select name="repositoryViewerCustomerForm" property="projectId" styleClass="textBox">
						 <html:options collection="projectList" property="id" labelProperty="name"/>
					</html:select>		
				</td>		
				<td>&nbsp;</td>			
			</tr> 	
			<tr class="gapFormBody">
		      <td colspan="4">&nbsp;</td>
		    </tr>	    		
		</table>

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120"> 
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('repositoryViewerCustomerForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>	      
			  <td>&nbsp;</td>
			</tr></table>
		</display:headerfootergrid> 
	
		<div>&nbsp;</div>
		
		<display:headerfootergrid width="100%" type="HEADER">
			&nbsp;
		</display:headerfootergrid>

		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="gapFormBody">
		      <td>&nbsp;</td>
		    </tr>	    	
			<tr class="formBody">
				<td colspan="4">
					<display:table border="1" width="100%" name="custRepositFileList" scope="session" pagesize="0" requestURI="../do/showRepositoryViewerCustomer?operation=navigate">
					  <display:column width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />
					  <display:column property="name" sort="true" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" />
					  <display:column width="100" sort="true" property="author" align="center" title="label.formRepository.author" description="label.formRepository.author.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_AUTHOR%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="repositoryViewerCustomerForm" />					  
					  <display:column property="name" sort="true" align="left" title="label.formRepository.fullPath" description="label.formRepository.fullPath.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_FULLPATH%>" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="navigate"/>
					  <display:column width="80" sort="true" property="fileSize" title="label.formRepository.size" description="label.formRepository.size.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_SIZE%>" />
					  <display:column width="50" sort="true" property="revision" title="label.formRepository.revision" description="label.formRepository.revision.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_REVISION%>" />
					  <display:column width="10" property="id" align="center" title="grid.title.empty" description="label.formRepository.viewer.desc" decorator="com.pandora.gui.taglib.decorator.RepositoryOnlineViewerDecorator" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_VIEWER%>" />					  
				</display:table>		
				</td>
			</tr>
		    <tr class="gapFormBody">
		      <td>&nbsp;</td>
		    </tr>	    	
		</table>

		<display:headerfootergrid type="FOOTER">			
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120"> 
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('repositoryViewerCustomerForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>	      
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('repositoryViewerCustomerForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>      
			</tr></table>
		</display:headerfootergrid> 		
	
  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<jsp:include page="footer.jsp" />    		