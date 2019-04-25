package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.service.TeamService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 球队管理
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/teamManage")
public class TeamManageController {
	private final static Logger logger = LoggerFactory.getLogger(TeamManageController.class);
    private String errmsg;

    @Autowired
    private TeamService teamService;

	/**
	 * 获取球队列表
	 * @param page 分页
	 * @param type 0：所有球队 1：我加入的球队 2：我可以加入的球队   3：我创建的球队
	 * @param keyword 球队名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamList")
	public JsonElement getTeamList(Integer page, Integer type, String keyword) {
		Integer nowPage = 1;
		if (page > 0) {
			nowPage = page;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			searchBean.addParpField("type", type);
			pageInfo = teamService.getTeamList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取球队列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newDataInstance(pageInfo);
	}

    /**
     * 获取所有球队 或者 可以加入的球队列表
     * @param page 分页
     * @param type 0：所有球队 1：我加入的球队 2：我可以加入的球队   3：我创建的球队
     * @param keyword 球队名称
     * @return
     */
    @ResponseBody
    @RequestMapping("getTeamList4")
    public JsonElement getTeamList4(Integer page, Integer type, String keyword) {
        Integer nowPage = 1;
        if (page > 0) {
            nowPage = page;
        }
        POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(Const.ROWSPERPAGE , nowPage);
        try {
            SearchBean searchBean = new SearchBean();
            if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword)){
                searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
            }
            if(type == null || type == 0){
            	//所有球队
                pageInfo = teamService.getTeamList(searchBean, pageInfo);
            }else if(type >= 1){
                searchBean.addParpField("userId", WebUtil.getUserIdBySessionId());
				searchBean.addParpField("type", type);
                pageInfo = teamService.getMyTeamList(searchBean, pageInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errmsg = "前台-获取球队列表出错。";
            logger.error(errmsg+ e );
            return JsonWrapper.newErrorInstance(errmsg);
        }
        return JsonWrapper.newDataInstance(pageInfo);
    }

	/**
	 * 获取我创建的球队列表 或者 我加入的球队列表
	 * @param page 分页
	 * @param type 0：我加入的 1：我创建的
	 * @param keyword 球队名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMyTeamList")
	public JsonElement getMyTeamList(Integer page,Integer type, String keyword) {
		Integer nowPage = 1;
		if (page > 0) {
			nowPage = page;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			searchBean.addParpField("type", type);
			searchBean.addParpField("userId", WebUtil.getUserIdBySessionId());
			pageInfo = teamService.getMyCreateTeamList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取我创建的球队列表出错。";
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 创建球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveTeamInfo")
	public JsonElement saveTeamInfo(String teamInfo, String logoPath) {
		try {
			if(StringUtils.isNotEmpty(teamInfo) && StringUtils.isNotEmpty(logoPath)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(teamInfo);
				TeamInfo teamInfoBean = (TeamInfo) net.sf.json.JSONObject.toBean(jsonObject, TeamInfo.class);
				teamInfoBean.setTiLogo(PropertyConst.DOMAIN + logoPath);
				teamService.saveOrUpdateTeamInfo(teamInfoBean);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建球队时出错。" + e);
			return JsonWrapper.newErrorInstance("创建球队时出错");
		}
	}

	/**
	 * 创建球队——上传logo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "uploadImg")
	public JsonElement uploadImg(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		try {
			String logoPath = null;
			System.out.println("执行upload");
			request.setCharacterEncoding("UTF-8");
			logger.info("执行图片上传");
			if(!file.isEmpty()) {
				logger.info("成功获取照片");
				String fileName = file.getOriginalFilename();
				String type = null;
				type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
				logger.info("图片初始名称为：" + fileName + " 类型为：" + type);
				if (type != null) {
					if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {
						// 项目在容器中实际发布运行的根路径
						String realPath = WebUtil.getRealPath(PropertyConst.TEAM_LOGO_PATH);
						// 自定义的文件名称
						String trueFileName = String.valueOf(System.currentTimeMillis()) + "."+type;

						File targetFile = new File(realPath, trueFileName);
						if(!targetFile.exists()){
							targetFile.mkdirs();
						}
						file.transferTo(targetFile);
						logoPath = PropertyConst.TEAM_LOGO_PATH+trueFileName;
						logger.info("文件成功上传到指定目录下");
					}else {
						return JsonWrapper.newErrorInstance("文件类型不正确");
					}
				}else {
					return JsonWrapper.newErrorInstance("文件不存在");
				}
			}else {
				return JsonWrapper.newErrorInstance("文件不存在");
			}
			return JsonWrapper.newDataInstance(logoPath);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传球队logo时出错。" + e);
			return JsonWrapper.newErrorInstance("上传球队logo时出错");
		}
	}

	/**
	 * 删除球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delTeamById")
	public JsonElement delTeamById(Long teamId) {
		try {
			boolean flag = false;
			if(teamId != null){
				flag = teamService.delTeamById(teamId);
			}
			if(flag){
				return JsonWrapper.newSuccessInstance();
			}
			return JsonWrapper.newErrorInstance("无权删除该球队");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除球队时出错。" + e);
			return JsonWrapper.newErrorInstance("删除球队时出错");
		}
	}

	/**
	 * 获取球队详情
	 * @param teamId:球队id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTeamDetailById")
	public JsonElement getTeamDetailById(Long teamId) {
		try {
			Map<String, Object> result = teamService.getTeamInfoById(teamId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取球队详情时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取球队详情时出错");
		}
	}


	/**
	 * 获取球队记分详情
	 * @param teamId:球队id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTeamPointByYear")
	public JsonElement getTeamPointByYear(String date, Long teamId) {
		try {
			List<Map<String, Object>> result = teamService.getTeamPointByYear(date, teamId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取球队记分详情时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取球队记分详情时出错");
		}
	}



}
