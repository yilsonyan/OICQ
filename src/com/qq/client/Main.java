package com.qq.client;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.qq.dao.JkuserDaoImpl;
import com.qq.model.Jkuser;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgHead;
import com.qq.util.ImageUtil;
import com.qq.util.MD5Util;

/**
 * QQ项目启动主类 1.显示登陆主界面 登陆成功跳转到QQ主界面 2.提供注册功能 注册成功跳转到登陆界面 3.忘记密码 自动登录 记住密码
 * 选择登陆状态等功能
 * 
 * @author yy
 * 
 */
public class Main {

	private JFrame jf_login; // 登陆界面
	// 登陆界面的jknum输入框 密码输入框
	private JFormattedTextField jt_jknum;
	private JTextField jt_passwd;
	ClientConnection conn = ClientConnection.getIns();
	JTextField username; // 用户名输入框
	JPasswordField pwd; // 密码输入框
	JComboBox com1; // 登陆状态选择框
	JCheckBox c1; // 记住密码
	JCheckBox c2; // 自动登录
	private Jkuser user = null;
	private String[] str = new String[2];
	private int jknum = 0;
	
	/**
	 * 
	 * 显示登陆界面
	 */
	public void showLoginUI() {
		jf_login = new JFrame("仿QQ通讯项目");
		// 布局用到的一些组件的声明
		JLabel jl1; // 登陆头像
		JLabel jl2; // 注册提示
		JLabel jl3; // 忘记密码

		JButton b1; // 登录按钮

		JLabel jl_bg = new JLabel();
		// 设置布局方式为绝对布局
		jf_login.setLayout(null);
		jf_login.setBounds(0, 0, 380, 220);

		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/bg.jpg");
		jl_bg = new JLabel(bg_icon);
		jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) jf_login.getContentPane();
		imgPanel.setOpaque(false);
		jf_login.getLayeredPane().setLayout(null);
		jf_login.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);

		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/test.jpg");
		Image logo = icon.getImage();
		jf_login.setIconImage(logo);
		com1 = new JComboBox(); // 登陆状态的选择
		com1.addItem("在线");
		com1.addItem("隐身");
		com1.setBounds(38, 105, 60, 25);
		username = new JTextField(); // QQ号码
		username.setBounds(112, 35, 160, 27);
		pwd = new JPasswordField(); // QQ密码
		pwd.setBounds(112, 68, 160, 27);
		c1 = new JCheckBox("记住密码"); // 记住密码选项
		c1.setBounds(112, 105, 78, 25);
		c1.setContentAreaFilled(false);
		c2 = new JCheckBox("自动登录"); // 自动登录选项
		c2.setBounds(192, 105, 78, 25);
		c2.setContentAreaFilled(false);
		jl2 = new JLabel("注册账号"); // 注册账号选项
		jl2.setBounds(287, 35, 70, 27);
		jl2.setForeground(Color.blue);
		b1 = new JButton("登陆"); // 登陆按钮的设置
		ImageIcon login_icon = new ImageIcon("./images/login.jpg");
		b1.setIcon(login_icon);
		b1.setBounds(143, 140, login_icon.getIconWidth() - 10,
				login_icon.getIconHeight());
		jl3 = new JLabel("忘记密码");
		jl3.setBounds(287, 70, 70, 27);
		jl3.setForeground(Color.blue);
		// imagePane中组件的初始化
		jl1 = new JLabel(); // 登陆头像
		ImageIcon login_img = null;
		File saveFile = new File("F:/QQmsg/user.dat");
		Jkuser jkuser = null;
		if (saveFile.exists()) {
			ObjectInputStream oins = null;
			try {
				oins = new ObjectInputStream(new FileInputStream(saveFile));
				jkuser = (Jkuser) oins.readObject();
				if(jkuser.getIconpath() != null) {
					login_img = new ImageIcon(ImageUtil.getBytesFromFile(jkuser
							.getIconpath()));
				} else {
					login_img = new ImageIcon("./images/logo.jpg");
				}
				username.setText(jkuser.getJknum() + "");
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				try {
					oins.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			login_img = new ImageIcon("./images/logo.jpg");
		}

		jl1.setIcon(login_img);
		jl1.setBounds(38, 40, login_img.getIconWidth(),
				login_img.getIconHeight());

		// 向imagePane中添加组件
		imgPanel.add(jl1);
		imgPanel.add(com1);
		imgPanel.add(username);
		imgPanel.add(pwd);
		imgPanel.add(c1);
		imgPanel.add(c2);
		imgPanel.add(jl2);
		imgPanel.add(b1);
		imgPanel.add(jl3);

		// 各种监听事件的处理

		// 注册账号事件监听
		jl2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showRegUI();
			}

		});
		// 忘记密码事件监听
		jl3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showFindPwdUI();
			}
		});

		// 自动登录监听 如果选中了自动登录 但是没有选中保存密码 那么把保存密码选中
		c2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (c2.isSelected()) {
					if (!c1.isSelected()) {
						c1.setSelected(true);
					}
				}
			}
		});

		// 登录按钮的事件监听
		b1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loginAction();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		b1.setMnemonic(KeyEvent.VK_ENTER);

		jf_login.setResizable(false);
		jf_login.setLocationRelativeTo(null);
		jf_login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// 检查是否需要填充密码 是否需要自动登录
		File check = new File("F:/QQmsg/state.dat");
		if (check.exists()) {
			BufferedReader br = null;
			boolean flag = false;
			try {
				br = new BufferedReader(new FileReader(check));
				String n1 = br.readLine().trim();
				String n2 = br.readLine().trim();
				if (n1.equals("1")) {
					pwd.setText(jkuser.getPassword());
					c1.setSelected(true);
					if (n2.equals("1")) {
						c2.setSelected(true);
						// 判断是否已经登陆
						File loginstate = new File("F:/QQmsg/loginstate.dat");
						if (loginstate.exists()) {
							ArrayList<Integer> list;
							try {
								ObjectInputStream oins = new ObjectInputStream(
										new FileInputStream(loginstate));
								list = (ArrayList<Integer>) oins.readObject();
								if (list.indexOf((Integer) jkuser.getJknum()) != -1) {
									flag = true;
								}
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
						if (!flag)
						{
							this.loginAction();
							return;
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		jf_login.setVisible(true);

	}

	/**
	 * 找回密码界面
	 */
	private void showFindPwdUI() {
		final JFrame find = new JFrame("找回密码");
		JLabel label = new JLabel("请输入您的QQ号码:");
		JButton btn = new JButton("下一步");
		final JTextField f1 = new JTextField();
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel l1 = new JLabel(bg_icon);
		l1.setBounds(0,0,300,140);
		find.setBounds(0, 0, 300,140);
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) find.getContentPane();
		imgPanel.setOpaque(false);
		find.getLayeredPane().setLayout(null);
		find.getLayeredPane().add(l1, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/test.jpg");
		Image logo = icon.getImage();
		find.setIconImage(logo);
		
		
		label.setBounds(20,30,140,25);
		f1.setBounds(165,30,90,25);
		btn.setBounds(85,75,100,35);
		
		//添加组件
		find.add(label);
		find.add(f1);
		find.add(btn);
		
		//添加监听事件
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String num = f1.getText().trim();
				if(num.equals("") || num == null) {
					JOptionPane.showMessageDialog(find, "QQ号码不能为空!");
					return;
				}
				
				try {
					jknum = Integer.parseInt(num);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(find, "QQ号码必须是数字!");
					return;
				}
				

				if(conn.conn2Server()) {
					str = conn.forgetPwd(jknum);
					if(str == null) {
						JOptionPane.showMessageDialog(find, "QQ号码不存在!");
						return;
					}else {
						find.dispose();showFindUI2();
					}
				}
				
				
			}
		});
		
		find.setResizable(false);
		find.setVisible(true);
		find.setLocationRelativeTo(null);
		find.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * 找回密码第二步UI
	 * @param user
	 */
	protected void showFindUI2() {
		final JFrame find = new JFrame("找回密码");
		JLabel label = new JLabel("密码保护问题:");
		JButton btn = new JButton("下一步");
		JLabel label2 = new JLabel(str[0].trim());
		JLabel label3 = new JLabel("请填写正确答案");
		final JTextField f1 = new JTextField();
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel l1 = new JLabel(bg_icon);
		l1.setBounds(0,0,300,200);
		find.setBounds(0, 0, 300,200);
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) find.getContentPane();
		imgPanel.setOpaque(false);
		find.getLayeredPane().setLayout(null);
		find.getLayeredPane().add(l1, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/test.jpg");
		Image logo = icon.getImage();
		find.setIconImage(logo);
		
		
		label.setBounds(20,30,140,25);
		label2.setBounds(130,30,90,25);
		btn.setBounds(85,120,100,35);
		label3.setBounds(20,65,140,25);
		f1.setBounds(130,65,90,25);
		
		
		//添加组件
		find.add(label);
		find.add(label2);
		find.add(btn);
		find.add(label3);
		find.add(f1);
		
		
		//添加监听事件
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = f1.getText().trim();
				if(str == null || str.equals("")) {
					JOptionPane.showMessageDialog(find, "答案不能为空!");
					return;
				}else if(!(MD5Util.MD5(str).equals(Main.this.str[1]))) {
					JOptionPane.showMessageDialog(find, "答案错误，请重新填写!");
					return;
				} else {
					find.dispose();
					showFindUI3();
				}
				
				
			}
		});

		find.setResizable(false);
		find.setVisible(true);
		find.setLocationRelativeTo(null);
		find.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * 找回密码最后一步UI
	 */
	protected void showFindUI3() {
		final JFrame find = new JFrame("找回密码");
		JLabel l2 = new JLabel("请输入新密码:");
		JLabel l3 = new JLabel("请确认新密码:");
		final JPasswordField p1 = new JPasswordField();
		final JPasswordField p2 = new JPasswordField();
		JButton btn = new JButton("确认修改");
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel l1 = new JLabel(bg_icon);
		l1.setBounds(0,0,300,200);
		find.setBounds(0, 0, 300,200);
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) find.getContentPane();
		imgPanel.setOpaque(false);
		find.getLayeredPane().setLayout(null);
		find.getLayeredPane().add(l1, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);
		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/test.jpg");
		Image logo = icon.getImage();
		find.setIconImage(logo);
		
		
		l2.setBounds(20,30,140,25);
		p1.setBounds(130,30,90,25);
		btn.setBounds(85,120,100,35);
		l3.setBounds(20,65,140,25);
		p2.setBounds(130,65,90,25);
		
		
		//添加组件
		find.add(l2);
		find.add(p1);
		find.add(btn);
		find.add(l3);
		find.add(p2);
		
		
		//添加监听事件
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String pwd1 = p1.getText();
				String pwd2 = p2.getText();
				if(pwd1.equals("")) {
					JOptionPane.showMessageDialog(find, "新密码不能为空!");
					return;
				} else if(!pwd1.equals(pwd2)) {
					JOptionPane.showMessageDialog(find, "两次密码不一致!");
					p1.setText("");
					p2.setText("");
					return;
				} else {
					byte state = conn.changePwd(jknum, pwd1.trim());
					if(state == 1) {
						JOptionPane.showMessageDialog(find, "修改成功!");
						find.dispose();
						return;
					} else {
						JOptionPane.showMessageDialog(find, "修改失败!");
						find.dispose();
						return;
					}
				}
			}
		});

		find.setResizable(false);
		find.setVisible(true);
		find.setLocationRelativeTo(null);
		find.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	// 登陆事件处理
	private void loginAction() throws FileNotFoundException {
		// 1.取得输入的jk号和密码
		String jkStr = username.getText().trim();
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(jkStr);
		if (!isNum.matches()) {
			JOptionPane.showMessageDialog(jf_login, "登陆失败,QQ号码必须是数字!");
			username.setText("");
			pwd.setText("");
			return;
		}

		int jkNum = Integer.parseInt(jkStr);
		String password = pwd.getText().trim();
		int state = com1.getSelectedIndex() + 1;

		// 判断是否已经登陆
		File loginstate = new File("F:/QQmsg/loginstate.dat");
		if (loginstate.exists()) {
			ArrayList<Integer> list;
			try {
				ObjectInputStream oins = new ObjectInputStream(
						new FileInputStream(loginstate));
				list = (ArrayList<Integer>) oins.readObject();
				if (list.indexOf((Integer) jkNum) != -1) {
					JOptionPane.showMessageDialog(jf_login, "已登陆此帐号，不能重复登陆!");
					this.username.setText("");
					this.pwd.setText("");
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// 2.连结上服务器
		if (conn.conn2Server()) {// 如果能连结上服务器
			// 3.登陆
			Jkuser jkuser = null;
			if ((jkuser = conn.loginServer(jkNum, password, state)) != null) {

				// 状态文件序列化到本地
				File stateFile = new File("F:/QQmsg/state.dat");
				if (!stateFile.getParentFile().exists()) {
					stateFile.getParentFile().mkdirs();
				}
				PrintWriter pw = new PrintWriter(stateFile);
				boolean flag1 = c1.isSelected();
				boolean flag2 = c2.isSelected();
				if (!flag1 && !flag2) {
					pw.println(0);
					pw.println(0);
				} else if (!flag1 && flag2) {
					pw.println(0);
					pw.println(1);
				} else if (flag1 && !flag2) {
					pw.println(1);
					pw.println(0);
				} else if (flag1 && flag2) {
					pw.println(1);
					pw.println(1);
				}
				pw.flush();
				pw.close();
				// 一登陆直接把user对象序列化保存到本地
				if (flag1) {
					jkuser.setPassword(pwd.getText());
				}

				File file = new File("F:/QQmsg/user.dat");
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(new FileOutputStream(file));
					oos.writeObject(jkuser);
				} catch (IOException e) {
					e.printStackTrace();
				}

				jf_login.dispose();
				// 4.显示聊天主界面 //登陆成功了，要关掉登陆界面
				MainUI mainUI = new MainUI(jkuser);
				mainUI.showMainUI();
				// 5.启动接收线程
				conn.start();
				// 6.将用户树加给连结对象,做为消息监听器
				conn.addMsgListener(mainUI);
				// 更新状态文件
				if (loginstate.exists()) {
					try {
						ObjectInputStream oins = new ObjectInputStream(
								new FileInputStream(loginstate));
						ArrayList<Integer> loginList = (ArrayList<Integer>) oins
								.readObject();
						loginList.add(jkNum);
						// 重新写回去
						ObjectOutputStream oos3 = new ObjectOutputStream(
								new FileOutputStream(loginstate));
						oos3.writeObject(loginList);
						oos3.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					List<Integer> list = new ArrayList<Integer>();
					list.add(jkNum);
					try {
						ObjectOutputStream oos2 = new ObjectOutputStream(
								new FileOutputStream(loginstate));
						oos2.writeObject(list);
						oos2.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				conn.closeMe();
				JOptionPane.showMessageDialog(jf_login, "登陆失败,请确认帐号正确!");
				username.setText("");
				pwd.setText("");
			}
		} else {
			conn.closeMe();
			JOptionPane.showMessageDialog(jf_login, "连结失败,请确认服务器开启,IP和端口正确!");
		}
	}

	/**
	 * 显示注册页面
	 */
	private void showRegUI() {
		final JFrame jf_reg = new JFrame("QQ注册界面");
		jf_reg.setLayout(null);
		jf_reg.setBounds(0, 0, 425, 700);

		// 注册界面用到的组件声明

		// 标签
		JLabel jl1; // 用户昵称
		JLabel jl2; // 密码
		JLabel jl3; // 密码重复
		JLabel jl4; // 用户性别
		JLabel jl5; // 用户邮箱
		JLabel jl6; // 手机号码
		JLabel jl7; // 用户所在地
		JLabel jl8; // 个性签名
		JLabel jl9; // 密保问题
		JLabel jl10; // 密保问题答案
		final JLabel jl11; // 用户昵称提示信息
		final JLabel jl12; // 密码提示信息
		final JLabel jl13; // 密码重复提示信息
		final JLabel jl14; // 用户性别提示信息
		final JLabel jl15; // 用户邮箱提示信息
		final JLabel jl16; // 手机号码提示信息
		final JLabel jl17; // 用户所在地提示信息
		final JLabel jl18; // 个性签名提示信息
		final JLabel jl19; // 密保问题提示信息
		final JLabel jl20; // 密保问题答案提示信息

		// 确认注册按钮
		JButton submit;

		// 文本框和密码框
		final JTextField jt1; // 用户昵称输入
		final JComboBox jt2; // 性别输入
		final JTextField jt3; // 邮箱
		final JTextField jt4; // 手机输入
		final JTextField jt5; // 用户所在地输入
		final JTextArea jt6; // 个性签名输入
		final JTextField jt7; // 密保问题输入
		final JTextField jt8; // 密保答案输入
		final JPasswordField jp1; // 密码输入
		final JPasswordField jp2; // 密码重复输入

		// 设置标题头像
		ImageIcon icon = new ImageIcon("./images/logo.jpg");
		Image logo = icon.getImage();
		jf_reg.setIconImage(logo);
		// 设置背景图片
		ImageIcon bg_icon = new ImageIcon("./images/reg_bg.jpg");
		JLabel jl_bg = new JLabel(bg_icon);
		jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imgPanel = (JPanel) jf_reg.getContentPane();
		imgPanel.setOpaque(false);
		jf_reg.getLayeredPane().setLayout(null);
		jf_reg.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
		// 设置imagePane的布局方式为绝对布局
		imgPanel.setLayout(null);
		// ImagePane中组件的初始化
		jl1 = new JLabel("用户昵称(必填):");
		jl1.setBounds(40, 40, 100, 25);
		jt1 = new JTextField();
		jt1.setBounds(150, 40, 130, 30);
		jl11 = new JLabel("");
		jl11.setForeground(Color.red);
		jl11.setBounds(300, 40, 100, 25);
		jl2 = new JLabel("登录密码(必填):");
		jl2.setBounds(40, 80, 100, 25);
		jp1 = new JPasswordField();
		jp1.setBounds(150, 80, 130, 30);
		jl12 = new JLabel("");
		jl12.setForeground(Color.red);
		jl12.setBounds(300, 80, 100, 25);
		jl3 = new JLabel("确认密码(必填):");
		jl3.setBounds(40, 120, 100, 25);
		jp2 = new JPasswordField();
		jp2.setBounds(150, 120, 130, 30);
		jl13 = new JLabel("");
		jl13.setForeground(Color.red);
		jl13.setBounds(300, 120, 100, 25);
		jl4 = new JLabel("您的性别:");
		jl4.setBounds(40, 160, 100, 25);
		jt2 = new JComboBox();
		jt2.addItem("-----------------------");
		jt2.addItem("男");
		jt2.addItem("女");
		jt2.setBounds(150, 160, 130, 30);
		jl14 = new JLabel("");
		jl14.setForeground(Color.red);
		jl14.setBounds(300, 160, 100, 25);
		jl5 = new JLabel("您的邮箱:");
		jl5.setBounds(40, 200, 100, 25);
		jt3 = new JTextField();
		jt3.setBounds(150, 200, 130, 30);
		jl15 = new JLabel("");
		jl15.setForeground(Color.red);
		jl15.setBounds(300, 200, 100, 25);
		jl6 = new JLabel("您的手机号码:");
		jl6.setBounds(40, 240, 100, 25);
		jt4 = new JTextField();
		jt4.setBounds(150, 240, 130, 30);
		jl16 = new JLabel("");
		jl16.setForeground(Color.red);
		jl16.setBounds(300, 240, 100, 25);
		jl7 = new JLabel("您的所在地:");
		jl7.setBounds(40, 280, 100, 25);
		jt5 = new JTextField();
		jt5.setBounds(150, 280, 130, 30);
		jl17 = new JLabel("");
		jl17.setForeground(Color.red);
		jl17.setBounds(300, 280, 100, 25);
		jl8 = new JLabel("您的个性签名:");
		jl8.setBounds(40, 320, 100, 25);
		jt6 = new JTextArea();
		jt6.setBounds(150, 320, 130, 140);
		jl18 = new JLabel("");
		jl18.setForeground(Color.red);
		jl18.setBounds(300, 320, 100, 25);
		jl9 = new JLabel("密保问题(必填):");
		jl9.setBounds(40, 475, 100, 25);
		jt7 = new JTextField();
		jt7.setBounds(150, 475, 130, 30);
		jl19 = new JLabel("");
		jl19.setForeground(Color.red);
		jl19.setBounds(300, 475, 100, 25);
		jl10 = new JLabel("密保答案(必填):");
		jl10.setBounds(40, 515, 100, 25);
		jt8 = new JTextField();
		jt8.setBounds(150, 515, 130, 30);
		jl20 = new JLabel("");
		jl20.setForeground(Color.red);
		jl20.setBounds(300, 515, 100, 25);
		submit = new JButton();
		ImageIcon reg_icon = new ImageIcon("./images/reg.jpg");
		submit.setIcon(reg_icon);
		submit.setBounds(150, 580, reg_icon.getIconWidth(),
				reg_icon.getIconHeight());
		submit.setContentAreaFilled(false);

		// 向imagePane中添加组件
		imgPanel.add(jl1);
		imgPanel.add(jt1);
		imgPanel.add(jl11);
		imgPanel.add(jl2);
		imgPanel.add(jp1);
		imgPanel.add(jl12);
		imgPanel.add(jl3);
		imgPanel.add(jp2);
		imgPanel.add(jl13);
		imgPanel.add(jl4);
		imgPanel.add(jt2);
		imgPanel.add(jl14);
		imgPanel.add(jl5);
		imgPanel.add(jt3);
		imgPanel.add(jl15);
		imgPanel.add(jl6);
		imgPanel.add(jt4);
		imgPanel.add(jl16);
		imgPanel.add(jl7);
		imgPanel.add(jl17);
		imgPanel.add(jt5);
		imgPanel.add(jl8);
		imgPanel.add(jt6);
		imgPanel.add(jl18);
		imgPanel.add(jl9);
		imgPanel.add(jt7);
		imgPanel.add(jl19);
		imgPanel.add(jl10);
		imgPanel.add(jt8);
		imgPanel.add(jl20);
		imgPanel.add(submit);

		// 注册按钮的监听
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 首先需要完成一些客户端的验证工作
				String name = jt1.getText().trim();
				String password = jp1.getText().trim();
				String password2 = jp2.getText().trim();
				String sex = (String) jt2.getItemAt(jt2.getSelectedIndex());
				String email = jt3.getText().trim();
				String phone = jt4.getText().trim();
				String site = jt5.getText().trim();
				String signature = jt6.getText().trim();
				String question = jt7.getText().trim();
				String answer = jt8.getText().trim();

				boolean flag1 = Pattern
						.matches(
								"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$",
								email);
				boolean flag2 = Pattern
						.matches(
								"1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}",
								phone);

				if (name.equals("") || name == null) {
					jl11.setText("昵称不能为空");
					jl12.setText("");
					jl13.setText("");
					jl19.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					jl15.setText("");
					jl16.setText("");
					return;
				} else if (password.equals("") || password == null) {
					jl12.setText("密码不能为空");
					jl11.setText("");
					jl13.setText("");
					jl19.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					jl15.setText("");
					jl16.setText("");
					return;
				} else if (!password.equals(password2)) {
					jl13.setText("两次密码不匹配");
					jl11.setText("");
					jl12.setText("");
					jl19.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					jl15.setText("");
					jl16.setText("");
					return;
				} else if (!email.equals("") && !flag1) {
					jl15.setText("邮箱不合法");
					jl11.setText("");
					jl12.setText("");
					jl13.setText("");
					jl16.setText("");
					jl19.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					return;
				} else if (!phone.equals("") && !flag2) {
					jl16.setText("手机号不合法");
					jl11.setText("");
					jl12.setText("");
					jl13.setText("");
					jl15.setText("");
					jl19.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					return;
				} else if (question.equals("") || question == null) {
					jl19.setText("密保问题未设置");
					jl11.setText("");
					jl12.setText("");
					jl13.setText("");
					jl20.setText("");
					jp1.setText("");
					jp2.setText("");
					jl15.setText("");
					jl16.setText("");
					return;
				} else if (answer.equals("") || answer == null) {
					jl20.setText("密保答案未设置");
					jl11.setText("");
					jl12.setText("");
					jl13.setText("");
					jl19.setText("");
					jp1.setText("");
					jp2.setText("");
					jl15.setText("");
					jl16.setText("");
					return;
				}
				System.out.println("start：" + answer);
				// 验证已完成 开始向服务器发送注册请求 这里jknum均赋值为0 请求到了服务器端再进行重新分配
				Jkuser jkuser = new Jkuser(0, name, password2, signature, null,
						site, phone, email, 0, question, answer, sex);
				System.out.println("next：" + jkuser.getAnswer());
				String s = "服务器链接失败!";
				if (ClientConnection.getIns().conn2Server()) {
					try {
						int jknum = ClientConnection.getIns().regServer(jkuser);
						if (jknum == -1) {
							s = "注册失败,错识码:" + jknum;
						} else {
							s = "注册成功,你的JK号:" + jknum;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(jf_reg, s);
				conn.closeMe();
				jf_reg.dispose();

			}
		});

		jf_reg.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jf_reg.dispose();
			}
		});

		jf_reg.setResizable(false);
		jf_reg.setLocationRelativeTo(null);
		jf_reg.setVisible(true);
	}

	/**
	 * 客户端的开启
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.showLoginUI();
	}

}
