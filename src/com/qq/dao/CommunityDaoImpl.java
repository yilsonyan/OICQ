package com.qq.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.qq.model.Community;
import com.qq.model.Jkfile;
import com.qq.model.Jkuser;
import com.qq.util.JdbcUtil;

public class CommunityDaoImpl implements CommunityDao {

	/**
	 * 得到一个群的基本信息
	 */
	@Override
	public Community getBasicInfo(int cid) {
		Community community = new Community();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from community where cid = ?";
		//首先得到群的基本信息
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			rs = ps.executeQuery();
			if(rs.next()) {
				community.setName(rs.getString(2));
				community.setOwner(rs.getInt(3));
				community.setDes(rs.getString(4));
				String path = rs.getString(5);
				if(path!=null && !path.equals("")) {
					File file = new File(path);
					community.setIconpath(file);
				}
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//填完基本信息后 把群内的所有成员的信息添加进来
		List<Integer> uidList = new ArrayList<Integer>();
		List<Jkuser> userList = new ArrayList<Jkuser>();
		//首先通过映射表查id
		sql = "select jid from uc where cid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			rs = ps.executeQuery();
			while(rs.next()) {
				uidList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int k = 0; k < uidList.size(); k++) {
			int uid = uidList.get(k);
			sql = "select * from jkuser where jknum = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, uid);
				rs = ps.executeQuery();
				if(rs.next()) {
					Jkuser jkuser2 = new Jkuser();
					jkuser2.setJknum(rs.getInt(1));
					jkuser2.setName(rs.getString(2));
					jkuser2.setSignature(rs.getString(4));
					String path = rs.getString(5);
					if(path!=null && !path.equals("")) {
						File file = new File(path);
						jkuser2.setIconpath(file);
					}
					jkuser2.setSite(rs.getString(6));
					jkuser2.setPhone(rs.getString(7));
					jkuser2.setEmail(rs.getString(8));
					jkuser2.setState(rs.getInt(9));
					jkuser2.setSex(rs.getInt(11));
					userList.add(jkuser2);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		community.setUserList(userList);
		
		
		
		//得到群里的全部共享文件
		List<Integer> fileidList = new ArrayList<Integer>();
		List<Jkfile> fileList = new ArrayList<Jkfile>();
		sql = "select fid from cf where cid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			rs = ps.executeQuery();
			while(rs.next()) {
				fileidList.add(rs.getInt(1));
			}
			for (int i = 0; i < fileidList.size(); i++) {
				int fid = fileidList.get(i);
				sql = "select * from jkfile where fid = ?";
				ps = conn.prepareStatement(sql);
				ps.setObject(1, fid);
				rs = ps.executeQuery();
				if(rs.next()) {
					Jkfile jkfile = new Jkfile();
					jkfile.setFid(fid);
					jkfile.setFilename(rs.getString(3));
					jkfile.setUid(rs.getInt(4));
					String path = rs.getString(2);
					File file = new File(path);
					jkfile.setFile(file);
					fileList.add(jkfile);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		community.setFileList(fileList);
		return community;
	}

	/**
	 * 根据cid获得其拥有者的jknum
	 */
	@Override
	public int getOwnerByCid(int cid) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int jknum = 0;
		String sql = "select owner from community where cid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			rs = ps.executeQuery();
			if(rs.next()) {
				jknum = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jknum;
	}

	/**
	 * 向群组-用户映射之中插入一条记录
	 */
	@Override
	public int insertLog(int jknum, int cid) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		int res = 0;
		String sql = "insert into uc values(?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.setObject(2, cid);
			res = ps.executeUpdate();
		} catch (SQLException e) {
		}
		return res;
	}

	@Override
	public int addCommunity(String name, int owner, String des, String path) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select max(cid) from community";
		int currentCid = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				currentCid = rs.getInt(1) + 1;
			}else {
				currentCid = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "insert into community values(?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, currentCid);
			ps.setObject(2, name);
			ps.setObject(3, owner);
			ps.setObject(4, des);
			ps.setObject(5, path);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "insert into uc values(?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, owner);
			ps.setObject(2, currentCid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return currentCid;
		
	}

	/**
	 * 根据id删除一个群组
	 */
	@Override
	public int deleteCommunity(int cid) {
		int state = 0;
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "delete from community where cid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return state;
	}

	/**
	 * 得到一个群的所有在线用户jknum集合
	 */
	@Override
	public List<Integer> getAllOnLineUsers(int cid) {
		List<Integer> uidList = new ArrayList<Integer>();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select jid from uc where cid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, cid);
			rs = ps.executeQuery();
			while(rs.next()) {
				uidList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		List<Integer> list2 = new ArrayList<Integer>();
		for (int i = 0; i < uidList.size(); i++) {
			list2.add(uidList.get(i));
		};
		//把不在线的用户移除
		for (int i = 0; i < list2.size(); i++) {
			int uid = list2.get(i);
			sql = "select state from jkuser where jknum = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, uid);
				rs = ps.executeQuery();
				if(rs.next()) {
					int state = rs.getInt(1);
					if(state == 0) {
						uidList.remove((Integer)uid);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return uidList;
	}

}
