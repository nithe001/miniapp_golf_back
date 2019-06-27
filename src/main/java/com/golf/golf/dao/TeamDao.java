package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.TeamUserMapping;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
		}else if((Integer)parp.get("type") == 3){
			//只显示我创建的球队
			hql.append("AND t.ti_create_user_id = :userId ");
		}

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
	 * 比赛——获取已经选中的球队列表
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
		hql.append("SELECT u.uiId as uiId,u.uiRealName as uiRealName,u.uiHeadimg as uiHeadimg,m.tumUserType as tumUserType  ");
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
	 * 获取球队记分详情 按平均杆排名 单练的不算入内
	 *
	 * 只计算比赛结束并且球队确认过的场次成绩
	 * @return
	 */
	public List<Map<String, Object>> getTeamPointByYear(Map<String, Object> parp) {
		StringBuilder allHql = new StringBuilder();
		StringBuilder countHhql = new StringBuilder();
		StringBuilder changciHql = new StringBuilder();
		//获取用户的总参赛场次
		countHhql.append("select t.user_id,count(t.match_id)as totalMatchNum,t.point from (" +
						"select tum.tum_user_id as user_id,s.ms_match_id as match_id,tum.tum_point as point from team_user_mapping as tum LEFT JOIN match_score as s " +
						"on (tum.tum_user_id = s.ms_user_id and s.ms_is_team_submit = 1 " +
						"and s.ms_create_time >=:startYear and s.ms_create_time <=:endYear )" +
						"GROUP BY tum.tum_user_id ,s.ms_match_id)" +
						"as t group by t.user_id");
		//获取用户前n场的总杆数和平均杆数
		changciHql.append(" SELECT  " +
					" tt.user_id,  " +
					" sum(tt.sum_rod_num) as sumRodNum,  " +
					" round((sum(tt.sum_rod_num) /:changCi),2) AS avgRodNum  " +
					"FROM  " +
						" (  " +
						"  SELECT *  FROM  " +
						"   (  " +
						"    SELECT  " +
						"     tum.tum_user_id AS user_id,  " +
						"     s.ms_match_id AS match_id,  " +
						"     sum(s.ms_rod_num) AS sum_rod_num  " +
						"    FROM  " +
						"     team_user_mapping AS tum  " +
						"    LEFT JOIN match_score AS s ON (  " +
						"     tum.tum_user_id = s.ms_user_id  " +
						"     AND s.ms_is_team_submit = 1 and s.ms_create_time >=:startYear AND s.ms_create_time <=:endYear " +
						"    )  " +
						"    WHERE  " +
						"     tum.tum_user_type != 2 " +
						"    GROUP BY  " +
						"     s.ms_match_id  " +
						"    ORDER BY  " +
						"    IF (ISNULL(sum(s.ms_rod_num)),1,0),  " +
						"    sum(s.ms_rod_num)  " +
						"   ) AS t  " +
						"  WHERE  " +
						"   (  " +
						"    SELECT  " +
						"     count(1) + 1  " +
						"    FROM  " +
						"     (  " +
						"      SELECT  " +
						"       tum.tum_user_id AS user_id,  " +
						"       s.ms_match_id AS match_id,  " +
						"       sum(s.ms_rod_num) AS sum_rod_num  " +
						"      FROM  " +
						"       team_user_mapping AS tum  " +
						"      LEFT JOIN match_score AS s ON (  " +
						"       tum.tum_user_id = s.ms_user_id  " +
						"       AND s.ms_is_team_submit = 1 and s.ms_create_time >=:startYear AND s.ms_create_time <=:endYear " +
						"      )  " +
						"      WHERE  " +
						"       tum.tum_user_type != 2 " +
						"      GROUP BY  " +
						"       s.ms_match_id  " +
						"      ORDER BY  " +
						"  " +
						"      IF (ISNULL(sum(s.ms_rod_num)),1,0),  " +
						"      sum(s.ms_rod_num)  " +
						"     ) AS ts  " +
						"    WHERE  " +
						"     ts.user_id = t.user_id  " +
						"    AND ts.sum_rod_num < t.sum_rod_num  " +
						"   ) <= :changCi  " +
						" ) AS tt  " +
						"GROUP BY tt.user_id ");


		allHql.append("select u.ui_id as uid,u.ui_nick_name as nickName,u.ui_real_name as realName,count.totalMatchNum,count.point,changci.sumRodNum,changci.avgRodNum ");
		allHql.append("from ("+countHhql.toString()+") as count ");
		allHql.append(" left join ");
		allHql.append("("+changciHql.toString()+") as changci");
		allHql.append(" on count.user_id = changci.user_id ");
		allHql.append(" left join user_info as u on u.ui_id = count.user_id ");
		List<Map<String, Object>> list = dao.createSQLQuery(allHql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 球队比分、积分榜——场次
	 * @return
	 */
	public List<Map<String, Object>> getTeamPointByChangci(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("select t.userId,t.nickName,t.realName,round(SUM(t.rodNum)/sum(t.count),2) AS avgRodNum," +
				"t.rodNum as sumRodNum,ifnull(tum.tum_point,0) as point,sum(t.count) as totalHoleNum ");
		hql.append(" from ( ");
		hql.append("SELECT " +
				"s.ms_user_id AS userId, " +
				"u.ui_nick_name as nickName, " +
				"u.ui_real_name as realName, " +
				"SUM(s.ms_rod_num) AS rodNum, " +
				"s.ms_match_id as matchId," +
				"count(s.ms_id) as count " +
				"FROM match_score AS s,user_info as u " +
				"WHERE s.ms_team_id = :teamId and s.ms_user_id = u.ui_id " +
				"GROUP BY s.ms_match_id,s.ms_user_id )as t,team_user_mapping as tum  ");
		hql.append(" where exists ( ");
		hql.append(" select count(*) from ");
		hql.append(" ( " +
				"SELECT " +
				"s.ms_user_id AS userId, " +
				"u.ui_nick_name as nickName, " +
				"u.ui_real_name as realName, " +
				"SUM(s.ms_rod_num) AS rodNum, " +
				"s.ms_match_id as matchId," +
				"count(s.ms_id) as count " +
				"FROM match_score AS s,user_info as u " +
				"WHERE s.ms_team_id = :teamId and s.ms_user_id = u.ui_id " +
				"GROUP BY s.ms_match_id, s.ms_user_id )as ts ");
		hql.append(" where ts.rodNum <=t.rodNum " +
					"GROUP BY ts.matchId " +
					"HAVING COUNT(*) <= :changCi ");
		hql.append(") and tum.tum_user_type != 2 and t.userId = tum.tum_user_id ");
		hql.append(" GROUP BY t.userId ORDER BY avgRodNum ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
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
	 * 列出当年所有本球队相关的比赛情况统计
	 * 比赛名称、参赛队、时间、参赛人数、基础积分、杆差倍数、赢球奖分
	 * @return
	 */
	public List<Map<String, Object>> getTeamMatchByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("select t.*,c.ic_base_score as baseScore,c.ic_rod_cha as rodCha,c.ic_win_score as winScore from (" +
				"SELECT m.mi_id as matchId,m.mi_title as matchTitle,m.mi_match_time as applyTime," +
				"count(mugm.mugm_user_id) as userCount " +
					"FROM match_user_group_mapping AS mugm, match_info AS m " +
						"WHERE mugm.mugm_match_id = m.mi_id and mugm.mugm_team_id = :teamId " +
					" ) as t LEFT JOIN integral_config as c on t.matchId = c.ic_match_id " +
				"where t.applyTime >= :startYear and t.applyTime <= :endYear ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
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

}
