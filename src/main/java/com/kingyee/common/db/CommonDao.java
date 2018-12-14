package com.kingyee.common.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.kingyee.common.db.BaseDAO;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;

/**
 * BaseService提供dao注入
 * 
 * @author 李旭光
 * @version 2009-6-30
 */
public class CommonDao {

	@Autowired
	protected BaseDAO dao;

	/**
	 * 通过主键取得数据库对象，由于load不存在的id时，会直接抛出异常，所以建议使用get方法。
	 * 
	 * @param <E>
	 *            对象类型
	 * @param table
	 *            表
	 * @param s
	 *            主键
	 * @return
	 */
	@Deprecated
	public <E> E load(Class<E> table, Serializable s) {
		return dao.load(table, s);
	}

	/**
	 * 通过主键取得数据库对象
	 * 
	 * @param <E>
	 *            对象类型
	 * @param table
	 *            表
	 * @param s
	 *            主键
	 * @return
	 */
	public <E> E get(Class<E> table, Serializable s) {
		return dao.get(table, s);
	}

	/**
	 * 保存数据对象
	 * 
	 * @param <E>
	 * @param table
	 * @return 主键
	 */
	@SuppressWarnings("unchecked")
	public <E extends Serializable> E save(Object table) {
		return (E) dao.save(table);
	}

	/**
	 * 更新
	 * 
	 * @param table
	 */
	public void update(Object table) {
		dao.update(table);
	}

	/**
	 * 删除一条数据
	 * 
	 * @param table
	 */
	public void del(Object table) {
		dao.delete(table);
	}

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param cls
	 * @param pk
	 * 
	 * @version 2012-10-31
	 */
	public void del(Class<?> cls, Long pk) {
		dao.delete(dao.load(cls, pk));
	}

	/**
	 * 
	 * @param searchBean
	 * @param pageInfo
	 * 
	 *            模糊查询 2015 5-7 李芳
	 */
	public <T extends Object> void query(SearchBean searchBean, POJOPageInfo<T> pageInfo) {
		dao.query(searchBean, pageInfo);
	}

	/**
	 * 2015-5-9李芳
	 * 
	 * 批量保存
	 * 
	 * @param objs
	 */
	public void saveAll(Collection<?> objs) {
		for (Object object : objs) {
			dao.save(object);
		}
	}

	/**
	 * 2015-5-9李芳 批量修改
	 * 
	 * @param objs
	 */
	public void updateAll(Collection<?> objs) {
		for (Object object : objs) {
			dao.update(object);
		}
	}

	/**
	 * 查询多条数据
	 * 
	 * @param hql
	 * @return
	 */
	public <T> List<T> getAlls(String hql) {
		return dao.createQuery(hql);
	}	
	
	public <E> List<E> createQuery(String hql) { //修改
		return dao.createQuery(hql);
	}
	
	/**
	 * 查询总记录数
	 * @param query
	 * @return
	 */
	
	public List<String> selectList(String hql, List values) {
		return dao.createQuery(hql, values);
	}

	public <T> List<T> selectObj(String hql, List values) {
		return dao.createQuery(hql, values);
	}

	public List<Long> selectHql(String hql) {
		return dao.createQuery(hql);
	}

	public <E> List<E> createSQLQuery(String hql,Map parp,int start,int rowsPerPage) {
		return dao.createSQLQuery(hql,parp,start,rowsPerPage);
	}
	public long createSQLCountQuery(String hql,Map parp) {
		return dao.createSQLCountQuery(hql,parp);
	}


    /**
     * 查询第几条-->第几条记录
     * @param hql
     * @param offset
     * @param maxRow
     * @return
     */
    public <E> List<E> createQuery(final String hql, final Map parp, final int offset, final int maxRow) { //添加
        return dao.createQuery(hql, parp, offset, maxRow);
    }

    /**
     * 查询总记录数
     * @param query
     * @return
     */
    public Long createCountQuery(final String query, final Map parp) {		//添加
        return dao.createCountQuery(query, parp);
    }

    /**
     * 带条件查询.
     *
     * @param query
     * @param parp
     * @return List
     */
    public <E> List<E> createQuery(final String query, final Map parp) {
        return dao.createQuery(query, parp);
    }

    /**
     * 通过SQL文查询
     * @param query
     * @return
     */
    public Integer executeHql(final String query, final Map parp) {
        return dao.executeHql(query,parp);
    }

    /**
     * 执行Hql
     *
     * @param hql
     * @param value
     */
    public void executeHql(String hql, Object value) {
        dao.executeHql(hql, new Object[] { value });
    }
}
