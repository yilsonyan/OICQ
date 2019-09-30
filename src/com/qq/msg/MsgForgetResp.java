package com.qq.msg;

/**
 * 密码忘记回应消息
 * @author yy
 *
 */
public class MsgForgetResp extends MsgHead {
	
	@Override
	public String toString() {
		return "MsgForgetResp [question=" + question + ", answer=" + answer
				+ "]";
	}
	private String question;	//密保问题
	private String answer;	//密保答案
	
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
