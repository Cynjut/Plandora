<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="description" content="freemind flash browser"/>
<meta name="keywords" content="freemind,flash"/>
<title><bean:write name="mindMapForm" property="formTitle" filter="false" /></title>
<script type="text/javascript" src="../jscript/flashobject.js"></script>
<style type="text/css">
	
	/* hide from ie on mac \*/
	html {
		height: 100%;
		overflow: hidden;
	}
	
	#flashcontent {
		height: 100%;
	}
	/* end hide */

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
</script></head>
<body onLoad="giveFocus();">
	
	<div id="flashcontent" onmouseover="giveFocus();">
		 Flash plugin or Javascript are turned off.
		 Activate both  and reload to view the mindmap
	</div>
	
	<script type="text/javascript">
		// <![CDATA[

		// for allowing using http://.....?mindmap.mm mode
		function getMap(map){
		  var result=map;
		  var loc=document.location+'';
		  if(loc.indexOf(".mm")>0 && loc.indexOf("?")>0){
			result=loc.substring(loc.indexOf("?")+1);
		  }
		  return result;
		}
		
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
		fo.addVariable("initLoadFile","<bean:write name="mindMapForm" property="serverURI" filter="false" />");
		fo.addVariable("buttonsPos","top");
		fo.addVariable("min_alpha_buttons",20);
		fo.addVariable("max_alpha_buttons",100);
		fo.addVariable("baseImagePath","../images/");
		
		//fo.addVariable("justMap","false");
		//fo.addVariable("offsetX","left");
		//fo.addVariable("offsetY","top");
		
		fo.write("flashcontent");
	</script>

</body>
</html>