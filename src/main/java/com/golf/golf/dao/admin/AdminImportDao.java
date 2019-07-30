package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.ParkInfo;
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
}
