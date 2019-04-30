package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.dsig.Transform;
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
		hql.append("WHERE 1=1 ");
		hql.append(" AND m.mi_type = 1 ");

		if((Integer)parp.get("type") == 1){
			//我参加的比赛
			hql.append("AND m.mi_id IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) ");
		}else if((Integer)parp.get("type") == 2){
			//我可以报名的比赛 包括我创建的比赛
			hql.append("AND (m.mi_id NOT IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) or m.mi_create_user_id = :userId)");
		}else if((Integer)parp.get("type") == 3){
			//我创建的比赛
			hql.append("AND m.mi_create_user_id = :userId ");
		}
		hql.append("GROUP BY m.mi_id ");
		if(parp.get("keyword") != null){
			hql.append("WHERE 1=1 AND m.mi_title LIKE :keyword ");
		}
		//计算我附近的比赛

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) from ("+hql.toString()+" ) as t", parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY m.mi_apply_end_time");

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
     * 获取本组的用户(包括赛长)
     * @return
     */
    public List<Map<String, Object>> getMatchGroupListByGroupId(Long groupId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("groupId",groupId);
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName ");
        hql.append("FROM MatchUserGroupMapping AS g,UserInfo as u WHERE 1=1 ");
        hql.append("AND g.mugmGroupId = :groupId AND g.mugmUserId = u.uiId ");
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
		Map<String, Object> parp = new HashMap<>();
		parp.put("teamIdList",teamIdList);
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TeamInfo AS t WHERE 1=1 ");
        sql.append(" AND t.tiId in ( :teamIdList )");
        return dao.createQuery(sql.toString(),parp);
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
	 * 获取球场 场地（旧版为选择器picker现改为页面列表显示）
	 * @return
	 */
	/*public List<String> getParkInfoList(String city) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.piName FROM ParkInfo AS p WHERE 1=1 ");
		sql.append("AND p.piCity = '"+city+"'");
		return dao.createQuery(sql.toString());
	}*/

	public List<ParkInfo> getParkInfoList(Map<String, Object> parp) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM ParkInfo AS p WHERE 1=1 ");
		if(parp.get("keywords") != null){
			sql.append("AND p.piCity = :keywords");
		}
		//计算我附近的
		return dao.createQuery(sql.toString());
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

	/**
	 * 创建比赛—选择球队——确认选择——通过id查询球队名称和logo
	 * @return
	 */
	public List<TeamInfo> getTeamsById(List<Long> teamIds) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("teamIds", teamIds);
		StringBuilder sql = new StringBuilder();
		sql.append("FROM TeamInfo AS t ");
		sql.append("WHERE t.tiId in (:teamIds) ");
		return dao.createQuery(sql.toString(),parp);
	}

	/**
	 * 分组记分卡——获取半场球洞
	 * @return
	 */
	public List<Map<String, Object>> getParkPartitionList(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.ppId as holeId,pp.ppPId as parkId,pp.ppName as ppName,pp.ppHoleNum as holeNum," +
				"pp.ppHoleStandardRod as holeStandardRod ");
		sql.append("FROM MatchInfo AS m,ParkInfo as p,ParkPartition as pp ");
		sql.append("WHERE m.miId = "+matchId);
		sql.append(" and p.piId = m.miParkId ");
		sql.append(" and (m.miZoneAfterNine = pp.ppName or m.miZoneBeforeNine = pp.ppName) ");
		sql.append(" and pp.ppPId = p.piId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 分组记分卡——获取本组用户
	 * @return
	 */
	public List<Map<String, Object>> getUserInfoListByGroupId(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT us.*, s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_is_up as is_up," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_rod_num as rod_num," +
				"s.ms_push_rod_num as push_num ");
		sql.append("FROM ( ");
		sql.append("SELECT u.ui_id AS ui_id," +
					"u.ui_real_name AS real_name " +
					"FROM match_user_group_mapping AS gm, " +
					"user_info AS u " +
					"WHERE 1 = 1 " +
					"AND gm.mugm_match_id = " +matchId+
					" AND gm.mugm_group_id = " +groupId+
					" AND gm.mugm_user_id = u.ui_id ");
		sql.append(" ) AS us " +
				"LEFT JOIN match_score AS s ON ( " +
				"s.ms_match_id = " +matchId+
				" AND s.ms_group_id = " +groupId+
				" AND s.ms_user_id = us.ui_id" +
				")" +
				"GROUP BY s.ms_id, us.ui_id " +
				"ORDER BY us.ui_id ,s.ms_hole_name ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}


	/**
	 * 分组记分卡——获取本组用户得分
	 * @return
	 */
	public List<MatchScore> getMatchScoreList(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("from MatchScore as s ");
		sql.append("WHERE s.msMatchId = "+matchId+" and s.msGroupId = "+groupId );
		sql.append(" ORDER BY s.ms_hole_name");

		return dao.createQuery(sql.toString());
	}

	//本组用户
	public List<Map<String, Object>> getUserListById(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
//		sql.append("SELECT u.uiId as uiId, u.uiRealName as uiRealName ");
		sql.append("SELECT m.mugmUserId as uiId, m.mugmUserName as uiRealName ");
		sql.append("FROM MatchUserGroupMapping as m ");
		sql.append("where m.mugmMatchId = "+matchId);
		sql.append(" and m.mugmGroupId = "+groupId);
		sql.append(" order by m.mugmUserId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	//本用户得分情况
	public List<Map<String, Object>> getScoreByUserId(Long groupId, Long uiId, MatchInfo matchInfo) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchInfo.getMiId());
		parp.put("groupId", groupId);
		parp.put("beforeHole", matchInfo.getMiZoneBeforeNine());
		parp.put("afterHole", matchInfo.getMiZoneAfterNine());
		parp.put("userId", uiId);
		parp.put("parkId", matchInfo.getMiParkId());
		StringBuilder sql = new StringBuilder();
		sql.append("select p.*,s.ms_id as ms_id,s.ms_user_id as ms_user_id,s.ms_user_name as ms_user_name," +
				"s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_is_up AS is_up," +
				"s.ms_rod_num AS rod_num," +
				"s.ms_push_rod_num AS push_num, s.ms_rod_cha as rod_cha from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		sql.append("and s.ms_match_id = :matchId and s.ms_group_id = :groupId and s.ms_user_id = :userId) ");
		sql.append("where 1=1 and (p.pp_name = :beforeHole or p.pp_name = :afterHole)  and p.pp_p_id = :parkId " +
				" ORDER BY p.pp_name ,p.pp_hole_num");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	//获取总计
	public List<Map<String, Object>> getTotalScoreWithUser(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.mugm_user_id as user_id, sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
		sql.append("FROM match_user_group_mapping AS m LEFT JOIN match_score AS s ");
		sql.append("on (m.mugm_user_id = s.ms_user_id and s.ms_match_id = m.mugm_match_id and s.ms_group_id = m.mugm_group_id ) where m.mugm_match_id = "+matchId+" AND m.mugm_group_id = "+groupId);
		sql.append(" GROUP BY m.mugm_id ORDER BY m.mugm_user_id");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	//本场地总杆数
	public Long getTotalRod(MatchInfo matchInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(t.ppHoleStandardRod) as totalRod FROM ParkPartition as t where t.ppPId = " + matchInfo.getMiParkId()+
				"AND ( t.ppName = '" +matchInfo.getMiZoneBeforeNine()+"' OR t.ppName = '"+matchInfo.getMiZoneAfterNine()+"')");
		return dao.createCountQuery(sql.toString());
	}

	//获取用户所在的球队id
	public List<Long> getTeamIds(List<Long> teamIds, Long userId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIds",teamIds);
		parp.put("userId",userId);
		StringBuilder sql = new StringBuilder();
		sql.append("select m.tumTeamId from TeamUserMapping as m where m.tumTeamId in(:teamIds) and m.tumUserId = :userId");
		return dao.createQuery(sql.toString(),parp);
	}

	//单练——我所在的组
	public MatchGroup getMyGroupById(Long miId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchGroup as t where t.mgMatchId = "+miId);
		List<MatchGroup> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	//获取参赛球队的队长
	public List<Long> getTeamCaptailByTeamIds(List<Long> teamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("select m.tumUserId from TeamUserMapping as m where m.tumTeamId in(:teamIdList) and m.tumUserType = 0");
		return dao.createQuery(sql.toString(),parp);
	}

	//通过比赛id，组id，用户id 获取mapping
	public MatchUserGroupMapping getMatchGroupMappingByUserId(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId ="+matchId+" and m.mugmGroupId = "+groupId+" and m.mugmUserId="+userId);
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
