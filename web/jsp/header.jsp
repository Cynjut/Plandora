<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>

	<head>
		<title><bean:message key="header.title" /></title>
		<logic:present name="CURRENT_USER_SESSION" property="name">
			<meta http-equiv='Content-Type' content='text/html; charset=<bean:write name="CURRENT_USER_SESSION" property="encoding" filter="false"/>'>
		</logic:present>
		<meta http-equiv="Pragma" CONTENT="no-cache" />
		<meta http-equiv="Expires" CONTENT="-0" />
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link href="../css/ganttpanel.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />	
	</head>

	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" height="100%">

	<div id="floatPanel" style="position:absolute; visibility:hidden; z-index:1000000;">
		<table border="0" cellpadding="0" cellspacing="0" width="205">
			<tr class="pagingEnd">
				<td colspan="3"><img src="../images/floatpanelheader.gif" width="100%" height="24" border="0" usemap="#floatPanelCloseMap" /></td>
			</tr>
			<tr class="formBody">
				<td width="1" class="formLabel">&nbsp;</td>
				<td width="98%" valign="top">
					<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#fbf8f1">
						<tr class="gapFormBody"><td width="1" height="5">&nbsp;</td></tr>
						<tr class="tableRowOdd"><td class="formBody"><div id="floatPanelContent"></div></td></tr>
						<tr class="gapFormBody"><td width="1" height="5">&nbsp;</td></tr>
					</table>
				</td>
				<td width="1" class="formLabel">&nbsp;</td>
			</tr>
			<tr class="pagingEnd">
				<td colspan="3" height="3">&nbsp;</td>
		    </tr>
		</table>
		<map name="floatPanelCloseMap">
			<area shape="rect" coords="180,1,215,30" href="javascript:void(0);" onClick="closeFloatPanel();" />
		</map>
	</div>
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td background="../images/header_backg.png" width="10">&nbsp;</td>
		<td width="100"><a href="../do/login?operation=resolveForward"><img border="0" src="../images/proj_logo.png"></a></td>		
		<td background="../images/header_backg.png">&nbsp;</td>
		<td background="../images/header_backg.png" class="tableCell">
			<div align="right" > 
				<span id="ajaxResponse"></span>&nbsp;&nbsp;&nbsp;&nbsp;
				<logic:present name="CURRENT_USER_SESSION" property="name">
				
				   (<bean:write name="CURRENT_USER_SESSION" property="name" filter="false"/>) -

					<logic:present name="USER_SURVEY_LIST" property="class">
						<a href="javascript:displayMessage('../do/showSurvey?operation=selectSurvey',420,110);"><bean:message key="title.formSurvey.header"/></a> -		 
					</logic:present>
				   
					<logic:present name="CURRENT_USER_SESSION" property="showReportMenu">
						<a href="../do/showRepositoryViewerCustomer?operation=prepareForm&projectId=ALL"><bean:message key="title.formCustRepository.header"/></a> - 					
						<a href="../do/viewReport?operation=prepareForm&projectId=ALL"><bean:message key="title.manageReport.header"/></a> - 
					</logic:present>
				    
 				    <a href="../do/manageOption?operation=prepareForm"><bean:message key="title.manageOption"/></a> - 
	   				<a href="javascript:logout()"><bean:message key="title.logout"/></a>
				</logic:present>
			</div>
		</td>
		<td background="../images/header_backg.png" width="20">&nbsp;</td>
		<td background="../images/header_backg.png" width="10">&nbsp;</td>
	</tr>
	<tr>
	  	<plandora-html:headerMenu name="resTaskForm"/> 	
	</tr>	
	<tr class="gapFormBody">
		<td colspan="6">&nbsp;</td>
	</tr>	
	</table>
		
	<jsp:include page="validator.jsp" />
    <script language="JavaScript" src="../jscript/ajax.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/ajaxsync.js" type="text/JavaScript"></script>	
    <script language="JavaScript" src="../jscript/default.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/dateUtil.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/calendar1.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/floatPanel.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/modal_panel.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/ajax-dynamic-content.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/modal_panel_ajax.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/swfobject.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/repository.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/slidemenu.js" type="text/JavaScript"></script>
   
	<script language="JavaScript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/Javascript"></script>    
    <script language="JavaScript" src="../jscript/highcharts.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/highcharts-more.js" type="text/JavaScript"></script>
    <script language="JavaScript" src="../jscript/exporting.js" type="text/JavaScript"></script>

    <script>
    	messageObj = new DHTML_modalMessage();	// We only create one object of this class
		messageObj.setShadowOffset(5);	// Large shadow
    
    	function logout(){
		if ( confirm("<bean:message key="message.confirmLogout"/>")) {    	
				window.location = "../do/login?operation=doLogout";	    	
    		}
    	}
    	
		function displayMessage(url, w, h){
			messageObj.setSource(url + '&' + new Date().getTime() );
			messageObj.setCssClassMessageBox(false);
			messageObj.setSize(w, h);
			messageObj.setShadowDivVisible(true);
			messageObj.display();
		}

    	
		function displayStaticMessage(messageContent, w, h) {
			messageObj.setHtmlContent(messageContent);
			messageObj.setSize(w, h);
			messageObj.setSource(false);
			messageObj.setShadowDivVisible(true);	
			messageObj.display();			
		}
    	
		function closeMessage() {
			messageObj.close();	
		}
				
		function callSurvey(){
			closeMessage();
			with(document.forms["showUserSurveyForm"]){
				window.location="../do/showSurvey?operation=prepareForm&id=" + surveyId.value;
			}
		}
				
    </script>
    
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="90%">
	<tr>
		<td width="2">&nbsp;</td>	
		<td valign="top" align="center">