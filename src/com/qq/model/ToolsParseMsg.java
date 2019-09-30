package com.qq.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.swing.ImageIcon;

import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgAddCommunity;
import com.qq.msg.MsgAddCommunityResp;
import com.qq.msg.MsgAddFriendResp;
import com.qq.msg.MsgAddGroup;
import com.qq.msg.MsgAddGroupResp;
import com.qq.msg.MsgChangePwd;
import com.qq.msg.MsgChangePwdResp;
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
import com.qq.msg.MsgForgetResp;
import com.qq.msg.MsgHead;
import com.qq.msg.MsgHeaderUpload;
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.msg.MsgLogin;
import com.qq.msg.MsgLoginResp;
import com.qq.msg.MsgReg;
import com.qq.msg.MsgRegResp;
import com.qq.util.LogTools;

/**
 * QQ 消息解包工具类 根据定义的通信规则 将得到的数据库转化为消息对象
 * 
 * @author yy
 * 
 */
public class ToolsParseMsg {
	/**
	 * 将从流上得到的数据块 解析为消息对象
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static MsgHead parseMsg(byte[] data) throws Exception {
		int totalLength = data.length + 4;
		System.out.println(totalLength + "开始解析");
		// 将字节数据转换为内存流
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dins = new DataInputStream(bais);
		byte msgType = dins.readByte(); // 读取消息类型
		int dest = dins.readInt(); // 读取接受数据一方
		int src = dins.readInt(); // 读取发送数据的一方
		MsgHead msgHead = new MsgHead();// 将消息头数据赋值
		msgHead.setTotalLength(totalLength);
		msgHead.setType(msgType);
		msgHead.setDest(dest);
		msgHead.setSrc(src);
		if (msgType == IMsgConstance.command_reg) {
			int jknum = dins.readInt();
			String name = readString(dins, 16).trim();
			String password = readString(dins, 32).trim();
			String signature = readString(dins, 44).trim();
			String site = readString(dins, 40).trim();
			String phone = readString(dins, 11).trim();
			String email = readString(dins, 20).trim();
			int state = dins.readInt();
			String question = readString(dins, 30).trim();
			String answer = readString(dins, 32).trim();
			int sex = dins.readInt();
			MsgReg reg = new MsgReg();
			copyHead(msgHead, reg);
			Jkuser jkuser = new Jkuser(jknum, name, null, signature, null,
					site, phone, email, state, question, "", "");
			jkuser.setPassword(password);
			jkuser.setSex(sex);
			jkuser.setAnswer(answer);
			System.out.println("answer:" + answer);
			reg.setJkuser(jkuser);
			return reg;

		} else if (msgType == IMsgConstance.command_reg_resp) {
			byte state = dins.readByte();
			int jknum = dins.readInt();
			MsgRegResp msgRegResp = new MsgRegResp(state, jknum);
			copyHead(msgHead, msgRegResp);
			return msgRegResp;
		} else if (msgType == IMsgConstance.command_login) {
			String password = readString(dins, 32).trim();
			int state = dins.readInt();
			MsgLogin msgLogin = new MsgLogin();
			copyHead(msgHead, msgLogin);
			msgLogin.setPassword(password);
			msgLogin.setState(state);
			return msgLogin;
		} else if (msgType == IMsgConstance.command_login_resp) {
			MsgLoginResp loginResp = new MsgLoginResp();
			copyHead(msgHead, loginResp);
			loginResp.setState(dins.readByte());
			return loginResp;
		} else if (msgType == IMsgConstance.command_headerupload) {
			MsgHeaderUpload headerUpload = new MsgHeaderUpload();
			copyHead(msgHead, headerUpload);
			return headerUpload;
		} else if (msgType == IMsgConstance.command_headerupload_resp) {
			MsgHeaderUploadResp headerUploadResp = new MsgHeaderUploadResp();
			copyHead(msgHead, headerUploadResp);
			headerUploadResp.setState(dins.readByte());
			return headerUploadResp;
		} else if (msgType == IMsgConstance.command_find) {
			MsgFind find = new MsgFind();
			copyHead(msgHead, find);
			find.setClassify(dins.readByte());
			find.setFindId(dins.readInt());
			return find;
		} else if (msgType == IMsgConstance.command_find_resp) {
			MsgFindResp findResp = new MsgFindResp();
			copyHead(msgHead, findResp);
			findResp.setState(dins.readByte());
			return findResp;
		} else if (msgType == IMsgConstance.command_chatText) {
			MsgChatText chatText = new MsgChatText();
			copyHead(msgHead, chatText);
			chatText.setCharTxt(readString(dins, 60));
			chatText.setSendTime(readString(dins, 25));
			return chatText;
		} else if (msgType == IMsgConstance.command_onLine) {
			return msgHead;
		} else if (msgType == IMsgConstance.command_offLine) {
			return msgHead;
		} else if (msgType == IMsgConstance.command_commuChatTxt) {
			MsgCommuChatText chatText = new MsgCommuChatText();
			copyHead(msgHead, chatText);
			chatText.setchatTxt(readString(dins, 60).trim());
			chatText.setSendTime(readString(dins, 25).trim());
			chatText.setDestCid(dins.readInt());
			return chatText;
		} else if (msgType == IMsgConstance.command_addFriend) {
			return msgHead;
		} else if (msgType == IMsgConstance.command_addFriend_resp) {
			MsgAddFriendResp addFriendResp = new MsgAddFriendResp();
			copyHead(msgHead, addFriendResp);
			addFriendResp.setRes(dins.readByte());
			return addFriendResp;
		} else if (msgType == IMsgConstance.command_addCommunity) {
			MsgAddCommunity addCommunity = new MsgAddCommunity();
			copyHead(msgHead, addCommunity);
			addCommunity.setDestCid(dins.readInt());
			return addCommunity;
		} else if (msgType == IMsgConstance.command_addCommunity_resp) {
			MsgAddCommunityResp addCommunityResp = new MsgAddCommunityResp();
			copyHead(msgHead, addCommunityResp);
			addCommunityResp.setRes(dins.readInt());
			addCommunityResp.setDestcid(dins.readInt());
			return addCommunityResp;
		} else if (msgType == IMsgConstance.command_chatFile) {
			MsgChatFile chatFile = new MsgChatFile();
			copyHead(msgHead, chatFile);
			chatFile.setFileName(readString(dins, 256).trim());
			chatFile.setSendTime(readString(dins, 25).trim());
			byte[] data1 = new byte[chatFile.getTotalLength() - 294];
			dins.readFully(data1);
			chatFile.setFileData(data1);
			return chatFile;
		} else if (msgType == IMsgConstance.command_commuChatFile) {
			MsgCommuChatFile chatFile = new MsgCommuChatFile();
			copyHead(msgHead, chatFile);
			chatFile.setSendTime(readString(dins, 25).trim());
			chatFile.setDestCid(dins.readInt());
			chatFile.setFileName(readString(dins, 256).trim());
			byte[] data1 = new byte[chatFile.getTotalLength() - 298];
			dins.readFully(data1);
			chatFile.setFileData(data1);
			return chatFile;
		} else if(msgType == IMsgConstance.command_addGroup) {
			MsgAddGroup addGroup = new MsgAddGroup();
			copyHead(msgHead, addGroup);
			addGroup.setGroupName(readString(dins, 256).trim());
			return addGroup;
		} else if(msgType == IMsgConstance.command_addGroup_resp) {
			MsgAddGroupResp addGroupResp = new MsgAddGroupResp();
			copyHead(msgHead, addGroupResp);
			addGroupResp.setState(dins.readByte());
			return addGroupResp;
		} else if(msgType == IMsgConstance.command_deleteFriend) {
			return msgHead;
		} else if(msgType == IMsgConstance.command_deleteFriend_resp) {
			MsgDeleteFriendResp deleteFriendResp = new MsgDeleteFriendResp();
			copyHead(msgHead, deleteFriendResp);
			deleteFriendResp.setState(dins.readByte());
			deleteFriendResp.setGid(dins.readInt());
			return deleteFriendResp;
		} else if(msgType == IMsgConstance.command_deleteGroup) {
			MsgDeleteGroup deleteGroup = new MsgDeleteGroup();
			copyHead(msgHead, deleteGroup);
			deleteGroup.setGid(dins.readInt());
			return deleteGroup;
		} else if(msgType == IMsgConstance.command_deleteGroup_resp) {
			MsgDeleteGroupResp deleteGroupResp = new MsgDeleteGroupResp();
			copyHead(msgHead, deleteGroupResp);
			deleteGroupResp.setState(dins.readByte());
			deleteGroupResp.setGid(dins.readInt());
			return deleteGroupResp;
		} else if(msgType == IMsgConstance.command_createCommunity) {
			MsgCreateCommunity community = new MsgCreateCommunity();
			copyHead(msgHead, community);
			community.setcName(readString(dins, 100).trim());
			community.setcDes(readString(dins, 300).trim());
			community.setFileName(readString(dins, 100).trim());
			byte[] data1 = new byte[community.getTotalLength() - 513];
			dins.readFully(data1);
			community.setIcon(data1);
			return community;
		} else if(msgType == IMsgConstance.command_createCommunity_resp) {
			MsgCreateCommunityResp communityResp = new MsgCreateCommunityResp();
			copyHead(msgHead, communityResp);
			communityResp.setState(dins.readByte());
			communityResp.setCid(dins.readInt());
			return communityResp;
		} else if(msgType == IMsgConstance.command_deleteCommunity) {
			MsgDeleteCommunity community = new MsgDeleteCommunity();
			copyHead(msgHead, community);
			community.setCid(dins.readInt());
			return community;
		} else if(msgType == IMsgConstance.command_deleteCommunity_resp) {
			MsgDeleteCommunityResp communityResp = new MsgDeleteCommunityResp();
			copyHead(msgHead, communityResp);
			communityResp.setState(dins.readByte());
			communityResp.setCid(dins.readInt());
			return communityResp;
		} else if(msgType == IMsgConstance.command_commu_offLine) {
			return msgHead;
		} else if(msgType == IMsgConstance.command_commu_onLine) {
			return msgHead;
		} else if(msgType == IMsgConstance.command_forgetPwd) {
			return msgHead;
		} else if(msgType == IMsgConstance.command_forgetPwd_resp) {
			MsgForgetResp forgetResp = new MsgForgetResp();
			copyHead(msgHead, forgetResp);
			forgetResp.setQuestion(readString(dins, 300));
			forgetResp.setAnswer(readString(dins, 300));
			return forgetResp;
		} else if(msgType == IMsgConstance.command_changePwd) {
			MsgChangePwd changePwd = new MsgChangePwd();
			copyHead(msgHead, changePwd);
			changePwd.setNewPwd(readString(dins, 32));
			return changePwd;
		} else if(msgType == IMsgConstance.command_changePwd_resp) {
			MsgChangePwdResp changePwdResp = new MsgChangePwdResp();
			copyHead(msgHead, changePwdResp);
			changePwdResp.setState(dins.readByte());
			return changePwdResp;
		} else {
			String logMsg = "解包未知消息类型，无法解包:type:" + msgType;
			LogTools.ERROR(ToolsParseMsg.class, logMsg);// 记录日志
		}
		return null;
	}

	/**
	 * 复制消息头的数据
	 * 
	 * @param head
	 * @param dest
	 */
	private static void copyHead(MsgHead head, MsgHead dest) {
		dest.setTotalLength(head.getTotalLength());
		dest.setType(head.getType());
		dest.setDest(head.getDest());
		dest.setSrc(head.getSrc());
	}

	/*
	 * 从流中读取len长度个字节 转化为字符串后返回
	 * 
	 * @param dins
	 * 
	 * @param len
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	private static String readString(DataInputStream dins, int len)
			throws Exception {
		byte[] data = new byte[len];
		dins.readFully(data);
		return new String(data);// 使用系统默认字符集编码
	}
}
