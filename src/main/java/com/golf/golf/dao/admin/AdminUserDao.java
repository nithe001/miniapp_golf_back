package com.golf.golf.dao.admin;

import com.golf.common.db.CommonDao;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nmy on 2016/7/29.
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
	 * 前台微信用户列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getWechatUserList(SearchBean searchBean, POJOPageInfo pageInfo) {
		Map<String, Object> parp = searchBean.getParps();
		StringBuffer hql = new StringBuffer();
		hql.append(" from wechat_user_info as wu left join user_info as u on wu.wui_u_id = u.ui_id ");
		hql.append(" where 1=1 ");
		if(parp.get("keyword") != null){
			hql.append("and (wu.wui_nick_name like :keyword or u.ui_real_name like :keyword) ");
		}
		if(parp.get("startDate") != null){
			hql.append(" and wu.create_time >=:startDate ");
		}
		if(parp.get("endDate") != null){
			hql.append(" and wu.create_time <=:endDate ");
		}
		Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
		if (count == null || count.intValue() == 0) {
			pageInfo.setItems(new ArrayList<Map<String, Object>>());
			pageInfo.setCount(0);
			return pageInfo;
		}
		String select ="select wu.wui_id as wui_id,wu.wui_headimgurl as wui_headimg,wu.wui_nick_name as wui_nick_name,wu.wui_openid as wui_openid," +
				"u.ui_real_name as ui_real_name,wu.wui_sex as wui_sex,wu.wui_province as wui_province," +
				"wu.wui_city as wui_city,wu.create_time as create_time, u.ui_type as ui_type,wu.wui_is_valid as wui_is_valid ";
		hql.append("group by wu.wui_id order by wu.create_time ");
		List<Map<String, Object>> list = dao.createSQLQuery(select+hql.toString(),parp, pageInfo.getStart(),
														pageInfo.getRowsPerPage(), Transformers.ALIAS_TO_ENTITY_MAP);
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
    public POJOPageInfo getMiniappUserList(SearchBean searchBean, POJOPageInfo pageInfo) {
        Map<String, Object> parp = searchBean.getParps();
        StringBuffer hql = new StringBuffer();
        hql.append(" from user_info as u  ");
        hql.append(" where 1=1 ");
        if(parp.get("keyword") != null){
            hql.append("and  u.ui_real_name like :keyword  ");
        }
        if(parp.get("startDate") != null){
            hql.append(" and u.create_time >=:startDate ");
        }
        if(parp.get("endDate") != null){
            hql.append(" and u.create_time <=:endDate ");
        }
        Long count = dao.createSQLCountQuery("SELECT COUNT(*) "+hql.toString(), parp);
        if (count == null || count.intValue() == 0) {
            pageInfo.setItems(new ArrayList<Map<String, Object>>());
            pageInfo.setCount(0);
            return pageInfo;
        }
        String select ="select u.ui_id as ui_id,u.ui_headimg  as ui_headimg,u.ui_nick_name as ui_nick_name,u.ui_open_id as ui_openid, " +
                "u.ui_real_name as ui_real_name,u.ui_sex as ui_sex,  " +
                "u.ui_create_time as ui_create_time, u.ui_type as ui_type,u.ui_is_valid as ui_is_valid ";
        hql.append("group by u.ui_id order by u.ui_create_time , u.ui_open_id ");
        List<Map<String, Object>> list = dao.createSQLQuery(select+hql.toString(),parp, pageInfo.getStart(),
                pageInfo.getRowsPerPage(), Transformers.ALIAS_TO_ENTITY_MAP);
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
        return count != null && count.intValue() != 0;
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

	//导入用户ID查用户信息-

    public UserInfo getUserById(Long userId) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM UserInfo as t  WHERE  t.uiId = " +userId);
        List <UserInfo> list = dao.createQuery(hql.toString());
        if(list == null || list.size() ==0){
            return null;
        }
        return list.get(0);
    }
}
