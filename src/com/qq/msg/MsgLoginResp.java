package com.qq.msg;


import com.qq.model.Jkuser;

//登陆请求应答消息类
public class MsgLoginResp  extends MsgHead {
	
	private byte state;	//0:登陆success  其他：失败

	@Override
	public String toString() {
		return "MsgLoginResp [state=" + state + "]";
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
	

}
