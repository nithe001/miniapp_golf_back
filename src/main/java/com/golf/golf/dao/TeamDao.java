package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.ParkPartition;
import com.golf.golf.db.TeamInfo;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台-球队
 * Created by nmy on 2017/7/1.
 */
@Repository
public class TeamDao extends CommonDao {

    /**
     * 获取所有球队列表
     * @return
     */
    public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("from team_info as t LEFT JOIN ( ");
		hql.append("select count(tm.tum_user_id) as userCount,tm.tum_team_id as tum_team_id ");
		hql.append("from team_user_mapping as tm where 1=1 GROUP BY tum_team_id ");
		hql.append(")as tum on t.ti_id = tum.tum_team_id ");
		hql.append("where 1=1 ");

        if(parp.get("keyword") != null){
            hql.append("AND t.ti_name LIKE :keyword ");
        }

        Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(null);
            pageInfo.setCount(0);
            return pageInfo;
        }
		hql.append("ORDER BY t.ti_create_time desc ");
		List<Map<String, Object>> list = dao.createSQLQuery("select t.ti_id as ti_id,t.ti_name as tiName,tum.*,t.ti_create_time as ti_create_time,t.ti_logo as logo " + hql.toString(),
				parp, pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }

    /**
     * 获取  我加入的球队列表 或者  可以加入的球队
	 * type 0：所有球队   1：可以加入的球队  2：我加入的球队
     * @return
     */
    public POJOPageInfo getMyTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuilder hql = new StringBuilder();
        hql.append("from team_info as t LEFT JOIN team_user_mapping as tm on t.ti_id = tm.tum_team_id ");
		hql.append("where 1=1 ");
        if((Integer)parp.get("type") == 1){
			hql.append("and t.ti_id not in (select m.tum_team_id from team_user_mapping as m where m.tum_user_id = :userId) ");
		}else if((Integer)parp.get("type") == 2){
			//我加入的球队
			hql.append("and t.ti_id in (select m.tum_team_id from team_user_mapping as m where m.tum_user_id = :userId) ");
		}

        if(parp.get("keyword") != null){
            hql.append("AND t.ti_name LIKE :keyword ");
        }

        Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<Object[]>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        hql.append("GROUP BY t.ti_id ");
		hql.append("ORDER BY t.ti_create_time DESC");

		List<Map<String, Object>> list = dao.createSQLQuery("select t.ti_id as tiId,t.ti_name as tiName, count(tm.tum_user_id) as userCount,t.ti_logo as logo " + hql.toString(),
				parp,pageInfo.getStart(), pageInfo.getRowsPerPage(), Transformers.ALIAS_TO_ENTITY_MAP);
        pageInfo.setCount(count.intValue());
        pageInfo.setItems(list);
        return pageInfo;
    }

	/**
	 * 通过球队id获取队长
	 * @return
	 */
	public List<String> getCaptainByTeamId(Long teamId) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT u.uiRealName FROM TeamUserMapping AS t,UserInfo as u WHERE 1=1 " +
				"AND t.tumTeamId = "+teamId+" AND t.tumUserType = 1 AND t.tumUserId = u.uiId ");
		return dao.createQuery(hql.toString());
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


	/**
	 * 获取我创建的球队列表
	 * @return
	 */
	public POJOPageInfo getMyCreateTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM TeamUserMapping AS tm,TeamInfo AS t WHERE 1=1 ");
		hql.append("AND tm.tumTeamId = t.tiId ");
		hql.append("and tm.tumUserId = :userId ");
		if((Integer)parp.get("type") == 1){
			//我创建的球队
			hql.append("and tm.tumUserType = :type ");
		}
		hql.append("GROUP BY tm.tumTeamId ");

		if(parp.get("keyword") != null){
			hql.append("AND t.tiName LIKE :keyword ");
		}

		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(null);
			pageInfo.setCount(0);
			return pageInfo;
		}
		hql.append("ORDER BY t.tiCreateTime desc ");
		List<Map<String, Object>> list = dao.createQuery("select t.tiId as ti_id,t.tiName AS tiName,count(tm.tumUserId) AS userCount,t.tiLogo as logo " + hql.toString(),
				parp, pageInfo.getStart(), pageInfo.getRowsPerPage(),Transformers.ALIAS_TO_ENTITY_MAP);
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);
		return pageInfo;
	}
}
