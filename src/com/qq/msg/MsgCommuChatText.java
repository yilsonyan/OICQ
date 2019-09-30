package com.qq.msg;

/**
 * 群聊消息类
 * @author yy
 *
 */
public class MsgCommuChatText extends MsgHead {
	
	private int destCid;	//目标群号码
	private String sendTime;	//发送时间
	private String chatTxt;	//发送内容
	
	
	public int getDestCid() {
		return destCid;
	}
	public void setDestCid(int destCid) {
		this.destCid = destCid;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getchatTxt() {
		return chatTxt;
	}
	public void setchatTxt(String chatTxt) {
		this.chatTxt = chatTxt;
	}
	@Override
	public String toString() {
		return "MsgCommuChatText [destCid=" + destCid + ", sendTime="
				+ sendTime + ", chatTxt=" + chatTxt + "]";
	}

}
