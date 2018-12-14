package com.kingyee.common.mail;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMultipart;

import com.kingyee.common.util.TimeUtil;
import com.sun.mail.smtp.SMTPAddressFailedException;

/**
 * HTML邮件
 * 
 * @author peihong
 * 
 */
public class HtmlMail extends Mail {

	/**
	 * 文本邮件构造函数
	 * 
	 * @param smtp
	 * @param username
	 * @param pwd
	 */
	public HtmlMail(String smtp, String un, String pwd) {
		super(smtp, un, pwd);
		super.multipart = new MimeMultipart();
	}

	/**
	 * @see Mail
	 */
	public void setContent(String content) throws MessagingException {
		super.messageBodyPart.setContent(content, "text/html;charset=UTF-8");
		super.multipart.addBodyPart(messageBodyPart);
	}

	public static void main(String[] args) throws MessagingException {
		try{
	//		Mail mail = MailHelper.buildTextMail(Const.EMAIL_SMTP, Const.EMAIL_FROM_ADDRESS, Const.EMAI_FORM_PWD);
	//		mail.setFromAddress(Const.EMAIL_FROM_ADDRESS);
			Mail mail = MailHelper.buildHtmlMail("smtp.163.com", "kingyeeAppTest@163.com", "kingyee");
			// 发送地址
			mail.setFromAddress("kingyeeAppTest@163.com");
			// 接受地址
			mail.setToAddress(new String[] { "ph@kingyee.com.cn","lpeng@kingyee.com.cn" });
			// 发送时间
			mail.setSendDate(TimeUtil.stringToDate("2010-05-30 11:45", "yyyy-MM-dd HH:mm"));
			// 标题
			mail.setSubject("gga");
			// 邮件内容.
			mail.setContent("你今天吃了么？");
			MailHelper.sendMail(mail);
		} catch (SMTPAddressFailedException e) {
			throw e;
		} catch (SendFailedException e) {
			throw new SendFailedException("医脉通用户注册邮件发送失败！");
		}
	}
}
