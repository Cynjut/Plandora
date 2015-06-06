<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<%@ page import="com.pandora.PlanningRelationTO"%>
<%@ page import="com.pandora.PreferenceTO"%>


<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveOccurrence"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function changeSource(){
		 buttonClick("occurrenceForm", "refreshSource");
     }

     function removeKpiLink(kpiId, occId){
    	 with(document.forms["occurrenceForm"]){
    		operation.value = "removeKpiLink";
    		selectedKpi.value = kpiId;
    		id.value = occId;
    		submit();
		 }         
     }

     function callProjectEditForm(){
    	 with(document.forms["occurrenceForm"]){
    		window.location = "../do/manageProject?operation=editProject&id=" + projectId.value;
		 }         
     }
     
    function openOccHistPopup(id) {
	    var pathWindow ="../do/manageHistOccurrence?operation=prepareForm&occId=" + id;
		window.open(pathWindow, 'occHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
     
    function removeRelation(operat, planningId, relatedId, fwd, listKey) {
		window.location = "../do/manageOccurrence?operation=" + operat + "&linkRelation=" + relatedId + "&SOURCE_ENTITY_ID=" + planningId + "&RELATION_FORWARD=" + fwd + "&COLLECTION_KEY=" + listKey;	
    }
     
	function selectedPlanning(planId){
    	with(document.forms["occurrenceForm"]){
			linkRelation.value = planId;
		}
	}
     
	function gridComboFilterRefresh(url, param, cbName){
		javascript:buttonClick('occurrenceForm', 'refresh');
	}

	function gridTextFilterRefresh(url, param, fldName){
		javascript:buttonClick('occurrenceForm', 'refresh');
	}
     
	function occTableRemoveRow(argForm, rowId){
		with(document.forms[argForm]){
    		operation.value = 'occTableRemoveRow';
    		genericTag.value = rowId;
    		submit();
    	}         
	}

	function occTableAddRow(argForm, tableId){
		with(document.forms[argForm]){
			operation.value = "occTableAddRow";
	   		genericTag.value = tableId;
	    	submit();
	    }         
	}
          
</script>

<html:form action="manageOccurrence">
	<html:hidden name="occurrenceForm" property="operation"/>
	<html:hidden name="occurrenceForm" property="id"/>	
	<html:hidden name="occurrenceForm" property="genericTag"/>	
	<html:hidden name="occurrenceForm" property="projectId"/>
		
	<plandora-html:shortcut name="occurrenceForm" property="goToOccForm" fieldList="projectId"/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formOccurrence"/>
	</display:headerfootergrid>
	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.PRJ"/>:&nbsp;</td>
      <td width="400" class="formBody">
        <bean:write name="occurrenceForm" property="projectName" filter="false"/>
      </td>
      <td width="160">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formOccurrence.name"/>:&nbsp;</td>
      <td width="400" class="formBody">
        <html:text name="occurrenceForm" property="name" styleClass="textBox" size="70" maxlength="70"/>
      </td>
      <td width="160">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    </table>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
	  <td width="150" class="formTitle"><bean:message key="label.formOccurrence.source"/>:&nbsp;</td>
      <td width="180" class="formBody">
	      <logic:notEqual name="occurrenceForm" property="id" value="">
	      		<B><bean:write name="occurrenceForm" property="sourceName" filter="false"/></B>
	      		<html:hidden name="occurrenceForm" property="source"/>
	      </logic:notEqual>	  
	      <logic:equal name="occurrenceForm" property="id" value="">
				<html:select name="occurrenceForm" property="source" styleClass="textBox" onkeypress="javascript:changeSource();" onchange="javascript:changeSource();">
					<html:options collection="sourceList" property="id" labelProperty="genericTag" filter="false"/>
				</html:select>    
	      </logic:equal>
	  </td>      		            		      	      
      <td width="50" class="formTitle"><bean:message key="label.formOccurrence.status"/>:&nbsp;</td>
      <td>
	    	<bean:write name="occurrenceForm" property="statusComboHtml" filter="false"/>
	  </td>
      <td width="10">&nbsp;</td>
    </tr>
  	</table>  	
  	
    <table width="98%" border="0" cellspacing="0" cellpadding="0">    
	    <bean:write name="occurrenceForm" property="fieldsHtml" filter="false"/>
  	</table>      	

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="2">
		  <plandora-html:metafield name="occurrenceForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="150" forward="manageOccurrence?operation=navigate" /> 
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>	  
    </tr>
  	</table>      		
  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formPlanning.visibility"/>:&nbsp;</td>      
      <td class="formBody">
		<html:select name="occurrenceForm" property="visible" styleClass="textBox">
			<html:options collection="visibilityList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>    
  	</table>  	

    <logic:notEqual name="occurrenceForm" property="id" value="">
    
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
    	    <td width="10">&nbsp;</td>
        	<td width="150" class="formTitle">&nbsp;</td>
			<td>
				<table width="65%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:relationship name="occurrenceForm" entity="<%=PlanningRelationTO.ENTITY_OCCU%>" property="id" projectProperty="projectId" collection="occRelationshipList" forward="showOccurrence" removeFunction="removeRelation"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>
	    <tr class="gapFormBody">
	      <td colspan="4">&nbsp;</td>
	    </tr>
	    
	   	<logic:equal name="occurrenceForm" property="source" value="com.pandora.bus.occurrence.StrategicObjectivesOccurrence">
			<tr class="pagingFormBody">
	    	    <td>&nbsp;</td>
	        	<td class="formTitle">&nbsp;</td>
				<td>

				<table width="65%" border="0" cellspacing="0" cellpadding="0">
				  	<tr class="formBody">
						<td><img src="../images/relation.gif" border="0">&nbsp;&nbsp;<b><bean:message key="label.occurrence.so.kpi.title"/></b></td>
					</tr>
					<tr class="formBody">
						<td>
							<table class="table" width="100%" border="1" bordercolor="#10389C" cellspacing="1" cellpadding="2">
							  <tr class="tableRowHeader">
							     <th colspan="6">
							       <table border="0" cellspacing="0" cellpadding="0" width="100%">
							       <tr class="tableRowHeader">
							       		<td width="120" align="right"><bean:message key="label.occurrence.so.kpi.name"/>:</td>
							       		<td>
											<html:select name="occurrenceForm" property="selectedKpi" styleClass="textBox">
												<html:options collection="openIndicatorsList" property="id" labelProperty="name" filter="false"/>
												<html:options collection="closeIndicatorsList" property="id" labelProperty="name" styleClass="diffopt" filter="false"/>
											</html:select>      
							       		</td>
							       		<td width="100" align="right"><bean:message key="label.occurrence.so.kpi.weight"/>:</td>
							       		<td width="80">
											<html:select name="occurrenceForm" property="kpiWeight" styleClass="textBox">
												<html:options collection="kpiWeightList" property="id" labelProperty="genericTag" filter="false"/>
											</html:select>      
							       		</td>							       		
							       		<td width="30">
							       			<html:button property="linkKpiButton" styleClass="button" onclick="javascript:buttonClick('occurrenceForm', 'saveKpiLink');">
									    	    <bean:message key="button.ok" />
	      									</html:button>    
							       		</td>
							       </tr>
							       </table>
							     </th>
							  </tr>
							</table>
						</td>
					</tr>	
					<tr class="formBody"><td>
						<bean:write name="occurrenceForm" property="kpiListHtml" filter="false"/>					
					</td></tr>						
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
					
				</table></td>
				<td>&nbsp;</td>
			</tr>
		</logic:equal>
	        		
		</table>
					
	</logic:notEqual>
  	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('occurrenceForm', 'saveOccurrence');">
				<bean:write name="occurrenceForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('occurrenceForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="150">      
			  <html:button property="goToprojectForm" styleClass="button" onclick="javascript:callProjectEditForm();">
				 <bean:message key="label.formOccurrence.back.project"/>
			  </html:button>    
		  </td>      
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('occurrenceForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
   	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.occurrenceList"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="occurrenceList" scope="session" pagesize="6"  frm="occurrenceForm">
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="label.Id" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.OccurrenceColorGridDecorator" />
				  <plandora-html:pcolumn sort="true" width="15%" likeSearching="true" comboFilter="true" property="id" title="label.formOccurrence.source" description="label.formOccurrence.source" visibleProperty="<%=PreferenceTO.OCC_SW_SOURCE%>" decorator="com.pandora.gui.taglib.decorator.ProjectGridOccurrenceDecorator" />
				  <plandora-html:pcolumn sort="true" width="20%" align="center" property="project.name" comboFilter="true" title="label.formOccurrence.project" description="label.formOccurrence.project.desc" visibleProperty="<%=PreferenceTO.OCC_SW_PROJECT%>" />
				  <plandora-html:pcolumn sort="true" width="15%" likeSearching="true" property="id" title="label.formOccurrence.status"description="label.formOccurrence.status" visibleProperty="<%=PreferenceTO.OCC_SW_STATUS%>" decorator="com.pandora.gui.taglib.decorator.OccurrenceStatusDecorator" />
				  <plandora-html:pcolumn sort="true" property="name" likeSearching="true" title="label.grid.occurr.name" />
				  <plandora-html:pcolumn sort="true" width="5%" align="center" property="visible" likeSearching="true" title="label.formPlanning.visibility" description="label.formPlanning.visibility" visibleProperty="<%=PreferenceTO.PLA_SW_VIS%>" decorator="com.pandora.gui.taglib.decorator.PlanningVisibilityGridDecorator"/>
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'occurrenceForm', 'editOccurrence'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'occurrenceForm', 'removeOccurrence'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'OCC'" />				  
			</plandora-html:ptable>	
		</td>
	</tr> 
	</table>
		
	<display:headerfootergrid type="FOOTER">				
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
			<html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('occurrenceForm', 'refresh');">
				<bean:message key="button.refresh"/>
			</html:button>    
		</td>
		<td class="textBoxOverTitleArea">
			<html:checkbox property="hideClosedOccurrences" name="occurrenceForm" >
				<bean:message key="label.occurrence.hideClosed"/>
			</html:checkbox>
		</td>	   		
		</tr></table>  	
	</display:headerfootergrid>
		
  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["occurrenceForm"]){	
		name.focus(); 
	}
</script>