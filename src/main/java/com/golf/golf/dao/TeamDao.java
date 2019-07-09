package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.TeamUserMapping;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台-球队
 * Created by nmy on 2017/7/1.
 */
@Repository
public class TeamDao extends CommonDao {

	/**
	 * 获取球队列表
	 * type 0：所有球队 1：已加入球队 2：可加入球队  3：我创建的球队
	 * @return
	 */
    public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where tm.tum_user_type != 2 GROUP BY tum_team_id ");
		hql.append(")as tum on t.ti_id = tum.tum_team_id ");
		hql.append("where t.ti_is_valid = 1 ");

		if((Integer)parp.get("type") == 1){
			//已加入球队 包括我创建的球队，作为队长可以管理报名
			hql.append("and (t.ti_id in (SELECT tum.tum_team_id FROM team_user_mapping AS tum WHERE tum.tum_user_id = :userId) or t.ti_create_user_id = :userId)");
		}else if((Integer)parp.get("type") == 2){
			//可加入球队
			hql.append("and t.ti_id not in (SELECT tum.tum_team_id FROM team_user_mapping AS tum WHERE tum.tum_user_id = :userId) ");
		}
		/*else if((Integer)parp.get("type") == 3){
			//只显示我创建的球队
			hql.append("AND t.ti_create_user_id = :userId ");
		}*/

        if(parp.get("keyword") != null){
            hql.append("AND t.ti_name LIKE :keyword ");
        }
        //去掉已经选中的队伍
		if(parp.get("teamIds") != null){
			hql.append("AND t.ti_id not in (:teamIds) ");
		}

        Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<>());
            pageInfo.setCount(0);
            return pageInfo;
        }
		hql.append("ORDER BY t.ti_create_time desc ");
        String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo ";
		List<Map<String, Object>> list = dao.createSQLQuery( select + hql.toString(),
				parp, pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }


	/**
	 * 获取球队列表
	 * type 3：我创建的球队 ，包括待审核人数
	 * @return
	 */
	public POJOPageInfo getMyCreateTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where tm.tum_user_type != 2 GROUP BY tum_team_id ");
		hql.append(")as tum on t.ti_id = tum.tum_team_id ");

		hql.append("LEFT JOIN (" +
					"SELECT " +
					"count(tm1.tum_user_id) AS applyUserCount, " +
					"tm1.tum_team_id AS tum_team_id " +
					"FROM team_user_mapping AS tm1 " +
					"WHERE tm1.tum_user_type = 2 " +
					"GROUP BY tm1.tum_team_id " +
					") AS tum1 ON t.ti_id = tum1.tum_team_id ");

		hql.append("where t.ti_is_valid = 1 ");
		hql.append("AND t.ti_create_user_id = :userId ");

		if(parp.get("keyword") != null){
			hql.append("AND t.ti_name LIKE :keyword ");
		}
		//去掉已经选中的队伍
		if(parp.get("teamIds") != null){
			hql.append("AND t.ti_id not in (:teamIds) ");
		}

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY t.ti_create_time desc ");
		String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,tum1.applyUserCount,t.ti_create_time as ti_create_time,t.ti_logo as logo ";
		List<Map<String, Object>> list = dao.createSQLQuery( select + hql.toString(),
				parp, pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}


	/**
	 * 创建比赛——获取参赛球队列表 或 上报球队列表
	 * @return
	 */
	public POJOPageInfo getChooseTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where tm.tum_user_type != 2 GROUP BY tum_team_id ");
		hql.append(")as tum on t.ti_id = tum.tum_team_id ");
		hql.append("where t.ti_id in(:chooseTeamIds) and t.ti_is_valid = 1 ");
		String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo ";

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		List<Map<String, Object>> list = dao.createSQLQuery(select+ hql.toString(),parp,Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 比赛——获取上报球队列表 不包括参赛队 也不包括已经选中的上报队
	 * type 0:已选 1备选
	 * @return
	 */
	public POJOPageInfo getReportTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		String select = "select t.ti_id as tiId,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo ";
		hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where tm.tum_user_type != 2 GROUP BY tum_team_id ");
		hql.append(")as tum on t.ti_id = tum.tum_team_id ");
		hql.append("where t.ti_is_valid = 1 ");

		List<Long> list1 = (List<Long>)parp.get("joinTeamIdList");
		List<Long> list2 = (List<Long>)parp.get("reportTeamIdList");

		if("0".equals(parp.get("type").toString())){
			//备选球队 去除参赛队和选中的上报球队
			if(parp.get("joinTeamIdList") != null && list1.size()>0){
				hql.append("and t.ti_id not in(:joinTeamIdList) ");
			}
			if(parp.get("reportTeamIdList") != null && list2.size()>0){
				hql.append("and t.ti_id not in(:reportTeamIdList) ");
			}
		}else{
			//已选球队
			if(parp.get("reportTeamIdList") != null && list1.size()>0){
				hql.append("and t.ti_id in(:reportTeamIdList) ");
			}
		}

		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		List<Map<String, Object>> list = dao.createSQLQuery(select+hql.toString(),parp,Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 通过球队id获取队长
	 * @return
	 */
	public List<String> getCaptainByTeamId(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiNickName FROM TeamUserMapping AS t,UserInfo as u WHERE 1=1 " +
				"AND t.tumTeamId = "+teamId+" AND t.tumUserType = 0 AND t.tumUserId = u.uiId ");
		return dao.createQuery(hql.toString());
	}

	/**
	 * 判断是否是该队队长
	 * @return
	 */
	public Long isCaptainIdByTeamId(Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM TeamUserMapping AS t WHERE 1=1 " +
				"AND t.tumTeamId = "+teamId+" AND t.tumUserType = 0 AND t.tumUserId = " + userId);
		return dao.createCountQuery(hql.toString());
	}

	/**
	 * 是否在该球队中
	 * @param teamId:球队id
	 * @param userId:用户id
	 * @return
	 */
	public Long isInTeamById(Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM TeamUserMapping AS t WHERE 1=1 " +
				"AND t.tumTeamId = "+teamId+" AND t.tumUserId = " + userId);
		return dao.createCountQuery(hql.toString());
	}


	/**
	 * 获取本球队的前12个队员
	 * @return
	 */
	public List<Map<String, Object>> getTeamUserListByTeamId(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiNickName as uiNickName,u.uiHeadimg as uiHeadimg,m.tumUserType as tumUserType  ");
		hql.append("FROM TeamUserMapping AS m,UserInfo AS u WHERE 1=1 ");
		hql.append("AND m.tumTeamId = "+teamId);
		hql.append(" and m.tumUserId = u.uiId ");
		hql.append(" and m.tumUserType != 2 ");
		hql.append("ORDER BY m.tumUserType ");
		List<Map<String, Object>> list = dao.createQuery(hql.toString(),0, 12,Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 删除球队
	 * @return
	 */
	public void delTeam(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("update TeamInfo as t set t.tiIsValid = 0 WHERE t.tiId = "+teamId);
		dao.executeHql(hql.toString());
	}


	/**
	 * 获取本球友代表此球队的参赛场次
	 * @return
	 */
	public List<Map<String, Object>> getJoinMatchChangCiByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("select count(t.matchId) as count from (select s.ms_match_id as matchId from match_score as s,match_info as m " +
				"where s.ms_team_id = :teamId and s.ms_user_id = :userId " +
				"and s.ms_is_team_submit = 1 and s.ms_match_type = 1 " +
				"and m.mi_is_valid = 1 " +
				"and s.ms_create_time >=:startYear and s.ms_create_time <=:endYear " +
				" GROUP BY s.ms_match_id) as t ");
		 return dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}


	/**
	 * 获取本球队所有的球友
	 * @return
	 */
	public List<TeamUserMapping> getTeamUserList(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append(" from TeamUserMapping as t where t.tumTeamId = :teamId and t.tumUserType != 2 ");
		return dao.createQuery(hql.toString(), parp);
	}

	/**
	 * 计算每个球友前n场的平均杆和总杆 按平均杆排名 单练的不算入内
	 * 积分榜那里的平均杆数是指每场（18洞）的平均杆数，不是每洞的。
	 * 球队比分排名杆数少的排前面，积分榜是积分多的排前面
	 * @return
	 */
	public List<Map<String, Object>> getUserSumRodScore(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT t.userId,sum(t.sumRod) as sumRodNum,ROUND(sum(t.sumRod) /:changCi,2) AS avgRodNum FROM ");
		hql.append(" (");
		hql.append(" SELECT " +
				"s.ms_user_id AS userId, " +
				"s.ms_match_id AS matchId, " +
				"sum(s.ms_rod_num) AS sumRod " +
				"FROM match_score AS s " +
				"WHERE s.ms_user_id = :userId "+
				"and s.ms_team_id = :teamId "+
				"and s.ms_type = 0 " +
				"and s.ms_is_team_submit = 1 " +
				"and s.ms_create_time >=:startYear and s.ms_create_time <=:endYear " +
				"GROUP BY s.ms_match_id " +
				"ORDER BY sumRod " +
				"LIMIT 0,:changCi");
		hql.append(") AS t ");
		return dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 计算每个球友前n场的总积分，按照积分排名 单练的不算入内
	 * 积分榜那里的平均杆数是指每场（18洞）的平均杆数，不是每洞的。
	 * 积分榜是积分多的排前面
	 * @return
	 */
	public List<Map<String, Object>> getUserSumPointScore(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT t.userId,sum(t.sumRod) as sumRodNum,ROUND(sum(t.sumRod) /:changCi,2) AS avgRodNum,sum(p.tup_match_point) AS sumPoint ");
		hql.append(" FROM (");
				hql.append(" SELECT " +
						"s.ms_user_id AS userId, " +
						"s.ms_match_id AS matchId, " +
						"sum(s.ms_rod_num) AS sumRod " +
						"FROM match_score AS s " +
						"WHERE s.ms_user_id = :userId " +
						"AND s.ms_team_id = :teamId "+
						"AND s.ms_type = 0 " +
						"and s.ms_is_team_submit = 1 " +
						"and s.ms_create_time >=:startYear and s.ms_create_time <=:endYear " +
						"GROUP BY s.ms_match_id ");
				hql.append(") AS t,team_user_point AS p ");
		hql.append("WHERE p.tup_match_id = t.matchId ");
		hql.append("AND p.tup_user_id = :userId ");
		hql.append("AND p.tup_team_id = :teamId ");
        hql.append("GROUP BY p.tup_match_id ");
		hql.append("ORDER BY sumPoint DESC LIMIT 0,:changCi");
		return dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 获取球队已报名的用户
	 * @param teamId:球队id
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, Integer type) {
		StringBuilder hql = new StringBuilder();
		List<Map<String, Object>> list = null;
		hql.append("SELECT " +
				"u.uiId AS uiId, " +
				"u.uiHeadimg AS uiHeadimg, " +
				"u.uiRealName AS uiRealName, " +
				"u.uiNickName AS uiNickName ");
		hql.append("FROM " +
				"TeamUserMapping AS m, " +
				"UserInfo AS u " +
				"WHERE " +
				"m.tumUserId = u.uiId and m.tumTeamId = "+teamId);
		if(type == 0){
			hql.append(" AND m.tumUserType = 2 " );
			hql.append(" order by m.tumCreateTime desc " );
		}else if(type == 1){
			hql.append(" AND m.tumUserType = 1 " );
			hql.append(" order by m.tumCreateTime " );
		}else{
			hql.append(" order by m.tumCreateTime " );
		}
		list = dao.createQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 获取该用户
	 * @param teamId:球队id
	 * @return
	 */
	public TeamUserMapping getTeamUserMapping(Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM TeamUserMapping as tum where tum.tumTeamId = "+teamId+ " and tum.tumUserId = "+userId);
		List<TeamUserMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 退出该球队
	 * @param teamId:球队id
	 * @param userId:用户id
	 * @return
	 */
	public void deleteFromTeamUserMapping(Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM TeamUserMapping where tumTeamId = "+teamId+ " and tumUserId = "+userId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 获取球队比赛榜
	 * 列出当年所有本球队相关的比赛情况统计 只列比赛结束且球队确认过的比赛
	 * 比赛名称、参赛队、时间、参赛人数、基础积分、杆差倍数、赢球奖分
	 * @return
	 */
	public List<Map<String, Object>> getTeamMatchByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m.mi_id as matchId,m.mi_title as matchTitle,m.mi_match_time as applyTime," +
					"team.ti_abbrev as teamAbbrev,c.ic_base_score as baseScore," +
					"c.ic_rod_cha as rodCha,c.ic_win_score as winScore " +
					"FROM integral_config AS c,match_info as m,team_info as team " +
					"WHERE c.ic_match_id = m.mi_id and (c.ic_team_id = :teamId or c.ic_report_team_id = :teamId) and c.ic_team_id = team.ti_id " +
					"and m.mi_is_valid = 1 " +
					"and m.mi_match_time >= :startYear and m.mi_match_time <= :endYear ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp,Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 是否是该球队队长
	 * @param userId:用户id
	 * @param teamId:球队id
	 * @return
	 */
	public Long getIsCaptain(Long userId, Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM TeamUserMapping as tum where tum.tumUserType = 0 and tum.tumTeamId = "+teamId+ " and tum.tumUserId = "+userId);
		return dao.createCountQuery(hql.toString());
	}

	/**
	 * 是否是上报球队队长
	 * @param userId:用户id
	 * @param reportTeamIdList:球队id
	 * @return
	 */
	public Long getIsReportTeamCaptain(Long userId, List<Long> reportTeamIdList) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("userId",userId);
		parp.put("reportTeamIdList",reportTeamIdList);
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(*) FROM TeamUserMapping as tum where tum.tumUserType = 0 " +
				"and tum.tumTeamId in (:reportTeamIdList) and tum.tumUserId = :userId ");
		return dao.createCountQuery(hql.toString(),parp);
	}


    public List<Map<String, Object>> getUserPointByChangci(Map<String, Object> parp) {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT sum(t.point) AS sumPoint ");
        hql.append("FROM ( SELECT tup.tup_match_point AS point ");
        hql.append("FROM team_user_point AS tup " +
                "WHERE tup.tup_user_id = :userId " +
                "AND tup.tup_team_id = :teamId " +
                "ORDER BY tup.tup_match_point DESC " +
                "LIMIT 0, :changCi ");
        hql.append(" ) AS t");
        return dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
    }

    //获取球队的用户个数
    public Long getUserCountByTeamId(Long teamId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TeamUserMapping as t ");
        sql.append("WHERE t.tumTeamId = "+teamId);
        return dao.createCountQuery("select count(*) "+sql.toString());
    }
}
