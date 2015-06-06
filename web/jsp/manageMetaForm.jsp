<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveMetaForm"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
          
</script>

<html:form action="manageMetaForm">
	<html:hidden name="metaFormForm" property="operation"/>
	<html:hidden name="metaFormForm" property="id"/>	
		
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.metaForm"/>
	</display:headerfootergrid>
	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="300" class="formTitle"><bean:message key="label.metaform.name"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFormForm" property="name" styleClass="textBox" size="30" maxlength="30"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.viewblecols"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFormForm" property="viewableCols" styleClass="textBox" size="50" maxlength="100"/>   		
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.gridNum"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFormForm" property="gridNumber" styleClass="textBox" size="7" maxlength="3"/>   		
      </td>
      <td>&nbsp;</td>
    </tr>    

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.filterId"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFormForm" property="filterColId" styleClass="textBox" size="10" maxlength="10"/>   		
      </td>
      <td>&nbsp;</td>
    </tr>    

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.jsBeforeSave"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="metaFormForm" property="jsBeforeSave" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.jsAfterSave"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="metaFormForm" property="jsAfterSave" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.metaform.jsAfterLoad"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="metaFormForm" property="jsAfterLoad" styleClass="textBox" cols="86" rows="4" />
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
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('metaFormForm', 'saveMetaForm');">
				<bean:write name="metaFormForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('metaFormForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('metaFormForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.metaformList"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="metaFormList" scope="session" pagesize="6" frm="metaFormForm">
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" />			
				  <plandora-html:pcolumn property="name" likeSearching="true" title="label.metaform.name" />
				  <plandora-html:pcolumn width="15%" align="center" property="gridNumber" title="label.metaform.gridNum" />				  
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'metaFormForm', 'editMetaForm'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'metaFormForm', 'removeMetaForm'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>

	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('metaFormForm', 'refresh');">
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
	with(document.forms["metaFormForm"]){	
		name.focus(); 
	}
</script> 