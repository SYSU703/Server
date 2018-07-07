package models;

/**
 * 具体群信息
 * @author wsq
 *
 */
public class CompleteGroupInfo {
	
	private String groupId;
	private String announcement;
	private String groupName;
	private String managerId;
	private String managerNickName;
	private String memberNumber;
	private GroupRecordInfo lastMessage;
	
	public CompleteGroupInfo(int gid, String announce, String gName, String mid, String mNickName, int mNum) {
		groupId = String.valueOf(gid);
		announcement = announce;
		groupName = gName;
		managerId = mid;
		managerNickName = mNickName;
		memberNumber = String.valueOf(mNum);
		lastMessage = null;
	}

	public CompleteGroupInfo(int gid, String announce, String gName, String mid, String mNickName, int mNum, GroupRecordInfo message) {
		groupId = String.valueOf(gid);
		announcement = announce;
		groupName = gName;
		managerId = mid;
		managerNickName = mNickName;
		memberNumber = String.valueOf(mNum);
		lastMessage = message;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = String.valueOf(groupId);
	}

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
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

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(int memberNumber) {
		this.memberNumber = String.valueOf(memberNumber);
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public GroupRecordInfo getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(GroupRecordInfo lastMessage) {
		this.lastMessage = lastMessage;
	}
	
	
}
