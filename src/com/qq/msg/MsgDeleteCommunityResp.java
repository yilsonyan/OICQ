package com.qq.msg;

/**
 * 群组删除回应消息
 * @author yy
 *
 */
public class MsgDeleteCommunityResp extends MsgHead {
	
	private byte state;	//1成功0失败
	private int cid;	//删除的群的id
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
	@Override
	public String toString() {
		return "MsgDeleteCommunityResp [state=" + state + ", cid=" + cid + "]";
	}
}
