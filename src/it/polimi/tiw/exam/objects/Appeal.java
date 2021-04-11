package it.polimi.tiw.exam.objects;

import java.util.Date;

public class Appeal {

	private int appealId;
	private int courseId;
	private Date date;
	
	//default constructor
	public Appeal() {}
	
	//constructor with attributes
	public Appeal (int id, int courseId, Date date) {
		this.appealId=id;
		this.courseId=courseId;
		this.date=date;
	}
	
	
	// getters
	
	public int getAppealId() {
		return this.appealId;
	}
	
	public int getCourseId() {
		return this.courseId;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	
	//setters
	
	public void setAppealId(int id) {
		this.appealId=id;
	}
	
	public void setCourseId(int id) {
		this.courseId=id;
	}
	
	public void setDate(Date date) {
		this.date=date;
	}
	
	
	
}
