package it.polimi.tiw.exam.objects;

public class ErrorMsg {

	int status;
	String message;
	
	public ErrorMsg(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}

}
