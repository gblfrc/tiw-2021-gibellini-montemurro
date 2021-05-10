package it.polimi.tiw.exam.forms;

public class UserForm {

	private int personId;
	private String password;
	private String idError;
	private String pwdError;
	
	
	public UserForm(int id, String pwd){
		super();
		setPersonId(id);
		setPassword(pwd);
	}
	
	
	//getters
	
	public int getPersonId(){
		return this.personId;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	
	//setters
	
	public void setPersonId(int id){
		if (id>0) this.personId=id;
		else idError = "Nonexistent user";
	}
	
	public void setPassword(String pwd) {
		if (pwd == null || pwd.isEmpty()) {
			pwdError = "Invalid password";
		}
		else password = pwd;
	}
	
	
	//validity checker
	
	public boolean isValid() {
		if(idError != null || pwdError != null) return false;
		else return true;
	}
	
}
