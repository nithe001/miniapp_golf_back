package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamUserMapping;
import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关DAO
 * Created by nmy on 2016/7/29.
 */
@Repository
public class UserDao extends CommonDao {


    /**
     * 根据openid，取得用户的微信信息
     * @param openid
     * @return
     */
    public WechatUserInfo getWechatUserByOpenid(String openid){
        Map<String, String> parp = new HashMap<>();
        parp.put("openid", openid);
        StringBuilder hql = new StringBuilder();
        hql.append("FROM WechatUserInfo WHERE wuiOpenid = :openid");
        return dao.findOne(hql.toString(), parp);
    }


    /**
     * 根据telNo，取得用户的信息
     * @param telNo 手机号
     * @return
     */
    public UserInfo getUserByTelNo(String telNo){
        Map<String, String> parp = new HashMap<>();
        parp.put("telNo", telNo);

        StringBuilder hql = new StringBuilder();
        hql.append("FROM UserInfo WHERE cuTelNo = :telNo");

        return dao.findOne(hql.toString(), parp);

    }

    /**
     * 根据用户表主键，取得我的患者的信息
     * @param doctorId 用户表id
     * @return
     */
    public List<UserInfo> getMyPatients(Long doctorId){
        Map<String, Long> parp = new HashMap<>();
        parp.put("doctorId", doctorId);

        StringBuilder hql = new StringBuilder();
        hql.append("FROM UserInfo WHERE cuDoctorId = :doctorId ");
        hql.append(" AND cuIsValid = 1 ");

        return dao.createQuery(hql.toString(), parp);

    }

	//更新用户绑定状态
	public void updateSubscribeTypeByOpenId(String openId) {
		Map<String, Object> parp = new HashMap<>();
		parp.put("openId", openId);
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE WechatUserInfo AS t SET t.cwuSubscribe = 0 WHERE t.openid=:openId ");
		dao.executeHql(hql.toString(),parp);
	}

	/**
	 * 通过openid获取用户微信信息
	 * @return
	 */
	public WechatUserInfo getUserInfoByOpenId(String openId) {
		WechatUserInfo user = null;
		Map<String, Object> parp = new HashMap<String, Object>();
		parp.put("openId", openId);
		StringBuffer hql = new StringBuffer();
		hql.append("FROM WechatUserInfo WHERE wuiOpenid = :openId");
		List<WechatUserInfo> userList = dao.createQuery(hql.toString(), parp);
		if(userList != null && userList.size() > 0){
			user = userList.get(0);
		}
		return user;
	}

	/**
	 * 获取我的活动
	 * @return
	 */
	public List<MatchInfo> getCalendarListByUserId(Long cuClub) {
		Map<String, Object> parp = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("FROM WxCalendar WHERE wcIsDel = 0 ");
		if(cuClub != null){
			parp.put("clubId", cuClub);
			hql.append("AND wcActivitiesId = :clubId OR (wcActivitiesId <> :clubId AND wcIsOpen = 1) ");
		}else{
			hql.append("AND wcIsOpen = 1 ");
		}
		hql.append("ORDER BY wcEventTime DESC ");
		return dao.createQuery(hql.toString(), parp);
	}

	/**
	 * 根据OPENID获取用户信息
	 * @return
	 */
	public UserInfo getUserByOpenid(String openId) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM UserInfo as u WHERE u.uiOpenId = " +openId);
		return dao.findOne(hql.toString(), null);
	}

    /**
     * 是否是我的队友
     * @return
     */
    public Long getIsMyTeammate(Long myUserId, Long otherUserId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("myUserId", myUserId);
        parp.put("otherUserId", otherUserId);
        StringBuffer hql = new StringBuffer();

        hql.append("FROM TeamUserMapping as m WHERE m.tumUserId = :otherUserId ");
        hql.append("AND m.tumTeamId IN ( ");
        hql.append("select m1.tumTeamId from TeamUserMapping as m1 where m1.tumUserId = :myUserId ");
        hql.append(") ");
        Long count = dao.createCountQuery("SELECT COUNT(*) "+ hql.toString(), parp);
        if(count == 0){
            return null;
        }
        List<Long> list = dao.createQuery("SELECT m.tumTeamId "+ hql.toString(), parp);
        return list.get(0);
    }

    /**
     * 通过用户id获取用户微信信息
     * @return
     */
    public WechatUserInfo getWechatUserByUserId(Long userId) {
        StringBuffer hql = new StringBuffer();
        hql.append("FROM WechatUserInfo as w WHERE w.wuiUId = " +userId);
        return (WechatUserInfo)dao.createQuery(hql.toString());
    }
}
