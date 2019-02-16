package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.ParkPartition;
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

    /**
     * 查询球场列表-所有球场
     * @return
     */
    public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM ParkInfo AS p WHERE 1=1 ");
        if(parp.get("keyword") != null){
            hql.append("AND p.piName LIKE :keyword  ");
        }

        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<ParkInfo>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        hql.append("GROUP BY p.piCreateTime ");
        List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }

    /**
     * 查询球场列表-附近球场
     * @return
     */
    public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM ParkInfo AS p WHERE 1=1 ");
        if(parp.get("keyword") != null){
            hql.append("AND p.piName LIKE :keyword  ");
        }
       /* searchBean.addParpField("minlng", minlng);
        searchBean.addParpField("maxlng", maxlng);
        searchBean.addParpField("minlat", minlat);
        searchBean.addParpField("maxlat", maxlat);*/
//        String hql = "from Property where longitude>=? and longitude =<? and latitude>=? latitude=<? and state=0";

        hql.append("AND p.piLng >= :minlng ");
        hql.append("AND p.piLng <= :maxlng ");
        hql.append("AND p.piLat >= :minlat ");
        hql.append("AND p.piLat <= :maxlat ");

        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<ParkInfo>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        hql.append("GROUP BY p.piCreateTime ");
        List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }

    /**
     * 查询球场区域
     * @return
     */
    public POJOPageInfo getParkListByRegion(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("select DISTINCT p.piCity from ParkInfo as p where p.piIsValid = 1");
        List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setItems(list);
        return pageInfo;
    }


    /**
     * 查询该区域下的球场
     * @return
     */
    public POJOPageInfo getParkListByRegionName(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append(" from ParkInfo as p where p.piIsValid = 1");
        if(parp.get("keyword") != null){
            hql.append("AND p.piName LIKE :keyword  ");
        }
        hql.append("AND p.piCity = :regionName  ");

        Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<ParkInfo>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        hql.append("GROUP BY p.piCreateTime ");
        List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }








    /**
     * 创建比赛—点击球场-获取分区和洞
     * @return
     */
    public List<ParkPartition> getParkZoneAndHole(Long parkId) {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT DISTINCT p.ppName FROM ParkPartition AS p WHERE 1=1 ");
        hql.append("AND p.ppPId = " +parkId);
        return dao.createQuery(hql.toString());
    }

}
