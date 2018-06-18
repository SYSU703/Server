package fastchat;

import fastchat.User;
import fastchat.Connectsql;
import fastchat.Handle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Group {

	/**
	 * @author wrf
	 * 创建群功能，输入相关信息，返回是否成功建立
	 */
	static public boolean createGroup(int gid, String announcement, String groupname, String user_uid) {
		if (groupname.length() <= 0) return false;
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupchat (gid, announcement, groupname, user_uid) "
				+ "values(?,?,?,?)"; 
		PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setInt(1, gid);  
 	        pstmt.setString(2, announcement);
 	        pstmt.setString(3, groupname);
 	        pstmt.setString(4, user_uid); // 设置相关属性内容
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		    return false;
		}
	    Group.addGroupmemebr(gid, user_uid);
		return true;
	}
	
	
	/**
	 * @author wrf
	 * 传入gid判断是否存在改群聊
	 */
	static public boolean isExistGroup(int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupchat";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(1)) {  // 如果groupchat表中存在该群聊gid，返回true
		    		return true;
		        }
		    }
		} catch (SQLException e) {}
		return false;
	}
	
	static public boolean dropGroupBygid (int gid) {
		if (Group.isExistGroup(gid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupchat where gid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
		    pstmt.executeUpdate();       
		    pstmt.close();
		    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	  * @author wrf
	  * 获取特定群的群成员id
	  */
	static public List<String> getGroupMembers(int gid) {
		if (!Group.isExistGroup(gid))
			return null;
		List<String> groupMembers = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user_uid from groupmember where group_gid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			pstmt.executeQuery();
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				groupMembers.add(rs.getString(1));
			}
			return groupMembers;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @author wrf
	 * 添加群成员，传入gid和uid，该表表示所有群的所有成员
	 */
	static public boolean addGroupmemebr(int gid, String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupmember (group_gid, user_uid) " + "values(?,?)";  // 需要将创建者加入群成员表
		PreparedStatement qstmt;
		try {
	        qstmt = (PreparedStatement) conn.prepareStatement(sql);
	        qstmt.setInt(1, gid);
            qstmt.setString(2, uid);
            qstmt.executeUpdate();
            qstmt.close();
	        conn.close();
		} catch (SQLException e) {
			//e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	
	
	
	
	/**
	 * @author wrf
	 * 删除groupapply表中的某个条目
	 */
	static public boolean dropGroupApply (String uid, int gid) {
		if (Group.isExistGroupApply(uid, gid) == false) return false;  // 确保存在该条目
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupapply where group_gid=? and user_uid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
		    pstmt.setString(2, uid);
		    pstmt.executeUpdate();       
		    pstmt.close();
		    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @author wrf
	 * 删除groupinvite表中的某个条目
	 */
	static public boolean dropGroupInvite (int gid, String uid) {
		if (Group.isExistGroupInvite(gid, uid) == false) return false;  // 确保存在该条目
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupinvite where user_uid=? and group_gid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(2, gid);
		    pstmt.setString(1, uid);
		    pstmt.executeUpdate();       
		    pstmt.close();
		    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @author wrf
	 * 在groupapply表中是否存在某个条目
	 */
	static public boolean isExistGroupApply (String uid, int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupapply";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(2) && uid.equals(rs.getString(1))) {  // 找到匹配的groupapply中的记录
		    		return true;
		    	}
		    }
		    return false;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * @author wrf
	 * 在groupinvite表中是否存在某个条目
	 */
	static public boolean isExistGroupInvite (int gid, String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupinvite";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(1) && uid.equals(rs.getString(2))) {  // 找到匹配的groupapply中的记录
		    		return true;
		    	}
		    }
		    return false;
		} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * 目标用户是否是目标群成员
	 * @param uid 目标用户id
	 * @param gid 目标群id
	 * @return 目标用户是否是群成员
	 */
	static public boolean isMember(String uid, int gid) {
		return Group.getGroupMembers(gid).contains(uid);
	}
}
