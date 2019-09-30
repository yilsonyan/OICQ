package com.qq.util;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.Server;

/**
 * H2服务器
 *
 */
public class H2Server {

	private static Server server;

	public void start() {
		try {
			server = Server.createTcpServer(
					new String[] { "-tcp", "-tcpAllowOthers", "-tcpPort",
							"8082" }).start();
			
			//初始化H2 读取sql文件  创建QQ数据表
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement ps = null;
			for (int i = 1; i <= 55; i++) {
				String fileName = "./sql/"+String.valueOf(i)+".sql";
				try {
					BufferedReader br = new BufferedReader(new FileReader(fileName));
					StringBuffer buffer = new StringBuffer();
					String line = "";
					while((line = br.readLine()) != null) {
						buffer.append(line);
					}
					ps = conn.prepareStatement(buffer.toString());
					ps.execute();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			System.out.println("启动h2出错：" + e.toString());

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		if (server != null) {
			System.out.println("正在关闭h2...");
			server.stop();
			System.out.println("关闭成功.");
		}
	}

}