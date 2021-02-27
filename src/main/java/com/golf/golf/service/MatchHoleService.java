package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.golf.bean.HoleMatchScoreResultBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchHoleResult;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScoreStartHole;
import com.golf.golf.db.UserInfo;
import com.golf.golf.enums.MatchResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

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
	 * 获取两球或四球比洞赛记分卡
	 * 1、如果是多队双人比赛，不管比杆比洞，每组，每个队不超过两人，也可以是一人，每组最多两个队。生成记分卡时，只有一个队的两个人才能放入一行。
	 * 2、对于单人比洞，每组只能两个人
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

        //用户总分
        List<Map<String, Object>> totalScoreList = new ArrayList<>();
        if (matchInfo.getMiMatchFormat2() == 2){ //四人四球
           for (Map<String, Object> user : userList) {
               Long uiId = matchService.getLongValue(user, "uiId");
               List<Map<String, Object>> totalScore = matchDao.getTotalScoreWithUser(matchId, uiId);
               Long uid = matchService.getLongValue(user, "uiId");
               Long tid = matchService.getLongValue(user, "team_id");
               totalScore.get(0).put("user_id", uid);
               totalScore.get(0).put("team_id", tid);
               totalScoreList.add( totalScore.get(0));
           }
         }else{
                //个人比洞及四人两球
            //第一个用户
            Long uiId = matchService.getLongValue(userList.get(0), "uiId");
            List<Map<String, Object>> totalScore = matchDao.getTotalScoreWithUser(matchId, uiId);
            totalScoreList.add(totalScore.get(0));
            //最后一个用户
            if( userList.size() >1 ){
                uiId = matchService.getLongValue(userList.get(userList.size()-1), "uiId");
                totalScore = matchDao.getTotalScoreWithUser(matchId, uiId);
                totalScoreList.add(totalScore.get(0));
            }
        }
        result.put("totalScoreList", totalScoreList);

		//解码用户昵称
		matchService.decodeUserNickName(userList);
		//设置用户名
		matchService.setUserName(userList);

		//第一行
		List<Map<String,Object>> userList0 = new ArrayList<>();
		//第二行
		List<Map<String,Object>> userList1 = new ArrayList<>();
		if (matchDao.groupIsGuest(groupId) ==1 ) { //助威组
            userList0.add(userList.get(0));
            if (userList.size() == 2) {
                userList1.add(userList.get(1));
            }else if (userList.size() >2) {
                userList0.add(userList.get(1));
                userList1.add(userList.get(2));
            }

            if (userList.size() > 3) {
                userList1.add(userList.get(3));
            }

        } else {
            Long teamIdTemp_ = null;
            for (Iterator<Map<String, Object>> userListIterator = userList.iterator(); userListIterator.hasNext(); ) {
                Map<String, Object> groupUser = userListIterator.next();
                Long teamId = matchService.getLongValue(groupUser, "team_id");
                if (teamIdTemp_ == null || teamId.equals(teamIdTemp_)) {
                    teamIdTemp_ = teamId;
                    userList0.add(groupUser);
                    userListIterator.remove();
                }
            }
            userList1.addAll(userList);
         }
        result.put("userList0", userList0);
        result.put("userList1", userList1);
		//第一条记录：半场球洞
		List<Map<String, Object>> parkHoleList = matchDao.getParkPartitionList(matchId);
		result.put("parkHoleList", parkHoleList);

		//获取每个用户得分，并计算成绩
		List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
		HoleMatchScoreResultBean endTrChengji = new HoleMatchScoreResultBean();
		Map<String, Object> mapResult = null;
		if (userList != null && userList.size() > 0) {
            mapResult = getChengjiList(chengjiList,endTrChengji,userList0,userList1,groupId,matchInfo,result);
		}
		//成绩行
		result.put("chengjiList", mapResult.get("chengjiList"));

		//固定的尾列：总标准杆数
		Long totalStandardRod = matchDao.getTotalRod(matchInfo);
		result.put("totalStandardRod", totalStandardRod);

		//固定的尾列：成绩行(一个数据)
		result.put("chengjiResult", endTrChengji);


		return result;
	}
   /**
    * 把同组每个用户的比洞赛逐洞比分以及双方比洞赛的成绩结果放到result中
    */
    public Map<String, Object> getChengjiList(List<HoleMatchScoreResultBean> chengjiList,HoleMatchScoreResultBean endTrChengji,
                               List<Map<String, Object>> userList0,List<Map<String, Object>> userList1,Long groupId, MatchInfo matchInfo, Map<String, Object> result) {
        //第一对儿用户 得分
        Long userId0 = matchService.getLongValue(userList0.get(0), "uiId");
        String userName00 = matchService.getName(userList0.get(0), "uiRealName");
        Long teamId0 = matchService.getLongValue(userList0.get(0),"team_id");
        List<Map<String, Object>> uscoreList00 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId0);
        for(int i=0;i<18;i++){
            uscoreList00.get(i).put("userId",userId0);//由于新记分卡在matchscore中没记录，查不到用户userId，在这里放进去
        }
        List<Map<String, Object>> uscoreList0 = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> uscoreList1 = new ArrayList<Map<String, Object>>();
        result.put("uscoreList00", uscoreList00);
        String userName01 = null;
        if(userList0.size() ==1 ){
            uscoreList0.addAll(uscoreList00);
        }else{
            userId0 = matchService.getLongValue(userList0.get(1), "uiId");
            userName01 = matchService.getName(userList0.get(1), "uiRealName");
            teamId0 = matchService.getLongValue(userList0.get(1),"team_id");
            List<Map<String, Object>> uscoreList01 = matchDao.getScoreByUserId(groupId, userId0, matchInfo, teamId0);
            result.put("uscoreList01", uscoreList01);

            for(int i=0;i<18;i++){

                uscoreList01.get(i).put("userId",userId0); //由于新记分卡在matchscore中没记录，查不到用户userId，在这里放进去
                Integer rodNum00 = matchService.getIntegerValueWithNull(uscoreList00.get(i),"rod_num");
                Integer rodNum01 = matchService.getIntegerValueWithNull(uscoreList01.get(i),"rod_num");
                Map<String, Object> map = new HashMap<String, Object>();
               // if( !"null".equals(rodNum00) && !"undefined".equals(rodNum00)){
                if ( rodNum00 !=null){
                    if(  rodNum01 !=null){
                        if (rodNum00 < rodNum01) {
                            map.put("rod_num", rodNum00);
                        } else {
                            map.put("rod_num", rodNum01);
                        }
                    } else { map.put("rod_num", rodNum00);}
                } else { map.put("rod_num", rodNum01);}



                //球洞号
                Integer holeNum0 = matchService.getIntegerValue(uscoreList00.get(i),"pp_hole_num");
                //球洞名称
                String holeName0 = matchService.getName(uscoreList00.get(i),"pp_name");
                //前后场
                String beforeAfter0 = matchService.getName(uscoreList00.get(i),"before_after");
                map.put("pp_hole_num", holeNum0);
                map.put("pp_name",holeName0);
                map.put("before_after",beforeAfter0);
                uscoreList0.add(map);
                }
        }
        //第二对儿用户 得分
        if (userList1.size()>0) {
            Long userId1 = matchService.getLongValue(userList1.get(0), "uiId");
            String userName10 = matchService.getName(userList1.get(0), "uiRealName");
            Long teamId1 = matchService.getLongValue(userList1.get(0), "team_id");
            List<Map<String, Object>> uscoreList10 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId1);
            for (int i = 0; i < 18; i++) {
                uscoreList10.get(i).put("userId", userId1);//由于新记分卡在matchscore中没记录，查不到用户userId，在这里放进去
            }
            result.put("uscoreList10", uscoreList10);
            String userName11 = null;
            if (userList1.size() == 1) {
                uscoreList1.addAll(uscoreList10);
            } else {
                userId1 = matchService.getLongValue(userList1.get(1), "uiId");
                userName11 = matchService.getName(userList1.get(1), "uiRealName");
                teamId1 = matchService.getLongValue(userList1.get(1), "team_id");
                List<Map<String, Object>> uscoreList11 = matchDao.getScoreByUserId(groupId, userId1, matchInfo, teamId1);
                result.put("uscoreList11", uscoreList11);

                for (int i = 0; i < 18; i++) {
                    uscoreList11.get(i).put("userId", userId1);//由于新记分卡在matchscore中没记录，查不到用户userId，在这里放进去
                    Integer rodNum10 = matchService.getIntegerValueWithNull(uscoreList10.get(i), "rod_num");
                    Integer rodNum11 = matchService.getIntegerValueWithNull(uscoreList11.get(i), "rod_num");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (rodNum10 != null) {
                        if (rodNum11 != null) {
                            if (rodNum10 < rodNum11) {
                                map.put("rod_num", rodNum10);
                            } else {
                                map.put("rod_num", rodNum11);
                            }
                        } else {
                            map.put("rod_num", rodNum10);
                        }
                    } else {
                        map.put("rod_num", rodNum11);
                    }

                    //球洞号
                    Integer holeNum1 = matchService.getIntegerValue(uscoreList10.get(i), "pp_hole_num");
                    //球洞名称
                    String holeName1 = matchService.getName(uscoreList10.get(i), "pp_name");

                    //前后场
                    String beforeAfter1 = matchService.getName(uscoreList10.get(i),"before_after");
                    map.put("pp_hole_num", holeNum1);
                    map.put("pp_name", holeName1);
                    map.put("before_after", beforeAfter1);
                    uscoreList1.add(map);
                }
            }

            //开球球洞
            MatchScoreStartHole scoreStartHole = matchService.getStartHole(matchInfo.getMiId(), groupId);
            //最终成绩
            Integer score = 0; //成绩
            Integer holeLeft = 18; //剩余洞数
            //从开球的洞开始算
            int startIndex = 0;
            if(scoreStartHole != null){
                if(scoreStartHole.getShBeforeAfter() == 0){
                    //前9洞
                    startIndex = scoreStartHole.getShHoleNum()-1;
                }else{
                    //后9洞
                    startIndex = 9 + scoreStartHole.getShHoleNum()-1;
                }
            }
            //截取列表 重新组合
            List<Map<String, Object>> uscoreList0end = new ArrayList<>(uscoreList0.subList(0,startIndex));
            List<Map<String, Object>> uscoreList0start = new ArrayList<>(uscoreList0.subList(startIndex,uscoreList0.size()));
            uscoreList0start.addAll(uscoreList0end);
            uscoreList0.clear();
            uscoreList0.addAll(uscoreList0start);

            //截取列表 重新组合
            List<Map<String, Object>> uscoreList1end = new ArrayList<>(uscoreList1.subList(0,startIndex));
            List<Map<String, Object>> uscoreList1start = new ArrayList<>(uscoreList1.subList(startIndex,uscoreList1.size()));
            uscoreList1start.addAll(uscoreList1end);
            uscoreList1.clear();
            uscoreList1.addAll(uscoreList1start);
            for (Map<String, Object> map0 : uscoreList0) {
                //第一对儿用户 杆数
                Integer rodNum0 = matchService.getIntegerValueWithNull(map0, "rod_num");
                //球洞号
                Integer holeNum0 = matchService.getIntegerValue(map0, "pp_hole_num");
                //球洞名称
                String holeName0 = matchService.getName(map0, "pp_name");
                //前后场
                Integer beforeAfter0 = matchService.getIntegerValue(map0, "before_after");
                HoleMatchScoreResultBean bean = new HoleMatchScoreResultBean();
                bean.setBeforeAfter(beforeAfter0);
                bean.setHoleName(holeName0);
                bean.setHoleNum(holeNum0);
                for (Map<String, Object> map1 : uscoreList1) {
                    //第二对儿用户 杆数
                    Integer rodNum1 = matchService.getIntegerValueWithNull(map1, "rod_num");
                    //球洞号
                    Integer holeNum1 = matchService.getIntegerValue(map1, "pp_hole_num");
                    //球洞名称
                    String holeName1 = matchService.getName(map1, "pp_name");
                    //前后场
                    Integer beforeAfter1 = matchService.getIntegerValue(map1, "before_after");
                    if (holeNum0.equals(holeNum1) && holeName0.equals(holeName1) && beforeAfter0.equals(beforeAfter1)) {
                        //设置开球球洞的颜色
                        if(scoreStartHole != null && holeNum0.equals(scoreStartHole.getShHoleNum())
                                && holeName0.equals(scoreStartHole.getShHoleName())
                                && beforeAfter0.equals(scoreStartHole.getShBeforeAfter())){
                            bean.setIsStartHole(1);
                        }
                        if (rodNum0 != null && rodNum1 != null) {
                            holeLeft -=1;
//                你设一个“得分”变量，初始值为0，
//                A1洞，双方平，则得分=等分+0，
//                A2洞，上方赢，则得分=得分+1=1，
//                A3洞，下方赢，则得分=得分-1=0，
//                A4洞，上方赢，则得分=得分+1=1，
//                依次类推，得分是个计数，不是杆差。
                            if (rodNum0.equals(rodNum1)) {
                                //打平
                                score = score + 0;
                            } else if (rodNum0 < rodNum1) {
                                //上赢
                                score = score + 1;
                            } else if (rodNum0 > rodNum1) {
                                //下赢
                                score = score - 1;
                            }
                            if (score == 0) {
                                bean.setNum(null);
                                bean.setUpDnAs(MatchResultEnum.AS.text());
                            } else if (score > 0) {
                                bean.setNum(score);
                                bean.setUpDnAs(MatchResultEnum.UP.text());
                            } else if (score < 0) {
                                bean.setNum(Math.abs(score));
                                bean.setUpDnAs(MatchResultEnum.DN.text());
                            }
                            BeanUtils.copyProperties(bean, endTrChengji);
                            //同时更新比洞赛输赢表
                            if (matchDao.groupIsGuest(groupId) != 1) { // 比赛组，
                                //看有没有子比赛
                                Integer childId = matchDao.childExist(matchInfo.getMiId(),teamId0,teamId1);
                                Integer childMax = matchDao.getMaxMatchChildId(matchInfo.getMiId());
                                if (childMax == null ){childMax = 0; }
                               //更新第一队记录
                                MatchHoleResult matchHoleResult = matchDao.getMatchHoleResult(matchInfo.getMiId(), groupId, teamId0);
                                if (matchHoleResult == null) {
                                    matchHoleResult = new MatchHoleResult();
                                    matchHoleResult.setMhrMatchId(matchInfo.getMiId());
                                    if ( childId==0) {
                                        matchHoleResult.setMhrMatchChildId(childMax + 1);
                                    } else {
                                        matchHoleResult.setMhrMatchChildId(childId);
                                    }
                                    matchHoleResult.setMhrGroupId(groupId);
                                    matchHoleResult.setMhrTeamId(teamId0);
                                }
                                matchHoleResult.setMhrUserName0(userName00);
                                matchHoleResult.setMhrUserName1(userName01);
                                matchHoleResult.setMhrHoleLeft(holeLeft);
                                matchHoleResult.setMhrResult(score);

                                if(matchHoleResult.getMhrId() == null) {
                                    matchDao.save(matchHoleResult);
                                }else{
                                    matchDao.update(matchHoleResult);
                                }
                               //更新第二队记录
                                matchHoleResult = matchDao.getMatchHoleResult(matchInfo.getMiId(), groupId, teamId1);
                                if (matchHoleResult == null) {
                                    matchHoleResult = new MatchHoleResult();
                                    matchHoleResult.setMhrMatchId(matchInfo.getMiId());
                                    if ( childId==0) {
                                        matchHoleResult.setMhrMatchChildId(childMax + 1);
                                    } else {
                                        matchHoleResult.setMhrMatchChildId(childId);
                                    }
                                    matchHoleResult.setMhrGroupId(groupId);
                                    matchHoleResult.setMhrTeamId(teamId1);
                                }
                                matchHoleResult.setMhrUserName0(userName10);
                                matchHoleResult.setMhrUserName1(userName11);
                                matchHoleResult.setMhrHoleLeft(holeLeft);
                                matchHoleResult.setMhrResult(-score);

                                if(matchHoleResult.getMhrId() == null) {
                                    matchDao.save(matchHoleResult);
                                }else{
                                    matchDao.update(matchHoleResult);
                                }
                            }
                        }
                        chengjiList.add(bean);
                        break;
                    }
                }
            }
        }
        if(chengjiList != null && chengjiList.size() > 0){
            //成绩 重新排序  场次-球洞号
            chengjiList = chengjiList.stream()
                    .sorted(Comparator.comparing(HoleMatchScoreResultBean::getBeforeAfter)
                            .thenComparing(HoleMatchScoreResultBean::getHoleName)
                            .thenComparing(HoleMatchScoreResultBean::getHoleNum)
                     ).collect(Collectors.toList());
        }
        result.put("uscoreList0", uscoreList0);
        result.put("uscoreList1", uscoreList1);
        result.put("chengjiList", chengjiList);
        return result;
    }

    private static Integer comparingByBeforeAfter(Map<String, Object> map){
        return ((Integer) map.get("before_after"));
    }

    private static String comparingByHoleName(Map<String, Object> map){
        return (String) map.get("pp_name");
    }

    private static Integer comparingByHoleNum(Map<String, Object> map){
        return (Integer) map.get("pp_hole_num");
    }


    public static void main(String[] args) {
        List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
        HoleMatchScoreResultBean bean1 = new HoleMatchScoreResultBean();
        bean1.setBeforeAfter(1);
        bean1.setHoleNum(1);
        HoleMatchScoreResultBean bean2 = new HoleMatchScoreResultBean();
        bean2.setBeforeAfter(0);
        bean2.setHoleNum(5);
        HoleMatchScoreResultBean bean3 = new HoleMatchScoreResultBean();
        bean3.setHoleNum(3);
        chengjiList.add(bean1);
        chengjiList.add(bean2);
        chengjiList.add(bean3);
        List<HoleMatchScoreResultBean> chengjiList1 = new ArrayList<>(chengjiList.subList(0,1));
        List<HoleMatchScoreResultBean> chengjiList2 = new ArrayList<>(chengjiList.subList(1,chengjiList.size()));
        chengjiList1.addAll(chengjiList2);
        System.out.println(chengjiList1);


        /*List<HoleMatchScoreResultBean> list = chengjiList.stream()
                .sorted(Comparator.comparing(HoleMatchScoreResultBean::getBeforeAfter)
                        .thenComparing(HoleMatchScoreResultBean::getHoleNum)).collect(Collectors.toList());
        chengjiList.forEach(r -> System.out.println(r.getHoleNum()));
        System.out.println("===================================");
        list.forEach(r -> System.out.println(r.getHoleNum()));*/
    }


}
