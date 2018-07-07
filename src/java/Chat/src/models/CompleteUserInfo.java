package models;
import java.util.Date;

/**
 * 具体用户信息，密码不一定表示该用户的密码(需要区分该用户是否为当前用户)
 * @author wsq
 *
 */
public class CompleteUserInfo {
	
	private String userId;
	private String userNickName;
	private String userBirthday;
	private String userSex;
	private boolean userState;
	private FriendRecordInfo lastMessage;
	
	public CompleteUserInfo(String uId, String uNickName, Date uBirthday, boolean uSex, boolean uState) {
		userId = uId;
		userNickName = uNickName;
		userBirthday = uBirthday.toString();
		if (uSex)
			userSex = "male";
		else
			userSex = "female";
		userState = uState;
		lastMessage = null;
	}

	public CompleteUserInfo(String uId, String uNickName, Date uBirthday, boolean uSex, boolean uState, FriendRecordInfo message) {
		userId = uId;
		userNickName = uNickName;
		userBirthday = uBirthday.toString();
		if (uSex)
			userSex = "male";
		else
			userSex = "female";
		userState = uState;
		lastMessage = message;
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

	public String getUserBirthday() {
		return userBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}

	public void setUserSex(boolean uSex) {
		if (uSex)
			userSex = "male";
		else
			userSex = "female";
	}

	public void setUserState(boolean userState) {
		this.userState = userState;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public boolean isUserState() {
		return userState;
	}

	public FriendRecordInfo getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(FriendRecordInfo lastMessage) {
		this.lastMessage = lastMessage;
	}

	
	
}
