<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="javascript">

	function refreshBody(){
    	with(document.forms["costForm"]){
        	operation.value = "refresh";
        	submit();			
        }	
	}

    function editCost(costId) {
		with(document.forms["costForm"]){
			displayMessage("../do/showCostEdit?operation=prepareForm&usedByExpenseForm=off&forwardAfterSave=goToCostForm&editCostId=" + costId + "&projectId=" + projectId.value, 530, 340);    	    
		}	    	    
    }


    function removeCost(costId) {
		with(document.forms["costForm"]){
			if ( confirm("<bean:message key="message.cost.confirmRemoveCost"/>")) {
	        	operation.value = "removeCost";
	        	id.value = costId;
	        	submit();			
			}
		}	    	    
    }

    	
	function showHideChild(parentId){
		var img = document.getElementById("IMG_" + parentId);
		if (img!=null) {
			var el = document.getElementById("DIV_" + parentId);
			var ec = document.getElementById("DIV_CELL_" + parentId);
			if (el!=null && ec!=null) {	
				var str = img.src;
				var isMinus = endsWith(str, 'minus.gif');
				var pos =-1;
				if (isMinus) {
					pos = str.indexOf("minus.gif", 1);
					img.src = str.substr(0,pos) + "plus.gif";
					el.style.display =  'none' ;
					ec.style.display =  'none' ;
					behidden = true ;
				} else {
					pos = str.indexOf("plus.gif", 1);
					img.src = str.substr(0,pos) + "minus.gif";
					el.style.display =  'block';
					ec.style.display =  'block';
					behidden = false;												
				}
			}
		}
	}
    
	function showIconCost(argId){
		var el = document.getElementById( argId );
		if (el!=null) {
			el.style.display =  'block'; 
			behidden = false;  
		}	
	}
		
	function hideIconCost(argId){
		var el = document.getElementById( argId );
		if (el!=null) {
			el.style.display =  'none' ; 
			behidden = true ;  
		}		
	}  
	
	function showExpenseReport(eid){
		with(document.forms["costForm"]){
   			operation.value = "showExpenseReport";
   			expenseId.value = eid;
			submit();
   		}	
	}	
	 
</script>

<html:form action="showCostPanel">
	<html:hidden name="costForm" property="operation"/>
	<html:hidden name="costForm" property="id"/>
	<html:hidden name="costForm" property="showEditCost"/>
	<html:hidden name="costForm" property="projectId"/>
	<html:hidden name="costForm" property="expenseId"/>
	<html:hidden name="costForm" property="expenseReportURL"/>
	
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.cost"/>
		</display:headerfootergrid>
		  	
		<table width="97%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td colspan="5">&nbsp;</td>
	    </tr>
	   
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="120" class="formTitle"><bean:message key="label.cost.type"/>:&nbsp;</td>
	      <td class="formBody" colspan="3">
				<html:select name="costForm" property="type" styleClass="textBox" onkeypress="javascript:refreshBody();" onchange="javascript:refreshBody();">
					<html:options collection="costPanelTypes" property="id" labelProperty="genericTag"/>
				</html:select>
	      </td>
	    </tr>
		
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.cost.initialDate"/>:&nbsp;</td>
			<td width="100"><plandora-html:calendar name="costForm" property="initialDate" styleClass="textBoxDisabled" /></td>
	        <td width="70" class="formTitle"><bean:message key="label.cost.finalDate"/>:&nbsp;</td>		
			<td><plandora-html:calendar name="costForm" property="finalDate" styleClass="textBoxDisabled" /></td>
		</tr> 
	
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.cost.gran"/>:&nbsp;</td>		
			<td colspan="3">
				<html:select name="costForm" property="granularity" styleClass="textBox" onkeypress="javascript:refreshBody();" onchange="javascript:refreshBody();">
					<html:options collection="costPanelGran" property="id" labelProperty="genericTag"/>
				</html:select>			
			</td>
		</tr> 
	
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.cost.viewMode"/>:&nbsp;</td>		
			<td colspan="3">
				<html:select name="costForm" property="viewMode" styleClass="textBox" onkeypress="javascript:refreshBody();" onchange="javascript:refreshBody();">
					<html:options collection="costPanelViewModes" property="id" labelProperty="genericTag"/>
				</html:select>			
			</td>
		</tr> 

		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle">&nbsp;</td>		
			<td colspan="3">
		   		<html:checkbox property="hideOutOfRange" name="costForm" >
		   			<bean:message key="label.cost.hideRange"/>
		   		</html:checkbox>	
			</td>
		</tr> 	
	
	    <tr class="gapFormBody">
	      <td colspan="5">&nbsp;</td>
	    </tr>
	    </table>
	
		<display:headerfootergrid type="FOOTER">			
			<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('costForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td width="120">      
			      <html:button property="showEditPanel" styleClass="button" onclick="javascript:buttonClick('costForm', 'showEditPanel');">
				    <bean:message key="label.cost.new"/>
			      </html:button>    
			  </td>		
			  <td>&nbsp;</td>
			  <td width="120">
			  	  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('costForm', 'backward');">
				      <bean:message key="button.backward"/>
			      </html:button>    
			  </td>		
			</tr>
		  </table>
	  	</display:headerfootergrid>
  	 
	</td><td width="10">&nbsp;</td>
	
	<logic:equal name="costForm" property="canShowChart" value="on">
		<td width="500">
			<display:headerfootergrid width="100%" type="HEADER">
				&nbsp;
			</display:headerfootergrid>
	
			<div id="COST_CHART" style="width:380px; height:150px; overflow-x:hidden; overflow-y:hidden; border:1px">
			</div>
			<script type="text/javascript">
				function callGadget_COST_CHART(){
					swfobject.embedSWF("open-flash-chart", "COST_CHART", 
							"480", "150", "9.0.0", "expressInstall.swf", 
							{ "data-file": "../do/showCostPanel?operation=renderChart", "loading": "loading..."});
				}
				callGadget_COST_CHART();			
			</script>
	
			<display:headerfootergrid type="FOOTER">			
				<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>
				  <td width="120">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('costForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
				  </td>
				</tr>
			  </table>
			</display:headerfootergrid> 
			
		</td><td width="10">&nbsp;</td>
	</logic:equal>
	
	
	</tr></table>

	<div>&nbsp;</div>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>	
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.cost.gridtitle"/>		
	</display:headerfootergrid>

	<table width="98%" id="cap_panel" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
	</table>	

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top">
      		<bean:write name="costForm" property="costHtmlTitle" filter="false" />
      </td>
      <td valign="top">
			<div id="cost_spreadsheet" style="width:950px; overflow-x: scroll; overflow-y: hidden;">
				<bean:write name="costForm" property="costHtmlBody" filter="false" />
				<br>
			</div>      
      </td>
    </tr>
	</table>	
	
	
	<display:headerfootergrid type="FOOTER">				
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="showEditPanel" styleClass="button" onclick="javascript:buttonClick('costForm', 'showEditPanel');">
			<bean:message key="label.cost.new"/>
		  </html:button>    
		</td>		
		<td>&nbsp;</td>
		<td width="120">
		  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('costForm', 'backward');">
			<bean:message key="button.backward"/>
		  </html:button>    
		</td>		
		</tr></table>  	
	</display:headerfootergrid>

  </td><td width="10">&nbsp;</td>
  </tr></table>
</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["costForm"]){	
   		<logic:notEqual name="costForm" property="expenseReportURL" value="">	
			window.open('<bean:write name="costForm" property="expenseReportURL" filter="false"/>', 'ExpenseReport', 'width=600, height=450, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');			
		</logic:notEqual>
   		expenseReportURL.value = '';
	
		if (showEditCost.value=='on') {
			displayMessage("../do/showCostEdit?operation=prepareForm&usedByExpenseForm=off&forwardAfterSave=goToCostForm&editCostId=&projectId=" + projectId.value, 530, 340);
		}
		document.getElementById("cost_spreadsheet").style.width = screen.width - 420;
	}
</script> 