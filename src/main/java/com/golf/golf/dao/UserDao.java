package com.golf.golf.dao;

import com.golf.common.db.CommonDao;
import com.golf.golf.db.*;
import com.golf.golf.service.UserService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private UserService userService;


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
     * 查出系统所有微信授权用户信息,在整队认领时使用
     * @param openid
     * @return
     */
    public List<Map<String,Object>>  getAllOpenIdUser(){
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT u.ui_open_id as openId, u.ui_id as userId, u.ui_real_name as userName ");
        hql.append(" FROM user_info as u WHERE u.ui_open_id  is not null " );
        return dao.createSQLQuery(hql.toString() ,Transformers.ALIAS_TO_ENTITY_MAP);
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
	public WechatUserInfo getWechatUserInfoByOpenId(String openId) {
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
	public UserInfo getUserInfoByOpenid(String openId) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM UserInfo as u WHERE u.uiOpenId = '" +openId+"'");
		return dao.findOne(hql.toString(), null);
	}

    /**
     * 根据USERID获取用户信息 nhq
     * @return
     */
    public UserInfo getUserInfoByUserId(Long userId) {
        StringBuffer hql = new StringBuffer();
        hql.append("FROM UserInfo as u WHERE u.uiId = '" +userId+"'");
        return dao.findOne(hql.toString(), null);
    }
    /**
     * 是否是我的队友（入队审核通过的）
     * @return
     */
    public List<Long> getIsMyTeammate(Long myUserId, Long otherUserId) {
        Map<String, Object> parp = new HashMap<String, Object>();
        parp.put("myUserId", myUserId);
        parp.put("otherUserId", otherUserId);
        StringBuffer hql = new StringBuffer();

        hql.append("SELECT m.tumTeamId FROM TeamUserMapping as m WHERE m.tumUserId = :otherUserId and m.tumUserType !=2 ");
        hql.append("AND m.tumTeamId IN ( ");
        hql.append("select m1.tumTeamId from TeamUserMapping as m1 where m1.tumUserId = :myUserId ");
        hql.append(") ");
        List<Long> list = dao.createQuery(hql.toString(), parp);
       	return list;
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
	 * 今年参加比赛的场数 包括单练
	 */
	public Long getMatchCountByYear(Map<String, Object> parp) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT count(*) FROM ( SELECT s.ms_match_id FROM match_score AS s,match_info as m " +
				"WHERE s.ms_user_id = :userId " +
				"and s.ms_match_id = m.mi_id " +
				"and m.mi_is_valid = 1 " +
				"GROUP BY s.ms_match_id " +
				") AS t");
		return dao.createSQLCountQuery(hql.toString(), parp);
	}

	/**
	 * 年度成绩分析 计算单练
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
		hql.append("FROM match_score AS s,match_info as m WHERE s.ms_user_id = :userId ");
		hql.append(" and s.ms_match_id = m.mi_id and m.mi_is_valid = 1 ");
		hql.append(" and s.ms_create_time >=:startTime and s.ms_create_time <=:endTime ");
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
		hql.append("FROM MatchInfo as m, MatchScore as s ");
		hql.append("where s.msUserId = :userId ");
		hql.append("and m.miType = 1 ");
		hql.append("and m.miIsValid = 1 ");
		hql.append("and m.miId = s.msMatchId ");
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

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchInfo AS m set m.miUpdateUserName = '"+ uiRealName+"' where m.miUpdateUserId = "+uiId);
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

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchScore AS s set s.msCreateUserName = '"+uiRealName+"' where s.msCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchScore AS s set s.msUpdateUserName = '"+uiRealName+"' where s.msUpdateUserId ="+uiId);
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

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchUserGroupMapping AS g set g.mugmCreateUserName = '"+uiRealName+"' where g.mugmCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchUserGroupMapping AS g set g.mugmUpdateUserName = '"+uiRealName+"' where g.mugmUpdateUserId ="+uiId);
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

		hql.delete(0,hql.length());
		hql.append("UPDATE TeamInfo AS t set t.tiUpdateUserName = '"+uiRealName+"' where t.tiUpdateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新球队用户mapping
	 * @return
	 */
	public void updateTeamUserMappingInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE TeamUserMapping AS t set t.tumCreateUserName = '"+uiRealName+"' where t.tumCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE TeamUserMapping AS t set t.tumUpdateUserName = '"+uiRealName+"' where t.tumUpdateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新球队用户积分表
	 * @return
	 */
	public void updateTeamUserPointInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE TeamUserPoint AS t set t.tupCreateUserName = '"+uiRealName+"' where t.tupCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE TeamUserPoint AS t set t.tupUpdateUserName = '"+uiRealName+"' where t.tupUpdateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}



	/**
	 * 更新比赛积分计算配置表
	 * @return
	 */
	public void updateMatchIntegralConfigInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE IntegralConfig AS t set t.icCreateUserName = '"+uiRealName+"' where t.icCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE IntegralConfig AS t set t.icUpdateUserName = '"+uiRealName+"' where t.icUpdateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新比赛分组表
	 * @return
	 */
	public void updateMatchGroupInfo(Long uiId, String uiRealName) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchGroup AS g set g.mgCreateUserName = '"+uiRealName+"' where g.mgCreateUserId ="+uiId);
		dao.executeHql(hql.toString());

		hql.delete(0,hql.length());
		hql.append("UPDATE MatchGroup AS g set g.mgUpdateUserName = '"+uiRealName+"' where g.mgUpdateUserId ="+uiId);
		dao.executeHql(hql.toString());
	}
	/**
	 * 用真名查询叫这个名字的用户信息,把真实用户排前面 nhq
	 * @return
	 */
	public List<UserInfo> getUserIdByRealName(String realName) {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM UserInfo as u WHERE u.uiRealName = '"+realName+"'");
		hql.append(" and 1=1");
		hql.append(" ORDER BY u.uiOpenId DESC");
		return dao.createQuery(hql.toString());
	}
    /**
     * 用真名查询叫这个名字的真实用户的信息, nhq
     * @return
     */
    public List<UserInfo> getOpenIdByRealName(String realName) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM UserInfo as u WHERE u.uiRealName = '"+realName+"'");
        hql.append(" and u.uiOpenId !=null and 1=1");
        return dao.createQuery(hql.toString());
    }
		/**
         * 用我的真名查询除了我之外，叫此名的导入用户及所在球队，
         * @return
         */
	public List<Map<String, Object>> getUserCountByRealName(String uiRealName ) {
			StringBuilder hql = new StringBuilder();
			//不从matchscore中找关联球队了，而是从teamusermapping 中找nhq
			//hql.append("select u.uiId as userId,u.uiRealName as realName,s.msTeamId as teamId,t.tiAbbrev as teamName,s.msMatchId as matchId,s.msMatchTitle as matchTitle ");
			hql.append("select u.uiId as userId,u.uiRealName as realName,t.tiName as teamName ");
			//hql.append(" from UserInfo as u,MatchScore as s,TeamInfo as t ");
			hql.append(" from UserInfo as u,TeamUserMapping as m,TeamInfo as t ");
			//hql.append(" where u.uiId = s.msUserId ");
			//hql.append(" and s.msTeamId = t.tiId ");
			hql.append(" where u.uiId = m.tumUserId ");
			hql.append(" and m.tumTeamId = t.tiId ");
			hql.append(" and u.uiRealName='" + uiRealName + "'");
			//只认领还没有openid的用户，只有这样的用户才是导入用户 nhq
			hql.append(" and u.uiOpenId = null");
			//hql.append(" and s.msIsClaim = 0 ");
			hql.append(" GROUP BY u.uiId ");

		return dao.createQuery(hql.toString(),Transformers.ALIAS_TO_ENTITY_MAP);
	}

	/**
	 * 更新这个导入用户的 比赛分组mapping 为db的id
	 * @return
	 */
	public void updateImportMatchMappingUserId(Long importUserId, Long myUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchUserGroupMapping AS t set t.mugmUserId = "+myUserId+" where t.mugmUserId ="+importUserId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新这个导入用户的成绩id为db的id
	 * 并设置是否认领为1
	 * @return
	 */
	public void updateImportMatchScoreUserId(Long importUserId, Long myUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE MatchScore AS t set t.msUserId = "+myUserId+" , t.msIsClaim = 1 ");
		hql.append(" where t.msUserId ="+importUserId);
		dao.executeHql(hql.toString());
	}

	/**
	 * 更新这个导入用户的 球队分组mapping 为db的id
	 * @return
	 */
	public void updateImportTeamMappingUserId(Long importUserId, Long myUserId) {
		StringBuilder hql = new StringBuilder();
		hql.append("UPDATE TeamUserMapping AS t set t.tumUserId = "+myUserId+" where t.tumUserId ="+importUserId);
		dao.executeHql(hql.toString());
        //同时修改 teamuserpoint nhq
		StringBuilder hql1 = new StringBuilder();
		hql1.append("UPDATE TeamUserPoint AS t set t.tupUserId = "+myUserId+" where t.tupUserId ="+importUserId);
		dao.executeHql(hql1.toString());

	}
	/**
	 * 删除用户的 观赛者记录 nhq
	 */
	public void deleteImportMatchWatchUserId(String openid, String matchid) {
		Long matchId = Long.parseLong(matchid);
		UserInfo userInfo = userService.getUserInfoByOpenId(openid);
		Long myUserId = userInfo.getUiId();

		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM MatchJoinWatchInfo AS t WHERE t.mjwiMatchId =" + matchId);
		hql.append("and t.mjwiUserId =" + myUserId);
		dao.executeHql(hql.toString());
	}
	/**
	 * 获取这些用户加入的球队
	 * @return
	 */
	public List<Map<String, Object>> getUserJoinTeamList(List<Long> userIdList) {
		return null;
	}

	}
