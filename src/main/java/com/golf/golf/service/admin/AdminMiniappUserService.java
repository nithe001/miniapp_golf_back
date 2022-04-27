package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.EncryptUtil;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.UserDao;
import com.golf.golf.dao.admin.AdminUserDao;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 * 
 * @author fanyongqian
 * 2016年10月31日
 */
@Service
public class AdminMiniappUserService implements IBaseService {
	
    @Autowired
    private AdminUserDao dao;
    @Autowired
    private UserDao userDao;
    @Autowired
	private MatchService matchService;
    @Autowired
    private UserService userService;


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
	 * 小程序用户列表
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getMiniappUserList(SearchBean searchBean, POJOPageInfo pageInfo){
		pageInfo = dao.getMiniappUserList(searchBean,pageInfo);
		if(pageInfo.getCount() >0){
			updatePageInfo(pageInfo);
		}
		return pageInfo;
	}

	private void updatePageInfo(POJOPageInfo pageInfo) {
		for(Map<String, Object> result:(List<Map<String, Object>>)pageInfo.getItems()){
			Long createTime = matchService.getLongValue(result,"create_time");
			if(createTime != null){
				result.put("create_time",TimeUtil.longToString(createTime, TimeUtil.FORMAT_DATETIME_HH_MM));
			}
		}
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
		db.setAuRole(user.getAuRole());
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


	//获取前台用户微信信息 + 个人信息
	public Map<String, Object> getMiniappUserById(Long userId) {
		Map<String, Object> parp = new HashMap<String,Object>();
		UserInfo  user = dao.get(UserInfo.class,userId);
		parp.put("userInfo",user);
		return parp;
	}


	/**
	 * 设置微信用户为赛事管理员
	 * @return
	 */
	public void setAdmin(Long userId) {
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public UserInfo getMiniappUser(Long userId) {
		return dao.get(UserInfo.class,userId);
	}

	/**
	 * 编辑——更新微信用户详细信息
	 * @param user
	 * @return
	 */
	public void updateUserInfo(UserInfo user) {
		if(user.getUiId() != null){
			UserInfo db = dao.get(UserInfo.class,user.getUiId());
			db.setUiRealName(user.getUiRealName());
			db.setUiOpenId(user.getUiOpenId());
			db.setUiType(user.getUiType());
			db.setUiSex(user.getUiSex());
			db.setUiTelNo(user.getUiTelNo());
			db.setUiEmail(user.getUiEmail());
			db.setUiGraduateSchool(user.getUiGraduateSchool());
			db.setUiGraduateDepartment(user.getUiGraduateDepartment());
			db.setUiGraduateTime(user.getUiGraduateTime());
			db.setUiMajor(user.getUiMajor());
			db.setUiStudentId(user.getUiStudentId());
			db.setUiWorkUnit(user.getUiWorkUnit());
			db.setUiOccupation(user.getUiOccupation());
			db.setUiAddress(user.getUiAddress());
			db.setUiHomeCourse(user.getUiHomeCourse());
			db.setUiType(user.getUiType());
			user.setUiUpdateUserName(AdminUserUtil.getShowName());
			user.setUiUpdateUserId(AdminUserUtil.getUserId());
			user.setUiUpdateTime(System.currentTimeMillis());
			dao.update(db);
		}else{
			user.setUiCreateUserName(AdminUserUtil.getShowName());
			user.setUiCreateUserId(AdminUserUtil.getUserId());
			user.setUiCreateTime(System.currentTimeMillis());
			dao.save(user);
		}
	}

    /**
     * 认领虚拟用户
     * @param
     * @return
     */
    public void claimUser(String ownerUserName,Long userId ){
        List<UserInfo> userInfo = userDao.getOpenIdByRealName(ownerUserName);
        if (userInfo.size() ==1 ) {
            //只有一个叫这个名字的真实用户
            String  openid = userInfo.get(0).getUiOpenId();
            userService.updateClaimUserScore(openid,userId.toString() );
        }

    }


}
