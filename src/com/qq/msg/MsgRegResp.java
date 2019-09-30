
package com.qq.msg;

import com.qq.model.Jkuser;

/**
 * 注册消息应答类
 * @author yy
 *
 */
public class MsgRegResp extends MsgHead {
	
	private byte state;	//0:注册成功  其他:注册error
	private int jknum;	//注册成功返回的jknum
	
	
	public byte getState() {
		return state;
	}
	public void setState(byte state) {
		this.state = state;
	}
	public int getJknum() {
		return jknum;
	}
	public void setJknum(int jknum) {
		this.jknum = jknum;
	}
	
	public MsgRegResp(byte state, int jknum) {
		super();
		this.state = state;
		this.jknum = jknum;
	}
	@Override
	public String toString() {
		return "MsgRegResp [state=" + state + ", jknum=" + jknum + "]";
	}


}
