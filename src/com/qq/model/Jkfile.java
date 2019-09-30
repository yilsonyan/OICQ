package com.qq.model;

import java.io.File;
import java.io.Serializable;


/**
 * 对应数据库中的文件实体
 * @author yy
 *
 */
public class Jkfile implements Serializable, Comparable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fid;	//唯一的标识一个文件
	private File file;	//文件
	private String filename;	//文件名字
	private int uid;	//文件的发布者
	private String sendTime;	//文件的发送时间
	
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Jkfile(int fid, File file, String filename, int uid) {
		super();
		this.fid = fid;
		this.file = file;
		this.filename = filename;
		this.uid = uid;
	}
	public Jkfile() {
		super();
	}
	@Override
	public String toString() {
		return "Jkfile [fid=" + fid + ", filename="
				+ filename + ", uid=" + uid + "]";
	}
	@Override
	public int compareTo(Object o) {
		Jkfile file = (Jkfile) o;
		if(this.sendTime.compareTo(file.getSendTime()) < 0) {
			return 1;
		}else if(this.sendTime.compareTo(file.getSendTime()) == 0) {
			if(this.uid < file.getUid()) return 1;
			else return 0;
		}else {
			return -1;
		}
	}

	
	
	
	
	
}
