/**
 * Simple Template for the Big Screens Class, Fall 2013
 * <https://github.com/ITPNYU/BigScreens>
 * 
 * This class is designed auto-detect the ID number based on IP address for using MPE at IAC or ITP
 */

package mpe.examples;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IDGetter {

	public static void main(String[] args) {
		getID();
	}

	public static int getID() {
		int id = -1;
		try {
			InetAddress local = InetAddress.getLocalHost();
			String hostname = local.getHostName();

			InetAddress[] all = InetAddress.getAllByName(hostname);
			for (int i = 0; i < all.length; i++) {
				String ip = all[i].getHostAddress();
				System.out.println(i + ": " + all[i].getHostName() + " " + ip);
				if (ip.indexOf("192") > -1) {
					String sid = ip.substring(ip.length()-1,ip.length());
					id = Integer.parseInt(sid);
					System.out.println("Found the ID: "+ id);
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		if (id < 0) {
			System.out.println("IP Address not detected properly");
		}
		
		return id;



	}


}


