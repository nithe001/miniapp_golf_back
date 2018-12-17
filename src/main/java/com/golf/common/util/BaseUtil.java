package com.golf.common.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基础帮助类
 *
 * @author lixl
 */
public class BaseUtil {

	private static final String EMPTY = "";

	/**
	 * 对象是否为 null 或 空串
	 *
	 * @param obj
	 * @return boolean
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof Map) {
			return ((Map) obj).size() == 0;
		} else if (obj instanceof List) {
			return ((List) obj).size() == 0;
		} else if (obj.getClass().isArray()) {
			return ((Object[]) obj).length == 0;
		} else {
			return EMPTY.equals(obj.toString());
		}
	}

	/**
	 * 对象是否不为空
	 *
	 * @param obj
	 * @return boolean
	 */
	public static boolean isNotEmpty(Object obj) {
		return !(isEmpty(obj));
	}

	/**
	 * 向创建指定大小的LIST对象。
	 *
	 * @param list
	 * @param size
	 * @param clazz
	 * @return List
	 */
	public static List createEmptyObjToList(List list, int size, Class clazz) {
		if(list == null) {
			list = new ArrayList();
		}
		if (list.size() < size) {
			for (int i = list.size(); i < size; i++) {
				try {
					list.add(clazz.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 向创建指定大小的Set对象。
	 *
	 * @param set
	 * @param size
	 * @param clazz
	 * @return List
	 */
	public static void createEmptyObjToSet(Set set, int size, Class clazz) {
		if(set == null) {
			set = new LinkedHashSet();
		}
		if (set.size() < size) {
			for (int i = set.size(); i < size; i++) {
				try {
					set.add(clazz.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}