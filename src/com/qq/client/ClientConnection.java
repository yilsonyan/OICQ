package com.qq.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.qq.model.ChatLog;
import com.qq.model.Community;
import com.qq.model.Jkuser;
import com.qq.model.ToolsCreateMsg;
import com.qq.model.ToolsParseMsg;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgChangePwd;
import com.qq.msg.MsgChangePwdResp;
import com.qq.msg.MsgChatText;
import com.qq.msg.MsgCommuChatText;
import com.qq.msg.MsgFind;
import com.qq.msg.MsgFindResp;
import com.qq.msg.MsgForgetResp;
import com.qq.msg.MsgHead;
import com.qq.msg.MsgHeaderUpload;
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.msg.MsgLogin;
import com.qq.msg.MsgLoginResp;
import com.qq.msg.MsgReg;
import com.qq.msg.MsgRegResp;
import com.qq.util.LogTools;
import com.qq.util.MD5Util;

/**
 * QQ客户端的通信模块,提供： 1.登陆，注册接口调用； 2.在独立线程中接收服务器消息 3.将接收到的消息分发给监听器对象
 */
public class ClientConnection extends Thread {

	private static final ClientConnection ins = new ClientConnection();// 本类单实例对象
	private Socket client; // 与服务器的连结对象
	private DataOutputStream dous;// 输出流对象
	private DataInputStream dins;// 输入流对象
	public DataInputStream getDins() {
		return dins;
	}

	public void setDins(DataInputStream dins) {
		this.dins = dins;
	}



	private List<IClientMsgListener> listeners = new ArrayList<IClientMsgListener>(); // 装载所有的监听器对象
	
	/** 不需要创建对象,所以构造器私有 */
	private ClientConnection() {

	}

	// 单实例对象访问方法
	public static ClientConnection getIns() {
		return ins;
	}

	/** 连结上服务器,是否连结成功 */
	public boolean conn2Server() {
		try {
			// 1.创建一个到服务器端的Socket对象
			client = new Socket(IMsgConstance.serverIP,
					IMsgConstance.serverPort);
			// 2.得到输入输出流对象
			// 3.包装为可读写原始数据类型的输入输出流
			this.dous = new DataOutputStream(client.getOutputStream());
			this.dins = new DataInputStream(client.getInputStream());
			return true;
		} catch (Exception ef) {
			ef.printStackTrace();
		}
		return false;
	}

	public DataOutputStream getDous() {
		return dous;
	}

	public void setDous(DataOutputStream dous) {
		this.dous = dous;
	}

	/**
	 * 将注册的用户信息提交到服务器 等待服务器的响应 返回jknum
	 * 
	 * @param jkuser
	 * @return
	 * @throws Exception
	 */
	public int regServer(Jkuser jkuser) throws Exception {
		int state = 0;
		MsgReg msgReg = new MsgReg();
		msgReg.setTotalLength(250);
		msgReg.setType(IMsgConstance.command_reg);
		msgReg.setDest(IMsgConstance.Server_JK_NUMBER);
		msgReg.setSrc(0);
		msgReg.setJkuser(jkuser);
		this.sendMsg(msgReg);
		// 发送了登陆请求之后,必须读到一条应答的消息
		MsgHead regResp = readFromServer();
		MsgRegResp resp = (MsgRegResp) regResp;

		if (resp.getState() == 0) {
			return resp.getDest();
		}

		return -1;

	}

	/**
	 * 不断接受服务器传来的消息 并且交给监听器处理
	 */
	public void run() {
		while (true) {
			try {
				// 接收一条消息
				MsgHead m = readFromServer();
				// 将消息分发给监听器去处理
				for (IClientMsgListener lis : listeners) {
					lis.fireMsg(m);
				}
			} catch (Exception ef) {
				ef.printStackTrace();
				break; // 如果读取出错,则退出
			}
		}
		LogTools.INFO(this.getClass(), "客户端接收线程己退出!");

	}

	
	
	/**
	 * 从输入流上读取一条服务器端发来的消息 这个方法会阻塞，必须在独立线程中
	 * 
	 * @return:读取到的消息对象
	 */
	public  MsgHead readFromServer() throws Exception {
		int totalLen = dins.readInt();
		LogTools.INFO(this.getClass(), "客户端读到消息总长为:" + totalLen);
		byte[] data = new byte[totalLen - 4];
		dins.readFully(data); // 读取数据块
		MsgHead msg = ToolsParseMsg.parseMsg(data);// 解包为消息对象
		LogTools.INFO(this.getClass(), "客户端收到消息:" + msg);
		return msg;
	}

	/** 发送一条消息到服务器的方法 */
	public void sendMsg(MsgHead msg) throws Exception {
		LogTools.INFO(this.getClass(), "客户端发出消息:" + msg);
		byte[] data = ToolsCreateMsg.packMsg(msg);// 打包对象为数据块
		this.dous.write(data);// 发送
		this.dous.flush();
	}

	
	/**
	 * 向服务器端发送忘记密码请求消息 并得到服务器端的回应
	 * @param jknum
	 * @return
	 */
	public String[] forgetPwd(int jknum) {
		String[] str = new String[2];
		//向服务器端发送请求
		MsgHead forgetPwd = new MsgHead();
		forgetPwd.setTotalLength(13);
		forgetPwd.setSrc(jknum);
		forgetPwd.setDest(IMsgConstance.Server_JK_NUMBER);
		forgetPwd.setType(IMsgConstance.command_forgetPwd);
		try {
			this.sendMsg(forgetPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			MsgHead head = readFromServer();
			MsgForgetResp forgetResp = (MsgForgetResp) head;
			if(forgetResp.getSrc() == 0) return null;
			str[0] = forgetResp.getQuestion().trim();
			str[1] = forgetResp.getAnswer().trim();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;
		
	}
	
	
	/**
	 * 修改密码 成功返回1 失败的话返回0
	 * @param srcNum
	 * @param newPwd
	 */
	public byte changePwd(int srcNum, String newPwd) {
		MsgChangePwd changePwd = new MsgChangePwd();
		changePwd.setTotalLength(45);
		changePwd.setType(IMsgConstance.command_changePwd);
		changePwd.setSrc(srcNum);
		changePwd.setDest(IMsgConstance.Server_JK_NUMBER);
		changePwd.setNewPwd(MD5Util.MD5(newPwd));
		try {
			this.sendMsg(changePwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MsgChangePwdResp changePwdResp = null;
		
		try {
			MsgHead head = readFromServer();
			changePwdResp = (MsgChangePwdResp) head;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return changePwdResp.getState();
	}
	
	
	/**
	 * 为连结对象加入一个消息处理监听器对象
	 * 
	 * @param l
	 *            :消息处理监听器对象
	 */
	public void addMsgListener(IClientMsgListener l) {
		this.listeners.add(l);
	}

	// 关闭与一个客户机的连结
	public void closeMe() {
		try {
			this.client.close();
		} catch (Exception ef) {
		}
	}

	/**
	 * 向某个好友发送消息
	 * 
	 * @param srcJknum
	 * @param destJknum
	 * @param content
	 * @return
	 */
	public int sendMsg2One(int srcJknum, int destJknum, String content,
			String sendTime) {
		MsgChatText chatText = new MsgChatText();
		chatText.setTotalLength(98);
		chatText.setSrc(srcJknum);
		chatText.setDest(destJknum);
		chatText.setCharTxt(content);
		chatText.setSendTime(sendTime);
		chatText.setType(IMsgConstance.command_chatText);
		try {
			this.sendMsg(chatText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 向服务器发送群聊信息
	 * @param srcNum
	 * @param destCid
	 * @param chatTxt
	 * @param sendTime
	 */
	public void sendCommuTxt(int srcNum, int destCid, String chatTxt, String sendTime) {
		MsgCommuChatText chatText = new MsgCommuChatText();
		chatText.setTotalLength(102);
		chatText.setSrc(srcNum);
		chatText.setDest(IMsgConstance.Server_JK_NUMBER);
		chatText.setchatTxt(chatTxt);
		chatText.setDestCid(destCid);
		chatText.setType(IMsgConstance.command_commuChatTxt);
		chatText.setSendTime(sendTime);
		try {
			this.sendMsg(chatText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 向服务器发送登陆请求
	 * 
	 * @param jkNum
	 * @param pwd
	 * @return
	 */
	public Jkuser loginServer(int jkNum, String pwd, int state) {
		try {
			MsgLogin msgLogin = new MsgLogin();
			msgLogin.setTotalLength(49);
			msgLogin.setType(IMsgConstance.command_login);
			msgLogin.setSrc(jkNum);
			msgLogin.setState(state);
			msgLogin.setDest(IMsgConstance.Server_JK_NUMBER);
			msgLogin.setPassword(pwd);
			this.sendMsg(msgLogin);
			// 必须等到一条应答消息
			MsgHead loginResp = readFromServer();
			MsgLoginResp mlr = (MsgLoginResp) loginResp;
			if (mlr.getState() == 1) {
				return null;
			} else {
				ObjectInputStream oins = new ObjectInputStream(dins);
				Jkuser jkuser = new Jkuser();
				jkuser = (Jkuser) oins.readObject();
				System.out.println("已收到:" + jkuser);
				return jkuser;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
