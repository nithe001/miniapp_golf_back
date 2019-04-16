package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 比赛活动
 * Created by nmy on 2017/7/1.
 */
@Repository
public class MatchDao extends CommonDao {

	/**
	 * 获取比赛列表 0：全部比赛  1：我参加的比赛  2：我可以报名的比赛
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append(" from ( SELECT m.mi_id AS mi_id," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_apply_end_time as mi_apply_end_time," +
				"m.mi_is_end AS mi_is_end ");
		hql.append("FROM match_info AS m ");
		hql.append("LEFT JOIN match_join_watch_info AS j ON m.mi_id = j.mjwi_match_id ");
		hql.append("WHERE 1=1 ");
		hql.append(" and j.mjwi_type = 1 ");

		if((Integer)parp.get("type") == 1){
			//我参加的比赛
			hql.append("AND m.mi_id IN (SELECT j1.mjwi_match_id FROM match_join_watch_info AS j1 WHERE j1.mjwi_user_id = :userId) ");
		}else if((Integer)parp.get("type") == 2){
			//我可以报名的比赛
			hql.append("AND m.mi_id NOT IN (SELECT j1.mjwi_match_id FROM match_join_watch_info AS j1 WHERE j1.mjwi_user_id = :userId) ");
		}else if((Integer)parp.get("type") == 3){
			//我创建的比赛
			hql.append("AND m.mi_create_user_id = :userId ");
		}
		hql.append("GROUP BY m.mi_id ");
		hql.append(") as ma LEFT JOIN match_join_watch_info AS mj on ma.mi_id = mj.mjwi_match_id ");

		if(parp.get("keyword") != null){
			hql.append("WHERE 1=1 AND ma.mi_title LIKE :keyword ");
		}
		hql.append("GROUP BY ma.mi_id ");

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from (select * "+hql.toString()+" ) as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY ma.mi_apply_end_time");

		String select = "select ma.*,count(mj.mjwi_user_id) as userCount ";
		List<Map<String,Object>> list = dao.createSQLQuery(select+hql.toString(), parp,
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

    /**
     * 获取本比赛的围观用户 或者 报名用户 列表
     * @param type 类型：0：围观 1：报名
     * @return
     */
    public List<Map<String, Object>> getUserListByMatchId(Long matchId, Integer type, POJOPageInfo pageInfo) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM MatchJoinWatchInfo AS j,UserInfo AS u WHERE 1=1 ");
        hql.append(" and j.mjwiUserId = u.uiId and j.mjwiType = " +type);
        hql.append(" and j.mjwiMatchId = "+matchId);
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
        if (count == null || count.intValue() == 0) {
            return null;
        }
		hql.append(" ORDER BY j.mjwiCreateTime DESC");
		List<Map<String, Object>> list = dao.createQuery("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName " + hql.toString(),
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
        return list;
    }

	/**
	 * 获取比赛赛长
	 * @return
	 */
	public List<Map<String, Object>> getCaptainListByMatchId(Long matchId, POJOPageInfo pageInfo) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchUserGroupMapping AS m,UserInfo AS u WHERE 1=1 ");
		hql.append(" and m.mugmUserId = u.uiId " );
		hql.append(" and m.mugmUserType = 1 ");
		hql.append(" and m.mugmMatchId = "+matchId);
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
		if (count == null || count.intValue() == 0) {
			return null;
		}
		hql.append(" ORDER BY m.mugmCreateTime DESC");
		List<Map<String, Object>> list = dao.createQuery("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName " + hql.toString(),
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}



    /**
     * 获取本组的用户
     * @return
     */
    public List<Map<String, Object>> getMatchGroupListByGroupId(Long groupId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("groupId",groupId);
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName ");
        hql.append("FROM MatchUserGroupMapping AS g,UserInfo as u WHERE 1=1 ");
        hql.append("AND g.mugmGroupId = :groupId AND g.mugmUserId = u.uiId and g.mugmUserType = 0 ");
        hql.append("ORDER BY g.mugmGroupId ASC,g.mugmCreateTime DESC ");

		List<Map<String, Object>> list = dao.createQuery(hql.toString(), parp, 0, 4, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
    }

    /**
     * 判断是否赛长
     * @return
     */
    public Long getIsCaptain(SearchBean searchBean) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchUserGroupMapping AS m WHERE 1=1 ");
        hql.append("AND m.mugmMatchId = :matchId ");
        hql.append("AND m.mugmUserType = 1 ");
        hql.append("AND m.mugmUserId = :userId ");
        return dao.createCountQuery("SELECT COUNT(*) "+hql.toString(),parp);
    }

    /**
     * 判断是否可被记分
     * @return
     */
    public Long getScoreTypeCount(SearchBean searchBean) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchScoreUserMapping AS m WHERE 1=1 ");
        hql.append("AND m.msumMatchId = :matchId ");
        hql.append("AND m.msumGroupId = :groupId ");
        hql.append("AND m.msumCreateUserId = :matchUserId ");
        hql.append("AND m.msumScorerId = :scorerId ");
        return dao.createCountQuery("SELECT COUNT(*) "+hql.toString(),parp);
    }

    /**
     * 报名——获取比赛赛长和分组
     * @return
     */
    public List<MatchUserGroupMapping> getMatchGroupMappingList(Long matchId) {
        /*SELECT
        m.mugm_id,
                m.mugm_group_id,
                m.mugm_group_name,
                m.mugm_is_captain,
                m.mugm_user_id,
                m.mugm_user_name from match_user_group_mapping AS m
	, match_group AS g
        where m.mugm_group_id = g.mg_id
        and m.mugm_match_id = 1
        GROUP BY
        m.mugm_is_captain,m.mugm_group_name,m.mugm_user_id
        ORDER BY m.mugm_is_captain desc*/
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT m.mugmId,m.mugmGroupId,m.mugmGroupName,m.mugmUserType,m.mugmUserId,m.mugmUserName  ");
        hql.append("FROM MatchScoreUserMapping AS m , MatchGroup AS g WHERE 1=1 ");
        hql.append("AND m.mugmGroupId = g.mgId ");
        hql.append("AND m.mugmMatchId = " +matchId);
        hql.append(" GROUP BY ");
        hql.append("m.mugmUserType,m.mugmGroupName,m.mugmUserId ");
        hql.append("ORDER BY m.mugmUserType desc ");
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
        if(count == 0){
            return null;
        }
        return dao.createQuery(hql.toString());
    }

	/**
	 * 取消报名，退出分组 到临时分组     赛长将多个球友退出分组
	 * @return
	 */
	public void updateMyMatchGroupMapping(Map<String, Object> parp) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchUserGroupMapping AS m SET m.mugmUserType = 0 ");
		sql.append(" WHERE m.mugmMatchId = :matchId ");
		sql.append(" AND m.mugmGroupId = :groupId ");
		if(parp.get("userIdList") != null){
			sql.append(" AND m.mugmUserId in (:userIdList) ");
		}else if(parp.get("userId") != null){
			sql.append(" AND m.mugmUserId = :userId ");
		}
		dao.executeHql(sql.toString(), parp);
	}

	/**
	 * 获取临时分组中的球友
	 * @param matchId 比赛id
	 * @param groupId 比赛分组id
	 * @return
	 */
	public List<MatchUserGroupMapping> getUserByTemporary(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM MatchUserGroupMapping AS m WHERE 1=1 ");
		sql.append(" AND m.mugmMatchId = "+matchId);
		sql.append(" AND m.mugmGroupId = "+groupId);
		sql.append(" AND m.mugmUserType = 0");
		return dao.createQuery(sql.toString());
	}


    /**
     * 获取参赛球队
     * @return
     */
    public List<TeamInfo> getTeamListByIds(List<Long> teamIdList) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TeamInfo AS t WHERE 1=1 ");
        sql.append(" AND t.tiId in ( "+teamIdList +")");
        return dao.createQuery(sql.toString());
    }

	/**
	 * 单练——选择器——获取球场 城市
	 * @return
	 */
	public List<String> getParkInfoCityList() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.piCity FROM ParkInfo AS p WHERE 1=1 ");
		sql.append("GROUP BY p.piCity ");
		sql.append("ORDER BY p.piId");
		return dao.createQuery(sql.toString());
	}

	/**
	 * 单练——选择器——获取球场 场地
	 * @return
	 */
	public List<String> getParkInfoList(String city) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.piName FROM ParkInfo AS p WHERE 1=1 ");
		sql.append("AND p.piCity = '"+city+"'");
		return dao.createQuery(sql.toString());
	}

	/**
	 * 根据球场名称获取球场信息
	 * @return
	 */
	public ParkInfo getParkIdByName(String parkName) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM ParkInfo AS p WHERE 1=1 ");
		sql.append("AND p.piName = '"+parkName+"'");
		List<ParkInfo> parkInfo = dao.createQuery(sql.toString());
		if(parkInfo != null && parkInfo.size()>0){
			return parkInfo.get(0);
		}
		return null;
	}

	/**
	 * 单练——查询是否有我正在进行的单练
	 * @return
	 */
	public MatchInfo getMySinglePlay(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchInfo AS t WHERE 1=1 ");
		sql.append("AND t.miCreateUserId = "+userId);
		sql.append("AND t.miIsEnd = 1");
		List<MatchInfo> matchInfo = dao.createQuery(sql.toString());
		if(matchInfo != null && matchInfo.size()>0){
			return matchInfo.get(0);
		}
		return null;
	}

	/**
	 * 查询比赛分组
	 * @return
	 */
	public List<MatchGroup> getMatchGroupList_(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchGroup AS t WHERE 1=1 ");
		sql.append("AND t.mgMatchId = "+matchId);
		return dao.createQuery(sql.toString());
	}

	/**
	 * 获取比赛最大组
	 * @return
	 */
	public MatchGroup getMaxGroupByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchGroup AS t WHERE t.mgId = (SELECT MAX(g.mgId) FROM MatchGroup as g where g.mgMatchId = "+matchId+") ");
		List<MatchGroup> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 比赛详情——赛长获取已经报名的用户
	 * @return
	 */
	public List<Map<String, Object>> getApplyUserByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.ui_headimg as uiHeadimg ");
		sql.append("FROM MatchJoinWatchInfo as j,UserInfo as u where " +
				"j.mjwiUserId = u.uiId and j.mjwiMatchId = "+matchId+" and j.mjwiType = 1 ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 比赛详情——比赛选了球队，从球队中选 去除已经参赛的用户
	 * @return
	 */
	public List<Map<String, Object>> getApplyUserListByMatchId(Long matchId, List<Long> teamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.ti_id as tiId,t.ti_name as tiName,u.ui_id as uiId,u.ui_real_name as uiRealName,u.ui_headimg as uiHeadimg ");
		sql.append("FROM team_user_mapping AS tum,user_info as u,team_info as t ");
		sql.append("WHERE tum.tum_user_id = u.ui_id ");
		sql.append("and tum.tum_team_id = t.ti_id ");
		sql.append("and tum.tum_team_id IN ( SELECT t.ti_id FROM team_info AS t WHERE t.ti_id IN (:teamIdList))");
		sql.append("and u.ui_id not in (select gm.mugm_user_id from match_user_group_mapping as gm where gm.mugm_match_id = :matchId) ");
		sql.append("GROUP BY tum.tum_user_id ");
		sql.append("ORDER BY tum.tum_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 赛长——本组用户列表
	 * @return
	 */
	public List<Map<String, Object>> getUserListByMatchIdGroupId(Long matchId, Long groupId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("groupId", groupId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg ");
		sql.append("FROM MatchUserGroupMapping AS m,UserInfo as u ");
		sql.append("WHERE m.mugmUserId = u.uiId ");
		sql.append("and m.mugmMatchId = :matchId ");
		sql.append("and m.mugmGroupId = :groupId ");
		sql.append("ORDER BY m.mugmCreateTime DESC");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 比赛详情——保存——将用户从该分组删除
	 * @return
	 */
	public void delUserByMatchIdGroupId(Long matchId, Long groupId, Long userId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("groupId", groupId);
		parp.put("userId", userId);
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MatchUserGroupMapping ");
		sql.append("WHERE mugmUserId = :userId ");
		sql.append("and mugmMatchId = :matchId ");
		sql.append("and mugmGroupId = :groupId ");
		dao.executeHql(sql.toString(),parp);
	}
}
