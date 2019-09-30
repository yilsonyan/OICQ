package com.qq.model;

import java.io.Serializable;



public class FriendApplyResp implements Serializable { 
	
	private int srcid;
	private int destid;
	private int state;
	private int res;
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
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

}
