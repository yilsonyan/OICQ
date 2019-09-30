package com.qq.msg;

/**
 * 添加好友回应消息
 * @author yy
 *
 */
public class MsgAddFriendResp extends MsgHead {
	private byte res;
	
	public byte getRes() {
		return res;
	}

	public void setRes(byte res) {
		this.res = res;
	}

	@Override
	public String toString() {
		return "MsgAddFriendResp [res=" + res + "]";
	}
	
	
}
