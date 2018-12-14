package com.kingyee.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RC4Util {
	
    private static Logger log = LoggerFactory.getLogger(RC4Util.class);
	
	public static final int KEY_LENGTH = 8;
	
	public static String SUFFIX = "@#kingyee#@";

	/**
	 * 数据转化 byte-hex
	 * 
	 * @param bs
	 * @return
	 */
	public static String bytesToHexString(byte[] bs) {
		StringBuffer sb = new StringBuffer();
		String hex = "";
		for (int i = 0; i < bs.length; i++) {
			hex = Integer.toHexString(bs[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 数据转化 hex-byte
	 * 
	 * @param in
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] hexStringToBytes(String in) throws UnsupportedEncodingException {
		byte[] arrB = in.getBytes("utf-8");
		int iLen = arrB.length;
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	private static byte[] initRc4Key(byte[] b_key) {
		byte state[] = new byte[256];

		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}
		int index1 = 0;
		int index2 = 0;
		if (b_key == null || b_key.length == 0) {
			return null;
		}
		for (int i = 0; i < 256; i++) {
			index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
			byte tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;
			index1 = (index1 + 1) % b_key.length;
		}
		return state;
	}

	/**
	 * rc4算法
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] rc4(byte[] data, byte[] key) {
		int x = 0;
		int y = 0;
		int xorIndex;
		byte[] result = new byte[data.length];
		key = initRc4Key(key);
		for (int i = 0; i < data.length; i++) {
			x = (x + 1) & 0xff;
			y = ((key[x] & 0xff) + y) & 0xff;
			byte tmp = key[x];
			key[x] = key[y];
			key[y] = tmp;
			xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
			result[i] = (byte) (data[i] ^ key[xorIndex]);
		}
		return result;
	}
	
	/**
	 * 使用key，将content进行RC4加密
	 * @param content
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String content, String key) throws UnsupportedEncodingException{
		String encode = RC4Util.bytesToHexString(RC4Util.rc4(content.getBytes("utf-8"), key.getBytes("utf-8")));
		return encode;
	}
	
	/**
	 * 使用key，将encodedStr进行RC4解密
	 * @param encodedStr
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String encodedStr, String key) throws UnsupportedEncodingException{
		if(encodedStr == null){
			return "";
		}
		try{
			byte[] b = RC4Util.rc4(hexStringToBytes(encodedStr), RC4Util.hexStringToBytes(key));
			String decode = new String(b, "utf-8");
			return decode;
		}catch(UnsupportedEncodingException e){
			log.error("RC4解码失败", e);
			return encodedStr;
		}
	}
	
	/**
	 * 将content进行RC4加密。使用的key为自动随机生成的128位的key。并且在加密后的字符串最后，缀上suffix用来区分，是否此字符串是被RC4加密过得。
	 * 即最终返回结果为：RC4(content) + Hex(randomkey) +  Hex(RC4Util.SUFFIX)
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String content) throws UnsupportedEncodingException{
		String key = RandomUtil.generateString(128);
		String encode = RC4Util.bytesToHexString(RC4Util.rc4(content.getBytes("utf-8"), key.getBytes("utf-8")));
		encode = encode + RC4Util.bytesToHexString(key.getBytes("utf-8"))
				+ RC4Util.bytesToHexString(RC4Util.SUFFIX.getBytes("utf-8"));

		return encode;
	}
	
	/**
	 * 将content进行RC4解密。通过解密以下结构字符串：RC4(content) + Hex(randomkey) +  Hex(RC4Util.SUFFIX)。
	 * 如果不是以RC4Util.SUFFIX结尾的字符串，则直接返回本身的encodedStr
	 * @param encodedStr 待解密字符串
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String encodedStr){
		if(encodedStr == null){
			return "";
		}
		try{
			String suffix = RC4Util.bytesToHexString(RC4Util.SUFFIX.getBytes("utf-8"));
			if(encodedStr.endsWith(suffix)){
				int len = RC4Util.bytesToHexString(RC4Util.SUFFIX.getBytes("utf-8")).length();
				String key = encodedStr.substring(encodedStr.length() - 256 - len, encodedStr.length() - len);
				String content = encodedStr.substring(0, encodedStr.length() - 256 - len);
				byte[] b = RC4Util.rc4(hexStringToBytes(content), RC4Util.hexStringToBytes(key));
				String decode = new String(b, "utf-8");
				return decode;
			}else{
				return encodedStr;
			}
		}catch(UnsupportedEncodingException e){
			log.error("RC4解码失败", e);
			return encodedStr;
		}
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println(RC4Util.encode4RandomKeyAndSuffix("ph@kingyee.com.cn"));
		System.out.println(RC4Util.encode("2015/04/16", "@#44591@#864828022098155"));
		//System.out.println(RC4Util.decode("ph@kingyee.com.cn", "1234"));
	}
}
