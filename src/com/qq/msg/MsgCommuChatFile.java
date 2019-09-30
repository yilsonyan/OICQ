package com.qq.msg;

import java.util.Arrays;

/**
 * 群聊文件消息类
 * @author yy
 *
 */
public class MsgCommuChatFile extends MsgHead {
	private String sendTime;	//文件的发送时间
	private byte[] fileData;	//文件的数据
	private int destCid;	//目标群号码
	private String fileName;	//文件名称
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	public int getDestCid() {
		return destCid;
	}
	public void setDestCid(int destCid) {
		this.destCid = destCid;
	}
	@Override
	public String toString() {
		return "MsgCommuChatFile [sendTime=" + sendTime +  ", destCid=" + destCid
				+ ", fileName=" + fileName + "]";
	}

}
