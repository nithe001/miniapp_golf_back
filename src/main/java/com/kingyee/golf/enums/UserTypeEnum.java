package com.kingyee.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：用户类型枚举类
 * @author ph
 * @CreateTime 2017-4-5
 */
public enum UserTypeEnum implements IEnum {
	/** 1:理事会:<b style="font:bold;"> 0 </b> */
	COUNCIL("1"),
    /** 2:委员会:<b style="font:bold;"> 0 </b> */
	COMMITTEE("2"),
	/** 3：普通用户:<b style="font:bold;"> 0 </b> */
	DOMESTIC("3");

	private String text;

    UserTypeEnum(String text) {
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
		for(UserTypeEnum model : UserTypeEnum.values()) {
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
        for(UserTypeEnum model : UserTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
