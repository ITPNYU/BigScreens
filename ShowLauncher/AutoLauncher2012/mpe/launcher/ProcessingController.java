package mpe.launcher;

import java.io.*;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

import mpe.config.ConsoleReader;
import mpe.config.FileParser;

public class ProcessingController extends PApplet {

	int connectionPort = 9005;
	String[] connectionHosts;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.
	ArrayList apps;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.
	ControllerThread[] connections;

	int counter = 0;
	int start = 0;
	Project current;
	Project next;

	PFont f;

	public static void main(String[] args) {
		PApplet.main(new String[] {"mpe.launcher.ProcessingController"});
	}

	public void setup() {
		size(1200,200);
		f = createFont("MyriadPro-Bold",24,true);
		String file = "test.txt";
		apps = new ArrayList();
		init(file);


		connections = new ControllerThread[connectionHosts.length];

		System.out.println("Connecting to " + connections.length + " clients");
		for (int i = 0; i < connections.length; i++) {
			connections[i] = new ControllerThread(connectionHosts[i],connectionPort);
			connections[i].start();
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("\n\n");

		killBackground();
		launchBackground();

		delay(1000);



		nextProject();


	}

	public void killBackground() {
		System.out.println("Killing background");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("kbackground");
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void launchBackground() {
		System.out.println("Launching background");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("background");
		}		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void killProject() {

		System.out.println("Killing app");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("kill");
		}
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void nextProject() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		current = (Project) apps.get(counter);
		System.out.println("Launching " + current.title + " by " + current.name);
		for (int i = 0; i < connections.length; i++) {
			if (current.title.equals("P.Life")) {
				connections[i].broadcast("!" + current.path);
			} else if (current.titles) {
				connections[i].broadcast(current.path);
			} else {
				connections[i].broadcast("*" + current.path);
			}
		}
		counter = (counter + 1) % apps.size();
		next = (Project) apps.get(counter);
		start = millis();

	}

	public void draw() {
		background(255);
		textFont(f,36);
		String s = "Now playing: " + current.title + " by " + current.name;
		fill(0);
		text(s,10,40);

		int howLong = current.time*1000;
		int elapsed = millis() - start;
		int timeLeft = howLong - elapsed;


		s =  timeLeft/1000 + " seconds to go.";
		s += "\nNext up: " + next.title + " by " + next.name; 
		textFont(f,24);
		text(s,10,80);





		if (millis() - start > howLong) {
			killProject();
			nextProject();
		}


		textFont(f,12);
		stroke(0);
		fill(175);
		if (mouseX > 10 && mouseX < 170 && mouseY > 150 && mouseY < 170) {
			fill(100);
		}
		rect(10,150,160,20);
		fill(0);
		text("click here to quit everything",20,164);

	}



	public void mousePressed() {
		if (mouseX > 10 && mouseX < 170 && mouseY > 150 && mouseY < 170) {
			killAll();
		}
	}



	public void init(String file) {

		String[] lines = loadStrings(file);
		connectionHosts = lines[0].split(",");
		for (int i = 0; i < connectionHosts.length; i++) {
			System.out.println("Client IP: " + connectionHosts[i]);
		}

		connectionPort = Integer.parseInt(lines[1]);
		System.out.println("Port: " + connectionPort);


		for (int i = 2; i < lines.length; i++) {
			String[] project = lines[i].split(",");
			if (project[0].charAt(0) == '#') {
				// SKIP
			} else {
				Project p = new Project(project[1],project[0],Integer.parseInt(project[3]),project[2],Boolean.parseBoolean(project[4]));
				p.print();
				apps.add(p);
			}
		}

	}

	public void keyPressed() {
		try {
			int i;
			if (key == 'a') {
				i = 10;
			} else if (key == 'b') {
				i = 11;
			} else if (key == 'c') {
				i = 12;
			} else {
				i = Integer.parseInt("" + (char) key);
			}
			if (i < apps.size()) {
				counter = i;
				killProject();
				nextProject();
			}
		} catch (Exception e) {

		}

	}

	public void killAll() {
		killBackground();
		killProject();
		System.exit(0);
	}

}


