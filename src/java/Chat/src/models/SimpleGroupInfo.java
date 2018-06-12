package models;

/**
 * 简单群信息，用于搜索以及群列表中做简单显示
 * @author wsq
 *
 */
public class SimpleGroupInfo {
	
	private String groupId;
	private String groupName;
	
	public SimpleGroupInfo(int gId, String gName) {
		groupId = String.valueOf(gId);
		groupName = gName;
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

	
	
}
