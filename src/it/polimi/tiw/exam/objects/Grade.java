package it.polimi.tiw.exam.objects;

public class Grade {
	private int id_app;
	private int id_stud;
	private String state;
	private boolean failed;
	private boolean recalled;
	private boolean absent;
	private int grade;
	private boolean merit;
	
	public Grade() {
		super();
	}
	
	public Grade(int id_app, int id_stud, String state, boolean failed, boolean recalled, boolean absent, int grade, boolean merit) {
		this.id_app = id_app;
		this.id_stud = id_stud;
		this.state = state;
		this.failed = failed;
		this.recalled = recalled;
		this.absent = absent;
		this.grade = grade;
		this.merit = merit;
	}
	
	public int getId_app() {
		return id_app;
	}

	public void setId_app(int id_app) {
		this.id_app = id_app;
	}

	public int getId_stud() {
		return id_stud;
	}

	public void setId_stud(int id_stud) {
		this.id_stud = id_stud;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public boolean isRecalled() {
		return recalled;
	}

	public void setRecalled(boolean recalled) {
		this.recalled = recalled;
	}

	public boolean isAbsent() {
		return absent;
	}

	public void setAbsent(boolean absent) {
		this.absent = absent;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public boolean isMerit() {
		return merit;
	}

	public void setMerit(boolean merit) {
		this.merit = merit;
	}
}
