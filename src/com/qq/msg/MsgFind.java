package com.qq.msg;

/**
 * 查找请求消息类
 * @author yy
 *
 */
public class MsgFind  extends MsgHead {
	
	private byte classify;	//0 查找好友 1 查找群
	private int findId;	//查找id 群号或者qq号码
	
	public byte getClassify() {
		return classify;
	}
	public void setClassify(byte classify) {
		this.classify = classify;
	}
	public int getFindId() {
		return findId;
	}
	public void setFindId(int findId) {
		this.findId = findId;
	}
	@Override
	public String toString() {
		return "MsgFind [classify=" + classify + ", findId=" + findId + "]";
	}

	
}
