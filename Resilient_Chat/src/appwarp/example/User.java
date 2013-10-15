package appwarp.example;

public class User {

	private String name;
	private boolean status;
	
	public User(String name, boolean status){
		this.name = name;
		this.status = status;
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isStatusOnline() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
}
