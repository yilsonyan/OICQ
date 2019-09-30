package com.qq.server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.qq.dao.BaseJdbcDao;
import com.qq.dao.CommuApplyDapImpl;
import com.qq.dao.CommuApplyRespDaoImpl;
import com.qq.dao.CommuChatLogDaoImpl;
import com.qq.dao.CommunityDaoImpl;
import com.qq.dao.FriendApplyDaoImpl;
import com.qq.dao.FriendApplyRespDaoImpl;
import com.qq.dao.GroupDaoImpl;
import com.qq.dao.JkfileDaoImpl;
import com.qq.dao.JkuserDaoImpl;
import com.qq.model.ChatLog;
import com.qq.model.CommuApply;
import com.qq.model.CommuApplyResp;
import com.qq.model.Community;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgAddCommunity;
import com.qq.msg.MsgAddCommunityResp;
import com.qq.msg.MsgAddFriendResp;
import com.qq.msg.MsgAddGroup;
import com.qq.msg.MsgAddGroupResp;
import com.qq.msg.MsgChatFile;
import com.qq.msg.MsgChatText;
import com.qq.msg.MsgCommuChatFile;
import com.qq.msg.MsgCommuChatText;
import com.qq.msg.MsgCreateCommunity;
import com.qq.msg.MsgCreateCommunityResp;
import com.qq.msg.MsgDeleteCommunity;
import com.qq.msg.MsgDeleteCommunityResp;
import com.qq.msg.MsgDeleteFriendResp;
import com.qq.msg.MsgDeleteGroup;
import com.qq.msg.MsgDeleteGroupResp;
import com.qq.msg.MsgFind;
import com.qq.msg.MsgFindResp;
import com.qq.msg.MsgHead;
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.util.ImageUtil;

/**
 * 服务器管管理客户处理线程,转发消息的类 此类只需要提供方法调用,所以皆为静态方法
 */
public class ChatTools {
	// 保存处理线程的队列对象
	private static Map<Jkuser, ServerThread> stList = new HashMap();

	private ChatTools() {
	}// 不需要创建引类对象,构造器则私有

	/**
	 * 当用户登陆成功后将对应的处理线程对象加入到队列中 并给其好友发送上线消息
	 * 
	 * @param ct
	 *            :处理线程对象
	 */
	public static void addClient(Jkuser user, ServerThread ct) {
		stList.put(user, ct);
		// 发送其上线的消息
		if(user.getState() == 1) {
			sendOnOffLineMsg(user, true);
		}
	}

	/**
	 * 用户退出系统 1.移除处理队列中的处理线程对象 2.对其好友发送下线消息
	 * 
	 * @param user
	 *            :退出用户对象
	 */
	public static void removeClient(Jkuser user) {
		stList.remove(user);
		//同时向好友发送离线通知
		if(user.getState() == 1) {
			sendOnOffLineMsg(user, false);
		}
	}

	/**
	 * 当这个用户的好友发送 上线/下线消息
	 * 
	 * @param user
	 *            :上线/下线的用户
	 */
	public static void sendOnOffLineMsg(Jkuser user, boolean onLine) {
		// 给这个用户的好友发送：我己上线的消息
		ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) user.getGroupList();
		for (int i = 0; i < gList.size(); i++) {
			Jkgroup jkgroup = gList.get(i);
			ArrayList<Jkuser> uList = (ArrayList<Jkuser>) jkgroup.getUserList();
			for (int j = 0; j < uList.size(); j++) {
				Jkuser jkuser = uList.get(j);
				if(stList.get(getUserByNum(jkuser.getJknum())) != null) {
					MsgHead head = new MsgHead();
					head.setTotalLength(13);
					head.setSrc(user.getJknum());
					head.setDest(jkuser.getJknum());
					if(onLine) {
						head.setType(IMsgConstance.command_onLine);
					} else {
						head.setType(IMsgConstance.command_offLine);
					}
					stList.get(getUserByNum(jkuser.getJknum())).sendMsg2Me(head);
				}
			}
			
		}
		
		JkuserDaoImpl daoImpl = new JkuserDaoImpl();
		List<Integer> cidList = daoImpl.getAllCids(user.getJknum());
		if(cidList == null || cidList.size() == 0) return;
		for (int i = 0; i < cidList.size(); i++) {
			int cid = cidList.get(i);
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			List<Integer> uList = communityDaoImpl.getAllOnLineUsers(cid);
			for (int j = 0; j < uList.size(); j++) {
				int uid = uList.get(j);
				if(uid == user.getJknum()) continue;
					MsgHead msgHead = new MsgHead();
					msgHead.setTotalLength(13);
					msgHead.setDest(cid);
					msgHead.setSrc(user.getJknum());
					if(onLine) {
						msgHead.setType(IMsgConstance.command_commu_onLine);
					}else {
						msgHead.setType(IMsgConstance.command_commu_offLine);
					}
					stList.get(getUserByNum(uid)).sendMsg2Me(msgHead);
				}
			}
		}
		
		

	/**
	 * 根基jknum在stList集合中得到user对象
	 * 
	 * @param jknum
	 * @return
	 */
	private static Jkuser getUserByNum(int jknum) {
		Jkuser jkuser = null;
		Set<Jkuser> set = stList.keySet();
		Iterator<Jkuser> iterator = set.iterator();
		while (iterator.hasNext()) {
			Jkuser jkuser2 = iterator.next();
			if (jkuser2.getJknum() == jknum) {
				jkuser = jkuser2;
				break;
			}
		}

		return jkuser;
	}

	/**
	 * 给队列中的某一个用户发送消息
	 * 
	 * @param srcUser
	 *            ：发送者
	 * @param msg
	 *            :消息内容
	 * @throws SQLException
	 */
	public static synchronized void sendMsg2One(Jkuser srcUser, MsgHead msg)
			throws SQLException {

		// 头像上传请求
		if (msg.getType() == IMsgConstance.command_headerupload) {
			try {
				ObjectInputStream oins = new ObjectInputStream(stList.get(
						srcUser).getDins());
				File file = (File) oins.readObject();

				// 首先把文件保存到本地
				// 文件缩放至60*60大小
				String path = "F:/QQimg/u" + srcUser.getJknum() + System.currentTimeMillis() +  ".jpg";
				BufferedImage bi = ImageUtil.compressImage(file, 60, 60);
				ImageIO.write(bi, "jpg", new FileOutputStream(path));
				// 修改数据库记录
				JkuserDaoImpl userImpl = new JkuserDaoImpl();
				int state = userImpl.updateIcon(srcUser.getJknum(), path);

				// 发送消息给客户端
				MsgHeaderUploadResp headerUploadResp = new MsgHeaderUploadResp();
				headerUploadResp.setTotalLength(14);
				headerUploadResp.setSrc(IMsgConstance.Server_JK_NUMBER);
				headerUploadResp.setDest(srcUser.getJknum());
				headerUploadResp
						.setType(IMsgConstance.command_headerupload_resp);
				headerUploadResp.setState((byte) state);

				stList.get(srcUser).sendMsg2Me(headerUploadResp);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} else if (msg.getType() == IMsgConstance.command_find) {
			try {
				// 查找请求
				MsgFind find = (MsgFind) msg;
				int findId = find.getFindId();
				byte classify = find.getClassify();
				// 到数据库中查询数据
				Object object = BaseJdbcDao.findById(classify, findId);
				MsgFindResp findResp = new MsgFindResp();
				findResp.setTotalLength(14);
				findResp.setSrc(IMsgConstance.Server_JK_NUMBER);
				findResp.setDest(srcUser.getJknum());
				findResp.setType(IMsgConstance.command_find_resp);
				if (object == null) {
					findResp.setState((byte) 0);
					stList.get(srcUser).sendMsg2Me(findResp);
				} else {
					findResp.setState((byte) 1);
					stList.get(srcUser).sendMsg2Me(findResp);
					// 直接把对象信息序列化到客户端
					ObjectOutputStream oos = new ObjectOutputStream(stList.get(
							srcUser).getDous());
					oos.writeObject(object);
					oos.flush();
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg.getType() == IMsgConstance.command_chatText) {
			MsgChatText chatText = (MsgChatText) msg;
			// 数据库中添加记录
			ChatLog chatLog = new ChatLog();
			chatLog.setContent(chatText.getCharTxt());
			chatLog.setSrcid(chatText.getSrc());
			chatLog.setDestid(chatText.getDest());
			chatLog.setSendtime(chatText.getSendTime());
			// 判断消息接受者是否在线 如果在线 状态设置为1表示已接收 否则0表示未接收
			boolean flag = false;
			Set<Jkuser> uSet = stList.keySet();
			Iterator<Jkuser> iterator = uSet.iterator();
			Jkuser dest = null;
			while (iterator.hasNext()) {
				Jkuser user = iterator.next();
				if (user.getJknum() == chatText.getDest()) {
					flag = true;
					dest = user;
					break;
				}
			}

			if (flag) {
				chatLog.setState(1);
			} else {
				chatLog.setState(0);
			}
			ChatLogDaoImpl chatLogDaoImpl = new ChatLogDaoImpl();
			int state = chatLogDaoImpl.save(chatLog);
			// 如果对方在线 就把消息直接发送到对方那里
			if (flag) {
				stList.get(dest).sendMsg2Me(chatText);
			}
			return;
		} else if(msg.getType() == IMsgConstance.command_addFriend) {
			int destNum = msg.getDest();
			//若目标用户在线   直接把消息转发给目标用户 否则把数据存储到数据库
			if(stList.get(getUserByNum(destNum)) == null) {
				FriendApplyDaoImpl applyDaoImpl = new FriendApplyDaoImpl();
				applyDaoImpl.addLog(msg.getSrc(), msg.getDest(), 0);
			}else {
				stList.get(getUserByNum(destNum)).sendMsg2Me(msg);	//直接把添加好友请求发送到对应的客户端
			}
		}else if(msg.getType() == IMsgConstance.command_addFriend_resp) {
			MsgAddFriendResp addFriendResp = (MsgAddFriendResp) msg;
			int destNum = msg.getDest();
			//若目标用户在线 那么直接转发给目标用户  否则存到数据库
			if(stList.get(getUserByNum(destNum)) == null) {
				FriendApplyRespDaoImpl applyRespDaoImpl = new FriendApplyRespDaoImpl();
				applyRespDaoImpl.add(msg.getSrc(), destNum, 0, addFriendResp.getRes());
			}else {
				stList.get(getUserByNum(destNum)).sendMsg2Me(msg);	//直接把添加好友请求发送到对应的客户端
			}
		}else if(msg.getType() == IMsgConstance.command_commuChatTxt) {
			MsgCommuChatText chatText = (MsgCommuChatText) msg;
			int cid = chatText.getDestCid();
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			Community community = communityDaoImpl.getBasicInfo(cid);
			ArrayList<Jkuser> uList = (ArrayList<Jkuser>) community.getUserList();
			//首先 把这条聊天记录插入到数据库中
			CommuChatLogDaoImpl chatLogDaoImpl = new CommuChatLogDaoImpl();
			int lid = chatLogDaoImpl.insertLog(cid, chatText.getSrc(), chatText.getchatTxt(), chatText.getSendTime());
			for (int i = 0; i < uList.size(); i++) {
				int jknum = uList.get(i).getJknum();
				if(jknum == chatText.getSrc()) continue;
				if(stList.get(getUserByNum(jknum)) == null) {
					//若是用户不在线  那么添加mapping
					chatLogDaoImpl.addMapping(lid, jknum);
				}else {
					stList.get(getUserByNum(jknum)).sendMsg2Me(chatText);
				}
			}
		}else if(msg.getType() == IMsgConstance.command_addCommunity) {
			MsgAddCommunity addCommunity = (MsgAddCommunity) msg;
			int cid = addCommunity.getDestCid();
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			int owner = communityDaoImpl.getOwnerByCid(cid);
			addCommunity.setDest(owner);
			if (stList.get(getUserByNum(owner)) == null) {
				CommuApply apply = new CommuApply();
				apply.setCid(addCommunity.getDestCid());
				apply.setDestid(owner);
				apply.setSrcid(addCommunity.getSrc());
				apply.setState(0);
				CommuApplyDapImpl applyDapImpl = new CommuApplyDapImpl();
				applyDapImpl.save(apply);
			}else {
				stList.get(getUserByNum(owner)).sendMsg2Me(addCommunity);
				//顺便把申请入群的用户序列化到客户端
				OutputStream os = stList.get(getUserByNum(owner)).getDous();
				try {
					ObjectOutputStream oos = new ObjectOutputStream(os);
					JkuserDaoImpl daoImpl = new JkuserDaoImpl();
					Jkuser jkuser = daoImpl.getBasicInfo(addCommunity.getSrc());
					oos.writeObject(jkuser);
					oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(msg.getType() == IMsgConstance.command_addCommunity_resp) {
			MsgAddCommunityResp addCommunityResp = (MsgAddCommunityResp) msg;
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			if(addCommunityResp.getRes() == 1) {
				/**
				 * 添加群组-好友映射
				 */
				communityDaoImpl.insertLog(addCommunityResp.getDest(), addCommunityResp.getDestcid());
			}
			if(stList.get(getUserByNum(addCommunityResp.getDest())) == null) {
				CommuApplyResp applyResp = new CommuApplyResp();
				applyResp.setCid(addCommunityResp.getDestcid());
				applyResp.setSrcid(addCommunityResp.getSrc());
				applyResp.setDestid(addCommunityResp.getDest());
				applyResp.setState(0);
				applyResp.setRes(addCommunityResp.getRes());
				CommuApplyRespDaoImpl applyRespDaoImpl = new CommuApplyRespDaoImpl();
				applyRespDaoImpl.save(applyResp);
			}else {
				stList.get(getUserByNum(addCommunityResp.getDest())).sendMsg2Me(addCommunityResp);
				if(addCommunityResp.getRes() == 1) {
					//群的基本信息序列化到本地
					try {
						ObjectOutputStream oos = new ObjectOutputStream(stList.get(getUserByNum(addCommunityResp.getDest())).getDous());
						oos.writeObject(communityDaoImpl.getBasicInfo(addCommunityResp.getDestcid()));
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else if(msg.getType() == IMsgConstance.command_chatFile) {
			MsgChatFile chatFile = (MsgChatFile)msg;
			int destJknum =  chatFile.getDest();
			int srcNum = chatFile.getSrc();
			String fileName = chatFile.getFileName();
			String sendTime = chatFile.getSendTime();
			//在线的话直接转发  否则传到数据库
			if(stList.get(getUserByNum(destJknum)) == null) {
				String path = "F:/QQServer/"+fileName;
				try {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
					bos.write(chatFile.getFileData());
					bos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				JkfileDaoImpl fileDaoImpl = new JkfileDaoImpl();
				//添加文件
				int fid = fileDaoImpl.addFile(path, fileName, srcNum, sendTime);
				//添加映射
				fileDaoImpl.addUFMapping(destJknum, fid, sendTime,0);
			}else {
				stList.get(getUserByNum(destJknum)).sendMsg2Me(chatFile);
			}
		}else if(msg.getType() == IMsgConstance.command_commuChatFile) {
			MsgCommuChatFile chatFile = (MsgCommuChatFile) msg;
			int cid = chatFile.getDestCid();
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			Community community = communityDaoImpl.getBasicInfo(cid);
			List<Jkuser> uList = community.getUserList();
			//添加群组-文件映射
			String path = "F:/QQServer/"+chatFile.getSrc()+chatFile.getFileName();
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
				bos.write(chatFile.getFileData());
				bos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			JkfileDaoImpl daoImpl = new JkfileDaoImpl();
			int fid = daoImpl.addFile(path, chatFile.getFileName(), chatFile.getSrc(), chatFile.getSendTime());
			
			//文件添加完成后 添加映射
			daoImpl.addCfMapping(chatFile.getDestCid(), fid);
			
			for (int i = 0; i < uList.size(); i++) {
				if(uList.get(i).getJknum() == chatFile.getSrc()) continue;
				//在线直接转发 不在线把记录存到数据库中
				if(stList.get(getUserByNum(uList.get(i).getJknum())) == null) {
					
					daoImpl.addUcfMapping(uList.get(i).getJknum(), cid, fid);
					
				}else {
					stList.get(getUserByNum(uList.get(i).getJknum())).sendMsg2Me(chatFile);
				}
				
			}
			
			
			
			
		}else if(msg.getType() == IMsgConstance.command_addGroup) {
			MsgAddGroup addGroup = (MsgAddGroup) msg;
			GroupDaoImpl daoImpl = new GroupDaoImpl();
			int uid = addGroup.getSrc();
			String groupName = addGroup.getGroupName();
			int gid = daoImpl.addGroup(groupName, uid);
			
			//向客户端发送添加分组回应消息
			MsgAddGroupResp addGroupResp = new MsgAddGroupResp();
			addGroupResp.setTotalLength(14);
			addGroupResp.setType(IMsgConstance.command_addGroup_resp);
			addGroupResp.setSrc(gid);
			addGroupResp.setDest(addGroup.getSrc());
			
			addGroupResp.setState((byte)1);
			
			stList.get(getUserByNum(addGroup.getSrc())).sendMsg2Me(addGroupResp);
			
		}else if(msg.getType() == IMsgConstance.command_deleteFriend) {
			int srcNum = msg.getSrc();
			int destNum = msg.getDest();
			GroupDaoImpl daoImpl = new GroupDaoImpl();
			int gid1 = daoImpl.getGidByJknum(srcNum, destNum);
			int gid2 = daoImpl.getGidByJknum(destNum, srcNum);
			int state1 = daoImpl.deleteFriends(srcNum, gid1);
			int state2 = daoImpl.deleteFriends(destNum, gid2);
			
			//向客户端发送删除好友回应消息
			MsgDeleteFriendResp deleteFriendResp = new MsgDeleteFriendResp();
			deleteFriendResp.setTotalLength(18);
			deleteFriendResp.setType(IMsgConstance.command_deleteFriend_resp);
			deleteFriendResp.setSrc(msg.getSrc());
			deleteFriendResp.setDest(msg.getDest());
			if(state1 == 1 && state2 == 1) {
				deleteFriendResp.setState((byte)1);
			}else {
				deleteFriendResp.setState((byte)0);
			}
			deleteFriendResp.setGid(gid2);
			stList.get(getUserByNum(srcNum)).sendMsg2Me(deleteFriendResp);
			
			//判断对方在不在线  在线的话同样要发送删除好友消息回应
			if(stList.get(getUserByNum(destNum)) != null) {
				if(state1 == 1 && state2 == 1) {
					deleteFriendResp.setSrc(destNum);
					deleteFriendResp.setDest(srcNum);
					deleteFriendResp.setGid(gid1);
					stList.get(getUserByNum(destNum)).sendMsg2Me(deleteFriendResp);
					
				}
			}
			
			
		}else if(msg.getType() == IMsgConstance.command_deleteGroup) {
			MsgDeleteGroup deleteGroup = (MsgDeleteGroup) msg;
			int gid = deleteGroup.getGid();
			int srcNum = deleteGroup.getSrc();
			
			
			GroupDaoImpl daoImpl = new GroupDaoImpl();
			int state = daoImpl.deleteGroup(gid);
			
			//向客户端发送分组删除回应消息
			MsgDeleteGroupResp deleteGroupResp = new MsgDeleteGroupResp();
			deleteGroupResp.setTotalLength(18);
			deleteGroupResp.setType(IMsgConstance.command_deleteGroup_resp);
			deleteGroupResp.setSrc(IMsgConstance.Server_JK_NUMBER);
			deleteGroupResp.setDest(srcNum);
			deleteGroupResp.setGid(gid);
			if(state == 1) {
				deleteGroupResp.setState((byte)1);
			}else {
				deleteGroupResp.setState((byte)0);
			}
			
			stList.get(getUserByNum(srcNum)).sendMsg2Me(deleteGroupResp);
			
		}else if(msg.getType() == IMsgConstance.command_createCommunity) {
			MsgCreateCommunity community = (MsgCreateCommunity) msg;
			int owner = community.getSrc();
			String name = community.getcName();
			String des = community.getcDes();
			byte[] data = community.getIcon();
			
			//首先把头像保存到本地
			String path = "F:/QQimg/"+owner+community.getFileName();
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
				bos.write(data);
				bos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//数据库中添加群记录
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			int cid = communityDaoImpl.addCommunity(name, owner, des, path);
			
			//向客户端发送回应消息
			MsgCreateCommunityResp communityResp = new MsgCreateCommunityResp();
			communityResp.setTotalLength(18);
			communityResp.setType(IMsgConstance.command_createCommunity_resp);
			communityResp.setSrc(IMsgConstance.Server_JK_NUMBER);
			communityResp.setDest(owner);
			communityResp.setCid(cid);
			if(cid > 0) {				
				communityResp.setState((byte)1);
			}else {
				communityResp.setState((byte)0);
			}
			
			stList.get(getUserByNum(owner)).sendMsg2Me(communityResp);
			
		}else if(msg.getType() == IMsgConstance.command_deleteCommunity) {
			MsgDeleteCommunity community = (MsgDeleteCommunity) msg;
			int cid = community.getCid();
			CommunityDaoImpl communityDaoImpl = new CommunityDaoImpl();
			int state = communityDaoImpl.deleteCommunity(cid);
			
			MsgDeleteCommunityResp communityResp = new MsgDeleteCommunityResp();
			communityResp.setTotalLength(18);
			communityResp.setType(IMsgConstance.command_deleteCommunity_resp);
			communityResp.setSrc(IMsgConstance.Server_JK_NUMBER);
			communityResp.setDest(community.getSrc());
			communityResp.setCid(cid);
			if(state == 1) {
				communityResp.setState((byte) 1);
			}else {
				communityResp.setState((byte) 0);
			}
			
			System.out.println(communityResp.getState()+"------------");
			
			stList.get(getUserByNum(community.getSrc())).sendMsg2Me(communityResp);
			
		}
	}
}
