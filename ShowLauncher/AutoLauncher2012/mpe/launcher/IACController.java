package mpe.launcher;

import java.io.*;
import java.util.ArrayList;

import mpe.config.ConsoleReader;
import mpe.config.FileParser;

public class IACController {

	int connectionPort = 9005;
	String[] connectionHosts;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.


	ArrayList loopApps;
	ArrayList allApps;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.

	boolean autoloop;
	boolean background = false;

	ControllerThread[] connections;

	int counter = 0;
	int start = 0;
	Project current;
	Project next;

	long startTime = 0;

	int frameCounter = 0;

	ConsoleReader reader;

	LoopThread looper;

	public static void main(String[] args) {
		IACController control = new IACController();
		String file = "";
		if (args.length > 0) {
			file = args[0];
		} else {
			file = null;
		}
		control.go(file);
		//control.go("test.txt");
	}

	public void go(String file) {

		startTime = System.currentTimeMillis();

		reader = new ConsoleReader(System.in);
		//file = "iac2010.txt";
		if (file == null) {
			System.out.println("What file do you want to load?");
			System.out.print("%: ");
			file = reader.readLine();
		}
		allApps = new ArrayList();
		loopApps = new ArrayList();
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

		autoloop = false;

		System.out.println("\nWould you like to run projects manually or loop?");
		System.out.println("Type 'm' for manual, 'l' for loop");
		System.out.print("%: ");
		String input = reader.readLine();
		if (input.toLowerCase().equals("m")) {
			autoloop = false;
		} else {
			autoloop = true;
		}


		while (true) {
			if (autoloop) {
				looper = new LoopThread(this);
				System.out.println("\nStarting automatic loop\n");
				//killBackground();
				if (!background) launchBackground();
				nextProject(true,true);
				looper.start();
				while (autoloop) {
					loopMenu();
				}
			} 

			while (!autoloop) {
				menu();
			}
		}


	}

	public void killBackground() {
		System.out.println("Killing background");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("kbackground");
		}
		background = false;
	}
	
	public void killTitles() {
		System.out.println("Killing titles only");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("ktitles");
		}
		background = false;
	}
	

	public void launchBackground() {
		System.out.println("Launching background");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("background");
		}		
		background = true;
	}

	public void killProject() {
		System.out.println("Killing app");
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("kill");
		}
	}

	public void nextProject(boolean titles, boolean automatic) {
		ArrayList projectList;
		if (autoloop) projectList = loopApps;
		else projectList = allApps;

		current = (Project) projectList.get(counter);

		System.out.println("Launching " + current.title + " by " + current.name);
		for (int i = 0; i < connections.length; i++) {
			if (current.titles && titles && !automatic) {
				connections[i].broadcast("$" + current.path);
			} else if (current.titles && titles && automatic) {
				connections[i].broadcast("!" + current.path);
			} else {
				connections[i].broadcast("*" + current.path);
			}
		}
		counter = (counter + 1) % projectList.size();
		next = (Project) projectList.get(counter);
		start = millis();
	}

	public void advanceToProject() {
		for (int i = 0; i < connections.length; i++) {
			connections[i].broadcast("#" + current.path);
		}

	}

	int millis() {
		return (int) (System.currentTimeMillis() - startTime);
	}

	String[] loadStrings(String file) {

		ArrayList a = new ArrayList();
		try{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				a.add(strLine);
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}

		String[] lines = new String[a.size()];
		for (int i = 0; i < a.size(); i++) {
			String l =(String) a.get(i);
			lines[i] = l;
		}
		return lines;
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
			//System.out.println(lines[i]);
			String[] project = lines[i].split(",");
			if (project[0].charAt(0) == '#') {
				// SKIP
			} else if (project[0].charAt(0) == '!') {
				Project p = new Project(project[1],project[0],Integer.parseInt(project[3]),project[2],Boolean.parseBoolean(project[4]));
				//p.print();
				allApps.add(p);
			} else {
				Project p = new Project(project[1],project[0],Integer.parseInt(project[3]),project[2],Boolean.parseBoolean(project[4]));
				//p.print();
				allApps.add(p);
				loopApps.add(p);
			}
		}

	}




	public void menu() {
		System.out.println("Type b to launch background.");
		System.out.println("Type kb to kill background.");
		System.out.println("Type k to kill app.");
		System.out.println("Type # of app to start titles and n to continue to app.");
		System.out.println("Type kt to kill titles only");
		System.out.println("Type .# to run app directly.");
		System.out.println("Type !# to run app with titles auto-advancing to app");
		System.out.println("Type l to go back to loop.\n");
		System.out.println("Type q to quit.\n");
		//System.out.println("Type .# of app to start app without titles.");

		for (int i = 0; i < allApps.size(); i++) { 
			Project p = (Project) allApps.get(i);
			if (i < 10) {
				System.out.print(" ");
			}
			System.out.print(i + ": " + p.title);
			int spaces = 16 - p.title.length();
			for (int j = 0; j < spaces; j++) {
				System.out.print(" ");
			}
			System.out.print("(" + p.name + ")");

			spaces = 24 - p.name.length();
			for (int j = 0; j < spaces; j++) {
				System.out.print(" ");
			}


			String titles = "xxxxx";
			if (p.titles)titles = "titles"; 
			System.out.println(" " + titles);
		}

		/*System.out.println("To launch on only one host, type host id comma app id");
		for (int i = 0; i < connectionHosts.length; i++) { 
			System.out.println(i + ",#" + " for " + connectionHosts[i]);
		}*/
		System.out.println();

		System.out.print("%: ");
		String line = reader.readLine();
		if (line.trim().equals("k")) {
			killProject();
		} else if (line.trim().equals("b")) {
			launchBackground();
		} else if (line.trim().equals("q")) {
			killAll();
		} else if (line.trim().equals("l")) {
			killAllButBackground();
		} else if (line.trim().equals("kb")) {
			killBackground();
		} else if (line.trim().equals("kt")) {
			killTitles();
		} else if (line.trim().equals("n")) {
			advanceToProject();
		} else {
			try {
				line = line.trim();
				if (line.charAt(0) == '.') {
					line = line.substring(1,line.length());
					int id = Integer.parseInt(line.trim());
					counter = id;
					killProject();
					nextProject(false,true);
				} else if (line.charAt(0) == '!'){
					line = line.substring(1,line.length());
					int id = Integer.parseInt(line.trim());
					counter = id;
					killProject();
					nextProject(true,true);
				} else {
					int id = Integer.parseInt(line.trim());
					counter = id;
					killProject();
					nextProject(true,false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	public void loopMenu() {
		System.out.println("\n  ****** Type q and hit enter at any time to quit loop!       ******");
		System.out.println("\n  ****** Type x and hit enter at any time to quit everything! ******");
		String line = reader.readLine();
		if (line.trim().equals("q")) {
			killAllButBackground();
		} 
		if (line.trim().equals("x")) {
			killAll();
		} 

	}

	public void killAll() {
		killBackground();
		killProject();
		System.exit(0);
	}

	public void killAllButBackground() {
		killProject();
		counter = 0;
		autoloop = !autoloop;
		if (!autoloop) {
			looper.quit();
			looper = null;
		}
	}

}


