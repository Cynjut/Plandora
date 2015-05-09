<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<html>
	<title><bean:message key="title.ganttChart"/></title>
	<head>
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
	</head>
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" onLoad="showWindow();">
	
	<script>
		function showWindow(){
			window.moveTo(0, 0);
		}
	</script>

	<html:form  action="ganttViewer">
		<html:hidden name="ganttViewerForm" property="operation"/>
		<html:hidden name="ganttViewerForm" property="type"/>	
	</html:form>		

	<center>
	
	<APPLET code="com.pandora.gui.gantt.Gantt.class" archive="../jsp/gantt.jar" width="1005" height="640">
	
		<param name="TIMEUNIT" value="60000" />
		<param name="SLOTSIZE" value="1440" />
		<param name="PARENTSLOTSIZE" value="10080" />
		<param name="NWMARKCOLOR" value="EFEFEF" />

		<bean:write name="ganttViewerForm" property="editable" filter="false"/>		
		<param name="HZOOM" value="10" />
		<param name="VZOOM" value="10" />
		<param name="ROWHEIGHT" value="18" />
		<param name="SLOTWIDTH" value="15" />

		<bean:write name="ganttViewerForm" property="serverURI" filter="false"/>		
		<bean:write name="ganttViewerForm" property="initialDate" filter="false"/>
		<bean:write name="ganttViewerForm" property="slots" filter="false"/>		
		<bean:write name="ganttViewerForm" property="language" filter="false"/>
		<bean:write name="ganttViewerForm" property="country" filter="false"/>		
		<bean:write name="ganttViewerForm" property="username" filter="false"/>
		<bean:write name="ganttViewerForm" property="password" filter="false"/>
				
		<bean:write name="ganttViewerForm" property="resNum" filter="false"/>
		<bean:write name="ganttViewerForm" property="resBody" filter="false"/>		

		<bean:write name="ganttViewerForm" property="layerNum" filter="false"/>
		<bean:write name="ganttViewerForm" property="layerBody" filter="false"/>		

		<bean:write name="ganttViewerForm" property="jobNum" filter="false"/>
		<bean:write name="ganttViewerForm" property="jobBody" filter="false"/>		

		<bean:write name="ganttViewerForm" property="allocUnitNum" filter="false"/>
		<bean:write name="ganttViewerForm" property="allocUnitBody" filter="false"/>		

		<bean:write name="ganttViewerForm" property="depedenceNum" filter="false"/>
		<bean:write name="ganttViewerForm" property="depedenceBody" filter="false"/>		

		<param name="BUTTONNUM" value="5">
		<param name="BUTTON_1" value="<bean:message key="gantt.label.zoomIn"/>|ZOOM_IN_BUTTON|target|TRUE|../images/gantt/zoom-in.gif" />
		<param name="BUTTON_2" value="<bean:message key="gantt.label.zoomOut"/>|ZOOM_OUT_BUTTON|target|trUe|../images/gantt/zoom-out.gif" />
		<param name="BUTTON_3" value="<bean:message key="gantt.label.saveJobs"/>|SAVE_BUTTON|target|true|../images/gantt/save.gif" />	
		<param name="BUTTON_4" value="SEPARATOR|SEPARATOR|SEPARATOR|true|SEPARATOR" />
		<param name="BUTTON_5" value="<bean:message key="label.manageTask.projResource"/>|LAYER_COMBO|empty|true|empty" />
		
		<param name="MENUNUM" value="1">
		<param name="MENU_1" value="JOB_AREA|<bean:message key="gantt.label.editAlloc"/>|ALLOC_EDIT_TARGET|" />

	</APPLET>
	
	</center>
	</body>
</html>