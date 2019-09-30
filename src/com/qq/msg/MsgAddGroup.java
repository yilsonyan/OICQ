package com.qq.msg;

/**
 * 分组添加消息类
 */
public class MsgAddGroup extends MsgHead {
	
	private String groupName;	//群组名字

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "MsgAddGroup [groupName=" + groupName + "]";
	}
}
