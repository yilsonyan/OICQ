package com.qq.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.qq.model.CommuChatLog;
import com.qq.model.Community;
import com.qq.model.Jkfile;
import com.qq.model.Jkgroup;
import com.qq.model.Jkuser;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgCommuChatFile;
import com.qq.msg.MsgCommuChatText;
import com.qq.msg.MsgCreateCommunity;
import com.qq.msg.MsgDeleteCommunity;
import com.qq.msg.MsgHead;
import com.qq.util.ImageUtil;
import com.qq.util.OpenTree;

/**
 * QQ通信系统 在主界面中显示QQ聊天群 并处理相关消息
 * 
 * @author yy
 */
public class CommunityTree extends JTree {

	private static final long serialVersionUID = 1L;
	private List<JFrame> jfList = new ArrayList<JFrame>();
	private List<JTextArea> jtList = new ArrayList<JTextArea>();
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private DefaultMutableTreeNode root;// 树上的根节点
	private Jkuser jkuser; // 用户对象
	private int size = 0;
	private JLabel l1 = null;
	private ListSource listSource = null;
	private BaseList transferList = null;
	private JScrollPane panel = null;
	private List<JLabel> labelList = new ArrayList<JLabel>(); // 文件上传标签列表
	File selectedFile = null;
	private Object userO = null;
	// 取得连结器的单实例对象
	private ClientConnection conn = ClientConnection.getIns();
	private Map<Integer, Integer> sendFrameMap = new HashMap<Integer, Integer>();
	private CommuMemberTree memberTree = null;
	Point p = null;
	private CommunityTree ct = null;
	String commu_name = null;
	String commu_des = null;
	private List<CommuMemberTree> memList = new ArrayList<CommuMemberTree>();
	
	// 创建树时，传入jkuser对象
	public CommunityTree(Jkuser jkuser) {
		ct = this;
		this.jkuser = jkuser;
		root = new DefaultMutableTreeNode(new NodeData(3, "群管理"));
		DefaultTreeModel tm = new DefaultTreeModel(root);
		tm.setAsksAllowsChildren(true);
		// 添加分组
		List<Community> clist = jkuser.getCommuList();
		DefaultMutableTreeNode guide = new DefaultMutableTreeNode("我的QQ群   "
				+ clist.size());
		root.add(guide);
		for (int i = 0; i < clist.size(); i++) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					new NodeData(1, clist.get(i)));
			node.setAllowsChildren(false);
			guide.add(node);
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

		OpenTree.expandTree(this, true);

		final JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem addCommunity = new JMenuItem("创建群");
		jPopupMenu.add(addCommunity);
		
		final JPopupMenu jPopupMenu2 = new JPopupMenu();
		JMenuItem deleteCommunity  = new JMenuItem("删除群");
		jPopupMenu2.add(deleteCommunity);
		
		// 给树加上Mouse事件监听器，双击弹出界面给用户发消息
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 双击事件
					showSendFrame();// 弹出发送消息框
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					if(e.getX() > 210) {
						jPopupMenu.show(ct,e.getX(),e.getY());
						return;
					}
					// 得到树上选中的节点:
					TreePath tp = ct.getSelectionPath();;
					if (tp == null) {// 未选中树节点
						return;
					}
					
					Object obj = tp.getLastPathComponent();// 取得选中的节点
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
					userO = node.getUserObject();// 取得节点内的对象
					if(userO instanceof NodeData && (((NodeData) userO).nodeType == 1)) {			
						jPopupMenu2.show(ct,e.getX(),e.getY());
					}
					
				}
			}
		});
		
		addCommunity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				final JFrame frame = new JFrame("创建群");
				// 设置标题头像
				ImageIcon icon = new ImageIcon("./images/logo.jpg");
				Image logo = icon.getImage();
				frame.setIconImage(logo);
				// 设置背景图片
				ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
				JLabel jl_bg = new JLabel(bg_icon);
				jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
				// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
				JPanel imgPanel = (JPanel) frame.getContentPane();
				imgPanel.setOpaque(false);
				frame.getLayeredPane().setLayout(null);
				frame.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
				// 设置imagePane的布局方式为绝对布局
				imgPanel.setLayout(null);
				
				JLabel name = new JLabel("群名称");
				JLabel des = new JLabel("群简介");
				JLabel cmu_icon = new JLabel("群头像");
				final JTextField jt_name = new JTextField();
				final JTextField jt_des = new JTextField();
				JButton btn = new JButton("创建");
				final JLabel isSelect = new JLabel("尚未选择文件(双击选择)");
				final JLabel header = new JLabel();
				
				name.setBounds(35,20,60,30);
				jt_name.setBounds(110,20,120,30);
				des.setBounds(35,60,60,30);
				jt_des.setBounds(110,60,120,30);
				cmu_icon.setBounds(35,100,60,30);
				btn.setBounds(110,175,80,30);
				isSelect.setBounds(110,100,140,30);
				header.setBounds(260,100,60,60);
				
				isSelect.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							JFileChooser chooser = new JFileChooser();
							chooser.showOpenDialog(frame);
							selectedFile = chooser.getSelectedFile();
							if(selectedFile == null) {
								JOptionPane.showMessageDialog(frame, "未选择任何文件");
								return;
							}else {
								String fileName = selectedFile.getName();
								int index = fileName.lastIndexOf(".");
								String append = fileName.substring(index+1);
								if(append.equals("jpg") || append.equals("png") || append.equals("bmp")
										||append.equals("jpeg")) {
									
									frame.remove(isSelect);
									frame.remove(header);
									BufferedImage newImg = ImageUtil.compressImage(selectedFile, 60, 60);
									header.setIcon(new ImageIcon(newImg));
									header.setBounds(110,100,60,60);
									frame.add(header);
									isSelect.setText(fileName);
									isSelect.setBounds(180,100,80,30);
									frame.add(isSelect);
									frame.repaint();
									
								}else {
									JOptionPane.showMessageDialog(frame, "选择文件格式有误");
									return;
								}
							}
						}
					}
				});
				
				
				btn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						//首先进行客户端校验
						if(jt_name.getText().trim().equals("")) {
							JOptionPane.showMessageDialog(frame, "群名称不能为空");
							return;
						}
						if(jt_des.getText().trim().equals("")) {
							JOptionPane.showMessageDialog(frame, "群简介不能为空");
							return;
						}
						if(selectedFile == null) {
							JOptionPane.showMessageDialog(frame, "未选择合适的头像");
							return;
						}
						
						List<Community> cList = CommunityTree.this.jkuser.getCommuList();
						for (int i = 0; i < cList.size(); i++) {
							System.out.println("index:" + i + "  " + cList.get(i).getName() +"  " +jt_name.getText());
							if(cList.get(i).getName().trim().equals(jt_name.getText().trim())) {
								JOptionPane.showMessageDialog(frame, "该群名称已存在");
								return;
							}
						}
						
						commu_name = jt_name.getText();
						commu_des = jt_des.getText();
						
						//向服务器端发送群组创建消息
						MsgCreateCommunity community = new MsgCreateCommunity();
						community.setTotalLength(513+(int)selectedFile.length());
						community.setSrc(CommunityTree.this.jkuser.getJknum());
						community.setDest(IMsgConstance.Server_JK_NUMBER);
						community.setType(IMsgConstance.command_createCommunity);
						community.setcName(commu_name);
						community.setcDes(commu_des);
						community.setIcon(ImageUtil.getBytesFromFile(selectedFile));
						community.setFileName(selectedFile.getName());
						
						try {
							conn.sendMsg(community);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						frame.dispose();
						
						
					}
				});
				
				
				frame.add(name);
				frame.add(des);
				frame.add(cmu_icon);
				frame.add(jt_name);
				frame.add(jt_des);
				frame.add(btn);
				frame.add(isSelect);
				frame.add(header);
				
				frame.setLayout(null);
				frame.setBounds(0,0,340,260);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
			}
		});
		
		
		deleteCommunity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NodeData data = (NodeData) userO;
				Community community = (Community) data.value;
				
				if(community.getOwner() != CommunityTree.this.jkuser.getJknum()) {
					JOptionPane.showConfirmDialog(ct, "只有群的创建者才能删除该群");
					return;
				}
				
				MsgDeleteCommunity deleteCommunity = new MsgDeleteCommunity();
				deleteCommunity.setTotalLength(17);
				deleteCommunity.setType(IMsgConstance.command_deleteCommunity);
				deleteCommunity.setSrc(CommunityTree.this.jkuser.getJknum());
				deleteCommunity.setDest(IMsgConstance.Server_JK_NUMBER);
				deleteCommunity.setCid(community.getCid());
				
				try {
					conn.sendMsg(deleteCommunity);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
		
	}

	/**
	 * 处理客户端接收到一条消息时的动作,被主界面窗体对象调用
	 * 
	 * @param m
	 *            :接收到的消息对象
	 */
	public void onMsgRecive(MsgHead m) {
		if (m.getType() == IMsgConstance.command_commuChatTxt) {
			MsgCommuChatText chatText = (MsgCommuChatText) m;
			int cid = chatText.getDestCid();
			Community community = getCommunityById(cid);
			Jkuser jkuser = getJkuserByIdAndCommu(chatText.getSrc(), community);
			if (sendFrameMap.get(cid) == null) {
				// 首先初始化群成员树对象
				memberTree = new CommuMemberTree(community, jkuser);
				showSendMsgUI(community);
				JTextArea sendArea = jtList.get(map.get(cid));
				// 为JTextArea文本域设置内容
				String sendTime = chatText.getSendTime();
				String appendStr = jkuser.getName() + "(" + jkuser.getJknum()
						+ ")" + sendTime.substring(11);
				String space1 = "";
				int len1 = appendStr.getBytes().length;
				for (int i = 0; i < 2; i++) {
					space1 += "  ";
				}
				String appendStr2 = chatText.getchatTxt();
				String space2 = "";
				int len2 = appendStr2.getBytes().length;
				for (int i = 0; i < 2; i++) {
					space2 += "  ";
				}
				sendArea.setText(sendArea.getText() + "\n" + space1 + appendStr
						+ "\n" + space2 + appendStr2 + "\n");

				sendFrameMap.put(cid, 1);
			} else {
				JTextArea sendArea = jtList.get(map.get(cid));
				// 为JTextArea文本域设置内容
				String sendTime = chatText.getSendTime();
				String appendStr = jkuser.getName() + "(" + jkuser.getJknum()
						+ ")" + sendTime.substring(11);
				String space1 = "";
				int len1 = appendStr.getBytes().length;
				for (int i = 0; i < 2; i++) {
					space1 += "  ";
				}
				String appendStr2 = chatText.getchatTxt();
				String space2 = "";
				int len2 = appendStr2.getBytes().length;
				for (int i = 0; i < 2; i++) {
					space2 += "  ";
				}
				sendArea.setText(sendArea.getText() + "\n" + space1 + appendStr
						+ "\n" + space2 + appendStr2 + "\n");
			}
		} else if (m.getType() == IMsgConstance.command_commuChatFile) {
			MsgCommuChatFile chatFile = (MsgCommuChatFile) m;
			int cid = chatFile.getDestCid();
			Community community = getCommunityById(cid);
			if (m.getSrc() == 0) {
				if (sendFrameMap.get(cid) == null) {
					memberTree = new CommuMemberTree(community, jkuser);
					showSendMsgUI(community);
					JTextArea sendArea = jtList.get(map.get(cid));
					String appendStr2 = "有未接受的新文件";
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space2
							+ appendStr2 + "\n");

					sendFrameMap.put(cid, 1);

					int index = map.get(cid);
					JFrame frame = jfList.get(index);
					JLabel label = labelList.get(index);
					frame.remove(label);
					ImageIcon fileIcon = new ImageIcon(
							"./images/file_upload2.png");
					label.setIcon(fileIcon);
					label.setBounds(90, 325, 30, 25);
					frame.add(label);
					frame.repaint();
				} else {
					JTextArea sendArea = jtList.get(map.get(cid));
					// 为JTextArea文本域设置内容
					String appendStr2 = "有未接受的新文件";
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space2
							+ appendStr2 + "\n");

					int index = map.get(cid);
					JFrame frame = jfList.get(index);
					JLabel label = labelList.get(index);
					frame.remove(label);
					ImageIcon fileIcon = new ImageIcon(
							"./images/file_upload2.png");
					label.setIcon(fileIcon);
					label.setBounds(90, 325, 30, 25);
					frame.add(label);
					frame.repaint();
				}
			} else {
				Jkuser jkuser = getJkuserByIdAndCommu(chatFile.getSrc(),
						community);
				if (sendFrameMap.get(cid) == null) {

					List<Jkfile> fList = community.getFileList();
					Jkfile jFile = new Jkfile();
					String path = "F:/QQimgClient/" + chatFile.getSrc()
							+ chatFile.getFileName();
					File up_file = new File(path);
					try {
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(up_file));
						bos.write(chatFile.getFileData());
						bos.flush();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					jFile.setFile(up_file);
					jFile.setFilename(chatFile.getFileName());
					jFile.setSendTime(chatFile.getSendTime());
					jFile.setUid(chatFile.getSrc());
					fList.add(jFile);

					// 首先初始化群成员树对象
					memberTree = new CommuMemberTree(community, jkuser);
					showSendMsgUI(community);
					JTextArea sendArea = jtList.get(map.get(cid));
					// 为JTextArea文本域设置内容
					String sendTime = chatFile.getSendTime();
					String appendStr = jkuser.getName() + "("
							+ jkuser.getJknum() + ")" + sendTime.substring(11);
					String space1 = "";
					int len1 = appendStr.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space1 += "  ";
					}
					String appendStr2 = "发送文件 " + chatFile.getFileName();
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space1
							+ appendStr + "\n" + space2 + appendStr2 + "\n");

					sendFrameMap.put(cid, 1);

					int index = map.get(cid);
					JFrame frame = jfList.get(index);
					JLabel label = labelList.get(index);
					frame.remove(label);
					ImageIcon fileIcon = new ImageIcon(
							"./images/file_upload2.png");
					label.setIcon(fileIcon);
					label.setBounds(90, 325, 30, 25);
					frame.add(label);
					frame.repaint();

				} else {
					JTextArea sendArea = jtList.get(map.get(cid));
					// 为JTextArea文本域设置内容
					String sendTime = chatFile.getSendTime();
					String appendStr = jkuser.getName() + "("
							+ jkuser.getJknum() + ")" + sendTime.substring(11);
					String space1 = "";
					int len1 = appendStr.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space1 += "  ";
					}
					String appendStr2 = "发来文件 " + chatFile.getFileName();
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int i = 0; i < 2; i++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space1
							+ appendStr + "\n" + space2 + appendStr2 + "\n");

					int index = map.get(cid);
					JFrame frame = jfList.get(index);
					JLabel label = labelList.get(index);
					frame.remove(label);
					ImageIcon fileIcon = new ImageIcon(
							"./images/file_upload2.png");
					label.setIcon(fileIcon);
					label.setBounds(90, 325, 30, 25);
					frame.add(label);
					frame.repaint();
				}
			}
		} else if(m.getType() == IMsgConstance.command_commu_onLine) {
			int srcNum = m.getSrc();
			int cid = m.getDest();
			List<Jkuser> ul = getCommunityById(cid).getUserList();
			for (int i = 0; i < ul.size(); i++) {
				if(ul.get(i).getJknum() == srcNum) {
					ul.get(i).setState(1);
					break;
				}
			}
			if(map.get(cid)!=null) {
				CommuMemberTree cmt = memList.get(map.get(cid));
				cmt.setCommunity(getCommunityById(cid));
				cmt.go();
				jfList.get(map.get(cid)).repaint();
				
			}
		} else if(m.getType() == IMsgConstance.command_commu_offLine) {
			int srcNum = m.getSrc();
			int cid = m.getDest();
			List<Jkuser> ul = getCommunityById(cid).getUserList();
			for (int i = 0; i < ul.size(); i++) {
				if(ul.get(i).getJknum() == srcNum) {
					ul.get(i).setState(0);
					break;
				}
			}
			if(map.get(cid)!=null) {
				CommuMemberTree cmt = memList.get(map.get(cid));
				cmt.setCommunity(getCommunityById(cid));
				cmt.go();
				jfList.get(map.get(cid)).repaint();
				
			}
		}
	}

	/**
	 * 根据群对象和uid获得用户对象
	 * 
	 * @param uid
	 * @param community
	 * @return
	 */
	private Jkuser getJkuserByIdAndCommu(int uid, Community community) {
		ArrayList<Jkuser> uList = (ArrayList<Jkuser>) community.getUserList();
		Jkuser jkuser = null;
		for (int i = 0; i < uList.size(); i++) {
			if (uList.get(i).getJknum() == uid) {
				jkuser = uList.get(i);
				break;
			}
		}
		return jkuser;
	}

	/**
	 * 根据cid获得目标群
	 * 
	 * @param cid
	 * @return
	 */
	private Community getCommunityById(int cid) {
		ArrayList<Community> commuList = (ArrayList<Community>) jkuser
				.getCommuList();
		for (int i = 0; i < commuList.size(); i++) {
			if (commuList.get(i).getCid() == cid) {
				return commuList.get(i);
			}
		}
		return null;
	}

	/* 双击树上的群节点时，弹出消息发送框 */
	private void showSendFrame() {
		// 得到树上选中的节点:
		TreePath tp = this.getSelectionPath();
		if (tp == null) {// 未选中树节点
			return;
		}
		Object obj = tp.getLastPathComponent();// 取得选中的节点
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
		Object userO = node.getUserObject();// 取得节点内的对象
		if ((userO instanceof NodeData) && (((NodeData) userO).nodeType == 1)) {
			NodeData data = (NodeData) userO;
			final Community destCommunity = (Community) data.value;
			memberTree = new CommuMemberTree(destCommunity, jkuser);
			// 弹出发送消息框
			if (sendFrameMap.get(destCommunity.getCid()) == null) {
				showSendMsgUI(destCommunity);
				sendFrameMap.put(destCommunity.getCid(), 1);
			}
		}
	}

	/**
	 * 弹出聊天主界面
	 * 
	 * @param destUser
	 */
	private void showSendMsgUI(final Community destCommunity) {
		final JFrame jf_send = new JFrame("QQ群聊天");
		jf_send.setLayout(null);
		jf_send.setBounds(0, 0, 540, 480);
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
				ImageUtil.getBytesFromFile(destCommunity.getIconpath()));
		header.setIcon(headerIcon);
		header.setBounds(20, 20, 60, 60);
		min = new JLabel();
		close = new JLabel();
		final ImageIcon minIcon = new ImageIcon("./images/min.png");
		final ImageIcon minIcon2 = new ImageIcon("./images/min1.png");
		min.setIcon(minIcon);
		min.setBounds(500, 10, 10, 10);
		final ImageIcon closeIcon = new ImageIcon("./images/close.png");
		final ImageIcon closeIcon2 = new ImageIcon("./images/close1.png");
		close.setIcon(closeIcon);
		close.setBounds(520, 10, 10, 10);
		name = new JLabel(destCommunity.getName() + "("
				+ destCommunity.getCid() + ")");
		name.setFont(new Font("宋体", Font.PLAIN, 16));
		name.setBounds(110, 25, 150, 30);
		star = new JLabel();
		ImageIcon starIcon = new ImageIcon("./images/star.jpg");
		star.setIcon(starIcon);
		star.setBounds(110, 55, 30, 30);
		signature = new JLabel(destCommunity.getDes());
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
		memberTree.setBorder(BorderFactory.createLineBorder(Color.gray));
		JScrollPane memberPane = new JScrollPane(memberTree);

		memberPane.setBounds(360, 95, 180, 345);

		int index = jfList.size();
		jfList.add(jf_send);
		jtList.add(chatArea);
		labelList.add(fileUpload);
		memList.add(memberTree);
		// 把目标用户jknum号码和index通过map绑定起来
		map.put(destCommunity.getCid(), index);

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
		jf_send.add(memberPane);
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
				sendFrameMap.remove(destCommunity.getCid());
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
				map.remove(destCommunity.getCid());
				sendFrameMap.remove(destCommunity.getCid());
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
				conn.sendCommuTxt(jkuser.getJknum(), destCommunity.getCid(),
						txt, sendTime);
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
				chatArea.setText(chatArea.getText() + "\n" + space1 + appendStr
						+ "\n" + space2 + appendStr2 + "\n");
				sendArea.setText("");
			}
		});

		fileUpload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 弹出文件管理页面
				final JFrame frame = new JFrame("群文件管理");
				ImageIcon titleIcon = new ImageIcon("./images/file_upload.png");
				frame.setIconImage(titleIcon.getImage());

				// 设置背景图片
				ImageIcon bg_icon = new ImageIcon("./images/reg_bg1.jpg");
				JLabel mainbg = new JLabel(bg_icon);
				mainbg.setBounds(0, 0, bg_icon.getIconWidth(),
						bg_icon.getIconHeight());
				// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
				final JPanel imgPanel = (JPanel) frame.getContentPane();
				imgPanel.setOpaque(false);
				frame.getLayeredPane().setLayout(null);
				frame.getLayeredPane().add(mainbg,
						new Integer(Integer.MIN_VALUE));
				// 设置imagePane的布局方式为绝对布局
				imgPanel.setLayout(null);

				size = destCommunity.getFileList().size();
				l1 = new JLabel("共有 " + size + " 个文件");
				l1.setBounds(20, 20, 100, 20);

				JButton btn_upload = new JButton("上传文件");
				btn_upload.setBounds(365, 20, 100, 20);

				JLabel l2 = new JLabel("文件列表");
				l2.setBounds(20, 60, 100, 20);

				final List<Object> list = new ArrayList<Object>();

				List<Jkfile> fList = destCommunity.getFileList();
				for (Jkfile file : fList) {
					list.add(file);
				}

				// 构造源
				listSource = new ListSource();
				listSource.setSources(list);
				// 构造BaseList
				transferList = new BaseList();
				// TransferPanel实现ListCellIface接口
				transferList.setCellIface(new CellPanel());
				// 设置源
				transferList.setSource(listSource);
				transferList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
						10));
				panel = new JScrollPane(transferList,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				panel.setBounds(20, 100, 445, 200);
				frame.add(panel);
				frame.add(btn_upload);
				frame.add(l1);

				btn_upload.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

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
						chatArea.setText(chatArea.getText() + "\n" + space1
								+ appendStr + "\n" + space2 + appendStr2 + "\n");

						String name = file.getName();

						// 向服务器发送群文件上传消息
						MsgCommuChatFile chatFile = new MsgCommuChatFile();
						chatFile.setFileName(name);
						chatFile.setTotalLength(298 + (int) file.length());
						chatFile.setType(IMsgConstance.command_commuChatFile);
						chatFile.setSrc(jkuser.getJknum());
						chatFile.setDest(IMsgConstance.Server_JK_NUMBER);
						chatFile.setSendTime(sendTime);
						chatFile.setDestCid(destCommunity.getCid());
						chatFile.setFileData(ImageUtil.getBytesFromFile(file));
						try {
							conn.sendMsg(chatFile);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

						frame.remove(l1);
						frame.remove(panel);
						size++;
						l1 = new JLabel("共有" + (size) + "个文件");
						l1.setBounds(20, 20, 100, 20);
						frame.add(l1);
						l1.updateUI();

						Jkfile up_file = new Jkfile();
						up_file.setFile(file);
						up_file.setFilename(file.getName());
						up_file.setSendTime(sendTime);
						up_file.setUid(jkuser.getJknum());
						list.add(up_file);

						System.out.println("size:" + list.size());
						// 构造源
						listSource = new ListSource();
						listSource.setSources(list);
						// 构造BaseList
						transferList = new BaseList();
						// TransferPanel实现ListCellIface接口
						transferList.setCellIface(new CellPanel());
						// 设置源
						transferList.setSource(listSource);
						transferList.setBorder(BorderFactory.createEmptyBorder(
								0, 0, 0, 10));
						panel = new JScrollPane(transferList,
								JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						panel.setBounds(20, 100, 445, 200);
						frame.add(panel);
						panel.updateUI();

						List<Jkfile> fList = destCommunity.getFileList();
						Jkfile jFile = new Jkfile();
						jFile.setFile(file);
						jFile.setFilename(file.getName());
						jFile.setSendTime(sendTime);
						jFile.setUid(jkuser.getJknum());
						fList.add(jFile);

					}
				});

				frame.add(l2);
				frame.setLayout(null);
				frame.setBounds(0, 0, 500, 400);
				frame.setVisible(true);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

		jf_send.setLocationRelativeTo(null);
		jf_send.setResizable(false);
		jf_send.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf_send.setVisible(true);
	}

	/**
	 * 处理未接受的群聊消息 并且弹出对话窗口
	 * 
	 * @param cmuChatLog
	 */
	public void precessCommuMsg(ArrayList<CommuChatLog> cmuChatLog) {
		Map<Integer, ArrayList<CommuChatLog>> logsMap = new HashMap<Integer, ArrayList<CommuChatLog>>();// <cid,commuChatLog>mapping
		// 消息根据cid进行分组
		for (int i = 0; i < cmuChatLog.size(); i++) {
			int cid = cmuChatLog.get(i).getCid();
			if (logsMap.get(cid) == null) {
				ArrayList<CommuChatLog> logs = new ArrayList<CommuChatLog>();
				logs.add(cmuChatLog.get(i));
				logsMap.put(cid, logs);
			} else {
				logsMap.get(cid).add(cmuChatLog.get(i));
			}
		}

		// 弹出对应的窗口
		Set<Integer> idSet = logsMap.keySet();
		Iterator<Integer> iterator = idSet.iterator();
		while (iterator.hasNext()) {
			int cid = iterator.next();
			Community community = getCommunityById(cid);
			ArrayList<CommuChatLog> cmuLists = logsMap.get(cid);
			for (int i = 0; i < cmuLists.size(); i++) {
				CommuChatLog chatText = cmuChatLog.get(i);
				if (i == 0 && sendFrameMap.get(cid) == null) {
					// 首先初始化群成员树对象
					memberTree = new CommuMemberTree(community, jkuser);
					showSendMsgUI(community);
					JTextArea sendArea = jtList.get(map.get(cid));
					// 为JTextArea文本域设置内容
					String sendTime = chatText.getSendTime();
					String appendStr = jkuser.getName() + "("
							+ jkuser.getJknum() + ")" + sendTime.substring(11);
					String space1 = "";
					int len1 = appendStr.getBytes().length;
					for (int j = 0; j < 2; j++) {
						space1 += "  ";
					}
					String appendStr2 = chatText.getContent();
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int j = 0; j < 2; j++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space1
							+ appendStr + "\n" + space2 + appendStr2 + "\n");

					sendFrameMap.put(cid, 1);
				} else {
					JTextArea sendArea = jtList.get(map.get(cid));
					// 为JTextArea文本域设置内容
					String sendTime = chatText.getSendTime();
					String appendStr = jkuser.getName() + "("
							+ jkuser.getJknum() + ")" + sendTime.substring(11);
					String space1 = "";
					int len1 = appendStr.getBytes().length;
					for (int j = 0; j < 2; j++) {
						space1 += "  ";
					}
					String appendStr2 = chatText.getContent();
					String space2 = "";
					int len2 = appendStr2.getBytes().length;
					for (int j = 0; j < 2; j++) {
						space2 += "  ";
					}
					sendArea.setText(sendArea.getText() + "\n" + space1
							+ appendStr + "\n" + space2 + appendStr2 + "\n");
				}
			}

		}

	}

}
