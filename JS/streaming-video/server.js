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
  console.log(pathname);

  // Serve up files
  if (/screen/.test(pathname)) {
    pathname = '/screen.html';
  }
  else if (/controller/.test(pathname)) {
    pathname = '/controller.html';
  }

  // Parse file extension
  var ext = path.extname(pathname);

  // Map extension to file type
  var typeExt = {
    '.html': 'text/html',
    '.js':   'text/javascript',
    '.css':  'text/css'
  };

  var contentType = typeExt[ext] || 'text/plain';

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
    }
  );
  
}

// WebSockets
var io = require('socket.io').listen(httpServer);

// Keep track of screens
var screens = {};

// Keep track of how many screens have completed each frame
var numSynced = 0;

// Utility function for getting size of screens obj
var sizeOf = function(obj) {
	var num = 0;
	for(var key in obj) {
		num++;
	}
	return num;
}

// For every connection...
io.sockets.on('connection', 

	function (socket) {
	
		console.log("New client: " + socket.id);

		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////// REGULAR OLE SOCKET STUFF ////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////

		socket.on('screen', function(id){
			console.log("Screen " + id + " connected.");
		})

		// Keep track of peer connections
		socket.on('peer_id', function(data) {
			// Data comes in as whatever was sent, including objects
			console.log("Received: 'peer_id' " + data);
			
			// Send it to all of the clients
			socket.broadcast.emit('peer_id', data);
		});	

		// Keep track of disconnected  clients				
		socket.on('disconnect', function() {
			console.log(socket.id + " disconnected.");
		});
	}
);
