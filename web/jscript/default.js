//--------------------------------------
//This is the default edit calling
//--------------------------------------
function edit(argId, argForm, argOperation){
	callAction(argId, argForm, argOperation);
}


//--------------------------------------
//This is the  default remove calling
//--------------------------------------
function removeWithoutConfirm(argId, argForm, argOperation){
	callAction(argId, argForm, argOperation);
}

//--------------------------------------
// Generic struts operation calling
//--------------------------------------
function callAction(argId, argForm, argOperation){
	with(document.forms[argForm]){
   		id.value = argId;
    	operation.value = argOperation;
    	submit();
    }         
}

//--------------------------------------
// Generic routine performed after button click
//--------------------------------------
function buttonClick(argForm, argOperation){
	with(document.forms[argForm]){
    	operation.value = argOperation;
    	submit();
    }         
}


//--------------------------------------
// The function below is used by shortcut feature
//--------------------------------------
function goToForm(src, openingType){
   	if (src=="REQ") {
      	src = "../do/manageCustRequest?operation=prepareForm&showBackward=on";
   	} 

	if (openingType==1) {
		window.location = src;
	} else {
		window.open(src,'_blank');
	}			
}



function removeAttachment(argId, argForm, argFwd, message){
    if ( confirm(message)) {
		window.location = "../do/manageAttachment?operation=removeAttachment&fwd=" + argFwd + "&fileId=" + argId;
    }
}    


function downloadAttachment(argId){
	window.location = "../do/downloadAttachment?id=" + argId;
}

//--------------------------------------
// function to open the link relation popup
//--------------------------------------
function openLinkRelationPopup(projectId){
	var pathWindow ="../do/searchPlanning?operation=prepareForm&type=ALL&projectId=" + projectId;
	window.open(pathWindow, 'searchPlanning', 'width=650, height=300, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
	return false;
}

//--------------------------------------
// Generic function to call the MindMap popup
//--------------------------------------
function loadMindMap(id){
	window.open('../do/viewMindMap?operation=prepareForm&entityid=' + id, 'MindMap', 'width=800, height=600, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=yes');
}

//--------------------------------------
// Generic routines performed by 'get user info' ajax feature
//--------------------------------------
function showUserInfo(argForm, username){
	with(document.forms[argForm]){
		operation.value = "getUserInfo";
   		id.value = username;
    }
    var ajaxRequestObj = ajaxSyncInit();         
	ajaxSyncProcess(document.forms[argForm], callBackShowUserInfoClick, username, argForm, ajaxRequestObj);		
}
function callBackShowUserInfoClick(username, argForm, objRequest) {  
    if(isSyncAjax(objRequest)){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
		var content = objRequest.responseText;
		document.getElementById("floatPanelContent").innerHTML = content;		
    }  
}

//--------------------------------------
// Generic routines performed by 'discussion topic' feature
//--------------------------------------
function showHide(argId){
	var el = document.getElementById( argId );
	if (el!=null) {
		if((el.style.display || 'block') == 'block') { 
			el.style.display =  'none' ; 
			behidden = true ;  
		} else { 
			el.style.display =  'block'; 
			behidden = false;  
		}
	}
}

function replyPost(argForm, topicInfo){
	with(document.forms[argForm]){
    	operation.value = 'replyDiscussion';
    	genericTag.value = topicInfo;
    	submit();
    }         
}

function removePost(argForm, topicInfo, confirmation){
	with(document.forms[argForm]){
    	if ( confirm(confirmation)) {
        	operation.value = 'removeTopic';
        	genericTag.value = topicInfo;
        	submit();
     	}		
    }         
}


//--------------------------------------
// Generic routine performed by 'add' and 'remove' icons at Meta Table
//--------------------------------------
function metaTableRemoveRow(argForm, rowId){
	with(document.forms[argForm]){
    	operation.value = 'metaTableRemoveRow';
    	genericTag.value = rowId;
    	submit();
    }         
}

function metaTableAddRow(argForm, metaTableId){
	with(document.forms[argForm]){
		operation.value = "metaTableAddRow";
   		genericTag.value = metaTableId;
    	submit();
    }         
}


//--------------------------------------
// Generic routine performed by 'add' and 'remove from list box' features
//--------------------------------------
function hasOptionCode(formName, insValue, selName){
	selectName = document.forms[formName].elements[selName];   	  
     	        
	var end = selectName.length - 1;
	for(var j=0; j< end; j++){
	   var id = selectName.options[j].value;
	   if(id == insValue){
		return true;
	   }
	}
	  
	// Verify all options
	return false;
}

//--------------------------------------
// Generic routine performed by 'add' and 'remove from list box' features
//--------------------------------------
function insertSelectBox(formName, selID, selName, errMsg){ 
	return insertSelectBoxWithAddContent(formName, selID, selName, errMsg, "", ""); 
}

//--------------------------------------
// Generic routine performed by 'add' and 'remove from list box' features
//--------------------------------------
function insertSelectBoxWithAddContent(formName, selID, selName, errMsg, content, key) {
	selectName = document.forms[formName].elements[selName];
    selectID = document.forms[formName].elements[selID]; 

    if(hasOptionCode(formName, selectID.value + key, selName)) {
		alert(errMsg);
	  	return false;	
    } else {
    
		// Insert the data into list    
		var opSelected = selectID.selectedIndex;
		if (opSelected>-1){
			var optSpcText = selectName.options[selectName.length-1].text;
			var optSpcValue = selectName.options[selectName.length-1].value;
	        var space = new Option(optSpcText, optSpcValue);
	        
        	if (typeof(opSelected)=="undefined") {
        		//source is a text box
	            var newItem = new Option(selectID.value, selectID.value);
        	    selectName.options[selectName.length-1] = newItem;
	        } else {
        		//source is a combo box
				var opSelected = selectID.selectedIndex;
				selectName.options[selectName.length-1].value= selectID.options[opSelected].value + key; 
				selectName.options[selectName.length-1].text = selectID.options[opSelected].text + content;
	    	}
			selectName.options[selectName.length] = space;
		}
    }
 	return true;
}
 
 
//--------------------------------------
// Generic routine performed by 'add' and 'remove from list box' features
//--------------------------------------
function removeSelectBox(formName, selName, errMsg){
	selectName = document.forms[formName].elements[selName];
   	
	var end = selectName.length - 1;
  	var find = false;
  	for(var i = end; i >=0; i--){
		if(selectName.options[i].selected && selectName.options[i+1]){
	 		find= true;
			var opSelected = selectName.options[i+1].selected;
			selectName.options[i] = new Option(selectName.options[i+1].text, selectName.options[i+1].value);
			for(var temp = i+1; temp < selectName.length-1; temp ++ ){
				selectName.options[temp] = new Option(selectName.options[temp+1].text, selectName.options[temp+1].value);
			}
			selectName.options[i].selected = opSelected;
			selectName.length = selectName.length-1;
	  	}
  	}

  	if (!find){
		alert(errMsg);
		return false;
  	} else {
		return true;
  	}
}

//--------------------------------------
// Generic routine used to select all elements of list box multiple=true
//--------------------------------------
function selectAllItens(formName, selName){
  	selectName = document.forms[formName].elements[selName];
 	var end = selectName.length - 1;
  	for(var i = end; i >=0; i--){
		selectName.options[i].selected = true;
  	}
}


function isAllDigits(argvalue) {
    argvalue = argvalue.toString();
    var validChars = "0123456789";
    var startFrom = 0;
    if (argvalue.substring(0, 2) == "0x") {
       validChars = "0123456789abcdefABCDEF";
       startFrom = 2;
    } else if (argvalue.charAt(0) == "0") {
       validChars = "01234567";
       startFrom = 1;
    } else if (argvalue.charAt(0) == "-") {
        startFrom = 1;
    }
    
    for (var n = startFrom; n < argvalue.length; n++) {
        if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
    }
    return true;
}


function isFloatNumber(argvalue, isCommaDecimal) {
    argvalue = argvalue.toString();
    
    var validChars = "0123456789.";
    if (isCommaDecimal) {
    	validChars = "0123456789,";
    }
    
    var startFrom = 0;
    if (argvalue.charAt(0) == "-") {
        startFrom = 1;
    }
    for (var n = startFrom; n < argvalue.length; n++) {
        if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
    }
    return true;
}



function isCurrency(argvalue){
    argvalue = argvalue.toString();
    var validChars = "0123456789.,";

    var startFrom = 0;
    if (argvalue.charAt(0) == "-") {
        startFrom = 1;
    }
    
    for (var n = startFrom; n < argvalue.length; n++) {
        if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) {
           return false;
        }
    }
    
    return true;
}


function endsWith(str, suffix) {
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}


//--------------------------------------
// This function is used only by popup of upload picture (called by manageOption)
//--------------------------------------
function uploadPicture(path, confimMsg){
	with(document.forms["optionPictureForm"]){
		var saveit = false;
		
		if (path) {
			saveit = true;
		} else {
			if ( confirm(confimMsg)) {
				saveit = true;
			}		
		}
		
		if (saveit) {
			operation.value = "savePic";
			closeMessage();
			submit();			
		}
	}
}


//--------------------------------------
// The functions below are used by SnipArtifactAction during the ajax refresh procedures...
//--------------------------------------
function refreshSnip(snipId, commandId, divId){
	with(document.forms['snipArtifactForm']){
		operation.value = "refreshSnip";
   		snip.value = snipId;
   		refreshCommand.value = commandId;
    }         
    var ajaxRequestObj = ajaxSyncInit();         
	ajaxSyncProcess(document.forms['snipArtifactForm'], callBackRefreshSnip, divId, commandId, ajaxRequestObj);		
}
function callBackRefreshSnip(divId, commandId, objRequest) {  
    if(isSyncAjax(objRequest)){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
		var content = objRequest.responseText;
		document.getElementById(divId).innerHTML = content;		
    }  
}
	

	
//--------------------------------------
// The functions below are used by ManageAttachment popup
//--------------------------------------
function editAttachment(argId, argForm){
	displayMessage('../do/manageAttachment?operation=editAttachment&fileId=' + argId, 600, 385);
}            

function removeAttach(argId, argForm, msg){
	with(document.forms["attachmentForm"]){
		removeAttachment(argId, argForm, fwd.value, msg);			
	}
}            

function openAttachmentHistPopup(id){
	var pathWindow ="../do/manageHistAttach?operation=prepareForm&attachmentId=" + id;
	window.open(pathWindow, 'attachHist', 'width=470, height=175, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
}	

function saveAttachment(){
	with(document.forms["attachmentForm"]){
		if (type.value=="-1") {
			alert('The [Content Type] field is mandatory.');
		} else {
			buttonClick('attachmentForm', 'save');
		}
	}
}	

//the function below is used by repositoryUpload.jsp
function uploadFile(){
	with(document.forms["repositoryUploadForm"]){
    	operation.value = "save";
    	closeMessage();
    	submit();			
	}	
}


//--------------------------------------
// The functions below are used by snipArtifact popup
//--------------------------------------
function submitSnip(){
	var snp;
	with(document.forms["snipArtifactForm"]){
		operation.value = "submitSnip";
		snp = snip.value;
	}
    var ajaxRequestObj = ajaxSyncInit();   
	ajaxSyncProcess(document.forms["snipArtifactForm"], callBackSubmitSnip, snp, '', ajaxRequestObj);		
}
function callBackSubmitSnip(snipId, dummy, objRequest) {
	if(isSyncAjax(objRequest)){
		document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
		var content = objRequest.responseText;

		//call the method from manageArtifact.jsp that shows the content into mceTiny object
		showEditor(content);
	}  
}


//--------------------------------------
//The function below are used by forms to request the relation grid in ajax format 
//--------------------------------------
function requestArtifactBody(pTableID, frm, params){
	with(document.forms[frm]){
		operation.value = "requestArtifactBody";
	}
    var ajaxRequestObj = ajaxSyncInit();         
	ajaxSyncProcess(document.forms[frm], callbackRequestArtifactBody, pTableID, params, ajaxRequestObj);		
}

function callbackRequestArtifactBody(pTableID, params, objRequest) {
	if(isSyncAjax(objRequest)){
	
		//hide ajax icon
		var obj = document.getElementById("ajaxResponse");
		if (obj) {
			obj.innerHTML = ""; 
		}
	
		var content = objRequest.responseText;
		if (content) {
			var d = document.getElementById(pTableID);
			if (d) {
				d.innerHTML = content;				
			}
		}
	}  
}


//--------------------------------------
//The function below are used by forms to request the data of grids
//--------------------------------------
function requestPTableBody(pTableID, frm, params){
	with(document.forms[frm]){
		operation.value = "requestPTableBody";
	}
    var ajaxRequestObj = ajaxSyncInit();         
	ajaxSyncProcess(document.forms[frm], callBackRequestPTableBody, pTableID, params, ajaxRequestObj);		
}

function callBackRequestPTableBody(pTableID, params, objRequest) {
	if(isSyncAjax(objRequest)){
	
		//hide ajax icon
		var obj = document.getElementById("ajaxResponse");
		if (obj) {
			obj.innerHTML = ""; 
		}
	
		var content = objRequest.responseText;
		if (content) {
			var d = document.getElementById("DIV_" + pTableID);
			if (d) {
				d.innerHTML = content;				
			}
		}
	}  
}
function requestPTablePanel(pTableID, frm, params){
	with(document.forms[frm]){
		operation.value = "requestPTableBody";
	}
    var ajaxRequestObj = ajaxSyncInit();         
	ajaxSyncProcess(document.forms[frm], callBackrequestPTablePanel, pTableID, params, ajaxRequestObj);		
}
function callBackrequestPTablePanel(pTableID, params, objRequest) {
	if(isSyncAjax(objRequest)){
		document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon
		var content = objRequest.responseText;
		if (content) {
			
			var sufix = "COL";
			if (params.substr(0,6)=='search') {
				sufix = "SCH";
			}
			
			var d = document.getElementById("DIV_PANEL_" + sufix + "_" + pTableID);
			if (d) {
				d.innerHTML = content;				
			}
		}
	}  
	return false;
}


//--------------------------------------
// generate a URL to be used by Shortcut taglib
//--------------------------------------
function getShortcutURL(fieldList){
	var contentOfRequest = "";
	var currentUrl = document.URL;
	if (fieldList) {
	
		if ( currentUrl.indexOf('?')>0 ) {
			contentOfRequest = "&";
		} else {
			contentOfRequest = "?";
		}					
	
		var tokens = fieldList.split(",")
		if (tokens) {
		    for(i = 0; i < tokens.length; i++){  
		    	token = tokens[i];
			
				//try to get parameter by ID
		    	var obj = document.getElementById(token);
				if (!obj) {
					//try to get parameter by NAME
					obj = document.forms[0].elements[token];
				}

		        if(obj){
		            contentOfRequest = contentOfRequest + token + "=" + escape(obj.value) + "&";  
		        }
		    }
		    
		    if ( contentOfRequest.indexOf('operation')<0 ) {
		    	contentOfRequest = contentOfRequest + "operation=prepareForm";
		    }
		}	
	}

	return escape(currentUrl + contentOfRequest);
}


function getTs2Str() {
	return '' + (new Date()).getTime();
}

//--------------------------------------
// the function below is called by rssPopup.jsp
//--------------------------------------
function changeEdiLink(uri, ediID) {
	var edidivObs = document.getElementById("EDI_LINK")
	if (edidivObs){
		edidivObs.innerHTML = uri + ediID;
	}	
}