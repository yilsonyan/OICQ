package com.qq.msg;

/**
 * 入群申请回应消息类
 * @author yy
 *
 */
public class MsgAddCommunityResp extends MsgHead {

	private int res;	//1同意 0拒绝
	private int destcid;	//目标申请的qq群号码
	
	
	
	public int getDestcid() {
		return destcid;
	}
	public void setDestcid(int destcid) {
		this.destcid = destcid;
	}
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}
	@Override
	public String toString() {
		return "MsgAddCommunityResp [res=" + res + ", destcid=" + destcid + "]";
	}
	

}
