package com.qq.msg;

/**
 * 修改密码请求消息
 * @author yy
 *
 */
public class MsgChangePwd extends MsgHead {
	private String newPwd;	//新的密码

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	@Override
	public String toString() {
		return "MsgChangePwd [newPwd=" + newPwd + "]";
	}
}
