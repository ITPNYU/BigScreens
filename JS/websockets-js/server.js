// Get on the internet and listen for requests
var http = require('http');
var fs = require('fs'); // Using the filesystem module
var path = require('path');
var httpServer = http.createServer(requestHandler);
httpServer.listen(8080);

// How many clients do we wait for before starting?
// Commands should be: node localServer.js 3
var numScreensReqd = process.argv[2];

// Serve up files
function requestHandler(req, res) {

  var pathname = req.url;
  console.log(pathname);

  // Serve up right file
  if (/screen/.test(pathname)) {
    pathname = '/screen.html';
  }
  else if(/[0-2]/.test(pathname)) {
  	pathname = '/user.html';
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

		function broadcastToUsers(screenId, msg, data) {
			var thisScreensUsers = users[screenId]
			if(thisScreensUsers) {
				for(var userId in thisScreensUsers) {
					thisScreensUsers[userId].emit(msg, data);
				}
			}
		}

		// Keep track of users connecting
		socket.on('user', function(data){
			socket.userId = data.userId;
			socket.screenId = data.screenId;
			if(!(socket.screenId in users)) {
				users[socket.screenId] = {};
			}
			users[socket.screenId][socket.userId] = socket;
			console.log("User " + data.userId + " connected.");

			// See if this user's screen is ready and add user
			if(screens[socket.screenId]) {
				socket.emit('ready');
				screens[socket.screenId].emit('add', socket.userId);
			}
		});

		// Keep track of screens connecting
		socket.on('screen', function(id){
			socket.screenId = id;
			screens[socket.screenId] = socket;
			console.log("Screen " + id + " connected.");

			// Wait for requisite number of screens to connect before starting
			if(sizeOf(screens) >= numScreensReqd) {
				// Tell all the users in front of this screen, screen is ready, if there are any
				broadcastToUsers(socket.screenId, 'ready');
			}

			// Add already connected users to this screen
			var thisScreensUsers = users[socket.screenId]
			if(thisScreensUsers) {
				for(var userId in thisScreensUsers) {
					screens[socket.screenId].emit('add', userId);
				}
			}
		});

		// // Send poke data to screens
		socket.on('poke', function(data) {
			console.log("Poke from user in front of screen " + data.screenId);	

			// Poke user on screen
			if(screens[data.screenId]) {		
				screens[data.screenId].emit('poked', data.userId);
			}
		});

		// Keep track of disconnected users and screens				
		socket.on('disconnect', function() {
			console.log(socket.id + " disconnected.");

			// Remove users
			if(socket.userId) {
				console.log("User " + socket.userId + " has disconnected.");
				delete users[socket.screenId][socket.userId];

				// Remove user from screen
				if(screens[socket.screenId]) {
					screens[socket.screenId].emit('remove', socket.userId);
				}
			}
			// Remove screens
			else if(socket.screenId) {
				console.log("Screen " + socket.screenId + " has disconnected");
				delete screens[socket.screenId];
				// Tell users of that screen, screen is down
				broadcastToUsers(socket.screenId, 'down');
			}
		});	
});