package com.golf.golf.web.admin;

import com.golf.common.gson.JsonWrapper;
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
import java.util.List;

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
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"importScore"})
	public JsonElement importScore(MultipartFile file) throws IOException {
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
			int n = xwb.getNumberOfSheets();
			//导入球队详情
			List<String> joinTeamIdList = adminImportService.importTeamInfo(xwb);
			//导入比赛详情
			Long matchId = adminImportService.importMatchInfo(xwb,joinTeamIdList);
			//导入用户
			adminImportService.importUserInfo(xwb);
			//导入球队球友mapping
			adminImportService.importTeamUserMapping(xwb);
			//导入比赛球友mapping
			adminImportService.importMatchUserMapping(xwb,matchId);
			//导入成绩
			adminImportService.importScoreInfo(xwb,matchId);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理——导入成绩时出错。"+ e );
			return JsonWrapper.newErrorInstance("后台管理——导入成绩时出错。");
		}
	}
}
