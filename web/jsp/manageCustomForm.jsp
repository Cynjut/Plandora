<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>


<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.customForm.confirmRemove"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation);
         }
     }
     
     function beforeSaveEvent(){
     
     	<bean:write name="customFormForm" property="jsBeforeSave" filter="false" />
     
     	javascript:buttonClick('customFormForm', 'saveCustomForm');
     }
     
     
     function afterSaveEvent(){
     	<bean:write name="customFormForm" property="jsAfterSave" filter="false" />
     }
     

     function afterLoadEvent(){
     	<bean:write name="customFormForm" property="jsAfterLoad" filter="false" />
     }
     
</script>


<html:form action="manageCustomForm">
	<html:hidden name="customFormForm" property="operation"/>
	<html:hidden name="customFormForm" property="genericTag"/>
	<html:hidden name="customFormForm" property="id"/>	
	<html:hidden name="customFormForm" property="metaFormId"/>
	<html:hidden name="customFormForm" property="afterSuccessfullySave"/>
		
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:write name="customFormForm" property="formTitle" filter="false"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="5">&nbsp;</td>
    </tr>
    	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="3">
	  	<plandora-html:metafield name="customFormForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="120" forward="manageCustomForm?operation=navigate"/>     
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>
  	
    <tr class="gapFormBody">
      <td colspan="5">&nbsp;</td>
    </tr> 
    </table>
    
   	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:beforeSaveEvent();">
				<bean:write name="customFormForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('customFormForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('customFormForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="label.customForm.listtitle"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="customList" scope="session" pagesize="customFormRowNumber" frm="customFormForm">
				<plandora-html:pcolumn width="4%" property="id" align="center" title="grid.title.empty" />			
				<plandora-html:metafieldPcolumn property="additionalFields" comboFilter="true" likeSearching="true" align="center" />			
				<plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'customFormForm', 'editRecord'" />
				<plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'customFormForm', 'removeRecord'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>

   	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('customFormForm', 'refresh');">
			<bean:message key="button.refresh"/>
		  </html:button>    
		</td>
		<td class="textBoxOverTitleArea">
			<bean:message key="label.customForm.hideRecords"/>&nbsp;
			<html:text name="customFormForm" property="daysToHideRecords" styleClass="textBox" size="3" maxlength="4"/>&nbsp;
			<bean:message key="label.days"/>
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
	with(document.forms["customFormForm"]){
		afterLoadEvent();
		
		if (afterSuccessfullySave.value == "on") {
			afterSaveEvent();
		}		
	}
</script>    	
    
  	