package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.EncryptUtil;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminParkDao;
import com.golf.golf.db.AdminUser;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球场管理
 * @author nmy
 * 2016年10月31日
 */
@Service
public class AdminParkService implements IBaseService {
	
    @Autowired
    private AdminParkDao adminParkDao;

    /**
     * 球场列表
     * @param searchBean
     * @param pageInfo
     * @return
     */
    public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo){
    	return adminParkDao.getParkList(searchBean,pageInfo);
    }


	/**
     * 保存球场
     * @param parkInfo
     * @return
     */
    public Long save(ParkInfo parkInfo){
    	parkInfo.setPiCreateTime(System.currentTimeMillis());
		parkInfo.setPiCreateUserId(AdminUserUtil.getUserId());
		parkInfo.setPiCreateUserName(AdminUserUtil.getShowName());
        return adminParkDao.save(parkInfo);
    }

    /**
     * 根据id获取球场信息
     * @param id
     * @return
     */
    public ParkInfo getById(Long id){
        return adminParkDao.get(ParkInfo.class, id);
    }

    /**
     * 更新球场信息
     * @param parkInfo
     */
    public void edit(ParkInfo parkInfo){
		ParkInfo db = getById(parkInfo.getPiId());
		db.setPiLogo(parkInfo.getPiLogo());
		db.setPiAddress(parkInfo.getPiAddress());
		db.setPiIsValid(parkInfo.getPiIsValid());
		db.setPiUpdateTime(System.currentTimeMillis());
		db.setPiUpdateUserId(AdminUserUtil.getUserId());
		db.setPiUpdateUserName(AdminUserUtil.getShowName());
        adminParkDao.update(db);
    }

    /**
     * 恢复、注销球场
     * @param parkId
     */
    public void update(Long parkId) {
		ParkInfo db = adminParkDao.get(ParkInfo.class, parkId);
		if(db.getPiIsValid() == 0){
			db.setPiIsValid(1);
		}else{
			db.setPiIsValid(0);
		}
		db.setPiUpdateTime(System.currentTimeMillis());
		db.setPiUpdateUserId(AdminUserUtil.getUserId());
		db.setPiUpdateUserName(AdminUserUtil.getShowName());
		adminParkDao.update(db);
    }

    /**
     * 查看球场名是否已经存在
     * @param name
     * @return
     */
    public boolean checkName(String name){
    	return adminParkDao.checkName(name);
    }
}
