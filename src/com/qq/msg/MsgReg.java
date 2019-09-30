package com.qq.msg;

import java.io.Serializable;

import com.qq.model.Jkuser;

/**
 * 注册请求消息类
 * @author yy
 *
 */
public class MsgReg  extends MsgHead implements Serializable {
	
	private Jkuser jkuser;	//注册用户对象

	public Jkuser getJkuser() {
		return jkuser;
	}

	public void setJkuser(Jkuser jkuser) {
		this.jkuser = jkuser;
	}
	
	
	public MsgReg() {
		
	}
	
	public MsgReg(Jkuser jkuser) {
		this.jkuser = jkuser;
	}

	@Override
	public String toString() {
		return "MsgReg [jkuser=" + jkuser + "]";
	}
	
}
