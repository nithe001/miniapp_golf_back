package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.util.TimeUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminImportDao;
import com.golf.golf.db.*;
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
			//用球队简称 查询是否有该球队
			TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);
			if(teamInfo == null){
				//新建球队
				teamInfo = new TeamInfo();
				teamInfo.setTiName(teamName);
				teamInfo.setTiAbbrev(teamName);
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
			//增加球队球友
			//第二张工作表
			sheet = xwb.getSheetAt(1);
			for(int i = 2; i < sheet.getPhysicalNumberOfRows(); i++){
				//从第三行开始
				row = sheet.getRow(i);
				//第二列 球队
				String teamName4Score = row.getCell(1).toString();
				//第4列 改球队的球友
				String userName = row.getCell(3).toString();
				System.out.println(userName);
				if(teamName4Score.equals(teamName)){
					//查看是否有该球友
					Long teamUserMappingCount = adminImportDao.getTeamUserMappingByUserName(userName);
					if(teamUserMappingCount >0){
						continue;
					}
					//如果没有，创建一个虚拟用户,没有openid的
					UserInfo userInfo = new UserInfo();
					userInfo.setUiRealName(userName);
					userInfo.setUiType(2);
					userInfo.setUiIsValid(1);
					userInfo.setUiCreateTime(System.currentTimeMillis());
					userInfo.setUiCreateUserId(AdminUserUtil.getUserId());
					userInfo.setUiCreateUserName(AdminUserUtil.getShowName());
					adminImportDao.save(userInfo);

					//用球队简称 查询是否有该球队
					TeamInfo teamInfo = adminImportDao.getTeamInfoByName(teamName);
					TeamUserMapping teamUserMapping = new TeamUserMapping();
					teamUserMapping.setTumTeamId(teamInfo.getTiId());
					teamUserMapping.setTumUserId(userInfo.getUiId());
					teamUserMapping.setTumUserType(1);
					teamUserMapping.setTumCreateTime(System.currentTimeMillis());
					teamUserMapping.setTumCreateUserId(AdminUserUtil.getUserId());
					teamUserMapping.setTumCreateUserName(AdminUserUtil.getShowName());
					adminImportDao.save(teamUserMapping);
				}
			}
		}
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
			String matchTitle = row.getCell(0).toString();
			//球场
			String parkName = row.getCell(1).toString();
			ParkInfo parkInfo = getParkInfoByName(parkName);
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
			matchInfo.setMiCreateTime(System.currentTimeMillis());
			matchInfo.setMiCreateUserId(AdminUserUtil.getUserId());
			matchInfo.setMiCreateUserName(AdminUserUtil.getShowName());
			if(matchInfo != null && matchInfo.getMiId() != null){
				adminImportDao.update(matchInfo);
			}else{
				adminImportDao.save(matchInfo);
			}
			matchId = matchInfo.getMiId();
		}
		return matchId;
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
			String teamName = row.getCell(1).toString();
			//第4列 改球队的球友
			String userName = row.getCell(3).toString();
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

			MatchUserGroupMapping matchUserGroupMapping = new MatchUserGroupMapping();
		}
	}

	/**
	 * 根据球场名称获取球场信息
	 * @return
	 */
	public ParkInfo getParkInfoByName(String parkName) {
		return adminImportDao.getParkInfoByName(parkName);
	}


}
