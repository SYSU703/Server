package models;

import java.util.Date;
import java.text.SimpleDateFormat;

public class RecordInfo {
	
	private String message;
	private String sender_uid;
	private String receiver_uid;
	private String record_time;
	
	public RecordInfo(String mess, String senderId, String receiverId, Date time) {
		message = mess;
		sender_uid = senderId;
		receiver_uid = receiverId;
		record_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
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

	public String getReceiver_uid() {
		return receiver_uid;
	}

	public void setReceiver_uid(String receiver_uid) {
		this.receiver_uid = receiver_uid;
	}

	public String getRecord_time() {
		return record_time;
	}

	public void setRecord_time(String record_time) {
		this.record_time = record_time;
	}
	
	
}
