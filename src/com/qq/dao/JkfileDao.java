package com.qq.dao;

/**
 * 文件实体操作定义
 * @author yy
 *
 */
public interface JkfileDao {
	public int addFile(String path, String name, int jid, String sendTime);	//添加一个文件
	public int addUFMapping(int destid, int fid, String sendTime, int curState);	//添加用户和文件映射
	public int addCfMapping(int cid, int fid);	//添加群组文件映射
	public int addUcfMapping(int jknum, int cid, int fid);	//添加用户群组文件映射
}
