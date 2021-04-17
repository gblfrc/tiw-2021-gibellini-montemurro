package it.polimi.tiw.exam.objects;

public class Course {
	private int courseId;
	private int professorId;
	private String title;
	
	public Course() {
		super();
	}

	public Course(int courseId, int professorId, String title) {
		this.courseId=courseId;
		this.professorId=professorId;
		this.title=title;
	}
	
	
	//getters
	
	public int getCourseId() {
		return courseId;
	}

	public int getProfessorId() {
		return professorId;
	}

	public String getTitle() {
		return title;
	}

	
	//setters
	
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public void setProfessorId(int professorId) {
		this.professorId = professorId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
