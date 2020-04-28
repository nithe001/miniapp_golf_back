package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

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
	 * 双人比杆赛记分卡 每组4人 每2人一个小组 同一个球队的放一行
     * 	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，
     * 	 每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
     * 	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
     * 	 * 3、单人比杆赛分组不用有任何限制。
     * 	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * @return
	 */
	public Map<String, Object> getDoubleRodScoreCardByGroupId(Long matchId, Long groupId) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
        result.put("matchInfo", matchInfo);
		//固定的首列：本组用户
		List<Map<String, Object>> userList = matchDao.getUserListByScoreCard(matchId, groupId);
		//解码用户昵称
		matchService.decodeUserNickName(userList);
		//设置用户名
		matchService.setUserName(userList);
		result.put("userList", userList);

		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);
        //第一行
        List<Map<String,Object>> userList0 = new ArrayList<>();
        //第二行
        List<Map<String,Object>> userList1 = new ArrayList<>();
		List<Long> joinTeamIdList = matchService.getLongTeamIdList(matchInfo.getMiJoinTeamIds());
		//可能的情况：第一组 第一队2人，第二队1人，那么第一队的2人一行，第二队的1人自己一行
		if(userList != null && userList.size() >0){
			//有参赛队的，按照队伍分组
			if(joinTeamIdList != null && joinTeamIdList.size() >0){
				Long teamIdTemp = null;
				for(Iterator<Map<String,Object>> userListIterator = (Iterator<Map<String, Object>>) userList.iterator(); userListIterator.hasNext();){
					Map<String,Object> groupUser = userListIterator.next();
					if(joinTeamIdList.size() == 1){
						//同一个参赛球队
						if(userList0.size() < 2){
							userList0.add(groupUser);
							userListIterator.remove();
						}
					}else{
						//不是同一个队伍 把一个球队的放一起
						Long teamId = matchService.getLongValue(groupUser,"team_id");
						if(teamIdTemp == null || teamId.equals(teamIdTemp)){
							teamIdTemp = teamId;
							userList0.add(groupUser);
							userListIterator.remove();
						}
					}
				}
				userList1.addAll(userList);
			}else{
				//没有参赛队的，每个比赛分组 前两个人一小组，后两个人一小组 如果是两个人，就一人一个分组
				if(userList.size() <= 2){
					userList0.add(userList.get(0));
					userList1.add(userList.get(1));
				}else{
					userList0.add(userList.get(0));
					userList0.add(userList.get(1));
					userList1.add(userList.get(2));
					if(userList.size() ==4){
						userList1.add(userList.get(3));
					}
				}
				String userIds0 = "";
				String userIds1 = "";
				if(userList0.size()>0){
					for(Map<String,Object> user:userList0){
						userIds0 += ","+matchService.getLongValue(user,"uiId");
					}
				}
				if(userList1.size()>0){
					for(Map<String,Object> user:userList1){
						userIds1 += ","+matchService.getLongValue(user,"uiId");
					}
				}
				result.put("userIds0", userIds0);
				result.put("userIds1", userIds1);
			}
        }
        result.put("userList0", userList0);
        result.put("userList1", userList1);

        List<Map<String, Object>> uscoreList0 = new ArrayList<>();
        if(userList0.size()>0){
            Long userId0 = matchService.getLongValue(userList0.get(0), "uiId");
            Long teamId0 = matchService.getLongValue(userList0.get(0),"team_id");
            uscoreList0 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId0);
            for(Map<String, Object> map0:uscoreList0) {
                map0.put("userId", userId0);
                UserInfo userInfo0 = matchDao.get(UserInfo.class, userId0);
                map0.put("userName", StringUtils.isNotEmpty(userInfo0.getUiRealName()) ? userInfo0.getUiRealName() : userInfo0.getUiNickName());
            }
        }
        List<Map<String, Object>> uscoreList1 = new ArrayList<>();
        if(userList1.size()>0){
            Long userId1 = matchService.getLongValue(userList1.get(0), "uiId");
            Long teamId1 = matchService.getLongValue(userList1.get(0),"team_id");
            uscoreList1 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId1);
            for(Map<String, Object> map1:uscoreList1) {
                map1.put("userId", userId1);
                UserInfo userInfo1 = matchDao.get(UserInfo.class, userId1);
                map1.put("userName", StringUtils.isNotEmpty(userInfo1.getUiRealName()) ? userInfo1.getUiRealName() : userInfo1.getUiNickName());
            }
        }
        result.put("uscoreList0", uscoreList0);
        result.put("uscoreList1", uscoreList1);

		//固定的尾列：总标准杆数
		Long totalStandardRod = matchDao.getTotalRod(matchInfo);
		result.put("totalStandardRod", totalStandardRod);

		//总计
		List<Map<String, Object>> totalScoreList = new ArrayList<>();
		if(userList0 != null && userList0.size() >0){
			Long userId0 = matchService.getLongValue(userList0.get(0),"uiId");
			List<Map<String, Object>> mapss0 = matchDao.getSum(matchId, groupId,userId0);
			if(mapss0.size()>0){
				totalScoreList.add(mapss0.get(0));
			}
		}
		if(userList1 != null && userList1.size() >0){
			Long userId1 = matchService.getLongValue(userList1.get(0),"uiId");
			List<Map<String, Object>> mapss1 = matchDao.getSum(matchId, groupId,userId1);
			if(mapss1.size()>0){
				totalScoreList.add(mapss1.get(0));
			}
		}


		result.put("totalScoreList", totalScoreList);
		return result;
	}
}
