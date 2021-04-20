package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.excel.ExcelData;
import com.golf.common.excel.ExcelUtils;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.TimeUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.admin.AdminExportService;
import com.golf.golf.service.admin.AdminMatchService;
import com.golf.golf.service.admin.AdminTeamService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 导出功能
 * @author nmy
 * 2020年8月6日
 */
@Controller
@RequestMapping(value = "/admin/export")
public class AdminExportController {
	private final static Logger logger = LoggerFactory.getLogger(AdminExportController.class);

	@Autowired
	private AdminExportService adminExportService;
	@Autowired
	private MatchService matchService;
	@Autowired
	private AdminMatchService adminMatchService;
    @Autowired
    private AdminTeamService adminTeamService;


	/**
	 * 赛事活动列表
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm, String keyword, String startDate, String endDate, Integer page, Integer rowsPerPage,
					   Integer type, Integer state, Integer isValid){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if (type != null) {
				searchBean.addParpField("type", type);
			}
			if (state != null) {
				searchBean.addParpField("state", state);
			}
			if (isValid != null) {
				searchBean.addParpField("isValid", isValid);
			}
			pageInfo = adminMatchService.matchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取赛事活动列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("type",type);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("page",page);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		mm.addAttribute("state",state);
		mm.addAttribute("isValid",isValid);
		mm.addAttribute("startDate", startDate);
		mm.addAttribute("endDate", endDate);
		return "admin/export/list";
	}

	/**
	 * 导出比赛详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"exportScore"})
	public JsonElement exportScore(HttpServletResponse response, Long matchId){
		try {
			MatchInfo matchInfo = matchService.getMatchById(matchId);
			Map<String,Object> score = matchService.getTotalScoreByMatchId(matchId, 0,0);
			ExcelData excelData = adminExportService.exportExcel(score,matchInfo);
			String fileName = matchInfo.getMiTitle()+"-比赛成绩导出-" + TimeUtil.longToString(System.currentTimeMillis(), "yyyy-MM-dd") + ".xlsx";
			ExcelUtils.exportExcel(response, fileName, excelData);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理——比赛成绩导出时出错。"+ e );
			return JsonWrapper.newErrorInstance("后台管理——比赛成绩导出时出错。");
		}
	}


    /**
     * 导出球队
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"exportTeam"})
    public JsonElement exportScore(HttpServletResponse response){
        try {
            List<Map<String, Object>> userTeamList = adminTeamService.getUserTeamList();
            ExcelData excelData = adminExportService.exportUserTeamExcel(userTeamList);
            String fileName = "球队信息导出-" + TimeUtil.longToString(System.currentTimeMillis(), "yyyy-MM-dd") + ".xlsx";
            ExcelUtils.exportExcel(response, fileName, excelData);
            return JsonWrapper.newSuccessInstance();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("后台管理——球队信息导出时出错。"+ e );
            return JsonWrapper.newErrorInstance("后台管理——球队信息导出时出错。");
        }
    }
}
