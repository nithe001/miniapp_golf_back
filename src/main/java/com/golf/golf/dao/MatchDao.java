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
	 * 获取比赛列表 0：全部比赛 1：我参加的比赛  2：可报名的比赛 3:已报名的比赛  4：我创建的比赛
	 * 比赛按时间最近优先和位置最近优先排序；选“我的比赛”只列我参加的比赛，其他一样
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m.mi_type as mi_type,m.mi_id AS mi_id," +
				"m.mi_logo as mi_logo," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_apply_end_time AS mi_apply_end_time," +
				"m.mi_is_end AS mi_is_end," +
				"j.mjwi_type AS type," +
				"count(j.mjwi_user_id) AS userWatchCount," +
				"count(mugm.mugm_user_id) AS userCount,"+
				"m.mi_match_format_1 as mi_match_format_1," +
				"m.mi_match_format_2 as mi_match_format_2 ");
		if(parp.get("myLat") != null && parp.get("myLng") != null ){

			hql.append(",( 6371 * acos (" +
					"      cos ( radians(:myLat) ) " +
					"      * cos( radians( p.pi_lat ) ) " +
					"      * cos( radians( p.pi_lng ) - radians(:myLng) ) " +
					"      + sin ( radians(:myLat) ) " +
					"      * sin( radians( p.pi_lat ) ) " +
					"  ) " +
					"  ) AS distance ");
		}
		hql.append(" FROM match_info AS m ");
		hql.append(" LEFT JOIN match_join_watch_info AS j ON (m.mi_id = j.mjwi_match_id and m.mi_is_valid = 1 AND j.mjwi_type = 0) ");
		hql.append(" LEFT JOIN park_info as p on m.mi_park_id = p.pi_id ");
		hql.append(" LEFT JOIN match_user_group_mapping AS mugm ON mugm.mugm_match_id = m.mi_id ");
		hql.append(" WHERE m.mi_is_valid = 1 ");

		//0：全部比赛 1：我参加的比赛
		if((Integer)parp.get("type") == 1){
			//比分——我的比赛 包括我的单练
			hql.append(" AND m.mi_id IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) " +
					"AND m.mi_is_end != 0 ");
		}else{
			//比分——全部比赛（除了报名中的）
			hql.append(" AND m.mi_is_end != 0 AND m.mi_type = 1 ");
		}
		if(parp.get("keyword") != null){
			hql.append(" AND m.mi_title LIKE :keyword ");
		}
		hql.append(" GROUP BY m.mi_id ");
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append(" ORDER BY IF(ISNULL(distance),1,0),distance,m.mi_is_end, m.mi_create_time desc  ");
		List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 获取比赛列表 3:已报名的比赛 包括我创建的正在报名中的比赛
	 * 比赛按时间最近优先和位置最近优先排序；选“我的比赛”只列我参加的比赛，其他一样
	 * @return
	 */
	public POJOPageInfo getMyJoinMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m.mi_type as mi_type,m.mi_id AS mi_id," +
				"m.mi_logo as mi_logo," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_apply_end_time AS mi_apply_end_time," +
				"m.mi_is_end AS mi_is_end," +
				"count(mugm.mugm_user_id) AS userCount,"+
				"m.mi_match_format_1 as mi_match_format_1," +
				"m.mi_match_format_2 as mi_match_format_2 ");
		hql.append(" FROM match_info AS m ");
		hql.append(" LEFT JOIN match_user_group_mapping AS mugm ON mugm.mugm_match_id = m.mi_id ");
		hql.append(" WHERE m.mi_is_valid = 1 AND m.mi_type = 1 ");
		hql.append(" AND ((m.mi_create_user_id = 1 AND m.mi_is_end = 0) " +
							"or mugm.mugm_user_id = 1) ");
		if(parp.get("keyword") != null){
			hql.append(" AND m.mi_title LIKE :keyword ");
		}
		hql.append(" GROUP BY m.mi_id ");
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append(" ORDER BY mugm.mugm_create_time desc ");
		List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 获取比赛列表 :2：可报名的比赛
	 * 比赛按时间最近优先和位置最近优先排序
	 * @return
	 */
	public POJOPageInfo getCanJoinMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT  t.*, ( " +
				"6371 * acos( " +
				"cos(radians(39.9219)) * cos(radians(p.pi_lat)) * cos( " +
				"radians(p.pi_lng) - radians(116.44355) " +
				") + sin(radians(39.9219)) * sin(radians(p.pi_lat)) " +
				") " +
				") AS distance  FROM (");
		hql.append("SELECT m.mi_type as mi_type,m.mi_id AS mi_id," +
				"m.mi_logo as mi_logo," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_park_id AS mi_park_id," +
				"m.mi_create_time AS mi_create_time," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_is_end AS mi_is_end," +
				"m.mi_join_team_ids AS mi_join_team_ids," +
				"count(mg.mugm_user_id) AS userCount,"+
				"m.mi_match_format_1 as mi_match_format_1," +
				"m.mi_match_format_2 as mi_match_format_2 ");
		hql.append(" FROM match_info AS m, match_user_group_mapping AS mg ");
		hql.append(" WHERE m.mi_id = mg.mugm_match_id and m.mi_type = 1 and m.mi_is_valid = 1 and m.mi_is_end = 0 ");
//		hql.append("and m.mi_id not in(  ");
//		hql.append(" select mugm.mugm_match_id from match_user_group_mapping as mugm where mugm.mugm_user_id = :userId ");
//		hql.append(")  and m.mi_create_user_id !=:userId");

		if(parp.get("keyword") != null){
			hql.append(" AND m.mi_title LIKE :keyword ");
		}
		hql.append(" GROUP BY m.mi_id ");

		hql.append(") AS t LEFT JOIN park_info AS p ON t.mi_park_id = p.pi_id ");

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append(" ORDER BY IF (ISNULL(distance), 1, 0),distance,t.mi_create_time DESC ");
		List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 获取比赛列表 我创建的比赛
	 * @return
	 */
	public POJOPageInfo getMyMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m.mi_type as mi_type,m.mi_id AS mi_id," +
				"m.mi_logo as mi_logo," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_apply_end_time AS mi_apply_end_time," +
				"m.mi_is_end AS mi_is_end," +
				"j.mjwi_type AS type," +
				"count(j.mjwi_user_id) AS userWatchCount," +
				"count(mugm.mugm_user_id) AS userCount,"+
				"m.mi_match_format_1 as mi_match_format_1," +
				"m.mi_match_format_2 as mi_match_format_2 ");
		hql.append(" FROM match_info AS m ");
		hql.append(" LEFT JOIN match_join_watch_info AS j ON (m.mi_id = j.mjwi_match_id and m.mi_is_valid = 1 AND j.mjwi_type = 0) ");
		hql.append(" LEFT JOIN match_user_group_mapping AS mugm ON mugm.mugm_match_id = m.mi_id ");
		hql.append(" WHERE m.mi_is_valid = 1 ");
		hql.append(" AND m.mi_create_user_id = :userId ");
		if(parp.get("keyword") != null){
			hql.append(" AND m.mi_title LIKE :keyword ");
		}
		hql.append(" GROUP BY m.mi_id ");
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append(" ORDER BY m.mi_is_end, m.mi_create_time ");
		List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
				pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

    /**
     * 获取本比赛的围观用户列表
     * @return
     */
    public List<Map<String, Object>> getWatchUserListByMatchId(Long matchId, POJOPageInfo pageInfo) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM MatchJoinWatchInfo AS j,UserInfo AS u WHERE 1=1 ");
        hql.append(" and j.mjwiUserId = u.uiId and j.mjwiType = 0 ");
        hql.append(" and j.mjwiMatchId = "+matchId);
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
        if (count == null || count.intValue() == 0) {
			return new ArrayList<>();
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
		hql.append("select u.ui_id as uiId,u.ui_headimg as uiHeadimg,u.ui_real_name as uiRealName,t.ti_abbrev as tiAbbrev ");
		hql.append(" from match_user_group_mapping as m LEFT JOIN user_info as u on m.mugm_user_id = u.ui_id ");
		hql.append(" LEFT JOIN team_info as t on m.mugm_team_id = t.ti_id ");
		hql.append(" where m.mugm_user_type = 0 ");
		hql.append(" and m.mugm_match_id = "+matchId);
		hql.append(" ORDER BY m.mugm_team_id,m.mugm_create_time DESC");
		return dao.createSQLQuery(hql.toString(),
				null,pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
	}


    /**
     * 获取本组的用户(包括赛长)
     * @return
     */
    public List<Map<String, Object>> getMatchGroupListByGroupId(Long matchId, Long groupId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("groupId",groupId);
		parp.put("matchId",matchId);
        StringBuilder hql = new StringBuilder();
		hql.append("select u.ui_id as uiId,u.ui_headimg as uiHeadimg,u.ui_real_name as uiRealName,t.ti_abbrev as tiAbbrev ");
		hql.append(" from match_user_group_mapping as m LEFT JOIN user_info as u on m.mugm_user_id = u.ui_id ");
		hql.append(" LEFT JOIN team_info as t on m.mugm_team_id = t.ti_id ");
		hql.append(" where m.mugm_group_id = :groupId ");
		hql.append(" and m.mugm_match_id = :matchId ");
		hql.append(" and m.mugm_user_type != 2 ");
		hql.append(" ORDER BY m.mugm_team_id,m.mugm_create_time");
		return dao.createSQLQuery(hql.toString(), parp,0,4,Transformers.ALIAS_TO_ENTITY_MAP);
    }

    /**
     * 判断是否赛长
     * @return
     */
    public Long getIsCaptain(Long matchId, Long userId) {
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchUserGroupMapping AS m WHERE 1=1 ");
        hql.append("AND m.mugmMatchId =  "+matchId);
        hql.append(" AND m.mugmUserType = 0 ");
        hql.append("AND m.mugmUserId =  "+userId);
        return dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
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
     * 获取参赛球队
     * @return
     */
    public List<TeamInfo> getTeamListByIds(List<Long> teamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("teamIdList",teamIdList);
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TeamInfo AS t WHERE 1=1 ");
        sql.append(" AND t.tiId in ( :teamIdList )");
        return dao.createQuery(sql.toString(),parp);
    }

	/**
	 * 创建比赛—点击球场-获取分区和洞
	 * @return
	 */
	public List<Object[]> getParkZoneAndHole(Long parkId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT DISTINCT p.ppName,count(p.ppName) FROM ParkPartition AS p WHERE 1=1 ");
		hql.append("AND p.ppPId = " +parkId);
		hql.append("GROUP BY p.ppName");
		return dao.createQuery(hql.toString());
	}

	/**
	 * 查询球场区域
	 * @return
	 */
	public POJOPageInfo getParkListByRegion(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("select DISTINCT p.piCity from ParkInfo as p where p.piIsValid = 1");
		List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setItems(list);
		return pageInfo;
	}


	/**
	 * 查询该区域下的球场
	 * @return
	 */
	public POJOPageInfo getParkListByRegionName(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT p.pi_id as pi_id,p.pi_name as pi_name,p.pi_address as pi_address,p.pi_lat as pi_lat,p.pi_lng as pi_lng ");
		hql.append(" from park_info as p where p.pi_is_valid = 1 ");
		if(parp.get("keyword") != null){
			hql.append("AND p.pi_name LIKE :keyword  ");
		}
		hql.append("AND p.pi_city = :city  ");

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString() +") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<Map<String, Object>>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("GROUP BY p.pi_name ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 查询球场列表-附近球场
	 * @return
	 */
	public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT p.pi_id as pi_id,p.pi_name as pi_name,p.pi_address as pi_address,p.pi_lat as pi_lat,p.pi_lng as pi_lng ");
		if(parp.get("myLat") != null && parp.get("myLng") != null ){
			hql.append(",( 6371 * acos (" +
					"      cos ( radians(:myLat) ) " +
					"      * cos( radians( p.pi_lat ) ) " +
					"      * cos( radians( p.pi_lng ) - radians(:myLng) ) " +
					"      + sin ( radians(:myLat) ) " +
					"      * sin( radians( p.pi_lat ) ) " +
					"  ) " +
					"  ) AS distance ");
		}
		hql.append(" FROM park_info AS p WHERE p.pi_is_valid = 1 ");
		if(parp.get("keyword") != null){
			hql.append("AND p.pi_name LIKE :keyword  ");
		}

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<Map<String, Object>>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append(" ORDER BY IF(ISNULL(distance),1,0),distance");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage(), Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
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
	public MatchInfo getMySinglePlay(Map<String,Object> parp) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM MatchInfo AS t WHERE 1=1 ");
		sql.append(" AND t.miCreateUserId = :userId");
		sql.append(" AND t.miType = 0 ");
		sql.append(" AND t.miIsEnd = 1 ");
		sql.append(" AND t.miCreateTime >= :startTime ");
		sql.append(" AND t.miCreateTime <= :endTime ");
		List<MatchInfo> matchInfo = dao.createQuery(sql.toString(), parp);
		if(matchInfo != null && matchInfo.size()>0){
			return matchInfo.get(0);
		}
		return null;
	}

	/**
	 * 查询比赛分组
	 * @return
	 */
	public List<Map<String,Object>> getMatchGroupList_(Long matchId,Integer matchState) {
		StringBuilder sql = new StringBuilder();
		if(matchState == 0){
			//报名中，显示所有的分组
			sql.append("select t.mgMatchId as matchId,t.mgId as groupId,t.mgGroupName as groupName FROM MatchGroup AS t WHERE t.mgMatchId = "+matchId);
		}else{
			//比赛中、结束 只显示有人的分组
			sql.append("select t.mgMatchId as matchId,t.mgId as groupId,t.mgGroupName as groupName FROM MatchGroup AS t,MatchUserGroupMapping as m WHERE t.mgMatchId = m.mugmMatchId and t.mgId = m.mugmGroupId ");
			sql.append("AND t.mgMatchId = "+matchId);
			sql.append("  GROUP BY t.mgId ");
		}
		return dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
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
	 * 比赛详情——赛长删除本组用户——获取本组用户 不包括已报名的
	 * @return
	 */
	public List<Map<String, Object>> getUserListByGroupId(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg,t.tiAbbrev as tiAbbrev ");
		sql.append("FROM MatchUserGroupMapping as g,UserInfo as u,TeamInfo as t where " +
				"g.mugmUserId = u.uiId and g.mugmMatchId = "+matchId+" and g.mugmGroupId ="+groupId+
				" and g.mugmTeamId = t.tiId and g.mugmUserId != "+userId);
		sql.append(" and g.mugmUserType != 2 ");
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
	 * 赛长——删除用户——本组用户列表
	 * 不包括自己
	 * @return
	 */
	public List<Map<String, Object>> getUserListByMatchIdGroupId(Long matchId, Long groupId, Long myUserId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("groupId", groupId);
		parp.put("myUserId", myUserId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg ");
		sql.append("FROM MatchUserGroupMapping AS m,UserInfo as u ");
		sql.append("WHERE m.mugmUserId = u.uiId ");
		sql.append("and m.mugmUserId != :myUserId ");
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

	/**
	 * 分组记分卡——获取半场球洞
	 * @return
	 */
	public List<Map<String, Object>> getParkPartitionList(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " +
				"pp.pp_id AS holeId, " +
				"pp.pp_p_id AS parkId, " +
				"pp.pp_name AS ppName, " +
				"pp.pp_hole_num AS holeNum, " +
				"pp.pp_hole_standard_rod AS holeStandardRod " +
				"FROM " +
				"match_info AS m, " +
				"park_info AS p, " +
				"park_partition AS pp " +
				"WHERE m.mi_id = " +matchId+" AND p.pi_id = m.mi_park_id " +
				"AND m.mi_zone_before_nine = pp.pp_name AND pp.pp_p_id = p.pi_id ");
		sql.append("union all ");
		sql.append("SELECT " +
				"pp.pp_id AS holeId, " +
				"pp.pp_p_id AS parkId, " +
				"pp.pp_name AS ppName, " +
				"pp.pp_hole_num AS holeNum, " +
				"pp.pp_hole_standard_rod AS holeStandardRod " +
				"FROM " +
				"match_info AS m, " +
				"park_info AS p, " +
				"park_partition AS pp " +
				"WHERE m.mi_id = " +matchId+" AND p.pi_id = m.mi_park_id " +
				"AND m.mi_zone_after_nine = pp.pp_name AND pp.pp_p_id = p.pi_id ");

		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 总比分——获取前后半场球洞
	 * @return
	 */
	public List<Map<String, Object>> getBeforeAfterParkPartitionList(Long matchId,Integer type) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " +
				"pp.pp_id AS holeId, " +
				"pp.pp_p_id AS parkId, " +
				"pp.pp_name AS ppName, " +
				"pp.pp_hole_num AS holeNum, " +
				"pp.pp_hole_standard_rod AS holeStandardRod " +
				"FROM " +
				"match_info AS m, " +
				"park_info AS p, " +
				"park_partition AS pp " +
				"WHERE m.mi_id = " +matchId+" AND p.pi_id = m.mi_park_id ");
		if(type == 0){
			sql.append("AND m.mi_zone_before_nine = pp.pp_name ");
		}else{
			sql.append("AND m.mi_zone_after_nine = pp.pp_name ");
		}
		sql.append(" AND pp.pp_p_id = p.pi_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}
	/**
	 * 记分卡——获取本组用户
	 * 同一球队的分在一行
	 */
	public List<Map<String, Object>> getUserListByScoreCard(Long matchId, Long groupId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select mugm.mugm_user_id AS uiId, " +
				"u.ui_real_name AS uiRealName, " +
				"u.ui_headimg AS uiHeadimg, " +
				"mugm.mugm_team_id AS team_id, " +
				"mugm.mugm_group_id AS group_id, " +
				"sum(s.ms_rod_num) AS sumRodNum, " +
				"sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("from match_user_group_mapping as mugm LEFT JOIN match_score AS s on ");
		sql.append("(mugm.mugm_match_id = s.ms_match_id and mugm.mugm_group_id = s.ms_group_id) ");
		sql.append("LEFT JOIN user_info AS u ON mugm.mugm_user_id = u.ui_id ");
		sql.append("where mugm.mugm_match_id = "+matchId);
		if(groupId != null){
			sql.append(" and mugm.mugm_group_id = "+groupId);
		}
		if(teamId != null){
			sql.append(" and mugm.mugm_team_id = "+teamId);
		}
		sql.append(" GROUP BY mugm.mugm_team_id,mugm.mugm_user_id ORDER BY mugm.mugm_user_id,mugm.mugm_team_id");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本组用户 和 用户的总杆差
	 * 按照球队分组并按照参赛范围的球队顺序来排序
	 * scoreType:0:上报球队记分 1：比赛球队记分
	 */
	public List<Map<String, Object>> getUserListById(Long matchId, Long groupId, Long teamId, Integer scoreType) {
		StringBuilder sql = new StringBuilder();
		sql.append("select m.mugm_user_id AS uiId,u.ui_nick_name AS uiNickName,u.ui_real_name AS uiRealName,u.ui_headimg AS uiHeadimg," +
				"m.mugm_team_id AS team_id,m.mugm_group_id AS group_id,sum(s.ms_rod_num) AS sumRodNum,sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("FROM match_user_group_mapping as m LEFT JOIN match_score AS s on (m.mugm_match_id = s.ms_match_id AND s.ms_type = "+scoreType+") ");
		sql.append("LEFT JOIN user_info AS u ON m.mugm_user_id = u.ui_id ");
		sql.append("where m.mugm_user_id = s.ms_user_id ");
		sql.append(" and m.mugm_match_id = "+matchId);
		if(groupId != null){
			sql.append(" and m.mugm_group_id = "+groupId);
		}
		if(teamId != null){
			sql.append(" and m.mugm_team_id = "+teamId);
		}
		sql.append(" GROUP BY m.mugm_user_id ORDER BY sumRodNum ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取单练的本组用户
	 */
	public List<Map<String, Object>> getSingleUserListById(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.mugm_user_id as uiId, m.mugm_user_name as uiRealName,u.ui_headimg AS uiHeadimg,m.mugm_team_id as team_id ");
		sql.append("FROM match_user_group_mapping as m left join user_info as u ");
		sql.append("on m.mugm_user_id = u.ui_id ");
		sql.append("where m.mugm_match_id = "+matchId);
		sql.append(" and m.mugm_group_id = "+groupId);
		sql.append(" order by m.mugm_user_id ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}


	/**
	 * 本用户得分情况
	 */
	public List<Map<String, Object>> getScoreByUserId(Long groupId, Long uiId, MatchInfo matchInfo, Long teamId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchInfo.getMiId());
		parp.put("teamId", teamId);
		if(groupId != null){
			parp.put("groupId", groupId);
		}
		parp.put("beforeHole", matchInfo.getMiZoneBeforeNine());
		parp.put("afterHole", matchInfo.getMiZoneAfterNine());
		parp.put("userId", uiId);
		parp.put("parkId", matchInfo.getMiParkId());
		StringBuilder sql = new StringBuilder();


		sql.append("select * from (select p.*,s.ms_id as ms_id,s.ms_user_id as ms_user_id,s.ms_user_name as ms_user_name," +
				"s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_is_up AS is_up," +
				"s.ms_rod_num AS rod_num," +
				"s.ms_push_rod_num AS push_num, s.ms_rod_cha as rod_cha,0 as before_after,s.ms_is_par as is_par," +
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_bogey as is_bogey,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId ");
		if(groupId != null){
			sql.append(" and s.ms_group_id = :groupId ");
		}
		if(teamId != null){
			sql.append(" and s.ms_team_id = :teamId ");
		}
		sql.append(" and s.ms_user_id = :userId and s.ms_before_after = 0 ) ");
		sql.append("where p.pp_name = :beforeHole and p.pp_p_id = :parkId ORDER BY p.pp_name, p.pp_hole_num LIMIT 99999999) as t");

		sql.append(" union all ");

		sql.append("select * from (select p.*,s.ms_id as ms_id,s.ms_user_id as ms_user_id,s.ms_user_name as ms_user_name," +
				"s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_is_up AS is_up," +
				"s.ms_rod_num AS rod_num," +
				"s.ms_push_rod_num AS push_num, s.ms_rod_cha as rod_cha ,1 as before_after,s.ms_is_par as is_par," +
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_bogey as is_bogey,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId ");
		if(groupId != null){
			sql.append(" and s.ms_group_id = :groupId ");
		}
		if(teamId != null){
			sql.append(" and s.ms_team_id = :teamId ");
		}
		sql.append(" and s.ms_user_id = :userId and s.ms_before_after = 1) ");
		sql.append("where p.pp_name = :afterHole and p.pp_p_id = :parkId " +
				" ORDER BY p.pp_name ,p.pp_hole_num LIMIT 99999999) as p1");

		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}


	/**
	 * 获取总计
	 */
	public List<Map<String, Object>> getTotalScoreWithUser(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.mugm_user_id AS user_id, sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
		sql.append("FROM match_user_group_mapping as g LEFT JOIN match_score AS s ");
        sql.append("ON (s.ms_match_id = g.mugm_match_id " +
                "AND s.ms_user_id = g.mugm_user_id and g.mugm_team_id = s.ms_team_id ) ");
        sql.append("where g.mugm_match_id = "+matchId+" AND g.mugm_group_id = "+groupId);
		sql.append(" GROUP BY g.mugm_user_id ORDER BY g.mugm_user_id,g.mugm_team_id");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 本场地总杆数
	 * @return
	 */
	public Long getTotalRod(MatchInfo matchInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(t.ppHoleStandardRod) as totalRod FROM ParkPartition as t where t.ppPId = " + matchInfo.getMiParkId()+
				"AND ( t.ppName = '" +matchInfo.getMiZoneBeforeNine()+"' OR t.ppName = '"+matchInfo.getMiZoneAfterNine()+"')");
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 获取用户所在的球队id
	 * @return
	 */
	public List<Long> getTeamIds(List<Long> teamIds, Long userId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIds",teamIds);
		parp.put("userId",userId);
		StringBuilder sql = new StringBuilder();
		sql.append("select m.tumTeamId from TeamUserMapping as m where m.tumTeamId in(:teamIds) and m.tumUserId = :userId");
		return dao.createQuery(sql.toString(),parp);
	}

	/**
	 * 单练——我所在的组
	 * @return
	 */
	public MatchGroup getMyGroupById(Long miId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchGroup as t where t.mgMatchId = "+miId);
		List<MatchGroup> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取参赛球队的队长
	 * @return
	 */
	public List<Map<String,Object>> getTeamCaptailByTeamIds(List<Long> teamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("select m.tumUserId as userId,m.tumTeamId as teamId from TeamUserMapping as m where m.tumTeamId in(:teamIdList) and m.tumUserType = 0");
		return dao.createQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 通过比赛id，组id，用户id 获取mapping
	 * @return
	 */
	public MatchUserGroupMapping getMatchGroupMappingByUserId(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId = "+matchId+" and m.mugmUserId= "+userId);
		if(groupId != null){
			sql.append(" and m.mugmGroupId = "+groupId);
		}
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询是否是参赛人员
	 * @return
	 */
	public Long getIsContestants(Long userId, Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId ="+matchId+" and m.mugmUserId="+userId);
	    return dao.createCountQuery("SELECT COUNT(*) "+sql.toString());
	}


	/**
	 * 比赛——分队统计——获取每个队排前n名的人的杆数和排名
	 * @return
	 */
	public List<Map<String, Object>> getMatchRodScoreByMingci_old(Long matchId, Integer mingci) {
		StringBuilder hql = new StringBuilder();
		hql.append("select t.userId,u.ui_real_name as realName,u.ui_nick_name as nickName," +
				"t.teamId,t.teamName,round(SUM(t.rodNum)/sum(t.count),2) AS avgRodNum," +
				"t.rodNum as sumRodNum ");
		hql.append("from ( ");
		hql.append(" SELECT " +
				"s.ms_user_id AS userId, " +
				"SUM(s.ms_rod_num) AS rodNum, " +
				"s.ms_team_id AS teamId, " +
				"teamInfo.ti_name AS teamName, " +
				"count(s.ms_id) AS count " +
				"FROM " +
				"match_score AS s, " +
				"team_info AS teamInfo " +
				"WHERE " +
				"s.ms_match_id = " +matchId+
				" AND s.ms_team_id = teamInfo.ti_id " +
				"AND s.ms_type = 0 " +
				"GROUP BY " +
				"s.ms_user_id, " +
				"s.ms_team_id " +
				"ORDER BY " +
				"rodNum ");
		hql.append(")as t ,team_user_mapping as tum,user_info as u ");
		hql.append("where exists ( ");
			hql.append(" select count(*) from (");
			hql.append(" SELECT " +
					"s.ms_user_id AS userId, " +
					"SUM(s.ms_rod_num) AS rodNum, " +
					"s.ms_team_id AS teamId, " +
					"teamInfo.ti_name AS teamName, " +
					"count(s.ms_id) AS count " +
					"FROM " +
					"match_score AS s, " +
					"team_info AS teamInfo " +
					"WHERE " +
					"s.ms_match_id = " +matchId+
					" AND s.ms_team_id = teamInfo.ti_id " +
					"AND s.ms_type = 0 " +
					"GROUP BY " +
					"s.ms_user_id, " +
					"s.ms_team_id " +
					"ORDER BY " +
					"rodNum ");
			hql.append(" )as ts ");
		hql.append("where ts.rodNum <=t.rodNum " +
					"GROUP BY ts.teamId " +
					"HAVING COUNT(*) <= "+mingci+" ");
		hql.append(" ) ");
		hql.append("and tum.tum_user_type != 2 and t.userId = tum.tum_user_id ");
		hql.append("GROUP BY t.userId,t.teamId ");
		hql.append("ORDER BY sumRodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 比赛——分队统计——获取每个队排前n名的人的杆数和排名
	 * @return
	 */
	public List<Map<String, Object>> getMatchRodScoreByMingci(Long matchId, Integer mingci) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT *,round(SUM(t.rodNum)/sum(t.count),2) AS avgRodNum ");
		hql.append("from ( ");
		hql.append(" SELECT " +
				"s.ms_user_id AS userId, " +
				"u.ui_nick_name AS nickName, " +
				"u.ui_real_name AS realName, " +
				"SUM(s.ms_rod_num) AS rodNum, " +
				"s.ms_match_id AS matchId, " +
				"s.ms_team_id AS teamId, " +
				"t.ti_name as teamName, " +
				"count(s.ms_id) as count " +
				"FROM " +
				"match_score AS s, " +
				"user_info AS u, " +
				"team_info as t " +
				"WHERE " +
				"s.ms_match_id = " +matchId+
				" AND s.ms_user_id = u.ui_id " +
				"AND s.ms_type = 0 " +
				"and s.ms_team_id = t.ti_id " +
				"GROUP BY " +
				"s.ms_user_id, " +
				"s.ms_team_id ");
		hql.append(")as t ");
		hql.append("where exists ( ");
		hql.append(" select count(*) from (");
		hql.append(" SELECT " +
				"s.ms_user_id AS userId, " +
				"u.ui_nick_name AS nickName, " +
				"u.ui_real_name AS realName, " +
				"SUM(s.ms_rod_num) AS rodNum, " +
				"s.ms_match_id AS matchId, " +
				"s.ms_team_id AS teamId, " +
				"t.ti_name as teamName, " +
				"count(s.ms_id) as count " +
				"FROM " +
				"match_score AS s, " +
				"user_info AS u, " +
				"team_info as t " +
				"WHERE " +
				"s.ms_match_id = " +matchId+
				" AND s.ms_user_id = u.ui_id " +
				"AND s.ms_type = 0 " +
				"and s.ms_team_id = t.ti_id " +
				"GROUP BY " +
				"s.ms_user_id, " +
				"s.ms_team_id ");
		hql.append(" )as ts ");
		hql.append("where ts.rodNum <=t.rodNum " +
				"GROUP BY ts.teamId " +
				"HAVING COUNT(*) <= "+mingci+" ");
		hql.append(" ) ");
		hql.append("GROUP BY t.userId ");
		hql.append("ORDER BY t.teamId, t.rodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 判断是否是我创建的比赛
	 */
	public Long getIsMyCreatMatch(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM MatchInfo AS m WHERE m.miId = "+matchId+" and m.miCreateUserId = "+userId);
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 创建比赛—获取球场城市列表
	 * @param keyword 搜索关键字
	 * @return
	 */
	public List<String> getParkCityList(String keyword) {
		Map<String,Object> parp = new HashMap<>();
		if(StringUtils.isNotEmpty(keyword)){
			parp.put("keyword","%"+keyword+"%");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT(piCity) FROM ParkInfo AS p ");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append("where p.piCity like :keyword ");
		}
		return dao.createQuery(sql.toString(), parp);
	}

	/**
	 * 获取本组人数
	 */
	public Long getGroupUserCountById(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) FROM MatchUserGroupMapping AS g where g.mugmMatchId ="+matchId+
				" and g.mugmGroupId = "+groupId);
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 成绩提交 球队确认  获取本队球友的总得分
	 */
	public List<Map<String, Object>> getSumScoreListByMatchIdTeamId(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " +
				"s.ms_team_id AS teamId, " +
				"s.ms_user_id AS userId, " +
				"s.ms_user_name AS userName, " +
				"SUM(s.ms_rod_num) as sumRodNum, " +
				"SUM(s.ms_push_rod_num) as sumPushNum ");
		sql.append("FROM match_score AS s ");
		sql.append("WHERE s.ms_match_id = "+matchId +" AND s.ms_team_id = "+teamId+" GROUP BY s.ms_user_id ORDER BY sumRodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取球友在该队伍的积分情况
	 */
	public TeamUserMapping getTeamUserMappingByIds(Long teamId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM TeamUserMapping AS m where m.tumTeamId ="+teamId+" and m.tumUserId = "+userId);
		List<TeamUserMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取本场比赛所有参赛人员的排名，按照杆数排名
	 */
	public List<Map<String, Object>> getRankingListByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select @rows\\:=@rows+1 as rows, if(@gnum=sumRodNum,@rownum\\:=@rownum,@rownum\\:=@rownum+1) as rank, score.* from(");
		sql.append("SELECT s.ms_team_id AS teamId, s.ms_user_id AS userId, s.ms_user_name AS userName, SUM(s.ms_rod_num) AS sumRodNum," +
					"SUM(s.ms_push_rod_num) AS sumPushNum " +
				"FROM match_score AS s WHERE s.ms_match_id = " + matchId+ " GROUP BY s.ms_user_id ORDER BY sumRodNum ");
		sql.append(") as score,(select @rownum\\:=0,@gnum\\:=0,@rows\\:=0) number ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取用户前后半场的得分情况
	 * type:区分前后半场
	 * scoreType：区分上报球队的成绩 和 本队成绩
	 */
	public List<Map<String, Object>> getBeforeAfterScoreByUserId(Long uiId, MatchInfo matchInfo,Integer type,Long teamId,Integer scoreType) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchInfo.getMiId());
		if(type == 0){
			parp.put("holeName", matchInfo.getMiZoneBeforeNine());
		}else{
			parp.put("holeName", matchInfo.getMiZoneAfterNine());
		}
		parp.put("userId", uiId);
		parp.put("parkId", matchInfo.getMiParkId());
		parp.put("type", type);
		parp.put("scoreType", scoreType);
		parp.put("teamId", teamId);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from (select p.*,s.ms_id as ms_id,s.ms_user_id as ms_user_id,s.ms_user_name as ms_user_name," +
				"s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_is_up AS is_up," +
				"s.ms_rod_num AS rod_num," +
				"s.ms_push_rod_num AS push_num, s.ms_rod_cha as rod_cha,0 as before_after,s.ms_is_par as is_par," +
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId ");
		sql.append(" and s.ms_user_id = :userId  ");
		sql.append(" and s.ms_before_after = :type  ");
		sql.append(" and s.ms_type = :scoreType ");
		if(teamId != null){
			sql.append(" and s.ms_team_id = :teamId  ");
		}
		sql.append(" )) as t ");
		sql.append("where t.pp_name = :holeName and t.pp_p_id = :parkId  ");
		sql.append(" ORDER BY t.pp_name, t.pp_hole_num ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 是否参赛球队的队员
	 */
	public Long getIsJoinTeamsUser(Long userId, List<Long> teamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("userId", userId);
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM TeamUserMapping as m where m.tumUserId = :userId and m.tumTeamId in (:teamIdList) ");
		return dao.createCountQuery(sql.toString(),parp);
	}

	/**
	 * 比赛——普通用户从一个组退出比赛
	 * @return
	 */
	public void delFromMatch(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MatchUserGroupMapping as m where m.mugmUserId = "+userId+" and m.mugmMatchId = "+matchId+" AND m.mugmGroupId ="+groupId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 获取随机用户最大的用户id
	 * @return
	 */
	public Long getMaxOtherUserId() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT MAX(mg.mugmUserId) FROM MatchUserGroupMapping as mg where mg.mugmMatchId in (" +
				"select m.miId from MatchInfo as m where m.miType = 0" +
				") ");
		List<Long> maxid = dao.createQuery(sql.toString());
		if(maxid != null && maxid.size()>0){
			return maxid.get(0);
		}
		return null;
	}

	/**
	 * 获取比赛的本球队所有用户(首列显示)
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.ui_id as uiId, u.ui_real_name as uiRealName, u.ui_headimg as uiHeadimg, " +
				"m.mugm_team_id AS team_id  ");
		sql.append("FROM match_user_group_mapping as m,user_info as u ");
		sql.append("where m.mugm_user_id = u.ui_id and m.mugm_match_id = "+matchId);
		sql.append(" and m.mugm_team_id = "+teamId);
		sql.append(" order by m.mugm_user_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 是否已经围观
	 * @return
	 */
	public Long getIsWatch(Long userId, Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM MatchJoinWatchInfo as j ");
		sql.append("where j.mjwiMatchId = "+matchId);
		sql.append(" and j.mjwiUserId = "+userId);
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 是否已经报名
	 * @return
	 */
	public Long getIsApply(Long userId, Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM MatchUserGroupMapping as g ");
		sql.append("where g.mugmMatchId = "+matchId);
		sql.append(" and g.mugmUserId = "+userId);
		sql.append(" and g.mugmUserType = 2");
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 是否已经有提交成绩的计算配置
	 * @return
	 */
	public IntegralConfig getSubmitScoreConfig(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM IntegralConfig as c ");
		sql.append("where c.icMatchId = "+matchId);
		sql.append(" and c.icTeamId = "+teamId);
		List<IntegralConfig> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}


	/**
	 * 获取用户所在球队id
	 * @return
	 */
	public TeamUserMapping getTeamUserMappingByUserId(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM TeamUserMapping as t ");
		sql.append("where t.tumUserId = "+userId);
		List<TeamUserMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 成绩提交，将该组的得分成绩标为已确认
	 * （注意球友加入球队是否成功）
	 * @return
	 */
	public void updateMatchScoreById(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s,TeamUserMapping as m SET s.msIsTeamSubmit = 1 ");
		sql.append(" WHERE s.msMatchId = "+matchId);
		sql.append(" AND s.msTeamId = m.tumTeamId ");
		sql.append(" AND s.msUserId = m.tumUserId ");
		sql.append(" AND m.tumUserType != 2 ");
		sql.append(" AND s.msTeamId = "+teamId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 撤销成绩提交，将该组的得分成绩标为未确认
	 * @return
	 */
	public void cancelMatchScoreById(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s SET s.msIsTeamSubmit = 0 ");
		sql.append(" WHERE s.msMatchId = "+matchId);
		sql.append(" AND s.msTeamId = "+teamId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 比洞赛——获取本次比赛中的分组和每组用户的总分
	 * @return
	 */
	public List<Map<String, Object>> getMatchHoleScoreList(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select s.msTeamId as teamId,s.msGroupId as groupId,s.msGroupName as groupName,s.msUserId as userId,sum(s.msRodNum) as sumRod from MatchScore as s where s.msMatchId = "+matchId+" GROUP BY s.msUserId ");
		return dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}


	/**
	 * 我的——历史成绩——获取我参加的所有比赛所在的球场(不包括单练)
	 * @return
	 */
	public List<Map<String, Object>> getParkListByUserId(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select m.miId as miId, m.miParkId as parkId ,m.miParkName as parkName," +
				"m.miZoneBeforeNine as beforeNine , m.miZoneAfterNine as afterNine " +
				"from MatchInfo as m where m.miId in(" +
				"select DISTINCT(g.mugmMatchId) from MatchUserGroupMapping as g where g.mugmUserId = "+userId+") and m.miType = 1 order by m.miParkId ");
		return dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 单练——更新比赛用户mapping中的临时用户姓名
	 * @return
	 */
	public void updateMatchUserMapping(Map<String,Object> parp) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchUserGroupMapping AS t SET t.mugmUserName = :userName ");
		sql.append(" WHERE t.mugmMatchId = :matchId");
		sql.append(" AND t.mugmGroupId = :groupId");
		sql.append(" AND t.mugmUserId = :userId");
		dao.executeHql(sql.toString(), parp);
	}

	/**
	 * 单练——更新记分表中的临时用户姓名
	 * @return
	 */
	public void updateMatchScoreUserInfo(Map<String, Object> parp) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s SET s.msUserName = :userName ");
		sql.append(" WHERE s.msMatchId = :matchId");
		sql.append(" AND s.msGroupId = :groupId");
		sql.append(" AND s.msUserId = :userId");
		dao.executeHql(sql.toString(), parp);
	}

	/**
	 * 是否本队队长
	 */
	public Long getIsTeamCaptain(Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM TeamUserMapping as tum WHERE tum.tumUserType = 0 AND tum.tumTeamId = "+teamId+ " and tum.tumUserId = "+userId);
		return dao.createCountQuery(hql.toString());
	}

	/**
	 * 是否本比赛塞长
	 */
	public Long getIsMatchCaptain(Long matchId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM  MatchUserGroupMapping as g WHERE g.mugmUserType = 0 AND g.mugmMatchId = "+matchId+ " and g.mugmUserId = "+userId);
		return dao.createCountQuery(hql.toString());
	}




	/**
	 * 计算差点 取最近十场比赛的成绩平均（不够十场按实际场数），减去72然后再乘0.8 不包括单练
	 * 在算差点的时候，要检查下所有18洞洞杆数不为零（是否没记成绩的洞杆数为零？）有零的这一场就不能计算差点。
	 */
	public List<Object> getLessFiveMatchByUserId(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT AVG(t.sum) as avg FROM ( ");
		hql.append("SELECT sum(s.ms_rod_num) AS sum FROM match_score AS s, match_info as m" +
				" WHERE m.mi_type = 1 " +
				"and m.mi_id = s.ms_match_id " +
				"and s.ms_user_id = "+userId +
				" and s.ms_rod_num != 0 "+
				"GROUP BY s.ms_match_id HAVING count(s.ms_id)>18 ORDER BY s.ms_create_time DESC ");
		hql.append(" ) as t ");
		return dao.createSQLQuery(hql.toString(),0,10);
	}



	/**
	 * 获取我参加的所有比赛所在的球场 和总杆差 (比赛id，球场id，球场名称,前半场名称，后半场名称)
	 */
	public List<Map<String, Object>> getTotalChaListByUserId(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT s.ms_match_id as miId,m.mi_park_id as parkId,m.mi_park_name as parkName," +
				"m.mi_zone_before_nine as beforeNine , m.mi_zone_after_nine as afterNine,sum(s.ms_rod_cha) as sumRodCha,m.mi_match_time as time ");
		sql.append("FROM match_score AS s,match_info as m " +
				"WHERE s.ms_user_id = "+userId+ " and s.ms_match_id = m.mi_id GROUP BY s.ms_match_id order by m.mi_match_time desc ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//获取每组人数
    public List<Map<String,Object>> getCountUserByMatchId(Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select g.mugm_group_name as groupName,count(g.mugm_user_id) as count from match_user_group_mapping as g where g.mugm_match_id =" +matchId+
                " GROUP BY g.mugm_group_id");
        return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
    }

	//删除比赛 置为不可用
	public void updateMatchState(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchInfo AS m set m.miIsValid = 0");
		sql.append("WHERE m.miId = " +matchId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 记分卡 初始化 查询我是否可以记分
	 * 我是否是同组比赛人员或者是被邀请到本组记分的用户
	 * @return
	 */
	public Long getMeCanScore(Long matchId, Long groupId, Long myUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM  MatchUserGroupMapping as g WHERE g.mugmMatchId = "+matchId);
		if(groupId != null){
			hql.append(" and g.mugmGroupId = "+groupId);
		}
		hql.append(" and g.mugmUserId = "+myUserId);
		return dao.createCountQuery(hql.toString());
	}

	/**
	 * 获取参赛队员列表
	 * @return
	 */
	public List<Object[]> getMatchUserGroupMappingList(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchUserGroupMapping as g,TeamInfo as t WHERE g.mugmTeamId = t.tiId and g.mugmMatchId = "+matchId);
		hql.append(" order by g.mugmGroupId ");
		return dao.createQuery(hql.toString());
	}

	/**
	 * 获取参赛队员列表
	 * @return
	 */
	public List<MatchUserGroupMapping> getMatchUserGroupMappingListByMatchId(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchUserGroupMapping as g WHERE g.mugmMatchId = "+matchId);
		return dao.createQuery(hql.toString());
	}

	/**
	 * 查询是否有我生成的邀请记分二维码
	 * @return
	 */
	public MatchScoreUserMapping getMatchScoreUserMapping(Long matchId, Long groupId, Long myUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchScoreUserMapping as t WHERE t.msumMatchId = "+matchId+" and t.msumGroupId = "+groupId);
		hql.append(" and t.msumMatchUserId = "+myUserId);
		List<MatchScoreUserMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}


	//获取用户所在球队的简称
	public String getTeamAbbrevByUserId(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT t.tiAbbrev FROM TeamUserMapping as tm,TeamInfo as t WHERE t.tiId = tm.tumTeamId and tm.tumUserId = "+userId);
		List<String> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取单练的groupId
	 * @return
	 */
	public Long getSingleMatchGroupIdByMatchId(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT t.mgId FROM MatchGroup as t WHERE t.mgMatchId = "+matchId);
		List<Long> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 创建比赛——获取赛长用户所在球队，是否同时是参赛球队的队长 如果是让用户选择一个做代表队 不包括未审核通过的
	 * @return
	 */
	public List<Map<String,Object>> getCaptainTeamIdList_notuse(List<Long> teamIds,Long captainUserId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIds",teamIds);
		parp.put("captainUserId",captainUserId);
		StringBuilder hql = new StringBuilder();
		hql.append("select t.tiId as teamId,t.tiName as teamName " +
				"from TeamUserMapping as tum,TeamInfo as t where tum.tumTeamId = t.tiId and tum.tumUserId = :captainUserId ");
		if(teamIds != null && teamIds.size()>0){
			hql.append(" and t.tiId in (:teamIds)");
		}
		List<Map<String,Object>> list = dao.createQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		if(list != null && list.size()>0){
			return list;
		}
		return null;
	}

	/**
	 * 获取赛长代表哪个队比赛
	 * @return
	 */
	public Long getTeamIdByMatchIdAndUserId(Long matchId, Long captainUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from MatchUserGroupMapping as g where g.mugmMatchId = "+matchId+" and g.mugmUserId = "+captainUserId);
		List<MatchUserGroupMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0).getMugmTeamId();
		}
		return null;
	}

	/**
	 * 获取本球队所有用户，去除已经报名、参赛的同队球友
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, String keyword, List<Long> userIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("teamId",teamId);
		parp.put("userIdList",userIdList);

		if(StringUtils.isNotEmpty(keyword) && !keyword.equals("undefined") && !keyword.equals("null")){
			parp.put("keyword","%"+keyword+"%");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId, u.uiRealName as uiRealName, u.uiHeadimg as uiHeadimg,t.tiAbbrev as tiAbbrev ");
		sql.append("FROM TeamUserMapping as m,UserInfo as u,TeamInfo as t ");
		sql.append(" where m.tumTeamId = t.tiId ");
		sql.append(" and m.tumUserId = u.uiId ");
		sql.append(" and m.tumTeamId = :teamId ");
		if(userIdList != null && userIdList.size()>0){
			sql.append(" and u.uiId not in (:userIdList) ");
		}
		if(parp.get("keyword") != null){
			sql.append(" and (u.uiRealName like :keyword or u.uiNickName like :keyword)");
		}
		sql.append(" GROUP by u.uiId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本比赛所有用户id
	 * @return
	 */
	public List<Long> getUserListByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.mugmUserId FROM MatchUserGroupMapping as m ");
		sql.append(" where m.mugmMatchId = "+matchId);
		return dao.createQuery(sql.toString());
	}


	/**
	 * 比赛详情——赛长获取已经报名的用户
	 * @return
	 */
	public List<Map<String, Object>> getApplyUserByMatchId(Long matchId, String keyword, Long groupId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("groupId",groupId);
		if(StringUtils.isNotEmpty(keyword) && !keyword.equals("undefined") && !keyword.equals("null")){
			parp.put("keyword","%"+keyword+"%");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg,t.tiAbbrev as tiAbbrev ");
		sql.append("FROM MatchUserGroupMapping as m,UserInfo as u,TeamInfo as t where " +
				"m.mugmUserId = u.uiId and m.mugmMatchId = :matchId and m.mugmUserType = 2 and m.mugmTeamId = t.tiId ");

		sql.append(" and m.mugmGroupId = :groupId ");
		if(parp.get("keyword") != null){
			sql.append(" and (u.uiRealName like :keyword or u.uiNickName like :keyword)");
		}
		List<Map<String, Object>> list = dao.createQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}


	/**
	 * 创建比赛——选择上报球队——获取参赛用户所在的上级球队
	 * @return
	 */
	public List<Map<String,Object>> getJoinTeamListByMatchId(List<Long> idList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("idList",idList);
		StringBuilder sql = new StringBuilder();
		sql.append("select ti.ti_id as teamId,count(*) as count,ti.ti_name as teamName,ti.ti_logo as teamLogo " +
				"from team_user_mapping as t,team_info as ti where t.tum_user_id in ( ");
		sql.append("select tum.tum_user_id from team_user_mapping as tum where tum.tum_team_id in(:idList) ");
		sql.append(")and t.tum_team_id = ti.ti_id GROUP BY t.tum_team_id having count >1 ");
		return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 更新比赛——如果参赛球队有改变，删除比赛mapping表该球队的信息 除了自己
	 * @return
	 */
	public void delMatchUserMappingByTeamId(Long matchId, List<Long> teamIdList, Long userId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamIdList",teamIdList);
		parp.put("userId",userId);
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MatchUserGroupMapping as t ");
		sql.append("WHERE t.mugmMatchId = :matchId ");
		sql.append("and t.mugmTeamId in (:teamIdList) ");
		sql.append("and t.mugmUserId != :userId");
		dao.executeHql(sql.toString(),parp);
	}

	/**
	 * 获取我加入的球队id
	 * @return
	 */
	public List<Long> getMyJoinTeamList(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select tum.tumTeamId from TeamUserMapping as tum where tum.tumUserType != 2 and tum.tumUserId = "+userId);
		List<Long> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list;
		}
		//这里返回空list是为了从小程序分享进来的用户，如果没有任何球队，在上一步处理中，从参赛队中选代表队
		return new ArrayList<>();
	}

	/**
	 * 获取记分详情
	 * @return
	 */
	public MatchScore getScoreByReportTeam(Long reportTeamId, Long matchId, Long groupId, Long userId,
										   Integer beforeAfter,String holeName,Integer holeNum,Integer standardRod) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("reportTeamId",reportTeamId);
		parp.put("matchId",matchId);
		parp.put("groupId",groupId);
		parp.put("userId",userId);
		parp.put("beforeAfter",beforeAfter);
		parp.put("holeName",holeName);
		parp.put("holeNum",holeNum);
		parp.put("standardRod",standardRod);
		StringBuilder hql = new StringBuilder();
		hql.append("from MatchScore as s where s.msTeamId = :reportTeamId ");
		hql.append(" and s.msMatchId = :matchId ");
		hql.append(" and s.msGroupId = :groupId ");
		hql.append(" and s.msUserId = :userId ");
		hql.append(" and s.msBeforeAfter = :beforeAfter ");
		hql.append(" and s.msHoleName = :holeName ");
		hql.append(" and s.msHoleNum = :holeNum ");
		hql.append(" and s.msHoleStandardRod = :standardRod ");
		List<MatchScore> list = dao.createQuery(hql.toString(),parp);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}


	/**
	 * 分队统计——获取本次比赛的分组，和每组中每个球队的总分
	 * @return
	 */
	public List<Map<String, Object>> getEveGroupScoreList(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select s.msTeamId as teamId,s.msGroupId as groupId,s.msGroupName as groupName,sum(s.msRodNum) as sumRod ");
		sql.append(" from MatchScore as s where s.msMatchId = "+matchId);
		sql.append(" GROUP BY s.msTeamId,s.msGroupId ");
		sql.append(" order BY s.msGroupId,sumRod ");
		return dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 赛长操作报名——获取已报名的球友
	 * @return
	 */
	public List<Long> getApplyUserIdList(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select m.mugmUserId ");
		sql.append(" from MatchUserGroupMapping as m ");
		sql.append(" where m.mugmMatchId = "+matchId);
		sql.append(" and m.mugmTeamId = "+teamId);
		return dao.createQuery(sql.toString());
	}

	/**
	 * 队内赛，取前n名，获取本队总参赛人数
	 * @return
	 */
	public Long getTeamUserCount(Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(m.mugmId) ");
		sql.append(" from MatchUserGroupMapping as m ");
		sql.append(" where m.mugmTeamId = "+teamId);
		sql.append(" and m.mugmUserType != 2 ");
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 多队比赛，取前n名，取参赛人数最少的那个队的显示
	 * @return
	 */
	public List<Long> getTeamUserCountList(List<Long> joinTeamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIds",joinTeamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("select count(m.mugmId) as count ");
		sql.append(" from MatchUserGroupMapping as m ");
		sql.append(" where m.mugmTeamId in (:teamIds) ");
		sql.append(" and m.mugmUserType !=2 ");
		sql.append(" group by m.mugmTeamId ");
		sql.append(" order by count ");
		return dao.createQuery(sql.toString(),parp);
	}

	/**
	 * 单队比赛，取前n名的成绩 (参赛人数 显示的是各队实际的参赛人数)
	 * @return
	 */
	public List<Map<String, Object>> getMatchRodTotalScoreByMingci(Long matchId, Integer mingci) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT round(AVG(t.sumRod), 2) AS avgRodNum,SUM(t.sumRod) AS sumRodNum ");
		hql.append("FROM ( ");
		hql.append("SELECT " +
					"mm.mugm_match_id AS matchId, " +
					"mm.mugm_group_id AS groupId, " +
					"mm.mugm_user_id AS userId, " +
					"sum(s.ms_rod_num) AS sumRod " +
					"FROM match_user_group_mapping AS mm " +
					"LEFT JOIN match_score AS s ON ( " +
						"s.ms_team_id = mm.mugm_team_id " +
						"AND s.ms_match_id = mm.mugm_match_id " +
						"AND s.ms_group_id = mm.mugm_group_id " +
						"AND s.ms_user_id = mm.mugm_user_id " +
					") " +
					"WHERE mm.mugm_match_id = " +matchId+
					" GROUP BY mm.mugm_user_id " +
					"ORDER BY sumRod " +
					"LIMIT 0,"+mingci);
		hql.append(") as t");
		return dao.createSQLQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取单练记分卡的总计
	 */
	public List<Map<String, Object>> getSingleTotalScoreWithUser(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.mugm_user_id AS user_id, sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
		sql.append("FROM match_user_group_mapping as g LEFT JOIN match_score AS s ");
		sql.append("ON (s.ms_match_id = g.mugm_match_id " +
				"AND s.ms_user_id = g.mugm_user_id ) ");
		sql.append("where g.mugm_match_id = "+matchId+" AND g.mugm_group_id = "+groupId);
		sql.append(" GROUP BY g.mugm_user_id ORDER BY g.mugm_user_id");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}
}
