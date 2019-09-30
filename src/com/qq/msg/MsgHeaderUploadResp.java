package com.qq.msg;

/**
 * 头像上传回应消息类
 * @author yy
 *
 */
public class MsgHeaderUploadResp extends MsgHead {
	
	
	private byte state;	//1:成功  其他:失败

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgHeaderUploadResp [state=" + state + "]";
	}
	
}
