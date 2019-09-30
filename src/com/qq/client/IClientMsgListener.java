package com.qq.client;

import com.qq.msg.MsgHead;

/**
 * QQ项目
 * 通讯模块的消息处理监听器接口定义
 * @author yy
 *
 */
public interface IClientMsgListener {
	
	/**
	 * 处理接收到的一条消息
	 * @param msg
	 */
	public void fireMsg(MsgHead msg);
	
}
