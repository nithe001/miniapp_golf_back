package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.ParkPartition;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.TeamUserMapping;
import com.golf.golf.db.UserInfo;
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
				String logo = matchService.getName(result, "logo");
				if(StringUtils.isNotEmpty(logo)){
					result.put("logo", PropertyConst.DOMAIN + logo);
				}
			}
		}
    }


	/**
     * 获取  我加入的球队列表 或者  可以加入的球队
     * @return
     */
    public POJOPageInfo getMyTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getMyTeamList(searchBean,pageInfo);
    }

    /**
     * 查询球场列表——所有球场
     * @return
     */
    public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getParkList(searchBean,pageInfo);
    }

    /**
     * 查询球场列表——附近的球场
     * @return
     */
    public POJOPageInfo getParkListNearby(SearchBean searchBean, POJOPageInfo pageInfo) {
        UserInfo userInfo = teamDao.get(UserInfo.class, UserUtil.getUserId());
        if(userInfo != null && StringUtils.isNotEmpty(userInfo.getUiLongitude())
                && StringUtils.isNotEmpty(userInfo.getUiLatitude())){
            //用户经纬度存在, 计算我附近5千米的经纬度
            searchBean = findNeighPosition(searchBean, Double.parseDouble(userInfo.getUiLongitude()),
                    Double.parseDouble(userInfo.getUiLatitude()));
        }
        return teamDao.getParkListNearby(searchBean,pageInfo);
    }

    /**
     * 计算我附近5千米的经纬度
     * @return
     */
    public SearchBean findNeighPosition(SearchBean searchBean, double longitude,double latitude){
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
//        Object[] values = {minlng,maxlng,minlat,maxlat};
        searchBean.addParpField("minlng", minlng);
        searchBean.addParpField("maxlng", maxlng);
        searchBean.addParpField("minlat", minlat);
        searchBean.addParpField("maxlat", maxlat);

        return searchBean;
    }

    /**
     * 查询球场区域
     * @return
     */
    public POJOPageInfo getParkListByRegion(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getParkListByRegion(searchBean,pageInfo);
    }

    /**
     * 查询该区域下的球场
     * @return
     */
    public POJOPageInfo getParkListByRegionName(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getParkListByRegionName(searchBean,pageInfo);
    }

    /**
     * 创建比赛—点击球场-获取分区和洞
     * @return
     */
    public List<ParkPartition> getParkZoneAndHole(Long parkId) {
        return teamDao.getParkZoneAndHole(parkId);
    }

	/**
	 * 获取我创建的球队列表
	 * @return
	 */
	public POJOPageInfo getMyCreateTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
		pageInfo = teamDao.getMyCreateTeamList(searchBean,pageInfo);
		getCaptain(pageInfo);
		return pageInfo;
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
			Long teamId = teamDao.save(teamInfoBean);
			//向球队用户表新增一条记录
			TeamUserMapping teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumTeamId(teamId);
			teamUserMapping.setTumUserId(WebUtil.getUserIdBySessionId());
			teamUserMapping.setTumUserType(1);
			teamUserMapping.setTumType(1);
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
		for(Map<String, Object> user :userList){
			String headimg = matchService.getName(user,"uiHeadimg");
			user.put("uiHeadimg", PropertyConst.DOMAIN + headimg);
		}
		result.put("userList",userList);
		return result;
	}
}
