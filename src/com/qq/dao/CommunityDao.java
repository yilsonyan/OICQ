package com.qq.dao;

import java.util.List;

import com.qq.model.Community;
import com.qq.model.Jkuser;

/**
 * 群实体操作定义
 * @author yy
 *
 */
public interface CommunityDao {
	
	public Community getBasicInfo(int cid);
	public int getOwnerByCid(int cid);
	public int insertLog(int jknum, int cid);
	public int addCommunity(String name, int owner, String des, String path);	//创建一个新群
	public int deleteCommunity(int cid);	//删除一个群
	public List<Integer> getAllOnLineUsers(int cid);	//得到一个群的所有在线用户的jknum
}
