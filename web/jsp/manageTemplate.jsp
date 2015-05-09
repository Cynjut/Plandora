<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">
	
	function clickNodeTemplate(nodeId, planningId) {
		edit(nodeId, 'templateForm', 'editTemplateNode');
	}
	    
</script>

<html:form action="manageTemplate"> 
	<html:hidden name="templateForm" property="operation"/>
	<html:hidden name="templateForm" property="id"/>
	<html:hidden name="templateForm" property="nodeId"/>
	<html:hidden name="templateForm" property="nodeRelatedTemplateId"/>
		
	<br>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td colspan="3">
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.manageTemplate"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="70">&nbsp; </td>
      <td width="100">&nbsp;</td>
      <td width="100">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.manageTemplate.name"/>:&nbsp;</td>
      <td colspan="3" class="formBody">
        <html:text name="templateForm" property="name" styleClass="textBox" size="50" maxlength="70"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.manageTemplate.enable"/>:&nbsp;</td>
      <td class="formBody">
			<html:select name="templateForm" property="enable" styleClass="textBox">
				<html:options collection="enableList" property="id" labelProperty="genericTag"/>
			</html:select>
      </td>
      <td class="formTitle"><bean:message key="label.manageTemplate.category"/>:&nbsp;</td>
      <td class="formBody">
			<html:select name="templateForm" property="category" styleClass="textBox">
				<html:options collection="categoryList" property="id" labelProperty="name"/>
			</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
  	</table>
  	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('templateForm', 'saveTemplate');">
				<bean:write name="templateForm" property="saveLabel" />
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('templateForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">      
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('templateForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>
		  </td>	                	  
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
		
  </td><td width="10">&nbsp;</td></tr>
  
   	
  <tr><td width="10">&nbsp;</td><td height="300" valign="top">
  
  
  	<!-- NODES-->
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.manageTemplate.nodes"/>
	</display:headerfootergrid>
  	      
    <logic:equal name="templateForm" property="nodeType" value="NONE">
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr height="235"><td class="formBody">&nbsp;</td></tr>
	  	</table>    
  	</logic:equal>

	<logic:notEqual name="templateForm" property="nodeType" value="NONE">

		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150" class="formTitle"><bean:message key="label.formApplyTaskTemplate.name"/>:&nbsp;</td>
	      <td class="formBody">
	        <html:text name="templateForm" property="nodeName" styleClass="textBox" size="50" maxlength="70"/>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.formApplyTaskTemplate.desc"/>:&nbsp;</td>
	      <td class="formBody">
         	<html:textarea name="templateForm" property="nodeDescription" styleClass="textBox" cols="65" rows="5" />
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.formApplyTaskTemplate.project"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="templateForm" property="nodeRelatedProjectId" styleClass="textBox">
				<html:options collection="projectList" property="id" labelProperty="name"/>
			</html:select>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageTemplate.nextNode"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="templateForm" property="nodeNextNodeId" styleClass="textBox">
				<html:options collection="templateNodeList" property="id" labelProperty="genericTag"/>
			</html:select>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    
	  	</table>
 	</logic:notEqual>
 
 	<logic:equal name="templateForm" property="nodeType" value="STEP">

		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.formApplyTaskTemplate.resource"/>:&nbsp;</td>
	      <td class="formBody">
         	<html:textarea name="templateForm" property="stepResourceList" styleClass="textBox" cols="65" rows="3" />
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="gapFormBody">
	      <td colspan="4">&nbsp;</td>
	    </tr>			    
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150" class="formTitle"><bean:message key="label.formApplyTaskTemplate.category"/>:&nbsp;</td>
	      <td class="formBody">
	      	<html:select name="templateForm" property="stepCategory" styleClass="textBox">
				<html:options collection="taskCategoryList" property="id" labelProperty="name"/>
			</html:select>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageTemplate.categoryRegEx"/>:&nbsp;</td>
	      <td class="formBody">
	        <html:text name="templateForm" property="stepCategoryRegex" styleClass="textBox" size="50" maxlength="70"/>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	  	</table> 	
  	</logic:equal>

    <logic:equal name="templateForm" property="nodeType" value="DECISION">

		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageTemplate.nextNodeIfFalse"/>:&nbsp;</td>
	      <td class="formBody">
		  	<html:select name="templateForm" property="decisionIfFalseNextNodeId" styleClass="textBox">
				<html:options collection="templateNodeList" property="id" labelProperty="genericTag"/>
			</html:select>	        
	      </td>
	      <td>&nbsp;</td>
	    </tr>		
	    <tr class="pagingFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150" class="formTitle"><bean:message key="label.formApplyTaskTemplate.question"/>:&nbsp;</td>
	      <td class="formBody">
	        <html:text name="templateForm" property="decisionQuestion" styleClass="textBox" size="50" maxlength="70"/>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	  	</table> 
  	</logic:equal>

  
  	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>			  
		  <logic:equal name="templateForm" property="showCommitRevert" value="off">		      
			  <td width="100">
				  <html:button property="addStep" styleClass="button" onclick="javascript:buttonClick('templateForm', 'addStep');">
					<bean:message key="label.manageTemplate.addnode"/>
				  </html:button>    
			  </td>
			  <logic:equal name="templateForm" property="showDecision" value="on">
				  <td width="100">
					  <html:button property="addDecision" styleClass="button" onclick="javascript:buttonClick('templateForm', 'addDecision');">
						<bean:message key="label.manageTemplate.adddecision"/>
					  </html:button>    
				  </td>	  
			  </logic:equal>
			  <td>&nbsp;</td>
		   </logic:equal> 

		  <logic:equal name="templateForm" property="showCommitRevert" value="on">		      
			  <td>&nbsp;</td>
			  <td width="70">
				  <html:button property="commitNode" styleClass="button" onclick="javascript:buttonClick('templateForm', 'commitNode');">
					Commit
				  </html:button>    
			  </td>
			  <td width="70">
				  <html:button property="revertNode" styleClass="button" onclick="javascript:buttonClick('templateForm', 'revertNode');">
					<bean:message key="button.cancel"/>
				  </html:button>    
			  </td>	  
		   </logic:equal> 
		</tr></table>
	</display:headerfootergrid>   
  
  </td><td width="10">&nbsp;</td> <td width="400" height="300">
  
  
  	<!-- DIAGRAM-->
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.manageTemplate.diagram"/>
	</display:headerfootergrid>
	
  	<table width="90%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td class="formBody">
			<div id="workFlowDiagramDiv" style="width:385px; height:250px; overflow: scroll;">
				<img border="0" id="workFlowDiagram" src="../do/manageTemplate?operation=renderImage&bgcolor=EFEFEF" usemap="#workFlowDiagramMap" />
			</div>
			<map name="workFlowDiagramMap">
				<bean:write name="templateForm" property="htmlMap" filter="false"/>
			</map>
      </td>
    </tr>
    </table>

	<display:headerfootergrid type="FOOTER">
		<table width="90%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td><center>
			  <html:button property="reset" styleClass="button" onclick="centralizeDiagram();">
				<bean:message key="title.formApplyTaskTemplate.centralize"/>
			  </html:button>    
		  </center></td>
		</tr></table>
	</display:headerfootergrid>   
	
  </td><td width="10">&nbsp;</td></tr>
  

  <tr><td width="10">&nbsp;</td><td colspan="3">
  
  	<br/>
  	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.manageTemplate.list"/>
	</display:headerfootergrid>
	    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<display:table border="1" width="100%" name="templateList" scope="session" pagesize="10" requestURI="../do/manageTemplate?operation=navigate">
				  <display:column sort="true" likeSearching="true" property="name" title="label.manageTemplate.name" />			
				  <display:column width="15%" sort="true" property="finalDate" title="label.manageTemplate.disabled" />
				  <display:column width="15%" sort="true" property="category.name" title="label.manageTemplate.category" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'templateForm', 'editTemplate'" />
				  <display:column width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'templateForm', 'removeTemplate'" />
			</display:table>		
		</td>
	</tr> 
	</table>

	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('templateForm', 'refreshList');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">      
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('templateForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>
		  </td>	          
		</tr></table>
	</display:headerfootergrid>
    
  </td><td width="10">&nbsp;</td></tr>	
  </table>

</html:form>

<jsp:include page="footer.jsp" />  
<script> 
	with(document.forms["templateForm"]){	
		name.focus();
	}
	setTimeout('centralizeDiagram()', 500);
</script>