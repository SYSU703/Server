package models;

/**
 * ���������Ϣ���������û�����������Լ��յ��ĺ���������Ϣ
 * @author wsq
 *
 */
public class FriendAddInfo {

	private String username;	// Ŀ���û�id
	private String nickname;	// Ŀ���û��ǳ�
	/**
	 *  ��Ϣ״̬
	 *  ��ǰ�û�Ϊ������ʱ��0��ʾ�ȴ��ظ���1��ʾ��ͬ��
	 *  ��ǰ�û�Ϊ������ʱ��0��ʾ��δ����1��ʾ��ͬ��
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
