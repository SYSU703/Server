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
	 * ������
	 * @author wsq
	 * @param rid Ŀ���¼rid
	 * @return �Ƿ����Ŀ���¼
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
	 * ��ȡĿ�������¼��Ϣ
	 * @author wsq
	 * @param rid Ŀ�������¼id
	 * @return �����¼��Ϣ���������ݣ������ߣ������ߣ�����ʱ��
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
	 * ��ȡ�����û���������ϢͨѶ��rid�б�
	 * @author wsq
	 * @param uid_one �û�1��id
	 * @param uid_two �û�2��id
	 * @return �û�1��2֮��������Ϣ������rid�б�
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
	 * Ŀ���¼�Ƿ��Ѷ�
	 * @param rid Ŀ���¼rid
	 * @return rid�Ƿ��Ѷ�
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
	 * ��δ����Ϣ״̬��Ϊ�Ѷ�
	 * @author wsq
	 * @param rid Ŀ��rid
	 * @return �޸�״̬�Ƿ�ɹ�
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
		String sql = "insert into friendrecord (rid, message, sender_uid, receiver_uid, recordtime, isread) "
				+ "values(?,?,?,?,?,?)";
		Timestamp time = new Timestamp(recordTime.getTime());
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setNull(1, Types.INTEGER); // rid������
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
