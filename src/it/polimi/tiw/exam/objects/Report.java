package it.polimi.tiw.exam.objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Report {
	
	int reportId;
	Appeal appeal;
	LocalDate creationDate;
	LocalTime creationTime;
	List<Student> students;
	
	public Report() {
		super();
	}
	
	public Report (int id, Appeal appeal, LocalDate creationDate, LocalTime creationTime, List<Student> students) {
		this.reportId=id;
		this.appeal=appeal;
		this.creationDate=creationDate;
		this.creationTime=creationTime;
		this.students=students;
	}
	
	
	//getters
	
	public int getReportId() {
		return reportId;
	}
	
	public Appeal getAppeal() {
		return appeal;
	}
	
	public LocalDate getCreationDate() {
		return creationDate;
	}
	
	public LocalTime getCreationTime() {
		return creationTime;
	}
	
	public List<Student> getStudents(){
		return students;
	}
	
	
	//setters
	
	public void setReportId (int id) {
		this.reportId=id;
	}
	
	public void setAppeal (Appeal appeal) {
		this.appeal=appeal;
	}
	
	public void setCreationDate (LocalDate date) {
		this.creationDate=date;
	}
	
	public void setCreationTime (LocalTime time) {
		this.creationTime=time;
	}
	
	public void setStudents(List<Student> students) {
		this.students=students;
	}

}
