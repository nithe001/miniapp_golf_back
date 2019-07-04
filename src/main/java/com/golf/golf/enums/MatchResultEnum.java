package com.golf.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：比洞赛记分卡成绩结果枚举
 * @author nmy
 * @CreateTime 2017-4-5
 */
public enum MatchResultEnum implements IEnum {
	/** 用户:<b style="font:bold;"> 0 </b> */
	AS("A/S"),
	UP("UP"),
	DN("DN");

	private String text;

    MatchResultEnum(String text) {
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
		for(MatchResultEnum model : MatchResultEnum.values()) {
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
        for(MatchResultEnum model : MatchResultEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
