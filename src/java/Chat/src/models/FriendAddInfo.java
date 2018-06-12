package models;

/**
 * 好友添加信息，包括该用户发起的申请以及收到的好友邀请信息
 * @author wsq
 *
 */
public class FriendAddInfo {

	private String username;	// 目标用户id
	private String nickname;	// 目标用户昵称
	/**
	 *  消息状态
	 *  当前用户为发送者时，0表示等待回复，1表示已同意
	 *  当前用户为接收者时，0表示暂未处理，1表示已同意
	 */
	private String state;
	
	public FriendAddInfo(String uid, String name, int st) {
		username = uid;
		nickname = name;
		if (st == 1)
			state = "accept";
		else
			state = "waiting";
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String aimUserId) {
		this.username = aimUserId;
	}

	public String getNickName() {
		return nickname;
	}

	public void setNickName(String aimUserNickName) {
		this.nickname = aimUserNickName;
	}

	public String getState() {
		return state;
	}

	public void setState(int state) {
		if (state == 1)
			this.state = "accept";
		else
			this.state = "waiting";
	}
	

}
