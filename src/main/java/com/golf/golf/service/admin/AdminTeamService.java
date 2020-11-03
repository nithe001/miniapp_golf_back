package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.dao.UserDao;
import com.golf.golf.dao.admin.AdminTeamDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.MatchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球队管理
 * @author nmy
 * 2017年05月08日
 */
@Service
public class AdminTeamService implements IBaseService {
	
    @Autowired
    private AdminTeamDao adminTeamDao;
	@Autowired
	private MatchService matchService;
	@Autowired
	private TeamDao teamDao;
    @Autowired
    private UserDao userDao;
	/**
	 * 球队列表
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo<MatchInfo> teamList(SearchBean searchBean, POJOPageInfo pageInfo) throws UnsupportedEncodingException {
		pageInfo = adminTeamDao.teamList(searchBean,pageInfo);
		getCaptain(pageInfo);
		return pageInfo;
	}
	//队长
	private void getCaptain(POJOPageInfo pageInfo) throws UnsupportedEncodingException {
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			for(Map<String,Object> result : (List<Map<String,Object>>)pageInfo.getItems()){
				Integer count = matchService.getIntegerValue(result, "userCount");
				if(count == 0){
					result.put("userCount", 0);
				}
				Long teamId = matchService.getLongValue(result, "tiId");
				if(teamId != null){
					List<Map<String,Object>> captainList = teamDao.getCaptainByTeamId(teamId);
					//用户昵称解码
					matchService.decodeUserNickName(captainList);
					if(captainList != null && captainList.size() >0){
						Map<String,Object> cap = captainList.get(0);
						String realName = matchService.getName(cap,"uiRealName");
						String nickName = matchService.getName(cap,"uiNickName");
						if(StringUtils.isNotEmpty(realName)){
							result.put("captain", realName);
						}else{
							result.put("captain", nickName);
						}
					}
				}
				Long createTime = matchService.getLongValue(result, "createTime");
				if(createTime != null){
					result.put("createTime", TimeUtil.longToString(createTime, TimeUtil.FORMAT_DATETIME_HH_MM));
				}
				Long updateTime = matchService.getLongValue(result, "updateTime");
				if(updateTime != null){
					result.put("updateTime", TimeUtil.longToString(updateTime, TimeUtil.FORMAT_DATETIME_HH_MM));
				}
			}
		}
	}

	/**
	 * 获取球队
	 * @param id
	 * @return
	 */
	public TeamInfo getMatchById(Long id) {
		return adminTeamDao.get(TeamInfo.class,id);
	}

	/**
	 * 获取球队明细
	 * @param teamId
	 * @return
	 */
	public Map<String,Object> getMatchInfoById(Long teamId) {
		Map<String,Object> result = new HashMap<>();
		TeamInfo teamInfo = adminTeamDao.get(TeamInfo.class,teamId);
		result.put("teamInfo", teamInfo);
		List<Map<String, Object>> userList = adminTeamDao.getTeamUserListByTeamId(teamId);
		result.put("userList",userList);
		return result;
	}

	/**
	 * 修改状态
	 * @param teamId 球队id
	 * @return
	 */
	public void updateTeamState(Long teamId) {
		TeamInfo teamInfo = adminTeamDao.get(TeamInfo.class,teamId);
		if(teamInfo.getTiIsValid() == 0){
			teamInfo.setTiIsValid(1);
		}else{
			teamInfo.setTiIsValid(0);
		}
		adminTeamDao.update(teamInfo);
	}

	/**
	 * 编辑球队-保存
	 * @return
	 */
	public void teamEdit(TeamInfo teamInfo) {
		TeamInfo db = adminTeamDao.get(TeamInfo.class,teamInfo.getTiId());
		db.setTiAddress(teamInfo.getTiAddress());
		db.setTiSignature(teamInfo.getTiSignature());
		db.setTiDigest(teamInfo.getTiDigest());
		db.setTiJoinOpenType(teamInfo.getTiJoinOpenType());
		db.setTiInfoOpenType(teamInfo.getTiInfoOpenType());
		db.setTiMatchResultAuditType(teamInfo.getTiMatchResultAuditType());
		db.setTiUserInfoType(teamInfo.getTiUserInfoType());
		db.setTiIsValid(teamInfo.getTiIsValid());
		db.setTiUpdateUserName(AdminUserUtil.getShowName());
		db.setTiUpdateTime(System.currentTimeMillis());
		db.setTiUpdateUserId(AdminUserUtil.getUserId());
		adminTeamDao.update(db);
	}

    /**
     * 删除
     * @param teamId 球队id
     * @return
     */
    public void delTeam(Long teamId) {
		TeamInfo teamInfo = adminTeamDao.get(TeamInfo.class,teamId);
        adminTeamDao.del(teamInfo);
        //删除球队用户配置
        adminTeamDao.delTeamUserMapping(teamId);
        //删除比赛用户配置中对应球队信息
        adminTeamDao.delMatchTeamUserMapping(teamId);
        //删除比赛成绩表中对应球队的比分
        adminTeamDao.delMatchScoreByTeamId(teamId);
        //删除成绩确认配置
        adminTeamDao.delMatchScoreSubmitConfigByTeamId(teamId);
        //删除比洞赛输赢表
		adminTeamDao.delMatchHoleResultByTeamId(teamId);
		//删除球队积分
		adminTeamDao.delTeamUserPointByTeamId(teamId);
    }

	/**
	 * 操作球队球友
	 * @param teamId 球队id
	 * @param userId 用户id
	 * @param type 类型 0：设为队长  1：取消设为队长or同意入队  2：移出队伍
	 * @return
	 */
	public void updateTeamUser(Long teamId, Long userId, Integer type) {
		if(type < 2){
			adminTeamDao.updateUserType(teamId,userId,type);
		}else{
			//移出队伍
			adminTeamDao.delUserFromTeamUserMapping(teamId,userId);
		}
	}


    /**
     * 整队认领 用小程序中所有注册用户认领所选球队中和其实名一样的导入用户
     * @param teamId 球队id
     * @return
     */
    public void claimTeamUser(Long teamId) {
        //获取系统所有微信授权用户的信息
        List<Map<String,Object>> userList = userDao.getAllOpenIdUser();
        //获取本队所有用户的信息
        List<Map<String, Object>> teamUserList=teamDao.getTeamUserListByTeamId(teamId,0);
        for (Map<String, Object> user:userList) {
            String userName = matchService.getName(user,"userName");
            Long myUserId =  matchService.getLongValue(user,"userId");
            for (Map<String, Object> tuser:teamUserList) {
                String tuserName = matchService.getName(tuser,"uiRealName");
                Long importUserId =  matchService.getLongValue(tuser,"uiId");
                String importOpenId =  matchService.getName(tuser,"openId");
                if (userName.equals(tuserName) && importOpenId == null) {
                    //更新这个导入用户的 比赛分组mapping 为db的id
                    userDao.updateImportMatchMappingUserId(importUserId, myUserId);
                    //更新这个导入用户的 比赛成绩 的用户id为db的id 并设置是否认领为1
                    userDao.updateImportMatchScoreUserId(importUserId, myUserId);
                    //更新这个导入用户的 球队分组mapping 为db的id 以及积分表
                    userDao.updateImportTeamMappingUserId(importUserId, myUserId);
                    //删除这个导入用户
                    //UserInfo chooseUser = userDao.getUserInfoByUserId(importUserId);
                    UserInfo chooseUser = userDao.get(UserInfo.class, importUserId);
                    userDao.del(chooseUser);
                }
            }
        }
    }

    /**
     * 查询球友球队
     * @return
     */
    public List<Map<String, Object>> getUserTeamList() {
        return adminTeamDao.getUserTeamList();
    }
}
