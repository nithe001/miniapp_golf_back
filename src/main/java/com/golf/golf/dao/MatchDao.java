package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import org.springframework.stereotype.Repository;

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
	 * 获取全部比赛列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchInfo AS m WHERE 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("AND m.miTitle LIKE :keyword ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order ="ORDER BY t.matchTime DESC,t.applyEndTime ASC";
		List<MatchInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}

	/**
	 * 获取我参加的比赛列表
	 * @return
	 */
	public POJOPageInfo getMyMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM MatchInfo AS m, MatchJoinWatchInfo AS j WHERE 1=1 ");
        hql.append(" AND m.miId = j.mjwiMatchId and j.mjwiUserId = :userId ");
        if(parp.get("keyword") != null){
            hql.append("AND m.miTitle LIKE :keyword ");
        }
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<MatchInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order ="ORDER BY t.matchTime DESC,t.applyEndTime ASC";
		List<MatchInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}



    /**
     * 获取本比赛的围观用户列表
     * 类型：0：观战 1：报名
     * @return
     */
    public List<Object[]> getWatchUserListByMatchId(Long matchId) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM MatchJoinWatchInfo AS j,UserInfo AS u WHERE 1=1 ");
        hql.append(" and j.mjwiUserId = u.uiId and j.mjwiType = 1 ");
        hql.append(" and j.mjwiMatchId = "+matchId);
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
        if (count == null || count.intValue() == 0) {
            return null;
        }
        String order =" ORDER BY j.mjwiCreateTime DESC ";
        return dao.createQuery(hql.toString()+order);
    }

    /**
     * 获取本比赛的队伍
     * @return
     */
    public List<Object[]> getMatchGroupList(Long matchId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("matchId",matchId);
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchUserGroupMapping AS g,UserInfo as u WHERE 1=1 ");
        hql.append("AND g.mgiMatchId = :matchId AND g.mgiUserId = u.uiId ");
        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(),parp);
        if(count == 0){
            return null;
        }
        hql.append("ORDER BY g.mgiCreateTime DESC ");
        return dao.createQuery(hql.toString(),parp);
    }

    /**
     * 判断是否赛长
     * @return
     */
    public Long getIsCaptain(SearchBean searchBean) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM MatchUserGroupMapping AS m WHERE 1=1 ");
        hql.append("AND m.mugmMatchId = :matchId ");
        hql.append("AND m.mugmIsCaptain = 1 ");
        hql.append("AND m.mugmUserId = :scorerId ");
        return dao.createCountQuery("SELECT COUNT(*) "+hql.toString(),parp);
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
}
