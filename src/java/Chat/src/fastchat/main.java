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
		/*sendFriendRequest/dropFriend/dropFriendAdd/isExistFriendAdd����
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("�������û�1uid��");   
		String a = sc_a.nextLine();  //��ȡ�ַ���������
		Scanner sc_b = new Scanner(System.in);   
		System.out.println("�������û�2uid��");   
		String b = sc_b.nextLine();  //��ȡ�ַ���������
		System.out.println(Handle.changeFriendAddState(a, b, true));
		*/
		
		/*����getGroupbyGid/getAllGroupApply����
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("������Ⱥ���֣�");   
		int a = sc_a.nextInt();
		System.out.println(Handle.dissolveGroup(a));
		//Iterator it1 = Handle.getAllGroupApply(a).iterator(); 
 		//while (it1.hasNext()) {
 		//	System.out.println(it1.next());
 		//}
 		*/
		
		
		/*����createGroup/modifyGroup����
		Scanner sc_q = new Scanner(System.in);   
		System.out.println("������id��");   
		int gid = sc_q.nextInt();  //��ȡ�ַ���������
		
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("�����빫�棺");   
		String announcement = sc_a.nextLine();  //��ȡ�ַ���������
		
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("������Ⱥ�ǳƣ�");   
		String groupname = sc_b.nextLine();  //��ȡ�ַ���������
		 
		Scanner sc_c = new Scanner(System.in);  
		System.out.println("�����봴���ߣ�");   
		String user_uid = sc_c.nextLine();  //��ȡ�ַ���������
		
		System.out.println(Group.createGroup(gid, announcement, groupname, user_uid));
		*/

		
		
		/*����getInfo/getAllFriend/getSenderUid/getRecieverUid/getAllGroup/getAllManageGroup/getAllGroupInvite����
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("�������û�����");   
		String username = sc_a.nextLine();  //��ȡ�ַ���������
		Iterator it1 = User.getAllFriend(username).iterator(); 
 		while (it1.hasNext()) {
 			System.out.println(it1.next());
 		}
 		*/
 		
		
		/*����addGroupMember/changeManager/sendGroupApply/sendGroupInvite/handleGroupInvite/handleGroupSend����
		Scanner sc_q = new Scanner(System.in);   
		System.out.println("������Ⱥid��");   
		int gid = sc_q.nextInt();
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("������uid��");   
		String uid = sc_b.nextLine();  //��ȡ�ַ���������
		System.out.println(Handle.handleGroupInvite(gid, uid, true));
		*/
		
 		
 		
		/*����createUser/modifyinfo����
		Scanner sc_a = new Scanner(System.in);   
		System.out.println("�������û�����");   
		String username = sc_a.nextLine();  //��ȡ�ַ���������
		
		Scanner sc_b = new Scanner(System.in);  
		System.out.println("���������룺");   
		String password = sc_b.nextLine();  //��ȡ�ַ���������
		 
		Scanner sc_c = new Scanner(System.in);  
		System.out.println("�������ǳƣ�");   
		String unickname = sc_c.nextLine();  //��ȡ�ַ���������

		Scanner sc_d = new Scanner(System.in);  
		System.out.println("���������գ�");   
		String a = sc_d.nextLine();  //��ȡ�ַ���������
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//Сд��mm��ʾ���Ƿ���  
		java.util.Date birthday;
		try {
			birthday = sdf.parse(a);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}  
		
		Scanner sc_e = new Scanner(System.in);  
		System.out.println("�������Ա�");   
		boolean sex = sc_e.nextLine() != null;  //��ȡ�ַ���������
		System.out.println(User.modifyInfo(username, password, unickname, birthday));
		*/
        

		
	}

}
