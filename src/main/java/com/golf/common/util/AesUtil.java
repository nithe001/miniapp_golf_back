package com.golf.common.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

public class AesUtil {

	public static final String key = "76CAA1C88F7F8D1D";//十六位十六进制数作为秘钥

	private static final String CHARSET_NAME = "UTF-8";
	private static final String AES_NAME = "AES";
	private static final String ALGORITHM = "AES/CBC/PKCS7Padding";
	private static final String IV = "91129048100F0494";//十六位十六进制数作为秘钥偏移量

	private static Charset CHARSET = Charset.forName("utf-8");

	public static boolean initialized = false;

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

	static {
		Security.addProvider(new BouncyCastleProvider());
	}



	/**
	 * AES解密
	 *
	 * @param content
	 *            密文
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException
	 */
	public byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
		initialize();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			Key sKeySpec = new SecretKeySpec(keyByte, "AES");
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void initialize() {
		if (initialized)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialized = true;
	}

	// 生成iv
	public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
		params.init(new IvParameterSpec(iv));
		return params;
	}







	/**
	 * 加密
	 */
	public static String encrypt(String content, String key) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
			return parseByte2HexStr(cipher.doFinal(content.getBytes(CHARSET_NAME)));
		} catch (Exception ex) {
			throw new Exception("解密失败");
		}
	}

	/**
	 * 解密
	 */
	public static String decrypt(String content, String key) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
			return new String(cipher.doFinal(Objects.requireNonNull(parseHexStr2Byte(content))), CHARSET_NAME);
		} catch (Exception ex) {
			throw new Exception("解密失败");
		}
	}


	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}




	public static byte[] parseHexStr2Byte(String hexStr){
		if(hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length()/2];
		for (int i = 0;i< hexStr.length()/2; i++) {
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}








	public static void main(String[] args) throws Exception {
//		String key = RandomUtil.generateString(43);
//		System.out.println("加密key=="+key);
//		String text = "8fhByJqbCdjJEA0UGFxqv4q8lZQ2OYP+2+yNgkyLHyM=";
		String text = "xmi6SRDCSbpKxU2BNGKS210eExZU7qeYhagmrOwMbx8%3D%0D%0A";
//		String text = "2015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/162015/04/16";
		String key = "xHIAiqiOshfEOY2EHt7KIZQuyqnocyFir9ZBJ7tDSJg";
		// System.out.println(RC4Util.encode4RandomKeyAndSuffix("nmy@golf.com.cn"));
//		System.out.println("==" + AesUtil.urlEncoder(text, key));
		System.out.println("=="+ AesUtil.urlDecoder(text, key));
		System.out.println("=="+ AesUtil.decode(text, key));
		System.out.println(URLDecoder.decode(text, "UTF-8"));
	}
}
