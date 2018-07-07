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

import com.mysql.jdbc.Statement;

public class Group {

	/**
	 * @author wrf
	 * 创建群功能，输入相关信息，返回是否成功建立
	 * @throws SQLException 
	 */
	static public int createGroup(String announcement, String groupname, String user_uid) throws SQLException {
		if (groupname.length() <= 0) return -1;
		int result = 0;
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupchat (announcement, groupname, user_uid) "
				+ "values(?,?,?)"; 
		PreparedStatement pstmt = null;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS
);
 	        pstmt.setString(1, announcement);
 	        pstmt.setString(2, groupname);
 	        pstmt.setString(3, user_uid); // 设置相关属性内容
	        pstmt.executeUpdate();
	        ResultSet rs = pstmt.getGeneratedKeys();
	        while (rs.next())
	        	result = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		    return -1;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	    Group.addGroupmemebr(result, user_uid);
		return result;
	}
	
	
	/**
	 * @author wrf
	 * 传入gid判断是否存在改群聊
	 * @throws SQLException 
	 */
	static public boolean isExistGroup(int gid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupchat";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(1)) {  // 如果groupchat表中存在该群聊gid，返回true
		    		return true;
		        }
		    }
		} catch (SQLException e) {
			return false;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
		return false;
	}
	
	static public boolean dropGroupBygid (int gid) throws SQLException {
		if (Group.isExistGroup(gid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupchat where gid=?";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
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

	/**
	  * @author wrf
	  * 获取特定群的群成员id
	 * @throws SQLException 
	  */
	static public List<String> getGroupMembers(int gid) throws SQLException {
		if (!Group.isExistGroup(gid))
			return null;
		List<String> groupMembers = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user_uid from groupmember where group_gid=?";
		PreparedStatement pstmt = null;
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
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	/**
	 * @author wrf
	 * 添加群成员，传入gid和uid，该表表示所有群的所有成员
	 * @throws SQLException 
	 */
	static public boolean addGroupmemebr(int gid, String uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupmember (group_gid, user_uid) " + "values(?,?)";  // 需要将创建者加入群成员表
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			pstmt.setString(2, uid);
			pstmt.executeUpdate();
    		return true;
		} catch (SQLException e) {
			//e.printStackTrace();
		    return false;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	
	
	
	
	/**
	 * @author wrf
	 * 删除groupapply表中的某个条目
	 * @throws SQLException 
	 */
	static public boolean dropGroupApply (String uid, int gid) throws SQLException {
		if (Group.isExistGroupApply(uid, gid) == false) return false;  // 确保存在该条目
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupapply where group_gid=? and user_uid=?";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
		    pstmt.setString(2, uid);
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
	
	/**
	 * @author wrf
	 * 删除groupinvite表中的某个条目
	 * @throws SQLException 
	 */
	static public boolean dropGroupInvite (int gid, String uid) throws SQLException {
		if (Group.isExistGroupInvite(gid, uid) == false) return false;  // 确保存在该条目
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupinvite where user_uid=? and group_gid=?";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setInt(2, gid);
		    pstmt.setString(1, uid);
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
	
	/**
	 * @author wrf
	 * 在groupapply表中是否存在某个条目
	 * @throws SQLException 
	 */
	static public boolean isExistGroupApply (String uid, int gid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupapply";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(2) && uid.equals(rs.getString(1))) {  // 找到匹配的groupapply中的记录
		    		return true;
		    	}
		    }
		    return false;
		} catch (SQLException e) {
			return false;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	/**
	 * @author wrf
	 * 在groupinvite表中是否存在某个条目
	 * @throws SQLException 
	 */
	static public boolean isExistGroupInvite (int gid, String uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupinvite";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (gid == rs.getInt(1) && uid.equals(rs.getString(2))) {  // 找到匹配的groupapply中的记录
		    		return true;
		    	}
		    }
		    return false;
		} catch (SQLException e) {
			return false;
		} finally {
			   pstmt.close();
		       conn.close();
		   }
	}
	
	/**
	 * 目标用户是否是目标群成员
	 * @param uid 目标用户id
	 * @param gid 目标群id
	 * @return 目标用户是否是群成员
	 * @throws SQLException 
	 */
	static public boolean isMember(String uid, int gid) throws SQLException {
		return Group.getGroupMembers(gid).contains(uid);
	}
}
