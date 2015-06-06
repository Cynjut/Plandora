<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<%@ page import="com.pandora.PreferenceTO"%>

<script language="javascript">

	function showIconTask(argId){
		var el = document.getElementById( argId );
		if (el!=null) {
			el.style.display =  'block'; 
			behidden = false;  
		}	
	}
		
	function hideIconTask(argId){
		var el = document.getElementById( argId );
		if (el!=null) {
			el.style.display =  'none' ; 
			behidden = true ;  
		}		
	}
	
	function editPostTask(argId){
   		var splitResult = argId.split("-");
   		if (splitResult.length == 3) {
		    displayMessage("../do/showAgilePanelTask?operation=showEditPopup&requirementId=&taskId=" + splitResult[0] + "&resourceId=" + splitResult[1] , 480, 310);
		}
	}

	function newPostTask(reqId){
	    displayMessage("../do/showAgilePanelTask?operation=showEditPopup&taskId=&requirementId=" + reqId, 480, 310);
	}
	
	function removePostTask(argId){
   		var splitResult = argId.split("-");
   		if (splitResult.length == 3) {
		    //displayMessage("../do/refuse?operation=prepareForm&forwardAfterRefuse=goToAgileForm&refuseType=TSK&refusedId=" + splitResult[0], 475, 220);
		    window.location = "../do/showAgilePanel?operation=removeResTask&resourceId=" + splitResult[1] + "&taskId=" + splitResult[0];
		}
	}

	function removeReq(argId){
   		with(document.forms["agilePanelForm"]){	
			if ( confirm("<bean:message key="message.agilePanelForm.removeReq"/>")) {
		 		window.location = "../do/showAgilePanel?operation=removeReq&reqId=" + argId;
        	}    	
		}
	}

	function showNewReqForm(){
		 with(document.forms["agilePanelForm"]){
		    var it = iterationSelected.value;
		    var pr = projectId.value;
		    displayMessage("../do/showAgilePanelReq?operation=prepareForm&iteration=" + it + "&reqProjectId=" + pr , 480, 230);		    
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
	
	function showMaximizedGadget(gagId){
		with(document.forms["agilePanelForm"]){
   			operation.value = "navigate";
   			maximizedGadgetId.value = gagId;
			submit();
   		}	
	}
		
</script>

<html:form  action="/showAgilePanel">
	<html:hidden name="agilePanelForm" property="projectId"/>
	<html:hidden name="agilePanelForm" property="operation"/>
	<html:hidden name="agilePanelForm" property="maximizedGadgetId"/>
		
	<plandora-html:shortcut name="agilePanelForm" property="goToAgileForm" fieldList="projectId"/>

	<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	  	
	<br>

<logic:present name="agilePanelForm" property="gadgetHtmlBody">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td>
</logic:present>

		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.agilePanelForm"/>  <bean:write name="agilePanelForm" property="projectName" filter="false"/>
		</display:headerfootergrid>
	  	
	  	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="70">&nbsp;</td>
	      <td width="270">&nbsp;</td>
	      <td width="100">&nbsp;</td>      
	      <td>&nbsp;</td>
	      <td width="10">&nbsp;</td>
	    </tr>		  	
	    <tr class="formBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.agilePanelForm.iteration"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="agilePanelForm" property="iterationSelected" styleClass="textBox">
				<html:options collection="iterationList" property="id" labelProperty="name" filter="false"/>
			</html:select>
	      </td>
	      <td class="formTitle"><bean:message key="label.agilePanelForm.group"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="agilePanelForm" property="groupBy" styleClass="textBox">
				<html:options collection="groupByList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>
	      </td>
	      <td>&nbsp;</td>      
	    </tr>
	    <tr class="formBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.agilePanelForm.category"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="agilePanelForm" property="categorySelected" styleClass="textBox">
				<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>
	      </td>
	      <td>&nbsp;</td>
		  <td>&nbsp;</td>	      
	      <td>&nbsp;</td>      
	    </tr>
	    <tr class="gapFormBody">
	      <td colspan="6">&nbsp;</td>
	    </tr>	   		
	    <tr class="formBody">
	      <td>&nbsp;</td>	  
	      <td class="formBody" colspan="2">
		   		<html:checkbox property="hideOldIterations" name="agilePanelForm" >
		   			<bean:message key="label.agilePanelForm.filter5"/>
		   		</html:checkbox>
	      </td>
	      <td class="formBody" colspan="2">
		   		<html:checkbox property="hideCancelTasks" name="agilePanelForm" >
		   			<bean:message key="label.agilePanelForm.filter2"/>
		   		</html:checkbox>      
		  </td>
		  <td>&nbsp;</td>
	    </tr>
	    <tr class="formBody">
	      <td>&nbsp;</td>
	      <td class="formBody" colspan="2">
		   		<html:checkbox property="hideFinishedReq" name="agilePanelForm" >
		   			<bean:message key="label.agilePanelForm.filter1"/>
		   		</html:checkbox>      
	      </td>
		  <td class="formBody" colspan="2">
		   		<html:checkbox property="showChart" name="agilePanelForm" >
		   			<bean:message key="label.agilePanelForm.filter4"/>
		   		</html:checkbox>      		  
		  </td>
	      <td>&nbsp;</td>      
	    </tr>
	    <tr class="formBody">
	      <td>&nbsp;</td>
	      <td class="formBody" colspan="4">
		   		<html:checkbox property="hideTasksWithoutReq" name="agilePanelForm" >
		   			<bean:message key="label.agilePanelForm.filter3"/>
		   		</html:checkbox>      
	      </td>
	      <td>&nbsp;</td>      
	    </tr>	
	    <tr class="gapFormBody">
	      <td colspan="6">&nbsp;</td>
	    </tr>
	    <tr class="gapFormBody">
	      <td colspan="6">&nbsp;</td>
	    </tr>	   				
		</table>  	
		
		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('agilePanelForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="newreq" styleClass="button" onclick="javascript:showNewReqForm();">
					 <bean:message key="title.agilePanelForm.newReq"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			<logic:notPresent name="agilePanelForm" property="gadgetHtmlBody">	      
			  <td width="100">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('agilePanelForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
			  </td>	      
			</logic:notPresent>
			</tr></table>  	
		</display:headerfootergrid>
		
		
<logic:present name="agilePanelForm" property="gadgetHtmlBody">
	</td>

	<td width="10">&nbsp;</td>
	
	<td width="400">

    	<div class="rndborder" style="width:100%">
	    	<b class="b1"></b><b class="b2"></b><b class="b3"></b><b class="b4"></b>
	    	<div class="paneltitle">
				<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>
				  <bean:write name="agilePanelForm" property="gadgetIconBody" filter="false" />
				</tr></table>
	    	</div>
			
			<div class="gridcontent">
		
				<table width="97%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<bean:write name="agilePanelForm" property="gadgetHtmlBody" filter="false" />
				</tr>
				</table>
			</div>
			
			<div class="panelfooter">
				<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>
				  <td width="100">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('agilePanelForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
				  </td>
				</tr></table>  			
	    	</div>
			<b class="b4"></b><b class="b3"></b><b class="b2"></b><b class="b1"></b>
		</div>
		
	</td>	
	</tr></table>
</logic:present>	

	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="label.agilePanelForm.reqPanel"/>
	</display:headerfootergrid>
	    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="reqTaskBoardList" scope="session" pagesize="0">
				<display:column property="id" width="20%" sort="true" maxWords="<%=PreferenceTO.LIST_NUMWORDS%>" title="label.agilePanelForm.grid.req" decorator="com.pandora.gui.taglib.decorator.AgilePanelReqDecorator" />			
				<display:column property="priority" width="7%" sort="true" align="center" title="label.requestPriority" description="label.requestPriority" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_PRIOR%>" decorator="com.pandora.gui.taglib.decorator.RequirementGridPriorityDecorator" />			
				<display:column property="id" width="7%" sort="true" align="center" title="label.agilePanelForm.iteration" description="label.agilePanelForm.grid.iteration.desc" visibleProperty="<%=PreferenceTO.HOME_REQLIST_SW_ITERA%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelIterationDecorator" />
	
		        <logic:equal name="agilePanelForm" property="groupBy" value="1">
					<display:column property="id" align="center" title="label.agilePanelForm.grid.todo" description="label.agilePanelForm.grid.todo.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_TODO%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="TODO" />
					<display:column property="id" align="center" title="label.agilePanelForm.grid.progress" description="label.agilePanelForm.grid.progress.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_PROG%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="PROGRESS" />
					<display:column property="id" align="center" title="label.agilePanelForm.grid.done" description="label.agilePanelForm.grid.done.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_DONE%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="DONE" />
				</logic:equal>			
				<logic:equal name="agilePanelForm" property="groupBy" value="2">
					<display:column property="id" align="center" title="label.agilePanelForm.grid.range1" description="label.agilePanelForm.grid.range1.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_RANGE1%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="EST_RANGE_1" />
					<display:column property="id" align="center" title="label.agilePanelForm.grid.range2" description="label.agilePanelForm.grid.range2.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_RANGE2%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="EST_RANGE_2" />
					<display:column property="id" align="center" title="label.agilePanelForm.grid.range3" description="label.agilePanelForm.grid.range3.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_RANGE3%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="EST_RANGE_3" />					
				</logic:equal>
				<logic:equal name="agilePanelForm" property="groupBy" value="3">
					<display:column property="id" align="center" title="label.agilePanelForm.grid.assig1" description="label.agilePanelForm.grid.assig1.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_ASSIG1%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="ASSIG_1" />
					<display:column property="id" align="center" title="label.agilePanelForm.grid.assig2" description="label.agilePanelForm.grid.assig2.desc" visibleProperty="<%=PreferenceTO.AGILE_PANEL_ASSIG2%>" decorator="com.pandora.gui.taglib.decorator.AgilePanelTaskDecorator" tag="ASSIG_2" />				
				</logic:equal>
				<logic:equal name="agilePanelForm" property="groupBy" value="4">
					<display:resourceTaskcolumn property="id" align="center" />			
				</logic:equal>
			</display:table>
		</td>
	</tr> 
	</table>
      		
	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('agilePanelForm', 'refresh');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td width="30">&nbsp;</td>      
		  <td>&nbsp;</td>
		  <td width="50" align="right">
			<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('agilePanelForm', 'backward');">
				<bean:message key="button.backward"/>
			</html:button>    
		  </td>      
		</tr></table>  	
	</display:headerfootergrid>
			  	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />

<script> 
   	with(document.forms["agilePanelForm"]){	
		if (maximizedGadgetId.value!=null && maximizedGadgetId.value != "" ) {
			displayMessage('../do/showGadgetProperty?operation=maximize&forwardAfterSave=..%2Fdo%2FshowAgilePanel%3Foperation%3DcloseMaximizeGadget&gagid=' + maximizedGadgetId.value, 700, 500);
			maximizedGadgetId.value = ""; 
		}		
	}
</script> 