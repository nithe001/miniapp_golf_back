package com.golf.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

public class CommonUtil {

    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
	
	/**
	 * 将str转换为前后都有逗号分隔的字符串。<br/>
	 * eg: str = ,免疫科,感染科,急诊科,  ，返回 ,免疫科,感染科,急诊科,
	 * eg: str = 免疫科,感染科,急诊科    ，返回 ,免疫科,感染科,急诊科,
	 * @param str ,免疫科,肿瘤科,老年科,或者 免疫科,肿瘤科,老年科
	 * @return 拼接好的字符串
	 */
	public static String convert2CommaString(String str){
		if(str == null || "".equals(str)){
			return str;
		}
		if(!str.startsWith(",")){
			str = "," + str;
		}
		if(!str.endsWith(",")){
			str = str + ",";
		}
		str = str.replaceAll(" ", "");
		
		return str;
	}
	
	/**
	 * 将str转换为前后都有逗号分隔的字符串。<br/>
	 * eg: str = ,免疫科,感染科,急诊科,  ，返回 免疫科,感染科,急诊科
	 * eg: str = 免疫科,感染科,急诊科    ，返回 免疫科,感染科,急诊科
	 * @param str ,免疫科,肿瘤科,老年科,或者 免疫科,肿瘤科,老年科
	 * @return 拼接好的字符串
	 */
	public static String removeCommaFromString(String str){
		if(str == null || "".equals(str)){
			return str;
		}
		if(str.startsWith(",")){
			str = str.substring(1, str.length());
		}
		if(str.endsWith(",")){
			str = str.substring(0, str.length() - 1);
		}
		str = str.replaceAll(" ", "");
		
		return str;
	}
	
	/**
	 * 将str1和str2拼接起来(先去除重复，在拼接)。<br/>
	 * eg: str1 = ,免疫科,感染科,急诊科, str2 = ,免疫科,肿瘤科,老年科, 返回结果 ,感染科,急诊科,免疫科,肿瘤科,老年科,
	 * @param str1 ,免疫科,感染科,急诊科,
	 * @param str2 ,免疫科,肿瘤科,老年科,
	 * @return 拼接好的字符串
	 */
	public static String join2String(String str1, String str2){
		if(str1 == null || "".equals(str1)){
			if(str2 == null || "".equals(str2)){
				return "";
			}else{
				return str2;
			}
		}else{
			if(str2 == null || "".equals(str2)){
				return str1;
			}
		}
		// 去除重复
		str1 = CommonUtil.removeDuplicate(str1, str2);
		// str1为空，证明2串相同
		if(str1.equals("")){
			return str2;
		}
		// str2去除第一个逗号，然后合并字符串。
		str1 = str1.substring(1) + str2.substring(1);

		return str1;
	}
	
	/**
	 * 将str1和str2拼接起来(先去除重复，在拼接)。<br/>
	 * eg: str1 = ,1,3,5, str2 = ,1,2,4, 返回结果 ,1,2,3,4,5,
	 * @param str1 ,1,3,5,
	 * @param str2 ,1,2,4,
	 * @return 拼接好的字符串
	 */
	public static String join2NumString(String str1, String str2){
		if(str1 == null || "".equals(str1)){
			if(str2 == null || "".equals(str2)){
				return "";
			}else{
				return str2;
			}
		}else{
			if(str2 == null || "".equals(str2)){
				return str1;
			}
		}
		// 去除重复
		str1 = CommonUtil.removeDuplicate(str1, str2);
		// str1为空，证明2串相同
		if(str1.equals("")){
			return str2;
		}
		// str2去除第一个逗号，然后合并字符串。
		str1 = str1.substring(1) + str2.substring(1);
		// 字符串数组转化为数组数组
		int[] intArray = CommonUtil.stringArray2IntArray(str1.split(","));
		// 排序
		Arrays.sort(intArray);
		String[] strArray = CommonUtil.intArray2StringArray(intArray);
		String joinedStr = ",";
		for(int i = 0; i < strArray.length; i++){
			joinedStr = joinedStr + strArray[i] + ",";
		}
		return joinedStr;
	}

	/**
	 * 从orignal中去掉duplicate包含的字符串。<br/>
	 * eg: orignal = ,1,2,3,4,5, duplicate = ,2,4, 返回结果 ,1,3,5,
	 * @param orignal 原生的字符串
	 * @param duplicate 要被去除的字符串
	 * @return 除重后的字符串。(orignal为空，则返回空串。duplicate为空，返回orignal)
	 * @throws Exception
	 */
	public static String removeDuplicate(String orignal, String duplicate){
		if(orignal == null || "".equals(orignal)){
			return "";
		}
		if(duplicate == null || "".equals(duplicate)){
			return orignal;
		}
		// 去重字符串数组
		String[] duplicateArray = duplicate.split(",");

		for(int i = 0; i < duplicateArray.length; i++){
			orignal = orignal.replace("," + duplicateArray[i] + ",", ",");
		}
		// 如果只剩下“,”，则证明orignal全部为空了
		if(orignal.equals(",")){
			orignal = "";
		}
		return orignal;
	}

	/**
	 * 从str1和str2取得相同的字符串。<br/>
	 * eg: str1 = ,1,2,3,4,5, str2 = ,2,4, 返回结果 ,2,4,
	 * @param str1
	 * @param str2
	 * @return 相同的字符串。(str1为空或者str2为空，返回"")，在没有相同的情况的，在返回""
	 * @throws Exception
	 */
	public static String getDuplicate(String str1, String str2){
		if(str1 == null || "".equals(str1) || str2 == null || "".equals(str2)){
			return "";
		}
		// 去重字符串数组
		String[] str2Array = str2.split(",");
		String result = ",";
		for(int i = 0; i < str2Array.length; i++){
			int index = str1.indexOf("," + str2Array[i] + ",");
			if(index > -1){
				result = result + str2Array[i] + ",";
			}
		}
		// 如果只剩下“,”，则证明没有相同的
		if(result.equals(",")){
			result = "";
		}
		return result;
	}

	/**
	 * str1和str2，是否有相同的内容。
	 * @param str1
	 * @param str2
	 * @return true：有相同内容，false:没有
	 */
	public static boolean hasDuplicate(String str1, String str2){
		if("".equals(getDuplicate(str1, str2))){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 字符串数组转化为int数组
	 * @param stringArray
	 * @return
	 */
	public static int[] stringArray2IntArray(String[] stringArray){
		int[] intArray = new int[stringArray.length];
		for(int i = 0; i < stringArray.length; i++){
			intArray[i] = Integer.valueOf(stringArray[i]).intValue();
		}
		return intArray;
	}

	/**
	 * int数组转化为字符串数组
	 * @param intArray
	 * @return
	 */
	public static String[] intArray2StringArray(int[] intArray){
		String[] stringArray = new String[intArray.length];
		for(int i = 0; i < intArray.length; i++){
			stringArray[i] = String.valueOf(intArray[i]);
		}
		return stringArray;
	}

	/**
	 * 字符串数组转化为Long型List。
	 * @return
	 */
	public static List<Long> stringArray2List(String[] stringArray){
		List<Long> list = new ArrayList<Long>();
		for(int i = 0; i < stringArray.length; i++){
			list.add(Long.valueOf(stringArray[i]));
		}
		return list;
	}

	/**
	 * 字符串数组转化为Long数组
	 * @param stringArray
	 * @return
	 */
	public static Long[] stringArray2LongArray(String[] stringArray){
		Long[] intArray = new Long[stringArray.length];
		for(int i = 0; i < stringArray.length; i++){
			intArray[i] = Long.valueOf(stringArray[i]);
		}
		return intArray;
	}


	/**
	 * HTML特殊字符的处理
	 * @return string
	 * @throws Exception
	 */
	public static String toHTMLString(String strCheck){
	    StringBuffer strReturn=new StringBuffer();
        if(strCheck != null){
            for (int i=0;strCheck!=null&&i<strCheck.length();i++){
                char chrGet=strCheck.charAt(i);
                if(chrGet=='\'')
                    strReturn.append("&#039;");
                else if(chrGet=='\"')
                    strReturn.append("&#034;");
                else if(chrGet=='<')
                    strReturn.append("&lt;");
                else if(chrGet=='>')
                    strReturn.append("&gt;");
                else if(chrGet=='&')
                    strReturn.append("&amp;");
                else if(chrGet== ' ')
                    strReturn.append("&nbsp;");
                else if(chrGet=='\n')
                    strReturn.append("<br/>");
                else
                    strReturn.append(chrGet);
            }
        }
	    return strReturn.toString();
	}

	/**
	 * HTML特殊字符的处理
	 * @return string
	 * @throws Exception
	 */
	public static String html2String(String strCheck){
		if(strCheck != null){
			strCheck = strCheck.replaceAll("&#039;", "\'");
			strCheck = strCheck.replaceAll("&#034;", "\"");
			strCheck = strCheck.replaceAll("&lt;", "<");
			strCheck = strCheck.replaceAll("&gt;", ">");
			strCheck = strCheck.replaceAll("&amp;", "&");
			strCheck = strCheck.replaceAll("&nbsp;", " ");
			strCheck = strCheck.replaceAll("<br/>", "\n");
		}else{
			return "";
		}

	    return strCheck;
	}

	/**
	 * 判断该类中的属性值是否包含NULL空值
	 * @param obj
	 * @param checkedList --需要检查是否为空值的字段
	 * @return true包含空值，false不包含空值
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkObjectHasNullValue(Object obj, List<String> checkedList) {
		boolean result = false;
		Class classType = obj.getClass();
		try {
			if(checkedList != null && checkedList.size() > 0) {
				BeanInfo beanInfo = Introspector.getBeanInfo(classType);
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				for(PropertyDescriptor pd : propertyDescriptors) {
					if(!checkedList.contains(pd.getName())) {  //包含不需要检测NULL值的项跳过循环
//					if(uncheckedList.contains(pd.getName())) {  包含不需要检测NULL值的项跳过循环
						continue;
					}
					Method readMethod = pd.getReadMethod();
					String methodReturnTypeStr = readMethod.getReturnType().toString();
					if(methodReturnTypeStr.substring(methodReturnTypeStr.lastIndexOf(".")+1).equals("String")) {
						if(readMethod.invoke(obj) == null || "".equals(readMethod.invoke(obj).toString())) {
							System.out.println(readMethod.toString());
							result = true;
							break;
						}
					} else if(readMethod.invoke(obj) == null) {
						result = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 验证邮箱地址是否合法
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email){
		final String reg = "^([\\w\\.-]+)@([\\w\\.-]+)\\.\\w+$";
		final Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(email).find();
	}

	/**
	 * 根据传入的参数，返回此数值的大小和单位
	 * 例如：10485760 返回10MB，1024 返回 1KB
	 * @param size
	 * @return
	 */
	public static String longSize2String(Long size){
		BigDecimal tmp = new BigDecimal(size);
		String[] unit = {"B","KB","MB","GB","TB","PB","EB","ZB","YB","BB"};
		int i = 0;
		while(tmp.longValue() >= 1024){
			tmp = tmp.divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_UP);
			i++;
		}
		String result = tmp.toString() + unit[i];
		return result;
	}


	/**
	 * 将指定路径的文件，写到输入流对象中
	 *
	 * @param path
	 *            文件路径
	 * @param servletContext servletContext对象
	 * @return 输入流对象
	 */
	public static InputStream writeInputStream(String path,
			ServletContext servletContext) {
		InputStream downlaodStream = null;
		try {
			// 取得输入文件流
			InputStream is = servletContext.getResourceAsStream(path);
			// 输出文件流
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 缓冲区大小
			byte[] buffer = new byte[1024];
			int readLength = 0;
			while ((readLength = is.read(buffer)) != -1) {
				baos.write(buffer, 0, readLength);
			}
			is.close();
			baos.close();
			byte[] ba = baos.toByteArray();
			downlaodStream = new ByteArrayInputStream(ba);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return downlaodStream;
	}

	/**
	 * 格式化输入文件名【防止输入文件名为乱码】
	 *
	 * @param fileName
	 *            文件名
	 * @return 转换后文件名
	 */
	public static String formatFileName(String fileName) {
		try {
			fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * 是否含有中文
	 *
	 */
	public static boolean hasChina(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fa5]")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否包含英文
	 *
	 */
	public static boolean hasEnglish(String str) {
		if (StringUtils.isEmpty(String.valueOf(str))) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\u0041-\\u005A]")
					|| str.substring(i, i + 1).matches("[\\u0061-\\u007A]")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * byte编码为gb2312
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isChinese(byte b){
		if(b >= (byte)0x81 && b <= (byte)0xFE){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 是否是数字
	 *
	 */
	public static boolean isDigit(String str) {
		if (StringUtils.isEmpty(String.valueOf(str))) {
			return false;
		}
		if (str.matches("[0-9]*")) {
			return true;
		}
		return false;
	}
	
	private static String[] SPIDERS = { "Googlebot", "msnbot", "Baiduspider", "bingbot", "Sogou web spider",
		"Sogou inst spider", "Sogou Pic Spider", "JikeSpider", "Sosospider", "Slurp", "360Spider", "YodaoBot",
		"OutfoxBot", "fast-webcrawler", "lycos_spider", "scooter", "ia_archiver", "MJ12bot", "AhrefsBot"};
	
	/**
	 * 判断是否是爬虫的访问请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isRequestFromSpider(HttpServletRequest request) {
		boolean isSpider = false;
		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null && userAgent.trim().length() > 0) {
			userAgent = userAgent.trim().toLowerCase();
			for (String spider : SPIDERS) {
				if (userAgent.indexOf(spider.toLowerCase()) >= 0) {
					isSpider = true;
					break;
				}
			}
		}
		return isSpider;
	}
	
	/**
	 * 取得请求的IP地址
	 * @param request
	 * @return
	 */
	public static String getRemoteIpAddr(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		if(isValidIpAddr(ip)){
			return ip.split(",")[0];
		}
		
		ip = request.getHeader("Proxy-Client-IP");
		if(isValidIpAddr(ip)){
			return ip;
		}
		
		ip = request.getHeader("WL-Proxy-Client-IP");
		if(isValidIpAddr(ip)){
			return ip;
		}
		
		ip = request.getHeader("HTTP_CLIENT_IP");
		if(isValidIpAddr(ip)){
			return ip;
		}
		
		return request.getRemoteAddr();
	}
	
	/**
	 * 判断IP地址是否有效
	 * @param ip
	 * @return
	 */
	private static boolean isValidIpAddr(String ip){
		return ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown");
	}
	
	/**
	 * 取得浏览的base路径
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getBasePath(HttpServletRequest request) throws UnsupportedEncodingException{
		String path = request.getContextPath();
		int port = request.getServerPort();
        String basePath = null;
		if(port == 80){
            basePath = request.getScheme() + "://"
                    + request.getServerName() + path + "/";
        }else{
            basePath = request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + path + "/";
        }

		return basePath;
	}
	
	/**
	 * 取得当前请求的完全URL
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getFullUrl(HttpServletRequest request, boolean encode) throws UnsupportedEncodingException{
		String orginUrl = request.getRequestURL().toString();
		if(null != request.getQueryString()){
			orginUrl += "?" + request.getQueryString();
		}else{
			Map<String, String[]> parpMap = request.getParameterMap();
			Set<String> keys = parpMap.keySet();
			String value = null;
			StringBuffer querys = new StringBuffer();
			for(String key : keys){
				value = parpMap.get(key)[0];
				if(null != value && !value.isEmpty()){
					querys.append(key);
					querys.append("=");
					querys.append(value);
					querys.append("&");
				}
			}
			if(querys.length() != 0){
				orginUrl += "?" + querys.substring(0, querys.length()-1).toString();
			}
		}
		try{
			if(encode){
				orginUrl = URLEncoder.encode(orginUrl,"UTF-8");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return orginUrl;
	}
	
	/**
	 * 将checkboxlist传来的字符串1, 2, 3转化为,1,2,3,
	 * @param ckStr
	 * @return
	 */
	public static String checkBoxStrToCommaStr(String ckStr){
		// 将分科正好好，按照逗号分隔
		StringBuffer branchStr = new StringBuffer();
		if(ckStr != null && !ckStr.equals("")){
			String[] branchArray = ckStr.split(",");
			for(String branch : branchArray) {
				branchStr.append(",").append(branch.trim());
			}
			branchStr.append(",");
		}
		return branchStr.toString();
	}
	
	/**
	 * 字符串转成list
	 * 结果：[1,2,3] 
	 * @author SQ
	 * @date 2014年12月25日 下午4:28:17 
	 *
	 */
	public static List<String> strToList(String str){
		List<String> list = new ArrayList<String>();
		if(StringUtils.isNotEmpty(str)){
			//,1,2,3, 截成  1,2,3
			String s = str.substring(1, str.length()-1);
			if(s.indexOf(",") >= 0){
				String[] arr = s.split(",");
				for(int i = 0; i < arr.length; i++){
					list.add(arr[i].trim());
				}
			}else{
				list.add(s.trim());
			}
		}
		return list;
	}
	
	
	/**
	 * list 转成 字符串
	 * 结果,1,2,3,
	 * @author SQ
	 * @date 2014年12月26日 上午10:27:05 
	 *
	 */
	public static String listToStr(List<String> list){
		String str = "";
		if(list != null && list.size() > 0){
			str = ",";
			for(String s:list){
				str = str+s+",";
			}
		}
		return str;
	}

	
	public static String decode(String code) throws Exception {
        String fis = URLDecoder.decode(code, "gb2312");
        String sec = new String(fis.getBytes("gb2312"), "gb2312");
        if (fis.equals(sec))
            return fis;
        else {
            return URLDecoder.decode(code, "utf-8");
        }
	}
	
	public static void main(String[] args) throws Exception {
//		String orignal = ",免疫科,感染科,急诊科,";
//		String duplicate = ",免疫科,肿瘤科,老年科,";
//		String result = CommonUtil.join2String(orignal, duplicate);
//		System.out.println(result);
//		 
//		System.out.println(URLEncoder.encode("http://www.baidu.com","UTF-8"));
		System.out.println(CommonUtil.checkBoxStrToCommaStr(""));
//
//		result = CommonUtil.join2String(orignal, duplicate);
//		System.out.println(result);
//		System.out.println(longSize2String((long)Math.pow(2, 20)));
//		String orignal = "";
//		System.out.println(orignal.split(",").length);
	}
}
