/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate remote exec.
 *  $ CLASSPATH=.:../build javac Exec.java 
 *  $ CLASSPATH=.:../build java Exec
 * You will be asked username, hostname, displayname, passwd and command.
 * If everything works fine, given command will be invoked 
 * on the remote side and outputs will be printed out.
 *
 */
import com.jcraft.jsch.*;
import java.io.*;

public class Exec {
	public static void execute(String[] args) {

		// Give up if any of the arguments aren't filed in
		for (String arg : args) {
			if (arg == null) {
				System.out
						.println("Failed attempt. Make sure you've provided a password and check your settings.XML file.");
				return;
			}
		}

		String user = args[0];
		String host = args[1];
		String password = args[2];
		String commands = args[3];

		try {
			// Start new session
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);

			// Authentication stuff
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			// Execute commands
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(commands);

			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if(i < 0)
					break;
				System.out.print(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				System.out.println("exit-status: "
						+ channel.getExitStatus());
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		String passwd;

		public void setPassword(String password) {
			passwd = password;
		}

		// Methods required by the interface that we are not using
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return false;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
			System.out.println(message);
		}

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {

			String[] response = { passwd };
			return response;
		}
	}
}
