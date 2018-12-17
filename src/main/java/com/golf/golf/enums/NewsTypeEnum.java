package com.golf.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：服务器环境枚举类
 * @author nmy
 * @CreateTime 2017-4-5
 */
public enum NewsTypeEnum implements IEnum {
	/** 文献类型 1*/
	MEDLIVE("1"),
	/** 管理员发布的文献:文献类型 2*/
	SYSTEM("2");

	private String text;

    NewsTypeEnum(String text) {
		this.text = text;
	}

	@Override
	public String text() {
		return this.text;
	}

	@Override
	public String value() {
		return this.ordinal() + "";
	}
	
	@Override
	public String toString() {
		return this.ordinal() + "(" + this.text + ")";
	}

	/**
	 * 根据value值获取对应类型的英文名称
	 * @param ordinal
	 * @return
	 */
	public static String getTextByOrdinal(String ordinal) {
		for(NewsTypeEnum model : NewsTypeEnum.values()) {
			if(model.value().equals(ordinal)) {
				return model.text();
			}
		}
		return "";
	}

    /**
     * 根据value值获取对应类型的英文名称
     * @return
     */
    public static List<String> textList() {
        List<String> list = new ArrayList<String>();
        for(NewsTypeEnum model : NewsTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
