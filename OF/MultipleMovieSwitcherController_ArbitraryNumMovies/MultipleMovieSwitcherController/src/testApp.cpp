#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup(){
	
	ofBackground( 0 );
	
	// open an outgoing connection to HOST:PORT
    for(int i = 0; i < NUM_MOVIES; i++) {
        senders[i].setup( HOST, PORT);
    }
    
    movie = 0;
}

//--------------------------------------------------------------
void testApp::update(){
	
}

//--------------------------------------------------------------
void testApp::draw(){
	// display instructions
    ofDrawBitmapString("Press the number of the movie to switch to.",10,ofGetHeight()/2-10);
    ofDrawBitmapString("Playing movie: "+ ofToString(movie),10,ofGetHeight()/2+10);
	
}

//--------------------------------------------------------------
void testApp::keyPressed  (int key){
    for(int i = 0; i < NUM_MOVIES; i++) {
        int ascii = i + 48;
        if ( key == ascii ){
            movie = i;
            ofxOscMessage m;
            m.setAddress( "/movie/state" );
            cout << "Key Pressed: " << i << "\n";
            m.addIntArg( i );
        
            for(int j = 0; j < NUM_MOVIES; j++) {
                senders[j].sendMessage( m );
            }
            cout << "Play movie number: " << i << "\n";
        }
    }
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


