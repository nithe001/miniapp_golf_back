package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 球队管理
 * @author nmy
 * 2017年05月08日
 */
@Repository
public class AdminTeamDao extends CommonDao {

	/**
	 * 球队列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo teamList(SearchBean searchBean, POJOPageInfo pageInfo){
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where 1=1 GROUP BY tum_team_id ");
		hql.append(")as tum on ");
		if(parp.get("state") != null){
			hql.append(" (t.ti_id = tum.tum_team_id and t.ti_is_valid = :state) ");
		}else{
			hql.append(" t.ti_id = tum.tum_team_id ");
		}
		hql.append("where 1=1  ");
		if(parp.get("keyword") != null){
			hql.append("AND t.ti_name LIKE :keyword ");
		}
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(null);
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY t.ti_create_time desc ");
		String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo,t.ti_create_time as createTime," +
				"t.ti_create_user_name as createUser,ti_update_time as updateTime,ti_update_user_name as updateUser,ti_is_valid as valid ";
		List<Map<String, Object>> list = dao.createSQLQuery( select + hql.toString(),
				parp, pageInfo.getStart(), pageInfo.getRowsPerPage(), Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

}
