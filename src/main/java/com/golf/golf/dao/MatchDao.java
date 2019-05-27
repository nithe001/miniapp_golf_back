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
	 * 获取比赛列表 0：全部比赛  1：我参加的比赛  2：我可以报名的比赛 3:我创建的比赛
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m.mi_id AS mi_id," +
				"m.mi_logo as mi_logo," +
				"m.mi_title AS mi_title," +
				"m.mi_park_name AS mi_park_name," +
				"m.mi_match_time AS mi_match_time," +
				"m.mi_apply_end_time AS mi_apply_end_time," +
				"m.mi_is_end AS mi_is_end," +
				"j.mjwi_type AS type," +
				"count(j.mjwi_user_id) AS userCount,m.mi_match_format_1 as mi_match_format_1,m.mi_match_format_2 as mi_match_format_2 ");
		hql.append("FROM match_info AS m ");
		hql.append("LEFT JOIN match_join_watch_info AS j ON (m.mi_id = j.mjwi_match_id and m.mi_is_valid = 1 AND j.mjwi_type = 0) ");
		hql.append("LEFT JOIN park_info as p on m.mi_park_id = p.pi_id ");
		hql.append("WHERE 1=1 ");
		hql.append(" AND m.mi_type = 1 ");

		if((Integer)parp.get("type") == 1){
			//我参加的比赛
			hql.append("AND m.mi_id IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) ");
		}else if((Integer)parp.get("type") == 2){
			//我可以报名的比赛   包括我创建的正在报名的比赛，作为赛长可以管理报名
			hql.append("AND m.mi_is_end = 0 AND (m.mi_id NOT IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) or (m.mi_create_user_id = :userId and m.mi_is_end = 0))");
		}else if((Integer)parp.get("type") == 3){
			//我创建的比赛
			hql.append("AND m.mi_create_user_id = :userId ");
		}
		if(parp.get("keyword") != null){
			hql.append("AND m.mi_title LIKE :keyword ");
		}

		//计算我附近的比赛
		if(parp.get("minlng") != null){
			hql.append("AND p.pi_lng >= :minlng ");
		}
		if(parp.get("minlat") != null){
			hql.append("AND p.pi_lat >= :minlat ");
		}
		hql.append("GROUP BY m.mi_id ");

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+") as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY m.mi_is_end, m.mi_apply_end_time");

		List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
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
		hql.append(" FROM MatchUserGroupMapping AS m,UserInfo AS u WHERE 1=1 ");
		hql.append(" and m.mugmUserId = u.uiId " );
		hql.append(" and m.mugmUserType = 0 ");
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
     * 获取本组的用户(包括赛长)
     * @return
     */
    public List<Map<String, Object>> getMatchGroupListByGroupId(Long matchId, Long groupId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("groupId",groupId);
		parp.put("matchId",matchId);
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName ");
        hql.append("FROM MatchUserGroupMapping AS g,UserInfo as u WHERE 1=1 ");
        hql.append("AND g.mugmGroupId = :groupId and g.mugmMatchId = :matchId AND g.mugmUserId = u.uiId ");
        hql.append("ORDER BY g.mugmCreateTime");

		List<Map<String, Object>> list = dao.createQuery(hql.toString(), parp, 0, 4, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
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
	 * 创建比赛——获取选中的参赛球队的详情
	 * @return
	 */
	public List<Map<String, Object>> getTeamListByTeamIds(List<Long> ids) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("ids",ids);
		StringBuilder hql = new StringBuilder();
		hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where tm.tum_user_type != 2 GROUP BY tum_team_id ");
		hql.append(")as tum on (t.ti_id = tum.tum_team_id and t.ti_id in(:ids) and t.ti_is_valid = 1 )");
		hql.append("where 1=1 ");
		String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo ";
		return dao.createSQLQuery( select + hql.toString(), parp,Transformers.ALIAS_TO_ENTITY_MAP);
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
		hql.append(" from ParkInfo as p where p.piIsValid = 1");
		if(parp.get("keyword") != null){
			hql.append("AND p.piName LIKE :keyword  ");
		}
		hql.append("AND p.piCity = :regionName  ");

		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<ParkInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("GROUP BY p.piCreateTime ");
		List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
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
		hql.append("FROM ParkInfo AS p WHERE 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("AND p.piName LIKE :keyword  ");
		}

		if(parp.get("city") != null){
			hql.append("AND p.piCity = :city  ");
		}

		hql.append("AND p.piLng >= :minlng ");
//		hql.append("AND p.piLng <= :maxlng ");
		hql.append("AND p.piLat >= :minlat ");
//		hql.append("AND p.piLat <= :maxlat ");

		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<ParkInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY p.piLng,p.piLat ");
		List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
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
		sql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg ");
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
	 * 获取本组用户 和 用户的总杆差
	 */
	public List<Map<String, Object>> getUserListById(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, t1.*  FROM ( " +
						"SELECT u.ui_id AS uiId, u.ui_real_name AS uiRealName, u.ui_headimg AS uiHeadimg " +
						"FROM match_user_group_mapping AS m, user_info AS u " +
						"WHERE m.mugm_user_id = u.ui_id AND m.mugm_match_id =  " +matchId );
		if(groupId != null){
			sql.append(" and m.mugm_group_id = "+groupId);
		}
		sql.append(" ORDER BY m.mugm_user_id ) AS t " +
				"LEFT JOIN ( SELECT s.ms_user_id, sum(s.ms_rod_cha) AS sumRodCha FROM  match_score AS s " +
							"WHERE  s.ms_match_id = "+matchId +" GROUP BY  s.ms_user_id  ) AS t1 " +
				"ON t.uiId = t1.ms_user_id");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取单练的本组用户
	 */
	public List<Map<String, Object>> getSingleUserListById(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.mugmUserId as uiId, m.mugmUserName as uiRealName  ");
		sql.append("FROM MatchUserGroupMapping as m ");
		sql.append("where m.mugmMatchId = "+matchId);
		sql.append(" and m.mugmGroupId = "+groupId);
		sql.append(" order by m.mugmUserId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}


	/**
	 * 本用户得分情况
	 */
	public List<Map<String, Object>> getScoreByUserId(Long groupId, Long uiId, MatchInfo matchInfo) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchInfo.getMiId());
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
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId ");
		if(groupId != null){
			sql.append(" and s.ms_group_id = :groupId ");
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
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId ");
		if(groupId != null){
			sql.append(" and s.ms_group_id = :groupId ");
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
                "AND s.ms_user_id = g.mugm_user_id ) ");
        sql.append("where g.mugm_match_id = "+matchId+" AND g.mugm_group_id = "+groupId);
		sql.append(" GROUP BY g.mugm_user_id ORDER BY g.mugm_user_id");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
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
	public List<Long> getTeamCaptailByTeamIds(List<Long> teamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("select m.tumUserId from TeamUserMapping as m where m.tumTeamId in(:teamIdList) and m.tumUserType = 0");
		return dao.createQuery(sql.toString(),parp);
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
	 * 比赛——group——分队统计
	 * 比杆赛：按创建比赛时“参赛范围”选择的球队统计成绩并按平均杆数排名，（球队、参赛人数、平均杆数、总杆数、排名）
	 * @return
	 */
	public List<Map<String, Object>> getMatchRodTotalScore(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT t.ti_name as teamName, count(DISTINCT(s.ms_user_id)) as userCount,round(AVG(s.ms_rod_num),2) AS avgRodNum, SUM(DISTINCT(s.ms_rod_num)) AS sumRodNum ");
		hql.append("FROM  match_score AS s,team_info as t,match_user_group_mapping as mm ");
		hql.append("WHERE s.ms_match_id = "+matchId+" and s.ms_team_id = t.ti_id AND s.ms_match_id = mm.mugm_match_id GROUP BY s.ms_team_id ORDER BY avgRodNum desc ");
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
		sql.append("SELECT count(*) FROM MatchUserGroupMapping AS g where g.mugmMatchId ="+matchId+" and g.mugmGroupId = "+groupId);
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 将该用户从报名表删除
	 */
	public void delUserFromApply(Long matchId,Long uId) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete FROM MatchJoinWatchInfo AS t where t.mjwiUserId="+uId+" and t.mjwiMatchId = "+matchId);
		dao.executeHql(sql.toString());
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
	 */
	public List<Map<String, Object>> getBeforeAfterScoreByUserId(Long uiId, MatchInfo matchInfo,Integer type) {
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
		sql.append(" and s.ms_before_after = :type )) as t ");
		sql.append("where t.pp_name = :holeName and t.pp_p_id = :parkId ORDER BY t.pp_name, t.pp_hole_num ");
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
		sql.append("DELETE FROM MatchUserGroupMapping as m where m.mugmUserId = "+userId+" and m.mugmMatchId "+matchId+" AND m.mugmGroupId ="+groupId);
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
		sql.append("SELECT u.uiId as uiId, u.uiRealName as uiRealName, u.uiHeadimg as uiHeadimg, sum(m.msRodCha) as sumRodCha  ");
		sql.append("FROM MatchScore as m,UserInfo as u ");
		sql.append("where m.msUserId = u.uiId and m.msMatchId = "+matchId);
		sql.append(" and m.msTeamId = "+teamId);
		sql.append(" GROUP by u.uiId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 是否已经报名
	 * @return
	 */
	public Long getisApplyOrWatch(Long userId, Long matchId,Integer type) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM MatchJoinWatchInfo as j ");
		sql.append("where j.mjwiMatchId = "+matchId);
		sql.append(" and j.mjwiUserId = "+userId);
		sql.append(" and j.mjwiType =  "+type);
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
	 * @return
	 */
	public void updateMatchScoreById(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s SET s.msIsTeamSubmit = 1 ");
		sql.append(" WHERE s.msMatchId = "+matchId);
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
	 * 计算差点的最低要求：判断用户是否至少打了5场比赛
	 */
	public Long getLessFiveMatchByUserId(Long userId) {
		return 1L;
	}



	/**
	 * 获取我参加的所有比赛所在的球场 和总杆差 (比赛id，球场id，球场名称,前半场名称，后半场名称)
	 */
	public List<Map<String, Object>> getTotalChaListByUserId(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT s.ms_match_id as miId,m.mi_park_id as parkId,m.mi_park_name as parkName," +
				"m.mi_zone_before_nine as beforeNine , m.mi_zone_after_nine as afterNine,sum(s.ms_rod_cha) as sumRodCha,m.mi_match_time as time ");
		sql.append("FROM match_score AS s,match_info as m " +
				"WHERE s.ms_match_type = 1 AND s.ms_user_id = "+userId+ " and s.ms_match_id = m.mi_id GROUP BY s.ms_match_id order by m.mi_match_time desc ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//获取每组人数
    public List<Map<String,Object>> getCountUserByMatchId(Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select g.mugm_group_name as groupName,count(g.mugm_user_id) as count from match_user_group_mapping as g where g.mugm_match_id =" +matchId+
                " GROUP BY g.mugm_group_id");
        return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
    }
}
