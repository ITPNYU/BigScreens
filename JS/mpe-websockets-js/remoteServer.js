/**
* Built upon Shawn Van Every's 
* microstories example for Live Web
* http://itp.nyu.edu/~sve204/liveweb_fall2014/syllabus.html
*/

// Get on the internet and listen for requests
var http = require('http');
var fs = require('fs'); // File system module
var path = require('path');
var httpServer = http.createServer(requestHandler);
httpServer.listen(8081);

// How many clients do we wait for before advancing?
var numSyncReqd = process.argv[2];

// Serve up files
function requestHandler(req, res) {

  var pathname = req.url;
  console.log(pathname);

  // Serve up user interface
  if (pathname == '/') {
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

  // Serve up appropriate file
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

// For every new socket connection...
io.sockets.on('connection', 
	
	function (socket) {
	
		console.log("New client: " + socket.id);

		function broadcastToUsers(msg, data) {
			console.log("Broadcasting: " + msg);
			userClients.forEach(function(client){
				client.emit(msg, data);
			})
		}

		// User connected
		socket.on('user', function(id){
			socket.userId = id;
			users[socket.userId] = socket;
			console.log("User " + socket.userId + " connected.");
		})

		// Controller connected
		socket.on('controller', function(){
			console.log("Controller connected.")
		})

		// Screen connected
		socket.on('screen', function(screenId){
			console.log("Screen " + screenId + " connected.")
		})

		// Send mouse data to all clients
		socket.on('mouse', function(data) {
			// Data comes in as whatever was sent, including objects
			//console.log('Received: mouse', data);			
			socket.broadcast.emit('mouse', data);
		});

		// Listen for erase events
		socket.on('erase', function() {	
			console.log('Erase!');				
			socket.broadcast.emit('erase');		
		});
				
		// Keep track of disconnected users				
		socket.on('disconnect', function() {
			console.log(socket.id + " disconnected.");
			if(socket.userId) {
				console.log("User " + socket.userId + " has disconnected");
				delete users[socket.userId];
			}
		});
	}
);
