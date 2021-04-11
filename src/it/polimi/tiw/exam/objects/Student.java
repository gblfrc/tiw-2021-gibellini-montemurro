package it.polimi.tiw.exam.objects;

public class Student {
	
	private int studentId;
	private String name;
	private String surname;
	private String email;
	//private String password;
	private String degreeCourse;
	
	//default constructor
	public Student() {}
	
	
	//constructor given all attributes
	public Student(int id, String name, String surname, String email, String degCourse) {
		this.studentId=id;
		this.name=name;
		this.surname=surname;
		this.email=email;
		this.degreeCourse=degCourse;
	}

	
	//getters 

	public int getStudentID() {
		return this.studentId;
	}
	
	public String getName() {
		return this.name;
	}

	public String getSurname() {
		return this.surname;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getDegreeCourse() {
		return this.degreeCourse;
	}
	
	
	//setters
	
	public void setStudentId(int id) {
		this.studentId=id;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setSurname(String surname) {
		this.surname=surname;
	}
	
	public void setEmail(String email) {
		this.email=email;
	}
	
	public void setDegreeCourse (String dc) {
		this.degreeCourse=dc;
	}
	
	
	
	
}
