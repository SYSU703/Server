package models;

/**
 * 简单用户信息，用于好友列表等只需要简要信息的地方
 * @author dell
 *
 */
public class SimpleUserInfo {
	private String userId;
	private String userNickName;
	private boolean userState;
	
	public SimpleUserInfo(String uId, String uNickName, boolean uState) {
		userId = uId;
		userNickName = uNickName;
		userState = uState;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public boolean isUserState() {
		return userState;
	}

	public void setUserState(boolean userState) {
		this.userState = userState;
	}

	
	
}
