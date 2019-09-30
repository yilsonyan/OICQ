package com.qq.msg;

/**
 * 所有消息类型的父类
 * @author yy
 *
 */
public class MsgHead {
	
	private int totalLength;	//消息的总长度
	private byte type;	//消息的类型
	private int dest;	//消息接收方的jk号码
	private int src;	//消息发送方的jk号码
	
	
	
	public int getTotalLength() {
		return totalLength;
	}
	
	public String toString(){
		return "totalLength:"+totalLength+" type:"+type
		+" dest:"+dest+" src:"+src;
	}
	
	//get set
	
	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public int getDest() {
		return dest;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}
	public int getSrc() {
		return src;
	}
	public void setSrc(int src) {
		this.src = src;
	}
	
	
}
