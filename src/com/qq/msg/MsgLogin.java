package com.qq.msg;

/**
 * 登陆请求消息类
 * @author yy
 *
 */
public class MsgLogin extends MsgHead {
	
	private String password;	//登陆密码
	private int state;	//登陆状态 1在线 2隐身

	public MsgLogin(String password,int state) {
		super();
		this.password = password;
		this.state = state;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MsgLogin() {
		super();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MsgLogin [password=" + password + ", state=" + state + "]";
	}

}
