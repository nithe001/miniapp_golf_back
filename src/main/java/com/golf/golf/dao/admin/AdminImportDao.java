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
	public Long getTeamUserMappingByUserName(String userName) {
		StringBuilder hql = new StringBuilder();
		hql.append("select count(*) FROM TeamUserMapping as t,UserInfo AS u WHERE t.tumUserId = u.uiId and u.uiRealName = '"+userName+"'");
		return dao.createCountQuery(hql.toString());
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
}
