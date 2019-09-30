package com.qq.dao;

import java.util.List;

import com.qq.model.Jkuser;

/**
 * QQ用户实体操作定义
 * @author yy
 *
 */
public interface JkuserDao {
	
	
	public int regUser(Jkuser jkuser);	//注册用户  返回为其分配的jkuser
	public Jkuser checkLogin(int jknum, String password,int state);	//用户的登陆 成功返回用户的全部信息
	public Jkuser findPwd(int jknum);	//找回密码  返回用户的jknum 和  密保问题 密保答案
	public int changePwd(int jknum, String newPwd);	//修改密码 成功返回1
	public void offOnline(int jknum);	//设置qq离线
	public int updateIcon(int jknum,String iconpath);	//修改头像
	public int updateUserInfo(Jkuser jkuser);	//更新用户的基本资料  
	public Jkuser getBasicInfo(int jknum);
	public List<Integer> getAllCids(int jid);
}
