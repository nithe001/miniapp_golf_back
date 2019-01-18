package com.golf.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：是否赛长枚举类
 * @author nmy
 * @CreateTime 2017-4-5
 */
public enum MatchGroupUserMappingTypeEnum implements IEnum {
	/** 临时分组 0*/
	TEMPORARY("0"),
    /** 赛长 1*/
	CAPTION("1"),
	/** 普通球友 1*/
	ORDINARY("2");

	private String text;

    MatchGroupUserMappingTypeEnum(String text) {
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
		for(MatchGroupUserMappingTypeEnum model : MatchGroupUserMappingTypeEnum.values()) {
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
        for(MatchGroupUserMappingTypeEnum model : MatchGroupUserMappingTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
