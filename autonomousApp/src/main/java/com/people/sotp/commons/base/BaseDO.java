package com.people.sotp.commons.base;

import java.io.Serializable;

/**
 * 数据对象基类
 */
public abstract class BaseDO implements Serializable {

	private static final long serialVersionUID = 1L;
	// 每页显示行数
	public long pagesize = 10;
	// 页码
	public long page = 1;
	// 总数
	public long total = 0;
	public long changepage = 1;

	public long startNum;

	public long endNum;

	public String ids;

	public long getChangepage() {
		return changepage;
	}

	public void setChangepage(long changepage) {
		this.changepage = changepage;
	}

	public long getPagesize() {
		return pagesize;
	}

	public void setPagesize(long pagesize) {
		this.pagesize = pagesize;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getStartNum() {
		return (page - 1) * pagesize;
	}

	public void setStartNum(long startNum) {
		this.startNum = startNum;
	}

	public long getEndNum() {
		return page * pagesize;
	}

	public void setEndNum(long endNum) {
		this.endNum = endNum;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
