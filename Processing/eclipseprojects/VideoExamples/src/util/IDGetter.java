/**
 * Fall 2013 Big Screens Examples
 * https://github.com/ITPNYU/BigScreens
 * 
 * Note this project uses Processing 2.0.1
 */


package util;

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


