function initCanvas(lines, canvasWidth, canvasHeight){
	var canvas = document.getElementById('SNIP_FLOW_CANVAS');
	if (canvas && canvas.getContext) {
  		var ctx = canvas.getContext('2d');
  		
		ctx.clearRect(0, 0, canvasWidth, canvasHeight);
		canvas.style.border = "red 1px solid";
		
		var top = 20;
		for(i = 0; i < lines.length; i++){
			if (lines[i]!="") {
				top = rect(ctx, 20, top, 100, lines[i]);
				top = top + 20;
			}
		}
	}
}


function rect(ctx, x, y, w, content) {
	ctx.beginPath();
	
	var h = y + 10;
	var sizepix = 0;
	var cutline = "";
	var numLines = 1;
	var defaultLineHeight = 13;
	
	var wordlist = content.split(" ")
	for(j = 0; j < wordlist.length; j++){
		sizepix = sizepix + ctx.measureText(wordlist[j] + " ").width;
		cutline = cutline + wordlist[j] + " ";
		if (sizepix > (w - 20)) {
			ctx.fillText(cutline, x + 5, h+3, w-10);
			cutline = "";
			sizepix = 0;
			h = h + defaultLineHeight;
			numLines = numLines + 1;
		}
	}
	
	if (cutline!="") {
		ctx.fillText(cutline, x, h, w);
	}

	ctx.rect(x, y, w, numLines * defaultLineHeight);
	ctx.closePath();
	ctx.stroke();
	
	return h;
}