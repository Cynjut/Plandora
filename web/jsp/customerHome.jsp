<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<%@ page import="com.pandora.PreferenceTO"%>

<jsp:include page="header.jsp" />

<script language="javascript">

     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmGiveUpRequest"/>")) {
	        removeWithoutConfirm(argId, argForm, argOperation);
         }
     }
     
     function saveRequirement(argForm, argOperation){
         with(document.forms["custReqForm"]){
			if (isPreApproveCheckbox.checked==true){
				if ( confirm("<bean:message key="message.confirmSaveRequest"/>")) {
				  	buttonClick(argForm, argOperation);
				}	  	
         	} else {
			  	buttonClick(argForm, argOperation);
         	}
         }             
     }
     
    function openPopup(id) {
	    var pathWindow ="../do/manageHistRequest?operation=prepareForm&reqId=" + id;
		window.open(pathWindow, 'reqHist', 'width=540, height=330, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }

    function hideClosedkCheck(){
         with(document.forms["custReqForm"]){
         	if (hideClosedRequests.value=="on"){
         		hideClosedRequests.value = "";
         	} else {
         		hideClosedRequests.value = "on";
         	}
         }             
    }
     
    function preApproveRequestCheck(){
         with(document.forms["custReqForm"]){
         	if (isPreApproveRequest.value=="on"){
         		isPreApproveRequest.value = "";
         	} else {
         		isPreApproveRequest.value = "on";
         	}
         }             
    }

    function edit(id, src){
    	if (src =="REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;
    	} else if (src =="ADJUST_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;
    	} else if (src =="REF_REQ") {
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=on&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=off&id=" + id;    		
    	} else if (src =="ADJUST_REQ_WOUT_PRJ") {    		
    		window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=on&isAdjustmentWithoutProj=on&readOnlyMode=off&id=" + id;
		} else if (src =="RO_MODE") {
			window.location = "../do/manageCustRequest?operation=editRequest&isReopenRequirement=off&showBackward=on&isAdjustment=off&isAdjustmentWithoutProj=off&readOnlyMode=on&id=" + id;		    		
    	}
    }
    
    function changeProject() {
	    repaintCheckBox();
	  	buttonClick("custReqForm", "refreshProject");    	    
    }

    function checkPreApproveTrigger(){
         with(document.forms["custReqForm"]){
         	isPreApproveCheckbox.checked = true;
         	isPreApproveRequest.value = "on";
         }                 
    }


	function goToForwardPage(){
	  	buttonClick("custReqForm", "refreshArtifacts");  
	}
	
	
    function repaintCheckBox() {
        with(document.forms["custReqForm"]){
         	var saValue = preApproveList.value;
	        saValue = saValue.toString();
	        var validChar = "1";
	        var comboIndex = projectRelated.selectedIndex ;       
	        
	        <logic:equal name="custReqForm" property="preApproveReqShow" value="on">
		        if (validChar.indexOf(saValue.substring(comboIndex, comboIndex+1)) > -1){
	        	   	estimTime.disabled="";	        
		        	estimTime.className="textBox";	        	
			        isPreApproveCheckbox.disabled="";
			        isPreApproveCheckbox.className="selectBox";	
			        selectedResource.disabled="";
			        selectedResource.className="selectBox";			        	        
		        } else {
		        	estimTime.disabled="true";
	   				estimTime.className="textBoxDisabled";
			        isPreApproveCheckbox.disabled="true";
			        isPreApproveCheckbox.className="textBoxDisabled";     
			        selectedResource.disabled="off";
			        selectedResource.className="textBoxDisabled";			          		        
		        }
	        </logic:equal>	        
         }
    }
		    
</script>

<html:form  action="manageCustRequest">
	<html:hidden name="custReqForm" property="operation"/>
	<html:hidden name="custReqForm" property="genericTag"/>
	<html:hidden name="custReqForm" property="id"/>
	<html:hidden name="custReqForm" property="iteration"/>		
	<html:hidden name="custReqForm" property="hideClosedRequests"/>
	<html:hidden name="custReqForm" property="isPreApproveRequest"/>
	<html:hidden name="custReqForm" property="showBackward"/>
	<html:hidden name="custReqForm" property="isAdjustment"/>
	<html:hidden name="custReqForm" property="preApproveList"/>
	<html:hidden name="custReqForm" property="readOnlyMode"/>
	<html:hidden name="custReqForm" property="canChangeRequester"/>
	<logic:equal name="custReqForm" property="isAdjustment" value="on">
		<html:hidden name="custReqForm" property="description"/>
	</logic:equal>
	<logic:equal name="custReqForm" property="isAdjustment" value="off">
		<logic:equal name="custReqForm" property="isRequesterLeader" value="on">	  
			<html:hidden name="custReqForm" property="deadlineDate"/>
		</logic:equal>			
	</logic:equal>
			
	<br>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	

	<display:headerfootergrid width="100%" type="HEADER">
		<logic:equal name="custReqForm" property="isAdjustment" value="off">
			<bean:message key="title.request"/>
		</logic:equal>      	
		<logic:equal name="custReqForm" property="isAdjustment" value="on">
			<bean:message key="title.request.adjustment"/>		
		</logic:equal>      
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="130">&nbsp;</td>
      <td width="100">&nbsp;</td>
      <td width="100">&nbsp;</td>
      <td width="60">&nbsp;</td>
      <td width="110">&nbsp;</td>
      <td>&nbsp;</td>     
      <td width="10">&nbsp;</td>
    </tr>	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requester"/>:&nbsp;</td>
      <td class="formBody">
		<logic:equal name="custReqForm" property="canChangeRequester" value="on">   
	  		<html:select name="custReqForm" property="requesterId" styleClass="textBox">
				<html:options collection="projCustomerList" property="id" labelProperty="username"/>
			</html:select>					 
		</logic:equal>    
		<logic:equal name="custReqForm" property="canChangeRequester" value="off">
			<html:hidden name="custReqForm" property="requesterId"/>    
        	<html:text name="custReqForm" property="requester" styleClass="textBoxDisabled" size="10" disabled="true"/>
		</logic:equal>    
      </td>

      <td class="formTitle"><bean:message key="label.reqNum"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="custReqForm" property="reqNum" styleClass="textBoxDisabled" size="6" disabled="true"/>
      </td>

      <td class="formTitle">*<bean:message key="label.requestSuggestedDate"/>:&nbsp;</td>
      <td class="formBody">
		  <logic:equal name="custReqForm" property="isAdjustment" value="off">
		  	  <plandora-html:calendar name="custReqForm" property="suggestedDate" styleClass="textBoxDisabled" />
		  </logic:equal>
		  <logic:equal name="custReqForm" property="isAdjustment" value="on">
			  <html:text name="custReqForm" property="suggestedDate" styleClass="textBoxDisabled" size="10" disabled="true"/>		  
		  </logic:equal>
      </td>
      
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requestProject"/>:&nbsp;</td>
      <td colspan="3" class="formBody">
		  <logic:equal name="custReqForm" property="isAdjustmentWithoutProj" value="on">
		  		<html:select name="custReqForm" property="projectRelated" styleClass="textBox" disabled="true">
					<html:options collection="projectList" property="id" labelProperty="name"/>
				</html:select>			
		  </logic:equal>
		  <logic:equal name="custReqForm" property="isAdjustmentWithoutProj" value="off">
		  		<html:select name="custReqForm" property="projectRelated" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
					<html:options collection="projectList" property="id" labelProperty="name"/>
				</html:select>
		  </logic:equal>
      </td>

	  <logic:equal name="custReqForm" property="isAdjustment" value="on">
		  <logic:equal name="custReqForm" property="isRequesterLeader" value="off">	  
		  	  <td class="formTitle"><b><bean:message key="label.requestdeadline"/></b>:&nbsp;</td>
	    	  <td class="formBody" colspan="2">
		  		  <plandora-html:calendar name="custReqForm" property="deadlineDate" styleClass="textBoxDisabled" />	      
		      </td>
		  </logic:equal>		      
	  </logic:equal>
	  
	  <logic:equal name="custReqForm" property="isAdjustment" value="off">
	  	<logic:equal name="custReqForm" property="preApproveReqShow" value="off">
	  	  <td class="formBody" colspan="3">&nbsp;</td>
	  	</logic:equal>
	  	<logic:equal name="custReqForm" property="preApproveReqShow" value="on">
	  	  <td class="formTitle"><bean:message key="label.manageTask.estTime"/>:&nbsp;</td>
	  	  <td class="formBody"><html:text name="custReqForm" property="estimTime" styleClass="textBox" size="4" maxlength="4" onchange="javascript:checkPreApproveTrigger();"/></td>
	  	  <td class="formBody">&nbsp;</td>
	  	</logic:equal>	  	
	  </logic:equal>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requestCategory"/>:&nbsp;</td>
      <td colspan="3" class="formBody">
	  		<html:select name="custReqForm" property="categoryId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
				<html:options collection="categoryList" property="id" labelProperty="name"/>
			</html:select>
      </td>
	  <logic:equal name="custReqForm" property="isAdjustment" value="on">
		  <td class="formBody" colspan="3">&nbsp;</td>	  	  
	  </logic:equal>
	  <logic:equal name="custReqForm" property="isAdjustment" value="off">
	  	<logic:equal name="custReqForm" property="preApproveReqShow" value="off">
	  	  	<td class="formBody" colspan="3">&nbsp;</td>
	  	</logic:equal>
	  	<logic:equal name="custReqForm" property="preApproveReqShow" value="on">
	  	  <td class="formTitle"><bean:message key="label.request.selRes"/>:&nbsp;</td>
	  	  <td class="formBody">
			<html:select name="custReqForm" property="selectedResource" styleClass="textBox" onkeypress="javascript:checkPreApproveTrigger();" onchange="javascript:checkPreApproveTrigger();">
				<html:options collection="projResList" property="id" labelProperty="name"/>
			</html:select>      
	  	  </td>
	  	  <td class="formBody">&nbsp;</td>
	  	</logic:equal>	  	
	  </logic:equal>
    </tr>            
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.requestPriority"/>:&nbsp;</td>
      <td colspan="3" class="formBody">
		<html:select name="custReqForm" property="priority" styleClass="textBox">
			<html:options collection="priorityList" property="id" labelProperty="genericTag"/>
		</html:select>      
      </td>
  	  <td class="formTitle">&nbsp;</td>
      <td class="formBody" colspan="2">&nbsp;</td>
    </tr>    
    </table>
    
    
    <table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="130" class="formTitle"><bean:message key="label.requestDesc"/>:&nbsp;</td>
      <td class="formBody">
		<logic:equal name="custReqForm" property="isAdjustment" value="off">
	      	<html:textarea name="custReqForm" property="description" styleClass="textBox" cols="85" rows="5" />
		</logic:equal>      	
		<logic:equal name="custReqForm" property="isAdjustment" value="on">
	      	<html:textarea name="custReqForm" property="description" styleClass="textBoxDisabled" readonly="true" cols="85" rows="5" />		
		</logic:equal>
      </td>
      <td width="10">&nbsp;</td>	  
    </tr>
    
    <logic:equal name="custReqForm" property="isReopenRequirement" value="on">
	    <table width="100%" border="0" cellspacing="0" cellpadding="0">    
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="130" class="formTitle">**<bean:message key="label.requestComment.reopen"/>:&nbsp;</td>
	      <td class="formBody">
	      	<html:textarea name="custReqForm" property="comment" styleClass="textBox" cols="85" rows="5" />
	      </td>
	      <td width="10">&nbsp;</td>	  
	    </tr>
    </logic:equal>    

    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td colspan="2">
		  <plandora-html:metafield name="custReqForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" forward="manageCustRequest?operation=refreshAfterMetaFieldUpdate"/> 
	  </td>		  
      <td width="10">&nbsp;</td>	  
    </tr>

	<logic:equal name="custReqForm" property="preApproveReqShow" value="on">    
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle">&nbsp;</td>
	      <td class="formBody">
		    <input type="checkbox" name="isPreApproveCheckbox" onclick="javascript:preApproveRequestCheck();"/><bean:message key="label.requestPreApprove"/>
	      </td>
	      <td>&nbsp;</td>
	    </tr>    
	</logic:equal>	    
	<logic:notEqual name="custReqForm" property="preApproveReqShow" value="on">
	    <input type="hidden" name="isPreApproveCheckbox" value=""/>
	</logic:notEqual>
	
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
  	</table>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
    <tr class="formNotes">
      <td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="label.requestSuggestedDate.obs"/></div></td>
      <td colspan="3">&nbsp;</td>
    </tr> 	    
  	</table>
			
    <logic:equal name="custReqForm" property="isReopenRequirement" value="on">  	
		<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
	    <tr class="formNotes">
	      <td><div>&nbsp;&nbsp;**&nbsp;<bean:message key="label.requestComment.obs"/></div></td>
	      <td colspan="3">&nbsp;</td>
	    </tr>	    
	  	</table>  	
    </logic:equal>  	

	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
	<tr class="pagingFormBody">
		<td width="20">&nbsp;</td>
		<td>
			<table width="80%" border="0" cellspacing="0" cellpadding="0">
				<tr class="gapFormBody">
					<td colspan="2">&nbsp;</td>
				</tr>		
			  	<plandora-html:attachment name="custReqForm" collection="attachmentList" removedForward="refreshCustAfterAttach"/> 	
			</table> 
		</td>
		<td width="10">&nbsp;</td>
	</tr>
	
	<logic:equal name="custReqForm" property="canSeeArtifacts" value="on">    
		<tr class="pagingFormBody">
			<td width="20">&nbsp;</td>
			<td>
				<table width="55%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:artifact name="custReqForm" property="reqNum" projectProperty="projectRelated" collection="repositoryList"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>
	</logic:equal>	
	
	<logic:equal name="custReqForm" property="canSeeDiscussion" value="on">    
		<tr class="pagingFormBody">
			<td width="20">&nbsp;</td>
			<td>
				<logic:notEqual name="custReqForm" property="id" value="">
					<plandora-html:discussiontopic name="custReqForm" collection="discussionTopicList" action="manageCustRequest" forward="manageCustRequest?operation=refreshAfterTopicDiscussion" />
				</logic:notEqual>
			</td>
			<td width="10">&nbsp;</td>
		</tr>
	</logic:equal>    
	</table> 
	
	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <logic:equal name="custReqForm" property="readOnlyMode" value="off">
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:saveRequirement('custReqForm', 'saveRequirement');">
					<bean:write name="custReqForm" property="saveLabel" />    	    
				  </html:button>    
			  </td>
		  </logic:equal>
		  <td width="120">	
			  <logic:equal name="custReqForm" property="isAdjustment" value="off">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('custReqForm', 'clear');">
					<bean:message key="button.new"/>
				  </html:button>    		    	  
			  </logic:equal>		  
			  <logic:equal name="custReqForm" property="isAdjustment" value="on">
				  <logic:equal name="custReqForm" property="isRequesterLeader" value="on">	
					 &nbsp;	  
				  </logic:equal>
				  <logic:equal name="custReqForm" property="isRequesterLeader" value="off">		  
					  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('custReqForm', 'clear');">
						<bean:message key="button.new"/>
					  </html:button>			  
				  </logic:equal>
			  </logic:equal>	        
		  </td>
		  <td>&nbsp;</td>
		  <td width="50" align="right">
			  <logic:equal name="custReqForm" property="showBackward" value="on">
				<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('custReqForm', 'backward');">
					<bean:message key="button.backward"/>
				</html:button>    
			  </logic:equal>
		  </td>      
		</tr></table>  	
  	</display:headerfootergrid> 
	

	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="label.requestList"/>
	</display:headerfootergrid>
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="requirementList" scope="session" pagesize="<%=PreferenceTO.HOME_REQULIST_NUMLINE%>" requestURI="../do/manageCustRequest?operation=navigate">
				<display:column sort="true" likeSearching="true" width="5%" property="id" title="label.gridReqNum" />			
	   		    <display:column sort="true" likeSearching="true" property="description" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.requestDesc" decorator="com.pandora.gui.taglib.decorator.RequirementGridHilight" />
				<display:column sort="true" likeSearching="true" width="7%" property="requester.username" align="center" title="label.showAllReqForm.grid.requester" />    					
				<display:column sort="true" width="12%" align="center" property="project.name" title="label.requestProject" description="label.requestProject" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PROJ%>" />
				<display:column sort="true" width="7%" align="center" property="priority" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />
			    <display:column sort="true" width="10%" property="category.name" align="center" title="label.manageTask.category" description="label.manageTask.category" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_CATEG%>" />
 			    <display:column sort="true" width="16%" align="center" property="requirementStatus.name" title="label.requestStatus" description="label.requestStatus" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_STAT%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridStatus" />
				<display:column sort="true" width="10%" property="iteration" align="center" title="label.occurrence.iteration" description="label.manageTask.grid.iteration.req.desc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_ITERA%>" decorator="com.pandora.gui.taglib.decorator.IterationLinkDecorator" tag="req_only"/> 			    
				<display:metafieldcolumn width="10%" property="id" align="center" description="label.manageOption.showMetaField" visibleProperty="<%=PreferenceTO.LIST_ALL_REQ_SW_META_FIELD%>" />
				<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridEditDecorator" />
				<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RequirementGridDeleteDecorator" tag="'custReqForm', 'giveUpRequest'" />
				<display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'REQ'" />
 			    <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.AttachmentGridDecorator" tag="REQ" />
			</display:table>					
		</td>
	</tr> 
	</table>
      		
	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('custReqForm', 'refreshRequest');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td width="30">&nbsp;</td>
		  <logic:equal name="custReqForm" property="isAdjustment" value="off">
			  <td class="textBoxOverTitleArea">	
				<input type="checkbox" name="hideClosedCheckbox" onclick="javascript:hideClosedkCheck();"/><bean:message key="label.requestHideClosed"/>
			  </td>
		  </logic:equal>
		  <logic:equal name="custReqForm" property="isAdjustment" value="on">
			  <td>&nbsp;</td>
		  </logic:equal>	  
		</tr></table> 
	</display:headerfootergrid> 	
  	
</td><td width="20">&nbsp</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<logic:equal name="custReqForm" property="isAdjustment" value="off">
	<script> 
        with(document.forms["custReqForm"]){	
			hideClosedCheckbox.checked=(hideClosedRequests.value=="on");
			description.focus(); 			
		}
	</script>  
</logic:equal>