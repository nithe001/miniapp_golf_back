/*
 * Created on 2005-7-13
 *
 * 时间工具类.
 */
package com.golf.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Random;


/**
 * 微信用户昵称解码
 *
 * @author
 */
public class WechatNickNameUtil {

	/**
	 * 随机生成n个1-9的不重复的数
	 * @return Date
	 */
	public static String decodeUserNickName(String name) throws UnsupportedEncodingException {
		String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		Boolean isLegal = name.matches(base64Pattern);
		if (isLegal) {
			name = new String(Base64.decodeBase64(name.getBytes()),"utf-8");
			if(StringUtils.isNotEmpty(name)){
				System.out.println(name);
			}
		}
		return name;
	}

	public static void main(String[] args) {
	}
}