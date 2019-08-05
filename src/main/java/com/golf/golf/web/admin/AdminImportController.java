package com.golf.golf.web.admin;

import com.golf.common.gson.JsonWrapper;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.admin.AdminImportService;
import com.google.gson.JsonElement;
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
	 * 是否覆盖：1：是 0：否
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"importScore"})
	public JsonElement importScore(MultipartFile file) throws IOException {
//		,Integer isCoverMatch,Integer isCoverTeam,Integer isCoverScore
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
//			int n = xwb.getNumberOfSheets();
			//导入球队详情，返回球队idList
			String joinTeamIdList = adminImportService.importTeamInfo(xwb);
			//导入比赛详情,返回比赛id
			MatchInfo matchInfo = adminImportService.importMatchInfo(xwb,joinTeamIdList);
			if(matchInfo == null){
				return JsonWrapper.newErrorInstance("比赛场地不存在。");
			}
			//导入用户/球队球友mapping/比赛球友mapping、导入比赛分组
			adminImportService.importData(xwb,matchInfo);
			//更新队长
			adminImportService.updateTeamCap(xwb,joinTeamIdList);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理——导入成绩时出错。"+ e );
			return JsonWrapper.newErrorInstance("后台管理——导入成绩时出错。");
		}
	}
}
