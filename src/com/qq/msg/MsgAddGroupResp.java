package com.qq.msg;

/**
 * 分组添加请求回应消息
 * @author yy
 *
 */
public class MsgAddGroupResp extends MsgHead {
	
	private byte state;	//0添加失败 1添加成功

	public byte getState() {
		return state;
	}
	public void setState(byte state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "MsgAddGroupResp [state=" + state + "]";
	}
}
