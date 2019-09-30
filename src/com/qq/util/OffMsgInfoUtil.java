package com.qq.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.qq.client.CommunityTree;
import com.qq.model.ChatLog;
import com.qq.model.CommuChatLog;
import com.qq.model.Jkfile;


/**
 * 下线弹窗工具类
 *
 */
public class OffMsgInfoUtil {
	private Point oldP;										// 上一次坐标,拖动窗口时用
	private TipWindow tw = null;							// 提示框
	private ImageIcon img = null;							// 图像组件
	private JLabel imgLabel = null; 						// 背景图片标签
	private JPanel headPan = null;
	private JPanel feaPan = null;
	private JPanel btnPan = null;
	private JLabel head = null;								// 蓝色标题
	private JLabel close = null;							// 关闭按钮
	private JTextArea feature = null;						// 内容
	private JScrollPane jfeaPan = null;
	public JLabel sure = null;
	private String titleT = null;
	private String word = null;
	private String time = null;
	private ArrayList<ChatLog> cList;
	private ArrayList<CommuChatLog> cmuChatLog;
	private CommunityTree communityTree;
	private List<Jkfile> fileList = new ArrayList<Jkfile>();	//未处理的文件列表
	
	public OffMsgInfoUtil(List<ChatLog> cList,List<CommuChatLog> cmuChatLog, CommunityTree communityTree,
			ArrayList<Jkfile> fileList) {
		this.cList = (ArrayList<ChatLog>) cList;
		this.cmuChatLog = (ArrayList<CommuChatLog>) cmuChatLog;
		this.communityTree = communityTree;
		this.fileList = fileList;
	}
	
	
	public void init() {
		// 新建300x220的消息提示框
		tw = new TipWindow(300, 220);
		img = new ImageIcon("background.gif");
		imgLabel = new JLabel(img);
		// 设置各个面板的布局以及面板中控件的边界
		headPan = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		feaPan = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		btnPan = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		head = new JLabel(titleT);
		close = new JLabel(" x");
		feature = new JTextArea(word);
		jfeaPan = new JScrollPane(feature);
		sure = new JLabel("查看离线消息");
		sure.setHorizontalAlignment(SwingConstants.CENTER);

		// 将各个面板设置为透明，否则看不到背景图片
		((JPanel) tw.getContentPane()).setOpaque(false);
		headPan.setOpaque(false);
		feaPan.setOpaque(false);
		btnPan.setOpaque(false);

		// 设置JDialog的整个背景图片
		tw.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
		imgLabel.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
		headPan.setPreferredSize(new Dimension(300, 60));

		// 设置提示框的边框,宽度和颜色
		tw.getRootPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

		close.setFont(new Font("Arial", Font.BOLD, 15));
		close.setPreferredSize(new Dimension(20, 20));
		close.setVerticalTextPosition(JLabel.CENTER);
		close.setHorizontalTextPosition(JLabel.CENTER);
		close.setCursor(new Cursor(12));
		close.setToolTipText("关闭");

		head.setPreferredSize(new Dimension(250, 35));
		head.setVerticalTextPosition(JLabel.CENTER);
		head.setHorizontalTextPosition(JLabel.CENTER);
		head.setFont(new Font("宋体", Font.PLAIN, 12));
		head.setForeground(Color.blue);

		feature.setEditable(false);
		feature.setForeground(Color.red);
		feature.setFont(new Font("宋体", Font.PLAIN, 13));
		feature.setBackground(new Color(184, 230, 172));
		// 设置文本域自动换行
		feature.setLineWrap(true);

		jfeaPan.setPreferredSize(new Dimension(250, 80));
		jfeaPan.setBorder(null);
		jfeaPan.setBackground(Color.black);

		// 为了隐藏文本域，加个空的JLabel将他挤到下面去
		JLabel jsp = new JLabel();
		jsp.setPreferredSize(new Dimension(300, 25));

		sure.setPreferredSize(new Dimension(110, 30));
		// 设置标签鼠标手形
		sure.setCursor(new Cursor(12));
		headPan.add(close);
		headPan.add(head);

		feaPan.add(jsp);
		feaPan.add(jfeaPan);


		btnPan.add(sure);

		tw.add(headPan, BorderLayout.NORTH);
		tw.add(feaPan, BorderLayout.CENTER);
		tw.add(btnPan, BorderLayout.SOUTH);
	}

	public void handle() {
		// 为更新按钮增加相应的事件
		sure.addMouseListener(new MouseAdapter() {
			
			public void mouseEntered(MouseEvent e) {
				sure.setBorder(BorderFactory.createLineBorder(Color.gray));
			}
			public void mouseExited(MouseEvent e) {
				sure.setBorder(null);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//展示所有的离线消息
				final JFrame frame = new JFrame("离线消息提示框");
				frame.setBounds(0,0,500,400);
				// 设置标题头像
				ImageIcon icon = new ImageIcon("./images/logo.jpg");
				Image logo = icon.getImage();
				frame.setIconImage(logo);
				// 设置背景图片
				ImageIcon bg_icon = new ImageIcon("./images/reg_bg1.jpg");
				JLabel jl_bg = new JLabel(bg_icon);
				jl_bg.setBounds(0, 0, bg_icon.getIconWidth(), bg_icon.getIconHeight());
				// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
				JPanel imgPanel = (JPanel) frame.getContentPane();
				imgPanel.setOpaque(false);
				frame.getLayeredPane().setLayout(null);
				frame.getLayeredPane().add(jl_bg, new Integer(Integer.MIN_VALUE));
				// 设置imagePane的布局方式为绝对布局
				imgPanel.setLayout(null);
				
				final ArrayList<String> strList = new ArrayList<String>();
				
				for (int i = 0; i < cList.size(); i++) {
					ChatLog chatLog = cList.get(i);
					String str = chatLog.getSrcid() + " 在 " + chatLog.getSendtime().trim() + "  对你说:" + chatLog.getContent();
					strList.add(str);
				}
				
				for (int j = 0; j < fileList.size(); j++) {
					Jkfile file = fileList.get(j);
					String str = file.getUid()+" 在 "+file.getSendTime().trim().substring(5)+" 发来文件 "+file.getFilename().trim()+"    (接收请双击)";
					strList.add(str);
				}
		
				final JList list = new JList(strList.toArray());
				final JScrollPane pane = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				pane.setBounds(0,0,500,18*(cList.size()+fileList.size()));
				JButton btn = new JButton("查看所有未处理的群消息");
				btn.setBounds(140,18*cList.size()+30,220,30);
				
				
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							JList list2 = (JList) e.getSource();
							int index = list2.locationToIndex(e.getPoint());
							if(index >= cList.size()) {
								index-=cList.size();
								Jkfile jkfile = fileList.get(index);
								File file = jkfile.getFile();
								JFileChooser chooser = new JFileChooser();
								chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								File file2 = new File("G:/");
								chooser.setCurrentDirectory(file2);
								int num = chooser.showSaveDialog(chooser);
								if(num == chooser.APPROVE_OPTION) {
									String path = chooser.getSelectedFile().toString()+"/"+jkfile.getFilename();
									FileOutputStream fos = null;
									FileInputStream fins = null;
									try {
										fos = new FileOutputStream(path);
										fins = new FileInputStream(file);
										byte[] data = new byte[1024];
										while(fins.read(data) != -1) {
											fos.write(data);
											fos.flush();
										}
									} catch (FileNotFoundException e1) {
										e1.printStackTrace();
									} catch (IOException e1) {
										e1.printStackTrace();
									}finally {
										if(fos!=null) {
											try {
												fos.close();
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										}
										if(fins!= null) {
											try {
												fins.close();
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										}
									}
								}
							}
							
							String s = strList.get(index);
							strList.remove(s);
							frame.remove(pane);
							JList list1 = new JList(strList.toArray());
							JScrollPane pane = new JScrollPane(list1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							pane.setBounds(0,0,500,18*(strList.size()));
							frame.add(pane);
							pane.updateUI();
							frame.repaint();
						}
					}
				});
				
				
				btn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					
						communityTree.precessCommuMsg(cmuChatLog);
					}
				});
				
				
				frame.add(btn);
				frame.add(pane);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setLayout(null);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				
				
				
				
				tw.dispose();
			}
			
		});
		
		// 右上角关闭按钮事件
		close.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				tw.close();
			}
			public void mouseEntered(MouseEvent e) {
				close.setBorder(BorderFactory.createLineBorder(Color.gray));
			}
			public void mouseExited(MouseEvent e) {
				close.setBorder(null);
			}
		});
	}

	public void show(String titleT,String word){
		this.titleT = titleT;
		this.word = word;
		init();
		handle();
		tw.setAlwaysOnTop(true);
		tw.setUndecorated(true);
		tw.setResizable(false);
		tw.setVisible(true);
		tw.run();
	}
}

class TipWindow extends JDialog {
	private static final long serialVersionUID = 8541659783234673950L;
	private static Dimension dim;
	private int x, y;
	private int width, height;
	private static Insets screenInsets;

	public TipWindow(int width, int height) {
		this.width = width;
		this.height = height;
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				this.getGraphicsConfiguration());
		x = (int) (dim.getWidth() - width - 3);
		y = (int) (dim.getHeight() - screenInsets.bottom - 3);
		initComponents();
	}

	public void run() {
		for (int i = 0; i <= height; i += 10) {
			try {
				this.setLocation(x, y - i);
				Thread.sleep(5);
			} catch (InterruptedException ex) {
			}
		}
//		// 此处代码用来实现让消息提示框5秒后自动消失
//		while(true) {}
	}

	private void initComponents() {
		this.setSize(width, height);
		this.setLocation(x, y);
		this.setBackground(Color.gray);
	}

	public void close() {
		x = this.getX();
		y = this.getY();
		int ybottom = (int) dim.getHeight() - screenInsets.bottom;
		for (int i = 0; i <= ybottom - y; i += 10) {
			try {
				setLocation(x, y + i);
				Thread.sleep(5);
			} catch (InterruptedException ex) {
			}
		}
		dispose();
	}

}