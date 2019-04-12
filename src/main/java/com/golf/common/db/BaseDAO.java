package com.golf.common.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.IntegerType;

import com.golf.common.model.ISearchBean.Sort;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.DecimalUtil;

/**
 * <b>Spring 的 Hibernate工具类</b>
 * 
 * 以hibernate为基础,提供基础查询/添加/删除/修改方法.
 * 
 * @author 刘佳 裴宏
 * @version 1.0 20070424 初版
 * @version 2.0 20150430 改为适配hibernate4的版本
 */
public class BaseDAO {

	public final static String DB_PARP = "p";

	private SessionFactory sessionFactory;

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @return List
	 */
	public <E> List<E> createQuery(final String query, final int offset, final int maxRow) {
		Query q = getCurrentSession().createQuery(query);
		q = limit(q, offset, maxRow);
		return (List<E>) q.list();
	}

	public Long createCountQuery(final String query, final int offset, final int maxRow) {
		List<Long> r = this.createQuery(query, offset, maxRow);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public <E> List<E> createQuery(final String query, final Map parp, final int offset, final int maxRow) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();
	}

	public Long createCountQuery(final String query, final Map parp, final int offset, final int maxRow) {
		List<Long> r = this.createQuery(query, parp, offset, maxRow);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	public <E> List<E> createQuery(final String query, final List parp, final int offset, final int maxRow) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();
	}

	public Long createCountQuery(final String query, final List parp, final int offset, final int maxRow) {
		List<Long> r = this.createQuery(query, parp, offset, maxRow);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public <E> List<E> createQuery(final String query, final Map parp) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		return q.list();
	}
	
	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public <E> E findOne(final String query, final Map parp) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		List<E> list = q.list();
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public Long createCountQuery(final String query, final Map parp) {
		List<Long> r = this.createQuery(query, parp);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	public <E> List<E> createQuery(final String query, final List parp) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		return q.list();
	}

	public Long createCountQuery(final String query, final List parp) {
		List<Long> r = this.createQuery(query, parp);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @return List
	 */
	public <E> List<E> createQuery(final String query) {
		Query q = getCurrentSession().createQuery(query);
		return q.list();
	}

	public Long createCountQuery(final String query) {
		List<Long> r = this.createQuery(query);
		if (r != null && r.size() > 0) {
			return r.get(0);
		}
		return Long.valueOf("0");
	}

	/**
	 * Load table object.
	 * 
	 * @param table
	 * @param s
	 * @return Object
	 * @throws HibernateException
	 */
	public <E> E load(final Class table, final Serializable s) throws HibernateException {
		Object obj = getCurrentSession().load(table, s);
		// 为了立刻知道关联表记录是否存在
		obj.toString();
		return (E) obj;
	}

	/**
	 * immediately get tabel obj wihthout proxy
	 * 
	 * @param <E>
	 * @param table
	 * @param s
	 * @return
	 * @throws HibernateException
	 */
	public <E> E get(final Class table, final Serializable s) throws HibernateException {
		Object obj = getCurrentSession().get(table, s);
		// 为了立刻知道关联表记录是否存在
		obj.toString();
		return (E) obj;
	}

	/**
	 * Load table object. make the most of loadTable(Class table, Serializable
	 * s).
	 * 
	 * @param table
	 * @param s
	 * @return Object
	 * @throws HibernateException
	 */
	public <E> E load(Object table, Serializable s) throws HibernateException {
		return this.load(table.getClass(), s);
	}

	public Serializable save(Object table) throws HibernateException {
		return getCurrentSession().save(table);
	}

	public void update(Object table) throws HibernateException {
		getCurrentSession().update(table);
	}

	public void delete(Object table) throws HibernateException {
		getCurrentSession().delete(table);
	}

	public void saveOrUpdate(Object table) throws HibernateException {
		getCurrentSession().saveOrUpdate(table);
	}

	/**
	 * Append Parameter.
	 * 
	 * @param query
	 * @param parp
	 *            List or Map
	 * @return HibNateQuery
	 * @throws HibernateException
	 */
	private Query addParameter(Query query, Object parp) throws HibernateException {
		int countArrayParameter = 0;
		int countParameter = 0;
		if (parp != null) {
			// 参数以list封装.
			if (parp instanceof List) {
				List parpList = (List) parp;
				for (int i = 0; i < parpList.size(); i++) {
					Object p = parpList.get(i);
					if (p instanceof List) {
						countArrayParameter++;
						query.setParameterList(DB_PARP + countArrayParameter, (ArrayList) p);
					} else {
						query.setParameter(countParameter, p);
						countParameter++;
					}
				}
				// 参数以map封装
			} else if (parp instanceof Map) {
				String[] parps = query.getNamedParameters();
				Map parpMap = (Map) parp;
				for (int i = 0; i < parps.length; i++) {
					Object parpValue = parpMap.get(parps[i]);
					if (parpValue == null || "".equals(parpValue)) {
						throw new HibernateException("parp notfound.");
					}
					if (parpValue instanceof List) {
						query.setParameterList(parps[i], (ArrayList) parpValue);
					} else {
						query.setParameter(parps[i], parpValue);
					}

				}
			}
		}

		return query;
	}

	/**
	 * 清理session
	 */
	public void clear() throws HibernateException {
		this.getCurrentSession().clear();
	}

	/**
	 * 刷新session
	 */
	public void flush() throws HibernateException {
		this.getCurrentSession().flush();
	}

	/**
	 * Set query limit.
	 * 
	 * @param query
	 * @param offset
	 * @param maxRow
	 * @retur Query HibNateQuery
	 */
	private Query limit(Query query, int offset, int maxRow) {
		query.setFirstResult(offset);
		query.setMaxResults(maxRow);

		return query;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * 参照Query等方法，对SQLQuery进行包装
	 */

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public <E> List<E> createSQLQuery(final String query, final Map parp, final int offset, final int maxRow) {
		Query q = getCurrentSession().createSQLQuery(query);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = addParameter(q, parp);
		// q = limit(q, offset, maxRow);
		// return q.list();
		// };
		// });
	}
	
	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public <E> List<E> createSQLQuery(final String query, final Map parp, final int offset, final int maxRow, Map<String, Class> entityMap) {
		SQLQuery q = getCurrentSession().createSQLQuery(query);
		if(!entityMap.isEmpty()){
			Iterator<String> keyIte = entityMap.keySet().iterator();
			while(keyIte.hasNext()){
				String key = keyIte.next();
				q = q.addEntity(key, entityMap.get(key));
			}
		}
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = addParameter(q, parp);
		// q = limit(q, offset, maxRow);
		// return q.list();
		// };
		// });
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @return List
	 */
	public <E> List<E> createSQLQuery(final String query, final int offset, final int maxRow) {
		Query q = getCurrentSession().createSQLQuery(query);
		q = limit(q, offset, maxRow);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = limit(q, offset, maxRow);
		// return q.list();
		// };
		// });
	}
	
	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @return List
	 */
	public <E> List<E> createSQLQuery(final String query, final int offset, final int maxRow, Map<String, Class> entityMap) {
		SQLQuery q = getCurrentSession().createSQLQuery(query);
		if(!entityMap.isEmpty()){
			Iterator<String> keyIte = entityMap.keySet().iterator();
			while(keyIte.hasNext()){
				String key = keyIte.next();
				q = q.addEntity(key, entityMap.get(key));
			}
		}
		q = limit(q, offset, maxRow);
		return q.list();
		
		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = limit(q, offset, maxRow);
		// return q.list();
		// };
		// });
	}

	public <E> List<E> createSQLQuery(final String query, final Map parp) {
		Query q = getCurrentSession().createSQLQuery(query);
		q = addParameter(q, parp);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = addParameter(q, parp);
		// return q.list();
		// };
		// });
	}

	public <E> List<E> createSQLQuery(final String query, final List parp) {
		Query q = getCurrentSession().createSQLQuery(query);
		q = addParameter(q, parp);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = addParameter(q, parp);
		// return q.list();
		// };
		// });
	}

	/**
	 * 通过SQL文查询.
	 * 
	 * @param query
	 * @return List
	 */
	public <E> List<E> createSQLQuery(final String query) {//477
		Query q = getCurrentSession().createSQLQuery(query);
		return q.list();
	}

	public SQLQuery creatSQLQuery(final String query, final List parp) {
		SQLQuery q = getCurrentSession().createSQLQuery(query);
		q = addParameter(q, parp);
		return q;
	}

	public SQLQuery createSQLQuery(final String query, final List parp, final int offset, final int maxRow) {
		SQLQuery q = getCurrentSession().createSQLQuery(query);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q;
	}

	public Integer createSQLCountQuery(final String query, final List parp) {
		List list = creatSQLQuery(query, parp).addScalar("total", IntegerType.INSTANCE).list();
		if (null == list || list.isEmpty())
			return 0;
		else
			return (Integer) list.get(0);
	}

	public long createSQLCountQuery(final String query, final Map parp) {
		SQLQuery q = getCurrentSession().createSQLQuery(query);
		q = (SQLQuery) addParameter(q, parp);
		return DecimalUtil.objectTolong(q.uniqueResult());
	}

	/**
	 * 通过SQL文删除.
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public Integer delete(final String query, final Map parp) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		return q.executeUpdate();
	}

	/**
	 * 通过SQL文删除.
	 * 
	 * @param query
	 * @return List
	 */
	public Integer delete(final String query) {
		Query q = getCurrentSession().createQuery(query);
		return q.executeUpdate();
	}

	/**
	 * Set SQLQuery limit.
	 * 
	 * @param sqlQuery
	 * @param offset
	 * @param maxRow
	 * @return
	 */
	private SQLQuery limit(SQLQuery sqlQuery, int offset, int maxRow) {
		sqlQuery.setFirstResult(offset);
		sqlQuery.setMaxResults(maxRow);

		return sqlQuery;
	}

	/**
	 * 
	 * @param query
	 * @param parp
	 * @return
	 * @throws HibernateException
	 */
	private SQLQuery addParameter(SQLQuery query, Object parp) throws HibernateException {
		int countArrayParameter = 0;
		int countParameter = 0;
		if (parp != null) {
			// 参数以list封装.
			if (parp instanceof List) {
				List parpList = (List) parp;
				for (int i = 0; i < parpList.size(); i++) {
					Object p = parpList.get(i);
					if (p instanceof List) {
						countArrayParameter++;
						query.setParameterList(DB_PARP + countArrayParameter, (ArrayList) p);
					} else {
						query.setParameter(countParameter, p);
						countParameter++;
					}
				}
				// 参数以map封装
			} else if (parp instanceof Map) {
				String[] parps = query.getNamedParameters();
				Map parpMap = (Map) parp;
				for (int i = 0; i < parps.length; i++) {
					Object parpValue = parpMap.get(parps[i]);
					if (parpValue == null || "".equals(parpValue)) {
						throw new HibernateException("parp notfound.");
					}
					if (parpValue instanceof List) {
						query.setParameterList(parps[i], (ArrayList) parpValue);
					} else {
						query.setParameter(parps[i], parpValue);
					}

				}
			}
		}

		return query;
	}

	// 2015-5-7 -lifang
	public <T> void query(SearchBean searchBean, POJOPageInfo<T> pageInfo) {

		String countHql = "select count(*) ";
		StringBuffer whereHql = new StringBuffer(" from " + pageInfo.getEntityClz().getSimpleName()
				+ " where 1=1 ");
		for (Entry<String, Object> en : searchBean.getParps().entrySet()) {
			if (en.getValue() == null) {
				continue;
			}
			if (en.getValue() instanceof Object[] && ((Object[]) en.getValue()).length == 0) {
				continue;
			}
			if (en.getValue() instanceof Object[] && ((Object[]) en.getValue()).length > 0) {
				searchBean.getParps().put(en.getKey(), ((Object[]) en.getValue())[0]);
			}
			if (StringUtils.isEmpty(String.valueOf(en.getValue()))) {
				continue;
			}
			if (en.getValue() instanceof String) {
				en.setValue("%" + en.getValue() + "%");
				whereHql.append(" and " + en.getKey() + " like :" + en.getKey());
			} else {
				whereHql.append(" and " + en.getKey() + " = :" + en.getKey());
			}
		}
		if (!searchBean.getSort().isEmpty()) {
			whereHql.append(" order by ");
			int i = 0;
			for (Entry<String, Sort> en : searchBean.getSort().entrySet()) {
				whereHql.append(" " + en.getKey() + " " + en.getValue());
				i++;
				if (i != searchBean.getSort().size()) {
					whereHql.append(" , ");
				}
			}
		}
		Long count = (Long) this.createQuery(countHql + whereHql.toString(), searchBean.getParps()).get(0);
		if (pageInfo.getNowPage() <= 1) {
			pageInfo.setNowPage(1);
		}
		pageInfo.setCount(count.intValue());
		if (pageInfo.getNowPage() > pageInfo.getPages()) {
			pageInfo.setNowPage(pageInfo.getPages());
		}
		if (pageInfo.getLimit() <= 0) {
			pageInfo.setRowsPerPage(10);
		}
		List<T> list = this.createQuery(whereHql.toString(), searchBean.getParps(), pageInfo.getStart(),
				pageInfo.getLimit());
		pageInfo.setItems(list);

		for (Entry<String, Object> en : searchBean.getParps().entrySet()) {
			if (en.getValue() == null) {
				continue;
			}
			if (en.getValue() instanceof String) {
				en.setValue(((String) en.getValue()).replace("%", ""));
			}
		}
	}

	// ---end

	// 2015-5-9 李芳
	/**
	 * 执行Hql
	 * 
	 * @param hql
	 * @param values
	 */
	public void executeHql(String hql, Object[] values) {
		Query query = getCurrentSession().createQuery(hql);
		int i = 0;
		for (Object value : values) {
			query.setParameter(i++, value);
		}
		query.executeUpdate();
	}

	/**
	 * 执行Hql
	 * 
	 * @param hql
	 * @param values
	 */
	public void executeHql(String hql, List<Object> values) {
		this.executeHql(hql, values.toArray(new Object[] {}));
	}

	/**
	 * 执行Hql
	 * 
	 * @param hql
	 * @param value
	 */
	public void executeHql(String hql, Object value) {
		this.executeHql(hql, new Object[] { value });
	}

	/**
	 * 执行Hql
	 * 
	 * @param hql
	 */
	public void executeHql(String hql) {
		this.executeHql(hql, new Object[] {});
	}
	
	/**
	 * 更新或者删除操作
	 * 
	 * @param query
	 * @param parp
	 * @return List
	 */
	public Integer executeHql(final String query, final Map parp) {
		Query q = getCurrentSession().createQuery(query);
		q = addParameter(q, parp);
		return q.executeUpdate();
	}
	

	// ---end
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}



	public <E> List<E> createSQLQuery(final String query, final Map parp, ResultTransformer transformers) {
		Query q = getCurrentSession().createSQLQuery(query);
		q.setResultTransformer(transformers);
		q = addParameter(q, parp);
		return q.list();

		// return (List<E>) getHibernateTemplate().execute(new
		// HibernateCallback() {
		// public Object doInHibernate(Session session) throws
		// HibernateException,
		// java.sql.SQLException {
		// Query q = session.createSQLQuery(query);
		// q = addParameter(q, parp);
		// return q.list();
		// };
		// });
	}

	public <E> List<E> createSQLQuery(final String query, ResultTransformer transformers) {
		Query q = getCurrentSession().createSQLQuery(query);
		q.setResultTransformer(transformers);
		return q.list();
	}

	public <E> List<E> createQuery(final String query, final Map parp, ResultTransformer transformers) {
		Query q = getCurrentSession().createQuery(query);
		q.setResultTransformer(transformers);
		q = addParameter(q, parp);
		return q.list();
	}

	public <E> List<E> createQuery(final String query, ResultTransformer transformers) {
		Query q = getCurrentSession().createQuery(query);
		q.setResultTransformer(transformers);
		return q.list();
	}

	public <E> List<E> createQuery(final String query, final int offset, final int maxRow, ResultTransformer transformers) {
		Query q = getCurrentSession().createQuery(query);
		q.setResultTransformer(transformers);
		q = limit(q, offset, maxRow);
		return q.list();
	}

	public <E> List<E> createQuery(final String query, final Map parp, final int offset, final int maxRow, ResultTransformer transformers) {
		Query q = getCurrentSession().createQuery(query);
		q.setResultTransformer(transformers);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();
	}

	public <E> List<E> createSQLQuery(final String query, final Map parp,  final int offset, final int maxRow, ResultTransformer transformers) {
		Query q = getCurrentSession().createSQLQuery(query);
		q.setResultTransformer(transformers);
		q = addParameter(q, parp);
		q = limit(q, offset, maxRow);
		return q.list();
	}

}