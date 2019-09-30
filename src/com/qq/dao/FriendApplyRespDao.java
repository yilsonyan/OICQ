package com.qq.dao;

import java.util.List;

import com.qq.model.FriendApplyResp;

/**
 * 好友申请恢复消息 动作定义
 * @author yy
 *
 */
public interface FriendApplyRespDao {
	
	public int add(int srcNum,int destNum, int state, int res);
	public List<FriendApplyResp> queryLog(int jknum);
	public void changeState(int srcNum, int destNum);
	
	
	
}
