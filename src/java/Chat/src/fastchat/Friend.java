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
	 * 传入两个uid，删除friendship里面的条目，以下两个函数都实现这个功能，但每个都需要调用以保证(a,b)和(b,a)都能删除
	 * @throws SQLException 
	 */
	static public boolean dropFriend(String uid1, String uid2) throws SQLException {
		   Connection conn = Connectsql.getConn();
		   Iterator it1 = User.getAllFriend(uid1).iterator(); 
		   if (it1.hasNext() == false) return false;  // 如果输入的不为合法user
	 		while (it1.hasNext()) {
	 			if (it1.next().equals(uid2)) {
	 				break;
	 			}
	 			if (it1.hasNext() == false) {  // 判断是否存在好友关系
	 				return false;
	 			}
	 		}
		   String sql = "delete from friendship where user_uid1=? and user_uid2=?";
		   PreparedStatement pstmt = null;
		   try {
		       pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       pstmt.setString(1, uid1);
		       pstmt.setString(2, uid2);  // 传入的是(a,b),但在表中以(b,a)的形式存储，这时无法删除，要在调用第个函数
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
		       pstmt.setString(2, uid1);  // 传入的是(a,b),但在表中以(b,a)的形式存储
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
	 * 传入一个uid，查找friendadd表，返回一个sender_uid的List信息(receiver_uid为传入的uid且未处理的)
	 * 
	 * @modifier wsq 将其改为获取向目标用户发送好友申请的对象记录
	 * @param uid 接收者id
	 * @return 获取目标用户收到的好友申请信息列表，每条记录包括消息用户本身角色(为接收者)，发送者id，昵称，受理状态，0表示暂未处理，1表示已同意
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
	 * 传入一个uid，查找friendadd表，返回一个receiver_uid的List信息(sender_uid为传入的uid且未处理的）
	 * 
	 * @modifier wsq 将其改为获取目标用户发送的好友申请记录，并删除状态为1的记录(表示已同意，在调用该函数的时候已知会用户)
	 * @param uid 发送者id
	 * @return 获取目标用户发送的好友申请信息列表，每条记录包括消息用户本身角色(为发送者)，接收者id，昵称，受理状态，0表示等待回复，1表示已同意
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
		    		dropFriendAdd(uid, rs.getString(1));	// 删除已同意的好友添加记录
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
	 * 向friendship表中增加条目，在别的方法中调用
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
	 * 判断是否在friendadd表中存在该条目，用于删除条目之前的判断
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
	 * 删除friendadd表中的某一条目，注意delete即使该
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
