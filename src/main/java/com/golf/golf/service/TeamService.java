package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.MatchGroup;
import com.golf.golf.db.MatchUserGroupMapping;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.TeamUserMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球队管理
 * Created by nmy on 2017/7/1.
 */
@Service
public class TeamService implements IBaseService {

    @Autowired
    private TeamDao teamDao;
	@Autowired
	private MatchService matchService;

	/**
	 * 获取球队列表
	 * type 0：所有球队 1：我加入的球队 2：我可以加入的球队   3：我创建的球队
	 * @return
	 */
	public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		pageInfo = teamDao.getTeamList(searchBean,pageInfo);
		getCaptain(pageInfo);
		return pageInfo;
	}

    //队长
	private void getCaptain(POJOPageInfo pageInfo) {
		if(pageInfo.getCount() >0 && pageInfo.getItems() != null && pageInfo.getItems().size() >0){
			for(Map<String,Object> result : (List<Map<String,Object>>)pageInfo.getItems()){
				Integer count = matchService.getIntegerValue(result, "userCount");
				if(count == 0){
					result.put("userCount", 0);
				}
				//队长名字
				Long teamId = matchService.getLongValue(result, "tiId");
				if(teamId != null){
					List<String> captainList = teamDao.getCaptainByTeamId(teamId);
					if(captainList == null || captainList.size() ==0){
						result.put("captain", "未知");
					}else{
						result.put("captain", captainList.get(0));
					}
				}
				//我是否是队长
				Long isCaptain = teamDao.isCaptainIdByTeamId(teamId, WebUtil.getUserIdBySessionId());
				result.put("isCaptain", isCaptain);
			}
		}
    }


    /**
     * 计算我附近8千米的经纬度
	 * dis 周围千米数
     * @return
     */
    public SearchBean findNeighPosition(SearchBean searchBean, double longitude,double latitude, double dis){
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
//        double dis = 3;//3千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        double minlat =latitude-dlat;
        double maxlat = latitude+dlat;
        double minlng = longitude -dlng;
        double maxlng = longitude + dlng;
//        Object[] values = {minlng,maxlng,minlat,maxlat};
        searchBean.addParpField("minlng", minlng+"");
        searchBean.addParpField("maxlng", maxlng+"");
        searchBean.addParpField("minlat", minlat+"");
        searchBean.addParpField("maxlat", maxlat+"");

        return searchBean;
    }

	public static void main(String[] args) {
		double longitude = 116.44355;
		double latitude = 39.9219;
		findNeighPosition(longitude,latitude);
	}

	public static void findNeighPosition(double longitude,double latitude){
		//先计算查询点的经纬度范围
		double r = 6371;//地球半径千米
		double dis = 0.5;//0.5千米距离
		double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
		dlng = dlng*180/Math.PI;//角度转为弧度
		double dlat = dis/r;
		dlat = dlat*180/Math.PI;
		double minlat =latitude-dlat;
		double maxlat = latitude+dlat;
		double minlng = longitude -dlng;
		double maxlng = longitude + dlng;

		String hql = "from Property where longitude>=? and longitude =<? and latitude>=? latitude=<? and state=0";
		Object[] values = {minlng,maxlng,minlat,maxlat};
	}

	/**
	 * 创建更新球队
	 * @return
	 */
	public void saveOrUpdateTeamInfo(TeamInfo teamInfoBean) {
		if(teamInfoBean.getTiId() == null){
			teamInfoBean.setTiCreateTime(System.currentTimeMillis());
			teamInfoBean.setTiCreateUserId(WebUtil.getUserIdBySessionId());
			teamInfoBean.setTiCreateUserName(WebUtil.getUserNameBySessionId());
			teamInfoBean.setTiIsValid(1);
			Long teamId = teamDao.save(teamInfoBean);
			//向球队用户表新增一条记录
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(WebUtil.getUserIdBySessionId());
			//设置队长
			teamUserMapping.setTumUserType(0);
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserId(WebUtil.getUserIdBySessionId());
			teamUserMapping.setTumCreateUserName(WebUtil.getUserNameBySessionId());
			teamDao.save(teamUserMapping);
		}else{
			TeamInfo db = teamDao.get(TeamInfo.class,teamInfoBean.getTiId());
			db.setTiName(teamInfoBean.getTiName());
			db.setTiCreateTime(System.currentTimeMillis());
			db.setTiAddress(teamInfoBean.getTiAddress());
			db.setTiDigest(teamInfoBean.getTiDigest());
			db.setTiSignature(teamInfoBean.getTiSignature());
			db.setTiJoinOpenType(teamInfoBean.getTiJoinOpenType());
			db.setTiInfoOpenType(teamInfoBean.getTiInfoOpenType());
			db.setTiUserInfoType(teamInfoBean.getTiUserInfoType());
			db.setTiMatchResultAuditType(teamInfoBean.getTiMatchResultAuditType());
			db.setTiUpdateTime(System.currentTimeMillis());
			db.setTiUpdateUserId(WebUtil.getUserIdBySessionId());
			db.setTiUpdateUserName(WebUtil.getUserNameBySessionId());
			teamDao.update(db);
		}

	}

	/**
	 * 获取球队详情
	 * @param teamId:球队id count 显示的队员人数
	 * @return
	 */
	public Map<String, Object> getTeamInfoById(Long teamId) {
		Map<String, Object> result = new HashMap<>();
		TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
		teamInfo.getCreateTimeStr();
		result.put("teamInfo",teamInfo);
		List<Map<String, Object>> userList = teamDao.getTeamUserListByTeamId(teamId);
		result.put("userList",userList);
		Long isCaptain = teamDao.isCaptainIdByTeamId(teamId, WebUtil.getUserIdBySessionId());
		result.put("isCaptain",isCaptain);
		Long isInTeam = teamDao.isInTeamById(teamId, WebUtil.getUserIdBySessionId());
		result.put("isInTeam",isInTeam);
		return result;
	}

	/**
	 * 删除球队
	 * @return
	 */
	public boolean delTeamById(Long teamId) {
		Long userId = WebUtil.getUserIdBySessionId();
		//是否是该球队的队长
		Long count = teamDao.isCaptainIdByTeamId(teamId,userId);
		if(count >0){
			//删除球队、用户mapping 逻辑删除
			teamDao.delTeam(teamId);
			return true;
		}
		return false;
	}

	/**
	 * 获取球队记分详情
	 * @param teamId:球队id
	 * @param type:0比分榜 按平均杆排名 1积分榜:按北大发明的积分方法积分，方法另附 2获取比赛榜 列出当年所有本球队相关的比赛情况统计
	 * @return
	 */
	public List<Map<String, Object>> getTeamPointByYear(Integer type, String date, Long teamId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("startYear", TimeUtil.getYearFirst(Integer.parseInt(date)));
		parp.put("endYear", TimeUtil.getYearLast(Integer.parseInt(date)));
		parp.put("teamId", teamId);
		if(type == 0){
			//比分榜
			return teamDao.getTeamPointByYear(parp);
		}else if(type == 1){
			//TODO
			//积分榜
			return null;
		}else{
			//比赛榜 列出当年所有本球队相关的比赛情况统计
			return teamDao.getTeamMatchByYear(parp);
		}
	}

	/**
	 * 获取球队已报名的用户或者球队用户列表
	 * @param teamId:球队id
	 * @param type:0已报名的 1本队用户 2本队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, Integer type) {
		return teamDao.getUserListByTeamId(teamId, type);
	}

	/**
	 * 更新球队用户
	 * @param teamId:球队id
	 * @param userIds:用户
	 * @param type:0添加已报名的 1删除这些用户
	 * @return
	 */
	public void updateTeamUserByTeamId(Long teamId, String userIds, Integer type) {
		if(StringUtils.isNotEmpty(userIds)){
			userIds = userIds.replace("[","");
			userIds = userIds.replace("]","");
			userIds = userIds.replace("\"","");
			String[] uIds = userIds.split(",");
			for(String userId :uIds){
				if(StringUtils.isNotEmpty(userId)){
					TeamUserMapping teamUserMapping = teamDao.getTeamUserMapping(teamId,Long.parseLong(userId));
					if(type == 0){
						//设置为 普通队员
						teamUserMapping.setTumUserType(1);
						teamUserMapping.setTumCreateTime(System.currentTimeMillis());
						teamUserMapping.setTumCreateUserId(WebUtil.getUserIdBySessionId());
						teamUserMapping.setTumCreateUserName(WebUtil.getUserNameBySessionId());
						teamDao.update(teamUserMapping);
					}else{
						teamDao.del(teamUserMapping);
					}
				}
			}
		}
	}


	/**
	 * 获取本球队所有用户
	 * @param teamId:球队id
	 * type:0已报名的 1本队用户 2本队所有用户
	 * @return
	 */
	public List<Map<String, Object>> getAllUserListByTeamId(Long teamId) {
		return teamDao.getUserListByTeamId(teamId, 2);
	}

	/**
	 * 申请加入或退出该球队
	 * @param teamId:球队id
	 * @param type:0加入 1退出
	 * @return
	 */
	public void joinOrQuitTeamById(Long teamId, Integer type) {
		Long userId = WebUtil.getUserIdBySessionId();
		if(type == 0){
			TeamInfo teamInfo = teamDao.get(TeamInfo.class, teamId);
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(userId);
			//是否开启入队审核
			if(teamInfo.getTiJoinOpenType() == 1){
				//申请入队
				teamUserMapping.setTumUserType(2);
			}else{
				//直接入队
				teamUserMapping.setTumUserType(1);
			}
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserName(WebUtil.getUserNameBySessionId());
			teamUserMapping.setTumCreateUserId(WebUtil.getUserIdBySessionId());
			teamDao.save(teamUserMapping);
		}else{
			teamDao.deleteFromTeamUserMapping(teamId, userId);
		}
	}
}
