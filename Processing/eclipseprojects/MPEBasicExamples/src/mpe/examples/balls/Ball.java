/**
 * Ball class for simple bouncing ball demo
 */

package mpe.examples.balls;

import mpe.client.TCPClient;
import processing.core.PApplet;

public class Ball {
	
	PApplet parent;
	TCPClient client;
    
	float x = 0;    // Ellipse x location
    float y = 0;    // Ellipse y location
    float xdir = 1; // x velocity
    float ydir = 1; // y velocity
    
    float r = 24; // size
	
    Ball(PApplet _parent, TCPClient _client, float _x, float _y){
		parent = _parent;
		client = _client;
        xdir = parent.random(-5,5);
        ydir = parent.random(-5,5);
		x = _x;
		y = _y;
	}

    // A simple bounce across the screen
    public void calc(){
    	// Note the use of the master width!
    	if (x < 0 || x > client.getMWidth()) xdir *= -1;
    	if (y < 0 || y > client.getMHeight()) ydir *= -1;
    	x += xdir;
    	y += ydir;
    }
	
	public void draw(){
		parent.stroke(0);
		parent.fill(0,100);
		parent.ellipse(x,y,r,r);
	}
}
