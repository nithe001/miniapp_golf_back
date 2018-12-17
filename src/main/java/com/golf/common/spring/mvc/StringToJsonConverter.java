// ======================================
// File Name:StringToJsonConverter.java
// Create Date:2014-1-16
// ======================================
package com.golf.common.spring.mvc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author cky
 * @version 2014-1-16 下午9:09:38
 * 
 */
public class StringToJsonConverter implements Converter<String, JsonElement> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.core.convert.converter.Converter#convert(java.lang
	 * .Object)
	 */
	@Override
	public JsonElement convert(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		return new JsonParser().parse(source);
	}

}
