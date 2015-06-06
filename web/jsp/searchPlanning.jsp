<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
   	
   	<logic:equal name="searchPlanningForm" property="type" value="REQ">
		<title><bean:message key="title.searchRequirement"/></title>
	</logic:equal>
   	<logic:equal name="searchPlanningForm" property="type" value="ALL">
		<title><bean:message key="title.search"/></title>
	</logic:equal>
	
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" >

	<jsp:include page="validator.jsp" />
    <script language="JavaScript" src="../jscript/ajaxsync.js" type="text/JavaScript"></script>	
	<script language="JavaScript" src="../jscript/default.js" type=text/JavaScript></script>
	<script language="JavaScript">

	function noneSelected(){
		alert("<bean:message key="message.search.selectOne"/>")
	}
	
	function selectOption(){
		var reqId = "";
		with(document.forms["searchPlanningForm"]){

	    	//verify which radiobox was selected        	
			if (gridRadio) {
		    	if (gridRadio.length){
				   for(var i=0; i<gridRadio.length; i++){
					   if (gridRadio[i].checked){
						   reqId = gridRadio[i].value;
					   }	   
				   }
				} else {
				   if (gridRadio.checked){
		              reqId = gridRadio.value;
		           }else{
		              reqId = '';
		           }
				}
			}
			
			if (reqId != ''){
				opener.selectedPlanning(reqId);
				window.close();
			}else{
				noneSelected();
			}
		}
	}
	
</script>

<html:form  action="searchPlanning">
<html:hidden name="searchPlanningForm" property="operation"/>
<html:hidden name="searchPlanningForm" property="projectId"/>
		
<br>

<table width="100%" height="80%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<logic:equal name="searchPlanningForm" property="type" value="REQ">
			<bean:message key="title.searchRequirement"/>
		</logic:equal>
		<logic:equal name="searchPlanningForm" property="type" value="ALL">
			<bean:message key="title.search"/>
		</logic:equal>
	</display:headerfootergrid>
	    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="70">&nbsp; </td>
      <td width="110">&nbsp; </td>
      <td width="70">&nbsp; </td>
      <td width="70">&nbsp; </td>      
      <td width="10">&nbsp; </td>            
      <td width="110">&nbsp; </td>     
      <td width="70">&nbsp; </td>     
	  <td width="20">&nbsp; </td>
	  <td width="70">&nbsp; </td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
	  <logic:equal name="searchPlanningForm" property="type" value="REQ">
		      <td class="formTitle"><bean:message key="label.requester"/>:&nbsp;</td>
		      <td class="formBody">
				<html:select name="searchPlanningForm" property="customerId" styleClass="textBox">
					<option value=""><bean:message key="label.all"/></option>		
					<html:options collection="customerList" property="id" labelProperty="name" filter="false"/>
				</html:select>
		      </td>
		      <td class="formTitle"><bean:message key="label.requestStatus"/>:&nbsp;</td>
		      <td class="formBody">
				<html:select name="searchPlanningForm" property="reqStatus" styleClass="textBox">
					<option value="-1"><bean:message key="label.searchRequirement.allExceptFinishing"/></option>
					<html:options collection="reqStatusList" property="id" labelProperty="name" filter="false"/>
				</html:select>
		      </td>      		            
	  </logic:equal>
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.keyword"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="searchPlanningForm" property="keyword" styleClass="textBox" size="10" maxlength="40"/>
      </td>
      <td>&nbsp;</td>
	  <logic:equal name="searchPlanningForm" property="type" value="ALL">
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
	  </logic:equal>      
      <td>
      	<html:button property="search" styleClass="button" onclick="javascript:buttonClick('searchPlanningForm', 'search');">
			<bean:message key="button.search"/>
	    </html:button>          
      </td>            
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
	</table>
	
    <table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="10">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
    </table>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="searchPlanningList" pagesize="10" scope="session" frm="searchPlanningForm">		
				  <plandora-html:pcolumn width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.RadioGridDecorator" />
				  <plandora-html:pcolumn width="5%" property="id" title="grid.title.empty" />			
				  <plandora-html:pcolumn property="description" maxWords="18" title="label.requestDesc" />
				  <logic:equal name="searchPlanningForm" property="type" value="REQ">
				     <plandora-html:pcolumn width="15%" property="suggestedDate" align="center" title="label.requestSuggestedDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;0" />
				     <plandora-html:pcolumn width="7%" property="requester.username" align="center" title="label.requester" />
				  </logic:equal>
				  <logic:equal name="searchPlanningForm" property="type" value="ALL">
				  	<plandora-html:pcolumn width="15%" property="type" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridPlanningTypeDecorator" />
				  </logic:equal>
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>
    
    <table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="gapFormBody">
		<td width="10">&nbsp;</td>
		<td>&nbsp;</td>
		<td width="10">&nbsp;</td>
	</tr>
    </table>
	
	<display:headerfootergrid type="FOOTER">	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="70">   
			  <logic:equal name="searchPlanningForm" property="hasOptionsList" value="filled">
				  <html:button property="ok" styleClass="button" onclick="javascript:selectOption();">
					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="button.ok"/>&nbsp;&nbsp;&nbsp;
				  </html:button>
			  </logic:equal>
			  <logic:equal name="searchPlanningForm" property="hasOptionsList" value="empty">
				  <html:button property="ok" styleClass="button" onclick="javascript:noneSelected();">
					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="button.ok"/>&nbsp;&nbsp;&nbsp;
				  </html:button>
			  </logic:equal>	      
		  </td>
		  <td width="70">      
			  <html:button property="cancel" styleClass="button" onclick="javascript:window.close();">
				<bean:message key="button.cancel"/>
			  </html:button>    
		  </td>      
		  <td>&nbsp;</td>      
		</tr></table>  	
  	</display:headerfootergrid> 
	
	
</td><td width="20">&nbsp;</td>
</tr>
<tr><td colspan="3" height="50%">&nbsp;</td></tr>
</table>

</html:form>

<script>
	document.forms["searchPlanningForm"].keyword.focus();
</script> 