package com.qq.dao;

public interface GroupDao {
	
	public void addFriends(int gid, int jknum);	//向分组中添加好友
	public int addGroup(String name, int jknum);	//添加分组
	public int deleteFriends(int jid, int gid);	//删除好友
	public int getGidByJknum(int srcNum, int destNum);
	public int deleteGroup(int gid);	//删除一个分组
}
