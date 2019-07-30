package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.util.TimeUtil;
import com.golf.golf.dao.admin.AdminImportDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.ParkInfo;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public boolean importTeamInfo(XSSFWorkbook xwb) {
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row = sheet.getRow(1);
		//参赛球队
		String teamInfo = row.getCell(5).toString();
		String[] teamInfoSplit = teamInfo.split(",");
		for(String teamName :teamInfoSplit){

		}
		return false;
	}

	/**
	 * 导入比赛详情
	 * @return
	 */
	public boolean importMatchInfo(XSSFWorkbook xwb) {
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row;
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i+1);
			//比赛标题
			String matchTitle = row.getCell(0).toString();
			//球场
			String parkName = row.getCell(1).toString();
			//场地（A,B）
			String changdi = row.getCell(2).toString();
			String[] changdiSplit = changdi.split(",");
			//比赛时间
			String matchTime = row.getCell(3).toString();
			Long mTime = TimeUtil.stringToLong(matchTime,TimeUtil.FORMAT_DATE);
			matchTime = TimeUtil.longToString(mTime,TimeUtil.FORMAT_DATE);
			//赛制
			String matchType = row.getCell(4).toString();
			String[] matchTypeSplit = matchType.split(",");

			ParkInfo parkInfo = getParkInfoByName(parkName);
			if(parkInfo == null){
				logger.error("数据库没有此球场："+parkName);
				return false;
			}

			MatchInfo matchInfo = new MatchInfo();
			matchInfo.setMiType(1);
			matchInfo.setMiTitle(matchTitle);
			matchInfo.setMiParkId(parkInfo.getPiId());
			matchInfo.setMiParkName(parkInfo.getPiName());
			matchInfo.setMiZoneBeforeNine(changdiSplit[0]);
			matchInfo.setMiZoneAfterNine(changdiSplit[1]);
			matchInfo.setMiMatchTime(matchTime);
			matchInfo.setMiMatchFormat2(matchTypeSplit[0].equals("个人")?0:1);
			matchInfo.setMiMatchFormat1(matchTypeSplit[1].equals("比杆")?0:1);
			System.out.println(matchInfo);
			return true;
		}
		return false;
	}

	/**
	 * 根据球场名称获取球场信息
	 * @return
	 */
	public ParkInfo getParkInfoByName(String parkName) {
		return adminImportDao.getParkInfoByName(parkName);
	}
}
