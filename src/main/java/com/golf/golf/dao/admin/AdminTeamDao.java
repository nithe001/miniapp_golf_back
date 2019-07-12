package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.IntegralConfig;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchUserGroupMapping;
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
		if(parp.get("isValid") != null){
			hql.append(" (t.ti_id = tum.tum_team_id and t.ti_is_valid = :isValid) ");
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

	/**
	 * 获取本球队的所有队员 包括申请入队的
	 * @return
	 */
	public List<Map<String, Object>> getTeamUserListByTeamId(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiNickName as uiNickName,u.uiHeadimg as uiHeadimg,m.tumUserType as tumUserType  ");
		hql.append("FROM TeamUserMapping AS m,UserInfo AS u WHERE 1=1 ");
		hql.append("AND m.tumTeamId = "+teamId);
		hql.append(" and m.tumUserId = u.uiId ");
		hql.append("ORDER BY m.tumUserType ");
		List<Map<String, Object>> list = dao.createQuery(hql.toString(),0, 12,Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

    /**
     * 删除球队用户配置
     * @param teamId 球队id
     * @return
     */
    public void delTeamUserMapping(Long teamId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM TeamUserMapping AS m WHERE m.tumTeamId= "+teamId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除比赛用户配置中对应球队信息
     * @param teamId 球队id
     * @return
     */
    public void delMatchTeamUserMapping(Long teamId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM MatchUserGroupMapping AS t WHERE t.mugmTeamId= "+teamId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除比赛成绩表中对应球队的比分
     * @param teamId 球队id
     * @return
     */
    public void delMatchScoreByTeamId(Long teamId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM MatchScore AS t WHERE t.msTeamId= "+teamId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除成绩确认配置
     * @param teamId 球队id
     * @return
     */
    public void delMatchScoreSubmitConfigByTeamId(Long teamId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM IntegralConfig AS t WHERE t.icTeamId= "+teamId);
        dao.executeHql(hql.toString());
    }
}
