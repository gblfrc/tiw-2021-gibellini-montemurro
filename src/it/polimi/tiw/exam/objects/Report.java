package it.polimi.tiw.exam.objects;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Report {
	
	int reportId;
	Appeal appeal;
	Date creationDate;
	Time creationTime;
	List<Grade> grades;
	
	//default constructor
	public Report() {
		super();
	}
	
	//constructor with attributes
	public Report (int id, Appeal appeal, Date creationDate, Time creationTime, List<Grade> grades) {
		this.reportId=id;
		this.appeal=appeal;
		this.creationDate=creationDate;
		this.creationTime=creationTime;
		this.grades=grades;
	}
	
	
	//getters
	
	public int getReportId() {
		return reportId;
	}
	
	public Appeal getAppeal() {
		return appeal;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public Time getCreationTime() {
		return creationTime;
	}
	
	public List<Grade> getGrades(){
		return grades;
	}
	
	
	//setters
	
	public void setReportId (int id) {
		this.reportId=id;
	}
	
	public void setAppeal (Appeal appeal) {
		this.appeal=appeal;
	}
	
	public void setCreationDate (Date date) {
		this.creationDate=date;
	}
	
	public void setCreationTime (Time time) {
		this.creationTime=time;
	}
	
	public void setGrades(List<Grade> grades) {
		this.grades=grades;
	}

}
