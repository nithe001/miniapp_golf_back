package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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
//				"count(DISTINCT(j.mjwi_user_id)) AS userWatchCount," +
                "sum(j.mjwi_watch_num) AS userWatchCount," +
                "mugm.userCount,"+
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
        hql.append(" LEFT JOIN match_join_watch_info AS j ON (m.mi_id = j.mjwi_match_id AND j.mjwi_type = 0) ");
        hql.append(" LEFT JOIN park_info as p on m.mi_park_id = p.pi_id ");
        hql.append(" LEFT JOIN " +
                " (SELECT DISTINCT " +
                " mg.mugm_match_id," +
                " count(mg.mugm_user_id) AS userCount " +
                " FROM " +
                " match_user_group_mapping AS mg " +
                " WHERE " +
                " mg.mugm_is_auto_cap IS NULL " +
                " OR mg.mugm_is_auto_cap = 0 " +
                " GROUP BY mg.mugm_match_id) AS mugm " +
                "ON mugm.mugm_match_id = m.mi_id ");
        hql.append(" WHERE m.mi_is_valid = 1 ");

        //0：全部比赛（除了不可用的比赛） 1：我参加的比赛（包括我参加的正在报名的比赛）
        if((Integer)parp.get("type") == 1){
            //比分——我的比赛 包括我的单练
            hql.append(" AND m.mi_id IN (SELECT g.mugm_match_id FROM match_user_group_mapping AS g WHERE g.mugm_user_id = :userId) ");
        }else{
            //比分——全部比赛（除了不可用的比赛,包括选择公开的单练）
            hql.append(" AND ( m.mi_type = 1 OR (m.mi_type = 0 AND m.mi_match_open_type = 1 ))");
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
        hql.append(" order by abs(DATEDIFF(m.mi_match_time,now())) ");
		/*if(parp.get("type") != null && (Integer)parp.get("type") == 1){
			//比分下 我的比赛按时间排序，距离今天越近的排前面
		}else{
			hql.append(" ORDER BY IF(ISNULL(distance),1,0),distance,m.mi_is_end, abs(DATEDIFF(m.mi_match_time,now()))  ");
		}*/
        List<Map<String,Object>> list = dao.createSQLQuery(hql.toString(), parp,
                pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }
	/**
	 * 获取比赛列表 3:已报名的比赛 包括我创建的正在报名中的比赛 不管是否加入分组
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
		hql.append(" LEFT JOIN match_user_group_mapping AS mugm  ");
		hql.append(" on mugm.mugm_match_id = m.mi_id ");
		hql.append(" where m.mi_is_valid = 1 and m.mi_type = 1 AND m.mi_is_end = 0 and (m.mi_create_user_id = :userId or mugm.mugm_user_id = :userId) ");
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
	 * 获取比赛列表 :正在报名的比赛(全部正在报名的比赛)
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
				"m.mi_match_format_2 as mi_match_format_2, " +
                "m.mi_create_user_id as mi_create_user_id ");
		hql.append(" FROM match_info AS m, match_user_group_mapping AS mg ");
		hql.append(" WHERE m.mi_id = mg.mugm_match_id and m.mi_type = 1 and m.mi_is_valid = 1 and m.mi_is_end = 0 ");

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
//		报名下，可报名的比赛，按照时间距离今天越近的靠前排
		hql.append(" order by abs(DATEDIFF(t.mi_match_time,now())) ");
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
				"sum(j.mjwi_watch_num) AS userWatchCount," +
                //"count(j.mjwi_user_id) AS userWatchCount,"
				"count(mugm.mugm_user_id) AS userCount,"+
				"m.mi_match_format_1 as mi_match_format_1," +
				"m.mi_match_format_2 as mi_match_format_2 ");
		hql.append(" FROM match_info AS m ");
		hql.append(" LEFT JOIN match_join_watch_info AS j ON (m.mi_id = j.mjwi_match_id and m.mi_is_valid = 1 AND j.mjwi_type = 0) ");
		hql.append(" LEFT JOIN match_user_group_mapping AS mugm ON mugm.mugm_match_id = m.mi_id ");
/* 下面这段代码是为了用新的方法计算访问数量，在getMatchList哪里可以，这里不行，先不管了 nhq
        hql.append(" LEFT JOIN " +
                " (SELECT DISTINCT " +
                " mg.mugm_match_id," +
                " count(mg.mugm_user_id) AS userCount " +
                " FROM " +
                " match_user_group_mapping AS mg " +
                " WHERE " +
                " mg.mugm_is_auto_cap IS NULL " +
                " OR mg.mugm_is_auto_cap = 0 " +
                " GROUP BY mg.mugm_match_id) AS mugm " +
                "ON mugm.mugm_match_id = m.mi_id ");
                */
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
		hql.append(" order by m.mi_is_end, abs(DATEDIFF(m.mi_match_time,now())) ");
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
    public List<Map<String, Object>> getWatchUserListByMatchId(Long matchId) {
        StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName,u.uiNickName as uiNickName,j.mjwiWatchNum as watchNum");
        hql.append(" FROM MatchJoinWatchInfo AS j,UserInfo AS u WHERE 1=1 ");
        hql.append(" and j.mjwiUserId = u.uiId and j.mjwiType = 0 ");
        hql.append(" and j.mjwiMatchId = "+matchId);
		hql.append(" ORDER BY j.mjwiCreateTime DESC");
		List<Map<String, Object>> list = dao.createQuery(hql.toString(),Transformers.ALIAS_TO_ENTITY_MAP);
        return list;
    }

	/**
	 * 获取比赛赛长(不用管是否删除)
	 * @return
	 */
	public List<Map<String, Object>> getCaptainListByMatchId(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select u.ui_id as uiId,u.ui_headimg as uiHeadimg,u.ui_real_name as uiRealName,u.ui_nick_name as uiNickName,t.ti_abbrev as tiAbbrev ");
		hql.append(" from match_user_group_mapping as m LEFT JOIN user_info as u on m.mugm_user_id = u.ui_id ");
		hql.append(" LEFT JOIN team_info as t on m.mugm_team_id = t.ti_id ");
		hql.append(" where m.mugm_user_type = 0 ");
		hql.append(" and m.mugm_match_id = "+matchId);
		hql.append(" ORDER BY m.mugm_team_id,m.mugm_create_time DESC");
		return dao.createSQLQuery(hql.toString(),Transformers.ALIAS_TO_ENTITY_MAP);
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
		hql.append("select u.ui_id as uiId,u.ui_headimg as uiHeadimg," +
                "u.ui_real_name as uiRealName,u.ui_nick_name as uiNickName," +
                "t.ti_id as tiId,t.ti_abbrev as tiAbbrev ");
		hql.append(" from match_user_group_mapping as m LEFT JOIN user_info as u on m.mugm_user_id = u.ui_id ");
		hql.append(" LEFT JOIN team_info as t on m.mugm_team_id = t.ti_id ");
		hql.append(" where m.mugm_group_id = :groupId ");
		hql.append(" and m.mugm_match_id = :matchId ");
		hql.append(" and m.mugm_is_del != 1 ");
		hql.append(" ORDER BY m.mugm_team_id,m.mugm_update_time ");

		return dao.createSQLQuery(hql.toString(), parp,0,4,Transformers.ALIAS_TO_ENTITY_MAP);
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
	 * 获取我是队长的球队
	 */
	public List<Long> getMyTeamInfoList(List<Long> teamIdList,Long userId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("userId",userId);
		parp.put("teamIdList",teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("select tum.tumTeamId ");
		sql.append("FROM TeamUserMapping AS tum,TeamInfo as t ");
		sql.append("WHERE tum.tumUserId = :userId and tum.tumTeamId in (:teamIdList) ");
		sql.append("and tum.tumUserType = 0 ");
		sql.append("and tum.tumTeamId = t.tiId ");
		return dao.createQuery(sql.toString(),parp);
	}

    /**
     * 获取我加入的球队（不管是不是申请入队）
     */
    public List<Long> getMyJoinTeamInfoList(List<Long> teamIdList,Long userId) {
        Map<String, Object> parp = new HashMap<>();
        parp.put("userId",userId);
        parp.put("teamIdList",teamIdList);
        StringBuilder sql = new StringBuilder();
        sql.append("select tum.tumTeamId ");
        sql.append("FROM TeamUserMapping AS tum,TeamInfo as t ");
        sql.append("WHERE tum.tumUserId = :userId and tum.tumTeamId in (:teamIdList) ");
        sql.append("and tum.tumTeamId = t.tiId ");
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
        if(parp.get("myLat") != null && parp.get("myLng") != null ){
            hql.append(" ORDER BY IF(ISNULL(distance),1,0),distance");
        }else{
            hql.append(" ORDER BY p.pi_id ");
        }
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
	public List<Map<String,Object>> getMatchGroupList(Long matchId,Integer matchState) {
		StringBuilder sql = new StringBuilder();
		if(matchState == 0){
			//报名中，显示所有的分组
			sql.append("select t.mgMatchId as matchId,t.mgId as groupId,t.mgGroupName+0 as groupName,t.mgGroupNotice as groupNotice,t.mgIsGuest as groupIsGuest FROM MatchGroup AS t WHERE t.mgMatchId = "+matchId);
		}else{
			//比赛中、结束 只显示有人的分组
			sql.append("select t.mgMatchId as matchId,t.mgId as groupId,t.mgGroupName+0 as groupName ,t.mgGroupNotice as groupNotice,t.mgIsGuest as groupIsGuest FROM MatchGroup AS t,MatchUserGroupMapping as m WHERE t.mgMatchId = m.mugmMatchId and t.mgId = m.mugmGroupId ");
			sql.append("AND t.mgMatchId = "+matchId);
			sql.append("  GROUP BY t.mgId ");
		}
		sql.append(" order BY groupName ");
		return dao.createQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取比赛最大组
	 * @return
	 */
	public Integer getMaxGroupByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT MAX(g.mgGroupName+0) FROM MatchGroup as g where g.mgMatchId = "+matchId);
		List<Integer> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 比赛详情——赛长获取待分组人员不进行任何筛选，直接取所有待分组的
	 * @return
	 */
	public List<Map<String, Object>> getUserListByGroupId(Long matchId,String keyword, Long groupId,Integer isDel) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		if(StringUtils.isNotEmpty(keyword)){
			parp.put("keyword","%"+keyword.trim()+"%");
		}
		parp.put("groupId",groupId);
		parp.put("isDel",isDel);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, team.ti_abbrev AS tiAbbrev FROM ");
		sql.append("(SELECT g.mugm_id as mappingId," +
						"u.ui_id AS uiId, " +
						"u.ui_real_name AS uiRealName, " +
						"u.ui_nick_name AS uiNickName, " +
						"u.ui_headimg AS uiHeadimg, " +
						"g.mugm_team_id AS teamId " +
						"FROM " +
						"match_user_group_mapping AS g, " +
						"user_info AS u " +
						"WHERE " +
						"g.mugm_user_id = u.ui_id " +
						"AND g.mugm_match_id = :matchId ");
		if(isDel == 0 && groupId != null){
			sql.append(" AND g.mugm_group_id = :groupId ");
		}
		sql.append(" AND g.mugm_is_del = :isDel ) AS t ");
		sql.append("LEFT JOIN team_info AS team ON t.teamId = team.ti_id ");
		if(parp.get("keyword") != null){
			sql.append("where t.uiRealName like :keyword or t.uiNickName like :keyword ");
		}
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
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
	public List<Map<String, Object>> getUserListByScoreCard(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select mugm.mugm_user_id AS uiId, " +
				"u.ui_real_name AS uiRealName, " +
				"u.ui_nick_name AS uiNickName, " +
				"u.ui_headimg AS uiHeadimg, " +
				"mugm.mugm_team_id AS team_id, " +
				"mugm.mugm_group_id AS group_id, " +
				"sum(s.ms_rod_num) AS sumRodNum, " +
				"sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("from match_user_group_mapping as mugm LEFT JOIN match_score AS s on ");
		sql.append("(mugm.mugm_match_id = s.ms_match_id and mugm.mugm_group_id = s.ms_group_id) ");
		sql.append("LEFT JOIN user_info AS u ON mugm.mugm_user_id = u.ui_id ");
		sql.append("where mugm.mugm_match_id = "+matchId);
		sql.append(" and mugm.mugm_is_del != 1 ");
		if(groupId != null){
			sql.append(" and mugm.mugm_group_id = "+groupId);
		}
		sql.append(" GROUP BY mugm.mugm_team_id,mugm.mugm_user_id ORDER BY mugm.mugm_team_id,mugm.mugm_update_time");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本组用户 和 用户的总杆差
	 * 按照球队分组并按照参赛范围的球队顺序来排序
	 */
	public List<Map<String, Object>> getUserListById(Long matchId, Long groupId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select " +
				" u.ui_nick_name AS uiNickName," +
				" u.ui_real_name AS uiRealName," +
				" u.ui_headimg AS uiHeadimg,score.* from (" );
		sql.append("select m.mugm_user_id AS uiId,m.mugm_team_id AS team_id,m.mugm_group_id AS group_id," +
					"(IFNULL(sum(s.ms_rod_num),0)) AS sumRodNum,sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("FROM match_user_group_mapping as m LEFT JOIN match_score AS s " +
			//	"on (m.mugm_match_id = s.ms_match_id and m.mugm_user_id = s.ms_user_id and s.ms_type = 0) "); nhq
				"on (m.mugm_match_id = s.ms_match_id and m.mugm_user_id = s.ms_user_id and m.mugm_team_id=s.ms_team_id and s.ms_type = 0) ");
		sql.append("where m.mugm_match_id = "+matchId );
		if(groupId != null){
			sql.append(" and m.mugm_group_id = "+groupId);
		}
		if(teamId != null){
			sql.append(" and m.mugm_team_id = "+teamId);
		}
		sql.append(" and m.mugm_is_del != 1 ");
		sql.append(" GROUP BY m.mugm_user_id " );
		sql.append(" )score LEFT JOIN user_info AS u ON score.uiId = u.ui_id ");
		sql.append("ORDER BY score.sumRodNum !=0 desc,score.sumRodNum ");
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
     * 获取总计——多队双人比杆，按照球队来分组
     */
    public List<Map<String, Object>> getTotalScoreWithUserByGroupTeam(Long matchId, Long groupId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT g.mugm_user_id AS user_id,g.mugm_team_id AS team_id, sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
        sql.append("FROM match_user_group_mapping as g LEFT JOIN match_score AS s ");
        sql.append("ON (s.ms_match_id = g.mugm_match_id " +
                "AND s.ms_user_id = g.mugm_user_id and g.mugm_team_id = s.ms_team_id ) ");
        sql.append("where g.mugm_match_id = "+matchId+" AND g.mugm_group_id = "+groupId);
        sql.append(" AND g.mugm_is_del != 1 ");
        sql.append(" GROUP BY g.mugm_team_id,g.mugm_user_id ORDER BY g.mugm_team_id,g.mugm_user_id");
        return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
    }


	/**
	 * 按组获取总计
	 */
	public List<Map<String, Object>> getTotalScoreWithUserByGroup(Long matchId, Long groupId) {
	        MatchInfo matchInfo = get(MatchInfo.class,matchId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.mugm_user_id AS user_id,g.mugm_team_id AS team_id, sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
		sql.append("FROM match_user_group_mapping as g LEFT JOIN match_score AS s ");
        sql.append("ON (s.ms_match_id = g.mugm_match_id " +
                "AND s.ms_user_id = g.mugm_user_id ");
        if(matchInfo.getMiJoinOpenType() != 1){
            sql.append(" and g.mugm_team_id = s.ms_team_id  ");
        }
        sql.append(" ) ");
        sql.append("where g.mugm_match_id = "+matchId+" AND g.mugm_group_id = "+groupId);
		sql.append(" AND g.mugm_is_del != 1 ");
		sql.append(" GROUP BY g.mugm_user_id ORDER BY g.mugm_team_id,g.mugm_user_id");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}


	/** 获取用户总计 nhq
	 */
    public List<Map<String, Object>> getTotalScoreWithUser(Long matchId, Long userId) {
        MatchInfo matchInfo = get(MatchInfo.class,matchId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.ms_user_id AS user_id,s.ms_team_id AS team_id,sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
        sql.append("FROM match_score AS s ");
        sql.append(" where s.ms_match_id = "+matchId+" AND s.ms_user_id = "+userId);

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
	 * 获取我在本比赛中的
	 * @return
	 */
	public MatchUserGroupMapping getMatchGroupMappingByMyUserId(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId = "+matchId+" and m.mugmUserId= "+userId);
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 1、更新比赛信息—获取比赛详情——获取我的参赛代表球队
	 * 2、保存或更新计分数据——获取我的参赛代表队
	 * 3、点击围观用户并邀请其记分——查询我所在的比赛分组
	 * 4、获取用户在每个球洞的得分情况——查询我所在的比赛分组
	 * 5、查看用户基本信息——查询我所在的比赛分组——通过我的分组查询被查看用户是否已经被邀请记分
	 * @return
	 */
	public MatchUserGroupMapping getMatchGroupMappingByUserId(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId = "+matchId+" and m.mugmUserId= "+userId);
		sql.append(" and m.mugmIsDel = 0 ");
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
	 * 查询用户是否已报名
	 * @return
	 */
	public MatchUserGroupMapping getIsInMatch(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId = "+matchId+" and m.mugmUserId= "+userId);
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 查询用户是否在临时分组中 通过比赛id，组id，用户id 获取mapping
	 * @return
	 */
	public List<MatchUserGroupMapping> getIsInMatchGroupMappingByUserId(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m where m.mugmMatchId = "+matchId+" and m.mugmUserId= "+userId);
		return dao.createQuery(sql.toString());
	}

	/**
	 * 查询用户是有比赛成绩的记录 通过比赛id，用户id 获取mapping
	 * @return
	 */
	public List<MatchScore> getIsInMatchByUserId(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchScore as m where m.msMatchId = "+matchId+" and m.msUserId= "+userId);
		return dao.createQuery(sql.toString());
	}

	/**
	 * 查询是否是参赛人员
	 * @return
	 */
	public Long getIsContestants(Long userId, Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchUserGroupMapping as m " +
                "where m.mugmMatchId ="+matchId+" and m.mugmUserId="+userId);
	    return dao.createCountQuery("SELECT COUNT(*) "+sql.toString());
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
		sql.append(" and g.mugmIsDel != 1 ");
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 成绩提交 球队确认  获取本队球友的总得分
	 * @param teamType 0：参赛队，1：上报队
	 */
	public List<Map<String, Object>> getSumScoreListByMatchIdTeamId(Long matchId, Long teamId, List<Long> userIdList, Integer teamType) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		parp.put("userIdList",userIdList);
		parp.put("teamType",teamType);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " +
				"s.ms_team_id AS teamId, " +
				"s.ms_user_id AS userId, " +
				"s.ms_user_name AS userName, " +
				"SUM(s.ms_rod_num) as sumRodNum, " +
				"SUM(s.ms_push_rod_num) as sumPushNum ");
		sql.append("FROM match_score AS s ");
		sql.append("WHERE s.ms_match_id = :matchId AND s.ms_team_id = :teamId ");
		if(userIdList != null && userIdList.size()>0){
			sql.append(" and s.ms_user_id in (:userIdList) ");
		}
		/* 都是取参赛球队的成绩，所以下面两句没用了 nhq
		if(teamType != null){
			sql.append(" and s.ms_type = :teamType ");
		}
		*/
		sql.append(" GROUP BY s.ms_user_id ORDER BY sumRodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本场比赛所有参赛人员的排名，按照杆数排名
	 * @param teamType 0：参赛队，1：上报队
	 */
	public List<Map<String, Object>> getRankingListByMatchId(Long matchId,Long teamId, List<Long> userIdList, Integer teamType) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		parp.put("userIdList",userIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT s.ms_team_id AS teamId, s.ms_user_id AS userId, s.ms_user_name AS userName, SUM(s.ms_rod_num) AS sumRodNum," +
					"SUM(s.ms_push_rod_num) AS sumPushNum " +
				"FROM match_score AS s WHERE s.ms_match_id = :matchId and s.ms_team_id = :teamId ");
		if(userIdList != null && userIdList.size()>0){
			sql.append(" and s.ms_user_id in (:userIdList) ");
		}
		/* 都是取参赛球队的成绩，所以下面两句没用了 nhq
		if(teamType != null){
			sql.append(" and s.ms_type = "+teamType);
		}
		*/
		sql.append(" GROUP BY s.ms_user_id ORDER BY sumRodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取用户前后半场的得分情况
	 * type:区分前后半场
	 */
	public List<Map<String, Object>> getBeforeAfterScoreByUserId(Long uiId, MatchInfo matchInfo,Integer type,Long teamId) {
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
		parp.put("teamId", teamId);

		StringBuilder sql = new StringBuilder();
		sql.append("select p.*,s.ms_id as ms_id,s.ms_user_id as ms_user_id,s.ms_user_name as ms_user_name," +
				"s.ms_score AS score," +
				"s.ms_hole_name AS hole_name," +
				"s.ms_hole_num AS hole_num," +
				"s.ms_hole_standard_rod AS hole_standard_rod," +
				"s.ms_is_up AS is_up," +
				"s.ms_rod_num AS rod_num," +
				"s.ms_push_rod_num AS push_num, s.ms_rod_cha as rod_cha,0 as before_after,s.ms_is_par as is_par," +
				"s.ms_is_bird as is_bird,s.ms_is_eagle as is_eagle,s.ms_is_on as is_on " +
				"from park_partition as p LEFT JOIN match_score as s on (" +
				"s.ms_hole_name = p.pp_name and s.ms_hole_num = p.pp_hole_num ");
		if(teamId != null){
			sql.append(" and s.ms_team_id = :teamId  ");
		}
		sql.append(" and s.ms_user_id = :userId  ");
		sql.append(" and s.ms_type = 0 ");
		sql.append(" and s.ms_match_id = :matchId ");
		sql.append(" and s.ms_before_after = :type  ");
		sql.append(" ) ");
		sql.append(" where p.pp_name = :holeName and p.pp_p_id = :parkId  ");
		sql.append(" ORDER BY p.pp_name, p.pp_hole_num");
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
		sql.append("SELECT count(*) FROM TeamUserMapping as m where m.tumUserId = :userId and m.tumTeamId in (:teamIdList) ");
		return dao.createCountQuery(sql.toString(),parp);
	}

	/**
	 * 是否参赛球队的队员
	 */
	public List<Long> getIsJoinTeam(Long userId, List<Long> teamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("userId", userId);
		parp.put("teamIdList", teamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.tumTeamId FROM TeamUserMapping as m where m.tumUserId = :userId and m.tumTeamId in (:teamIdList) ");
		return dao.createQuery(sql.toString(),parp);
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
	 * 获取本比赛（本组）的本球队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long matchId, Long teamId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.ui_id as uiId, u.ui_real_name as uiRealName, u.ui_nick_name as uiNickName," +
                " u.ui_headimg as uiHeadimg, " +
				"m.mugm_team_id AS team_id  ");
		sql.append("FROM match_user_group_mapping as m,user_info as u ");
		sql.append("where m.mugm_user_id = u.ui_id and m.mugm_match_id = "+matchId);
		if(teamId != null){
			sql.append(" and m.mugm_team_id = "+teamId);
		}
        sql.append(" and m.mugm_group_id = "+groupId);
        sql.append(" and m.mugm_is_del = 0 ");
		sql.append(" order by m.mugm_user_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本比赛本球队所有用户，(除了自动设置的赛长,这个条件去掉 nhq)
	 * @return
	 */
	public List<Map<String, Object>> getUserListByMatchTeamIdWithOutTeamCap(Long matchId, Long teamId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
//		parp.put("capUserList",capUserList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, team.ti_name AS tiName,team.ti_abbrev AS tiAbbrev,team.ti_id as tiId FROM ");
		sql.append("(SELECT g.mugm_id as mappingId," +
				"u.ui_id AS uiId, " +
				"u.ui_real_name AS uiRealName, " +
				"u.ui_nick_name AS uiNickName, " +
				"u.ui_headimg AS uiHeadimg, " +
				"g.mugm_team_id AS teamId " +
				"FROM " +
				"match_user_group_mapping AS g, " +
				"user_info AS u " +
				"WHERE " +
				"g.mugm_user_id = u.ui_id " +
				"AND g.mugm_match_id = :matchId and g.mugm_team_id = :teamId ");
		//sql.append(" and (g.mugm_is_auto_cap is null or g.mugm_is_auto_cap = 0) ");
		sql.append(" ) AS t ");
		sql.append("LEFT JOIN team_info AS team ON t.teamId = team.ti_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取本比赛本球队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getUserListByMatchTeamId(Long matchId, Long teamId, String keyword) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		if(StringUtils.isNotEmpty(keyword)){
			parp.put("keyword","%"+keyword+"%");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, team.ti_name AS tiName,team.ti_abbrev AS tiAbbrev,team.ti_id as tiId FROM ");
		sql.append("(SELECT g.mugm_id as mappingId," +
				"u.ui_id AS uiId, " +
				"u.ui_real_name AS uiRealName, " +
				"u.ui_nick_name AS uiNickName, " +
				"u.ui_headimg AS uiHeadimg, " +
				"g.mugm_team_id AS teamId " +
				"FROM " +
				"match_user_group_mapping AS g, " +
				"user_info AS u " +
				"WHERE " +
				"g.mugm_user_id = u.ui_id " +
				//"AND g.mugm_match_id = :matchId and g.mugm_team_id = :teamId and (g.mugm_is_auto_cap = 0 or g.mugm_is_auto_cap is null) ");
                "AND g.mugm_match_id = :matchId and g.mugm_team_id = :teamId");
		if(parp.get("keyword") != null){
			sql.append(" and (u.ui_real_name like :keyword or u.ui_nick_name like :keyword) ");
		}
		sql.append(" ) AS t ");
		sql.append("LEFT JOIN team_info AS team ON t.teamId = team.ti_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
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
     * 查询浏览数量
     * @return
     */
    public Long getWatchNum(Long userId, Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT mjwiWatchNum FROM MatchJoinWatchInfo as j ");
        sql.append("where j.mjwiMatchId = "+matchId);
        sql.append(" and j.mjwiUserId = "+userId);
        return dao.createCountQuery(sql.toString());
    }
    /**
     * 更改浏览次数
     */
    public void updateWatchNum(Long userId,Long matchId,Long watchNum) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE MatchJoinWatchInfo AS m set m.mjwiWatchNum =" + watchNum);
        sql.append(" WHERE mjwiMatchId = " + matchId);
        sql.append(" and m.mjwiUserId= " + userId);

        dao.executeHql(sql.toString());
    }

	/**
	 * 普通用户——查询自己是否已经报名
	 * @return
	 */
	public Long getIsApplyNormalUser(Long userId, Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM MatchUserGroupMapping as g ");
		sql.append("where g.mugmMatchId = "+matchId);
		sql.append(" and g.mugmUserId = "+userId);
		sql.append(" and g.mugmGroupId = "+groupId);
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 是否已经有提交成绩的计算配置
	 * type:是上报球队的队长，获取上报球队的配置
	 * 增加了上报球队的参数
	 * @return
	 */
	public IntegralConfig getSubmitScoreConfig(Long matchId, Long teamId, Long reportteamId, Integer scoreType) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM IntegralConfig as c ");
		sql.append("where c.icMatchId = "+matchId);
        sql.append(" and c.icScoreType = "+scoreType);
		sql.append(" and c.icTeamId = "+teamId);
		sql.append(" and c.icReportTeamId = "+reportteamId);
		//sql.append(" and c.icCreateUserId = "+captainUserId); nhq
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
	public void updateMatchScoreById(Long matchId, Long teamId, List<Long> userList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		parp.put("userList",userList);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s SET s.msIsTeamSubmit = 1 ");
		sql.append(" WHERE s.msMatchId = :matchId ");
		sql.append(" AND s.msTeamId = :teamId ");
		if(userList != null && userList.size()>0){
			sql.append(" AND s.msUserId in(:userList)");
		}
		dao.executeHql(sql.toString(),parp);
	}

	/**
	 * 撤销成绩提交，将该组的得分成绩标为未确认
	 * 由于把上报的成绩管理放到了teamuserpoint 中，所以撤销上报时这个函数没用了 nhq
	 * @return

	public void cancelMatchScoreById(Long matchId, Long teamId,List<Long> userIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		parp.put("userIdList",userIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s SET s.msIsTeamSubmit = 0 ");
		sql.append(" WHERE s.msMatchId = :matchId ");
		sql.append(" AND s.msTeamId = :teamId ");
		if(userIdList != null && userIdList.size()>0){
			sql.append(" AND s.msUserId in(:userIdList) ");
		}
		dao.executeHql(sql.toString(),parp);
	}
*/
	/**
	 * 比洞赛——获取本次比赛中的分组和每组用户的总分
	 * @return
	 */
	public List<Map<String, Object>> getMatchHoleScoreList(Long matchId,Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select s.msTeamId as teamId,s.msGroupId as groupId,s.msGroupName as groupName," +
                "s.msUserId as userId,sum(s.msRodNum) as sumRod " +
                "from MatchScore as s where s.msMatchId = "+matchId+" and s.msTeamId = "+teamId+" GROUP BY s.msUserId ");
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
		hql.append("SELECT ROUND(AVG(t.sum),1) as avg FROM ( ");
		hql.append("SELECT sum(s.ms_rod_num) AS sum FROM match_score AS s, match_info as m" +
				" WHERE m.mi_type = 1 AND m.mi_is_end = 2 " +
				" and m.mi_id = s.ms_match_id " +
				" and s.ms_user_id = "+userId +
				" and s.ms_rod_num != 0 "+
				" and s.ms_type = 0 "+
				" GROUP BY s.ms_match_id HAVING count(s.ms_id)=18 ORDER BY sum limit 0,10 ");
		hql.append(" ) as t ");
		return dao.createSQLQuery(hql.toString());
	}



	/**
	 * 获取我参加的所有比赛所在的球场 和总杆差 (比赛id，球场id，球场名称,前半场名称，后半场名称)
	 */
	public List<Map<String, Object>> getTotalChaListByUserId(Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT s.ms_match_id as miId,m.mi_park_id as parkId,m.mi_park_name as parkName," +
				"m.mi_zone_before_nine as beforeNine , m.mi_zone_after_nine as afterNine,sum(s.ms_rod_cha) as sumRodCha,m.mi_match_time as time ");
		sql.append("FROM match_score AS s,match_info as m " +
				"WHERE s.ms_user_id = "+userId+ " and s.ms_match_id = m.mi_id and m.mi_is_valid = 1 GROUP BY s.ms_match_id order by m.mi_match_time desc ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取每组球队个数 或 指定组 球队个数
	 */
	public List<Map<String,Object>> getTeamCountByMatchId(Long matchId,Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select g.mugm_group_id AS groupId,g.mugm_group_name as groupName,count(DISTINCT(g.mugm_team_id)) AS count " +
				"from match_user_group_mapping as g " +
				"where g.mugm_match_id =" +matchId+
                " and g.mugm_group_name is not null " +
				" and g.mugm_is_del !=1 ");
		if(groupId != null){
			sql.append(" and g.mugm_group_id = "+groupId);
		}
		sql.append(" GROUP BY g.mugm_group_id ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取每组人数
	 */
    public List<Map<String,Object>> getUserCountByMatchId(Long matchId,Long groupId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select g.mugm_group_id AS groupId,g.mugm_group_name as groupName,g.mugm_team_id as teamId,count(g.mugm_user_id) as count " +
				"from match_user_group_mapping as g " +
				"where g.mugm_match_id =" +matchId+
				" and g.mugm_is_del !=1 ");
        if(groupId != null){
			sql.append(" and g.mugm_group_id = "+groupId);
		}
		sql.append(" GROUP BY g.mugm_group_id ,g.mugm_team_id");
        return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
    }

	/**
	 * 公开赛、队内赛、获取每组人数
	 */
	public List<Map<String, Object>> getUserCountWithEveGroupByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select g.mugm_group_id AS groupId," +
				" g.mugm_team_id as teamId," +
				" g.mugm_group_name AS groupName," +
				" count(g.mugm_user_id) AS count " +
				"from match_user_group_mapping as g " +
				"where g.mugm_match_id =" +matchId+
				" and g.mugm_is_del !=1 ");
		sql.append(" GROUP BY g.mugm_group_id ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 删除比赛 置为不可用
	 */
	public void updateMatchState(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchInfo AS m set m.miIsValid = 0");
		sql.append(" WHERE m.miId = " +matchId);
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
	 * 查询是否有我生成的二维码
	 * @return
	 */
	public MatchUserQrcode getHasMyQRCode(Long matchId, Long groupId, Long userId, Integer type) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchUserQrcode as t WHERE t.muqMatchId = "+matchId+" and t.muqGroupId = "+groupId);
		hql.append(" and t.muqMatchUserId = "+userId);
		hql.append(" and t.muqType = "+type);
		List<MatchUserQrcode> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询是否有我扫描的二维码
	 * @return
	 */
	public MatchScoreUserMapping getHasMyScanQRCode(Long matchId, Long groupId, Long userId,Integer type) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchScoreUserMapping as t WHERE t.msumMatchId = "+matchId+" and t.msumGroupId = "+groupId);
		hql.append(" and t.msumScoreUserId = "+userId);
		hql.append(" and t.msumType = "+type);
		List<MatchScoreUserMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询我是否已经被邀请记分
	 * @return
	 */
	public MatchScoreUserMapping getMatchScoreUserMapping(Long matchId, Long groupId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchScoreUserMapping as t WHERE t.msumMatchId = "+matchId+" and t.msumGroupId = "+groupId);
		hql.append(" and t.msumScoreUserId = "+userId);
		List<MatchScoreUserMapping> list = dao.createQuery(hql.toString());
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
	 * 获取本参赛队的队长
	 * @return
	 */
	public List<Long> getCaptainUserListByJoinTeamId(Long teamId,Long myUserId) {
		Map<String, Object> parp = new HashMap<>();
        parp.put("teamId",teamId);
		parp.put("myUserId",myUserId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.tumUserId ");
		sql.append(" FROM TeamUserMapping as t ");
		sql.append(" where t.tumTeamId = :teamId ");
		sql.append(" and t.tumUserType = 0 ");
		return dao.createQuery(sql.toString(), parp);
	}

	/**
	 * 获取我所在的某个参赛队的球友
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, String keyword) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("teamId",teamId);
		if(StringUtils.isNotEmpty(keyword) && !keyword.equals("undefined") && !keyword.equals("null")){
			parp.put("keyword","%"+keyword+"%");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT u.uiId as uiId, u.uiRealName as uiRealName,u.uiNickName as uiNickName, " +
				"u.uiHeadimg as uiHeadimg,t.tiAbbrev as tiAbbrev,t.tiId as tiId ");
		sql.append("FROM TeamUserMapping as m,UserInfo as u,TeamInfo as t ");
		sql.append(" where m.tumTeamId = t.tiId ");
		sql.append(" and m.tumUserId = u.uiId ");
		sql.append(" and m.tumTeamId = :teamId ");
		if(parp.get("keyword") != null){
			sql.append(" and (u.uiRealName like :keyword or u.uiNickName like :keyword)");
		}
		sql.append(" order by t.tiId ");
		List<Map<String, Object>> list = dao.createQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}



	/**
	 * 更新比赛——如果参赛球队有改变，删除比赛mapping表该球队的信息
	 * @return
	 */
	public void delMatchUserMappingByTeamId(Long matchId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MatchUserGroupMapping as t ");
		sql.append("WHERE t.mugmMatchId = :matchId ");
		dao.executeHql(sql.toString(),parp);
	}

	/**
	 * 获取我加入的球队id
	 * @return
	 */
	public List<Long> getMyJoinTeamList(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select tum.tumTeamId from TeamUserMapping as tum " +
                "where tum.tumUserType != 2 and tum.tumUserId = "+userId);
		List<Long> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list;
		}
		//这里返回空list是为了从小程序分享进来的用户，如果没有任何球队，在上一步处理中，从参赛队中选代表队
		return new ArrayList<>();
	}


	/**
	 * 赛长操作报名——获取全部已报名的球友
	 * @return
	 */
	public List<Map<String, Object>> getApplyUserIdList(Long matchId, String keyword) {
        Map<String, Object> parp = new HashMap<>();
        parp.put("matchId",matchId);
        if(StringUtils.isNotEmpty(keyword)){
            parp.put("keyword","%"+keyword.trim()+"%");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.*, team.ti_name AS tiName,team.ti_abbrev AS tiAbbrev,team.ti_id as tiId FROM ");
        sql.append("(SELECT g.mugm_id as mappingId," +
                "u.ui_id AS uiId, " +
                "u.ui_real_name AS uiRealName, " +
                "u.ui_nick_name AS uiNickName, " +
                "u.ui_headimg AS uiHeadimg, " +
                "g.mugm_team_id AS teamId " +
                "FROM " +
                "match_user_group_mapping AS g, " +
                "user_info AS u " +
                "WHERE " +
                "g.mugm_user_id = u.ui_id " +
                "AND g.mugm_match_id = :matchId ");
        sql.append(" ) AS t ");
        sql.append("LEFT JOIN team_info AS team ON t.teamId = team.ti_id ");
        if(parp.get("keyword") != null){
            sql.append("where t.uiRealName like :keyword or t.uiNickName like :keyword ");
        }
		sql.append(" order by team.ti_id ");
        List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
        return list;
	}

    /**
     * 比赛详情——赛长获取待分组人员不进行任何筛选，直接取所有待分组的
     * @return
     */
    public List<Map<String, Object>> getWaitGroupUserList(Long matchId, Long teamId, String keyword) {
        Map<String, Object> parp = new HashMap<>();
        parp.put("matchId",matchId);
		parp.put("teamId",teamId);
        if(StringUtils.isNotEmpty(keyword)){
            parp.put("keyword","%"+keyword.trim()+"%");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.*, team.ti_abbrev AS tiAbbrev,team.ti_id as tiId FROM ");
        sql.append("(SELECT g.mugm_id as mappingId," +
                "u.ui_id AS uiId, " +
                "u.ui_real_name AS uiRealName, " +
                "u.ui_nick_name AS uiNickName, " +
                "u.ui_headimg AS uiHeadimg, " +
                "g.mugm_team_id AS teamId " +
                "FROM " +
                "match_user_group_mapping AS g, " +
                "user_info AS u " +
                "WHERE g.mugm_user_id = u.ui_id " +
                "AND g.mugm_match_id = :matchId ");
		sql.append("and (g.mugm_is_auto_cap = 0 or g.mugm_is_auto_cap is null) ");
        if(teamId != null){
			sql.append(" AND g.mugm_team_id = :teamId ");
		}
        sql.append(" AND g.mugm_is_del = 1 ) AS t ");
        sql.append("LEFT JOIN team_info AS team ON t.teamId = team.ti_id ");
        if(parp.get("keyword") != null){
            sql.append("where t.uiRealName like :keyword or t.uiNickName like :keyword ");
        }
        List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
        return list;
    }

	/**
	 * 单队比赛，取前n名的成绩 (参赛人数 显示的是各队实际的参赛人数)
	 * 平均杆：前N的总杆数除以n
	 * @return
	 */
	public List<Map<String, Object>> getMatchRodTotalScoreByMingci(Long matchId,Long teamId, Integer mingci) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("teamId",teamId);
		parp.put("mingci",mingci);
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT round(SUM(t.sumRod)/:mingci, 2) AS avgRodNum,SUM(t.sumRod) AS sumRodNum ");
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
					"WHERE mm.mugm_match_id =:matchId ");
		if(teamId != null){
			hql.append(" and mm.mugm_team_id = :teamId ");
		}
		hql.append(" GROUP BY mm.mugm_user_id " +
					"ORDER BY sum(s.ms_rod_num) != 0 desc,sum(s.ms_rod_num) " +
					"LIMIT 0,:mingci");
		hql.append(") as t");
		return dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
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

	/**
	 * 获取本比赛的报名（参赛）用户人数 已分组的
	 */
	public Long getMatchUserCountAlreadyGroup(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(t.mugmUserId) FROM MatchUserGroupMapping as t ");
		sql.append("where t.mugmMatchId ="+matchId+" and t.mugmIsDel = 0 ");
		//sql.append("and (t.mugmIsAutoCap is null or t.mugmIsAutoCap = 0) ");
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 获取本比赛的报名（参赛）用户人数
	 */
	public Long getAllMatchApplyUserCount(Long matchId,Long userId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("userId",userId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(t.mugmUserId) FROM MatchUserGroupMapping as t ");
		sql.append("where t.mugmMatchId = :matchId ");
		//sql.append("and (t.mugmIsAutoCap is null or t.mugmIsAutoCap = 0) ");
		return dao.createCountQuery(sql.toString(),parp);
	}

	/**
	 * 队际赛：单人赛：获取所有参赛队伍中，参赛人数最少的数
	 */
	public List<Map<String,Object>> getUserCountByMatchUserMappingTeamId(Long matchId, List<Long> joinTeamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("joinTeamIdList",joinTeamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(mugm.mugm_user_id) as userCount FROM match_user_group_mapping AS mugm ");
		sql.append("where mugm.mugm_match_id =:matchId ");
		sql.append("and mugm.mugm_team_id in(:joinTeamIdList) ");
		sql.append("and mugm.mugm_is_del !=1 ");
		sql.append("GROUP BY mugm.mugm_team_id ");
		sql.append("ORDER BY userCount ");
		return dao.createSQLQuery(sql.toString(),parp , Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 队际赛：双人赛：获取最少的组数，
	 */
	public List<Map<String,Object>> getUserCountByMatchGroupTeamId(Long matchId, List<Long> joinTeamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("joinTeamIdList",joinTeamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(distinct mugm.mugm_group_id) as groupCount FROM match_user_group_mapping AS mugm ");
		sql.append("where mugm.mugm_match_id =:matchId ");
		sql.append("and mugm.mugm_team_id in(:joinTeamIdList) ");
		sql.append("and mugm.mugm_is_del !=1 ");
		sql.append("GROUP BY mugm.mugm_team_id ");
		sql.append("ORDER BY groupCount ");
		return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 删除比赛分组，除了我所在的组
	 */
	public void delMatchGroup(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from MatchGroup as g ");
		sql.append("where g.mgMatchId = "+matchId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 获取本比赛每个队的参赛人数 显示的是各队实际的参赛人数, 也就是mugm表中 is_del为0的记录，为1的人会显示在已报名未分组人员中
     * */
	public List<Map<String, Object>> getJoinMatchUserList(Long matchId,List<Long> joinTeamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("joinTeamIdList",joinTeamIdList);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.ti_id as teamId, t.ti_name as teamName, t.ti_abbrev as teamAbbrev,u.matchId, u.isDel, count(u.userId) as userCount FROM team_info AS t ");
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT " +
					"m.mugm_match_id as matchId, " +
					"m.mugm_team_id as tId, " +
					"m.mugm_is_del as isDel, " +
					"m.mugm_user_id as userId " +
					"FROM match_user_group_mapping AS m  " +
					"where m.mugm_team_id IN (:joinTeamIdList) " +
					"AND m.mugm_match_id = :matchId " +
					"GROUP BY m.mugm_team_id,m.mugm_user_id ");
		sql.append(") AS u ON ( t.ti_id = u.tId AND u.isDel = 0 ) " +
				"WHERE t.ti_id IN (:joinTeamIdList) GROUP BY t.ti_id ");
		return dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//获取给定球队参与比赛的的球友
	public List<Long> getScoreUserList(Long matchId, Long teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select s.msUserId ");
		sql.append("FROM MatchScore AS s ");
		sql.append("where s.msMatchId = "+matchId);
		sql.append(" and s.msTeamId = "+teamId);
		sql.append(" GROUP BY s.msUserId " );
		return dao.createQuery(sql.toString());
	}

	/**
	 * 获取本球队所有的球友Id  不知道为啥这个方法放到teamDao 中不行 nhq
	 * @return
	 */
	public List <Long> getTeamUserIdList(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select t.tumUserId ");
		hql.append(" from TeamUserMapping as t where t.tumTeamId = "+teamId );
		hql.append(" and t.tumUserType != 2 ");
		return dao.createQuery(hql.toString());
	}
	//获取球友本次比赛的积分
	public TeamUserPoint getTeamUserPoint(Long matchId, Long teamId, Long userId, Long captainUserId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM TeamUserPoint AS t where t.tupMatchId = "+matchId+" and t.tupTeamId ="+teamId+" and t.tupUserId = "+userId);
		//sql.append(" and t.tupCreateUserId = "+captainUserId); nhq 不需要把队长作为判断条件
		List<TeamUserPoint> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}


	//获取球友的得分
    public MatchScore getMatchScoreByIds(Long userId_, Long matchId, Long groupId, Long teamId,Integer beforeAfter, String holeName,
                                         Integer holeNum) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from MatchScore as m ");
        sql.append("where m.msMatchId = "+matchId);
        sql.append(" and m.msGroupId = "+groupId);
        if(teamId != null){
			sql.append(" and m.msTeamId = "+teamId);
		}
        sql.append(" and m.msUserId = "+userId_);
        sql.append(" and m.msBeforeAfter = "+beforeAfter);
        sql.append(" and m.msHoleName = '"+holeName+"' ");
        sql.append(" and m.msHoleNum = "+holeNum);
        List<MatchScore> matchScoreList = dao.createQuery(sql.toString());
        if(matchScoreList != null && matchScoreList.size()>0){
            return matchScoreList.get(0);
        }
        return null;
    }

    //从matchHoleResult表中获取参赛队
    public List<Map<String,Object>> getJoinTeamMappingList(Long matchId, Integer childId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
        parp.put("childId",childId);

	    StringBuilder sql = new StringBuilder();
        sql.append("select mhr.mhr_team_id as teamId from match_hole_result as mhr where mhr.mhr_match_id = :matchId  " +
					"  and mhr.mhr_match_child_id = :childId GROUP BY mhr.mhr_team_id ");
        return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
    }

    //从matchHoleResult表中获取子比赛列表
    public List<Map<String,Object>> getChildMappingList(Long matchId) {
        Map<String, Object> parp = new HashMap<>();
        parp.put("matchId",matchId);
               StringBuilder sql = new StringBuilder();
        sql.append("select mhr.mhr_match_child_id as childId, ti.ti_abbrev as teamAbbrev ");
        sql.append(" from match_hole_result as mhr ,team_info as ti  where mhr.mhr_match_id = :matchId  " +
                "  and mhr.mhr_team_id = ti.ti_id GROUP BY mhr.mhr_match_child_id, mhr.mhr_team_id ORDER by mhr.mhr_match_child_id ");
        return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
    }

    //从matchHoleResult表中获取参赛队各组情况
    public List<Map<String,Object>> getTeamGroupResult(Long matchId,Integer childId, Long teamId) {
        Map<String, Object> parp = new HashMap<>();
        parp.put("matchId",matchId);
        parp.put("childId",childId);
        parp.put("teamId",teamId);
        StringBuilder sql = new StringBuilder();
        sql.append("select mhr.mhr_group_id as groupId, mhr.mhr_user_name0 as userName0, mhr.mhr_user_name1 as userName1, mhr.mhr_result as result,mhr.mhr_hole_left as holeLeft ");
        sql.append(" from match_hole_result as mhr ");
        sql.append(" Where mhr.mhr_match_id = :matchId  and  mhr.mhr_match_child_id = :childId and mhr.mhr_team_id = :teamId ");
        sql.append(" ORDER BY mhr.mhr_group_id ");
        return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
    }

    //获取matchHoleResult该球队在本比赛中的平、赢组个数
    public List<Map<String, Object>> getPingWinNumList(Long matchId,Integer childId, Long teamId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(CASE WHEN mhr.mhr_team_id = "+teamId+" and mhr.mhr_result >0 then 1 else 0 end) AS winNum, ");
        sql.append("sum(CASE WHEN mhr.mhr_team_id = "+teamId+"  and mhr.mhr_result =0 then 1 else 0 end) AS pingNum ");
        sql.append("from match_hole_result as mhr where mhr.mhr_match_id = "+matchId+ " and mhr.mhr_match_child_id = "+childId);
        return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
    }

    //获取matchHoleResult本球队在本比赛中的输球组和赢球组列表
    public List<MatchHoleResult> getMatchHoleWinOrLoseList(Long matchId, Long teamId) {
        StringBuilder sql = new StringBuilder();
        sql.append("from MatchHoleResult as mhr " +
                "where mhr.mhrMatchId = " +matchId);
        if(teamId != null){
			sql.append(" and mhr.mhrTeamId = "+teamId);
		}
        return dao.createQuery(sql.toString());
    }

    //获取matchHoleResult比洞赛输赢结果
    public MatchHoleResult getMatchHoleResult(Long matchId, Long groupId, Long teamId) {
        StringBuilder sql = new StringBuilder();
        sql.append("FROM MatchHoleResult AS t where t.mhrMatchId = "+matchId+" and t.mhrTeamId ="+teamId+" and t.mhrGroupId = "+groupId);
        List<MatchHoleResult> list = dao.createQuery(sql.toString());
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    //获取matchHoleResult多队比洞赛的子比赛最大序号
    public Integer getMaxMatchChildId(Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MAX(m.mhrMatchChildId) FROM MatchHoleResult as m where m.mhrMatchId = "+matchId);
        List<Integer> list = dao.createQuery(sql.toString());
        if(list != null && list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }


    //获取matchHoleResult中该两队是否已存在childId
    public Integer  childExist(Long matchId,Long teamId0,Long teamId1) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("matchId", matchId);
        parp.put("teamId0", teamId0);
        parp.put("teamId1", teamId1);
        StringBuffer hql = new StringBuffer();

        hql.append("SELECT m.mhrMatchChildId FROM MatchHoleResult as m WHERE m.mhrMatchId = :matchId  and m.mhrTeamId = :teamId0 ");
        hql.append("AND m.mhrGroupId IN ( ");
        hql.append("select m1.mhrGroupId from MatchHoleResult as m1 where m1.mhrMatchId = :matchId  and m1.mhrTeamId = :teamId1 ");
        hql.append(") ");
        List<Integer> list = dao.createQuery(hql.toString(), parp);
        if(list != null && list.size()>0){
            return list.get(0);
        }else{
            return 0;
        }
    }


    //查询我的观战数据
    public MatchJoinWatchInfo getMatchWatchInfo(Long matchId, Long userId) {
        StringBuilder sql = new StringBuilder();
        sql.append("from MatchJoinWatchInfo as t " +
                "where t.mjwiMatchId = " +matchId+
                " and t.mjwiUserId = "+userId);
        List<MatchJoinWatchInfo> list = dao.createQuery(sql.toString());
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

	//我是否是参赛人员(显示邀请记分按钮)
    public Long getIsJoinMatchUser(Long matchId, Long userId) {
        StringBuilder sql = new StringBuilder();
        sql.append("from MatchUserGroupMapping as g " +
                "where g.mugmMatchId = " +matchId+
                " and g.mugmUserId = "+userId+
				" and g.mugmIsDel = 0");
        return dao.createCountQuery("select count(*) "+sql.toString());
    }

	/**
	 * 进一步筛选用是否在这些上报球队中，查询球队的id
	 */
	public List<Long> getReportTeamIdListByUserId(Long userId, List<Long> reportTeamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("userId",userId);
		parp.put("reportTeamIdList",reportTeamIdList);

		StringBuilder sql = new StringBuilder();
		sql.append("select tum.tumTeamId from TeamUserMapping as tum where tum.tumUserId =:userId ");
		sql.append("and tum.tumTeamId in (:reportTeamIdList) ");
		return dao.createQuery(sql.toString(),parp);
	}

	/**
	 * 同时更新记分卡的比赛得分和上报球队得分
	 */
	public void updateScoreAndReportScore(Long userId,UserInfo myUserInfo, MatchScore scoreDb,Integer beforeAfter,
										  ParkPartition parkPartition,List<Long> reportTeamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("reportTeamIdList",reportTeamIdList);
		parp.put("matchId",scoreDb.getMsMatchId());
		parp.put("groupId",scoreDb.getMsGroupId());
		parp.put("userId",userId);
		parp.put("isUp",scoreDb.getMsIsUp());
		parp.put("rodNum",scoreDb.getMsRodNum());
		parp.put("rodCha",scoreDb.getMsRodCha());
		parp.put("pushRod",scoreDb.getMsPushRodNum());
		parp.put("isPar",scoreDb.getMsIsPar());
		parp.put("isBird",scoreDb.getMsIsBird());
		parp.put("isEagle",scoreDb.getMsIsEagle());
		parp.put("isOn",scoreDb.getMsIsOn());
		parp.put("isBomb",scoreDb.getMsIsBomb());
		parp.put("isBogey",scoreDb.getMsIsBogey());
		parp.put("updateTime",System.currentTimeMillis());
		parp.put("updateUserId",myUserInfo.getUiId());
		parp.put("updateUserName",myUserInfo.getUserName());


		parp.put("beforeAfter",beforeAfter);
		parp.put("holeName",parkPartition.getppName());
		parp.put("holeNum",parkPartition.getPpHoleNum());
		parp.put("standardRod",parkPartition.getPpHoleStandardRod());
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchScore AS s " +
				"SET s.msIsUp = :isUp, " +
				" s.msRodNum = :rodNum, " +
				" s.msRodCha = :rodCha, " +
				" s.msPushRodNum = :pushRod, " +
				" s.msIsPar = :isPar, " +
				" s.msIsBird = :isBird, " +
				" s.msIsEagle = :isEagle, " +
				" s.msIsOn = :isOn, " +
				" s.msIsBomb = :isBomb, " +
				" s.msIsBogey = :isBogey, " +
				" s.msUpdateUserId = :updateUserId, " +
				" s.msUpdateTime = :updateTime, " +
				" s.msUpdateUserName = :updateUserName ");
		sql.append("WHERE s.msUserId = :userId " +
				"AND s.msMatchId = :matchId " +
				"AND s.msGroupId = :groupId " +
				"AND s.msBeforeAfter = :beforeAfter " +
				"AND s.msHoleName = :holeName " +
				"AND s.msHoleNum = :holeNum " +
				"AND s.msHoleStandardRod = :standardRod ");
		if(reportTeamIdList != null && reportTeamIdList.size()>0){
			sql.append(" AND s.msType = 1 and s.msTeamId in(:reportTeamIdList) ");
		}
		dao.executeHql(sql.toString(),parp);
		System.out.println();
	}


	//该组重复的用户个数（情况：选了2个以上球队，某个人同时是这两个队的队长，且这个人在同一个组里，分别代表不同的球队）
	public List<Object[]> getHavingCountUserId(Long matchId, Long groupId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select m.mugm_user_id,count(*) from match_user_group_mapping as m where m.mugm_match_id = "+matchId+" and m.mugm_group_id = "+groupId +
				" and m.mugm_is_del =0 group by m.mugm_user_id HAVING count(1)>1 ");
		return dao.createSQLQuery(hql.toString());
	}

	//换组 或者选一组报名的时候，查询用户是否在本比赛中
	public MatchUserGroupMapping getIsInMatchUserMapping(Long matchId, Long teamId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("from MatchUserGroupMapping as g " +
				"where g.mugmMatchId = " +matchId+
				" and g.mugmUserId = "+userId);
		if(teamId != null){
            sql.append(" and g.mugmTeamId = "+teamId);
        }
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	//获取该输赢组该球队的参赛人员 是否在上报球队中
	public List<Map<String,Object>> getUserListByGroupIdInMatchScoreForReport(Long matchId, Long groupId, Long teamId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("matchId",matchId);
		parp.put("groupId",groupId);
		parp.put("teamId",teamId);
		StringBuilder sql = new StringBuilder();
		sql.append("select s1.ms_user_id as userId " );
		sql.append(" from match_score as s1 where s1.ms_match_id = :matchId and s1.ms_group_id = :groupId and s1.ms_user_id in (");
		sql.append(" 	select s.ms_user_id from match_score as s where s.ms_match_id = :matchId and s.ms_group_id = :groupId and s.ms_team_id = :teamId ");
		sql.append(" 	and s.ms_type = 0 GROUP BY s.ms_user_id ");
		sql.append(" ) and s1.ms_type = 1 GROUP BY s1.ms_user_id ");
		return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//查看是否有上报队输赢结果
	public Long getReportMatchHoleReslutCount(MatchHoleResult bean) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) FROM MatchHoleResult as t where t.mhrMatchId = "+bean.getMhrMatchId());
		sql.append(" and t.mhrGroupId = "+bean.getMhrGroupId());
		sql.append(" and t.mhrTeamId = "+bean.getMhrTeamId());
		sql.append(" and t.mhrResult = "+bean.getMhrResult());
		return dao.createCountQuery(sql.toString());

	}
	//撤销成绩上报 删除球队用户积分及成绩 nhq
	public void delTeamUserPoint(Long matchId, Long teamId, Long captainUserId) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete FROM TeamUserPoint as t where t.tupMatchId = "+matchId);
		sql.append(" and t.tupTeamId = "+teamId);
		//sql.append(" and t.tupCreateUserId = "+captainUserId); 不用判断队长信息
		dao.executeHql(sql.toString());
	}
    //我是否是本组参赛人员(显示邀请记分按钮)
    public Long getIsJoinMatchGroup(Long matchId, Long groupId, Long userId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) FROM MatchUserGroupMapping as t where t.mugmMatchId = "+matchId);
        sql.append(" and t.mugmGroupId = "+groupId);
        sql.append(" and t.mugmUserId = "+userId);
        sql.append(" and t.mugmIsDel = 0");
        return dao.createCountQuery(sql.toString());
    }

	//更新所有参赛球友的isdel，除了我
	public void updateUserMappingIsDel(Long matchId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("update MatchUserGroupMapping as t set t.mugmIsDel = 1");
		sql.append("where t.mugmMatchId = "+matchId);
		sql.append(" and t.mugmUserId != "+userId);
		dao.executeHql(sql.toString());
	}

	/**
	 * 获取本比赛所有赛长列表
	 * @return
	 */
	public List<Map<String, Object>> getAllMatchCaptainList(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiId as uiId,u.uiHeadimg as uiHeadimg,u.uiRealName as uiRealName,u.uiNickName as uiNickName ");
		hql.append(" FROM MatchUserGroupMapping AS mugm,UserInfo AS u WHERE 1=1 ");
		hql.append(" and mugm.mugmUserId = u.uiId ");
		hql.append(" and mugm.mugmUserType = 0 ");
		hql.append(" and mugm.mugmMatchId = "+matchId);
		hql.append(" ORDER BY mugm.mugmCreateTime DESC");
		List<Map<String, Object>> list = dao.createQuery(hql.toString(),Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 更新本比赛的其他分组，重新排列
	 * @return
	 */
	public void updateGroupNames(Long matchId,String groupName) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MatchGroup AS t SET t.mgGroupName = t.mgGroupName -1 ");
		sql.append(" WHERE t.mgMatchId = "+matchId);
		sql.append(" AND t.mgGroupName > "+groupName);
		dao.executeHql(sql.toString());
	}

	/**
	 * 获取所有参赛队的队长
	 * @return
	 */
	public List<Long> getCapUserListByJoinTeamId(List<Long> joinTeamIds) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamIds",joinTeamIds);
		StringBuilder sql = new StringBuilder();
		sql.append("select t.tumUserId from TeamUserMapping as t ");
		sql.append(" WHERE t.tumTeamId in(:teamIds) ");
		sql.append(" AND t.tumUserType = 0 ");
		return dao.createQuery(sql.toString(),parp);
	}

	/**
	 * 获取临时球友，按照id升序排列，取第一个临时球友
	 * @return
	 */
	public MatchUserGroupMapping getLinshiUserByMatchId(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" from MatchUserGroupMapping as t ");
		sql.append(" WHERE t.mugmMatchId ="+matchId);
		sql.append(" AND t.mugmIsLinshi = 1 ");
		sql.append(" order by t.mugmUserId desc ");
		List<MatchUserGroupMapping> list = dao.createQuery(sql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}
	//判断是不是赛友，参考判断队友那个设计
	public Long getIsMyMatchmate(Long myUserId, Long otherUserId) {
		Map<String, Object> parp = new HashMap<String, Object>();
		parp.put("myUserId", myUserId);
		parp.put("otherUserId", otherUserId);
		StringBuffer hql = new StringBuffer();

		hql.append("SELECT m.mugmMatchId FROM MatchUserGroupMapping as m WHERE m.mugmUserId = :otherUserId ");
		hql.append("AND m.mugmMatchId IN ( ");
		hql.append("select m1.mugmMatchId from MatchUserGroupMapping as m1 where m1.mugmUserId = :myUserId ");
		hql.append(") ");
		List<Long> list = dao.createQuery(hql.toString(), parp);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	//双人比杆赛记分卡——总计
	public List<Map<String, Object>> getSum(Long matchId, Long groupId, Long userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(s.ms_rod_num) AS sum_rod_num, sum(s.ms_push_rod_num) AS sum_push_num, sum(s.ms_rod_cha) AS sum_rod_cha ");
		sql.append("FROM match_score AS s ");
		sql.append("where s.ms_match_id = "+matchId+" AND s.ms_group_id = "+groupId);
		sql.append(" AND s.ms_user_id = "+userId );
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//比赛——比分榜——比赛的所有用户和其总杆数（没有参赛队的情况下）
	public List<Map<String, Object>> getUserListByIdWithOutTeam(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select " +
				" u.ui_nick_name AS uiNickName," +
				" u.ui_real_name AS uiRealName," +
				" u.ui_headimg AS uiHeadimg,score.* from (" );
		sql.append("select m.mugm_user_id AS uiId,m.mugm_group_id AS group_id," +
				"(IFNULL(sum(s.ms_rod_num),0)) AS sumRodNum,sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("FROM match_user_group_mapping as m LEFT JOIN match_score AS s " +
					"on (m.mugm_match_id = s.ms_match_id and m.mugm_user_id = s.ms_user_id and s.ms_type = 0) ");
		sql.append("where m.mugm_match_id = "+matchId );
		sql.append(" and m.mugm_is_del != 1 ");
		sql.append(" GROUP BY m.mugm_user_id " );
		sql.append(" )score LEFT JOIN user_info AS u ON score.uiId = u.ui_id ");
		sql.append("ORDER BY score.sumRodNum !=0 desc,score.sumRodNum ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}
    //查询是否在比分表有数据（对于从后台导入的比赛信息，有得分记录，如果调整了分组，match_score表要同步调整分组）
    public Long getUserMatchScoreCountById(Long userId, Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from MatchScore as s ");
        sql.append("where s.msMatchId = "+matchId );
        sql.append(" and s.msUserId = "+userId );
        return dao.createCountQuery(sql.toString());
    }

    //判断一个组是否嘉宾组
    public Long groupIsGuest(Long groupId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from MatchGroup as s ");
        sql.append(" where s.mgId = "+groupId );
        sql.append(" and s.mgIsGuest = 1");
        return dao.createCountQuery(sql.toString());
    }

    //调整分组的同时更新match_score表的分组id
    public void updateGroupIdWithMatchScore(Long userId, Long matchId, Long groupId) {
        StringBuilder sql = new StringBuilder();
        sql.append("update MatchScore as s ");
        sql.append(" set s.msGroupId = "+groupId );
        sql.append(" where s.msMatchId = "+matchId );
        sql.append(" and s.msUserId = "+userId );
        dao.executeHql(sql.toString());
    }

    /**
     * 修改比赛结束状态
     */

    public void updateMatchIsEnd(Long matchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE MatchInfo AS m set m.miIsEnd = 2 ");
        sql.append(" WHERE m.miId = " + matchId);
        dao.executeHql(sql.toString());
    }

}
