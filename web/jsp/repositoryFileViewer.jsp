<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

   	<logic:equal name="repositoryFileViewerForm" property="outputType" value="1">
		<head>
			<title>
				<bean:write name="repositoryFileViewerForm" property="formTitle" filter="false" />
			</title>
   		
			<script type="text/javascript" src="../jscript/flashobject.js"></script>
			<style type="text/css">
				html {
					height: 100%;
					overflow: hidden;
				}
				#flashcontent {
					height: 100%;
				}		
				body {
					height: 100%;
					margin: 0;
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
			<div id="flashcontent" onmouseover="giveFocus();">
				 Flash plugin or Javascript are turned off.
				 Activate both  and reload to view the mindmap
			</div>
			<script type="text/javascript">
				var fo = new FlashObject("visorFreemind", "visorFreeMind", "100%", "100%", 6, "#ffffff");
				fo.addParam("quality", "high");
		        fo.addParam("bgcolor", "#ffffff");
		        fo.addVariable("scale","exactFit");
				fo.addParam("salign","LT");
				fo.addParam("wmode", "opaque");
				fo.addVariable("openUrl", "_self");
				fo.addVariable("startCollapsedToLevel","3");
		        fo.addVariable("defaultWordWrap",200);		
				fo.addVariable("maxNodeWidth","200");
				fo.addVariable("defaultToolTipWordWrap",200);				
		        fo.addVariable("scaleTooltips","false");
		        fo.addVariable("unfoldAll","false");        		
		        fo.addVariable("mainNodeShape","ellipse");
				fo.addVariable("initLoadFile","<bean:write name="repositoryFileViewerForm" property="serverURI" filter="false" />");
				fo.addVariable("buttonsPos","top");
				fo.addVariable("min_alpha_buttons",20);
				fo.addVariable("max_alpha_buttons",100);
				fo.addVariable("baseImagePath","../images/");
				fo.write("flashcontent");
			</script>
		</body>
	</logic:equal>


   	<logic:equal name="repositoryFileViewerForm" property="outputType" value="2">

	    <head> 
			<title>
				<bean:write name="repositoryFileViewerForm" property="formTitle" filter="false" />
			</title>
       	 	<style type="text/css" media="screen"> 
				html, body	{ height:100%; }
				body { margin:0; padding:0; overflow:auto; }   
				#flashContent { display:none; }
        	</style> 
		
			<script type="text/javascript" src="../jscript/swfobject.js"></script>
			<script type="text/javascript" src="../jscript/flexpaper_flash.js"></script>
		
	        <script type="text/javascript">
            	var swfVersionStr = "10.0.0";         
	            var flashvars = { 
	                  SwfFile : escape("<bean:write name="repositoryFileViewerForm" property="serverURI" filter="false" />"),
					  Scale : 0.6, 
					  ZoomTransition : "easeOut",
					  ZoomTime : 0.5,
	  				  ZoomInterval : 0.1,
	  				  FitPageOnLoad : false,
	  				  FitWidthOnLoad : true,
	  				  PrintEnabled : true,
	  				  FullScreenAsMaxWindow : false,
	  				  ProgressiveLoading : true,
	  				  
	  				  PrintToolsVisible : true,
	  				  ViewModeToolsVisible : true,
	  				  ZoomToolsVisible : true,
	  				  FullScreenVisible : true,
	  				  NavToolsVisible : true,
	  				  CursorToolsVisible : true,
	  				  SearchToolsVisible : true,
  				  
	  				  localeChain: "<bean:write name="repositoryFileViewerForm" property="userLocale" filter="false" />"
				};
				var params = {  }
	            
	            params.quality = "high";
	            params.bgcolor = "#ffffff";
	            params.allowscriptaccess = "sameDomain";
	            params.allowfullscreen = "true";
	            var attributes = {};
	            attributes.id = "FlexPaperViewer";
	            attributes.name = "FlexPaperViewer";
	            swfobject.embedSWF(
	                "FlexPaperViewer", "flashContent", "580", "380",
	                swfVersionStr, "expressInstall", flashvars, params, attributes);
				swfobject.createCSS("#flashContent", "display:block;text-align:left;");	            
	        </script> 
    	</head> 
   		<body> 
    		<div style="position:absolute;left:10px;top:10px;">
	        	<div id="flashContent"> 
	        		<p> 
		        		To view this page ensure that Adobe Flash Player version 
						9.0.124 or greater is installed. 
					</p> 
					<script type="text/javascript"> 
						var pageHost = ((document.location.protocol == "https:") ? "https://" :	"http://"); 
						document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
									+ pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
					</script> 
	        	</div> 
        	</div>
   		</body> 
	</logic:equal>
	
	
   	<logic:equal name="repositoryFileViewerForm" property="outputType" value="3">

	    <head> 
			<title>
				<bean:write name="repositoryFileViewerForm" property="formTitle" filter="false" />
			</title>
    	</head> 
   		<body> 
			<frameset rows="1,*" frameborder="no" border="0" framespacing="0">
			    <frame src="" name="empty" marginwidth="0" noresize>
			    <frame src="<bean:write name="repositoryFileViewerForm" property="serverURI" filter="false" />" name="viewer" marginwidth="0" noresize>
			    <noframes>
				    <body>
				      <center>Your browser doesn't have frame support.</center>
				    </body>
			    </noframes>
			</frameset>
   		</body> 	
	</logic:equal>
	
</html>