package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import com.golf.golf.dao.TeamDao;



import javax.xml.crypto.dsig.Transform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * 获取球队
	 * @return
	 */
	public TeamInfo getTeamInfoByName(String teamName,String teamNameAbbrev) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM TeamInfo as t WHERE t.tiName = '"+teamName+"'");
		hql.append(" and t.tiAbbrev = '"+teamNameAbbrev+"'");
		List<TeamInfo> list = dao.createQuery(hql.toString());

		if(list != null && list.size() >0){

				return list.get(0);
			}
		return null;
	}

	/**
	 * 获取球队
	 * @return
	 */
	public TeamInfo getTeamInfoByAbbrevName(String teamNameAbbrev, List<Long> joinTeamIdList) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("teamNameAbbrev",teamNameAbbrev);
		parp.put("joinTeamIdList",joinTeamIdList);
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM TeamInfo as t WHERE t.tiAbbrev = :teamNameAbbrev");
		hql.append(" and t.tiId in (:joinTeamIdList)");
		List<TeamInfo> list = dao.createQuery(hql.toString(),parp);
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据用户ID及球队ID查询是否存在球队用户mapping
	 * @return
	 */
	public TeamUserMapping getTeamUserMappingByTeamId(Long teamId,Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM TeamUserMapping as t WHERE t.tumTeamId = "+teamId+" and t.tumUserId = "+userId);
		List<TeamUserMapping> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

    /**
     * 根据用户ID及所有参赛队查询是否存在球队用户mapping，有多个只取第一个
     * @return
     */
    public Map<String,Object> findTeamUserMappingByUserName(String userName, List<Long> joinTeamIdList) {
        Map<String,Object> parp = new HashMap<>();
        parp.put("userName",userName);
        parp.put("joinTeamIdList",joinTeamIdList);
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT  t.tumUserId as userId, t.tumTeamId as teamId, t.tumUserType as userType FROM TeamUserMapping as t, UserInfo as u WHERE u.uiRealName = :userName and t.tumUserId = u.uiId ");
        hql.append(" and t.tumTeamId in (:joinTeamIdList)");
        List<Map<String,Object>> list = dao.createQuery(hql.toString(),parp,Transformers.ALIAS_TO_ENTITY_MAP);
        if(list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }


    /**
	 * 根据用户名查询是否存在球队用户mapping
	 * @return
	 */
	public MatchInfo getMatchInfoByMatchTitle(String matchTitle,String matchTime) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchInfo as t WHERE t.miTitle = '"+matchTitle+"'");
		hql.append(" and t.miMatchTime = '"+matchTime+"'");
		List<MatchInfo> list = dao.createQuery(hql.toString());
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}


	/**
	 * 根据用户名查询用户名单
	 * @return

	public List<UserInfo> getUserListByRealName(String userName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM UserInfo as t WHERE t.uiRealName = '"+userName+"'");
		return dao.createQuery(hql.toString());
	}
*/
	/**
	 * 根据用户名查询是否存在球队用户mapping
	 * @return
	 */
	public Map<String,Object> getUserByRealNameTeamName(String userName, String teamName, String teamAbbrev) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiId as userId,t.tiId as teamId, tum.tumUserType as userType FROM TeamUserMapping as tum , UserInfo as u,TeamInfo as t WHERE tum.tumUserId = u.uiId ");
		hql.append(" and tum.tumTeamId = t.tiId ");
		hql.append(" and u.uiRealName = '"+userName+"'");
		hql.append(" and t.tiName = '"+teamName+"'");
		hql.append(" and t.tiAbbrev = '"+teamAbbrev+"'");
		List<Map<String,Object>> list = dao.createQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
		if(list != null && list.size() >0){
			return list.get(0);
		}
		return null;
	}

    /**
     * 根据用户名查询整个系统中是否存在该用户
     * @return
     */
    public Map<String,Object> getUserByRealName(String userName) {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT u.uiId as userId FROM UserInfo as u WHERE  u.uiRealName = '"+userName+"'");
        List<Map<String,Object>> list = dao.createQuery(hql.toString(), Transformers.ALIAS_TO_ENTITY_MAP);
        if(list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }
	/**
	 * 查询是否存在比赛用户mapping
	 * @return
	 */
	public MatchUserGroupMapping getMatchUserMapping(Long matchId, Long teamId,  Long userId) {
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
	 * 不用组名做判断 nhq
	 * @return
	 */
	public MatchScore getMatchScoreByUser(Long teamId, Long matchId, String groupName, Long userId, Integer holeNum,
										  String holeName,Integer beforeAfter) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchScore as t WHERE t.msTeamId = "+teamId);
		hql.append(" and t.msMatchId = "+matchId);
		//hql.append(" and t.msGroupName = '"+groupName+"'");
		hql.append(" and t.msUserId = "+userId);
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
