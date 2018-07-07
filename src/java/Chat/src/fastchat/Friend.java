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
	 * @throws SQLException 
	 */
	static public boolean dropFriend(String uid1, String uid2) throws SQLException {
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
		   PreparedStatement pstmt = null;
		   try {
		       pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       pstmt.setString(1, uid1);
		       pstmt.setString(2, uid2);  // �������(a,b),���ڱ�����(b,a)����ʽ�洢����ʱ�޷�ɾ����Ҫ�ڵ��õڸ�����
		       pstmt.executeUpdate();       
		   } catch (SQLException e) {
			   return false;  
		   }
		   finally {
			   pstmt.close();
		       conn.close();
		   }
		return true;
	}
	
	
	static public boolean dropFriend2(String uid1, String uid2) throws SQLException {
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
		   PreparedStatement pstmt = null;
		   try {
		       pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       pstmt.setString(1, uid2);
		       pstmt.setString(2, uid1);  // �������(a,b),���ڱ�����(b,a)����ʽ�洢
		       pstmt.executeUpdate();       
		   } catch (SQLException e) {
			   return false;
		   } finally {
			   pstmt.close();
		       conn.close();
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
	 * @throws SQLException 
	 */
	static public List<FriendAddInfo> getSenderInfo(String uid) throws SQLException {
		List<FriendAddInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, friendadd.state "
				+ "from friendadd, user "
				+ "where friendadd.receiver_uid=? and friendadd.sender_uid = user.uid";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new FriendAddInfo(rs.getString(1), rs.getString(2), rs.getInt(3)));
		    }
		    return info;
		} catch (SQLException e) {
		    return null;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	
	/**
	 * @author wrf
	 * ����һ��uid������friendadd������һ��receiver_uid��List��Ϣ(sender_uidΪ�����uid��δ����ģ�
	 * 
	 * @modifier wsq �����Ϊ��ȡĿ���û����͵ĺ��������¼����ɾ��״̬Ϊ1�ļ�¼(��ʾ��ͬ�⣬�ڵ��øú�����ʱ����֪���û�)
	 * @param uid ������id
	 * @return ��ȡĿ���û����͵ĺ���������Ϣ�б�ÿ����¼������Ϣ�û������ɫ(Ϊ������)��������id���ǳƣ�����״̬��0��ʾ�ȴ��ظ���1��ʾ��ͬ��
	 * @throws SQLException 
	 */
	static public List<FriendAddInfo> getReceiverInfo(String uid) throws SQLException {
		List<FriendAddInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, friendadd.state "
				+ "from friendadd, user "
				+ "where friendadd.sender_uid=? and friendadd.receiver_uid = user.uid";
		PreparedStatement pstmt = null;
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
		} catch (SQLException e) {
		    return null;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	
	

	/**
	 * @author wrf
	 * ��friendship����������Ŀ���ڱ�ķ����е���
	 * @throws SQLException 
	 */
	static public boolean addFriendintoFriendship(String s_uid, String r_uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "insert into friendship (user_uid1, user_uid2) "
				+ "values(?,?)";
	    PreparedStatement pstmt = null;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setString(1, s_uid);
 	        pstmt.setString(2, r_uid);
	        pstmt.executeUpdate();
		} catch (SQLException e) {
		    return false;
		} finally {
			   pstmt.close();
		       conn.close();
		}
		return true;
	}
	
	
	
	/**
	 * @author wrf
	 * �ж��Ƿ���friendadd���д��ڸ���Ŀ������ɾ����Ŀ֮ǰ���ж�
	 * @throws SQLException 
	 */
	static public boolean isExistFriendAdd(String s_uid, String r_uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from friendadd";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (s_uid.equals(rs.getString(2)) && r_uid.equals(rs.getString(3))) {
		    		return true;
		    	}
		    }
	        return false;
		} catch (SQLException e) {
		    e.printStackTrace();
	        return false;
		} finally {
			   pstmt.close();
		       conn.close();
		}
	}

	
	
	/**
	 * @author wrf
	 * ɾ��friendadd���е�ĳһ��Ŀ��ע��delete��ʹ��
	 * @throws SQLException 
	 */
	static public boolean dropFriendAdd(String s_uid, String r_uid) throws SQLException {
		if (Friend.isExistFriendAdd(s_uid, r_uid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "delete from friendadd where sender_uid=? and receiver_uid=?";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setString(1, s_uid);
		    pstmt.setString(2, r_uid);
		    pstmt.executeUpdate();       
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			   pstmt.close();
		       conn.close();
		}
		return true;
	}
	
}
