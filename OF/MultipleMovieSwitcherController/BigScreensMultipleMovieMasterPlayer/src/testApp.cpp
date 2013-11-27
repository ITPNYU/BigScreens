#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup(){

	looping = false;
	started = false;
	fullscreen = false;
	count = 0;
	
 	// Load initial settings
	loadSettings("settings.xml");
	ofSetVerticalSync(true);

    // Set up OSC
	receiver.setup( port );
    

	// open an outgoing connection to HOST:PORT
	sender1.setup( host1, port1 );
	sender2.setup( host2, port2 );
	
	// Load movie
    for(int i = 0; i < NUM_MOVIES; i++) {
        movies[i].loadMovie(movieFiles[i]);
        movies[i].play();
        movies[i].update();
    }
    
    state = 0;

	// Turns looping off
	if (!looping) {
		movies[state].setLoopState(OF_LOOP_NONE);
	} else {
		movies[state].setLoopState(OF_LOOP_NORMAL);
	}
	
	ofBackground( 0, 0, 0 );

}

//--------------------------------------------------------------
void testApp::update(){
    for(int i = 0; i < NUM_MOVIES; i++) {
        movies[i].setPaused(state != i);
        cout << "Movie " << i << " is" << (movies[i].isPaused() ? " not" : "") << " playing.\n";
    }
	movies[state].update();
    
	float p = movies[state].getPosition();
	//printf("%f\n",p);
	
	// Broadcast current position information of movie
	if ((!started || count % howOften == 0)) {
		ofxOscMessage m;
		m.setAddress( "/movie/position" );
		m.addFloatArg(p);
		sender1.sendMessage(m);
		sender2.sendMessage(m);
		started = true;
	}
    
    while( receiver.hasWaitingMessages() ) {
		// Get the next message
		ofxOscMessage m;
		receiver.getNextMessage( &m );
        // Receive which movie to play
        if( m.getAddress() == "/movie/state" ) {
            state = m.getArgAsInt32(0);
            cout << "message received: " << state << "\n";
            
            // Re-broadcast to client players
            sender1.sendMessage(m);
            sender2.sendMessage(m);
        }
    }
    
	count++;
}

//--------------------------------------------------------------
void testApp::draw(){
	if (fullscreen) {
		ofHideCursor();
	}
	// Display the movie
	movies[state].draw(movieX,movieY,movieWidth,movieHeight);
}

//--------------------------------------------------------------
void testApp::keyPressed(int key){
}

//--------------------------------------------------------------
void testApp::keyReleased(int key){

}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y){

}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){

}

//--------------------------------------------------------------
void testApp::gotMessage(ofMessage msg){

}

//--------------------------------------------------------------
void testApp::dragEvent(ofDragInfo dragInfo){

}

void testApp::loadSettings(string fileString){
	
	string host_address1;
	string host_address2;
	string filename;
	
	//--------------------------------------------- get configs
    ofxXmlSettings xmlReader;
	bool result = xmlReader.loadFile(fileString);
	if(!result) printf("error loading xml file\n");
	
	host_address1 = xmlReader.getValue("settings:client1:address","test",0);
	port1 = xmlReader.getValue("settings:client1:port",5204,0);
	host1 = (char *) malloc(sizeof(char)*host_address1.length());
	strcpy(host1, host_address1.c_str());
	
	host_address2 = xmlReader.getValue("settings:client2:address","test",0);
	port2 = xmlReader.getValue("settings:client2:port",5204,0);
	host2 = (char *) malloc(sizeof(char)*host_address2.length());
	strcpy(host2, host_address2.c_str());
	
	xmlReader.pushTag("settings");
    for(int i = 0; i < NUM_MOVIES; i++) {
        string filename = xmlReader.getValue("movie:","test",i);
        movieFiles[i] = (char *) malloc(sizeof(char)*filename.length());
        strcpy(movieFiles[i], filename.c_str());
        cout << "movie loaded: " << filename << "\n";
    }
	xmlReader.popTag();
	
	howOften = xmlReader.getValue("settings:howoften",60,0);
	port = xmlReader.getValue("settings:port",9999,0);
    
	int w = xmlReader.getValue("settings:dimensions:width", 640, 0);
	int h = xmlReader.getValue("settings:dimensions:height", 480, 0);
	
	movieWidth = xmlReader.getValue("settings:dimensions:movieWidth", 640, 0);
	movieHeight = xmlReader.getValue("settings:dimensions:movieHeight", 480, 0);
	
	movieX = xmlReader.getValue("settings:dimensions:movieX", 640, 0);
	movieY = xmlReader.getValue("settings:dimensions:movieY", 480, 0);
	
	ofSetWindowShape(w, h);
	
	if(xmlReader.getValue("settings:loop", "false", 0).compare("true") == 0) {
		looping = true;
	}
	
	if(xmlReader.getValue("settings:go_fullscreen", "false", 0).compare("true") == 0) {
		fullscreen = true;
		ofSetFullscreen(true);
	}
}


