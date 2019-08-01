package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 赛事活动
 * @author nmy
 * 2017年05月08日
 */
@Repository
public class AdminMatchDao extends CommonDao {

	/**
	 * 赛事活动列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo matchList(SearchBean searchBean, POJOPageInfo pageInfo){
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS m WHERE 1=1 ");
		//状态 进行中、已结束
		if(parp.get("state") != null){
			hql.append("AND m.miIsEnd = :state ");
		}
		if(parp.get("keyword") != null){
			hql.append("AND m.miTitle LIKE :keyword ");
		}
		if(parp.get("type") != null){
			hql.append("AND m.miType = :type ");
		}
		if(parp.get("isValid") != null){
			hql.append("AND m.miIsValid = :isValid ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY m.miCreateTime DESC ");
		List<MatchInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}



	/**
	 * 高球规则列表
	 * @return
	 */
	public POJOPageInfo matchRuleList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchRule AS r WHERE 1=1 ");
		if(parp.get("keywords") != null){
			hql.append("AND r.mrTitle LIKE :keyword ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchRule>());
			pageInfo.setCount(0);
			return pageInfo;
		}
//		hql.append("ORDER BY r.mrCreateTime DESC ");
		List<MatchRule> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

    /**
     * 删除比赛对应的用户mapping
     * @return
     */
    public void delMatchUserMapping(Long matchId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM MatchUserGroupMapping AS t WHERE " +
                "t.mugmMatchId= "+matchId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除比赛对应的用户比分
     * @return
     */
    public void delMatchScore(Long matchId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM MatchScore AS t WHERE t.msMatchId = "+matchId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除比赛分组
     * @return
     */
    public void delMatchGroup(Long matchId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM MatchGroup AS t WHERE t.mgMatchId = "+matchId);
        dao.executeHql(hql.toString());
    }

    /**
     * 删除比赛球队确认配置
     * @return
     */
    public void delMatchScoreConfig(Long matchId) {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM IntegralConfig AS t WHERE t.icMatchId = "+matchId);
        dao.executeHql(hql.toString());
    }

	/**
	 * 删除比赛输赢情况
	 * @return
	 */
	public void delMatchWinOrLose(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchHoleResult AS t WHERE t.mhrMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 删除比赛观战信息
	 * @return
	 */
	public void delMatchWatchInfo(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchJoinWatchInfo AS t WHERE t.mjwiMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}


	/**
	 * 删除比赛对应的邀请记分信息
	 * @return
	 */
	public void delMatchUserApplyScoreInfo(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchScoreUserMapping AS t WHERE t.msumMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 删除比赛对应的用户积分信息
	 * @return
	 */
	public void delMatchUserPointInfo(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM TeamUserPoint AS t WHERE t.tupMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 删除比赛对应的生成二维码信息
	 * @return
	 */
	public void delMatchQRCodeInfo(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchUserQrcode AS t WHERE t.muqMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 获取比赛对应的二维码信息
	 * @return
	 */
	public List<MatchUserQrcode> getMatchUserQRCodeList(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchUserQrcode as t WHERE t.muqMatchId = "+matchId);
		return dao.createQuery(hql.toString());
	}

	/**
	 * 删除比赛对应的扫描二维码信息
	 * @return
	 */
	public void delScanMatchQRCodeInfo(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchScoreUserMapping AS t WHERE t.msumMatchId = "+matchId);
		dao.executeHql(hql.toString());
	}


	/**
	 * 设为赛长、取消设为赛长
	 * @param matchId 比赛id
	 * @param userId 用户id
	 * @param type 类型 0：设为队长  1：取消设为队长
	 * @return
	 */
	public void updateUserType(Long matchId, Long userId, Integer type) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE FROM MatchUserGroupMapping AS t set t.mugmUserType = "+type);
		hql.append(" WHERE t.mugmMatchId= "+matchId);
		hql.append(" and t.mugmUserId= "+userId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 移出比赛
	 * @param matchId 比赛id
	 * @param userId 用户id
	 * @return
	 */
	public void delUserFromTeamUserMapping(Long matchId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchGroupUserMapping AS t WHERE t.mugmMatchId= "+matchId);
		hql.append(" and t.mugmUserId= "+userId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 获取参赛队员列表
	 * @return
	 */
	public List<Map<String,Object>> getMatchUserGroupMappingList(Long matchId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT g.mugmUserId as userId,g.mugmGroupName+0 as groupName,g.mugmUserName as userName,t.tiAbbrev as teamAbbrev,g.mugmUserType as userType ");
		hql.append(" FROM MatchUserGroupMapping as g,TeamInfo as t WHERE g.mugmTeamId = t.tiId and g.mugmMatchId = "+matchId);
		hql.append(" order by groupName ");
		return dao.createQuery(hql.toString(),Transformers.ALIAS_TO_ENTITY_MAP);
	}
}
