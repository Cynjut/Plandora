<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>


<html:form action="showBrowseArtifact">
	<html:hidden name="browseArtifactForm" property="operation"/>
	<html:hidden name="browseArtifactForm" property="showUserPwd"/>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
		
		</br>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="370">&nbsp;
			<bean:message key="title.formRepository.folder"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>
		</table>

		<table width="100%" border="0" height="230" cellspacing="0" cellpadding="0">
			
			<tr class="gapFormBody">
			  <td colspan="5">&nbsp;</td>
			</tr>	    		
			<tr class="formBody">
				<td class="formTitle" width="80"><bean:message key="label.formRepository.type"/>:&nbsp;</td>
				<td colspan="3" class="formBody" width="330">
			  		<html:select name="browseArtifactForm" property="artifactSaveType" styleClass="textBox">
			             <html:options collection="artifactExportList" property="id" labelProperty="genericTag" filter="false"/>
					</html:select>
				</td>
				<td class="formTitle">&nbsp;</td>
			</tr>			
			<tr class="formBody">
				<td class="formTitle"><bean:message key="label.formRepository.log"/>:&nbsp;</td>
				<td colspan="3" class="formBody">
					<html:textarea name="browseArtifactForm" property="artifactSaveLog" styleClass="textBox" cols="60" rows="3" />
				</td>
				<td class="formTitle">&nbsp;</td>
			</tr>
						
			<logic:equal name="browseArtifactForm" property="showUserPwd" value="on">
			<tr class="formBody">
				<td class="formTitle" width="80"><bean:message key="label.formRepository.user"/>:&nbsp;</td>
				<td class="formBody" width="80">
					<html:text name="browseArtifactForm" property="artifactSaveUser" styleClass="textBox" size="20" maxlength="30"/>
				</td>
				<td class="formTitle" width="70"><bean:message key="label.formRepository.pwd"/>:&nbsp;</td>
				<td class="formBody" width="180">
					<html:password name="browseArtifactForm" property="artifactSavePwd" maxlength="30" size="20" styleClass="textBox"/>
				</td>				
				<td class="formTitle">&nbsp;</td>
			</tr>			
			</logic:equal>
			
			<tr class="gapFormBody">
			  <td colspan="5">&nbsp;</td>
			</tr>
			<tr class="formBody">
				<td colspan="5">
					<logic:equal name="browseArtifactForm" property="showUserPwd" value="on">
						<div id="BROWSE_REPOSISTORY_BODY" style="width:440px; height:150px; overflow: scroll;">					
					</logic:equal>
					<logic:equal name="browseArtifactForm" property="showUserPwd" value="off">
						<div id="BROWSE_REPOSISTORY_BODY" style="width:440px; height:170px; overflow: scroll;">					
					</logic:equal>	
						<plandora-html:ptable width="100%" name="artifactsFolderList" ajax="false" frm="browseArtifactForm" pagesize="0">
							<plandora-html:pcolumn width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />
							<plandora-html:pcolumn width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntityRadioBoxDecorator" />
							<plandora-html:pcolumn property="name" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="browse" />					  
							<plandora-html:pcolumn width="50" property="revision" title="label.formRepository.revision" />
							<plandora-html:pcolumn width="130" align="center" property="creationDate" title="label.formRepository.date" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
						</plandora-html:ptable>							
					</div>					
				</td>
			</tr>
			<tr class="gapFormBody">
				<td colspan="5">&nbsp;</td>
			</tr>	    		
		</table>
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formLabel">
		  <td width="20">&nbsp;</td>
		  <td>
				<center>
					<html:button property="close" styleClass="button" onclick="javascript:buttonClick('browseArtifactForm', 'saveArtifact');">
							<bean:message key="button.ok"/>
					</html:button>												
					<html:button property="cancel" styleClass="button" onclick="closeMessage();">
						<bean:message key="button.cancel"/>
					</html:button>											
				</center>
		  </td>
		  <td width="20">&nbsp;</td>	      
		</tr>
		</table>
		
	</td><td width="10">&nbsp;</td>
	</tr></table>
</html:form>	