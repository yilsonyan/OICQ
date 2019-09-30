package com.qq.model;

import java.io.Serializable;

/**
 * 群消息记录对应的实体
 * @author yy
 *
 */
public class CommuChatLog implements Serializable {
	
	private int cid;	//群id
	private int srcid;	//发送者的jknum
	private String content;	//发送的内容
	private int lid;	//唯一标示一个群聊记录
	private String sendTime;	//消息的发送时间
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getSrcid() {
		return srcid;
	}
	public void setSrcid(int srcid) {
		this.srcid = srcid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	@Override
	public String toString() {
		return "CommuChatLog [cid=" + cid + ", srcid=" + srcid + ", content="
				+ content + ", lid=" + lid + ", sendTime=" + sendTime + "]";
	}
	
	

}
