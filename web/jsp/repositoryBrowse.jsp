<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

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
							<display:table border="1" width="100%" name="repositoryFileList" scope="session" pagesize="0">
								<display:column width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator" />								
								<display:column width="10" property="isDirectory" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RepositoryEntityCheckBoxDecorator" />
								<display:column property="name" align="left" title="label.formRepository.name" decorator="com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator" tag="browse" />					  
								<display:column width="100" property="author" align="center" title="label.formRepository.author"/>					  
								<display:column width="80" property="fileSize" title="label.formRepository.size" />			  
								<display:column width="50" property="revision" title="label.formRepository.revision" />
								<display:column width="130" align="center" property="creationDate" title="label.formRepository.date" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
							</display:table>		
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