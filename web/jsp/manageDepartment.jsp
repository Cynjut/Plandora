<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<html:form  action="depart">
	<html:hidden name="departmentForm" property="operation"/>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.department"/>
	</display:headerfootergrid>
	  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="pagingFormBody">
	  <td width="100%">&nbsp;</td>
	</tr>
	</table>

	<plandora-html:forms name="departmentForm" table="department" tablePkList="id" 
  					   lableList="label.department.form@bundle" collection="departmentList" styleTitle="formTitle" 
  					   titleWidth="10%" styleBody="formBody" styleForms="textBox" /> 
  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="pagingFormBody">
	  <td width="9%">&nbsp;</td>
	  <td width="41%">&nbsp; </td>
	  <td width="11%">&nbsp;</td>
	  <td width="39%">&nbsp;</td>
	</tr>
	<tr>
	  <td width="9%" class="formTitle"><bean:message key="label.name"/>:&nbsp;</td>
	  <td width="41%" class="formBody">
		<input type="text" name="idTask" maxlength="30" size="30" class="textBox">
	  </td>
	  <td width="11%" class="formTitle">&nbsp;</td>
	  <td width="39%" class="formBody">&nbsp;</td>
	</tr>
	<tr>
	  <td width="9%" class="formTitle"><bean:message key="label.descricao"/>:&nbsp;</td>
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
		  <td width="15%">
			<input type="submit" name="sendRequest" value="  Salvar   " class="button">
		  </td>
		  <td width="15%">
			<input type="submit" name="sendRequest" value="   Novo    " class="button">
		  </td>
		  <td>&nbsp;</td>
		  <td width="20%">
			<input type="submit" name="sendRequest" value="  Voltar   " class="button">
		  </td>
		</tr></table>
	</display:headerfootergrid>

</html:form>

<jsp:include page="footer.jsp" />

<!-- Fim do código -->
<script> document.forms["departmentForm"].username.focus(); </script>
