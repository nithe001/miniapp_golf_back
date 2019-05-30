package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.golf.bean.DoubleRodUserScoreBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 双人比杆 记分卡 service
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchDoubleRodService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	protected MatchService matchService;

	/**
	 * 双人比杆赛记分卡 每组4人 每2人一个小组
	 * @return
	 */
	public Map<String, Object> getDoubleRodScoreCardByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//固定的首列：本组用户
		List<Map<String, Object>> userList = matchDao.getUserListById(matchId, groupId);
		result.put("userList", userList);

		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);


		//第二条和第三条记录：用户得分
		List<DoubleRodUserScoreBean> scoreTotalList = new ArrayList<>();
		for(int i=0;i<userList.size();i++){
			if(i>=userList.size()){
				break;
			}
			Long userId0 = matchService.getLongValue(userList.get(i), "uiId");
			Long userId1 = matchService.getLongValue(userList.get(i+1), "uiId");
			i++;
			//构造用户成绩数据
			DoubleRodUserScoreBean bean = createUserScoreList(userId0,userId1, groupId, matchInfo);
			scoreTotalList.add(bean);
		}
		//第二条和第三条，用户得分list
		result.put("allUserScoreList", scoreTotalList);

		//固定的尾列：总标准杆数
		Long totalStandardRod = matchDao.getTotalRod(matchInfo);
		result.put("totalStandardRod", totalStandardRod);
		//用户总分
		List<Map<String, Object>> totalScoreList = matchDao.getTotalScoreWithUser(matchId, groupId);
		result.put("totalScoreList", totalScoreList);
		return result;
	}

	//双人比杆——构造数据
	private DoubleRodUserScoreBean createUserScoreList(Long userId0, Long userId1, Long groupId, MatchInfo matchInfo) {
		DoubleRodUserScoreBean bean = new DoubleRodUserScoreBean();
		//第二条记录：用户得分
		List<Map<String, Object>[]> scoreList = new ArrayList<>();
		//第一个用户的得分情况
		List<Map<String, Object>> uscoreList0 = matchDao.getScoreByUserId(groupId, userId0, matchInfo);
		//第二个用户的得分情况
		List<Map<String, Object>> uscoreList1 = matchDao.getScoreByUserId(groupId, userId1, matchInfo);

		for(Map<String, Object> map0:uscoreList0){
			Map<String, Object>[] map = new Map[2];
			map[0] = map0;
			//球洞号
			Integer holeNum = matchService.getIntegerValue(map0,"pp_hole_num");
			//球洞名称
			String holeName = matchService.getName(map0,"pp_name");
			if(matchService.getLongValue(map0,"ms_user_id") == null){
				UserInfo userInfo = matchDao.get(UserInfo.class,userId0);
				map0.put("ms_user_id",userId0);
				map0.put("ms_user_name",userInfo.getUiRealName());
			}
			for(Map<String, Object> map1:uscoreList1){
				//球洞号
				Integer holeNum1 = matchService.getIntegerValue(map1,"pp_hole_num");
				//球洞名称
				String holeName1 = matchService.getName(map1,"pp_name");
				if(holeNum.equals(holeNum1) && holeName.equals(holeName1)){
					if(matchService.getLongValue(map1,"ms_user_id") == null){
						UserInfo userInfo = matchDao.get(UserInfo.class,userId1);
						map1.put("ms_user_id",userId1);
						map1.put("ms_user_name",userInfo.getUiRealName());
					}
					//第二个用户的杆数
					map[1] = map1;
					scoreList.add(map);
					break;
				}
			}
			bean.setUserScoreList(scoreList);
		}
		return bean;
	}


}