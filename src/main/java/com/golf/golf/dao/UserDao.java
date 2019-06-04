package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.*;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
		hql.append("FROM UserInfo as u WHERE u.uiOpenId = '" +openId+"'");
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


	/**
	 * 今年参加比赛的场数 不包括单练
	 */
	public Long getMatchCountByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT count(*) FROM ( SELECT s.ms_match_id FROM match_score AS s " +
				"WHERE s.ms_match_type = 1 AND s.ms_user_id = 1 " +
				"GROUP BY s.ms_match_id " +
				") AS t");
		return dao.createSQLCountQuery(hql.toString(), parp);
	}

	/**
	 * 年度成绩分析 不计算单练的
	 * 计算一年内平均每18洞分项的数量
	 * “暴洞”是指+3及以上的洞数总和
	 * 开球情况对应记分卡 球道滚轮的箭头
	 * 标ON是计算出来的，如果某洞：杆数-推杆数=该洞标准杆数-2，则该洞为 标ON
	 * @return
	 */
	public List<Map<String, Object>> getScoreByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT " +
				"sum(s.ms_is_par) as par,sum(s.ms_is_bird) as bird,COALESCE(sum(s.ms_is_eagle),0) as eagle,sum(s.ms_push_rod_num) as pushNum, " +
				"sum(CASE WHEN s.ms_rod_cha = 1 then 1 else 0 end) as one, " +
				"sum(CASE WHEN s.ms_rod_cha = 2  then 1 else 0 end) as two, " +
				"count(s.ms_is_bomb or 0) as baodong, " +
				"sum(CASE WHEN s.ms_is_up = \"开球直球\" then 1 else 0 end) AS zhi, " +
				"sum(CASE WHEN s.ms_is_up = \"开球偏右\" then 1 else 0 end) AS you, " +
				"sum(CASE WHEN s.ms_is_up = \"开球偏左\" then 1 else 0 end) AS zuo, " +
				"sum(CASE WHEN s.ms_is_up = \"开球出界\" then 1 else 0 end) AS chu, " +
				"count(s.ms_is_on or 0) as biaoOn ");
		hql.append("FROM match_score AS s WHERE s.ms_match_type = 1 and s.ms_user_id = :userId and s.ms_create_time >=:startTime and s.ms_create_time <=:endTime ");
		List<Map<String, Object>> list = dao.createSQLQuery(hql.toString(), parp, Transformers.ALIAS_TO_ENTITY_MAP);
		return list;
	}

	/**
	 * 年度成绩分析——总杆数
	 * @return
	 */
	public Long getSumRod(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT sum(s.msRodNum) ");
		hql.append("FROM MatchScore as s where s.msUserId = :userId ");
		hql.append("and s.msMatchType = 1 ");
		hql.append("and s.msCreateTime >= :startTime ");
		hql.append("and s.msCreateTime <= :endTime ");
		return dao.createCountQuery(hql.toString(), parp);
	}

	/**
	 * 高球规则
	 * @return
	 */
	public List<MatchRule> getMatchRuleList() {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MatchRule AS r WHERE 1=1 ");
		Long count = dao.createCountQuery("SELECT COUNT(*) "+hql.toString());
		if (count == null || count.intValue() == 0) {
			return new ArrayList<>();
		}
		return dao.createQuery(hql.toString());
	}

	/**
	 * 更新用户真实姓名
	 * @return
	 */
	public void updateMatchInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchInfo AS m set m.miCreateUserName = '"+ uiRealName+"' where m.miCreateUserId = "+uiId);
		dao.executeHql(hql.toString());
	}


	/**
	 * 更新用户真实姓名
	 * @return
	 */
	public void updateMatchScore(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchScore AS s set s.msUserName = '"+uiRealName+"' where s.msUserId ="+uiId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新用户真实姓名
	 * @return
	 */
	public void updateMatchUserGroupMapping(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchUserGroupMapping AS g set g.mugmUserName = '"+uiRealName+"' where g.mugmUserId ="+uiId);
		dao.executeHql(hql.toString());
	}


	/**
	 * 更新用户真实姓名
	 * @return
	 */
	public void updateTeamInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE TeamInfo AS t set t.tiCreateUserName = '"+uiRealName+"' where t.tiCreateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}
}
