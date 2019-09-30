package com.qq.util;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * 开启树的节点	打开某棵树的全部节点
 * @author yy
 *
 */
public class OpenTree {
	
	public static void expandTree(JTree tree,boolean flag) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree,new TreePath(root),flag);
	}

	private static void expandAll(JTree tree, TreePath treePath, boolean flag) {
		TreeNode node = (TreeNode) treePath.getLastPathComponent();
		if(node.getChildCount() >= 0) {
			for (Enumeration e = node.children();e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = treePath.pathByAddingChild(n);
				expandAll(tree, path, flag);
			}
		}
		if(flag) {
			tree.expandPath(treePath);
		}else {
			tree.collapsePath(treePath);
		}
	}
	

}
