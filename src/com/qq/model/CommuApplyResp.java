package com.qq.model;

import java.io.Serializable;

/**
 * 入群申请回应消息
 * @author yy
 *
 */
public class CommuApplyResp implements Serializable {
	
	private int cid;
	private int srcid;
	private int destid;
	private int state;
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}
	private int res;
	
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getSrcid() {
		return srcid;
	}
	public void setSrcid(int srcid) {
		this.srcid = srcid;
	}
	public int getDestid() {
		return destid;
	}
	public void setDestid(int destid) {
		this.destid = destid;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "CommuApplyResp [cid=" + cid + ", srcid=" + srcid + ", destid="
				+ destid + ", state=" + state + "]";
	}


}
