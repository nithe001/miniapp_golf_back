package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.apache.commons.lang3.StringUtils;
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
	 * 获取全部比赛列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS m WHERE 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("AND m.miTitle LIKE :keyword ");
		}

		//获取可以报名的比赛
        if(parp.get("joinEndTime") != null){
            hql.append("AND m.miMatchTime < :joinEndTime ");
        }

		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order ="ORDER BY m.miMatchTime DESC";
		List<MatchInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 获取我参加的比赛列表
	 * @return
	 */
	public POJOPageInfo getMyMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchInfo AS m, MatchJoinWatchInfo AS j WHERE 1=1 ");
        hql.append(" AND m.miId = j.mjwiMatchId and j.mjwiUserId = :userId ");
        if(parp.get("keyword") != null){
            hql.append("AND m.miTitle LIKE :keyword ");
        }
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order ="ORDER BY t.matchTime DESC,t.applyEndTime ASC";
		List<MatchInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}



    /**
     * 获取本比赛的围观用户 或者 报名用户 列表
     * 类型：0：围观 1：报名
     * @return
     */
    public List<UserInfo> getUserListByMatchId(Long matchId, Integer type) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM MatchJoinWatchInfo AS j,UserInfo AS u WHERE 1=1 ");
        hql.append(" and j.mjwiUserId = u.uiId and j.mjwiType = " +type);
        hql.append(" and j.mjwiMatchId = "+matchId);
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
        if (count == null || count.intValue() == 0) {
            return null;
        }
        String order =" ORDER BY j.mjwiCreateTime DESC ";
        return dao.createQuery("SELECT u.* "+hql.toString()+order);
    }

    /**
     * 获取本比赛的队伍
     * @return
     */
    public List<Object[]> getMatchGroupList(Long matchId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("matchId",matchId);
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchUserGroupMapping AS g,UserInfo as u WHERE 1=1 ");
        hql.append("AND g.mugmMatchId = :matchId AND g.mugmUserId = u.uiId ");
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(),parp);
        if(count == 0){
            return null;
        }
        hql.append("ORDER BY g.mugmCreateTime DESC ");
        return dao.createQuery(hql.toString(),parp);
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
        hql.append("AND m.mugmIsCaptain = 1 ");
        hql.append("AND m.mugmUserId = :scorerId ");
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
	 * 省份总评(例子)
	 * @return
	 */
	public List<Map<String, Object>> getProvinceList(SearchBean searchBean) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT cup.province as name,  ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT u.cu_id AS cu_id,h.ch_province AS province ");
		sql.append(" 	FROM cm_user AS u, wbc_user_hospital_mapping AS hm, cm_hospital AS h  ");
		sql.append(" 	WHERE u.cu_id = hm.wuhm_user_id and hm.wuhm_hospital_id = h.ch_id ");
		//地区经理id
		if(parp.get("dsmUserId") != null) {
			sql.append(" AND u.cu_dsm_user_id = :dsmUserId ");
		}else if(parp.get("regionIdList") != null){
			//大区经理、市场部、boss 所属大区id
			sql.append(" and u.cu_region_id in (:regionIdList) ");
		}
		sql.append(" GROUP BY u.cu_id, h.ch_province ");

		sql.append(" ) AS cup");
		sql.append(" LEFT JOIN wbc_month_goal_and_daily_summary AS w ");
		sql.append(" ON (cup.cu_id = w.wmg_record_belong_user_id ");
		sql.append(" and w.wmg_product_type = :productType ");
		sql.append(" and w.wmg_type = :type ");
		if((Integer.parseInt(parp.get("type").toString())) == 0){
			if(StringUtils.isNotEmpty(parp.get("month").toString())){
				sql.append(" AND w.wmg_month = :month ");
			}
		}else if((Integer.parseInt(parp.get("type").toString())) == 1){
			if(StringUtils.isNotEmpty((String)parp.get("startDate"))){
				sql.append(" AND w.wmg_date >= :startDate ");
			}
			if(StringUtils.isNotEmpty((String)parp.get("endDate"))){
				sql.append(" AND w.wmg_date <= :endDate ");
			}
		}
		sql.append(" ) ");

		sql.append(" WHERE 1=1 ");
		sql.append(" GROUP BY province ORDER BY convert(province using 'GBK') ");

		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}


    /**
     * 获取参赛球队
     * @return
     */
    public List<TeamInfo> getTeamListByIds(String miJoinTeamIds) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TeamInfo AS t WHERE 1=1 ");
        sql.append(" AND t.tiId in ( "+miJoinTeamIds +")");
        return dao.createQuery(sql.toString());
    }
}
