<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.formForum.confirmRemoveDiscussion"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
      
     function changeProject(){
		 buttonClick("viewReportForm", "refreshProject");
     }
          
</script>

<html:form action="manageDiscussion">
	<html:hidden name="discussionForm" property="operation"/>
	<html:hidden name="discussionForm" property="id"/>	
	<html:hidden name="discussionForm" property="projectId"/>	
		
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="label.formForum.title"/>
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
			<td valign="top">&nbsp;</td>	
			<td class="formTitle"><bean:message key="label.formForum.project"/>:&nbsp;</td>		
			<td>
				<html:select name="discussionForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
					 <html:options collection="projectList" property="id" labelProperty="name" filter="false"/>
				</html:select>		
			</td>		
	        <td class="formTitle"><bean:message key="label.formForum.grid.category"/>:&nbsp;</td>
			<td>
		  		<html:select name="discussionForm" property="categoryId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
		             <html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
				</html:select>
			</td>
			<td>&nbsp;</td>			
		</tr> 	
		<tr class="gapFormBody">
			<td colspan="6">&nbsp;</td>
		</tr>
		</table>

		<display:headerfootergrid type="FOOTER">	
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">&nbsp;</td>
			  <td>&nbsp;</td>
				  <td width="120">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('discussionForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
				  </td>
			</tr></table>
		</display:headerfootergrid>
	
		<div>&nbsp;</div>
		
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="label.formForum.topic"/>
		</display:headerfootergrid>
				  		   
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
			<plandora-html:ptable width="100%" name="discussionList" frm="discussionForm" pagesize="6">
				<plandora-html:pcolumn width="2%" property="id" align="center" title="label.formForum.grid.id" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			
				<plandora-html:pcolumn property="name" likeSearching="true" title="label.formForum.grid.discussion.name" />
				<plandora-html:pcolumn width="15%" align="center" property="owner.username" likeSearching="true" title="label.formForum.grid.owner" />
				<plandora-html:pcolumn width="15%" align="center" property="category.name" likeSearching="true" title="label.formForum.grid.category" />
				<plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'discussionForm', 'editDiscussion'" />
				<plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'discussionForm', 'removeDiscussion'" />
			</plandora-html:ptable>		
			</td>
		</tr> 
		</table>

		<display:headerfootergrid type="FOOTER">			
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			<td width="120">
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('discussionForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    		
			</td> 
			<td>&nbsp;</td>
			</tr></table>  	
		</display:headerfootergrid>
		
	</td><td width="10">&nbsp;</td>
	</tr></table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["discussionForm"]){	
		name.focus(); 
	}
</script>    	