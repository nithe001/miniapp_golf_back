package com.golf.common.model;

import java.util.Map;

/**
 * 查询条件接口
 * 
 * @author 李旭光
 * @version 2010-09-28
 * 
 */
public interface ISearchBean {

	/**
	 * 排序类型
	 * 
	 * @author 李旭光
	 * @version 2010-09-28
	 */
	public static enum Sort {
		/**
		 */
		desc, /**
		 */
		asc;

		/**
		 * 根据名称获取对象
		 * 
		 * @param name
		 * @return
		 */
		public static Sort getInstance(String name) {
			if (name == null) {
				return asc;
			}
			if (desc.name().equals(name.toLowerCase())) {
				return desc;
			}
			return asc;
		}

	}

	/**
	 * 取得排序信息
	 * 
	 * @return
	 */
	public Map<String, Sort> getSort();

	/**
	 * 取得查询条件信息
	 * 
	 * @return
	 */
	public Map<String, Object> getParps();

}
