package com.qq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qq.model.FriendApply;
import com.qq.model.FriendApplyResp;
import com.qq.util.JdbcUtil;

/**
 * 好友申请回复记录 实现
 * @author yy
 *
 */
public class FriendApplyRespDaoImpl implements FriendApplyRespDao {

	@Override
	public int add(int srcNum, int destNum, int state,int res) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into friendapplyresp values(?,?,?,?)";
		int r = 0;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, srcNum);
			ps.setObject(2, destNum);
			ps.setObject(3, state);
			ps.setObject(4, res);
			r = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return r;
	}

	@Override
	public List<FriendApplyResp> queryLog(int jknum) {
		List<FriendApplyResp> apply = new ArrayList<FriendApplyResp>();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from friendapplyresp where state = 0 and destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while(rs.next()) {
				FriendApplyResp apply2 = new FriendApplyResp();
				apply2.setSrcid(rs.getInt(1));
				apply2.setDestid(rs.getInt(2));
				apply2.setState(rs.getInt(3));
				apply2.setRes(rs.getInt(4));
				apply.add(apply2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return apply;
		
	}

	@Override
	public void changeState(int srcNum, int destNum) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "update friendapplyresp set state = 1 where srcid = ? and destid = ?";
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
