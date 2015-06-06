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
         if ( confirm("<bean:message key="message.confirmRemoveRisk"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function callProjectEditForm(){
    	 with(document.forms["riskForm"]){
    		window.location = "../do/manageProject?operation=editProject&id=" + projectId.value;
		 }         
     }
     
    function openRiskHistPopup(id) {
	    var pathWindow ="../do/manageHistRisk?operation=prepareForm&riskId=" + id;
		window.open(pathWindow, 'riskHist', 'width=1024, height=235, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
    }
     
    function selectedPlanning(planId){
    	with(document.forms["riskForm"]){
			linkRelation.value = planId;
		}
	}
    
    function removeRelation(operat, planningId, relatedId, fwd, listKey) {
		window.location = "../do/manageRisk?operation=" + operat + "&linkRelation=" + relatedId + "&SOURCE_ENTITY_ID=" + planningId + "&RELATION_FORWARD=" + fwd + "&COLLECTION_KEY=" + listKey;	
    }
     
	function gridComboFilterRefresh(url, param, cbName){
		javascript:buttonClick('riskForm', 'refresh');
	}

	function gridTextFilterRefresh(url, param, fldName){
		javascript:buttonClick('riskForm', 'refresh');
	}
	
	function saveRisk(){
		var status = document.getElementById("status").value;
		if(status == 2){
			displayMessage("../do/manageRisk?operation=showConfirmation", 480, 130);
		}else{
			javascript:buttonClick('riskForm', 'updateOrInsertRisk');
		}
	}
     
	function saveRiskWithIssue(){
		document.getElementById("create_issue").value = "on";
		javascript:buttonClick('riskForm', 'updateOrInsertRisk');
	}
	
	function saveOnlyRisk(){
		document.getElementById("create_issue").value = "off";
		javascript:buttonClick('riskForm', 'updateOrInsertRisk');
	}
	
</script>

<html:form action="manageRisk">
	<html:hidden name="riskForm" property="operation"/>
	<html:hidden name="riskForm" property="id"/>	
	<html:hidden name="riskForm" property="projectId"/>
	<html:hidden name="riskForm" property="genericTag"/>	
	<html:hidden name="riskForm" property="showIssueConfirmation"/>	
	<input type="hidden" name="create_issue" id="create_issue" value="off"/>
		
	<plandora-html:shortcut name="riskForm" property="goToRiskForm" fieldList="projectId"/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formRisk"/>
	</display:headerfootergrid>
	  	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.PRJ"/>:&nbsp;</td>
      <td width="400" class="formBody">
        <bean:write name="riskForm" property="projectName" filter="false"/>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formRisk.name"/>:&nbsp;</td>
      <td width="400" class="formBody">
        <html:text name="riskForm" property="name" styleClass="textBox" size="50" maxlength="50"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.description"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="riskForm" property="description" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>


    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.type"/>:&nbsp;</td>
      <td class="formBody">
   		<html:radio property="riskType" value="0" />&nbsp;<bean:message key="label.formRisk.type.0"/>
   		&nbsp;&nbsp;&nbsp;&nbsp;
   		<html:radio property="riskType" value="1" />&nbsp;<bean:message key="label.formRisk.type.1"/>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.category"/>:&nbsp;</td>
      <td class="formBody">
	  		<html:select name="riskForm" property="categoryId" styleClass="textBox">
	             <html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.responsible"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="riskForm" property="responsible" styleClass="textBox" size="40" maxlength="40"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    </table>    
    
    
    <table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formRisk.probability"/>:&nbsp;</td>
      <td width="180" class="formBody">
	  		<html:select name="riskForm" property="probability" styleClass="textBox">
	             <html:options collection="probabilityList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>
      </td>
      <td width="60" class="formTitle"><bean:message key="label.formRisk.impact"/>:&nbsp;</td>
      <td width="100" class="formBody">
	  		<html:select name="riskForm" property="impact" styleClass="textBox">
	             <html:options collection="impactList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>
      </td>
      <td width="70" class="formTitle"><bean:message key="label.formRisk.tendency"/>:&nbsp;</td>      
      <td class="formBody">
	  		<html:select name="riskForm" property="tendency" styleClass="textBox">
	             <html:options collection="tendencyList" property="id" labelProperty="genericTag" filter="false"/>
			</html:select>
      </td>
      <td width="10" >&nbsp;</td>
    </tr>
    </table>    


	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formRisk.strategy"/>:&nbsp;</td>
      <td width="400" class="formBody">
   		<html:textarea name="riskForm" property="strategy" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.contingency"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="riskForm" property="contingency" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.comment"/>:&nbsp;</td>
      <td class="formBody">
   		<html:textarea name="riskForm" property="riskComment" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.status"/>:&nbsp;</td>      
      <td class="formBody">
	  		<html:select name="riskForm" property="status" styleId="status" styleClass="textBox">
	             <html:options collection="riskStatusList" property="id" labelProperty="name" filter="false"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formRisk.impact.title"/>:&nbsp;</td>      
      <td class="formBody">
			<html:checkbox name="riskForm" property="costImpact" styleClass="textBox"/><bean:message key="label.formRisk.impact.cost"/>&nbsp;&nbsp;
			<html:checkbox name="riskForm" property="qualityImpact" styleClass="textBox"/><bean:message key="label.formRisk.impact.qual"/>&nbsp;&nbsp;
			<html:checkbox name="riskForm" property="scopeImpact" styleClass="textBox"/><bean:message key="label.formRisk.impact.scop"/>&nbsp;&nbsp;
			<html:checkbox name="riskForm" property="timeImpact" styleClass="textBox"/><bean:message key="label.formRisk.impact.time"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formPlanning.visibility"/>:&nbsp;</td>      
      <td class="formBody">
		<html:select name="riskForm" property="visible" styleClass="textBox">
			<html:options collection="visibilityList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td>&nbsp;</td>
    </tr> 	    	    
    		    	    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td colspan="2">
		  <plandora-html:metafield name="riskForm" collection="metaFieldList" styleTitle="formTitle" styleBody="formBody" styleForms="textBox" titleWidth="150" forward="manageRisk?operation=navigate" /> 
	  </td>		  
      <td>&nbsp;</td>	  
    </tr>
    
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr> 
    </table>

    <logic:notEqual name="riskForm" property="id" value="">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
    	    <td width="10">&nbsp;</td>
        	<td width="150" class="formTitle">&nbsp;</td>
			<td>
				<table width="65%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
				  	<plandora-html:relationship name="riskForm" entity="<%=PlanningRelationTO.ENTITY_RISK%>" property="id" projectProperty="projectId" collection="riskRelationshipList" forward="showRisk" removeFunction="removeRelation"/> 	
				</table> 
			</td>
			<td width="10">&nbsp;</td>
		</tr>
		<tr class="gapFormBody">
		  <td colspan="4">&nbsp;</td>
		</tr> 		
		</table>
	</logic:notEqual>

	<display:headerfootergrid type="FOOTER">			
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:saveRisk();">
				<bean:write name="riskForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('riskForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="150">      
			  <html:button property="projectForm" styleClass="button" onclick="javascript:callProjectEditForm();">
				 <bean:message key="label.formRisk.back.project"/>
			  </html:button>    
		  </td>      
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('riskForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr>
	  </table>
  	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.riskList"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="riskList" scope="session" pagesize="6" frm="riskForm" >
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridMindMapLinkDecorator" />			
				  <plandora-html:pcolumn sort="true" property="name" likeSearching="true" title="label.formRisk.name" />
				  <plandora-html:pcolumn sort="true" width="15%" align="center" property="project.name" title="label.formRisk.project" description="label.formRisk.project.desc" visibleProperty="<%=PreferenceTO.RSK_SW_PROJECT%>" />
				  <plandora-html:pcolumn sort="true" width="10%" align="center" comboFilter="true" property="riskType" title="label.formRisk.type" description="label.formRisk.type" visibleProperty="<%=PreferenceTO.RSK_SW_TYPE%>" decorator="com.pandora.gui.taglib.decorator.RiskTypeGridDecorator" />				  
				  <plandora-html:pcolumn sort="true" width="10%" align="center" comboFilter="true" property="status.name" title="label.formRisk.status" description="label.formRisk.status.desc" visibleProperty="<%=PreferenceTO.RSK_SW_STATUS%>" />
				  <plandora-html:pcolumn sort="true" width="5%" align="center" property="probability" title="label.formRisk.probability" description="label.formRisk.probability" visibleProperty="<%=PreferenceTO.RSK_SW_PROB%>" tag="PROB" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn sort="true" width="5%" align="center" property="impact" title="label.formRisk.impact" description="label.formRisk.impact" visibleProperty="<%=PreferenceTO.RSK_SW_IMPACT%>" tag="IMPA" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn sort="true" width="5%" align="center" property="tendency" title="label.formRisk.tendency" description="label.formRisk.tendency" visibleProperty="<%=PreferenceTO.RSK_SW_TENDENCY%>" tag="TEND" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn width="5%" align="center" property="costImpact" title="label.formRisk.impact.cost" description="label.formRisk.impact.cost" visibleProperty="<%=PreferenceTO.RSK_SW_IMP_COST%>" tag="I_COST" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn width="5%" align="center" property="qualityImpact" title="label.formRisk.impact.qual" description="label.formRisk.impact.qual" visibleProperty="<%=PreferenceTO.RSK_SW_IMP_QUAL%>" tag="I_QUAL" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn width="5%" align="center" property="scopeImpact" title="label.formRisk.impact.scop" description="label.formRisk.impact.scop" visibleProperty="<%=PreferenceTO.RSK_SW_IMP_SCOP%>" tag="I_SCOP" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn width="5%" align="center" property="timeImpact" title="label.formRisk.impact.time" description="label.formRisk.impact.time" visibleProperty="<%=PreferenceTO.RSK_SW_IMP_TIME%>" tag="I_TIME" decorator="com.pandora.gui.taglib.decorator.RiskHistoryGridStatusDecorator" />
				  <plandora-html:pcolumn sort="true" width="15%" align="center" property="category.name" comboFilter="true" likeSearching="true" title="label.formRisk.category" description="label.formRisk.category" visibleProperty="<%=PreferenceTO.RSK_SW_CATEGORY%>" />
				  <plandora-html:pcolumn sort="true" width="20%" align="center" property="responsible" likeSearching="true" title="label.formRisk.responsible" description="label.formRisk.responsible" visibleProperty="<%=PreferenceTO.RSK_SW_RESPONSIBLE%>" />				  
				  <plandora-html:pcolumn sort="true" width="5%" align="center" property="visible" likeSearching="true" title="label.formPlanning.visibility" description="label.formPlanning.visibility" visibleProperty="<%=PreferenceTO.PLA_SW_VIS%>" decorator="com.pandora.gui.taglib.decorator.PlanningVisibilityGridDecorator"/>
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'riskForm', 'editRisk'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'riskForm', 'removeRisk'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDetailDecorator" tag="'RSK'" />				  
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>
		
	<display:headerfootergrid type="FOOTER">				
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('riskForm', 'refresh');">
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

