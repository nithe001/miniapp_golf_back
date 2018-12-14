package com.kingyee.common.util;

import com.kingyee.common.gson.JsonWrapper;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ph on 2017/5/12.
 */
public class ValidateUtil {


    /** 英文字母 、数字和下划线 */
    public final static Pattern GENERAL = Pattern.compile("^\\w+$");
    /** 数字 */
    public final static Pattern NUMBERS = Pattern.compile("\\d+");
    /** 分组 */
    public final static Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
    /** IP v4 */
    public final static Pattern IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
    /** 货币 */
    public final static Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
    /** 邮件 */
    public final static Pattern EMAIL = Pattern.compile("(\\w|.)+@\\w+(\\.\\w+){1,2}");
    /** 移动电话 */
    public final static Pattern MOBILE = Pattern.compile("1\\d{10}");
    /** 身份证号码 */
    public final static Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
    /** 邮编 */
    public final static Pattern ZIP_CODE = Pattern.compile("\\d{6}");
    /** 生日 */
    public final static Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-\\.年]?)(\\d{1,2})([/\\-\\.月]?)(\\d{1,2})日?$");
    /** URL */
    public final static Pattern URL = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
    /** 中文字、英文字母、数字和下划线 */
    public final static Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[\\u0391-\\uFFE5\\w]+$");
    /** UUID */
    public final static Pattern UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
    /** 不带横线的UUID */
    public final static Pattern UUID_SIMPLE = Pattern.compile("^[0-9a-z]{32}$");

    /**
     * 验证是否为空<br>
     * 对于String类型判定是否为empty(null 或 "")<br>
     *
     * @param value 值
     * @return 是否为空
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T value) {
        return (null == value || (value instanceof String && StringUtils.isEmpty((String) value)));
    }

    /**
     * 验证是否为空<br>
     * 对于String类型判定是否为empty(null 或 "")<br>
     *
     * @param value 值
     * @return 是否为空
     * @return 是否为空
     */
    public static <T> boolean isNotEmpty(T value) {
        return false == isEmpty(value);
    }

    /**

     * 给定内容是否匹配正则

     * @param regex 正则

     * @param content 内容

     * @return 正则为null或者""则不检查，返回true，内容为null返回false

     */
    public static boolean isMatch(String regex, String content) {
        if(content == null) {
            //提供null的字符串为不匹配
            return false;
        }
        if(StringUtils.isEmpty(regex)) {
            //正则不存在则为全匹配
            return true;
        }
        return Pattern.matches(regex, content);
    }

    /**

     * 给定内容是否匹配正则

     * @param pattern 模式

     * @param content 内容

     * @return 正则为null或者""则不检查，返回true，内容为null返回false

     */
    public static boolean isMatch(Pattern pattern, String content) {
        if(content == null || pattern == null) {
            //提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 通过正则表达式验证
     *
     * @param regex 正则
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMactchRegex(String regex, String value) {
        return isMatch(regex, value);
    }


    /**
     * 通过正则表达式验证
     *
     * @param pattern 正则模式
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMactchRegex(Pattern pattern, String value) {
        return isMatch(pattern, value);
    }

    /**
     * 验证是否为英文字母 、数字和下划线
     *
     * @param value 值
     * @return 是否为英文字母 、数字和下划线
     */
    public static boolean isGeneral(String value) {
        return isMactchRegex(GENERAL, value);
    }


    /**
     * 验证是否为给定长度范围的英文字母 、数字和下划线
     *
     * @param value 值
     * @param min 最小长度，负数自动识别为0
     * @param max 最大长度，0或负数表示不限制最大长度
     * @return 是否为给定长度范围的英文字母 、数字和下划线
     */
    public static boolean isGeneral(String value, int min, int max) {
        String reg = "^\\w{" + min + "," + max + "}$";
        if (min < 0) {
            min = 0;
        }
        if (max <= 0) {
            reg = "^\\w{" + min + ",}$";
        }
        return isMactchRegex(reg, value);
    }


    /**
     * 验证是否为给定最小长度的英文字母 、数字和下划线
     *
     * @param value 值
     * @param min 最小长度，负数自动识别为0
     * @return 是否为给定最小长度的英文字母 、数字和下划线
     */
    public static boolean isGeneral(String value, int min) {
        return isGeneral(value, min, 0);
    }


    /**
     * 验证该字符串是否是数字
     *
     * @param value 字符串内容
     * @return 是否是数字
     */
    public static boolean isNumber(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return isMactchRegex(NUMBERS, value);
    }

    /**
     * 验证是否为货币
     *
     * @param value 值
     * @return 是否为货币
     */
    public static boolean isMoney(String value) {
        return isMactchRegex(MONEY, value);
    }

    /**
     * 验证是否为邮政编码（中国）
     *
     * @param value 值
     * @return 是否为邮政编码（中国）
     */
    public static boolean isZipCode(String value) {
        return isMactchRegex(ZIP_CODE, value);
    }

    /**
     * 验证是否为可用邮箱地址
     *
     * @param value 值
     * @return 否为可用邮箱地址
     */
    public static boolean isEmail(String value) {
        return isMactchRegex(EMAIL, value);
    }

    /**
     * 验证是否为手机号码（中国）
     *
     * @param value 值
     * @return 是否为手机号码（中国）
     */
    public static boolean isMobile(String value) {
        return isMactchRegex(MOBILE, value);
    }

    /**
     * 验证是否为身份证号码（18位中国）<br>
     * 出生日期只支持到到2999年
     *
     * @param value 值
     * @return 是否为身份证号码（18位中国）
     */
    public static boolean isCitizenId(String value) {
        return isMactchRegex(CITIZEN_ID, value);
    }

    /**
     * 验证是否为IPV4地址
     *
     * @param value 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv4(String value) {
        return isMactchRegex(IPV4, value);
    }

    /**
     * 验证是否为URL
     *
     * @param value 值
     * @return 是否为URL
     */
    public static boolean isUrl(String value) {
        try {
            new java.net.URL(value);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * 验证是否为汉字
     *
     * @param value 值
     * @return 是否为汉字
     */
    public static boolean isChinese(String value) {
        return isMactchRegex("^[\\u4E00-\\u9FFF]+$", value);
    }


    /**
     * 验证是否为中文字、英文字母、数字和下划线
     *
     * @param value 值
     * @return 是否为中文字、英文字母、数字和下划线
     */
    public static boolean isGeneralWithChinese(String value) {
        return isMactchRegex(GENERAL_WITH_CHINESE, value);
    }

    /**
     * 验证是否为UUID<br>
     * 包括带横线标准格式和不带横线的简单模式
     *
     * @param value 值
     * @return 是否为UUID
     */
    public static boolean isUUID(String value) {
        return isMactchRegex(UUID, value) || isMactchRegex(UUID_SIMPLE, value);
    }

	public static void main(String[] args) {
		if(!ValidateUtil.isMobile("18311056956")){
			System.out.println("不是手机号");
		}
	}

}
