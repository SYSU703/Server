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
	 * ������
	 * @author wsq
	 * @param rid Ŀ���¼rid
	 * @param type 0��ʾ˽����Ϣ��1��ʾȺ��Ϣ
	 * @return �Ƿ����Ŀ���¼
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
	 * ��ȡĿ�������¼��Ϣ
	 * @author wsq
	 * @param rid Ŀ�������¼id
	 * @return �����¼��Ϣ���������ݣ������ߣ������ߣ�����ʱ��
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
	 * ��ȡ�����û���������ϢͨѶ��rid�б�
	 * @author wsq
	 * @param uid_one �û�1��id
	 * @param uid_two �û�2��id
	 * @return �û�1��2֮��������Ϣ������rid�б�
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
	 * Ŀ���¼�Ƿ��Ѷ�
	 * @param rid Ŀ���¼rid
	 * @return rid�Ƿ��Ѷ�
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
	 * ��δ����Ϣ״̬��Ϊ�Ѷ�
	 * @author wsq
	 * @param rid Ŀ��rid
	 * @return �޸�״̬�Ƿ�ɹ�
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
	 * �����ݿ�ע��һ����¼
	 * @author wsq
	 * @param message ��¼��������
	 * @param senderId ������id
	 * @param receiverId ������id
	 * @param recordTime ��¼ʱ��
	 * @return ע������Ƿ�ɹ�
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
	 * ɾ�������û�������������¼
	 * @author wsq
	 * @param uid_one �û�1id
	 * @param uid_two �û�2id
	 * @return ɾ�������Ƿ�ɹ�
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
	 * ��ȡ������δ����Ϣ�ĺ����б�
	 * @author wsq
	 * @param uid Ŀ���û�(������)id
	 * @return ��Ŀ���û��յ���δ����Ϣ������id�б�
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
	 * ��Ŀ���û�����δ����Ϣ��Ϊ�Ѷ�
	 * @author wsq
	 * @param uid Ŀ���û�(������)id
	 * @return �޸�״̬�Ƿ�ɹ�
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
	 * ��ȡĿ����Ϣ��¼��Ϣ
	 * @author wsq
	 * @param rid Ŀ���¼id
	 * @return Ŀ����Ϣ��¼�������������ݣ�������id������Ⱥid������ʱ��
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
	 * ��ȡĿ��Ⱥ�������¼rid�б�
	 * @author wsq
	 * @param gid Ŀ��Ⱥid 
	 * @return Ŀ��Ⱥ�����¼rid�б�
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
	 * @param rid Ŀ����Ϣid
	 * @param uid Ŀ���û�id
	 * @return Ŀ���û��Ƿ����Ŀ��Ⱥ��Ϣ
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
	 * ɾ��Ŀ���û���Ŀ��Ⱥδ����Ϣ���ݿ��еļ�¼
	 * @author wsq
	 * @param uid Ŀ���û�
	 * @param gid Ŀ��Ⱥ
	 * @return Ŀ���û���Ŀ��Ⱥ�е�δ����Ϣ��¼ɾ���Ƿ�ɹ�
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
	 * Ⱥδ����Ϣ������Ӽ�¼
	 * @author wsq
	 * @param rid Ŀ���¼id
	 * @param receiver_id ������id
	 * @return ��Ӽ�¼�Ƿ�ɹ�
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
	 * ����һ��Ⱥ�����¼������ÿһ��Ⱥ��Ա(�����߳���)������һ��δ����Ϣ��¼
	 * @author wsq
	 * @param message ��Ϣ����
	 * @param sender_id ������id
	 * @param group_id Ŀ��Ⱥid
	 * @return �����Ƿ�ɹ�
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
	 * @param uid Ŀ���û�
	 * @return ��ȡĿ���û�δ����ϢȺ
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
	 * ɾ��Ŀ��Ⱥ�������Ⱥ��Ϣ��¼(����Ⱥ��ɢ)
	 * @param gid Ŀ��Ⱥid
	 * @return ɾ�������Ƿ�ɹ�
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
	 * @param uid Ŀ���û�id
	 * @param gid Ŀ��Ⱥid
	 * @return ɾ���û���Ⱥ�����������Ƿ�ɹ�
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
	
