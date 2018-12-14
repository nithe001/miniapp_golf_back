package com.kingyee.common.model;

import java.util.List;

/**
 * 分页信息
 * 
 * @author 李旭光
 * @version 2010-7-23
 */
public interface IPageInfo<E> {

	/**
	 * 取得总记录数
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * 设置总记录数
	 * 
	 * @param count
	 */
	public void setCount(int count);

	/**
	 * 开始的记录
	 * 
	 * @return
	 */
	public int getStart();

	/**
	 * 每次取得的记录数
	 * 
	 * @return
	 */
	public int getLimit();

	/**
	 * 记录数据
	 * 
	 * @param items
	 */
	public void setItems(List<E> items);

	/**
	 * 记录数据
	 * 
	 * @return
	 */
	public List<E> getItems();

}
