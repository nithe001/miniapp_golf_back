package com.kingyee.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 计算类
 * 
 * @author lixl
 * @version 2012-06-21
 */
public class DecimalUtil {

	private BigDecimal _val;

	public static final int ZERO = 0;
	public static final String STRING_ZERO = "0";
	public static final Double DOUBLE_ZERO = Double.valueOf(STRING_ZERO);

	private static final BigDecimal zeroThroughTWENTY[] = {
			new BigDecimal(ZERO), new BigDecimal(1), new BigDecimal(2),
			new BigDecimal(3), new BigDecimal(4), new BigDecimal(5),
			new BigDecimal(6), new BigDecimal(7), new BigDecimal(8),
			new BigDecimal(9), new BigDecimal(10), new BigDecimal(11),
			new BigDecimal(12), new BigDecimal(13), new BigDecimal(14),
			new BigDecimal(15), new BigDecimal(16), new BigDecimal(17),
			new BigDecimal(18), new BigDecimal(19), new BigDecimal(20) };

	private static final BigDecimal BIGDECIMAL_ZERO = zeroThroughTWENTY[ZERO];

	public static final String DECIMAL_FORMAT = "###,###,###,##0.00";

	/**
	 * 初始化
	 * 
	 * @param n
	 * @return
	 */
	private static BigDecimal valueOf(long n) {
		if (n <= 20 && n >= ZERO) {
			return zeroThroughTWENTY[(int) n];
		}
		return BigDecimal.valueOf(n);
	}

	private static BigDecimal valueOf(String str) {
		return new BigDecimal(str);
	}

	/**
	 * double 获得类型 BigDecimal
	 * 
	 * @param val
	 * @return
	 */
	public static BigDecimal getBigDecimal(double val) {
		long num = (long) val;
		if (num == val) {
			return valueOf(num);
		} else {
			return valueOf(String.valueOf(val));
		}
	}

	/**
	 * String 获得类型 BigDecimal
	 * 
	 * @param val
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object obj) {
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		} else if (obj instanceof Double) {
			return getBigDecimal(((Double) obj).doubleValue());
		} else if (obj instanceof Integer) {
			return getBigDecimal(((Integer) obj).doubleValue());
		}
		return valueOf(obj.toString());
	}

	/**
	 * double类型 转换为 Double 对象
	 * 
	 * @param val
	 * @return
	 */
	public static Double doubleToObject(double val) {
		return new Double(val);
	}

	/**
	 * String 转换为 Double 对象
	 * 
	 * @param val
	 * @return
	 */
	public static Double stringToDouble(String str) {
		if (BaseUtil.isEmpty(str)) {
			return DOUBLE_ZERO;
		}
		return Double.valueOf(str);
	}

	/**
	 * Double对象 转换为 double类型
	 * 
	 * @param val
	 * @return
	 */
	public static long objectTolong(Long dbl) {
		if (BaseUtil.isEmpty(dbl)) {
			return ZERO;
		}
		return dbl.longValue();
	}

	/**
	 * String 转换为 long 对象
	 * 
	 * @param val
	 * @return
	 */
	public static long stringTolong(String str) {
		if (BaseUtil.isEmpty(str)) {
			return ZERO;
		}
		return Long.parseLong(str);
	}

	/**
	 * Object对象 转换为 double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static long objectTolong(Object obj) {
		if (obj instanceof Long) {
			return objectTolong((Long) obj);
		}
		if (BaseUtil.isEmpty(obj)) {
			return ZERO;
		} else {
			return stringTolong(obj.toString());
		}
	}
	
	/**
	 * Object对象 转换为 double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Long objectTolongNull(Object obj) {
		if (obj instanceof Long) {
			return objectTolong((Long) obj);
		}
		if (BaseUtil.isEmpty(obj)) {
			return null;
		} else {
			return stringTolong(obj.toString());
		}
	}

	/**
	 * Double对象 转换为 double类型
	 * 
	 * @param val
	 * @return
	 */
	public static double objectTodouble(Double dbl) {
		if (BaseUtil.isEmpty(dbl) || dbl.isNaN()) {
			return ZERO;
		}
		return dbl.doubleValue();
	}

	/**
	 * String 转换为 double类型
	 * 
	 * @param val
	 * @return
	 */
	public static double stringTodouble(String str) {
		if (BaseUtil.isEmpty(str) || !NumberUtils.isNumber(str)) {
			return ZERO;
		}
		return Double.parseDouble(str);
	}

	/**
	 * Object对象 转换为 double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static double objectTodouble(Object obj) {
		if (obj instanceof Double) {
			return objectTodouble((Double) obj);
		}
		if (BaseUtil.isEmpty(obj)) {
			return ZERO;
		} else {
			return stringTodouble(obj.toString());
		}
	}

	/**
	 * double 初始化
	 * 
	 * @param val
	 * @return
	 */
	public static DecimalUtil valueOf(double val) {
		return new DecimalUtil(val);
	}

	/**
	 * Double 初始化
	 * 
	 * @param val
	 * @return
	 */
	public static DecimalUtil valueOf(Double dbl) {
		return new DecimalUtil(dbl);
	}

	/**
	 * Double 初始化
	 * 
	 * @param val
	 * @return
	 */
	public static DecimalUtil valueOf(BigDecimal big) {
		return new DecimalUtil(big);
	}

	/**
	 * 相加
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double add(double val1, double val2) {
		return getBigDecimal(val1).add(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相加
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double add(String val1, String val2) {
		return getBigDecimal(val1).add(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相加
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double add(Double val1, Double val2) {
		return getBigDecimal(val1).add(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相减
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double sub(double val1, double val2) {
		return getBigDecimal(val1).subtract(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相减
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double sub(String val1, String val2) {
		return getBigDecimal(val1).subtract(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相减
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double sub(Double val1, Double val2) {
		return getBigDecimal(val1).subtract(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相乘
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double mul(double val1, double val2) {
		return getBigDecimal(val1).multiply(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相乘
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double mul(String val1, String val2) {
		return getBigDecimal(val1).multiply(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相乘
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double mul(Double val1, Double val2) {
		return getBigDecimal(val1).multiply(getBigDecimal(val2)).doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double div(double val1, double val2) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), 10)
				.doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double div(String val1, String val2) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), 10)
				.doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double div(Double val1, Double val2) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), 10)
				.doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @param mode
	 * @return
	 */
	public static double div(double val1, double val2, int mode) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), mode)
				.doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @param mode
	 * @return
	 */
	public static double div(String val1, String val2, int mode) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), mode)
				.doubleValue();
	}

	/**
	 * 相除
	 * 
	 * @param val1
	 * @param val2
	 * @param mode
	 * @return
	 */
	public static double div(Double val1, Double val2, int mode) {
		return getBigDecimal(val1).divide(getBigDecimal(val2), mode)
				.doubleValue();
	}

	/**
	 * 
	 * @param number
	 * @param format
	 * @return String
	 */
	public static String formatToString(double number) {
		return formatToString(number, DECIMAL_FORMAT);
	}

	/**
	 * 
	 * @param number
	 * @param format
	 * @return String
	 */
	public static String formatToString(double number, String format) {
		return new DecimalFormat(format).format(number);
	}

	/**
	 * 格式化数值
	 * 
	 * @param val
	 * @return
	 */
	public static double format(double val) {
		return format(val, ZERO);
	}

	/**
	 * 格式化数值
	 * 
	 * @param val
	 * @return
	 */
	public static String format(String val) {
		if (BaseUtil.isEmpty(val)) {
			return STRING_ZERO;
		}
		return format(val, ZERO);
	}

	/**
	 * 格式化数值
	 * 
	 * @param val
	 * @return
	 */
	public static String format(String val, int mode) {
		if (BaseUtil.isEmpty(val)) {
			return STRING_ZERO;
		}
		return String.valueOf(format(objectTodouble(val), mode));
	}

	/**
	 * 格式化数值
	 * 
	 * @param val
	 * @param mode
	 * @return
	 */
	public static double format(double val, int mode) {
		if (mode <= ZERO) {
			return Math.round(val);
		}
		int n = (int) Math.pow(10, mode);
		return (double) Math.round(val * n) / n;
	}

	/**
	 * 用double 类型构造对象
	 * 
	 * @param val
	 */
	private DecimalUtil(double val) {
		_val = getBigDecimal(val);
	}

	/**
	 * 用字符类型构造对象
	 * 
	 * @param val
	 */
	public DecimalUtil(String str) {
		_val = getBigDecimal(str);
	}

	/**
	 * 用Double构造对象
	 * 
	 * @param val
	 */
	private DecimalUtil(Double str) {
		_val = getBigDecimal(objectTodouble(str));
	}

	/**
	 * 用BigDecimal构造对象
	 * 
	 * @param val
	 */
	private DecimalUtil(BigDecimal big) {
		_val = big;
	}

	/**
	 * 与当前数值进行 相加
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil add(double val) {
		_val = _val.add(getBigDecimal(val));
		return this;
	}

	/**
	 * 与当前数值进行 相加
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil add(String str) {
		_val = _val.add(getBigDecimal(str));
		return this;
	}

	/**
	 * 与当前数值进行 相加
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil add(DecimalUtil obj) {
		_val = _val.add(obj.getValue());
		return this;
	}

	/**
	 * 与当前数值进行 相加
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil add(Double dbl) {
		_val = _val.add(getBigDecimal(dbl));
		return this;
	}

	/**
	 * 与当前数值进行 相加
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil add(BigDecimal big) {
		_val = _val.add(big);
		return this;
	}

	/**
	 * 与当前数值进行 相减
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil sub(double val) {
		_val = _val.subtract(getBigDecimal(val));
		return this;
	}

	/**
	 * 与当前数值进行 相减
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil sub(String str) {
		_val = _val.subtract(getBigDecimal(str));
		return this;
	}

	/**
	 * 与当前数值进行 相减
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil sub(DecimalUtil obj) {
		_val = _val.subtract(obj.getValue());
		return this;
	}

	/**
	 * 与当前数值进行 相减
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil sub(Double dbl) {
		_val = _val.subtract(getBigDecimal(dbl));
		return this;
	}

	/**
	 * 与当前数值进行 相减
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil sub(BigDecimal big) {
		_val = _val.subtract(big);
		return this;
	}

	/**
	 * 与当前数值进行 相乘
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil mul(double val) {
		_val = _val.multiply(getBigDecimal(val));
		return this;
	}

	/**
	 * 与当前数值进行 相乘
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil mul(String str) {
		_val = _val.multiply(getBigDecimal(str));
		return this;
	}

	/**
	 * 与当前数值进行 相乘
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil mul(DecimalUtil obj) {
		_val = _val.multiply(obj.getValue());
		return this;
	}

	/**
	 * 与当前数值进行 相乘
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil mul(Double dbl) {
		_val = _val.multiply(getBigDecimal(dbl));
		return this;
	}

	/**
	 * 与当前数值进行 相乘
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil mul(BigDecimal big) {
		_val = _val.multiply(big);
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(double val) {
		if (val == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(val), 10);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(String str) {
		if (BaseUtil.isEmpty(str) || STRING_ZERO.equals(str)) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(str), 10);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(DecimalUtil obj) {
		if (BaseUtil.isEmpty(obj) || obj.getValue().doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(obj.getValue(), 10);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(Double dbl) {
		if (BaseUtil.isEmpty(dbl) || dbl.doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(dbl), 10);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(BigDecimal big) {
		if (BaseUtil.isEmpty(big) || big.doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(big, 10);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(double val, int mode) {
		if (val == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(val), mode);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(String str, int mode) {
		if (BaseUtil.isEmpty(str) || STRING_ZERO.equals(str)) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(str), mode);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(DecimalUtil obj, int mode) {
		if (BaseUtil.isEmpty(obj) || obj.getValue().doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(obj), mode);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(Double dbl, int mode) {
		if (BaseUtil.isEmpty(dbl) || dbl.doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(getBigDecimal(dbl), mode);
		}
		return this;
	}

	/**
	 * 与当前数值进行 相除
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil div(BigDecimal big, int mode) {
		if (BaseUtil.isEmpty(big) || big.doubleValue() == ZERO) {
			_val = BIGDECIMAL_ZERO;
		} else {
			_val = _val.divide(big, mode);
		}
		return this;
	}

	/**
	 * 设置 scale
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil setScale(int newScale, int mode) {
		_val = _val.setScale(newScale, mode);
		return this;
	}

	/**
	 * 设置 scale
	 * 
	 * @param val
	 * @return
	 */
	public DecimalUtil setScale(int newScale) {
		return setScale(newScale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 字符串输出
	 * 
	 * @return
	 */
	public String toString() {
		return _val.toString();
	}

	/**
	 * double 输出
	 * 
	 * @return
	 */
	public double doubleValue() {
		return _val.doubleValue();
	}

	/**
	 * double 输出
	 * 
	 * @return
	 */
	public Double toDoubleValue() {
		return stringToDouble(_val.toString());
	}

	/**
	 * 获得值
	 * 
	 * @return
	 */
	private BigDecimal getValue() {
		return _val;
	}
}
