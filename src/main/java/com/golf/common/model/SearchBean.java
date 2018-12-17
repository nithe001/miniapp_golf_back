package com.golf.common.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 检索条件bean
 * 
 * @author 李旭光
 * @version 2010-09-28
 * 
 */
public class SearchBean implements ISearchBean {

	/**
	 * 检索条件
	 */
	private Map<String, Object> parps = new HashMap<String, Object>();

	/**
	 * 排序规则
	 */
	private Map<String, Sort> sort = new LinkedHashMap<String, Sort>();

	/**
	 * 添加查询条件
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public SearchBean addParpField(String field, Object value) {
		this.parps.put(field, value);
		return this;
	}

	/**
	 * 添加排序条件<br>
	 * {@link #addSortField(String, String)}
	 * 
	 * @param field
	 * @param sort
	 * @return
	 */
	@Deprecated
	public SearchBean addSortField(String field, Sort sort) {
		this.sort.put(field, sort);
		return this;
	}

	/**
	 * 添加排序条件
	 * 
	 * @param field
	 *            排序字段
	 * @param direction
	 *            排序类型
	 * @return
	 */
	public SearchBean addSortField(String field, String direction) {
		this.sort.put(field, Sort.getInstance(direction));
		return this;
	}

	public void setParps(Map<String, Object> parps) {
		this.parps = parps;
	}

	public void setSort(Map<String, Sort> sort) {
		this.sort = sort;
	}

	@Override
	public Map<String, Object> getParps() {
		return this.parps;
	}

	@Override
	public Map<String, Sort> getSort() {
		return this.sort;
	}

}
