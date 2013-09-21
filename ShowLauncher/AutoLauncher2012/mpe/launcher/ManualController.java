package mpe.launcher;

import java.io.*;

import mpe.config.ConsoleReader;
import mpe.config.FileParser;

public class ManualController {

	static int connectionPort = 8000;
	static String[] connectionHosts;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.
	static String[] apps;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.
	static String[] names;// = {"localhost"}; // Put all of the IP numbers of the hosts in this array.

	public static void main(String[] args) throws IOException, InterruptedException {

		ConsoleReader reader = new ConsoleReader(System.in);
		String file = "";
		if (args.length > 0) {
			file = args[0];
		} else {

			System.out.println("What file do you want to load?");
			System.out.print("%: ");
			file = reader.readLine();
		}
		init(file);

		ControllerThread[] connections = new ControllerThread[connectionHosts.length];

		System.out.println("Connecting to " + connections.length + " clients");
		for (int i = 0; i < connections.length; i++) {
			connections[i] = new ControllerThread(connectionHosts[i],connectionPort);
			connections[i].start();
		}

		System.out.println("\n\n");

		while (true) {
			menu();
			System.out.print("%: ");
			String line = reader.readLine();
			if (line.trim().equals("k")) {
				System.out.println("Killing app");
				for (int i = 0; i < connections.length; i++) {
					connections[i].broadcast("kill");
				}
			} else if (line.trim().equals("b")) {
				System.out.println("Launch background");
				for (int i = 0; i < connections.length; i++) {
					connections[i].broadcast("background");
				}
			} else if (line.trim().equals("kb")) {
				System.out.println("Killing background");
				for (int i = 0; i < connections.length; i++) {
					connections[i].broadcast("kbackground");
				}
			} else {
				try {
					// If we only want to launch on one client
					String[] tokens = line.trim().split(",");
					if (tokens.length > 1) {
						int id = Integer.parseInt(tokens[1]);
						int client = Integer.parseInt(tokens[0]);
						connections[client].broadcast("*" + apps[id]);
					} else {
						line = line.trim();
						if (line.charAt(0) == '.') {
							//System.out.println("NOTEXT");
							line = line.substring(1,line.length());
							int id = Integer.parseInt(line.trim());
							System.out.println("Launching " + apps[id]);
							for (int i = 0; i < connections.length; i++) {
								connections[i].broadcast("*" + apps[id]);
							}
						} else {
							int id = Integer.parseInt(line.trim());
							System.out.println("Launching " + apps[id]);
							for (int i = 0; i < connections.length; i++) {
								connections[i].broadcast(apps[id]);
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Error");
				}
			}
			System.out.println(line);
		}
	}


	public static void menu() {
		System.out.println("Type b to launch background.");
		System.out.println("Type kb to kill background.");
		System.out.println("Type k to kill app.\n");
		System.out.println("Type # of app to start app.\n");
		System.out.println("Type .# of app to start app without titles.\n");

		for (int i = 0; i < apps.length; i++) { 
			if (i < 10) {
				System.out.print(" ");
			}
			System.out.print(i + ": " + names[i]);
			int spaces = 14 - names[i].length();
			for (int j = 0; j < spaces; j++) {
				System.out.print(" ");
			}
			System.out.println("(" + apps[i] + ")");
		}

		/*System.out.println("To launch on only one host, type host id comma app id");
		for (int i = 0; i < connectionHosts.length; i++) { 
			System.out.println(i + ",#" + " for " + connectionHosts[i]);
		}*/
		System.out.println();

	}

	public static void init(String file) {
		FileParser fp = new FileParser(file);
		if (fp.fileExists()) {
			String hosts = fp.getStringValue("hosts");

			connectionHosts = hosts.split(",");
			for (int i = 0; i < connectionHosts.length; i++) {
				System.out.println("Client IP: " + connectionHosts[i]);
			}

			connectionPort = fp.getIntValue("port");
			System.out.println("Port: " + connectionPort);

			String appls = fp.getStringValue("apps");
			apps = appls.split(",");

			String nameys = fp.getStringValue("names");
			names = nameys.split(",");

			for (int i = 0; i < apps.length; i++) { 
				//System.out.println("Apps to Launch: " +  names[i]+ ": " + apps[i]);
			}
		} else {
			System.out.println("Couldn't find ini file.");
			System.exit(0);
		}
	}

}