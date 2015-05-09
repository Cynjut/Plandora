<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>
<jsp:include page="header.jsp" />

<script language="javascript">
	function showLockInfo(name, dt, comment){
		var content = name + "<br>" + dt + "<br>" + comment;
		document.getElementById("floatPanelContent").innerHTML = content;		
	}
	
	function changeGrant(idx){
		with(document.forms["repositoryViewerForm"]){
   			operation.value = "grant";
   			id.value = idx;
   		}
		ajaxProcess(document.forms["repositoryViewerForm"], callBackGrantClick, idx);		
	}
	
	function callBackGrantClick(idx) {  
	    if(isAjax()){  
	       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
			var content = objRequest.responseText;
			if (content != '') {
				var i = content.indexOf("|");
				var newImg = "../images/" + content.substr(0,i);
				var newLabel = content.substr(i+1);			
			   	(document.getElementById("GRANT_"+idx)).src = newImg;  
				(document.getElementById("GRANT_"+idx)).setAttribute("title", newLabel);
				(document.getElementById("GRANT_"+idx)).setAttribute("alt", newLabel);		   	
			}
	    }  
	}
	
	function showOnlineViewer(projectId, outFormat, newpath){
	    var pathWindow ="../do/repositoryFileViewer?operation=prepareForm&projectId=" + projectId + "&outputType=" + outFormat + "&newpath=" + newpath;
		window.open(pathWindow, 'fileViewer', 'width=600, height=400, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	}
	
	function goToForwardPage(){
		buttonClick('repositoryViewerForm', 'refresh');
	}

	function newArtifact(){
    	 with(document.forms["repositoryViewerForm"]){
    	 	displayMessage("../do/showNewArtifact?operation=prepareForm&projectId=" + projectId.value + "&id=", 600, 400);
		 }         
	}

	function openUploadFilePopup(){
    	 with(document.forms["repositoryViewerForm"]){
    	 	displayMessage("../do/repositoryUpload?operation=prepareForm&folderCreation=off&projectId=" + projectId.value + "&path=" + path.value, 530, 200);
		 }         
	}

	function openNewFolderPopup(){
    	 with(document.forms["repositoryViewerForm"]){
    	 	displayMessage("../do/repositoryUpload?operation=prepareForm&folderCreation=on&projectId=" + projectId.value + "&path=" + path.value, 530, 200);
		 }         
	}
	
	
</script>

<html:form action="showRepositoryViewer" enctype="multipart/form-data">
	<html:hidden name="repositoryViewerForm" property="operation"/>
	<html:hidden name="repositoryViewerForm" property="id"/>
	<html:hidden name="repositoryViewerForm" property="emulateCustomerViewer"/>
	<html:hidden name="repositoryViewerForm" property="showUploadButton"/>
	<html:hidden name="repositoryViewerForm" property="projectId"/>
	<html:hidden name="repositoryViewerForm" property="genericTag"/>
	<html:hidden name="repositoryViewerForm" property="path"/>

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.formRepository"/>
		</display:headerfootergrid>
	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		    <tr class="gapFormBody">
		      <td>&nbsp;</td>
		    </tr>	    		
			<tr class="formBody">
				<td>
					<logic:equal name="repositoryViewerForm" property="emulateCustomerViewer" value="off">
						<display:table border="1" width="100%" name="repositoryFileList" scope="session" pagesize="0" requestURI="../do/showRepositoryViewer?operation=refresh">
						  <display:column width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />
						  <display:column property="name" sort="true" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" />					  
						  <display:column width="100" sort="true" property="author" align="center" title="label.formRepository.author" description="label.formRepository.author.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_AUTHOR%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="repositoryViewerForm" />					  
						  <display:column property="name" sort="true" align="left" title="label.formRepository.fullPath" description="label.formRepository.fullPath.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_FULLPATH%>" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="navigate"/>
						  <display:column width="10" property="id" align="center" title="grid.title.empty" description="label.formRepository.locked.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_LOCK%>" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryLockDecorator" />
						  <display:column width="80" sort="true" property="fileSize" title="label.formRepository.size" description="label.formRepository.size.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_SIZE%>" />				  
						  <display:column width="50" sort="true" property="revision" title="label.formRepository.revision" description="label.formRepository.revision.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_REVISION%>" />
						  <display:column width="130" align="center" property="creationDate" title="label.formRepository.date" description="label.formRepository.date.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_DATE%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />					  
						  <display:column width="20" align="center" property="id" title="grid.title.empty" description="label.formRepository.log.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_LOG%>" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryLogDecorator" />
						  <display:column width="20" align="center" property="id" title="grid.title.empty" description="label.formRepository.perm.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_GRANT%>" decorator="com.pandora.gui.taglib.decorator.RepositoryPermissionDecorator" />
						  <display:column width="20" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ArtifactEditDecorator" />
						  <display:column width="20" property="id" align="center" title="grid.title.empty" description="label.formRepository.viewer.desc" decorator="com.pandora.gui.taglib.decorator.RepositoryOnlineViewerDecorator" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_VIEWER%>" />
						</display:table>
					</logic:equal>

					<logic:equal name="repositoryViewerForm" property="emulateCustomerViewer" value="on">
						<display:table border="1" width="100%" name="custRepositFileList" scope="session" pagesize="0" requestURI="../do/showRepositoryViewer?operation=refresh">
						  <display:column width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />
						  <display:column property="name" sort="true" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" />
						  <display:column width="100" sort="true" property="author" align="center" title="label.formRepository.author" description="label.formRepository.author.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_AUTHOR%>" decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="repositoryViewerCustomerForm" />					  
						  <display:column property="name" sort="true" align="left" title="label.formRepository.fullPath" description="label.formRepository.fullPath.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_FULLPATH%>" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="navigate"/>
						  <display:column width="80" sort="true" property="fileSize" title="label.formRepository.size" description="label.formRepository.size.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_SIZE%>" />
						  <display:column width="50" sort="true" property="revision" title="label.formRepository.revision" description="label.formRepository.revision.desc" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_REVISION%>" />
						  <display:column width="10" property="id" align="center" title="grid.title.empty" description="label.formRepository.viewer.desc" decorator="com.pandora.gui.taglib.decorator.RepositoryOnlineViewerDecorator" visibleProperty="<%=PreferenceTO.REPOSITORY_GRID_VIEWER%>" />					  
						</display:table>
					</logic:equal>		
				</td>
			</tr>
			<tr class="gapFormBody">
		      <td>&nbsp;</td>
		    </tr>	    		
		</table>

		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="formBody">
	      <td align="right">
				<logic:equal name="repositoryViewerForm" property="emulateCustomerViewer" value="on">
					<a href="../do/showRepositoryViewer?operation=toggleCustomerViewer">
						<bean:message key="title.formRepository.resourceview"/>
					</a>
				</logic:equal>
				<logic:equal name="repositoryViewerForm" property="emulateCustomerViewer" value="off">
					<a href="../do/showRepositoryViewer?operation=toggleCustomerViewer">
						<bean:message key="title.formRepository.customerview"/>
					</a>
				</logic:equal>	      
	      </td>
	    </tr>
	  	</table>
	
		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="artifact" styleClass="button" onclick="javascript:newArtifact();">
					<bean:message key="label.artifactTag.new"/>
				  </html:button>    
			  </td>
			  <logic:equal name="repositoryViewerForm" property="showUploadButton" value="on">
				  <td width="120">
					  <html:button property="upload" styleClass="button" onclick="javascript:openUploadFilePopup();">
							<bean:message key="label.formRepository.uploadbutton"/>
					  </html:button>    
				  </td>
				  <td>
					  <html:button property="newFolder" styleClass="button" onclick="javascript:openNewFolderPopup();">
							<bean:message key="label.formRepository.createFolder"/>
					  </html:button>    
				  </td>				  
			  </logic:equal>			      
			  <logic:equal name="repositoryViewerForm" property="showUploadButton" value="off">
			  <td>&nbsp;</td>
			  </logic:equal>			      
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('repositoryViewerForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			  <td width="20">&nbsp;</td>	      
			</tr></table>
		</display:headerfootergrid> 

		  
  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<jsp:include page="footer.jsp" />