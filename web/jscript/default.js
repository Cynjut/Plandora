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


function removeAttachment(argId, argForm, argFwd, message){
    if ( confirm(message)) {
		window.location = "../do/manageAttachment?operation=removeAttachment&fwd=" + argFwd + "&id=" + argId;
    }
}    


function downloadAttachment(argId){
	window.location = "../do/downloadAttachment?id=" + argId;
}

//--------------------------------------
// Generic function to call the MindMap popup
//--------------------------------------
function loadMindMap(id){
	window.open('../do/viewMindMap?operation=prepareForm&entityid=' + id, 'MindMap', 'width=640, height=300, location=no, menubar=no, status=no, toolbar=no, scrollbars=no, resizable=no');
}

//--------------------------------------
// Generic routines performed by 'get user info' ajax feature
//--------------------------------------
function showUserInfo(argForm, username){
	with(document.forms[argForm]){
		operation.value = "getUserInfo";
   		id.value = username;
    }         
	ajaxProcess(document.forms[argForm], callBackShowUserInfoClick, username);		
}
function callBackShowUserInfoClick(username) {  
    if(isAjax()){  
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


//--------------------------------------
// This function can be used by forms to jump to html anchors
//--------------------------------------
function goToAnchor(anc) {
	this.location = "#" + anc;
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

