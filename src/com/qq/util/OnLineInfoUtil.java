package com.qq.util;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 * 上线弹窗工具类
 *
 */
public class OnLineInfoUtil {
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
	private JLabel sure = null;
	private String titleT = null;
	private String word = null;
	private String time = null;
	private SimpleDateFormat sdf = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
		sure = new JLabel("确定");
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
			public void mouseClicked(MouseEvent e) {
				tw.close();
			}
			public void mouseEntered(MouseEvent e) {
				sure.setBorder(BorderFactory.createLineBorder(Color.gray));
			}
			public void mouseExited(MouseEvent e) {
				sure.setBorder(null);
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
		time = sdf.format(new Date());
		init();
		handle();
		tw.setAlwaysOnTop(true);
		tw.setUndecorated(true);
		tw.setResizable(false);
		tw.setVisible(true);
		tw.run();
	}
}

class TipWindow1 extends JDialog {
	private static final long serialVersionUID = 8541659783234673950L;
	private static Dimension dim;
	private int x, y;
	private int width, height;
	private static Insets screenInsets;

	public TipWindow1(int width, int height) {
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
		// 此处代码用来实现让消息提示框5秒后自动消失
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		close();
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