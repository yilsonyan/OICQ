package com.qq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qq.util.JdbcUtil;

public class CommuChatLogDaoImpl implements CommuChatLogDao {

	/**
	 * 插入一条记录
	 */
	@Override
	public int insertLog(int cid, int srcNum, String chatTxt, String sendTime) {
		int state = 0;
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int currentNum = 0;
		String sql = "select max(lid) from commuchatlog";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				currentNum = rs.getInt(1) + 1;
			}else {
				currentNum++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "insert into commuchatlog values(?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			ps.setObject(2, srcNum);
			ps.setObject(3, chatTxt);
			ps.setObject(4, currentNum);
			ps.setObject(5, sendTime);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return currentNum;
	}

	
	/**
	 * 添加群聊天记录和用户映射
	 */
	@Override
	public int addMapping(int lid, int jid) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into ul values(?,?,?)";
		int state = 0;
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, lid);
			ps.setObject(2, jid);
			ps.setObject(3, 0);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return state;
	}

}
