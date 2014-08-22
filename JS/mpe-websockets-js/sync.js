// MPE-ize the socket
var sync = function(socket) {

	// Tell server a sync client has connected
	socket.on('connect', function() {
		console.log("Connected to mpe server.");
		socket.emit('sync', id);
	});

	// Manually keep track of what frame this screen is on
	frame = 0;

	// Reset - automatically resets when new sync client connects
	socket.on('reset', function(){
		console.log("Reset");
		reset();
		frame = 0;
		socket.emit('done', { id : id, frameCount : frame });
	})

	// Advance frame
	socket.on('frameEvent', function(nextFrame){
		// Catch up if you have to
		while(nextFrame > frame) {
			frame++;
			frameEvent();
			setTimeout(function(){
				socket.emit('done', { id : id, frameCount : frame });
			}, FRAMERATE_IN_MS)
		}
	});

	// Place screen
	setTimeout(function(){
		var body = document.getElementsByTagName('body')[0];
		body.style.width = TOTAL_WIDTH + 'px';
		body.style.height = HEIGHT + 'px';
		var scale = 'scale(' + SCALE + ',' + SCALE + ')';
		body.style.transform = scale;
		body.style['-webkit-transform'] = scale;
		body.style['-ms-transform'] = scale;
		body.style['transform-origin'] = '0 0';
		body.style.position = 'relative';
		body.style.left = (-id*100) + '%';
	}, 1000);
}