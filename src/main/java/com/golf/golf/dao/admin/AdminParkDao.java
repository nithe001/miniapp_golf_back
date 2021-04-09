package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nmy on 2016/7/29.
 */
@Repository
public class AdminParkDao extends CommonDao {


	/**
	 * 球场列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM ParkInfo AS p WHERE 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("AND (p.piName LIKE :keyword OR p.piName LIKE :keyword) ");
		}
		if(parp.get("state") != null){
			hql.append("AND (p.piIsValid = :state) ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<ParkInfo>());
			pageInfo.setCount(0);
			return pageInfo;
		}
//		hql.append(" ORDER BY convert(p.piCity ,'GBK'), convert(p.piName ,'GBK') ");
		List<ParkInfo> list = dao.createQuery(hql.toString(), parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);

		return pageInfo;
	}

	/**
	 * 查看球场名是否已经存在
	 * @param name
	 * @return
	 */
	public boolean checkName(String city, String name) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("parkName", name);
		parp.put("city", city);
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT COUNT(*) FROM ParkInfo AS p WHERE p.piName = :parkName and p.piCity = :city ");
		Long count = dao.createCountQuery(hql.toString(), parp);
        return count != null && count.intValue() != 0;
	}

	/**
	 * 根据城市和球场名称获取球场信息
	 * @return
	 */
	public ParkInfo getByCityAndName(String city, String parkName) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("parkName", parkName);
		parp.put("city", city);
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM ParkInfo AS p WHERE p.piName = :parkName and p.piCity = :city ");
		return (ParkInfo)dao.findOne(hql.toString(), parp);
	}

	/**
	 * 根据id获取球场和球区信息
	 * @param id
	 * @return
	 */
	public  List<ParkPartition> getParkZoneById(Long id) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM ParkPartition as p WHERE p.ppPId = "+id);
		return dao.createQuery(hql.toString());
	}

    /**
     * 根据球场球洞信息
     * @param id
     * @return
     */
    public  ParkPartition getParkHole(Long id,String zone,Integer holeNum ) {
        Map<String, Object> parp = new HashMap<String,Object>();
        parp.put("id", id);
        parp.put("holeNum", holeNum);
        if(StringUtils.isNotEmpty(zone)){
            parp.put("zone","%"+zone.trim()+"%");
        }

        StringBuilder hql = new StringBuilder();
        hql.append(" FROM ParkPartition as p WHERE p.ppPId = :id");
        hql.append(" and p.ppName = :zone" );
        hql.append(" and p.ppHoleNum = :holeNum");
        return (ParkPartition)dao.findOne(hql.toString(), parp);
    }
}
