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
	 * ����Ⱥ���ܣ����������Ϣ�������Ƿ�ɹ�����
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
 	        pstmt.setString(3, user_uid); // ���������������
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
	 * ����gid�ж��Ƿ���ڸ�Ⱥ��
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
		    	if (gid == rs.getInt(1)) {  // ���groupchat���д��ڸ�Ⱥ��gid������true
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
	  * ��ȡ�ض�Ⱥ��Ⱥ��Աid
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
	 * ���Ⱥ��Ա������gid��uid���ñ��ʾ����Ⱥ�����г�Ա
	 * @throws SQLException 
	 */
	static public boolean addGroupmemebr(int gid, String uid) throws SQLException {
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupmember (group_gid, user_uid) " + "values(?,?)";  // ��Ҫ�������߼���Ⱥ��Ա��
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
	 * ɾ��groupapply���е�ĳ����Ŀ
	 * @throws SQLException 
	 */
	static public boolean dropGroupApply (String uid, int gid) throws SQLException {
		if (Group.isExistGroupApply(uid, gid) == false) return false;  // ȷ�����ڸ���Ŀ
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
	 * ɾ��groupinvite���е�ĳ����Ŀ
	 * @throws SQLException 
	 */
	static public boolean dropGroupInvite (int gid, String uid) throws SQLException {
		if (Group.isExistGroupInvite(gid, uid) == false) return false;  // ȷ�����ڸ���Ŀ
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
	 * ��groupapply�����Ƿ����ĳ����Ŀ
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
		    	if (gid == rs.getInt(2) && uid.equals(rs.getString(1))) {  // �ҵ�ƥ���groupapply�еļ�¼
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
	 * ��groupinvite�����Ƿ����ĳ����Ŀ
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
		    	if (gid == rs.getInt(1) && uid.equals(rs.getString(2))) {  // �ҵ�ƥ���groupapply�еļ�¼
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
	 * Ŀ���û��Ƿ���Ŀ��Ⱥ��Ա
	 * @param uid Ŀ���û�id
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ���û��Ƿ���Ⱥ��Ա
	 * @throws SQLException 
	 */
	static public boolean isMember(String uid, int gid) throws SQLException {
		return Group.getGroupMembers(gid).contains(uid);
	}
}
