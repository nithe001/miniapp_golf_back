/*
 * Created on 2005-11-14
 *
 */
package com.kingyee.common.mail;

import javax.mail.MessagingException;

/**
 * 邮件发送助手
 * 
 * @author 马劼
 */
public class MailHelper {

	/**
	 * 获得文本型邮件.
	 * 
	 * @param smtp
	 *          smtp地址
	 * @param un
	 *          smtp用户名
	 * @param pwd
	 *          smtp密码
	 * @return TextMail 文本型邮件.
	 */
	public static TextMail buildTextMail(String smtp, String un, String pwd) {
		return new TextMail(smtp, un, pwd);
	}
	
	/**
	 * 获得HTML型邮件.
	 * 
	 * @param smtp
	 *          smtp地址
	 * @param un
	 *          smtp用户名
	 * @param pwd
	 *          smtp密码
	 * @return TextMail 文本型邮件.
	 */
	public static HtmlMail buildHtmlMail(String smtp, String un, String pwd) {
		return new HtmlMail(smtp, un, pwd);
	}

	/**
	 * 发送邮件
	 * 
	 * @param toAddress
	 * @param mail
	 * @throws MessagingException
	 */
	public static void sendMail(Mail mail)
	    throws MessagingException {
		mail.sendMail();
	}
	
	/**
	 * 发送邮件 带编码
	 * @author SQ
	 * @date 2015年1月22日 下午2:13:27 
	 *
	 */
	public static void sendMail(Mail mail, String charset)
		    throws MessagingException {
			mail.sendMail(charset);
		}

}
