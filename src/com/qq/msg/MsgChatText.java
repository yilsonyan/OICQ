package com.qq.msg;

/**
 * 聊天文本消息类
 * @author yy
 *
 */
public class MsgChatText extends MsgHead {
	
	private String charTxt;	//聊天消息的文本内容
	private String sendTime;
	
	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getCharTxt() {
		return charTxt;
	}

	public void setCharTxt(String charTxt) {
		this.charTxt = charTxt;
	}

	@Override
	public String toString() {
		return "MsgChatText [charTxt=" + charTxt + "]";
	}

	
}
