package com.qq.model;

import java.io.Serializable;

public class FriendApply implements Serializable {
	
	private int srcid;
	private int destid;
	private int state;
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
