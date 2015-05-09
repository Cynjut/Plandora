<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.expense.confirmRemoveExpense"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function changeExpenseItemId(expId){
		with(document.forms["expenseForm"]){	
			displayMessage("../do/showCostEdit?operation=prepareForm&usedByExpenseForm=on&forwardAfterSave=goToExpenseForm&editCostId=" + expId + "&projectId=" + projectId.value, 530, 340);		
		}	      
     }
     
     function removeExpenseItem(expId){
		with(document.forms["expenseForm"]){
		    if ( confirm("<bean:message key="message.expense.confirmRemoveExpense"/>")) {
				removeExpenseId.value = expId;
				buttonClick('expenseForm', 'removeItem');				    
		    }			
		}	      
     }


	 function addExpenseItem(){
		with(document.forms["expenseForm"]){
			buttonClick('expenseForm', 'showAddCost');
		}	 
	 }
	 

     function changeProject(){
		with(document.forms["expenseForm"]){
			buttonClick('expenseForm', 'changeProject');
		}	      
     }
     
     
     function showReport(eid){
    	with(document.forms["expenseForm"]){
			expenseId.value = eid;
			buttonClick('expenseForm', 'showReport');	
		}     
	}
     
</script>

<html:form action="manageExpense">
	<html:hidden name="expenseForm" property="operation"/>
	<html:hidden name="expenseForm" property="id"/>
	<html:hidden name="expenseForm" property="expenseId"/>
	<html:hidden name="expenseForm" property="removeExpenseId"/>
	<html:hidden name="expenseForm" property="showSaveButton"/>
	<html:hidden name="expenseForm" property="showAddForm"/>
		
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.expense"/>
	</display:headerfootergrid>
	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.expense.user"/>:&nbsp;</td>
      <td width="400" class="formBody">
        <html:text name="expenseForm" property="username" styleClass="textBox" disabled="true" size="50" maxlength="50"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.expense.project"/>:&nbsp;</td>
      <td class="formBody">
     	  <logic:equal name="expenseForm" property="changeProject" value="on">
		  		<html:select name="expenseForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
		             <html:options collection="expenseProjectList" property="id" labelProperty="name"/>
				</html:select>
		  </logic:equal>
     	  <logic:equal name="expenseForm" property="changeProject" value="off">
		  		<html:select name="expenseForm" property="projectId" styleClass="textBox" disabled="true">
		             <html:options collection="expenseProjectList" property="id" labelProperty="name"/>
				</html:select>
		  </logic:equal>
      </td>
      <td>&nbsp;</td>
    </tr>   
    		    	    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="2">
		  <plandora-html:metafield name="expenseForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="150" forward="manageExpense?operation=navigate" /> 
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.expense.comment"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="expenseForm" property="comment" styleClass="textBox" cols="86" rows="5" />
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr> 
    </table>



	<table width="98%" border="0" cellspacing="0" cellpadding="0">  	
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
	
	<tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle">&nbsp;</td>
      <td width="530" class="formBody">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formLabel">
					<td width="10">&nbsp;</td>
					<td><bean:message key="title.expense.itemList"/></td>
					<td width="40">
						<html:button property="addExpItem" styleClass="button" onclick="javascript:addExpenseItem();">
							<bean:message key="button.addInto"/>
						</html:button>			
					</td>
				</tr>
				<tr class="pagingFormBody">
					<td colspan="3" class="formBody">
						<display:table border="1" width="100%" name="expenseItemList" pagesize="0" scope="session">
							  <display:column width="80" align="center" property="firstInstallmentDate" title="label.expense.date" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
							  <display:column property="name" title="label.costedit.name"/>
							  <display:column width="130" property="category.name" title="label.costedit.category"/>
							  <display:column width="80" align="center" property="totalValue" title="label.costedit.value" decorator="com.pandora.gui.taglib.decorator.ExpenseTotalDecorator"/>
  					  		  <display:column width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseApproverRefuserDecorator" />							  
  					  		  <display:column width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseEditDecorator" />
					  		  <display:column width="15" align="center" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseRemoveDecorator" />							  
						</display:table>
					</td>
				</tr>
				<tr class="pagingFormBody">
					<td colspan="3">
						<table width="100%" border="0" cellspacing="0" cellpadding="4">
							<tr class="rowHighlight">
								<td align="right"><b><bean:message key="title.expense.total"/>:&nbsp;</b></td>
								<td width="80"><center><b><bean:write name="expenseForm" property="totalExpense" /></b></center></td>
								<td width="45">&nbsp;</td>
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

	<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
    <tr class="formNotes">
      <td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="label.expense.note"/></div></td>
      <td>&nbsp;</td>
    </tr> 	    
  	</table>


	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <logic:equal name="expenseForm" property="showSaveButton" value="on">
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('expenseForm', 'saveExpense');">
					<bean:write name="expenseForm" property="saveLabel" />
				  </html:button>    
			  </td>
		  </logic:equal>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('expenseForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('expenseForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr>
	  </table>
  	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.expenseList"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="expenseList" scope="session" pagesize="6" requestURI="../do/manageExpense?operation=navigate">
				  <display:column width="4%" property="id" align="center" title="grid.title.empty" />			
				  <display:column sort="true" width="15%" align="center" property="creationDate" title="label.expense.creation" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2"/>				  
				  <display:column sort="true" align="center" property="project.name" title="label.expense.project" />		  
				  <display:column sort="true" width="10%" align="center" property="id" title="label.expense.status" decorator="com.pandora.gui.taglib.decorator.ExpenseStatusDecorator" />
				  <display:column sort="true" width="15%" align="center" property="total" title="title.expense.total" decorator="com.pandora.gui.taglib.decorator.ExpenseTotalDecorator" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ExpenseReportDecorator" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'expenseForm', 'editExpense'" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'expenseForm', 'removeExpense'" />			  
			</display:table>		
		</td>
	</tr> 
	</table>
		
	<display:headerfootergrid type="FOOTER">				
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('expenseForm', 'refresh');">
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
	<logic:notEqual name="expenseForm" property="reportURL" value="">	
		window.open('<bean:write name="expenseForm" property="reportURL" filter="false"/>', 'ExpenseReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
	</logic:notEqual>

	with(document.forms["expenseForm"]){	
		if (showAddForm.value=='on') {
			displayMessage("../do/showCostEdit?operation=prepareForm&usedByExpenseForm=on&forwardAfterSave=goToExpenseForm&editCostId=&projectId=" + projectId.value, 530, 340);
		}
	}
</script> 