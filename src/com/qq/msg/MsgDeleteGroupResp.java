package com.qq.msg;

/**
 * 群组删除回应消息
 * @author yy
 *
 */
public class MsgDeleteGroupResp extends MsgHead {
	
	private byte state;	//1成功 0失败
	private int gid;

	public int getGid() {
		return gid;
	}

	public void setGid(int Gid) {
		this.gid = Gid;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgDeleteGroupResp [state=" + state + ", Gid=" + gid + "]";
	}
}
