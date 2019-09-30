package com.qq.msg;


/**
 * 分组删除消息
 * @author yy
 *
 */
public class MsgDeleteGroup extends MsgHead {
	
	private int gid;	//删除的分组id

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}
	@Override
	public String toString() {
		return "MsgDeleteGroup [cid=" + gid + "]";
	}
}
