package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.util.MathUtil;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.MatchScoreDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScoreNetHole;
import com.golf.golf.db.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 净杆计算service
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchScoreService implements IBaseService {

	@Autowired
	private MatchDao matchDao;
	@Autowired
	private MatchScoreDao matchScoreDao;
	@Autowired
	private MatchService matchService;

	/**
	 *  生成计算净杆的随机6个球洞
	 *  这6个洞对每场比赛来说是固定的，不同的比赛是不同的。另外算好后显示的时候要判断下是在记分截止以后。
	 * @param matchInfo:比赛详情
	 * @return
	 */
	public void saveMatchNetRodHole(MatchInfo matchInfo) {
		//前9洞
		String zoneBeforeNine = matchInfo.getMiZoneBeforeNine();
		String zoneAfterNine = matchInfo.getMiZoneAfterNine();
		//6个随机数
		Integer[] randomHole = MathUtil.getRandom(6);
		//获取本场比赛是否设置了随机球洞
		MatchScoreNetHole matchScoreNetHole = matchScoreDao.getMatchNetRodHole(matchInfo.getMiId());
		if(matchScoreNetHole == null){
			matchScoreNetHole = new MatchScoreNetHole();
			matchScoreNetHole.setMsntMatchId(matchInfo.getMiId());
			matchScoreNetHole.setMsntHoleBeforeName(zoneBeforeNine);
			matchScoreNetHole.setMsntHoleBeforeNum1(randomHole[0]);
			matchScoreNetHole.setMsntHoleBeforeNum2(randomHole[1]);
			matchScoreNetHole.setMsntHoleBeforeNum3(randomHole[2]);
			matchScoreNetHole.setMsntHoleAfterName(zoneAfterNine);
			matchScoreNetHole.setMsntHoleAfterNum1(randomHole[3]);
			matchScoreNetHole.setMsntHoleAfterNum2(randomHole[4]);
			matchScoreNetHole.setMsntHoleAfterNum3(randomHole[5]);
			matchScoreNetHole.setMsCreateTime(System.currentTimeMillis());
			matchScoreNetHole.setMsnrCreateUserId(matchInfo.getMiCreateUserId());
			matchScoreNetHole.setMsCreateUserName(matchInfo.getMiCreateUserName());
			matchScoreDao.save(matchScoreNetHole);
		}
	}


	/**
	 * 比赛——group——总比分
	 * 罗列每个参赛球友的记分卡。其中的数字“蓝色是Par,红色是小鸟，灰色是高于标准杆的。黑色是老鹰”
	 * @param matchId 比赛id
	 * @return
	 */
	public Map<String, Object> getTotalScoreByMatchId(Long matchId) throws Exception {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		List<MatchGroupUserScoreBean> list = new ArrayList<>();

		//所有球洞
		List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
		//每洞杆数
		List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
		//获取前半场球洞
		List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,0);
		matchService.getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
		//获取后半场球洞
		List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchId,1);
		matchService.getNewParkHoleList(parkHoleList,parkRodList,afterParkHoleList);

		//第一条记录（半场分区信息）
		MatchGroupUserScoreBean thBean = new MatchGroupUserScoreBean();
		thBean.setUserId(0L);
		thBean.setUserName("Hole");
		thBean.setUserScoreTotalList(parkHoleList);
		list.add(thBean);

		//第二条记录（总杆）
		MatchGroupUserScoreBean thBean2 = new MatchGroupUserScoreBean();
		thBean2.setUserId(0L);
		thBean2.setUserName("杆差");
		thBean2.setUserScoreTotalList(parkHoleList);
		list.add(thBean2);


		//本比赛的所有用户和其去掉随机杆后的总杆数，为0的排后面(首列显示)
		List<Map<String, Object>> userList = null;
		//本比赛要去掉的随机洞
		MatchScoreNetHole scoreNetHole = matchScoreDao.getMatchNetRodHole(matchId);
		List<Integer> beforeHoleNum = new ArrayList<>();
		List<Integer> afterHoleNum = new ArrayList<>();
		if(scoreNetHole != null){
			beforeHoleNum.add(scoreNetHole.getMsntHoleBeforeNum1());
			beforeHoleNum.add(scoreNetHole.getMsntHoleBeforeNum2());
			beforeHoleNum.add(scoreNetHole.getMsntHoleBeforeNum3());
			afterHoleNum.add(scoreNetHole.getMsntHoleAfterNum1());
			afterHoleNum.add(scoreNetHole.getMsntHoleAfterNum2());
			afterHoleNum.add(scoreNetHole.getMsntHoleAfterNum3());
		}
		//参赛球队
		String joinTeamIds = matchInfo.getMiJoinTeamIds();
		if(StringUtils.isNotEmpty(joinTeamIds)){
			//有参赛队
			userList = matchScoreDao.getUserListByMatchId(matchInfo,beforeHoleNum,afterHoleNum);
		}else{
			//没有参赛球队
			//TODO
//			userList = matchDao.getUserListByIdWithOutTeam(matchId);
		}
		//获取本场地18洞的总标准杆
		Long sum18 = matchScoreDao.getSumStandardRod(matchInfo);

		if(userList != null && userList.size() >0){
			for(Map<String, Object> user:userList){
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				Long userId = matchService.getLongValue(user,"uiId");
				Long teamId = matchService.getLongValue(user, "team_id");
				String userRealName = user.get("uiRealName") == null ? null:user.get("uiRealName").toString();
				//本用户的前后半场总得分情况
				List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,0,teamId);
				matchService.createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,1,teamId);
				matchService.createNewUserScore(userScoreList, uScoreAfterList);
				bean.setUserScoreTotalList(userScoreList);
				bean.setUserId(userId);
				bean.setUserName(userRealName);
				list.add(bean);

				//计算净杆：公式:  差点=（其余12洞杆数总和×1.5—18洞标准杆数）×0.8。
				//为防作弊，合计12洞杆数总和时采2.3.4制：Par3洞成绩超过5杆，此洞以5杆计算；Par4洞成绩超过7杆，此洞以7杆计算；Par5洞成绩超过9杆，此洞以9杆计算。
				//是否par （0：否 1：是）比标准杆多一杆或者标准杆完成
				//净杆=总杆-差点
				//TODO
				Long sumRod = matchService.getLongValue(user,"sumRodNum");
				double cha = (sumRod*1.5 - sum18)*0.8;
				Long chaLong = Math.round(96.1);
				Long netRod = sumRod - chaLong;
				user.put("netRod",netRod);
			}
		}
		result.put("userList", userList);
		result.put("list", list);
		return result;
	}


	/**
	 * 获取用户在每个球洞的得分情况
	 */
	public void createNewUserScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
		if (userList != null && userList.size() > 0) {
			for (Map<String, Object> user : userList) {
				Long uiId = matchService.getLongValue(user, "uiId");
				Long teamId = matchService.getLongValue(user, "team_id");
				MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
				bean.setUserId(uiId);
				bean.setUserName(matchService.getName(user, "uiRealName"));
				//本用户的前后半场总得分情况
				List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();
				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,0,teamId);
				matchService.createNewUserScore(userScoreList, uScoreBeforeList);
				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(uiId, matchInfo,1,teamId);
				matchService.createNewUserScore(userScoreList, uScoreAfterList);
				bean.setUserScoreTotalList(userScoreList);
				list.add(bean);

				//计算净杆
			}
		}
	}
}
