var httpAction = "";  
var httpMethod = "";  

// Creates a new instante of HttpRequest.   
function ajaxSyncInit() { 
	var localObjRequest;

	if (window.XMLHttpRequest) {

   		// Mozilla,Safari, Chrome, IE7+
   		try {  
	   		localObjRequest = new XMLHttpRequest();  
		} catch(exc) {  
	       	alert("The browser cannot handler the Ajax resources.");  
	       	localObjRequest = false;  
		}

	} else if (window.ActiveXObject) {
		// IE6-
		try {
			localObjRequest = new ActiveXObject("Microsoft.XMLHTTP");
		} catch(e) {
			try {  
	   			localObjRequest = new ActiveXObject("Msxml2.XMLHTTP"); 
			} catch(ex) {  
	       		alert("The browser cannot handler the Ajax resources.");  
   				localObjRequest = false;  
			}
		}
	}

	return localObjRequest;
}

   
// Gets the form fields in order to concatenate the variable (contentOfRequest) that must be sent to server
function getSyncDataForm(myForm, argId, argTp) {
	if(myForm != "" && myForm.action != undefined){  
    	httpAction = myForm.action;  
       	httpMethod = myForm.method;  
       	contentOfRequest = "";  
       	for(i = 0; i < myForm.elements.length; i++){  
        	elementos = myForm.elements[i];  
       		if(elementos.name != "" && elementos.name != "theFile"){  
            	contentOfRequest += elementos.name + "=" + escape(elementos.value) + "&";  
          	}  
       	}  
		contentOfRequest += "ajaxGridId=" + argId + "&";  
		contentOfRequest += "ajaxGridParam=" + argTp + "&";  
		
       	contentOfRequest = contentOfRequest.substring(0, contentOfRequest.length -1);  
	} else {  
    	alert("Invalid Form.");  
    }  
}  
   
// Process the request to Server through the form struts
function ajaxSyncProcess(myForm, fuctionProcess, argId, argTp, objRequest) {
	if(objRequest){  
    	getSyncDataForm(myForm, argId, argTp);  
       	objRequest.open(httpMethod, httpAction, true);
       	objRequest.onreadystatechange = fuctionProcess;  
  	    objRequest.onreadystatechange = function() { 	
           	fuctionProcess(argId, argTp, objRequest);
   	    }
       	
        if(httpMethod == "post"){  
           	objRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");  
        }  
        contentOfRequest = contentOfRequest != "" ? contentOfRequest : null;  
       	objRequest.send(contentOfRequest); 
	
	}  
}  
   
// Checks if the HttpRequest object contain a valid response.  
function isSyncAjax(objRequest) {				  
	if(objRequest.readyState == 4){  
		if(objRequest.status == 200){  
			return true;  
		}  
	}  
    return false;  
} 