package com.qq.msg;

/**
 * QQ项目的一些常量定义
 * 
 * @author yy
 * 
 */
public interface IMsgConstance {

	String serverIP = "localhost";// 服务器IP
	int serverPort = 9090; // 服务器端口
	int Server_JK_NUMBER = 10000;// 服务器的JK号
	
	
	// 系统用到的消息类型定义
	byte command_reg = 0x01;// 注册请求消息
	byte command_reg_resp = 0x02;// 注册应答消息
	byte command_login = 0x03;	//登陆请求消息
	byte command_login_resp = 0x04;	//登陆应答消息
	byte command_headerupload = 0x05;	//头像上传消息类
	byte command_headerupload_resp = 0x06;	//头像上传回应类
	byte command_find = 0x07;	//查找请求消息类
	byte command_find_resp = 0x08;	//查找请求回应消息类
	byte command_chatText = 0x09;	//聊天文本消息类
	byte command_chatFile = 0x10;	//聊天文件消息类
	byte command_commuChatFile = 0x11;	//群聊文件消息类
	byte command_onLine = 0x12;	//好友上线通知消息	*无消息体
	byte command_offLine = 0x13;	//好友离线消息通知	*无消息体
	byte command_addFriend_resp =  0x14;	//好友申请回应消息
	byte command_addFriend = 0x15;	//好友申请消息
	byte command_commuChatTxt = 0x16;	//群聊文本消息
	byte command_addCommunity = 0x17;	//入群申请消息
	byte command_addCommunity_resp = 0x18;	//入群申请回应消息类
	byte command_addGroup = 0x19;	//添加分组消息类
	byte command_addGroup_resp = 0x20;	//添加分组消息回应类
	byte command_deleteFriend = 0x21;	//删除好友消息 *无消息体
	byte command_deleteFriend_resp = 0x22;	//删除好友请求回应
	byte command_deleteGroup = 0x23;	//删除分组消息
	byte command_deleteGroup_resp = 0x24;	//删除分组回应消息
	byte command_createCommunity = 0x25;	//群组创建消息
	byte command_createCommunity_resp = 0x26;	//群组创建回应消息
	byte command_deleteCommunity = 0x27;	//删除群组消息
	byte command_deleteCommunity_resp = 0x28;	//删除群组回应消息
	byte command_commu_onLine = 0x29;	//上线提醒至群信息 *无消息体
	byte command_commu_offLine = 0x30;	//上线提醒至群信息 *无消息体
	byte command_forgetPwd = 0x31;	//忘记密码消息
	byte command_forgetPwd_resp = 0x32;	//忘记密码回应
	byte command_changePwd = 0x33;	//修改密码
	byte command_changePwd_resp = 0x34;	//修改密码回应
	
}
