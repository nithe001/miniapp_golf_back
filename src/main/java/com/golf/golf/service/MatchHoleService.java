package com.golf.golf.service;

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

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 比洞赛 记分卡 service
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchHoleService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	protected MatchService matchService;


	/**
	 * 获取比洞赛记分卡
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人，如果又是队式的，则一组的两个人要是两个队的
	 * 3、单人比杆赛分组不用有任何限制。
	 * 4、一个队队内，及多个队之间没法进行比洞赛
	 * @return
	 */
	public Map<String, Object> updateOrGetHoleScoreCardByGroupId(Long matchId, Long groupId) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//固定的首列：本组用户
		List<Map<String, Object>> userList = matchDao.getUserListByScoreCard(matchId, groupId);
		//解码用户昵称
		matchService.decodeUserNickName(userList);
		//设置用户名
		matchService.setUserName(userList);

		//第一行
		List<Map<String,Object>> userList0 = new ArrayList<>();
		//第二行
		List<Map<String,Object>> userList1 = new ArrayList<>();
		Long teamIdTemp_ = null;
		for(Iterator<Map<String,Object>> userListIterator = (Iterator<Map<String, Object>>) userList.iterator(); userListIterator.hasNext();){
			Map<String,Object> groupUser = userListIterator.next();
			Long teamId = matchService.getLongValue(groupUser,"team_id");
			if(teamIdTemp_ == null || teamId.equals(teamIdTemp_)){
				teamIdTemp_ = teamId;
				userList0.add(groupUser);
				userListIterator.remove();
			}
		}
		userList1.addAll(userList);
		result.put("userList0", userList0);
		result.put("userList1", userList1);

		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);

		//获取每个用户得分，并计算成绩
		List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
		HoleMatchScoreResultBean endTrChengji = new HoleMatchScoreResultBean();
		if (userList != null && userList.size() > 0) {
			getChengjiList(chengjiList,endTrChengji,userList0,userList1,groupId,matchInfo,result);
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
		if(matchInfo.getMiMatchFormat1() == 1){
			//双人比洞，总分去重
			if(totalScoreList != null && totalScoreList.size() >0){
				Long teamIdTemp = 0l;
				for(Iterator<Map<String,Object>> scoreIterator = (Iterator<Map<String, Object>>) totalScoreList.iterator(); scoreIterator.hasNext();){
					Map<String,Object> map = scoreIterator.next();
					Long teamId = matchService.getLongValue(map,"team_id");
					if(teamId.equals(teamIdTemp)){
						scoreIterator.remove();
					}
					teamIdTemp = teamId;
				}
			}
		}
		result.put("totalScoreList", totalScoreList);
		return result;
	}

	//更新比洞赛结果输赢表 n:0:第一个用户  1：第二个用户
	public void saveOrUpdateMatchHoleResult(HoleMatchScoreResultBean bean,Long matchId, Long groupId, Long teamId,Integer n) {
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

	public void getChengjiList(List<HoleMatchScoreResultBean> chengjiList,HoleMatchScoreResultBean endTrChengji,
								List<Map<String, Object>> userList0,List<Map<String, Object>> userList1,Long groupId, MatchInfo matchInfo, Map<String, Object> result) {
		if (userList0 != null && userList0.size() > 0 && userList1 != null && userList1.size() > 0 ) {
			//第一个用户 得分
			Long userId0 = matchService.getLongValue(userList0.get(0), "uiId");
			Long teamId0 = matchService.getLongValue(userList0.get(0),"team_id");
			List<Map<String, Object>> uscoreList0 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId0);
			result.put("uscoreList0", uscoreList0);
			//第二个用户 得分
			Long userId1 = matchService.getLongValue(userList1.get(0), "uiId");
			Long teamId1 = matchService.getLongValue(userList1.get(0),"team_id");
			List<Map<String, Object>> uscoreList1 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId1);
			result.put("uscoreList1", uscoreList1);

			//最终成绩
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
//                你设一个“得分”变量，初始值为0，
//                A1洞，双方平，则得分=等分+0，
//                A2洞，上方赢，则得分=得分+1=1，
//                A3洞，下方赢，则得分=得分-1=0，
//                A4洞，上方赢，则得分=得分+1=1，
//                依次类推，得分是个计数，不是杆差。
							if(rodNum0.equals(rodNum1)){
								//打平
								score = score + 0;
							}else if(rodNum0 < rodNum1){
								//上赢
								score = score + 1;
							}else if(rodNum0 > rodNum1){
								//下赢
								score = score - 1;
							}
							if(score == 0){
								bean.setNum(null);
								bean.setUpDnAs(MatchResultEnum.AS.text());
							}else if(score > 0){
								bean.setNum(score);
								bean.setUpDnAs(MatchResultEnum.UP.text());
							}else if(score < 0){
								bean.setNum(Math.abs(score));
								bean.setUpDnAs(MatchResultEnum.DN.text());
							}
							BeanUtils.copyProperties(bean,endTrChengji);
							//同时更新比洞赛输赢表
							saveOrUpdateMatchHoleResult(bean,matchInfo.getMiId(),groupId,teamId0,0);
							saveOrUpdateMatchHoleResult(bean,matchInfo.getMiId(),groupId,teamId1,1);
						}
						chengjiList.add(bean);
						break;
					}
				}
			}
		}
	}
}
