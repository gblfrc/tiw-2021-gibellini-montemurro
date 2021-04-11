package it.polimi.tiw.exam.objects;

public class Course {
	private int id_course;
	private int id_professor;
	private String title;
	
	public Course() {
		super();
	}

	public Course(int id_course, int id_professor, String title) {
		this.id_course=id_course;
		this.id_professor=id_course;
		this.title=title;
	}
	
	public int getId_course() {
		return id_course;
	}

	public void setId_course(int id_course) {
		this.id_course = id_course;
	}

	public int getId_professor() {
		return id_professor;
	}

	public void setId_professor(int id_professor) {
		this.id_professor = id_professor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
