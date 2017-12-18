package service.users;

public class UserInfo {
	
	private String userName;
	private int userID;

	public UserInfo(String userName, int userID) {
		this.userName = userName;
		this.userID = userID;
	}
	
	public int getID() {
		return userID;
	}
	
	public String getName() {
		return userName;
	}
	
}
