package com.qq.client;

import java.io.File;

import com.qq.model.Community;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;

/**
 * 封装节点的数据
 * @author yy
 *
 */
public class NodeData {
	
	public int nodeType;	//节点的类型 0组 1群 2在线好友 3根节点  4离线好友
	public Object value;	//根据节点类型决定其具体的类型 
	//可能是jkgroup 或者 jkuser 或者 community	或者是根节点的纯字符串类型
	public int onlineNum = 0;
	public int totalNum = 0;
	
	public File getIconFile() {
		if(nodeType == 0 || nodeType == 3) return null;
		else if(nodeType == 1) {
			Community community = (Community) value;
			return community.getIconpath();
		} else {
			Jkuser jkuser = (Jkuser) value;
			return jkuser.getIconpath();
		}
	}
	
	public NodeData(int nodeType, Object value) {
		this.nodeType = nodeType;
		this.value = value;
	}
	
	public NodeData(int nodeType, Object value, int onlineNum, int totalNum) {
		super();
		this.nodeType = nodeType;
		this.value = value;
		this.onlineNum = onlineNum;
		this.totalNum = totalNum;
	}

	@Override
	public String toString() {
		if(nodeType == 0) {
			Jkgroup group = (Jkgroup)value;
			return group.getName() + " " + onlineNum + "/" + totalNum;
		}else if(nodeType == 1) {
			Community community = (Community) value;
			return community.getName() + "(" + ((community.getDes() == null || community.getDes().equals(""))?"":community.getDes()) + ")";
		} else if(nodeType == 2 || nodeType == 4) {
			Jkuser user = (Jkuser) value;
			if(user.getSignature()!=null && !user.getSignature().equals(""))
				return user.getName() + "("  + user.getSignature() + ")";
			else 
				return user.getName() + "()";
		} else {
			return (String) value;
		}
	}
	

}
