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
 * 后台处理的类，用来处理从前端传来的数据
 */
public class Handle {
	
	
	/**
	 * @author wrf
	 * 发送验证码功能
	 * @param targetEmail 输入的注册邮箱
	 * @return 发送的验证码，看是否匹配，如果出现错误则返回null
	 */
	static public String sendEmail(String targetEmail) {
    	try {
    	Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", "smtp.163.com");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        
        Session session = Session.getInstance(prop); // 创建出与指定邮件服务器会话的session
        session.setDebug(true);
        Message message = Mail.createMessage(session, targetEmail); // 创建会话信息
        Transport ts = session.getTransport();
        ts.connect("m15639081168@163.com", "tigerone987"); // 连接上邮件服务器，其内部会自动帮你进行base64编码
        ts.sendMessage(message, message.getAllRecipients()); // 发送邮件目的方
        ts.close(); // 断开与服务器的连接
    	} catch (Exception e) {
    		return null;
    	}
        return Mail.a;
    }
	
	
	/**登录模块函数开始**/
	/**
	 * @author wsq
	 * 用户登录，改变用户状态为在线状态
	 * @param uid 登录用户id
	 * @param password 登录用户密码
	 * @return 登录是否成功
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
	 * 用户登出，改变用户状态为离线状态
	 * @param uid 注销用户的id
	 * @return 是否成功注销，应始终为true
	 */
	static public boolean logout(String uid) {
		User.changeState(uid, false);
		return true;
	}
	
	/**
	 * @author wsq
	 * @param uId 注册用户id，为邮箱@前的值，因此唯一
	 * @param password 登录密码
	 * @param nickName 用户昵称
	 * @param birthday 用户生日，String类型，在本函数中包装成Date类型
	 * @param sex 性别
	 * @return 注册是否成功
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
	/**登录模块函数结束**/
	
	
	
	
	
	/**个人信息及群信息模块函数开始**/
	/**
	 * @author wsq
	 * 获取某个人具体个人信息
	 * @param uid 查询用户的id
	 * @return 完整的用户信息，包括用户id，昵称，生日，性别，是否在线，如果是用户自身而非对其他人的搜索，则也返回密码
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
	 * @param uid 修改信息者id
	 * @param password 修改密码，如果没有修改密码则为之前的值
	 * @param nickName 修改昵称，如果没有修改昵称则为之前的值
	 * @param birthday 修改生日，如果没有修改生日则为之前的值
	 * @param sex 修改性别，如果没有修改性别则为之前的值
	 * @return 修改是否成功
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
	/**个人信息模块函数结束**/
	
	
	
	
	
	/**用户界面模块函数开始**/
	/**
	 * 获取好友列表
	 * @author wsq
	 * @param uid 当前用户id
	 * @return 当前用户好友列表，每一条好友记录信息包含好友id，好友昵称以及好友状态(是否在线)
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
	 * 获取群列表
	 * @author wsq
	 * @param uid 当前用户id
	 * @return 当前用户群列表，每一条群记录信息包含群id，群名
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
	/**用户界面模块函数结束**/
	
	
	
	
	
	/**搜索模块函数开始**/
	/**
	 * 搜索获取用户简要信息
	 * @author wsq
	 * @param uid 搜索目标用户id
	 * @return 用户简要信息，包括用户id，用户昵称，用户状态
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
	 * 搜索获取群简要信息
	 * @author wsq
	 * @param gid 搜索群号
	 * @return 简要群信息，包括群id，群名
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
	 * 获取完整群信息
	 * @param gid 目标群id
	 * @return 目标群完整信息，包括群id，群公告，群名，管理员id，管理员名字，群人数
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
	/**搜索模块函数结束**/
	
	
	
	
	
	/**好友申请以及回应模块函数开始**/
	/**
	 * 获取当前用户发出的好友申请消息列表
	 * @author wsq
	 * @param uid 当前用户id
	 * @return 消息列表，每一条信息包括目标用户id，目标用户昵称，受理状态
	 */
	static public List<FriendAddInfo> getFriendApplyInfo(String uid) {
		return Friend.getReceiverInfo(uid);
	}
	
	/**
	 * 获取当前用户接收的好友请求消息列表
	 * @author wsq
	 * @param uid 当前用户id
	 * @return 消息列表，每一条信息包括目标用户id，目标用户昵称，受理状态
	 */
	static public List<FriendAddInfo> getFriendInviteInfo(String uid) {
		return Friend.getSenderInfo(uid);
	}
	
	/**
	 * @author wrf
	 * 传入两个uid，更新friendadd好友添加邀请表，状态为未处理设置为0
	 * 
	 * @modifier wsq 添加对双方是否在好友添加消息记录表中存在的判断
	 */
	static public boolean sendFriendRequest(String sender_uid, String receiver_uid) {
		if (Friend.isExistFriendAdd(sender_uid, receiver_uid) || Friend.isExistFriendAdd(receiver_uid, sender_uid))
			return false;
		Iterator it1 = User.getAllFriend(sender_uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(receiver_uid)) return false;  // 首先保证两者不能为好友
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
	 * 根据传入的agree判断是否同意添加，如果agree为true，将对应条目状态改为1，同时更新friendship表，agree为false表示拒绝，直接删除该条目，
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
	 * 消除两个用户间的好友关系
	 * @author wsq
	 * @param uid1 用户1id
	 * @param uid2 用户2id
	 * @return 删除是否成功
	 */
	static public boolean deleteFriend(String uid1, String uid2) {
		if (!User.getAllFriend(uid1).contains(uid2))
			return false;
		Friend.dropFriend(uid1, uid2);
		Friend.dropFriend2(uid1, uid2);
		Record.clearFriendRecord(uid1, uid2); // 删除两人之间的通信记录
		return true;
	}
	/**好友申请以及回应模块函数结束**/
	
	
	
	/**群申请、邀请以及回应模块函数开始**/
	/**
	 * @author wrf
	 * 发送一个群加入请求，更新groupapply表
	 */
	static public boolean sendGroupApply (String uid, int gid) {
		Iterator it1 = User.getAllGroup(uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(gid)) return false;  // 首先保证该用户还不是群成员
 		}
		if (Group.isExistGroup(gid) == false) return false;  // 确定群存在
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
	 * 发送一个群邀请请求，更新groupinvite表
	 */
	static public boolean sendGroupInvite (int gid, String uid) {
		Iterator it1 = User.getAllGroup(uid).iterator(); 
 		while (it1.hasNext()) {
 			if (it1.next().equals(gid)) return false;  // 首先保证该用户还不是群成员
 		}
		if (Group.isExistGroup(gid) == false) return false;  // 确定群存在
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
	 * 群管理员收到，得到所有申请加入该群的用户信息
	 * 包括用户ID，用户昵称
	 * @author wsq
	 * @param gid 目标群id
	 * @return 目标群收到的用户申请列表，每一条记录包括用户ID，用户昵称，用户状态(可以忽略)
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
	 * 用户收到的邀请加入的群的信息
	 * 包括群id，群名，群主id，群主昵称
	 * @author wsq
	 * @param uid 目标用户id
	 * @return 目标用户收到的群邀请列表，每一条记录包括群id，群名，管理员id,管理员昵称
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
	 * 通过agree真假来判断是否同意加群的申请，并作出相关处理
	 */
	static public boolean handleGroupApply (int gid, String uid, boolean agree) {
		if (agree == false) {
			if (Group.dropGroupApply(uid, gid) == true) return true;  // 拒绝申请，将groupapply对应条目删除
		} else {
			if (Group.isExistGroupApply(uid, gid) == true) {
				if (Group.addGroupmemebr(gid, uid) == true && Group.dropGroupApply(uid, gid) == true) return true;
			}		// 同意申请，将groupapply对应条目删除，在groupmember表里面添加
		}
		return false;
	}
	
	
	/**
	 * @author wrf
	 * 通过agree真假来判断是否同意加群的邀请，并作出相关处理
	 */
	static public boolean handleGroupInvite (int gid, String uid, boolean agree) {
		if (agree == false) {
			if (Group.dropGroupInvite(gid, uid) == true) return true;  // // 拒绝申请，将groupinvite对应条目删除
		} else {
			if (Group.isExistGroupInvite(gid, uid) == true) {
				if (Group.addGroupmemebr(gid, uid) == true && Group.dropGroupInvite(gid, uid) == true) return true;
			}	// 同意申请，将groupinvite对应条目删除，在groupmember表里面添加
		}
		return false;
	}
	
	/**群申请、邀请以及回应模块函数结束**/
	
	
	
	
	
	/**群管理模块函数开始**/
	/**
	 * 创建群，群号由系统通过随机获得且唯一，初始时公告内容为空，管理员为创建者
	 * @author wsq
	 * @param uid 创建者id
	 * @param gName 群名
	 * @return 群号
	 */
	static public String createGroup(String uid, String gName) {
		int answer = Group.createGroup("", gName, uid);
		if (answer == -1)
			return "fail";
		return String.valueOf(answer);
	}
	
	/**
	 * 获取目标群的管理员id，可用于分配权限，以及消息通知
	 * @author wsq
	 * @param gid 目标群id
	 * @return 目标群管理员id
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
	 * 传入gid和uid，改变管理者
	 */
	static public boolean changeManager(int gid, String uid) {
		if (Group.isExistGroup(gid) == false) return false;
		Iterator it1 = User.getAllGroup(uid).iterator(); 
		if (it1.hasNext() == false) return false;  // 表示该用户没有加群
		while (it1.hasNext()) {
		    if (it1.next().equals(gid)) break;
		 	if (it1.hasNext() == false) return false;  // 表示该用户没有加入改群
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
	 * 传入gid，可以修改公告和群名称，群管理员在另一个函数中修改
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
	 * 解散群，更新groupmember表、groupinvite和groupapply表
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
		if (Group.dropGroupBygid(gid) == false) return false;  // 因为主键约束，所以必须最后删除groupchat表中的内容
		return true;
	}
	
	/**
	 * 获取目标群成员简要信息
	 * @author wsq
	 * @param gid 目标群id
	 * @return 目标群成员列表，每条记录包含用户id，昵称以及是否在线
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
	 * 退群操作，用户不能是管理员
	 * @author wsq
	 * @param uid 目标用户
	 * @param gid 目标群
	 * @return 退群是否成功
	 */
	static public boolean quitGroup(String uid, int gid) {
		if (uid.equals(Handle.getGroupManager(gid))) return false; // 当前用户为管理员
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
	/**群管理模块函数结束**/
	
	
	
	
	
	
	/**好友消息记录模块函数开始**/
	/**
	 * 返回目标用户与目标好友的聊天记录列表
	 * @author wsq
	 * @param uid 目标用户id
	 * @param friend_uid 好友id
	 * @return 聊天记录列表，每一条信息包括信息内容，发送人ID，接收人ID，记录时间
	 */
	static public List<FriendRecordInfo> getRecordWithFriend(String uid, String friend_uid) {
		List<Integer> rids = Record.getRecordOfFriendIdsByUser(uid, friend_uid);
		List<FriendRecordInfo> info = new ArrayList<>();
		for (int i = 0; i < rids.size(); i++)
			info.add(Record.getRecordOfFriendInfoById(rids.get(i)));
		Record.readAllRecordOfFriend(uid); // 此时已经获取了所有消息，因此原先的未读消息变为已读
		return info;
	}
	
	/**
	 * 发送消息，在数据库中添加该条记录
	 * @author wsq
	 * @param sender_uid 发送者id
	 * @param receiver_uid 接收者id
	 * @param message 信息
	 * @return 发送信息是否成功
	 */
	static public boolean sendFriendRecord(String sender_uid, String receiver_uid, String message) {
		Date currentTime = new Date();
		return Record.createFriendRecord(message, sender_uid, receiver_uid, currentTime);
	}
	
	/**
	 * 获取向目标用户发送未读消息的用户列表
	 * @author wsq
	 * @param uid 目标用户
	 * @return 向目标用户发送了未读消息的用户列表，每条记录包括用户id，用户昵称，状态(是否在线)
	 */
	static public List<SimpleUserInfo> getFriendNotRead(String uid) {
		List<String> uids = Record.getFriendNotRead(uid);
		List<SimpleUserInfo> info = new ArrayList<>();
		for (int i = 0; i < uids.size(); i++)
			info.add(Handle.getSimpleUserInfo(uids.get(i)));
		return info;
	}
	/**好友消息记录模块函数结束**/
	
	

	/**群消息记录模块开始**/
	
	/**
	 * 获取目标用户具有未读消息的群列表
	 * @author wsq
	 * @param uid 目标id
	 * @return 目标用户具有未读消息的列表，每条记录包括群id，群名
	 */
	static public List<SimpleGroupInfo> getGroupNotRead(String uid) {
		List<Integer> gids = Record.getGroupNotRead(uid);
		List<SimpleGroupInfo> info = new ArrayList<> ();
		for (int i = 0; i < gids.size(); i++)
			info.add(Handle.getSimpleGroupInfo(gids.get(i)));
		return info;
	}
	
	/**
	 * 获取目标群聊天记录
	 * @author wsq
	 * @param uid 目标用户id
	 * @param gid 目标群id
	 * @return 目标群所以聊天记录，每条记录包括内容，发送者id，群id，消息时间
	 */
	static public List<GroupRecordInfo> getRecordWithGroup(String uid, int gid) {
		if (!Group.isMember(uid, gid))
			return null;
		List<GroupRecordInfo> info = new ArrayList<> ();
		List<Integer> rids = Record.getRecordOfGroupInfoByGroup(gid);
		for (int i = 0; i < rids.size(); i++) {
			info.add(Record.getRecordOfGroupInfoById(rids.get(i)));
		}
		Record.readRecordOfGroup(uid, gid); // 将群中所有未读消息置为已读
		return info;
	}
	
	/**
	 * 向群中发送消息
	 * @param message 消息内容
	 * @param sender_id 发送者id
	 * @param gid 群id
	 * @return 发送消息是否成功
	 */
	static public boolean sendGroupRecord(String message, String sender_id, int gid) {
		if (!Group.isMember(sender_id, gid))
			return false;
		Date currentTime = new Date();
		return Record.createGroupRecord(message, sender_id, gid, currentTime);
	}
	/**群消息记录模块结束**/
}
