package mpe.launcher;

public class LoopThread extends Thread {

	IACController control;
	boolean running;
	
	LoopThread(IACController c) {
		control = c;
		
	}
	
	public void start() {
		running = true;
		super.start();
	}
	
	public void quit() {
		this.interrupt();
		running = false;
	}

	public void run() {

		while (running) {
			
			int howLong = control.current.time*1000;
			int elapsed = control.millis() - control.start;
			int timeLeft = (howLong - elapsed)/1000;

			if (control.frameCounter % (30*30) == 0) {
				System.out.print  ("\nNow playing: " + control.current.title + " by " + control.current.name);
				System.out.print  ("  --   " + timeLeft + " seconds to go.");
				System.out.println("  --   Next up: " + control.next.title + " by " + control.next.name);
				System.out.print("%: ");
			}
			
			control.frameCounter++;

			if (control.millis() - control.start > howLong) {
				control.killProject();
				control.nextProject(true,true);
				control.frameCounter = 0;
			}

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				//System.out.println("Interrupting to quit");
				//e.printStackTrace();
			}
			


		}



	}
}
