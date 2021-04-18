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
        Integer matchType =  matchInfo.getMiType();
        List<Long> childMatchIds = matchService.getLongIdListReplace(matchInfo.getMiChildMatchIds());
        result.put("matchInfo", matchInfo);
        //本比赛要去掉的随机洞
        List<MatchGroupUserScoreBean> list = new ArrayList<>();
        List<Map<String, Object>> userList;
        MatchScoreNetHole scoreNetHole;
        /*
        MatchScoreNetHole scoreNetHole = matchScoreDao.getMatchNetRodHole(matchId);

        //本比赛的所有用户和其去掉随机杆后的总杆数，为0的排后面(首列显示)
        List<Map<String, Object>> userList = null;
        if(scoreNetHole == null || (scoreNetHole != null
                && StringUtils.isEmpty(scoreNetHole.getMsntHoleBeforeName()) && StringUtils.isEmpty(scoreNetHole.getMsntHoleAfterName()))){
            result.put("list", list);
            userList = new ArrayList<>();
            result.put("userList", userList);
            return result;
        }
*/
        //所有球洞
        List<MatchTotalUserScoreBean> parkHoleList = new ArrayList<>();
        //每洞杆数
        List<MatchTotalUserScoreBean> parkRodList = new ArrayList<>();
        //获取前半场球洞
        Long parkMatchId;
        if ( matchInfo.getMiType() ==2 ){
            parkMatchId=childMatchIds.get(0);
        } else {
            parkMatchId = matchId;
        }
        List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList( parkMatchId,0);
        matchService.getNewParkHoleList(parkHoleList,parkRodList,beforeParkHoleList);
        //获取后半场球洞
        List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList( parkMatchId,1);
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

        //获取用户及成绩列表

        if(matchType ==2){
            userList =matchScoreDao.getUserScorePointByMatchId(matchId,null,matchInfo.getMiType(), childMatchIds);
        } else{
            userList = matchScoreDao.getUserListByMatchId(matchInfo,childMatchIds);
            createNewUserNetScoreList(userList, list, matchInfo);
        }

        result.put("userList", userList);
        result.put("list", list);
        return result;
    }

  //计算净杆
  public void createNewUserNetScoreList(List<Map<String, Object>> userList, List<MatchGroupUserScoreBean> list,MatchInfo matchInfo) {
        Integer matchType =  matchInfo.getMiType();
        MatchScoreNetHole scoreNetHole;
        Long sum18 = matchScoreDao.getSumStandardRod(matchInfo);

      if (userList != null && userList.size() > 0) {
          for (Map<String, Object> user : userList) {

              MatchGroupUserScoreBean bean = new MatchGroupUserScoreBean();
                  Integer holeCount = matchService.getIntegerValue(user, "holeCount");
                  Long userId = matchService.getLongValue(user, "uiId");
                  Long childMatchId = matchService.getLongValue(user, "match_id");
                  Long teamId = matchService.getLongValue(user, "team_id");
                  Long groupId = matchService.getLongValue(user, "group_id");
                  String teamAbbrev = user.get("teamAbbrev") == null ? null : user.get("teamAbbrev").toString();
                  String userRealName = user.get("uiRealName") == null ? null : user.get("uiRealName").toString();
                  String userHeadimg = user.get("uiHeadimg") == null ? null : user.get("uiHeadimg").toString();
                  String matchName = user.get("matchName") == null ? null : user.get("matchName").toString();
                  //本用户的前后半场总得分情况
                  List<MatchTotalUserScoreBean> userScoreList = new ArrayList<>();

                  //本用户前半场得分情况
                  List<Map<String, Object>> uScoreBeforeList = matchDao.getBeforeAfterScoreByUserId(userId, childMatchId, 0, null);
                  //防作弊计算前半场9洞的总杆数
                  scoreNetHole = matchScoreDao.getMatchNetRodHole(childMatchId);
                  Integer beforeTotalRodNum = createNewUserScore(userScoreList, uScoreBeforeList, scoreNetHole);

                  //本用户后半场得分情况
                  List<Map<String, Object>> uScoreAfterList = matchDao.getBeforeAfterScoreByUserId(userId, childMatchId,  1, null);
                  //防作弊计算后半场9洞的总杆数
                  Integer afterTotalRodNum = createNewUserScore(userScoreList, uScoreAfterList, scoreNetHole);
                  bean.setUserScoreTotalList(userScoreList);
                  bean.setUserId(userId);
                  bean.setUserName(userRealName);
                  bean.setUserHeadimg(userHeadimg);
                  bean.setTeamAbbrev(teamAbbrev);
                  bean.setTeamId(teamId);
                  bean.setMatchId(childMatchId);
                  bean.setMatchName(matchName);
                  bean.setGroupId(groupId);
                  bean.setHoleCount(holeCount);
                  list.add(bean);

                  //计算净杆：公式:  差点=（其余12洞杆数总和×1.5—18洞标准杆数）×0.8。
                  //为防作弊，合计12洞杆数总和时采2.3.4制：
                  //先算出18洞的新新贝利亚总杆：Par3洞成绩超过5杆，此洞以5杆计算；Par4洞成绩超过7杆，此洞以7杆计算；Par5洞成绩超过9杆，此洞以9杆计算。
                  //12洞总杆 = 18洞的新新贝利亚防作弊总杆，减去6洞总杆
                  //是否par （0：否 1：是）比标准杆多一杆或者标准杆完成
                  //净杆=总杆-差点


                  //不防作弊时用户的18洞总杆数
                  Long sumRod = matchScoreDao.getSumRod(userId, childMatchId);
                  //防作弊18洞总杆数
                  Integer sumRodXxbly = beforeTotalRodNum + afterTotalRodNum;
                  if (holeCount < 18) {
                      //有球洞没记分，就不计算差点
                      sumRod = 0L;
                  }
                  if (sumRod != null && sumRod != 0) {
                      //本用户随机抽取的6个洞总杆数
                      //Long radomSumRod = matchScoreDao.getRandomSumRod(userId,matchInfo,beforeHoleNum,afterHoleNum);

                      //差点 加个提示

                      BigDecimal ch = new BigDecimal((sumRodXxbly * 1.5 - sum18) * 0.8).setScale(2, BigDecimal.ROUND_HALF_UP);
                      double cha = ch.doubleValue();

                      //净杆
                      double netRod = sumRod.doubleValue() - cha;
                      //保留两位小数
                      netRod = (double) Math.round(netRod * 100) / 100;

                      user.put("sumRodNum", netRod);
                      bean.setTotalNetRodScore(netRod);
                  } else {
                      user.put("sumRodNum", 0);
                      bean.setTotalNetRodScore(0);
                  }

          }
      }

      if (userList != null && userList.size() > 0) {
          //排序 差点越高名次越好
          Collections.sort(userList, new Comparator<Map<String, Object>>() {
              @Override
              public int compare(Map<String, Object> bean1, Map<String, Object> bean2) {
                  Double d1 = Double.valueOf(bean1.get("sumRodNum").toString());
                  Double d2 = Double.valueOf(bean2.get("sumRodNum").toString());
                  if ((d1 == null || d1 == 0) && (d2 == null || d2 == 0)) {
                      return 0;
                  }
                  if (d1 == null || d1 == 0) {
                      return 1;
                  }
                  if (d2 == null || d2 == 0) {
                      return -1;
                  }
                  return new Double(d1).compareTo(new Double(d2));
              }
          });
      }

      if (list != null && list.size() > 0) {
          List<MatchGroupUserScoreBean> newList = new ArrayList<>();
          newList.addAll(list.subList(2, list.size()));
          list.removeAll(list.subList(2, list.size()));
          //排序 差点越高名次越好
          Collections.sort(newList, new Comparator<MatchGroupUserScoreBean>() {
              @Override
              public int compare(MatchGroupUserScoreBean bean1, MatchGroupUserScoreBean bean2) {
                  Double d1 = new Double(bean1.getTotalNetRodScore());
                  Double d2 = new Double(bean2.getTotalNetRodScore());
                  if ((d1 == null || d1 == 0) && (d2 == null || d2 == 0)) {
                      return 0;
                  }
                  if (d1 == null || d1 == 0) {
                      return 1;
                  }
                  if (d2 == null || d2 == 0) {
                      return -1;
                  }
                  return new Double(d1).compareTo(new Double(d2));
              }
          });
          list.addAll(newList);
      }

  }
	//格式化用户半场得分
	//计算净杆：公式:  差点=（其余12洞杆数总和×1.5—18洞标准杆数）×0.8。
	//为防作弊，合计12洞杆数总和时采2.3.4制：Par3洞成绩超过5杆，此洞以5杆计算；Par4洞成绩超过7杆，此洞以7杆计算；Par5洞成绩超过9杆，此洞以9杆计算。
	//是否par （0：否 1：是）比标准杆多一杆或者标准杆完成
	//净杆=总杆-差点

    public Integer createNewUserScore(List<MatchTotalUserScoreBean> userScoreList, List<Map<String, Object>> uScoreList,
                                      MatchScoreNetHole scoreNetHole) {
        Integer totalRod = 0;
        //杆差
        Integer totalRodCha = 0;
        for(Map<String, Object> map:uScoreList){
            MatchTotalUserScoreBean bean = new MatchTotalUserScoreBean();

            //本洞号码
            Integer holeNum = matchService.getIntegerValue(map,"pp_hole_num");

            //本洞名字
            String holeName = matchService.getName(map,"pp_name");

            //本洞标准杆
            Integer standardRodNum = matchService.getIntegerValue(map,"pp_hole_standard_rod");

            //本洞打出的杆数
            Integer rodNum = matchService.getIntegerValue(map,"rod_num");
            //如果某洞没记分，则这洞按double par-1 处理
            if (rodNum ==0) {
                rodNum= 2*standardRodNum-1;
            }
            //下面算法的原理是对所有的洞做去顶的处理求和 然后减去抽去六洞的不去顶的和。
            if ((holeName.equals(scoreNetHole.getMsntHoleBeforeName()) && holeNum !=scoreNetHole.getMsntHoleBeforeNum1() && holeNum !=scoreNetHole.getMsntHoleBeforeNum2()
                 && holeNum !=scoreNetHole.getMsntHoleBeforeNum3()) ||(holeName.equals(scoreNetHole.getMsntHoleAfterName()) && holeNum !=scoreNetHole.getMsntHoleAfterNum1()
                    && holeNum !=scoreNetHole.getMsntHoleAfterNum2()
                    && holeNum !=scoreNetHole.getMsntHoleAfterNum3())){
                if(rodNum>=2*standardRodNum) { rodNum = 2*standardRodNum-1;}
                totalRod += rodNum;
            }else {
                if(rodNum>=2*standardRodNum) {
                    totalRod -= rodNum-2*standardRodNum+1;
                    }
            }
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
