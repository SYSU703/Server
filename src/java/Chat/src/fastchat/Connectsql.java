package fastchat;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connectsql {

	/**
	 * @author wrf
	 * ���ݿ����Ӻ������������޸�url��username��password
	 */
	static public Connection getConn() {    
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");  // classLoader,���ض�Ӧ����      
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
		String url = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf-8&useSSL=false";  // ���ݿ���Ϣ�����Ϊdatabase����
		String username = "root";  // ���ݿ��˻���һ��Ϊroot
		String password = "";  // ���ݿ�����		   
		try{
		    conn = DriverManager.getConnection(url, username, password);  // ��������
	    } catch (SQLException e) {
		    e.printStackTrace();
		}
		return conn;
	}
	
	
}
