package fastchat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import models.FriendRecordInfo;
import models.GroupRecordInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Record {
	
	/**
	 * 测试用
	 * @author wsq
	 * @param rid 目标记录rid
	 * @param type 0表示私聊消息，1表示群消息
	 * @return 是否存在目标记录
	 */
	static public boolean hasRecord(int rid, int type) {
		Connection conn = Connectsql.getConn();
		if (type != 0 && type != 1)
			return false;
		String sql = "select rid from friendrecord";
		if (type == 1)
			sql = "select rid from grouprecord";
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
	static public FriendRecordInfo getRecordOfFriendInfoById(int rid) {
		Connection conn = Connectsql.getConn();
		String sql = "select message, sender_uid, receiver_uid, recordtime from friendrecord where rid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setInt(1, rid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next())
		    	return new FriendRecordInfo(rs.getString("message"), rs.getString("sender_uid"), rs.getString("receiver_uid"), rs.getTimestamp("recordtime"));
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
	static public List<Integer> getRecordOfFriendIdsByUser(String uid_one, String uid_two) {
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
	static public boolean isReadOfFriend(int rid) {
		if (!hasRecord(rid, 0)) return false;
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
	static public boolean readRecordOfFriend(int rid) {
		if (isReadOfFriend(rid))
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
		String sql = "insert into friendrecord (message, sender_uid, receiver_uid, recordtime, isread) "
				+ "values(?,?,?,?,?)";
		Timestamp time = new Timestamp(recordTime.getTime());
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, message);
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
	static public boolean readAllRecordOfFriend(String uid) {
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
				readRecordOfFriend(rs.getInt("rid"));
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 获取目标消息记录信息
	 * @author wsq
	 * @param rid 目标记录id
	 * @return 目标消息记录，包括聊天内容，发送者id，所在群id，发送时间
	 */
	static public GroupRecordInfo getRecordOfGroupInfoById(int rid) {
		Connection conn = Connectsql.getConn();
		String sql = "select message, user_uid, group_gid, recordtime from grouprecord where rid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setInt(1, rid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next())
		    	return new GroupRecordInfo(rs.getString("message"), rs.getString("user_uid"), rs.getInt("group_gid"), rs.getTimestamp("recordtime"));
		    return null;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * 获取目标群内聊天记录rid列表
	 * @author wsq
	 * @param gid 目标群id 
	 * @return 目标群聊天记录rid列表
	 */
	static public List<Integer> getRecordOfGroupInfoByGroup(int gid) {
		List<Integer> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select rid from grouprecord where group_gid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				info.add(rs.getInt("rid"));
			return info;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * @author wsq
	 * @param rid 目标消息id
	 * @param uid 目标用户id
	 * @return 目标用户是否读过目标群消息
	 */
	static public boolean isReadOfGroup(int rid, String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupnotread where rid = ? and receiver_id = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setInt(1, rid);
			pstmt.setString(2, uid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 删除目标用户在目标群未读消息数据库中的记录
	 * @author wsq
	 * @param uid 目标用户
	 * @param gid 目标群
	 * @return 目标用户在目标群中的未读消息记录删除是否成功
	 */
	static public boolean readRecordOfGroup(String uid, int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupnotread where receiver_id = ? and gid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setInt(2, gid);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 群未读消息表中添加记录
	 * @author wsq
	 * @param rid 目标记录id
	 * @param receiver_id 接收者id
	 * @return 添加记录是否成功
	 */
	static public boolean createGroupNotRead(int rid, String receiver_id, int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupnotread (rid, receiver_id, gid) "
				+ "values(?,?,?)";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, rid);
			pstmt.setString(2, receiver_id);
			pstmt.setInt(3, gid);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 创建一个群聊天记录，并对每一个群成员(发送者除外)增添以一条未读消息记录
	 * @author wsq
	 * @param message 消息内容
	 * @param sender_id 发送者id
	 * @param group_id 目标群id
	 * @return 操作是否成功
	 */
	static public boolean createGroupRecord(String message, String sender_id, int group_id, Date recordTime) {
		Connection conn = Connectsql.getConn();
		String sql = "insert into grouprecord (message, user_uid, group_gid, recordtime) "
				+ "values(?,?,?,?)";
		Timestamp time = new Timestamp(recordTime.getTime());
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, message);
			pstmt.setString(2, sender_id);
			pstmt.setInt(3, group_id);
			pstmt.setTimestamp(4, time);
			pstmt.executeUpdate();
			int rid = 0;
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next())
				rid = rs.getInt(1);
			List<String> receivers = Group.getGroupMembers(group_id);
			receivers.remove(sender_id);
			for (int i = 0; i < receivers.size(); i++)
				createGroupNotRead(rid, receivers.get(i), group_id);
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * @author wsq
	 * @param uid 目标用户
	 * @return 获取目标用户未读消息群
	 */
	static public List<Integer> getGroupNotRead(String uid) {
		List<Integer> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select gid from groupnotread where receiver_id = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (!info.contains(rs.getInt("gid")))
					info.add(rs.getInt("gid"));
			}
			return info;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * 删除目标群相关所有群消息记录(用于群解散)
	 * @param gid 目标群id
	 * @return 删除操作是否成功
	 */
	static public boolean clearGroupRecord(int gid) {
		Connection conn = Connectsql.getConn();
		String sql1 = "delete from grouprecord where group_gid = ?";
		String sql2 = "delete from groupnotread where gid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql1);
			pstmt.setInt(1, gid);
			pstmt.executeUpdate();
			pstmt = (PreparedStatement)conn.prepareStatement(sql2);
			pstmt.setInt(1, gid);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * @author wsq
	 * @param uid 目标用户id
	 * @param gid 目标群id
	 * @return 删除用户在群中所有言论是否成功
	 */
	static public boolean clearGroupRecordForUser(String uid, int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "delete from grouprecord where receiver_id = ? and group_gid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setInt(2, gid);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {}
		return false;
	}
}
	
