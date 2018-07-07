package models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupRecordInfo {
	private String message;
	private String sender_uid;
	private int group_id;
	private String record_time;
	private int record_id;
	
	public GroupRecordInfo(String mess, String sid, int gid, Date time, int rid) {
		message = mess;
		sender_uid = sid;
		group_id = gid;
		record_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
		record_id = rid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender_uid() {
		return sender_uid;
	}

	public void setSender_uid(String sender_uid) {
		this.sender_uid = sender_uid;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getRecord_time() {
		return record_time;
	}

	public void setRecord_time(String record_time) {
		this.record_time = record_time;
	}

	public int getRecord_id() {
		return record_id;
	}

	public void setRecord_id(int record_id) {
		this.record_id = record_id;
	}
	
	
}
