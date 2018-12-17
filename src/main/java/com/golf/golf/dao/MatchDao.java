package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 比赛活动
 * Created by nmy on 2017/7/1.
 */
@Repository
public class MatchDao extends CommonDao {
	/**
	 * 获取赛事列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS t WHERE 1=1 ");

		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order ="ORDER BY t.createTime DESC,t.state DESC";
		List<MatchInfo> list = dao.createQuery(hql.toString()+order,  pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}
}
