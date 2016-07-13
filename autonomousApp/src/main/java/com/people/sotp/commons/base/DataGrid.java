package com.people.sotp.commons.base;

import java.util.ArrayList;
import java.util.List;

/**
 * DataGrid对象
 * 
 * @author jianshi.dlw
 */
@SuppressWarnings("rawtypes")
public class DataGrid implements java.io.Serializable {

	private static final long serialVersionUID = 8993695240960018877L;
	private Integer total = 0;

	private List rows = new ArrayList();

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}
}
