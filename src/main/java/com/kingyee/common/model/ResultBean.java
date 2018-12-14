package com.kingyee.common.model;


/**
 * 查询结果bean
 * 
 * @author peihong
 * @version 2011-06-21
 */
public class ResultBean<T> {

	private T result;
	
	/** 分页信息 */
	private IPageInfo pageInfo;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public IPageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(IPageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	
}
