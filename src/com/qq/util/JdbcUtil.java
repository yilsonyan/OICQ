package com.qq.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * db工具类
 * @author yy
 *
 */
public class JdbcUtil {

	private static String username = "root";
	private static String password = "";
	private static String url = "jdbc:h2:E:/h2/qq;AUTO_SERVER=TRUE";
	private static String driver = "org.h2.Driver";

	/**
	 * 得到和数据库的一个连接
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
	public static void closeAll(Connection conn,Statement st){
		closeAll(conn, st, null);
	}
	
	public static void closeAll(Connection conn, Statement st, ResultSet rs) {
		try {
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	

}
