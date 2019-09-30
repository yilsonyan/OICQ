package com.qq.client;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.qq.model.Community;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.util.ImageUtil;

/**
 * 为JTree的节点设置个性化的图片
 * 
 * @author yy
 * 
 */
public class MyRender extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		// 执行父类默认的节点绘制操作

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof String)
			return this;
		NodeData data = (NodeData) node.getUserObject();
		ImageIcon icon = null;
		switch (data.nodeType) {
		case 0:
			return this;
		case 1:
			Community community = (Community) data.value;
			BufferedImage bi = ImageUtil.compressImage(community.getIconpath(),
					20, 20);
			this.setIcon(new ImageIcon(bi));
			return this;
		case 2:
			Jkuser jkuser = (Jkuser) data.value;
			BufferedImage bi2 = ImageUtil.compressImage(jkuser.getIconpath(),
					20, 20);
			this.setIcon(new ImageIcon(bi2));
			return this;
		case 3:
			return this;
		case 4:
			Jkuser jkuser2 = (Jkuser) data.value;
			String uname1 = "user" + jkuser2.getJknum() + ".jpg";

			BufferedImage bi3 = ImageUtil.compressImage(jkuser2.getIconpath(),
					20, 20);
			BufferedImage bi4 = ImageUtil.convert2GrayPicture(bi3);
			this.setIcon(new ImageIcon(bi4));

			return this;
		}
		return this;
	}

}
