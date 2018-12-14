package com.kingyee.golf.dao.admin;

import com.kingyee.common.db.CommonDao;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;
import com.kingyee.golf.db.MatchInfo;
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
	 * 专家课件列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo matchList(SearchBean searchBean, POJOPageInfo pageInfo){
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS t WHERE 1=1 ");
		//状态 进行中、已结束
		if(parp.get("state") != null){
			hql.append("AND c.wcState = :state ");
		}
		if(parp.get("keywords") != null){
			hql.append("AND c.wcTitle LIKE :keyword ");
		}
		if(parp.get("isOpen") != null){
			hql.append("AND c.wcIsOpen = :isOpen ");
		}
		if(parp.get("isDel") != null){
			hql.append("AND c.wcIsDel = :isDel ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("RDER BY c.wcCreateTime DESC ");
		List<MatchInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

}
