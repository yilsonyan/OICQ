package com.qq.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qq.util.MD5Util;

/**
 * 对应Jkuser实体
 * @author yy
 *
 */
public class Jkuser implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private int jknum;	//用户jknum
	private String name;	//用户昵称
	private String password;	//用户的密码
	private String signature;	//用户的个性签名
	private File iconpath;	//用户的头像
	private String site;	//用户所在地
	private String phone;	//用户电话号码
	private String email;	//用户的邮箱
	private int state;	//用户的state 1 在线 2 隐身
	private String question;	//密保问题
	private String answer;	//密保的答案
	private int sex;	//性别
	private List<Jkgroup> groupList;	//分组列表
	private List<Community> commuList;	//群组列表
	private List<CommuChatLog> cmuChatLogList = new ArrayList<CommuChatLog>();	//未处理的群聊消息记录
	private List<ChatLog> logList = new ArrayList<ChatLog>();	//未处理的聊天记录集合
	private List<FriendApply> applyList = new ArrayList<FriendApply>();	//未处理的好友申请集合
	private List<Jkuser> uList = new ArrayList<Jkuser>();	//申请入群的好友列表
	private List<Community> cmuList = new ArrayList<Community>();	//申请入群的群列表
	private List<Jkfile> fileList = new ArrayList<Jkfile>();	//未接受的文件列表
	private Map<Integer, Integer> hasNewFile = new HashMap<Integer, Integer>();	//cid--hasNewFile映射 0无 1有
	

	public Map<Integer, Integer> getHasNewFile() {
		return hasNewFile;
	}


	public void setHasNewFile(Map<Integer, Integer> hasNewFile) {
		this.hasNewFile = hasNewFile;
	}


	public List<Jkfile> getFileList() {
		return fileList;
	}


	public void setFileList(List<Jkfile> fileList) {
		this.fileList = fileList;
	}


	public List<Jkuser> getuList() {
		return uList;
	}


	public void setuList(List<Jkuser> uList) {
		this.uList = uList;
	}


	public List<Community> getCmuList() {
		return cmuList;
	}


	public void setCmuList(List<Community> cmuList) {
		this.cmuList = cmuList;
	}


	public List<CommuApply> getCmuApplyList() {
		return cmuApplyList;
	}


	public void setCmuApplyList(List<CommuApply> cmuApplyList) {
		this.cmuApplyList = cmuApplyList;
	}


	public List<CommuApplyResp> getCmuApplyRespList() {
		return cmuApplyRespList;
	}


	public void setCmuApplyRespList(List<CommuApplyResp> cmuApplyRespList) {
		this.cmuApplyRespList = cmuApplyRespList;
	}


	private List<FriendApplyResp> applyRespList = new ArrayList<FriendApplyResp>();	//未处理的好友处理回应集合
	private List<CommuApply> cmuApplyList = new ArrayList<CommuApply>();	//未处理的群申请
	private List<CommuApplyResp> cmuApplyRespList = new ArrayList<CommuApplyResp>();	//未处理的群申请回应消息
	
	
	public List<FriendApply> getApplyList() {
		return applyList;
	}


	public void setApplyList(List<FriendApply> applyList) {
		this.applyList = applyList;
	}


	public List<FriendApplyResp> getApplyRespList() {
		return applyRespList;
	}


	public void setApplyRespList(List<FriendApplyResp> applyRespList) {
		this.applyRespList = applyRespList;
	}


	public List<ChatLog> getLogList() {
		return logList;
	}


	public void setLogList(List<ChatLog> logList) {
		this.logList = logList;
	}


	public List<CommuChatLog> getCmuChatLogList() {
		return cmuChatLogList;
	}


	public void setCmuChatLogList(List<CommuChatLog> cmuChatLogList) {
		this.cmuChatLogList = cmuChatLogList;
	}


	public List<Jkgroup> getGroupList() {
		return groupList;
	}


	public void setGroupList(List<Jkgroup> groupList) {
		this.groupList = groupList;
	}


	public List<Community> getCommuList() {
		return commuList;
	}


	public void setCommuList(List<Community> commuList) {
		this.commuList = commuList;
	}


	public Jkuser() {
		super();
	}


	public Jkuser(int jknum, String name, String password, String signature,
			File iconpath, String site, String phone, String email,
			int state, String question, String answer,String sex) {
		super();
		this.jknum = jknum;
		this.name = name;
		this.password = MD5Util.MD5(password);
		this.signature = signature;
		this.iconpath = iconpath;
		this.site = site;
		this.phone = phone;
		this.email = email;
		this.state = state;
		this.question = question;
		this.answer = MD5Util.MD5(answer);
		if(sex.equals("男")) {
			this.sex = 1;
		}else if(sex.equals("女")) {
			this.sex = 2;
		}else {
			this.sex = 0;
		}
	}
	
	@Override
	public String toString() {
		return "Jkuser [jknum=" + jknum + ", name=" + name + ", password="
				+ password + ", signature=" + signature + ", iconpath="
				+ iconpath + ", site=" + site + ", phone=" + phone + ", email="
				+ email + ", state=" + state + ", question=" + question
				+ ", answer=" + answer + ", sex=" + sex + ", groupList="
				+ groupList + ", commuList=" + commuList + ", cmuChatLogList="
				+ cmuChatLogList + ", logList=" + logList + ", applyList="
				+ applyList + ", uList=" + uList + ", cmuList=" + cmuList
				+ ", fileList=" + fileList + " hasNewFile=" + hasNewFile + ", applyRespList="
				+ applyRespList + ", cmuApplyList=" + cmuApplyList
				+ ", cmuApplyRespList=" + cmuApplyRespList + "]";
	}


	public int getJknum() {
		return jknum;
	}
	public void setJknum(int jknum) {
		this.jknum = jknum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public File getIconpath() {
		return iconpath;
	}
	public void setIconpath(File iconpath) {
		this.iconpath = iconpath;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}


	public int getSex() {
		return sex;
	}


	public void setSex(int sex) {
		this.sex = sex;
	}
	
}
