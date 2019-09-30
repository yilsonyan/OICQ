package com.qq.client;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;



/**
 * 基本数据模型
 * 主要用于数据刷新
 * BaseList Demo 核心类
 * @ClassName BaseModel
 * @author Jet
 * @date 2012-8-7
 */

public class BaseModel {
	private Vector<BaseList> repository = new Vector<BaseList>();
	private BaseList bl;
	// 注册监听器，如果这里没有使用Vector而是使用ArrayList那么要注意同步问题
	public void addSourceRefreshListener(BaseList list) {
		repository.addElement(list);// 这步要注意同步问题
	}

	// 如果这里没有使用Vector而是使用ArrayList那么要注意同步问题
	public void notifySourceRefreshEvent(List<Object> event) {
		Enumeration<BaseList> en = repository.elements();// 这步要注意同步问题
		while (en.hasMoreElements()) {
			bl = (BaseList) en.nextElement();
			bl.sourceRefreshEvent(event);
		}
	}
	// 删除监听器，如果这里没有使用Vector而是使用ArrayList那么要注意同步问题
	public void removeSourceRefreshListener(BaseList srl) {
		repository.remove(srl);// 这步要注意同步问题
	}
}
