<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<%@ page import="com.pandora.gui.struts.form.GeneralStrutsForm"%>
<%@ page import="com.pandora.ProjectStatusTO"%>
<%@ page import="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator"%>
<%@ page import="com.pandora.gui.taglib.decorator.ProjectGridNumericBoxDecorator"%>


<jsp:include page="header.jsp" />

<script language="javascript">

     function changeStatus(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmChangeStatusProj"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation);
         }
     }     

	 function remove(userId, dummy){
		 with(document.forms["projectForm"]){
		    removedUserId.value = userId;
    		operation.value = "removeUser";
    		submit();
		 }         
	 }

	 function changeRepository(){
	 	buttonClick('projectForm', 'changeRepository');
	 }
	
	 function saveProject(){
		buttonClick('projectForm', 'saveProject');
	 }

	 function check_DISABLE_USER_COL(userId){
	 	//dummy
	 }
	 
	 function check_CUSTOMER_COL(userId){
	 	//dummy
	 }

	 function check_REQ_ACCEPT_CUSTOMER_COL(userId){
	 	//dummy	 
	 }

	 function check_ALLOW_SEE_DISCUSSION_COL(userId){
	 	//dummy	 
	 }
	 
	 function check_CAN_SEE_OTHER_REQS_COL(userId){
	 	//dummy
	 }

	 function check_CAN_OPEN_OTHEROWNER_REQS_COL(userId){
		 with(document.forms["projectForm"]){
			var checkObjSeeOther = eval("cb_" + userId + "_CAN_SEE_OTHER_REQS_COL");			
			checkObjSeeOther.checked=true;
		 }
	 }

	 function check_CAN_SELF_ALLOC_COL(userId){
		 with(document.forms["projectForm"]){
			var checkObjRes = eval("cb_" + userId + "_RESOURCE_COL");
			var checkObjSelfAlloc = eval("cb_" + userId + "_CAN_SELF_ALLOC_COL");			
			if (checkObjSelfAlloc.checked){
				checkObjRes.checked=true;
			}
		 }
	 }


	 function check_RESOURCE_COL(userId){
		 with(document.forms["projectForm"]){
		 	var checkObjLead = eval("cb_" + userId + "_LEADER_COL");
			var checkObjRes = eval("cb_" + userId + "_RESOURCE_COL");
			var checkObjCus = eval("cb_" + userId + "_CUSTOMER_COL");
			var checkObjSeeCust = eval("cb_" + userId + "_ALLOW_SEE_CUSTOMER_COL");
			var checkObjSeeDisc = eval("cb_" + userId + "_ALLOW_SEE_DISCUSSION_COL");
			
			if (checkObjRes.checked){
				checkObjCus.checked=true;
				checkObjSeeDisc.checked=true;
			} else {
				checkObjLead.checked=false;
				checkObjSeeCust.checked=false;			
			}
		 }	 
	 }

	 function check_LEADER_COL(userId){
		 with(document.forms["projectForm"]){
		 	var checkObjLead = eval("cb_" + userId + "_LEADER_COL");
			var checkObjRes = eval("cb_" + userId + "_RESOURCE_COL");
			var checkObjCus = eval("cb_" + userId + "_CUSTOMER_COL");
			
			var checkObjRep = eval("cb_" + userId + "_CAN_SEE_REPOSITORY_COL");
			var checkObjInv = eval("cb_" + userId + "_CAN_SEE_INVOICE_COL");
			var checkObjAgl = eval("cb_" + userId + "_CAN_SELF_ALLOC_COL");
			var checkObjSeeDisc = eval("cb_" + userId + "_ALLOW_SEE_DISCUSSION_COL");
				
			if (checkObjLead.checked){			
				checkObjRes.checked=true;
				checkObjCus.checked=true;
				checkObjRep.checked=true;
				checkObjInv.checked=true;
				checkObjAgl.checked=true;
				checkObjSeeDisc.checked=true;
			}			
		 }
	 }

	 function check_CAN_SEE_REPOSITORY_COL(userId){
	 }


	 function check_CAN_SEE_INVOICE_COL(userId){
	 }


	 function check_ALLOW_PRE_APPROVE_COL(userId){
	 }


	 function check_ALLOW_SEE_CUSTOMER_COL(userId){
		 with(document.forms["projectForm"]){
			var checkObjRes = eval("cb_" + userId + "_RESOURCE_COL");
			var checkObjSeeCust = eval("cb_" + userId + "_ALLOW_SEE_CUSTOMER_COL");			
			if (checkObjSeeCust.checked){
				checkObjRes.checked=true;
			}
		 }
	 }
	     
    function edit(projId, src){
    	if (src =="PRJ") {
	   		window.location = "../do/manageProject?operation=editProject&id=" + projId;
    	}
    }


    function callOccBook(){
    	 with(document.forms["projectForm"]){
    		window.location = "../do/manageOccurrence?operation=prepareForm&projectId=" + id.value;
		 }         
    }
	 
    function openOccHistPopup(id) {
	    var pathWindow ="../do/manageHistOccurrence?operation=prepareForm&occId=" + id;
		window.open(pathWindow, 'occHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

    	  
   	function showResourceCapacity(resId, projId) {
	   	window.location = "../do/showResCapacityPanel?operation=prepareForm&type=RES&resourceId=" + resId + "&projectId=" + projId;
   	}
   	
   	   
   	function gridComboFilterRefresh(url, param, cbName){
		javascript:buttonClick('projectForm', 'refreshList');
	}

	function gridTextFilterRefresh(url, param, fldName){
		javascript:buttonClick('projectForm', 'refreshList');
	}
</script>

<html:form  action="manageProject">
	<html:hidden name="projectForm" property="operation"/>
	<html:hidden name="projectForm" property="id"/>
	<html:hidden name="projectForm" property="genericTag"/>	
	<html:hidden name="projectForm" property="saveMethod"/>
	<html:hidden name="projectForm" property="removedUserId"/>	
	<html:hidden name="projectForm" property="showRepositoryUserPwd"/>
				
	<plandora-html:shortcut name="projectForm" property="goToProject" fieldList="id"/>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.project"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="130">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.projName"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="projectForm" property="name" styleClass="textBox" size="30" maxlength="30" />
	  </td>
      <td>&nbsp;</td>
	</tr>
    <tr class="pagingFormBody">	
       <td>&nbsp;</td>
       <td class="formTitle"><bean:message key="label.projDesc"/>:&nbsp;</td>    
       <td class="formBody">
         <html:textarea name="projectForm" property="description" styleClass="textBox" cols="85" rows="5" />
       </td>
       <td>&nbsp;</td>
	</tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
      <td class="formBody">
	   		<html:checkbox property="allowBillable" name="projectForm" >
	   			<bean:message key="label.projAllowBillable"/>
	   		</html:checkbox>      
      </td>
      <td>&nbsp;</td>	  
    </tr>
	</table>
	       
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="130" class="formTitle"><bean:message key="label.projParent"/>:&nbsp;</td>
      <td width="230" class="formBody">
		<html:select name="projectForm" property="parentProject" styleClass="textBox">
			<html:options collection="parentPrjList" property="id" labelProperty="name" filter="false"/>
		</html:select>      
      </td>
      <td width="130" class="formTitle"><bean:message key="label.projStatus"/>:&nbsp;</td>
      <td width="250" class="formBody">
		<html:select name="projectForm" property="projectStatus" styleClass="textBox">
			<html:options collection="parentPrjStatusList" property="id" labelProperty="name" filter="false"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.canAlloc"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="projectForm" property="canAlloc" styleClass="textBox">
			<html:options collection="canAllocList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td class="formTitle"><bean:message key="label.projEstimClosure"/>:&nbsp;</td>
      <td class="formBody">
      		<plandora-html:calendar name="projectForm" property="estimatedClosureDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.projBudget"/>:&nbsp;</td>
      <td class="formBody">
      	<bean:write name="projectForm" property="budgetCurrencySymbol" filter="false" />&nbsp;
      	<html:text name="projectForm" property="budget" styleClass="textBox" size="15" maxlength="20" />
      </td>
      <td>&nbsp;</td>
	  <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>    
        
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="5">
		  <plandora-html:metafield name="projectForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="130" forward="manageProject?operation=navigate" /> 
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>
    
    <tr class="gapFormBody">
      <td colspan="7">&nbsp;</td>
    </tr>	    
	</table>
	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="150">
			  <html:button property="save" styleClass="button" onclick="javascript:saveProject();">
				<bean:write name="projectForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('projectForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('projectForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
    
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.projectRes"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="120">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.projUserSearch"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="projectForm" property="userSearch" styleClass="textBox" size="20" /> 
      	<html:button property="search" styleClass="button" onclick="javascript:buttonClick('projectForm', 'searchUser');">
    	    &nbsp;<bean:message key="button.ok"/>&nbsp;
	    </html:button>    
	  </td>
      <td>&nbsp;</td>
	</tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.projResUser"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="projectForm" property="selectedUserId" styleClass="textBox">
			<html:options collection="UserList" property="id" labelProperty="nameWithCompany" filter="false"/>
		</html:select>            
      	<html:button property="addUser" styleClass="button" onclick="javascript:buttonClick('projectForm', 'addUser');">
			<bean:message key="button.addInto"/>
	    </html:button>    		
	  </td>
      <td>&nbsp;</td>
	</tr>    
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="formBody">
				<td>
					<plandora-html:ptable width="90%" name="allocList" frm="projectForm">
						  <plandora-html:pcolumn width="15%" property="username" title="label.userName"  decorator="com.pandora.gui.taglib.decorator.UserInfoDecorator" tag="projectForm"/>
						  <plandora-html:pcolumn property="name" title="label.name" />	
						  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.CustomerRoleDecorator" />
  						  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ResourceCapacityDecorator" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C1" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CUSTOMER_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C2" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.RESOURCE_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C3" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.LEADER_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C4" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.ALLOW_PRE_APPROVE_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C5" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.ALLOW_SEE_CUSTOMER_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C11" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CAN_SELF_ALLOC_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C13" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CAN_SEE_REPOSITORY_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C14" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CAN_SEE_INVOICE_COL %>" />						  						  						  
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C8" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.ALLOW_SEE_TECH_COMMENTS %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C9" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.ALLOW_SEE_DISCUSSION_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C10" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CAN_SEE_OTHER_REQS_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C12" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.CAN_OPEN_OTHEROWNER_REQS_COL %>" />
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C7" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.REQ_ACCEPT_CUSTOMER_COL %>" />	
						  <plandora-html:pcolumn width="4%" property="id" title="label.proj.grid.C6" decorator="com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator" tag="<%=ProjectGridCheckBoxDecorator.DISABLE_USER_COL %>" />
						  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="''" />
					</plandora-html:ptable>
				</td>
			</tr> 
			</table>
      </td>
      <td>&nbsp;</td>
	</tr>	
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>		
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td align="right">
    		<a class="gridLink" href="javascript:showHide('projectLegend');"><bean:message key="label.proj.grid.legend"/></a>  
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>		
    </table>
    
	<div id="projectLegend">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="120">&nbsp;</td>
	      <td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr><td class="tableCell"><bean:message key="label.proj.C1"/></td></tr> 
				<tr><td class="tableCell"><bean:message key="label.proj.C2"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C3"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C4"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C5"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C11"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C13"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C14"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C8"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C9"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C10"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C12"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C7"/></td></tr>
				<tr><td class="tableCell"><bean:message key="label.proj.C6"/></td></tr>
				</table>
	      </td>
	      <td>&nbsp;</td>
		</tr>
	    <tr class="gapFormBody">
	      <td colspan="4">&nbsp;</td>
	    </tr>
	    </table>			
	</div>
	<script>javascript:showHide('projectLegend');</script>	
	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="150">
			  <html:button property="save" styleClass="button" onclick="javascript:saveProject();">
				<bean:write name="projectForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('projectForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('projectForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 

	<logic:notEqual name="projectForm" property="htmlQualifications" value="">

		<div>&nbsp;</div>
		
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.project.qualification"/>
		</display:headerfootergrid>
	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">	
	    <tr class="gapFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="200">&nbsp;</td>
	      <td width="200">&nbsp;</td>
		  <td width="200">&nbsp;</td>
		  <td width="200">&nbsp;</td>
		  <td width="200">&nbsp;</td>
		  <td>&nbsp;</td>
		  <td width="10">&nbsp;</td>
	    </tr>	    
	
		<bean:write name="projectForm" property="htmlQualifications" filter="false" />
	
	    <tr class="gapFormBody">
	      <td colspan="6">&nbsp;</td>
	    </tr>	
	  	</table>
	
		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="150">
				  <html:button property="save" styleClass="button" onclick="javascript:saveProject();">
					<bean:write name="projectForm" property="saveLabel" filter="false"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('projectForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
		</display:headerfootergrid> 	
	
	</logic:notEqual>
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.project.repository"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">	
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="130">&nbsp;</td>
      <td width="230">&nbsp;</td>
	  <td width="130">&nbsp;</td>
	  <td width="250">&nbsp;</td>
	  <td>&nbsp;</td>
	  <td width="10">&nbsp;</td>
    </tr>	    
	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.projRepositoryClass"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="projectForm" property="repositoryClass" styleClass="textBox" onkeypress="javascript:changeRepository();" onchange="javascript:changeRepository();">
			<html:options collection="repositoryList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td colspan="3">&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

	<logic:equal name="projectForm" property="showRepositoryUserPwd" value="on">

	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.projRepositoryURL"/>:&nbsp;</td>
	      <td class="formBody" colspan="4">
			<html:text name="projectForm" property="repositoryURL" styleClass="textBox" size="83" maxlength="200" />
	      </td>
	      <td>&nbsp;</td>	  
	    </tr>
	 
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.projRepositoryUser"/>:&nbsp;</td>
	      <td class="formBody">
			<html:text name="projectForm" property="repositoryUser" styleClass="textBox" size="10" maxlength="30" />
	      </td>
	      <td class="formTitle"><bean:message key="label.projRepositoryPass"/>:&nbsp;</td>
	      <td class="formBody">
			<html:password name="projectForm" property="repositoryPass" styleClass="textBox" size="10" maxlength="30" />
	      </td>	  
	      <td>&nbsp;</td>	  
	      <td>&nbsp;</td>	  
	    </tr>
	    
	</logic:equal>
		
    <tr class="gapFormBody">
      <td colspan="7">&nbsp;</td>
    </tr>	

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td colspan="4" class="formBody"><b><bean:message key="title.project.policy"/></b></td>
      <td>&nbsp;</td>	  
    </tr>

    <tr class="gapFormBody">
      <td colspan="7">&nbsp;</td>
    </tr>	

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="formBody" colspan="3">
	   		<html:checkbox property="policyCheckEntityReference" name="projectForm" >
	   			<bean:message key="title.project.policy.4"/>
	   		</html:checkbox>      
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="formBody" colspan="3">
	   		<html:checkbox property="policyAllowOnlyOpenProj" name="projectForm" >
	   			<bean:message key="title.project.policy.1"/>
	   		</html:checkbox>      
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="formBody" colspan="3">
	   		<html:checkbox property="policyAllowOnlyResources" name="projectForm" >
	   			<bean:message key="title.project.policy.2"/>
	   		</html:checkbox>      
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

	<logic:equal name="projectForm" property="showRepositoryUserPwd" value="on">
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>
	      <td class="formBody" colspan="3">
		   		<html:checkbox property="policyCheckRepositorySource" name="projectForm" >
		   			<bean:message key="title.project.policy.3"/>
		   		</html:checkbox>      
	      </td>
	      <td>&nbsp;</td>
	      <td>&nbsp;</td>	  
	    </tr>
	</logic:equal>
	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="formBody" colspan="3">
	   		<html:checkbox property="policyMandatoryComment" name="projectForm" >
	   			<bean:message key="title.project.policy.5"/>
	   		</html:checkbox>      
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>	  
    </tr>

    <tr class="gapFormBody">
      <td colspan="7">&nbsp;</td>
    </tr>	
  	</table>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="150">
			  <html:button property="save" styleClass="button" onclick="javascript:saveProject();">
				<bean:write name="projectForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('projectForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.projectList"/>
	</display:headerfootergrid>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="projectList" scope="session" pagesize="10"  frm="projectForm">
				  <plandora-html:pcolumn sort="true" likeSearching="true" width="20%" property="name" title="label.projName" />			
				  <plandora-html:pcolumn sort="true" likeSearching="true" property="description" maxWords="40" title="label.projDesc" />
				  <plandora-html:pcolumn sort="true" likeSearching="true" comboFilter="true" width="20%" align="center" property="parentProject.name" title="label.projParent" />	
				  <plandora-html:pcolumn sort="true" width="8%" property="projectStatus.name" comboFilter="true" align="center" title="label.projStatus" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.ProjectGridEditDecorator" tag="'PRJ'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>
      	
	<display:headerfootergrid type="FOOTER">		
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('projectForm', 'refreshList');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		</tr>
		</table>  	
	</display:headerfootergrid> 

</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<script> 
  document.forms["projectForm"].name.focus(); 
</script>