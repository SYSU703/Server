package fastchat;

import fastchat.Connectsql;
import models.*;

import java.util.List;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * @author wrf
 * ��̨������࣬���������ǰ�˴���������
 */
public class Handle {
	
	
	/**
	 * @author wrf
	 * ������֤�빦��
	 * @param targetEmail �����ע������
	 * @return ���͵���֤�룬���Ƿ�ƥ�䣬������ִ����򷵻�null
	 */
	static public String sendEmail(String targetEmail) {
    	try {
    	Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", "smtp.163.com");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        
        Session session = Session.getInstance(prop); // ��������ָ���ʼ��������Ự��session
        session.setDebug(true);
        Message message = Mail.createMessage(session, targetEmail); // �����Ự��Ϣ
        Transport ts = session.getTransport();
        ts.connect("m15639081168@163.com", "tigerone987"); // �������ʼ������������ڲ����Զ��������base64����
        ts.sendMessage(message, message.getAllRecipients()); // �����ʼ�Ŀ�ķ�
        ts.close(); // �Ͽ��������������
    	} catch (Exception e) {
    		return null;
    	}
        return Mail.a;
    }
	
	
	/**��¼ģ�麯����ʼ**/
	/**
	 * @author wsq
	 * �û���¼���ı��û�״̬Ϊ����״̬
	 * @param uid ��¼�û�id
	 * @param password ��¼�û�����
	 * @return ��¼�Ƿ�ɹ�
	 */
	static public boolean login(String uid, String password) {
		if (User.isExistUser(uid, password)) {
			User.changeState(uid, true);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @author wsq
	 * �û��ǳ����ı��û�״̬Ϊ����״̬
	 * @param uid ע���û���id
	 * @return �Ƿ�ɹ�ע����Ӧʼ��Ϊtrue
	 */
	static public boolean logout(String uid) {
		User.changeState(uid, false);
		return true;
	}
	
	/**
	 * @author wsq
	 * @param uId ע���û�id��Ϊ����@ǰ��ֵ�����Ψһ
	 * @param password ��¼����
	 * @param nickName �û��ǳ�
	 * @param birthday �û����գ�String���ͣ��ڱ������а�װ��Date����
	 * @param sex �Ա�
	 * @return ע���Ƿ�ɹ�
	 */
	static public boolean signin(String uid, String password, String nickName, String birthday, String sex) {
		if (User.isExistUser(uid)) {
			System.out.println("userId: " + uid + " is exist");
			return false;
		}
		Date birth;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			birth = sdf.parse(birthday);
		} catch (ParseException e) {
			return false;
		}
		boolean s = (sex == "male");
		return User.createUser(uid, password, nickName, birth, s);
	}
	/**��¼ģ�麯������**/
	
	
	
	
	
	/**������Ϣ��Ⱥ��Ϣģ�麯����ʼ**/
	/**
	 * @author wsq
	 * ��ȡĳ���˾��������Ϣ
	 * @param uid ��ѯ�û���id
	 * @return �������û���Ϣ�������û�id���ǳƣ����գ��Ա��Ƿ����ߣ�������û�������Ƕ������˵���������Ҳ��������
	 */
	static public CompleteUserInfo getCompleteUserInfo(String uid) {
		Connection conn = Connectsql.getConn();
		if (!User.isExistUser(uid)) return null;
		String sql = "select unickname, birthday, sex, userstate from user where uid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next())
		    	return new CompleteUserInfo(uid, rs.getString(1), rs.getDate(2), rs.getBoolean(3), rs.getBoolean(4));
		    return null;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * @author wsq
	 * @param uid �޸���Ϣ��id
	 * @param password �޸����룬���û���޸�������Ϊ֮ǰ��ֵ
	 * @param nickName �޸��ǳƣ����û���޸��ǳ���Ϊ֮ǰ��ֵ
	 * @param birthday �޸����գ����û���޸�������Ϊ֮ǰ��ֵ
	 * @param sex �޸��Ա����û���޸��Ա���Ϊ֮ǰ��ֵ
	 * @return �޸��Ƿ�ɹ�
	 */
	static public boolean modifyMyInfo(String uid, String password, String nickName, String birthday, String sex) {
		CompleteUserInfo I = Handle.getCompleteUserInfo(uid);
		String pw = password, nname = nickName, bir = birthday;
		boolean s = false;
		if (nickName == null)
			nname = I.getUserNickName();
		if (birthday == null)
			bir = I.getUserBirthday();
		if (sex == null)
			s = I.getUserSex() == "male";
		else
			s = sex == "male";
		Date birth;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			birth = sdf.parse(bir);
		} catch (ParseException e) {
			return false;
		}
		if (password == null) {
			Connection conn = Connectsql.getConn();
			String sql = "select password from user where uid=?";
			PreparedStatement pstmt;
			try {
			    pstmt = (PreparedStatement)conn.prepareStatement(sql);
			    pstmt.setString(1, uid);
			    ResultSet rs = pstmt.executeQuery();
			    while (rs.next())
			    	pw = rs.getString(1);
			} catch (SQLException e) {}
		}
		return User.modifyInfo(uid, pw, nname, birth, s);
	}
	/**������Ϣģ�麯������**/
	
	
	
	
	
	/**�û�����ģ�麯����ʼ**/
	/**
	 * ��ȡ�����б�
	 * @author wsq
	 * @param uid ��ǰ�û�id
	 * @return ��ǰ�û������б�ÿһ�����Ѽ�¼��Ϣ��������id�������ǳ��Լ�����״̬(�Ƿ�����)
	 */
	static public List<SimpleUserInfo> getAllFriendInfo(String uid) {
		List<SimpleUserInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, user.userstate from friendship, user "
				+ "where (friendship.user_uid1 =? and friendship.user_uid2=user.uid) or (friendship.user_uid2=? and friendship.user_uid1=user.uid)";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    pstmt.setString(2, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new SimpleUserInfo(rs.getString(1), rs.getString(2), rs.getBoolean(3)));
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * ��ȡȺ�б�
	 * @author wsq
	 * @param uid ��ǰ�û�id
	 * @return ��ǰ�û�Ⱥ�б�ÿһ��Ⱥ��¼��Ϣ����Ⱥid��Ⱥ��
	 */
	static public List<SimpleGroupInfo> getAllGroupInfo(String uid) {
		List<SimpleGroupInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select groupchat.gid, groupchat.groupname from groupchat, groupmember where groupmember.user_uid=? and groupchat.gid=groupmember.group_gid";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new SimpleGroupInfo(rs.getInt(1), rs.getString(2)));
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	/**�û�����ģ�麯������**/
	
	
	
	
	
	/**����ģ�麯����ʼ**/
	/**
	 * ������ȡ�û���Ҫ��Ϣ
	 * @author wsq
	 * @param uid ����Ŀ���û�id
	 * @return �û���Ҫ��Ϣ�������û�id���û��ǳƣ��û�״̬
	 */
	static public SimpleUserInfo getSimpleUserInfo(String uid) {
		Connection conn = Connectsql.getConn();
		String sql = "select unickname, userstate from user where uid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				return new SimpleUserInfo(uid, rs.getString(1), rs.getBoolean(2));
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * ������ȡȺ��Ҫ��Ϣ
	 * @author wsq
	 * @param gid ����Ⱥ��
	 * @return ��ҪȺ��Ϣ������Ⱥid��Ⱥ��
	 */
	static public SimpleGroupInfo getSimpleGroupInfo(int gid) {
		Connection conn = Connectsql.getConn();
		String sql = "select groupname from groupchat where gid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				return new SimpleGroupInfo(gid, rs.getString(1));
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * ��ȡ����Ⱥ��Ϣ
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ��Ⱥ������Ϣ������Ⱥid��Ⱥ���棬Ⱥ��������Աid������Ա���֣�Ⱥ����
	 */
	static public CompleteGroupInfo getCompleteGroupInfo(int gid) {
		int number = 0;
		Connection conn = Connectsql.getConn();
		String sql = "select groupchat.announcement, groupchat.groupname, user.uid, user.unickname "
		+ "from groupchat, user "
		+ "where groupchat.gid=? and groupchat.user_uid = user.uid";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			ResultSet rs = pstmt.executeQuery();
			number = Group.getGroupMembers(gid).size();
			while (rs.next())
				return new CompleteGroupInfo(gid, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), number);
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	/**����ģ�麯������**/
	
	
	
	
	
	/**���������Լ���Ӧģ�麯����ʼ**/
	/**
	 * ��ȡ��ǰ�û������ĺ���������Ϣ�б�
	 * @author wsq
	 * @param uid ��ǰ�û�id
	 * @return ��Ϣ�б�ÿһ����Ϣ����Ŀ���û�id��Ŀ���û��ǳƣ�����״̬
	 */
	static public List<FriendAddInfo> getFriendApplyInfo(String uid) {
		return Friend.getReceiverInfo(uid);
	}
	
	/**
	 * ��ȡ��ǰ�û����յĺ���������Ϣ�б�
	 * @author wsq
	 * @param uid ��ǰ�û�id
	 * @return ��Ϣ�б�ÿһ����Ϣ����Ŀ���û�id��Ŀ���û��ǳƣ�����״̬
	 */
	static public List<FriendAddInfo> getFriendInviteInfo(String uid) {
		return Friend.getSenderInfo(uid);
	}
	
	/**
	 * @author wrf
	 * ��������uid������friendadd������������״̬Ϊδ��������Ϊ0
	 * 
	 * @modifier wsq ��Ӷ�˫���Ƿ��ں��������Ϣ��¼���д��ڵ��ж�
	 */
	static public boolean sendFriendRequest(String sender_uid, String receiver_uid) {
		if (Friend.isExistFriendAdd(sender_uid, receiver_uid) || Friend.isExistFriendAdd(receiver_uid, sender_uid))
			return false;
		Iterator it1 = User.getAllFriend(sender_uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(receiver_uid)) return false;  // ���ȱ�֤���߲���Ϊ����
 		}
 		Connection conn = Connectsql.getConn();
		String sql = "insert into friendadd (state, sender_uid, receiver_uid) "
				+ "values(0,?,?)";
	    PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        pstmt.setString(1, sender_uid);
 	        pstmt.setString(2, receiver_uid);
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
	 * ���ݴ����agree�ж��Ƿ�ͬ����ӣ����agreeΪtrue������Ӧ��Ŀ״̬��Ϊ1��ͬʱ����friendship��agreeΪfalse��ʾ�ܾ���ֱ��ɾ������Ŀ��
	 */
	static public boolean handleFriendApply(String s_uid, String r_uid, boolean agree) {
		if (agree == false) {
			Friend.dropFriendAdd(s_uid, r_uid);
			return true;
		} else if (agree == true) {
			if (Friend.isExistFriendAdd(s_uid, r_uid) == false) return false;
			Connection conn = Connectsql.getConn();
			String sql = "update friendadd set state='1' where sender_uid=? and receiver_uid=?";
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
			Friend.addFriendintoFriendship(s_uid, r_uid);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * ���������û���ĺ��ѹ�ϵ
	 * @author wsq
	 * @param uid1 �û�1id
	 * @param uid2 �û�2id
	 * @return ɾ���Ƿ�ɹ�
	 */
	static public boolean deleteFriend(String uid1, String uid2) {
		if (!User.getAllFriend(uid1).contains(uid2))
			return false;
		Friend.dropFriend(uid1, uid2);
		Friend.dropFriend2(uid1, uid2);
		Record.clearFriendRecord(uid1, uid2); // ɾ������֮���ͨ�ż�¼
		return true;
	}
	/**���������Լ���Ӧģ�麯������**/
	
	
	
	/**Ⱥ���롢�����Լ���Ӧģ�麯����ʼ**/
	/**
	 * @author wrf
	 * ����һ��Ⱥ�������󣬸���groupapply��
	 */
	static public boolean sendGroupApply (String uid, int gid) {
		Iterator it1 = User.getAllGroup(uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(gid)) return false;  // ���ȱ�֤���û�������Ⱥ��Ա
 		}
		if (Group.isExistGroup(gid) == false) return false;  // ȷ��Ⱥ����
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupapply (user_uid, group_gid) "
				+ "values(?,?)"; 
		PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql); 
 	        pstmt.setString(1, uid);
 	        pstmt.setInt(2, gid); 
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
		} catch (SQLException e) {
			//e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	/**
	 * @author wrf
	 * ����һ��Ⱥ�������󣬸���groupinvite��
	 */
	static public boolean sendGroupInvite (int gid, String uid) {
		Iterator it1 = User.getAllGroup(uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(gid)) return false;  // ���ȱ�֤���û�������Ⱥ��Ա
 		}
		if (Group.isExistGroup(gid) == false) return false;  // ȷ��Ⱥ����
		Connection conn = Connectsql.getConn();
		String sql = "insert into groupinvite (group_gid, user_uid) "
				+ "values(?,?)"; 
		PreparedStatement pstmt;
	    try {
 	        pstmt = (PreparedStatement) conn.prepareStatement(sql); 
 	        pstmt.setString(2, uid);
 	        pstmt.setInt(1, gid); 
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
		} catch (SQLException e) {
			//e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	/**
	 * Ⱥ����Ա�յ����õ�������������Ⱥ���û���Ϣ
	 * �����û�ID���û��ǳ�
	 * @author wsq
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ��Ⱥ�յ����û������б�ÿһ����¼�����û�ID���û��ǳƣ��û�״̬(���Ժ���)
	 */
	static public List<SimpleUserInfo> getAllGroupApply (int gid) {
		List<SimpleUserInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, user.userstate from groupapply, user where user.uid=groupapply.user_uid and groupapply.group_gid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new SimpleUserInfo(rs.getString(1), rs.getString(2), rs.getBoolean(3)));
		    }
		    return info;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * �û��յ�����������Ⱥ����Ϣ
	 * ����Ⱥid��Ⱥ����Ⱥ��id��Ⱥ���ǳ�
	 * @author wsq
	 * @param uid Ŀ���û�id
	 * @return Ŀ���û��յ���Ⱥ�����б�ÿһ����¼����Ⱥid��Ⱥ��������Աid,����Ա�ǳ�
	 */
	static public List<GroupInviteInfo> getAllGroupInvite(String uid) {
		List<GroupInviteInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select groupinvite.group_gid, groupchat.groupname, user.uid, user.unickname "
				+ "from groupinvite, groupchat, user "
				+ "where groupinvite.user_uid=? and groupinvite.group_gid=groupchat.gid and groupchat.user_uid=user.uid";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new GroupInviteInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
		    }
		    return info;
		} catch (SQLException e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * @author wrf
	 * ͨ��agree������ж��Ƿ�ͬ���Ⱥ�����룬��������ش���
	 */
	static public boolean handleGroupApply (int gid, String uid, boolean agree) {
		if (agree == false) {
			if (Group.dropGroupApply(uid, gid) == true) return true;  // �ܾ����룬��groupapply��Ӧ��Ŀɾ��
		} else {
			if (Group.isExistGroupApply(uid, gid) == true) {
				if (Group.addGroupmemebr(gid, uid) == true && Group.dropGroupApply(uid, gid) == true) return true;
			}		// ͬ�����룬��groupapply��Ӧ��Ŀɾ������groupmember���������
		}
		return false;
	}
	
	
	/**
	 * @author wrf
	 * ͨ��agree������ж��Ƿ�ͬ���Ⱥ�����룬��������ش���
	 */
	static public boolean handleGroupInvite (int gid, String uid, boolean agree) {
		if (agree == false) {
			if (Group.dropGroupInvite(gid, uid) == true) return true;  // // �ܾ����룬��groupinvite��Ӧ��Ŀɾ��
		} else {
			if (Group.isExistGroupInvite(gid, uid) == true) {
				if (Group.addGroupmemebr(gid, uid) == true && Group.dropGroupInvite(gid, uid) == true) return true;
			}	// ͬ�����룬��groupinvite��Ӧ��Ŀɾ������groupmember���������
		}
		return false;
	}
	
	/**Ⱥ���롢�����Լ���Ӧģ�麯������**/
	
	
	
	
	
	/**Ⱥ����ģ�麯����ʼ**/
	/**
	 * ����Ⱥ��Ⱥ����ϵͳͨ����������Ψһ����ʼʱ��������Ϊ�գ�����ԱΪ������
	 * @author wsq
	 * @param uid ������id
	 * @param gName Ⱥ��
	 * @return Ⱥ��
	 */
	static public String createGroup(String uid, String gName) {
		int answer = Group.createGroup("", gName, uid);
		if (answer == -1)
			return "fail";
		return String.valueOf(answer);
	}
	
	/**
	 * ��ȡĿ��Ⱥ�Ĺ���Աid�������ڷ���Ȩ�ޣ��Լ���Ϣ֪ͨ
	 * @author wsq
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ��Ⱥ����Աid
	 */
	static public String getGroupManager(int gid) {
		if (!Group.isExistGroup(gid)) return null;
		Connection conn = Connectsql.getConn();
		String sql = "select user_uid from groupchat where gid = ?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setInt(1, gid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				return rs.getString(1);
			return null;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * @author wrf
	 * ����gid��uid���ı������
	 */
	static public boolean changeManager(int gid, String uid) {
		if (Group.isExistGroup(gid) == false) return false;
		Iterator it1 = User.getAllGroup(uid).iterator(); 
		if (it1.hasNext() == false) return false;  // ��ʾ���û�û�м�Ⱥ
		while (it1.hasNext()) {
		    if (it1.next().equals(gid)) break;
		 	if (it1.hasNext() == false) return false;  // ��ʾ���û�û�м����Ⱥ
		}
		Connection conn = Connectsql.getConn();
		String sql = "update groupchat set user_uid=? where gid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setString(1, uid);
		    pstmt.setInt(2, gid);
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
	 * ����gid�������޸Ĺ����Ⱥ���ƣ�Ⱥ����Ա����һ���������޸�
	 */
	static public boolean modifyGroup(int gid, String announcement, String groupname) {
		if (Group.isExistGroup(gid) == false) return false;
		Connection conn = Connectsql.getConn();
		CompleteGroupInfo group = Handle.getCompleteGroupInfo(gid);
		String ann = announcement, gname = groupname;
		if (announcement == null)
			ann = group.getAnnouncement();
		if (groupname == null)
			gname = group.getGroupName();
		String sql = "update groupchat set announcement=?, groupname=? where gid=?";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    pstmt.setString(1, ann);
		    pstmt.setString(2, gname);
		    pstmt.setInt(3, gid);
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
	 * ��ɢȺ������groupmember��groupinvite��groupapply��
	 */
	static public boolean dissolveGroup (int gid) {
		if (Group.isExistGroup(gid) == false) return false;
		Connection conn = Connectsql.getConn();
		String sql2 = "delete from groupinvite where group_gid=?";
		String sql3 = "delete from groupapply where group_gid=?";
		String sql1 = "delete from groupmember where group_gid=?";
		PreparedStatement pstmt1, pstmt2, pstmt3;
		try {
		    pstmt1 = (PreparedStatement) conn.prepareStatement(sql1);
		    pstmt2 = (PreparedStatement) conn.prepareStatement(sql2); 
		    pstmt3 = (PreparedStatement) conn.prepareStatement(sql3); 
		    pstmt1.setInt(1, gid);
		    pstmt2.setInt(1, gid);
		    pstmt3.setInt(1, gid);
		    pstmt1.executeUpdate();    
		    pstmt2.executeUpdate(); 
		    pstmt3.executeUpdate(); 
		    pstmt1.close();
		    pstmt2.close();
		    pstmt3.close();
		    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		Record.clearGroupRecord(gid);
		if (Group.dropGroupBygid(gid) == false) return false;  // ��Ϊ����Լ�������Ա������ɾ��groupchat���е�����
		return true;
	}
	
	/**
	 * ��ȡĿ��Ⱥ��Ա��Ҫ��Ϣ
	 * @author wsq
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ��Ⱥ��Ա�б�ÿ����¼�����û�id���ǳ��Լ��Ƿ�����
	 */
	static public List<SimpleUserInfo> getGroupMembers(int gid) {
		List<SimpleUserInfo> info = new ArrayList<>();
		Connection conn = Connectsql.getConn();
		String sql = "select user.uid, user.unickname, user.userstate from groupmember, user "
				+ "where groupmember.group_gid =? and groupmember.user_uid=user.uid";
		PreparedStatement pstmt;
		try {
		    pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    pstmt.setInt(1, gid);
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
		    	info.add(new SimpleUserInfo(rs.getString(1), rs.getString(2), rs.getBoolean(3)));
		    }
		    return info;
		} catch (SQLException e) {}
		return null;
	}
	
	/**
	 * ��Ⱥ�������û������ǹ���Ա
	 * @author wsq
	 * @param uid Ŀ���û�
	 * @param gid Ŀ��Ⱥ
	 * @return ��Ⱥ�Ƿ�ɹ�
	 */
	static public boolean quitGroup(String uid, int gid) {
		if (uid.equals(Handle.getGroupManager(gid))) return false; // ��ǰ�û�Ϊ����Ա
		Connection conn = Connectsql.getConn();
		String sql = "delete from groupmember where user_uid=? and group_gid=?";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setInt(2, gid);
			pstmt.executeUpdate();
			Record.readRecordOfGroup(uid, gid);
			Record.clearGroupRecordForUser(uid, gid);
			return true;
		} catch (SQLException e) {}
		return false;
	}
	/**Ⱥ����ģ�麯������**/
	
	
	
	
	
	
	/**������Ϣ��¼ģ�麯����ʼ**/
	/**
	 * ����Ŀ���û���Ŀ����ѵ������¼�б�
	 * @author wsq
	 * @param uid Ŀ���û�id
	 * @param friend_uid ����id
	 * @return �����¼�б�ÿһ����Ϣ������Ϣ���ݣ�������ID��������ID����¼ʱ��
	 */
	static public List<FriendRecordInfo> getRecordWithFriend(String uid, String friend_uid) {
		List<Integer> rids = Record.getRecordOfFriendIdsByUser(uid, friend_uid);
		List<FriendRecordInfo> info = new ArrayList<>();
		for (int i = 0; i < rids.size(); i++)
			info.add(Record.getRecordOfFriendInfoById(rids.get(i)));
		Record.readAllRecordOfFriend(uid); // ��ʱ�Ѿ���ȡ��������Ϣ�����ԭ�ȵ�δ����Ϣ��Ϊ�Ѷ�
		return info;
	}
	
	/**
	 * ������Ϣ�������ݿ�����Ӹ�����¼
	 * @author wsq
	 * @param sender_uid ������id
	 * @param receiver_uid ������id
	 * @param message ��Ϣ
	 * @return ������Ϣ�Ƿ�ɹ�
	 */
	static public boolean sendFriendRecord(String sender_uid, String receiver_uid, String message) {
		Date currentTime = new Date();
		return Record.createFriendRecord(message, sender_uid, receiver_uid, currentTime);
	}
	
	/**
	 * ��ȡ��Ŀ���û�����δ����Ϣ���û��б�
	 * @author wsq
	 * @param uid Ŀ���û�
	 * @return ��Ŀ���û�������δ����Ϣ���û��б�ÿ����¼�����û�id���û��ǳƣ�״̬(�Ƿ�����)
	 */
	static public List<SimpleUserInfo> getFriendNotRead(String uid) {
		List<String> uids = Record.getFriendNotRead(uid);
		List<SimpleUserInfo> info = new ArrayList<>();
		for (int i = 0; i < uids.size(); i++)
			info.add(Handle.getSimpleUserInfo(uids.get(i)));
		return info;
	}
	/**������Ϣ��¼ģ�麯������**/
	
	

	/**Ⱥ��Ϣ��¼ģ�鿪ʼ**/
	
	/**
	 * ��ȡĿ���û�����δ����Ϣ��Ⱥ�б�
	 * @author wsq
	 * @param uid Ŀ��id
	 * @return Ŀ���û�����δ����Ϣ���б�ÿ����¼����Ⱥid��Ⱥ��
	 */
	static public List<SimpleGroupInfo> getGroupNotRead(String uid) {
		List<Integer> gids = Record.getGroupNotRead(uid);
		List<SimpleGroupInfo> info = new ArrayList<> ();
		for (int i = 0; i < gids.size(); i++)
			info.add(Handle.getSimpleGroupInfo(gids.get(i)));
		return info;
	}
	
	/**
	 * ��ȡĿ��Ⱥ�����¼
	 * @author wsq
	 * @param uid Ŀ���û�id
	 * @param gid Ŀ��Ⱥid
	 * @return Ŀ��Ⱥ���������¼��ÿ����¼�������ݣ�������id��Ⱥid����Ϣʱ��
	 */
	static public List<GroupRecordInfo> getRecordWithGroup(String uid, int gid) {
		if (!Group.isMember(uid, gid))
			return null;
		List<GroupRecordInfo> info = new ArrayList<> ();
		List<Integer> rids = Record.getRecordOfGroupInfoByGroup(gid);
		for (int i = 0; i < rids.size(); i++) {
			info.add(Record.getRecordOfGroupInfoById(rids.get(i)));
		}
		Record.readRecordOfGroup(uid, gid); // ��Ⱥ������δ����Ϣ��Ϊ�Ѷ�
		return info;
	}
	
	/**
	 * ��Ⱥ�з�����Ϣ
	 * @param message ��Ϣ����
	 * @param sender_id ������id
	 * @param gid Ⱥid
	 * @return ������Ϣ�Ƿ�ɹ�
	 */
	static public boolean sendGroupRecord(String message, String sender_id, int gid) {
		if (!Group.isMember(sender_id, gid))
			return false;
		Date currentTime = new Date();
		return Record.createGroupRecord(message, sender_id, gid, currentTime);
	}
	/**Ⱥ��Ϣ��¼ģ�����**/
}
