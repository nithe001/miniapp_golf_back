package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
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
     * @return
     */
    public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM TeamInfo AS t WHERE 1=1 ");
        if(parp.get("keyword") != null){
            hql.append("AND t.tiTeamName LIKE :keyword ");
        }

        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<TeamInfo>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        String order ="ORDER BY t.tiCreateTime DESC";
        List<TeamInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }

    /**
     * 获取我加入的球队列表
     * @return
     */
    public POJOPageInfo getMyTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT count(tm.tumUserId),t.tiId,t.tiName ");
        hql.append("FROM TeamInfo AS t, TeamUserMapping as tm WHERE 1=1 ");
        hql.append("AND t.tiId = tm.tumTeamId ");
        hql.append("AND tm.tumUserId = :userId ");

        hql.append("AND (tm.tumType = 1 OR tm.tumUserType = 1) ");
        if(parp.get("keyword") != null){
            hql.append("AND t.tiTeamName LIKE :keyword ");
        }

        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<Object[]>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        hql.append("GROUP BY tm.tumId ");
        String order ="ORDER BY tm.tumCreateTime DESC";
        List<Object[]> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }
}
