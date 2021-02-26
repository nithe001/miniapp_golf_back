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
//         sql.append(" and s.ms_user_id = 670 ");
		 sql.append(" GROUP BY s.ms_user_id ");
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
}
