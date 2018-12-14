package com.kingyee.common.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesUtil {
	
	private static Charset CHARSET = Charset.forName("utf-8");
	
	public static String encode(String text, String key)
			throws Exception {
		
		if(key == null || key.length() != 43){
			throw new Exception("秘钥长度必须为43个字符。");
		}
		
		byte[] aeskey = Base64.decodeBase64(key + "=");
		
		// 设置加密模式为AES的CBC模式
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(aeskey, "AES");
		IvParameterSpec iv = new IvParameterSpec(aeskey, 0, 16);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

		// 加密
		byte[] encrypted = cipher.doFinal(text.getBytes(CHARSET));

		// 使用BASE64对加密后的字符串进行编码
		String base64Encrypted = Base64.encodeBase64String(encrypted);

		return base64Encrypted;
	}

	public static String decode(String text, String key)
			throws Exception {
		
		if(key == null || key.length() != 43){
			throw new Exception("秘钥长度必须为43个字符。");
		}
		byte[] aeskey = Base64.decodeBase64(key + "=");
		
		// 设置解密模式为AES的CBC模式
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec key_spec = new SecretKeySpec(aeskey, "AES");
		IvParameterSpec iv = new IvParameterSpec(aeskey, 0, 16);
		cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

		// 使用BASE64对密文进行解码
		byte[] encrypted = Base64.decodeBase64(text);

		// 解密
		byte[] original = cipher.doFinal(encrypted);
		String decode = new String(original, CHARSET);
		return decode;
	}

	public static String urlEncoder(String text, String key)throws Exception{
		return URLEncoder.encode(AesUtil.encode(text, key), "UTF-8");
	}
	
	public static String urlDecoder(String text, String key)throws Exception{
		return AesUtil.decode(URLDecoder.decode(text, "UTF-8"), key);
	}
	
	public static void main(String[] args) throws Exception {
//		String key = RandomUtil.generateString(43);
//		System.out.println("加密key=="+key);
//		String text = "8fhByJqbCdjJEA0UGFxqv4q8lZQ2OYP+2+yNgkyLHyM=";
		String text = "xmi6SRDCSbpKxU2BNGKS210eExZU7qeYhagmrOwMbx8%3D%0D%0A";
//		String text = "2015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/16";
		String key = "xHIAiqiOshfEOY2EHt7KIZQuyqnocyFir9ZBJ7tDSJg";
		// System.out.println(RC4Util.encode4RandomKeyAndSuffix("ph@kingyee.com.cn"));
//		System.out.println("==" + AesUtil.urlEncoder(text, key));
		System.out.println("=="+ AesUtil.urlDecoder(text, key));
		System.out.println("=="+ AesUtil.decode(text, key));
		System.out.println(URLDecoder.decode(text, "UTF-8"));
	}
}
