package com.qq.msg;

/**
 * 查找请求回应消息类
 * @author yy
 *
 */
public class MsgFindResp extends MsgHead {
	
	private byte state;	//1:成功 0：无

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgFindResp [state=" + state + "]";
	}
	
}
