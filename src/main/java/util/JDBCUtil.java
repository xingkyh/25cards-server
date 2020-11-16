package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 1.简单的JDBC工具，将数据库的配置及常用操作getConnection与closeConnection封装起来
 * 为使用者访问数据库提供一个统一的入口
 * 2.main函数中是使用该工具的范例
 * 3.该类主要用于教学，实际使用中应该使用数据库连接池提高性能
 * 4.该工具需要JDBC4.0以上的包
 * 5.可改进:driverName,url,userName,password这些可以放入配置文件，然后动态读取.
 * 6.该类仅用于访问mysql数据库，main函数的范例中使用到的表名为user,有两列int id,string name
 * @author zhrb
 *
 */
public class JDBCUtil {
	private static String driverName = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/25Cards?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
	private static String userName = "root";
	private static String password = "plumk";

	public static void registerDriver() {
		try {
			// jdbc4.0以前需要这句进行驱动注册
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到驱动");
		}
	}

	public static Connection getConnection() throws SQLException {
		Connection conn = null;
		System.out.println("正在连接数据库...");
		registerDriver();
		conn = DriverManager.getConnection(url, userName, password);
		System.out.println("数据库已连接!");
		return conn;

	}

	public static void closeConnection(Connection conn) {
		System.out.println("正在释放所有资源...");
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放所有资源
	 */
	public static void realeaseAll(ResultSet rs,Statement stmt,Connection conn,PreparedStatement pStatement){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pStatement!=null) {
			try {
				pStatement.close();
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		closeConnection(conn);
	}
	//使用范例
	/*public static void main(String[] args) {
		//1.传统用法:一般都这样使用
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from study";//表中有id和name这列
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String id = rs.getString("id");
				String name = rs.getString("name");
				System.out.format("该记录的id=%s,姓名=%s\n",id,name);
			}
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JDBCUtil.realeaseAll(rs, stmt, conn,null);
		}
	}*/
}
