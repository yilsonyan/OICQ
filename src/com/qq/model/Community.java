package com.qq.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应群组实体	类似QQ群
 * @author yy
 *
 */
public class Community implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cid;	//群号码  群创建时为其分配
	private String name;	//群组的名字
	private int owner;	//群的拥有者
	private String des;	//群组的介绍性短语
	private File iconpath;	//群组的头像
	private List<Jkuser> userList = new ArrayList<Jkuser>() ;	//群组的所有成员
	private List<Jkfile> fileList = new ArrayList<Jkfile>() ;	//群共享中的文件集合
	
	public List<Jkuser> getUserList() {
		return userList;
	}
	public void setUserList(List<Jkuser> userList) {
		this.userList = userList;
	}
	public List<Jkfile> getFileList() {
		return fileList;
	}
	public void setFileList(List<Jkfile> fileList) {
		this.fileList = fileList;
	}
	public List<Jkuser> getList() {
		return userList;
	}
	public void setList(List<Jkuser> list) {
		this.userList = list;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
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
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public File getIconpath() {
		return iconpath;
	}
	public void setIconpath(File iconpath) {
		this.iconpath = iconpath;
	}
	public Community(int cid, String name, int owner, String des, File iconpath) {
		super();
		this.cid = cid;
		this.name = name;
		this.owner = owner;
		this.des = des;
		this.iconpath = iconpath;
	}
	public Community() {
		super();
	}
	@Override
	public String toString() {
		return "Community [cid=" + cid + ", name=" + name + ", owner=" + owner
				+ ", des=" + des + ", iconpath=" + iconpath + ", userList="
				+ userList + ", fileList=" + fileList + "]";
	}
	
	
	
	
	
}
