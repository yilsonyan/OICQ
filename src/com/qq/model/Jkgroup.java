package com.qq.model;

import java.io.Serializable;
import java.util.List;

/**
 * QQ群组实体
 * @author yy
 *
 */
public class Jkgroup implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gid;	//小组编号  唯一的标识一个小组
	private String name;	//小组的名字
	private int owner;	//小组拥有者的jknum
	private List<Jkuser> userList;	//分组里的用户集合
	
	
	public List<Jkuser> getUserList() {
		return userList;
	}

	public void setUserList(List<Jkuser> userList) {
		this.userList = userList;
	}

	public Jkgroup() {
		
	}
	
	public Jkgroup(int gid, String name, int owner) {
		super();
		this.gid = gid;
		this.name = name;
		this.owner = owner;
	}
	
	
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Jkgroup [gid=" + gid + ", name=" + name + ", owner=" + owner
				+ ", userList=" + userList + "]";
	}

	
	
	

}
