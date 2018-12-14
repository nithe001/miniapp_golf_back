package com.kingyee.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyUtil {
    private static Logger log = LoggerFactory.getLogger(PropertyUtil.class);

	private static Hashtable<String, Properties> pptContainer = new Hashtable<String, Properties>();

	/** 缺省的property文件名称 */
	private static String defaultPropertyFile = "/configConst.properties";
	private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");


	public final static String getPropertyValue(String key) {
		Properties ppts = getProperties(defaultPropertyFile);
//		return ppts == null ? null : ppts.getProperty(key);
		return convertValue(ppts, key);
	}


	public final static String getPropertyValue(String propertyFilePath, String key) {
		if(propertyFilePath == null) {
			propertyFilePath = defaultPropertyFile;
		}
		Properties ppts = getProperties(propertyFilePath);
//		return ppts == null ? null : ppts.getProperty(key);
		return convertValue(ppts, key);
	}


	public final static Properties getProperties(String propertyFilePath) {
		if (propertyFilePath == null) {
			log.error("propertyFilePath is null!");
			return null;
		}
		Properties ppts = pptContainer.get(propertyFilePath);
		if (ppts == null) {
			ppts = loadPropertyFile(propertyFilePath);
			if (ppts != null) {
				pptContainer.put(propertyFilePath, ppts);
			}
		}
		return ppts;
	}

	private static Properties loadPropertyFile(String propertyFilePath) {
		InputStream is = PropertyUtil.class.getResourceAsStream(propertyFilePath);

		Properties ppts = new Properties();
		try {
			ppts.load(is);
			return ppts;
		} catch (Exception e) {
			log.debug("加载属性文件出错:" + propertyFilePath, e);
			return null;
		}
	}

	/**
	 * 支持${xxx}的正则替换
	 * @param ppts
	 * @param key
	 * @return
	 */
	private static String convertValue(Properties ppts, String key){
		if(ppts != null){
			String value = ppts.getProperty(key);
			Matcher matcher = PATTERN.matcher(value);
			StringBuffer buffer = new StringBuffer();
			while (matcher.find()) {
				String matcherKey = matcher.group(1);
				String matchervalue = ppts.getProperty(matcherKey);
				if (matchervalue != null) {
					matcher.appendReplacement(buffer, matchervalue);
				}
			}
			matcher.appendTail(buffer);
			return buffer.toString();
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		
		String  str="/deployHistory/rollback/79";
		int i=str.lastIndexOf("/");
		String a = str.substring(i+1,str.length());
		System.out.println(a);
	}
}
