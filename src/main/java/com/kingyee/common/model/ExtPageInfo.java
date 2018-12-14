package com.kingyee.common.model;

import java.util.List;

/**
 * EXT用到的分页信息
 *
 * @author 李旭光
 * @version 2010-7-23
 */
public class ExtPageInfo<T> implements IPageInfo<T> {

	/**
	 */
	private int count;

	/**
	 */
	private int start;

	/**
	 */
	private int limit;

	/**
	 */
	private List<T> items;

	public ExtPageInfo() {
	}

	public ExtPageInfo(int start, int limit) {
		this.start = start;
		this.limit = limit;
	}

	/**
	 * @return
	 */
	@Override
	public int getCount() {
		return this.count;
	}

	/**
	 * @return
	 */
	@Override
	public int getLimit() {
		return this.limit;
	}

	/**
	 * @return
	 */
	@Override
	public int getStart() {
		return this.start;
	}

	/**
	 * @param  count
	 */
	@Override
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param start   the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @param limit   the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

}
