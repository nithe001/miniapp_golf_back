package com.kingyee.common.model;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通对象的分页信息
 * 
 * @author 李旭光
 * @version 2010-7-26
 * @param <T>
 */
public class POJOPageInfo<T> implements IPageInfo<T>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2600940576717621591L;
	
	/**
	 */
	private int count;

	/**
	 */
	private int rowsPerPage = 10;

	/**
	 */
	private int nowPage;

	/**
	 */
	private List<T> items = new ArrayList<T>();
	public POJOPageInfo() {
	}
	public POJOPageInfo(int rowsPerPage, int nowPage) {
		this.rowsPerPage = rowsPerPage;
		this.nowPage = nowPage;
	}

	public POJOPageInfo(int rowsPerPage, int nowPage, int count) {
		this.rowsPerPage = rowsPerPage;
		this.nowPage = nowPage;
		this.setCount(count);
	}
	
	/**
	 * @return
	 */
	@Override
	public int getCount() {
		return this.count;
	}

	@Override
	public int getLimit() {
		return this.rowsPerPage;
	}

	@Override
	public int getStart() {
		return (this.nowPage - 1) * this.rowsPerPage;
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
	 * @return   the rowsPerPage
	 */
	public int getRowsPerPage() {
		return rowsPerPage;
	}
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * @return   the nowPage
	 */
	public int getNowPage() {
		return nowPage;
	}
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}
	
	/**
	 * 总共多少页
	 * @return
	 */
	public int getPages(){
		if(getCount() == 0){
			return 1;
		}
		
		int limit = getLimit();
		if(limit == 0){
			limit = getCount();
		}
		int pages = 0;
		if(getCount() % limit == 0){
			pages = getCount() / limit;
		}else{
			pages = getCount() / limit + 1;
		}
		return pages;
	}
	
	/**
	 * 是否是最后一页
	 * @return
	 */
	public boolean isLast(){
		return getNowPage() >= getPages();
	}
	/**
	 * 是否是首页
	 * @return
	 */
	public boolean isFirst(){
		return getNowPage() <= 1;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<T> getEntityClz(){
		Type type = getClass().getGenericSuperclass();
		if(type instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType)type;
			Type[] types = pt.getActualTypeArguments();
			if(types.length >0 && types[0] instanceof Class){
				return (Class) types[0];
			}
		}
		return (Class)Object.class;
	}
}
