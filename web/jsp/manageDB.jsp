<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<jsp:include page="header.jsp" />

<html:form  action="manageDB">

	<html:hidden name="dbForm" property="operation"/>
 
	<div>&nbsp;</div>
 
	<display:headerfootergrid width="100%" type="HEADER">
		SQL
	</display:headerfootergrid>
   
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="pagingFormBody">
	  <td width="9%">&nbsp;</td>
	  <td width="41%">&nbsp; </td>
	  <td width="11%">&nbsp;</td>
	  <td width="39%">&nbsp;</td>
	</tr>
	<tr>
	  <td class="formTitle">SQL:&nbsp;</td>
	  <td class="formBody">
		 <html:textarea name="dbForm" property="sql" styleClass="textBox" cols="85" rows="8"/>
	  </td>
	  <td class="formTitle">&nbsp;</td>
	  <td class="formBody">&nbsp;</td>
	</tr>
	<tr class="pagingFormBody">
	  <td>&nbsp;</td>
	  <td>&nbsp; </td>
	  <td>&nbsp;</td>
	  <td>&nbsp;</td>
	</tr>
	</table>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="perform" styleClass="button" onclick="javascript:buttonClick('dbForm', 'performQuery');">
				Executar
			  </html:button>          
		  </td>    
		  <td>&nbsp;</td>      
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('dbForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
  
	<div>&nbsp;</div>


	<bean:write name="dbForm" property="result" filter="false" />
  
</html:form>

<jsp:include page="footer.jsp" />