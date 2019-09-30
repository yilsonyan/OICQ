package com.qq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qq.model.Jkfile;
import com.qq.util.JdbcUtil;

public class JkfileDaoImpl extends BaseJdbcDao<Jkfile> implements JkfileDao {

	@Override
	public int addFile(String path, String name, int jid, String sendTime) {
		int currentFid = 0;
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select max(fid) from jkfile";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				currentFid = rs.getInt(1) + 1;
			}else {
				currentFid = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//insert 数据
		sql = "insert into jkfile values(?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			System.out.println(path+"---="+name);
			ps.setObject(1, currentFid);
			ps.setObject(2, path);
			ps.setObject(3, name);
			ps.setObject(4, jid);
			ps.setObject(5, sendTime);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return currentFid;
	}

	@Override
	public int addUFMapping(int destid, int fid, String sendTime, int curState) {
		int state = 0;
		Connection conn =  JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into uf values(?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, destid);
			ps.setObject(2, fid);
			ps.setObject(3, sendTime);
			ps.setObject(4, curState);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}

	
	/**
	 * 添加群组文件映射
	 */
	@Override
	public int addCfMapping(int cid, int fid) {
		int state = 0;
		Connection conn = JdbcUtil.getConnection();
		String sql = "insert into cf values(?,?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			ps.setObject(2, fid);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return state;
	}

	
	/**
	 * 添加用户群组文件映射
	 */
	@Override
	public int addUcfMapping(int jknum, int cid, int fid) {
		int state = -0;
		Connection conn = JdbcUtil.getConnection();
		String sql = "insert into ucf values(?,?,?,?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.setObject(2, cid);
			ps.setObject(3, fid);
			ps.setObject(4, 0);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return state;
	}
	
}
