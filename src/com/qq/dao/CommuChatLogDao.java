package com.qq.dao;

/**
 * 群聊记录实体 操作定义
 * @author yy
 *
 */
public interface CommuChatLogDao {
	
	public int insertLog(int cid, int srcNum, String chatTxt,String sendTime);
	public int addMapping(int lid, int jid);

}
