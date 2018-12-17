package com.golf.common.spring.mvc;import java.util.Locale;import java.util.Map;import org.springframework.core.io.Resource;import org.springframework.web.context.WebApplicationContext;import org.springframework.web.servlet.support.RequestContextUtils;/** * spring mvc 上下文工具类 *  * @author 刘佳 * @version 1.0 20140213 *  */public class SpringMVCContext {	/**	 * 取得ApplicationContext	 * 	 * @return ApplicationContext	 */	public static WebApplicationContext getMVCContext() {		return RequestContextUtils.getWebApplicationContext(WebUtil.getRequest());	}	/**	 * 取得Bean	 * 	 * @param beanName	 *            bean的名字	 * 	 * @return Object	 */	@SuppressWarnings("unchecked")	public static <E> E getBean(String beanName) {		return (E) getMVCContext().getBean(beanName);	}	public static <T> T getBean(Class<T> cls) {		Map<String, T> map = getMVCContext().getBeansOfType(cls);		if (map == null) {			return null;		}		T result = null;		for (T model : map.values()) {			result = model;			break;		}		return result;	}	/**	 * 取得message	 * 	 * @param name	 * @return String	 */	public static String getMessage(String name) {		return getMVCContext().getMessage(name, new Object[] {},				Locale.SIMPLIFIED_CHINESE);	}	/**	 * 取得message	 * 	 * @param name	 * @param para	 * @return String	 */	public static String getMessage(String name, Object[] para) {		return getMVCContext()				.getMessage(name, para, Locale.SIMPLIFIED_CHINESE);	}	/**	 * 获得外部文件	 * 	 * @param path	 * @return Resource	 */	public static Resource getResource(String path) {		return getMVCContext().getResource(path);	}	/**	 * 取得给定locale下的message	 * 	 * @param name	 * @param local	 * @return	 */	public static String getMessage(String name, Locale local) {		if (local == null) {			local = Locale.SIMPLIFIED_CHINESE;		}		return getMVCContext().getMessage(name, new Object[] {}, local);	}}