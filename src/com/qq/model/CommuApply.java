package com.qq.model;

import java.io.Serializable;


/**
 * 入群申请实体
 * @author yy
 *
 */
public class CommuApply implements Serializable {
	
	private int srcid;
	private int cid;
	private int destid;
	private int state;
	public int getSrcid() {
		return srcid;
	}
	public void setSrcid(int srcid) {
		this.srcid = srcid;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
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
		return "CommuApply [srcid=" + srcid + ", cid=" + cid + ", destid="
				+ destid + ", state=" + state + "]";
	}


}
