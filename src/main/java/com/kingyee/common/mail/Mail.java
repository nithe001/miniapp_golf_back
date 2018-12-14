/*
 * Created on 2005-11-14
 */
package com.kingyee.common.mail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

/**
 * 基础邮件格式
 * 
 * @author 马劼
 */
public abstract class Mail {

	/** BodyPart */
	protected BodyPart messageBodyPart = null;

	/** Multipart */
	protected Multipart multipart = null;

	/** MimeMessage */
	protected MimeMessage mailMessage = null;

	/** MimeMessage */
	protected Session mailSession = null;

	/** MimeMessage */
	protected Properties mailProperties = System.getProperties();

	/** MimeMessage */
	protected InternetAddress fromAddress = null;

	/** InternetAddress */
	protected InternetAddress toAddress = null;

	/** MailAuthenticator */
	protected MailAuthenticator authenticator = null;

	/** Mail Topic */
	protected String mailSubject = null;

	/** Date */
	protected Date mailSendDate = null;

	/** 附件 */
	protected List<String> file = null;

	/**
	 * 默认构造
	 * 
	 * @param smtp
	 * @param username
	 * @param password
	 */
	public Mail(String smtp, String un, String pwd) {
		this.mailProperties.put("mail.smtp.host", smtp);
		this.mailProperties.put("mail.smtp.auth", "true");
		this.mailSession = Session.getInstance(mailProperties, new MailAuthenticator(un, pwd));
		this.mailMessage = new MimeMessage(mailSession);
		this.messageBodyPart = new MimeBodyPart();
		this.file = new ArrayList<String>();
	}

	/**
	 * Set mail subject.
	 * 
	 * @param subject
	 * @throws MessagingException
	 */
	public void setSubject(String subject) throws MessagingException {
		if (subject == null) {
			this.mailSubject = "";
		} else {
			this.mailSubject = subject;
		}
		this.mailMessage.setSubject(this.mailSubject);
	}

	/**
	 * 添加附件
	 * 
	 * @param name
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void addFile(String name) throws IOException, MessagingException {
		if (StringUtils.isNotEmpty(name)) {
			this.file.add(name);
		}
	}

	/**
	 * Set MailContent
	 * 
	 * @param strContent
	 * @throws MessagingException
	 */
	public abstract void setContent(String strContent) throws MessagingException;

	/**
	 * Set send date.
	 * 
	 * @param sendDate
	 * @throws MessagingException
	 */
	public void setSendDate(Date sendDate) throws MessagingException {
		this.mailSendDate = sendDate;
		this.mailMessage.setSentDate(sendDate);
	}

	/**
	 * Set FromAddress
	 * 
	 * @param fromAddress
	 * @throws MessagingException
	 */
	public void setFromAddress(String fromAddress) throws MessagingException {
		this.fromAddress = new InternetAddress(fromAddress);
		this.mailMessage.setFrom(this.fromAddress);
	}

	/**
	 * Set ToAddress
	 * 
	 * @param toAddress
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setToAddress(String[] toAddress) throws AddressException, MessagingException {
		InternetAddress[] address = new InternetAddress[toAddress.length];
		for (int i = 0; i < toAddress.length; i++) {
			// this.toAddress = new InternetAddress(toAddress[i]);
			// this.mailMessage.addRecipient(Message.RecipientType.TO,
			// this.toAddress);
			address[i] = new InternetAddress(toAddress[i]);
		}
		this.mailMessage.setRecipients(Message.RecipientType.TO, address);
	}

	/**
	 * Set toAddress
	 * 
	 * @param toAddress
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setToAddress(List<String> toAddress) throws AddressException, MessagingException {
		InternetAddress[] address = new InternetAddress[toAddress.size()];
		for (int i = 0; i < toAddress.size(); i++) {
			address[i] = new InternetAddress(toAddress.get(i));
		}
		this.mailMessage.setRecipients(Message.RecipientType.TO, address);
	}

	/**
	 * Send mail.
	 * 
	 * @throws MessagingException
	 */
	public void sendMail() throws MessagingException {
		if (!this.file.isEmpty()) {
			for (String name : this.file) {
				try {
					MimeBodyPart part = new MimeBodyPart();
					part.attachFile(name);
					this.multipart.addBodyPart(part);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.mailMessage.setContent(multipart);
		Transport.send(mailMessage);
	}
	
	public void sendMail(String charset) throws MessagingException {
		if (!this.file.isEmpty()) {
			for (String name : this.file) {
				try {
					MimeBodyPart part = new MimeBodyPart();
					part.attachFile(name);//  D://手机.ppt
					File file = new File(name);
					part.setFileName(MimeUtility.encodeWord(file.getName(), charset, null));
					this.multipart.addBodyPart(part);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.mailMessage.setContent(multipart);
		Transport.send(mailMessage);
	}
}
