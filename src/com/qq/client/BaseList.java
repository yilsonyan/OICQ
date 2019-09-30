package com.qq.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * 自定义JList,每个cell都是一个component,这个component就可以任意定义了,比如用一个JPanel来展示
 * BaseList Demo 核心类
 * @ClassName BaseList
 * @author Jet
 * @date 2012-8-7
 */
public class BaseList extends JComponent{
	private static final long serialVersionUID = 1L;
	// list的源
	private ListSource source;

	public ListSource getSource() {
		return source;
	}
	/**
	 * 设置BaseList的源
	 * @param source ListSource类型参数
	 */
	public void setSource(ListSource source) {
		if (source == null) {
			return;
		} else {
			this.source.removeSourceRefreshListener(this);
			this.source = null;
		}
		this.source = source;
		this.source.addSourceRefreshListener(this);
		this.refreshData();
	}

	// 显示控件 定义一个接口
	private ListCellIface celliface;
	/**
	 * 设置要显示的控件
	 * @param cell
	 */
	public void setCellIface(ListCellIface cell) {
		this.celliface = cell;
	}

	// 所有的控件 在对当前项操作时调用此变量
	private List<JComponent> totalcell = new ArrayList<JComponent>();

	// 选中的颜色
	private Color selectColor = new Color(252, 233, 161);

	public Color getSelectColor() {
		return selectColor;
	}

	public void setSelectColor(Color selectColor) {
		this.selectColor = selectColor;
	}

	// 鼠标经过的颜色
	private Color passColor = new Color(196, 227, 248);

	public Color getPassColor() {
		return passColor;
	}

	public void setPassColor(Color passColor) {
		this.passColor = passColor;
	}

	// 选中的索引
	private int selectIndex = -1;

	/**
	 * 选中某一行时执行此方法
	 * @param selectIndex
	 */
	public void setSelectIndex(int selectIndex) {
		for (int i = 0; i < totalcell.size(); i++) {
			//所有项设置无背景
			totalcell.get(i).setOpaque(false);
			totalcell.get(i).setBackground(null);
			if (celliface != null)
				((ListCellIface) totalcell.get(i)).setSelect(false);
		}
		if (selectIndex < totalcell.size() && selectIndex > -1) {
			//为选中项设置背景
			totalcell.get(selectIndex).setOpaque(true);
			totalcell.get(selectIndex).setBackground(blist.getSelectColor());
			if (celliface != null)
				((ListCellIface) totalcell.get(selectIndex)).setSelect(true);
		}

		this.selectIndex = selectIndex;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	// 自身
	private BaseList blist = this;

	public BaseList() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		source = new ListSource();
	}
	/**
	 * 刷新数据
	 */
	public void refreshData() {
		if (source.getAllCell().size() != 0) {
			//排序
			Collections.sort(source.getAllCell(), source.getComparator());
		}
		this.removeAll();
		this.totalcell.clear();
		for (int i = 0; i < source.getAllCell().size(); i++) {
			JComponent cell = null;
			if (celliface != null) {
				try {
					celliface = celliface.getClass().newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (celliface == null) {
				cell = new JLabel(source.getAllCell().get(i).toString());
				cell.setMaximumSize(new Dimension(ScreenUtil.getScreeWidth(),
						30));
				cell.setPreferredSize(new Dimension(0, 30));
				cell.setOpaque(false);
//				cell.setBackground(Color.orange);
			} else {
				cell = celliface.getListCell(this, source.getAllCell().get(i));
			}
			//对整个cell添加鼠标监听事件
			cell.addMouseListener(new MouseAdapter() {
				//鼠标点击
				public void mouseClicked(MouseEvent e) {
					for (int i = 0; i < totalcell.size(); i++) {
						if (e.getSource().equals(totalcell.get(i))) {
							//当前选中项
							blist.setSelectIndex(i);
							break;
						}
					}
				}
				//鼠标移入
				public void mouseEntered(MouseEvent e) {
					for (int i = 0; i < totalcell.size(); i++) {
						if (i != blist.getSelectIndex()) {
							//非选中项
							if (e.getSource().equals(totalcell.get(i))) {
								totalcell.get(i).setOpaque(true);
								totalcell.get(i).setBackground(blist.getPassColor());
								break;
							}
						}
					}
				}
				//鼠标移出
				public void mouseExited(MouseEvent e) {
					JComponent jc = (JComponent) e.getSource();

					if (blist.getSelectIndex() < totalcell.size()) {
						if (blist.getSelectIndex() != -1) {
							if (!jc.equals(totalcell.get(blist.getSelectIndex()))) {
								//非选中项
								jc.setOpaque(false);
								jc.setBackground(null);
							}
						} else {
							jc.setOpaque(false);
							jc.setBackground(null);
						}
					}
				}
			});
			//将cell逐条加入totalcell中,在此为totalcell赋值
			this.totalcell.add(cell);
			this.add(cell);
		}
		if (blist.getSelectIndex() != -1
				&& blist.getSelectIndex() < source.getAllCell().size()) {
			//为选中项设置背景
			totalcell.get(blist.getSelectIndex())
					.setBackground(blist.getSelectColor());
		}
		this.revalidate();
		this.repaint();
	}

	/**
	 * 源数据更新
	 * @param event
	 */
	public void sourceRefreshEvent(List event) {
		this.refreshData();
	}
}