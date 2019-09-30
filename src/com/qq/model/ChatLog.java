package com.qq.model;

import java.io.Serializable;

/**
 * 聊天记录表
 * @author yy
 *
 */
public class ChatLog  implements Serializable {
	
	
	private int srcid;	//发送方id	
	private int destid;	//接收方id
	private String content;	//内容
	private int state;	//状态 0未接受  1 已接受
	private String sendTime;	//发送时间
	public int getSrcid() {
		return srcid;
	}
	public void setSrcid(int srcid) {
		this.srcid = srcid;
	}
	public int getDestid() {
		return destid;
	}
	public void setDestid(int destid) {
		this.destid = destid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getSendtime() {
		return sendTime;
	}
	public void setSendtime(String sendTime) {
		this.sendTime = sendTime;
	}
	@Override
	public String toString() {
		return "ChatLog [srcid=" + srcid + ", destid=" + destid + ", content="
				+ content + ", state=" + state + ", sendTime=" + sendTime + "]";
	}
}
