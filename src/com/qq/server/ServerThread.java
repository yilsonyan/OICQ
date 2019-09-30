package com.qq.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;

import com.qq.dao.JkuserDaoImpl;
import com.qq.model.Jkuser;
import com.qq.model.ToolsCreateMsg;
import com.qq.model.ToolsParseMsg;
import com.qq.msg.IMsgConstance;
import com.qq.msg.MsgChangePwd;
import com.qq.msg.MsgChangePwdResp;
import com.qq.msg.MsgForgetResp;
import com.qq.msg.MsgHead;
import com.qq.msg.MsgHeaderUploadResp;
import com.qq.msg.MsgLogin;
import com.qq.msg.MsgLoginResp;
import com.qq.msg.MsgReg;
import com.qq.msg.MsgRegResp;
import com.qq.util.LogTools;
import com.qq.util.MD5Util;

/**
 * 服务器线程 处理一个特定的客户端
 * 
 * @author yy
 */
public class ServerThread extends Thread {

	private Socket client; // 表示连接的客户端对象
	private DataInputStream dins;
	private DataOutputStream dous;
	private Jkuser owerUser; // 这个线程处理的用户对象
	private boolean loginOk = false; // 是否成功登陆的标志

	public DataInputStream getDins() {
		return dins;
	}

	public void setDins(DataInputStream dins) {
		this.dins = dins;
	}

	public DataOutputStream getDous() {
		return dous;
	}

	public void setDous(DataOutputStream dous) {
		this.dous = dous;
	}

	public ServerThread(Socket client) {
		this.client = client;
	}

	/**
	 * 连接成功后 读取第一条客户端消息 可能是注册 登陆 或者忘记密码
	 */
	@SuppressWarnings("unused")
	private boolean readFirstMsg() {
		try {
			// 包装为可读写原始数据类型的输入输出流
			this.dous = new DataOutputStream(client.getOutputStream());
			this.dins = new DataInputStream(client.getInputStream());

			MsgHead msg = reciveData(); // 读取第一条消息:
			if (msg.getType() == IMsgConstance.command_login) {// 如果是登陆请求
				MsgLogin ml = (MsgLogin) msg;
				Jkuser jkuser = checkLogin(ml);
				MsgLoginResp loginResp = new MsgLoginResp();
				loginResp.setTotalLength(14);
				loginResp.setSrc(IMsgConstance.Server_JK_NUMBER);
				loginResp.setType(IMsgConstance.command_login_resp);
				if(jkuser == null) {
					loginResp.setDest(0);
					loginResp.setState((byte)1);
					//send to client
					this.sendMsg2Me(loginResp);
					return false;
				}else {
					this.owerUser = jkuser;
					loginResp.setState((byte)0);
					loginResp.setDest(jkuser.getJknum());
					this.sendMsg2Me(loginResp);
					ObjectOutputStream oos = new ObjectOutputStream(dous);
					oos.writeObject(jkuser);
					oos.flush();
					return true;
				}
			}
			if (msg.getType() == IMsgConstance.command_reg) {// 如果是注册请求
				MsgReg reg = (MsgReg) msg;
				JkuserDaoImpl daoImpl = new JkuserDaoImpl();
				int jknum = daoImpl.regUser(reg.getJkuser());
				MsgRegResp msgRegResp = new MsgRegResp((byte) 0, jknum);
				msgRegResp.setTotalLength(18);
				msgRegResp.setType(IMsgConstance.command_reg_resp);
				msgRegResp.setSrc(IMsgConstance.Server_JK_NUMBER);
				msgRegResp.setDest(jknum);
				
				// 发送消息到客户端
				this.sendMsg2Me(msgRegResp);
			}
			if(msg.getType() == IMsgConstance.command_forgetPwd) {
				JkuserDaoImpl daoImpl = new JkuserDaoImpl();
				Jkuser user = daoImpl.getBasicInfo(msg.getSrc());
				MsgForgetResp forgetResp = new MsgForgetResp();
				forgetResp.setTotalLength(613);
				forgetResp.setType(IMsgConstance.command_forgetPwd_resp);
				if(user == null) {
					forgetResp.setSrc(0);
					forgetResp.setQuestion("");
					forgetResp.setAnswer("");
				}else {
					forgetResp.setSrc(IMsgConstance.Server_JK_NUMBER);
					String question = user.getQuestion().trim();
					String answer = user.getAnswer().trim();
					forgetResp.setQuestion(question);
					forgetResp.setAnswer(answer);
				}
				forgetResp.setDest(msg.getSrc());
				System.out.println(forgetResp);
				this.sendMsg2Me(forgetResp);
				
			}
			if(msg.getType() == IMsgConstance.command_changePwd) {
				System.out.println("---------------");
				int srcNum  =msg.getSrc();
				MsgChangePwd changePwd = (MsgChangePwd) msg;
				System.out.println("---"+changePwd);
				String newPwd  = changePwd.getNewPwd();
				JkuserDaoImpl daoImpl = new JkuserDaoImpl();
				int status = daoImpl.changePwd(srcNum, newPwd);
				
				
				MsgChangePwdResp changePwdResp = new MsgChangePwdResp();
				changePwdResp.setTotalLength(14);
				changePwdResp.setType(IMsgConstance.command_changePwd_resp);
				changePwdResp.setSrc(IMsgConstance.Server_JK_NUMBER);
				changePwdResp.setDest(srcNum);
				if(status == 1) {
					changePwdResp.setState((byte) 1);
				}else {
					changePwdResp.setState((byte) 0);
				}
				this.sendMsg2Me(changePwdResp);
			}
			
		} catch (Exception ef) {
			LogTools.ERROR(this.getClass(), "readFirstMsg失败:" + ef);
		}
		return false;
	}

	/**
	 * 检查登陆是否成功
	 * @param ml	接受的MsgLogin对象
	 * @return
	 */
	private Jkuser checkLogin(MsgLogin ml) {
		JkuserDaoImpl impl = new JkuserDaoImpl();
		Jkuser user = impl.checkLogin(ml.getSrc(), MD5Util.MD5(ml.getPassword()),ml.getState());
		return user;
	}

	// 线程中执行接收消息的方法
	public void run() {
		try {
			/**
			 * 读取第一条消息，这个时候称之为：同步消息，等待的消息，必须等消息到达读到才能进行后续代码执行。
			 * 有三种可能：1 登陆 2 注册 3 忘记密码
			 */
			while(!loginOk) {
				loginOk = readFirstMsg(); // 读取第一条消息
			}

			if (loginOk) {
				// 如果登陆成功，将这个处理线程对象加入到队列中
				ChatTools.addClient(this.owerUser, this);
			}
			while (loginOk) {
				MsgHead msg = this.reciveData();// 循环接收消息
				ChatTools.sendMsg2One(this.owerUser, msg);// 分发这条消息
			}
		} catch (Exception ef) {
			LogTools.ERROR(this.getClass(), "服务器读消息出错:" + ef);
		}
		// 用户离线了,从队列中移除这个用户对应的处理线程对象
		if(loginOk) {
			ChatTools.removeClient(this.owerUser);
		}

	}

	/**
	 * 从输入流上读取数据块,解包为消息对象
	 * 
	 * @return:将读到的数据块解析为消息对象
	 */
	private MsgHead reciveData() throws Exception {
		int len = dins.readInt(); // 读取消息长度
		LogTools.INFO(this.getClass(), "服务器读消息长度:" + len);
		byte[] data = new byte[len - 4];
		dins.readFully(data);
		/**
		 * 现在data数组中有数据了 接下来就是将data字节数组中的数据解析到对应的消息领域对象中
		 */
		MsgHead msg = ToolsParseMsg.parseMsg(data);// 解析为消息对象
		LogTools.INFO(this.getClass(), "服务器读到消息对象:" + msg);
		System.out.println("type:"+msg.getType());
		return msg;
	}

	/**
	 * 发送一条消息对象给这个对象所代表的客户端用户
	 * 
	 * @param msg
	 *            :要发送的消息对象
	 * @return:是否发送成功
	 */
	public boolean sendMsg2Me(MsgHead msg) {
		try {
			byte[] data = ToolsCreateMsg.packMsg(msg);// 将消息对象打包为字节组
			this.dous.write(data);
			this.dous.flush();
			LogTools.INFO(this.getClass(), "服务器发出消息对象:" + msg + "len:" + msg.getTotalLength());
			
			ByteArrayInputStream bins = new ByteArrayInputStream(data);
			DataInputStream dins = new DataInputStream(bins);
			return true;
		} catch (Exception ef) {
			LogTools.ERROR(this.getClass(), "服务器发出消息出错:" + msg);
		}
		return false;
	}

	/**
	 * 断开连结这个处理线程与客户机的连结, 发生异常,或处理线程退出时调用
	 */
	public void disConn() {
		try {
			this.client.close();
		} catch (Exception ef) {
		}
	}
}
