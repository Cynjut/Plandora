function clickBrowseRepository(path, prjid){
	with(document.forms[0]){		
		operation.value = "browseRepository";
		genericTag.value = path;
		id.value = prjid;
	}
	ajaxProcess(document.forms[0], callBackClickBrowseRepository, path, prjid);		
}
			
function callBackClickBrowseRepository(objId) {  
    if(isAjax()){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
		var content = objRequest.responseText;
       	document.getElementById("BROWSE_REPOSISTORY_BODY").innerHTML = content;  
    }  
}




function clickCheckBoxRepository(path, entity){
	with(document.forms[0]){		
		operation.value = "checkBoxRepository";
		genericTag.value = path;
		id.value = entity;
	}
	ajaxProcess(document.forms[0], callBackclickCheckBoxRepository, path, entity);		
}
			
function callBackclickCheckBoxRepository(objId) {  
    if(isAjax()){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
    }  
}



function clickRadioBoxRepository(path, entity){
	with(document.forms[0]){		
		operation.value = "radioBoxRepository";
		genericTag.value = path;
		id.value = entity;
	}
	ajaxProcess(document.forms[0], callBackclickRadioBoxRepository, path, entity);		
}
			
function callBackclickRadioBoxRepository(objId) {  
    if(isAjax()){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
    }  
}



function breakRepositoryEntityLink(msg, pathId, entity) {
	if ( confirm(msg) ) {
		with(document.forms[0]){		
			operation.value = "breakRepositoryEntityLink";
			genericTag.value = pathId;
			id.value = entity;
		}
		ajaxProcess(document.forms[0], callBackbreakRepositoryEntityLink, pathId, entity);		
	}
}

function callBackbreakRepositoryEntityLink(pathId, entity) {  
    if(isAjax()){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon`
       	goToForwardPage(); 
    }  
}


function clickArtifactCategory(catId){
	with(document.forms[0]){		
   		operation.value = "clickArtifactCategory";
   		document.forms['newArtifactForm'].selectedTemplate.value=''; //clean current selected template
    	genericTag.value = catId;
   	}
	ajaxProcess(document.forms[0], callBackClickArtifactCategory, catId);		
}

function callBackClickArtifactCategory(objId) {  
    if(isAjax()){  
       	document.getElementById("ajaxResponse").innerHTML = ""; //hide ajax icon  
		var content = objRequest.responseText;
       	document.getElementById("ARTIFACT_BODY").innerHTML = content;  
    }  
}

function selectTemplate(){
	if (document.forms['newArtifactForm'].selectedTemplate.value.length==0) {
		alert('The template field is mandatory'); return 0;
	}
	if (document.forms['newArtifactForm'].name.value.length==0) {
		alert('The name field is mandatory'); return 0;
	}		
	var tplid = document.forms['newArtifactForm'].selectedTemplate.value;
	var name = document.forms['newArtifactForm'].name.value;
	var prjid = document.forms['newArtifactForm'].projectId.value;
	var pid = document.forms['newArtifactForm'].id.value;
	window.location = "../do/manageArtifact?operation=prepareForm&templateId=" + tplid + "&name=" + name + "&projectId=" + prjid + "&planningId=" + pid;;
}
