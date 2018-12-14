package com.kingyee.common.util;


public class PropertyConst {


	/**系统环境，开发(dev)OR测试(test)OR正式(product)*/
	public static String ENVIRONMENT = PropertyUtil.getPropertyValue("environment");
    /** 图片上传路径*/
    public static String PIC_PATH = PropertyUtil.getPropertyValue("pic.path");
    /** 微信头像路径*/
    public static String HEADIMG_PATH = PropertyUtil.getPropertyValue("headimg.path");
    /** 二维码路径*/
    public static String QRCODE_PATH = PropertyUtil.getPropertyValue("qrcode.path");
	/** domain*/
	public static String DOMAIN = PropertyUtil.getPropertyValue("domain");
	/** PPT上传路径*/
	public static String PPT_PATH = PropertyUtil.getPropertyValue("ppt.path");
	/** ppt转换图片后的图片路径*/
	public static String PPT_IMAGE_PATH = PropertyUtil.getPropertyValue("ppt.image.path");
	/** 上传待转换的ppt路径*/
	public static String PPT_BACKUPS_IMAGE_PATH = PropertyUtil.getPropertyValue("ppt.image.ppt.path");

	/** smtp地址*/
	public static String EMAIL_SMTP = PropertyUtil.getPropertyValue("mail.smtp");
	/** smtp用户名*/
	public static String EMAIL_FROM_ADDRESS = PropertyUtil.getPropertyValue("mail.username");
	/** smtp密码*/
	public static String EMAI_FORM_PWD = PropertyUtil.getPropertyValue("mail.password");
	/** 发送邮件开关，控制提醒邮件的发送,取值为on/off */
	public static String MAIL_SWTICH = PropertyUtil.getPropertyValue("email.swtich");

	/** 音频文件上传路径*/
	public static String AUDIO_PATH = PropertyUtil.getPropertyValue("audio.path");
}
