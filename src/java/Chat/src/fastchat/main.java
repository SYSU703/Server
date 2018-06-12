package fastchat;

import fastchat.Handle;
import fastchat.Connectsql;
import fastchat.User;
import fastchat.Friend;
import fastchat.Group;
import java.sql.Date;
import java.util.Iterator;  
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Handle handle = new Handle();
		System.out.println(handle.login("df", "ddf"));
		/*sendFriendRequest/dropFriend/dropFriendAdd/isExistFriendAdd函数
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("请输入用户1uid：");   
		String a = sc_a.nextLine();  //读取字符串型输入
		Scanner sc_b = new Scanner(System.in);   
		System.out.println("请输入用户2uid：");   
		String b = sc_b.nextLine();  //读取字符串型输入
		System.out.println(Handle.changeFriendAddState(a, b, true));
		*/
		
		/*测试getGroupbyGid/getAllGroupApply函数
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("请输入群名字：");   
		int a = sc_a.nextInt();
		System.out.println(Handle.dissolveGroup(a));
		//Iterator it1 = Handle.getAllGroupApply(a).iterator(); 
 		//while (it1.hasNext()) {
 		//	System.out.println(it1.next());
 		//}
 		*/
		
		
		/*测试createGroup/modifyGroup函数
		Scanner sc_q = new Scanner(System.in);   
		System.out.println("请输入id：");   
		int gid = sc_q.nextInt();  //读取字符串型输入
		
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("请输入公告：");   
		String announcement = sc_a.nextLine();  //读取字符串型输入
		
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("请输入群昵称：");   
		String groupname = sc_b.nextLine();  //读取字符串型输入
		 
		Scanner sc_c = new Scanner(System.in);  
		System.out.println("请输入创立者：");   
		String user_uid = sc_c.nextLine();  //读取字符串型输入
		
		System.out.println(Group.createGroup(gid, announcement, groupname, user_uid));
		*/

		
		
		/*测试getInfo/getAllFriend/getSenderUid/getRecieverUid/getAllGroup/getAllManageGroup/getAllGroupInvite函数
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("请输入用户名：");   
		String username = sc_a.nextLine();  //读取字符串型输入
		Iterator it1 = User.getAllFriend(username).iterator(); 
 		while (it1.hasNext()) {
 			System.out.println(it1.next());
 		}
 		*/
 		
		
		/*测试addGroupMember/changeManager/sendGroupApply/sendGroupInvite/handleGroupInvite/handleGroupSend函数
		Scanner sc_q = new Scanner(System.in);   
		System.out.println("请输入群id：");   
		int gid = sc_q.nextInt();
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("请输入uid：");   
		String uid = sc_b.nextLine();  //读取字符串型输入
		System.out.println(Handle.handleGroupInvite(gid, uid, true));
		*/
		
 		
 		
		/*测试createUser/modifyinfo函数
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("请输入用户名：");   
		String username = sc_a.nextLine();  //读取字符串型输入
		
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("请输入密码：");   
		String password = sc_b.nextLine();  //读取字符串型输入
		 
		Scanner sc_c = new Scanner(System.in);  
		System.out.println("请输入昵称：");   
		String unickname = sc_c.nextLine();  //读取字符串型输入

		Scanner sc_d = new Scanner(System.in);  
		System.out.println("请输入生日：");   
		String a = sc_d.nextLine();  //读取字符串型输入
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		java.util.Date birthday;
		try {
			birthday = sdf.parse(a);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}  
		
		Scanner sc_e = new Scanner(System.in);  
		System.out.println("请输入性别：");   
		boolean sex = sc_e.nextLine() != null;  //读取字符串型输入
		System.out.println(User.modifyInfo(username, password, unickname, birthday));
		*/
        

		
	}

}
