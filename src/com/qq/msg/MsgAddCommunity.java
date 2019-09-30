package com.qq.msg;

/**
 * 入群请求消息类
 * @author yy
 *
 */
public class MsgAddCommunity extends MsgHead {
	private int destCid;	//目标群的id

	public int getDestCid() {
		return destCid;
	}
	public void setDestCid(int destCid) {
		this.destCid = destCid;
	}
	@Override
	public String toString() {
		return "MsgAddCommunity [destCid=" + destCid + "]";
	}
	
}
