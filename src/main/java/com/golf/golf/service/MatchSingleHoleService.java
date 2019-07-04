package com.golf.golf.service;

import com.golf.common.Const;
import com.golf.common.IBaseService;
import com.golf.golf.bean.HoleMatchScoreResultBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchHoleResult;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import com.golf.golf.enums.MatchResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单人比洞 记分卡 service
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchSingleHoleService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	protected MatchService matchService;


	/**
	 * 获取单人比洞赛记分卡 每组2人
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
	 * 3、单人比杆赛分组不用有任何限制。
	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * @return
	 */
	public Map<String, Object> updateOrGetSingleHoleScoreCardByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//固定的首列：本组用户
		List<Map<String, Object>> userList = matchDao.getUserListByScoreCard(matchId, groupId,null);
		result.put("userList", userList);

		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);

		//获取每个用户得分，并计算成绩

		List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
		HoleMatchScoreResultBean endTrChengji = new HoleMatchScoreResultBean();
		if (userList != null && userList.size() > 0) {
			//第一个用户 得分
			Long userId0 = matchService.getLongValue(userList.get(0), "uiId");
			Long teamId0 = matchService.getLongValue(userList.get(0),"team_id");
			List<Map<String, Object>> uscoreList0 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId0);
			result.put("uscoreList0", uscoreList0);
			//第二个用户 得分
			Long userId1 = matchService.getLongValue(userList.get(1), "uiId");
			Long teamId1 = matchService.getLongValue(userList.get(1),"team_id");
			List<Map<String, Object>> uscoreList1 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId1);
			result.put("uscoreList1", uscoreList1);

			Integer score = 0;
			for(Map<String, Object> map0:uscoreList0){
				map0.put("userId",userId0);
				UserInfo userInfo0 = matchDao.get(UserInfo.class,userId0);
				map0.put("userName",StringUtils.isNotEmpty(userInfo0.getUiRealName())?userInfo0.getUiRealName():userInfo0.getUiNickName());
				//第二个用户 杆数
				Integer rodNum0 = matchService.getIntegerValueWithNull(map0,"rod_num");
				//球洞号
				Integer holeNum0 = matchService.getIntegerValue(map0,"pp_hole_num");
				//球洞名称
				String holeName0 = matchService.getName(map0,"pp_name");
				for(Map<String, Object> map1:uscoreList1){
					map1.put("userId",userId1);
					UserInfo userInfo1 = matchDao.get(UserInfo.class,userId1);
					map1.put("userName",StringUtils.isNotEmpty(userInfo1.getUiRealName())?userInfo1.getUiRealName():userInfo1.getUiNickName());
					//第二个用户 杆数
					Integer rodNum1 = matchService.getIntegerValueWithNull(map1,"rod_num");
					//球洞号
					Integer holeNum1 = matchService.getIntegerValue(map1,"pp_hole_num");
					//球洞名称
					String holeName1 = matchService.getName(map1,"pp_name");
					if(holeNum0.equals(holeNum1) && holeName0.equals(holeName1)){
						HoleMatchScoreResultBean bean = new HoleMatchScoreResultBean();
						if(rodNum0 != null && rodNum1 != null){
							Integer chengji = rodNum1 - rodNum0;
							score = score + chengji;
							bean.setNum(Math.abs(score));
							if(score == 0){
								bean.setNum(null);
								bean.setUpDnAs(MatchResultEnum.AS.text());
							}else if(score >0){
								bean.setUpDnAs(MatchResultEnum.UP.text());
							}else{
								bean.setUpDnAs(MatchResultEnum.DN.text());
							}
							BeanUtils.copyProperties(bean,endTrChengji);
							//同时更新比洞赛输赢表
							saveOrUpdateMatchHoleResult(bean,matchId,groupId,teamId0,0);
							saveOrUpdateMatchHoleResult(bean,matchId,groupId,teamId1,1);
						}
						chengjiList.add(bean);
						break;
					}
				}
			}
		}
		//成绩行
		result.put("chengjiList", chengjiList);

		//固定的尾列：总标准杆数
		Long totalStandardRod = matchDao.getTotalRod(matchInfo);
		result.put("totalStandardRod", totalStandardRod);

		//固定的尾列：成绩行(一个数据)
		result.put("chengjiResult", endTrChengji);

		//用户总分
		List<Map<String, Object>> totalScoreList = matchDao.getTotalScoreWithUser(matchId, groupId);
		result.put("totalScoreList", totalScoreList);
		return result;
	}

	//更新比洞赛结果输赢表 n:0:第一个用户  1：第二个用户
	private void saveOrUpdateMatchHoleResult(HoleMatchScoreResultBean bean,Long matchId, Long groupId, Long teamId,Integer n) {
		MatchHoleResult matchHoleResult = matchDao.getMatchHoleResult(matchId,groupId,teamId);
		if(matchHoleResult == null) {
			matchHoleResult = new MatchHoleResult();
			matchHoleResult.setMhrMatchId(matchId);
			matchHoleResult.setMhrGroupId(groupId);
			matchHoleResult.setMhrTeamId(teamId);
		}
		//比赛结果（0：打平 1：打赢  2：打输）
		if(bean.getUpDnAs().equals(MatchResultEnum.AS.text())){
			matchHoleResult.setMhrResult(0);
		}else if(bean.getUpDnAs().equals(MatchResultEnum.UP.text())){
			if(n == 0){
				//第一个赢，第二个就记输
				matchHoleResult.setMhrResult(Integer.parseInt(MatchResultEnum.UP.value()));
			}else{
				matchHoleResult.setMhrResult(Integer.parseInt(MatchResultEnum.DN.value()));
			}
		}else if(bean.getUpDnAs().equals(MatchResultEnum.DN.text())){
			if(n == 0){
				//第一个输，第二个就记赢
				matchHoleResult.setMhrResult(Integer.parseInt(MatchResultEnum.DN.value()));
			}else{
				matchHoleResult.setMhrResult(Integer.parseInt(MatchResultEnum.UP.value()));
			}
		}
		if(matchHoleResult.getMhrId() == null) {
			matchDao.save(matchHoleResult);
		}else{
			matchDao.update(matchHoleResult);
		}
	}


}
