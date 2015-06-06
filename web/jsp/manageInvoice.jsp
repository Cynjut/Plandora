<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveInvoice"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function saveWithConfirmation(){
         if ( confirm("<bean:message key="message.confirmSaveInvoice"/>")) {
	         buttonClick('invoiceForm', 'saveInvoice');
         }
     }
     
     function removeInvoiceItem(iid) {
    	with(document.forms["invoiceForm"]){
			removedInvoiceItemId.value = iid;
			callAction(id.value, 'invoiceForm', 'removeInvoiceItem');
		}     
     }
    
	function editInvoiceItem(){
		buttonClick('invoiceForm', 'showEditInvoiceItem');
	}


	function changeInvoiceItemId(iid){
    	with(document.forms["invoiceForm"]){
			editInvoiceItemId.value = iid;
			callAction(id.value, 'invoiceForm', 'showEditInvoiceItem');
		}     
	}
     
	function openInvHistPopup(iid){
	    var pathWindow ="../do/manageHistInvoice?operation=prepareForm&invId=" + iid;
		window.open(pathWindow, 'invHist', 'width=780, height=300, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');	
	}
     
	function gridComboFilterRefresh(url, param, cbName){
		javascript:buttonClick('invoiceForm', 'refresh');
	}

	function gridTextFilterRefresh(url, param, fldName){
		javascript:buttonClick('invoiceForm', 'refresh');
	}
     
</script>

<html:form action="manageInvoice">
	<html:hidden name="invoiceForm" property="operation"/>
	<html:hidden name="invoiceForm" property="genericTag"/>	
	<html:hidden name="invoiceForm" property="id"/>	
	<html:hidden name="invoiceForm" property="projectId"/>
	<html:hidden name="invoiceForm" property="removedInvoiceItemId"/>	
	<html:hidden name="invoiceForm" property="editInvoiceItemId"/>	
	<html:hidden name="invoiceForm" property="showEditInvoiceItem"/>	
		
	<plandora-html:shortcut name="invoiceForm" property="goToInvoiceForm" fieldList="projectId"/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.invoiceForm"/>
	</display:headerfootergrid>
  	
  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.invoiceForm.project"/>:&nbsp;</td>
      <td width="210" class="formBody">
        <bean:write name="invoiceForm" property="projectName" filter="false"/>
      </td>
      <td width="110">&nbsp;</td>
      <td width="250">&nbsp;</td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.name"/>:&nbsp;</td>
      <td class="formBody" colspan="2">
        <html:text name="invoiceForm" property="name" styleClass="textBox" size="50" maxlength="50"/>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.number"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="invoiceForm" property="invoiceNumber" styleClass="textBox" size="30" maxlength="40"/>
      </td>
      <td class="formTitle"><bean:message key="label.invoiceForm.purchaseOrder"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="invoiceForm" property="purchaseOrder" styleClass="textBox" size="25" maxlength="40"/>
      </td>
      <td>&nbsp;</td>
    </tr>    

    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>

	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.invoicedate"/>:&nbsp;</td>
      <td class="formBody">
		  <plandora-html:calendar name="invoiceForm" property="invoiceDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>

	<tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.duedate"/>:&nbsp;</td>
      <td class="formBody">
		  <plandora-html:calendar name="invoiceForm" property="dueDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>

    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.category"/>:&nbsp;</td>
      <td class="formBody">
	  		<html:select name="invoiceForm" property="categoryId" styleClass="textBox">
	             <html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.status"/>:&nbsp;</td>
      <td class="formBody">
	  		<html:select name="invoiceForm" property="invoiceStatusId" styleClass="textBox">
	             <html:options collection="invoiceStatusList" property="id" labelProperty="name" filter="false"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.description"/>:&nbsp;</td>
      <td class="formBody" colspan="3">
   	     <html:textarea name="invoiceForm" property="description" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.invoiceForm.contact"/>:&nbsp;</td>
      <td class="formBody" colspan="2">
        <html:text name="invoiceForm" property="contact" styleClass="textBox" size="50" maxlength="70"/>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="4">
		  <plandora-html:metafield name="invoiceForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="150" forward="manageInvoice?operation=navigate" /> 
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>     
    </table>
       
   
  	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">  	
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
	
	<tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle">&nbsp;</td>
      <td width="480" class="formBody">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formLabel">
					<td width="10">&nbsp;</td>
					<td><bean:message key="title.invoiceForm.itemList"/></td>
					<td width="50">
						<html:button property="addInvoiceItem" styleClass="button" onclick="javascript:editInvoiceItem();">
							<bean:message key="button.addInto"/>
						</html:button>			
					</td>
				</tr>
				<tr class="pagingFormBody">
					<td colspan="3" class="formBody">
						<plandora-html:ptable width="100%" name="invoiceItemList" pagesize="10" scope="session" frm="invoiceForm">
							  <plandora-html:pcolumn align="center" property="type" title="label.invoiceForm.itemList.type" decorator="com.pandora.gui.taglib.decorator.InvoiceTypeDecorator" />
							  <plandora-html:pcolumn width="150" property="itemName" title="label.invoiceForm.itemList.name"/>
							  <plandora-html:pcolumn width="50" align="center" property="amount" title="label.invoiceForm.itemList.qty"/>
  							  <plandora-html:pcolumn width="140" align="center" property="price" title="label.invoiceForm.itemList.prc" decorator="com.pandora.gui.taglib.decorator.InvoicePriceDecorator"/> 							  
  					  		  <plandora-html:pcolumn width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.InvoiceItemEditDecorator" />
					  		  <plandora-html:pcolumn width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.InvoiceItemRemoveDecorator" />							  
						</plandora-html:ptable>
					</td>
				</tr>
				<tr class="pagingFormBody">
					<td colspan="3">
						<table width="100%" border="0" cellspacing="0" cellpadding="4">
							<tr class="rowHighlight">
								<td align="right"><b><bean:message key="title.invoiceForm.total"/>:&nbsp;</b></td>
								<td width="140"><center><b><bean:write name="invoiceForm" property="totalInvoice" filter="false"/></b></center></td>
								<td width="30">&nbsp;</td>
							</tr>					
						</table>
					</td>
				</tr>
				<tr class="gapFormBody">
					<td colspan="3">&nbsp;</td>
				</tr> 				
				<tr class="gapFormBody">
					<td colspan="3">&nbsp;</td>
				</tr> 								
			</table>				

      </td>
      <td>&nbsp;</td>
    </tr>
  	</table>

    <display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('invoiceForm', 'saveInvoice');">
				<bean:write name="invoiceForm" property="saveLabel" filter="false" />
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('invoiceForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('invoiceForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
   	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.invoiceList"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="invoiceList" scope="session" pagesize="6"  frm="invoiceForm">
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty"/>			
				  <plandora-html:pcolumn sort="true" property="name" likeSearching="true" title="label.invoiceForm.name" description="title.invoiceForm.grid.name" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_NAME%>"/>
				  <plandora-html:pcolumn width="80"  align="center" sort="true" likeSearching="true" property="invoiceNumber" title="title.invoiceForm.grid.number" description="label.invoiceForm.number" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_NUMBER%>" />
				  <plandora-html:pcolumn width="80"  align="center" sort="true" likeSearching="true" property="purchaseOrder" title="title.invoiceForm.grid.purchaseorder" description="label.invoiceForm.purchaseOrder" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_PO%>" />				  
				  <plandora-html:pcolumn width="120" align="center" sort="true" property="project.name" comboFilter="true" title="label.invoiceForm.project" description="label.invoiceForm.project" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_PRJ%>"/>
				  <plandora-html:pcolumn width="80"  align="center" sort="true" property="id" comboFilter="true" title="label.invoiceForm.status" description="label.invoiceForm.status" decorator="com.pandora.gui.taglib.decorator.InvoiceStatusDecorator" />
				  <plandora-html:pcolumn width="100" align="center" sort="true" property="id" comboFilter="true" title="label.invoiceForm.category" description="label.invoiceForm.category" decorator="com.pandora.gui.taglib.decorator.InvoiceCategoryDecorator" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_CATEG%>" />
				  <plandora-html:pcolumn width="80"  align="center" property="dueDate" title="title.invoiceForm.grid.duedate" description="label.invoiceForm.duedate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_DUEDT%>" />
				  <plandora-html:pcolumn width="80"  align="center" property="invoiceDate" title="title.invoiceForm.grid.invoicedate" description="label.invoiceForm.invoicedate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_INVDT%>" />
				  <plandora-html:pcolumn width="80"  align="center" sort="true" likeSearching="true" property="contact" title="label.invoiceForm.contact" description="label.invoiceForm.contact" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_CONTACT%>" />
				  <plandora-html:pcolumn width="80"  align="center" sort="true" property="id" title="title.invoiceForm.total" description="title.invoiceForm.grid.total" decorator="com.pandora.gui.taglib.decorator.InvoiceTotalDecorator" visibleProperty="<%=PreferenceTO.INV_INVLIST_SW_TOTAL%>" />
				  <plandora-html:pcolumn width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'invoiceForm', 'editInvoice'" />
				  <plandora-html:pcolumn width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'invoiceForm', 'removeInvoice'" />
				  <plandora-html:pcolumn width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'INV'" />				  
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>
		
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('invoiceForm', 'refresh');">
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
	with(document.forms["invoiceForm"]){	
		name.focus();
		if (showEditInvoiceItem.value=='on') {
			displayMessage("../do/showInvoiceItem?operation=showEditPopup&invoiceId=" + id.value + "&editInvoiceItemId=" + editInvoiceItemId.value, 380, 190);
		}		 
	}
</script> 