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
				Long teamId = matchService.getLongValue(result, "tiId");
				if(teamId != null){
					List<String> captainList = teamDao.getCaptainByTeamId(teamId);
					if(captainList == null || captainList.size() ==0){
						result.put("captain", "未知");
					}else{
						result.put("captain", captainList.get(0));
					}
				}
//				String logo = matchService.getName(result, "logo");
//				if(StringUtils.isNotEmpty(logo)){
//					result.put("logo", PropertyConst.DOMAIN + logo);
//				}
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
			teamUserMapping.setTumType(1);
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserId(WebUtil.getUserIdBySessionId());
			teamUserMapping.setTumCreateUserName(WebUtil.getUserNameBySessionId());
			teamUserMapping.setTumIsValid(1);
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
	 * @return
	 */
	public List<Map<String, Object>> getTeamPointByYear(String date, Long teamId) {
		Map<String,Object> parp = new HashMap<>();
		parp.put("startYear", TimeUtil.getYearFirst(Integer.parseInt(date)));
		parp.put("endYear", TimeUtil.getYearLast(Integer.parseInt(date)));
		parp.put("teamId", teamId);
		return teamDao.getTeamPointByYear(parp);
	}

	/**
	 * 获取球队已报名的用户或者球队用户列表
	 * @param teamId:球队id
	 * @param type:0已报名的 1本队用户
	 * @return
	 */
	public List<Map<String, Object>> getUserListByTeamId(Long teamId, Integer type) {
		if(type == 0){
			return teamDao.getApplyUserListByTeamId(teamId);
		}else{
			return teamDao.getUserListByTeamId(teamId);
		}
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
}
