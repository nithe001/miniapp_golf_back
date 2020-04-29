/*
 * Created on 2005-7-13
 *
 * 时间工具类.
 */
package com.golf.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


/**
 * 数字工具
 *
 * @author
 */
public class MathUtil {

	/**
	 * 随机生成n个1-9的不重复的数
	 * @return Date
	 */
	public static Integer[] getRandom(int num){
		Random ran = new Random();
		HashSet hs = new HashSet();
		for(;;){
			int tmp = ran.nextInt(9)+1;
			hs.add(tmp);
			if(hs.size() == 6) break;
		}
		System.out.println(hs);
		Integer[] holes = (Integer[]) hs.toArray(new Integer[] {});//关键语句
		return holes;
	}

	public static void main(String[] args) {
		Random ran = new Random();
		System.out.println(ran.nextInt(100));
	}
}