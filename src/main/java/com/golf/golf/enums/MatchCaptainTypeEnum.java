package com.golf.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：是否赛长枚举类
 * @author nmy
 * @CreateTime 2017-4-5
 */
public enum MatchCaptainTypeEnum implements IEnum {
	/** 否 0*/
	YES("0"),
    /** 是 1*/
	NO("1");

	private String text;

    MatchCaptainTypeEnum(String text) {
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
		for(MatchCaptainTypeEnum model : MatchCaptainTypeEnum.values()) {
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
        for(MatchCaptainTypeEnum model : MatchCaptainTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
