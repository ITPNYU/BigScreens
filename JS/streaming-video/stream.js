// Set up p2p stream
var Stream = function(socket, callback) {
	
	console.log("New Stream");

	// Stream Video
	var peer_id = null;

	// Register for an API Key:	http://peerjs.com/peerserver
	var peer = new Peer({key: '1h5ritm7owxko6r'});

	// Get an ID from the PeerJS server		
	peer.on('open', function(id) {
	  console.log('My peer ID is: ' + id);
	  peer_id = id;
	  if (socket != null) {
		socket.emit('peer_id', peer_id);
	  }
	});
	
	// Listen for "call" from camera
	peer.on('call', function(incoming_call) {
		console.log("Incoming!");
		incoming_call.answer(null);
		// Listen for incoming data
		incoming_call.on('stream', function(remoteStream) { 
			callback(window.URL.createObjectURL(remoteStream) || remoteStream); 
		});
	});	
}