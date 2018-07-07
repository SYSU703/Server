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
	 * 创建用户功能，输入相关信息，返回是否成功建立，但date类需要在前端进行判断保证其合法，其他错误输入已在该函数处理
	 * @throws SQLException 
	 *
	 * @modifier wsq
	 * 将参数userstate去掉，创建用户时用默认为下线状态
	 */
	static public boolean createUser(String uid, String password, String unickname, 
			Date birthday, boolean sex) throws SQLException {
		if (uid.length() <= 0 || password.length() <= 0 || unickname.length()<= 0) return false;  // 确保三项都不为空，birthday可在前端处理输入合法
		Connection conn = Connectsql.getConn();
		String sql = "insert into user (uid, password, unickname, birthday, sex, userstate) "
				+ "values(?,?,?,?,?,?)";  // sex为1代表男性，0代表女性(默认为男性），userstate=1代表在线（默认不在线）
	    PreparedStatement pstmt = null;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setString(1, uid);
 	        pstmt.setString(2, password);
 	        pstmt.setString(3, unickname);
 	        pstmt.setDate(4, new java.sql.Date(birthday.getTime()));
 	        pstmt.setBoolean(5, sex);
 	        pstmt.setBoolean(6, false); // 设置相关属性内容
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
	
	
	static public boolean isExistUser(String uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from user";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))) {  // 找到匹配的uid记录
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
	
	static public boolean isExistUser(String uid, String password) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "select * from user";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))&& password.equals(rs.getString(2))) {  // 找到匹配的uid记录
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
	
	
	
	
	/**
	 * @author wrf
	 * 更新用户信息，更新密码、昵称、生日，最好研究一下怎么用PreparedStatement
	 * @throws SQLException 
	 *
	 * @modifier wsq
	 * 添加了性别参数，使得性别也可以更改
	 */
	static public boolean modifyInfo(String uid, String password, String unickname, Date birthday, boolean sex) throws SQLException {
		if (User.isExistUser(uid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql = "update user set password=?, unickname=?, birthday=?, sex=? where uid=?";
		PreparedStatement pstmt = null;
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
		} finally {
			   pstmt.close();
		       conn.close();
		   }
		return true;
	}
	
	
	/**
	 * @author wrf
	 * 传入一个uid，返回一个List好友信息，可用于显示所有好友
	 * @throws SQLException 
	 */
	static public List<String> getAllFriend(String uid) throws SQLException {
		List<String> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select * from friendship";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(1))) {  // 因为增加好友只增加一条记录，所以两列都要查
		    		info.add(rs.getString(2));
		    	} else if (uid.equals(rs.getString(2))) {
		    		info.add(rs.getString(1));
		    	}
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
	 * 传入uid,得到这个用户所加入的所有群id，返回一个List(由群id组成)
	 * @throws SQLException 
	 */
	static public List<Integer> getAllGroup(String uid) throws SQLException {
		List<Integer> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select * from groupmember";
		PreparedStatement pstmt = null;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery(sql);
		    while (rs.next()) {
		    	if (uid.equals(rs.getString(2))) {  // 如果groupmember表中存在该群聊gid，将其加入list中
		    		info.add(rs.getInt(1));
		        }
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
	  * @author wsq
	  * 修改用户状态，用于上线、下线操作
	 * @throws SQLException 
	  */
	static public boolean changeState(String uid, boolean state) throws SQLException {
		if (User.isExistUser(uid) == false) return false;
		Connection conn = Connectsql.getConn();
		int sta = 0;
		if (state)
			sta = 1;
		String sql = "update user set userstate='" + sta + "' where uid='" + uid + "'";
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
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
