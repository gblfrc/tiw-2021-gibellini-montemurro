package it.polimi.tiw.exam.objects;

public class User {

	private int personId;
	private String accessRights;
	
	
	//default constructor
	public User() {}
	
	//constructor with attributes
	public User(int id, String rights) {
		this.personId=id;
		this.accessRights=rights;
	}
	
	
	//getters
	
	public int getPersonId() {
		return this.personId;
	}
	
	public String getAccessRights() {
		return this.accessRights;
	}
	
	
	//setters
	
	public void setPersonId(int id) {
		this.personId=id;
	}
	
	public void setAccessRights(String rights) {
		this.accessRights=rights;
	}
	
	
	
}
