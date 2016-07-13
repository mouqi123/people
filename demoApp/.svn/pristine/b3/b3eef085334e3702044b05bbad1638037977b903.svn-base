package com.people.sotp.common.dao;

import java.util.List;

public interface BaseDao {
	/**
	 * 按照条件查询多行
	 * 
	 * @param sqlmapper
	 * @param map
	 * @return
	 */
	public List<?> query(String sqlmapper, Object map);

	/**
	 * 按照条件查询数据
	 * 
	 * @param sqlmapper
	 * @param obj
	 * @return
	 */
	public Object queryOne(String sqlmapper, Object obj);

	/**
	 * 无条件查
	 * 
	 * @param sqlmapper
	 * @return
	 */
	public List<?> query(String sqlmapper);

	/**
	 * 无条件查询
	 * 
	 * @param sqlmapper
	 * @return
	 */
	public Integer queryTotal(String sqlmapper);

	/**
	 * 有条件查询
	 * 
	 * @param sqlmapper
	 * @param obj
	 * @return
	 */
	public Integer queryTotal(String sqlmapper, Object obj);

	/**
	 * 添加数据
	 * 
	 * @param sqlmapper
	 * @param obj
	 * @return
	 */
	public int insert(String sqlmapper, Object obj);

	/**
	 * 更新数据
	 * 
	 * @param sqlmapper
	 * @param obj
	 * @return
	 */
	public int update(String sqlmapper, Object obj);

	/**
	 * 删除数据
	 * 
	 * @param sqlmapper
	 * @param obj
	 * @return
	 */
	public int delete(String sqlmapper, Object obj);
}
