package com.golf.common.spring;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * spring 工具类
 * 
 * @author 刘佳
 * @version 1.0 20070424 初版
 * 
 */
public class SpringContext implements ApplicationContextAware {

	/**
	 * Spring的ApplicationContext
	 */
	protected static ApplicationContext appContext;

	/**
	 * 设置ApplicationContext
	 * 
	 * @param ac
	 *            ApplicationContext
	 * @throws BeansException
	 */
	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		SpringContext.appContext = ac;
	}

	/**
	 * 取得ApplicationContext
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getAppContext() {
		return appContext;
	}

	/**
	 * 取得Bean
	 * 
	 * @param beanName
	 *            bean的名字
	 * 
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getBean(String beanName) {
		return (E)appContext.getBean(beanName);
	}

	/**
	 * 取得message
	 * 
	 * @param name
	 * @return String
	 */
	public static String getMessage(String name) {
		return appContext.getMessage(name, new Object[] {},
				Locale.SIMPLIFIED_CHINESE);
	}

	/**
	 * 取得message
	 * 
	 * @param name
	 * @param para
	 * @return String
	 */
	public static String getMessage(String name, Object[] para) {
		return appContext.getMessage(name, para, Locale.SIMPLIFIED_CHINESE);
	}

	/**
	 * 获得外部文件
	 * 
	 * @param path
	 * @return Resource
	 */
	public static Resource getResource(String path) {
		return appContext.getResource(path);
	}
}
