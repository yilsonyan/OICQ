package com.qq.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qq.model.ChatLog;
import com.qq.model.CommuApply;
import com.qq.model.CommuApplyResp;
import com.qq.model.CommuChatLog;
import com.qq.model.Community;
import com.qq.model.FriendApply;
import com.qq.model.FriendApplyResp;
import com.qq.model.Jkfile;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.util.JdbcUtil;
import com.qq.util.MD5Util;

public class JkuserDaoImpl extends BaseJdbcDao<Jkuser> implements JkuserDao {

	@Override
	public int regUser(Jkuser jkuser) {
		// 首先得到目前最大的jknum号码
		String sql = "select max(jknum) from jkuser";
		Connection conn = JdbcUtil.getConnection();
		ResultSet rs = null;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int currentNum = -1;
		try {
			if (rs.next() && rs.getInt(1) > 0) {
				currentNum = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 分配QQ号码 这里就不筛选靓号了 直接+1分配
		if (currentNum == -1) {
			currentNum = 100000;
		} else {
			currentNum++;
		}

		jkuser.setJknum(currentNum);
		// 把数据保存到数据库
		save(jkuser);
		sql = "update jkuser set iconpath = ? where jknum = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setObject(1, "F:/QQimg/default_header.jpg");
			ps.setObject(2, currentNum);
			ps.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// 为每个qq号码创建一个默认的分组"我的好友" 并且和他绑定
		// 1.首先当前最大的gidnum
		sql = "select max(gid) from jkgroup";
		int curGid = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				curGid = rs.getInt(1);
			}
			curGid++;
			sql = "insert into jkgroup values (?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, curGid);
			ps.setString(2, "我的好友");
			ps.setInt(3, currentNum);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		

		return currentNum;
	}

	/**
	 * 检查用户是否存在 若是存在 返回该用户的所有信息
	 */
	@Override
	public Jkuser checkLogin(int jknum, String password, int state) {
		Jkuser jkuser = new Jkuser();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		// 首先得到用户的基本信息
		sql = "select * from jkuser where jknum = ? and password = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.setObject(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				jkuser.setJknum(rs.getInt(1));
				jkuser.setName(rs.getString(2));
				jkuser.setSignature(rs.getString(4));
				String path = rs.getString(5);
				if (path != null && !path.equals("")) {
					File file = new File(path);
					jkuser.setIconpath(file);
				}
				jkuser.setSite(rs.getString(6));
				jkuser.setPhone(rs.getString(7));
				jkuser.setEmail(rs.getString(8));
				jkuser.setState(rs.getInt(9));
				jkuser.setSex(rs.getInt(11));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jkuser.setState(state);

		// 更新用户的状态为隐身或者在线
		sql = "update jkuser set state = ? where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, state);
			ps.setObject(2, jknum);
			ps.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// 得到用户的所有分组信息
		List<Jkgroup> groupList = new ArrayList<Jkgroup>();
		sql = "select * from jkgroup where owner = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				Jkgroup jkgroup = new Jkgroup();
				jkgroup.setGid(rs.getInt(1));
				jkgroup.setName(rs.getString(2));
				jkgroup.setOwner(jknum);
				groupList.add(jkgroup);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 得到每个分组的好友信息
		for (int i = 0; i < groupList.size(); i++) {
			int gid = groupList.get(i).getGid();
			// 得到所有的好友信息
			List<Integer> friendidList = new ArrayList<Integer>();
			sql = "select jid from ug where gid = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, gid);
				rs = ps.executeQuery();
				while (rs.next()) {
					friendidList.add(rs.getInt(1));
				}
				List<Jkuser> userList = new ArrayList<Jkuser>();
				for (int j = 0; j < friendidList.size(); j++) {
					sql = "select * from jkuser where jknum = ?";
					ps = conn.prepareStatement(sql);
					ps.setObject(1, friendidList.get(j));
					rs = ps.executeQuery();

					if (rs.next()) {
						Jkuser jkuser2 = new Jkuser();
						jkuser2.setJknum(rs.getInt(1));
						jkuser2.setName(rs.getString(2));
						jkuser2.setSignature(rs.getString(4));
						String path = rs.getString(5);
						if (path != null && !path.equals("")) {
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
				}
				groupList.get(i).setUserList(userList);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		jkuser.setGroupList(groupList);

		// 得到用户所加入的所有的群的信息
		List<Community> communityList = new ArrayList<Community>();
		List<Integer> communityidList = new ArrayList<Integer>();
		sql = "select cid from uc where jid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				communityidList.add(rs.getInt(1));
			}
			for (int j = 0; j < communityidList.size(); j++) {
				Community community = new Community();
				int cid = communityidList.get(j);
				community.setCid(cid);
				sql = "select * from community where cid = ?";
				ps = conn.prepareStatement(sql);
				ps.setObject(1, cid);
				rs = ps.executeQuery();
				if (rs.next()) {
					community.setName(rs.getString(2));
					community.setOwner(rs.getInt(3));
					community.setDes(rs.getString(4));
					String path = rs.getString(5);
					if (path != null && !path.equals("")) {
						File file = new File(path);
						community.setIconpath(file);
					}
				}
				// 将群里所有用户添加进来
				List<Integer> uidList = new ArrayList<Integer>();
				List<Jkuser> userList = new ArrayList<Jkuser>();
				sql = "select jid from uc where cid = ?";
				ps = conn.prepareStatement(sql);
				ps.setObject(1, cid);
				rs = ps.executeQuery();
				while (rs.next()) {
					uidList.add(rs.getInt(1));
				}
				for (int k = 0; k < uidList.size(); k++) {
					int uid = uidList.get(k);
					sql = "select * from jkuser where jknum = ?";
					ps = conn.prepareStatement(sql);
					ps.setObject(1, uid);
					rs = ps.executeQuery();
					if (rs.next()) {
						Jkuser jkuser2 = new Jkuser();
						jkuser2.setJknum(rs.getInt(1));
						jkuser2.setName(rs.getString(2));
						jkuser2.setSignature(rs.getString(4));
						String path = rs.getString(5);
						if (path != null && !path.equals("")) {
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
				}
				community.setUserList(userList);
				// 得到群里的全部共享文件
				List<Integer> fileidList = new ArrayList<Integer>();
				List<Jkfile> fileList = new ArrayList<Jkfile>();
				sql = "select fid from cf where cid = ?";
				ps = conn.prepareStatement(sql);
				ps.setObject(1, cid);
				rs = ps.executeQuery();
				while (rs.next()) {
					fileidList.add(rs.getInt(1));
				}
				for (int i = 0; i < fileidList.size(); i++) {
					int fid = fileidList.get(i);
					sql = "select * from jkfile where fid = ?";
					ps = conn.prepareStatement(sql);
					ps.setObject(1, fid);
					rs = ps.executeQuery();
					if (rs.next()) {
						Jkfile jkfile = new Jkfile();
						jkfile.setFid(fid);
						jkfile.setFilename(rs.getString(3));
						jkfile.setUid(rs.getInt(4));
						String path = rs.getString(2);
						File file = new File(path);
						jkfile.setFile(file);
						jkfile.setSendTime(rs.getString(5));
						fileList.add(jkfile);
					}
				}
				community.setFileList(fileList);
				communityList.add(community);
				
				sql = "select * from ucf where state = 0 and cid = ? and jknum = ?";
				ps = conn.prepareStatement(sql);
				ps.setObject(1, cid);
				ps.setObject(2, jknum);
				rs = ps.executeQuery();
				if(rs.next()) {
					jkuser.getHasNewFile().put(cid, 1);
				}else {
					jkuser.getHasNewFile().put(cid, 0);
				}
				
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jkuser.setCommuList(communityList);
		
		
		//更新所有群文件为已处理
		sql = "update ucf set state = 1 where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		

		/**
		 * 获得所有的未处理消息 包括群聊消息和用户聊天消息
		 */
		List<Integer> idList = new ArrayList<Integer>(); // 未处理群消息id集合
		List<CommuChatLog> chatLogList = new ArrayList<CommuChatLog>(); // 未处理群消息集合
		sql = "select lid from ul where jid = ? and state = 0 order by lid";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				idList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 根据id集合去查群消息
		for (int i = 0; i < idList.size(); i++) {
			int id = idList.get(i);
			sql = "select * from commuchatlog where lid = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, id);
				rs = ps.executeQuery();
				CommuChatLog chatLog = new CommuChatLog();
				if (rs.next()) {
					chatLog.setCid(rs.getInt(1));
					chatLog.setSrcid(rs.getInt(2));
					chatLog.setContent(rs.getString(3));
					chatLog.setLid(rs.getInt(4));
					chatLog.setSendTime(rs.getString(5));
					chatLogList.add(chatLog);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		jkuser.setCmuChatLogList(chatLogList);

		List<ChatLog> logList = new ArrayList<ChatLog>(); // 消息记录集合
		// 查找用户所有的未处理消息
		sql = "select * from chatlog where state = 0 and destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				ChatLog chatLog = new ChatLog();
				chatLog.setSrcid(rs.getInt(1));
				chatLog.setDestid(rs.getInt(2));
				chatLog.setContent(rs.getString(3));
				chatLog.setState(rs.getInt(4));
				chatLog.setSendtime(rs.getString(5));
				logList.add(chatLog);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jkuser.setLogList(logList);
		// 更新所有消息为已处理
		sql = "update chatlog set state = 1 where destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "update ul set state = 1 where jid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 获得未处理的好友申请消息和未处理的好友申请消息回应
		List<FriendApply> applyList = new ArrayList<FriendApply>();
		List<FriendApplyResp> applyRespList = new ArrayList<FriendApplyResp>();

		// 好友申请
		sql = "select * from friendapply where destid = ? and state = 0";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				FriendApply apply = new FriendApply();
				apply.setSrcid(rs.getInt(1));
				apply.setDestid(rs.getInt(2));
				apply.setState(rs.getInt(3));
				applyList.add(apply);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 好友申请回应
		sql = "select * from friendapplyresp where destid = ? and state = 0";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				FriendApplyResp applyResp = new FriendApplyResp();
				applyResp.setSrcid(rs.getInt(1));
				applyResp.setDestid(rs.getInt(2));
				applyResp.setState(rs.getInt(3));
				applyResp.setRes(rs.getInt(4));
				applyRespList.add(applyResp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jkuser.setApplyList(applyList);
		jkuser.setApplyRespList(applyRespList);
		// 好友申请和回应全部变为已处理状态
		sql = "update friendapply set state = 1 where destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql = "update friendapplyresp set state = 1 where destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 群的申请
		sql = "select * from commuapply where state = 0 and destid = ?";
		ArrayList<CommuApply> cmuApplyList = new ArrayList<CommuApply>();
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				CommuApply apply = new CommuApply();
				apply.setSrcid(rs.getInt(1));
				apply.setCid(rs.getInt(2));
				apply.setDestid(rs.getInt(3));
				apply.setState(0);
				cmuApplyList.add(apply);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jkuser.setCmuApplyList(cmuApplyList);

		sql = "select * from commuapplyresp where state = 0 and destid = ?";
		ArrayList<CommuApplyResp> cmuApplyRespList = new ArrayList<CommuApplyResp>();
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while (rs.next()) {
				CommuApplyResp applyResp = new CommuApplyResp();
				applyResp.setCid(rs.getInt(1));
				applyResp.setSrcid(rs.getInt(2));
				applyResp.setDestid(rs.getInt(3));
				applyResp.setState(0);
				applyResp.setRes(rs.getInt(5));
				cmuApplyRespList.add(applyResp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jkuser.setCmuApplyRespList(cmuApplyRespList);

		// 更新消息状态为已处理
		sql = "update commuapply set state = 1 where destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 更新消息状态为已处理
		sql = "update commuapplyresp set state = 1 where destid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//所有申请入群的用户列表
		ArrayList<Jkuser> uList = new ArrayList<Jkuser>();
		for (int i = 0; i < cmuApplyList.size(); i++) {
			int srcid = cmuApplyList.get(i).getSrcid();
			sql  = "select * from jkuser where jknum = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, srcid);
				rs = ps.executeQuery();
				if(rs.next()) {
					Jkuser jkuser2 = new Jkuser();
					jkuser2.setJknum(rs.getInt(1));
					jkuser2.setName(rs.getString(2));
					jkuser2.setSignature(rs.getString(4));
					String path = rs.getString(5);
					if (path != null && !path.equals("")) {
						File file = new File(path);
						jkuser2.setIconpath(file);
					}
					jkuser2.setSite(rs.getString(6));
					jkuser2.setPhone(rs.getString(7));
					jkuser2.setEmail(rs.getString(8));
					jkuser2.setState(rs.getInt(9));
					jkuser2.setSex(rs.getInt(11));
					uList.add(jkuser2);
				}
			} catch (SQLException e) {	
				e.printStackTrace();
			}
		}
		
		jkuser.setuList(uList);
		
		//所有申请的群信息
		ArrayList<Community> cmuList = new ArrayList<Community>();
		for (int i = 0; i < cmuApplyRespList.size(); i++) {
			int cid = cmuApplyRespList.get(i).getCid();
			sql = "select * from community where cid = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, cid);
				rs = ps.executeQuery();
				if(rs.next()) {
					Community community = new Community();
					community.setCid(rs.getInt(1));
					community.setName(rs.getString(2));
					community.setOwner(rs.getInt(3));
					community.setDes(rs.getString(4));
					String path = rs.getString(5);
					if(path!=null && !path.equals("")) {
						File file = new File(path);
						community.setIconpath(file);
					}
					cmuList.add(community);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		jkuser.setCmuList(cmuList);
		//查询用户所有的未接收文件
		ArrayList<Jkfile> fileList = new ArrayList<Jkfile>();
		ArrayList<Integer> fidList = new ArrayList<Integer>();
		ArrayList<String> sendTimeList = new ArrayList<String>(); 
		sql = "select * from uf where state = 0 and desid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			while(rs.next()) {
				fidList.add(rs.getInt(2));
				sendTimeList.add(rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < fidList.size(); i++) {
			int fid = fidList.get(i);
			sql = "select * from jkfile where fid = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, fid);
				rs = ps.executeQuery();
				while(rs.next()) {
					Jkfile jkfile = new Jkfile();
					jkfile.setSendTime(sendTimeList.get(i));
					jkfile.setFid(rs.getInt(1));
					String path = rs.getString(2);
					if(path!=null && !path.equals("")) {
						File file = new File(path);
						jkfile.setFile(file);
					}
					jkfile.setFilename(rs.getString(3));
					jkfile.setUid(rs.getInt(4));
					fileList.add(jkfile);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		jkuser.setFileList(fileList);
		//设置所有未处理文件状态为已处理
		sql = "update uf set state = 1 where desid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	
		
		
		return jkuser;
	}

	/**
	 * 传入jknum 返回密保问题 密保答案 以及jknum 若是传入的jknum是不存在的 那么返回null
	 */
	@Override
	public Jkuser findPwd(int jknum) {
		Jkuser jkuser = new Jkuser();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from jkuser where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			if (rs.next()) {
				jkuser.setJknum(rs.getInt(1));
				jkuser.setQuestion(rs.getString(10));
				jkuser.setAnswer(rs.getString(12));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return jkuser;
	}

	/**
	 * 修改密码 成功返回1
	 */
	public int changePwd(int jknum, String newPwd) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		int res = 0;
		String sql = "update jkuser set password = ? where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, newPwd);
			ps.setObject(2, jknum);
			res = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 设置用户的状态为离线
	 */
	@Override
	public void offOnline(int jknum) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		String sql = "update jkuser set state = 0 where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传头像功能的实现
	 */
	@Override
	public int updateIcon(int jknum, String iconpath) {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		int state = 0;
		ResultSet rs = null;
		String sql = "update jkuser set iconpath = ? where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, iconpath);
			ps.setObject(2, jknum);
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * 更新用户的基本信息
	 */
	@Override
	public int updateUserInfo(Jkuser jkuser) {
		System.out.println(jkuser);
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		int state = 0;
		String sql = "update jkuser set name = ? , signature = ? , site = ? , phone = ? , email = ? , sex = ? where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jkuser.getName());
			ps.setObject(2, jkuser.getSignature());
			ps.setObject(3, jkuser.getSite());
			ps.setObject(4, jkuser.getPhone());
			ps.setObject(5, jkuser.getEmail());
			ps.setObject(6, jkuser.getSex());
			ps.setObject(7, jkuser.getJknum());
			state = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public Jkuser getBasicInfo(int jknum) {
		Jkuser jkuser2 = new Jkuser();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from jkuser where jknum = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jknum);
			rs = ps.executeQuery();
			if (rs.next()) {
				jkuser2.setJknum(rs.getInt(1));
				jkuser2.setName(rs.getString(2));
				jkuser2.setSignature(rs.getString(4));
				String path = rs.getString(5);
				if (path != null && !path.equals("")) {
					File file = new File(path);
					jkuser2.setIconpath(file);
				}
				jkuser2.setSite(rs.getString(6));
				jkuser2.setPhone(rs.getString(7));
				jkuser2.setEmail(rs.getString(8));
				jkuser2.setState(rs.getInt(9));
				jkuser2.setSex(rs.getInt(11));
				jkuser2.setQuestion(rs.getString(10));
				jkuser2.setAnswer(rs.getString(12));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jkuser2;
	}

	
	/**
	 * 得到一个用户存在的所有群的id集合
	 */
	@Override
	public List<Integer> getAllCids(int jid) {
		List<Integer> cidList = new ArrayList<Integer>();
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select cid from uc where jid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, jid);
			rs = ps.executeQuery();
			while(rs.next()) {
				cidList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cidList;
	}

}
