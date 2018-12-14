package com.kingyee.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：学术活动类型枚举类
 * @author ph
 * @CreateTime 2017-4-5
 */
public enum ActivitiesTypeEnum implements IEnum {
	/** 益心论道:<b style="font:bold;"> 0 </b> */
    YXLD("1"),
    /** 心内外科沙龙:<b style="font:bold;"> 0 </b> */
	SALON("2"),
	/** 北青CTO俱乐部:<b style="font:bold;"> 0 </b> */
	CTO("3"),
	/** 女医师俱乐部:<b style="font:bold;"> 0 </b> */
	DOCTRESS("4");

	private String text;

    ActivitiesTypeEnum(String text) {
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
		for(ActivitiesTypeEnum model : ActivitiesTypeEnum.values()) {
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
        for(ActivitiesTypeEnum model : ActivitiesTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
