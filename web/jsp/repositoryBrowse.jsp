<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>	
	<body>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="10">&nbsp;</td><td>
			
			</br>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formLabel">
			  <td width="370">&nbsp;	
					<bean:message key="title.formRepository"/>
			  </td>
			  <td>&nbsp;</td>
			</tr>
			</table>

			<table width="100%" border="0" height="300" cellspacing="0" cellpadding="0">
				<tr class="gapFormBody">
				  <td>&nbsp;</td>
				</tr>	    		
				<tr class="formBody">
					<td>
						<div id="BROWSE_REPOSISTORY_BODY" style="width:570px; height:300px; overflow: scroll;">
							<plandora-html:ptable width="100%" name="repositoryFileList" ajax="false" frm="repositoryViewerForm" pagesize="0">
								<plandora-html:pcolumn width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />								
								<plandora-html:pcolumn width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntityCheckBoxDecorator" />
								<plandora-html:pcolumn property="name" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="browse" />					  
								<plandora-html:pcolumn width="100" property="author" align="center" title="label.formRepository.author"/>					  
								<plandora-html:pcolumn width="80" property="fileSize" title="label.formRepository.size" />			  
								<plandora-html:pcolumn width="50" property="revision" title="label.formRepository.revision" />
								<plandora-html:pcolumn width="130" align="center" property="creationDate" title="label.formRepository.date" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
							</plandora-html:ptable>		
						</div>					
					</td>
				</tr>
				<tr class="gapFormBody">
				  <td>&nbsp;</td>
				</tr>	    		
			</table>
		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formLabel">
			  <td width="20">&nbsp;</td>
			  <td>
					<center>
						<html:button property="close" styleClass="button" onclick="closeMessage();goToForwardPage();">
							<bean:message key="button.close"/>
						</html:button>												
					</center>
			  </td>
			  <td width="20">&nbsp;</td>	      
			</tr>
			</table>
			
		</td><td width="10">&nbsp;</td>
		</tr></table>
		
	</body>
</html>