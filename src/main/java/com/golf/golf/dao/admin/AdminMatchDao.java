package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchRule;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 赛事活动
 * @author nmy
 * 2017年05月08日
 */
@Repository
public class AdminMatchDao extends CommonDao {

	/**
	 * 赛事活动列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo matchList(SearchBean searchBean, POJOPageInfo pageInfo){
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS m WHERE 1=1 ");
		//状态 进行中、已结束
		if(parp.get("state") != null){
			hql.append("AND m.miIsEnd = :state ");
		}
		if(parp.get("keyword") != null){
			hql.append("AND m.miTitle LIKE :keyword ");
		}
		if(parp.get("type") != null){
			hql.append("AND m.miType = :type ");
		}
		if(parp.get("state") != null){
			hql.append("AND m.miIsEnd = :state ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY m.miCreateTime DESC ");
		List<MatchInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}



	/**
	 * 高球规则列表
	 * @return
	 */
	public POJOPageInfo matchRuleList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchRule AS r WHERE 1=1 ");
		if(parp.get("keywords") != null){
			hql.append("AND r.mrTitle LIKE :keyword ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchRule>());
			pageInfo.setCount(0);
			return pageInfo;
		}
//		hql.append("ORDER BY r.mrCreateTime DESC ");
		List<MatchRule> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}
}
