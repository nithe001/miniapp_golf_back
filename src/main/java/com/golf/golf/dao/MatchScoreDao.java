package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScoreNetHole;
import com.golf.golf.db.MatchScoreStartHole;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 净杆计算service
 * Created by nmy on 2017/7/1.
 */
@Repository
public class MatchScoreDao extends CommonDao {

	public MatchScoreNetHole getMatchNetRodHole(Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("FROM MatchScoreNetHole as t where t.msntMatchId = "+matchId);
		List<MatchScoreNetHole> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	//本比赛的所有用户，为0的排后面(首列显示)
	 public List<Map<String, Object>> getUserListByMatchId(MatchInfo matchInfo,List<Long> childMatchIds,Integer orderType) {
	    Integer matchType =  matchInfo.getMiType();
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchInfo.getMiId());
         parp.put("childMatchIds",childMatchIds);
		StringBuilder sql = new StringBuilder();
		 sql.append("select " +
				 " u.ui_headimg AS uiHeadimg,"+
				 " s.ms_team_id as team_id," +
				 "s.ms_match_id as match_id," +
				 "s.ms_match_title as matchName," +
				 "s.ms_group_id as group_id," +
				 "s.ms_user_id as uiId," +
				 "s.ms_user_name as uiRealName," +
                 "COUNT(s.ms_id) AS holeCount " );
		 if (orderType ==3 ) {
             sql.append(" FROM  match_score AS s INNER JOIN user_info AS u ON s.ms_user_id = u.ui_id   and u.ui_sex='女'  ");
         } else {
             sql.append(" FROM  match_score AS s LEFT JOIN user_info AS u ON s.ms_user_id = u.ui_id ");
         }
         if (matchType ==2) {
             sql.append(" WHERE s.ms_match_id IN ( :childMatchIds) ");
         }else {
             sql.append(" WHERE s.ms_match_id = :matchId ");
         }
		 sql.append(" and s.ms_type = 0  ");
//         sql.append(" and s.ms_user_id = 670 ");
		 sql.append(" GROUP BY s.ms_user_id, s.ms_match_id ");
		 return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	 }

	//获取本场地18洞的总标准杆
	public Long getSumStandardRod(MatchInfo matchInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(p.pp_hole_standard_rod) as sumRod from park_partition as p where p.pp_p_id = "+matchInfo.getMiParkId());
		sql.append(" and (p.pp_name = '"+matchInfo.getMiZoneBeforeNine()+"' or p.pp_name = '"+matchInfo.getMiZoneAfterNine()+"')");
		List<Object> obj = dao.createSQLQuery(sql.toString());
		if(obj != null && obj.size() >0){
			return obj.get(0) != null ? Long.parseLong(obj.get(0).toString()):0L;
		}
		return 0L;
	}




	//获取本用户18洞的总杆数（不防作弊）
	public Long getSumRod(Long userId,Long matchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(s.ms_rod_num) as sumRod from match_score as s ");
		sql.append(" where s.ms_match_id = "+matchId);
		sql.append(" and s.ms_user_id = "+userId);
		sql.append(" and s.ms_type = 0 ");
		List<Object> obj = dao.createSQLQuery(sql.toString());
		if(obj != null && obj.size() >0){
			return obj.get(0) != null ? Long.parseLong(obj.get(0).toString()):0L;
		}
		return 0L;
	}

	/**
	 * 查询是否有本组得分-为了设置开球洞
	 */
	public Long getStartHoleCountByGroupId(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from MatchScoreStartHole as s ");
		sql.append(" where s.shMatchId = "+matchId+" and s.shGroupId = "+groupId );
		return dao.createCountQuery(sql.toString());
	}

	/**
	 * 获取本组开球球洞
	 */
	public MatchScoreStartHole getStartHole(Long matchId, Long groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("from MatchScoreStartHole as s ");
		sql.append("where s.shMatchId = "+matchId+" and s.shGroupId = "+groupId );
		List<MatchScoreStartHole> list = dao.createQuery(sql.toString());
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
     *从teamuserpoint 表中取一场父比赛的所有球员的净杆,这类记录和球队积分的区别时reportteamid = 0
     * 由于总杆和净杆不管那个父比赛取，都是一样的，所以一个子比赛的数据在这个表里只存一份
     *scoreType: 0:总杆，1，净杆
     **/
    public List<Map<String, Object>> getUserScorePointByMatchId(Long matchId,Long teamId,Integer matchType,List<Long> childMatchIds,Integer orderType) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("matchId", matchId);
        parp.put("teamId", teamId);
        parp.put("childMatchIds", childMatchIds);

        StringBuilder sql = new StringBuilder();
        if (orderType == 3) {
            sql.append("SELECT score1.* from ( ");
        }
        sql.append("SELECT " +
                " m.mi_title AS matchName, score.* from ( ");
        sql.append(" SELECT tup.tup_user_id AS uiId,tup.tup_user_name AS uiRealName,tup.tup_user_headimg AS uiHeadimg," +
                "tup.tup_team_id AS team_id, tup.tup_team_abbrev AS teamAbbrev, tup.tup_group_id AS group_id," +
                " tup.tup_match_id AS match_id,tup.tup_match_point AS sumRodNet,tup.tup_hole_count AS holeCount ");
        sql.append(" FROM team_user_point AS tup " );
        if  (matchType  ==2) {
            sql.append(" WHERE tup.tup_match_id IN (:childMatchIds ) ");
        } else {
            sql.append(" WHERE tup.tup_match_id = :matchId ");
        }
        if  (teamId  !=null) {
            sql.append(" AND tup.tup_team_id = :teamId ");
        }
        sql.append(" AND tup.tup_report_team_id = 0 ");
        sql.append(" )score LEFT JOIN match_info AS m ON score.match_id = m.mi_id ");
        if (orderType == 3) {
            sql.append(" )score1 INNER JOIN user_info AS u ON score1.uiId =u.ui_id  and u.ui_sex='女'  ");
            sql.append("ORDER BY score1.holeCount=18 DESC, score1.sumRodNet ");
        } else {
            sql.append("ORDER BY   score.holeCount=18 DESC, score.sumRodNet ");
        }

        List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
        return list;
    }
}
