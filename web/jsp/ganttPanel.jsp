<jsp:include page="encoding.jsp" />
<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<html>
	<head>
		<title><bean:message key="header.title" /></title>
		
		<logic:present name="CURRENT_USER_SESSION" property="name">
			<meta http-equiv='Content-Type' content='text/html; charset=<bean:write name="CURRENT_USER_SESSION" property="encoding" filter="false"/>'>
		</logic:present>
		
		<link href="../css/styleDefault.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link href="../css/ganttpanel.css" id="style" TYPE="text/css" rel="STYLESHEET">
		<link rel="shortcut icon" type="image/x-icon" href="../images/favicon.ico" />
		
		<script language="JavaScript" src="../jscript/ajax.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/ajaxsync.js" type="text/JavaScript"></script>	
		<script language="JavaScript" src="../jscript/default.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/dateUtil.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/calendar1.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/modal_panel.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/ajax-dynamic-content.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/modal_panel_ajax.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/swfobject.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/repository.js" type="text/JavaScript"></script>
		<script language="JavaScript" src="../jscript/slidemenu.js" type="text/JavaScript"></script>

		<script LANGUAGE="JavaScript">
			
			// global variables used while dragging and event handlers
			var offsetXReference = 0;
			var selectedObj;
			var selectedTask;
			var SEPARATOR_WIDTH = 7;
			var SLOT_WIDTH = 20;
			var GANTT_LEFT_POS = 30;
			var TITLE_MAX_WIDTH = 800;
			
			var cavs;
			var ctx;
			
			var messageObj = new DHTML_modalMessage();	// We only create one object of this class
			messageObj.setShadowOffset(5);	// Large shadow			
			
			document.onmousedown = function(event) {
				evt = event || window.event;
				return grabObject(evt);
			};			


			document.onmouseup = function(event) {
				evt = event || window.event;
				return releaseObject(evt);
			};			
			
			
			function scrollBar(isBarReference) {
				var refBar = document.getElementById('ganttBarBox');
				var refLabel = document.getElementById('ganttlabelBox');
				var refTitleLabel = document.getElementById('gantttitlelabelBox');
				var barsTitle = document.getElementById('ganttBarTitleBox');
				
				if (isBarReference) {
					refLabel.scrollTop = refBar.scrollTop
				} else {
					refBar.scrollTop = refLabel.scrollTop;					
				}
				refTitleLabel.scrollLeft = refLabel.scrollLeft;
				barsTitle.scrollLeft = refBar.scrollLeft;
			}

			// positioning an object at a specific 'x' coordinate
			function shiftTo(toChangeObj, x) {

				var redimBox = document.getElementById('ganttboxBarRedim');
				var ref = document.getElementById('ganttlabelBox');
				var bars = document.getElementById('ganttBarBox');
				var jobsArea = document.getElementById('jobsAreaTable');

				if (toChangeObj.id == "ganttBoxSeparator") {
					selectedObj.style.cursor="w-resize";
					redimBox.style.display = "none";
					var labelTbl = document.getElementById('ganttlabelBoxTable');
					if (x > TITLE_MAX_WIDTH) {
						x = TITLE_MAX_WIDTH;
					}
					if (x < GANTT_LEFT_POS + 50) {
						x = GANTT_LEFT_POS + 50;
					}
					
				} else if (toChangeObj.id.substring(0, 9) == "ganttJob_") {
					selectedObj.style.cursor="move";
					redimBox.style.display = "none";
					x = x - ref.offsetWidth + bars.scrollLeft - GANTT_LEFT_POS - 5;
					if (x < 0) {
						x = 0;
					}
					if ((x + toChangeObj.offsetWidth) > (bars.offsetWidth + bars.scrollLeft) ) {
						x = (bars.offsetWidth + bars.scrollLeft) - toChangeObj.offsetWidth;
					}
					//right limit to job move action...
					if ((selectedTask.offsetLeft + selectedTask.offsetWidth) > jobsArea.offsetWidth) {
						x = jobsArea.offsetWidth - selectedTask.offsetWidth;
					}
				
				} else if (toChangeObj.id == "ganttboxBarRedim" && selectedTask) {
					redimBox.style.cursor="w-resize";
					//right limit to job redim action...					
					if ((x + bars.scrollLeft - ref.offsetWidth) > jobsArea.offsetWidth) {
						var diff = 0;
						if ((bars.scrollLeft-ref.offsetWidth)>0) {
							diff = (bars.scrollLeft - ref.offsetWidth);
						}					
						x = jobsArea.offsetWidth - diff;
					}
					
				} else {
					redimBox.style.display = "none";
				}
				
				if (!isNaN(x)) {
					toChangeObj.style.left = x;	
				}
			}


			// set selectedObj with the reference to dragged element
			function setSelectedObj(evt) {
				var redimBox = document.getElementById('ganttboxBarRedim');
				
				var objectToBeChanged = evt.srcElement? evt.srcElement : evt.target; 
				if (objectToBeChanged) {
					if (objectToBeChanged.id != "ganttboxBarRedim") {
						redimBox.style.display = "none";
					}
				
					if (objectToBeChanged.id == "ganttBoxSeparator" || objectToBeChanged.id.substring(0, 9) == "ganttJob_" || objectToBeChanged.id == "ganttboxBarRedim") {
						selectedObj = objectToBeChanged;

						//show the icon to redim a block						
						shiftRedimBox(objectToBeChanged);
						
						return;
					}				
				}
				selectedObj = null;	
				createCanvasOverlay();
				return;
			}
			
			
			function dragIt(evt) {
				if (selectedObj) {
					selectedObj.style.cursor="w-resize";
					shiftTo(selectedObj, (evt.clientX - offsetXReference));
					return false;
				}
			}
			
			
			function grabObject(evt) {
				setSelectedObj(evt);
				if (selectedObj) {

					var offX = (!evt.offsetX)? evt.layerX : evt.offsetX; 
					offsetXReference = offX;
					
					document.onmousemove = function(e) {
						evt = e || window.event;
						return dragIt(evt);
					};
				}
			}
			
			
			// perform boxes repositioning
			function releaseObject(evt) {
				if (selectedObj) {					
					if (selectedObj.id == "ganttBoxSeparator") {	
						changePanelPosition(evt, selectedObj.offsetLeft);
						
					} else if (selectedObj.id.substring(0, 9) == "ganttJob_") {	
						var currentPos = selectedObj.offsetLeft;
						var slot = Math.floor( currentPos / SLOT_WIDTH );
						selectedObj.style.left = Math.floor(slot * SLOT_WIDTH);
						shiftRedimBox(selectedObj);
						
						//call the event to save the move command...
						changeGanttTask(slot, selectedTask.id.substring(9), true);
						
					} else if (selectedObj.id == "ganttboxBarRedim" && selectedTask) {	
						var panel = document.getElementById('ganttlabelBox');
						var bars = document.getElementById('ganttBarBox');
						var currentPos = selectedObj.offsetLeft - panel.offsetWidth - GANTT_LEFT_POS;
						var slot = Math.floor( currentPos / SLOT_WIDTH );
						selectedTask.style.width = Math.floor(slot * SLOT_WIDTH) - selectedTask.offsetLeft + bars.scrollLeft;
						if (selectedTask.offsetWidth<SLOT_WIDTH) {
							selectedTask.style.width = SLOT_WIDTH + "px";
						}
						selectedObj.style.display = "none";
						redimMacroTask(selectedTask);
						
						//call the event to save the redim command...
						if (selectedTask) {						
							changeGanttTask(Math.round(selectedTask.offsetWidth/SLOT_WIDTH), selectedTask.id.substring(9), false);
						}
					}
					selectedObj = null;
					document.onmousemove = null;
				}
			}
			

			function changeGanttTask(rfSlt, resTskId, isMoveTaskCommand){
				with(document.forms["ganttPanelForm"]){
		   			operation.value = "changeGanttTask";
		   			refSlot.value = rfSlt;
		   			resourceTaskId.value = resTskId;
		   			if (isMoveTaskCommand) {
		   				changeTaskCommand.value = "1";
		   			} else {
		   				changeTaskCommand.value = "0";
		   			}
		   		}
				
				var ajaxRequestObj = ajaxSyncInit();
				ajaxSyncProcess(document.forms["ganttPanelForm"], callBackChangeGanttTask, resTskId, "", ajaxRequestObj);		
			}

			
			function callBackChangeGanttTask(resTskId, dummy, objRequest) {			
				if(isSyncAjax(objRequest)){
					document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
					var content = objRequest.responseText;
					if (content) {
						if (content.length>800) {
							alert("Unexpected Error: the session probably has expired.");	
						}
						if (content.substring(0, 2)!='OK') {
							alert(content);	
						}
					} 
					
				}  
			}
			
			
			function changePanelPosition(evt, xpos){
			
				var panel = document.getElementById('ganttlabelBox');
				panel.style.width = (xpos - panel.offsetLeft) + "px";
				
				var boxTitlePanel = document.getElementById('gantttitlelabelBox');
				boxTitlePanel.style.width = (xpos - panel.offsetLeft) + "px";
				
				var bars = document.getElementById('ganttBarBox');
				var oldPos = bars.offsetLeft;
				bars.style.left = (xpos + SEPARATOR_WIDTH) + "px";  
				var diff = oldPos - bars.offsetLeft;

				bars.style.width = bars.offsetWidth + diff + "px";
				
				var barsTitle = document.getElementById('ganttBarTitleBox');
				barsTitle.style.left = bars.style.left;
				barsTitle.style.width = bars.style.width;
			}			
			
			
			function shiftRedimBox(objectToBeChanged) {
				if (objectToBeChanged.id.substring(0, 9) == "ganttJob_") {
					selectedTask = objectToBeChanged;
					var redimBox = document.getElementById('ganttboxBarRedim');
					var boxPanel = document.getElementById('ganttlabelBox');
					var boxTimeLine = document.getElementById('ganttBarTable');
					var boxBar = document.getElementById('ganttBarBox');
					var barsTitle = document.getElementById('ganttBarTitleBox');
					redimBox.style.display = "block";
					
					var adjust = 3;
					if (navigator.appVersion.indexOf("MSIE") != -1) {
						adjust = 8;
					}
					
					redimBox.style.left = objectToBeChanged.offsetLeft + GANTT_LEFT_POS + boxPanel.offsetWidth + objectToBeChanged.offsetWidth + SEPARATOR_WIDTH - boxBar.scrollLeft;
					redimBox.style.top = boxPanel.offsetTop - 32 + objectToBeChanged.offsetTop + (objectToBeChanged.offsetHeight/2) + barsTitle.offsetHeight - adjust - boxBar.scrollTop;
					redimBox.style.Height = 7;
					redimMacroTask(objectToBeChanged);
				}			
			}


			function colapseExpandMacroTask(macroTaskId) {
				var macroTaskObj = document.getElementById("macroTask_" + macroTaskId);
				if (macroTaskObj) {
					var macroTaskImgObj = document.getElementById("img_" + macroTaskId);
					if (macroTaskImgObj) {
					
						//change the icon of colapse/expand
						var colapse = (macroTaskImgObj.src.indexOf("plus")>=0);
						if (colapse) {
							macroTaskImgObj.src = "../images/minus.gif";
						} else {
							macroTaskImgObj.src = "../images/plus.gif";
						}
							
						//hide/show the tasks related to macro task...
						var childTaskObj = macroTaskObj.getAttribute("childTask");
						if (childTaskObj) {
							var childTaskList = childTaskObj.split(';');
							for (j=0; j< childTaskList.length; j++) {
								var childTaskTR = document.getElementById("t_" + childTaskList[j]);
								var childTaskBAR = document.getElementById("b_" + childTaskList[j]);
								if (childTaskTR && childTaskBAR) {
									if (colapse) {
										childTaskTR.style.display = 'table-row'; 
										childTaskBAR.style.display = 'table-row'; 
									} else {
										childTaskTR.style.display = 'none';
										childTaskBAR.style.display = 'none';
									}
								}
							}
						}
					}
				} 
			}

			
			function redimMacroTask(objectToBeChanged) {
				var mtask = objectToBeChanged.getAttribute("macroTask");
				if (mtask) {
					var macroTaskList = mtask.split(';');
					for (i=0; i< macroTaskList.length; i++) {
						var macroTaskObj = document.getElementById("macroTask_" + macroTaskList[i]);
						if (macroTaskObj) {
							
							var childTaskObj = macroTaskObj.getAttribute("childTask");
							if (childTaskObj) {
								var childTaskList = childTaskObj.split(';');
								var minLeft = Number.MAX_VALUE;
								var maxWidth = 0;
								
								for (j=0; j< childTaskList.length; j++) {
									var childTaskObj = document.getElementById("ganttJob_" + childTaskList[j]);
									if (!childTaskObj) {
										childTaskObj = document.getElementById("notEditableJob_" + childTaskList[j]);
									}
									if (childTaskObj) {
										if (childTaskObj.offsetLeft < minLeft) {
											minLeft = childTaskObj.offsetLeft;
										}
										if ((childTaskObj.offsetLeft + childTaskObj.offsetWidth) > maxWidth ) {
											maxWidth = (childTaskObj.offsetLeft + childTaskObj.offsetWidth);
										}									
									}
								}
								
								var adjust = 0;
								if (navigator.appVersion.indexOf("MSIE") == -1) {
									adjust = 10;
								}
								macroTaskObj.style.left = minLeft + "px";
								macroTaskObj.style.width = (maxWidth - minLeft - adjust)+ "px";
							}
						}
					}
				}			
			}
			
			function createCanvasOverlay() {
				var boxSep = document.getElementById('ganttBoxSeparator');
				var jobsArea = document.getElementById('jobsAreaTable');
				var bars = document.getElementById('ganttBarBox');
				var panel = document.getElementById('ganttlabelBox');
				
				
				//var ctx = document.querySelector('canvas').getContext('2d');
				//ctx.save();				
				//ctx.lineWidth = 1;
				//ctx.fillStyle = ctx.strokeStyle = '#000';
				//ctx.beginPath();
				//ctx.moveTo(340,20);           // Create a starting point
				//ctx.lineTo(100,60);          // Create a horizontal line
				//ctx.arcTo(400,60,400,165,5); // Create an arc
				//ctx.lineTo(150,120);         // Continue with vertical line
				//ctx.stroke();                // Draw it
				//ctx.restore();
			}
				
							
	    	function logout(){
	    		if ( confirm("<bean:message key="message.confirmLogout"/>")) {    	
	    				window.location = "../do/login?operation=doLogout";	    	
	        	}
	        }

	    	function refreshChart(){
	    		buttonClick("ganttPanelForm", "refresh");
	        }

	    	function editTaskAlloc(taskId, resId, projId, visib){
	    		displayMessage("../do/showGanttEdit?operation=prepareForm&taskId=" + taskId + "&resourceId=" + resId + "&projectId=" + projId + "&visibility=" + visib, 500, 260);
	        }	
	    	
	    	function createNewTask(projectId){
	    	    displayMessage("../do/showAgilePanelTask?operation=showEditPopup&taskId=&requirementId=&taskProjectId=" + projectId, 480, 300);
	    	}

			function displayMessage(url, w, h){
				messageObj.setSource(url);
				messageObj.setCssClassMessageBox(false);
				messageObj.setSize(w, h);
				messageObj.setShadowDivVisible(true);
				messageObj.display();
			}
			
			function closeMessage() {
				messageObj.close();	
			}

		</script>
		
	</head>
	
	<body bgColor="#ffffff" leftMargin="0" topMargin="0" marginheight="0" marginwidth="0" height="100%">
	<canvas width="2000px" height="600px" style="position: absolute; pointer-events: none; left: 0; top: 0; z-index: 5;"></canvas>
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
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="90%">
	<tr>
		<td width="2">&nbsp;</td>	
		<td valign="top" align="center">

		<html:form action="ganttPanel">
			<html:hidden name="ganttPanelForm" property="operation"/>
			<html:hidden name="ganttPanelForm" property="projectId"/>
			<html:hidden name="ganttPanelForm" property="lockedGantt"/>
			<html:hidden name="ganttPanelForm" property="refSlot"/>
			<html:hidden name="ganttPanelForm" property="resourceTaskId"/>
			<html:hidden name="ganttPanelForm" property="changeTaskCommand"/>
			
			<br>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td width="10">&nbsp;</td><td>
			
				<display:headerfootergrid width="100%" type="HEADER">
					<bean:message key="title.ganttChart"/>
				</display:headerfootergrid>
			
				<table border="0" cellspacing="0" cellpadding="0"><tr class="formBody">
					<td class="formTitle" width="10">&nbsp;</td>
					<td class="gapFormBody" width="30">&nbsp;
						<logic:equal name="ganttPanelForm" property="lockedGantt" value="1">
							<a href="../do/ganttPanel?operation=changeLockStatus" border="0"><img src="../images/lockedit.gif" border="0" title="<bean:message key='gantt.tip.unlocked'/>"  alt="<bean:message key='gantt.tip.locked'/>" /></a>
						</logic:equal>
						<logic:equal name="ganttPanelForm" property="lockedGantt" value="0">
							<a href="../do/ganttPanel?operation=changeLockStatus" border="0"><img src="../images/unlock.png" border="0" title="<bean:message key='gantt.tip.locked'/>"  alt="<bean:message key='gantt.tip.locked'/>" /></a>
						</logic:equal>						
					</td>
					<!--
					<td class="gapFormBody" width="30">
						<a href="javascript:createNewTask(<bean:write name='ganttPanelForm' property='projectId' filter='false'/>);" border="0"><img border="0" src="../images/newTask.gif" title="<bean:message key='gantt.tip.newtask'/>"  alt="<bean:message key='gantt.tip.newtask'/>" ></a>
					</td>
					-->
					<td class="formTitle" width="10">&nbsp;</td>
					<td class="formTitle"><bean:message key="gantt.label.iniDate"/>:&nbsp;</td>
					<td><plandora-html:calendar name="ganttPanelForm" property="initialDate" styleClass="textBoxDisabled" /></td>
					<td class="formTitle" width="10">&nbsp;</td>
					<td class="formTitle"><bean:message key="gantt.label.finDate"/>:&nbsp;</td>
					<td><plandora-html:calendar name="ganttPanelForm" property="finalDate" styleClass="textBoxDisabled" /></td>
					<td class="formTitle" width="70px"><bean:message key="gantt.label.resource"/>:&nbsp;</td>
					<td>
						<html:select name="ganttPanelForm" property="resourceId" styleClass="textBox" onkeypress="javascript:refreshChart();" onchange="javascript:refreshChart();">
							<html:options collection="ganttResourceList" property="id" labelProperty="username" filter="false"/>
						</html:select>
					</td>
					<td class="formTitle" width="70px"><bean:message key="gantt.label.visible"/>:&nbsp;</td>
					<td>
						<html:select name="ganttPanelForm" property="visibility" styleClass="textBox" onkeypress="javascript:refreshChart();" onchange="javascript:refreshChart();">
							<html:options collection="ganttVisibilityList" property="id" labelProperty="genericTag" filter="false"/>
						</html:select>
					</td>
				</tr></table>
				
				<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
				<br><br><br><br><br><br><br><br><br><br><br>
			
				<div id="gantttitlelabelBox" style="left:30px; width:370px; height:32px">
					<table border="0" width="1340px" cellspacing="0" cellpadding="0">
						<tr>
							<th class="gantttitle" width="100px">ID</th>
							<th class="gantttitle"><bean:message key="gantt.label.task"/></th>
							<th class="gantttitle" width="70px"><bean:message key="gantt.label.iniDateSm"/></th>
							<th class="gantttitle" width="70px"><bean:message key="gantt.label.finDateSm"/></th>
							<th class="gantttitle" width="130px"><bean:message key="gantt.label.category"/></th>
							<th class="gantttitle" width="300px"><bean:message key="gantt.label.proj"/></th>							
							<th class="gantttitle" width="140px"><bean:message key="gantt.label.resource"/></th>
							<th class="gantttitle" width="130px"><bean:message key="gantt.label.status"/></th>							
						</tr>
					</table>
				</div>

				<div id="ganttlabelBox" style="left:30px; width:370px; height:371px; top:32px" onscroll="javascript:scrollBar(false);">
					<table id="ganttlabelBoxTable" width="1340px" border="0" cellspacing="0" cellpadding="0">
						<bean:write name="ganttPanelForm" property="htmlGanttLeftBar" filter="false"/>		
					</table>
				</div>
	
				<div id="ganttBarTitleBox" style="height:32px">
					<bean:write name="ganttPanelForm" property="htmlGanttTimeLine" filter="false"/>					
				</div>
				
				<div id="ganttBarBox" style="height:371px" onscroll="javascript:scrollBar(true);">
					<bean:write name="ganttPanelForm" property="htmlGanttBody" filter="false"/>
				</div>

				
				<div id="ganttboxBarRedim"><!-- IE bug: this comment must be here because, empty div seems to make IE ignore height property and define a arbitrary value--></div>
				<div id="ganttBoxSeparator" style="height:382px;"></div>
	
				<display:headerfootergrid type="FOOTER">				
					<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
					  <td width="120">      
					  	<html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('ganttPanelForm', 'refresh');">
					  		<bean:message key="button.refresh"/>
					  	</html:button>    
					  </td>
					  <td class="textBoxOverTitleArea">
						 <html:checkbox property="hideOccurrences" name="ganttPanelForm" >
						  	<bean:message key="gantt.label.hideOcc"/>
						 </html:checkbox>
					  </td>	   				
					  <td>&nbsp;</td>
					  <td width="120">
					  	<html:button property="backward" styleClass="button" onclick="javascript:buttonClick('ganttPanelForm', 'backward');">
					  		<bean:message key="button.backward"/>
					  	</html:button>    
					  </td>
					</tr></table>  	
				</display:headerfootergrid>
	
			</td><td width="10">&nbsp;</td>
			</tr></table>
	
		</html:form>

<jsp:include page="footer.jsp" />