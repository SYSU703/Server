package fastchat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import models.RecordInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Record {
	
	/**
	 * 测试用
	 * @author wsq
	 * @param rid 目标记录rid
	 * @return 是否存在目标记录
	 */
	static public boolean hasRecord(int rid) {
		Connection conn = Connectsql.getConn();
		String sql = "select rid from friendrecord";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				if (rid == rs.getInt("rid"))
					return true;
			return false;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 获取目标聊天记录信息
	 * @author wsq
	 * @param rid 目标聊天记录id
	 * @return 聊天记录信息，包括内容，发送者，接收者，发送时间
	 */
	static public RecordInfo getRecordInfoById(int rid) {
		Connection conn = Connectsql.getConn();
		String sql = "select message, sender_uid, receiver_uid, recordtime from friendrecord where rid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setInt(1, rid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next())
		    	return new RecordInfo(rs.getString("message"), rs.getString("sender_uid"), rs.getString("receiver_uid"), rs.getTimestamp("recordtime"));
		    return null;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * 获取两个用户间所有信息通讯的rid列表
	 * @author wsq
	 * @param uid_one 用户1的id
	 * @param uid_two 用户2的id
	 * @return 用户1、2之间所有信息往来的rid列表
	 */
	static public List<Integer> getRecordIdsByUser(String uid_one, String uid_two) {
		List<Integer> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select rid from friendrecord where "
				+ "(sender_uid = ? and receiver_uid = ?) or (sender_uid = ? and receiver_uid = ?)";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid_one);
			pstmt.setString(2, uid_two);
			pstmt.setString(3, uid_two);
			pstmt.setString(4, uid_one);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				info.add(rs.getInt("rid"));
			}
			return info;
		} catch (SQLException e) {}
		return null;	
	}
	
	/**
	 * 目标记录是否已读
	 * @param rid 目标记录rid
	 * @return rid是否已读
	 */
	static public boolean isRead(int rid) {
		if (!hasRecord(rid)) return false;
		Connection conn = Connectsql.getConn();
		String sql = "select isread from friendrecord where rid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, rid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getBoolean("isread");
			}
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 将未读消息状态设为已读
	 * @author wsq
	 * @param rid 目标rid
	 * @return 修改状态是否成功
	 */
	static public boolean readRecord(int rid) {
		if (isRead(rid))
			return false;
		Connection conn = Connectsql.getConn();
		String sql = "update friendrecord set isread=? where rid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setBoolean(1, true);
			pstmt.setInt(2, rid);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 向数据库注入一条记录
	 * @author wsq
	 * @param message 记录聊天内容
	 * @param senderId 发送者id
	 * @param receiverId 接收者id
	 * @param recordTime 记录时间
	 * @return 注入操作是否成功
	 */
	static public boolean createFriendRecord(String message, String senderId, String receiverId, Date recordTime) {
		Connection conn = Connectsql.getConn();
		String sql = "insert into friendrecord (rid, message, sender_uid, receiver_uid, recordtime, isread) "
				+ "values(?,?,?,?,?,?)";
		Timestamp time = new Timestamp(recordTime.getTime());
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setNull(1, Types.INTEGER); // rid自增长
			pstmt.setString(2, senderId);
			pstmt.setString(3, receiverId);
			pstmt.setTimestamp(4, time);
			pstmt.setBoolean(5, false);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 删除两个用户间的所有聊天记录
	 * @author wsq
	 * @param uid_one 用户1id
	 * @param uid_two 用户2id
	 * @return 删除操作是否成功
	 */
	static public boolean clearFriendRecord(String uid_one, String uid_two) {
		Connection conn = Connectsql.getConn();
		String sql = "delete from friendrecord where "
				+ "(sender_uid = ? and receiver_uid = ?) or (sender_uid = ? and receiver_uid = ?)";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid_one);
			pstmt.setString(2, uid_two);
			pstmt.setString(3, uid_two);
			pstmt.setString(1, uid_one);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 获取发送了未读消息的好友列表
	 * @author wsq
	 * @param uid 目标用户(接收者)id
	 * @return 向目标用户收到的未读消息发送者id列表
	 */
	static public List<String> getFriendNotRead(String uid) {
		List<String> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select sender_uid from friendrecord "
				+ "where receiver_uid = ? and isread = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setBoolean(2, false);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (!info.contains(rs.getString("sender_uid")))
					info.add(rs.getString("sender_uid"));
			}
			return info;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * 将目标用户所有未读消息设为已读
	 * @author wsq
	 * @param uid 目标用户(接收者)id
	 * @return 修改状态是否成功
	 */
	static public boolean readAllRecord(String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select rid from friendrecord "
				+ "where receiver_uid = ? and isread = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setBoolean(2, false);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				readRecord(rs.getInt("rid"));
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
}
