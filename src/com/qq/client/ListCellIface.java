package com.qq.client;

import javax.swing.JComponent;



/**
 * Cell的接口类，用于构造BaseList中的cell
 * BaseList Demo 重要类
 * @ClassName ListCellIface
 * @author Jet
 * @date 2012-8-7
 */
public interface ListCellIface {
	public JComponent getListCell(BaseList list,Object value);
	public void setSelect(boolean iss);
}
