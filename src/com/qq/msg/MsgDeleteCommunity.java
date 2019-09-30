package com.qq.msg;

/**
 * 群组删除消息
 * @author yy
 *
 */
public class MsgDeleteCommunity extends MsgHead {
	
	private int cid;	//群组id

	@Override
	public String toString() {
		return "MsgDeleteCommunity [cid=" + cid + "]";
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
	

}
