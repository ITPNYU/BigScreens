// Scale the body for testing in local mode
setTimeout(function(){
	var body = document.body;
	body.style.width = WIDTH + 'px';
	body.style.height = HEIGHT + 'px';
	var scale = 'scale(' + SCALE + ',' + SCALE + ')';
	body.style.transform = scale;
	body.style['-webkit-transform'] = scale;
	body.style['-ms-transform'] = scale;
	body.style['transform-origin'] = '0 0';
}, 500);