package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminImportDao;
import com.golf.golf.db.*;
import com.golf.golf.service.MatchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
	 * @return
	 */
	public List<String> importTeamInfo(XSSFWorkbook xwb) {
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row = sheet.getRow(1);
		//参赛球队id
		List<String> joinTeamIdList = new ArrayList<>();
		//参赛球队
		String joinTeamName = row.getCell(5).toString();
		String[] joinTeamNameSplit = joinTeamName.split(",");
		for(String teamName :joinTeamNameSplit){
			teamName = teamName.trim();
			//用球队简称 查询是否有该球队
			TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);
			if(teamInfo == null){
				teamInfo = new TeamInfo();
			}
			teamInfo.setTiName(teamName);
			teamInfo.setTiAbbrev(teamName);
			teamInfo.setTiIsValid(1);
			if(teamInfo != null && teamInfo.getTiId() != null){
				teamInfo.setTiUpdateTime(System.currentTimeMillis());
				teamInfo.setTiUpdateUserId(AdminUserUtil.getUserId());
				teamInfo.setTiUpdateUserName(AdminUserUtil.getShowName());
				adminImportDao.update(teamInfo);
			}else{
				teamInfo.setTiCreateTime(System.currentTimeMillis());
				teamInfo.setTiCreateUserId(AdminUserUtil.getUserId());
				teamInfo.setTiCreateUserName(AdminUserUtil.getShowName());
				adminImportDao.save(teamInfo);
			}
			joinTeamIdList.add(teamInfo.getTiId().toString());
		}
		return joinTeamIdList;
	}

	/**
	 * 导入比赛详情
	 * @return
	 */
	public Long importMatchInfo(XSSFWorkbook xwb,List<String> joinTeamIdList) throws Exception {
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row ;
		Long matchId = null;
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			//比赛标题
			String matchTitle = row.getCell(0).toString().trim();
			//球场
			String parkName = row.getCell(1).toString();
			ParkInfo parkInfo = adminImportDao.getParkInfoByName(parkName);
			if(parkInfo == null){
				logger.error("数据库没有此球场："+parkName);
				throw new Exception("数据库没有此球场："+parkName);
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

			//根据比赛标题获取是否有此比赛
			MatchInfo matchInfo = adminImportDao.getMatchInfoByMatchTitle(matchTitle);
			if(matchInfo == null){
				matchInfo = new MatchInfo();
			}
			matchInfo.setMiType(1);
			matchInfo.setMiTitle(matchTitle);
			matchInfo.setMiParkId(parkInfo.getPiId());
			matchInfo.setMiParkName(parkInfo.getPiName());
			matchInfo.setMiZoneBeforeNine(changdiSplit[0]);
			matchInfo.setMiZoneAfterNine(changdiSplit[1]);
			matchInfo.setMiMatchTime(matchTime);
			matchInfo.setMiMatchOpenType(2);
			matchInfo.setMiJoinOpenType(2);
			matchInfo.setMiMatchFormat2(matchTypeSplit[0].equals("个人")?0:1);
			matchInfo.setMiMatchFormat1(matchTypeSplit[1].equals("比杆")?0:1);
			matchInfo.setMiJoinTeamIds(String.join(",", joinTeamIdList));
			matchInfo.setMiIsEnd(2);
			matchInfo.setMiIsValid(1);
			if(matchInfo != null && matchInfo.getMiId() != null){
				matchInfo.setMiUpdateTime(System.currentTimeMillis());
				matchInfo.setMiUpdateUserId(AdminUserUtil.getUserId());
				matchInfo.setMiUpdateUserName(AdminUserUtil.getShowName());
				adminImportDao.update(matchInfo);
			}else{
				matchInfo.setMiCreateTime(System.currentTimeMillis());
				matchInfo.setMiCreateUserId(AdminUserUtil.getUserId());
				matchInfo.setMiCreateUserName(AdminUserUtil.getShowName());
				adminImportDao.save(matchInfo);
			}
			matchId = matchInfo.getMiId();
		}
		return matchId;
	}

	/**
	 * 导入用户
	 * @return
	 */
	public void importUserInfo(XSSFWorkbook xwb) {
		//第二张工作表
		XSSFSheet sheet = xwb.getSheetAt(1);
		XSSFRow row;
		for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第三行开始
			row = sheet.getRow(i);
			//第4列 球友
			String userName = row.getCell(3).toString().trim();
			//查看是否有该球友
			UserInfo userInfo = adminImportDao.getUserByRealName(userName);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.setUiType(2);
				userInfo.setUiIsValid(1);
			}
			userInfo.setUiRealName(userName);
			if(userInfo != null && userInfo.getUiId() != null){
				userInfo.setUiUpdateTime(System.currentTimeMillis());
				userInfo.setUiUpdateUserId(AdminUserUtil.getUserId());
				userInfo.setUiUpdateUserName(AdminUserUtil.getShowName());
				adminImportDao.update(userInfo);
			}else{
				userInfo.setUiCreateTime(System.currentTimeMillis());
				userInfo.setUiCreateUserId(AdminUserUtil.getUserId());
				userInfo.setUiCreateUserName(AdminUserUtil.getShowName());
				adminImportDao.save(userInfo);
			}
		}
	}

	/**
	 * 导入球队球友mapping
	 * @return
	 */
	public void importTeamUserMapping(XSSFWorkbook xwb) {
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row = sheet.getRow(1);
		//参赛球队
		String joinTeamName = row.getCell(5).toString();
		String[] joinTeamNameSplit = joinTeamName.split(",");
		for(String teamName :joinTeamNameSplit){
			teamName = teamName.trim();
			//增加球队球友
			//第二张工作表
			sheet = xwb.getSheetAt(1);
			for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++){
				//从第三行开始
				row = sheet.getRow(i);
				//第二列 球队
				String teamName4Score = row.getCell(1).toString().trim();
				//第4列 该球队的球友
				String userName = row.getCell(3).toString().trim();
				//获取球友信息
				UserInfo userInfo = adminImportDao.getUserByRealName(userName);
				//球队信息
				TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);

				if(teamName4Score.equals(teamName)){
					//查看是否有该球友的teamUserMapping
					TeamUserMapping teamUserMapping = adminImportDao.getTeamUserMappingByUserId(userInfo.getUiId());
					if(teamUserMapping == null){
						teamUserMapping = new TeamUserMapping();
					}
					teamUserMapping.setTumTeamId(teamInfo.getTiId());
					teamUserMapping.setTumUserId(userInfo.getUiId());
					teamUserMapping.setTumUserType(1);
					teamUserMapping.setTumCreateTime(System.currentTimeMillis());
					teamUserMapping.setTumCreateUserId(AdminUserUtil.getUserId());
					teamUserMapping.setTumCreateUserName(AdminUserUtil.getShowName());

					if(teamUserMapping != null && teamUserMapping.getTumId() != null){
						teamUserMapping.setTumUpdateTime(System.currentTimeMillis());
						teamUserMapping.setTumUpdateUserId(AdminUserUtil.getUserId());
						teamUserMapping.setTumUpdateUserName(AdminUserUtil.getShowName());
						adminImportDao.update(userInfo);
					}else{
						teamUserMapping.setTumCreateTime(System.currentTimeMillis());
						teamUserMapping.setTumCreateUserId(AdminUserUtil.getUserId());
						teamUserMapping.setTumCreateUserName(AdminUserUtil.getShowName());
						adminImportDao.save(teamUserMapping);
					}
				}
			}
		}
	}



	/**
	 * 导入比赛球友mapping
	 * @return
	 */
	public void importMatchUserMapping(XSSFWorkbook xwb, Long matchId) {
		//第二张工作表
		XSSFSheet sheet = xwb.getSheetAt(1);
		XSSFRow row;
		for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第三行开始
			row = sheet.getRow(i);
			//第二列 球队
			String teamName = row.getCell(1).toString().trim();
			//第4列 该球队的球友
			String userName = row.getCell(3).toString().trim();
			//第3列 所在分组
			String groupName = row.getCell(2).toString().trim();
			if(StringUtils.isNotEmpty(groupName)){
				Double d = Double.parseDouble(groupName);
				groupName = d.intValue()+"";
			}

			Long teamId = null;
			Long userId = null;
			TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);
			if(teamInfo != null){
				teamId = teamInfo.getTiId();
			}
			//查看是否有该球友
			UserInfo userInfo = adminImportDao.getUserByRealName(userName);
			if (userInfo != null) {
				userId = userInfo.getUiId();
			}
			MatchUserGroupMapping matchUserGroupMapping = adminImportDao.getMatchUserMapping(matchId,teamId,userId);
			if(matchUserGroupMapping == null){
				matchUserGroupMapping = new MatchUserGroupMapping();
			}
			matchUserGroupMapping.setMugmMatchId(matchId);
			matchUserGroupMapping.setMugmTeamId(teamId);
			matchUserGroupMapping.setMugmUserId(userId);
			matchUserGroupMapping.setMugmUserName(userName);
			matchUserGroupMapping.setMugmUserType(1);
			matchUserGroupMapping.setMugmIsAutoCap(0);

			//分组
			MatchGroup matchGroup = adminImportDao.getMatchGroupByName(matchId,groupName);
			if(matchGroup == null){
				matchGroup = new MatchGroup();
			}
			matchGroup.setMgMatchId(matchId);
			matchGroup.setMgGroupName(groupName);
			if(matchGroup != null && matchGroup.getMgId() != null){
				matchGroup.setMgUpdateTime(System.currentTimeMillis());
				matchGroup.setMgUpdateUserId(AdminUserUtil.getUserId());
				matchGroup.setMgUpdateUserName(AdminUserUtil.getShowName());
				adminImportDao.update(matchGroup);
			}else{
				matchGroup.setMgCreateTime(System.currentTimeMillis());
				matchGroup.setMgCreateUserId(AdminUserUtil.getUserId());
				matchGroup.setMgCreateUserName(AdminUserUtil.getShowName());
				adminImportDao.save(matchGroup);
			}
			matchUserGroupMapping.setMugmGroupId(matchGroup.getMgId());
			matchUserGroupMapping.setMugmGroupName(groupName);
			matchUserGroupMapping.setMugmIsDel(0);
			if(matchUserGroupMapping != null && matchUserGroupMapping.getMugmId() != null){
				matchUserGroupMapping.setMugmUpdateTime(System.currentTimeMillis());
				matchUserGroupMapping.setMugmUpdateUserId(AdminUserUtil.getUserId());
				matchUserGroupMapping.setMugmUpdateUserName(AdminUserUtil.getShowName());
				adminImportDao.update(matchUserGroupMapping);
			}else{
				matchUserGroupMapping.setMugmCreateTime(System.currentTimeMillis());
				matchUserGroupMapping.setMugmCreateUserId(AdminUserUtil.getUserId());
				matchUserGroupMapping.setMugmCreateUserName(AdminUserUtil.getShowName());
				adminImportDao.save(matchUserGroupMapping);
			}
		}
	}

	/**
	 * 导入成绩
	 * @return
	 */
	public void importScoreInfo(XSSFWorkbook xwb, Long matchId) {
		MatchInfo matchInfo = adminImportDao.get(MatchInfo.class,matchId);
		//第二张工作表
		XSSFSheet sheet = xwb.getSheetAt(1);
		XSSFRow row;
		for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
			//从第三行开始
			row = sheet.getRow(i);
			//第2列 球队
			String teamName = row.getCell(1).toString().trim();
			TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);
			//第3列 分组
			String groupName = row.getCell(2).toString().trim();
			if(StringUtils.isNotEmpty(groupName)){
				Double d = Double.parseDouble(groupName);
				groupName = d.intValue()+"";
			}
			MatchGroup matchGroup = adminImportDao.getMatchGroupByName(matchId,groupName);
			//第4列 球友
			String userName = row.getCell(3).toString().trim();
			UserInfo userInfo = adminImportDao.getUserByRealName(userName);
			//从第5列开始 每洞得分
			//前9洞
			for(int j = 4;j<13;j++){
				Integer holeNum = j-3;
				saveOrUpdateUserScore(row,j,holeNum,matchInfo,teamInfo,matchGroup,userInfo,0);
			}
			//后9洞
			for(int j = 14;j<23;j++){
				Integer holeNum = j-13;
				saveOrUpdateUserScore(row,j,holeNum,matchInfo,teamInfo,matchGroup,userInfo,1);
			}
		}
	}

	//新增或更新球友成绩
	private void saveOrUpdateUserScore(XSSFRow row, int j, Integer holeNum,MatchInfo matchInfo,TeamInfo teamInfo,
									   MatchGroup matchGroup,UserInfo userInfo,Integer beforeAfter) {
		String scoreEveHole = row.getCell(j).toString().trim();
		Integer score = null;
		if(StringUtils.isNotEmpty(scoreEveHole)){
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
		MatchScore matchScore = adminImportDao.getMatchScoreByUser(
				teamInfo.getTiId(),matchInfo.getMiId(),matchGroup.getMgGroupName(),userInfo.getUiRealName(),holeNum,holeName,beforeAfter);
		if(matchScore == null){
			matchScore = new MatchScore();
		}
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
		matchScore.setMsRodCha(score-parkPartition.getPpHoleStandardRod());
		if(matchScore != null && matchScore.getMsId() != null){
			matchScore.setMsUpdateTime(System.currentTimeMillis());
			matchScore.setMsUpdateUserId(AdminUserUtil.getUserId());
			matchScore.setMsUpdateUserName(AdminUserUtil.getShowName());
			adminImportDao.update(matchScore);
		}else{
			matchScore.setMsCreateTime(System.currentTimeMillis());
			matchScore.setMsCreateUserId(AdminUserUtil.getUserId());
			matchScore.setMsCreateUserName(AdminUserUtil.getShowName());
			adminImportDao.save(matchScore);
		}
	}


}
