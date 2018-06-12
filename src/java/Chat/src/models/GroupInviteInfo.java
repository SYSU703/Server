package models;

/**
 * 群邀请信息，由被邀请用户收到
 * @author wsq
 *
 */
public class GroupInviteInfo {
	
	private String groupId;
	private String groupName;
	private String managerId;
	private String managerNickName;
	
	public GroupInviteInfo(int gId, String gName, String mId, String mNickName) {
		groupId = String.valueOf(gId);
		groupName = gName;
		managerId = mId;
		managerNickName = mNickName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = String.valueOf(groupId);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getManagerNickName() {
		return managerNickName;
	}

	public void setManagerNickName(String managerNickName) {
		this.managerNickName = managerNickName;
	}

}
