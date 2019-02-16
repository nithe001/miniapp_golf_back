package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.bean.MatchUserGroupMappingBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.MatchGroupUserMappingTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * 获取球队列表
     * @return
     */
    public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getTeamList(searchBean,pageInfo);
    }

    /**
     * 获取我加入的球队列表
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



}
