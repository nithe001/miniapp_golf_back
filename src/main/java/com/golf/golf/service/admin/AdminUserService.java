package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.EncryptUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminUserDao;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理
 * 
 * @author fanyongqian
 * 2016年10月31日
 */
@Service
public class AdminUserService implements IBaseService {
	
    @Autowired
    private AdminUserDao dao;

    /**
     * 管理员用户列表
     * @param searchBean
     * @param pageInfo
     * @return
     */
    public POJOPageInfo getAdminUserList(SearchBean searchBean, POJOPageInfo pageInfo){
    	return dao.getAdminUserList(searchBean,pageInfo);
    }

	/**
	 * 微信用户列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getWechatUserList(SearchBean searchBean, POJOPageInfo pageInfo){
		return dao.getWechatUserList(searchBean,pageInfo);
	}

    /**
     * 保存用户
     * @param user
     * @return
     */
    public Long save(AdminUser user){
        user.setAuCreateUserId(AdminUserUtil.getUserId());
        user.setAuCreateUserName(AdminUserUtil.getShowName());
        user.setAuCreateDate(System.currentTimeMillis());
        user.setAuIsValid(1);
        user.setAuPassword(EncryptUtil.getSHA256Value(user.getAuPassword()));
        return dao.save(user);
    }

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    public AdminUser getById(Long id){
        return dao.get(AdminUser.class, id);
    }

    /**
     * 更新用户信息
     * @param user
     */
    public void edit(AdminUser user){
        AdminUser db = getById(user.getAuId());
        if(StringUtils.isNotEmpty(user.getAuPassword())){
            db.setAuPassword(EncryptUtil.getSHA256Value(user.getAuPassword()));
        }
        db.setAuShowName(user.getAuShowName());
        db.setAuEmail(user.getAuEmail());
        db.setAuUpdateUserId(AdminUserUtil.getUserId());
        db.setAuUpdateUserName(AdminUserUtil.getShowName());
        db.setAuUpdateDate(System.currentTimeMillis());
        dao.update(db);
    }

    /**
     * 更新信息
     * @param table
     */
    public void update(Object table) {
        dao.update(table);
    }

    /**
     * 查看用户名是否已经存在
     * @param name
     * @return
     */
    public boolean checkName(String name){
    	return dao.checkName(name);
    }

    //新增前台用户
	public void saveWechatUser(UserInfo user) {
		dao.save(user);
	}

	//获取前台用户信息+微信信息
	public Map<String, Object> getWechatUserById(Long id) {
		Map<String, Object> parp = new HashMap<String,Object>();
		WechatUserInfo wechatUser = dao.getWechatUserByUserId(id);
		UserInfo UserInfo = dao.get(UserInfo.class, id);
		parp.put("wechatUser",wechatUser);
		parp.put("UserInfo",UserInfo);
		return parp;
	}

	//编辑-保存
	public void editWechatUser(UserInfo user) {
		UserInfo db = dao.get(UserInfo.class,user.getId());
		db.setType(user.getType());
		db.setClub(user.getClub());
		dao.update(db);
	}

	//导入用户-根据信息查询用户是否存在
	public UserInfo getUserByInfo(String userName, String hospital, String telNo) {
		return dao.getUserByInfo(userName,hospital,telNo);
	}

	public void saveUser(UserInfo user) {
		dao.save(user);
	}

	/**
	 * 设置微信用户为管理员
	 * @return
	 */
	public void setAdmin(Long userId) {
	}
}
