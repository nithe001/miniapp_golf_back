package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.PropertyConst;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.dao.admin.AdminTeamDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.service.MatchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

	/**
	 * 球队列表
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo<MatchInfo> teamList(SearchBean searchBean, POJOPageInfo pageInfo){
		pageInfo = adminTeamDao.teamList(searchBean,pageInfo);
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

}
