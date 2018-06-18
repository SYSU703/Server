package fastchat;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connectsql {

	/**
	 * @author wrf
	 * 数据库连接函数，请自行修改url，username和password
	 */
	static public Connection getConn() {    
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");  // classLoader,加载对应驱动      
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
		String url = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf-8&useSSL=false";  // 数据库信息，最后为database名称
		String username = "root";  // 数据库账户，一般为root
		String password = "";  // 数据库密码		   
		try{
		    conn = DriverManager.getConnection(url, username, password);  // 建立连接
	    } catch (SQLException e) {
		    e.printStackTrace();
		}
		return conn;
	}
	
	
}
