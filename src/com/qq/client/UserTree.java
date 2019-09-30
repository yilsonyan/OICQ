package com.qq.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.soap.Node;

import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgAddGroup;
import com.qq.msg.MsgChatFile;
import com.qq.msg.MsgChatText;
import com.qq.msg.MsgDeleteFriendResp;
import com.qq.msg.MsgDeleteGroup;
import com.qq.msg.MsgHead;
import com.qq.util.ImageUtil;
import com.qq.util.OnLineInfoUtil;

/**
 * QQ通信系统 在主界面中显示好友/分组列表的树形结构 并处理相关消息
 * 
 * @author yy
 */
public class UserTree extends JTree {

	private static final long serialVersionUID = 1L;
	private Map<Integer, Integer> sendFrameMap = new HashMap<Integer, Integer>();// <jknum,times>mapping
	private Object userO = null;
	private Jkuser jkuser; // 用户对象
	Point p = null;
	private UserTree ut = null;

	private List<JFrame> jfList = new ArrayList<JFrame>();
	private List<JTextArea> jtList = new ArrayList<JTextArea>();
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	DefaultMutableTreeNode root = null;
	// 取得连结器的单实例对象
	private ClientConnection conn = ClientConnection.getIns();
	String input = null;
	// 创建树时，传入jkuser对象
	public UserTree(Jkuser jkuser) {
		this.jkuser = jkuser;
		go();
		ut = this;
	}

	private void go() {
		root = new DefaultMutableTreeNode(new NodeData(3, "好友管理"));
		DefaultTreeModel tm = new DefaultTreeModel(root);
		tm.setAsksAllowsChildren(true);
		// 添加分组
		List<Jkgroup> glist = jkuser.getGroupList();
		for (int i = 0; i < glist.size(); i++) {
			// 添加分组里的全部好友
			List<Jkuser> ulist = glist.get(i).getUserList();
			List<Jkuser> onUlist = new ArrayList<Jkuser>(); // 在线好友列表
			List<Jkuser> offUlist = new ArrayList<Jkuser>(); // 不在线列表
			for (int j = 0; j < ulist.size(); j++) {
				if (ulist.get(j).getState() == 1) {
					onUlist.add(ulist.get(j));
				} else {
					offUlist.add(ulist.get(j));
				}
			}
			DefaultMutableTreeNode gnode = new DefaultMutableTreeNode(
					new NodeData(0, glist.get(i), onUlist.size(), ulist.size()));
			root.add(gnode);
			for (int j = 0; j < onUlist.size(); j++) {
				DefaultMutableTreeNode unode = new DefaultMutableTreeNode(
						new NodeData(2, onUlist.get(j)));
				unode.setAllowsChildren(false);
				gnode.add(unode);
			}
			for (int j = 0; j < offUlist.size(); j++) {
				DefaultMutableTreeNode unode = new DefaultMutableTreeNode(
						new NodeData(4, offUlist.get(j)));
				unode.setAllowsChildren(false);

				gnode.add(unode);
			}

		}

		this.setCellRenderer(new MyRender());

		// 设置几个父节点的背景图片
		DefaultTreeCellRenderer render = (DefaultTreeCellRenderer) this
				.getCellRenderer();
		ImageIcon openIcon = new ImageIcon("./images/list_open.png");
		ImageIcon closeIcon = new ImageIcon("./images/list_close.png");
		render.setOpenIcon(openIcon);
		render.setClosedIcon(closeIcon);

		this.setRootVisible(false);
		this.putClientProperty("JTree.lineStyle", "None");// 去掉连接线
		this.setModel(tm);

		final JPopupMenu menu = new JPopupMenu();
		JMenuItem deleteFriend = new JMenuItem("删除好友");
		menu.add(deleteFriend);
		JMenuItem selectFriend = new JMenuItem("查看基本信息");
		menu.add(selectFriend);

		final JPopupMenu menu2 = new JPopupMenu();
		JMenuItem deleteGroup = new JMenuItem("删除分组");
		menu2.add(deleteGroup);

		final JPopupMenu menu3 = new JPopupMenu();
		JMenuItem addNewGroup = new JMenuItem("创建新分组");
		menu3.add(addNewGroup);

		// 给树加上Mouse事件监听器，双击弹出界面给用户发消息
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 双击事件
					showSendFrame();// 弹出发送消息框
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if (e.getX() > 210) {
						menu3.show(ut, e.getX(), e.getY());
						return;
					}
					// 得到树上选中的节点:
					TreePath tp = ut.getSelectionPath();
					;
					if (tp == null) {// 未选中树节点
						return;
					}

					Object obj = tp.getLastPathComponent();// 取得选中的节点
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
					userO = node.getUserObject();// 取得节点内的对象
					if (userO instanceof NodeData
							&& (((NodeData) userO).nodeType == 2 || ((NodeData) userO).nodeType == 4)) {
						menu.show(ut, e.getX(), e.getY());
					} else if (userO instanceof NodeData
							&& ((NodeData) userO).nodeType == 0) {
						NodeData data = (NodeData) userO;
						Jkgroup group = (Jkgroup) data.value;
						if (group.getName().trim().equals("我的好友")) {
							return;
						}
						menu2.show(ut, e.getX(), e.getY());
					}
				}
			}
		});

		addNewGroup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				input = JOptionPane.showInputDialog(ut, "请输入分组名称").trim();
				List<Jkgroup> gList = jkuser.getGroupList();
				boolean flag = false;

				for (int i = 0; i < gList.size(); i++) {
					if (gList.get(i).getName().equals(input)) {
						flag = true;
						break;
					}
				}

				if (flag) {
					JOptionPane.showMessageDialog(ut, "该名称已存在!");
				} else {

					MsgAddGroup addGroup = new MsgAddGroup();
					addGroup.setTotalLength(269);
					addGroup.setType(IMsgConstance.command_addGroup);
					addGroup.setDest(IMsgConstance.Server_JK_NUMBER);
					addGroup.setSrc(jkuser.getJknum());
					addGroup.setGroupName(input);

					try {
						conn.sendMsg(addGroup);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}

			}
		});
		deleteFriend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((userO instanceof NodeData)
						&& (((NodeData) userO).nodeType == 2 || ((NodeData) userO).nodeType == 4)) {// 选中的是一个用户节点对象
					NodeData data = (NodeData) userO;
					final Jkuser user = (Jkuser) data.value;
					// 弹出发送消息框

					int n = JOptionPane.showConfirmDialog(ut,
							"您确定要删除" + user.getName() + "(" + user.getJknum()
									+ ")" + "吗 ?");
					if (n == JOptionPane.YES_OPTION) {
						// 向服务器发送删除好友请求
						MsgHead head = new MsgHead();
						head.setSrc(jkuser.getJknum());
						head.setDest(user.getJknum());
						head.setType(IMsgConstance.command_deleteFriend);
						head.setTotalLength(13);

						try {
							conn.sendMsg(head);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		selectFriend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((userO instanceof NodeData)
						&& (((NodeData) userO).nodeType == 2 || ((NodeData) userO).nodeType == 4)) {// 选中的是一个用户节点对象
					NodeData data = (NodeData) userO;
					final Jkuser user = (Jkuser) data.value;
					// 弹出发送消息框
					JFrame frame = new JFrame("用户资料");
					frame.setLayout(null);
					frame.setBounds(0, 0, 420, 370);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);

					// 设置标题头像
					ImageIcon icon = new ImageIcon("./images/logo.jpg");
					Image logo = icon.getImage();
					frame.setIconImage(logo);
					// 设置背景图片
					ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
					JLabel jl_bg = new JLabel(bg_icon);
					jl_bg.setBounds(0, 0, bg_icon.getIconWidth(),
							bg_icon.getIconHeight());
					// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
					JPanel imgPanel = (JPanel) frame.getContentPane();
					imgPanel.setOpaque(false);
					frame.getLayeredPane().setLayout(null);
					frame.getLayeredPane().add(jl_bg,
							new Integer(Integer.MIN_VALUE));
					// 设置imagePane的布局方式为绝对布局
					imgPanel.setLayout(null);

					JLabel jl_jknum = new JLabel("QQ号码:");
					jl_jknum.setBounds(40, 20, 60, 30);
					JLabel jl_jknum2 = new JLabel(user.getJknum() + "");
					jl_jknum2.setBounds(110, 20, 85, 30);
					JLabel jl_name = new JLabel("昵称:");
					jl_name.setBounds(40, 60, 60, 30);
					JLabel jl_name2 = new JLabel(user.getName());
					jl_name2.setBounds(110, 60, 260, 30);
					JLabel jl_sign = new JLabel("个性签名:");
					jl_sign.setBounds(40, 100, 60, 30);
					JLabel jl_sign2 = new JLabel(user.getSignature());
					jl_sign2.setBounds(110, 100, 260, 30);
					JLabel jl_site = new JLabel("地点:");
					jl_site.setBounds(40, 140, 60, 30);
					JLabel jl_site2 = new JLabel(user.getSite());
					jl_site2.setBounds(110, 140, 85, 30);
					JLabel jl_phone = new JLabel("电话:");
					jl_phone.setBounds(40, 180, 60, 30);
					JLabel jl_phone2 = new JLabel(user.getPhone());
					jl_phone2.setBounds(110, 180, 85, 30);
					JLabel jl_email = new JLabel("邮箱:");
					jl_email.setBounds(40, 220, 60, 30);
					JLabel jl_email2 = new JLabel(user.getEmail());
					jl_email2.setBounds(110, 220, 260, 30);
					JLabel jl_sex = new JLabel("性别:");
					jl_sex.setBounds(40, 260, 60, 30);
					String sex = (user.getSex() == 0) ? "男" : "女";
					JLabel jl_sex2 = new JLabel(sex);
					jl_sex2.setBounds(110, 260, 85, 30);

					// 添加组件
					frame.add(jl_jknum2);
					frame.add(jl_jknum);
					frame.add(jl_name);
					frame.add(jl_name2);
					frame.add(jl_sign);
					frame.add(jl_sign2);
					frame.add(jl_site);
					frame.add(jl_site2);
					frame.add(jl_phone2);
					frame.add(jl_phone);
					frame.add(jl_email2);
					frame.add(jl_email);
					frame.add(jl_sex2);
					frame.add(jl_sex);
				}
			}
		});

		deleteGroup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				NodeData data = (NodeData) userO;
				Jkgroup group = (Jkgroup) data.value;
				int n = JOptionPane.showConfirmDialog(ut,
						"您确定要删除分组  '" + group.getName() + "'  吗?");
				if (n == JOptionPane.YES_OPTION) {
					MsgDeleteGroup deleteGroup = new MsgDeleteGroup();
					deleteGroup.setTotalLength(17);
					deleteGroup.setType(IMsgConstance.command_deleteGroup);
					deleteGroup.setSrc(jkuser.getJknum());
					deleteGroup.setDest(IMsgConstance.Server_JK_NUMBER);
					deleteGroup.setGid(group.getGid());

					try {
						conn.sendMsg(deleteGroup);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		});

	}

	/**
	 * 处理客户端接收到一条消息时的动作,被主界面窗体对象调用 1.登陆成功后，接收到好友分组 2.添加一个好友成功 3.接到聊天消息
	 * 4.接到文件传送消息
	 * 
	 * @param m
	 *            :接收到的消息对象
	 */
	public void onMsgRecive(MsgHead m) {
		if (m.getType() == IMsgConstance.command_chatText) {
			MsgChatText chatText = (MsgChatText) m;
			if (map.get(m.getSrc()) != null) {
				int index = map.get(m.getSrc());
				JFrame jf_send = jfList.get(index);
				JTextArea chatArea = jtList.get(index);
				jf_send.setExtendedState(JFrame.NORMAL);
				String sendTime = chatText.getSendTime();
				sendTime = sendTime.substring(11);
				int len1 = sendTime.getBytes().length;
				String space1 = "";
				for (int i = 0; i < 3; i++) {
					space1 += "  ";
				}
				String txt = chatText.getCharTxt();
				int len2 = txt.getBytes().length;
				String space2 = "";
				for (int i = 0; i < 3; i++) {
					space2 += "  ";
				}
				chatArea.setText(chatArea.getText() + "\n" + space1 + sendTime
						+ "\n" + space2 + txt + "\n");
			} else {
				// 首先获得发送方的基本信息
				int jknum = chatText.getSrc();
				Jkuser src = null;
				ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) jkuser
						.getGroupList();
				boolean flag = false;
				for (int i = 0; i < gList.size(); i++) {
					Jkgroup jkgroup = gList.get(i);
					ArrayList<Jkuser> uList = (ArrayList<Jkuser>) jkgroup
							.getUserList();
					for (int j = 0; j < uList.size(); j++) {
						if (uList.get(j).getJknum() == jknum) {
							flag = true;
							src = uList.get(j);
							break;
						}
					}
					if (flag)
						break;
				}
				showSendMsgUI(src);
				jfList.get(map.get(jknum)).setExtendedState(JFrame.NORMAL);
				String sendTime = chatText.getSendTime();
				sendTime = sendTime.substring(11);
				int len1 = sendTime.getBytes().length;
				String space1 = "";
				for (int i = 0; i < 3; i++) {
					space1 += "  ";
				}
				String txt = chatText.getCharTxt();
				int len2 = txt.getBytes().length;
				String space2 = "";
				for (int i = 0; i < 3; i++) {
					space2 += "  ";
				}
				jtList.get(map.get(jknum)).setText(
						space1 + sendTime + "\n" + space2 + txt + "\n");
			}
		} else if (m.getType() == IMsgConstance.command_onLine) {
			int onNum = m.getSrc();
			OnLineInfoUtil infoUtil = new OnLineInfoUtil();
			infoUtil.show("QQ好友上线提醒", "您的好友 " + onNum + " 已经上线");
			updateUserState(onNum, 1);
			go();
		} else if (m.getType() == IMsgConstance.command_offLine) {
			int offNum = m.getSrc();
			OnLineInfoUtil infoUtil = new OnLineInfoUtil();
			infoUtil.show("QQ好友离线提醒", "您的好友 " + offNum + " 已经离线");
			updateUserState(offNum, 2);
			go();
		} else if (m.getType() == IMsgConstance.command_chatFile) {
			MsgChatFile chatFile = (MsgChatFile) m;
			int srcNum = chatFile.getSrc();
//			if (sendFrameMap.get(srcNum) == null) {
			if(map.get(srcNum) == null) {
				Jkuser srcUser = getUserByJknum(srcNum);
				showSendMsgUI(srcUser);
				String sendTime = chatFile.getSendTime();
				sendTime = srcUser.getName() + "(" + srcUser.getJknum() + ")"
						+ sendTime.substring(11);
				int len1 = sendTime.getBytes().length;
				String space1 = "";
				for (int i = 0; i < 3; i++) {
					space1 += "  ";
				}
				String txt = "发来文件 " + chatFile.getFileName();
				int len2 = txt.getBytes().length;
				String space2 = "";
				for (int i = 0; i < 3; i++) {
					space2 += "  ";
				}
				jtList.get(map.get(srcNum)).setText(
						space1 + sendTime + "\n" + space2 + txt + "\n");
				String name = chatFile.getFileName();
				int n = JOptionPane.showConfirmDialog(
						jfList.get(map.get(srcUser.getJknum())), "是否接收"
								+ chatFile.getSrc() + "传来的文件" + name);
				if (n == JOptionPane.YES_OPTION) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					File file = new File("G:/");
					chooser.setCurrentDirectory(file);
					int num = chooser.showSaveDialog(chooser);
					if (num == chooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().toString()
								+ "/" + chatFile.getFileName();
						BufferedOutputStream bous = null;
						try {
							bous = new BufferedOutputStream(
									new FileOutputStream(path));
							bous.write(chatFile.getFileData());
							bous.flush();
							System.out.println(chatFile.getFileData());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (bous != null) {
								try {
									bous.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				Jkuser srcUser = getUserByJknum(srcNum);
				String sendTime = chatFile.getSendTime();
				sendTime = srcUser.getName() + "(" + srcUser.getJknum() + ")"
						+ sendTime.substring(11);
				int len1 = sendTime.getBytes().length;
				String space1 = "";
				for (int i = 0; i < 3; i++) {
					space1 += "  ";
				}
				String txt = "发来文件 " + chatFile.getFileName();
				int len2 = txt.getBytes().length;
				String space2 = "";
				for (int i = 0; i < 3; i++) {
					space2 += "  ";
				}
				jtList.get(map.get(srcNum)).setText(
						space1 + sendTime + "\n" + space2 + txt + "\n");
				String name = chatFile.getFileName();
				int n = JOptionPane.showConfirmDialog(
						jfList.get(map.get(srcUser.getJknum())), "是否接收"
								+ chatFile.getSrc() + "传来的文件" + name);
				if (n == JOptionPane.YES_OPTION) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					File file = new File("G:/");
					chooser.setCurrentDirectory(file);
					int num = chooser.showSaveDialog(chooser);
					if (num == chooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().toString()
								+ "/" + chatFile.getFileName();
						BufferedOutputStream bous = null;
						try {
							bous = new BufferedOutputStream(
									new FileOutputStream(path));
							bous.write(chatFile.getFileData());
							bous.flush();
							System.out.println(chatFile.getFileData());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (bous != null) {
								try {
									bous.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据jknum获得某个好友的基本信息
	 * 
	 * @param srcNum
	 * @return
	 */
	private Jkuser getUserByJknum(int srcNum) {
		Jkuser u = null;
		boolean flag = true;
		ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) jkuser.getGroupList();
		for (int i = 0; i < gList.size(); i++) {
			ArrayList<Jkuser> uList = (ArrayList<Jkuser>) gList.get(i)
					.getUserList();
			for (int j = 0; j < uList.size(); j++) {
				if (uList.get(j).getJknum() == srcNum) {
					u = uList.get(j);
					flag = false;
					break;
				}
			}
			if (!flag)
				break;
		}
		return u;
	}

	/**
	 * 更新某个用户的状态
	 * 
	 * @param jknum
	 * @param state
	 */
	private void updateUserState(int jknum, int state) {
		ArrayList<Jkgroup> glist = (ArrayList<Jkgroup>) jkuser.getGroupList();
		boolean flag = false;
		for (int i = 0; i < glist.size(); i++) {
			ArrayList<Jkuser> ulist = (ArrayList<Jkuser>) glist.get(i)
					.getUserList();
			for (int j = 0; j < ulist.size(); j++) {
				Jkuser user = ulist.get(j);
				if (user.getJknum() == jknum) {
					user.setState(state);
					flag = true;
					break;
				}
			}
			if (flag)
				break;
		}
	}

	/* 双击树上的好友节点时，弹出消息发送框 */
	private void showSendFrame() {
		// 得到树上选中的节点:
		TreePath tp = this.getSelectionPath();
		if (tp == null) {// 未选中树节点
			return;
		}
		Object obj = tp.getLastPathComponent();// 取得选中的节点
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
		userO = node.getUserObject();// 取得节点内的对象
		if ((userO instanceof NodeData)
				&& (((NodeData) userO).nodeType == 2 || ((NodeData) userO).nodeType == 4)) {// 选中的是一个用户节点对象
			NodeData data = (NodeData) userO;
			final Jkuser destUser = (Jkuser) data.value;
			// 弹出发送消息框
			if (sendFrameMap.get(destUser.getJknum()) == null) {
				showSendMsgUI(destUser);
				sendFrameMap.put(destUser.getJknum(), 1);
			}
		}
	}

	void showSendMsgUI(final Jkuser destUser) {
		final JFrame jf_send = new JFrame("QQ聊天窗口");
		jf_send.setLayout(null);
		jf_send.setBounds(0, 0, 380, 480);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/logo.jpg");
		Image logo = icon.getImage();
		jf_send.setIconImage(logo);
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/send_bg.jpg");
		JLabel jl_bg = new JLabel(bg_icon);
		jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) jf_send.getContentPane();
		imgPanel.setOpaque(false);
		jf_send.getLayeredPane().setLayout(null);
		jf_send.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);
		// 去掉标题栏目
		jf_send.setUndecorated(true);
		jf_send.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		// 用到的组件的声明
		JLabel header;
		final JLabel min;
		final JLabel close;
		JLabel name;
		JLabel star;
		JLabel signature;
		JLabel fileUpload;
		JLabel distance_control;
		final JTextArea chatArea;
		JScrollPane chatPane;
		JLabel font;
		JLabel face;
		final JTextArea sendArea;
		JScrollPane sendPane;
		JButton close_btn;
		JButton send_btn;

		// 组件的实例化
		header = new JLabel();
		ImageIcon headerIcon = new ImageIcon(
				ImageUtil.getBytesFromFile(destUser.getIconpath()));
		header.setIcon(headerIcon);
		header.setBounds(20, 20, 60, 60);
		min = new JLabel();
		close = new JLabel();
		final ImageIcon minIcon = new ImageIcon("./images/min.png");
		final ImageIcon minIcon2 = new ImageIcon("./images/min1.png");
		min.setIcon(minIcon);
		min.setBounds(340, 10, 10, 10);
		final ImageIcon closeIcon = new ImageIcon("./images/close.png");
		final ImageIcon closeIcon2 = new ImageIcon("./images/close1.png");
		close.setIcon(closeIcon);
		close.setBounds(360, 10, 10, 10);
		name = new JLabel(destUser.getName() + "(" + destUser.getJknum() + ")");
		name.setFont(new Font("宋体", Font.PLAIN, 16));
		name.setBounds(110, 25, 200, 30);
		star = new JLabel();
		ImageIcon starIcon = new ImageIcon("./images/star.jpg");
		star.setIcon(starIcon);
		star.setBounds(110, 55, 30, 30);
		signature = new JLabel(destUser.getSignature());
		signature.setFont(new Font("黑体", Font.PLAIN, 14));
		signature.setBounds(150, 55, 200, 30);
		fileUpload = new JLabel();
		ImageIcon fileIcon = new ImageIcon("./images/file_upload.png");
		fileUpload.setIcon(fileIcon);
		fileUpload.setBounds(90, 325, 30, 25);
		distance_control = new JLabel();
		ImageIcon distanceIcon = new ImageIcon("./images/distance_control.png");
		distance_control.setIcon(distanceIcon);
		distance_control.setBounds(125, 325, 30, 25);
		chatArea = new JTextArea();
		chatPane = new JScrollPane(chatArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatArea.setOpaque(false);
		chatPane.setBounds(20, 95, 340, 225);
		chatArea.setEditable(false);
		font = new JLabel();
		ImageIcon fontIcon = new ImageIcon("./images/font.png");
		font.setIcon(fontIcon);
		font.setBounds(20, 325, 30, 25);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.gray));
		font.setBorder(BorderFactory.createLineBorder(Color.blue));
		face = new JLabel();
		ImageIcon faceIcon = new ImageIcon("./images/face.png");
		face.setIcon(faceIcon);
		face.setBounds(55, 325, 30, 25);
		distance_control.setBorder(BorderFactory.createLineBorder(Color.blue));
		fileUpload.setBorder(BorderFactory.createLineBorder(Color.blue));
		face.setBorder(BorderFactory.createLineBorder(Color.blue));
		sendArea = new JTextArea();
		sendPane = new JScrollPane(sendArea);
		sendPane.setBounds(20, 360, 340, 80);
		sendPane.setBorder(BorderFactory.createLineBorder(Color.gray));
		sendArea.setOpaque(false);
		close_btn = new JButton("关闭");
		close_btn.setBounds(245, 450, 60, 25);
		send_btn = new JButton("发送");
		send_btn.setBounds(310, 450, 60, 25);

		int index = jfList.size();
		jfList.add(jf_send);
		jtList.add(chatArea);
		// 把目标用户jknum号码和index通过map绑定起来
		map.put(destUser.getJknum(), index);

		// 组件的添加
		jf_send.add(header);
		jf_send.add(min);
		jf_send.add(close);
		jf_send.add(name);
		jf_send.add(star);
		jf_send.add(signature);
		jf_send.add(fileUpload);
		jf_send.add(distance_control);
		jf_send.add(chatPane);
		jf_send.add(font);
		jf_send.add(face);
		jf_send.add(sendPane);
		jf_send.add(close_btn);
		jf_send.add(send_btn);
		// 事件的监听
		min.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				jf_send.setExtendedState(JFrame.ICONIFIED);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				min.setIcon(minIcon2);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				min.setIcon(minIcon);
			}

		});

		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendFrameMap.remove(destUser.getJknum());
				jf_send.dispose();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				close.setIcon(closeIcon2);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				close.setIcon(closeIcon);
			}
		});

		close_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jf_send.dispose();
				// 同时把集合里的jframe和jtextarea移除 并且消除对应的mapping映射
				jfList.remove(jf_send);
				jtList.remove(chatArea);
				map.remove(destUser.getJknum());
				sendFrameMap.remove(destUser.getJknum());
			}
		});

		send_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String txt = sendArea.getText().trim();
				if (txt == null || txt.equals("")) {
					JOptionPane.showMessageDialog(jf_send, "不能发送空消息");
					return;
				}
				Date date = new Date(System.currentTimeMillis());
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String sendTime = format.format(date);
				int state = conn.sendMsg2One(jkuser.getJknum(),
						destUser.getJknum(), txt, sendTime);
				if (state == 1) {
					String appendStr = sendTime.substring(11);
					String space1 = "";
					int len1 = appendStr.getBytes().length;
					for (int i = 5; i < 56 - len1; i++) {
						space1 += "  ";
					}
					String appendStr2 = txt;
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int i = 6; i < 56 - len2; i++) {
						space2 += "  ";
					}
					chatArea.setText(chatArea.getText() + "\n" + space1
							+ appendStr + "\n" + space2 + appendStr2 + "\n");
					sendArea.setText("");
				}
			}
		});

		jf_send.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
			}
		});

		jf_send.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point p1 = e.getPoint();
				Point p2 = jf_send.getLocation();
				p2.x += p1.x - p.x;
				p2.y += p1.y - p.y;
				jf_send.setLocation(p2);
			}
		});

		fileUpload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(jf_send);
				File file = chooser.getSelectedFile();
				Date date = new Date(System.currentTimeMillis());
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String sendTime = format.format(date);

				String appendStr = sendTime.substring(11);
				String space1 = "";
				int len1 = appendStr.getBytes().length;
				for (int i = 5; i < 56 - len1; i++) {
					space1 += "  ";
				}
				String appendStr2 = "您成功发送文件" + file.getName();
				String space2 = "";
				int len2 = appendStr2.getBytes().length;
				for (int i = 6; i < 56 - len2; i++) {
					space2 += "  ";
				}
				chatArea.setText(chatArea.getText() + "\n" + space1 + appendStr
						+ "\n" + space2 + appendStr2 + "\n");

				// 向服务器端发送文件
				MsgChatFile chatFile = new MsgChatFile();
				chatFile.setTotalLength((int) file.length() + 294);
				chatFile.setSrc(jkuser.getJknum());
				chatFile.setDest(destUser.getJknum());
				chatFile.setSendTime(sendTime);
				chatFile.setType(IMsgConstance.command_chatFile);
				chatFile.setFileName(file.getName());
				chatFile.setFileData(ImageUtil.getBytesFromFile(file));
				try {
					conn.sendMsg(chatFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});

		jf_send.setLocationRelativeTo(null);
		jf_send.setResizable(false);
		jf_send.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf_send.setVisible(true);
	}
}
