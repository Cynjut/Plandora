var objRequest;  
var action = "";  
var method = "";  
var conteudoRequisicao = "";  

// Creates a new instance of HttpRequest.  
function ajaxInit() {  
	try {  
		objRequest = new ActiveXObject("Microsoft.XMLHTTP");  
	} catch(e) {  
		try {  
      		objRequest = new ActiveXObject("Msxml2.XMLHTTP");  
		} catch(ex) {  
      		try {  
				objRequest = new XMLHttpRequest(); 
				objRequest.overrideMimeType("text/html; charset=ISO-8859-1");
			} catch(exc) {  
		       	alert("The browser cannot handler the Ajax resources.");  
       			objRequest = false;  
			}  
     	}  
    }  
}  
   
// Gets the form fields in order to concatenate the variable (contentOfRequest) that must be sent to server
function getDataForm(myForm) {
	if(myForm != "" && myForm.action != undefined){  
    	action = myForm.action;  
       	method = myForm.method;  
       	contentOfRequest = "";  
       	for(i = 0; i < myForm.elements.length; i++){  
        	elementos = myForm.elements[i];  
          	if(elementos.name != ""){  
            	contentOfRequest += elementos.name + "=" + escape(elementos.value) + "&";  
          	}  
       	}  
       	contentOfRequest = contentOfRequest.substring(0, contentOfRequest.length -1);  
	} else {  
    	alert("Invalid Form.");  
    }  
}  
   

// Process the request to Server through the form struts
function ajaxProcess(myForm, fuctionProcess, argId) {
	if(!objRequest){
		ajaxInit();
	}
		  
	if(objRequest){  
		document.getElementById("ajaxResponse").innerHTML = "<img src='../images/indicator.gif' border='0'>";
    	getDataForm(myForm);  
       	objRequest.open(method, action, true);  
       	objRequest.onreadystatechange = fuctionProcess;  
	    objRequest.onreadystatechange = function() { 
        	fuctionProcess(argId);
	    }
       	
        if(method == "post"){  
           	objRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");  
        }  
        contentOfRequest = contentOfRequest != "" ? contentOfRequest : null;  
       	objRequest.send(contentOfRequest);  
	}  
}  

// Process request to server using GET format
function ajaxProcessUrl(url, fuctionProcess, argId) {  
	if(objRequest && url != ""){  
		method = "GET";  
		action = url;  
       	objRequest.open(method, action, true);  
	    objRequest.onreadystatechange = function() { 
        	fuctionProcess(argId);
	    }       	
		objRequest.send(null);  
	}  
}  

// send a synchronous request to server using GET format
function synchRequest(url) {
	var response = "";
	if(!objRequest){
		ajaxInit();
	}

	if(objRequest){  
       	objRequest.open("get", url, true);  
       	objRequest.send(null);
       	response = objRequest.responseText;
	}  
    
    return response;
}

   
// Checks if the HttpRequest object contain a valid response.  
function isAjax() {  
	if(objRequest.readyState == 4){  
		if(objRequest.status == 200){  
			return true;  
		//} else {  
		//	alert("Error: "+objRequest.statusText);  
		}  
	}  
    return false;  
}  
   
// Starts HttpRequest.  
window.onload=ajaxInit  