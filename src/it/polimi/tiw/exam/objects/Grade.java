package it.polimi.tiw.exam.objects;

public class Grade { //include student's info (see specifics)
	private int appealId;
	private int studentId;
	private String studentSurname;
	private String studentName;
	private String email;
	private String degreeCourse;
	private String grade; 
	private String state;
	
	
	public Grade() {
		super();
	}
	
	
	public Grade(int appealId, int studentId, String studentSurname, String studentName, String email,
			String degreeCourse, String grade, String state) {
		this.appealId = appealId;
		this.studentId = studentId;
		this.studentSurname = studentSurname;
		this.studentName = studentName;
		this.email = email;
		this.degreeCourse = degreeCourse;
		this.grade = grade;
		this.state = state;
	}


	//getters
	
	public int getAppealId() {
		return appealId;
	}

	public int getStudentId() {
		return studentId;
	}
	
	public String getStudentSurname() {
		return studentSurname;
	}
	
	public String getStudentName() {
		return studentName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getDegreeCourse() {
		return degreeCourse;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public String getState() {
		return state;
	}
		
	//setters
	
	public void setAppealId(int appealId) {
		this.appealId = appealId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public void setStudentSurname(String studentSurname) {
		this.studentSurname = studentSurname;
	}	

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDegreeCourse(String degreeCourse) {
		this.degreeCourse = degreeCourse;
	}
	public void setState(String state) {
		this.state=state;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

}
