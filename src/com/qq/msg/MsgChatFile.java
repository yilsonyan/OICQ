package com.qq.msg;

import java.util.Arrays;

/**
 * 聊天文件消息类
 * @author yy
 *
 */
public class MsgChatFile extends MsgHead {
	
	private String fileName;	//文件名字
	private byte[] fileData;	//文件数据
	private String sendTime;	//发送时间
	
	
	@Override
	public String toString() {
		return "MsgChatFile [fileName=" + fileName + ", sendTime=" + sendTime
				+ "]";
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
}
