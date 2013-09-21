package gui;

import java.awt.AWTException;
import java.awt.Robot;

import processing.core.PApplet;

public class Background extends PApplet {
	
	Robot r;

	public static void main(String[] args) {
		PApplet.main(new String[] {"gui.Background"});
	}
	
	public void init(){
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.addNotify();
		super.init();
	}
	
	public void setup() {
		size(3840,1080);
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		r.mouseMove(width, height);
	}
	
	public void draw() {
		frame.setLocation(0,0);
		background(0);
		//noCursor();
		noLoop();
	}
	
	
}
