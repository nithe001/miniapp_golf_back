package com.golf.golf.web;

import com.golf.common.Const;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.TeamService;
import com.golf.golf.service.UserService;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
	@Autowired
	private MatchService matchService;
	@Autowired
	private UserService userService;

	/**
	 * 获取球队列表
	 * @param page 分页
	 * @param type 0：所有球队 1：已加入球队 2：可加入球队  3：我创建的球队
	 * @param keyword 球队名称
	 * @param joinTeamIds 用于创建比赛时添加球队，如果不为空，就查询除去这些球队的列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTeamList")
	public JsonElement getTeamList(Integer page, Integer type, String keyword, String joinTeamIds, String openid) {
		Integer nowPage = 1;
		if (page > 0) {
			nowPage = page;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<Map<String, Object>>(Const.ROWSPERPAGE , nowPage);
		try {
			SearchBean searchBean = new SearchBean();
			if(StringUtils.isNotEmpty(keyword) && !"undefined".equals(keyword) && !"null".equals(keyword)){
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if(StringUtils.isNotEmpty(joinTeamIds) && !"undefined".equals(joinTeamIds) && !"[]".equals(joinTeamIds) && !"null".equals(joinTeamIds)){
				joinTeamIds = joinTeamIds.replace("[","");
				joinTeamIds = joinTeamIds.replace("]","");
				joinTeamIds = joinTeamIds.replace("\"","");
				List<Long> teamIds = matchService.getLongTeamIdList(joinTeamIds);
				searchBean.addParpField("teamIds", teamIds);
			}
			searchBean.addParpField("type", type);
			searchBean.addParpField("userId", userService.getUserIdByOpenid(openid));
			pageInfo = teamService.getTeamList(searchBean, pageInfo, openid);
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取球队列表出错。openid="+openid;
			logger.error(errmsg+ e );
			return JsonWrapper.newErrorInstance(errmsg);
		}
		return JsonWrapper.newDataInstance(pageInfo);
	}

	/**
	 * 获取已选球队列表
	 * @param page 分页
	 * @param keyword 球队名称
	 * @param joinTeamIds 用于创建比赛时添加球队，如果不为空，就查询除去这些球队的列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getJoinTeamList")
	public JsonElement getJoinTeamList(Integer page, String keyword, String joinTeamIds, String openid) {
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
			if(StringUtils.isNotEmpty(joinTeamIds) && !"undefined".equals(joinTeamIds) && !"[]".equals(joinTeamIds)){
				joinTeamIds = joinTeamIds.replace("[","");
				joinTeamIds = joinTeamIds.replace("]","");
				joinTeamIds = joinTeamIds.replace("\"","");
				List<Long> teamIds = matchService.getLongTeamIdList(joinTeamIds);
				searchBean.addParpField("chooseTeamIds", teamIds);
				pageInfo = teamService.getChooseTeamList(searchBean, pageInfo, openid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errmsg = "前台-获取已选球队列表出错。openid="+openid;
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
	public JsonElement saveTeamInfo(String teamInfo, String logoPath, String signature, String digest, String openid) {
		try {
			if(StringUtils.isNotEmpty(teamInfo) && StringUtils.isNotEmpty(logoPath)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(teamInfo);
				TeamInfo teamInfoBean = (TeamInfo) net.sf.json.JSONObject.toBean(jsonObject, TeamInfo.class);
				teamInfoBean.setTiLogo(logoPath);
				teamInfoBean.setTiSignature(signature);
				teamInfoBean.setTiDigest(digest);
				teamService.saveOrUpdateTeamInfo(teamInfoBean, openid);
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
	@RequestMapping(value = "uploadTeamLogo")
	public String uploadImg(HttpServletRequest request) throws IOException {
		MultipartHttpServletRequest req =(MultipartHttpServletRequest)request;
      	MultipartFile file =  req.getFile("file");
        logger.error("进入上传球队logo请求。");
		try {
			String logoPath = null;
			System.out.println("执行upload");
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
                        logger.info("文件类型不正确");
                        return "error";
					}
				}else {
                    logger.info("文件不存在");
                    return "error";
				}
			}else {
                logger.info("文件不存在");
                return "error";
			}
//            return JsonWrapper.newDataInstance(logoPath);
            return logoPath;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传球队logo时出错。" + e);
            return "error";
		}
	}

	/**
	 * 删除球队
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delTeamById")
	public JsonElement delTeamById(Long teamId, String openid) {
		try {
			boolean flag = false;
			if(teamId != null){
				flag = teamService.delTeamById(teamId,openid);
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
	public JsonElement getTeamDetailById(Long teamId, String openid) {
		try {
			Map<String, Object> result = teamService.getTeamInfoById(teamId, openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取球队详情时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取球队详情时出错");
		}
	}


	/**
	 * 获取球队记分详情  只计算比赛结束并且球队确认过的场次成绩
	 * 积分榜那里的平均杆数是指每场（18洞）的平均杆数，不是每洞的。
	 * 球队比分排名杆数少的排前面，积分榜是积分多的排前面
	 * 当某人实际场数少于选择的前N场时，要除以实际场数
	 * @param teamId:球队id
	 * @param type:0比分榜 按平均杆排名 1积分榜:按北大发明的积分方法积分，方法另附 2获取比赛榜 列出当年所有本球队相关的比赛情况统计
	 *            注意计算成绩时，看该球友在球队是否是审核通过的
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTeamPointByYear")
	public JsonElement getTeamPointByYear(Integer type, String date, Long teamId, Integer changci) {
		try {
			Map<String, Object> result = teamService.getTeamPointByYear(type, date, teamId, changci);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取球队记分详情时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取球队记分详情时出错");
		}
	}

	/**
	 * 获取球队已报名的用户或者球队用户列表
	 * @param teamId:球队id
	 * @param type:0已报名的 1本队用户
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getUserListByTeamId")
	public JsonElement getUserListByTeamId(Long teamId, Integer type) {
		try {
			List<Map<String, Object>> result = teamService.getUserListByTeamId(teamId, type);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取球队球友列表时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取球队球友列表时出错");
		}
	}

	/**
	 * 更新球队用户
	 * @param teamId:球队id
	 * @param userIds:用户
	 * @param type:0添加已报名的 1删除这些用户
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateTeamUserByTeamId")
	public JsonElement updateTeamUserByTeamId(Long teamId, String userIds, Integer type, String openid) {
		try {
			teamService.updateTeamUserByTeamId(teamId, userIds, type, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新球队用户时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("更新球队用户时出错");
		}
	}

	/**
	 * 获取本球队所有用户
	 * @param teamId:球队id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAllUserListByTeamId")
	public JsonElement getAllUserListByTeamId(Long teamId) {
		try {
			List<Map<String, Object>> result = teamService.getAllUserListByTeamId(teamId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取本球队所有用户时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("获取本球队所有用户时出错");
		}
	}

	/**
	 * 加入或退出该球队
	 * @param teamId:球队id
	 * @param type:0加入 1退出
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "joinOrQuitTeamById")
	public JsonElement joinOrQuitTeamById(Long teamId, Integer type, String openid) {
		try {
			teamService.joinOrQuitTeamById(teamId, type, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("加入或退出该球队时出错。球队id="+teamId + e);
			return JsonWrapper.newErrorInstance("加入或退出该球队时出错");
		}
	}

	/**
	 * 队长点击本队队员信息页面，有指定该用户成为队长按钮
	 * @param teamId:球队id
	 * @param userId:被指定人id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "setTeamCaptainByUserId")
	public JsonElement setTeamCaptainByUserId(Long teamId, Long userId, String openid) {
		try {
			teamService.setTeamCaptainByUserId(teamId, userId, openid);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("队长指定该用户成为队长时出错。球队id="+teamId +" 用户id="+userId+ e);
			return JsonWrapper.newErrorInstance("队长指定该用户成为队长时出错");
		}
	}


	/**
	 * 查询我是否填写了详细资料
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getHasDetail")
	public JsonElement getHasDetail(String openid) {
		try {
			boolean flag = teamService.getHasDetail(openid);
			return JsonWrapper.newDataInstance(flag);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询我是否填写了详细资料时出错。"+ e);
			return JsonWrapper.newErrorInstance("查询我是否填写了详细资料时出错");
		}
	}


}
