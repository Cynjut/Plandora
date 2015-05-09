<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="JavaScript">

	function showIssues(){
		var prjId;
		with(document.forms["viewBSCPanelForm"]){
			prjId = projectId.value;
		}	
		window.location = "../do/manageOccurrence?operation=prepareForm&projectId=" + prjId;	
	}

	function showRisk(){
		var prjId;
		with(document.forms["viewBSCPanelForm"]){
			prjId = projectId.value;
		}	
		window.location = "../do/manageRisk?operation=prepareForm&projectId=" + prjId;	
	}

	function showValues(){
		var prjId;
		with(document.forms["viewBSCPanelForm"]){
			prjId = projectId.value;
		}	
		window.location = "../do/viewBSC?operation=prepareForm&projectId=" + prjId;	
	}
	
	function showChart(kpi){
		window.location = "../do/viewBSCPanel?operation=showChart&kpiId=" + kpi;	
	}
	
</script>

<html:form  action="viewBSCPanel">
<html:hidden name="viewBSCPanelForm" property="operation"/>
<html:hidden name="viewBSCPanelForm" property="projectId"/>

<br>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td>

		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.viewBSCPanel"/>
		</display:headerfootergrid>
	
		<table width="98%" height="100" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
			<td width="10">&nbsp;</td>
			<td width="150">&nbsp;</td>
			<td>&nbsp;</td>
			<td width="10">&nbsp;</td>
		</tr>
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.viewBSCPanel.refDate"/>:&nbsp;</td>
			<td><plandora-html:calendar name="viewBSCPanelForm" property="refDate" styleClass="textBoxDisabled" /></td>
			<td>&nbsp;</td>			
		</tr> 
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td class="formTitle"><bean:message key="label.viewBSCPanel.category"/>:&nbsp;</td>
			<td>
		  		<html:select name="viewBSCPanelForm" property="categoryId" styleClass="textBox">
		             <html:options collection="categoryList" property="id" labelProperty="name"/>
				</html:select>		
			</td>
			<td>&nbsp;</td>		
		</tr>
	 	<tr class="formBody">
			<td>&nbsp;</td>
			<td class="formBody" colspan="2">
				<html:checkbox property="showOnlyCurrentProject" name="viewBSCPanelForm" >
					<bean:message key="label.viewBSCPanel.onlyproj"/>
				</html:checkbox>      
			</td>
			<td>&nbsp;</td>			
		</tr> 		 	
	 	<tr class="formBody">
			<td>&nbsp;</td>
			<td class="formBody" colspan="2">
				<html:checkbox property="showOnlyOpenedKpi" name="viewBSCPanelForm" >
					<bean:message key="label.viewBSCPanel.onlyOpen"/>
				</html:checkbox>      
			</td>
			<td>&nbsp;</td>			
		</tr> 		 	
		<tr class="formBody">
			<td>&nbsp;</td>	
	        <td colspan="2" class="formNotes"><bean:message key="label.message.kpi"/></td>
			<td>&nbsp;</td>			
		</tr> 	
		<tr class="gapFormBody">
			<td colspan="4">&nbsp;</td>
		</tr>
		</table>

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('viewBSCPanelForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="90">
					  <html:button property="buttonRisk" styleClass="button" onclick="javascript:showRisk();">
						<bean:message key="label.viewBSCPanel.risk"/>
					  </html:button>    
			  </td>
			  <td width="90">
					  <html:button property="buttonIssues" styleClass="button" onclick="javascript:showIssues();">
						<bean:message key="label.viewBSCPanel.issues"/>
					  </html:button>    
			  </td>	  
			  <td width="100">
					  <html:button property="buttonValues" styleClass="button" onclick="javascript:showValues();">
						<bean:message key="label.viewBSCPanel.valuesList"/>
					  </html:button>    
			  </td>      	        
			</tr></table>  			
		</display:headerfootergrid> 
		
	</td>

	<td width="10">&nbsp;</td>
	
	<td width="500">

    	<div class="rndborder" style="width:100%">
	    	<b class="b1"></b><b class="b2"></b><b class="b3"></b><b class="b4"></b>
	    	<div class="paneltitle">
				<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>
				  <bean:write name="viewBSCPanelForm" property="gadgetPropertyBody" filter="false" />
				</tr></table>
	    	</div>
			
			<div class="gridcontent">
		
				<table width="97%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<bean:write name="viewBSCPanelForm" property="gadgetHtmlBody" filter="false" />
				</tr>
				</table>
			</div>
			
			<div class="panelfooter">
				<table width="97%" border="0" cellspacing="0" cellpadding="0"><tr>
				  <td>&nbsp;</td>
				  <td width="100">
						<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('viewBSCPanelForm', 'backward');">
						<bean:message key="button.backward"/>
						</html:button>    
				  </td>
				</tr></table>  			
	    	</div>
			<b class="b4"></b><b class="b3"></b><b class="b2"></b><b class="b1"></b>
		</div>

	</td>	
	</tr></table>
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewBSCPanel.list"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr><td class="formBody">	
		<bean:write name="viewBSCPanelForm" property="bscTable" filter="false" />	
	</td></tr>    	
	</table>

	<display:headerfootergrid type="FOOTER">			
		&nbsp;
	</display:headerfootergrid>
	
	<div>&nbsp;</div>
  	
</html:form>

<jsp:include page="footer.jsp" />