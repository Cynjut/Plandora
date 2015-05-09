<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="JavaScript">
	
	function generate(rid, params){			
		var ro = document.forms["viewReportForm"].exportReportFormat.value;
		
		if (document.forms["viewReportForm"].exportReportFormat.value == 'PDF') {
			var content = '';
			
			for (x in params) {
				var p = document.getElementById(params[x]);
				if (p!=null) {
					content = content + params[x] + '=' + p.value + '&';
				}
			}
	
			tar = document.forms["viewReportForm"].target;
			window.open("../do/viewReport?operation=generate&reportId=" + rid + "&" + content + "&reportOutput=" + ro , "Report_" + rid, "width=700, height=600, location=no resizable=YES Directories=no Menubar=no Status=no Toolbar=no Titlebar=no");
			document.forms["viewReportForm"].target = "Report";  
			document.forms["viewReportForm"].target = tar;      
		
		} else {

			with(document.forms["viewReportForm"]){
				operation.value = "generate";
				reportId.value = rid;
				reportOutput.value = ro;
				submit();			
			}
		}
	 }
	 
     function changeProject(){
		 buttonClick("viewReportForm", "refreshProject");
     }
	
	
</script>

<html:form  action="viewReport">
<html:hidden name="viewReportForm" property="operation"/>
<html:hidden name="viewReportForm" property="reportId"/>
<html:hidden name="viewReportForm" property="reportOutput"/>

<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td valign="top">

	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.viewReport"/> <bean:write name="viewReportForm" property="projectName" />
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="10">&nbsp;</td>
		<td width="80">&nbsp;</td>
		<td width="200">&nbsp;</td>
		<td width="80">&nbsp;</td>
		<td width="200">&nbsp;</td>
		<td width="80">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
	<tr class="formBody">
		<td valign="top">&nbsp;</td>	
		<td class="formTitle"><bean:message key="label.viewReport.project"/>:&nbsp;</td>		
		<td>
			<html:select name="viewReportForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
				 <html:options collection="projectList" property="id" labelProperty="name"/>
			</html:select>		
		</td>		
        <td class="formTitle"><bean:message key="label.viewReport.category"/>:&nbsp;</td>
		<td>
	  		<html:select name="viewReportForm" property="categoryId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
	             <html:options collection="categoryList" property="id" labelProperty="name"/>
			</html:select>
		</td>
        <td class="formTitle"><bean:message key="label.viewReport.format"/>:&nbsp;</td>
		<td>
	  		<html:select name="viewReportForm" property="exportReportFormat" styleClass="textBox" >
	             <html:options collection="exportReportList" property="id" labelProperty="genericTag"/>
			</html:select>
		</td>
		<td>&nbsp;</td>			
	</tr> 	
	<tr class="gapFormBody">
		<td colspan="8">&nbsp;</td>
	</tr>
	</table>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120"> 
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('viewReportForm', 'refresh');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('viewReportForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
		</tr></table>  
   	</display:headerfootergrid> 

	<logic:notEmpty name="viewReportForm" property="reportTable">  	
	
		<div>&nbsp;</div>
		
		<display:headerfootergrid width="100%" type="HEADER">
			&nbsp;
		</display:headerfootergrid>
    		
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="gapFormBody">
			<td width="50">&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr class="formBody">
			<td>&nbsp;</td>
			<td>
				<bean:write name="viewReportForm" property="reportTable" filter="false" />	
			</td>
		</tr>
		</table>

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120"> 
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('viewReportForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
				  <td width="120">
					  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('viewReportForm', 'backward');">
						<bean:message key="button.backward"/>
					  </html:button>    
				  </td>
			</tr></table>  	
		</display:headerfootergrid>
		
		<div>&nbsp;</div>

	</logic:notEmpty>
	
</td><td width="20">&nbsp;</td>
</tr>
</table>

</html:form>

<jsp:include page="footer.jsp" />