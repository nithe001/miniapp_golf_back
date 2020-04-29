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

	//本比赛的所有用户和其去掉随机杆后的总杆数，为0的排后面(首列显示)
	 public List<Map<String, Object>> getUserListByMatchId(MatchInfo matchInfo,
														   List<Integer> beforeHoleNum, List<Integer> afterHoleNum) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("matchId",matchInfo.getMiId());
		parp.put("beforeHoleName",matchInfo.getMiZoneBeforeNine());
		 parp.put("beforeHoleNum",beforeHoleNum);
		parp.put("afterHoleName",matchInfo.getMiZoneAfterNine());
		parp.put("afterHoleNum",afterHoleNum);
		StringBuilder sql = new StringBuilder();
		 sql.append("select " +
				 " s.ms_team_id as team_id," +
				 "s.ms_match_id as match_id," +
				 "s.ms_match_title as match_title," +
				 "s.ms_group_id as group_id," +
				 "s.ms_user_id as uiId," +
				 "s.ms_user_name as uiRealName," +
				 "sum(s.ms_rod_num) AS sumRodNum " );
		 sql.append(" FROM" +
				 " match_score AS s " +
				 " WHERE " +
				 " s.ms_match_id = :matchId ");
		 sql.append(" AND ( " +
				 " (s.ms_hole_num NOT IN :beforeHoleNum " +
				 " and s.ms_hole_name = :beforeHoleName) " +
				 " or " +
				 " (s.ms_hole_num NOT IN :afterHoleNum " +
				 " and s.ms_hole_name = :afterHoleName) " +
				 ") ");
		 sql.append(" GROUP BY s.ms_user_id " +
				 " ORDER BY" +
				 " sum(s.ms_rod_num) != 0 DESC, " +
				 " sum(s.ms_rod_num) ");
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
}
