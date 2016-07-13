package com.people.sotp.common.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;

@Repository
public class BaseDaoImpl extends SqlSessionDaoSupport implements BaseDao {

	@Resource
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public int delete(String sqlmapper, Object obj) {
		return this.getSqlSession().delete(sqlmapper, obj);
	}

	public int insert(String sqlmapper, Object obj) {
		return this.getSqlSession().insert(sqlmapper, obj);
	}

	public List<?> query(String sqlmapper, Object obj) {
		return this.getSqlSession().selectList(sqlmapper, obj);
	}

	public int update(String sqlmapper, Object obj) {
		return this.getSqlSession().update(sqlmapper, obj);
	}

	public Object queryOne(String sqlmapper, Object obj) {
		return this.getSqlSession().selectOne(sqlmapper, obj);
	}

	public List<?> query(String sqlmapper) {
		return this.getSqlSession().selectList(sqlmapper);
	}

	public Integer queryTotal(String sqlmapper, Object obj) {
		return (Integer) this.getSqlSession().selectOne(sqlmapper, obj);
	}

	public Integer queryTotal(String sqlmapper) {
		return (Integer) this.getSqlSession().selectOne(sqlmapper);
	}

}
