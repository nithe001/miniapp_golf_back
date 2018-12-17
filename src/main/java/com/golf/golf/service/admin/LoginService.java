package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.golf.dao.admin.AdminUserDao;
import com.golf.golf.db.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录
 * 
 * @author fanyongqian
 * 2016年9月7日
 */
@Service
public class LoginService implements IBaseService {

    @Autowired
    private AdminUserDao dao;
    
    /**
     * 根据用户名查看此用户是否存在
     * @param name 用户名
     * @return
     */
    public AdminUser getUser(String name){
    	AdminUser user = null;
		StringBuilder hql = new StringBuilder();
		hql.append("FROM AdminUser AS u WHERE u.auIsValid = 1 ");
		Map<String, Object> parp = new HashMap<String,Object>();
		parp.put("username", name);
		hql.append("AND u.auUserName = :username");
		List<AdminUser> list = dao.createQuery(hql.toString(),parp);
		if(list !=null && list.size() > 0){
			user = list.get(0);
		}
    	return user;
    }
    
}
