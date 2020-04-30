package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.util.MathUtil;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTeamRankingBean;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

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
	 * 比赛——group——总比分——净杆
	 * 12洞总杆 = 18洞的新新贝利亚防作弊总杆，减去6洞总杆
	 * 罗列每个参赛球友的记分卡。其中的数字“蓝色是Par,红色是小鸟，灰色是高于标准杆的。黑色是老鹰”
	 * @param matchId 比赛id
	 * @return
	 */
	public Map<String, Object> getTotalScoreByMatchId(Long matchId) throws Exception {
		Map<String, Object> result = new HashMap<>();
		MatchInfo matchInfo = matchDao.get(MatchInfo.class, matchId);
		result.put("matchInfo", matchInfo);
		//本比赛要去掉的随机洞
		MatchScoreNetHole scoreNetHole = matchScoreDao.getMatchNetRodHole(matchId);
		List<MatchGroupUserScoreBean> list = new ArrayList<>();
		//本比赛的所有用户和其去掉随机杆后的总杆数，为0的排后面(首列显示)
		List<Map<String, Object>> userList = null;
		if(scoreNetHole == null && (scoreNetHole != null
				&& StringUtils.isEmpty(scoreNetHole.getMsntHoleBeforeName()) && StringUtils.isEmpty(scoreNetHole.getMsntHoleAfterName()))){
			result.put("list", list);
			userList = new ArrayList<>();
			result.put("userList", userList);
			return result;
		}

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
			userList = matchScoreDao.getUserListByMatchId(matchInfo);
		}else{
			//没有参赛球队
			//TODO
			userList = matchScoreDao.getUserListByIdWithOutTeam(matchId,beforeHoleNum,afterHoleNum);
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

				//本用户18洞最大值（防作弊）
				List<Map<String, Object>> uScoreBeforeXXList = matchScoreDao.getMaxRodScore(userId,matchId);
				//新新贝利亚计算
				createNewXXlist(uScoreBeforeXXList);

				//本用户前半场得分情况
				List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,0,teamId);
				//防作弊计算后半场9洞的总杆数
				Integer beforeTotalRodNum = createNewUserScore(userScoreList, uScoreBeforeList,uScoreBeforeXXList);

				//本用户后半场得分情况
				List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(userId, matchInfo,1,teamId);
				//防作弊计算后半场9洞的总杆数
				Integer afterTotalRodNum = createNewUserScore(userScoreList, uScoreAfterList,uScoreBeforeXXList);
				bean.setUserScoreTotalList(userScoreList);
				bean.setUserId(userId);
				bean.setUserName(userRealName);
				list.add(bean);

				//计算净杆：公式:  差点=（其余12洞杆数总和×1.5—18洞标准杆数）×0.8。
				//为防作弊，合计12洞杆数总和时采2.3.4制：
				//先算出18洞的新新贝利亚总杆：Par3洞成绩超过5杆，此洞以5杆计算；Par4洞成绩超过7杆，此洞以7杆计算；Par5洞成绩超过9杆，此洞以9杆计算。
				//12洞总杆 = 18洞的新新贝利亚防作弊总杆，减去6洞总杆
				//是否par （0：否 1：是）比标准杆多一杆或者标准杆完成
				//净杆=总杆-差点


				//不防作弊时用户的18洞总杆数
				Long sumRod = matchScoreDao.getSumRod(userId,matchId);
				if(sumRod != null && sumRod != 0){
					//本用户随机抽取的6个洞总杆数
					Long radomSumRod = matchScoreDao.getRandomSumRod(userId,matchInfo,beforeHoleNum,afterHoleNum);
					//防作弊18洞总杆数
					Integer sumRodXxbly = beforeTotalRodNum+afterTotalRodNum;
					//差点
					BigDecimal ch = new BigDecimal(((sumRodXxbly-radomSumRod)*1.5 - sum18)*0.8).setScale(2, BigDecimal.ROUND_HALF_UP);
					double cha = ch.doubleValue();
					//净杆
					double netRod = sumRod.doubleValue() - cha;
					user.put("sumRodNum",netRod);
				}else{
					user.put("sumRodNum",0);
				}
			}
		}
		if(userList != null && userList.size()>0){
			//排序 差点越高名次越好
			//排序
			Collections.sort(userList,new Comparator<Map<String, Object>>(){
				@Override
				public int compare(Map<String, Object> bean1,Map<String, Object> bean2){
					if(bean1.get("sumRodNum") == null){
						return -1;
					}
					if(bean2.get("sumRodNum") == null){
						return -1;
					}
					System.out.println(bean1.get("uiId"));
					String d1 = bean1.get("sumRodNum").toString();
					String d2 = bean2.get("sumRodNum").toString();
					System.out.println("1111111111111111111");
					return new Double(d2).compareTo(new Double(d1));}
			});
		}
		result.put("userList", userList);
		result.put("list", list);
		return result;
	}

	//防作弊
	private List<Long> createNewXXlist(List<Map<String, Object>> uScoreBeforeXXList) {
		List<Long> ids = new ArrayList<>();
		for(Map<String, Object> userScore:uScoreBeforeXXList){
			MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
			//标准杆
			Integer standardRodNum = matchService.getIntegerValue(userScore,"ms_hole_standard_rod");
			//最大杆
			Integer maxRodNum = matchService.getIntegerValue(userScore,"max_rod_num");
			//id
			Long id = matchService.getLongValue(userScore,"ms_id");

			//Par3洞成绩超过5杆，此洞以5杆计算；
			if(standardRodNum == 3 && maxRodNum >5){
				userScore.put("ms_rod_num",5);
				userScore.put("max_rod_num",5);
			}
			//Par4洞成绩超过7杆，此洞以7杆计算；
			if(standardRodNum == 4 && maxRodNum >7){
				userScore.put("ms_rod_num",7);
				userScore.put("max_rod_num",7);
			}
			//Par5洞成绩超过9杆，此洞以9杆计算。
			if(standardRodNum == 5 && maxRodNum >9){
				userScore.put("ms_rod_num",9);
				userScore.put("max_rod_num",9);
			}
			ids.add(id);
		}
		return ids;
	}

	//格式化用户半场得分
	//计算净杆：公式:  差点=（其余12洞杆数总和×1.5—18洞标准杆数）×0.8。
	//为防作弊，合计12洞杆数总和时采2.3.4制：Par3洞成绩超过5杆，此洞以5杆计算；Par4洞成绩超过7杆，此洞以7杆计算；Par5洞成绩超过9杆，此洞以9杆计算。
	//是否par （0：否 1：是）比标准杆多一杆或者标准杆完成
	//净杆=总杆-差点
	public Integer createNewUserScore(List<MatchTotalUserScoreBean> userScoreList, List<Map<String, Object>> uScoreList,
									  List<Map<String, Object>> uScoreBeforeXXList) {
		Integer totalRod = 0;
		//杆差
		Integer totalRodCha = 0;
		for(Map<String, Object> map:uScoreList){
			MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
			//本洞记录id
			Long msId = matchService.getLongValue(map,"ms_id");
			//本洞标准杆
			Integer standardRodNum = matchService.getIntegerValue(map,"hole_standard_rod");

			//本洞打出的杆数
			Integer rodNum = matchService.getIntegerValue(map,"rod_num");
			if(uScoreBeforeXXList != null && uScoreBeforeXXList.size() >0){
				//使用新新贝利亚计算出来的杆数
				for(Map<String, Object> xxbly:uScoreBeforeXXList){
					//本洞记录id
					Long msIdxx = matchService.getLongValue(xxbly,"ms_id");
					if(msId.equals(msIdxx)){
						rodNum = matchService.getIntegerValue(xxbly,"ms_rod_num");
						break;
					}
				}
			}
			totalRod += rodNum;
			//杆数
			bean.setRodNum(rodNum);
			//本洞标准杆
			bean.setHoleStandardRod(standardRodNum);
			userScoreList.add(bean);
		}
		//每个半场的总杆数
		MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();
		bean.setRodNum(totalRod);
		userScoreList.add(bean);
		return totalRod;
	}
}
