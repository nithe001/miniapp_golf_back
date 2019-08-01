package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 导入成绩
 * Created by nmy on 2016/7/29.
 */
@Repository
public class AdminImportDao extends CommonDao {

	/**
	 * 根据球场名称获取球场信息
	 * @return
	 */
	public ParkInfo getParkInfoByName(String parkName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM ParkInfo as p WHERE p.piName = '"+parkName+"'");
		List<ParkInfo> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据球场名称获取球场信息
	 * @return
	 */
	public TeamInfo getTeamInfoByName(String teamName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM TeamInfo as t WHERE t.tiName = '"+teamName+"'");
		List<TeamInfo> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据用户名查询是否存在球队用户mapping
	 * @return
	 */
	public TeamUserMapping getTeamUserMappingByUserId(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM TeamUserMapping as t WHERE t.tumUserId = "+userId);
		List<TeamUserMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据用户名查询是否存在球队用户mapping
	 * @return
	 */
	public MatchInfo getMatchInfoByMatchTitle(String matchTitle) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchInfo as t WHERE t.miTitle = '"+matchTitle+"'");
		List<MatchInfo> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据用户名查询是否存在球队用户mapping
	 * @return
	 */
	public UserInfo getUserByRealName(String userName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM UserInfo as t WHERE t.uiRealName = '"+userName+"'");
		List<UserInfo> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询是否存在比赛用户mapping
	 * @return
	 */
	public MatchUserGroupMapping getMatchUserMapping(Long matchId, Long teamId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchUserGroupMapping as t WHERE t.mugmMatchId = "+matchId);
		hql.append(" and t.mugmTeamId = "+teamId);
		hql.append(" and t.mugmUserId = "+userId);
		List<MatchUserGroupMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询比赛用户的分组
	 * @return
	 */
	public List<Object> getMatchUserMappingGroupList(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select t1.* from (select DISTINCT t.mugm_group_name+0 as gname from match_user_group_mapping as t where t.mugm_match_id =  "+matchId);
		hql.append(" as t1 ORDER BY t1.gname ");
		return dao.createSQLQuery(hql.toString());
	}

	/**
	 * 查询是否存在比赛分组
	 * @return
	 */
	public MatchGroup getMatchGroupByName(Long matchId, String groupName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchGroup as t WHERE t.mgMatchId = "+matchId);
		hql.append(" and t.mgGroupName = "+groupName);
		List<MatchGroup> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取本球场第j洞的详情
	 * @return
	 */
	public ParkPartition getParkPartition(Long miParkId, Integer holeNum, String holeName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM ParkPartition as t WHERE t.ppPId = "+miParkId);
		hql.append(" and t.ppHoleNum = "+holeNum);
		hql.append(" and t.ppName = '"+holeName+"'");
		List<ParkPartition> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取该用户是否有该洞的成绩
	 * @return
	 */
	public MatchScore getMatchScoreByUser(Long teamId, Long matchId, String groupName, String userName, Integer holeNum,
										  String holeName,Integer beforeAfter) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchScore as t WHERE t.msTeamId = "+teamId);
		hql.append(" and t.msMatchId = "+matchId);
		hql.append(" and t.msGroupName = '"+groupName+"'");
		hql.append(" and t.msUserName = '"+userName+"'");
		hql.append(" and t.msBeforeAfter = "+beforeAfter);
		hql.append(" and t.msHoleNum = "+holeNum);
		hql.append(" and t.msHoleName = '"+holeName+"'");
		List<MatchScore> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}
}
