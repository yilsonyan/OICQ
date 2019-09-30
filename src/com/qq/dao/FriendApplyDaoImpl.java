package com.qq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qq.model.FriendApply;
import com.qq.util.JdbcUtil;

/**
 * 
 * @author yy
 *
 */
public class FriendApplyDaoImpl implements FriendApplyDao {

	@Override
	public int addLog(int srcNum, int destNum, int state) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into friendapply values(?,?,?)";
		int res = 0;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, srcNum);
			ps.setObject(2, destNum);
			ps.setObject(3, state);
			res = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
		
	}

	@Override
	public List<FriendApply> queryLog(int jknum) {
		List<FriendApply> apply = new ArrayList<FriendApply>();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from friendapply where state = 0 and destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while(rs.next()) {
				FriendApply apply2 = new FriendApply();
				apply2.setSrcid(rs.getInt(1));
				apply2.setDestid(rs.getInt(2));
				apply2.setState(rs.getInt(3));
				apply.add(apply2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return apply;
	}

	
	/**
	 * ¸Ä±ä×´Ì¬
	 */
	@Override
	public void changeState(int srcNum, int destNum) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "update friendapply set state = 1 where srcid = ? and destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, srcNum);
			ps.setObject(2, destNum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
