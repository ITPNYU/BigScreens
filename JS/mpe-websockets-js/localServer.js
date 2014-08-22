// Get on the internet and listen for requests
var http = require('http');
var fs = require('fs'); // Using the filesystem module
var path = require('path');
var httpServer = http.createServer(requestHandler);
httpServer.listen(8080);

// How many clients do we wait for before starting?
// Commands should be: node localServer.js 3 false
var numScreensReqd = process.argv[2] || 0;
var isVerbose = process.argv[3];

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
var synced = {};

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
		//////////////////////// MPE-LIKE STUFF /////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		function broadcastToScreens(msg, data) {
			//console.log("Broadcasting: " + msg);
			for(var screenId in screens) {
				screens[screenId].emit(msg, data);
			}
		}

		// Keep track of number of synced screens that have connected
		socket.on('sync', function(id){
			socket.screenId = id;
			screens[socket.screenId] = socket;
			console.log("Screen " + id + " connected.");

			// Wait for "requred number of screens" to start
			// Default is 0
			if(sizeOf(screens) >= numScreensReqd) {
				broadcastToScreens('reset');
			}
		});

		// Frame event - syncing up all Sync clients
		socket.on('done', function(data){
			if(isVerbose) console.log("Screen " + data.id + " finished frame " + data.frameCount);
			synced[data.id] = true;

			var allSynced = function() {
				for(var screenId in synced) {
					if(!synced[screenId]) {
						return false;
					}
				}
				return true;
			}

			if(allSynced()) {
				if(isVerbose) console.log("Tally ho! All screens ready to go.");
				synced = {};
				broadcastToScreens('frameEvent', data.frameCount+1);	
			}
		});

		// Remove disconnected screens from screens lookup obj		
		socket.on('disconnect', function() {
			console.log("Screen " + socket.screenId + " has disconnected");
			if(socket.screenId) {
				delete screens[socket.screenId];
			}
		});

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

		// Keep track of controller(s);
		socket.on('controller', function(){
			console.log("Controller connected.");
		})
		
		// Send mouse data to all clients
		socket.on('mouse', function(data) {
			// Data comes in as whatever was sent, including objects
			if(isVerbose) console.log('Received: mouse', data);			
			broadcastToScreens('mouse', data);
		});

		// Send erase event
		socket.on('erase', function() {	
			console.log('Erase!');				
			broadcastToScreens('erase');		
		});

		// Keep track of disconnected  clients				
		socket.on('disconnect', function() {
			console.log(socket.id + " disconnected.");
		});
	}
);
