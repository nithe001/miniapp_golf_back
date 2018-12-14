package com.kingyee.golf.dao.admin;

import com.kingyee.common.db.CommonDao;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;
import com.kingyee.golf.db.AdminUser;
import com.kingyee.golf.db.UserInfo;
import com.kingyee.golf.db.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ph on 2016/7/29.
 */
@Repository
public class AdminUserDao extends CommonDao {


	/**
	 * 管理员用户列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo<AdminUser> getAdminUserList(SearchBean searchBean, POJOPageInfo<AdminUser> pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuilder hql = new StringBuilder();
		hql.append("FROM AdminUser AS user WHERE 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("AND (user.auUserName LIKE :keyword OR user.auShowName LIKE :keyword) ");
		}
		if(parp.get("State") != null){//状态
			hql.append("AND user.auIsValid = :State ");
		}
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<AdminUser>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order = "ORDER BY user.auCreateDate DESC";
		List<AdminUser> list = new ArrayList<AdminUser>();
		list = dao.createQuery(hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);

		return pageInfo;
	}

	/**
	 * 前台用户列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getWechatUserList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuffer hql = new StringBuffer();
		hql.append(" from cm_wechat_user as wu left join cm_user as u ");
		hql.append(" on wu.cwu_cu_id = u.cu_id where 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("and (wu.cwu_nickname like :keyword or u.cu_user_name like :keyword) ");
		}
		//关注状态（0：未关注；1：关注）
		if(parp.get("state") != null){
			hql.append("and wu.cwu_subscribe = :state ");
		}
		if(parp.get("startDate") != null){
			hql.append(" and wu.cwu_create_time >=:startDate ");
		}
		if(parp.get("endDate") != null){
			hql.append(" and wu.cwu_create_time <=:endDate ");
		}
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<Object[]>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String order = "order by wu.cwu_create_time desc";
		String select = "select wu.cwu_id,wu.cwu_cu_id,wu.cwu_openid,wu.cwu_nickname,u.cu_user_name," +
				"wu.cwu_sex,wu.cwu_province,wu.cwu_city,wu.cwu_subscribe,wu.cwu_subscribe_time ";
		List<Object[]> list = dao.createSQLQuery(select+hql.toString()+order, parp, pageInfo.getStart(), pageInfo.getRowsPerPage());
		pageInfo.setCount(count.intValue());
		pageInfo.setItems(list);

		return pageInfo;
	}


	/**
	 * 查看用户名是否已经存在
	 * @param name
	 * @return
	 */
	public boolean checkName(String name) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("auUserName", name);
		String sql = "SELECT COUNT(*) FROM AdminUser u WHERE u.auUserName = :auUserName";
		Long count = dao.createCountQuery(sql,parp);
		if(count == null || count.intValue() == 0){
			return false;
		}else{
			//用户名已存在
			return true;
		}
	}

	//用户id获取用户微信信息
	public WechatUserInfo getWechatUserByUserId(Long id) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("cuId", id);
		String sql = "FROM WechatUserInfo AS u WHERE u.cwuCuId = :cuId";
		List<WechatUserInfo> list = dao.createQuery(sql,parp);
		if(list == null || list.size() ==0){
			return null;
		}
		return list.get(0);
	}

	//导入用户-根据信息查询用户是否存在
	public UserInfo getUserByInfo(String userName, String hospital, String telNo) {
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("userName", userName.replaceAll("\"",""));
		String sql = "FROM UserInfo AS t WHERE t.cuUserName = :userName ";
		if(StringUtils.isNotEmpty(telNo)){
			parp.put("telNo", telNo);
			sql = sql+"AND t.cuTelNo = :telNo ";
		}
		if(StringUtils.isNotEmpty(hospital)){
			parp.put("hospital", hospital);
			sql = sql+"AND t.cuHospital = :hospital ";
		}
		List<UserInfo> list = dao.createQuery(sql,parp);
		if(list == null || list.size() ==0){
			return null;
		}
		return list.get(0);
	}
}
