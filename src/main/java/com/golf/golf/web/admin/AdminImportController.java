package com.golf.golf.web.admin;

import com.golf.common.util.TimeUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.admin.AdminImportService;
import com.golf.golf.service.admin.AdminParkService;
import com.google.gson.JsonElement;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 导入成绩
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/import")
public class AdminImportController {
	private final static Logger logger = LoggerFactory.getLogger(AdminImportController.class);

	@Autowired
	private AdminImportService adminImportService;
	@Autowired
	private AdminParkService adminParkService;

	/**
	 * 导入init
	 * @return
	 */
	@RequestMapping("init")
	public String importInit(){
		return "admin/importScore/init";
	}

	/**
	 * 导入比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"importScore"})
	public JsonElement importScore(MultipartFile file) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
		int n = xwb.getNumberOfSheets();
		//导入球队详情
		boolean result = adminImportService.importTeamInfo(xwb);
		if(result){
			//导入比赛详情
			result = adminImportService.importMatchInfo(xwb);
			if(result){
				//导入成绩
			}
		}
		return null;
	}

	/**
	 * 导入球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"importTeamInfo"})
	public JsonElement importTeamInfo(MultipartFile file) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
		int n = xwb.getNumberOfSheets();
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row;
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
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

			MatchInfo matchInfo = new MatchInfo();
			matchInfo.setMiType(1);
			matchInfo.setMiTitle(matchTitle);
			matchInfo.setMiZoneBeforeNine(changdiSplit[0]);
			matchInfo.setMiZoneAfterNine(changdiSplit[1]);
			matchInfo.setMiMatchTime(matchTime);
			matchInfo.setMiMatchFormat2(matchTypeSplit[0].equals("个人")?0:1);
			matchInfo.setMiMatchFormat1(matchTypeSplit[1].equals("比杆")?0:1);
			System.out.println(matchInfo);
		}
		return null;
	}


}
