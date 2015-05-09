var objFloatPanel
var topFloatPanel = -1000;
var ns4floatPanel = document.layers;
var ns6floatPanel = document.getElementById && !document.all;
var xOffSetFloatPanel = 0, yoffSetFloatPanel = 0;
var ie4floatPanel = document.all;
var floatPanelmenu = true;
var cont = 0;
var vkillcalend = 0;

if (ns4floatPanel) {
	objFloatPanel = document.floatPanel; 
} else if (ns6floatPanel) {
	objFloatPanel = document.getElementById("floatPanel").style; 
} else if (ie4floatPanel) {
	objFloatPanel = document.all.floatPanel.style; 
}

if(ns4floatPanel) {
	document.captureEvents(Event.MOUSEMOVE); 
} else { 
	objFloatPanel.visibility = "hidden";  
}
document.onclick = getMouseFloatPanel;
document.onkeydown = keyPressFloatPanel;

function openFloatPanel(contentfloatPanel){
	floatPanelmenu = true; cont=0;
	topFloatPanel = yoffSetFloatPanel;
	if(ns4floatPanel){
		objFloatPanel.document.open();
		objFloatPanel.visibility = "visible";
		objFloatPanel.document.write(contentfloatPanel);
		objFloatPanel.document.close();
	}
	if(ns6floatPanel){
		objFloatPanel.visibility = "visible";
		document.getElementById("floatPanelContent").innerHTML = contentfloatPanel;
	}
	if(ie4floatPanel){
		objFloatPanel.visibility = "visible";
		document.all("floatPanelContent").innerHTML = contentfloatPanel;
	}
}

function getMouseFloatPanel(e){
	if(cont <= 1){
		cont++;
	}
	if(cont==0 && objFloatPanel.visibility=="hidden"){
		cont=0
	}
	if(floatPanelmenu && cont==1){
		var x=(ns4floatPanel||ns6floatPanel)?e.pageX:event.x+document.body.scrollLeft;
		var y=(ns4floatPanel||ns6floatPanel)?e.pageY:event.y+document.body.scrollTop;
		objFloatPanel.left= x + xOffSetFloatPanel + "px";
		objFloatPanel.top= y + topFloatPanel + "px";
	}
}

function closeFloatPanel() {
	topFloatPanel = -1000;
	if(ns4floatPanel){
		objFloatPanel.visibility = "hidden";
        } else if (ns6floatPanel || ie4floatPanel) {
			objFloatPanel.visibility = "hidden";
		}
}

function keyPressFloatPanel(e) {
	var keyfloatPanel;
	if(ie4floatPanel) {
		e=window.event; keyfloatPanel = e.keyCode;
	}
	if(ns4floatPanel || ns6floatPanel) {
		keyfloatPanel = e.which;
	}
	if(keyfloatPanel == 27) {
		closeFloatPanel();
	}
}