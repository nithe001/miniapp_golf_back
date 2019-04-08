package com.golf.golf.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：记分——是否上球道类型
 * @author nmy
 * @CreateTime 2017-4-5
 */
public enum ScoreTypeEnum implements IEnum {
	/** 0 左*/
	LEFT("0"),
	/** 1 左前*/
	LEFT_FRONT("1"),
    /** 2 上球道*/
    UP("2"),
    /** 3 右前*/
    RIGHT_FRONT("3"),
    /** 4 右*/
    RIGHT("4");

	private String text;

    ScoreTypeEnum(String text) {
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
		for(ScoreTypeEnum model : ScoreTypeEnum.values()) {
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
        for(ScoreTypeEnum model : ScoreTypeEnum.values()) {
            list.add(model.text());
        }
        return list;
    }

}
