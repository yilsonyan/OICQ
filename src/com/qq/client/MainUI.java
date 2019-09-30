package com.qq.client;

import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.qq.dao.GroupDaoImpl;
import com.qq.dao.JkuserDaoImpl;
import com.qq.model.ChatLog;
import com.qq.model.CommuApply;
import com.qq.model.CommuApplyResp;
import com.qq.model.CommuChatLog;
import com.qq.model.Community;
import com.qq.model.FriendApply;
import com.qq.model.FriendApplyResp;
import com.qq.model.Jkfile;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgAddCommunity;
import com.qq.msg.MsgAddCommunityResp;
import com.qq.msg.MsgAddFriendResp;
import com.qq.msg.MsgAddGroup;
import com.qq.msg.MsgAddGroupResp;
import com.qq.msg.MsgCommuChatFile;
import com.qq.msg.MsgCreateCommunity;
import com.qq.msg.MsgCreateCommunityResp;
import com.qq.msg.MsgDeleteCommunityResp;
import com.qq.msg.MsgDeleteFriendResp;
import com.qq.msg.MsgDeleteGroupResp;
import com.qq.msg.MsgFind;
import com.qq.msg.MsgFindResp;
import com.qq.msg.MsgHead;
import com.qq.msg.MsgHeaderUpload;
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.util.ImageUtil;
import com.qq.util.OffMsgInfoUtil;
import com.qq.util.OpenTree;

/**
 * QQ通讯系统 客户端主界面 1.主界面对象是通信连结器的一个消息监听器它实现了
 * IClientMsgListener接口,当有消息到达时，主界面对象负责处理消息 2.主界面通过树形组件，显示用户的好友表
 * 3.通过菜单，提供查找好友的功能 查找群的功能
 */
public class MainUI extends JFrame implements IClientMsgListener {

	private Jkuser jkuser; // 用户对象
	private UserTree userTree;// 好友列表树
	private boolean nav1_state = true;
	private boolean nav2_state;
	JkuserDaoImpl daoImpl = new JkuserDaoImpl();
	private CommunityTree communityTree; // 群列表树
	private boolean flag = true; // true 表示当前是好友列表
	JLabel iconHeader = null; // 头像
	Label uname = null; // 昵称
	JLabel signature = null; // 个性签名
	JFrame jf_search = null;
	JComboBox classify_combo = null;
	JTextField t1 = null;
	File file = null;
	JFrame jf_edit = null;
	JLabel jlt3 = null;
	private MainUI mainUI = null;
	// 取得连结器的单实例对象
	private ClientConnection conn = ClientConnection.getIns();
	private String input = null;
	// 创建主界面对象时，传入对应的jk号，
	// 并创建树对象，加到界面上
	public MainUI(Jkuser jkuser) {
		this.jkuser = jkuser;
		userTree = new UserTree(jkuser);
		communityTree = new CommunityTree(jkuser);
		mainUI = this;
	}

	public Jkuser getJkuser() {
		return jkuser;
	}

	// 显示主界面
	public void showMainUI() throws FileNotFoundException {
		this.setTitle("QQ");
		this.setLayout(null);
		this.setSize(300, 680);

		ImageIcon titleIcon = new ImageIcon("./images/logo.jpg");
		this.setIconImage(titleIcon.getImage());

		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/mainbg.jpg");
		JLabel mainbg = new JLabel(bg_icon);
		mainbg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		final JPanel imgPanel = (JPanel) this.getContentPane();
		imgPanel.setOpaque(false);
		this.getLayeredPane().setLayout(null);
		this.getLayeredPane().add(mainbg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		if (jkuser.getIconpath() != null) {

			iconHeader = new JLabel();
			ImageIcon imageIcon = new ImageIcon(
					ImageUtil.getBytesFromFile(jkuser.getIconpath()));
			iconHeader.setIcon(imageIcon);
			iconHeader.setBounds(40, 20, 60, 60);
			this.add(iconHeader);
		}

		// 名字
		uname = new Label(jkuser.getName());
		uname.setBounds(110, 20, 165, 20);
		uname.setFont(new Font("微软雅正", Font.LAYOUT_NO_LIMIT_CONTEXT, 16));

		// 签名
		String append = (jkuser.getSignature() == null) ? "" : jkuser
				.getSignature();
		signature = new JLabel("签名:" + append);
		signature.setBounds(110, 60, 165, 20);

		// 添加导航图片

		final JLabel friend = new JLabel();
		ImageIcon friendIcon = new ImageIcon("./images/friend_now.png");
		friend.setOpaque(false);
		friend.setIcon(friendIcon);
		friend.setBounds(65, 100, 55, 40);

		final JLabel community = new JLabel();
		ImageIcon communityIcon = new ImageIcon("./images/community.png");
		community.setOpaque(false);
		community.setIcon(communityIcon);
		community.setBounds(155, 100, 55, 40);

		final JLabel search = new JLabel();
		ImageIcon searchIcon = new ImageIcon("./images/search.png");
		search.setIcon(searchIcon);
		search.setBounds(235, 100, 32, 32);

		userTree.setBounds(0, 150, 300, 450);
		communityTree.setBounds(0, 150, 300, 450);

		// 添加各个组件
		this.add(uname);
		this.add(friend);
		this.add(community);
		imgPanel.add(userTree);
		this.add(signature);
		imgPanel.add(communityTree);
		communityTree.setVisible(false);
		this.add(search);
		// 监听事件的添加
		uname.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showEditUI();
				}
			}
		});

		search.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showSearchUI();
			}
		});

		friend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!nav1_state) {
					ImageIcon friendIcon2 = new ImageIcon(
							"./images/friend_now.png");
					friend.setIcon(friendIcon2);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!nav1_state) {
					ImageIcon friendIcon2 = new ImageIcon("./images/friend.png");
					friend.setIcon(friendIcon2);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!nav1_state) {
					ImageIcon friendIcon2 = new ImageIcon(
							"./images/friend_now.png");
					friend.setIcon(friendIcon2);
					nav1_state = true;
					nav2_state = false;
					ImageIcon friendIcon3 = new ImageIcon(
							"./images/community.png");
					community.setIcon(friendIcon3);
				}

				if (!flag) {
					communityTree.setVisible(false);
					userTree.setVisible(true);
					flag = true;
				}
			}
		});

		community.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!nav2_state) {
					ImageIcon friendIcon2 = new ImageIcon(
							"./images/community_now.png");
					community.setIcon(friendIcon2);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!nav2_state) {
					ImageIcon friendIcon2 = new ImageIcon(
							"./images/community.png");
					community.setIcon(friendIcon2);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!nav2_state) {
					ImageIcon friendIcon2 = new ImageIcon(
							"./images/community_now.png");
					community.setIcon(friendIcon2);
					nav2_state = true;
					nav1_state = false;
					ImageIcon friendIcon3 = new ImageIcon("./images/friend.png");
					friend.setIcon(friendIcon3);

				}

				if (flag) {
					flag = false;
					userTree.setVisible(false);
					communityTree.setVisible(true);
				}

			}
		});

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// 更新登陆状态文件
				File loginstate = new File("F:/QQmsg/loginstate.dat");
				try {
					ObjectInputStream oins = new ObjectInputStream(
							new FileInputStream(loginstate));
					ArrayList<Integer> list = (ArrayList<Integer>) oins
							.readObject();
					list.remove((Integer) jkuser.getJknum());
					// 重新写回去
					ObjectOutputStream oos = new ObjectOutputStream(
							new FileOutputStream(loginstate));
					oos.writeObject(list);
					oos.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				// 更新数据库的用户状态为不在线
				JkuserDaoImpl userImpl = new JkuserDaoImpl();
				userImpl.offOnline(jkuser.getJknum());

			}
		});

		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
	
		
		/**
		 * 检查一下是否有离线信息
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				final ArrayList<ChatLog> logList = (ArrayList<ChatLog>) jkuser
						.getLogList();
				final ArrayList<CommuChatLog> cmuChatLog = (ArrayList<CommuChatLog>) jkuser
						.getCmuChatLogList();
				ArrayList<Jkfile> fileList = (ArrayList<Jkfile>) jkuser
						.getFileList();
				if ((fileList != null && fileList.size() > 0)
						|| (logList != null && logList.size() > 0)
						|| (cmuChatLog != null && cmuChatLog.size() > 0)) {
					final OffMsgInfoUtil infoUtil = new OffMsgInfoUtil(logList,
							cmuChatLog, communityTree, fileList);
					infoUtil.show("QQ离线消息提醒", "您有未处理的好友消息共"
							+ (logList.size() + fileList.size()) + "条,"
							+ "有未处理的群消息" + cmuChatLog.size() + "条");
				}
			}
		}).start();

		/**
		 * 检查是否有未处理的群文件
		 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<Integer, Integer> hasNewFile = jkuser.getHasNewFile();
				Set<Integer> set = hasNewFile.keySet();
				Iterator<Integer> iterator = set.iterator();
				while(iterator.hasNext()) {
					int cid = iterator.next();
					if(hasNewFile.get(cid) == 1) {
						MsgCommuChatFile m = new MsgCommuChatFile();
						m.setType(IMsgConstance.command_commuChatFile);
						m.setSrc(0);
						m.setDestCid(cid);
						communityTree.onMsgRecive(m);
					}
				}
			}
		}).start();
		
		/**
		 * 检查是否有未处理的消息 好友申请 群申请 申请回应等
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<FriendApply> applies = (ArrayList<FriendApply>) jkuser
						.getApplyList();
				if (applies != null) {
					for (int i = 0; i < applies.size(); i++) {
						FriendApply apply = applies.get(i);
						int n = JOptionPane.showConfirmDialog(userTree,
								apply.getSrcid() + " 请求添加您为好友,您是否同意?");
						MsgAddFriendResp addFriendResp = new MsgAddFriendResp();
						addFriendResp.setTotalLength(14);
						addFriendResp.setSrc(apply.getDestid());
						addFriendResp.setDest(apply.getSrcid());
						addFriendResp
								.setType(IMsgConstance.command_addFriend_resp);
						if (n == JOptionPane.YES_OPTION) {
							addFriendResp.setRes((byte) 1);

							int num = JOptionPane.showConfirmDialog(userTree,
									"是否将其保存到默认分组");
							if (num == JOptionPane.YES_OPTION) {
								Jkuser u = daoImpl.getBasicInfo(apply
										.getSrcid());
								addFriendToDefaultGroup(u);
							} else {
								Jkuser u = daoImpl.getBasicInfo(apply
										.getSrcid());
								addFriendsToGroup(u);
							}

						} else {
							addFriendResp.setRes((byte) 0);
						}
						try {
							conn.sendMsg(addFriendResp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<CommuApplyResp> applyResps2 = (ArrayList<CommuApplyResp>) jkuser
						.getCmuApplyRespList();
				if (applyResps2 != null) {
					for (int i = 0; i < applyResps2.size(); i++) {
						CommuApplyResp applyResp = applyResps2.get(i);
						if (applyResp.getRes() == 1) {
							JOptionPane.showMessageDialog(mainUI, "您通过了群"
									+ applyResp.getCid() + "的入群申请");

							ArrayList<Community> cList = (ArrayList<Community>) jkuser
									.getCommuList();
							Community community = jkuser.getCmuList().get(i);
							community.getUserList().add(jkuser);
							cList.add(community);

							mainUI.remove(communityTree);
							communityTree = new CommunityTree(jkuser);
							communityTree.setBounds(0, 150, 300, 450);
							mainUI.add(communityTree);
						} else {
							JOptionPane.showMessageDialog(mainUI, "群"
									+ applyResp.getCid() + "的负责人拒绝了您的入群申请");
						}
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<FriendApplyResp> applyResps = (ArrayList<FriendApplyResp>) jkuser
						.getApplyRespList();
				if (applyResps != null) {
					for (int i = 0; i < applyResps.size(); i++) {
						FriendApplyResp applyResp = applyResps.get(i);
						if (applyResp.getRes() == 1) {
							int n = JOptionPane.showConfirmDialog(userTree,
									applyResp.getSrcid() + " 同意添加您为好友 请选择分组");
							if (n == JOptionPane.YES_OPTION) {
								Jkuser u = daoImpl.getBasicInfo(applyResp
										.getSrcid());
								addFriendsToGroup(u);
							} else {
								Jkuser u = daoImpl.getBasicInfo(applyResp
										.getSrcid());
								addFriendToDefaultGroup(u);
							}
						} else {
							JOptionPane.showMessageDialog(userTree,
									applyResp.getSrcid() + " 拒绝添加您为好友");
						}
					}
				}

			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 处理入群申请和入群申请回应
				ArrayList<CommuApply> cmuApplyList = (ArrayList<CommuApply>) jkuser
						.getCmuApplyList();
				if (cmuApplyList != null) {
					for (int i = 0; i < cmuApplyList.size(); i++) {
						CommuApply apply = cmuApplyList.get(i);
						int n = JOptionPane.showConfirmDialog(mainUI,
								apply.getSrcid() + "申请加入群" + apply.getCid()
										+ "  是否同意?");
						MsgAddCommunityResp addCommunityResp = new MsgAddCommunityResp();
						addCommunityResp.setTotalLength(21);
						addCommunityResp.setSrc(apply.getDestid());
						addCommunityResp.setDest(apply.getSrcid());
						addCommunityResp
								.setType(IMsgConstance.command_addCommunity_resp);
						addCommunityResp.setDestcid(apply.getCid());

						if (n == JOptionPane.YES_OPTION) {
							addCommunityResp.setRes(1);
							// 修改jkuser的值
							ArrayList<Community> cmuList = (ArrayList<Community>) jkuser
									.getCommuList();
							for (int j = 0; j < cmuList.size(); j++) {
								if (cmuList.get(j).getCid() == apply.getCid()) {
									ArrayList<Jkuser> ul = (ArrayList<Jkuser>) cmuList
											.get(j).getUserList();
									ul.add(jkuser.getuList().get(i));
									break;
								}
							}

							mainUI.remove(communityTree);
							communityTree = new CommunityTree(jkuser);
							communityTree.setBounds(0, 150, 300, 450);
							mainUI.add(communityTree);
							addCommunityResp.setRes(1);
						} else {
							addCommunityResp.setRes(0);
						}
						try {
							conn.sendMsg(addCommunityResp);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		}).start();

	}

	/**
	 * 显示搜索界面
	 */
	protected void showSearchUI() {
		jf_search = new JFrame("QQ搜索");
		jf_search.setLayout(null);
		jf_search.setBounds(0, 0, 300, 200);

		ImageIcon titleIcon = new ImageIcon("./images/logo.jpg");
		jf_search.setIconImage(titleIcon.getImage());

		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/mainbg.jpg");
		JLabel mainbg = new JLabel(bg_icon);
		mainbg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		final JPanel imgPanel = (JPanel) jf_search.getContentPane();
		imgPanel.setOpaque(false);
		jf_search.getLayeredPane().setLayout(null);
		jf_search.getLayeredPane().add(mainbg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		JLabel classify_label = new JLabel("请选择查找类别:");
		classify_label.setBounds(20, 20, 100, 30);
		classify_combo = new JComboBox();
		classify_combo.addItem("QQ好友");
		classify_combo.addItem("QQ群");
		classify_combo.setBounds(130, 20, 100, 30);
		JLabel l1 = new JLabel("请输入查询号码:");
		l1.setBounds(20, 60, 100, 30);
		t1 = new JTextField();
		t1.setBounds(130, 60, 100, 30);

		JButton search_btn = new JButton("搜索");
		search_btn.setBounds(110, 110, 75, 30);

		// 组件的添加
		jf_search.add(classify_combo);
		jf_search.add(classify_label);
		jf_search.add(l1);
		jf_search.add(t1);
		jf_search.add(search_btn);

		search_btn.setActionCommand("search");
		// search按钮 点击事件
		search_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = t1.getText().trim();
				int num;
				byte classify = 0;
				try {
					num = Integer.parseInt(str);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(jf_search, "您输入的号码格式有误");
					t1.setText("");
					return;
				}
				classify = (byte) (classify_combo.getSelectedIndex() + 1);

				try {
					MsgFind find = new MsgFind();
					find.setTotalLength(18);
					find.setSrc(0);
					find.setDest(IMsgConstance.Server_JK_NUMBER);
					find.setType(IMsgConstance.command_find);
					find.setClassify(classify);
					find.setFindId(num);
					conn.sendMsg(find);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		jf_search.setLocationRelativeTo(null);
		jf_search.setVisible(true);
		jf_search.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * 显示查找结果
	 */
	protected void showFindResultUI(Object object) {
		final JFrame jf_result = new JFrame("QQ查找结果");
		jf_result.setLayout(null);
		jf_result.setBounds(0, 0, 300, 310);

		ImageIcon titleIcon = new ImageIcon("./images/logo.jpg");
		jf_result.setIconImage(titleIcon.getImage());

		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/mainbg.jpg");
		JLabel mainbg = new JLabel(bg_icon);
		mainbg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		final JPanel imgPanel = (JPanel) jf_result.getContentPane();
		imgPanel.setOpaque(false);
		jf_result.getLayeredPane().setLayout(null);
		jf_result.getLayeredPane().add(mainbg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		if (object instanceof Jkuser) {
			final Jkuser user = (Jkuser) object;
			JLabel l1 = new JLabel("查找结果类型:");
			l1.setBounds(20, 20, 100, 30);
			JLabel l2 = new JLabel("QQ用户(" + user.getJknum() + ")");
			l2.setBounds(130, 20, 100, 30);
			JLabel l3 = new JLabel("头像:");
			l3.setBounds(20, 65, 100, 30);
			ImageIcon header = new ImageIcon(ImageUtil.getBytesFromFile(user
					.getIconpath()));
			JLabel l4 = new JLabel();
			l4.setIcon(header);
			l4.setBounds(130, 65, 60, 60);
			JLabel l5 = new JLabel("昵称");
			l5.setBounds(20, 140, 100, 30);
			JLabel l6 = new JLabel(user.getName());
			l6.setBounds(130, 140, 100, 30);
			JLabel l7 = new JLabel("性别");
			l7.setBounds(20, 185, 100, 30);
			String sex = (user.getSex() == 1) ? "男" : "女";
			JLabel l8 = new JLabel(sex);
			l8.setBounds(130, 185, 100, 30);
			JButton btn = new JButton("添加好友");
			btn.setBounds(95, 235, 110, 30);

			// 添加组件
			jf_result.add(l1);
			jf_result.add(l2);
			jf_result.add(l3);
			jf_result.add(l4);
			jf_result.add(l5);
			jf_result.add(l6);
			jf_result.add(l7);
			jf_result.add(l8);
			jf_result.add(btn);

			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					MsgHead head = new MsgHead();
					head.setTotalLength(13);
					head.setSrc(jkuser.getJknum());
					head.setDest(user.getJknum());
					head.setType(IMsgConstance.command_addFriend);
					// 先检查是否已有该好友
					boolean flag = false;
					ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) jkuser
							.getGroupList();
					for (int i = 0; i < gList.size(); i++) {
						ArrayList<Jkuser> uList = (ArrayList<Jkuser>) gList
								.get(i).getUserList();
						for (int j = 0; j < uList.size(); j++) {
							if (uList.get(j).getJknum() == user.getJknum()) {
								flag = true;
								break;
							}
						}
						if (flag)
							break;
					}

					if (flag) {
						JOptionPane.showMessageDialog(jf_result,
								"您已添加该好友,无需重复添加!");
						return;
					} else {
						// 发动消息到服务器端
						try {
							conn.sendMsg(head);
							JOptionPane.showMessageDialog(jf_result, "您已申请添加 "
									+ user.getName() + "(" + user.getJknum()
									+ ") 为好友,等待对方回复.");
							jf_result.dispose();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

				}
			});

		} else {
			final Community community = (Community) object;
			JLabel l1 = new JLabel("查找结果类型:");
			l1.setBounds(20, 20, 100, 30);
			JLabel l2 = new JLabel("QQ群(" + community.getCid() + ")");
			l2.setBounds(130, 20, 100, 30);
			JLabel l3 = new JLabel("头像:");
			l3.setBounds(20, 65, 100, 30);
			ImageIcon header = new ImageIcon(
					ImageUtil.getBytesFromFile(community.getIconpath()));
			JLabel l4 = new JLabel();
			l4.setIcon(header);
			l4.setBounds(130, 65, 60, 60);
			JLabel l5 = new JLabel("昵称");
			l5.setBounds(20, 140, 100, 30);
			JLabel l6 = new JLabel(community.getName());
			l6.setBounds(130, 140, 100, 30);
			JLabel l7 = new JLabel("简介");
			l7.setBounds(20, 185, 100, 30);
			JLabel l8 = new JLabel(community.getDes());
			l8.setBounds(130, 185, 100, 30);
			JButton btn = new JButton("申请进入");
			btn.setBounds(95, 235, 110, 30);

			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					// 看看是否已经添加该群
					ArrayList<Community> commuList = (ArrayList<Community>) jkuser
							.getCommuList();
					boolean flag = true;
					for (int i = 0; i < commuList.size(); i++) {
						if (commuList.get(i).getCid() == community.getCid()) {
							flag = false;
							break;
						}
					}

					if (!flag) {
						JOptionPane.showMessageDialog(jf_result,
								"你已经进入该群,请不要重复申请!");
						return;
					}

					MsgAddCommunity addCommunity = new MsgAddCommunity();
					addCommunity.setTotalLength(17);
					addCommunity.setType(IMsgConstance.command_addCommunity);
					addCommunity.setSrc(jkuser.getJknum());
					addCommunity.setDest(IMsgConstance.Server_JK_NUMBER);
					addCommunity.setDestCid(community.getCid());
					try {
						conn.sendMsg(addCommunity);
						JOptionPane.showMessageDialog(jf_result, "您已经申请加入群"
								+ community.getCid() + ",等待群主回复!");
						jf_result.dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			});

			// 添加组件
			jf_result.add(l1);
			jf_result.add(l2);
			jf_result.add(l3);
			jf_result.add(l4);
			jf_result.add(l5);
			jf_result.add(l6);
			jf_result.add(l7);
			jf_result.add(l8);
			jf_result.add(btn);
		}

		jf_result.setLocationRelativeTo(null);
		jf_result.setVisible(true);
		jf_result.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * 编辑个人信息的页面
	 */
	protected void showEditUI() {
		jf_edit = new JFrame("个人信息的编辑");
		jf_edit.setLayout(null);
		jf_edit.setBounds(0, 0, 300, 500);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/logo.jpg");
		Image logo = icon.getImage();
		jf_edit.setIconImage(logo);
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel jl_bg = new JLabel(bg_icon);
		jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) jf_edit.getContentPane();
		imgPanel.setOpaque(false);
		jf_edit.getLayeredPane().setLayout(null);
		jf_edit.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		// 用到的组件声明
		JButton confirm;
		JButton uploadImg;
		JLabel l1;
		JLabel l2;
		JLabel l3;
		JLabel l4;
		JLabel l5;
		JLabel l6;
		JLabel l7;
		JLabel l8;
		JButton l9;
		JTextField t1;
		final JTextField t2;
		final JTextArea t4;
		final JTextField t5;
		final JTextField t6;
		final JTextField t7;
		final JComboBox t8;
		final JFileChooser imgChoose;

		// 组件的实例化
		l1 = new JLabel("QQ号码:");
		t1 = new JTextField();
		t1.setText(jkuser.getJknum() + "");
		l1.setBounds(20, 20, 55, 30);
		t1.setBounds(80, 20, 130, 30);
		t1.setEditable(false);
		l2 = new JLabel("昵称:");
		t2 = new JTextField();
		l2.setBounds(20, 55, 55, 30);
		t2.setBounds(80, 55, 130, 30);
		t2.setText(jkuser.getName());
		l3 = new JLabel("头像:");
		jlt3 = new JLabel();
		l3.setBounds(20, 105, 55, 30);
		jlt3.setBounds(80, 90, 60, 60);
		ImageIcon header = new ImageIcon(ImageUtil.getBytesFromFile(jkuser
				.getIconpath()));
		jlt3.setIcon(header);
		l9 = new JButton("上传新头像");
		l9.setBounds(170, 105, 110, 30);
		imgChoose = new JFileChooser();
		l4 = new JLabel("个性签名:");
		l4.setBounds(20, 160, 55, 30);
		t4 = new JTextArea();
		t4.setBounds(80, 160, 130, 80);
		t4.setText(jkuser.getSignature());
		l5 = new JLabel("居住地:");
		l5.setBounds(20, 250, 55, 30);
		t5 = new JTextField();
		t5.setText((jkuser.getSite() == null || jkuser.getSite().equals("")) ? ""
				: jkuser.getSite());
		t5.setBounds(80, 250, 130, 30);
		l6 = new JLabel("电话:");
		l6.setBounds(20, 290, 55, 30);
		t6 = new JTextField();
		t6.setText((jkuser.getPhone() == null || jkuser.getPhone().equals("")) ? ""
				: jkuser.getPhone());
		t6.setBounds(80, 290, 130, 30);
		l7 = new JLabel("电子邮箱:");
		l7.setBounds(20, 330, 55, 30);
		t7 = new JTextField();
		t7.setText((jkuser.getEmail() == null || jkuser.getEmail().equals("")) ? ""
				: jkuser.getEmail());
		t7.setBounds(80, 330, 130, 30);
		l8 = new JLabel("性别");
		l8.setBounds(20, 370, 55, 30);
		t8 = new JComboBox();
		t8.addItem("男");
		t8.addItem("女");
		t8.setBounds(80, 370, 130, 30);
		int sex = jkuser.getSex();
		if (sex == 1) {
			t8.setSelectedIndex(0);
		} else {
			t8.setSelectedIndex(1);
		}
		confirm = new JButton("确认修改");
		confirm.setBounds(75, 425, 130, 45);

		// 组件的添加
		jf_edit.add(l1);
		jf_edit.add(t1);
		jf_edit.add(l2);
		jf_edit.add(t2);
		jf_edit.add(l3);
		jf_edit.add(jlt3);
		jf_edit.add(l9);
		jf_edit.add(l4);
		jf_edit.add(t4);
		jf_edit.add(l5);
		jf_edit.add(t5);
		jf_edit.add(l6);
		jf_edit.add(t6);
		jf_edit.add(l7);
		jf_edit.add(t7);
		jf_edit.add(l8);
		jf_edit.add(t8);
		jf_edit.add(confirm);

		// 事件的监听
		l9.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imgChoose.showOpenDialog(jf_edit);
				file = imgChoose.getSelectedFile();
				if (file == null) {
					return;
				}
				if (file.exists()) {
					String fileName = file.getName();
					String lastName = fileName.substring(fileName
							.lastIndexOf(".") + 1);
					if (!lastName.equals("jpg") && !lastName.equals("png")) {
						JOptionPane.showMessageDialog(jf_edit, "您选择的文件格式不合法!");
						return;
					}
					MsgHeaderUpload headerUpload = new MsgHeaderUpload();
					headerUpload.setTotalLength(13);
					headerUpload.setDest(IMsgConstance.Server_JK_NUMBER);
					headerUpload.setSrc(jkuser.getJknum());
					headerUpload.setType(IMsgConstance.command_headerupload);
					ObjectOutputStream oos = null;

					try {
						conn.sendMsg(headerUpload);
						// 传送过去上传请求之后 直接把文件序列化后传送到服务器端
						oos = new ObjectOutputStream(conn.getDous());
						oos.writeObject(file);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// 确认修改事件
		confirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = t2.getText();
				String sign = t4.getText();
				String site = t5.getText();
				String phone = t6.getText();
				String email = t7.getText();
				int sex = t8.getSelectedIndex() + 1;
				// 手机号码和邮箱的验证
				boolean flag1 = Pattern
						.matches(
								"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$",
								email);
				boolean flag2 = Pattern
						.matches(
								"1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}",
								phone);
				if (!flag2) {
					JOptionPane.showMessageDialog(jf_edit, "手机号码格式不正确,请正确输入!");
					t6.setText("");
					return;
				}

				if (!flag1) {
					JOptionPane.showMessageDialog(jf_edit, "邮箱格式不正确,请正确输入!");
					t7.setText("");
					return;
				}

				Jkuser user = new Jkuser();
				user.setJknum(jkuser.getJknum());
				user.setName(name);
				user.setSignature(sign);
				user.setSite(site);
				user.setPhone(phone);
				user.setEmail(email);
				user.setSex(sex);

				JkuserDaoImpl userImpl = new JkuserDaoImpl();
				int state = userImpl.updateUserInfo(user);
				if (state == 1) {
					uname.setText(t2.getText());
					signature.setText(t4.getText());
					JOptionPane.showMessageDialog(jf_edit, "信息修改成功");
					return;
				}

			}
		});

		jf_edit.setLocationRelativeTo(null);
		jf_edit.setResizable(false);
		jf_edit.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf_edit.setVisible(true);
	}

	/**
	 * 把好友添加到默认的分组
	 * 
	 * @param u
	 */
	private void addFriendToDefaultGroup(Jkuser u) {
		Jkgroup g = null;
		ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) jkuser.getGroupList();
		for (int i = 0; i < gList.size(); i++) {
			Jkgroup jkgroup = gList.get(i);
			if (jkgroup.getName().equals("我的好友")) {
				g = jkgroup;
				break;
			}
		}
		GroupDaoImpl daoImpl = new GroupDaoImpl();
		daoImpl.addFriends(g.getGid(), u.getJknum());
		ArrayList<Jkuser> ulist = (ArrayList<Jkuser>) g.getUserList();
		ulist.add(u);
		this.remove(userTree);
		userTree = new UserTree(jkuser);

		userTree.setBounds(0, 150, 300, 450);
		SwingUtilities.updateComponentTreeUI(this);
		this.add(userTree);
	}

	/**
	 * 好友添加到分组
	 * 
	 * @param u
	 */
	private void addFriendsToGroup(final Jkuser u) {
		final JFrame jf_add = new JFrame("分组选择");
		jf_add.setLayout(null);
		jf_add.setBounds(0, 0, 200, 280);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/logo.jpg");
		Image logo = icon.getImage();
		jf_add.setIconImage(logo);
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel jl_bg = new JLabel(bg_icon);
		jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) jf_add.getContentPane();
		imgPanel.setOpaque(false);
		jf_add.getLayeredPane().setLayout(null);
		jf_add.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		final java.awt.List list = new java.awt.List();
		JScrollPane pane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setBounds(0, 0, 200, 200);

		JButton btn = new JButton("确认选择");
		btn.setBounds(50, 210, 100, 30);

		final ArrayList<Jkgroup> gList = (ArrayList<Jkgroup>) jkuser
				.getGroupList();
		for (int i = 0; i < gList.size(); i++) {
			list.addItem(gList.get(i).getName());
		}
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				Jkgroup jkgroup = gList.get(index);
				ArrayList<Jkuser> uList = (ArrayList<Jkuser>) jkgroup
						.getUserList();
				uList.add(u);

				GroupDaoImpl daoImpl = new GroupDaoImpl();
				daoImpl.addFriends(jkgroup.getGid(), u.getJknum());

				mainUI.remove(userTree);
				userTree = new UserTree(jkuser);
				userTree.setBounds(0, 150, 300, 450);
				mainUI.add(userTree);
				SwingUtilities.updateComponentTreeUI(mainUI);
				jf_add.dispose();
			}
		});

		jf_add.add(pane);
		jf_add.add(btn);
		jf_add.setVisible(true);
		jf_add.setLocationRelativeTo(null);
		jf_add.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	// 实现通讯消息监听器接口中的方法，
	// 处理通讯层来的消息
	public void fireMsg(MsgHead m) {
		if (m.getType() == IMsgConstance.command_chatText
				|| m.getType() == IMsgConstance.command_offLine
				|| m.getType() == IMsgConstance.command_onLine
				|| m.getType() == IMsgConstance.command_chatFile) {
			userTree.onMsgRecive(m);
		} else if (m.getType() == IMsgConstance.command_find_resp) {
			MsgFindResp findResp = (MsgFindResp) m;
			if (findResp.getState() == 0) {
				JOptionPane.showMessageDialog(jf_search, "该号码不存在,请重新输入!");
				t1.setText("");
				return;
			} else {
				Object object = null;
				try {
					ObjectInputStream oins = new ObjectInputStream(
							conn.getDins());
					object = oins.readObject();
					jf_search.dispose();
					showFindResultUI(object);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else if (m.getType() == IMsgConstance.command_headerupload_resp) {
			MsgHeaderUploadResp headerUploadResp = (MsgHeaderUploadResp) m;
			if (headerUploadResp.getState() == 1) {
				BufferedImage bi = ImageUtil.compressImage(file, 60, 60);
				ImageIcon changedIcon = new ImageIcon(bi);
				jlt3.setIcon(changedIcon);
				iconHeader.setIcon(changedIcon);
				JOptionPane.showMessageDialog(jf_edit, "头像更改成功");
				
				File saveFile = new File("F:/QQmsg/user.dat");
				try {
					ObjectInputStream oins = new ObjectInputStream(new FileInputStream(saveFile));
					Jkuser u1 = (Jkuser) oins.readObject();
					u1.setIconpath(file);
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
					oos.writeObject(u1);
					oos.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
			}
		} else if (m.getType() == IMsgConstance.command_addFriend) {
			int n = JOptionPane.showConfirmDialog(this, m.getSrc()
					+ " 申请添加您为好友  是否同意?");
			MsgAddFriendResp addFriendResp = new MsgAddFriendResp();
			addFriendResp.setTotalLength(14);
			addFriendResp.setSrc(m.getDest());
			addFriendResp.setDest(m.getSrc());
			addFriendResp.setType(IMsgConstance.command_addFriend_resp);
			if (n == JOptionPane.YES_OPTION) {

				int num = JOptionPane.showConfirmDialog(this, "是否将其保存到默认分组");

				if (num == JOptionPane.YES_OPTION) {
					Jkuser u = daoImpl.getBasicInfo(m.getSrc());
					addFriendToDefaultGroup(u);
				} else {
					Jkuser u = daoImpl.getBasicInfo(m.getSrc());
					addFriendsToGroup(u);
				}

				addFriendResp.setRes((byte) 1);
			} else {
				addFriendResp.setRes((byte) 0);
			}
			try {
				conn.sendMsg(addFriendResp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (m.getType() == IMsgConstance.command_addFriend_resp) {
			MsgAddFriendResp addFriendResp = (MsgAddFriendResp) m;
			if (addFriendResp.getRes() == 1) {
				int n = JOptionPane.showConfirmDialog(this, m.getSrc()
						+ " 已经同意您的申请并且把您加为好友  请选择分组");
				if (n == JOptionPane.YES_OPTION) {
					Jkuser u = daoImpl.getBasicInfo(m.getSrc());
					addFriendsToGroup(u);
				} else {
					// 默认我的好友中
					Jkuser u = daoImpl.getBasicInfo(m.getSrc());
					addFriendToDefaultGroup(u);
				}

			} else {
				JOptionPane.showMessageDialog(this, m.getSrc() + " 拒绝把您添加为好友");
			}
		} else if (m.getType() == IMsgConstance.command_addCommunity) {
			MsgAddCommunity addCommunity = (MsgAddCommunity) m;
			int n = JOptionPane.showConfirmDialog(this, m.getSrc() + " 申请进入群"
					+ addCommunity.getDestCid() + "  是否同意?");
			Jkuser u = new Jkuser();
			try {
				ObjectInputStream oins = new ObjectInputStream(conn.getDins());
				u = (Jkuser) oins.readObject();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			MsgAddCommunityResp addCommunityResp = new MsgAddCommunityResp();
			addCommunityResp.setTotalLength(21);
			addCommunityResp.setDest(m.getSrc());
			addCommunityResp.setSrc(m.getDest());
			addCommunityResp.setDestcid(addCommunity.getDestCid());
			addCommunityResp.setType(IMsgConstance.command_addCommunity_resp);
			if (n == JOptionPane.YES_OPTION) {
				// 更新UI
				// 修改jkuser的值
				ArrayList<Community> cmuList = (ArrayList<Community>) jkuser
						.getCommuList();
				for (int i = 0; i < cmuList.size(); i++) {
					if (cmuList.get(i).getCid() == addCommunity.getDestCid()) {
						ArrayList<Jkuser> ul = (ArrayList<Jkuser>) cmuList.get(
								i).getUserList();
						ul.add(u);
						break;
					}
				}

				this.remove(communityTree);
				communityTree = new CommunityTree(jkuser);
				communityTree.setBounds(0, 150, 300, 450);
				this.add(communityTree);
				addCommunityResp.setRes(1);

			} else {
				addCommunityResp.setRes(0);
			}
			try {
				conn.sendMsg(addCommunityResp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (m.getType() == IMsgConstance.command_addCommunity_resp) {
			MsgAddCommunityResp addCommunityResp = (MsgAddCommunityResp) m;
			if (addCommunityResp.getRes() == 1) {
				JOptionPane.showMessageDialog(this,
						"您已经通过群" + addCommunityResp.getDestcid() + "的申请");
				// 更新UI(CommunityTree)
				Community community = null;
				try {
					ObjectInputStream oins = new ObjectInputStream(
							conn.getDins());
					community = (Community) oins.readObject();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				ArrayList<Community> cmuList = (ArrayList<Community>) jkuser
						.getCommuList();
				cmuList.add(community);

				this.remove(communityTree);
				communityTree = new CommunityTree(jkuser);
				communityTree.setBounds(0, 150, 300, 450);
				this.add(communityTree);
				this.repaint();

			} else {
				JOptionPane.showMessageDialog(this,
						"群" + addCommunityResp.getDestcid() + "的申请被拒绝");
			}
		} else if (m.getType() == IMsgConstance.command_commuChatTxt
				|| m.getType() == IMsgConstance.command_commuChatFile
				|| m.getType() == IMsgConstance.command_commu_onLine
				|| m.getType() == IMsgConstance.command_commu_offLine) {
			communityTree.onMsgRecive(m);
		} else if(m.getType() == IMsgConstance.command_addGroup_resp) {
			MsgAddGroupResp addGroupResp = (MsgAddGroupResp) m;
			if(addGroupResp.getState() == 0) {
				JOptionPane.showMessageDialog(this, "服务器故障,添加分组失败");
			}else {
				Jkgroup jkgroup = new Jkgroup();
				jkgroup.setName(userTree.input);
				jkgroup.setUserList(new ArrayList<Jkuser>());
				jkgroup.setOwner(jkuser.getJknum());
				jkgroup.setGid(addGroupResp.getSrc());
				
				List<Jkgroup> gList = jkuser.getGroupList();
				gList.add(jkgroup);
				this.remove(userTree);
				userTree = new UserTree(jkuser);
				userTree.setBounds(0, 150, 300, 450);
				this.add(userTree);
				this.repaint();
				
				
			}
		} else if(m.getType() == IMsgConstance.command_deleteFriend_resp) {
			MsgDeleteFriendResp deleteFriendResp = (MsgDeleteFriendResp) m;
			if(deleteFriendResp.getState() == 0) {
				JOptionPane.showConfirmDialog(this, "服务器故障,删除好友失败");
				return;
			}
			int destNum = deleteFriendResp.getDest();
			int gid = deleteFriendResp.getGid();
			List<Jkgroup> gList = jkuser.getGroupList();
			Jkgroup jgroup = null;
			for (int i = 0; i < gList.size(); i++) {
				if(gList.get(i).getGid() == gid) {
					jgroup = gList.get(i);
					break;
				}
			}
			List<Jkuser> uList = jgroup.getUserList();
			for (int i = 0; i < uList.size(); i++) {
				if(uList.get(i).getJknum() == destNum) {
					uList.remove(i);
					break;
				}
			}
			//更新UI
			this.remove(userTree);
			userTree = new UserTree(jkuser);
			userTree.setBounds(0, 150, 300, 450);
			this.add(userTree);
			this.repaint();
		} else if(m.getType() == IMsgConstance.command_deleteGroup_resp) {
			MsgDeleteGroupResp deleteGroupResp = (MsgDeleteGroupResp) m;
			int gid = deleteGroupResp.getGid();
			int state = deleteGroupResp.getState();
			if(state == 1) {
				List<Jkgroup> gList = jkuser.getGroupList();
				for (int i = 0; i < gList.size(); i++) {
					if(gList.get(i).getGid() == gid) {
						gList.remove(i);
					}
				}
				this.remove(userTree);
				userTree = new UserTree(jkuser);
				userTree.setBounds(0, 150, 300, 450);
				this.add(userTree);
				this.repaint();
			}else {
				JOptionPane.showMessageDialog(userTree, "服务器故障,删除分组失败");
			}
		} else if(m.getType() == IMsgConstance.command_createCommunity_resp) {
			MsgCreateCommunityResp communityResp = (MsgCreateCommunityResp) m;
			List<Community> cmuList = jkuser.getCommuList();
			Community community = new Community();
			community.setCid(communityResp.getCid());
			community.setName(communityTree.commu_name);
			community.setDes(communityTree.commu_des);
			community.setIconpath(communityTree.selectedFile);
			community.setOwner(jkuser.getJknum());
			cmuList.add(community);
			List<Jkuser> uList = community.getUserList();
			uList.add(jkuser);
			
			this.remove(communityTree);
			communityTree = new CommunityTree(jkuser);
			communityTree.setBounds(0, 150, 300, 450);
			this.add(communityTree);
			this.repaint();
		} else if(m.getType() == IMsgConstance.command_deleteCommunity_resp) {
			MsgDeleteCommunityResp communityResp = (MsgDeleteCommunityResp) m;
			int cid = communityResp.getCid();
			System.out.println("state:" + communityResp.getState());
			if(communityResp.getState() == 0) {
				List<Community> cList = jkuser.getCommuList();
				Community cmu = new Community();
				for (int i = 0; i < cList.size(); i++) {
					if(cList.get(i).getCid() == cid) {
						cmu = cList.get(i);
						break;
					}
				}
				JOptionPane.showMessageDialog(communityTree, "删除群组"+cmu.getName()+"失败");
			}else {
				List<Community> cList = jkuser.getCommuList();
				for (int i = 0; i < cList.size(); i++) {
					if(cList.get(i).getCid() == cid) {
						cList.remove(i);
						break;
					}
				}
				this.remove(communityTree);
				communityTree = new CommunityTree(jkuser);
				communityTree.setBounds(0, 150, 300, 450);
				this.add(communityTree);
				this.repaint();
			}
		}
	}

}
