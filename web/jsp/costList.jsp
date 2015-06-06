<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="JavaScript">

	function showCostPanel(){
		var prjId;
		with(document.forms["costListForm"]){
			prjId = projectId.value
		}
		window.location = "../do/showCostPanel?operation=prepareForm&type=PRJ&projectId=" + prjId;	
	}
	
	function refreshBody(){
    	with(document.forms["costListForm"]){
        	operation.value = "refresh";
        	submit();			
        }	
	}
	
    function editCost(costId) {
		with(document.forms["costListForm"]){
			displayMessage("../do/showCostEdit?operation=prepareForm&usedByExpenseForm=off&forwardAfterSave=goToCostList&editCostId=" + costId + "&projectId=" + projectId.value, 530, 340);    	    
		}	    	    
    }
	
	function showExpenseReport(eid){
		with(document.forms["costListForm"]){
   			operation.value = "showExpenseReport";
   			expenseId.value = eid;
			submit();
   		}	
	}	
	
</script>

<html:form  action="showCostList">
	<html:hidden name="costListForm" property="operation"/>
	<html:hidden name="costListForm" property="projectId"/>
	<html:hidden name="costListForm" property="expenseReportURL"/>
	<html:hidden name="costListForm" property="expenseId"/>
	
	<plandora-html:shortcut name="costListForm" property="goToCostList" fieldList="projectId"/>
	
	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.cost"/>
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
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.cost.initialDate"/>:&nbsp;</td>
			<td><plandora-html:calendar name="costListForm" property="initialDate" styleClass="textBoxDisabled" /></td>
	        <td class="formTitle"><bean:message key="label.cost.finalDate"/>:&nbsp;</td>		
			<td><plandora-html:calendar name="costListForm" property="finalDate" styleClass="textBoxDisabled" /></td>
			<td>&nbsp;</td>			
		</tr> 
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.costedit.category"/>:&nbsp;</td>
			<td colspan="2">
		  		<html:select name="costListForm" property="categoryName" styleClass="textBox" onkeypress="javascript:refreshBody();" onchange="javascript:refreshBody();">
		             <html:options collection="costCategoryList" property="id" labelProperty="name" filter="false"/>
				</html:select>		
			</td>
			<td>&nbsp;</td>	
			<td>&nbsp;</td>			
		</tr> 	
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.costedit.status"/>:&nbsp;</td>
			<td colspan="2">
		  		<html:select name="costListForm" property="statusId" styleClass="textBox" onkeypress="javascript:refreshBody();" onchange="javascript:refreshBody();">
		             <html:options collection="costStatusFilterList" property="id" labelProperty="genericTag" filter="false"/>
				</html:select>		
			</td>
			<td>&nbsp;</td>	
			<td>&nbsp;</td>			
		</tr> 	
		<tr class="gapFormBody">
			<td colspan="6">&nbsp;</td>
		</tr>
		</table>
		
		<display:headerfootergrid type="FOOTER">	
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('costListForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td width="140">
				  <html:button property="showPanel" styleClass="button" onclick="javascript:showCostPanel();">
					 <bean:message key="title.costlist.tree"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>			  			  
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('costListForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>  	
		</display:headerfootergrid> 
		
		<div>&nbsp;</div>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.costlist.list"/>
		</display:headerfootergrid>
	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<plandora-html:ptable width="100%" name="prjCostList" scope="session" pagesize="0" frm="costListForm">
					<plandora-html:pcolumn width="5%" sort="true" property="cost.id" align="center" title="label.costlist.costid" />
					<plandora-html:pcolumn width="3%" property="installmentNum" align="center" title="grid.title.empty" />
					<plandora-html:pcolumn width="5%" sort="true" property="cost.expenseId" align="center" title="label.costlist.expense" description="label.costlist.expense" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_EXPE%>" decorator="com.pandora.gui.taglib.decorator.CostListShowReportDecorator"/>										
					<plandora-html:pcolumn property="cost.name" sort="true" align="center" title="label.costedit.name" />
					<plandora-html:pcolumn property="cost.description" sort="true" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.costedit.desc" description="label.costedit.desc" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_DESC%>"/>
					<plandora-html:pcolumn width="10%" comboFilter="true" sort="true" align="center" property="cost.project.name" title="label.costedit.prj" description="label.costedit.prj" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_PROJ%>" />
					<plandora-html:pcolumn width="10%" comboFilter="true" sort="true" property="cost.category.name" align="center" title="label.costedit.category" description="label.costedit.category" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_CATEG%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="category;$allCostCategoryList"/>
					<plandora-html:pcolumn width="10%" sort="true" property="cost.accountCode" align="center" title="label.costedit.acccode" description="label.costedit.acccode" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_ACCCD%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="acccode;$allCostAccountCodeList" />										  
					<plandora-html:pcolumn width="7%" sort="true" property="costStatus.id" align="center" title="label.costedit.status" description="label.costedit.status" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_STAT%>" decorator="com.pandora.gui.taglib.decorator.GridComboBoxDecorator" tag="status;$allCostStatusList"/>
					<plandora-html:pcolumn width="8%" sort="true" property="valueStr" align="center" title="label.costedit.value" description="label.costedit.value" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_VAL%>"/>
					<plandora-html:pcolumn width="7%" comboFilter="true" sort="true" property="approverName" align="center" title="label.costlist.approver" description="label.costlist.approver" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_APPR%>"/>
					<plandora-html:pcolumn width="7%" sort="true" property="dueDate" align="center" title="label.expense.duedate" description="label.expense.duedate" visibleProperty="<%=PreferenceTO.LIST_ALL_COST_SW_DUED%>" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0"/>
					<plandora-html:pcolumn width="2%" property="cost.id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.CostListEditDecorator" />					  
				</plandora-html:ptable>		
			</td>
		</tr> 
		</table>
	
		<display:headerfootergrid type="FOOTER">	
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('costListForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td align="center">
				  <logic:equal name="costListForm" property="showUpdateInBatch" value="on">
					  <html:button property="updateInBatch" styleClass="button" onclick="javascript:buttonClick('costListForm', 'updateInBatch');">
						   <bean:message key="label.costlist.updateInBatch"/>
					  </html:button>
				  </logic:equal>
				  <logic:equal name="costListForm" property="showUpdateInBatch" value="off">
						&nbsp;
				  </logic:equal>   	  
			  </td>      
			  <td width="50" align="right">
				<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('costListForm', 'backward');">
					<bean:message key="button.backward"/>
				</html:button>    
			  </td>      
			</tr></table>  	
		</display:headerfootergrid>
		
		<div>&nbsp;</div>
	
	</td><td width="20">&nbsp;</td>
	</tr>
	<tr><td colspan="3" height="50%">&nbsp;</td></tr>
	</table>

</html:form>

<jsp:include page="footer.jsp" /> 

<!-- End of source-code -->
<script> 
	with(document.forms["costListForm"]){	
   		<logic:notEqual name="costListForm" property="expenseReportURL" value="">	
			window.open('<bean:write name="costListForm" property="expenseReportURL" filter="false"/>', 'ExpenseReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
		</logic:notEqual>
   		expenseReportURL.value = '';
	}
</script>  			 	