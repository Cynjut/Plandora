<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<title><bean:write name="mindMapForm" property="formTitle" filter="false" /></title>
		<script type="text/javascript" language="JavaScript" src="../jscript/default.js"></script>
		<script type="text/javascript" language="JavaScript" src="../jscript/flashobject.js"></script>
		<script type="text/javascript" language="JavaScript" src="../jscript/ajaxsync.js"></script>	
		<script type="text/JavaScript" language="JavaScript" src="../jscript/modal_panel.js" ></script>
		<script>
			messageObj = new DHTML_modalMessage();	// We only create one object of this class
			messageObj.setShadowOffset(5);	// Large shadow
				
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
		</script>
		
		<style type="text/css">
			#popupfooter {
				position:absolute;
				bottom:0 !important;
				height:20px;
				width:100%;
			}
			
			html {
				overflow: hidden;
			}
			
			#flashcontent {
				height: 100%;
			}

			body {
				margin: 0 0 0 0;
				padding: 0;
				background-color: #ffffff;
			}
		</style>
		
		<script language="javascript">
			function giveFocus(){ 
				document.visorFreeMind.focus();  
			}
		</script>
	</head>
	
	<body onLoad="giveFocus();">
		<html:form action="viewMindMap"> 
			<html:hidden name="mindMapForm" property="operation"/>
			<html:hidden name="mindMapForm" property="id"/>
		
			<table width="100%" height="20px" border="0" cellspacing="0" cellpadding="0">
			<tr class="formLabel">
			   <td width="10">&nbsp;</td>
			   <td><center>
					<bean:message key="title.mindMapForm"/>
			   </center></td>
			   <td width="10">&nbsp;</td>      
			</tr>
			</table> 		
			
			<br/>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td width="10">&nbsp;</td><td>
			
				<display:headerfootergrid width="100%" type="HEADER">
					<bean:message key="title.mindMapForm.diagram"/>
				</display:headerfootergrid>
			
			  
				<div id="flashcontent" onmouseover="giveFocus();">
					 Flash plugin or Javascript are turned off.
					 Activate both  and reload to view the mindmap
				</div>
				
				<script type="text/javascript">
					var fo = new FlashObject("visorFreemind.swf", "visorFreeMind", "100%", "230", 6, "#9999ff");
					fo.addParam("quality", "high");

					fo.addParam("bgcolor", "#9999ff");
					fo.addVariable("scale","exactFit");
					fo.addParam("salign","LT");
					fo.addParam("wmode", "opaque");
							
					fo.addVariable("openUrl", "_self");
					fo.addVariable("startCollapsedToLevel","2");
					fo.addVariable("defaultWordWrap", "300");		
					fo.addVariable("maxNodeWidth", 200);
					fo.addVariable("defaultToolTipWordWrap",100);
					fo.addParam("useCodepage","true");
					
					
					fo.addVariable("shotsWidth","200");
					fo.addVariable("genAllShots","true");
					fo.addVariable("scaleTooltips","false");
					fo.addVariable("unfoldAll","true");        		
					fo.addVariable("mainNodeShape","rectangle");
					fo.addVariable("initLoadFile","<bean:write name="mindMapForm" property="serverURI" filter="false" />");
					fo.addVariable("buttonsPos","top");
					fo.addVariable("min_alpha_buttons",20);
					fo.addVariable("max_alpha_buttons",100);
					fo.addVariable("baseImagePath","../images/");
					
					fo.addVariable("justMap","false");
					//fo.addVariable("offsetX",100);
					//fo.addVariable("offsetY",100);
					
					fo.write("flashcontent");
				</script>
			
				<display:headerfootergrid type="FOOTER">	
					<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
					  <td>&nbsp;</td>
					</tr></table>
				</display:headerfootergrid> 
				
			</td><td width="10">&nbsp;</td>
			</tr></table>
			
			<br/>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td width="10">&nbsp;</td><td>
			
				<display:headerfootergrid width="100%" type="HEADER">
					<bean:message key="title.mindMapForm.trace"/>
				</display:headerfootergrid>

				<table width="98%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
						<td colspan="4">&nbsp;</td>
					</tr>		
					<plandora-html:artifact name="mindMapForm" property="planningId" projectProperty="projectId" ajax="true" collection="mindMapArtifactList" onlyBody="true" height="152"/> 	
				</table> 

				<display:headerfootergrid type="FOOTER">	
					<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
					  <td>&nbsp;</td>
					</tr></table>
				</display:headerfootergrid> 
				
			</td><td width="10">&nbsp;</td>
			</tr></table>

			<div id="popupfooter">						
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="formLabel">
				   <td width="10">&nbsp;</td>
				   <td><center>
					  <html:button property="close" styleClass="button" onclick="javascript:window.close();">
						<bean:message key="button.close"/>
					  </html:button>    
				   </center></td>
				   <td width="10">&nbsp;</td>      
				</tr>
				</table> 
			</div>
		</html:form>
	</body>
</html>