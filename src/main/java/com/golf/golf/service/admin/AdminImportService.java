package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminImportDao;
import com.golf.golf.db.*;
import com.golf.golf.service.MatchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell; //nhq
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导入成绩
 * @author nmy
 * 2016年10月31日
 */
@Service
public class AdminImportService implements IBaseService {
	private final static Logger logger = LoggerFactory.getLogger(AdminImportService.class);

	@Autowired
	private AdminImportDao adminImportDao;
	@Autowired
	private MatchService matchService;

	/**
	 * 导入球队详情
	 * 是否覆盖：1：是 0：否
	 * @return
	 */
    //为提高效率，如果导入数据时判断是是个新比赛，后面导入数据时就不先检索数据库判断存在不存在 nhq
	int isnewMatch = 1;

	public String importTeamInfo(XSSFWorkbook xwb) {
		//参赛球队id
		String joinTeamIds = "";
		//第2张工作表
		XSSFSheet sheet = xwb.getSheetAt(1);
		XSSFRow row;
		for(int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第2行开始
			row = sheet.getRow(i);
			if(row.getCell(0) == null || StringUtils.isEmpty(row.getCell(0).toString())){
				break;
			}
			//第一列参赛球队全称
			String teamName = row.getCell(0).toString();
			//第一列参赛球队简称
			String teamNameAbbrev = row.getCell(1).toString();
			//查询是否有该球队
			//加上队长信息来判断 nhq
			String teamCaps = row.getCell(2).toString();
			String[] teamCapsSplit = teamCaps.split(",");

			TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName,teamNameAbbrev);


			if(teamInfo == null){
				teamInfo = new TeamInfo();
				teamInfo.setTiName(teamName);
				teamInfo.setTiAbbrev(teamNameAbbrev);
				teamInfo.setTiLogo("");
				teamInfo.setTiSignature("");
				teamInfo.setTiDigest("");
				teamInfo.setTiAddress("");
				teamInfo.setTiJoinOpenType(0);
				teamInfo.setTiInfoOpenType(1);
				teamInfo.setTiUserInfoType(0);
				teamInfo.setTiMatchResultAuditType(0);
				teamInfo.setTiIsValid(1);
				teamInfo.setTiCreateTime(System.currentTimeMillis());
				//teamInfo.setTiCreateUserId(AdminUserUtil.getUserId());
				//teamInfo.setTiCreateUserName(AdminUserUtil.getShowName());
                //把新球队创建者直接写成牛合庆，方便对比赛配置进行修改 nhq
                Long userId =Long.parseLong("3");
                teamInfo.setTiCreateUserId(userId);
                teamInfo.setTiCreateUserName("牛合庆");
				adminImportDao.save(teamInfo);
			}
			joinTeamIds += teamInfo.getTiId().toString()+",";
		}
		return joinTeamIds;
	}


	/**
	 * 导入比赛详情
	 * 是否覆盖：1：是 0：否
	 * @return
	 */
	public MatchInfo importMatchInfo(XSSFWorkbook xwb,String joinTeamIds) throws Exception {
		//第1张工作表
		XSSFSheet sheet = xwb.getSheetAt(0);
		//从第2行开始
		XSSFRow row = sheet.getRow(1);
		//比赛标题
		String matchTitle = row.getCell(0).toString().trim();
		//球场
		String parkName = row.getCell(1).toString();
		ParkInfo parkInfo = adminImportDao.getParkInfoByName(parkName);
		if(parkInfo == null){
			return null;
		}
		//场地（A,B）
		String changdi = row.getCell(2).toString();
		String[] changdiSplit = changdi.split(",");
		//比赛时间
		String matchTime = row.getCell(3).toString();
		Long mTime = TimeUtil.stringToLong(matchTime,TimeUtil.NOW_DATE_CN_NO_DAY);
		matchTime = TimeUtil.longToString(mTime,TimeUtil.FORMAT_DATE);
		//赛制
		String matchType = row.getCell(4).toString();
		String[] matchTypeSplit = matchType.split(",");
		//类型：比分/报名
        String isEnd = row.getCell(5).toString();

		//按比赛名称和比赛时间查看比赛是否存在
		MatchInfo matchInfo = adminImportDao.getMatchInfoByMatchTitle(matchTitle,matchTime);
		if(matchInfo == null){
			matchInfo = new MatchInfo();
			matchInfo.setMiType(1);
			matchInfo.setMiTitle(matchTitle);
			matchInfo.setMiLogo("");
			matchInfo.setMiParkId(parkInfo.getPiId());
			matchInfo.setMiParkName(parkInfo.getPiName());
			matchInfo.setMiZoneBeforeNine(changdiSplit[0]);
			matchInfo.setMiZoneAfterNine(changdiSplit[1]);
			matchInfo.setMiMatchTime(matchTime);
			matchInfo.setMiMatchOpenType(1);
			matchInfo.setMiJoinOpenType(1);
			int i;
			if (matchTypeSplit[0].equals("两球")){
			    i=1;
            }  else if(matchTypeSplit[0].equals("四球")){
			    i = 2;
            } else { i=0;}
			matchInfo.setMiMatchFormat2(i);
			matchInfo.setMiMatchFormat1(matchTypeSplit[1].equals("比杆")?0:1);
			matchInfo.setMiJoinTeamIds(joinTeamIds);
			if (isEnd== null || isEnd =="" || isEnd.equals("比分")) {
                matchInfo.setMiIsEnd(1);
            } else {matchInfo.setMiIsEnd(0);}
			matchInfo.setMiIsValid(1);
			matchInfo.setMiCreateTime(System.currentTimeMillis());
			//matchInfo.setMiCreateUserId(AdminUserUtil.getUserId());
			//matchInfo.setMiCreateUserName(AdminUserUtil.getShowName());
		    //把比赛创建者直接写成牛合庆，方便对比赛配置进行修改 nhq
		    Long userId =Long.parseLong("3");
			matchInfo.setMiCreateUserId(userId);
		    matchInfo.setMiCreateUserName("牛合庆");
			adminImportDao.save(matchInfo);
		} else {
			isnewMatch = 0; //比赛已存在
			matchInfo.setMiJoinTeamIds(joinTeamIds);
			adminImportDao.update(matchInfo);
		}
		return matchInfo;
	}

	/**
	 * 导入用户、导入用户球队mapping、导入用户比赛maping、导入比赛分组
	 * @return
	 */
	public void importData(XSSFWorkbook xwb,MatchInfo matchInfo) {
	    //导入的类型
        Integer isEnd = matchInfo.getMiIsEnd();
		//参赛队id
		List<Long> joinTeamIdList = getLongList(matchInfo.getMiJoinTeamIds());
		//Arrays.asList(matchInfo.getMiJoinTeamIds().split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		//第3张工作表
		XSSFSheet sheet = xwb.getSheetAt(2);
		XSSFRow row;
		//第二张表，用于用简称找到全称
        XSSFSheet sheet1 = xwb.getSheetAt(1);
        XSSFRow row1;

        XSSFCell cell;
		for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第3行开始
			row = sheet.getRow(i);
			//第4列 球友
			String userName = row.getCell(3).toString().trim();
            //如果用户名为空，不导入不导入
            if (userName == null || userName == "") {
                continue;
            }
            String teamAbbrev;
			try {
                //第二列 球队
                teamAbbrev = row.getCell(1).toString().trim();

            } catch (Exception e){
                teamAbbrev ="";
            }

            String groupName;
            try {
                //第三列 分组
                groupName = row.getCell(2).toString().trim();

            } catch (Exception e){
                groupName = "";
            }
            /*
            cell = row.getCell(2);
            String groupName = null;
            if (cell != null ) {
                if (cell.getCellType() == 2) { //公式型,这部分为了解决单元格有公式
                    try {
                        groupName = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        groupName = String.valueOf(cell.getRichStringCellValue());
                    }
                } else {
                    groupName = row.getCell(2).toString().trim();
                }
            }
            */


             //导入用户 按人名+球队识别
             //得到球队全称
             String teamName = "";
             String teamAbbrev1 = "";
             if (teamAbbrev != null && teamAbbrev != "") {
                  for (int j = 1; j < sheet1.getPhysicalNumberOfRows(); j++) {
                     row1 = sheet1.getRow(j);
                     teamAbbrev1 = row1.getCell(1).toString().trim();
                     if (teamAbbrev.equals(teamAbbrev1)) {
                         teamName = row1.getCell(0).toString().trim();
                         break;
                      }
                   }
             }

             if (teamName != "" ) {

                    //用球队全称查看是否有该球友
                    Map<String, Object> userTeamMapping = adminImportDao.getUserByRealNameTeamName(userName, teamName, teamAbbrev);

                    UserInfo userInfo = null;
                    TeamInfo teamInfo = null;
                    Integer userType = 1;
                    Long userId = null;
                    //下面这个判断改变了用实名和球队名一起判断的逻辑，改由人的实名判断
                    if (userTeamMapping==null){
                        Map<String, Object> user = adminImportDao.getUserByRealName(userName);
                        userId = matchService.getLongValue(user, "userId");

                       if ( userId == null) {
                           userInfo = new UserInfo();
                           userInfo.setUiType(2);
                           userInfo.setUiIsValid(1);
                           userInfo.setUiRealName(userName);
                           userInfo.setUiCreateTime(System.currentTimeMillis());
                           userInfo.setUiCreateUserId(AdminUserUtil.getUserId());
                           userInfo.setUiCreateUserName(AdminUserUtil.getShowName());
                           adminImportDao.save(userInfo);
                       } else{
                           userInfo = adminImportDao.get(UserInfo.class, userId);
                       }
                           //导入用户球队mapping
                           teamInfo = this.importTeamUserMap(teamAbbrev, userInfo.getUiId(), joinTeamIdList);

                    } else {
                        userId = matchService.getLongValue(userTeamMapping, "userId");
                        Long teamId = matchService.getLongValue(userTeamMapping, "teamId");
                        userType = matchService.getIntegerValueWithNull(userTeamMapping, "userType");
                        if (userId == 3 ) {userType = 0;} //把牛合庆直接设置为赛长
                        userInfo = adminImportDao.get(UserInfo.class, userId);
                        teamInfo = adminImportDao.get(TeamInfo.class, teamId);
                    }
                    //导入用户比赛mapping
                    MatchGroup groupInfo = this.importMatchUserMap(matchInfo.getMiId(), teamInfo.getTiId(), groupName, userInfo.getUiId(), userName,userType,AdminUserUtil.getUserId(),AdminUserUtil.getShowName());
                   if (isEnd == 1) { // 导入比分
                       //从第5列开始 每洞得分
                       //前9洞
                       for (int j = 4; j < 13; j++) {
                          Integer holeNum = j - 3;
                          saveOrUpdateUserScore(row, j, holeNum, matchInfo, teamInfo, groupInfo, userInfo, 0);
                       }
                       //后9洞
                       for (int j = 14; j < 23; j++) {
                          Integer holeNum = j - 13;
                          saveOrUpdateUserScore(row, j, holeNum, matchInfo, teamInfo, groupInfo, userInfo, 1);
                      }
                 }

                } else {
                    if (isEnd == 1) {
                      continue;

                    } else {
                        //导入报名，
                        Map<String,Object> userTeamMapping = adminImportDao.findTeamUserMappingByUserName(userName, joinTeamIdList);
                        if ( userTeamMapping != null) {
                            Long userId = matchService.getLongValue(userTeamMapping, "userId");
                            Long teamId = matchService.getLongValue(userTeamMapping, "teamId");
                            Integer userType = matchService.getIntegerValue(userTeamMapping, "userType");
                            if (userId == 3 ) {userType = 0;} //把牛合庆直接设置为赛长
                            this.importMatchUserMap(matchInfo.getMiId(), teamId, groupName, userId, userName,userType,AdminUserUtil.getUserId(),AdminUserUtil.getShowName());
                        }

                    }
            }
		}
	}

	private List<Long> getLongList(String miJoinTeamIds) {
		List<Long> list = new ArrayList<>();
		String[] idSplit = miJoinTeamIds.split(",");
		for(String id :idSplit){
			if(StringUtils.isNotEmpty(id)){
				list.add(Long.parseLong(id));
			}
		}
		return list;
	}

	/**
	 * 导入用户比赛 mapping 和比赛group
	 * @return
	 */
	public MatchGroup importMatchUserMap(Long matchId, Long teamId, String groupName, Long userId, String userName,Integer userType,Long cUserId,String cUserName) {
        MatchGroup matchGroup = null;
	    if (groupName !=null && groupName !="" ) {
            Double d = Double.parseDouble(groupName);
            groupName = d.intValue() + "";
            //查询是否有分组
            matchGroup = adminImportDao.getMatchGroupByName(matchId, groupName);
            if (matchGroup == null) {
                matchGroup = new MatchGroup();
                matchGroup.setMgMatchId(matchId);
                matchGroup.setMgGroupName(groupName);
                matchGroup.setMgCreateTime(System.currentTimeMillis());
                matchGroup.setMgCreateUserId(cUserId);
                matchGroup.setMgCreateUserName(cUserName);
                adminImportDao.save(matchGroup);
            }
        }
		//查询是否有用户比赛mapping
		   MatchUserGroupMapping matchUserGroupMapping = adminImportDao.getMatchUserMapping(matchId, teamId, userId);

	    if(matchUserGroupMapping == null){
			matchUserGroupMapping = new MatchUserGroupMapping();
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmTeamId(teamId);
			matchUserGroupMapping.setMugmUserId(userId);
			matchUserGroupMapping.setMugmUserName(userName);
			matchUserGroupMapping.setMugmUserType(userType);
			if (userType ==0 ) {
			    matchUserGroupMapping.setMugmIsAutoCap(1);
            } else {
                matchUserGroupMapping.setMugmIsAutoCap(0);
            }

			if ( matchGroup != null) {
                matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
                matchUserGroupMapping.setMugmGroupName(groupName);
                matchUserGroupMapping.setMugmIsDel(0);
            } else {
                matchUserGroupMapping.setMugmIsDel(1);
            }
			matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
			matchUserGroupMapping.setMugmCreateUserId(cUserId);
			matchUserGroupMapping.setMugmCreateUserName(cUserName);
			adminImportDao.save(matchUserGroupMapping);
		} else {
            matchUserGroupMapping.setMugmUserName(userName);
            matchUserGroupMapping.setMugmUserType(userType);
            if (userType ==0 ) {
                matchUserGroupMapping.setMugmIsAutoCap(1);
            } else {
                matchUserGroupMapping.setMugmIsAutoCap(0);
            }
            if ( matchGroup != null) {
                matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
                matchUserGroupMapping.setMugmGroupName(groupName);
                matchUserGroupMapping.setMugmIsDel(0);
            } else {
                matchUserGroupMapping.setMugmIsDel(1);
            }
            adminImportDao.update(matchUserGroupMapping);
        }
	    if (matchGroup ==null) {
            return null;
        } else {
	        return matchGroup;
        }
	}

	/**
	 * 导入用户球队mapping
	 * @return
	 */
	private TeamInfo importTeamUserMap(String teamAbbrevName, Long userId, List<Long> joinTeamIdList) {
		//获取球队id
		TeamInfo teamInfo = adminImportDao.getTeamInfoByAbbrevName(teamAbbrevName,joinTeamIdList);
		//查看是否有该球友的teamUserMapping
		TeamUserMapping teamUserMapping = adminImportDao.getTeamUserMappingByTeamId(teamInfo.getTiId(),userId);
		if(teamUserMapping == null){
			teamUserMapping = new TeamUserMapping();
			teamUserMapping.setTumUserType(1);
			teamUserMapping.setTumTeamId(teamInfo.getTiId());
			teamUserMapping.setTumUserId(userId);
			teamUserMapping.setTumCreateTime(System.currentTimeMillis());
			teamUserMapping.setTumCreateUserId(AdminUserUtil.getUserId());
			teamUserMapping.setTumCreateUserName(AdminUserUtil.getShowName());
			adminImportDao.save(teamUserMapping);
		}
		return teamInfo;
	}


	/**
	 * 新增或更新球友成绩
	 * @return
	 */
	private void saveOrUpdateUserScore(XSSFRow row, int j, Integer holeNum,MatchInfo matchInfo,TeamInfo teamInfo,
									   MatchGroup matchGroup,UserInfo userInfo,Integer beforeAfter) {
		XSSFCell cell =row.getCell(j);
		String scoreEveHole = null;
		Integer score = 0;
		if (cell.getCellType() == 2) { //公式型,这部分为了解决单元格有公式
			try {
				scoreEveHole = String.valueOf(cell.getNumericCellValue());
			} catch (IllegalStateException e) {
				scoreEveHole = String.valueOf(cell.getRichStringCellValue());
			}
		}else{
				scoreEveHole = row.getCell(j).toString().trim();
		}
		if (StringUtils.isNotEmpty(scoreEveHole)) {
			Double d = Double.parseDouble(scoreEveHole);
			score = d.intValue();
		}
		//获取本球场第j洞的详情
		String holeName = "";
		if(beforeAfter == 0){
			holeName = matchInfo.getMiZoneBeforeNine();
		}else{
			holeName = matchInfo.getMiZoneAfterNine();
		}
		ParkPartition parkPartition = adminImportDao.getParkPartition(matchInfo.getMiParkId(),holeNum,holeName);
		MatchScore matchScore = null;
		if (isnewMatch == 0 ) {
			matchScore = adminImportDao.getMatchScoreByUser(
					teamInfo.getTiId(), matchInfo.getMiId(), matchGroup.getMgGroupName(), userInfo.getUiId(), holeNum, holeName, beforeAfter);
		}
		if(matchScore == null) {
			matchScore = new MatchScore();
			matchScore.setMsTeamId(teamInfo.getTiId());
			matchScore.setMsMatchId(matchInfo.getMiId());
			matchScore.setMsMatchTitle(matchInfo.getMiTitle());
			matchScore.setMsMatchType(matchInfo.getMiType());
			matchScore.setMsGroupId(matchGroup.getMgId());
			matchScore.setMsGroupName(matchGroup.getMgGroupName());
			matchScore.setMsUserId(userInfo.getUiId());
			matchScore.setMsUserName(userInfo.getUiRealName());
			matchScore.setMsType(0);
			matchScore.setMsRodNum(score);
			matchScore.setMsBeforeAfter(beforeAfter);
			matchScore.setMsHoleName(parkPartition.getppName());
			matchScore.setMsHoleNum(parkPartition.getPpHoleNum());
			matchScore.setMsHoleStandardRod(parkPartition.getPpHoleStandardRod());
			matchService.getScore(matchScore, parkPartition.getPpHoleStandardRod());
			Integer rodCha = score - parkPartition.getPpHoleStandardRod();
			matchScore.setMsRodCha(rodCha<0?0:rodCha);
			matchScore.setMsCreateTime(System.currentTimeMillis());
			matchScore.setMsCreateUserId(AdminUserUtil.getUserId());
			matchScore.setMsCreateUserName(AdminUserUtil.getShowName());
			matchScore.setMsIsClaim(0);
			adminImportDao.save(matchScore);
		} else{
			//覆盖，更新
			matchScore.setMsGroupId(matchGroup.getMgId());
			matchScore.setMsGroupName(matchGroup.getMgGroupName());
			matchScore.setMsIsClaim(0);
			matchService.getScore(matchScore, parkPartition.getPpHoleStandardRod());
			Integer rodCha = score - parkPartition.getPpHoleStandardRod();
			matchScore.setMsRodCha(rodCha<0?0:rodCha);
			matchScore.setMsUpdateTime(System.currentTimeMillis());
			matchScore.setMsUpdateUserId(AdminUserUtil.getUserId());
			matchScore.setMsUpdateUserName(AdminUserUtil.getShowName());
			adminImportDao.update(matchScore);
		}

	}

	/**
	 * 更新球队队长
	 * @return
	 */
	public void updateTeamCap(XSSFWorkbook xwb, String joinTeamIds) {
		//参赛队id
		List<Long> joinTeamIdList = getLongList(joinTeamIds);
//		List<Long> joinTeamIdList = Arrays.asList(joinTeamIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		//第2张工作表
		XSSFSheet sheet = xwb.getSheetAt(1);
		XSSFRow row;
		for(int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第2行开始
			row = sheet.getRow(i);
			if(row.getCell(1) == null || StringUtils.isEmpty(row.getCell(1).toString())){
				break;
			}
			//第一列参赛球队简称
			String teamAbbrev = row.getCell(1).toString();
			String teamName = row.getCell(0).toString();

			TeamInfo teamInfo = adminImportDao.getTeamInfoByAbbrevName(teamAbbrev,joinTeamIdList);
			//球队队长
			String teamCaps = row.getCell(2).toString();
			String[] teamCapsSplit = teamCaps.split(",");
			for(String cap :teamCapsSplit){
				Map<String,Object> userTeamMapping = adminImportDao.getUserByRealNameTeamName(cap,teamName,teamAbbrev);
                Long userId = null;
				if (userTeamMapping==null){
                    Map<String, Object> user = adminImportDao.getUserByRealName(cap);
                    userId = matchService.getLongValue(user, "userId");
                } else {
				    userId =  matchService.getLongValue(userTeamMapping, "userId");
                }
				UserInfo userInfo = null;
				if(userId == null){
					//有的队长没参加比赛
					userInfo = new UserInfo();
					userInfo.setUiType(2);
					userInfo.setUiIsValid(1);
					userInfo.setUiRealName(cap);
					userInfo.setUiCreateTime(System.currentTimeMillis());
					userInfo.setUiCreateUserId(AdminUserUtil.getUserId());
					userInfo.setUiCreateUserName(AdminUserUtil.getShowName());
					adminImportDao.save(userInfo);
				}else{
					userInfo = adminImportDao.get(UserInfo.class,userId);
				}

				TeamUserMapping teamUserMapping = adminImportDao.getTeamUserMappingByTeamId(teamInfo.getTiId(),userInfo.getUiId());
				if(teamUserMapping == null){
					teamUserMapping = new TeamUserMapping();
				}
				teamUserMapping.setTumUserType(0);
				if(teamUserMapping != null && teamUserMapping.getTumId() != null){
					teamUserMapping.setTumUpdateTime(System.currentTimeMillis());
					teamUserMapping.setTumUpdateUserId(AdminUserUtil.getUserId());
					teamUserMapping.setTumUpdateUserName(AdminUserUtil.getShowName());
					adminImportDao.update(teamUserMapping);
				}else{
					teamUserMapping.setTumTeamId(teamInfo.getTiId());
					teamUserMapping.setTumUserId(userInfo.getUiId());
					teamUserMapping.setTumCreateTime(System.currentTimeMillis());
					teamUserMapping.setTumCreateUserId(AdminUserUtil.getUserId());
					teamUserMapping.setTumCreateUserName(AdminUserUtil.getShowName());
					adminImportDao.save(teamUserMapping);
				}
			}
		}
	}

}
