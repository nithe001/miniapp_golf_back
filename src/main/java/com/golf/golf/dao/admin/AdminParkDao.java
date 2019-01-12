package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
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
		String order = "ORDER BY p.piCreateTime DESC";
		List<ParkInfo> list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);

		return pageInfo;
	}

	/**
	 * 查看球场名是否已经存在
	 * @param name
	 * @return
	 */
	public boolean checkName(String name) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("parkName", name);
		String sql = "SELECT COUNT(*) FROM ParkInfo AS p WHERE p.piName = :auUserName";
		Long count = dao.createCountQuery(sql,parp);
		if(count == null || count.intValue() == 0){
			return false;
		}else{
			//已存在
			return true;
		}
	}
}
