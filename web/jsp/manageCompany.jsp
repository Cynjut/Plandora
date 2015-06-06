<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.company.confirmRemove"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation);
         }
     }
</script>

<html:form  action="manageCompany">
	<html:hidden name="companyForm" property="operation"/>
	<html:hidden name="companyForm" property="id"/>

	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.company"/>
		</display:headerfootergrid>
		
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
		  <td width="10">&nbsp;</td>
		  <td width="100">&nbsp; </td>
		  <td width="220">&nbsp;</td>
		  <td width="80">&nbsp; </td>
		  <td width="200">&nbsp;</td>
		  <td>&nbsp;</td>
		  <td width="10">&nbsp;</td>
		</tr>
		
		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.name"/>:&nbsp;</td>
		  <td class="formBody" colspan="4">
				<html:text name="companyForm" property="name" styleClass="textBox" size="25" maxlength="25"/>		
		  </td>
		  <td>&nbsp;</td>
		</tr>
		
		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.fullname"/>:&nbsp;</td>
		  <td class="formBody" colspan="4">
				<html:text name="companyForm" property="fullName" styleClass="textBox" size="50" maxlength="255"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>

		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.number"/>:&nbsp;</td>
		  <td class="formBody" colspan="4">
				<html:text name="companyForm" property="companyNumber" styleClass="textBox" size="40" maxlength="50"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>

		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.address"/>:&nbsp;</td>
		  <td class="formBody" colspan="4">
				<html:text name="companyForm" property="address" styleClass="textBox" size="50" maxlength="255"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>

		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.city"/>:&nbsp;</td>
		  <td class="formBody">
				<html:text name="companyForm" property="city" styleClass="textBox" size="30" maxlength="70"/>
		  </td>
		  <td class="formTitle"><bean:message key="label.company.state"/>:&nbsp;</td>
		  <td class="formBody">
				<html:text name="companyForm" property="stateProvince" styleClass="textBox" size="20" maxlength="30"/>
		  </td>
		  <td>&nbsp;</td>
		  <td>&nbsp;</td>
		</tr>

		<tr>
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.company.country"/>:&nbsp;</td>
		  <td class="formBody" colspan="4">
				<html:text name="companyForm" property="country" styleClass="textBox" size="50" maxlength="90"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>

		
		<tr class="pagingFormBody">
		  <td colspan="7">&nbsp;</td>
		</tr>
		</table>
	
		<display:headerfootergrid type="FOOTER">	
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('companyForm', 'saveCompany');">
					<bean:write name="companyForm" property="saveLabel" filter="false" />
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('companyForm', 'clear');">
					<bean:message key="button.new"/>
				  </html:button>    
			  </td>
			  <td width="50%">&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('companyForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
	   	</display:headerfootergrid>
		
		<div>&nbsp;</div>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="label.company.titleList"/>
		</display:headerfootergrid>
	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<plandora-html:ptable width="100%" name="companyList" scope="session" pagesize="15" frm="companyForm">
					  <plandora-html:pcolumn width="20%" property="name" title="label.company.name" likeSearching="true" />
					  <plandora-html:pcolumn property="fullName" title="label.company.fullname" likeSearching="true" />
					  <plandora-html:pcolumn width="15%" property="companyNumber" title="label.company.number" />
					  <plandora-html:pcolumn width="15%" property="city" title="label.company.city" comboFilter="true" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'companyForm', 'editCompany'" />
					  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'companyForm', 'removeCompany'" />
				</plandora-html:ptable>
			</td>
		</tr> 
		</table>
		
		<display:headerfootergrid type="FOOTER">			
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('companyForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			</tr></table>  	
		</display:headerfootergrid> 

	</td><td width="20">&nbsp;</td>
  	</tr></table>
</html:form>

<jsp:include page="footer.jsp" />

<!-- Fim do codigo -->
<script> 
	document.forms["companyForm"].name.focus(); 
</script>