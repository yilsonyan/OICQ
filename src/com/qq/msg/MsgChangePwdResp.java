package com.qq.msg;

/**
 * 修改密码回应消息
 * @author yy
 *
 */
public class MsgChangePwdResp extends MsgHead {
	private byte state;	//0修改失败 1修改成功

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgChangePwdResp [state=" + state + "]";
	}
}
