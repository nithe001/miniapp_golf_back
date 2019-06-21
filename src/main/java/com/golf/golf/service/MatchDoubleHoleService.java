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
 * 双人比洞 记分卡 service
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchDoubleHoleService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	protected MatchService matchService;


	/**
	 * 获取双人比洞赛记分卡 每组4人 每2人一个小组 同一个球队的放一行
	 * @return
	 */
	public Map<String, Object> getDoubleHoleScoreCardByGroupId(Long matchId, Long groupId) {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		//固定的首列：本组用户 4 人 按照球队分组并按照参赛范围的球队顺序来排序
		List<Map<String, Object>> userList = matchDao.getUserListByScoreCard(matchId, groupId,null);
		result.put("userList", userList);

		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);

		//第二条和第三条记录：用户得分
		List<DoubleRodUserScoreBean> allUserScoreList = new ArrayList<>();
		Long tempTeamId = 0L;
		Long tempUserId = 0L;
		for(int i=0;i<userList.size();i++){
			Long userId = matchService.getLongValue(userList.get(i), "uiId");
			Long teamId = matchService.getLongValue(userList.get(i), "team_id");
			if(teamId.equals(tempTeamId)) {
				//构造用户成绩数据 同一个队的放一起
				createUserScoreList(tempUserId, userId, teamId, groupId, matchInfo, allUserScoreList);
			}
			tempTeamId = teamId;
			tempUserId = userId;
		}

		//中间滚动表格，用户得分 和成绩
		result.put("allUserScoreList", allUserScoreList);

		//固定的尾列：总标准杆数
		Long totalStandardRod = matchDao.getTotalRod(matchInfo);
		result.put("totalStandardRod", totalStandardRod);
		//用户总分
		List<Map<String, Object>> totalScoreList = matchDao.getTotalScoreWithUser(matchId, groupId);
		result.put("totalScoreList", totalScoreList);
		return result;
	}


	//双人比洞——构造数据
	private void createUserScoreList(Long userId0, Long userId1, Long teamId, Long groupId, MatchInfo matchInfo, List<DoubleRodUserScoreBean> allUserScoreList) {
		//第二条记录：用户得分
		List<Map<String, Object>[]> scoreList = new ArrayList<>();
		//第3条记录：成绩
		List<String> resultScoreList = new ArrayList<>();
		Map<String, Object>[] userMap = new Map[2];
		DoubleRodUserScoreBean userInfoBean = new DoubleRodUserScoreBean();
		DoubleRodUserScoreBean userScoreBean = new DoubleRodUserScoreBean();
		DoubleRodUserScoreBean totalBean = new DoubleRodUserScoreBean();


		//第一个用户
		List<Map<String, Object>> uscoreList0 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId);
		//第二个用户
		List<Map<String, Object>> uscoreList1 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId);
		for(Map<String, Object> map0:uscoreList0){
			Map<String, Object>[] map = new Map[2];
			map[0] = map0;
			//第一个用户本洞击出的杆数
			Integer rodNum1 = matchService.getIntegerValue(map0,"rod_num");
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
				//第二个用户本洞击出的杆数
				Integer rodNum2 = matchService.getIntegerValue(map1,"rod_num");
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

					//计算两个对手本洞的成绩 逐洞比上下两行当前成绩，用数字表示赢了几洞，上面赢的记UP，下面赢的记DN，数字表示到目前赢几洞，A/S表示平。
					//比洞赛中使用独特的计分和表述方式。
					// 假设球员A和球员B参加一对一比赛，在第一洞比赛中，A击出的杆数少于B，于是A赢得了该洞的胜利，这时表述为“A领先1洞”，
					// 如以B为主语，则表述为“B落后1洞”。
					// 这样在一轮比赛中，选手在每一个洞决出一个小分，最后当其中一方选手领先优势已不能被逆转(超分)时，这一回合比赛结束。
					// 这时胜利一方的领先洞数(X)和剩余未比赛的球。
					String cj = "";
					if(rodNum1 != 0 && rodNum2 != 0){
						if(rodNum1 < rodNum2){
							cj = rodNum2 - rodNum1+"UP";
						}else if(rodNum1 > rodNum2){
							cj = rodNum1 - rodNum2+"DN";
						}else{
							cj = "A/S";
						}
					}
					resultScoreList.add(cj);
					break;
				}
			}
		}
		userScoreBean.setUserScoreList(scoreList);
		totalBean.setTotalScoreList(resultScoreList);
		allUserScoreList.add(userScoreBean);
		allUserScoreList.add(totalBean);
	}

}
