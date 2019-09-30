package com.qq.msg;

/**
 * 删除好友请求回应消息类
 * @author yy
 *
 */
public class MsgDeleteFriendResp extends MsgHead {
	private byte state;	//0失败1成功
	private int gid = 0;
	
	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgDeleteFriendResp [state=" + state + ", gid=" + gid + "]";
	}

	
}
