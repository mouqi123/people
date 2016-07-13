package com.people.sotp.commons.base;

import java.io.Serializable;
import java.util.List;

/**
 * 数据对象基类
 */
public abstract class BaseDO implements Serializable {

	private static final long serialVersionUID = 1L;
	// 每页显示行数
	public long pagesize = 10;
	// 页码
	public long pageNo = 1;
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

	

	public long getPageNo() {
		return pageNo;
	}

	public void setPageNo(long pageNo) {
		this.pageNo = pageNo;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getStartNum() {
		return (pageNo - 1) * pagesize;
	}

	public void setStartNum(long startNum) {
		this.startNum = startNum;
	}

	public long getEndNum() {
		return pageNo * pagesize;
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
