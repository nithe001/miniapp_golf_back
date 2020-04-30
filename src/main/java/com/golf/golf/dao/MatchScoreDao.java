package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScoreNetHole;
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
	 public List<Map<String, Object>> getUserListByMatchId(MatchInfo matchInfo) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchInfo.getMiId());
		StringBuilder sql = new StringBuilder();
		 sql.append("select " +
				 " u.ui_headimg AS uiHeadimg,"+
				 " s.ms_team_id as team_id," +
				 "s.ms_match_id as match_id," +
				 "s.ms_match_title as match_title," +
				 "s.ms_group_id as group_id," +
				 "s.ms_user_id as uiId," +
				 "s.ms_user_name as uiRealName " );
		 sql.append(" FROM" +
				 " match_score AS s left join user_info AS u ON s.ms_user_id = u.ui_id " +
				 " WHERE " +
				 " s.ms_match_id = :matchId ");
		 sql.append(" and s.ms_type = 0 ");
		 sql.append(" GROUP BY s.ms_user_id ");
		 return dao.createSQLQuery(sql.toString(),parp, Transformers.ALIAS_TO_ENTITY_MAP);
	 }

	//获取本场地18洞的总标准杆
	public Long getSumStandardRod(MatchInfo matchInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(p.pp_hole_standard_rod) as sumRod from park_partition as p where p.pp_p_id = "+matchInfo.getMiId());
		sql.append(" and (p.pp_name = '"+matchInfo.getMiZoneBeforeNine()+"' or p.pp_name = '"+matchInfo.getMiZoneAfterNine()+"')");
		List<Object> obj = dao.createSQLQuery(sql.toString());
		if(obj != null && obj.size() >0){
			return obj.get(0) != null ? Long.parseLong(obj.get(0).toString()):0L;
		}
		return 0L;
	}


	//比赛——比分榜——比赛的所有用户和其总杆数（没有参赛队的情况下）——去掉随机6个洞
	public List<Map<String, Object>> getUserListByIdWithOutTeam(Long matchId,
																List<Integer> beforeHoleNum, List<Integer> afterHoleNum) {
		StringBuilder sql = new StringBuilder();
		sql.append("select " +
				" u.ui_nick_name AS uiNickName," +
				" u.ui_real_name AS uiRealName," +
				" u.ui_headimg AS uiHeadimg,score.* from (" );
		sql.append("select m.mugm_user_id AS uiId,m.mugm_group_id AS group_id," +
				"sum(s.ms_rod_num) AS sumRodNum,sum(s.ms_rod_cha) AS sumRodCha ");
		sql.append("FROM match_user_group_mapping as m LEFT JOIN match_score AS s " +
				"on (m.mugm_match_id = s.ms_match_id and m.mugm_user_id = s.ms_user_id and s.ms_type = 0) ");
		sql.append("where m.mugm_match_id = "+matchId );
		sql.append(" and m.mugm_is_del != 1 ");
		sql.append(" AND ( " +
				" (s.ms_hole_num NOT IN :beforeHoleNum " +
				" and s.ms_hole_name = :beforeHoleName) " +
				" or " +
				" (s.ms_hole_num NOT IN :afterHoleNum " +
				" and s.ms_hole_name = :afterHoleName) " +
				") ");
		sql.append(" GROUP BY m.mugm_user_id " );
		sql.append(" )score LEFT JOIN user_info AS u ON score.uiId = u.ui_id ");
		sql.append("ORDER BY score.sumRodNum !=0 desc,score.sumRodNum ");
		return dao.createSQLQuery(sql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
	}

	//本用户随机抽取的6个洞总杆数
	public Long getRandomSumRod(Long userId,MatchInfo matchInfo, List<Integer> beforeHoleNum, List<Integer> afterHoleNum) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchInfo.getMiId());
		parp.put("beforeHoleName",matchInfo.getMiZoneBeforeNine());
		parp.put("beforeHoleNum",beforeHoleNum);
		parp.put("afterHoleName",matchInfo.getMiZoneAfterNine());
		parp.put("afterHoleNum",afterHoleNum);
		parp.put("userId",userId);
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(s.ms_rod_num) AS sumRodNum " );
		sql.append(" FROM" +
				" match_score AS s " +
				" WHERE " +
				" s.ms_match_id = :matchId ");
		sql.append(" and s.ms_type = 0 ");
		sql.append(" AND ( " +
				" (s.ms_hole_num IN :beforeHoleNum " +
				" and s.ms_hole_name = :beforeHoleName) " +
				" or " +
				" (s.ms_hole_num IN :afterHoleNum " +
				" and s.ms_hole_name = :afterHoleName) " +
				") ");
		sql.append(" and s.ms_user_id = :userId ");
		List<Object> obj = dao.createSQLQuery(sql.toString(), parp);
		if(obj != null && obj.size() >0){
			return obj.get(0) != null ? Long.parseLong(obj.get(0).toString()):0L;
		}
		return 0L;
	}


	//本用户前半场每一种标准杆的最大值（防作弊）
	public List<Map<String, Object>> getMaxRodScore(Long userId, Long matchId) {Map<String, Object> parp = new HashMap<>();
		parp.put("matchId", matchId);
		parp.put("userId", userId);
		StringBuilder sql = new StringBuilder();
		sql.append("select max(s.ms_rod_num) as max_rod_num,s.* " +
				"from match_score as s where s.ms_match_id  = :matchId " +
				"and s.ms_user_id = :userId ");
		sql.append(" and s.ms_type = 0 ");
		/*if(teamId != null){
			sql.append(" and s.ms_team_id = :teamId  ");
		}*/
		sql.append(" group BY s.ms_hole_name,s.ms_hole_standard_rod ");
		List<Map<String, Object>> list = dao.createSQLQuery(sql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
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
}
