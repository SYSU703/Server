package fastchat;

import fastchat.Connectsql;
import fastchat.User;
import models.FriendAddInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Friend {

	/**
	 * @author wrf
	 * ��������uid��ɾ��friendship�������Ŀ����������������ʵ��������ܣ���ÿ������Ҫ�����Ա�֤(a,b)��(b,a)����ɾ��
	 */
	static public boolean dropFriend(String uid1, String uid2) {
		   Connection conn = Connectsql.getConn();
		   Iterator it1 = User.getAllFriend(uid1).iterator(); 
		   if (it1.hasNext() == false) return false;  // �������Ĳ�Ϊ�Ϸ�user
	 		while (it1.hasNext()) {
	 			if (it1.next().equals(uid2)) {
	 				break;
	 			}
	 			if (it1.hasNext() == false) {  // �ж��Ƿ���ں��ѹ�ϵ
	 				return false;
	 			}
	 		}
		   String sql = "delete from friendship where user_uid1=? and user_uid2=?";
		   PreparedStatement pstmt;
		   try {
		       pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       pstmt.setString(1, uid1);
		       pstmt.setString(2, uid2);  // �������(a,b),���ڱ�����(b,a)����ʽ�洢����ʱ�޷�ɾ����Ҫ�ڵ��õڸ�����
		       pstmt.executeUpdate();       
		       pstmt.close();
		       conn.close();
		   } catch (SQLException e) {
			   return false;
		   }
		return true;
	}
	
	
	static public boolean dropFriend2(String uid1, String uid2) {
		   Connection conn = Connectsql.getConn();
		   Iterator it1 = User.getAllFriend(uid1).iterator(); 
		   if (it1.hasNext() == false) return false;
	 	   while (it1.hasNext()) {
	 	   		if (it1.next().equals(uid2)) {
	 	        	break;
	 		    }
	 		    if (it1.hasNext() == false) {
	 			   return false;
	 		    }
	 	   }
		   String sql = "delete from friendship where user_uid1=? and user_uid2=?";
		   PreparedStatement pstmt;
		   try {
		       pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       pstmt.setString(1, uid2);
		       pstmt.setString(2, uid1);  // �������(a,b),���ڱ�����(b,a)����ʽ�洢
		       pstmt.executeUpdate();       
		       pstmt.close();
		       conn.close();
		   } catch (SQLException e) {
			   return false;
		   }
		return true;
	}
	
	/**
	 * @author wrf
	 * ����һ��uid������friendadd������һ��sender_uid��List��Ϣ(receiver_uidΪ�����uid��δ�����)
	 * 
	 * @modifier wsq �����Ϊ��ȡ��Ŀ���û����ͺ�������Ķ����¼
	 * @param uid ������id
	 * @return ��ȡĿ���û��յ��ĺ���������Ϣ�б�ÿ����¼������Ϣ�û������ɫ(Ϊ������)��������id���ǳƣ�����״̬��0��ʾ��δ����1��ʾ��ͬ��
	 */
	static public List<FriendAddInfo> getSenderInfo(String uid) {
		List<FriendAddInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, friendadd.state "
				+ "from friendadd, user "
				+ "where friendadd.receiver_uid=? and friendadd.sender_uid = user.uid";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new FriendAddInfo(rs.getString(1), rs.getString(2), rs.getInt(3)));
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	
	
	/**
	 * @author wrf
	 * ����һ��uid������friendadd������һ��receiver_uid��List��Ϣ(sender_uidΪ�����uid��δ����ģ�
	 * 
	 * @modifier wsq �����Ϊ��ȡĿ���û����͵ĺ��������¼����ɾ��״̬Ϊ1�ļ�¼(��ʾ��ͬ�⣬�ڵ��øú�����ʱ����֪���û�)
	 * @param uid ������id
	 * @return ��ȡĿ���û����͵ĺ���������Ϣ�б�ÿ����¼������Ϣ�û������ɫ(Ϊ������)��������id���ǳƣ�����״̬��0��ʾ�ȴ��ظ���1��ʾ��ͬ��
	 */
	static public List<FriendAddInfo> getReceiverInfo(String uid) {
		List<FriendAddInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, friendadd.state "
				+ "from friendadd, user "
				+ "where friendadd.sender_uid=? and friendadd.receiver_uid = user.uid";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new FriendAddInfo(rs.getString(1), rs.getString(2), rs.getInt(3)));
		    	if (rs.getInt(3) == 1)
		    		dropFriendAdd(uid, rs.getString(1));	// ɾ����ͬ��ĺ�����Ӽ�¼
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	
	
	

	/**
	 * @author wrf
	 * ��friendship����������Ŀ���ڱ�ķ����е���
	 */
	static public boolean addFriendintoFriendship(String s_uid, String r_uid) {
		Connection conn = Connectsql.getConn();
		String sql = "insert into friendship (user_uid1, user_uid2) "
				+ "values(?,?)";
	    PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setString(1, s_uid);
 	        pstmt.setString(2, r_uid);
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
		} catch (SQLException e) {
		    return false;
		}
		return true;
	}
	
	
	
	/**
	 * @author wrf
	 * �ж��Ƿ���friendadd���д��ڸ���Ŀ������ɾ����Ŀ֮ǰ���ж�
	 */
	static public boolean isExistFriendAdd(String s_uid, String r_uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from friendadd";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (s_uid.equals(rs.getString(2)) && r_uid.equals(rs.getString(3))) {
		    		return true;
		    	}
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
        return false;
	}

	
	
	/**
	 * @author wrf
	 * ɾ��friendadd���е�ĳһ��Ŀ��ע��delete��ʹ��
	 */
	static public boolean dropFriendAdd(String s_uid, String r_uid) {
		if (Friend.isExistFriendAdd(s_uid, r_uid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "delete from friendadd where sender_uid=? and receiver_uid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setString(1, s_uid);
		    pstmt.setString(2, r_uid);
		    pstmt.executeUpdate();       
		    pstmt.close();
		    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
}
