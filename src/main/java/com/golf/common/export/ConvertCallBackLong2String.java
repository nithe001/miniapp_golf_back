package com.golf.common.export;

import org.apache.commons.lang3.StringUtils;

import com.golf.common.util.TimeUtil;

public class ConvertCallBackLong2String implements ConvertCallBack {

	private String format = "yyyy-MM-dd";

	public ConvertCallBackLong2String() {
	}

	public ConvertCallBackLong2String(String format) {
		if (StringUtils.isNotEmpty(format)) {
			this.format = format;
		}

	}

	@Override
	public String getString(Object obj) {
		if (null != obj && StringUtils.isNotEmpty(obj.toString())) {
			return TimeUtil.longToString(Long.valueOf(obj.toString()), format);
		}
		return "";
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
