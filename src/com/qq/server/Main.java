package com.qq.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.qq.msg.IMsgConstance;
import com.qq.util.H2Server;
import com.qq.util.LogTools;

/**
 * 服务器创建类 创建服务器对象  并且等待客户端的连入 
 * @author yy
 *
 */
public class Main extends Thread {
	
	private int port;	//服务器端口
	private ServerSocket ss = null;
	private JFrame frame = null		;
	/**
	 * 创建服务器对象 并且传入端口号码
	 * @param port
	 */
	public Main(int port) {
		this.port = port;
	}
	
	public void run() {
		frame = new JFrame("服务器管理");
		frame.setLayout(null);
		frame.setBounds(0,0,400,200);
		
		JButton open = new JButton("开启服务器");
		JButton close = new JButton("关闭服务器");
		open.setBounds(60,30,100,40);
		close.setBounds(240,30,100,40);
		
		frame.add(open);
		frame.add(close);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ss != null) {
					JOptionPane.showMessageDialog(null, "服务器:"+ss.getLocalPort()+"已开启,勿重复开启");
				}else {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							setupServer();
						}
					}).start();
				}
			}
		});
		
		
		
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ss.close();
					ss = null;
					JOptionPane.showMessageDialog(null, "服务器已关闭");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
	}
	
	/**
	 * 在指定的端口上启动服务器
	 */
	private void setupServer() {
		try {
			ss = new ServerSocket(port);
			LogTools.INFO(this.getClass(), "服务器创建成功:" + port);
			JOptionPane.showMessageDialog(null, "服务器:"+ss.getLocalPort()+" 开启成功");
			
			System.out.println(11);
			
			File imgFile = new File("F:/QQimg"); 
			if(!imgFile.exists()) {
				imgFile.mkdirs();
				File img = new File("./images/default_header.jpg");
				FileInputStream fins = new FileInputStream(img);
				FileOutputStream fos = new FileOutputStream("F:/QQimg/default_header.jpg");
				byte[] data = new byte[1024];
				int len = 0;
				while((len = fins.read(data)) != -1) {
					fos.write(data, 0, len);
				}
				fos.close();
			}else {
				if(!new File("F:/QQimg/default_header.jpg").exists()) {
					File img = new File("./images/default_header.jpg");
					FileInputStream fins = new FileInputStream(img);
					FileOutputStream fos = new FileOutputStream("F:/QQimg/default_header.jpg");
					byte[] data = new byte[1024];
					int len = 0;
					while((len = fins.read(data)) != -1) {
						fos.write(data, 0, len);
					}
					fos.close();
					
					
				}
			}
			
			File file = new File("E:/h2/qq.mv.db");
			if(!file.exists()) {
				new H2Server().start();
			}
			
			/**
			 * 每当有一个客户端线程请求连接服务器  就启动一个线程去处理它
			 */
			while(true) {
				Socket client = ss.accept();
				String cAdd = client.getRemoteSocketAddress().toString();
				LogTools.INFO(this.getClass(), "进入连结:" + cAdd);
				ServerThread ct = new ServerThread(client);
				ct.start();		//启动一个线程去处理这个客户端
			}
		} catch (IOException e) {
			LogTools.ERROR(this.getClass(), "服务器创建失败:" + e);
		}
	}
	
	
	public static void main(String[] args) {
		Main main = new Main(IMsgConstance.serverPort);
		main.start();
	}
}
