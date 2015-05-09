function showTopMenu(){
	if (window.hideSlideStatus) {
		clearInterval(hideSlideStatus);
	}
	showSlideStatus = setInterval("showSlide()", 10);
}

function hideTopMenu(){
	clearInterval(showSlideStatus);
	hideSlideStatus = setInterval("hideSlide()", 10);
}

function showSlide(){
	var ie = document.all;
	var mz = document.getElementById && !document.all ? 1: 0;

	if (ie || mz){
		mn = (mz)? document.getElementById("slideTopBar").style : document.all.slideTopBar.style;
		mnc = (mz)? document.getElementById("slideContent").style : document.all.slideContent.style;
	}

	if ((ie || mz) && parseInt(mn.height) <= 20) {
		mn.height = parseInt(mn.height) + 1 + "px";
	} else if (window.showSlideStatus){
		mn.height = 20;
		mnc.display = 'block';
		clearInterval(showSlideStatus);
	}
}

function hideSlide(){
	var ie=document.all;
	var mz = document.getElementById && !document.all ? 1: 0;

	if (ie || mz){
		mn = (mz)? document.getElementById("slideTopBar").style : document.all.slideTopBar.style;
		mnc = (mz)? document.getElementById("slideContent").style : document.all.slideContent.style;
	}

	if ((ie || mz)&&parseInt(mn.height) >= 19) {
		mn.height = (parseInt(mn.height)-1 ) + "px";
	} else if (window.hideSlideStatus){
		mn.height = 4;
		mnc.display = 'none';
		clearInterval(hideSlideStatus);
	}
}
