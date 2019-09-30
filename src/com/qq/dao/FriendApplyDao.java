package com.qq.dao;

import java.util.List;

import com.qq.model.FriendApply;

/**
 * 好友申请记录表   动作定义
 * @author yy
 *
 */
public interface FriendApplyDao {
	
	public int addLog(int srcNum,int destNum,int state);
	public List<FriendApply> queryLog(int jknum);
	public void changeState(int srcNum,int destNum);
	
}
