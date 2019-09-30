package com.qq.msg;

/**
 * 群组创建回应消息
 * @author yy
 *
 */
public class MsgCreateCommunityResp extends MsgHead {
	
	private byte state;	//0失败1成功
	private int cid;	//群号码
	
	@Override
	public String toString() {
		return "MsgCreateCommunityResp [state=" + state + ", cid=" + cid + "]";
	}
	public byte getState() {
		return state;
	}
	public void setState(byte state) {
		this.state = state;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	
	
	
}
