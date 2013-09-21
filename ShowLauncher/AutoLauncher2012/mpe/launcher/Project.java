package mpe.launcher;

public class Project {
	public String name = "";
	public String title = "";
	public int time = 60;
	public String path = "";
	public boolean titles = true;
	
	public Project(String n, String t, int t_, String p, boolean titl) {
		name = n;
		title = t;
		time = t_;
		path = p;
		titles = titl;
	}
	
	void print() {
		System.out.println(name + " " + title + " " + time + " " + path + " " + titles);
	}
	

}
