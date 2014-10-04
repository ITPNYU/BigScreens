/**
* Built upon Shawn Van Every's 
* microstories example for Live Web
* http://itp.nyu.edu/~sve204/liveweb_fall2014/syllabus.html
*/

// Get on the internet and listen for requests
var http = require('http');
var fs = require('fs'); // Using the filesystem module
var path = require('path');
var httpServer = http.createServer(requestHandler);
httpServer.listen(8080);

// How many clients do we wait for before starting?
// Commands should be: node localServer.js 3
var numScreensReqd = process.argv[2] || 0;

// Serve up files
function requestHandler(req, res) {

	var pathname = req.url;

	// Serve up right file
	if (/screen/.test(pathname)) {
	pathname = '/screen.html';
	}

	// Parse file extension
	var ext = path.extname(pathname);

	// Map extension to file type
	var typeExt = {
	'.html': 'text/html',
	'.js':   'text/javascript',
	'.css':  'text/css',
	'.mov':  'video/quicktime',
	'.mp4':  'video/mp4',
	'.ogg':  'video/ogg',
	'.webm':  'video/webm'
	};

	var contentType = typeExt[ext] || 'text/plain';

	// Stream video
	if(/video/.test(contentType)) {
		//Stream video
		var file = path.resolve(__dirname, path.basename(pathname));
		var range = req.headers.range;
		var positions = range.replace(/bytes=/, "").split("-");
		var start = parseInt(positions[0], 10);

		fs.stat(file, function(err, stats){
			var total = stats.size;
			// if last byte position is not present then it is the last byte of the video file.
			var end = positions[1] ? parseInt(positions[1], 10) : total - 1;
			var chunksize = (end-start)+1;

			res.writeHead(206, { "Content-Range": "bytes " + start + "-" + end + "/" + total, 
			                     "Accept-Ranges": "bytes",
			                     "Content-Length": chunksize,
			                     "Content-Type": contentType});
			var stream = fs.createReadStream(file, { start: start, end: end })
					        .on("open", function() {
							          stream.pipe(res);
								        }).on("error", function(err) {
								          res.end(err);
								        });
			});
	}
	else {
		// Now read and write back the file with the appropriate content type
		fs.readFile(__dirname + pathname,
			function (err, data) {
			  if (err) {
			    res.writeHead(500);
			    return res.end('Error loading ' + pathname);
			  }
			  // Dynamically setting content type
			  res.writeHead(200,{ 'Content-Type': contentType });
			  res.end(data);

			}); 
	}
}


// WebSockets
var io = require('socket.io').listen(httpServer);

// Keep track of user clients
var users = {};
// Keep track of screens
var screens = {};

// Utility function for getting size of screens obj
var sizeOf = function(obj) {
	var num = 0;
	for(var key in obj) {
		num++;
	}
	return num;
}

// For every new socket connection...
io.sockets.on('connection', 

	function (socket) {
		console.log("New client: " + socket.id);

		// Keep track of screens connecting
		socket.on('screen', function(id){
			socket.screenId = id;
			screens[socket.screenId] = socket;
			console.log("Screen " + id + " connected.");

			// Wait for requisite number of screens to connect before starting
			if(sizeOf(screens) >= numScreensReqd) {
				socket.broadcast.emit('ready');
				socket.emit('ready');
			}

		});

		// Send sync command to other screens
		socket.on('sync', function(data) {
			console.log("Sync from master video player.", data);	
			socket.broadcast.emit('sync', data);
		});

		// Keep track of disconnected users and screens				
		socket.on('disconnect', function() {
			console.log(socket.id + " disconnected.");
			// Remove screens
			console.log("Screen " + socket.screenId + " has disconnected");
			delete screens[socket.screenId];
		});	
});