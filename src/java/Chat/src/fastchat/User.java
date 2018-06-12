package fastchat;

import fastchat.Connectsql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class User {

	/**
	 * @author wrf
	 * �����û����ܣ����������Ϣ�������Ƿ�ɹ���������date����Ҫ��ǰ�˽����жϱ�֤��Ϸ������������������ڸú�������
	 *
	 * @modifier wsq
	 * ������userstateȥ���������û�ʱ��Ĭ��Ϊ����״̬
	 */
	static public boolean createUser(String uid, String password, String unickname, 
			Date birthday, boolean sex) {
		if (uid.length() <= 0 || password.length() <= 0 || unickname.length()<= 0) return false;  // ȷ�������Ϊ�գ�birthday����ǰ�˴�������Ϸ�
		Connection conn = Connectsql.getConn();
		String sql = "insert into user (uid, password, unickname, birthday, sex, userstate) "
				+ "values(?,?,?,?,?,?)";  // sexΪ1�������ԣ�0����Ů��(Ĭ��Ϊ���ԣ���userstate=1�������ߣ�Ĭ�ϲ����ߣ�
	    PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setString(1, uid);
 	        pstmt.setString(2, password);
 	        pstmt.setString(3, unickname);
 	        pstmt.setDate(4, new java.sql.Date(birthday.getTime()));
 	        pstmt.setBoolean(5, sex);
 	        pstmt.setBoolean(6, false); // ���������������
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	
	static public boolean isExistUser(String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from user";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))) {  // �ҵ�ƥ���uid��¼
		    		return true;
		    	}
		    }
		} catch (SQLException e) {return false;}
		return false;
	}
	
	static public boolean isExistUser(String uid, String password) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from user";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))&& password.equals(rs.getString(2))) {  // �ҵ�ƥ���uid��¼
		    		return true;
		    	}
		    }
		} catch (SQLException e) {return false;}
		return false;
	}
	
	
	
	
	/**
	 * @author wrf
	 * �����û���Ϣ���������롢�ǳơ����գ�����о�һ����ô��PreparedStatement
	 *
	 * @modifier wsq
	 * �������Ա������ʹ���Ա�Ҳ���Ը���
	 */
	static public boolean modifyInfo(String uid, String password, String unickname, Date birthday, boolean sex) {
		if (User.isExistUser(uid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "update user set password=?, unickname=?, birthday=?, sex=? where uid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, password);
			pstmt.setString(2, unickname);
			pstmt.setDate(3, new java.sql.Date(birthday.getTime()));
			pstmt.setBoolean(4, sex);
			pstmt.setString(5, uid);
		    pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * @author wrf
	 * ����һ��uid������һ��List������Ϣ����������ʾ���к���
	 */
	static public List<String> getAllFriend(String uid) {
		List<String> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select * from friendship";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))) {  // ��Ϊ���Ӻ���ֻ����һ����¼���������ж�Ҫ��
		    		info.add(rs.getString(2));
		    	} else if (uid.equals(rs.getString(2))) {
		    		info.add(rs.getString(1));
		    	}
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	
	
	/**
	 * @author wrf
	 * ����uid,�õ�����û������������Ⱥid������һ��List(��Ⱥid���)
	 */
	static public List<Integer> getAllGroup(String uid) {
		List<Integer> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupmember";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(2))) {  // ���groupmember���д��ڸ�Ⱥ��gid���������list��
		    		info.add(rs.getInt(1));
		        }
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}

	/**
	  * @author wsq
	  * �޸��û�״̬���������ߡ����߲���
	  */
	static public boolean changeState(String uid, boolean state) {
		if (User.isExistUser(uid) == false) return false;
		Connection conn = Connectsql.getConn();
		int sta = 0;
		if (state)
			sta = 1;
		String sql = "update user set userstate='" + sta + "' where uid='" + uid + "'";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}