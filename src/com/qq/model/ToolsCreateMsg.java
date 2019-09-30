package com.qq.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.msg.MsgLogin;
import com.qq.msg.MsgLoginResp;
import com.qq.msg.MsgReg;
import com.qq.msg.MsgRegResp;
import com.qq.util.LogTools;
import com.qq.util.MD5Util;

/**
 * QQ 打包消息工具类 根据定义的通信规则 将需要传送的消息对象转化为数据块
 * 
 * @author yy
 * 
 */
public class ToolsCreateMsg {

	/**
	 * 将消息对象转化为字节数据
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static byte[] packMsg(MsgHead msg) throws IOException {
		// 创建内存输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(baos);
		writeHead(dous, msg);
		int msgType = msg.getType();
		if (msgType == IMsgConstance.command_reg) {
			MsgReg msgReg = (MsgReg) msg;
			dous.writeInt(msgReg.getJkuser().getJknum());
			writeString(dous, 16, msgReg.getJkuser().getName());
			writeString(dous, 32, msgReg.getJkuser().getPassword());
			writeString(dous, 44, msgReg.getJkuser().getSignature());
			writeString(dous, 40, msgReg.getJkuser().getSite());
			writeString(dous, 11, msgReg.getJkuser().getPhone());
			writeString(dous, 20, msgReg.getJkuser().getEmail());
			dous.writeInt(msgReg.getJkuser().getState());
			writeString(dous, 30, msgReg.getJkuser().getQuestion());
			writeString(dous, 32, msgReg.getJkuser().getAnswer());
			dous.writeInt(msgReg.getJkuser().getSex());
		} else if (msgType == IMsgConstance.command_reg_resp) {
			MsgRegResp msgRegResp = (MsgRegResp) msg;
			dous.writeByte(msgRegResp.getState());
			dous.writeInt(msgRegResp.getJknum());
		} else if (msgType == IMsgConstance.command_login) {
			MsgLogin msgLogin = (MsgLogin) msg;
			writeString(dous, 32, msgLogin.getPassword());
			dous.writeInt(msgLogin.getState());
		} else if (msgType == IMsgConstance.command_login_resp) {
			MsgLoginResp loginResp = (MsgLoginResp) msg;
			dous.writeByte(loginResp.getState());
		} else if (msgType == IMsgConstance.command_headerupload) {

		} else if (msgType == IMsgConstance.command_headerupload_resp) {
			MsgHeaderUploadResp headerUploadResp = (MsgHeaderUploadResp) msg;
			dous.writeByte(headerUploadResp.getState());
		} else if (msgType == IMsgConstance.command_find) {
			MsgFind find = (MsgFind) msg;
			dous.writeByte(find.getClassify());
			dous.writeInt(find.getFindId());
		} else if (msgType == IMsgConstance.command_find_resp) {
			MsgFindResp findResp = (MsgFindResp) msg;
			dous.writeByte(findResp.getState());
		} else if (msgType == IMsgConstance.command_chatText) {
			MsgChatText chatText = (MsgChatText) msg;
			writeString(dous, 60, chatText.getCharTxt());
			writeString(dous, 25, chatText.getSendTime());
		} else if (msgType == IMsgConstance.command_onLine) {

		} else if (msgType == IMsgConstance.command_offLine) {

		} else if (msgType == IMsgConstance.command_commuChatTxt) {
			MsgCommuChatText chatText = (MsgCommuChatText) msg;
			writeString(dous, 60, chatText.getchatTxt());
			writeString(dous, 25, chatText.getSendTime());
			dous.writeInt(chatText.getDestCid());
		} else if (msgType == IMsgConstance.command_addFriend) {

		} else if (msgType == IMsgConstance.command_addFriend_resp) {
			MsgAddFriendResp addFriendResp = (MsgAddFriendResp) msg;
			dous.writeByte(addFriendResp.getRes());
		} else if (msgType == IMsgConstance.command_addCommunity) {
			MsgAddCommunity addCommunity = (MsgAddCommunity) msg;
			dous.writeInt(addCommunity.getDestCid());
		} else if (msgType == IMsgConstance.command_addCommunity_resp) {
			MsgAddCommunityResp addCommunityResp = (MsgAddCommunityResp) msg;
			dous.writeInt(addCommunityResp.getRes());
			dous.writeInt(addCommunityResp.getDestcid());
		} else if (msgType == IMsgConstance.command_chatFile) {
			MsgChatFile chatFile = (MsgChatFile) msg;
			writeString(dous, 256, chatFile.getFileName());
			writeString(dous, 25, chatFile.getSendTime());
			dous.write(chatFile.getFileData());
		} else if (msgType == IMsgConstance.command_commuChatFile) {
			MsgCommuChatFile chatFile = (MsgCommuChatFile) msg;
			writeString(dous, 25, chatFile.getSendTime());
			dous.writeInt(chatFile.getDestCid());
			writeString(dous, 256, chatFile.getFileName());
			dous.write(chatFile.getFileData());
		} else if(msgType == IMsgConstance.command_addGroup) {
			MsgAddGroup addGroup = (MsgAddGroup) msg;
			writeString(dous, 256, addGroup.getGroupName());
		} else if(msgType == IMsgConstance.command_addGroup_resp) {
			MsgAddGroupResp addGroupResp = (MsgAddGroupResp) msg;
			dous.writeByte(addGroupResp.getState());
		} else if(msgType == IMsgConstance.command_deleteFriend) {
			
		} else if(msgType == IMsgConstance.command_deleteFriend_resp) {
			MsgDeleteFriendResp deleteFriendResp = (MsgDeleteFriendResp) msg;
			dous.writeByte(deleteFriendResp.getState());
			dous.writeInt(deleteFriendResp.getGid());
		} else if(msgType == IMsgConstance.command_deleteGroup) {
			MsgDeleteGroup deleteGroup = (MsgDeleteGroup) msg;
			dous.writeInt(deleteGroup.getGid());
		} else if(msgType == IMsgConstance.command_deleteGroup_resp) {
			MsgDeleteGroupResp deleteGroupResp = (MsgDeleteGroupResp) msg;
			dous.writeByte(deleteGroupResp.getState());
			dous.writeInt(deleteGroupResp.getGid());
		} else if(msgType == IMsgConstance.command_createCommunity) {
			MsgCreateCommunity community = (MsgCreateCommunity) msg;
			writeString(dous, 100, community.getcName());
			writeString(dous, 300, community.getcDes());
			writeString(dous, 100, community.getFileName());
			dous.write(community.getIcon());
		} else if(msgType == IMsgConstance.command_createCommunity_resp) {
			MsgCreateCommunityResp communityResp = (MsgCreateCommunityResp) msg;
			dous.writeByte(communityResp.getState());
			dous.writeInt(communityResp.getCid());
		} else if(msgType == IMsgConstance.command_deleteCommunity) {
			MsgDeleteCommunity community = (MsgDeleteCommunity) msg;
			dous.writeInt(community.getCid());
		} else if(msgType == IMsgConstance.command_deleteCommunity_resp) {
			MsgDeleteCommunityResp communityResp = (MsgDeleteCommunityResp) msg;
			dous.writeByte(communityResp.getState());
			dous.writeInt(communityResp.getCid());
		} else if(msgType == IMsgConstance.command_commu_onLine) {
			
		} else if(msgType == IMsgConstance.command_commu_offLine){
			
		} else if(msgType == IMsgConstance.command_forgetPwd) {
			
		} else if(msgType == IMsgConstance.command_forgetPwd_resp) {
			MsgForgetResp forgetResp = (MsgForgetResp) msg;
			writeString(dous, 300, forgetResp.getQuestion());
			writeString(dous, 300, forgetResp.getAnswer());
		} else if(msgType == IMsgConstance.command_changePwd) {
			MsgChangePwd changePwd = (MsgChangePwd) msg;
			writeString(dous, 32, changePwd.getNewPwd());
		} else if(msgType == IMsgConstance.command_changePwd_resp) {
			MsgChangePwdResp changePwdResp = (MsgChangePwdResp) msg;
			dous.writeByte(changePwdResp.getState());
		} else {
			String logMsg = "创建未知消息类型，无法打包:type:" + msgType;
			LogTools.ERROR(ToolsCreateMsg.class, logMsg);// 记录日志
		}
		dous.flush();
		byte[] data = baos.toByteArray();
		return data;// 返回打包后的数据,以方便发送

	}

	/**
	 * 向某个流中写入消息对象头部
	 * 
	 * @param dous
	 * @param m
	 * @throws IOException
	 */
	private static void writeHead(DataOutputStream dous, MsgHead m)
			throws IOException {
		dous.writeInt(m.getTotalLength());
		dous.writeByte(m.getType());
		dous.writeInt(m.getDest());
		dous.writeInt(m.getSrc());
	}

	/**
	 * 向流中写入len长度的字符串 如果长度不足len 补二进制0 '\0'
	 * 
	 * @param dous
	 * @param len
	 * @param s
	 * @throws IOException
	 */
	private static void writeString(DataOutputStream dous, int len, String s)
			throws IOException {
		byte[] data = s.getBytes();
		if (data.length > len) {
			throw new IOException("写入长度为" + data.length + ",超长!");
		}
		dous.write(data);
		while (len > data.length) {// 如果短，需要补0
			dous.writeByte('\0');// 补二进制0
			len--;
		}
	}
}
