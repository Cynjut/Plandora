<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveArea"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation),
	         alert(argId + " " + argForm + " " + argOperation);
         }
     }
</script>

<html:form  action="manageArea">
	<html:hidden name="areaForm" property="operation"/>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.area"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="pagingFormBody">
	  <td width="9%">&nbsp;</td>
	  <td width="41%">&nbsp; </td>
	  <td width="11%">&nbsp;</td>
	  <td width="39%">&nbsp;</td>
	</tr>
	<tr>
	  <td width="9%" class="formTitle"><bean:message key="label.nomeArea"/>:&nbsp;</td>
	  <td width="41%" class="formBody">
		<input type="text" name="idTask" maxlength="30" size="30" class="textBox">
	  </td>
	  <td width="11%" class="formTitle">&nbsp;</td>
	  <td width="39%" class="formBody">&nbsp;</td>
	</tr>
	<tr>
	  <td width="9%" class="formTitle"><bean:message key="label.descricaoArea"/>:&nbsp;</td>
	  <td width="52%" class="formBody" colspan="2">
		<textarea name="description" rows="3" cols="80" class="textBox"></textarea>
	  </td>
	  <td width="39%" class="formBody">&nbsp; </td>
	</tr>
	<tr class="pagingFormBody">
	  <td width="9%">&nbsp;</td>
	  <td width="41%">&nbsp; </td>
	  <td width="11%">&nbsp;</td>
	  <td width="39%">&nbsp;</td>
	</tr>
	</table>

	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('areaForm', 'saveArea');">
				<bean:write name="areaForm" property="saveLabel" />
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('areaForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td width="50%">&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('areaForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
   	</display:headerfootergrid>
	
	<div>&nbsp;</div>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.areaGrid"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="areaList" scope="session" pagesize="4">
				  <display:column property="name" title="label.nomeListArea" />
				  <display:column property="description" title="label.descricaoListArea" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'areaForm', 'editArea'" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'areaForm', 'removeArea'" />
			</display:table>
		</td>
	</tr> 
	</table>
	
	<display:headerfootergrid type="FOOTER">	
		&nbsp;
   	</display:headerfootergrid>	

</html:form>

<jsp:include page="footer.jsp" />

<!-- Fim do código -->
<script> document.forms["areaForm"].name.focus(); </script>