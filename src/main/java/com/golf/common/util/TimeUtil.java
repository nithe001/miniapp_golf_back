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
 * 时间工具
 *
 * @author 马劼
 */
public class TimeUtil {


	/** 日期格式 yyyy-MM-dd 2013-06-16 */
	public static String FORMAT_DATE = "yyyy-MM-dd";
	/** 日期格式 yyyy-MM-dd HH:mm:ss 2013-06-16 09:54:02 */
	public static String FORMAT_DATETIME_FULL = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 yyyy-MM-dd HH:mm:ss 2013-06-16 09:54:02 */
	public static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm";
	/** 日期格式 MM-dd 06-16 */
	public static String FORMAT_DATE_ONLY_DAY = "MM-dd";
	/** 日期格式 HH:mm 09:54 */
	public static String FORMAT_DATE_ONLY_HOUR = "HH:mm";
	/** 日期格式 yyyy-MM-dd HH:mm:ss 2013-06-16 09:54:02 */
	public static String FORMAT_DATETIME_HH = "yyyy-MM-dd HH";
	/** ueditor图片目录文件夹名称 - 日期格式 yyyy-MM-dd 2013-06-16 */
	public static String UEDITOR_FORMAT_DATE = "yyyyMMdd";
	/** 日期格式 yyyy-MM-dd 2013-06-16 */
	public static String FORMAT_DATE_CN = "yyyy年MM月";
	/** 日期格式 yyyy-MM-dd 2013-06-16 */
	public static String FORMAT_DATE_EN = "yyyy-MM";
	/** 日期格式 yyyy-MM-dd 2013-06-16 */
	public static String NOW_DATE_CN = "yyyy年MM月dd日";
	/** 日期格式 yyyy-MM-dd HH:mm:ss 2013-06-16 09:54:02 */
	public static String FORMAT_DATETIME_HH_MM = "yyyy-MM-dd HH:mm";
	public static String NOW_DATE_CN_NO_DAY = "yyyy年MM月dd";

	/**
	 * 得到当前时间
	 *
	 * @return nowstr
	 */
	public static Date getNowTime() {
		Date now = new Date();
		return now;
	}

	/**
	 * SimpleDateFormat到格式的日期
	 *
	 * @param f 格式
	 * @return SimpleDateFormat
	 */
	private static SimpleDateFormat getDateFormatter(String f) {
		SimpleDateFormat formatter = new SimpleDateFormat(f);
		// workaround for bug
		formatter.setTimeZone(java.util.TimeZone.getDefault());
		// don't alllow things like 1/35/99
		formatter.setLenient(false);
		return formatter;
	}

	/**
	 * 获得当前时间Calendar格式
	 *
	 * @param d
	 * @return now date
	 */
	public static Calendar getCalendar(Date d) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		return now;
	}

	/**
	 * 得到指定格式的日期字符串
	 *
	 * @param f 格式
	 * @return nowDate String.
	 */
	public static String getNowTime(String f) {
		return getDateFormatter(f).format(getNowTime());
	}

	/**
	 * 得到当前时间的毫秒部分
	 *
	 * @return str
	 */
	public static String getSecond() {
		return getNowTime("sss");
	}

	/**
	 * String到Date格式的转换
	 *
	 * @param d 日期
	 * @param f 格式
	 * @return date Date
	 */
	public static Date stringToDate(String d, String f) {
		// will throw exception if can't parse
		try {
			return getDateFormatter(f).parse(d);
		} catch (ParseException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 *  string 到 date 的转换
	 * @param d 日期
	 * @param f 格式
	 * @return
	 */
	public static Long stringToLong(String d, String f) {
		if(d == null){
			return null;
		}
		Date date = stringToDate(d,f);
		if(date == null) {
			return null;
		}
		return date.getTime();
	}

	/**
	 * date到String格式的转换
	 *
	 * @param d 日期
	 * @param f 格式
	 * @return date String
	 */
	public static String dateToString(Date d, String f) {
		return getDateFormatter(f).format(d);
	}

	/**
	 *  Long到String格式的转换
	 * @param d 日期
	 * @param f 格式
	 * @return date String
	 */
	public static String longToString(Long d, String f) {
		if(d == null){
			return "";
		}
		return dateToString(longToDate(d),f);
	}

	/**
	 * 当前时间的long格式,除以1000以Integer表示. 主要用于与c++交换日期,缺点是没有毫秒值.
	 *
	 * @return longDate Integer
	 */
	public static Integer nowDateToInteger() {
		return Integer.valueOf(String.valueOf(getNowTime().getTime() / 1000));
	}

	/**
	 * 转换指定日期到Integer格式
	 *
	 * @param date
	 * @return date Integer
	 */
	public static Integer dateToInteger(Date date) {
		if (date == null) {
			return new Integer(0);
		} else {
			return Integer.valueOf(String.valueOf(date.getTime() / 1000));
		}
	}

	/**
	 * integer to Date 主要用于从c++中传入的日期类型转换为Date类型.
	 *
	 * @param date
	 * @return date Date
	 */
	public static Date integerToDate(Integer date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(((long) date.intValue()) * 1000);
		return cal.getTime();
	}

	/**
	 * 转换现在的时间为Long格式 以毫秒为单位计算,从1970年1月1日0时0分0秒0000毫秒算起.
	 *
	 * @return longDate long
	 */
	public static long dateTolong() {
		return getNowTime().getTime();
	}

	/**
	 * @see this.dateTolong
	 *
	 * @return longDate Long
	 */
	public static Long dateToLong() {
		return new Long(getNowTime().getTime());
	}

	public static Long dateToLong(String format) {
		String date = getNowTime(format);
		return stringToLong(date, format);
	}

	/**
	 * this.dateTolong
	 *
	 * @return longDate string
	 */
	public static String dateToLongString() {
		return String.valueOf(getNowTime().getTime());
	}

	/**
	 * long转换为日期
	 *
	 * @param d 日期
	 * @return date Date
	 */
	public static Date longToDate(long d) {
		return new Date(d);
	}

	/**
	 * long转换为日期
	 *
	 * @param d 日期
	 * @return date Date
	 */
	public static Date longToDate(Long d) {
		return new Date(d.longValue());
	}

	/**
	 * lang or integer型转换成date string
	 * @param str
	 * @param format
	 * @return string date
	 */
	public static String getDate(String str, String format) {
		if (str.length() > 11) {
			return dateToString(longToDate(Long.valueOf(str)), format);
		} else {
			return dateToString(integerToDate(Integer.valueOf(str)), format);
		}
	}

	/**
	 * 取得某月最后一天的日期整数值
	 * @param year		年(yyyy)
	 * @param month	月(mm或m)
	 * @return int			日期整数值
	 */
	public static int getMaxDayOfMonth(String year, String month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 计算两个long型的时间之间的差（**天 ** 小时 ** 分钟）
	 * @param starttime   开始时间
	 * @param endtime	结束时间
	 * @return
	 */
	public static String getTimeDiff(long starttime, long endtime) {
		long left = endtime - starttime;
		if (left < 0) {
			left = 0;
		}
		return getTimeDesc(left);
	}

	/**
	 * 取得time代表x天x小时x分钟x秒
	 * @param time 毫秒
	 * @return
	 */
	public static String getTimeDesc(long time){
		StringBuffer sb = new StringBuffer();
		long daynum = time / (24 * 60 * 60 * 1000);
		time = time - daynum * (24 * 60 * 60 * 1000);
		long hournum = time / (3600 * 1000);
		time = time - hournum * (3600 * 1000);
		long minnum = time / (60 * 1000);
		time = time - minnum * (60 * 1000);
		long second = time / (1000);
		if (daynum > 0) {
			sb.append(daynum + "天");
		}
		if (hournum > 0) {
			sb.append(hournum + "小时");
		}
		if (minnum > 0) {
			sb.append(minnum + "分钟");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}

		if(sb.length() == 0){
			sb.append("0分钟");
		}

		return sb.toString();
	}

	public static Long getBeforeDate(Date date, int dayCount) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		c.add(Calendar.DATE, dayCount);
		return c.getTimeInMillis();
	}

	/**
	 * 获取当前日期之后next天的日期long值
	 * @param next--指定多少天
	 * @return
	 */
	public static Long getNextAssignDate(long next) {
		Long nowTime = getNowDate();
		return nowTime + (24 * 60 * 60 * 1000 * next);
	}

	/**
	 * 获得当前日期的零点的long型
	 *
	 * @return
	 */
	public static long getNowDate(){
		return stringToLong(dateToString(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}

	/**
	 * 获取具体日期的前几天
	 * @param date 具体日期
	 * @param num 相差数
	 * @return
	 * @throws ParseException
	 */
	public static String subDay(String date,Integer num) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(date);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);
		rightNow.add(Calendar.DAY_OF_MONTH, -num);
		Date dt1 = rightNow.getTime();
		String reStr = sdf.format(dt1);
		return reStr;
	}

	/**
	 * date2比date1多的天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDays(Date date1,Date date2)
	{
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1= cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if(year1 != year2)   //同一年
		{
			int timeDistance = 0 ;
			for(int i = year1 ; i < year2 ; i ++)
			{
				if(i%4==0 && i%100!=0 || i%400==0)    //闰年
				{
					timeDistance += 366;
				}
				else    //不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2-day1) ;
		}
		else    //不同年
		{
			return day2-day1;
		}
	}


	/**
	 * SimpleDateFormat到格式的日期
	 *
	 * @param f 格式
	 * @return SimpleDateFormat
	 */
	private static SimpleDateFormat getDateFormatter(String f, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(f, locale);
		// workaround for bug
		formatter.setTimeZone(java.util.TimeZone.getDefault());
		// don't alllow things like 1/35/99
		formatter.setLenient(false);
		return formatter;
	}


	/**
	 *  发动态  刚刚  一分钟前..
	 * @param date
	 * @return
	 */
	public static String getDynTime(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);


		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		long time = date.getTime();
		long now = System.currentTimeMillis();
		long s = now-time;

		calendar.setTime(new Date());
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0,0);
		calendar.set(Calendar.MILLISECOND, 0);
		long now0 = calendar.getTime().getTime();

		if(s<60*1000){
			return "刚刚";
		}else if(s<60*1000*60){
			return s/(1000*60)+"分钟前";
		}else if(s<60*1000*60*24){
			return s/(1000*60*60)+"小时前";
		}else if(time>now0-1000*60*60*24){
			return "昨天"+hour+"点"+minute+"分";
		}else if(time>now0-1000*60*60*24*2){
			return "前天"+hour+"点"+minute+"分";
		}
		return year%100+"年"+(month+1)+"月"+day+"日 "+hour +"点"+minute+"分";
	}




	/**
	 * date到String格式的转换
	 *
	 * @param d 日期
	 * @param f 格式
	 * @return date String
	 */
	public static String dateToString(Date d, String f, Locale locale) {
		return getDateFormatter(f, locale).format(d);
	}
	/**
	 * 本周的星期一
	 * @param date
	 * @return
	 */
	public static Long getMonday4Week(Date date){
		date = new Date(stringToLong(dateToString(date, TimeUtil.FORMAT_DATE), TimeUtil.FORMAT_DATE));
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		if(weekday == 1){
			return getBeforeDate(date, -6);
		}else if(weekday == 2){
			return stringToLong(dateToString(date, TimeUtil.FORMAT_DATE), TimeUtil.FORMAT_DATE);
		}else {
			return getBeforeDate(date, 2 - weekday);
		}

	}

	/**
	 * 获取当月第一天和最后一天
	 * @return
	 */
	public static Map<String,Object> getThisMonthTime(String f) {
		Map<String,Object> parp = new HashMap<String,Object>();
		Calendar cale = null;
		cale = Calendar.getInstance();
		Long firstday, lastday;
		// 获取前月的第一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		firstday = cale.getTime().getTime();
		if(StringUtils.isNotEmpty(f)){
			parp.put("startTime",longToString(firstday, f));
		}else{
			parp.put("startTime",firstday);
		}
		// 获取前月的最后一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		lastday = cale.getTime().getTime();
		if(StringUtils.isNotEmpty(f)){
			parp.put("endTime",longToString(lastday, f));
		}else{
			parp.put("endTime",lastday);
		}
		return parp;
	}

	/**
	 * 获取当天是本月的第几天
	 * @return
	 */
	public static Integer getDayForMonth() {
		Calendar cale = Calendar.getInstance();
		cale.setTime(getNowTime());
		int w = cale.get(Calendar.DAY_OF_MONTH);
		return w;
	}



	/**
	 * 获取当月1日早八点到4日晚八点
	 * @return
	 */
	public static Map<String,Object> getThisMonthFirstDateToThirdDate() {
		Map<String,Object> parp = new HashMap<String,Object>();
		Calendar cale = null;
		cale = Calendar.getInstance();
		Long firstday, lastday;
		// 获取当月的第一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		//将小时至8
		cale.set(Calendar.HOUR_OF_DAY, 8);
		//将分钟至0
		cale.set(Calendar.MINUTE, 0);
		//将秒至0
		cale.set(Calendar.SECOND,0);
		firstday = cale.getTime().getTime();
		parp.put("startTime",firstday);

		// 获取当月的第四天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 4);
		//将小时至20
		cale.set(Calendar.HOUR_OF_DAY, 20);
		//将分钟至0
		cale.set(Calendar.MINUTE, 0);
		//将秒至0
		cale.set(Calendar.SECOND,0);
		lastday = cale.getTime().getTime();
		parp.put("endTime",lastday);
//		System.out.println("当月的开始时间和结束时间分别是 ： " + firstday + " and " + lastday);
//		System.out.println("当月的开始时间和结束时间分别是 ： " + longToString(firstday,FORMAT_DATETIME) + " and " + longToString(lastday,FORMAT_DATETIME));
		return parp;
	}

	/**
	 * 获取当日的起始时间和结束时间
	 * @return
	 */
	public static Map<String,Object> getThisDayTime() {
		Map<String,Object> parp = new HashMap<String,Object>();
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		Long todayStartTime = todayStart.getTime().getTime();
		parp.put("startTime",todayStartTime);

		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		Long todayEndTime = todayEnd.getTime().getTime();
		parp.put("endTime",todayEndTime);
		//System.out.println("当天的开始时间和结束时间分别是 ： " + todayStartTime + " and " + todayEndTime);
		return parp;
	}

	/**
	 * 取得当周第一天
	 * @return
	 */
	public static Date getFirstDayOfWeek(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date date = cal.getTime();
		return date;
	}


	/**
	 * 由出生日期计算年龄
	 * @param birthday
	 * @return
	 */
	public static int getAge(String birthday){
		Date birthDay = TimeUtil.stringToDate(birthday, TimeUtil.FORMAT_DATE);

		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			return 0;
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				} else {
					// do nothing
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		} else {
			// monthNow<monthBirth
			// donothing
		}
		return age;
	}


	/**
	 * 由出生年份计算年龄
	 * @param birthYear
	 * @return
	 */
	public static int getAgeByYear(String birthYear){
		Date birthDay = TimeUtil.stringToDate(birthYear, "yyyy");

		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			return 0;
		}

		int yearNow = cal.get(Calendar.YEAR);

		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int age = yearNow - yearBirth;
		return age;
	}


	/**
	 * 取给定时间的零点值
	 * SQ
	 * 年-月-日 XX:XX:XX  -> 年-月-日 00:00:00
	 * @param time
	 */
	public static Long getZeroTime(Long time){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTimeInMillis();
	}

	/**
	 * 取给定时间第二天的毫秒值
	 * SQ
	 * @param time
	 */
	public static Long getNextDay(Long time){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTimeInMillis();
	}


	/**
	 * 获取当月的 天数
	 */
	public static int getCurrentMonthDay() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 根据年 月 获取对应的月份 天数
	 */
	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 获取本月工作日天数
	 */
	public static int getWorkingDaysForMonth(Calendar calendar) {
		Integer workDays = 0;
		Integer days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		try {
			calendar.set(Calendar.DATE, 1);//从每月1号开始
			for (int i = 0; i < days; i++) {
				Integer day = calendar.get(Calendar.DAY_OF_WEEK);
				if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
					workDays++;
				}
				calendar.add(Calendar.DATE, 1);
			}
			/*map.put("workDaysAmount", workDays);//工作日
			map.put("year", calendar.get(Calendar.YEAR));//实时年份
			map.put("month", calendar.get(Calendar.MONTH));//实时月份
			map.put("daysAmount", days);//自然日
			map.put("weeksAmount", calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));//周*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workDays;
	}


	//以下无用 可以留作参考
	// 法律规定的放假日期
	private List<String> lawHolidays = Arrays.asList("2019-01-01",
			"2019-02-04", "2019-02-05", "2019-02-06", "2019-02-07", "2019-02-08",
			"2019-02-09", "2019-02-10", "2019-04-05", "2019-05-01", "2019-06-07",
			"2019-09-13", "2019-10-01", "2019-10-02", "2019-10-03", "2019-10-04",
			"2019-10-05", "2019-10-06", "2019-10-07");

	// 由于放假需要额外工作的周末
	private List<String> extraWorkdays = Arrays.asList("2018-12-29", "2019-02-02", "2019-02-03", "2019-09-29", "2019-10-12");


	/**
	 * 判断是否是法定假日
	 *
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public boolean isLawHoliday(String calendar) throws Exception {
		this.isMatchDateFormat(calendar);
		if (lawHolidays.contains(calendar)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是周末
	 *
	 * @param calendar
	 * @return
	 */
	public static boolean isWeekends(String calendar) throws Exception {
		isMatchDateFormat(calendar);
		// 先将字符串类型的日期转换为Calendar类型
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(calendar);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(Calendar.DAY_OF_WEEK) == 1
				|| ca.get(Calendar.DAY_OF_WEEK) == 7) {
			return true;
		}
		return false;
	}


	/**
	 * 判断是否是需要额外补班的周末
	 *
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public boolean isExtraWorkday(String calendar) throws Exception {
		this.isMatchDateFormat(calendar);
		if (extraWorkdays.contains(calendar)) {
			return true;
		}
		return false;
	}


	/**
	 * 判断是否是休息日（包含法定节假日和不需要补班的周末）
	 *
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public boolean isHoliday(String calendar) throws Exception {
		this.isMatchDateFormat(calendar);
		// 首先法定节假日必定是休息日
		if (this.isLawHoliday(calendar)) {
			return true;
		}
		// 排除法定节假日外的非周末必定是工作日
		if (!this.isWeekends(calendar)) {
			return false;
		}
		// 所有周末中只有非补班的才是休息日
		if (this.isExtraWorkday(calendar)) {
			return false;
		}
		return true;
	}


	/**
	 * 使用正则表达式匹配日期格式
	 *
	 * @throws Exception
	 */
	private static void isMatchDateFormat(String calendar) throws Exception {
		Pattern pattern = compile("\\d{4}-\\d{2}-\\d{2}");
		Matcher matcher = pattern.matcher(calendar);
		boolean flag = matcher.matches();
		if (!flag) {
			throw new Exception("输入日期格式不正确，应该为2017-12-19");
		}
	}

	/**
	 * 根据日期获取日期所在周的周一和周日
	 * @param time
	 * @param type  1:周一 0：周日
	 * @return
	 */
	public static String getMondayAndSunday4Week(Date time, int type){
		Map<String, Object> parp = new HashMap<String, Object>();
		//判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
		if(1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}

		cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

		int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

		if(type == 1){
			String start = dateToString(cal.getTime(),TimeUtil.FORMAT_DATE);
			System.out.println("所在周星期一的日期："+dateToString(cal.getTime(),TimeUtil.FORMAT_DATE));
			System.out.println(cal.getFirstDayOfWeek()+"-"+day+"+6="+(cal.getFirstDayOfWeek()-day+6));
//            return stringToLong(start, TimeUtil.FORMAT_DATE);
			return start;
		}else{
			cal.add(Calendar.DATE, 6);
			String end = dateToString(cal.getTime(),TimeUtil.FORMAT_DATE);
			System.out.println("所在周星期日的日期："+dateToString(cal.getTime(),TimeUtil.FORMAT_DATE));
//            return stringToLong(end, TimeUtil.FORMAT_DATE);
			return end;
		}
	}

	/**
	 * 取得某月最后一天的日期Long值
	 * @param year		年(yyyy)
	 * @param month	月(mm或m)
	 * @return int			日期整数值
	 */
	public static Long getMaxDayOfMonth2(String year, String month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
//        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
		return null;
	}

	/**
	 * 获取指定日期的月份的第一天
	 * @return
	 */
	public static String getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//获取某月最小天数
		int firstDay = cal.getMinimum(Calendar.DATE);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取指定年月的第一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR, year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最小天数
		int firstDay = cal.getMinimum(Calendar.DATE);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH,firstDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取指定日期的月的最后一天
	 * @return
	 */
	public static String getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取指定年月的最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR, year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取当前月上月
	 * @param month
	 * @return
	 */
	public static String getLastMonth(String month) {
		DateFormat format2 =  new SimpleDateFormat("yyyy-MM");
		Date date = new Date();
		try{
			date = format2.parse(month);
		}catch(Exception e){
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
		c.add(Calendar.MONTH, -1);
		//上一个月
		String previousMonth = format.format(c.getTime());
		return previousMonth;
	}

	/**
	 * 获取当前月上月的第一天和最后一天
	 * @param month
	 * @param type 0:第一天 1：最后一天
	 * @return
	 */
	public static String getLastMonthFirstLastDay(String month,int type) {
		DateFormat format2 =  new SimpleDateFormat("yyyy-MM");
		Date date = new Date();
		try{
			date = format2.parse(month);
		}catch(Exception e){
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
		c.add(Calendar.MONTH, -1);
		//上一个月
		String previousMonth = format.format(c.getTime());
		if(type == 0){
			String firstDayOfPreviousMonth = getFirstDayOfMonth(Integer.parseInt(previousMonth.split("-")[0]),Integer.parseInt(previousMonth.split("-")[1]));
			return firstDayOfPreviousMonth;
		}
		if(type == 1){
			String lastDayOfPreviousMonth = getLastDayOfMonth(Integer.parseInt(previousMonth.split("-")[0]),Integer.parseInt(previousMonth.split("-")[1]));
			return lastDayOfPreviousMonth;
		}
		return null;
	}

	/**
	 * date到String格式的转换
	 *
	 * @param date 日期
	 * @param oldFormat 格式
	 * @param newFormat 格式
	 * @return date String
	 */
	public static String stringToString(String date, String oldFormat, String newFormat) {
		return longToString(stringToLong(date, oldFormat), newFormat);
	}

	/**
	 * 获取某年第一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Long getYearFirst(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst.getTime();
	}

	/**
	 * 获取某年最后一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Long getYearLast(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();
		return currYearLast.getTime();
	}


	public static void main(String[] args){
		System.out.println(getNowTime().getTime());
		System.out.println(longToString(1562924986957L,FORMAT_DATETIME_FULL));
	}
}